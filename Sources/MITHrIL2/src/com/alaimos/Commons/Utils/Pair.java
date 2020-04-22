package com.alaimos.Commons.Utils;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 26/12/2015
 */
public class Pair<E, F> implements Cloneable, Serializable {

    private static final long serialVersionUID = -8146960515032238074L;
    E first;
    F second;

    public Pair(E first, F second) {
        this.first = first;
        this.second = second;
    }

    public E getFirst() {
        return first;
    }

    public F getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) &&
                Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public Object clone() {
        Pair<?, ?> clone;
        try {
            clone = (Pair<?, ?>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e.getMessage());
        }
        return clone;
    }

}
