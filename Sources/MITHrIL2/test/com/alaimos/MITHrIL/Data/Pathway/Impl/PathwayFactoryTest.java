package com.alaimos.MITHrIL.Data.Pathway.Impl;

import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeSubType;
import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeType;
import com.alaimos.MITHrIL.Data.Pathway.Type.NodeType;
import com.alaimos.MITHrIL.Data.Pathway.Factory.PathwayFactory;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;

/**
 * Created by alaimos on 11/12/15.
 */
public class PathwayFactoryTest {

    private PathwayFactory generateTestPathwayFactory() {
        return PathwayFactory.getInstance();
    }

    @Test
    public void testGetNode() throws Exception {
        assertNotNull(generateTestPathwayFactory().getNode());
    }

    @Test
    public void testGetNode1() throws Exception {
        assertNotNull(generateTestPathwayFactory().getNode("nodeId", "node name", NodeType.valueOf("COMPOUND")));
    }

    @Test
    public void testGetNode2() throws Exception {
        assertNotNull(generateTestPathwayFactory().getNode("nodeId", "node name", "compound"));
    }

    @Test
    public void testGetEdgeDescription() throws Exception {
        assertNotNull(generateTestPathwayFactory().getEdgeDescription());
    }

    @Test
    public void testGetEdgeDescription1() throws Exception {
        assertNotNull(
                generateTestPathwayFactory()
                        .getEdgeDescription(EdgeType.valueOf("ECREL"), EdgeSubType.valueOf("ACTIVATION")));
    }

    @Test
    public void testGetEdgeDescription2() throws Exception {
        assertNotNull(generateTestPathwayFactory().getEdgeDescription("ecrel", "activation"));
    }

    @Test
    public void testGetEdgeDescription3() throws Exception {
        assertNotNull(generateTestPathwayFactory()
                .getEdgeDescription(EdgeType.valueOf("ECREL"), EdgeSubType.valueOf("ACTIVATION"), null));
    }

    @Test
    public void testGetEdgeDescription4() throws Exception {
        assertNotNull(generateTestPathwayFactory().getEdgeDescription("ecrel", "activation", null));
    }

    @Test
    public void testGetEdge() throws Exception {
        assertNotNull(generateTestPathwayFactory().getEdge());
    }

    @Test
    public void testGetEdge1() throws Exception {
        assertNotNull(generateTestPathwayFactory().getEdge(null, null));
    }

    @Test
    public void testGetEdge2() throws Exception {
        assertNotNull(generateTestPathwayFactory().getEdge(null, null, new EdgeDescription()));
    }

    @Test
    public void testGetEdge3() throws Exception {
        assertNotNull(generateTestPathwayFactory().getEdge(null, null, new ArrayList<>()));
    }

    @Test
    public void testGetEdge4() throws Exception {
        assertNotNull(
                generateTestPathwayFactory()
                        .getEdge(null, null, EdgeType.valueOf("ECREL"), EdgeSubType.valueOf("ACTIVATION")));
    }

    @Test
    public void testGetEdge5() throws Exception {
        assertNotNull(generateTestPathwayFactory().getEdge(null, null, "ecrel", "activation"));
    }

    @Test
    public void testGetEdge6() throws Exception {
        assertNotNull(generateTestPathwayFactory()
                .getEdge(null, null, EdgeType.valueOf("ECREL"), EdgeSubType.valueOf("ACTIVATION"), null));
    }

    @Test
    public void testGetEdge7() throws Exception {
        assertNotNull(generateTestPathwayFactory().getEdge(null, null, "ecrel", "activation", null));
    }

    @Test
    public void testGetGraph() throws Exception {
        assertNotNull(generateTestPathwayFactory().getGraph());
    }

    @Test
    public void testGetPathway() throws Exception {
        assertNotNull(generateTestPathwayFactory().getPathway());
    }

    @Test
    public void testGetPathway1() throws Exception {
        assertNotNull(generateTestPathwayFactory().getPathway("id", "name"));
    }

    @Test
    public void testGetPathway2() throws Exception {
        assertNotNull(generateTestPathwayFactory().getPathway("id", "name", (Graph) null));
    }

    @Test
    public void testGetPathway3() throws Exception {
        assertNotNull(generateTestPathwayFactory().getPathway("id", "name", new ArrayList<>()));
    }

    @Test
    public void testGetPathway4() throws Exception {
        assertNotNull(generateTestPathwayFactory().getPathway("id", "name", null, new ArrayList<>()));
    }

    @Test
    public void testGetPathway5() throws Exception {
        assertNotNull(generateTestPathwayFactory().getPathway("id", "name", "category1;category2"));
    }

    @Test
    public void testGetPathway6() throws Exception {
        assertNotNull(generateTestPathwayFactory().getPathway("id", "name", null, "category1;category2"));
    }
}