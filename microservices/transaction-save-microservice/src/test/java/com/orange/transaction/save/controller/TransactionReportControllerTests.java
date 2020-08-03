package com.orange.transaction.save.controller;

import com.orange.helper.dto.TransactionType;
import com.orange.helper.builder.TransactionBuilder;
import com.orange.helper.model.TransactionEntity;
import com.orange.transaction.save.persistence.TransactionRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.data.mongodb.port: 0"})
@Import(TransactionReportControllerImpl.class)
public class TransactionReportControllerTests {

    @Autowired
    private WebTestClient client;

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private TransactionBuilder transactionBuilder;

    @Before
    public void setupDb() {
        List<TransactionEntity> transactionEntities = new ArrayList<>();

        TransactionEntity transactionEntity1 = transactionBuilder.createTransactionEntity(1, TransactionType.IBAN_TO_WALLET);
        TransactionEntity transactionEntity2 = transactionBuilder.createTransactionEntity(2);
        TransactionEntity transactionEntity3 = transactionBuilder.createTransactionEntity(3, TransactionType.IBAN_TO_WALLET);
        TransactionEntity transactionEntity4 = transactionBuilder.createTransactionEntity(4, TransactionType.WALLET_TO_IBAN);

        transactionEntities.add(transactionEntity1);
        transactionEntities.add(transactionEntity2);
        transactionEntities.add(transactionEntity3);
        transactionEntities.add(transactionEntity4);

        for (TransactionEntity entity : transactionEntities) {
            StepVerifier.create(repository.save(entity))
                    .expectNextMatches(createdEntity -> entity.getTransactionId() == createdEntity.getTransactionId())
                    .verifyComplete();

        }
    }

    @Test
    public void testReportRetrieval() {
        String cnpPayer = "2750331323927";
        String ibanPayer = "RO09BCYP0000001234567890";
        getAndVerifyReport(cnpPayer, OK)
                .jsonPath("$.cnpPayer").isEqualTo(cnpPayer)
                .jsonPath("$.ibanPayer").isEqualTo(ibanPayer)
                .jsonPath("$.ibanToIbanReport.type").isEqualTo(TransactionType.IBAN_TO_IBAN.toString())
                .jsonPath("$.ibanToIbanReport.totalNumber").isEqualTo(1)
                .jsonPath("$.ibanToIbanReport.totalSum").isEqualTo(1000000)
                .jsonPath("$.ibanToIbanReport.transactionDtos.length()").isEqualTo(1);
    }

    private WebTestClient.BodyContentSpec getAndVerifyReport(String cnpPayer, HttpStatus expectedStatus) {
        return client.get()
                .uri("/transactions-report-from-database?cnpPayer=" + cnpPayer )
                .accept(APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_UTF8)
                .expectBody();
    }
}
