package com.alaimos.Commons.Observer.Interface;

import com.alaimos.Commons.Observer.ObserverImpl.Observer;

import java.util.Collection;
import java.util.List;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 25/12/2015
 */
public interface ObservableInterface {


    void addObserver(ObserverInterface o);

    void setObservers(Collection<? extends ObserverInterface> o);

    List<ObserverInterface> getObservers();

    void deleteObserver(ObserverInterface o);

    void deleteObservers();

    int countObservers();

}
