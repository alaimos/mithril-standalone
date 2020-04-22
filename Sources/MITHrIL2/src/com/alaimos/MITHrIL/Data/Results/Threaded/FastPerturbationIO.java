package com.alaimos.MITHrIL.Data.Results.Threaded;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class FastPerturbationIO {

    public final String name;
    public final Map<String, Double> value;
    public final boolean empty;

    @Contract(pure = true)
    public FastPerturbationIO() {
        this.name = null;
        this.value = null;
        this.empty = true;
    }

    @Contract(pure = true)
    public FastPerturbationIO(@NotNull String name, @NotNull Map<String, Double> value) {
        this.name = name;
        this.value = value;
        this.empty = false;
    }
}
