package com.alaimos.PHENSIM.Data.Results;

import com.alaimos.Commons.Math.PValue.Adjusters;
import com.alaimos.Commons.Utils.ArrayUtils;
import com.alaimos.Commons.Utils.Pair;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.PHENSIM.Data.Enums.State;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.commons.math3.stat.ranking.NaNStrategy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.0
 * @since 19/08/2020
 */
public class PhensimRun {

    private final RepositoryInterface repository;
    private final int numberOfIterations;
    private final SingleSimulation[] simulationResults;
    private boolean computedPValues = false;
    private final Map<String, Integer> pathways = new HashMap<>();
    private final Map<String, Map<String, Integer>> nodes = new HashMap<>();
    private double[] pathwayPValues;
    private double[] pathwayAdjustedPValues;
    private double[] nodesPValues;
    private double[] nodesAdjustedPValues;

    /**
     * Class constructor
     *
     * @param r                  a pathway repository
     * @param numberOfIterations the number of iterations of each simulation
     */
    public PhensimRun(RepositoryInterface r, int numberOfIterations, int numberOfSimulationsFirst, int numberOfSimulations) {
        repository = r;
        simulationResults = new SingleSimulation[numberOfIterations + 1];
        simulationResults[0] = new SingleSimulation(r, numberOfSimulationsFirst);
        for (var i = 1; i < simulationResults.length; i++) {
            simulationResults[i] = new SingleSimulation(r, numberOfSimulations);
        }
        this.numberOfIterations = numberOfIterations;
    }

    /**
     * Convert the array of pathways in a map from id to index
     *
     * @param pathways the array of pathways
     */
    private void pathwayArrayToMap(@NotNull String[] pathways) {
        this.pathways.clear();
        for (var i = 0; i < pathways.length; i++) {
            this.pathways.put(pathways[i], i);
        }
    }

    /**
     * Convert the array of (Pathway,Node) pairs in a map from id to index
     *
     * @param nodes the array of pathways
     */
    private void nodesArrayToMap(@NotNull Pair<String, String>[] nodes) {
        this.nodes.clear();
        for (var i = 0; i < nodes.length; i++) {
            var path = nodes[i].getFirst();
            var node = nodes[i].getSecond();
            if (!this.nodes.containsKey(path)) this.nodes.put(path, new HashMap<>());
            this.nodes.get(path).put(node, i);
        }
    }

    /**
     * Correct the activity scores using the median of the distribution.
     * The correction is performed in place without creating a new array.
     *
     * @param base   the original activity scores
     * @param median the median of the activity scores distribution
     * @return the corrected activity scores (no copy is performed the array is the same as base)
     */
    @NotNull
    private double[] correctActivityScore(double[] base, double[] median) {
        var sign = ArrayUtils.signum(base);
        return ArrayUtils.mulInPlace(ArrayUtils.absInPlace(ArrayUtils.diffInPlace(base, median)), sign);
    }

    /**
     * Compute pathway pValues
     */
    private void computePathwayPValues() {
        var pathwaysArray = simulationResults[0].getPathwayArray();
        var n = pathwaysArray.length;
        var inc = 1.0 / ((double) numberOfIterations);
        double[] randomActivityScore;
        pathwayArrayToMap(pathwaysArray);
        pathwayPValues = new double[n];
        Arrays.fill(pathwayPValues, 0);
        var baseActivityScores = simulationResults[0].getPathwayActivityScoresArray(pathwaysArray);
        var medianActivityScores = getPathwayMedianActivityScoresArray(pathwaysArray);
        baseActivityScores = correctActivityScore(baseActivityScores, medianActivityScores);
        for (var i = 1; i < simulationResults.length; i++) {
            randomActivityScore = correctActivityScore(
                    simulationResults[i].getPathwayActivityScoresArray(pathwaysArray),
                    medianActivityScores
            );
            for (var j = 0; j < n; j++) {
                var cond = (Double.isNaN(baseActivityScores[j])) ||
                        (baseActivityScores[j] < 0 && randomActivityScore[j] <= baseActivityScores[j]) ||
                        (baseActivityScores[j] > 0 && randomActivityScore[j] >= baseActivityScores[j]) ||
                        (baseActivityScores[j] == 0.0 && randomActivityScore[j] == 0.0);
                if (cond) pathwayPValues[j] += inc;
            }
        }
        pathwayAdjustedPValues = Adjusters.benjaminiHochberg(pathwayPValues);
    }

    /**
     * Compute nodes pValues
     */
    private void computeNodesPValues() {
        var nodesArray = simulationResults[0].getNodesArray();
        var n = nodesArray.length;
        var inc = 1.0 / ((double) numberOfIterations);
        double[] randomActivityScore;
        nodesArrayToMap(nodesArray);
        nodesPValues = new double[n];
        Arrays.fill(nodesPValues, 0);
        var baseActivityScores = simulationResults[0].getNodesActivityScoresArray(nodesArray);
        for (var i = 1; i < simulationResults.length; i++) {
            randomActivityScore = simulationResults[i].getNodesActivityScoresArray(nodesArray);
            for (var j = 0; j < n; j++) {
                var cond = (Double.isNaN(baseActivityScores[j])) ||
                        (baseActivityScores[j] < 0 && randomActivityScore[j] <= baseActivityScores[j]) ||
                        (baseActivityScores[j] > 0 && randomActivityScore[j] >= baseActivityScores[j]) ||
                        (baseActivityScores[j] == 0.0 && randomActivityScore[j] == 0.0);
                if (cond) nodesPValues[j] += inc;
            }
        }
        nodesAdjustedPValues = Adjusters.benjaminiHochberg(nodesPValues);
    }

    /**
     * Compute all pValues
     */
    private void computePValues() {
        if (!computedPValues) {
            computePathwayPValues();
            computeNodesPValues();
            computedPValues = true;
        }
    }

    /**
     * Reset the current instance clearing all its content
     *
     * @return this object for a fluent interface
     */
    public PhensimRun reset() {
        computedPValues = false;
        pathwayPValues = pathwayAdjustedPValues = nodesPValues = nodesAdjustedPValues = null;
        nodes.clear();
        pathways.clear();
        for (var simulationResult : simulationResults) {
            simulationResult.resetCounters();
        }
        return this;
    }

    /**
     * Returns the pathway repository linked to this object
     *
     * @return a repository object
     */
    public RepositoryInterface getRepository() {
        return repository;
    }

    /**
     * Get the number of iterations for the algorithm
     *
     * @return the number of iterations
     */
    public int getNumberOfIterations() {
        return numberOfIterations;
    }

    /**
     * Get the total number of simulations that will be performed for all iterations
     *
     * @return the number
     */
    public int getTotalNumberOfSimulations() {
        int total = 0;
        for (var s : simulationResults) {
            total += s.getNumberOfSimulations();
        }
        return total;
    }

    /**
     * Get the result of a specific simulation
     *
     * @param index the index
     * @return the simulation result
     */
    public SingleSimulation getSimulation(int index) {
        if (index < 0 || index >= simulationResults.length) throw new ArrayIndexOutOfBoundsException();
        return simulationResults[index];
    }

    /**
     * Get the main simulation result
     *
     * @return the result
     */
    public SingleSimulation getSimulation() {
        return simulationResults[0];
    }

    /**
     * Get the p-value of a pathway
     *
     * @param pathway the pathway identifier
     * @return the p-value
     */
    public double getPathwayPValue(String pathway) {
        if (!computedPValues) computePValues();
        int index = pathways.getOrDefault(pathway, -1);
        if (index < 0) return 1.0;
        return pathwayPValues[index];
    }

    /**
     * Get the adjusted p-value of a pathway
     *
     * @param pathway the pathway identifier
     * @return the p-value
     */
    public double getPathwayAdjustedPValue(String pathway) {
        if (!computedPValues) computePValues();
        int index = pathways.getOrDefault(pathway, -1);
        if (index < 0) return 1.0;
        return pathwayAdjustedPValues[index];
    }

    /**
     * Get the index of a node in a pathway
     *
     * @param pathway the pathway
     * @param node    the node
     * @return the index (or -1 if the node is not found)
     */
    private int getNodeIndex(String pathway, String node) {
        var p = simulationResults[0].getPathway(pathway);
        if (p == null) return -1;
        if (p.isVirtualPathway()) p = p.getVirtualPathwaySource();
        var map = nodes.get(p.getPathwayId());
        if (map == null) return -1;
        return map.getOrDefault(node, -1);
    }

    /**
     * Get the p-value of a node in a pathway
     *
     * @param pathway the pathway identifier
     * @param node    the node identifier
     * @return the p-value
     */
    public double getNodePValue(String pathway, String node) {
        if (!computedPValues) computePValues();
        var index = getNodeIndex(pathway, node);
        if (index < 0) return 1.0;
        return nodesPValues[index];
    }

    /**
     * Get the adjusted p-value of a node in a pathway
     *
     * @param pathway the pathway identifier
     * @param node    the node identifier
     * @return the p-value
     */
    public double getNodeAdjustedPValue(String pathway, String node) {
        if (!computedPValues) computePValues();
        var index = getNodeIndex(pathway, node);
        if (index < 0) return 1.0;
        return nodesAdjustedPValues[index];
    }

    /**
     * Gets the log probability of a pathway node
     *
     * @param pathway a pathway
     * @param node    the node
     * @return the probability
     */
    public double[] getLogProbabilities(String pathway, @Nullable String node) {
        return new double[]{
                simulationResults[0].getLogProbability(pathway, node, State.ACTIVE),
                simulationResults[0].getLogProbability(pathway, node, State.INHIBITED),
                simulationResults[0].getLogProbability(pathway, node, State.OTHERWISE)
        };
    }

    /**
     * Gets the log probability of a pathway
     *
     * @param pathway a pathway
     * @return the probability of the selected state
     */
    public double[] getPathwayLogProbabilities(String pathway) {
        return getLogProbabilities(pathway, null);
    }

    /**
     * Compute activity score for a pathway node
     *
     * @param pathway a pathway
     * @param node    the node
     * @return the activity score
     */
    public double getActivityScore(String pathway, @Nullable String node) {
//        var tmp = simulationResults[0].getActivityScore(pathway, node);
//        var med = getMedianActivityScore(pathway, node);
//        var sign = Math.signum(tmp);
//        return sign * Math.abs(tmp - med);
        return simulationResults[0].getActivityScore(pathway, node);
    }

    /**
     * Get the average perturbation for a node in a pathway
     *
     * @param pathway a pathway
     * @param node    a node
     * @return the perturbation
     */
    public double getAveragePerturbation(String pathway, String node) {
        return simulationResults[0].getAveragePerturbation(pathway, node);
    }

    /**
     * Get the average perturbation for the pathway
     *
     * @return the perturbation
     */
    public double getAveragePathwayPerturbation(String pathway) {
        return simulationResults[0].getAveragePathwayPerturbation(pathway);
    }

    /**
     * Compute median activity score for a pathway node
     *
     * @param pathway a pathway
     * @param node    the node
     * @return the median activity score
     */
    public double getMedianActivityScore(String pathway, @Nullable String node) {
        var n = simulationResults.length;
        var d = new double[n - 1];
        for (var i = 1; i < n; i++) {
            d[i - 1] = simulationResults[i].getActivityScore(pathway, node);
        }
        return new Median().withNaNStrategy(NaNStrategy.REMOVED).evaluate(d);
    }

    /**
     * Compute the activity score for a pathway
     *
     * @param pathway a pathway
     * @return the pathway activity score
     */
    public double getPathwayActivityScore(String pathway) {
//        return getActivityScore(pathway, null);
        var tmp = getActivityScore(pathway, null);
        var med = getMedianActivityScore(pathway, null);
        var sign = Math.signum(tmp);
        return sign * Math.abs(tmp - med);
    }

    /**
     * Set the list of direct targets and directly targeted genes from the input of a simulation
     *
     * @param simulateOn the input of a simulation
     * @return this object for a fluent interface
     */
    public PhensimRun setDirectTargets(List<String> simulateOn) {
        simulationResults[0].setDirectTargets(simulateOn);
        return this;
    }

    /**
     * Returns the list of direct targets
     *
     * @param pathway a pathway identifier
     * @return the list of direct targets
     */
    public List<String> getDirectTargets(String pathway) {
        return simulationResults[0].getDirectTargets(pathway);
    }

    /**
     * Returns a list of directly targeted genes of a pathway
     *
     * @param pathway a pathway identifier
     * @return the map containing all the directly targeted genes and their ancestors
     */
    public Map<String, List<String>> getDirectlyTargetedBy(String pathway) {
        return simulationResults[0].getDirectlyTargetedBy(pathway);
    }

    /**
     * Given an array of pathways returns an array with their median activity scores.
     *
     * @param pathways an array of pathway identifiers
     * @return an array of pathway activity scores
     */
    public double[] getPathwayMedianActivityScoresArray(@NotNull String[] pathways) {
        return Arrays.stream(pathways).mapToDouble(p -> this.getMedianActivityScore(p, null)).toArray();
    }

//    /**
//     * Given an array of (Pathway,Node) pairs returns an array with the median activity score of each node in its pathway
//     *
//     * @param nodes the array of (Pathway,Node) pairs
//     * @return an array of nodes activity score
//     */
//    public double[] getNodesMedianActivityScoresArray(@NotNull Pair<String, String>[] nodes) {
//        return Arrays.stream(nodes).mapToDouble(p -> this.getMedianActivityScore(p.getFirst(), p.getSecond())).toArray();
//    }

    /**
     * Make a printable matrix of pathway activity scores
     *
     * @return the printable matrix
     */
    public PrintableMatrix<String, String> makePathwayActivityScoresMatrix() {
        String[] pathways = simulationResults[0].getPathwayArray();
        var matrix = new PrintableMatrix<String, String>(simulationResults.length, pathways.length);
        var medianActivity = getPathwayMedianActivityScoresArray(pathways);
        matrix.setColumnNames(pathways);
        for (var i = 0; i < simulationResults.length; i++) {
//            var tmp = simulationResults[i].getPathwayActivityScoresArray(pathways);
            var tmp = correctActivityScore(
                    simulationResults[i].getPathwayActivityScoresArray(pathways),
                    medianActivity
            );
            matrix.setRow(i, tmp);
        }
        return matrix;
    }

    /**
     * Make a printable matrix of pathway activity scores
     *
     * @return the printable matrix
     */
    public PrintableMatrix<Pair<String, String>, String> makeNodesActivityScoresMatrix() {
        Pair<String, String>[] nodes = simulationResults[0].getNodesArray();
        var matrix = new PrintableMatrix<Pair<String, String>, String>(simulationResults.length, nodes.length);
//        var medianActivity = getNodesMedianActivityScoresArray(nodes);
        matrix.setColumnNames(nodes);
        for (int i = 0; i < simulationResults.length; i++) {
            var tmp = simulationResults[i].getNodesActivityScoresArray(nodes);
//            var sign = ArrayUtils.signum(tmp);
//            matrix.setRow(
//                    i,
//                    ArrayUtils.mulInPlace(ArrayUtils.absInPlace(ArrayUtils.diffInPlace(tmp, medianActivity)), sign)
//            );
            matrix.setRow(i, tmp);
        }
        return matrix;
    }
}
