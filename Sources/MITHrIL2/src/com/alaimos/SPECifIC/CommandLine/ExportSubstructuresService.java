package com.alaimos.SPECifIC.CommandLine;

import com.alaimos.Commons.CommandLine.InputParametersException;
import com.alaimos.Commons.CommandLine.Options;
import com.alaimos.Commons.CommandLine.Service;
import com.alaimos.Commons.Math.PValue.Adjuster;
import com.alaimos.Commons.Math.PValue.Adjusters;
import com.alaimos.Commons.Math.PValue.EmpiricalBrowns.DataMatrix;
import com.alaimos.Commons.Math.PValue.EmpiricalBrowns.EmpiricalBrownsMethod;
import com.alaimos.Commons.Utils.Triple;
import com.alaimos.MITHrIL.Common;
import com.alaimos.MITHrIL.Data.Pathway.Impl.MergedRepository;
import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.PathwayInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.MITHrIL.Data.Reader.BinaryReader;
import com.alaimos.MITHrIL.Data.Reader.MITHrIL.MITHrILOutputReader;
import com.alaimos.MITHrIL.Data.Reader.RemoteSpeciesDatabaseReader;
import com.alaimos.MITHrIL.Data.Records.Species;
import com.alaimos.MITHrIL.Data.Records.Type.EvidenceType;
import com.alaimos.MITHrIL.Data.Results.PathwayAnalysisResult;
import com.alaimos.SPECifIC.Algorithm.OptimizedVisit;
import com.alaimos.SPECifIC.CommandLine.Options.ExportSubstructuresOptions;
import com.alaimos.SPECifIC.Data.Reader.ExpressionDataReader;
import com.alaimos.SPECifIC.Data.Structures.CommunitySubGraph;
import com.alaimos.SPECifIC.Data.Structures.InducedSubGraph;
import com.alaimos.SPECifIC.Data.Structures.VisitTree;
import com.alaimos.SPECifIC.Data.Writer.AllWriter;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 05/01/2016
 */
public class ExportSubstructuresService implements Service {

    /**
     * Filters used to remove all drugs and orthology groups in order to avoid biases during enrichment analysis
     */
    private static final Pattern[] NODES_FILTER = new Pattern[]{
            Pattern.compile("^dr:(.*)"),
            Pattern.compile("^ko:(.*)")
    };

    protected ExportSubstructuresOptions options = new ExportSubstructuresOptions();

    @Override
    public String getShortName() {
        return "exportstructs";
    }

    @Override
    public String getDescription() {
        return "Main SPECIFIC algorithm: exports all specific substructures found a meta-pathway.";
    }

    @Override
    public Options getOptions() {
        return options;
    }

    public synchronized void report(String s) {
        if (options.isVerbose()) {
            System.out.print(s);
        }
    }

    public synchronized void report(Exception e) {
        if (options.isVerbose()) {
            e.printStackTrace();
        }
    }

    public synchronized void reportln(String s) {
        report(s + "\n");
    }

    /**
     * Generate a map of common parameters for the DFS algorithm
     *
     * @param p  a pathway
     * @param pa pathway analysis output
     * @return the map of parameters
     */
    private Map<String, Object> generateCommonParametersMap(PathwayInterface p, PathwayAnalysisResult pa,
                                                            boolean backward) {
        HashMap<String, Object> par = new HashMap<>();
        par.put("pathway", p);
        par.put("pathwayAnalysisResult", pa);
        par.put("combiner", new EmpiricalBrownsMethod());
        par.put("maxPValueNodes", options.getMaxPValueNodes());
        par.put("maxPValuePaths", options.getMaxPValuePath());
        par.put("minNumberOfNodes", options.getMinNumberOfNodes());
        par.put("reversed", backward);
        return par;
    }

    private VisitTree[] visitPathway(List<NodeInterface> nodeOfInterests, PathwayInterface mp,
                                     PathwayAnalysisResult pa, boolean backward) {
        report("Running DFS visit " + ((backward) ? "(Backward visit)" : "(Forward visit)"));
        Map<String, Object> commonPars = generateCommonParametersMap(mp, pa, backward);
        VisitTree[] trees = nodeOfInterests.stream().map(n -> {
            OptimizedVisit dfs = new OptimizedVisit();
            dfs.init().setParameters(commonPars)
               .setParameter("startingNode", n)
               .run();
            report(".");
            VisitTree tree = dfs.getOutput();
            if (tree == null) return null;
            return tree.setCombiner(new EmpiricalBrownsMethod());
        }).filter(Objects::nonNull).toArray(VisitTree[]::new);
        reportln("...OK!");
        if (trees.length <= 0) {
            reportln("No output found");
            System.exit(103);
        }
        return trees;
    }

    @Nullable
    private InducedSubGraph[] makeInducedSubgraphs(VisitTree[] trees, PathwayInterface mp) {
        if (options.isNoInduced()) {
            return null;
        }
        report("Building induced subgraphs");
        InducedSubGraph[] isg = Arrays.stream(trees)
                                      .map(t -> new InducedSubGraph(t, mp))
                                      .toArray(InducedSubGraph[]::new);
        reportln("...OK!");
        return isg;
    }

    @Nullable
    private CommunitySubGraph makeCommunity(VisitTree[] trees, PathwayInterface mp) {
        if (options.isNoCommunities()) {
            return null;
        }
        CommunitySubGraph csg = null;
        if (trees.length >= 2) {
            report("Building communities");
            csg = new CommunitySubGraph(trees, mp);
            reportln("...OK!");
        }
        return csg;
    }

    @SuppressWarnings("unchecked")
    private void exportAll(VisitTree[] trees, InducedSubGraph[] sgs, CommunitySubGraph csg) {
        if (options.getOutput() != null) {
            report("Exporting Results..");
            Adjuster pvAdjuster = Adjusters.getByName(options.getPValueAdjuster());
            new AllWriter(options.getMinNumberOfNodes(), options.getMaxPValuePath(), pvAdjuster, options)
                    .write(options.getOutput(), new Triple<>(trees, sgs, csg));
            reportln(".OK!");
        }
    }

    /**
     * Read pathway repository
     *
     * @param s species definition
     * @return the repository
     */
    protected RepositoryInterface getPathwayRepository(Species s) {
        return Common.getPathwayRepository(s, false, EvidenceType.STRONG, false, null, this::report);
    }


    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        try {
            if (!options.getMainInput().exists()) {
                throw new InputParametersException("Invalid main input file: file does not exist.");
            }
            if (!options.getDataFile().exists()) {
                throw new InputParametersException("Invalid expression data file: file does not exist.");
            }
            if (!options.isBinaryInput() && (options.getPerturbationsInput() == null ||
                    (options.getPerturbationsInput() != null && !options.getPerturbationsInput().exists()))) {
                throw new InputParametersException("Invalid perturbations file: file does not exist.");
            }
            report("Reading species database");
            RemoteSpeciesDatabaseReader speciesDbReader = RemoteSpeciesDatabaseReader.getInstance();
            HashMap<String, Species> db = speciesDbReader.readSpecies();
            reportln("...OK!");
            if (!db.containsKey(options.getOrganism())) {
                throw new InputParametersException("Invalid species: species not found.");
            }
            Species s = db.get(options.getOrganism());
            report("Reading pathways for " + s.getName());
            RepositoryInterface r = getPathwayRepository(s);
            r.setDefaultWeightComputation();
            reportln("...OK!");
            report("Reading input files");
            PathwayAnalysisResult pa = null;
            if (options.isBinaryInput()) {
                try {
                    pa = new BinaryReader<>(PathwayAnalysisResult.class).read(options.getMainInput());
                    if (pa == null) throw new NullPointerException("Unable to read pathway analysis output");
                } catch (Exception e) {
                    reportln("...ERROR!!\n");
                    report(e);
                    System.exit(102);
                }
            } else {
                pa = new MITHrILOutputReader(options.getMainInput(), options.getPerturbationsInput()).read();
            }
            if (pa == null) {
                System.exit(102);
            }
            reportln("...OK!");
            report("Merging pathways");
            MergedRepository rm =
                    Common.mergeRepositories(r, options.getIncludeCategories(), options.getExcludeCategories(),
                                             NODES_FILTER);
            PathwayInterface mp = rm.getPathway();
            reportln("...OK!");
            report("Reading expression/perturbation data");
            DataMatrix expressions = new ExpressionDataReader().read(options.getDataFile());
            reportln("...OK!");
            EmpiricalBrownsMethod.setDefaultDataMatrix(expressions);
            report("Collecting Results for merged pathway");
            Map<String, Double> mergedPValues =
                    pa.getNodePValues().entrySet().stream().flatMap(e -> e.getValue().entrySet().stream())
                      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Double::min));
            Map<String, Double> mergedPerturbations =
                    pa.getPerturbations().entrySet().stream().flatMap(e -> e.getValue().entrySet().stream())
                      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Double::max));
            Map<String, Double> mergedNodeAccumulators =
                    pa.getNodeAccumulators().entrySet().stream().flatMap(e -> e.getValue().entrySet().stream())
                      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Double::max));
            pa.getNodePValues().put(mp.getId(), mergedPValues);
            pa.getPerturbations().put(mp.getId(), mergedPerturbations);
            pa.getNodeAccumulators().put(mp.getId(), mergedNodeAccumulators);
            reportln("...OK!");
            List<NodeInterface> nodeOfInterests;
            if (options.getNodesOfInterest() == null) {
                report("Collecting pathways");
                final Map<String, Double> pValues = pa.getAdjustedPValues();
                PathwayInterface[] pathways = r.stream()
                                               .filter(p -> pValues.getOrDefault(p.getId(), 0.0) <=
                                                       options.getMaxPValuePathways())
                                               .toArray(PathwayInterface[]::new);
                if (pathways.length <= 0) {
                    reportln("...No significant pathways found!");
                    System.exit(104);
                } else {
                    reportln("...Found " + pathways.length + " pathways.");
                }
                report("Collecting preliminary set of NoIs");
                nodeOfInterests = Arrays.stream(pathways)
                                        .flatMap(p -> p.getGraph().getNodes().values().stream())
                                        .distinct()
                                        .filter(n -> mp.getGraph().hasNode(n.getId()))
                                        .filter(n -> mergedPValues.getOrDefault(n.getId(), 1.0) <=
                                                options.getMaxPValueNoIs())
                                        .collect(Collectors.toList());
                reportln("...Found " + nodeOfInterests.size() + " nodes.");
            } else {
                nodeOfInterests = options.getNodesOfInterest().stream().map(nId -> mp.getGraph().getNode(nId))
                                         .filter(Objects::nonNull).collect(Collectors.toList());
            }
            if (nodeOfInterests.size() <= 0) {
                reportln("No valid nodes of interest specified");
                System.exit(101);
            }
            VisitTree[] trees = visitPathway(nodeOfInterests, mp, pa, options.isBackwardVisit());
            InducedSubGraph[] sgs = makeInducedSubgraphs(trees, mp);
            CommunitySubGraph csg = makeCommunity(trees, mp);
            exportAll(trees, sgs, csg);
            reportln("Duplicate counter: " + VisitTree.duplicateCounter);
            reportln("Written structures without duplicates: " + AllWriter.writtenCount);
        } catch (Exception e) {
            e.printStackTrace();
            reportln(e.getMessage());
            System.exit(103);
        }
    }
}
