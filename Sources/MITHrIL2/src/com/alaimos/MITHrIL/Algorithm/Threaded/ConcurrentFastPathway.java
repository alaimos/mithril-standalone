package com.alaimos.MITHrIL.Algorithm.Threaded;

import com.alaimos.Commons.Algorithm.Interface.AlgorithmInterface;
import com.alaimos.Commons.Algorithm.Threaded.ConcurrentAbstractAlgorithm;
import com.alaimos.MITHrIL.Data.Pathway.Impl.MergedRepository;
import com.alaimos.MITHrIL.Data.Pathway.Interface.GraphInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 31/12/2015
 */
public class ConcurrentFastPathway extends ConcurrentAbstractAlgorithm<Map<String, Double>, Map<String, Double>> {

    private final ConcurrentMap<NodeInterface, Double> absoluteWeights;
    protected ConcurrentMap<String, ConcurrentMap<String, Double>> visitedPerturbations;
    protected ConcurrentMap<String, Double> perturbations;
    protected ConcurrentMap<String, Double> accumulators;
    private List<NodeInterface> sortedNodes = null;
    private GraphInterface metaPathwayGraph;

    public ConcurrentFastPathway(ConcurrentHashMap<String, Object> sharedParameters) {
        super(sharedParameters);
        this.absoluteWeights = new ConcurrentHashMap<>();
    }

    /**
     * Get the log-fold-change of a node. If no finite FC has been supplied its value will be set to 0
     *
     * @param id the id of the node
     * @return the fold change of that node
     */
    private double getExpression(String id) {
        return input.getOrDefault(id, 0.0);
    }

    /**
     * Compute sum of absolute values of weight of outgoing edges of a node
     *
     * @param u A node
     * @return The total weight
     */
    private double absoluteTotalWeight(NodeInterface u) {
        double weight;
        if (!absoluteWeights.containsKey(u)) {
            GraphInterface g = metaPathwayGraph;
            weight = 0d;
            for (NodeInterface d : g.outgoingNodes(u)) {
                weight += Math.abs(g.getEdge(u, d).computeWeight());
            }
            absoluteWeights.put(u, weight);
        } else {
            weight = absoluteWeights.get(u);
        }
        return weight;
    }

    protected double perturbation(@NotNull NodeInterface n, String startNode) {
        var nId = n.getId();
        if (!visitedPerturbations.containsKey(startNode)) {
            visitedPerturbations.put(startNode, new ConcurrentHashMap<>());
        }
        if (visitedPerturbations.get(startNode).containsKey(nId)) {
            return visitedPerturbations.get(startNode).get(nId);
        }
        if (perturbations.containsKey(nId)) {
            return perturbations.get(nId);
        }
        var exp = getExpression(nId);
        visitedPerturbations.get(startNode).put(nId, exp);
        var pf = exp;
        for (NodeInterface u : metaPathwayGraph.ingoingNodes(n)) {
            var tmp = metaPathwayGraph.getEdge(u, n).computeWeight() * (perturbation(u) / absoluteTotalWeight(u));
            if (Double.isFinite(tmp)) pf += tmp;
        }
        visitedPerturbations.get(startNode).put(nId, pf);
        perturbations.put(nId, pf);
        return pf;
    }


    /**
     * Compute perturbation of a node in a pathway
     *
     * @param n A node
     * @return The perturbation
     */
    private double perturbation(@NotNull NodeInterface n) {
        return perturbation(n, n.getId());
    }

    /**
     * Computes the total perturbation accumulation of a pathway
     *
     * @param pId   the id of the pathway
     * @param nodes the list of pathway nodes
     * @return the accumulation
     */
    private double accumulator(String pId, List<NodeInterface> nodes) {
        if (accumulators.containsKey(pId)) {
            return accumulators.get(pId);
        }
        double a = 0.0;
        for (var n : nodes) {
            a += n.getType().sign() * (perturbation(n) - getExpression(n.getId()));
        }
        accumulators.put(pId, a);
        return a;
    }

    /**
     * Computes the total pathway perturbation
     *
     * @param pId   the id of the pathway
     * @param nodes the list of pathway nodes
     * @return the pathway perturbation
     */
    private double pathwayPerturbation(String pId, List<NodeInterface> nodes) {
        if (accumulators.containsKey(pId)) {
            return accumulators.get(pId);
        }
        double a = 0.0;
        for (var n : nodes) {
            a += n.getType().sign() * perturbation(n);
        }
        accumulators.put(pId, a);
        return a;
    }


    /**
     * Gets object which sorts nodes first in order of increasing upstream nodes, then in order of increasing
     * in-degree, and finally in order of decreasing out-degree.
     *
     * @param g a pathway graph
     * @return a comparator to use with Collections.sort
     */
    private Comparator<NodeInterface> pathwayNodesComparator(@NotNull GraphInterface g) {
        Comparator<NodeInterface> c2 = Comparator.comparingInt(g::inDegree), c3 = Comparator.comparingInt(g::outDegree);
        return c2.thenComparing(c3.reversed());
    }

    /**
     * Get the list of nodes for the current pathway sorted by using a pseudo-topological ordering
     *
     * @return a list of sorted nodes
     */
    private List<NodeInterface> getSortedNodes() {
        if (sortedNodes == null) {
            sortedNodes = sortNodes(new ArrayList<>(metaPathwayGraph.getNodes().values()));
        }
        return sortedNodes;
    }

    /**
     * Sort nodes using a topological ordering algorithm
     *
     * @param nodes a list of nodes
     * @return a list of sorted nodes
     */
    private List<NodeInterface> sortNodes(@NotNull List<NodeInterface> nodes) {
        nodes.sort(pathwayNodesComparator(metaPathwayGraph));
        ArrayList<NodeInterface> sorted = new ArrayList<>(nodes.size());
        HashSet<NodeInterface> visited = new HashSet<>();
        for (NodeInterface n : nodes) {
            if (!visited.contains(n)) {
                Stack<NodeInterface> s = new Stack<>();
                s.push(n);
                while (!s.isEmpty()) {
                    NodeInterface curr = s.pop();
                    if (visited.contains(curr)) continue;
                    visited.add(curr);
                    sorted.add(curr);
                    metaPathwayGraph.outgoingNodesStream(curr)
                            .sorted(pathwayNodesComparator(metaPathwayGraph))
                            .filter(nxt -> !visited.contains(nxt))
                            .forEach(s::push);
                }
            }
        }
        assert sorted.size() == nodes.size();
        return sorted;
    }

    @Override
    public AlgorithmInterface<Map<String, Double>> init() {
        perturbations = new ConcurrentHashMap<>();
        visitedPerturbations = new ConcurrentHashMap<>();
        accumulators = new ConcurrentHashMap<>();
        sortedNodes = null;
        metaPathwayGraph = null;
        return this;
    }

    @Override
    public AlgorithmInterface<Map<String, Double>> clear() {
        super.clear();
        visitedPerturbations.clear();
        perturbations.clear();
        accumulators.clear();
        return this;
    }

    @Override
    public void run() {
        MergedRepository repository = getParameterNotNull("repository", MergedRepository.class);
        boolean accumulator = getOptionalParameter("accumulator", Boolean.class).orElse(false);
        var p = repository.getPathway();
        output = new HashMap<>();
        if (p.hasGraph()) {
            metaPathwayGraph = p.getGraph();
            if (metaPathwayGraph.countNodes() > 0 && metaPathwayGraph.countEdges() > 0) {
                for (NodeInterface n : getSortedNodes()) {
                    perturbation(n);
                }
                for (var pathway : repository.getVirtualPathways()) {
                    var n = repository.getNodesOfVirtualPathway(pathway);
                    output.put(pathway, (accumulator) ? accumulator(pathway, n) : pathwayPerturbation(pathway, n));
                }
            }
        }
    }
}
