package com.alaimos.PHENSIM.Algorithm.Matrix;

import com.alaimos.MITHrIL.Data.Pathway.Interface.EdgeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.PathwayInterface;
import org.ojalgo.matrix.Primitive64Matrix;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.1
 */
public class SerializablePathwayMatrix implements Serializable {

    private static final long serialVersionUID = 1603038152607265620L;
    private final String[] nodes;
    private final int[][] backEdges;
    private final double[] originalMatrix;
    private final double[] inverseMatrix;

    public SerializablePathwayMatrix(PathwayMatrix matrix) {
        var map = matrix.nodesMap();
        this.nodes = matrix.nodes().stream().map(NodeInterface::getId).toArray(String[]::new);
        this.backEdges = matrix.backEdges().stream().map(e -> new int[]{map.get(e.getStart()), map.get(e.getEnd())}).toArray(int[][]::new);
        this.originalMatrix = matrix.originalMatrix().toRawCopy1D();
        this.inverseMatrix = matrix.matrix().toRawCopy1D();
    }

    public List<NodeInterface> getNodes(PathwayInterface p) {
        var g = p.getGraph();
        var r = new ArrayList<NodeInterface>();
        for (var n : nodes) {
            r.add(g.getNode(n));
        }
        return r;
    }

    public List<EdgeInterface> getBackEdges(PathwayInterface p) {
        var g = p.getGraph();
        var r = new ArrayList<EdgeInterface>();
        for (var e : backEdges) {
            r.add(g.getEdge(nodes[e[0]], nodes[e[1]]));
        }
        return r;
    }

    public Primitive64Matrix.SparseReceiver getOriginalMatrix() {
        var res = Primitive64Matrix.FACTORY.makeSparse(nodes.length, nodes.length);
        for (var i = 0; i < originalMatrix.length; i++) {
            res.set(i, originalMatrix[i]);
        }
        return res;
    }

    public Primitive64Matrix getInverseMatrix() {
        var res = Primitive64Matrix.FACTORY.makeDense(nodes.length, nodes.length);
        for (var i = 0; i < inverseMatrix.length; i++) {
            res.set(i, inverseMatrix[i]);
        }
        return res.build();
    }
}
