package com.alaimos.MITHrIL.Data.Pathway.Interface;

import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeSubType;
import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeType;
import com.alaimos.MITHrIL.Data.Pathway.Type.NodeType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface of Pathway Factory
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 11/12/2015
 */
public interface PathwayFactoryInterface extends Serializable {

    NodeInterface getNode();

    NodeInterface getNode(String id, String name, NodeType type);

    NodeInterface getNode(String id, String name, String type);

    EdgeDescriptionInterface getEdgeDescription();

    EdgeDescriptionInterface getEdgeDescription(EdgeType type, EdgeSubType subType);

    EdgeDescriptionInterface getEdgeDescription(EdgeType type, EdgeSubType subType, PathwayInterface owner);

    EdgeDescriptionInterface getEdgeDescription(String type, String subType);

    EdgeDescriptionInterface getEdgeDescription(String type, String subType, PathwayInterface owner);

    EdgeInterface getEdge();

    EdgeInterface getEdge(NodeInterface start, NodeInterface end);

    EdgeInterface getEdge(NodeInterface start, NodeInterface end, EdgeDescriptionInterface description);

    EdgeInterface getEdge(NodeInterface start, NodeInterface end, List<EdgeDescriptionInterface> descriptions);

    EdgeInterface getEdge(NodeInterface start, NodeInterface end, EdgeType type, EdgeSubType subType);

    EdgeInterface getEdge(NodeInterface start, NodeInterface end, EdgeType type, EdgeSubType subType,
                          PathwayInterface owner);

    EdgeInterface getEdge(NodeInterface start, NodeInterface end, String type, String subType, PathwayInterface owner);


    EdgeInterface getEdge(NodeInterface start, NodeInterface end, String type, String subType);

    GraphInterface getGraph();

    PathwayInterface getPathway();

    PathwayInterface getPathway(String id, String name);

    PathwayInterface getPathway(String id, String name, GraphInterface graph);

    PathwayInterface getPathway(String id, String name, ArrayList<String> categories);

    PathwayInterface getPathway(String id, String name, GraphInterface graph, ArrayList<String> categories);

    PathwayInterface getPathway(String id, String name, String categories);

    PathwayInterface getPathway(String id, String name, GraphInterface graph, String categories);

    RepositoryInterface getRepository();

}
