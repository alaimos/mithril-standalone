package com.alaimos.MITHrIL.Data.Pathway.Interface;

import com.alaimos.Commons.Utils.ActionResult;
import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeSubType;
import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeType;
import com.alaimos.MITHrIL.Data.Pathway.Type.NodeType;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 06/12/2015
 */
public interface GraphInterface
        extends Cloneable, Iterable<NodeInterface>, WeightComputationAwareInterface<GraphInterface>, Serializable {

    boolean isEmpty();

    GraphInterface addNode(NodeInterface n);

    NodeInterface addNode(String id, String name, String type);

    NodeInterface addNode(String id, String name, NodeType type);

    NodeInterface getNode(String id);

    NodeInterface findNode(String needle);

    boolean removeNode(String id);

    boolean removeNode(NodeInterface n);

    boolean hasNode(NodeInterface n);

    boolean hasNode(String id);

    List<String> getEndpoints();

    GraphInterface setEndpoints(List<String> endpoints);

    int inDegree(NodeInterface n);

    int outDegree(NodeInterface n);

    Stream<NodeInterface> ingoingNodesStream(NodeInterface n);

    default List<NodeInterface> ingoingNodes(NodeInterface n) {
        Stream<NodeInterface> s = ingoingNodesStream(n);
        return ((s == null) ? null : s.collect(Collectors.toList()));
    }

    Stream<NodeInterface> outgoingNodesStream(NodeInterface n);

    default List<NodeInterface> outgoingNodes(NodeInterface n) {
        Stream<NodeInterface> s = outgoingNodesStream(n);
        return ((s == null) ? null : s.collect(Collectors.toList()));
    }

    default Stream<NodeInterface> upstreamNodesStream(NodeInterface n) {
        return upstreamNodes(n).stream();
    }

    int countUpstreamNodes(NodeInterface n);

    List<NodeInterface> upstreamNodes(NodeInterface n);

    default Stream<NodeInterface> downstreamNodesStream(NodeInterface n) {
        return downstreamNodes(n).stream();
    }

    int countDownstreamNodes(NodeInterface n);

    List<NodeInterface> downstreamNodes(NodeInterface n);

    Map<String, NodeInterface> getNodes();

    Stream<NodeInterface> getNodesStream();

    GraphInterface addEdge(EdgeInterface e);

    EdgeInterface addEdge(NodeInterface start, NodeInterface end, List<EdgeDescriptionInterface> descriptions);

    EdgeInterface addEdge(NodeInterface start, NodeInterface end, EdgeType type, EdgeSubType subType);

    EdgeInterface addEdge(String startId, String endId, String type, String subType);

    EdgeInterface getEdge(NodeInterface start, NodeInterface end);

    EdgeInterface getEdge(String startId, String endId);

    boolean hasEdge(EdgeInterface e);

    boolean hasEdge(NodeInterface start, NodeInterface end);

    boolean hasEdge(String startId, String endId);

    Map<String, ? extends Map<String, EdgeInterface>> getEdges();

    Stream<EdgeInterface> getEdgesStream();

    int countNodes();

    int countEdges();

    void runUpstream(NodeInterface currentNode, Function<NodeInterface, ActionResult> action);

    default void traverseUpstream(Collection<NodeInterface> results, NodeInterface currentNode) {
        traverseUpstream(results, currentNode, true);
    }

    void traverseUpstream(Collection<NodeInterface> results, NodeInterface currentNode, boolean markTraversal);

    void runDownstream(NodeInterface currentNode, Function<NodeInterface, ActionResult> action);

    default void traverseDownstream(Collection<NodeInterface> results, NodeInterface currentNode) {
        traverseDownstream(results, currentNode, true);
    }

    void traverseDownstream(Collection<NodeInterface> results, NodeInterface currentNode, boolean markTraversal);

    Object clone();

    GraphInterface setOwner(PathwayInterface o);

    PathwayInterface getOwner();

    boolean isOwnedBy(PathwayInterface o);

    default List<NodeInterface> completePathway(@NotNull Map<String, ?> expressionMap) {
        var result = new ArrayList<NodeInterface>();
        var endpoints = this.getEndpoints();
        for (var e : expressionMap.entrySet()) {
            var s = e.getKey();
            if (!this.hasNode(s)) {
                result.add(this.addNode(s, s, NodeType.valueOf("GENE")));
                endpoints.add(s);
            }
        }
        return result;
    }

}