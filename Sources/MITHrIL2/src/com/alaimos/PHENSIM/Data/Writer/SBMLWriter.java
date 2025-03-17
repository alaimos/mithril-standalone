package com.alaimos.PHENSIM.Data.Writer;

import com.alaimos.Commons.Writer.AbstractDataWriter;
import com.alaimos.Commons.Writer.DataWriterInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.GraphInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.MITHrIL.Data.Pathway.Type.NodeType;
import com.alaimos.PHENSIM.Data.Results.PhensimRun;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.sbml.jsbml.*;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.1
 */
public class SBMLWriter extends AbstractDataWriter<PhensimRun> {

    protected RepositoryInterface r;

    private final HashSet<String> identifiers = new HashSet<>();
    private final Map<String, Species> speciesMap = new HashMap<>();
    private final Map<String, Double> absoluteWeights = new HashMap<>();
    private int lastId = 1;
    private String pathway = null;
    private GraphInterface graph = null;
    private Model model = null;
    private Compartment compartment = null;

    public SBMLWriter(RepositoryInterface r) {
        this.r = r;
    }

    /**
     * Clean-up the node identifier so it is valid for the SBML standard. Entrez gene identifiers are prefixed with
     * "entrezId_" since number only identifiers are not supported. All "-" are replaced with "_". All ":" are replaced
     * with a "_".
     *
     * @param id An identifier
     * @return A valid identifier for SBML
     */
    public String cleanNodeIdentifier(String id) {
        var result = id;
        if (NumberUtils.isParsable(id)) {
            result = "entrezId_" + result;
        } else if (Character.isDigit(result.charAt(0))) {
            result = "node_" + result;
        }
        return result.replaceAll("[^a-zA-Z0-9_]", "_");
    }

    /**
     * Compute the total weight of a given node. The total weight is obtained by summing up the absolute weight values
     * for all outgoing edges.
     *
     * @param u The node for which the total weight is computed
     * @return the total weight
     */
    private double absoluteTotalWeight(String u) {
        try {
            if (absoluteWeights.containsKey(u)) return absoluteWeights.get(u);
            var start = graph.getNode(u);
            double weight = 0.0;
            for (NodeInterface d : graph.outgoingNodes(start)) {
                var w = Math.abs(graph.getEdge(start, d).computeWeight());
                weight += w;
            }
            absoluteWeights.put(u, weight);
            return weight;
        } catch (NullPointerException ignore) {
        }
        return Double.NaN;
    }

    /**
     * Add all species to the model
     *
     * @param data phensim results
     */
    public void createSpecies(PhensimRun data) {
        for (var n : graph.getNodes().values()) {
            var nId = this.cleanNodeIdentifier(n.getId());
            if (!identifiers.add(nId)) {
                nId = nId + "_" + lastId++;
                identifiers.add(nId);
            }
            var s = model.createSpecies(nId, compartment);
            s.setName(n.getName());
            s.setValue(data.getAveragePerturbation(pathway, n.getId()));
            try {
                s.setNotes("Average Perturbation: " + data.getAveragePerturbation(pathway, n.getId()) +
                                   "<br />Activity Score: " + data.getActivityScore(pathway, n.getId()) +
                                   "<br />p-value: " + data.getNodePValue(pathway, n.getId()) +
                                   "<br />FDR: " + data.getNodeAdjustedPValue(pathway, n.getId()));
            } catch (XMLStreamException ignore) {
            }
            if (n.getType() == NodeType.fromString("GENE")) {
                s.setSBOTerm(SBO.getGene());
            } else if (n.getType() == NodeType.fromString("COMPOUND")) {
                s.setSBOTerm(299); // metabolite
            } else if (n.getType() == NodeType.fromString("MIRNA")) {
                s.setSBOTerm(316); // microRNA
            } else {
                s.setSBOTerm(SBO.getEntity());
            }
            speciesMap.put(nId, s);
        }
    }

    /**
     * Create all reactions
     */
    public void createReactions() {
        var i = 0;
        for (var edgesEntry : graph.getEdges().entrySet()) {
            var start = this.cleanNodeIdentifier(edgesEntry.getKey());
            for (var edgeEntry : edgesEntry.getValue().entrySet()) {
                var end = this.cleanNodeIdentifier(edgeEntry.getKey());
                var weight = edgeEntry.getValue().computeWeight() / absoluteTotalWeight(edgesEntry.getKey());
                if (Double.isFinite(weight) && weight != 0) {
                    var reaction = model.createReaction("edge_" + (++i));
                    if (weight > 0) reaction.setSBOTerm(656); // activation
                    else reaction.setSBOTerm(169); // inhibition
                    reaction.setReversible(false);
                    var reactant = reaction.createReactant(speciesMap.get(start));
                    reactant.setSBOTerm(15);
                    reactant.setValue(weight);
                    reaction.createProduct(speciesMap.get(end)).setSBOTerm(11);
                }
            }
        }
    }

    /**
     * Write data
     *
     * @param data the data that will be written into a file
     * @return this object for a fluent interface
     */
    @Override
    public DataWriterInterface<PhensimRun> write(@NotNull PhensimRun data) {
        if (r.size() > 1) {
            if (!getFile().isDirectory() || !getFile().exists()) {
                getFile().mkdirs();
            }
        }
        for (var p : r) {
            if (p.hasGraph()) {
                var file = (r.size() > 1) ? new File(getFile(), p.getId() + ".sbml") : getFile();
                pathway = p.getId();
                speciesMap.clear();
                graph = p.getGraph();
                var document = new SBMLDocument(3, 1);
                model       = document.createModel(pathway + "_model");
                compartment = model.createCompartment(pathway + "_compartment");
                createSpecies(data);
                createReactions();
                try {
                    org.sbml.jsbml.SBMLWriter.write(document, file, "PHENSIM", "2.1.0.0");
                } catch (XMLStreamException | IOException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        return this;
    }
}
