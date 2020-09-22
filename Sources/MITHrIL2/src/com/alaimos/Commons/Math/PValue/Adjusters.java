package com.alaimos.Commons.Math.PValue;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.IntStream;

import static com.alaimos.Commons.Utils.ArrayUtils.*;

/**
 * Default implementations of p-value adjusters
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 03/01/2016
 */
public class Adjusters {

    /**
     * Returns a human readable list of adjusters
     *
     * @return a list of adjuster names
     */
    @Contract(" -> !null")
    public static String[] getNames() {
        return new String[]{
                "bonferroni",
                "holm",
                "hochberg",
                "BY (Benjamini & Yekutieli)",
                "BH (Benjamini & Hochberg)",
                "None"
        };
    }

    /**
     * Returns a p-value adjuster by its name
     *
     * @param name the name of a p-value adjuster
     * @return an adjuster
     */
    public static Adjuster getByName(String name) {
        name = name.toLowerCase();
        switch (name) {
            case "bonferroni":
                return Adjusters::bonferroni;
            case "holm":
                return Adjusters::holm;
            case "hochberg":
                return Adjusters::hochberg;
            case "by":
            case "benjaminiyekutieli":
                return Adjusters::benjaminiYekutieli;
            case "none":
                return Adjusters::none;
            case "bh":
            case "fdr":
            case "benjaminihochberg":
            default:
                return Adjusters::benjaminiHochberg;
        }
    }

    /**
     * Given a set of p-values, returns p-values adjusted using Bonferroni method
     *
     * @param pValues a set of p-values
     * @return a set of corrected p-values
     */
    public static double[] bonferroni(double... pValues) {
        var n = pValues.length;
        if (n == 1) return pValues.clone();
        return Arrays.stream(pValues).map(p -> Math.min(1, n * p)).toArray();
    }

    /**
     * Given a set of p-values, returns p-values adjusted using Holm (1979) method
     *
     * @param pValues a set of p-values
     * @return a set of corrected p-values
     */
    public static double[] holm(@NotNull double... pValues) {
        if (pValues.length == 1) return pValues.clone();
        var n = pValues.length;
        var o = order(pValues);
        var ro = order(o);
        var po = sortFromIndex(pValues, o);
        var pp = IntStream.range(0, n).mapToDouble(i -> (n - i) * po[i]).toArray();
        return sortFromIndex(parallelMin(cumulativeMax(pp), 1), ro);
    }

    /**
     * Given a set of p-values, returns p-values adjusted using Hochberg (1988) method
     *
     * @param pValues a set of p-values
     * @return a set of corrected p-values
     */
    public static double[] hochberg(@NotNull double... pValues) {
        if (pValues.length == 1) return pValues.clone();
        var n = pValues.length;
        var o = decreasingOrder(pValues);
        var ro = order(o);
        var po = sortFromIndex(pValues, o);
        var pp = IntStream.range(0, n).mapToDouble(i -> (i + 1) * po[i]).toArray();
        return sortFromIndex(parallelMin(cumulativeMin(pp), 1), ro);
    }

    /**
     * Given a set of p-values, returns p-values adjusted using Benjamini & Hochberg (1995) method
     *
     * @param pValues a set of p-values
     * @return a set of corrected p-values
     */
    public static double[] benjaminiHochberg(@NotNull double... pValues) {
        if (pValues.length == 1) return pValues.clone();
        int n = pValues.length;
        int[] o = decreasingOrder(pValues), ro = order(o);
        double[] po = sortFromIndex(pValues, o),
                pp = IntStream.range(0, n).mapToDouble(i -> (n * po[i]) / (n - i)).toArray();
        return sortFromIndex(parallelMin(cumulativeMin(pp), 1), ro);
    }

    /**
     * Given a set of p-values, returns p-values adjusted using Benjamini & Yekutieli (2001) method
     *
     * @param pValues a set of p-values
     * @return a set of corrected p-values
     */
    @NotNull
    public static double[] benjaminiYekutieli(@NotNull double... pValues) {
        if (pValues.length == 1) return pValues.clone();
        int n = pValues.length;
        double q = IntStream.range(0, n).mapToDouble(i -> 1 / (((double) i) + 1)).sum();
        int[] o = decreasingOrder(pValues), ro = order(o);
        double[] po = sortFromIndex(pValues, o),
                pp = IntStream.range(0, n).mapToDouble(i -> (q * n * po[i]) / (n - i)).toArray();
        return sortFromIndex(parallelMin(cumulativeMin(pp), 1), ro);
    }

    /**
     * A pass-through p-value adjustment
     *
     * @param pValues a list of p-values
     * @return the same list of p-values
     */
    @Contract(pure = true)
    public static double[] none(@NotNull double... pValues) {
        return pValues.clone();
    }

}
