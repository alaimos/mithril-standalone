package com.alaimos.MITHrIL.Data.Pathway.Type;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 09/12/2015
 */
public class NodeTypeTest {

    @Test
    public void testFromString() throws Exception {
        assertEquals(NodeType.valueOf("GENE"), NodeType.fromString("gene"));
        assertEquals(NodeType.valueOf("OTHER"), NodeType.fromString("test"));
        assertEquals(NodeType.valueOf("MIRNA"), NodeType.fromString("miRNA"));
    }

    @Test
    public void testAdd() throws Exception {
        NodeType testType = NodeType.add("TEST_TYPE", -1);
        assertEquals(NodeType.valueOf("TEST_TYPE"), testType);
        assertEquals(-1, NodeType.valueOf("TEST_TYPE").sign(), 0.001);
    }

    @Test
    public void testPrintEnum() throws Exception {
        NodeType.printByClass(NodeType.class);
    }
}