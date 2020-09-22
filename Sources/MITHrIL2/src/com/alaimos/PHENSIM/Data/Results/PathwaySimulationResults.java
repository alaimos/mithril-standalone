package com.alaimos.PHENSIM.Data.Results;

import com.alaimos.Commons.Math.PValue.Adjusters;
import com.alaimos.Commons.Utils.Utils;
import com.alaimos.MITHrIL.Data.Pathway.Interface.GraphInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.PathwayInterface;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.stat.inference.TTest;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Container of single pathway simulation data
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.0
 * @since 19/08/2020
 */
public class PathwaySimulationResults {

    public enum Computation {
        ACTIVITY,
        PRIOR
    }

    public enum State {
        ACTIVE(1),
        INHIBITED(-1),
        OTHERWISE(0);

        private final double value;

        State(double value) {
            this.value = value;
        }

        @Contract(pure = true)
        public double getValue() {
            return value;
        }

        @Nullable
        @Contract(pure = true)
        public static State fromValue(double value) {
            value = Math.round(value);
            if (value == OTHERWISE.value) {
                return OTHERWISE;
            } else if (value == ACTIVE.value) {
                return ACTIVE;
            } else if (value == INHIBITED.value) {
                return INHIBITED;
            }
            return null;
        }
    }

    private static class MyTTest extends TTest {

        MyTTest() {
            super();
        }

        @Override
        public double tTest(double m1, double m2, double v1, double v2, double n1, double n2)
                throws MaxCountExceededException, NotStrictlyPositiveException {
            return super.tTest(m1, m2, v1, v2, n1, n2);
        }
    }

    /**
     * X^2 distribution with one degree of freedom used to compute p-values.
     */
    private static final ChiSquaredDistribution chiSquaredDistribution = new ChiSquaredDistribution(1);

    private static final MyTTest TT = new MyTTest();

    private static final String PATHWAY_KEY = "___PATHWAY___";

    private String pathwayId;
    private HashSet<String> allNodes;
    private Map<Computation, Map<State, Map<String, Integer>>> counters;
    private Map<Computation, Map<State, Map<String, Double>>> probabilities;
    private Map<Computation, Map<String, Double>> expected;
    private Map<Computation, Map<String, Double>> variance;
    private Map<String, Double> pValues;
    private final Map<Computation, Map<State, Integer>> pathwayCounters;
    private Map<Computation, Map<State, Double>> pathwayProbabilities;
    private int numberOfIterations;
    private double minProb;
    private final List<String> directTargets;
    private HashMap<String, List<String>> directlyTargetedBy;
    private boolean probabilitiesComputed = false;
    private boolean pValuesComputed = false;

    public PathwaySimulationResults(String pathwayId, int numberOfIterations) {
        this.numberOfIterations = numberOfIterations;
        this.pathwayId = pathwayId;
        this.counters = initCounters();
        this.pathwayCounters = initPathwayCounters();
        this.allNodes = null;
        this.minProb = 0.5 / ((double) numberOfIterations);
        this.directTargets = new ArrayList<>();
        this.directlyTargetedBy = new HashMap<>();
    }

    /**
     * Initialize counters
     *
     * @return a new counters object
     */
    @Contract(" -> new")
    @NotNull
    private @Unmodifiable Map<Computation, Map<State, Map<String, Integer>>> initCounters() {
        return Map.of(
                Computation.ACTIVITY, initStateCounter(),
                Computation.PRIOR, initStateCounter()
        );
    }

    @NotNull
    private <T> @Unmodifiable Map<Computation, Map<State, T>> initPathwayCounters() {
        return Map.of(
                Computation.ACTIVITY, new HashMap<>(),
                Computation.PRIOR, new HashMap<>()
        );
    }

    /**
     * Initialize Single Computation Counters
     *
     * @return a new counters object
     */
    @NotNull
    private <T> @Unmodifiable Map<State, Map<String, T>> initStateCounter() {
        return Map.of(
                State.ACTIVE, new HashMap<>(),
                State.INHIBITED, new HashMap<>(),
                State.OTHERWISE, new HashMap<>()
        );
    }

    /**
     * Compute Expected Value and Variance for a single node
     *
     * @param node
     */
    private void singleExpectedAndVarianceComputation(String node) {
        double pAA = getProbability(node, Computation.ACTIVITY, State.ACTIVE),
                pAI = getProbability(node, Computation.ACTIVITY, State.INHIBITED),
                pAO = getProbability(node, Computation.ACTIVITY, State.OTHERWISE),
                pPA = getProbability(node, Computation.PRIOR, State.ACTIVE),
                pPI = getProbability(node, Computation.PRIOR, State.INHIBITED),
                pPO = getProbability(node, Computation.PRIOR, State.OTHERWISE),
                N = numberOfIterations;
        if (node == null) {
            node = PATHWAY_KEY;
        }
        double exp = pAA * State.ACTIVE.value + pAI * State.INHIBITED.value + pAO * State.OTHERWISE.value, //Expected value
                expR = pPA * State.ACTIVE.value + pPI * State.INHIBITED.value + pPO * State.OTHERWISE.value; //Expected value of null model
        double c = ((pAA * N) * (State.ACTIVE.value - exp) + (pAI * N) * (State.INHIBITED.value - exp) +
                (pAO * N) * (State.OTHERWISE.value - exp)) / N, //Unbiased correction for expected value
                cR = ((pPA * N) * (State.ACTIVE.value - expR) + (pPI * N) * (State.INHIBITED.value - expR) +
                        (pPO * N) * (State.OTHERWISE.value - expR)) / N; //Unbiased correction for expected value of null model
        exp = exp + c; //unbiased expected value
        expR = expR + cR; //unbiased expected value of random model
        expected.get(Computation.ACTIVITY).put(node, exp);
        expected.get(Computation.PRIOR).put(node, expR);
        double var = pAA * Math.pow(State.ACTIVE.value - exp, 2) + pAI * Math.pow(State.INHIBITED.value - exp, 2) +
                pAO * Math.pow(State.OTHERWISE.value - exp, 2), //Variance
                varR = pPA * Math.pow(State.ACTIVE.value - expR, 2) + pPI * Math.pow(State.INHIBITED.value - expR, 2) +
                        pPO * Math.pow(State.OTHERWISE.value - expR, 2); //Variance of null model
        var = (var * N) / (N - 1.0); //unbiased variance
        varR = (varR * N) / (N - 1.0); //unbiased variance of null model
        variance.get(Computation.ACTIVITY).put(node, var);
        variance.get(Computation.PRIOR).put(node, varR);
    }

    /**
     * Compute Expected Value and Variance for all nodes
     */
    private void computeExpectedAndVariance() {
        expected = Map.of(Computation.ACTIVITY, new HashMap<>(), Computation.PRIOR, new HashMap<>());
        variance = Map.of(Computation.ACTIVITY, new HashMap<>(), Computation.PRIOR, new HashMap<>());
        Set<String> nodes = getAllNodes();
        for (String node : nodes) {
            singleExpectedAndVarianceComputation(node);
        }
        singleExpectedAndVarianceComputation(null);
    }

    /**
     * Compute activity probabilities
     */
    private void computeProbabilities() {
        if (!probabilitiesComputed) {
            allNodes = null;
            Set<String> nodes = getAllNodes();
            probabilities = Map.of(Computation.ACTIVITY, initStateCounter(), Computation.PRIOR, initStateCounter());
            for (String n : nodes) {
                for (Computation c : new Computation[]{Computation.ACTIVITY, Computation.PRIOR}) {
                    double cA = getCounter(n, c, State.ACTIVE) + 0.00001,
                            cI = getCounter(n, c, State.INHIBITED) + 0.00001,
                            cO = getCounter(n, c, State.OTHERWISE) + 0.00001,
                            sum = cA + cI + cO;
                    probabilities.get(c).get(State.ACTIVE).put(n, cA / sum);
                    probabilities.get(c).get(State.INHIBITED).put(n, cI / sum);
                    probabilities.get(c).get(State.OTHERWISE).put(n, cO / sum);
                }
            }
            pathwayProbabilities = initPathwayCounters();
            for (Computation c : new Computation[]{Computation.ACTIVITY, Computation.PRIOR}) {
                double cA = getCounter(null, c, State.ACTIVE) + 0.00001,
                        cI = getCounter(null, c, State.INHIBITED) + 0.00001,
                        cO = getCounter(null, c, State.OTHERWISE) + 0.00001,
                        sum = cA + cI + cO;
                pathwayProbabilities.get(c).put(State.ACTIVE, cA / sum);
                pathwayProbabilities.get(c).put(State.INHIBITED, cI / sum);
                pathwayProbabilities.get(c).put(State.OTHERWISE, cO / sum);
            }
            probabilitiesComputed = true;
            computeExpectedAndVariance();
        }
    }

    /**
     * Get pathway id
     *
     * @return the pathway id
     */
    public String getPathwayId() {
        return pathwayId;
    }

    /**
     * Set pathway id
     *
     * @param pathwayId the pathway id
     * @return this object for a fluent interface
     */
    public PathwaySimulationResults setPathwayId(String pathwayId) {
        this.pathwayId = pathwayId;
        return this;
    }

    public int getNumberOfIterations() {
        return numberOfIterations;
    }

    public PathwaySimulationResults setNumberOfIterations(int numberOfIterations) {
        this.numberOfIterations = numberOfIterations;
        this.minProb = Math.log(0.5 / ((double) numberOfIterations));
        return this;
    }

    /**
     * Reset all counters
     *
     * @return this object for a fluent interface
     */
    public PathwaySimulationResults resetCounters() {
        this.counters = initCounters();
        probabilitiesComputed = false;
        pValuesComputed = false;
        return this;
    }

    /**
     * Increment an integer counter inside a map
     *
     * @param map the map
     * @param key the key
     * @param <T> the type of the key
     */
    protected synchronized <T> void increment(Map<T, Integer> map, T key) {
        probabilitiesComputed = false;
        map.put(key, 1 + (map.getOrDefault(key, 0)));
    }

    /**
     * Increment counter for a node
     *
     * @param node        a node
     * @param computation a computation type
     * @param state       a node state
     * @return this object for a fluent interface
     */
    public PathwaySimulationResults incrementCounter(@Nullable String node, Computation computation, State state) {
        if (node == null) {
            increment(pathwayCounters.get(computation), state);
        } else {
            increment(counters.get(computation).get(state), node);
        }
        return this;
    }

    /**
     * Get the counter for a node
     *
     * @param node        a node
     * @param computation the type of computation
     * @param state       the state of the node
     * @return the counter
     */
    public int getCounter(@Nullable String node, Computation computation, State state) {
        if (node == null) {
            return pathwayCounters.get(computation).getOrDefault(state, 0);
        } else {
            return counters.get(computation).get(state).getOrDefault(node, 0);
        }
    }

    /**
     * Gets the total number of iterations as a double
     *
     * @return the total number of iterations
     */
    protected double numberOfIterations() {
        return numberOfIterations;
    }

    /**
     * Gets all the counters for a state of a computation
     *
     * @param computation the type of computation
     * @param state       the state of the node
     * @return the counter
     */
    public Map<String, Integer> getCounters(Computation computation, State state) {
        return counters.get(computation).get(state);
    }

    /**
     * Gets the probability of a node
     *
     * @param node        the node
     * @param computation the type of computation
     * @param state       the state of the node
     * @return the probability
     */
    public double getProbability(@Nullable String node, Computation computation, State state) {
        if (!probabilitiesComputed) computeProbabilities();
        if (node == null) {
            return pathwayProbabilities.get(computation).getOrDefault(state, minProb);
        } else {
            return probabilities.get(computation).get(state).getOrDefault(node, minProb);
        }
    }

    /**
     * Gets the log probability of a node
     *
     * @param node        the node
     * @param computation the type of computation
     * @param state       the state of the node
     * @return the probability
     */
    public double getLogProbability(@Nullable String node, Computation computation, State state) {
        if (!probabilitiesComputed) computeProbabilities();
        if (node == null) {
            return Math.log(pathwayProbabilities.get(computation).getOrDefault(state, minProb));
        } else {
            return Math.log(probabilities.get(computation).get(state).getOrDefault(node, minProb));
        }
    }

    public double getLogInverseProbability(@Nullable String node, Computation computation, State state) {
        if (!probabilitiesComputed) computeProbabilities();
        if (node == null) {
            return Math.log(1.0 - pathwayProbabilities.get(computation).getOrDefault(state, minProb));
        } else {
            return Math.log(1.0 - probabilities.get(computation).get(state).getOrDefault(node, minProb));
        }
    }

    /**
     * Compute log-likelihood for a node
     *
     * @param node  the node
     * @param state the state of the node
     * @return the log-likelihood
     */
    public double getLikelihoodRatio(@Nullable String node, State state) {
        return getLogProbability(node, Computation.ACTIVITY, state) -
                getLogProbability(node, Computation.PRIOR, state);
    }

    /**
     * Compute activity score for a node
     *
     * @return the activity score
     */
    public double getPathwayActivityScore() {
        var a = getLikelihoodRatio(null, State.ACTIVE);
        var i = getLikelihoodRatio(null, State.INHIBITED);
        var o = getLikelihoodRatio(null, State.OTHERWISE);
        if (a > i && a > o) {
            return a;
        } else if (i > a && i > o) {
            return -i;
        }
        return 0;
    }

    /**
     * Compute activity score for a node
     *
     * @param node the node
     * @return the activity score
     */
    public double getActivityScore(@Nullable String node) {
        var a = getLogProbability(node, Computation.ACTIVITY, State.ACTIVE);
        var ia = getLogInverseProbability(node, Computation.ACTIVITY, State.ACTIVE);
        var i = getLogProbability(node, Computation.ACTIVITY, State.INHIBITED);
        var ii = getLogInverseProbability(node, Computation.ACTIVITY, State.INHIBITED);
        if (a > ia) return Math.abs(a - ia);
        else if (i > ii) return -Math.abs(i - ii);
        return 0;
    }

    private void computePValue(@Nullable String node) {
        if (node == null) node = PATHWAY_KEY;
        var exp = expected.get(Computation.ACTIVITY).getOrDefault(node, 0.0);
        var expR = expected.get(Computation.PRIOR).getOrDefault(node, 0.0);
        var var = variance.get(Computation.ACTIVITY).getOrDefault(node, 0.0);
        var varR = variance.get(Computation.PRIOR).getOrDefault(node, 0.0);
        pValues.put(node, TT.tTest(exp, expR, var, varR, numberOfIterations, numberOfIterations));
    }

    private void computePValues() {
        if (!pValuesComputed) {
            if (!probabilitiesComputed) computeProbabilities();
            Set<String> nodes = getAllNodes();
            pValues = new HashMap<>();
            for (String n : nodes) {
                computePValue(n);
            }
            Utils.applyArrayFunctionToMap(pValues, v -> {
                var r = Adjusters.benjaminiHochberg(Arrays.stream(v).mapToDouble(x -> x).toArray());
                return Arrays.stream(r).boxed().toArray(Double[]::new);
            }, String.class, Double.class);
            computePValue(null);
            pValuesComputed = true;
        }
    }


    /**
     * Compute p-value associated to one activity score
     *
     * @param node the node
     * @return the p-value
     */
    public double getPValue(@Nullable String node) {
        if (!pValuesComputed) computePValues();
        if (node == null) node = PATHWAY_KEY;
        return pValues.getOrDefault(node, 1.0);
        /*var exp = expected.get(Computation.ACTIVITY).getOrDefault(node, 0.0);
        var expR = expected.get(Computation.PRIOR).getOrDefault(node, 0.0);
        var var = variance.get(Computation.ACTIVITY).getOrDefault(node, 0.0);
        var varR = variance.get(Computation.PRIOR).getOrDefault(node, 0.0);
        return TT.tTest(exp, expR,
                var, varR,
                numberOfIterations, numberOfIterations);*/
    }

    /**
     * Get all nodes
     *
     * @return a set of nodes
     */
    private HashSet<String> getAllNodes() {
        if (allNodes == null) {
            allNodes = new HashSet<>();
            allNodes.addAll(counters.get(Computation.ACTIVITY).get(State.ACTIVE).keySet());
            allNodes.addAll(counters.get(Computation.ACTIVITY).get(State.INHIBITED).keySet());
            allNodes.addAll(counters.get(Computation.ACTIVITY).get(State.OTHERWISE).keySet());
            allNodes.addAll(counters.get(Computation.PRIOR).get(State.ACTIVE).keySet());
            allNodes.addAll(counters.get(Computation.PRIOR).get(State.INHIBITED).keySet());
            allNodes.addAll(counters.get(Computation.PRIOR).get(State.OTHERWISE).keySet());
        }
        return allNodes;
    }

    /**
     * Sets the list of direct targets
     *
     * @param targets a list of direct targets
     * @return this object for a fluent interface
     */
    public PathwaySimulationResults setDirectTargets(List<String> targets) {
        this.directTargets.addAll(targets);
        return this;
    }

    /**
     * Returns the list of direct targets
     *
     * @return the list of direct targets
     */
    public List<String> getDirectTargets() {
        return this.directTargets;
    }

    public HashMap<String, List<String>> getDirectlyTargetedBy() {
        return directlyTargetedBy;
    }

    public void setDirectlyTargetedBy(HashMap<String, List<String>> directlyTargetedBy) {
        this.directlyTargetedBy = directlyTargetedBy;
    }

    /**
     * Set the list of direct targets of a simulation
     *
     * @param simulateOn the nodes on which the simulation is being run
     * @param p          a pathway
     * @return this object for a fluent interface
     */
    public PathwaySimulationResults setDirectTargets(List<String> simulateOn, PathwayInterface p) {
        if (p.hasGraph()) {
            GraphInterface g = p.getGraph();
            simulateOn.forEach(s -> {
                if (g.hasNode(s)) {
                    setDirectTargets(
                            g.outgoingNodesStream(g.getNode(s)).map(NodeInterface::getId).collect(Collectors.toList()));
                    g.outgoingNodesStream(g.getNode(s)).forEach(n -> {
                        if (!directlyTargetedBy.containsKey(n.getId())) {
                            directlyTargetedBy.put(n.getId(), new ArrayList<>());
                        }
                        directlyTargetedBy.get(n.getId()).add(s);
                    });
                }
            });
        }
        return this;
    }


    @Override
    public String toString() {
        return "PathwaySimulationResults{" +
                "pathwayId='" + pathwayId + '\'' +
                ", counters=" + counters +
                ", numberOfIterations=" + numberOfIterations +
                '}';
    }
}
