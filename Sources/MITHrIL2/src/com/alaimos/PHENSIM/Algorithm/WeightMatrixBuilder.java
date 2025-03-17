package com.alaimos.PHENSIM.Algorithm;

import com.alaimos.Commons.Utils.Pair;
import com.alaimos.MITHrIL.Data.Pathway.Interface.EdgeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.GraphInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.PathwayInterface;
import com.alaimos.PHENSIM.Algorithm.Matrix.PathwayMatrix;
import org.jetbrains.annotations.NotNull;
import org.ojalgo.OjAlgoUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.1
 */
public class WeightMatrixBuilder {


    private static final Map<PathwayInterface, WeightMatrixBuilder> instances = new ConcurrentHashMap<>();

    @NotNull
    public static WeightMatrixBuilder getInstance(@NotNull PathwayInterface p, int threads, Consumer<String> report) {
        if (!instances.containsKey(p)) {
            instances.put(p, new WeightMatrixBuilder(p, threads, report));
        }
        return instances.get(p);
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

    private final PathwayInterface pathway;
    private final GraphInterface graph;
    private final Map<NodeInterface, Double> absoluteWeights = new ConcurrentHashMap<>();
    private List<NodeInterface> nodes;
    private List<EdgeInterface> backEdges;
    private PathwayMatrix matrix;

    private WeightMatrixBuilder(@NotNull PathwayInterface pathway, int threads, Consumer<String> report) {
        this.pathway = pathway;
        this.graph = pathway.getGraph();
        if (report != null) report.accept("Trying to fetch cached matrix");
        var doCreate = true;
        String filename = "pathway-matrix-" + pathway.hashCode() + ".dat";
        var tmp = PathwayMatrix.read(filename);
        if (tmp != null) {
            nodes = tmp.getNodes(pathway);
            backEdges = tmp.getBackEdges(pathway);
            matrix = new PathwayMatrix(nodes, buildNodesMap(), backEdges, this::absoluteTotalWeight,
                    threads, tmp.getOriginalMatrix(), tmp.getInverseMatrix());
            this.matrix.buildFromPreComputed();
            doCreate = false;
        } else {
            if (report != null) report.accept("...Matrix not found! Building from scratch.\n");
        }
        if (doCreate) {
            this.initMatrixFromScratch(threads, report, filename);
        }
        if (report != null) report.accept("...Ok!\n");
    }

    /**
     * Initializes a matrix from scratch if it couldn't be found in the cache
     *
     * @param threads  the number of threads used for the computation
     * @param report   a report function
     * @param filename the filename used to cache the matrix
     */
    private void initMatrixFromScratch(int threads, Consumer<String> report, String filename) {
        this.nodes = sortNodes(new ArrayList<>(graph.getNodes().values()), graph);
        this.backEdges = new ArrayList<>();
        this.matrix = new PathwayMatrix(nodes, buildNodesMap(), backEdges, this::absoluteTotalWeight, threads);
        if (report != null) report.accept("Building matrix...Filling");
        this.buildMatrix();
        if (report != null) report.accept("...Inverting");
        this.matrix.build();
        if (report != null) report.accept("...Caching");
        this.matrix.write(filename);
        OjAlgoUtils.limitThreadsTo(1);
    }

    /**
     * Build a map that links a given node to the index in the normalized weights matrix
     *
     * @return the map
     */
    @NotNull
    private Map<NodeInterface, Integer> buildNodesMap() {
        Map<NodeInterface, Integer> map = new ConcurrentHashMap<>();
        for (var i = 0; i < nodes.size(); i++) {
            map.put(nodes.get(i), i);
        }
        return map;
    }

    /**
     * Enum that implements colors for the DFS visit
     */
    private enum Color {
        WHITE,
        GRAY,
        BLACK
    }

    /**
     * Compute the total weight of a given node. The total weight is obtained by
     * summing up the absolute weight values for all outgoing edges.
     *
     * @param u The node for which the total weight is computed
     * @return the total weight
     */
    private double absoluteTotalWeight(@NotNull NodeInterface u) {
        try {
            if (absoluteWeights.containsKey(u)) return absoluteWeights.get(u);
            double weight = 0.0;
            for (NodeInterface d : graph.outgoingNodes(u)) {
                var w = Math.abs(graph.getEdge(u, d).computeWeight());
                weight += w;
            }
            absoluteWeights.put(u, weight);
            return weight;
        } catch (NullPointerException ignore) {
        }
        return Double.NaN;
    }

    /**
     * Build the normalized weight matrix for the chosen pathway. It uses a modified DFS algorithm
     * that checks for back-edges, cross-edges, and forward-edges. All cross- and forward-edges are kept.
     * Back-edges in a path u~>v->u are kept only if the sign of the path (computed using the pathSign method)
     * u~>v is different from the sign of the weight of edge v->u.
     */
    private void buildMatrix() {
        HashMap<NodeInterface, Color> visited = new HashMap<>();
        Stack<NodeInterface> stack = new Stack<>();
        HashMap<NodeInterface, NodeInterface> predecessors = new HashMap<>();
        for (var u : nodes) {
            if (visited.getOrDefault(u, Color.WHITE) == Color.WHITE) {
                visit(u, visited, stack, predecessors);
            }
        }
    }

/*
    /**
     * Compute the sign of the value computed by multiplying all weights for the edges contained in the path from "u" to "v"
     *
     * @param u            Start node of the path
     * @param v            End node of the path
     * @param predecessors A map giving a predecessor in the DFS tree for each visited node
     * @return The sign
     * /
    private double pathSign(NodeInterface u, NodeInterface v, @NotNull HashMap<NodeInterface, NodeInterface> predecessors) {
        NodeInterface pred, curr = v;
        double weight = 1;
        do {
            pred = predecessors.get(curr);
            if (pred == null) return 0;
            weight *= graph.getEdge(pred, curr).computeWeight(pathway);
            curr = pred;
        } while (pred != u);
        return Math.signum(weight);
//                        bSign = Math.signum(graph.getEdge(u, v).computeWeight());
//                        fSign = pathSign(v, u, predecessors);
//                        if (fSign != 0 & bSign != fSign) {
//                            this.matrix.set(u, v, w);
//                        } else if (fSign != 0) {
//                            backEdges.add(graph.getEdge(u, v));
//                        }
    }
*/

    /**
     * Implementation of the modified DFS algorithm that starting from a node "u" compute the normalized weight matrix.
     * It checks for back-edges, cross-edges, and forward-edges. All cross- and forward-edges are kept.
     * Back-edges in a path u~>v->u are kept only if the sign of the path (computed using the pathSign method)
     * u~>v is different from the sign of the weight of edge v->u.
     *
     * @param u            The starting point of the visit
     * @param visited      A map containing visited nodes
     * @param stack        The stack used to guide the visit
     * @param predecessors A map giving a predecessor in the DFS tree for each visited node
     */
    private void visit(NodeInterface u, HashMap<NodeInterface, Color> visited,
                       @NotNull Stack<NodeInterface> stack,
                       HashMap<NodeInterface, NodeInterface> predecessors) {
        Color cu;
        double w, wT;
        NodeInterface v;
        List<NodeInterface> outgoing;
        int outgoingSize;
        stack.push(u);
        while (!stack.isEmpty()) {
            u = stack.peek();
            cu = visited.getOrDefault(u, Color.WHITE);
            wT = absoluteTotalWeight(u);
            if (cu == Color.GRAY) {
                visited.put(u, Color.BLACK);
                stack.pop();
            } else if (cu == Color.WHITE) {
                visited.put(u, Color.GRAY);
                outgoing = graph.outgoingNodes(u);
                outgoingSize = outgoing.size();
                for (var i = outgoingSize - 1; i >= 0; i--) {
                    v = outgoing.get(i);
                    w = -graph.getEdge(u, v).computeWeight() / wT;
                    w = Double.isFinite(w) ? w : 0.0;
                    if (visited.getOrDefault(v, Color.WHITE) == Color.WHITE) {
                        this.matrix.set(u, v, w);
                        predecessors.put(v, u);
                        stack.push(v);
                    } else if (visited.getOrDefault(v, Color.WHITE) == Color.GRAY) {
                        backEdges.add(graph.getEdge(u, v));
                    } else if (visited.getOrDefault(v, Color.WHITE) == Color.BLACK) {
                        this.matrix.set(u, v, w);
                    }
                }
            } else {
                var p = predecessors.get(u);
                if (p != null) {
                    w = -graph.getEdge(p, u).computeWeight() / absoluteTotalWeight(p);
                    w = Double.isFinite(w) ? w : 0.0;
                    this.matrix.set(p, u, w);
                }
                stack.pop();
            }
        }
    }

    /**
     * Returns the pathway matrix object computed by this builder
     *
     * @return The PathwayMatrix object
     */
    public PathwayMatrix getMatrix() {
        return this.matrix;
    }

    /**
     * Returns the list of back-edges that are not included in the weight matrix
     *
     * @return The list of back-edges
     */
    public List<EdgeInterface> getBackEdges() {
        return this.backEdges;
    }

    /**
     * Converts an expression map used by PHENSIM in a version that can be used by this modified implementation
     *
     * @param expressions The PHENSIM expression map
     * @return The converted expression map
     */
    public Map<NodeInterface, Double> convertExpressions(@NotNull Map<String, Double> expressions) {
        var newExpressions = new HashMap<NodeInterface, Double>();
        for (var e : expressions.entrySet()) {
            var n = graph.getNode(e.getKey());
            if (n != null) {
                newExpressions.put(n, e.getValue());
            }
        }
        return newExpressions;
    }

    public void nonExpressedVisit(String nonExpressedNode, List<Integer> nonExp, Map<Integer, HashMap<Integer, Double>> weights) {
        var u = graph.getNode(nonExpressedNode);
        if (u == null) return;
        var map = matrix.nodesMap();
        var stack = new Stack<Pair<NodeInterface, Double>>();
        var visited = new HashSet<NodeInterface>();
        int uIdx = map.get(u);
        var min = (double) 1 / (2 * graph.countEdges());
        var startIdx = uIdx;
        nonExp.add(startIdx);
        NodeInterface v;
        List<NodeInterface> outgoing;
        Pair<NodeInterface, Double> tmp;
        int outgoingSize;
        double w, wT;
        stack.push(new Pair<>(u, 1.0));
        while (!stack.isEmpty()) {
            tmp = stack.pop();
            u = tmp.getFirst();
            uIdx = map.get(u);
            if (visited.contains(u)) continue;
            w = tmp.getSecond();
            visited.add(u);
            if (uIdx != startIdx) {
                if (!weights.containsKey(uIdx)) weights.put(uIdx, new HashMap<>());
                weights.get(uIdx).put(startIdx, w);
            }
            outgoing = graph.outgoingNodes(u);
            outgoingSize = outgoing.size();
            for (var i = outgoingSize - 1; i >= 0; i--) {
                v = outgoing.get(i);
                wT = w * matrix.getOrig(u, v);
                if (u != v && !visited.contains(v) && Math.abs(wT) > min) {
                    stack.push(new Pair<>(v, wT));
                }
            }
        }
    }

    public void setNonExpressed(@NotNull List<String> nonExpressedNodes) {
        var l = new ArrayList<Integer>();
        var weights = new ConcurrentHashMap<Integer, HashMap<Integer, Double>>();
        for (String node : nonExpressedNodes) {
            nonExpressedVisit(node, l, weights);
        }
        if (l.size() > 0) {
            matrix.setNonExpressedNodes(l);
            matrix.setNonExpressedWeights(weights);
        }
    }

}
