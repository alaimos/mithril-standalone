package com.alaimos.MITHrIL.Data.Pathway.Impl.Enrichment;

import com.alaimos.MITHrIL.Data.Pathway.Interface.Enrichment.EdgeDescriptionEnrichmentInterface;
import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeSubType;
import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeType;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 08/12/2015
 */
public class EdgeDescriptionEnrichment implements EdgeDescriptionEnrichmentInterface {

    private static final long serialVersionUID = 1920364967851205850L;
    protected EdgeType type;

    protected EdgeSubType subType;

    public EdgeDescriptionEnrichment(EdgeType type, EdgeSubType subType) {
        this.type = type;
        this.subType = subType;
    }

    @Override
    public EdgeType getType() {
        return this.type;
    }

    @Override
    public EdgeDescriptionEnrichmentInterface setType(EdgeType type) {
        this.type = type;
        return this;
    }

    @Override
    public EdgeSubType getSubType() {
        return this.subType;
    }

    @Override
    public EdgeDescriptionEnrichmentInterface setSubType(EdgeSubType subType) {
        this.subType = subType;
        return this;
    }

    @Override
    public String toString() {
        return "EdgeDescriptionEnrichment{" +
                "type=" + type +
                ", subType=" + subType +
                '}';
    }
}
