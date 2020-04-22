package com.alaimos.MITHrIL.Data.Pathway.Interface;

import com.alaimos.MITHrIL.Data.Pathway.Type.NodeType;

import java.io.Serializable;
import java.util.List;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @since 06/12/2015
 * @version 2.0.0.0
 */
public interface NodeInterface extends Comparable<NodeInterface>, Cloneable, Serializable {

    String getId();

    NodeInterface setId(String id);

    String getName();

    NodeInterface setName(String name);

    List<String> getAliases();

    NodeInterface addAliases(List<String> aliases);

    NodeInterface clearAliases();

    NodeInterface setAliases(List<String> aliases);

    NodeType getType();

    NodeInterface setType(NodeType type);

    boolean contains(String haystack);

    /*List<NodeInterface> ingoingEdges();

    List<NodeInterface> outgoingEdges();

    List<NodeInterface> upstreamEdges();

    List<NodeInterface> downstreamEdges();*/

    @SuppressWarnings("unchecked")
    Object clone();

}
