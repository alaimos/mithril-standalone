package com.alaimos.MITHrIL.Data.Pathway.Impl.Enrichment;

import com.alaimos.MITHrIL.Data.Pathway.Interface.Enrichment.EdgeDescriptionEnrichmentInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.Enrichment.EdgeEnrichmentInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.Enrichment.NodeEnrichmentInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 08/12/2015
 */
public class EdgeEnrichment implements EdgeEnrichmentInterface {

    private static final long serialVersionUID = 1784388901033680671L;
    protected NodeEnrichmentInterface                       start;
    protected NodeEnrichmentInterface                       end;
    protected ArrayList<EdgeDescriptionEnrichmentInterface> descriptions;

    public EdgeEnrichment(NodeEnrichmentInterface start, NodeEnrichmentInterface end) {
        this.start = start;
        this.end = end;
        this.descriptions = new ArrayList<>();
    }

    @Override
    public NodeEnrichmentInterface getStart() {
        return this.start;
    }

    @Override
    public EdgeEnrichmentInterface setStart(NodeEnrichmentInterface start) {
        this.start = start;
        return this;
    }

    @Override
    public NodeEnrichmentInterface getEnd() {
        return this.end;
    }

    @Override
    public EdgeEnrichmentInterface setEnd(NodeEnrichmentInterface end) {
        this.end = end;
        return this;
    }

    @Override
    public boolean isMultiEdge() {
        return (this.descriptions.size() > 1);
    }

    @Override
    public List<EdgeDescriptionEnrichmentInterface> getDescriptions() {
        return this.descriptions;
    }

    @Override
    public EdgeEnrichmentInterface addDescription(EdgeDescriptionEnrichmentInterface description) {
        this.descriptions.add(description);
        return this;
    }

    @Override
    public EdgeEnrichmentInterface addDescriptions(List<EdgeDescriptionEnrichmentInterface> descriptions) {
        this.descriptions.addAll(descriptions);
        return this;
    }

    @Override
    public EdgeEnrichmentInterface clearDescriptions() {
        this.descriptions.clear();
        return this;
    }

    @Override
    public EdgeEnrichmentInterface setDescription(EdgeDescriptionEnrichmentInterface description) {
        return this.clearDescriptions().addDescription(description);
    }

    @Override
    public EdgeEnrichmentInterface setDescriptions(List<EdgeDescriptionEnrichmentInterface> description) {
        return this.clearDescriptions().addDescriptions(description);
    }

    @Override
    public String toString() {
        return "EdgeEnrichment{" +
                "start=" + start +
                ", end=" + end +
                ", descriptions=" + descriptions +
                '}';
    }
}
