package com.alaimos.SPECifIC.Data.Structures;

import com.alaimos.Commons.Math.PValue.Combiner;
import com.alaimos.Commons.Math.PValue.EmpiricalBrowns.EmpiricalBrownsMethod;
import com.alaimos.Commons.Utils.Pair;
import com.alaimos.MITHrIL.Data.Pathway.Interface.EdgeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;
import com.google.common.base.Preconditions;
import com.pengyifan.commons.collections.tree.Tree;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A node in the tree of a DFS visit. Each path from the root of this tree to another node represents a path with a
 * certain p-value a source graph.
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 16/12/2016
 */
public class VisitTree extends Tree<GraphVisitNode, VisitTree> {

    protected static HashSet<String> allPaths         = new HashSet<>();
    public static    int             duplicateCounter = 0;

    private boolean  marked = false;
    private Combiner combiner;
    private double   adjustedPathPValue;
    private double   adjustedNeighborhoodPValue;
    private double   adjustedTreePValue;

    public VisitTree() {
    }

    public VisitTree(GraphVisitNode obj) {
        super(obj);
    }

    private String getHash(VisitTree nextNode) {
        return Stream.concat(
                this.getPathFromRoot().stream().map(tn -> tn.getObject().getNode().getId()),
                Stream.of(nextNode.getObject().getNode().getId())
        ).sorted().collect(Collectors.joining("-"));
    }

    public boolean checkAndAdd(VisitTree child) {
        Preconditions.checkNotNull(child, "The child is null");
        Preconditions.checkArgument(!this.isNodeAncestor(child), "The child is an ancestor of this node");
        String hash = getHash(child);
        if (allPaths.add(hash)) {
            add(child);
            return true;
        } else {
            duplicateCounter++;
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(getObject(), getParent());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof VisitTree)) {
            return false;
        }
        VisitTree rhs = (VisitTree) obj;
        return Objects.equals(getObject(), rhs.getObject());
    }

    public Stream<Double> getPathNodesPValuesStream() {
        return this.getPathFromRoot().stream().map(tn -> tn.getObject().getPValue());
    }

    /**
     * Returns the list of p-values from the root of this tree to this node
     *
     * @return A list of p-values
     */
    public double[] getPathNodesPValues() {
        return getPathNodesPValuesStream().mapToDouble(Double::doubleValue).toArray();
    }

    /**
     * Returns the list of perturbations from the root of this tree to this node
     *
     * @return A list of perturbations
     */
    public double[] getPathNodesPerturbations() {
        return this.getPathFromRoot().stream().mapToDouble(tn -> tn.getObject().getPerturbation()).toArray();
    }

    /**
     * Returns the list of nodes from the root of this tree to this node
     *
     * @return A list of nodes
     */
    public NodeInterface[] getPathNodes() {
        return this.getPathFromRoot().stream().map(tn -> tn.getObject().getNode()).toArray(NodeInterface[]::new);
    }

    /**
     * Returns the list of edges from the root of this tree to this node
     *
     * @return A list of edges
     */
    public EdgeInterface[] getPathEdges() {
        return this.getPathFromRoot().stream().map(tn -> tn.getObject().getEdge()).filter(Objects::nonNull)
                   .toArray(EdgeInterface[]::new);
    }

    /**
     * Compute the cumulative p-value of this path using a specific p-value combiner
     *
     * @param combiner a combiner method
     * @return a p-value
     */
    public double getPathPValue(Combiner combiner) {
        EmpiricalBrownsMethod.setFilter(Arrays.stream(getPathNodes()).map(NodeInterface::getId).toArray(String[]::new));
        return combiner.combine(getPathNodesPValuesStream().mapToDouble(Double::doubleValue).toArray());
    }

    /**
     * Compute the cumulative p-value of this path using a specific p-value combiner
     *
     * @return a p-value
     */
    public double getPathPValue() {
        EmpiricalBrownsMethod.setFilter(Arrays.stream(getPathNodes()).map(NodeInterface::getId).toArray(String[]::new));
        return combiner.combine(getPathNodesPValuesStream().mapToDouble(Double::doubleValue).toArray());
    }

    /**
     * Compute the extended cumulative p-value of this path using a specific p-value combiner and an extension
     *
     * @param combiner a combiner method
     * @return a p-value
     */
    public double getPathExtendedPValue(Combiner combiner, String extendedNode, double extension) {
        EmpiricalBrownsMethod.setFilter(
                Stream.concat(Arrays.stream(getPathNodes()).map(NodeInterface::getId), Stream.of(extendedNode))
                      .toArray(String[]::new));
        return combiner.combine(Stream.concat(getPathNodesPValuesStream(), Stream.of(extension))
                                      .mapToDouble(Double::doubleValue)
                                      .toArray());
    }

    /**
     * Compute the cumulative p-value threshold of this path using a specific p-value combiner and an extension
     *
     * @param combiner      a p-value combiner
     * @param extendedNode  an extension
     * @param nodeThreshold single node threshold
     * @return the combined threshold
     */
    public double getPathExtendedThresholdPValue(Combiner combiner, String extendedNode, double nodeThreshold) {
        String[] filter = Stream.concat(Arrays.stream(getPathNodes()).map(NodeInterface::getId), Stream.of(extendedNode))
                                .toArray(String[]::new);
        EmpiricalBrownsMethod.setFilter(filter);
        double[] pvalues = new double[filter.length];
        Arrays.fill(pvalues, nodeThreshold);
        return combiner.combine(pvalues);
    }

    /**
     * Compute the extended cumulative p-value of this path using a specific p-value combiner and an extension
     *
     * @param combiner a combiner method
     * @return a p-value
     */
    public double[] getPathExtendedPValue(Combiner combiner, Pair<String, Double>[] extension) {
        return Arrays.stream(extension).mapToDouble(e -> getPathExtendedPValue(combiner, e.getFirst(), e.getSecond()))
                     .toArray();
    }

    /**
     * Is this node marked as the last in a significant path?
     *
     * @return Is this node marked as the last in a significant path?
     */
    public boolean isMarked() {
        return marked;
    }

    /**
     * Checks if this node should be marked
     *
     * @param minNumberOfLevels a min number of levels
     * @param maxPathPValue     a max p-value
     * @param combiner          a p-value combiner
     */
    public void checkMark(int minNumberOfLevels, double maxPathPValue, Combiner combiner) {
        int level = this.getLevel();
        double pv = this.getPathPValue(combiner);
        this.combiner = combiner;
        marked = this.isLeaf() && level >= minNumberOfLevels && pv <= maxPathPValue;
    }

    /**
     * Count the number of marked leaves
     *
     * @return the number of marked leaves
     */
    public int countMarked() {
        return ((int) this.getLeaves().stream().filter(VisitTree::isMarked).count());
    }

    /**
     * Returns a stream of marked leaves
     *
     * @return the stream of marked leaves
     */
    public Stream<VisitTree> getMarkedLeavesStream() {
        return this.getLeaves().stream().filter(VisitTree::isMarked);
    }

    /**
     * Returns a list of marked leaves
     *
     * @return the list of marked leaves
     */
    public List<VisitTree> getMarkedLeaves() {
        return getMarkedLeavesStream().collect(Collectors.toList());
    }

    /**
     * Return the accumulator for the neighborhood of the current node
     *
     * @return double
     */
    public double getNeighborhoodAccumulator() {
        return this.getObject().getNode().getType().sign() * this.getObject().getPerturbation() +
                children().stream()
                          .mapToDouble(n -> n.getObject().getNode().getType().sign() * n.getObject().getPerturbation())
                          .sum();
    }

    /**
     * Return the accumulator for the path from this node to the root
     *
     * @return double
     */
    public double getPathAccumulator() {
        NodeInterface[] nodes = getPathNodes();
        double[] perts = getPathNodesPerturbations();
        double acc = 0.0;
        for (int i = 0; i < perts.length; i++) {
            acc += nodes[i].getType().sign() * perts[i];
        }
        return acc;
    }

    /**
     * Returns the accumulator computed for the tree starting in this node
     *
     * @return double
     */
    public double getTreeAccumulator() {
        return stream().mapToDouble(n -> n.getObject().getNode().getType().sign() * n.getObject().getPerturbation())
                       .sum();
    }

    public double getNeighborhoodPValue() {
        return getNeighborhoodPValue(combiner);
    }

    public double getNeighborhoodPValue(Combiner combiner) {
        EmpiricalBrownsMethod
                .setFilter(children().stream().map(n -> n.getObject().getNode().getId()).toArray(String[]::new));
        return combiner.combine(children().stream().mapToDouble(n -> n.getObject().getPValue()).toArray());
    }

    public double getTreePValue() {
        return getTreePValue(combiner);
    }

    public double getTreePValue(Combiner combiner) {
        EmpiricalBrownsMethod.setFilter(stream().map(n -> n.getObject().getNode().getId()).toArray(String[]::new));
        return combiner.combine(stream().mapToDouble(n -> n.getObject().getPValue()).toArray());
    }

    /**
     * Returns a sequential {@code Stream} with this tree as its source.
     *
     * @return a sequential {@code Stream} over the nodes of this tree
     */
    public Stream<VisitTree> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    /**
     * Set a p-value combiner
     *
     * @param combiner a p-value combiner
     * @return this object for a fluent interface
     */
    public VisitTree setCombiner(Combiner combiner) {
        this.combiner = combiner;
        this.children().forEach(c -> c.setCombiner(combiner));
        return this;
    }

    /**
     * Get a p-value combiner
     *
     * @return a p-value combiner
     */
    public Combiner getCombiner() {
        return combiner;
    }

    public double getAdjustedPathPValue() {
        return adjustedPathPValue;
    }

    public void setAdjustedPathPValue(double adjustedPathPValue) {
        this.adjustedPathPValue = adjustedPathPValue;
    }

    public double getAdjustedNeighborhoodPValue() {
        return adjustedNeighborhoodPValue;
    }

    public void setAdjustedNeighborhoodPValue(double adjustedNeighborhoodPValue) {
        this.adjustedNeighborhoodPValue = adjustedNeighborhoodPValue;
    }

    public double getAdjustedTreePValue() {
        return adjustedTreePValue;
    }

    public void setAdjustedTreePValue(double adjustedTreePValue) {
        this.adjustedTreePValue = adjustedTreePValue;
    }
}
