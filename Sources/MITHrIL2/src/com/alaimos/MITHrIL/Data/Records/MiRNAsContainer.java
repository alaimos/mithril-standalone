package com.alaimos.MITHrIL.Data.Records;

import com.alaimos.MITHrIL.Data.Pathway.Factory.EnrichmentFactory;
import com.alaimos.MITHrIL.Data.Pathway.Interface.Enrichment.EdgeEnrichmentInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.Enrichment.NodeEnrichmentInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.Enrichment.RepositoryEnrichmentInterface;
import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeSubType;
import com.alaimos.MITHrIL.Data.Pathway.Type.EdgeType;
import com.alaimos.MITHrIL.Data.Pathway.Type.NodeType;
import com.alaimos.MITHrIL.Data.Records.Type.EvidenceType;

import java.util.HashMap;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 24/12/2015
 */
public class MiRNAsContainer extends HashMap<String, MiRNA> {

    public boolean add(MiRNA m) {
        if (!containsKey(m.getMiRNAId())) {
            this.put(m.getMiRNAId(), m);
            return true;
        }
        return false;
    }

    public RepositoryEnrichmentInterface toEnrichment(EvidenceType filter) {
        EnrichmentFactory ef = EnrichmentFactory.getInstance();
        RepositoryEnrichmentInterface er = ef.getRepository();
        HashMap<String, NodeEnrichmentInterface> ends = new HashMap<>();
        forEach((s, miRNA) -> {
            String id = miRNA.getMiRNAId();
            NodeEnrichmentInterface n = ef.getNodeEnrichment(id, id, NodeType.fromString("MIRNA"));
            ends.put(id, n);
            miRNA.getTargets().forEach(target -> {
                if (target.getEvidenceType().value() <= filter.value()) {
                    NodeEnrichmentInterface t = ends.get(target.getTargetId());
                    if (t == null) {
                        t = ef.getNodeEnrichment(target.getTargetId(), target.getTargetName(),
                                NodeType.fromString("GENE")).setMustExist(true);
                        ends.put(target.getTargetId(), t);
                    }
                    EdgeEnrichmentInterface ee = ef.getEdgeEnrichment(n, t);
                    ee.addDescription(ef.getEdgeDescriptionEnrichment(EdgeType.fromString("MGREL"),
                            EdgeSubType.valueOf("MIRNA_INHIBITION")));
                    er.addAdditionalEdges(ee);
                }
            });
            miRNA.getTranscriptionFactors().forEach(tf -> {
                if (tf.getEvidenceType().value() <= filter.value()) {
                    NodeEnrichmentInterface t = ends.get(tf.getTfId());
                    if (t == null) {
                        t = ef.getNodeEnrichment(tf.getTfId(), tf.getTfId(), NodeType.fromString("GENE"))
                              .setMustExist(true);
                        ends.put(tf.getTfId(), t);
                    }
                    EdgeEnrichmentInterface ee = ef.getEdgeEnrichment(t, n);
                    EdgeSubType est = (tf.getTfType().equalsIgnoreCase("inhibition")) ?
                                      EdgeSubType.valueOf("TFMIRNA_INHIBITION") :
                                      EdgeSubType.valueOf("TFMIRNA_ACTIVATION");
                    ee.addDescription(ef.getEdgeDescriptionEnrichment(EdgeType.fromString("MGREL"), est));
                    er.addAdditionalEdges(ee);
                }
            });
        });
        return er;
    }

}
