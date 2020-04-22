package com.alaimos.MITHrIL.Data.Results;

import com.alaimos.MITHrIL.Data.Pathway.Interface.EdgeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 05/01/2016
 */
public class SignificantPath implements Serializable {

    private static final long serialVersionUID = -7289678298170765798L;

    private List<EdgeInterface> edges;
    private List<Double>        nodesPerturbation;
    private List<Double>        nodesPValues;
    private double              pathPValue;

    public SignificantPath(List<EdgeInterface> edges, List<Double> nodesPerturbation, List<Double> nodesPValues,
                           double pathPValue) {
        this.edges = edges;
        this.nodesPerturbation = nodesPerturbation;
        this.nodesPValues = nodesPValues;
        this.pathPValue = pathPValue;
    }

    public List<EdgeInterface> getEdges() {
        return edges;
    }

    public List<NodeInterface> getNodes() {
        List<NodeInterface> nodes = edges.stream().map(EdgeInterface::getStart).collect(Collectors.toList());
        nodes.add(edges.get(edges.size() - 1).getEnd());
        return nodes;
    }

    public List<Double> getNodesPerturbation() {
        return nodesPerturbation;
    }

    public List<Double> getNodesPValues() {
        return nodesPValues;
    }

    public double getPathPValue() {
        return pathPValue;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < edges.size(); i++) {
            EdgeInterface e = edges.get(i);
            if (i == 0) s.append(e.getStart().getName());
            s.append(" -- ").append(e.getDescription().getSubType()).append(" --> ").append(e.getEnd().getName());
        }
        s.append(" [").append(pathPValue).append("]");
        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SignificantPath)) return false;
        SignificantPath that = (SignificantPath) o;
        return Objects.equals(edges, that.edges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(edges);
    }

    /**
     * Checks if a path is contained in another path
     *
     * @param p the second path
     * @return true iif this path is contained in another path
     */
    public boolean isContained(SignificantPath p) {
        if (p.edges.size() < edges.size()) return false;
        if (this.equals(p)) return true;
        boolean contained = true;
        for (EdgeInterface e : edges) {
            contained = contained && p.edges.contains(e);
        }
        return contained;
    }
}
