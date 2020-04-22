package com.alaimos.Commons.Utils;

import com.alaimos.Commons.Math.PValue.EmpiricalBrowns.DataMatrix;
import com.alaimos.Commons.Math.PValue.EmpiricalBrowns.EmpiricalDistributionTransformation;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 03/01/2016
 */
public class ArrayUtilsTest {

    @Test
    public void testParallelMin() throws Exception {
        double[] v = new double[]{1, 2, 3, 4, 0, 5, 6, 7, 8},
                e = new double[]{1, 2, 3, 4, 0, 4, 4, 4, 4};
        assertArrayEquals(e, ArrayUtils.parallelMin(v, 4), 0.0);
    }

    @Test
    public void testParallelMax() throws Exception {
        double[] v = new double[]{1, 2, 3, 4, 0, 5, 6, 7, 8},
                e = new double[]{4, 4, 4, 4, 4, 5, 6, 7, 8};
        assertArrayEquals(e, ArrayUtils.parallelMax(v, 4), 0.0);
    }

    @Test
    public void testCumulativeMin() throws Exception {
        double[] v = new double[]{1, 2, 3, 4, 0, 5, 6, 7, 8},
                e = new double[]{1, 1, 1, 1, 0, 0, 0, 0, 0};
        assertArrayEquals(e, ArrayUtils.cumulativeMin(v), 0.0);
    }

    @Test
    public void testCumulativeMax() throws Exception {
        double[] v = new double[]{1, 2, 3, 4, 0, 5, 6, 7, 8},
                e = new double[]{1, 2, 3, 4, 4, 5, 6, 7, 8};
        assertArrayEquals(e, ArrayUtils.cumulativeMax(v), 0.0);
    }

    @Test
    public void testOrder() throws Exception {
        double[] v = new double[]{6, 3, 2, 9};
        int[] e = new int[]{2, 1, 0, 3};
        assertArrayEquals(e, ArrayUtils.order(v));
    }

    @Test
    public void testSortFromIndex() throws Exception {
        double[] v = new double[]{6, 3, 2, 9}, e = new double[]{2, 3, 6, 9};
        assertArrayEquals(e, ArrayUtils.sortFromIndex(v, ArrayUtils.order(v)), 0.0);
    }

    @Test
    public void testDecreasingOrder() throws Exception {
        double[] v = new double[]{6, 3, 2, 9};
        int[] e = new int[]{3, 0, 1, 2};
        assertArrayEquals(e, ArrayUtils.decreasingOrder(v));
    }

    @Test
    public void testCovarianceMatrix() throws Exception {
        DataMatrix dm = new DataMatrix(new String[]{"a", "b"}, new double[][]{
                {1, 2, 3},
                {2, 4, 6}
        });
        dm.transform(new EmpiricalDistributionTransformation());
        System.out.println(dm.computeCovarianceMatrix());
    }
}