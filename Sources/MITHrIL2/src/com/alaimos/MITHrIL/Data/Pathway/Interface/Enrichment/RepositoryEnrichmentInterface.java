package com.alaimos.MITHrIL.Data.Pathway.Interface.Enrichment;

import java.io.Serializable;
import java.util.List;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @since 08/12/2015
 * @version 2.0.0.0
 */
public interface RepositoryEnrichmentInterface extends Serializable {

    RepositoryEnrichmentInterface addAdditionalEdges(EdgeEnrichmentInterface additionalEdges);

    RepositoryEnrichmentInterface addAdditionalEdges(List<EdgeEnrichmentInterface> additionalEdges);

    RepositoryEnrichmentInterface setAdditionalEdges(List<EdgeEnrichmentInterface> additionalEdges);

    List<EdgeEnrichmentInterface> getAdditionalEdges();

}
