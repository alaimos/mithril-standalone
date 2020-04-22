package com.alaimos.MITHrIL.Data.Pathway.Interface;

import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeSubType;
import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeType;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;

/**
 * This interface represents a class which holds a DIRECTED multi-edge in a graph.
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 06/12/2015
 */
public interface EdgeInterface extends Cloneable, Serializable {

    /**
     * Gets the start node of this edge
     *
     * @return the start node
     */
    NodeInterface getStart();

    /**
     * Sets the start node of this edge
     *
     * @param start the new start node
     * @return this object for a fluent interface
     */
    EdgeInterface setStart(NodeInterface start);

    /**
     * Sets the end node of this edge
     *
     * @return the end node
     */
    NodeInterface getEnd();

    /**
     * Sets the end node of this edge
     *
     * @param end the end node
     * @return this object for a fluent interface
     */
    EdgeInterface setEnd(NodeInterface end);

    /**
     * Is this edge really a multi-edge? This means that the edge contains more than one description
     *
     * @return TRUE if this is a multi-edge
     */
    boolean isMultiEdge();

    /**
     * Gets the list of descriptions associated to this edge
     *
     * @return the list of descriptions
     */
    List<EdgeDescriptionInterface> getDescriptions();

    /**
     * Gets the descriptions associated to this edge as a stream
     *
     * @return a stream of descriptions
     */
    Stream<EdgeDescriptionInterface> getDescriptionsStream();

    /**
     * This method returns the first description of an edge. Useful if isMultiEdge == false
     *
     * @return the first description
     */
    EdgeDescriptionInterface getDescription();

    /**
     * Adds a description to this edge
     *
     * @param description the description
     * @return this object for a fluent interface
     */
    EdgeInterface addDescription(EdgeDescriptionInterface description);

    /**
     * Adds a description to this edge
     *
     * @param type    the type
     * @param subType the subtype
     * @param owner   the container if available or null
     * @return this object for a fluent interface
     */
    EdgeInterface addDescription(EdgeType type, EdgeSubType subType, PathwayInterface owner);

    /**
     * Adds a list of descriptions to this edge
     *
     * @param descriptions the list of descriptions
     * @return this object for a fluent interface
     */
    EdgeInterface addDescriptions(List<EdgeDescriptionInterface> descriptions);

    /**
     * Empties the list of descriptions
     *
     * @return this object for a fluent interface
     */
    EdgeInterface clearDescriptions();

    /**
     * Replaces the current list of descriptions of this node with a single description
     *
     * @param description the new description
     * @return this object for a fluent interface
     */
    EdgeInterface setDescription(EdgeDescriptionInterface description);

    /**
     * Replaces the current list of descriptions of this node with a single description
     *
     * @param type    the type
     * @param subType the subtype
     * @param owner   the container
     * @return this object for a fluent interface
     */
    EdgeInterface setDescription(EdgeType type, EdgeSubType subType, PathwayInterface owner);

    /**
     * Replaces the current list of descriptions of this node with a new list
     *
     * @param description a new list of descriptions
     * @return this object for a fluent interface
     */
    EdgeInterface setDescriptions(List<EdgeDescriptionInterface> description);

    /**
     * Sets the object which will compute a weight for this edge
     *
     * @param weightComputation the object
     * @return this object for a fluent interface
     */
    EdgeInterface setWeightComputationInterface(WeightComputationInterface weightComputation);

    /**
     * Gets the object which will compute a weight for this edge
     *
     * @return the object
     */
    WeightComputationInterface getWeightComputation();

    /**
     * Computes a weight for this edge
     *
     * @return the weight
     */
    double computeWeight();

    /**
     * Computes a weight for this edge in a pathway
     *
     * @param p the pathway
     * @return the weight
     */
    double computeWeight(PathwayInterface p);

    /**
     * Checks if this edge has the same ends of another one
     *
     * @param e the other edge
     * @return true if the two edges have the same ends
     */
    boolean partialEquals(EdgeInterface e);

    /**
     * Clone this object
     *
     * @return the clone
     */
    @SuppressWarnings("unchecked")
    Object clone();

    /**
     * Get all descriptions contained in a specific pathway
     *
     * @param o the pathway
     * @return this object
     */
    List<EdgeDescriptionInterface> getDescriptionsOwnedBy(PathwayInterface o);

    /**
     * Checks if at least one description is contained by a specific pathway
     *
     * @param o the pathway
     * @return true if the condition holds
     */
    boolean isOwnedBy(PathwayInterface o);

}
