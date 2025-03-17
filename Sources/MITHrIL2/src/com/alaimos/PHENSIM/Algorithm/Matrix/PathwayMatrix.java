package com.alaimos.PHENSIM.Algorithm.Matrix;

import com.alaimos.MITHrIL.Data.Pathway.Interface.EdgeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;
import com.alaimos.MITHrIL.Data.Reader.BinaryReader;
import com.alaimos.MITHrIL.Data.Writer.BinaryWriter;
import org.ojalgo.OjAlgoUtils;
import org.ojalgo.matrix.Primitive64Matrix;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.1
 */
public class PathwayMatrix {

    private final List<NodeInterface> nodes;
    private final Map<NodeInterface, Integer> nodesMap;
    private final List<EdgeInterface> backEdges;
    private final Primitive64Matrix.SparseReceiver matrixReceiver;
    private final ToDoubleFunction<NodeInterface> weightProvider;
    private Primitive64Matrix originalMatrix = null;
    private Primitive64Matrix builtMatrix;
    private List<Integer> nonExpressedNodes = null;
    private Map<Integer, HashMap<Integer, Double>> nonExpressedWeights = null;

    public PathwayMatrix(List<NodeInterface> nodes, Map<NodeInterface, Integer> nodesMap, List<EdgeInterface> backEdges,
                         ToDoubleFunction<NodeInterface> weightProvider, int threads,
                         Primitive64Matrix.SparseReceiver matrixReceiver, Primitive64Matrix inverseMatrix) {
        this.nodes = nodes;
        this.nodesMap = nodesMap;
        this.backEdges = backEdges;
        this.weightProvider = weightProvider;
        OjAlgoUtils.limitThreadsTo(threads);
        this.matrixReceiver = matrixReceiver;
        this.builtMatrix = inverseMatrix;
    }

    public PathwayMatrix(List<NodeInterface> nodes, Map<NodeInterface, Integer> nodesMap, List<EdgeInterface> backEdges,
                         ToDoubleFunction<NodeInterface> weightProvider, int threads) {
        this(nodes, nodesMap, backEdges, weightProvider, threads,
                Primitive64Matrix.FACTORY.makeSparse(nodes.size(), nodes.size()), null);
    }

    public double getOrig(NodeInterface u, NodeInterface v) {
        return originalMatrix.get((long) nodesMap.get(v), (long) nodesMap.get(u));
    }

    public void set(NodeInterface u, NodeInterface v, double value) {
        matrixReceiver.set((long) nodesMap.get(v), (long) nodesMap.get(u), value);
    }

    public void setNonExpressedNodes(List<Integer> nonExpressedNodes) {
        this.nonExpressedNodes = nonExpressedNodes;
    }

    public void setNonExpressedWeights(Map<Integer, HashMap<Integer, Double>> nonExpressedWeights) {
        this.nonExpressedWeights = nonExpressedWeights;
    }

    public void buildFromPreComputed() {
        originalMatrix = matrixReceiver.build();
    }

    public void build() {
        if (matrixReceiver != null && builtMatrix == null) {
            matrixReceiver.fillDiagonal(1.0);
            originalMatrix = matrixReceiver.build();
            builtMatrix = originalMatrix.invert();
        }
    }

    public List<NodeInterface> nodes() {
        return nodes;
    }

    public Map<NodeInterface, Integer> nodesMap() {
        return nodesMap;
    }

    public List<EdgeInterface> backEdges() {
        return backEdges;
    }

    public Primitive64Matrix matrix() {
        return builtMatrix;
    }

    public Primitive64Matrix originalMatrix() {
        return originalMatrix;
    }

    public double[] computePerturbation(Map<NodeInterface, Double> expressions) {
        if (builtMatrix == null) throw new NullPointerException("Pathway matrix has not been built!");
        var deReceiver = Primitive64Matrix.FACTORY.makeDense(this.nodes.size(), 1);
        for (var e : expressions.entrySet()) {
            deReceiver.set((long) nodesMap.get(e.getKey()), 0L, e.getValue());
        }
        var de = deReceiver.build();
        var perts = builtMatrix.multiply(de).toRawCopy1D();
        for (var i = 0; i < perts.length; i++) {
            if (!Double.isFinite(perts[i])) perts[i] = 0;
        }
/*
        for (var e : backEdges) {
            var start = e.getStart();
            var end = e.getEnd();
            var v = e.computeWeight() / weightProvider.applyAsDouble(start) * perts[nodesMap.get(start)];
            perts[nodesMap.get(end)] += v;
        }
*/
        if (nonExpressedWeights != null && nonExpressedNodes != null) {
            for (var e : nonExpressedWeights.entrySet()) {
                for (var e1 : e.getValue().entrySet()) {
                    perts[e.getKey()] += e1.getValue() * perts[e1.getKey()];
                }
            }
            for (var i : nonExpressedNodes) {
                perts[i] = 0;
            }
        }
        return perts;
    }

    public void write(String filename) {
        new BinaryWriter<SerializablePathwayMatrix>().write(filename, new SerializablePathwayMatrix(this));
    }

    public static SerializablePathwayMatrix read(String filename) {
        try {
            return new BinaryReader<>(SerializablePathwayMatrix.class).read(filename);
        } catch (Exception ignore) {
            return null;
        }
    }
}
