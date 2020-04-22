package com.alaimos.Commons.Observer.ObserverImpl;

import com.alaimos.Commons.Observer.Interface.EventInterface;

import java.util.Objects;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 25/12/2015
 */
public class Event implements EventInterface {

    private String eventName;
    private Object data;

    public Event(String eventName) {
        this(eventName, null);
    }

    public Event(String eventName, Object data) {
        this.eventName = eventName;
        this.data = data;
    }

    @Override
    public String getEventName() {
        return eventName;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return Objects.equals(eventName, event.eventName) &&
                Objects.equals(data, event.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventName, data);
    }
}
