package com.alaimos.Commons.Math.PValue;

/**
 * A function which combines input p-values
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 03/01/2016
 */
@FunctionalInterface
public interface Combiner {

    double combine(double... pValues);

}
