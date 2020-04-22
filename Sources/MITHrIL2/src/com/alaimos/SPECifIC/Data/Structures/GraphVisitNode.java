package com.alaimos.SPECifIC.Data.Structures;

import com.alaimos.MITHrIL.Data.Pathway.Interface.EdgeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;

import java.util.Objects;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 16/12/2016
 */
public class GraphVisitNode {

    private NodeInterface node;

    private double pValue;

    private double perturbation;

    private EdgeInterface edge;

    public GraphVisitNode(NodeInterface node, double pValue, double perturbation,
                          EdgeInterface edge) {
        this.node = node;
        this.pValue = pValue;
        this.perturbation = perturbation;
        this.edge = edge;
    }

    public GraphVisitNode(NodeInterface node, double pValue, double perturbation) {
        this.node = node;
        this.pValue = pValue;
        this.perturbation = perturbation;
        this.edge = null;
    }

    public NodeInterface getNode() {
        return node;
    }

    public GraphVisitNode setNode(NodeInterface node) {
        this.node = node;
        return this;
    }

    public double getPValue() {
        return pValue;
    }

    public GraphVisitNode setPValue(double pValue) {
        this.pValue = pValue;
        return this;
    }

    public double getPerturbation() {
        return perturbation;
    }

    public GraphVisitNode setPerturbation(double perturbation) {
        this.perturbation = perturbation;
        return this;
    }

    public EdgeInterface getEdge() {
        return edge;
    }

    public GraphVisitNode setEdge(EdgeInterface edge) {
        this.edge = edge;
        return this;
    }

    @Override
    public String toString() {
        return node.getId() + " (pValue=" + pValue + ", pert=" + perturbation + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GraphVisitNode)) return false;
        GraphVisitNode that = (GraphVisitNode) o;
        return Objects.equals(node, that.node);
    }

    @Override
    public int hashCode() {
        return Objects.hash(node);
    }
}
