package com.alaimos.SPECifIC.Data.Structures;

import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.Objects;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 16/12/2016
 */
public class DFSResult {

    private static NormalDistribution nd = new NormalDistribution();

    private NodeInterface startingNode;

    private VisitTree visit;

    private double zScore;

    private double pValue;

    public DFSResult(VisitTree visit, double zScore) {
        this.startingNode = visit.getObject().getNode();
        this.visit = visit;
        this.zScore = zScore;
        this.pValue = 1 - nd.cumulativeProbability(zScore);
    }

    public NodeInterface getStartingNode() {
        return startingNode;
    }

    public VisitTree getVisit() {
        return visit;
    }

    public double getZScore() {
        return zScore;
    }

    public double getPValue() {
        return pValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DFSResult)) return false;
        DFSResult dfsResult = (DFSResult) o;
        return Objects.equals(startingNode, dfsResult.startingNode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startingNode);
    }
}
