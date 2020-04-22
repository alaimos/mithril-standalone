package com.alaimos.MITHrIL.Data.Pathway.Interface.Enrichment;

import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeSubType;
import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeType;
import com.alaimos.MITHrIL.Data.Pathway.Type.NodeType;

import java.io.Serializable;
import java.util.List;

/**
 * Interface of enrichment factory
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 08/12/2015
 */
public interface EnrichmentFactoryInterface extends Serializable {

    EdgeDescriptionEnrichmentInterface getEdgeDescriptionEnrichment(EdgeType type, EdgeSubType subType);

    EdgeEnrichmentInterface getEdgeEnrichment(NodeEnrichmentInterface start, NodeEnrichmentInterface end);

    NodeEnrichmentInterface getNodeEnrichment(String id, String name, List<String> aliases, NodeType type);

    NodeEnrichmentInterface getNodeEnrichment(String id, String name, List<String> aliases, String type);

    NodeEnrichmentInterface getNodeEnrichment(String id, String name, NodeType type);

    NodeEnrichmentInterface getNodeEnrichment(String id, String name, String type);

    RepositoryEnrichmentInterface getRepository();

}
