package com.alaimos.MITHrIL.Data.Pathway.Interface;

import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeSubType;
import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeType;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * This interface represents classes which hold the type and subtype (description) of an edge in a specific pathway
 * @author Salvatore Alaimo, Ph.D.
 * @since 06/12/2015
 * @version 2.0.0.0
 */
public interface EdgeDescriptionInterface extends Cloneable, Serializable {

    /**
     * Gets the edge type
     *
     * @return the type
     */
    EdgeType getType();

    /**
     * Sets the edge type
     *
     * @param type the new type
     * @return this class for a fluent interface
     */
    EdgeDescriptionInterface setType(EdgeType type);

    /**
     * Gets the edge subtype
     *
     * @return the subtype
     */
    EdgeSubType getSubType();

    /**
     * Sets the edge subtype
     *
     * @param subType the subtype
     * @return this class for a fluent interface
     */
    EdgeDescriptionInterface setSubType(EdgeSubType subType);

    /**
     * Make a clone of this object
     *
     * @return a clone
     */
    @SuppressWarnings("unchecked")
    Object clone();

    /**
     * Set the pathway which contains this
     *
     * @param o the container or null if no container is available
     * @return this object for a fluent interface
     */
    EdgeDescriptionInterface setOwner(PathwayInterface o);

    /**
     * Get the pathway which contains this
     *
     * @return the container or null if no container is available
     */
    @Nullable
    PathwayInterface getOwner();

    /**
     * Checks if a pathway contains this
     *
     * @param o the container to check againts
     * @return TRUE iif this object if owned by the specified container
     */
    boolean isOwnedBy(PathwayInterface o);

}
