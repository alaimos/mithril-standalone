package com.alaimos.Commons.Utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.IntStream;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 03/01/2016
 */
public class ArrayUtils {

    public static double[] parallelMin(double[] array, double value) {
        return Arrays.stream(array).map(v -> Math.min(v, value)).toArray();
    }

    public static double[] parallelMin(@NotNull double[] array1, double[] array2) {
        return IntStream.range(0, array1.length).mapToDouble(i -> Math.min(array1[i], array2[i])).toArray();
    }

    public static double[] parallelMax(double[] array, double value) {
        return Arrays.stream(array).map(v -> Math.max(v, value)).toArray();
    }

    public static double[] parallelMax(@NotNull double[] array1, double[] array2) {
        return IntStream.range(0, array1.length).mapToDouble(i -> Math.max(array1[i], array2[i])).toArray();
    }

    @NotNull
    @Contract(pure = true)
    public static double[] cumulativeMin(@NotNull double[] array) {
        var tmp = new double[array.length];
        if (tmp.length == 0) return tmp;
        tmp[0] = array[0];
        for (var i = 1; i < tmp.length; i++) {
            tmp[i] = Math.min(tmp[i - 1], array[i]);
        }
        return tmp;
    }

    @NotNull
    @Contract(pure = true)
    public static double[] cumulativeMax(@NotNull double[] array) {
        var tmp = new double[array.length];
        if (tmp.length == 0) return tmp;
        tmp[0] = array[0];
        for (var i = 1; i < tmp.length; i++) {
            tmp[i] = Math.max(tmp[i - 1], array[i]);
        }
        return tmp;
    }

    public static int[] order(@NotNull int[] array) {
        return IntStream.range(0, array.length).boxed().sorted(Comparator.comparingInt(o -> array[o]))
                .mapToInt(v -> v).toArray();
    }

    public static int[] order(@NotNull double[] array) {
        return IntStream.range(0, array.length).boxed().sorted(Comparator.comparingDouble(o -> array[o]))
                .mapToInt(v -> v).toArray();
    }

    public static <T extends Comparable<T>> int[] order(@NotNull T[] array) {
        return IntStream.range(0, array.length).boxed().sorted(Comparator.comparing(o -> array[o]))
                .mapToInt(v -> v).toArray();
    }

    public static int[] decreasingOrder(@NotNull int[] array) {

        return IntStream.range(0, array.length).boxed().sorted((o1, o2) -> Integer.compare(array[o2], array[o1]))
                .mapToInt(v -> v).toArray();
    }

    public static int[] decreasingOrder(@NotNull double[] array) {
        return IntStream.range(0, array.length).boxed().sorted((o1, o2) -> Double.compare(array[o2], array[o1]))
                .mapToInt(v -> v).toArray();
    }

    public static <T extends Comparable<T>> int[] decreasingOrder(@NotNull T[] array) {
        return IntStream.range(0, array.length).boxed().sorted((o1, o2) -> array[o2].compareTo(array[o1]))
                .mapToInt(v -> v).toArray();
    }

    @NotNull
    @Contract(pure = true)
    public static int[] sortFromIndex(@NotNull int[] array, @NotNull int[] index) {
        if (array.length != index.length) throw new RuntimeException("Arrays should have the same length");
        var r = new int[array.length];
        for (var i = 0; i < index.length; i++) {
            r[i] = array[index[i]];
        }
        return r;
    }

    @NotNull
    @Contract(pure = true)
    public static double[] sortFromIndex(@NotNull double[] array, @NotNull int[] index) {
        if (array.length != index.length) throw new RuntimeException("Arrays should have the same length");
        var r = new double[array.length];
        for (var i = 0; i < index.length; i++) {
            r[i] = array[index[i]];
        }
        return r;
    }

    public static <T> T[] sortFromIndex(@NotNull T[] array, @NotNull int[] index) {
        if (array.length != index.length) throw new RuntimeException("Arrays should have the same length");
        @SuppressWarnings("unchecked")
        var r = (T[]) Array.newInstance(array.getClass(), array.length);
        for (var i = 0; i < index.length; i++) {
            r[i] = array[index[i]];
        }
        return r;
    }

    @NotNull
    public static double[] absInPlace(@NotNull double[] a) {
        for (var i = 0; i < a.length; i++) {
            a[i] = Math.abs(a[i]);
        }
        return a;
    }

    @NotNull
    public static double[] signumInPlace(@NotNull double[] a) {
        for (var i = 0; i < a.length; i++) {
            a[i] = Math.signum(a[i]);
        }
        return a;
    }

    @NotNull
    public static double[] sumInPlace(@NotNull double[] a1, @NotNull double[] a2) {
        assert a1.length == a2.length;
        for (var i = 0; i < a1.length; i++) {
            a1[i] = a1[i] + a2[i];
        }
        return a1;
    }

    @NotNull
    public static double[] diffInPlace(@NotNull double[] a1, @NotNull double[] a2) {
        assert a1.length == a2.length;
        for (var i = 0; i < a1.length; i++) {
            a1[i] = a1[i] - a2[i];
        }
        return a1;
    }

    @NotNull
    public static double[] mulInPlace(@NotNull double[] a1, @NotNull double[] a2) {
        assert a1.length == a2.length;
        for (var i = 0; i < a1.length; i++) {
            a1[i] = a1[i] * a2[i];
        }
        return a1;
    }

    @NotNull
    @Contract(pure = true)
    public static double[] abs(@NotNull double[] a) {
        var result = new double[a.length];
        for (var i = 0; i < a.length; i++) {
            result[i] = Math.abs(a[i]);
        }
        return result;
    }

    @NotNull
    @Contract(pure = true)
    public static double[] signum(@NotNull double[] a) {
        var result = new double[a.length];
        for (var i = 0; i < a.length; i++) {
            result[i] = Math.signum(a[i]);
        }
        return result;
    }

    @NotNull
    @Contract(pure = true)
    public static double[] sum(@NotNull double[] a1, @NotNull double[] a2) {
        assert a1.length == a2.length;
        var result = new double[a1.length];
        for (var i = 0; i < a1.length; i++) {
            result[i] = a1[i] + a2[i];
        }
        return result;
    }

    @NotNull
    @Contract(pure = true)
    public static double[] diff(@NotNull double[] a1, @NotNull double[] a2) {
        assert a1.length == a2.length;
        var result = new double[a1.length];
        for (var i = 0; i < a1.length; i++) {
            result[i] = a1[i] - a2[i];
        }
        return result;
    }

    @NotNull
    @Contract(pure = true)
    public static double[] mul(@NotNull double[] a1, @NotNull double[] a2) {
        assert a1.length == a2.length;
        var result = new double[a1.length];
        for (var i = 0; i < a1.length; i++) {
            result[i] = a1[i] * a2[i];
        }
        return result;
    }

}
