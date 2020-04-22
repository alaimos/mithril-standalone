package com.alaimos.MITHrIL.Data.Pathway.Impl;

import com.alaimos.MITHrIL.Data.Pathway.Type.NodeType;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @since 09/12/2015
 * @version 2.0.0.0
 */
public class NodeTest {

    @NotNull
    private List<String> generateAliasesList() {
        return Arrays.asList("alias 1", "alias2", "alias 3");
    }

    private Node generateTestNode() {
        Node n = new Node("nodeId", "node name", NodeType.valueOf("GENE"));
        n.addAliases(generateAliasesList());
        return n;
    }

    @Test
    public void testGetId() throws Exception {
        Node n = generateTestNode();
        assertEquals("nodeId", n.getId());
    }

    @Test
    public void testSetId() throws Exception {
        Node n = generateTestNode();
        n.setId("newNodeId");
        assertEquals("newNodeId", n.getId());
    }

    @Test
    public void testGetName() throws Exception {
        Node n = generateTestNode();
        assertEquals("node name", n.getName());
    }

    @Test
    public void testSetName() throws Exception {
        Node n = generateTestNode();
        n.setName("new node name");
        assertEquals("new node name", n.getName());
    }

    @Test
    public void testGetAliases() throws Exception {
        Node n = generateTestNode();
        assertEquals(generateAliasesList(), n.getAliases());
    }

    @Test
    public void testAddAliases() throws Exception {
        Node n = generateTestNode();
        List<String> newAliases = Arrays.asList("new alias 1", "new alias 2");
        n.addAliases(newAliases);
        List<String> allAliases = Arrays.asList("alias 1", "alias2", "alias 3", "new alias 1", "new alias 2");
        assertEquals(allAliases, n.getAliases());
    }

    @Test
    public void testClearAliases() throws Exception {
        Node n = generateTestNode();
        n.clearAliases();
        assertEquals(0, n.getAliases().size());
    }

    @Test
    public void testSetAliases() throws Exception {
        Node n = generateTestNode();
        List<String> newAliases = Arrays.asList("new alias 1", "new alias 2");
        n.setAliases(newAliases);
        assertEquals(newAliases, n.getAliases());
    }

    @Test
    public void testGetType() throws Exception {
        Node n = generateTestNode();
        assertEquals(NodeType.valueOf("GENE"), n.getType());
    }

    @Test
    public void testSetType() throws Exception {
        Node n = generateTestNode();
        n.setType(NodeType.valueOf("COMPOUND"));
        assertEquals(NodeType.valueOf("COMPOUND"), n.getType());
    }

    @Test
    public void testContains() throws Exception {
        Node n = generateTestNode();
        assertTrue(n.contains("node name"));
        assertTrue(n.contains("alias 1"));
        assertFalse(n.contains("alias 2"));
    }

    @Test
    public void testCompareTo() throws Exception {
        Node n = generateTestNode();
        assertEquals(0, n.compareTo(n));
    }

    @Test
    public void testEquals() throws Exception {
        Node n = generateTestNode();
        Node n1 = generateTestNode();
        n1.setId("newId");
        Node n2 = generateTestNode();
        assertTrue(n.equals(n2));
        assertFalse(n.equals(n1));
    }

    @Test
    public void testClone() throws Exception {
        Node n = generateTestNode();
        Node n1 = (Node) n.clone();
        assertFalse(n == n1);
        assertFalse(n.getAliases() == n1.getAliases());
    }
}