package com.alaimos.MITHrIL.Data.Pathway.Interface.Enrichment;

import com.alaimos.MITHrIL.Data.Pathway.Type.NodeType;

import java.io.Serializable;
import java.util.List;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @since 08/12/2015
 * @version 2.0.0.0
 */
public interface NodeEnrichmentInterface extends Serializable {

    String getId();

    NodeEnrichmentInterface setId(String id);

    String getName();

    NodeEnrichmentInterface setName(String name);

    List<String> getAliases();

    NodeEnrichmentInterface addAliases(List<String> aliases);

    NodeEnrichmentInterface clearAliases();

    NodeEnrichmentInterface setAliases(List<String> aliases);

    NodeType getType();

    NodeEnrichmentInterface setType(NodeType type);

    boolean isMustExist();

    NodeEnrichmentInterface setMustExist(boolean mustExist);

}
