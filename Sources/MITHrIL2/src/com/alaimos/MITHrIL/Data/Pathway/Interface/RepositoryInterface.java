package com.alaimos.MITHrIL.Data.Pathway.Interface;

import com.alaimos.Commons.Utils.Pair;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 08/12/2015
 */
public interface RepositoryInterface extends Collection<PathwayInterface>, Iterable<PathwayInterface>,
        WeightComputationAwareInterface<RepositoryInterface>, Cloneable, Serializable {

    /**
     * Add random decoy pathways
     *
     * @return this object
     */
    default RepositoryInterface addDecoys() {
        return addDecoys(System.currentTimeMillis());
    }

    /**
     * Add random decoy pathway.
     * A seed for the Random Number Generator can be set for reproducibility.
     *
     * @param rngSeed The RNG seed
     * @return this object
     */
    RepositoryInterface addDecoys(long rngSeed);

    boolean containsPathway(PathwayInterface p);

    boolean containsPathway(String p);

    PathwayInterface getPathwayById(String pathwayId);

    List<PathwayInterface> getPathwaysByCategory(String category);

    List<PathwayInterface> getPathwaysByCategory(List<String> category);

    List<PathwayInterface> getPathways();

    boolean removeNode(String id);

    boolean removeNode(NodeInterface n);

    List<String> getPathwayIdsByCategory(String category);

    List<String> getPathwayIdsByCategory(List<String> category);

    List<String> getPathwaysByNodeId(String nodeId);

    List<String> getCategories();

    PathwayInterface getDefaultVirtualSource();

    RepositoryInterface addVirtualPathway(PathwayInterface from, String id, List<Pair<String, String>> edges);

    default RepositoryInterface addVirtualPathway(String id, List<Pair<String, String>> edges) {
        return addVirtualPathway(getDefaultVirtualSource(), id, edges);
    }

    Set<String> getVirtualPathways();

    boolean hasVirtualPathway(String virtualPathwayId);

    RepositoryInterface setNameOfVirtualPathway(String virtualPathwayId, String name);

    String getNameOfVirtualPathway(String virtualPathwayId);

    List<NodeInterface> getNodesOfVirtualPathway(String virtualPathwayId);

    List<EdgeInterface> getEdgesOfVirtualPathway(String virtualPathwayId);

    List<Pair<String, String>> getEdgesByVirtualPathway(String virtualPathwayId);

    PathwayInterface getSourceOfVirtualPathway(String virtualPathwayId);

    default void completePathways(Map<String, ?> expressionMap) {
        for (PathwayInterface p : this) {
            p.completePathway(expressionMap);
        }
    }

    Object clone();

}
