package com.alaimos.MITHrIL.Data.Pathway.Impl;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @since 10/12/2015
 * @version 2.0.0.0
 */
public class PathwayTest {

    private Pathway generateTestPathway() {
        Pathway p = new Pathway("pathwayId", "pathway name", null, "category1; category2; ;category3");
        p.setImage("pathwayImage").setUrl("pathwayUrl");
        return p;
    }

    @Test
    public void testIsEmpty() throws Exception {
        assertFalse(generateTestPathway().isEmpty());
    }

    @Test
    public void testGetId() throws Exception {
        assertEquals("pathwayId", generateTestPathway().getId());
    }

    @Test
    public void testSetId() throws Exception {
        assertEquals("pathwayId1", generateTestPathway().setId("pathwayId1").getId());
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals("pathway name", generateTestPathway().getName());
    }

    @Test
    public void testSetName() throws Exception {
        assertEquals("pathway name 1", generateTestPathway().setName("pathway name 1").getName());
    }

    @Test
    public void testGetImage() throws Exception {
        assertEquals("pathwayImage", generateTestPathway().getImage());
    }

    @Test
    public void testSetImage() throws Exception {
        assertEquals("pathwayImage1", generateTestPathway().setImage("pathwayImage1").getImage());
    }

    @Test
    public void testGetUrl() throws Exception {
        assertEquals("pathwayUrl", generateTestPathway().getUrl());
    }

    @Test
    public void testSetUrl() throws Exception {
        assertEquals("pathwayUrl1", generateTestPathway().setUrl("pathwayUrl1").getUrl());
    }

    @Test
    public void testHasGraph() throws Exception {
        assertFalse(generateTestPathway().hasGraph());
    }

    @Test
    public void testGetGraph() throws Exception {
        assertEquals(null, generateTestPathway().getGraph());
    }

    @Test
    public void testSetGraph() throws Exception {
        assertEquals(0, generateTestPathway().setGraph(new Graph()).getGraph().countNodes());
    }

    @Test
    public void testGetCategories() throws Exception {
        assertEquals(3, generateTestPathway().getCategories().size());
    }

    @Test
    public void testAddCategory() throws Exception {
        assertEquals(4, generateTestPathway().addCategory("category4").getCategories().size());
    }

    @Test
    public void testAddCategories() throws Exception {
        assertEquals(5, generateTestPathway().addCategories(Arrays.asList("category4", "category5"))
                                             .getCategories().size());
    }

    @Test
    public void testClearCategories() throws Exception {
        assertEquals(0, generateTestPathway().clearCategories().getCategories().size());
    }

    @Test
    public void testSetCategory() throws Exception {
        assertEquals(1, generateTestPathway().setCategory("category4").getCategories().size());
    }

    @Test
    public void testSetCategories() throws Exception {
        assertEquals(2, generateTestPathway().setCategories(Arrays.asList("category4", "category5"))
                                             .getCategories().size());
    }

    @Test
    public void testHasCategory() throws Exception {
        assertTrue(generateTestPathway().hasCategory("category2"));
        assertFalse(generateTestPathway().hasCategory("category4"));
    }

    @Test
    public void testEquals() throws Exception {
        Pathway p1 = generateTestPathway(), p2 = generateTestPathway(), p3 = generateTestPathway();
        p3.setId("newId");
        assertTrue(p1.equals(p2));
        assertFalse(p1.equals(p3));
    }

    @Test
    public void testClone() throws Exception {
        Pathway p1 = generateTestPathway(), p2 = (Pathway) p1.clone();
        assertTrue(p1 != p2);
        assertTrue(p1.getCategories() != p2.getCategories());
        assertEquals(p1.getCategories(), p2.getCategories());
    }
}