package com.alaimos.Commons.Observer;

import com.alaimos.Commons.Observer.Interface.ObservableInterface;
import com.alaimos.Commons.Observer.Interface.ObserverInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class maintains a registry of all observer organized by observable.
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 21/04/16
 */
public final class ObserverRegistry {

    /**
     * A registry of observers
     */
    private static Map<Class<? extends ObservableInterface>, List<ObserverInterface>> observerRegistry =
            new HashMap<>();

    /**
     * A registry of observable
     */
    private static Map<Class<? extends ObservableInterface>, List<ObservableInterface>> observableRegistry =
            new HashMap<>();

    /**
     * This method registers/injects a new observer for a specific observable class
     *
     * @param observable the observable class
     * @param observer   the observer object to register/inject
     */
    public static void registerObserver(Class<? extends ObservableInterface> observable, ObserverInterface observer) {
        if (!observerRegistry.containsKey(observable)) {
            observerRegistry.put(observable, new ArrayList<>());
        }
        observerRegistry.get(observable).add(observer);
        injectObservers(observable, observer);
    }

    /**
     * This method is an utility used to inject observers to all observable registered class
     *
     * @param observable the observable class
     * @param observer   the observer
     */
    private static void injectObservers(Class<? extends ObservableInterface> observable, ObserverInterface observer) {
        if (observableRegistry.containsKey(observable)) {
            for (var o : observableRegistry.get(observable)) {
                o.addObserver(observer);
            }
        }
    }

    /**
     * This method injects all observers in an observable
     *
     * @param observable ths observable object
     */
    public static void injectObservers(ObservableInterface observable) {
        var clz = observable.getClass();
        if (!observableRegistry.containsKey(clz)) {
            observableRegistry.put(clz, new ArrayList<>());
        }
        if (!observableRegistry.get(clz).contains(observable)) {
            observableRegistry.get(clz).add(observable);
            if (observerRegistry.containsKey(clz)) {
                observable.setObservers(observerRegistry.get(clz));
            }
        }
    }


}
