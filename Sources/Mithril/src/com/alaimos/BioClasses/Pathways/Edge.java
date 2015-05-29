package com.alaimos.BioClasses.Pathways;

public interface Edge extends Cloneable {

    public Node getStart();

    public Edge setStart(Node start);

    public Node getEnd();

    public Edge setEnd(Node end);

    public Pathway.EdgeType getType();

    public Edge setType(Pathway.EdgeType type);

    public Pathway.EdgeSubType getSubType();

    public Edge setSubType(Pathway.EdgeSubType subType);

    public Edge addSubType(Pathway.EdgeSubType subType);

    public int getWeight();

    public boolean partialEquals(Edge e);

    @SuppressWarnings("unchecked")
    public Object clone();

}
