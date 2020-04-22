package com.alaimos.SPECifIC.Algorithm;

import com.alaimos.Commons.Algorithm.Impl.AbstractAlgorithm;
import com.alaimos.Commons.Algorithm.Interface.AlgorithmInterface;
import com.alaimos.Commons.Math.PValue.Combiner;
import com.alaimos.Commons.Math.PValue.EmpiricalBrowns.EmpiricalBrownsMethod;
import com.alaimos.Commons.Utils.Pair;
import com.alaimos.MITHrIL.Data.Pathway.Interface.EdgeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.PathwayInterface;
import com.alaimos.MITHrIL.Data.Results.PathwayAnalysisResult;
import com.alaimos.SPECifIC.Data.Structures.GraphVisitNode;
import com.alaimos.SPECifIC.Data.Structures.VisitTree;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 05/01/2016
 */
public class OptimizedVisit extends AbstractAlgorithm<VisitTree> {

    //region Private Classes

    /**
     * An element for the visit queue
     */
    private class Element extends Pair<NodeInterface, VisitTree> {
        private static final long serialVersionUID = 3671720235690774860L;

        Element(NodeInterface first, VisitTree second) {
            super(first, second);
        }
    }

    //endregion
    //region Input Parameters
    private PathwayInterface                                        pathway;
    private Combiner                                                pValueCombiner;
    private int                                                     minNumberOfLevels;
    private double                                                  nodesMaxPValue;
    private double                                                  pathsMaxPValue;
    private Function<NodeInterface, List<NodeInterface>>            nodesSupplier;
    private BiFunction<NodeInterface, NodeInterface, EdgeInterface> edgesSupplier;
    //endregion
    //region Output Values
    private VisitTree                                               visitTree;
    //endregion
    //region Internal Variables
    private HashSet<NodeInterface>                                  visited;
    private Map<String, Double>                                     allPValues;
    private Map<String, Double>                                     allPerturbations;
    private Map<String, Double>                                     allAccumulators;
    //endregion
    //region Private utility methods

    /**
     * Get the p-value computed by MITHrIL for a node in the graph
     *
     * @param n a node
     * @return a p-value for the specified node
     */
    private double getPValue(@NotNull NodeInterface n) {
        return allPValues.getOrDefault(n.getId(), 1.0);
    }


    /**
     * Get the perturbation computed by MITHrIL for a node in the graph
     *
     * @param n a node
     * @return a perturbation for the specified node
     */
    private double getPerturbation(@NotNull NodeInterface n) {
        return allPerturbations.getOrDefault(n.getId(), 0.0);
    }

    /**
     * Get the accumulator computed by MITHrIL for a node in the graph
     *
     * @param n a node
     * @return a perturbation for the specified node
     */
    private double getAccumulator(@NotNull NodeInterface n) {
        return allAccumulators.getOrDefault(n.getId(), 0.0);
    }

    /**
     * Make a node for the tree which represents the graph visit
     *
     * @param n A Node in the graph
     * @return A Node for the visit tree
     */
    @NotNull
    @Contract("_ -> !null")
    private VisitTree makeTreeNode(NodeInterface n) {
        return new VisitTree(new GraphVisitNode(n, getPValue(n), getAccumulator(n)));
    }

    /**
     * Make a node for the tree which represents the graph visit and add an edge to the previous node
     *
     * @param n A node in the graph
     * @param s The parent node in the visit tree
     * @return A node for the visit tree
     */
    @Contract("_, _ -> !null")
    private VisitTree makeTreeNode(NodeInterface n, VisitTree s) {
        VisitTree tmp = makeTreeNode(n);
        if (s != null) {
            tmp.getObject().setEdge(edgesSupplier.apply(s.getObject().getNode(), n));
        }
        return tmp;
    }

    /**
     * Make an element for the visit queue used to simulate recursion during the visit
     *
     * @param n a node in the graph
     * @return an element for the stack
     */
    @NotNull
    @Contract("_ -> !null")
    private Element makeVisitQueueElement(NodeInterface n) {
        return makeVisitQueueElement(n, null);
    }

    /**
     * Make an element for the visit queue used to simulate recursion during the visit
     *
     * @param n a node in the graph
     * @param t the previous node in the visit
     * @return an element for the stack
     */
    @NotNull
    @Contract("_, _ -> !null")
    private Element makeVisitQueueElement(NodeInterface n, VisitTree t) {
        return new Element(n, t);
    }

    /**
     * Compute the p-value that will be obtained by extending the current path with a new node
     *
     * @param currTree the last node in the current path
     * @param nn       the node that will be added
     * @return a p-value
     */
    private double computeExtendedPValue(@NotNull VisitTree currTree, @NotNull NodeInterface nn) {
        return currTree.getPathExtendedPValue(pValueCombiner, nn.getId(), getPValue(nn));
    }

    /**
     * Compute the p-value threshold that will be used for the extended path
     *
     * @param currTree the last node in the current path
     * @param nn       the node that will be added
     * @return a p-value threshold
     */
    private double computeExtendedThreshold(@NotNull VisitTree currTree, @NotNull NodeInterface nn) {
        return currTree.getPathExtendedThresholdPValue(pValueCombiner, nn.getId(), nodesMaxPValue);
    }

    /**
     * Initializes suppliers for the visit
     *
     * @param reversed Is backward visit?
     */
    private void initSuppliers(boolean reversed) {
        if (reversed) {
            nodesSupplier = pathway.getGraph()::ingoingNodes;
            edgesSupplier = (u, v) -> pathway.getGraph().getEdge(v, u);
        } else {
            nodesSupplier = pathway.getGraph()::outgoingNodes;
            edgesSupplier = (u, v) -> pathway.getGraph().getEdge(u, v);
        }
    }

    //endregion

    /**
     * Prepares the workflow for a new execution
     *
     * @return This object for a fluent interface
     */
    @Override
    public AlgorithmInterface<VisitTree> init() {
        super.init();
        visited = new HashSet<>();
        allPValues = null;
        allAccumulators = null;
        return this;
    }

    /**
     * A graph visit constrained by p-value
     *
     * @param n A starting point
     */
    private void visit(NodeInterface n) {
        Element curr, root = makeVisitQueueElement(n);
        Queue<Element> visitQueue = new ArrayDeque<>();
        visitQueue.add(root);
        VisitTree prevTree, currTree;
        while ((curr = visitQueue.poll()) != null) {
            NodeInterface currNode = curr.getFirst();
            if (visited.add(currNode)) {
                prevTree = curr.getSecond();
                currTree = makeTreeNode(currNode, prevTree);
                if (prevTree != null) {
                    if (!prevTree.checkAndAdd(currTree)) { // if a duplicate path is found
                        continue;                          // continue without exploring the current node
                    }
                } else {
                    output = currTree;
                }
                for (NodeInterface nn : nodesSupplier.apply(curr.getFirst())) {
                    if (!visited.contains(nn) &&
                            getPerturbation(nn) != 0.0 &&
                            getPValue(nn) <= nodesMaxPValue &&
                            computeExtendedPValue(currTree, nn) < computeExtendedThreshold(currTree, nn)) {
                        visitQueue.add(makeVisitQueueElement(nn, currTree));
                    }
                }
            }
        }
        if (output != null) {
            for (VisitTree l : output.getLeaves()) {
                l.checkMark(minNumberOfLevels, pathsMaxPValue, pValueCombiner);
            }
        }
    }

    /**
     * Runs an optimized visit of the graph starting from a node
     */
    @Override
    public void run() {
        pathway = getParameterNotNull("pathway", PathwayInterface.class);
        PathwayAnalysisResult result = getParameterNotNull("pathwayAnalysisResult", PathwayAnalysisResult.class);
        NodeInterface start = getParameterNotNull("startingNode", NodeInterface.class);
        pValueCombiner = getParameter("combiner", Combiner.class, new EmpiricalBrownsMethod());
        pathsMaxPValue = 1.0; //getParameter("maxPValuePaths", Double.class, 1e-5);
        nodesMaxPValue = getParameter("maxPValueNodes", Double.class, 0.05);
        minNumberOfLevels = getParameter("minNumberOfNodes", Integer.class, 5) - 1;
        boolean reversed = getParameter("reversed", Boolean.class, false);
        String pId = pathway.getId();
        allPerturbations = result.getPerturbations().get(pId);
        allAccumulators = result.getNodeAccumulators().get(pId);
        allPValues = result.getNodePValues().get(pId);
        initSuppliers(reversed);
        if (pathway.getGraph().hasNode(start) &&
                nodesSupplier.apply(start).size() > 0 &&
                getPValue(start) < nodesMaxPValue) {
            visit(start);
        }
    }
}
