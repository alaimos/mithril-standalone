package com.alaimos.MITHrIL.Data.Pathway.Impl;

import com.alaimos.MITHrIL.Data.Pathway.Interface.PathwayInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.MITHrIL.Data.Pathway.Type.NodeType;
import org.jetbrains.annotations.Contract;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 10/12/2015
 */
public class RepositoryTest {

    @Contract("_ -> !null")
    private Pathway generateTestPathway(String id) {
        return new Pathway(id, "pathwayName", null, "category1; category2");
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

    private Graph generateTestGraph(Pathway owner) {
        Node n1 = generateTestNode("n1"), n2 = generateTestNode("n2"), n3 = generateTestNode("n3"), n4 =
                generateTestNode("n4");
        Graph g = new Graph();
        g.addNode(n1).addNode(n2).addNode(n3).addNode(n4);
        g.addEdge(generateTestEdge(n1, n2)).addEdge(generateTestEdge(n2, n3)).addEdge(generateTestEdge(n3, n4));
        g.setEndpoints(Arrays.asList("n4", "n44"));
        g.setOwner(owner);
        return g;
    }

    private Repository generateTestRepository() {
        Pathway o = generateTestPathway("owner");
        Repository r = new Repository();
        o.setGraph(generateTestGraph(o));
        r.add(o);
        return r;
    }

    @Test
    public void testAddDecoys() throws Exception {
        Repository r = generateTestRepository();
        r.addDecoys(100L);
        assertEquals(2, r.size());
        assertTrue(r.containsPathway("ownerd"));
        assertEquals(4, r.getPathwayById("ownerd").getGraph().countNodes());
        assertTrue(r.getPathwayById("ownerd").getGraph().hasNode("n1"));
    }

    @Test
    public void testContainsPathway() throws Exception {
        assertTrue(generateTestRepository().containsPathway("owner"));
    }

    @Test
    public void testGetPathwayById() throws Exception {
        assertNotNull(generateTestRepository().getPathwayById("owner"));
    }

    @Test
    public void testGetPathwaysByCategory() throws Exception {
        List<PathwayInterface> ps = generateTestRepository().getPathwaysByCategory("category1");
        assertEquals(1, ps.size());
        assertEquals("owner", ps.get(0).getId());
    }

    @Test
    public void testGetPathwayIdsByCategory() throws Exception {
        List<String> ps = generateTestRepository().getPathwayIdsByCategory("category1");
        assertEquals(1, ps.size());
        assertEquals("owner", ps.get(0));
    }

    @Test
    public void testGetPathwaysByNodeId() throws Exception {
        List<String> ps = generateTestRepository().getPathwaysByNodeId("n1");
        assertEquals(1, ps.size());
        assertEquals("owner", ps.get(0));
    }

    @Test
    public void testOwner() throws Exception {
        RepositoryInterface r = generateTestRepository();
        assertTrue(r.stream().map(p -> p.getGraph().getEdgesStream().flatMap(e -> e.getDescriptions().stream())
                                        .allMatch(d -> d.isOwnedBy(p))).allMatch(Boolean::booleanValue));
    }

    @Test
    public void testSize() throws Exception {
        assertEquals(1, generateTestRepository().size());
    }

    @Test
    public void testIsEmpty() throws Exception {
        assertFalse(generateTestRepository().isEmpty());
    }

    @Test
    public void testContains() throws Exception {
        Repository r = generateTestRepository();
        assertTrue(r.contains(r.getPathwayById("owner")));
    }

    @Test
    public void testIterator() throws Exception {
        assertNotNull(generateTestRepository().iterator());
    }

    @Test
    public void testAdd() throws Exception {
        assertEquals(1, generateTestRepository().size());
    }

    @Test
    public void testRemove() throws Exception {
        Repository r = generateTestRepository();
        r.remove(r.getPathwayById("owner"));
        assertEquals(0, r.size());
    }

    @Test
    public void testClear() throws Exception {
        Repository r = generateTestRepository();
        r.clear();
        assertEquals(0, r.size());
    }

    @Test
    public void testClone() throws Exception {
        Repository r = generateTestRepository(), r1 = (Repository) r.clone();
        assertFalse(r == r1);
        assertEquals(r.size(), r1.size());
        assertEquals(r.getPathwayById("owner"), r1.getPathwayById("owner"));
        assertFalse(r.getPathwayById("owner") == r1.getPathwayById("owner"));
    }
}