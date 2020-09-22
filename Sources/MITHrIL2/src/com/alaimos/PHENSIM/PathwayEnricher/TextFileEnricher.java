package com.alaimos.PHENSIM.PathwayEnricher;

import com.alaimos.MITHrIL.Data.Pathway.Factory.EnrichmentFactory;
import com.alaimos.MITHrIL.Data.Pathway.Interface.Enrichment.EdgeDescriptionEnrichmentInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.Enrichment.EdgeEnrichmentInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.Enrichment.NodeEnrichmentInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.Enrichment.RepositoryEnrichmentInterface;
import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeSubType;
import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeType;
import com.alaimos.MITHrIL.Data.Pathway.Type.NodeType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Enriches pathway with elements taken from a text file
 * <p>
 * filter parameter of type <code>String</code> is <b>required</b>.
 * <p>
 * inputFile parameter of type <code>String</code> is <b>required</b>.
 * <p>
 * nodeTypesFile parameter of type <code>String</code> is <b>optional</b>.
 * <p>
 * edgeTypesFile parameter of type <code>String</code> is <b>optional</b>.
 * <p>
 * edgeSubTypesFile parameter of type <code>String</code> is <b>optional</b>.
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.0
 * @since 19/08/2020
 */
public class TextFileEnricher extends AbstractPathwayEnricher {

    private File inputFile;
    private EnrichmentFactory                        ef    = EnrichmentFactory.getInstance();
    private RepositoryEnrichmentInterface            re    = ef.getRepository();
    private HashMap<String, NodeEnrichmentInterface> nodes = new HashMap<>();

    /**
     * Get short name that identify  this enricher
     *
     * @return a short unique name
     */
    @Override
    public String getShortName() {
        return "textEnricher";
    }

    /**
     * Get a description for this enricher
     *
     * @return a description
     */
    @Override
    public String getDescription() {
        return "Enriches pathways with elements taken from a text file.";
    }

    private String[] splitString(String s) {
        return Arrays.stream(s.split("\t", -1)).map(String::trim).toArray(String[]::new);
    }

    private boolean inFilter(String needle, String haystack) {
        for (String s : haystack.split(",", -1)) {
            if (s.trim().equalsIgnoreCase(needle)) return true;
        }
        return false;
    }

    private void readAdditionalFile(String filename, Predicate<String> filter, Consumer<String[]> consumer) {
        File fl;
        if (filename != null && (fl = new File(filename)).exists()) {
            try (BufferedReader r = new BufferedReader(new FileReader(fl))) {
                String l;
                String[] f;
                while ((l = r.readLine()) != null) {
                    if (!l.isEmpty() && !l.startsWith("#")) {
                        f = splitString(l);
                        if (f.length > 0) {
                            if (filter.test(f[0])) {
                                consumer.accept(f);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void readAdditionalNodeTypes() {
        String nodeTypeFile = getParameter("nodeTypesFile", String.class);
        readAdditionalFile(nodeTypeFile, s -> NodeType.fromString(s).name().equals("OTHER"), fields -> {
            if (fields.length >= 2) {
                NodeType.add(fields[0], Double.parseDouble(fields[1]));
            } else {
                NodeType.add(fields[0]);
            }
        });
    }

    private void readAdditionalEdgeTypes() {
        String edgeTypesFile = getParameter("edgeTypesFile", String.class);
        readAdditionalFile(edgeTypesFile, s -> EdgeType.fromString(s).name().equals("OTHER"), fields -> {
            if (fields.length > 0) {
                NodeType.add(fields[0]);
            }
        });
    }

    private void readAdditionalEdgeSubTypes() {
        String edgeSubTypesFile = getParameter("edgeSubTypesFile", String.class);
        readAdditionalFile(edgeSubTypesFile, s -> EdgeSubType.fromString(s).name().equals("UNKNOWN"), fields -> {
            if (fields.length >= 4) {
                EdgeSubType.add(fields[0], Double.parseDouble(fields[1]), Integer.parseInt(fields[2]), fields[3]);
            } else if (fields.length == 3) {
                EdgeSubType.add(fields[0], Double.parseDouble(fields[1]), Integer.parseInt(fields[2]), "");
            } else if (fields.length == 2) {
                EdgeSubType.add(fields[0], Double.parseDouble(fields[1]), 0, "");
            } else {
                EdgeSubType.add(fields[0]);
            }
        });
    }

    /**
     * Make a new node object for the enrichment procedure
     *
     * @param id   The id of the node
     * @param name The name of the node
     * @param type The type of the node
     * @return The node object
     */
    private NodeEnrichmentInterface makeEnrichedNode(String id, String name, String type) {
        if (nodes.containsKey(id)) return nodes.get(id);
        NodeEnrichmentInterface node = ef.getNodeEnrichment(id, (name != null && !name.isEmpty()) ? name : id, type);
        nodes.put(id, node);
        return node;
    }

    /**
     * Make a new target node object for the enrichment procedure
     *
     * @param id   The id of the target node
     * @param name the name of the target node
     * @param type the type of the target node
     * @return The target object
     */
    private NodeEnrichmentInterface makeTargetEnrichment(String id, String name, String type) {
        if (nodes.containsKey(id)) return nodes.get(id);
        NodeEnrichmentInterface node =
                ef.getNodeEnrichment(id, (name != null && !name.isEmpty()) ? name : id,
                        (type != null && !type.isEmpty()) ? type : "GENE").setMustExist(true);
        nodes.put(id, node);
        return node;
    }

    /**
     * Make an enrichment edge object for two nodes
     *
     * @param start   the start node
     * @param end     the end node
     * @param type    the edge type
     * @param subType the edge subtype
     * @return the enrichment object
     */
    private EdgeEnrichmentInterface makeEdgeEnrichment(NodeEnrichmentInterface start, NodeEnrichmentInterface end,
                                                       String type, String subType) {
        EdgeEnrichmentInterface edge = ef.getEdgeEnrichment(start, end);
        EdgeDescriptionEnrichmentInterface edi =
                ef.getEdgeDescriptionEnrichment(EdgeType.fromString(type), EdgeSubType.fromString(subType));
        edge.addDescription(edi);
        return edge;
    }

    /**
     * Fills the repository enrichment object
     *
     * @param filter a filter
     */
    private void fillRepositoryEnrichment(String filter) {
        try (BufferedReader r = new BufferedReader(new FileReader(inputFile))) {
            NodeEnrichmentInterface start, end;
            EdgeEnrichmentInterface edge;
            String line;
            String[] s;
            while ((line = r.readLine()) != null) {
                if (!line.isEmpty() && !line.startsWith("#")) {
                    s = splitString(line);
                    if (s.length >= 9 && (filter != null && inFilter(filter, s[8]))) {
                        start = makeEnrichedNode(s[0], s[1], s[2]);
                        end = makeTargetEnrichment(s[3], s[4], s[5]);
                        edge = makeEdgeEnrichment(start, end, s[6], s[7]);
                        re.addAdditionalEdges(edge);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Builds and returns the data structures which contains the data that will be used to enrich repositories.
     *
     * @return the data to use to enrich a repository
     */
    @Override
    public RepositoryEnrichmentInterface getRepositoryEnrichment() {
        readAdditionalNodeTypes();
        readAdditionalEdgeTypes();
        readAdditionalEdgeSubTypes();
        String filter = getParameter("filter", String.class);
        inputFile = new File(getParameterNotNull("inputFile", String.class));
        if (!inputFile.exists()) {
            throw new RuntimeException("Invalid input file specified for the textEnricher.");
        }
        fillRepositoryEnrichment(filter);
        return re;
    }
}
