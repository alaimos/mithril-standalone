package com.alaimos.PHENSIM.Data.Generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * This class is used to generate subsets of nodes
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 10/01/2016
 */
public class RandomSubsetGenerator {

    private List<String> nodes  = null;
    private Random       random = null;

    public RandomSubsetGenerator(Random random) {
        this.random = random;
    }

    public RandomSubsetGenerator(List<String> nodes, Random random) {
        this.nodes = nodes;
        this.random = random;
    }

    public RandomSubsetGenerator(List<String> nodes) {
        this(nodes, new Random());
    }

    /**
     * Reset this object
     *
     * @return this object for a fluent interface
     */
    public RandomSubsetGenerator reset() {
        nodes = null;
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
     * Set the list of nodes
     *
     * @param nodes the new list of nodes
     * @return this object for a fluent interface
     */
    public RandomSubsetGenerator setNodes(List<String> nodes) {
        this.nodes = nodes;
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
    public RandomSubsetGenerator setRandom(Random random) {
        this.random = random;
        return this;
    }

    /**
     * Generate a random subset of nodes
     *
     * @param size the number of nodes in the subset
     * @return the random subset
     */
    public List<String> getRandomSubset(int size) {
        HashSet<String> result = new HashSet<>();
        while (result.size() < size) {
            result.add(nodes.get(random.nextInt(nodes.size())));
        }
        return new ArrayList<>(result);
    }
}
