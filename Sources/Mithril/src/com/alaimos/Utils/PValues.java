package com.alaimos.Utils;

import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.Arrays;
import java.util.Collections;

public class PValues {

    static class SortingHelper implements Comparable<SortingHelper> {

        public static double[] toValueArray(SortingHelper[] a) {
            double[] values = new double[a.length];
            for (int i = 0; i < a.length; i++) {
                values[i] = a[i].value;
            }
            return values;
        }

        public static int[] toIndexArray(SortingHelper[] a) {
            int[] idxs = new int[a.length];
            for (int i = 0; i < a.length; i++) {
                idxs[i] = a[i].index;
            }
            return idxs;
        }

        public static double[] toDoubleIndexArray(SortingHelper[] a) {
            double[] idxs = new double[a.length];
            for (int i = 0; i < a.length; i++) {
                idxs[i] = a[i].index;
            }
            return idxs;
        }

        public static SortingHelper[] fromValueArray(double[] a) {
            SortingHelper[] b = new SortingHelper[a.length];
            for (int i = 0; i < a.length; i++) {
                b[i] = new SortingHelper(i, a[i]);
            }
            return b;
        }

        public int index = 0;
        public double value = Double.NEGATIVE_INFINITY;

        public SortingHelper() {
            super();
        }

        public SortingHelper(int i, double d) {
            super();
            index = i;
            value = d;
        }

        @Override
        public int compareTo(SortingHelper o) {
            return Double.compare(value, o.value);
        }
    }

    public static double[] cumulativeMinimum(double[] a) {
        double[] b = new double[a.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = -a[i];
        }
        b = cumulativeMaximum(b);
        for (int i = 0; i < b.length; i++) {
            b[i] = -b[i];
        }
        return b;
    }

    public static double[] cumulativeMaximum(double[] a) {
        double[] b = new double[a.length];
        if (b.length >= 1) {
            b[0] = a[0];
            for (int i = 1; i < a.length; i++) {
                b[i] = Math.max(a[i], b[i - 1]);
            }
        }
        return b;
    }

    public static double[] fdrAdjustment(double[] pvalues) {
        double[] tmp = pvalues.clone();
        SortingHelper[] values = SortingHelper.fromValueArray(tmp);
        Arrays.sort(values, Collections.reverseOrder());
        double[] inverse = SortingHelper.toValueArray(values);
        double[] indexes = SortingHelper.toDoubleIndexArray(values);
        values = SortingHelper.fromValueArray(indexes);
        Arrays.sort(values);
        int[] orderedIndexes = SortingHelper.toIndexArray(values);
        int n = pvalues.length, l = pvalues.length;
        for (int i = 0; i < n; i++) {
            inverse[i] = ((double) n / ((double) (l--))) * inverse[i];
        }
        inverse = cumulativeMinimum(inverse);
        double[] result = new double[inverse.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = Math.min(inverse[orderedIndexes[i]], 1.0);
        }
        return result;
    }

    public static double[] bonferroniAdjustment(double[] pvalues) {
        double n = pvalues.length;
        double[] result = new double[pvalues.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = Math.min(n * pvalues[i], 1.0);
        }
        return result;
    }

    public static double normalCombine(double p1, double p2) {
        NormalDistribution n = new NormalDistribution(0, 1);
        if (p1 > 1) p1 = 1;
        if (p2 > 1) p2 = 1;
        if (p1 < 0) p1 = 0;
        if (p2 < 0) p2 = 0;
        double comb = n.inverseCumulativeProbability(p1) + n.inverseCumulativeProbability(p2);
        comb /= Math.sqrt(2);
        comb = n.cumulativeProbability(comb);
        if (Double.isNaN(comb)) {
            if (!Double.isNaN(p1)) {
                return p1;
            } else {
                return p2;
            }
        }
        return comb;
    }

    public static double fisherCombine(double p1, double p2) {
        double k = p1 * p2;
        double comb = k - k * (Math.log(p1) + Math.log(p2));
        if (Double.isNaN(comb)) {
            if (!Double.isNaN(p1)) {
                return p1;
            } else {
                return p2;
            }
        }
        return comb;
    }

}