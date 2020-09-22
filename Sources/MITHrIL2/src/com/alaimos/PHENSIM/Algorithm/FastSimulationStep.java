package com.alaimos.PHENSIM.Algorithm;

import com.alaimos.MITHrIL.Data.Pathway.Interface.GraphInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.PathwayInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.PHENSIM.Data.Enums.State;
import com.alaimos.PHENSIM.Data.Generator.RandomExpressionGenerator;
import com.alaimos.PHENSIM.Data.Results.SingleSimulation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the PHENSIM simulation step
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.0
 * @since 19/08/2020
 */
public class FastSimulationStep {

    //region MITHrIL nodes sorting algorithm
    /**
     * A list of sorted nodes for each pathways. Stored globally to avoid computing it for each step.
     */
    private static Map<PathwayInterface, List<NodeInterface>> sortedNodes = null;

    /**
     * Clears the list of sorted nodes
     */
    public static void clearSortedNodes(@NotNull RepositoryInterface r) {
        sortedNodes = new ConcurrentHashMap<>();
        for (var p : r) {
            var g = p.getGraph();
            var nodes = new ArrayList<>(g.getNodes().values());
            sortedNodes.put(p, sortNodes(nodes, g));
        }
    }

    /**
     * Gets object which sorts nodes first in order of increasing upstream nodes, then in order of increasing
     * in-degree, and finally in order of decreasing out-degree.
     *
     * @param g a pathway graph
     * @return a comparator to use with Collections.sort
     */
    private static Comparator<NodeInterface> pathwayNodesComparator(@NotNull GraphInterface g) {
        Comparator<NodeInterface> c2 = Comparator.comparingInt(g::inDegree), c3 = Comparator.comparingInt(g::outDegree);
        return c2.thenComparing(c3.reversed());
    }

    /**
     * Get the list of nodes for the current pathway sorted by using a pseudo-topological ordering
     *
     * @param p a pathway
     * @return a list of sorted nodes
     */
    public static List<NodeInterface> getSortedNodes(@NotNull PathwayInterface p) {
        var g = p.getGraph();
        List<NodeInterface> nodes;
        if (sortedNodes.containsKey(p)) {
            nodes = sortedNodes.get(p);
        } else {
            nodes = new ArrayList<>(g.getNodes().values());
            nodes = sortNodes(nodes, g); //Sort nodes to reduce recursion
            sortedNodes.put(p, nodes);
        }
        return nodes;
    }

    /**
     * Sort nodes using a topological ordering algorithm
     *
     * @param nodes a list of nodes
     * @param g     a graph
     * @return a list of sorted nodes
     */
    @NotNull
    private static List<NodeInterface> sortNodes(@NotNull List<NodeInterface> nodes, GraphInterface g) {
        nodes.sort(pathwayNodesComparator(g));
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
                    g.outgoingNodesStream(curr)
                            .sorted(pathwayNodesComparator(g))
                            .filter(nxt -> !visited.contains(nxt))
                            .forEach(s::push);
                }
            }
        }
        assert sorted.size() == nodes.size();
        return sorted;
    }
    //endregion

    //region Functional Interfaces

    /**
     * A simple functional interface used to wrap a method that handles an event
     */
    @FunctionalInterface
    public interface EventSender {
        void send();
    }
    //endregion

    //region Input Parameters
    private final RepositoryInterface repository;
    private final double epsilon;
    private final int numberOfRepetitions;
    private final List<String> nonExpressedNodes;
    private final RandomExpressionGenerator randomExpressionGenerator;
    private final SingleSimulation simulationResults;
    private final EventSender notifyStepEnd;
    //endregion

    //region Fast MITHrIL Implementation (only nodes and pathway perturbations are computed)
    protected Map<String, Double> expressions;
    protected Map<String, Map<String, Map<String, Double>>> visitedPerturbations;
    protected Map<String, Map<String, Double>> perturbations;

    /**
     * Compute sum of absolute values of weight of outgoing edges of a node
     *
     * @param u A node
     * @param p A Pathway which contains such node
     * @return The total weight
     */
    private double absoluteTotalWeight(@NotNull NodeInterface u, @NotNull PathwayInterface p) {
        GraphInterface g = p.getGraph();
        double weight = 0.0;
        for (NodeInterface d : g.outgoingNodes(u)) {
            weight += Math.abs(g.getEdge(u, d).computeWeight());
        }
        return weight;
    }

    /**
     * Update the internal perturbation map
     *
     * @param nId  a node
     * @param pId  a pathway
     * @param pert the perturbation value
     */
    protected void putPerturbation(String nId, String pId, double pert) {
        if (!perturbations.containsKey(pId)) {
            visitedPerturbations.put(pId, new HashMap<>());
            perturbations.put(pId, new HashMap<>());
        }
        perturbations.get(pId).put(nId, pert);
    }

    /**
     * Recursively compute the perturbation of a node in a pathway.
     * A starting node is provided to detect cycles during the execution.
     *
     * @param n         a node
     * @param p         a pathway
     * @param startNode the node where the chain of calls started
     * @return the perturbation value
     */
    protected double perturbation(@NotNull NodeInterface n, @NotNull PathwayInterface p, String startNode) {
        var pId = p.getId();
        var nId = n.getId();
        if (!visitedPerturbations.containsKey(pId)) {
            perturbations.put(pId, new HashMap<>());
            visitedPerturbations.put(pId, new HashMap<>());
        }
        if (!visitedPerturbations.get(pId).containsKey(startNode)) {
            visitedPerturbations.get(pId).put(startNode, new HashMap<>());
        }
        if (visitedPerturbations.get(pId).get(startNode).containsKey(nId)) {
            return visitedPerturbations.get(pId).get(startNode).get(nId);
        }
        if (perturbations.get(pId).containsKey(nId)) {
            return perturbations.get(pId).get(nId);
        }
        var exp = expressions.getOrDefault(nId, 0.0);
        visitedPerturbations.get(pId).get(startNode).put(nId, exp);
        var g = p.getGraph();
        var pf = exp;
        for (NodeInterface u : g.ingoingNodes(n)) {
            var tmp = g.getEdge(u, n).computeWeight() * (perturbation(u, p, startNode) / absoluteTotalWeight(u, p));
            if (Double.isFinite(tmp)) pf += tmp;
        }
        visitedPerturbations.get(pId).get(startNode).put(nId, pf);
        putPerturbation(nId, pId, pf);
        return pf;
    }

    /**
     * Compute perturbation of a node in a pathway
     *
     * @param n A node
     * @param p A pathway which contains such node
     * @return The perturbation
     */
    protected double perturbation(NodeInterface n, PathwayInterface p) {
        return perturbation(n, p, n.getId());
    }

    /**
     * Run the fast implementation of mithril
     */
    private void fastMITHrILRun() {
        var excludedNodes = randomExpressionGenerator.getNodesSet();
        expressions = randomExpressionGenerator.getRandomExpressions();
        visitedPerturbations = new HashMap<>();
        perturbations = new HashMap<>();
        for (var p : repository) {
            if (p.hasGraph()) {
                var g = p.getGraph();
                if (g.countNodes() > 0 && g.countEdges() > 0) {
                    var pId = p.getId();
                    for (var n : nonExpressedNodes) {
                        if (g.hasNode(n)) {
                            putPerturbation(n, pId, 0.0);
                        }
                    }
                    var pathwayPerturbation = 0.0;
                    var nodes = getSortedNodes(p);
                    for (var n : nodes) {
                        var nm = n.getId();
                        var tmp = perturbation(n, p);
                        if (!excludedNodes.contains(nm)) pathwayPerturbation += n.getType().sign() * tmp;
                        checkNodePerturbation(pId, nm, tmp);
                    }
                    checkPathwayPerturbation(pId, pathwayPerturbation);
                }
            }
        }
        for (var p : repository.getVirtualPathways()) {
            var pathwayPerturbation = 0.0;
            var source = repository.getSourceOfVirtualPathway(p);
            var nodes = repository.getNodesOfVirtualPathway(p);
            for (var n : nodes) {
                if (!excludedNodes.contains(n.getId()))
                    pathwayPerturbation += n.getType().sign() * perturbation(n, source);
            }
            checkPathwayPerturbation(p, pathwayPerturbation);
        }
    }
    //endregion

    /**
     * Class constructor
     *
     * @param epsilon                   the epsilon value
     * @param nonExpressedNodes         the list of non expressed nodes
     * @param randomExpressionGenerator a random expression generator
     * @param simulationResults         the object where all results for this iteration will be stored
     */
    public FastSimulationStep(RandomExpressionGenerator randomExpressionGenerator, double epsilon,
                              List<String> nonExpressedNodes, @NotNull SingleSimulation simulationResults,
                              @Nullable EventSender notifyStepEnd) {
        this.repository = simulationResults.getRepository();
        this.epsilon = epsilon;
        this.numberOfRepetitions = simulationResults.getNumberOfSimulations();
        this.nonExpressedNodes = nonExpressedNodes;
        this.randomExpressionGenerator = randomExpressionGenerator;
        this.simulationResults = simulationResults;
        this.notifyStepEnd = notifyStepEnd;
    }

    /**
     * Notify that an iteration is ending
     */
    private void notifyEnd() {
        if (notifyStepEnd != null) {
            notifyStepEnd.send();
        }
    }

    /**
     * Checks the perturbation of a node in a pathway and increments its counter
     *
     * @param p    the pathway id
     * @param n    the node id
     * @param pert the perturbation value
     */
    private void checkNodePerturbation(String p, @NotNull String n, double pert) {
        var state = (pert > epsilon) ? State.ACTIVE : (pert < -epsilon) ? State.INHIBITED : State.OTHERWISE;
        simulationResults.incrementNodeCounter(p, n, state).addToAveragePerturbation(p, n, pert);
    }

    /**
     * Checks the perturbation of a pathway and increments its counter
     *
     * @param p    the pathway id
     * @param pert the perturbation value
     */
    private void checkPathwayPerturbation(String p, double pert) {
        var state = (pert > epsilon) ? State.ACTIVE : (pert < -epsilon) ? State.INHIBITED : State.OTHERWISE;
        simulationResults.incrementPathwayCounter(p, state).addToAveragePerturbation(p, null, pert);
    }

    /**
     * Run the simulation step
     */
    public void run() {
        for (int i = 0; i < numberOfRepetitions; i++) {
            fastMITHrILRun();
            notifyEnd();
        }
    }
}