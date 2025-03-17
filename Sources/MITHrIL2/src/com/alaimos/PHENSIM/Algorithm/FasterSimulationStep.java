package com.alaimos.PHENSIM.Algorithm;

import com.alaimos.MITHrIL.Data.Pathway.Interface.PathwayInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.PHENSIM.Data.Enums.State;
import com.alaimos.PHENSIM.Data.Generator.RandomExpressionGenerator;
import com.alaimos.PHENSIM.Data.Results.SingleSimulation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Implementation of the PHENSIM simulation step
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.1
 */
public class FasterSimulationStep {

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
    private final RandomExpressionGenerator randomExpressionGenerator;
    private final SingleSimulation simulationResults;
    private final EventSender notifyStepEnd;
    private final Map<PathwayInterface, WeightMatrixBuilder> builders;
    //endregion

    //region SuperFAST MITHrIL Implementation (only nodes and pathway perturbations are computed)
    protected Map<String, Double> expressions;
    protected Map<String, Map<String, Map<String, Double>>> visitedPerturbations;
    protected Map<String, Map<String, Double>> perturbations;

    /**
     * Run the fast implementation of mithril
     */
    private void fastMITHrILRun() {
        var excludedNodes = randomExpressionGenerator.getNodesSet();
        expressions = randomExpressionGenerator.getRandomExpressions();
        var pertsCache = new HashMap<PathwayInterface, double[]>();
        for (var p : repository) {
            if (p.hasGraph()) {
                var g = p.getGraph();
                if (g.countNodes() > 0 && g.countEdges() > 0) {
                    var builder = builders.get(p);
                    var nodes = builder.getMatrix().nodes();
                    var perts = builder.getMatrix().computePerturbation(builder.convertExpressions(expressions));
                    var pathwayPerturbation = 0d;
                    var size = nodes.size();
                    var pId = p.getId();
                    for (int i = 0; i < size; i++) {
                        var node = nodes.get(i);
                        var id = node.getId();
                        var pert = perts[i];
                        if (!excludedNodes.contains(id) && pert != 0)
                            pathwayPerturbation += node.getType().sign() * pert;
                        checkNodePerturbation(pId, id, pert);
                    }
                    checkPathwayPerturbation(pId, pathwayPerturbation);
                    pertsCache.put(p, perts);
                }
            }
        }
        for (var p : repository.getVirtualPathways()) {
            var pathwayPerturbation = 0.0;
            var source = repository.getSourceOfVirtualPathway(p);
            var nodes = repository.getNodesOfVirtualPathway(p);
            var builder = builders.get(source);
            var nodesMap = builder.getMatrix().nodesMap();
            var cache = pertsCache.get(source);
            for (var n : nodes) {
                var id = n.getId();
                if (!excludedNodes.contains(id)) {
                    pathwayPerturbation += n.getType().sign() * cache[nodesMap.get(n)];
                }
            }
            checkPathwayPerturbation(p, pathwayPerturbation);
        }
    }
    //endregion

    /**
     * Class constructor
     *
     * @param randomExpressionGenerator a random LogFCs generator
     * @param epsilon                   the epsilon value
     * @param simulationResults         the object where all results will be stored
     * @param notifyStepEnd             notify to the caller thread that a step has been completed
     */
    public FasterSimulationStep(RandomExpressionGenerator randomExpressionGenerator, double epsilon,
                                @NotNull SingleSimulation simulationResults, @Nullable EventSender notifyStepEnd) {
        this.repository = simulationResults.getRepository();
        this.epsilon = epsilon;
        this.numberOfRepetitions = simulationResults.getNumberOfSimulations();
        this.randomExpressionGenerator = randomExpressionGenerator;
        this.simulationResults = simulationResults;
        this.notifyStepEnd = notifyStepEnd;
        this.builders = simulationResults.getBuilders();
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