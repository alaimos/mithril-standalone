package com.alaimos.MITHrIL.Algorithm;

import com.alaimos.Commons.Observer.Interface.EventInterface;
import com.alaimos.Commons.Observer.ObserverImpl.Observer;
import com.alaimos.MITHrIL.Data.Pathway.Interface.PathwayInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.MITHrIL.Data.Pathway.Impl.*;
import com.alaimos.MITHrIL.Data.Pathway.Type.NodeType;
import org.jetbrains.annotations.Contract;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 28/12/2015
 */
public class PathwayMergerTest {

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

    private Graph generateTestGraph1(Pathway owner) {
        Node n1 = generateTestNode("n1"), n2 = generateTestNode("n2"), n3 = generateTestNode("n3");
        Graph g = new Graph();
        g.addNode(n1).addNode(n2).addNode(n3);
        g.addEdge(generateTestEdge(n1, n2)).addEdge(generateTestEdge(n2, n3));
        g.setEndpoints(Collections.singletonList("n3"));
        g.setOwner(owner);
        return g;
    }

    private Graph generateTestGraph2(Pathway owner) {
        Node n1 = generateTestNode("n3"), n2 = generateTestNode("n4"), n3 = generateTestNode("n1");
        Graph g = new Graph();
        g.addNode(n1).addNode(n2).addNode(n3);
        g.addEdge(generateTestEdge(n1, n2)).addEdge(generateTestEdge(n2, n3));
        g.setEndpoints(Collections.singletonList("n1"));
        g.setOwner(owner);
        return g;
    }

    private Repository generateTestRepository() {
        Pathway p1 = generateTestPathway("pathway1"), p2 = generateTestPathway("pathway2"), p3 =
                generateTestPathway("p3");
        Repository r = new Repository();
        p1.setGraph(generateTestGraph1(p1)).setCategory("cat1");
        p2.setGraph(generateTestGraph2(p2)).setCategory("cat1");
        p3.setGraph(generateTestGraph2(p3)).setCategory("cat2");
        r.add(p1);
        r.add(p2);
        r.add(p3);
        return r;
    }

    @Test
    public void testPathwayMerger() throws Exception {
        RepositoryInterface r = generateTestRepository();
        PathwayMerger algorithm = new PathwayMerger();
        HashMap<String, Object> params = new HashMap<>();
        params.put("repository", r);
        params.put("exclude", new String[]{"cat2"});
        algorithm.addObserver(new Observer() {
            @Override
            public void dispatch(EventInterface event) {
                if (!actions.containsKey("mergingPathway")) {
                    addEventListener("mergingPathway", this::mergingPathway);
                }
                super.dispatch(event);
            }

            protected void mergingPathway(EventInterface event) {
                assertNotEquals("p3", ((PathwayInterface) event.getData()).getId());
            }
        });
        algorithm.init().setParameters(params).run();
        MergedRepository rm = algorithm.getOutput();
        assertEquals(1, rm.size());
        assertNotNull(rm.getPathway());
        assertTrue(rm.getPathway().hasGraph());
        assertEquals(4, rm.getPathway().getGraph().countNodes());
        assertEquals(4, rm.getPathway().getGraph().countEdges());
        assertTrue(rm.getPathway().getGraph().hasEdge("n1", "n2"));
        assertTrue(rm.getPathway().getGraph().hasEdge("n2", "n3"));
        assertTrue(rm.getPathway().getGraph().hasEdge("n3", "n4"));
        assertTrue(rm.getPathway().getGraph().hasEdge("n4", "n1"));
        assertFalse(rm.getPathway().getGraph().hasEdge("n4", "n3"));
        assertFalse(rm.getPathway().getGraph().hasEdge("n3", "n2"));
        assertFalse(rm.getPathway().getGraph().hasEdge("n2", "n1"));
    }
}