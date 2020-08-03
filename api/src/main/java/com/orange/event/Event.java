package com.orange.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event<K, T> {

    public enum Type {CREATE, DELETE}

    private Event.Type eventType;
    private K key;
    private T data;
    private LocalDateTime eventCreationTimestamp;

    public Event(Event.Type eventType, K key, T data) {
        this.eventType = eventType;
        this.key = key;
        this.data = data;
    }


    public int hashCode() {
        return Objects.hash(eventType, key, data);
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || o.getClass() != getClass()) return false;
        Event e = (Event) o;
        return eventType == e.eventType && key == e.key && data == e.data;
    }
}
