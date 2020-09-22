package com.alaimos.MITHrIL.Data.Pathway.Impl;

import com.alaimos.MITHrIL.Data.Pathway.Interface.*;
import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeSubType;
import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.1.0.0
 * @since 07/12/2015
 */
public class Edge implements EdgeInterface {

    private static final long                           serialVersionUID  = -6582893354929759738L;
    protected            NodeInterface                  start;
    protected            NodeInterface                  end;
    protected            List<EdgeDescriptionInterface> descriptions      = new ArrayList<>();
    protected            WeightComputationInterface     weightComputation = null;

    public Edge() {
        start = null;
        end = null;
    }

    public Edge(NodeInterface start, NodeInterface end) {
        this.start = start;
        this.end = end;
    }

    public Edge(NodeInterface start, NodeInterface end, EdgeDescriptionInterface description) {
        this(start, end);
        this.setDescription(description);
    }

    public Edge(NodeInterface start, NodeInterface end, List<EdgeDescriptionInterface> descriptions) {
        this(start, end);
        this.setDescriptions(descriptions);
    }

    public Edge(NodeInterface start, NodeInterface end, EdgeType type, EdgeSubType subType, PathwayInterface owner) {
        this(start, end, new EdgeDescription(type, subType, owner));
    }

    public Edge(NodeInterface start, NodeInterface end, String type, String subType, PathwayInterface owner) {
        this(start, end, new EdgeDescription(type, subType, owner));
    }

    @Override
    public NodeInterface getStart() {
        return this.start;
    }

    @Override
    public EdgeInterface setStart(NodeInterface start) {
        this.start = start;
        return this;
    }

    @Override
    public NodeInterface getEnd() {
        return this.end;
    }

    @Override
    public EdgeInterface setEnd(NodeInterface end) {
        this.end = end;
        return this;
    }

    @Override
    public boolean isMultiEdge() {
        return (this.descriptions.size() > 1);
    }

    @Override
    public List<EdgeDescriptionInterface> getDescriptions() {
        return this.descriptions;
    }

    /**
     * Gets the descriptions associated to this edge as a stream
     *
     * @return a stream of descriptions
     */
    @Override
    public Stream<EdgeDescriptionInterface> getDescriptionsStream() {
        return this.descriptions.stream();
    }

    @Override
    public EdgeDescriptionInterface getDescription() {
        return this.descriptions.get(0);
    }

    @Override
    public EdgeInterface addDescription(EdgeDescriptionInterface description) {
        if (!this.descriptions.contains(description)) {
            this.descriptions.add(description);
        }
        return this;
    }

    @Override
    public EdgeInterface addDescription(EdgeType type, EdgeSubType subType, PathwayInterface owner) {
        this.addDescription(new EdgeDescription(type, subType, owner));
        return this;
    }

    @Override
    public EdgeInterface addDescriptions(List<EdgeDescriptionInterface> descriptions) {
        for (EdgeDescriptionInterface d : descriptions) {
            if (!this.descriptions.contains(d)) {
                this.descriptions.add(d);
            }
        }
        return this;
    }

    @Override
    public EdgeInterface clearDescriptions() {
        this.descriptions.clear();
        return this;
    }

    @Override
    public EdgeInterface setDescription(EdgeDescriptionInterface description) {
        return this.clearDescriptions().addDescription(description);
    }

    @Override
    public EdgeInterface setDescription(EdgeType type, EdgeSubType subType, PathwayInterface owner) {
        return this.clearDescriptions().addDescription(type, subType, owner);
    }

    @Override
    public EdgeInterface setDescriptions(List<EdgeDescriptionInterface> description) {
        return this.clearDescriptions().addDescriptions(description);
    }

    @Override
    public EdgeInterface setWeightComputationInterface(WeightComputationInterface weightComputation) {
        this.weightComputation = weightComputation;
        return this;
    }

    @Override
    public WeightComputationInterface getWeightComputation() {
        return this.weightComputation;
    }

    @Override
    public double computeWeight() {
        if (this.weightComputation == null) {
            throw new RuntimeException("Weight computation procedure is not set.");
        }
        if (!isMultiEdge()) {
            return this.weightComputation.weight(this, this.getDescription());
        }
        double weight = 0.0;
        for (EdgeDescriptionInterface d : descriptions) {
            weight += weightComputation.weight(this, d);
        }
        return weight / Math.abs(weight); // Normalizes in the range [-1,1]
    }

    @Override
    public double computeWeight(PathwayInterface p) {
        if (this.weightComputation == null) return Double.NaN;
        List<EdgeDescriptionInterface> descr = getDescriptionsOwnedBy(p);
        if (descr.size() == 0) return 0.0;
        double weight = 0.0;
        for (EdgeDescriptionInterface d : descr) {
            weight += weightComputation.weight(this, d);
        }
        return weight / Math.abs(weight); // Normalizes in the range [-1,1]
        //return descr.stream().mapToDouble(d -> weightComputation.weight(this, d)).sum();
    }

    @Override
    public boolean partialEquals(EdgeInterface e) {
        return e != null && (e == this || start.equals(e.getStart()) && end.equals(e.getEnd()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge)) return false;
        Edge edge = (Edge) o;
        return Objects.equals(start, edge.start) &&
                Objects.equals(end, edge.end) &&
                Objects.equals(descriptions, edge.descriptions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, descriptions);
    }

    @Override
    public boolean isOwnedBy(PathwayInterface o) {
        for (EdgeDescriptionInterface d : descriptions) {
            if (d.isOwnedBy(o)) return true;
        }
        return false;
    }

    /**
     * Clone the object
     *
     * @return my clone
     */
    @SuppressWarnings("unchecked")
    public Object clone() {
        Edge clone;
        try {
            clone = (Edge) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e.getMessage());
        }
        clone.start = (NodeInterface) start.clone();
        clone.end = (NodeInterface) end.clone();
        clone.weightComputation = weightComputation; //this must not be cloned!!
        clone.descriptions = descriptions.stream().map(d -> (EdgeDescriptionInterface) d.clone())
                                         .collect(Collectors.toList());
        return clone;
    }

    @Override
    public List<EdgeDescriptionInterface> getDescriptionsOwnedBy(PathwayInterface o) {
        return descriptions.stream().filter(d -> d.isOwnedBy(o)).collect(Collectors.toList());
    }
}
