package com.alaimos.PHENSIM.Algorithm.OLD;

import com.alaimos.MITHrIL.Algorithm.MITHrIL;
import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.PathwayInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.PHENSIM.Data.Enums.State;
import com.alaimos.PHENSIM.Data.Generator.RandomExpressionGenerator;
import com.alaimos.PHENSIM.Data.Results.SingleSimulation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Implementation of the PHENSIM simulation step
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 03/08/2020
 */
public class SimulationStep {

    /**
     * A list of sorted nodes for each pathways. Stored globally to avoid computing it for each step.
     */
    private static Map<PathwayInterface, List<NodeInterface>> sortedNodes = null;

    /**
     * Set the new list of sorted nodes for each pathway
     *
     * @param newSortedNodes the list of sorted nodes
     */
    private synchronized void setSortedNodesIfNeeded(Map<PathwayInterface, List<NodeInterface>> newSortedNodes) {
        if (sortedNodes == null) {
            sortedNodes = newSortedNodes;
        }
    }

    /**
     * Clears the list of sorted nodes
     */
    public static void clearSortedNodes() {
        sortedNodes = null;
    }

    //region Functional Interfaces

    /**
     * A simple functional interface used to wrap a method that handles an event
     */
    @FunctionalInterface
    public interface EventSender {
        void send();
    }
    //endregion

    //region Fast MITHrIL
    private static class FastMITHrIL extends MITHrIL {

        /**
         * Returns a map os sorted nodes for each pathways.
         *
         * @return a map of sorted nodes
         */
        Map<PathwayInterface, List<NodeInterface>> getSortedNodes() {
            return sortedNodes;
        }

        /**
         * Run MITHrIL
         *
         * @param random            a random number generator
         * @param expressions       a map of expression values indexed by node id
         * @param repository        a pathway repository
         * @param nonExpressedNodes a list of non expressed nodes
         */
        void fastRun(Random random, Map<String, Double> expressions, @NotNull RepositoryInterface repository,
                     List<String> nonExpressedNodes, Map<PathwayInterface, List<NodeInterface>> sortedNodes,
                     SimulationStep thisStep) {
            init();
            if (sortedNodes != null) {
                this.sortedNodes = sortedNodes;
            }
            this.random = random;
            this.expressions = expressions;
            this.repository = repository;
            for (var p : repository) {
                if (p.hasGraph()) {
                    var g = p.getGraph();
                    if (g.countNodes() > 0 && g.countEdges() > 0) {
                        var pId = p.getId();
                        nonExpressedNodes.stream().filter(g::hasNode).forEach(n -> {
                            if (!count.containsKey(pId)) count.put(pId, new HashMap<>());
                            count.get(pId).put(n, 0);
                            putPerturbation(n, pId, 0.0, 0.0);
                        });
                        var pert = 0.0;
                        var nodes = getSortedNodes(p);
                        for (var n : nodes) {
                            var nm = n.getId();
                            var tmp = perturbation(n, p);
                            pert += n.getType().sign() * tmp;
                            thisStep.checkNodePerturbation(pId, nm, tmp);
                        }
                        thisStep.checkPathwayPerturbation(pId, pert);
                    }
                }
            }
            for (var p : repository.getVirtualPathways()) {
                var pert = 0.0;
                var source = repository.getSourceOfVirtualPathway(p);
                var nodes = repository.getNodesOfVirtualPathway(p);
                for (var n : nodes) {
                    pert += n.getType().sign() * perturbation(n, source);
                }
                thisStep.checkPathwayPerturbation(p, pert);
            }
        }

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

    /**
     * Class constructor
     *
     * @param epsilon                   the epsilon value
     * @param nonExpressedNodes         the list of non expressed nodes
     * @param randomExpressionGenerator a random expression generator
     * @param simulationResults         the object where all results for this iteration will be stored
     */
    public SimulationStep(RandomExpressionGenerator randomExpressionGenerator, double epsilon,
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
        var mithril = new FastMITHrIL();
        for (int i = 0; i < numberOfRepetitions; i++) {
            var expressions = randomExpressionGenerator.getRandomExpressions();
            mithril.init();
            mithril.fastRun(randomExpressionGenerator.getRandom(), expressions, repository, nonExpressedNodes,
                    sortedNodes, this);
            notifyEnd();
            if (sortedNodes == null) setSortedNodesIfNeeded(mithril.getSortedNodes());
        }
    }
}