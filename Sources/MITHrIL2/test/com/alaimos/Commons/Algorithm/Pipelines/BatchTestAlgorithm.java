package com.alaimos.Commons.Algorithm.Pipelines;

import com.alaimos.Commons.Algorithm.Impl.AbstractAlgorithm;

import java.util.stream.IntStream;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 30/12/2015
 */
public class BatchTestAlgorithm extends AbstractAlgorithm<Integer> {

    public BatchTestAlgorithm() {
    }

    @Override
    public void run() {
        int value = getParameterNotNull("value", Integer.class);
        int sum = IntStream.rangeClosed(1, value).sum();
        int m = 0;
        for (int i = Integer.MIN_VALUE; i < (Integer.MAX_VALUE / value); i++) {
            m = m + i;
            /*for (int j = Integer.MIN_VALUE; j < (Integer.MAX_VALUE / value); j++) {
                m = m + i + j;
            }*/
        }
        output = sum;
    }
}
