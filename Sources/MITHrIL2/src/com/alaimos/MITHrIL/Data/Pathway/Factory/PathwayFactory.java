package com.alaimos.MITHrIL.Data.Pathway.Factory;

import com.alaimos.MITHrIL.Data.Pathway.Impl.*;
import com.alaimos.MITHrIL.Data.Pathway.Interface.*;
import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeSubType;
import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeType;
import com.alaimos.MITHrIL.Data.Pathway.Type.NodeType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 11/12/2015
 */
public class PathwayFactory implements PathwayFactoryInterface {

    private static final long           serialVersionUID = 7711779320394058926L;
    private static       PathwayFactory instance         = new PathwayFactory();

    public static PathwayFactory getInstance() {
        return instance;
    }

    private PathwayFactory() {
    }

    @Override
    public NodeInterface getNode() {
        return new Node();
    }

    @Override
    public NodeInterface getNode(String id, String name, NodeType type) {
        return new Node(id, name, type);
    }

    @Override
    public NodeInterface getNode(String id, String name, String type) {
        return new Node(id, name, type);
    }

    @Override
    public EdgeDescriptionInterface getEdgeDescription() {
        return new EdgeDescription();
    }

    @Override
    public EdgeDescriptionInterface getEdgeDescription(EdgeType type, EdgeSubType subType) {
        return new EdgeDescription(type, subType, null);
    }

    @Override
    public EdgeDescriptionInterface getEdgeDescription(EdgeType type, EdgeSubType subType, PathwayInterface owner) {
        return new EdgeDescription(type, subType, owner);
    }

    @Override
    public EdgeDescriptionInterface getEdgeDescription(String type, String subType) {
        return new EdgeDescription(type, subType, null);
    }

    @Override
    public EdgeDescriptionInterface getEdgeDescription(String type, String subType, PathwayInterface owner) {
        return new EdgeDescription(type, subType, owner);
    }

    @Override
    public EdgeInterface getEdge() {
        return new Edge();
    }

    @Override
    public EdgeInterface getEdge(NodeInterface start, NodeInterface end) {
        return new Edge(start, end);
    }

    @Override
    public EdgeInterface getEdge(NodeInterface start, NodeInterface end, EdgeDescriptionInterface description) {
        return new Edge(start, end, description);
    }

    @Override
    public EdgeInterface getEdge(NodeInterface start, NodeInterface end, List<EdgeDescriptionInterface> descriptions) {
        return new Edge(start, end, descriptions);
    }

    @Override
    public EdgeInterface getEdge(NodeInterface start, NodeInterface end, EdgeType type, EdgeSubType subType) {
        return new Edge(start, end, type, subType, null);
    }

    @Override
    public EdgeInterface getEdge(NodeInterface start, NodeInterface end, EdgeType type, EdgeSubType subType,
                                 PathwayInterface owner) {
        return new Edge(start, end, type, subType, owner);
    }

    @Override
    public EdgeInterface getEdge(NodeInterface start, NodeInterface end, String type, String subType,
                                 PathwayInterface owner) {
        return new Edge(start, end, type, subType, owner);
    }

    @Override
    public EdgeInterface getEdge(NodeInterface start, NodeInterface end, String type, String subType) {
        return new Edge(start, end, type, subType, null);
    }

    @Override
    public GraphInterface getGraph() {
        return new Graph();
    }

    @Override
    public PathwayInterface getPathway() {
        return new Pathway();
    }

    @Override
    public PathwayInterface getPathway(String id, String name) {
        return new Pathway(id, name, null);
    }

    @Override
    public PathwayInterface getPathway(String id, String name, GraphInterface graph) {
        return new Pathway(id, name, graph);
    }

    @Override
    public PathwayInterface getPathway(String id, String name, ArrayList<String> categories) {
        return new Pathway(id, name, null, categories);
    }

    @Override
    public PathwayInterface getPathway(String id, String name, GraphInterface graph, ArrayList<String> categories) {
        return new Pathway(id, name, graph, categories);
    }

    @Override
    public PathwayInterface getPathway(String id, String name, String categories) {
        return new Pathway(id, name, null, categories);
    }

    @Override
    public PathwayInterface getPathway(String id, String name, GraphInterface graph, String categories) {
        return new Pathway(id, name, graph, categories);
    }

    @Override
    public RepositoryInterface getRepository() {
        return new Repository();
    }
}
