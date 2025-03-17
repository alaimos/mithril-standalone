package com.alaimos.Commons.Observer.ObserverImpl;

import com.alaimos.Commons.Observer.Interface.ObservableInterface;
import com.alaimos.Commons.Observer.Interface.ObserverInterface;
import com.alaimos.Commons.Observer.ObserverRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 25/12/2015
 */
public abstract class Observable implements ObservableInterface {

    /**
     * A list of observers
     */
    private final ArrayList<ObserverInterface> observers = new ArrayList<>();

    /**
     * This method notifies all observers
     *
     * @param event an event object
     */
    protected synchronized void notifyObservers(Event event) {
        ObserverRegistry.injectObservers(this);
        for (var o : observers) {
            o.dispatch(event);
        }
    }

    /**
     * This method notifies all observers
     *
     * @param event the name of an event
     */
    protected synchronized void notifyObservers(String event) {
        notifyObservers(new Event(event));
    }

    /**
     * This method notifies all observers
     *
     * @param event the name of an event
     * @param data  data associated to this event
     */
    protected synchronized void notifyObservers(String event, Object data) {
        notifyObservers(new Event(event, data));
    }

    /**
     * This method adds an observer
     *
     * @param o the observer to add
     */
    @Override
    public synchronized void addObserver(ObserverInterface o) {
        observers.add(o);
    }

    /**
     * This method adds a collection of observers to the list of observers
     *
     * @param o a collection of observers
     */
    @Override
    public synchronized void setObservers(Collection<? extends ObserverInterface> o) {
        observers.addAll(o);
    }

    /**
     * This method returns a list of all registered observers
     *
     * @return a list of observers
     */
    @Override
    public List<ObserverInterface> getObservers() {
        return observers;
    }

    /**
     * This method deletes an observer
     *
     * @param o an observer object
     */
    @Override
    public synchronized void deleteObserver(ObserverInterface o) {
        observers.remove(o);
    }

    /**
     * This method deletes all observers
     */
    @Override
    public synchronized void deleteObservers() {
        observers.clear();
    }

    /**
     * This method counts all observers
     *
     * @return the count
     */
    @Override
    public int countObservers() {
        return observers.size();
    }
}
