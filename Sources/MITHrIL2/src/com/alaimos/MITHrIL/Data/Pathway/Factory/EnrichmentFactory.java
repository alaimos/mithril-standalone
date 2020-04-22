package com.alaimos.MITHrIL.Data.Pathway.Factory;

import com.alaimos.MITHrIL.Data.Pathway.Impl.Enrichment.EdgeDescriptionEnrichment;
import com.alaimos.MITHrIL.Data.Pathway.Impl.Enrichment.EdgeEnrichment;
import com.alaimos.MITHrIL.Data.Pathway.Impl.Enrichment.NodeEnrichment;
import com.alaimos.MITHrIL.Data.Pathway.Impl.Enrichment.RepositoryEnrichment;
import com.alaimos.MITHrIL.Data.Pathway.Interface.Enrichment.*;
import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeSubType;
import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeType;
import com.alaimos.MITHrIL.Data.Pathway.Type.NodeType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 08/12/2015
 */
public class EnrichmentFactory implements EnrichmentFactoryInterface {

    private static final long              serialVersionUID = -7785685726481140095L;
    private static       EnrichmentFactory instance         = new EnrichmentFactory();

    public static EnrichmentFactory getInstance() {
        return instance;
    }

    private EnrichmentFactory() {
    }

    @Override
    public EdgeDescriptionEnrichmentInterface getEdgeDescriptionEnrichment(EdgeType type, EdgeSubType subType) {
        return new EdgeDescriptionEnrichment(type, subType);
    }

    @Override
    public EdgeEnrichmentInterface getEdgeEnrichment(NodeEnrichmentInterface start, NodeEnrichmentInterface end) {
        return new EdgeEnrichment(start, end);
    }

    @Override
    public NodeEnrichmentInterface getNodeEnrichment(String id, String name, List<String> aliases, NodeType type) {
        return new NodeEnrichment(id, name, aliases, type);
    }

    @Override
    public NodeEnrichmentInterface getNodeEnrichment(String id, String name, List<String> aliases, String type) {
        return new NodeEnrichment(id, name, aliases, type);
    }

    @Override
    public NodeEnrichmentInterface getNodeEnrichment(String id, String name, NodeType type) {
        return getNodeEnrichment(id, name, new ArrayList<>(), type);
    }

    @Override
    public NodeEnrichmentInterface getNodeEnrichment(String id, String name, String type) {
        return getNodeEnrichment(id, name, new ArrayList<>(), type);
    }

    @Override
    public RepositoryEnrichmentInterface getRepository() {
        return new RepositoryEnrichment();
    }
}
