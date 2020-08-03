package com.orange.transaction.save.service;

import com.orange.event.Event;
import com.orange.helper.exceptions.InvalidInputException;
import com.orange.helper.dto.TransactionDto;
import com.orange.helper.builder.TransactionBuilder;
import com.orange.transaction.save.persistence.TransactionRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.http.HttpStatus;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.Assert.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT, properties = {"spring.data.mongodb.port: 0"})
public class TransactionCrudTests {

    @Autowired
    private WebTestClient client;

	@Autowired
	private TransactionRepository repository;

	@Autowired
	private Sink channels;

	@Autowired
	TransactionBuilder transactionBuilder;

	private AbstractMessageChannel input = null;

	@Before
	public void setupDb() {
		input = (AbstractMessageChannel) channels.input();
		repository.deleteAll().block();
	}

	@Test
	public void testDuplicateError() {
		int transactionId = 100000;

		assertNull(repository.findByTransactionId(transactionId).block());
		sendSaveTransactionEvent(transactionId);

		assertNotNull(repository.findByTransactionId(transactionId).block());

		try {
			sendSaveTransactionEvent(transactionId);
			fail("Expected a MessagingException here!");
		} catch(MessagingException me) {
			if (me.getCause() instanceof InvalidInputException) {
				InvalidInputException iie = (InvalidInputException) me.getCause();
				assertEquals("Duplicate key, Transaction Id: " + transactionId, iie.getMessage());
			} else {
				fail("Expected a InvalidInputException as the root cause! ");
			}
		}
	}

	@Test
	public void testGetTransactionById() {

		int transactionId = 1;

		assertNull(repository.findByTransactionId(transactionId).block());
		assertEquals(0, (long)repository.count().block());

		sendSaveTransactionEvent(transactionId);

		assertNotNull(repository.findByTransactionId(transactionId).block());
		assertEquals(1, (long)repository.count().block());

		getAndVerifyTransaction(transactionId, OK)
				.jsonPath("$.transactionId").isEqualTo(transactionId);
	}

	@Test
	public void testGetTransactionInvalidParameterString() {

		getAndVerifyTransaction("/no-integer", BAD_REQUEST)
				.jsonPath("$.path").isEqualTo("/transaction/no-integer")
				.jsonPath("message").isEqualTo("Type mismatch.");
	}

	@Test
	public void testGetTransactionNotFound() {

		int transactionIdNotFound = 13;
		getAndVerifyTransaction(transactionIdNotFound, NOT_FOUND)
				.jsonPath("$.path").isEqualTo("/transaction/" + transactionIdNotFound)
				.jsonPath("$.message").isEqualTo("No transaction found for transactionId: " + transactionIdNotFound);
	}

	@Test
	public void testGetTransactionInvalidParameterNegativeValue() {
		int transactionIdInvalid = -1;

		getAndVerifyTransaction(transactionIdInvalid, UNPROCESSABLE_ENTITY)
				.jsonPath("$.path").isEqualTo("/transaction/" + transactionIdInvalid)
				.jsonPath("$.message").isEqualTo("Invalid transactionId: " + transactionIdInvalid);
	}


	private WebTestClient.BodyContentSpec getAndVerifyTransaction(int transactionId, HttpStatus expectedStatus) {
		return getAndVerifyTransaction("/" + transactionId, expectedStatus);
	}

	private WebTestClient.BodyContentSpec getAndVerifyTransaction(String transactionIdPath, HttpStatus expectedStatus) {
		return client.get()
				.uri("/transaction" + transactionIdPath)
				.accept(APPLICATION_JSON_UTF8)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON_UTF8)
				.expectBody();
	}

	private void sendSaveTransactionEvent(int transactionId) {
		TransactionDto transactionDto = transactionBuilder.createTransactionDto(transactionId);

		Event<Integer, TransactionDto> event = new Event(Event.Type.CREATE, transactionId, transactionDto);
		input.send(new GenericMessage<>(event));
	}

	private void sendDeleteTransactionEvent(int transactionId) {
		Event<Integer, TransactionDto> event = new Event(Event.Type.DELETE, transactionId, null);
		input.send(new GenericMessage<>(event));
	}

}