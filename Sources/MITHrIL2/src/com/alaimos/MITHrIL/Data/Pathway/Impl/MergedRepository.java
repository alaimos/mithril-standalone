package com.alaimos.MITHrIL.Data.Pathway.Impl;

import com.alaimos.Commons.Utils.Pair;
import com.alaimos.MITHrIL.Data.Pathway.Interface.EdgeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.PathwayInterface;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * This repository holds a single pathway which contains all the output from the merger of the pathways contained in
 * a repository
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.1.0.0
 * @since 08/12/2015
 */
public class MergedRepository extends Repository {

    private static final long serialVersionUID = -6720027108864697886L;
    private static final String REMAINING_PATHWAY_ID = "remaining";
    private static final String REMAINING_PATHWAY_NAME = "All remaining nodes";
    private PathwayInterface mergedPathway;

    public MergedRepository(PathwayInterface mergedPathway, @NotNull Map<String, List<Pair<String, String>>> pathwaysToEdges) {
        this.mergedPathway = mergedPathway;
        super.add(mergedPathway);
        for (Map.Entry<String, List<Pair<String, String>>> e : pathwaysToEdges.entrySet()) {
            this.addVirtualPathway(e.getKey(), e.getValue());
        }
    }

    public PathwayInterface getPathway() {
        return mergedPathway;
    }

    @Override
    public boolean add(@NotNull PathwayInterface pathwayInterface) {
        throw new UnsupportedOperationException("This repository is read only.");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("This repository is read only.");
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends PathwayInterface> c) {
        throw new UnsupportedOperationException("This repository is read only.");
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException("This repository is read only.");
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException("This repository is read only.");
    }

    @Override
    public PathwayInterface getDefaultVirtualSource() {
        return getPathway();
    }

    public Map<String, List<Pair<String, String>>> getPathwaysToEdgesIndex() {
        return this.virtualPathwaysEdges;
    }

    public List<NodeInterface> getNodesByPathway(String pathwayId) {
        return this.getNodesOfVirtualPathway(pathwayId);
    }

    public List<EdgeInterface> getEdgesByPathway(String pathwayId) {
        return this.getEdgesOfVirtualPathway(pathwayId);
    }

    @NotNull
    @Contract("_ -> new")
    private static Pair<List<EdgeInterface>, List<Pair<String, String>>> buildRemainingPathwayEdges(@NotNull List<NodeInterface> nodes) {
        var edgesList = new ArrayList<EdgeInterface>();
        var edgesNames = new ArrayList<Pair<String, String>>();
        for (var n : nodes) {
            edgesList.add(new Edge(n, n));
            edgesNames.add(new Pair<>(n.getId(), n.getId()));
        }
        return new Pair<>(edgesList, edgesNames);
    }

    @Override
    public void completePathways(Map<String, ?> expressionMap) {
        var added = this.mergedPathway.completePathway(expressionMap);
        if (added != null && added.size() > 0) {
            this.setNameOfVirtualPathway(REMAINING_PATHWAY_ID, REMAINING_PATHWAY_NAME);
            this.virtualPathwaysSource.put(REMAINING_PATHWAY_ID, this.mergedPathway);
            this.virtualPathwaysNodesObject.put(REMAINING_PATHWAY_ID, new ArrayList<>(added));
            var p = buildRemainingPathwayEdges(added);
            this.virtualPathwaysEdgesObject.put(REMAINING_PATHWAY_ID, p.getFirst());
            this.virtualPathwaysEdges.put(REMAINING_PATHWAY_ID, p.getSecond());
        }
    }

    /**
     * Clone the object
     *
     * @return my clone
     */
    public Object clone() {
        MergedRepository clone;
        clone = (MergedRepository) super.clone();
        clone.mergedPathway = clone.getPathwayById(this.mergedPathway.getId());
        clone.virtualPathwaysEdges = new HashMap<>();
        clone.virtualPathwaysSource = new HashMap<>();
        for (Map.Entry<String, List<Pair<String, String>>> e : virtualPathwaysEdges.entrySet()) {
            clone.virtualPathwaysEdges.put(e.getKey(), new ArrayList<>(e.getValue()));
            PathwayInterface p = virtualPathwaysSource.get(e.getKey());
            if (p != null) {
                clone.virtualPathwaysSource.put(e.getKey(), clone.getPathwayById(p.getId()));
            }
        }
        return clone;
    }
}
