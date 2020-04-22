package com.alaimos.MITHrIL.Data.Pathway.Type;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 09/12/2015
 */
public class EdgeSubTypeTest {

    @Test
    public void testFromString() throws Exception {
        assertEquals(EdgeSubType.valueOf("COMPOUND"), EdgeSubType.fromString("compound"));
        assertEquals(EdgeSubType.valueOf("MISSING_INTERACTION"), EdgeSubType.fromString("MISSING INTERACTION"));
        assertEquals(EdgeSubType.valueOf("TFMIRNA_ACTIVATION"), EdgeSubType.fromString("TfMirna Activation"));
    }

    @Test
    public void testAdd() throws Exception {
        EdgeSubType testType = EdgeSubType.add("TEST_TYPE", 1.0, 1, "-T->");
        assertEquals(EdgeSubType.valueOf("TEST_TYPE"), testType);
        assertEquals(1.0, EdgeSubType.valueOf("TEST_TYPE").weight(), 0.0001);
        assertEquals(1, EdgeSubType.valueOf("TEST_TYPE").priority());
        assertEquals("-T->", EdgeSubType.valueOf("TEST_TYPE").symbol());
    }

    @Test
    public void testPrintEnum() throws Exception {
        EdgeSubType.printByClass(EdgeSubType.class);
    }
}