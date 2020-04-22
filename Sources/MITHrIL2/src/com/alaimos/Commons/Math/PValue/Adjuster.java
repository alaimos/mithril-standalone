package com.alaimos.Commons.Math.PValue;

/**
 * A function which adjusts p-values on multiple hypotheses
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 03/01/2016
 */
@FunctionalInterface
public interface Adjuster {

    double[] adjust(double... pValues);

}
