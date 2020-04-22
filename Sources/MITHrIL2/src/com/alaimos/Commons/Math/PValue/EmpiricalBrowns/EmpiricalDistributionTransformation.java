package com.alaimos.Commons.Math.PValue.EmpiricalBrowns;

import com.alaimos.Commons.Math.PValue.Combiners;
import org.apache.commons.math3.random.EmpiricalDistribution;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.Variance;

import java.util.function.Function;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 14/02/2017
 */
public class EmpiricalDistributionTransformation implements Function<double[], double[]> {

    private static double popVar(final double[] x) {
        return (new Variance().evaluate(x) * (x.length - 1)) / x.length;
    }

    private static double popSd(final double[] x) {
        return Math.sqrt(popVar(x));
    }

    /**
     * Standardize a set of samples using population variance
     *
     * @param sample Sample to normalize.
     * @return standardized sample.
     */
    private static double[] standardize(final double[] sample) {
        var mean = new Mean().evaluate(sample);
        var sd = popSd(sample);
        var standardizedSample = new double[sample.length];
        for (int i = 0; i < sample.length; i++) {
            standardizedSample[i] = (sample[i] - mean) / sd;
        }
        return standardizedSample;
    }

    private static EmpiricalDistribution ecdf(double[] data) {
        EmpiricalDistribution distribution = new EmpiricalDistribution();
        distribution.load(data);
        return distribution;
    }

    /**
     * Applies this function to the given argument.
     *
     * @param data the function argument
     * @return the function result
     */
    @Override
    public double[] apply(double[] data) {
        if (Combiners.allEquals(data, 0.0)) return data.clone();
        data = standardize(data);
        EmpiricalDistribution distribution = ecdf(data);
        for (int i = 0; i < data.length; i++) {
            if (data[i] < 0) {
                data[i] = 0.0;
            } else {
                data[i] = -2 * Math.log(distribution.cumulativeProbability(data[i]));
            }
        }
        return data;
    }

}
