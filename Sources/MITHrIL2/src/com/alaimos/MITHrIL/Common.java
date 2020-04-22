package com.alaimos.MITHrIL;

import com.alaimos.MITHrIL.Algorithm.PathwayMerger;
import com.alaimos.MITHrIL.Algorithm.RepositoryEnricher;
import com.alaimos.MITHrIL.Data.Pathway.Impl.MergedRepository;
import com.alaimos.MITHrIL.Data.Pathway.Interface.Enrichment.RepositoryEnrichmentInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.MITHrIL.Data.Reader.BinaryReader;
import com.alaimos.MITHrIL.Data.Reader.RemoteMiRNATFReader;
import com.alaimos.MITHrIL.Data.Reader.RemoteMiRNATargetsReader;
import com.alaimos.MITHrIL.Data.Reader.RemotePathwayRepositoryReader;
import com.alaimos.MITHrIL.Data.Records.MiRNAsContainer;
import com.alaimos.MITHrIL.Data.Records.Species;
import com.alaimos.MITHrIL.Data.Records.Type.EvidenceType;

import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * Some common methods used to build repositories
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 08/01/16
 */
public class Common {

    /**
     * Download and import pathway repository (if an indexed local repository exists than it will use it)
     *
     * @param s                      A Species
     * @param noEnrichment           Do not enrich pathway with mirna
     * @param enrichmentEvidenceType Type of enrichment (STRONG, WEAK, PREDICTION)
     * @param addDecoys              Generate decoy pathways?
     * @param randomSeed             If addDecoys is true a seed for the random number generator. NULL otherwise.
     * @param report                 A function used to report progress status
     * @return A pathway repository
     */
    public static RepositoryInterface getPathwayRepository(Species s, boolean noEnrichment,
                                                           EvidenceType enrichmentEvidenceType, boolean addDecoys,
                                                           Long randomSeed, Consumer<String> report) {
        return getPathwayRepository(s, noEnrichment, enrichmentEvidenceType, addDecoys, randomSeed, report, true);
    }

    /**
     * Download and import pathway repository
     *
     * @param s                      A Species
     * @param noEnrichment           Do not enrich pathway with mirna
     * @param enrichmentEvidenceType Type of enrichment (STRONG, WEAK, PREDICTION)
     * @param addDecoys              Generate decoy pathways?
     * @param randomSeed             If addDecoys is true a seed for the random number generator. NULL otherwise.
     * @param report                 A function used to report progress status
     * @param indexed                Read from the indexed local repository if the file exists?
     * @return A pathway repository
     */
    public static RepositoryInterface getPathwayRepository(Species s, boolean noEnrichment,
                                                           EvidenceType enrichmentEvidenceType, boolean addDecoys,
                                                           Long randomSeed, Consumer<String> report, boolean indexed) {
        if (indexed) {
            String fileName = "index-" + s.getId() + "-" + enrichmentEvidenceType + "-repository.datz";
            try {
                RepositoryInterface r =
                        new BinaryReader<>(RepositoryInterface.class).read(fileName);
                if (r != null) return r;
            } catch (Exception ignore) {
            }
        }
        RemotePathwayRepositoryReader pathwayReader = new RemotePathwayRepositoryReader(s.getPathwayDatabaseUrl());
        RepositoryInterface r = pathwayReader.read();
        if (r == null) throw new RuntimeException("Unable to read pathway repository.");
        if (!noEnrichment && s.hasMiRNA()) {
            report.accept("...Reading miRNAs");
            RemoteMiRNATargetsReader rm = new RemoteMiRNATargetsReader(s.getMiRNADatabaseUrl());
            MiRNAsContainer m = rm.readMiRNAs();
            if (s.hasTF()) {
                RemoteMiRNATFReader tf = new RemoteMiRNATFReader(s.getTFDatabaseUrl());
                tf.readTranscriptionFactors(m);
            }
            RepositoryEnrichmentInterface rei = m.toEnrichment(enrichmentEvidenceType);
            report.accept("...Enriching pathways");
            RepositoryEnricher re = new RepositoryEnricher();
            re.init().setParameter("repository", r).setParameter("enrichment", rei).run();
            r = re.getOutput();
        }
        if (addDecoys) {
            report.accept("...Adding decoy pathways");
            if (randomSeed == null) {
                r.addDecoys();
            } else {
                r.addDecoys(randomSeed);
            }
        }
        return r;
    }

    /**
     * Merges all pathways in a repository to create a meta-pathway
     *
     * @param r                 A pathway repository
     * @param includeCategories A set of pathway categories to use
     * @param excludeCategories A set of pathway categories to exclude from the merging
     * @return A repository which contains the meta-pathway
     */
    public static MergedRepository mergeRepositories(RepositoryInterface r, String[] includeCategories,
                                                     String[] excludeCategories) {
        return mergeRepositories(r, includeCategories, excludeCategories, null, null, false);
    }

    /**
     * Merges all pathways in a repository to create a meta-pathway
     *
     * @param r                 A pathway repository
     * @param includeCategories A set of pathway categories to use
     * @param excludeCategories A set of pathway categories to exclude from the merging
     * @param nodesFilter       A set of regular expression to filter nodes
     * @return A repository which contains the meta-pathway
     */
    public static MergedRepository mergeRepositories(RepositoryInterface r, String[] includeCategories,
                                                     String[] excludeCategories, Pattern[] nodesFilter) {
        return mergeRepositories(r, includeCategories, excludeCategories, null, nodesFilter, false);
    }

    /**
     * Merges all pathways in a repository to create a meta-pathway
     *
     * @param r                 A pathway repository
     * @param includeCategories A set of pathway categories to use
     * @param excludeCategories A set of pathway categories to exclude from the merging
     * @param includePathways   A set of pathway Id to use
     * @param nodesFilter       A set of regular expression to filter nodes
     * @param disablePriority   Disable priority check when building pathway
     * @return A repository which contains the meta-pathway
     */
    public static MergedRepository mergeRepositories(RepositoryInterface r, String[] includeCategories,
                                                     String[] excludeCategories, String[] includePathways,
                                                     Pattern[] nodesFilter, boolean disablePriority) {
        PathwayMerger pm = new PathwayMerger();
        pm.init()
          .setParameter("repository", r)
          .setParameter("include", includeCategories)
          .setParameter("exclude", excludeCategories)
          .setParameter("includePathways", includePathways)
          .setParameter("nodesFilter", nodesFilter)
          .setParameter("disablePriority", disablePriority)
          .run();
        return pm.getOutput();
    }

}
