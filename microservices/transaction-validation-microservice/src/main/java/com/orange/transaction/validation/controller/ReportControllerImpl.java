package com.orange.transaction.validation.controller;

import com.orange.controller.validation.ReportController;
import com.orange.helper.builder.ReportBuilder;
import com.orange.helper.dto.ReportCompleteDto;
import com.orange.helper.exceptions.NotFoundException;
import com.orange.transaction.validation.integration.TransactionIntegrationImpl;
import io.github.resilience4j.reactor.retry.RetryExceptionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.handler.advice.RequestHandlerCircuitBreakerAdvice;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class ReportControllerImpl implements ReportController {
    private static final Logger LOG = LoggerFactory.getLogger(ReportControllerImpl.class);

    @Autowired
    TransactionIntegrationImpl integration;

    @Autowired
    ReportBuilder reportBuilder;

    @Override
    public Mono<ReportCompleteDto> getTransactionsReport(String cnpPayer, int delay, int faultPercent) {
        Mono<ReportCompleteDto> reportCompleteDtoMono = integration.getTransactionsReport(cnpPayer, delay, faultPercent)
                .onErrorMap(RetryExceptionWrapper.class, retryException -> retryException.getCause())
                .onErrorReturn(RequestHandlerCircuitBreakerAdvice.CircuitBreakerOpenException.class, getReportCompleteDto(cnpPayer))
                .doOnError(ex -> LOG.warn("getTransactionsReport failed: {}", ex.toString()))
                .log();

        return reportCompleteDtoMono;
    }

    private ReportCompleteDto getReportCompleteDto(String cnpPayer) {
        LOG.warn("Creating a fallback report for cnpPayer = {}", cnpPayer);

        if (cnpPayer.equals("111111")) {
            String errMsg = "Cnp Payer: " + cnpPayer + " not found in fallback cache!";
            LOG.warn(errMsg);
            throw new NotFoundException(errMsg);
        }
        ReportCompleteDto reportCompleteDto = reportBuilder.buildCompleteFallbackReport();

        return reportCompleteDto;
    }
}
