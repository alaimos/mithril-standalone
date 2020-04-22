package com.alaimos.MITHrIL.Data.Pathway.Interface.Enrichment;

import java.io.Serializable;
import java.util.List;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @since 08/12/2015
 * @version 2.0.0.0
 */
public interface EdgeEnrichmentInterface extends Serializable {


    NodeEnrichmentInterface getStart();

    EdgeEnrichmentInterface setStart(NodeEnrichmentInterface start);

    NodeEnrichmentInterface getEnd();

    EdgeEnrichmentInterface setEnd(NodeEnrichmentInterface end);

    boolean isMultiEdge();

    List<EdgeDescriptionEnrichmentInterface> getDescriptions();

    EdgeEnrichmentInterface addDescription(EdgeDescriptionEnrichmentInterface description);

    EdgeEnrichmentInterface addDescriptions(List<EdgeDescriptionEnrichmentInterface> descriptions);

    EdgeEnrichmentInterface clearDescriptions();

    EdgeEnrichmentInterface setDescription(EdgeDescriptionEnrichmentInterface description);

    EdgeEnrichmentInterface setDescriptions(List<EdgeDescriptionEnrichmentInterface> description);

}
