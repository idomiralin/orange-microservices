package com.orange.transaction.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.helper.dto.TransactionDto;
import com.orange.event.Event;
import com.orange.helper.builder.TransactionBuilder;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.orange.event.Event.Type.CREATE;
import static com.orange.event.Event.Type.DELETE;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EventMatcherTests {

	ObjectMapper mapper = new ObjectMapper();

	@Autowired
    TransactionBuilder transactionBuilder;

    @Test
    public void testSerializedEventWithEventObject() throws JsonProcessingException {
		TransactionDto transactionDto1 = transactionBuilder.createTransactionDto(1);
		TransactionDto transactionDto2 = transactionBuilder.createTransactionDto(2);

		Event<Integer, TransactionDto> event1 = new Event<>(CREATE, 1, transactionDto1);
		Event<Integer, TransactionDto> event2 = new Event<>(CREATE, 1, transactionDto1);
		Event<Integer, TransactionDto> event3 = new Event<>(DELETE, 2, null);
		Event<Integer, TransactionDto> event4 = new Event<>(CREATE, 2, transactionDto2);

		String event1ToJSonSerialized = mapper.writeValueAsString(event1);

		MatcherAssert.assertThat(event1ToJSonSerialized, Matchers.is(EventMatcher.returnEventMatcher(event2)));
		MatcherAssert.assertThat(event1ToJSonSerialized, Matchers.not(EventMatcher.returnEventMatcher(event3)));
		MatcherAssert.assertThat(event1ToJSonSerialized, Matchers.not(EventMatcher.returnEventMatcher(event4)));
    }
}
