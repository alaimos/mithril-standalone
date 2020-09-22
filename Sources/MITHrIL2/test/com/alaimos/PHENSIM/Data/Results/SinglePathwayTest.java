package com.alaimos.PHENSIM.Data.Results;

import com.alaimos.PHENSIM.Data.Enums.State;
import junit.framework.TestCase;

public class SinglePathwayTest extends TestCase {

    private SinglePathway sp;
    private SinglePathway spVirtual;

    public void setUp() throws Exception {
        super.setUp();
        sp = new SinglePathway("test", 100);
        for (var i = 0; i < 80; i++) {
            sp.incrementNodeCounter("test", State.ACTIVE);
        }
        for (var i = 0; i < 20; i++) {
            sp.incrementNodeCounter("test", State.INHIBITED);
        }
        for (var i = 0; i < 40; i++) {
            sp.incrementPathwayCounter(State.ACTIVE);
        }
        for (var i = 0; i < 60; i++) {
            sp.incrementPathwayCounter(State.INHIBITED);
        }
        spVirtual = new SinglePathway("test1", 100, true, sp);
    }

    public void testGetPathwayId() {
        assertEquals("test", sp.getPathwayId());
    }

    public void testGetNumberOfIterations() {
        assertEquals(100, sp.getNumberOfSimulations());
    }

    public void testSetNumberOfIterations() {
        sp.setNumberOfSimulations(101);
        assertEquals(101, sp.getNumberOfSimulations());
        sp.setNumberOfSimulations(100);
        assertEquals(100, sp.getNumberOfSimulations());
    }

    public void testResetCounters() {
        sp.resetCounters();
        assertEquals(0, sp.getPathwayCounter(State.ACTIVE));
        assertEquals(0, sp.getCounter("test", State.ACTIVE));
    }

    public void testIncrementNodeCounter() {
        assertEquals(80, sp.getCounter("test", State.ACTIVE));
        assertEquals(20, sp.getCounter("test", State.INHIBITED));
    }

    public void testIncrementPathwayCounter() {
        assertEquals(40, sp.getPathwayCounter(State.ACTIVE));
        assertEquals(60, sp.getPathwayCounter(State.INHIBITED));
    }

    public void testIncrementNodeCounterVirtual() {
        assertEquals(80, spVirtual.getCounter("test", State.ACTIVE));
        spVirtual.incrementNodeCounter("test", State.ACTIVE);
        assertEquals(80, spVirtual.getCounter("test", State.ACTIVE));
    }

    public void testGetLogProbability() {
        var p = 1 / ((double) sp.getNumberOfSimulations() * 1000.0);
        var a = 80 + p;
        var i = 20 + p;
        var s = a + i + p;
        assertEquals(Math.log(a / s), sp.getLogProbability("test", State.ACTIVE));
        assertEquals(Math.log(i / s), sp.getLogProbability("test", State.INHIBITED));
        assertEquals(Math.log(a / s), spVirtual.getLogProbability("test", State.ACTIVE));
        assertEquals(Math.log(i / s), spVirtual.getLogProbability("test", State.INHIBITED));
    }

    public void testGetPathwayLogProbability() {
        var p = 1 / ((double) sp.getNumberOfSimulations() * 1000.0);
        var a = 40 + p;
        var i = 60 + p;
        var s = a + i + p;
        assertEquals(Math.log(a / s), sp.getPathwayLogProbability(State.ACTIVE));
        assertEquals(Math.log(i / s), sp.getPathwayLogProbability(State.INHIBITED));
        assertEquals(Math.log(1.0 / 3), spVirtual.getPathwayLogProbability(State.ACTIVE));
        assertEquals(Math.log(1.0 / 3), spVirtual.getPathwayLogProbability(State.INHIBITED));
    }

    public void testGetActivityScore() {
        var p = 1 / ((double) sp.getNumberOfSimulations() * 1000.0);
        var a = 80 + p;
        var i = 20 + p;
        var s = a + i + p;
        var as = Math.abs(Math.log(a / s) - Math.log(1 - (a / s)));
        assertEquals(as, sp.getActivityScore("test"));
        assertEquals(as, spVirtual.getActivityScore("test"));
    }

    public void testGetPathwayActivityScore() {
        var p = 1 / ((double) sp.getNumberOfSimulations() * 1000.0);
        var a = 40 + p;
        var i = 60 + p;
        var s = a + i + p;
        var as = -Math.abs(Math.log(i / s) - Math.log(1 - (i / s)));
        assertEquals(as, sp.getPathwayActivityScore());
        assertTrue(Double.isNaN(spVirtual.getPathwayActivityScore()));
    }
}