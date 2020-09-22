package com.alaimos.PHENSIM.Algorithm;

import com.alaimos.Commons.Algorithm.Impl.AbstractAlgorithm;
import com.alaimos.Commons.Algorithm.Interface.AlgorithmInterface;
import com.alaimos.Commons.Utils.Triple;
import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.PathwayInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.PHENSIM.Data.Generator.ExpressionConstraint;
import com.alaimos.PHENSIM.Data.Generator.RandomExpressionGenerator;
import com.alaimos.PHENSIM.Data.Generator.RandomSubsetGenerator;
import com.alaimos.PHENSIM.Data.Results.PhensimRun;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the PHENSIM algorithm
 * <p>
 * <ul>
 * <li>Parameter <code>simulateOn</code> of type <code>List&lt;String&gt;</code>.</li>
 * <li>Parameter <code>constraints</code> of type <code>Map&lt;String, ExpressionConstraint&gt;</code>.</li>
 * <li>Parameter <code>repository</code> of type <code>RepositoryInterface</code>.</li>
 * <li>Optional parameter <code>random</code> of type <code>Random</code></li>
 * <li>Optional parameter <code>nonExpressedNodes</code> of type <code>List&lt;String&gt;</code></li>
 * <li>Optional parameter <code>numberOfRepetitions</code> of type <code>Integer</code></li>
 * <li>Optional parameter <code>epsilon</code> of type <code>Double</code></li>
 * <li>Optional parameter <code>threads</code> of type <code>Integer</code></li>
 * </ul>
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.0
 * @since 19/08/2020
 */
public class PHENSIM extends AbstractAlgorithm<PhensimRun> {

    //region Input Parameters
    protected RepositoryInterface repository;
    protected double epsilon;
    protected int numberOfRepetitions;
    protected int numberOfSimulationsFirst;
    protected int numberOfSimulations;
    protected List<String> simulateOn;
    protected Map<String, ExpressionConstraint> constraints;
    protected List<String> nonExpressedNodes;
    protected Random random;
    protected int threads;
    //endregion

    public PHENSIM() {
    }

    /**
     * Prepares the workflow for a new execution
     *
     * @return This object for a fluent interface
     */
    @Override
    public AlgorithmInterface<PhensimRun> init() {
        super.init();
        repository = null;
        epsilon = 0.001;
        numberOfRepetitions = 1000;
        numberOfSimulationsFirst = 1000;
        numberOfSimulations = 100;
        simulateOn = null;
        constraints = null;
        random = null;
        nonExpressedNodes = null;
        output = null;
        return this;
    }

    /**
     * Clone an ExpressionConstraint map changing its keys with a new random set
     *
     * @param original the original map
     * @param origKey  the original set of keys
     * @param newKey   the new set of keys
     * @return the cloned map
     */
    @NotNull
    private <T> Map<String, T> cloneAndChangeKeys(Map<String, T> original,
                                                  @NotNull List<String> origKey,
                                                  @NotNull List<String> newKey) {
        assert origKey.size() == newKey.size();
        var newMap = new HashMap<String, T>();
        for (var i = 0; i < origKey.size(); i++) {
            var kOld = origKey.get(i);
            var kNew = newKey.get(i);
            if (original.containsKey(kOld)) {
                newMap.put(kNew, original.get(kOld));
            }
        }
        return newMap;
    }

    /**
     * Compute the minimum degree of the constraints list
     *
     * @return the minimum degree
     */
    private int minDegree() {
        return repository.stream().filter(PathwayInterface::hasGraph).mapToInt(p -> {
            var g = p.getGraph();
            return g.getNodesStream()
                    .filter(n -> constraints.containsKey(n.getId()))
                    .mapToInt(g::outDegree)
                    .min()
                    .orElse(Integer.MAX_VALUE);
        }).min().orElse(0);
    }

    /**
     * Generate a list of all nodes with a degree greater or equal than the specified value
     * If the minimum degree is > 0 and no nodes are found, returns the list of all nodes
     *
     * @param minDegree the minimum degree
     * @param count     the counter to track recursive call avoiding stack overflows
     * @return the list of nodes
     */
    @NotNull
    @Contract("_,_ -> new")
    private List<String> makeAllNodesList(int minDegree, int count) {
        var allNodes = new HashSet<String>();
        for (var p : repository) {
            if (p.hasGraph()) {
                var g = p.getGraph();
                for (var n : g.getNodes().values()) {
                    if (g.outDegree(n) >= minDegree) {
                        allNodes.add(n.getId());
                    }
                }
            }
        }
        var s = numberOfRepetitions + 1;
        if (count < 100 && allNodes.size() < s && minDegree > 0) {
            return makeAllNodesList(Math.max(0, minDegree - 1), count + 1);
        } else if (count == 100 && minDegree > 0) {
            return makeAllNodesList(0, count + 1);
        } else {
            return new ArrayList<>(allNodes);
        }
    }

    /**
     * Generate a list of all nodes with a degree greater or equal than the specified value
     * If the minimum degree is > 0 and no nodes are found, returns the list of all nodes
     *
     * @param minDegree the minimum degree
     * @return the list of nodes
     */
    @NotNull
    @Contract("_ -> new")
    private List<String> makeAllNodesList(int minDegree) {
        return makeAllNodesList(minDegree, 0);
    }

    /**
     * Generate a random constraints object
     *
     * @param randomGenerator a random subset generator
     * @return the random constraints object
     */
    @NotNull
    private Map<String, ExpressionConstraint> makeRandomConstraints(@NotNull RandomSubsetGenerator randomGenerator) {
        var size = constraints.size();
        var oldSet = new ArrayList<>(constraints.keySet());
        var newSet = randomGenerator.getRandomSubset(size);
        return cloneAndChangeKeys(constraints, oldSet, newSet);
    }

    /**
     * Returns a random number generator to be used in a single thread
     *
     * @return the new random number generator
     */
    @NotNull
    @Contract(" -> new")
    private Random makeRandomGenerator() {
        return new Random(random.nextLong());
    }

    /**
     * Checks if two lists contain the same elements
     *
     * @param first  the first list
     * @param second the second list
     * @return a boolean indicating whether the two lists are equal
     */
    private boolean checkListEquals(@NotNull List<String> first, @NotNull List<String> second) {
        if (first.size() != second.size()) return false;
        var equals = true;
        for (var s : first) {
            equals = equals && second.contains(s);
        }
        return equals;
    }

    /**
     * Given a list and a collection of lists, it checks whether there is any element of the collection that is equal to
     * the list. If the collection is empty it returns <code>false</code>.
     *
     * @param first  a list of strings
     * @param second a collection of list of strings
     * @return the result
     */
    private boolean checkCollectionContainsList(@NotNull List<String> first, @NotNull Collection<List<String>> second) {
        var equals = false;
        for (var s : second) {
            equals = equals || checkListEquals(first, s);
        }
        return equals;
    }

    /**
     * Make the input array for the simulation threads
     *
     * @param threadRandomGenerator the random number generator for this thread
     * @return the input array
     */
    @NotNull
    private Triple<List<String>, Map<String, ExpressionConstraint>, Long>[] makeInputArray(Random threadRandomGenerator) {
        var minDegree = minDegree();
        var nodesList = makeAllNodesList(minDegree);
        var randSubset = new RandomSubsetGenerator(threadRandomGenerator).setNodes(nodesList);
        var n = numberOfRepetitions + 1;
        @SuppressWarnings("unchecked")
        var inputs = (Triple<List<String>, Map<String, ExpressionConstraint>, Long>[]) new Triple[n];
        var allChosenSets = new ArrayList<List<String>>();
        inputs[0] = new Triple<>(simulateOn, constraints, random.nextLong());
        allChosenSets.add(simulateOn);
        for (var i = 1; i < n; i++) {
            Map<String, ExpressionConstraint> randomConstraints;
            List<String> randomNodes;
            var j = 0;
            do {
                randomConstraints = makeRandomConstraints(randSubset);
                randomNodes = new ArrayList<>(randomConstraints.keySet());
                j++;
            } while (checkCollectionContainsList(randomNodes, allChosenSets) && j < n);
            allChosenSets.add(randomNodes);
            inputs[i] = new Triple<>(randomNodes, randomConstraints, random.nextLong());
        }
        allChosenSets.clear();
        return inputs;
    }

    /**
     * Runs PHENSIM algorithm.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        simulateOn = (List<String>) getParameterNotNull("simulateOn", List.class);
        constraints = (Map<String, ExpressionConstraint>) getParameterNotNull("constraints", Map.class);
        repository = getParameterNotNull("repository", RepositoryInterface.class);
        random = getOptionalParameter("random", Random.class).orElse(new Random());
        nonExpressedNodes =
                (List<String>) getOptionalParameter("nonExpressedNodes", List.class).orElse(Collections.EMPTY_LIST);
        numberOfRepetitions = getOptionalParameter("numberOfRepetitions", Integer.class).orElse(1000);
        numberOfSimulationsFirst = getOptionalParameter("numberOfSimulationsFirst", Integer.class).orElse(1000);
        numberOfSimulations = getOptionalParameter("numberOfSimulations", Integer.class).orElse(100);
        epsilon = getOptionalParameter("epsilon", Double.class).orElse(0.001);
        threads = getOptionalParameter("threads", Integer.class).orElse(1);
        notifyObservers("phensimInitializing");
        output = new PhensimRun(repository, numberOfRepetitions, numberOfSimulationsFirst, numberOfSimulations);
        var threadRandomGenerator = makeRandomGenerator();
        var n = numberOfRepetitions + 1;
        var inputs = makeInputArray(threadRandomGenerator);
        var executor = Executors.newFixedThreadPool(threads);
        notifyObservers("phensimInitialized");
        FastSimulationStep.clearSortedNodes(repository);
        notifyObservers("phensimStarting", output.getTotalNumberOfSimulations());
        for (var i = 0; i < n; i++) {
            var input = inputs[i];
            var results = output.getSimulation(i);
            var nSim = (i == 0) ? numberOfSimulationsFirst : numberOfSimulations;
            executor.submit(() -> {
                try {
                    var generator = new RandomExpressionGenerator(new Random(input.getThird()), epsilon)
                            .setNodes(input.getFirst())
                            .setConstraints(input.getSecond());
                    new FastSimulationStep(generator, epsilon, nonExpressedNodes, results,
                            () -> notifyObservers("phensimSimulationStepIterationDone")).run();
                    notifyObservers("phensimSimulationStepDone", nSim);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            output.setDirectTargets(simulateOn);
            notifyObservers("phensimDone");
        } catch (InterruptedException e) {
            notifyObservers("phensimError", e);
        }
    }

}
