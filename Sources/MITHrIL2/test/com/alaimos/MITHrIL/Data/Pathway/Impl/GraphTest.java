package com.alaimos.MITHrIL.Data.Pathway.Impl;

import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeSubType;
import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeType;
import com.alaimos.MITHrIL.Data.Pathway.Type.NodeType;
import org.jetbrains.annotations.Contract;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 10/12/2015
 */
public class GraphTest {

    @Contract("_ -> !null")
    private Pathway generateTestPathway(String id) {
        return new Pathway(id, "pathwayName", null);
    }

    @Contract("_ -> !null")
    private Node generateTestNode(String id) {
        return new Node(id, "node name " + id, NodeType.valueOf("GENE"));
    }

    @Contract("_, _, _ -> !null")
    private EdgeDescription generateTestDescription(String type, String subType, Pathway owner) {
        return new EdgeDescription(type, subType, owner);
    }

    @Contract("_, _ -> !null")
    private Edge generateTestEdge(Node start, Node end) {
        return new Edge(start, end, generateTestDescription("gerel", "activation", null));
    }

    private Graph generateTestGraph() {
        Node n1 = generateTestNode("n1"), n2 = generateTestNode("n2"), n3 = generateTestNode("n3"), n4 =
                generateTestNode("n4");
        Pathway owner = generateTestPathway("owner");
        Graph g = new Graph();
        g.setOwner(owner);
        g.addNode(n1).addNode(n2).addNode(n3).addNode(n4);
        g.addEdge(generateTestEdge(n1, n2)).addEdge(generateTestEdge(n2, n3)).addEdge(generateTestEdge(n3, n4));
        g.setEndpoints(Arrays.asList("n4", "n44"));
        return g;
    }

    @Test
    public void testIsEmpty() throws Exception {
        assertFalse(generateTestGraph().isEmpty());
    }

    @Test
    public void testGetNode() throws Exception {
        Graph g = generateTestGraph();
        assertNotNull(g.getNode("n1"));
        assertEquals("node name n1", g.getNode("n1").getName());
    }

    @Test
    public void testAddNode() throws Exception {
        Node t = generateTestNode("t");
        assertEquals(t, generateTestGraph().addNode(t).getNode("t"));
    }

    @Test
    public void testFindNode() throws Exception {
        Graph g = generateTestGraph();
        NodeInterface f = g.findNode("node name n1");
        assertNotNull(f);
        assertEquals("n1", f.getId());
    }

    @Test
    public void testHasNode() throws Exception {
        assertTrue(generateTestGraph().hasNode("n1"));
        assertFalse(generateTestGraph().hasNode("n9"));
    }


    @Test
    public void testGetEndpoints() throws Exception {
        assertEquals(1, generateTestGraph().getEndpoints().size());
    }

    @Test
    public void testSetEndpoints() throws Exception {
        assertEquals(2, generateTestGraph().setEndpoints(Arrays.asList("n1", "n4")).getEndpoints().size());
    }

    @Test
    public void testInDegree() throws Exception {
        Graph g = generateTestGraph();
        assertEquals(0, g.inDegree(g.getNode("n1")));
        assertEquals(1, g.inDegree(g.getNode("n2")));
    }

    @Test
    public void testOutDegree() throws Exception {
        Graph g = generateTestGraph();
        assertEquals(1, g.outDegree(g.getNode("n1")));
        assertEquals(0, g.outDegree(g.getNode("n4")));
    }

    @Test
    public void testIngoingNodes() throws Exception {
        Graph g = generateTestGraph();
        assertEquals(0, g.ingoingNodes(g.getNode("n1")).size());
        assertEquals(1, g.ingoingNodes(g.getNode("n2")).size());
        assertEquals(g.getNode("n1"), g.ingoingNodes(g.getNode("n2")).get(0));
    }

    @Test
    public void testOutgoingNodes() throws Exception {
        Graph g = generateTestGraph();
        assertEquals(0, g.outgoingNodes(g.getNode("n4")).size());
        assertEquals(1, g.outgoingNodes(g.getNode("n2")).size());
        assertEquals(g.getNode("n3"), g.outgoingNodes(g.getNode("n2")).get(0));
    }

    @Test
    public void testUpstreamNodes() throws Exception {
        Graph g = generateTestGraph();
        assertEquals(0, g.upstreamNodes(g.getNode("n1")).size());
        assertEquals(2, g.upstreamNodes(g.getNode("n3")).size());
    }

    @Test
    public void testDownstreamNodes() throws Exception {
        Graph g = generateTestGraph();
        assertEquals(0, g.downstreamNodes(g.getNode("n4")).size());
        assertEquals(2, g.downstreamNodes(g.getNode("n2")).size());
    }

    @Test
    public void testGetNodes() throws Exception {
        assertEquals(4, generateTestGraph().getNodes().size());
    }

    @Test
    public void testAddEdge() throws Exception {
        Graph g = generateTestGraph();
        assertEquals(0, g.outDegree(g.getNode("n4")));
        g.addEdge(g.getNode("n4"), g.getNode("n1"), EdgeType.valueOf("ECREL"), EdgeSubType.valueOf("ACTIVATION"));
        assertEquals(1, g.outDegree(g.getNode("n4")));
    }

    @Test
    public void testGetEdge() throws Exception {
        Graph g = generateTestGraph();
        assertNull(g.getEdge("n1", "n4"));
        assertNotNull(null, g.getEdge("n1", "n2"));

    }

    @Test
    public void testHasEdge() throws Exception {
        Graph g = generateTestGraph();
        assertFalse(g.hasEdge("n1", "n4"));
        assertTrue(g.hasEdge("n1", "n2"));
    }

    @Test
    public void testGetEdges() throws Exception {
        assertNotNull(generateTestGraph().getEdges().size());
    }

    @Test
    public void testCountNodes() throws Exception {
        assertEquals(4, generateTestGraph().countNodes());
    }

    @Test
    public void testCountEdges() throws Exception {
        assertEquals(3, generateTestGraph().countEdges());
    }

    @Test
    public void testIterator() throws Exception {
        assertNotNull(generateTestGraph().iterator());
    }

    @Test
    public void testGetOwner() throws Exception {
        Graph g = generateTestGraph();
        assertNotNull(g.getOwner());
        assertEquals("owner", g.getOwner().getId());
    }

    @Test
    public void testSetOwner() throws Exception {
        Graph g = generateTestGraph();
        g.setOwner(null);
        assertNull(g.getOwner());
    }

    @Test
    public void testIsOwnedBy() throws Exception {
        Graph g = generateTestGraph();
        assertTrue(g.isOwnedBy(generateTestPathway("owner")));
    }

    @Test
    public void testClone() throws Exception {
        Graph g = generateTestGraph(), g1 = (Graph) g.clone();
        assertNotSame(g, g1);
        assertEquals(g1, g);
    }

    @Test
    public void testRemove() throws Exception {
        Graph g = generateTestGraph();
        assertTrue(g.hasNode("n1"));
        assertTrue(g.removeNode("n1"));
        assertFalse(g.hasNode("n1"));
    }
}