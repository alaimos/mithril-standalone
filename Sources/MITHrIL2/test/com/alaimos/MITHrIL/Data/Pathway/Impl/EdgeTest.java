package com.alaimos.MITHrIL.Data.Pathway.Impl;

import com.alaimos.MITHrIL.Data.Pathway.Interface.PathwayInterface;
import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeType;
import com.alaimos.MITHrIL.Data.Pathway.Type.NodeType;
import org.jetbrains.annotations.Contract;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 09/12/2015
 */
public class EdgeTest {

    @Contract("_ -> !null")
    private Pathway generateTestPathway(String id) {
        return new Pathway(id, "pathwayName", null);
    }

    @Contract("_ -> !null")
    private Node generateTestNode(String id) {
        return new Node(id, "node name", NodeType.valueOf("GENE"));
    }

    @Contract("_, _, _ -> !null")
    private EdgeDescription generateTestDescription(String type, String subType, String id) {
        return new EdgeDescription(type, subType, generateTestPathway(id));
    }

    @Contract(" -> !null")
    private Edge generateTestEdge() {
        return new Edge(generateTestNode("start"), generateTestNode("end"),
                Arrays.asList(generateTestDescription("gerel", "activation", "pathway1"),
                        generateTestDescription("pprel", "expression", "pathway2")));
    }

    @Test
    public void testGetStart() throws Exception {
        assertEquals("start", generateTestEdge().getStart().getId());
    }

    @Test
    public void testSetStart() throws Exception {
        assertEquals("start1", generateTestEdge().setStart(generateTestNode("start1")).getStart().getId());
    }

    @Test
    public void testGetEnd() throws Exception {
        assertEquals("end", generateTestEdge().getEnd().getId());
    }

    @Test
    public void testSetEnd() throws Exception {
        assertEquals("end1", generateTestEdge().setEnd(generateTestNode("end1")).getEnd().getId());
    }

    @Test
    public void testIsMultiEdge() throws Exception {
        assertTrue(generateTestEdge().isMultiEdge());
    }

    @Test
    public void testGetDescriptions() throws Exception {
        assertEquals(2, generateTestEdge().getDescriptions().size());
    }

    @Test
    public void testGetDescription() throws Exception {
        assertEquals(EdgeType.valueOf("GEREL"), generateTestEdge().getDescription().getType());
    }

    @Test
    public void testAddDescription() throws Exception {
        assertEquals(3,
                generateTestEdge().addDescription(generateTestDescription("gerel", "missing interaction", "pathway3"))
                                  .getDescriptions().size());
    }

    @Test
    public void testAddDescriptions() throws Exception {
        assertEquals(4, generateTestEdge().addDescriptions(
                Arrays.asList(generateTestDescription("gerel", "missing interaction", "pathway3"),
                        generateTestDescription("pprel", "missing interaction", "pathway3"))).getDescriptions().size());
    }

    @Test
    public void testClearDescriptions() throws Exception {
        assertEquals(0, generateTestEdge().clearDescriptions().getDescriptions().size());
    }

    @Test
    public void testSetDescription() throws Exception {
        assertEquals(EdgeType.valueOf("PPREL"),
                generateTestEdge().setDescription(generateTestDescription("pprel", "missing interaction", "pathway3"))
                                  .getDescription().getType());
    }

    @Test
    public void testSetDescriptions() throws Exception {
        assertEquals(EdgeType.valueOf("PPREL"),
                generateTestEdge().setDescriptions(
                        Collections.singletonList(generateTestDescription("pprel", "missing interaction", "pathway3")))
                                  .getDescription().getType());
    }

    @Test
    public void testPartialEquals() throws Exception {
        Edge e1 = generateTestEdge(), e2 = generateTestEdge(), e3 = generateTestEdge();
        e2.clearDescriptions();
        e3.setEnd(generateTestNode("end1"));
        assertTrue(e1.partialEquals(e2));
        assertFalse(e1.partialEquals(e3));
    }

    @Test
    public void testEquals() throws Exception {
        Edge e1 = generateTestEdge(), e2 = generateTestEdge(), e3 = generateTestEdge();
        e3.setEnd(generateTestNode("end1"));
        assertTrue(e1.equals(e2));
        assertFalse(e1.equals(e3));
    }

    @Test
    public void testIsOwnedBy() throws Exception {
        Edge e = generateTestEdge();
        assertTrue(e.isOwnedBy(generateTestPathway("pathway1")));
        assertTrue(e.isOwnedBy(generateTestPathway("pathway2")));
        assertFalse(e.isOwnedBy(generateTestPathway("pathway3")));
    }

    @Test
    public void testClone() throws Exception {
        Edge e = generateTestEdge(), e1 = (Edge) e.clone();
        assertTrue(e.getStart() != e.getEnd());
        assertFalse(e.getWeightComputation() != e.getWeightComputation());
    }

    @Test
    public void testGetDescriptionsOwnedBy() throws Exception {
        Edge e = generateTestEdge();
        PathwayInterface p = generateTestPathway("pathway2");
        assertEquals(1, e.getDescriptionsOwnedBy(p).size());
        assertEquals(EdgeType.valueOf("PPREL"), e.getDescriptionsOwnedBy(p).get(0).getType());
    }
}