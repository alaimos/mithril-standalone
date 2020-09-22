package com.alaimos.PHENSIM.Data.Results;

import com.alaimos.PHENSIM.Data.Enums.State;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.0
 * @since 19/08/2020
 */
public class SinglePathway {

    private final String pathwayId;
    private final boolean isVirtualPathway;
    private final SinglePathway virtualPathwaySource;
    private final Map<String, Double> activityScores = new HashMap<>();
    private final Map<String, Double> averagePerturbation = new HashMap<>();
    private double averagePathwayPerturbation = 0.0;
    private HashSet<String> allNodes = null;
    private Map<State, Integer> pathwayCounter;
    private Map<State, Double> pathwayProbability;
    private Map<State, Map<String, Integer>> nodesCounters;
    private Map<State, Map<String, Double>> nodesProbabilities;
    private int numberOfSimulations;
    private double minProbability;
    private boolean probabilitiesComputed = false;

    /**
     * Class constructor
     *
     * @param pathwayId           a pathway identifier
     * @param numberOfSimulations the number of iterations
     */
    public SinglePathway(String pathwayId, int numberOfSimulations) {
        this(pathwayId, numberOfSimulations, false, null);
    }

    /**
     * Class constructor
     *
     * @param pathwayId           the id of a pathway
     * @param numberOfSimulations the number of iterations
     */
    public SinglePathway(String pathwayId, int numberOfSimulations, boolean isVirtualPathway, @Nullable SinglePathway virtualPathwaySource) {
        this.numberOfSimulations = numberOfSimulations;
        this.pathwayId = pathwayId;
        this.nodesCounters = (isVirtualPathway) ? null : initStateCounter();
        this.pathwayCounter = initStateCounter(() -> 0);
        this.isVirtualPathway = isVirtualPathway;
        this.virtualPathwaySource = virtualPathwaySource;
        computeMinProbability();
    }

    /**
     * Compute the minimum value of the probability
     */
    private void computeMinProbability() {
        this.minProbability = 0.5 / ((double) numberOfSimulations);
    }

    /**
     * Initialize a state counter
     *
     * @return a new counters object
     */
    @Contract("_ -> new")
    @NotNull
    private <T> Map<State, T> initStateCounter(@NotNull Supplier<T> generator) {
        return new HashMap<>(Map.of(
                State.ACTIVE, generator.get(),
                State.INHIBITED, generator.get(),
                State.OTHERWISE, generator.get()
        ));
    }

    /**
     * Initialize a state counter
     *
     * @param <T> the type of container object
     * @return a new counters object
     */
    @NotNull
    @Contract(" -> new")
    private <T> Map<State, Map<String, T>> initStateCounter() {
        return initStateCounter(HashMap::new);
    }

    /**
     * Compute activity probabilities
     */
    private void computeProbabilities() {
        if (!probabilitiesComputed) {
            double prior = 1 / ((double) numberOfSimulations * 1000.0);
            if (!isVirtualPathway) {
                Set<String> nodes = getAllNodes();
                nodesProbabilities = initStateCounter();
                for (String n : nodes) {
                    var cA = getCounter(n, State.ACTIVE) + prior;
                    var cI = getCounter(n, State.INHIBITED) + prior;
                    var cO = getCounter(n, State.OTHERWISE) + prior;
                    var sum = cA + cI + cO;
                    nodesProbabilities.get(State.ACTIVE).put(n, cA / sum);
                    nodesProbabilities.get(State.INHIBITED).put(n, cI / sum);
                    nodesProbabilities.get(State.OTHERWISE).put(n, cO / sum);
                }
            }
            var cA = getPathwayCounter(State.ACTIVE) + prior;
            var cI = getPathwayCounter(State.INHIBITED) + prior;
            var cO = getPathwayCounter(State.OTHERWISE) + prior;
            var sum = cA + cI + cO;
            pathwayProbability = Map.of(
                    State.ACTIVE, cA / sum,
                    State.INHIBITED, cI / sum,
                    State.OTHERWISE, cO / sum
            );
            probabilitiesComputed = true;
            activityScores.clear();
        }
    }

    /**
     * Increment an integer counter inside a map
     *
     * @param map the map
     * @param key the key
     * @param <T> the type of the key
     */
    protected <T> void increment(@NotNull Map<T, Integer> map, T key) {
        map.put(key, 1 + (map.getOrDefault(key, 0)));
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
     * Get the number of iterations for the simulation procedure
     *
     * @return the number of iterations
     */
    public int getNumberOfSimulations() {
        return numberOfSimulations;
    }

    /**
     * Is the the pathway of this object a virtual pathway?
     *
     * @return a boolean indicating whether the pathway is virtual
     */
    public boolean isVirtualPathway() {
        return isVirtualPathway;
    }

    /**
     * Returns the source of this pathway if it is virtual
     *
     * @return the virtual pathway source
     */
    public SinglePathway getVirtualPathwaySource() {
        return virtualPathwaySource;
    }

    /**
     * Get all nodes
     *
     * @return a set of nodes
     */
    public HashSet<String> getAllNodes() {
        if (allNodes == null) {
            allNodes = new HashSet<>();
            allNodes.addAll(nodesCounters.get(State.ACTIVE).keySet());
            allNodes.addAll(nodesCounters.get(State.INHIBITED).keySet());
            allNodes.addAll(nodesCounters.get(State.OTHERWISE).keySet());
        }
        return allNodes;
    }

    /**
     * Set the number of iterations for the simulation procedure
     *
     * @param numberOfSimulations the number of iterations
     * @return this object for a fluent interface
     */
    public SinglePathway setNumberOfSimulations(int numberOfSimulations) {
        this.numberOfSimulations = numberOfSimulations;
        computeMinProbability();
        return this;
    }

    /**
     * Reset all counters
     *
     * @return this object for a fluent interface
     */
    public SinglePathway resetCounters() {
        nodesCounters = (isVirtualPathway) ? null : initStateCounter();
        pathwayCounter = initStateCounter(() -> 0);
        allNodes = null;
        probabilitiesComputed = false;
        return this;
    }

    /**
     * Increment counter for a node
     *
     * @param node  a node
     * @param state a node state
     * @return this object for a fluent interface
     */
    public SinglePathway incrementNodeCounter(@Nullable String node, State state) {
        if (node == null) {
            increment(pathwayCounter, state);
        } else {
            // If I'm dealing with a virtual pathway it will use the counter already stored in its source
            // Therefore I can save same memory avoiding this increment
            if (!isVirtualPathway) {
                increment(nodesCounters.get(state), node);
            }
        }
        return this;
    }

    /**
     * Increment the pathway counter
     *
     * @param state the pathway state
     * @return this object for a fluent interface
     */
    public SinglePathway incrementPathwayCounter(State state) {
        return incrementNodeCounter(null, state);
    }

    /**
     * Add a perturbation value to compute the average
     *
     * @param node         a pathway node
     * @param perturbation a perturbation value
     */
    public void addToAveragePerturbation(@Nullable String node, double perturbation) {
        if (perturbation != 0.0) {
            perturbation = perturbation / numberOfSimulations;
            if (node == null) {
                averagePathwayPerturbation += perturbation;
            } else if (!isVirtualPathway) {
                averagePerturbation.put(node, averagePerturbation.getOrDefault(node, 0.0) + perturbation);
            }
        }
    }

    /**
     * Get the counter for a node
     *
     * @param node  a node
     * @param state the state of the node
     * @return the counter
     */
    public int getCounter(@Nullable String node, State state) {
        if (node == null) {
            return pathwayCounter.getOrDefault(state, 0);
        } else {
            if (isVirtualPathway) {
                return virtualPathwaySource.getCounter(node, state);
            } else {
                return nodesCounters.get(state).getOrDefault(node, 0);
            }
        }
    }

    /**
     * Get the counter for a pathway
     *
     * @param state the state of the pathway
     * @return the counter
     */
    public int getPathwayCounter(State state) {
        return getCounter(null, state);
    }

    /**
     * Gets all the counters for a state
     *
     * @param state the state of the node
     * @return the map of counter
     */
    public Map<String, Integer> getNodesCounters(State state) {
        return nodesCounters.get(state);
    }

    /**
     * Gets the log probability of a node
     *
     * @param node  the node
     * @param state the state of the node
     * @return the probability
     */
    public double getLogProbability(@Nullable String node, State state) {
        if (!probabilitiesComputed) computeProbabilities();
        if (node == null) {
            return Math.log(pathwayProbability.getOrDefault(state, minProbability));
        } else {
            if (isVirtualPathway) {
                return virtualPathwaySource.getLogProbability(node, state);
            } else {
                return Math.log(nodesProbabilities.get(state).getOrDefault(node, minProbability));
            }
        }
    }

    /**
     * Gets the log probability of a pathway
     *
     * @param state the state for which probability will be computed
     * @return the probability of the selected state
     */
    public double getPathwayLogProbability(State state) {
        return getLogProbability(null, state);
    }

    /**
     * Gets 1 - log probability of a node
     *
     * @param node  the node
     * @param state the state of the node
     * @return the probability
     */
    private double getLogInverseProbability(@Nullable String node, State state) {
        if (!probabilitiesComputed) computeProbabilities();
        if (node == null) {
            return Math.log(1.0 - pathwayProbability.getOrDefault(state, minProbability));
        } else {
            if (isVirtualPathway) {
                return virtualPathwaySource.getLogInverseProbability(node, state);
            } else {
                return Math.log(1.0 - nodesProbabilities.get(state).getOrDefault(node, minProbability));
            }
        }
    }

    /**
     * Compute activity score for a node
     *
     * @param node the node
     * @return the activity score
     */
    public double getActivityScore(@Nullable String node) {
        if (isVirtualPathway && node != null) return virtualPathwaySource.getActivityScore(node);
        if (!probabilitiesComputed) computeProbabilities();
        if (activityScores.containsKey(node)) return activityScores.get(node);
        var a = getLogProbability(node, State.ACTIVE);
        var ia = getLogInverseProbability(node, State.ACTIVE);
        var i = getLogProbability(node, State.INHIBITED);
        var ii = getLogInverseProbability(node, State.INHIBITED);
        var o = getLogProbability(node, State.OTHERWISE);
        var io = getLogInverseProbability(node, State.OTHERWISE);
        var res = (a > ia) ? (a - ia) : (i > ii) ? -(i - ii) : (o > io) ? 0.0 : Double.NaN;
        activityScores.put(node, res);
        return res;
    }

    /**
     * Get the average perturbation for a node
     *
     * @param node a node
     * @return the perturbation
     */
    public double getAveragePerturbation(String node) {
        if (isVirtualPathway) return virtualPathwaySource.getAveragePerturbation(node);
        return averagePerturbation.getOrDefault(node, 0.0);
    }

    /**
     * Get the average perturbation for the pathway
     *
     * @return the perturbation
     */
    public double getAveragePathwayPerturbation() {
        return averagePathwayPerturbation;
    }

    /**
     * Compute the activity score for a pathway
     *
     * @return the pathway activity score
     */
    public double getPathwayActivityScore() {
        return getActivityScore(null);
    }

}
