package com.alaimos.MITHrIL.Data.Results;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class represents output of MITHrIL Pathway Analysis
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 29/12/2015
 */
public class PathwayAnalysisResult implements Serializable {

    private static final long serialVersionUID = -7412684761080906119L;
    protected Map<String, Map<String, Double>> perturbations;
    protected Map<String, Map<String, Double>> nodeAccumulators;
    protected Map<String, Double> accumulators;
    protected Map<String, Double> impactFactors;
    protected Map<String, Double> probabilities;
    protected Map<String, Double> probabilitiesNetwork;
    protected Map<String, Double> correctedAccumulators;
    protected Map<String, Map<String, Double>> nodePValues;
    protected Map<String, Double> pValues;
    protected Map<String, Double> adjustedPValues;

    public PathwayAnalysisResult() {
        perturbations = new HashMap<>();
        accumulators = new HashMap<>();
        nodeAccumulators = new HashMap<>();
        impactFactors = new HashMap<>();
        probabilities = new HashMap<>();
        probabilitiesNetwork = new HashMap<>();
        correctedAccumulators = new HashMap<>();
        nodePValues = new HashMap<>();
        pValues = new HashMap<>();
        adjustedPValues = new HashMap<>();
    }

    public PathwayAnalysisResult(Map<String, Map<String, Double>> perturbations,
                                 Map<String, Map<String, Double>> nodesAccumulators,
                                 Map<String, Double> accumulators, Map<String, Double> impactFactors,
                                 Map<String, Double> probabilities, Map<String, Double> probabilitiesNetwork,
                                 Map<String, Double> correctedAccumulators,
                                 Map<String, Map<String, Double>> nodePValues, Map<String, Double> pValues,
                                 Map<String, Double> adjustedPValues) {
        this.perturbations = perturbations;
        this.nodeAccumulators = nodesAccumulators;
        this.accumulators = accumulators;
        this.impactFactors = impactFactors;
        this.probabilities = probabilities;
        this.probabilitiesNetwork = probabilitiesNetwork;
        this.correctedAccumulators = correctedAccumulators;
        this.nodePValues = nodePValues;
        this.pValues = pValues;
        this.adjustedPValues = adjustedPValues;
    }

    public Map<String, Map<String, Double>> getPerturbations() {
        return perturbations;
    }

    public Map<String, Map<String, Double>> getNodeAccumulators() {
        return nodeAccumulators;
    }

    public Map<String, Double> getAccumulators() {
        return accumulators;
    }

    public Map<String, Double> getImpactFactors() {
        return impactFactors;
    }

    public Map<String, Double> getProbabilities() {
        return probabilities;
    }

    public Map<String, Double> getProbabilitiesNetwork() {
        return probabilitiesNetwork;
    }

    public Map<String, Double> getCorrectedAccumulators() {
        return correctedAccumulators;
    }

    public Map<String, Double> getPValues() {
        return pValues;
    }

    public Map<String, Double> getAdjustedPValues() {
        return adjustedPValues;
    }

    public Map<String, Map<String, Double>> getNodePValues() {
        return nodePValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PathwayAnalysisResult)) return false;
        PathwayAnalysisResult that = (PathwayAnalysisResult) o;
        return Objects.equals(perturbations, that.perturbations) &&
                Objects.equals(nodeAccumulators, that.nodeAccumulators) &&
                Objects.equals(accumulators, that.accumulators) &&
                Objects.equals(impactFactors, that.impactFactors) &&
                Objects.equals(probabilities, that.probabilities) &&
                Objects.equals(probabilitiesNetwork, that.probabilitiesNetwork) &&
                Objects.equals(correctedAccumulators, that.correctedAccumulators) &&
                Objects.equals(nodePValues, that.nodePValues) &&
                Objects.equals(pValues, that.pValues) &&
                Objects.equals(adjustedPValues, that.adjustedPValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(perturbations, nodeAccumulators, accumulators, impactFactors, probabilities,
                probabilitiesNetwork, correctedAccumulators, nodePValues, pValues, adjustedPValues);
    }

    @Override
    public String toString() {
        return "PathwayAnalysisResult{" +
                "perturbations=" + perturbations +
                ", accumulators=" + accumulators +
                ", impactFactors=" + impactFactors +
                ", probabilities=" + probabilities +
                ", probabilities=" + probabilitiesNetwork +
                ", correctedAccumulators=" + correctedAccumulators +
                ", nodePValues=" + nodePValues +
                ", pValues=" + pValues +
                ", adjustedPValues=" + adjustedPValues +
                '}';
    }
}
