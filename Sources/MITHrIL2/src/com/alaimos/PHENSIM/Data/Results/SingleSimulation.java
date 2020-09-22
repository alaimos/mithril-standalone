package com.alaimos.PHENSIM.Data.Results;

import com.alaimos.Commons.Utils.Pair;
import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.PathwayInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.PHENSIM.Data.Enums.State;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.0
 * @since 19/08/2020
 */
public class SingleSimulation {

    private HashSet<Pair<String, String>> allNodes = null;
    private final Map<String, SinglePathway> pathwayResults;
    private final RepositoryInterface repository;
    private final Map<String, List<String>> directTargets;
    private final Map<String, Map<String, List<String>>> directlyTargetedBy;
    private int numberOfSimulations;

    /**
     * Class constructor
     *
     * @param r                   a pathway repository
     * @param numberOfSimulations the number of iterations of each simulation
     */
    public SingleSimulation(RepositoryInterface r, int numberOfSimulations) {
        repository = r;
        this.numberOfSimulations = numberOfSimulations;
        pathwayResults = new HashMap<>();
        directTargets = new HashMap<>();
        directlyTargetedBy = new HashMap<>();
        initPathwayResults();
    }

    /**
     * Initializes all results objects
     */
    private void initPathwayResults() {
        for (var p : repository) {
            var pId = p.getId();
            pathwayResults.put(pId, new SinglePathway(pId, numberOfSimulations));
        }
        for (var pId : repository.getVirtualPathways()) {
            var sId = repository.getSourceOfVirtualPathway(pId).getId();
            var source = pathwayResults.get(sId);
            if (source != null) {
                pathwayResults.put(pId, new SinglePathway(pId, numberOfSimulations, true, source));
            }
        }
    }

    /**
     * Get the repository used for this object
     *
     * @return a pathway repository
     */
    public RepositoryInterface getRepository() {
        return repository;
    }

    /**
     * Get the number of iterations
     *
     * @return the number of iterations
     */
    public int getNumberOfSimulations() {
        return numberOfSimulations;
    }

    /**
     * Get all nodes
     *
     * @return a set of nodes
     */
    public HashSet<Pair<String, String>> getAllNodes() {
        if (allNodes == null) {
            allNodes = new HashSet<>();
            for (var p : pathwayResults.values()) {
                if (!p.isVirtualPathway()) {
                    var pId = p.getPathwayId();
                    allNodes.addAll(p.getAllNodes().stream().map(n -> new Pair<>(pId, n)).collect(Collectors.toSet()));
                }
            }
        }
        return allNodes;
    }

    /**
     * Set the number of iterations
     *
     * @param numberOfSimulations the number of iterations
     * @return this object for a fluent interface
     */
    public SingleSimulation setNumberOfSimulations(int numberOfSimulations) {
        this.numberOfSimulations = numberOfSimulations;
        for (var p : pathwayResults.values()) {
            p.setNumberOfSimulations(numberOfSimulations);
        }
        return this;
    }

    /**
     * Reset all counters
     *
     * @return this object for a fluent interface
     */
    public SingleSimulation resetCounters() {
        allNodes = null;
        for (var p : pathwayResults.values()) {
            p.resetCounters();
        }
        return this;
    }

    /**
     * Increment counter for a node in a pathway
     *
     * @param pathway a pathway
     * @param node    a node
     * @param state   a node state
     * @return this object for a fluent interface
     */
    public SingleSimulation incrementNodeCounter(String pathway, @Nullable String node, State state) {
        var p = pathwayResults.get(pathway);
        if (p != null) p.incrementNodeCounter(node, state);
        return this;
    }

    /**
     * Add a perturbation value for a node in a pathway to compute the average
     *
     * @param pathway      a pathway
     * @param node         a node
     * @param perturbation a perturbation value
     */
    public void addToAveragePerturbation(String pathway, @Nullable String node, double perturbation) {
        if (perturbation != 0.0) {
            var p = pathwayResults.get(pathway);
            if (p != null) p.addToAveragePerturbation(node, perturbation);
        }
    }

    /**
     * Increment a pathway counter
     *
     * @param pathway a pathway
     * @param state   the pathway state
     * @return this object for a fluent interface
     */
    public SingleSimulation incrementPathwayCounter(String pathway, State state) {
        return incrementNodeCounter(pathway, null, state);
    }

    /**
     * Get a pathway from this set of results
     *
     * @param pathway a pathway identifier
     * @return the pathway result object
     */
    @Nullable
    public SinglePathway getPathway(String pathway) {
        return pathwayResults.get(pathway);
    }

    /**
     * Get the counter for a node in a pathway
     *
     * @param pathway a pathway
     * @param node    a node
     * @param state   the state of the node
     * @return the counter
     */
    public int getCounter(String pathway, @Nullable String node, State state) {
        var p = pathwayResults.get(pathway);
        if (p != null) return p.getCounter(node, state);
        return -1;
    }

    /**
     * Get the counter for a pathway
     *
     * @param pathway a pathway
     * @param state   the state of the pathway
     * @return the counter
     */
    public int getPathwayCounter(String pathway, State state) {
        return getCounter(pathway, null, state);
    }

    /**
     * Gets all the counters for a state in a pathway
     *
     * @param pathway a pathway
     * @param state   the state of the node
     * @return the map of counters
     */
    @Nullable
    public Map<String, Integer> getNodesCounters(String pathway, State state) {
        var p = pathwayResults.get(pathway);
        if (p != null) return p.getNodesCounters(state);
        return null;
    }

    /**
     * Gets the log probability of a pathway node
     *
     * @param pathway a pathway
     * @param node    the node
     * @param state   the state of the node
     * @return the probability
     */
    public double getLogProbability(String pathway, @Nullable String node, State state) {
        var p = pathwayResults.get(pathway);
        if (p != null) return p.getLogProbability(node, state);
        return Double.NaN;
    }

    /**
     * Gets the log probability of a pathway
     *
     * @param pathway a pathway
     * @param state   the state for which probability will be computed
     * @return the probability of the selected state
     */
    public double getPathwayLogProbability(String pathway, State state) {
        return getLogProbability(pathway, null, state);
    }

    /**
     * Compute activity score for a pathway node
     *
     * @param pathway a pathway
     * @param node    the node
     * @return the activity score
     */
    public double getActivityScore(String pathway, @Nullable String node) {
        var p = pathwayResults.get(pathway);
        if (p != null) return p.getActivityScore(node);
        return Double.NaN;
    }

    /**
     * Compute the activity score for a pathway
     *
     * @param pathway a pathway
     * @return the pathway activity score
     */
    public double getPathwayActivityScore(String pathway) {
        return getActivityScore(pathway, null);
    }

    /**
     * Get the average perturbation for a node in a pathway
     *
     * @param pathway a pathway
     * @param node    a node
     * @return the perturbation
     */
    public double getAveragePerturbation(String pathway, String node) {
        var p = pathwayResults.get(pathway);
        if (p != null) return p.getAveragePerturbation(node);
        return Double.NaN;
    }

    /**
     * Get the average perturbation for the pathway
     *
     * @return the perturbation
     */
    public double getAveragePathwayPerturbation(String pathway) {
        var p = pathwayResults.get(pathway);
        if (p != null) return p.getAveragePathwayPerturbation();
        return Double.NaN;
    }

    /**
     * Add a list of direct targets to the internal directTargets Object
     *
     * @param pathway the id of a pathway
     * @param targets the list of targets
     */
    private void setDirectTargets(String pathway, List<String> targets) {
        if (!directTargets.containsKey(pathway)) {
            directTargets.put(pathway, new ArrayList<>());
        }
        this.directTargets.get(pathway).addAll(targets);
    }

    /**
     * Add a directly targeted gene to the internal object
     *
     * @param pathway the id of a pathway
     * @param target  the directly targeted gene
     * @param by      the node that targets the target
     */
    private void setDirectlyTargetedBy(String pathway, String target, String by) {
        if (!directlyTargetedBy.containsKey(pathway)) {
            directlyTargetedBy.put(pathway, new HashMap<>());
        }
        if (!directlyTargetedBy.get(pathway).containsKey(target)) {
            directlyTargetedBy.get(pathway).put(target, new ArrayList<>());
        }
        directlyTargetedBy.get(pathway).get(target).add(by);
    }

    /**
     * Compute the list of direct targets for a pathway
     *
     * @param simulateOn a list of nodes
     * @param p          a pathway object
     */
    private void setDirectTargetsOfPathway(@NotNull List<String> simulateOn, @NotNull PathwayInterface p) {
        if (p.hasGraph()) {
            var pId = p.getId();
            var g = p.getGraph();
            simulateOn.stream().filter(g::hasNode).forEach(s -> {
                var sn = g.getNode(s);
                setDirectTargets(pId,
                        g.outgoingNodesStream(sn).map(NodeInterface::getId).collect(Collectors.toList()));
                g.outgoingNodesStream(sn).forEach(n -> {
                    setDirectlyTargetedBy(pId, n.getId(), s);
                });
            });
        }
    }

    /**
     * Compute the list of direct targets for a virtual pathway
     *
     * @param simulateOn a list of nodes
     * @param pId        a virtual pathway identifier
     */
    private void setDirectTargetsOfVirtualPathway(@NotNull List<String> simulateOn, @NotNull String pId) {
        var source = repository.getSourceOfVirtualPathway(pId);
        var nodes = repository.getNodesOfVirtualPathway(pId);
        var g = source.getGraph();
        simulateOn.stream().filter(g::hasNode).map(g::getNode).filter(nodes::contains).forEach(sn -> {
            var s = sn.getId();
            setDirectTargets(pId,
                    g.outgoingNodesStream(sn).filter(nodes::contains).map(NodeInterface::getId).collect(Collectors.toList()));
            g.outgoingNodesStream(sn).filter(nodes::contains).forEach(n -> {
                setDirectlyTargetedBy(pId, n.getId(), s);
            });
        });
    }

    /**
     * Set the list of direct targets and directly targeted genes from the input of a simulation
     *
     * @param simulateOn the input of a simulation
     * @return this object for a fluent interface
     */
    public SingleSimulation setDirectTargets(List<String> simulateOn) {
        for (var p : repository) {
            setDirectTargetsOfPathway(simulateOn, p);
        }
        for (var pId : repository.getVirtualPathways()) {
            setDirectTargetsOfVirtualPathway(simulateOn, pId);
        }
        return this;
    }

    /**
     * Returns the list of direct targets
     *
     * @param pathway a pathway identifier
     * @return the list of direct targets
     */
    public List<String> getDirectTargets(String pathway) {
        return this.directTargets.getOrDefault(pathway, Collections.emptyList());
    }

    /**
     * Returns a list of directly targeted genes of a pathway
     *
     * @param pathway a pathway identifier
     * @return the map containing all the directly targeted genes and their ancestors
     */
    public Map<String, List<String>> getDirectlyTargetedBy(String pathway) {
        return directlyTargetedBy.getOrDefault(pathway, Collections.emptyMap());
    }

    /**
     * Returns an array with the list of all pathways
     *
     * @return the array of pathways
     */
    public String[] getPathwayArray() {
        return pathwayResults.keySet().toArray(String[]::new);
    }

    /**
     * Returns an array of (Pathway,Node) pairs for all nodes in all pathways (virtual pathways are not included)
     *
     * @return the array of pairs
     */
    @SuppressWarnings("unchecked")
    public Pair<String, String>[] getNodesArray() {
        return getAllNodes().toArray(Pair[]::new);
    }

    /**
     * Given an array of pathways returns an array with their activity scores.
     *
     * @param pathways an array of pathway identifiers
     * @return an array of pathway activity scores
     */
    public double[] getPathwayActivityScoresArray(@NotNull String[] pathways) {
        return Arrays.stream(pathways).mapToDouble(this::getPathwayActivityScore).toArray();
    }

    /**
     * Given an array of (Pathway,Node) pairs returns an array with the activity score of each node in its pathway
     *
     * @param nodes the array of (Pathway,Node) pairs
     * @return an array of nodes activity score
     */
    public double[] getNodesActivityScoresArray(@NotNull Pair<String, String>[] nodes) {
        return Arrays.stream(nodes).mapToDouble(p -> this.getActivityScore(p.getFirst(), p.getSecond())).toArray();
    }

}
