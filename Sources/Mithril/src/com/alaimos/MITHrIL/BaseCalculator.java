package com.alaimos.MITHrIL;

import com.alaimos.BioClasses.Pathways.Repository;

public class BaseCalculator extends AbstractCalculator {

    @Override
    public double[] computeImpactFactor() {
        Repository r = Repository.getInstance();
        double[] results = new double[r.size()];
        for (int i = 0; i < r.size(); i++) {
            results[i] = computeImpactFactor(r.get(i));
        }
        return results;
    }

    @Override
    public double[] computeAccumulation() {
        Repository r = Repository.getInstance();
        double[] results = new double[r.size()];
        for (int i = 0; i < r.size(); i++) {
            results[i] = computeAccumulation(r.get(i));
        }
        return results;
    }

    @Override
    public double[] computeTotalPerturbation() {
        Repository r = Repository.getInstance();
        double[] results = new double[r.size()];
        for (int i = 0; i < r.size(); i++) {
            results[i] = computeTotalPerturbation(r.get(i));
        }
        return results;
    }

    @Override
    public double[] computeProbability() {
        Repository r = Repository.getInstance();
        double[] results = new double[r.size()];
        for (int i = 0; i < r.size(); i++) {
            results[i] = Math.log(1 / computePi(r.get(i)));
        }
        return results;
    }

}
