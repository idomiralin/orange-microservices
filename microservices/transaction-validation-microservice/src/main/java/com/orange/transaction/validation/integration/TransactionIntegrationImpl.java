package com.orange.transaction.validation.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.controller.validation.ReportController;
import com.orange.event.Event;
import com.orange.helper.dto.ReportCompleteDto;
import com.orange.helper.dto.TransactionDto;
import com.orange.controller.save.TransactionSaveController;
import com.orange.helper.exceptions.InvalidInputException;
import com.orange.helper.exceptions.NotFoundException;
import com.orange.helper.http.HttpErrorInfo;
//import io.github.resilience4j.circuitbreaker.CircuitBreakerOpenException;
//import io.github.resilience4j.circuitbreaker.CircuitBreakerOpenException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;

import static com.orange.event.Event.Type.CREATE;

@EnableBinding(TransactionIntegrationImpl.MessageSources.class)
@Component
public class TransactionIntegrationImpl implements ReportController {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionIntegrationImpl.class);

    private WebClient webClient;
    private final ObjectMapper mapper;

    private final String transactionSaveMicroserviceUrl;

    private final WebClient.Builder webClientBuilder;

    private MessageSources messageSources;

    private final int transactionSaveServiceTimeoutSec;

    public interface MessageSources {

        String OUTPUT_CHANNEL_TRANSACTIONS = "output-transactions";

        @Output(OUTPUT_CHANNEL_TRANSACTIONS)
        MessageChannel outputChannelTransactions();

    }

    @Autowired
    public TransactionIntegrationImpl(
        WebClient.Builder webClientBuilder,
        ObjectMapper mapper,
        MessageSources messageSources,

        @Value("${app.transaction-save-microservice.host}") String transactionSaveServiceHost,
        @Value("${app.transaction-save-microservice.port}") int    transactionSaveServicePort,
        @Value("${app.transaction-save-microservice.timeoutSec}") int transactionSaveServiceTimeoutSec
    ) {

        this.webClientBuilder = webClientBuilder;
        this.mapper = mapper;
        this.messageSources = messageSources;
        this.transactionSaveServiceTimeoutSec = transactionSaveServiceTimeoutSec;
        transactionSaveMicroserviceUrl = "http://" + transactionSaveServiceHost + ":" + transactionSaveServicePort;
    }

    public TransactionDto saveTransaction(TransactionDto body) {
        messageSources.outputChannelTransactions().send(MessageBuilder.withPayload(new Event(CREATE, body.getTransactionId(), body)).build());
        return body;
    }

    @Retry(name = "transaction-save-microservice")
    @CircuitBreaker(name = "transaction-save-microservice")
    @Override
    public Mono<ReportCompleteDto> getTransactionsReport(String cnpPayer, int delay, int faultPercent) {
        URI url = UriComponentsBuilder.fromUriString(transactionSaveMicroserviceUrl + "/transactions-report-from-database?cnpPayer={cnpPayer}&delay={delay}&faultPercent={faultPercent}").build(cnpPayer, delay, faultPercent);
        LOG.debug("Call to get transaction report");

        return getWebClient().get().uri(url)
                .retrieve().bodyToMono(ReportCompleteDto.class).log()
                .onErrorMap(WebClientResponseException.class, ex -> handleException(ex));
                //.timeout(Duration.ofSeconds(transactionSaveServiceTimeoutSec));
    }

    private WebClient getWebClient() {
        if (webClient == null) {
            webClient = WebClient.builder().build();
        }
        return webClient;
    }

    public Mono<Health> getTransactionStoreHealth() {
        return getHealth(transactionSaveMicroserviceUrl);
    }

    private Mono<Health> getHealth(String url) {
        url += "/actuator/health";
        LOG.debug("Will call the Health API on URL: {}", url);
        return webClient.get().uri(url).retrieve().bodyToMono(String.class)
            .map(s -> new Health.Builder().up().build())
            .onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
            .log();
    }

    private Throwable handleException(Throwable ex) {

        if (!(ex instanceof WebClientResponseException)) {
            LOG.warn("Got a unexpected error: {}, will rethrow it", ex.toString());
            return ex;
        }

        WebClientResponseException wcre = (WebClientResponseException)ex;

        switch (wcre.getStatusCode()) {

        case NOT_FOUND:
            return new NotFoundException(getErrorMessage(wcre));

        case UNPROCESSABLE_ENTITY :
            return new InvalidInputException(getErrorMessage(wcre));

        default:
            LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", wcre.getStatusCode());
            LOG.warn("Error body: {}", wcre.getResponseBodyAsString());
            return ex;
        }
    }

    private String getErrorMessage(WebClientResponseException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }
}