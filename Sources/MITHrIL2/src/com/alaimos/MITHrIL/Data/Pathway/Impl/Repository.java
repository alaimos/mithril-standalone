package com.alaimos.MITHrIL.Data.Pathway.Impl;

import com.alaimos.Commons.Utils.Pair;
import com.alaimos.MITHrIL.Algorithm.BatchDecoyBuilder;
import com.alaimos.MITHrIL.Data.Pathway.Interface.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.1.1.0
 * @since 08/12/2015
 */
public class Repository implements RepositoryInterface {

    private static final long serialVersionUID = -4870864603957397982L;
    protected HashMap<String, List<String>> pathwaysByCategory = new HashMap<>();
    protected HashMap<String, PathwayInterface> pathways = new HashMap<>();
    protected WeightComputationInterface weightComputation = null;
    protected Map<String, List<Pair<String, String>>> virtualPathwaysEdges = new HashMap<>();
    protected Map<String, String> virtualPathwaysName = new HashMap<>();
    protected Map<String, PathwayInterface> virtualPathwaysSource = new HashMap<>();
    protected Map<String, List<EdgeInterface>> virtualPathwaysEdgesObject = new HashMap<>();
    protected Map<String, List<NodeInterface>> virtualPathwaysNodesObject = new HashMap<>();

    public Repository() {
    }

    @Override
    public RepositoryInterface addDecoys(long rngSeed) {
        Random r = new Random(rngSeed);
        HashMap<String, NodeInterface> idToGenes = new HashMap<>();
        ArrayList<String> allGenes = new ArrayList<>();
        this.pathways.forEach((s, p) -> {
            if (p.hasGraph()) {
                p.getGraph().forEach(n -> {
                    if (!idToGenes.containsKey(n.getId())) {
                        idToGenes.put(n.getId(), n);
                        allGenes.add(n.getId());
                    }
                });
            }
        });
        BatchDecoyBuilder builder = new BatchDecoyBuilder();
        builder.init().setParameter("repository", this).setParameter("allNodes", allGenes)
               .setParameter("idToNodes", idToGenes).setParameter("random", r).run();
        builder.getOutput().forEach((i, p) -> {
            this.add(p);
        });
        return this;
    }

    @Override
    public boolean containsPathway(PathwayInterface p) {
        return pathways.containsKey(p.getId());
    }

    @Override
    public boolean containsPathway(String p) {
        return pathways.containsKey(p);
    }

    @Override
    @Nullable
    public PathwayInterface getPathwayById(String pathwayId) {
        return pathways.get(pathwayId);
    }

    @Override
    public List<PathwayInterface> getPathwaysByCategory(String category) {
        List<String> l = pathwaysByCategory.get(category);
        if (l == null) return null;
        return l.stream().map(this::getPathwayById).collect(Collectors.toList());
    }

    @Override
    public List<PathwayInterface> getPathwaysByCategory(List<String> category) {
        HashSet<PathwayInterface> ps = new HashSet<>();
        category.stream().map(this::getPathwaysByCategory).forEachOrdered(ps::addAll);
        return new ArrayList<>(ps);
    }

    @Override
    public List<String> getPathwayIdsByCategory(String category) {
        return pathwaysByCategory.get(category);
    }

    @Override
    public List<String> getPathwayIdsByCategory(List<String> category) {
        HashSet<String> ps = new HashSet<>();
        category.stream().map(this::getPathwayIdsByCategory).forEachOrdered(ps::addAll);
        return new ArrayList<>(ps);
    }

    @Override
    public List<String> getPathwaysByNodeId(String nodeId) {
        return pathways.values().stream().filter(pathway -> pathway.hasGraph() && pathway.getGraph().hasNode(nodeId))
                       .map(PathwayInterface::getId).collect(Collectors.toList());
    }

    @Override
    public List<String> getCategories() {
        return new ArrayList<>(pathwaysByCategory.keySet());
    }

    @Override
    public PathwayInterface getDefaultVirtualSource() {
        return null;
    }

    @Override
    public RepositoryInterface addVirtualPathway(PathwayInterface from, String id, List<Pair<String, String>> edges) {
        virtualPathwaysEdges.put(id, edges);
        virtualPathwaysSource.put(id, from);
        return this;
    }

    @Override
    public Set<String> getVirtualPathways() {
        return virtualPathwaysEdges.keySet();
    }

    @Override
    public boolean hasVirtualPathway(String virtualPathwayId) {
        return virtualPathwaysEdges.containsKey(virtualPathwayId);
    }

    @Override
    public RepositoryInterface setNameOfVirtualPathway(String virtualPathwayId, String name) {
        if (this.hasVirtualPathway(virtualPathwayId)) {
            virtualPathwaysName.put(virtualPathwayId, name);
        }
        return this;
    }

    @Override
    public String getNameOfVirtualPathway(String virtualPathwayId) {
        return virtualPathwaysName.getOrDefault(virtualPathwayId, virtualPathwayId);
    }

    @Override
    public List<NodeInterface> getNodesOfVirtualPathway(String virtualPathwayId) {
        if (!virtualPathwaysNodesObject.containsKey(virtualPathwayId)) {
            List<Pair<String, String>> edges = virtualPathwaysEdges.get(virtualPathwayId);
            if (edges == null) return null;
            PathwayInterface sourcePathway = virtualPathwaysSource.getOrDefault(virtualPathwayId, getDefaultVirtualSource());
            if (sourcePathway == null) return null;
            GraphInterface sourceGraph = sourcePathway.getGraph();
            HashSet<String> nodes = new HashSet<>();
            edges.forEach(p -> {
                nodes.add(p.getFirst());
                nodes.add(p.getSecond());
            });
            virtualPathwaysNodesObject.put(virtualPathwayId, nodes.stream().map(sourceGraph::getNode).collect(Collectors.toList()));
        }
        return virtualPathwaysNodesObject.get(virtualPathwayId);
    }

    @Override
    public List<EdgeInterface> getEdgesOfVirtualPathway(String virtualPathwayId) {
        if (!virtualPathwaysEdgesObject.containsKey(virtualPathwayId)) {
            List<Pair<String, String>> edges = virtualPathwaysEdges.get(virtualPathwayId);
            if (edges == null) return null;
            PathwayInterface sourcePathway = virtualPathwaysSource.getOrDefault(virtualPathwayId, getDefaultVirtualSource());
            if (sourcePathway == null) return null;
            GraphInterface sourceGraph = sourcePathway.getGraph();
            virtualPathwaysEdgesObject
                    .put(virtualPathwayId, edges.stream().map(p -> sourceGraph.getEdge(p.getFirst(), p.getSecond())).collect(Collectors.toList()));
        }
        return virtualPathwaysEdgesObject.get(virtualPathwayId);
    }

    @Override
    public List<Pair<String, String>> getEdgesByVirtualPathway(String virtualPathwayId) {
        return virtualPathwaysEdges.get(virtualPathwayId);
    }

    @Override
    public PathwayInterface getSourceOfVirtualPathway(String virtualPathwayId) {
        return virtualPathwaysSource.get(virtualPathwayId);
    }

    @Override
    public List<PathwayInterface> getPathways() {
        return new ArrayList<>(pathways.values());
    }

    @Override
    public boolean removeNode(String id) {
        boolean res = true;
        for (var p : this) {
            if (p.hasGraph()) {
                var g = p.getGraph();
                if (g.hasNode(id)) {
                    res = res && g.removeNode(id);
                }
            }
        }
        for (var entry : this.virtualPathwaysEdges.entrySet()) {
            var pId = entry.getKey();
            var tmp = entry.getValue().stream().filter(e -> !e.getFirst().equals(id) && !e.getSecond().equals(id)).collect(Collectors.toList());
            if (tmp.size() < entry.getValue().size()) {
                this.virtualPathwaysNodesObject.remove(pId);
                this.virtualPathwaysEdgesObject.remove(pId);
                this.virtualPathwaysEdges.put(pId, tmp);
            }
        }
        return res;
    }

    @Override
    public boolean removeNode(NodeInterface n) {
        return remove(n.getId());
    }

    @Override
    public RepositoryInterface setDefaultWeightComputation(WeightComputationInterface defaultWeightComputation,
                                                           boolean changeAll) {
        WeightComputationInterface old = this.weightComputation;
        this.weightComputation = defaultWeightComputation;
        if (this.weightComputation != old && changeAll) {
            pathways.forEach((s, p) -> p.setDefaultWeightComputation(defaultWeightComputation));
        }
        return this;
    }

    @Override
    public WeightComputationInterface getDefaultWeightComputation() {
        return weightComputation;
    }

    @Override
    public int size() {
        return pathways.size();
    }

    @Override
    public boolean isEmpty() {
        return pathways.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return o instanceof PathwayInterface && pathways.containsValue((PathwayInterface) o);
    }

    @NotNull
    @Override
    public Iterator<PathwayInterface> iterator() {
        return pathways.values().iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return pathways.values().toArray();
    }

    @NotNull
    @Override
    public <T> T[] toArray(T[] a) {
        return pathways.values().toArray(a);
    }

    @Override
    public boolean add(PathwayInterface pathwayInterface) {
        if (pathways.containsKey(pathwayInterface.getId())) return false;
        pathways.put(pathwayInterface.getId(), pathwayInterface);
        pathwayInterface.getCategories().forEach(s -> {
            if (!pathwaysByCategory.containsKey(s)) {
                pathwaysByCategory.put(s, new ArrayList<>());
            }
            pathwaysByCategory.get(s).add(pathwayInterface.getId());
        });
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (!(o instanceof PathwayInterface)) return false;
        PathwayInterface p = (PathwayInterface) o;
        p.getCategories().forEach(s -> {
            this.pathwaysByCategory.get(s).remove(p.getId());
        });
        return (pathways.remove(p.getId()) != null);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return pathways.values().containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends PathwayInterface> c) {
        c.forEach(this::add);
        return true;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        throw new RuntimeException("Unsupported operation");
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        throw new RuntimeException("Unsupported operation");
    }

    @Override
    public void clear() {
        pathways.clear();
    }

    /**
     * Clone the object
     *
     * @return my clone
     */
    @SuppressWarnings("unchecked")
    public Object clone() {
        Repository clone;
        try {
            clone = (Repository) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e.getMessage());
        }
        clone.pathwaysByCategory = new HashMap<>();
        clone.pathways = new HashMap<>();
        pathways.forEach((s, pathwayInterface) -> {
            clone.add((PathwayInterface) pathwayInterface.clone());
        });
        return clone;
    }
}
