package com.alaimos.MITHrIL.Data.Pathway.Type;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 09/12/2015
 */
public class EdgeTypeTest {

    @Test
    public void testFromString() throws Exception {
        assertEquals(EdgeType.valueOf("ECREL"), EdgeType.fromString("ecrel"));
        assertEquals(EdgeType.valueOf("PPREL"), EdgeType.fromString("pprel"));
        assertEquals(EdgeType.valueOf("MGREL"), EdgeType.fromString("mGrEl"));
    }

    @Test
    public void testAdd() throws Exception {
        EdgeType testType = EdgeType.add("TEST_TYPE");
        assertEquals(EdgeType.valueOf("TEST_TYPE"), testType);
    }

    @Test
    public void testPrintEnum() throws Exception {
        EdgeType.printByClass(EdgeType.class);
    }
}