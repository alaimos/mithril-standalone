package com.alaimos.Commons.Observer.ObserverImpl;

import com.alaimos.Commons.Observer.Interface.EventInterface;
import com.alaimos.Commons.Observer.Interface.EventListener;
import com.alaimos.Commons.Observer.Interface.ObserverInterface;

import java.util.HashMap;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 25/12/2015
 */
public abstract class Observer implements ObserverInterface {

    protected HashMap<String, EventListener> actions = new HashMap<>();

    /**
     * Dispatch an event
     *
     * @param event the event to dispatch
     */
    @Override
    public void dispatch(EventInterface event) {
        if (actions.containsKey(event.getEventName())) {
            actions.get(event.getEventName()).accept(event);
        }
    }

    /**
     * Add a listener so that an event can be dispatched to
     *
     * @param eventName the name of the event
     * @param listener  the listener
     * @return This object for a fluent interface
     */
    protected Observer addEventListener(String eventName, EventListener listener) {
        actions.put(eventName, listener);
        return this;
    }

}
