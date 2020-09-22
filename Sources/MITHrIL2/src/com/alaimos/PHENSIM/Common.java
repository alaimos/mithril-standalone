package com.alaimos.PHENSIM;

import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.MITHrIL.Data.Records.Species;
import com.alaimos.MITHrIL.Data.Records.Type.EvidenceType;
import com.alaimos.PHENSIM.PathwayEnricher.MultipleEnricher;
import com.alaimos.PHENSIM.PathwayEnricher.PathwayEnricherFactory;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.alaimos.MITHrIL.Common.getPathwayRepository;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.0
 * @since 19/08/2020
 */
public class Common {

    /**
     * Read pathway repository
     *
     * @param s species definition
     * @return the repository
     */
    public static RepositoryInterface getEnrichedRepository(Species s, Long randomSeed, Consumer<String> report,
                                                            List<String> enrichers, EvidenceType evidenceType,
                                                            Map<String, Object> parameters) {
        RepositoryInterface r = getPathwayRepository(s, true, null, false, randomSeed, report);
        if (enrichers != null && enrichers.size() > 0) {
            MultipleEnricher me = new MultipleEnricher();
            me.setSpecies(s);
            if (parameters != null) {
                me.setParameters(parameters);
            }
            me.addParameter("evidenceFilter", evidenceType);
            me.setEnrichers(enrichers.stream()
                                     .map(PathwayEnricherFactory.getInstance()::getPathwayEnricher)
                                     .collect(Collectors.toList()));
            r = me.enrichRepository(r);
        }
        return r;
    }

}
