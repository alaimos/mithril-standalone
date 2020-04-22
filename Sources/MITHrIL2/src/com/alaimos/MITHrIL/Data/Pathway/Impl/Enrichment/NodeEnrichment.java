package com.alaimos.MITHrIL.Data.Pathway.Impl.Enrichment;

import com.alaimos.MITHrIL.Data.Pathway.Interface.Enrichment.NodeEnrichmentInterface;
import com.alaimos.MITHrIL.Data.Pathway.Type.NodeType;

import java.util.List;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 08/12/2015
 */
public class NodeEnrichment implements NodeEnrichmentInterface {

    private static final long serialVersionUID = 6662365961120536620L;
    protected String       id;
    protected String       name;
    protected List<String> aliases;
    protected NodeType     type;
    protected boolean      mustExist;

    public NodeEnrichment(String id, String name, List<String> aliases,
                          NodeType type) {
        this(id, name, aliases, type, false);
    }

    public NodeEnrichment(String id, String name, List<String> aliases, String type) {
        this(id, name, aliases, type, false);
    }

    public NodeEnrichment(String id, String name, List<String> aliases,
                          NodeType type, boolean mustExist) {
        this.id = id;
        this.name = name;
        this.aliases = aliases;
        this.type = type;
        this.mustExist = mustExist;
    }

    public NodeEnrichment(String id, String name, List<String> aliases, String type, boolean mustExist) {
        this(id, name, aliases, NodeType.fromString(type), mustExist);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public NodeEnrichmentInterface setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public NodeEnrichmentInterface setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public NodeEnrichmentInterface addAliases(List<String> aliases) {
        aliases.stream().filter(s -> !s.isEmpty()).forEachOrdered(this.aliases::add);
        return this;
    }

    @Override
    public NodeEnrichmentInterface clearAliases() {
        this.aliases.clear();
        return this;
    }

    @Override
    public NodeEnrichmentInterface setAliases(List<String> aliases) {
        return this.clearAliases().addAliases(aliases);
    }

    @Override
    public NodeType getType() {
        return this.type;
    }

    @Override
    public NodeEnrichmentInterface setType(NodeType type) {
        this.type = type;
        return this;
    }

    @Override
    public boolean isMustExist() {
        return mustExist;
    }

    @Override
    public NodeEnrichmentInterface setMustExist(boolean mustExist) {
        this.mustExist = mustExist;
        return this;
    }

    @Override
    public String toString() {
        return "NodeEnrichment{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", aliases=" + aliases +
                ", type=" + type +
                ", mustExist=" + mustExist +
                '}';
    }
}
