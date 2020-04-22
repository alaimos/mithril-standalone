package com.alaimos.MITHrIL.Algorithm;

import com.alaimos.Commons.Algorithm.Impl.AbstractAlgorithm;
import com.alaimos.Commons.Algorithm.Interface.AlgorithmInterface;
import com.alaimos.MITHrIL.Data.Pathway.Factory.PathwayFactory;
import com.alaimos.MITHrIL.Data.Pathway.Interface.EdgeDescriptionInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.PathwayFactoryInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.PathwayInterface;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 31/12/2015
 */
public class DecoyBuilder extends AbstractAlgorithm<PathwayInterface> {

    protected PathwayFactoryInterface factory;

    public DecoyBuilder() {
    }

    /**
     * Prepares the workflow for a new execution
     *
     * @return This object for a fluent interface
     */
    @Override
    public AlgorithmInterface<PathwayInterface> init() {
        super.init();
        factory = PathwayFactory.getInstance();
        return this;
    }

    /**
     * Merges all the pathways in a repository building a new repository with a single pathway graph
     */
    @Override
    public void run() {
        var p = getParameterNotNull("pathway", PathwayInterface.class);
        @SuppressWarnings("unchecked")
        var allNodes = (List<String>) getParameterNotNull("allNodes", ArrayList.class);
        @SuppressWarnings("unchecked")
        var idToNodes = (Map<String, NodeInterface>) getParameterNotNull("idToNodes", Map.class);
        var r = getParameterNotNull("random", Random.class);
        var pf = getParameter("factory", PathwayFactoryInterface.class);
        if (pf != null && pf != factory) factory = pf;
        notifyObservers("startDecoyBuilding", p);
        var g = p.getGraph();
        var gn = factory.getGraph();
        output = factory.getPathway(p.getId() + "d", p.getName() + " - Decoy", gn);
        output.setCategories(p.getCategories());
        notifyObservers("mappingOldToNew", p);
        var oldToNew = new HashMap<String, String>();
        var used = new HashSet<String>();
        NodeInterface existingNode;
        for (NodeInterface n : g.getNodes().values()) {
            String newId = null, oldId = n.getId(), tmp;
            while (newId == null) {
                tmp = allNodes.get(r.nextInt(allNodes.size()));
                if (!tmp.equals(oldId) && used.add(tmp)) {
                    newId = tmp;
                }
            }
            oldToNew.put(oldId, newId);
            existingNode = idToNodes.get(newId);
            gn.addNode(newId, oldId, existingNode.getType()).setAliases(Collections.singletonList(newId));
        }
        notifyObservers("cloningEdges", p);
        g.getEdgesStream().forEach(e -> {
            String start = oldToNew.get(e.getStart().getId()), end = oldToNew.get(e.getEnd().getId());
            gn.addEdge(gn.getNode(start), gn.getNode(end),
                       e.getDescriptionsStream().map(e1 -> (EdgeDescriptionInterface) e1.clone()).map(e1 -> e1.setOwner(output))
                        .collect(Collectors.toList()));
        });
        notifyObservers("doneDecoyBuilding", p);
    }
}
