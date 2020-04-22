package com.alaimos.Commons.Utils;

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

    public static double[] parallelMin(double[] array1, double[] array2) {
        return IntStream.range(0, array1.length).mapToDouble(i -> Math.min(array1[i], array2[i])).toArray();
    }

    public static double[] parallelMax(double[] array, double value) {
        return Arrays.stream(array).map(v -> Math.max(v, value)).toArray();
    }

    public static double[] parallelMax(double[] array1, double[] array2) {
        return IntStream.range(0, array1.length).mapToDouble(i -> Math.max(array1[i], array2[i])).toArray();
    }

    public static double[] cumulativeMin(double[] array) {
        double[] tmp = new double[array.length];
        if (tmp.length == 0) return tmp;
        if (tmp.length >= 1) {
            tmp[0] = array[0];
            for (int i = 1; i < tmp.length; i++) {
                tmp[i] = Math.min(tmp[i - 1], array[i]);
            }
        }
        return tmp;
    }

    public static double[] cumulativeMax(double[] array) {
        double[] tmp = new double[array.length];
        if (tmp.length == 0) return tmp;
        if (tmp.length >= 1) {
            tmp[0] = array[0];
            for (int i = 1; i < tmp.length; i++) {
                tmp[i] = Math.max(tmp[i - 1], array[i]);
            }
        }
        return tmp;
    }

    public static int[] order(int[] array) {
        return IntStream.range(0, array.length).boxed().sorted(Comparator.comparingInt(o -> array[o]))
                        .mapToInt(v -> v).toArray();
    }

    public static int[] order(double[] array) {
        return IntStream.range(0, array.length).boxed().sorted(Comparator.comparingDouble(o -> array[o]))
                        .mapToInt(v -> v).toArray();
    }

    public static <T extends Comparable<T>> int[] order(T[] array) {
        return IntStream.range(0, array.length).boxed().sorted(Comparator.comparing(o -> array[o]))
                        .mapToInt(v -> v).toArray();
    }

    public static int[] decreasingOrder(int[] array) {

        return IntStream.range(0, array.length).boxed().sorted((o1, o2) -> Integer.compare(array[o2], array[o1]))
                        .mapToInt(v -> v).toArray();
    }

    public static int[] decreasingOrder(double[] array) {
        return IntStream.range(0, array.length).boxed().sorted((o1, o2) -> Double.compare(array[o2], array[o1]))
                        .mapToInt(v -> v).toArray();
    }

    public static <T extends Comparable<T>> int[] decreasingOrder(T[] array) {
        return IntStream.range(0, array.length).boxed().sorted((o1, o2) -> array[o2].compareTo(array[o1]))
                        .mapToInt(v -> v).toArray();
    }

    public static int[] sortFromIndex(int[] array, int[] index) {
        if (array.length != index.length) throw new RuntimeException("Arrays should have the same length");
        int[] r = new int[array.length];
        for (int i = 0; i < index.length; i++) {
            r[i] = array[index[i]];
        }
        return r;
    }

    public static double[] sortFromIndex(double[] array, int[] index) {
        if (array.length != index.length) throw new RuntimeException("Arrays should have the same length");
        double[] r = new double[array.length];
        for (int i = 0; i < index.length; i++) {
            r[i] = array[index[i]];
        }
        return r;
    }

    public static <T> T[] sortFromIndex(T[] array, int[] index) {
        if (array.length != index.length) throw new RuntimeException("Arrays should have the same length");
        @SuppressWarnings("unchecked")
        T[] r = (T[]) Array.newInstance(array.getClass(), array.length);
        for (int i = 0; i < index.length; i++) {
            r[i] = array[index[i]];
        }
        return r;
    }

}
