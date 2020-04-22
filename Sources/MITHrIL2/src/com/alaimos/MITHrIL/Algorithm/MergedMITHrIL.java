package com.alaimos.MITHrIL.Algorithm;

import com.alaimos.Commons.Algorithm.AlgorithmExecutionException;
import com.alaimos.Commons.Algorithm.Interface.AlgorithmInterface;
import com.alaimos.Commons.Math.PValue.Adjuster;
import com.alaimos.Commons.Math.PValue.Adjusters;
import com.alaimos.Commons.Math.PValue.Combiner;
import com.alaimos.Commons.Math.PValue.Combiners;
import com.alaimos.MITHrIL.Data.Pathway.Impl.MergedRepository;
import com.alaimos.MITHrIL.Data.Pathway.Interface.GraphInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.PathwayInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.MITHrIL.Data.Results.PathwayAnalysisResult;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 31/12/2015
 */
public class MergedMITHrIL extends MITHrIL {

    private PathwayInterface mergedPathway;
    private MergedRepository mergedRepository;


    public MergedMITHrIL() {
    }

    /**
     * Compute perturbation of a node in a pathway
     *
     * @param n A node
     * @return The perturbation
     */
    protected double perturbation(NodeInterface n) {
        return perturbations.get(mergedPathway.getId()).getOrDefault(n.getId(), 0.0);
    }

    /**
     * Computes the impact factor of a pathway
     *
     * @param p the pathway
     * @return the impact factor
     */
    @Override
    protected double impactFactor(PathwayInterface p) {
        if (impactFactors.containsKey(p.getId())) {
            return impactFactors.get(p.getId());
        }
        Supplier<Stream<Map.Entry<String, Double>>> stream = () -> expressions.entrySet().stream()
                                                                              .filter(diffExpFilteringPredicate);
        double cif = p.getGraph().getNodes().values().stream().mapToDouble(n -> Math.abs(perturbation(n))).sum(),
                deGenesPathway = intersect(stream.get().map(Map.Entry::getKey), p).size(),
                meanDE = intersectMap(stream.get(), p).map(Map.Entry::getValue).map(Math::abs)
                                                      .collect(Collectors.averagingDouble(Double::doubleValue)),
                pi = Math.log(probability(p));
        if ((meanDE * deGenesPathway) == 0.0) {
            impactFactors.put(p.getId(), 0.0);
            return 0.0;
        }
        cif /= (meanDE * deGenesPathway);
        cif -= pi;
        impactFactors.put(p.getId(), cif);
        return cif;
    }

    /**
     * Computes the total perturbation accumulation of a pathway
     *
     * @param p the pathway
     * @return the accumulation
     */
    @Override
    protected double accumulation(PathwayInterface p) {
        if (accumulators.containsKey(p.getId())) {
            return accumulators.get(p.getId());
        }
        double a = 0.0;
        for (NodeInterface v : p.getGraph().getNodes().values()) {
            a += v.getType().sign() * (perturbation(v) - getExpression(v));
        }
        accumulators.put(p.getId(), a);
        return a;
    }

    /**
     * Init new MITHrIL Computation object
     *
     * @param expChange an array of random log-fold-changes
     * @param nodes     a list of nodes
     * @param pathways  a list of pathways
     * @return a new instance of this object
     */
    @Override
    protected MITHrIL initSelf(double[] expChange, List<String> nodes, List<PathwayInterface> pathways) {
        MergedMITHrIL tmp;
        try {
            tmp = getClass().getDeclaredConstructor().newInstance();
            tmp.init();
            tmp.mergedRepository = mergedRepository;
            tmp.mergedPathway = mergedPathway;
            tmp.random = random;
            tmp.expressions = permuteExpressionMap(expChange, nodes);
            tmp.repository = repository;
            tmp.internalRun(pathways);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new AlgorithmExecutionException(e);
        }
        return tmp;
    }

    /**
     * Increment counters for p-value computation
     *
     * @param p                a pathway
     * @param tmp              a temporaty mithril instance
     * @param i                the current iteration index
     * @param randAccumulators the map of random accumulators
     */
    @Override
    protected void incrementCounters(PathwayInterface p, MITHrIL tmp, int i,
                                     HashMap<String, double[]> randAccumulators) {
        String pId = p.getId(), mpId = mergedPathway.getId();
        GraphInterface g = p.getGraph();
        randAccumulators.get(pId)[i] = tmp.accumulators.get(pId);
        incrementNodesCounters(g, tmp, pId, mpId);
    }

    /**
     * Prepares the workflow for a new execution
     *
     * @return This object for a fluent interface
     */
    @Override
    public AlgorithmInterface<PathwayAnalysisResult> init() {
        super.init();
        mergedPathway = null;
        mergedRepository = null;
        return this;
    }

    /**
     * Run method used internally in pValue computation
     */
    protected void internalRun(List<PathwayInterface> pathways) {
        this.probabilityComputation = this::defaultProbabilityComputation;
        singlePathwayRun(mergedPathway);
        for (PathwayInterface p : pathways) {
            this.accumulation(p);
        }
    }

    /**
     * Runs MITHrIL algorithm.
     * <p>
     * Requires parameter <code>expressions</code> of type <code>HashMap&lt;String, Double&gt;</code>.</p>
     * <p>
     * Requires parameter <code>repository</code> of type <code>MergedRepository</code>.</p>
     * <p>
     * Requires parameter <code>originalRepository</code> of type <code>RepositoryInterface</code>.</p>
     * <p>
     * Requires parameter <code>random</code> of type <code>Random</code>.</p>
     * <p>
     * Optional parameter <code>numberOfRepetitions</code> of type <code>int</code>.</p>
     * <p>
     * Optional parameter <code>probabilityComputation</code> of type <code>ProbabilityComputationInterface</code>.</p>
     * <p>
     * Optional parameter <code>pValueCombiner</code> of type <code>com.alaimos.Commons.Math.PValue.Combiner</code>.</p>
     * <p>
     * Optional parameter <code>pValueAdjuster</code> of type <code>com.alaimos.Commons.Math.PValue.Adjuster</code>.</p>
     */
    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        expressions = (Map<String, Double>) getParameterNotNull("expressions", Map.class);
        mergedRepository = getParameterNotNull("repository", MergedRepository.class);
        mergedPathway = mergedRepository.getPathway();
        repository = getParameterNotNull("originalRepository", RepositoryInterface.class);
        random = getParameterNotNull("random", Random.class);
        numberOfRepetitions = getOptionalParameter("numberOfRepetitions", Integer.class).orElse(2001);
        probabilityComputation = getOptionalParameter("probabilityComputation", ProbabilityComputationInterface.class)
                .orElse(this::defaultProbabilityComputation);
        pValueCombiner = getOptionalParameter("pValueCombiner", Combiner.class).orElse(Combiners::stouffer);
        pValueAdjuster = getOptionalParameter("pValueAdjuster", Adjuster.class).orElse(Adjusters::benjaminiHochberg);
        notifyObservers("startingMITHrILComputation");
        singlePathwayRun(mergedPathway);
        for (PathwayInterface p : repository) {
            this.singlePathwayRun(p);
        }
        notifyObservers("computingPValues");
        pValues();
        notifyObservers("adjustingPValues");
        adjustPValues();
        notifyObservers("doneMITHrILComputation");
    }

}
