package com.alaimos.MITHrIL.Data.Pathway.Interface.Enrichment;

import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeSubType;
import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeType;

import java.io.Serializable;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @since 06/12/2015
 * @version 2.0.0.0
 */
public interface EdgeDescriptionEnrichmentInterface extends Cloneable, Serializable {

    EdgeType getType();

    EdgeDescriptionEnrichmentInterface setType(EdgeType type);

    EdgeSubType getSubType();

    EdgeDescriptionEnrichmentInterface setSubType(EdgeSubType subType);

}
