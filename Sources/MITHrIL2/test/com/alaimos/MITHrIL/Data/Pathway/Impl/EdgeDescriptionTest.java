package com.alaimos.MITHrIL.Data.Pathway.Impl;

import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeSubType;
import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeType;
import org.jetbrains.annotations.Contract;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 09/12/2015
 */
public class EdgeDescriptionTest {

    @Contract(" -> !null")
    private Pathway generateTestPathway() {
        return new Pathway("pathwayId", "pathwayName", null);
    }

    @Contract(" -> !null")
    private EdgeDescription generateTestDescription() {
        return new EdgeDescription("geRel", "missing interaction", generateTestPathway());
    }

    @Test
    public void testGetType() throws Exception {
        assertEquals(EdgeType.valueOf("GEREL"), generateTestDescription().getType());
    }

    @Test
    public void testSetType() throws Exception {
        assertEquals(EdgeType.valueOf("PPREL"), generateTestDescription().setType(EdgeType.valueOf("PPREL")).getType());
    }

    @Test
    public void testGetSubType() throws Exception {
        assertEquals(EdgeSubType.valueOf("MISSING_INTERACTION"), generateTestDescription().getSubType());
    }

    @Test
    public void testSetSubType() throws Exception {
        assertEquals(EdgeSubType.valueOf("ACTIVATION"),
                generateTestDescription().setSubType(EdgeSubType.valueOf("ACTIVATION")).getSubType());
    }

    @Test
    public void testEquals() throws Exception {
        EdgeDescription d = generateTestDescription(), d1 = generateTestDescription();
        assertTrue(d.equals(d1));
    }

    @Test
    public void testClone() throws Exception {
        EdgeDescription d = generateTestDescription(), d1 = (EdgeDescription) d.clone();
        assertTrue(d.equals(d1));
        assertTrue(d.getOwner() == d1.getOwner());
    }

    @Test
    public void testSetOwner() throws Exception {
        assertEquals(null, generateTestDescription().setOwner(null).getOwner());
    }

    @Test
    public void testGetOwner() throws Exception {
        assertEquals(generateTestPathway(), generateTestDescription().getOwner());
    }

    @Test
    public void testIsOwnedBy() throws Exception {
        assertTrue(generateTestDescription().isOwnedBy(generateTestPathway()));
    }
}