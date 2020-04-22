package com.alaimos.Commons.Utils;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 26/12/2015
 */
public class MutablePair<E, F> extends Pair<E, F> {

    public MutablePair(E first, F second) {
        super(first, second);
    }

    public MutablePair<E, F> setFirst(E first) {
        this.first = first;
        return this;
    }

    public MutablePair<E, F> setSecond(F second) {
        this.second = second;
        return this;
    }

}
