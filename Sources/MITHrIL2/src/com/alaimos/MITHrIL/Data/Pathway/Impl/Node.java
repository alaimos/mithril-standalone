package com.alaimos.MITHrIL.Data.Pathway.Impl;

import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Type.NodeType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.1.0.0
 * @since 07/12/2015
 */
public class Node implements NodeInterface {

    private static final long              serialVersionUID = 642755128746768579L;
    protected            String            id;
    protected            String            name;
    protected            NodeType          type;
    protected            String            typeString;
    protected            ArrayList<String> aliases          = new ArrayList<>();

    public Node() {
        id = null;
        name = null;
        type = null;
    }

    public Node(String id, String name, NodeType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public Node(String id, String name, String type) {
        this(id, name, NodeType.fromString(type));
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public NodeInterface setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public NodeInterface setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public NodeInterface addAliases(List<String> aliases) {
        this.aliases.addAll(aliases);
        return this;
    }

    @Override
    public NodeInterface clearAliases() {
        this.aliases.clear();
        return this;
    }

    @Override
    public NodeInterface setAliases(List<String> aliases) {
        return this.clearAliases().addAliases(aliases);
    }

    @Override
    public NodeType getType() {
        return this.type;
    }

    @Override
    public NodeInterface setType(NodeType type) {
        this.type = type;
        return this;
    }

    @Override
    public boolean contains(String haystack) {
        return id.equals(haystack) || name.equals(haystack) || aliases.contains(haystack);
    }

    @Override
    public int compareTo(@NotNull NodeInterface o) {
        return id.compareTo(o.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node node = (Node) o;
        return Objects.equals(id, node.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Clone the object
     *
     * @return my clone
     */
    @SuppressWarnings("unchecked")
    public Object clone() {
        Node clone;
        try {
            clone = (Node) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e.getMessage());
        }
        clone.aliases = new ArrayList<>();
        clone.addAliases(aliases);
        return clone;
    }

    private void preSerialize() {
        typeString = type.toString();
        type = null;
    }

    private void postSerialize() {
        type = NodeType.fromString(typeString);
        typeString = null;
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
