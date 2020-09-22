package com.alaimos.PHENSIM.Data.Generator;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * This class is used to generate random expression values
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.0
 * @since 19/08/2020
 */
public class RandomExpressionGenerator {

    public static final double MIN = -100.0;
    public static final double CENTER = 0.0;
    public static final double MAX = 100.0;
    public static final double ALL_LFCS_STDEV = 4.0;
    public static final double LFCS_STDEV = 2.0;
    public static final double ALL_LFCS_MEAN = 0.0;
    public static final double UP_LFCS_MEAN = 5.0;
    public static final double DOWN_LFCS_MEAN = -5.0;

    private List<String> nodes = null;
    private HashSet<String> nodesSet = null;
    private Map<String, ExpressionConstraint> constraints = null;
    private Random random = null;
    private double epsilon = 0.001;

    public RandomExpressionGenerator(Random random) {
        this.random = random;
    }

    public RandomExpressionGenerator(Random random, double epsilon) {
        this.random = random;
        this.epsilon = epsilon;
    }

    /**
     * Generate a random number in a range (extremes included)
     *
     * @param min minimum value of the range
     * @param max maximum value of the range
     * @return a number
     */
    public double inclusiveRandomNumber(double min, double max) {
        double r = random.nextDouble();
        if (r < 0.5) {
            return ((1 - random.nextDouble()) * (max - min) + min);
        }
        return (random.nextDouble() * (max - min) + min);
    }

    /**
     * Generate a normally distributed random number
     *
     * @param mean mean of the distribution
     * @param sd   standard deviation of the distribution
     * @return a number
     */
    public double gaussianRandomNumber(double mean, double sd) {
        return sd * random.nextGaussian() + mean;
    }

    /**
     * Reset this object
     *
     * @return this object for a fluent interface
     */
    public RandomExpressionGenerator reset() {
        nodes = null;
        constraints = null;
        return this;
    }

    /**
     * Get the list of nodes
     *
     * @return the list of nodes
     */
    public List<String> getNodes() {
        return nodes;
    }

    /**
     * Get a set of input nodes for fast lookup
     *
     * @return the set of input nodes
     */
    public HashSet<String> getNodesSet() {
        if (nodesSet == null) {
            nodesSet = new HashSet<>(constraints.keySet());
        }
        return nodesSet;
    }

    /**
     * Set the list of nodes
     *
     * @param nodes the new list of nodes
     * @return this object for a fluent interface
     */
    public RandomExpressionGenerator setNodes(List<String> nodes) {
        this.nodes = nodes;
        return this;
    }

    /**
     * Get the map of constraints
     *
     * @return the map of constraints
     */
    public Map<String, ExpressionConstraint> getConstraints() {
        return constraints;
    }

    /**
     * Set a new map of constraints
     *
     * @param constraints a new map of constraints
     * @return this object for a fluent interface
     */
    public RandomExpressionGenerator setConstraints(Map<String, ExpressionConstraint> constraints) {
        this.constraints = constraints;
        return this;
    }

    /**
     * Get the random number generator
     *
     * @return the random number generator
     */
    public Random getRandom() {
        return random;
    }

    /**
     * Set a new random number generator
     *
     * @param random the new random number generator
     * @return this object for a fluent interface
     */
    public RandomExpressionGenerator setRandom(Random random) {
        this.random = random;
        return this;
    }

    /**
     * Generate a random expression value
     *
     * @param c an expression constraint
     * @return the expression value
     */
    protected double randomExpression(@NotNull ExpressionConstraint c) {
        switch (c) {
            case OVEREXPRESSION:
                return Math.max(epsilon, gaussianRandomNumber(UP_LFCS_MEAN, LFCS_STDEV));
            case UNDEREXPRESSION:
                return Math.min(-epsilon, gaussianRandomNumber(DOWN_LFCS_MEAN, LFCS_STDEV));
            case BOTH:
            default:
                return gaussianRandomNumber(ALL_LFCS_MEAN, ALL_LFCS_STDEV);
        }
    }

    /**
     * Generate random expressions
     *
     * @return generate random expressions
     */
    public Map<String, Double> getRandomExpressions() {
        HashMap<String, Double> expressions = new HashMap<>();
        for (String s : nodes) {
            expressions.put(s, constraints.containsKey(s) ? randomExpression(constraints.get(s)) : 0.0);
        }
        return expressions;
        /*return nodes.stream()
                    .map(s -> new Pair<>(s, constraints.containsKey(s) ? randomExpression(constraints.get(s)) : 0.0))
                    .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));*/
    }
}
