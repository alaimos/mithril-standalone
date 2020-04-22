package com.alaimos.MITHrIL.Algorithm;

import java.util.List;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 03/01/2016
 */
@FunctionalInterface
public interface ProbabilityComputationInterface {

    double computeProbability(List<String> allExperimentNodes, List<String> allNodesInPathway, List<String> allDENodes,
                              List<String> allDENodesInPathway);
}
