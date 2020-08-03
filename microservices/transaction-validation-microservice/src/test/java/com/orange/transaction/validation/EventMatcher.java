package com.orange.transaction.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.event.Event;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EventMatcher extends TypeSafeMatcher<String> {

    private static final Logger LOG = LoggerFactory.getLogger(EventMatcher.class);

    private ObjectMapper mapper = new ObjectMapper();

    private Event expectedEvent;

    private EventMatcher(Event expectedEvent) {
        this.expectedEvent = expectedEvent;
    }

    @Override
    protected boolean matchesSafely(String eventAsJson) {

        if (expectedEvent == null) return false;

        LOG.info("Convertion from event to json: {}", eventAsJson);
        Map mapEvent = convertJsonStringToMap(eventAsJson);
        mapEvent.remove("eventCreationTimestamp");

        Map mapExpectedEvent = getMapWithoutEventCreationTimestamp(expectedEvent);

        LOG.info("Comparison with expected event designed as a map: {}", mapExpectedEvent);
        return mapEvent.equals(mapExpectedEvent);
    }

    @Override
    public void describeTo(Description description) {
        String expectedJson = convertObjectToJsonString(expectedEvent);
        description.appendText(expectedJson);
    }

    public static Matcher<String> returnEventMatcher(Event expectedEvent) {
        return new EventMatcher(expectedEvent);
    }

   	private Map getMapWithoutEventCreationTimestamp(Event event) {
        Map mapEvent = convertObjectToMap(event);
        mapEvent.remove("eventCreationTimestamp");
        return mapEvent;
    }

    private Map convertObjectToMap(Object object) {
   		JsonNode node = mapper.convertValue(object, JsonNode.class);
   		return mapper.convertValue(node, Map.class);
   	}

    private String convertObjectToJsonString(Object object) {
   		try {
   			return mapper.writeValueAsString(object);
   		} catch (JsonProcessingException e) {
   			throw new RuntimeException(e);
   		}
   	}

    private Map convertJsonStringToMap(String eventAsJson) {
        try {
            return mapper.readValue(eventAsJson, new TypeReference<HashMap>(){});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
