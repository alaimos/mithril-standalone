package com.alaimos.MITHrIL.Data.Pathway.Impl;

import com.alaimos.MITHrIL.Data.Pathway.Interface.EdgeDescriptionInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.PathwayInterface;
import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeSubType;
import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Objects;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.1.0.0
 * @since 07/12/2015
 */
public class EdgeDescription implements EdgeDescriptionInterface {

    private static final long             serialVersionUID = 7519228777154653945L;
    protected            EdgeType         type;
    protected            String           typeString;
    protected            EdgeSubType      subType;
    protected            String           subTypeString;
    protected            PathwayInterface owner            = null;


    /**
     * Class constructor without parameters
     */
    public EdgeDescription() {
        type = null;
        subType = null;
    }

    /**
     * Create edge description from EdgeType and EdgeSubType objects
     *
     * @param type    Edge type
     * @param subType Edge subType
     */
    public EdgeDescription(EdgeType type, EdgeSubType subType, PathwayInterface owner) {
        this.type = type;
        this.subType = subType;
        this.owner = owner;
    }

    /**
     * Create edge description from string
     *
     * @param type    Edge type
     * @param subType Edge subType
     */
    public EdgeDescription(String type, String subType, PathwayInterface owner) {
        this(EdgeType.fromString(type), EdgeSubType.fromString(subType), owner);
    }

    /**
     * Get edge type
     *
     * @return the type
     */
    @Override
    public EdgeType getType() {
        return type;
    }

    /**
     * Set new edge type
     *
     * @param type the new edge type
     * @return this object
     */
    @Override
    public EdgeDescriptionInterface setType(EdgeType type) {
        this.type = type;
        return this;
    }

    /**
     * Get edge subtype
     *
     * @return the subtype
     */
    @Override
    public EdgeSubType getSubType() {
        return subType;
    }

    /**
     * Set new edge subtype
     *
     * @param subType the new subtype
     * @return this object
     */
    @Override
    public EdgeDescriptionInterface setSubType(EdgeSubType subType) {
        this.subType = subType;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EdgeDescription)) return false;
        EdgeDescription that = (EdgeDescription) o;
        return type == that.type &&
                subType == that.subType &&
                Objects.equals(owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, subType, owner);
    }

    /**
     * Clone the object
     *
     * @return my clone
     */
    @SuppressWarnings("unchecked")
    public Object clone() {
        EdgeDescription clone;
        try {
            clone = (EdgeDescription) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e.getMessage());
        }
        clone.owner = this.owner; // Owner must not be cloned!!
        return clone;
    }

    @Override
    public EdgeDescriptionInterface setOwner(PathwayInterface o) {
        this.owner = o;
        return this;
    }

    @Override
    public PathwayInterface getOwner() {
        return this.owner;
    }

    @Override
    public boolean isOwnedBy(PathwayInterface o) {
        return Objects.equals(owner, o);
    }

    private void preSerialize() {
        typeString = type.toString();
        subTypeString = subType.toString();
        type = null;
        subType = null;
    }

    private void postSerialize() {
        type = EdgeType.fromString(typeString);
        subType = EdgeSubType.fromString(subTypeString);
        typeString = null;
        subTypeString = null;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        preSerialize();
        stream.defaultWriteObject();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        postSerialize();
    }
}
