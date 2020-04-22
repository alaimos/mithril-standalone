package com.alaimos.Commons.Utils;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 26/12/2015
 */
public class Triple<E, F, G> implements Cloneable, Serializable {

    private static final long serialVersionUID = 6986881783328306404L;
    private              E    first;
    private              F    second;
    private              G    third;

    public Triple(E first, F second, G third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public E getFirst() {
        return first;
    }

    public F getSecond() {
        return second;
    }

    public G getThird() {
        return third;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Triple)) return false;
        Triple<?, ?, ?> pair = (Triple<?, ?, ?>) o;
        return Objects.equals(first, pair.first) &&
                Objects.equals(second, pair.second) &&
                Objects.equals(third, pair.third);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }

    @Override
    public Object clone() {
        Triple<?, ?, ?> clone;
        try {
            clone = (Triple<?, ?, ?>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e.getMessage());
        }
        return clone;
    }

}
