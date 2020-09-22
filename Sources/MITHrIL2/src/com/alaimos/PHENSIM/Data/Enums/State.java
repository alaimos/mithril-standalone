package com.alaimos.PHENSIM.Data.Enums;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.0
 * @since 19/08/2020
 */
public enum State {
    ACTIVE(0),
    INHIBITED(1),
    OTHERWISE(2);


    public static final int STATE_COUNT = 3;

    private final int value;

    State(int value) {
        this.value = value;
    }

    @Contract(pure = true)
    public int getValue() {
        return value;
    }

    @Nullable
    @Contract(pure = true)
    public static State fromValue(int value) {
        value = Math.round(value);
        if (value == OTHERWISE.value) {
            return OTHERWISE;
        } else if (value == ACTIVE.value) {
            return ACTIVE;
        } else if (value == INHIBITED.value) {
            return INHIBITED;
        }
        return null;
    }
}
