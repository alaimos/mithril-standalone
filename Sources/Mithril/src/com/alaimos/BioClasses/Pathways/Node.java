package com.alaimos.BioClasses.Pathways;

import java.util.List;

public interface Node extends Comparable<Node>,Cloneable {

    public String getEntryId();

    public Node setEntryId(String entryId);

    public String getName();

    public Node setName(String name);

    public Pathway.NodeType getType();

    public Node setType(Pathway.NodeType type);

    public int getIndex();

    public List<Node> parents();

    public List<Node> children();

    public List<Node> upstream();

    public List<Node> downstream();

    @SuppressWarnings("unchecked")
    public Object clone();

}
