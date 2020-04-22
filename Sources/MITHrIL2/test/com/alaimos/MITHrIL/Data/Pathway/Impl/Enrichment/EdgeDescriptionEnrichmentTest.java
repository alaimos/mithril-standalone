package com.alaimos.MITHrIL.Data.Pathway.Impl.Enrichment;

import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeSubType;
import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 11/12/2015
 */
public class EdgeDescriptionEnrichmentTest {

    private EdgeDescriptionEnrichment getTestEdgeDescriptionEnrichment() {
        return new EdgeDescriptionEnrichment(EdgeType.valueOf("ECREL"), EdgeSubType.valueOf("ACTIVATION"));
    }

    @Test
    public void testGetType() throws Exception {
        assertEquals(EdgeType.valueOf("ECREL"), getTestEdgeDescriptionEnrichment().getType());
    }

    @Test
    public void testSetType() throws Exception {
        assertEquals(EdgeType.valueOf("PPREL"),
                getTestEdgeDescriptionEnrichment().setType(EdgeType.valueOf("PPREL")).getType());
    }

    @Test
    public void testGetSubType() throws Exception {
        assertEquals(EdgeSubType.valueOf("ACTIVATION"), getTestEdgeDescriptionEnrichment().getSubType());
    }

    @Test
    public void testSetSubType() throws Exception {
        assertEquals(EdgeSubType.valueOf("BINDING_ASSOCIATION"),
                getTestEdgeDescriptionEnrichment().setSubType(EdgeSubType.valueOf("BINDING_ASSOCIATION")).getSubType());
    }
}