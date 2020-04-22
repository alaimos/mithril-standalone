package com.alaimos.Commons.Math.PValue;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 03/01/2016
 */
public class CombinersTest {

    protected static final double[] FIRST  = new double[]{0.0001, 0.0001, 0.9999, 0.9999};
    protected static final double[] SECOND = new double[]{0.016, 0.067, 0.250, 0.405, 0.871};

    @Test
    public void testFisher() throws Exception {
        assertTrue(Combiners.fisher(FIRST) < 0.01);
        assertFalse(Combiners.fisher(SECOND) < 0.01);
    }

    @Test
    public void testStouffer() throws Exception {
        assertFalse(Combiners.stouffer(FIRST) < 0.1);
        assertTrue(Combiners.stouffer(SECOND) < 0.1);
    }

    @Test
    public void testMean() throws Exception {
        assertFalse(Combiners.mean(FIRST) < 0.1);
        assertTrue(Combiners.mean(SECOND) < 0.1);
    }

    @Test
    public void testLogit() throws Exception {
        assertFalse(Combiners.logit(FIRST) < 0.1);
        assertTrue(Combiners.logit(SECOND) < 0.06);
    }

    @Test
    public void testWilkinson() throws Exception {
        assertTrue(Combiners.wilkinsonCombiner().combine(FIRST) < 0.001);
        assertFalse(Combiners.wilkinsonCombiner().combine(SECOND) < 0.05);
    }

    @Test
    public void testSumOfP() throws Exception {
        assertFalse(Combiners.sumOfP(FIRST) < 0.1);
        assertTrue(Combiners.sumOfP(SECOND) < 0.1);
    }

    @Test
    public void testVoteCounting() throws Exception {
        assertEquals(0.6875, Combiners.voteCountingCombiner().combine(FIRST), 0.001);
        assertEquals(0.1875, Combiners.voteCountingCombiner().combine(SECOND), 0.001);
    }

    @Test
    public void testTwoSidedToOneSided() throws Exception {
        assertEquals(0.025, Combiners.twoSidedToOneSided(0.05, false), 0.0001);
        assertEquals(0.975, Combiners.twoSidedToOneSided(0.05, true), 0.0001);
    }
}