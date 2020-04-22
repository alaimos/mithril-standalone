package com.alaimos.MITHrIL.Algorithm;

import com.alaimos.MITHrIL.Data.Pathway.Impl.*;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.MITHrIL.Data.Results.PathwayAnalysisResult;
import org.jetbrains.annotations.Contract;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 04/01/2016
 */
public class MITHrILTest {

    @Contract("_ -> !null")
    private Pathway generateTestPathway(String id) {
        return new Pathway(id, "pathwayName", null, "category1; category2");
    }

    @Contract("_ -> !null")
    private Node generateTestNode(String id) {
        return new Node(id, "node name " + id, "GENE");
    }

    @Contract("_, _, _ -> !null")
    private EdgeDescription generateTestDescription(String type, String subType, Pathway owner) {
        return new EdgeDescription(type, subType, owner);
    }

    @Contract("_, _, _ -> !null")
    private Edge generateTestEdge(Node start, Node end, String type) {
        return new Edge(start, end, generateTestDescription("gerel", type, null));
    }

    private Graph generateTestGraph(Pathway owner) {
        Node a = generateTestNode("a"), b = generateTestNode("b"), c = generateTestNode("c"), d = generateTestNode("d"),
                e = generateTestNode("e"), f = generateTestNode("f");
        Graph g = new Graph();
        g.setOwner(owner).addNode(a).addNode(b).addNode(c).addNode(d).addNode(e).addNode(f);
        g.addEdge(generateTestEdge(a, b, "expression")).addEdge(generateTestEdge(a, c, "repression"))
         .addEdge(generateTestEdge(c, d, "expression")).addEdge(generateTestEdge(b, e, "expression"))
         .addEdge(generateTestEdge(b, d, "repression")).addEdge(generateTestEdge(d, f, "repression"));
        g.setEndpoints(Arrays.asList("e", "f"));
        g.setOwner(owner);
        return g;
    }

    private Repository generateTestRepository() {
        Pathway o = generateTestPathway("testPathway");
        o.setGraph(generateTestGraph(o));
        Repository r = new Repository();
        o.setGraph(generateTestGraph(o));
        r.add(o);
        return r;
    }

    private HashMap<String, Double> generateExpressions() {
        HashMap<String, Double> expressions = new HashMap<>();
        expressions.put("a", 2.0);
        expressions.put("b", 0.0);
        expressions.put("c", 0.0);
        expressions.put("d", 1.0);
        expressions.put("e", 0.0);
        expressions.put("f", 0.0);
        double d = 1.0;
        int i = 1;
        for (; i <= 98; i++) {
            expressions.put("n" + i, d + i);
        }
        while (expressions.size() < 20000) {
            expressions.put("n" + (i++), 0.0);
        }
        return expressions;
    }

    @Test
    public void testRun() throws Exception {
        MITHrIL m = new MITHrIL();
        RepositoryInterface r = generateTestRepository();
        r.setDefaultWeightComputation();
        m.init().setParameter("expressions", generateExpressions()).setParameter("repository", r)
         .setParameter("random", new Random(123));
        m.run();
        PathwayAnalysisResult pa = m.getOutput();
        HashMap<String, Double> expectedPerturbations = new HashMap<>();
        expectedPerturbations.put("a", 2.0);
        expectedPerturbations.put("b", 1.0);
        expectedPerturbations.put("c", -1.0);
        expectedPerturbations.put("d", -0.5);
        expectedPerturbations.put("e", 0.5);
        expectedPerturbations.put("f", 0.5);
        assertNotNull(pa);
        assertEquals(expectedPerturbations, pa.getPerturbations().get("testPathway"));
        assertEquals(3.664432e-4, pa.getProbabilities().get("testPathway"), 0.000001);
        assertEquals(9.745, pa.getImpactFactors().get("testPathway"), 0.001);
        assertEquals(-0.5, pa.getAccumulators().get("testPathway"), 0.0);
        assertEquals(-0.5, pa.getCorrectedAccumulators().get("testPathway"), 0.0);
        assertTrue(pa.getPValues().get("testPathway") < 0.01);
    }

}