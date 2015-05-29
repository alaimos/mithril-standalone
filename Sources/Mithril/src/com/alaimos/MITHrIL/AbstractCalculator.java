package com.alaimos.MITHrIL;

import com.alaimos.BioClasses.Pathways.Node;
import com.alaimos.BioClasses.Pathways.Pathway;
import com.alaimos.BioClasses.Pathways.Repository;
import com.alaimos.Utils.PValues;
import org.apache.commons.math3.distribution.HypergeometricDistribution;
import org.apache.commons.math3.stat.descriptive.rank.Median;

import java.util.*;

public abstract class AbstractCalculator {

    //region Constants
    public final static Random MYRANDOM = new Random(1L);
    //endregion

    //region Variables
    public static int                                     NUMBER_OF_TESTS = 2001;
    protected     HashSet<String>                         arrayGenes      = new HashSet<>();
    protected     HashSet<String>                         deGenes         = new HashSet<>();
    protected     HashMap<String, Double>                 deltaE          = new HashMap<>();
    protected     HashMap<Pathway, HashMap<Node, Double>> computedPF      = new HashMap<>();
    protected     String[]                                rule            = null;
    protected     boolean                                 hasRule         = false;
    protected     double                                  meanDeltaE      = 0.0d;
    //endregion

    //region Utilites

    protected List<String> intersectGenes(Collection<String> gs, Pathway p) {
        List<String> result = new ArrayList<String>();
        for (String g : gs) {
            if (p.hasNode(g)) {
                result.add(g);
            }
        }
        return result;
    }

    //endregion

    //region P-Value computation

    @SuppressWarnings("unchecked")
    public double[] computePathwayPValue(Pathway p, double pIf) {
        if (pIf == 0.0) { //Nothing here
            return new double[]{1.0, 0.0};
        }

        HashMap<String, Double> copy = (HashMap<String, Double>) deltaE.clone();
        List<String> ig = intersectGenes(deGenes, p);
        double[] expChange = new double[ig.size()];
        int n = 0;
        for (String s : ig) {
            copy.remove(s);
            expChange[n++] = deltaE.get(s);
        }
        double[] randPIfs = new double[NUMBER_OF_TESTS];
        double pi = computePi(p), acc = computeAccumulation(p), pvalue = 0;
        for (int i = 0; i < NUMBER_OF_TESTS; i++) {
            try {
                AbstractCalculator tmpAc = getClass().newInstance();
                HashMap<String, Double> tmpDeltaE = (HashMap<String, Double>) copy.clone();
                for (double anExpChange : expChange) {
                    int k = MYRANDOM.nextInt(p.countNodes());
                    while (tmpDeltaE.containsKey(p.getNode(k).getEntryId())) {
                        k = MYRANDOM.nextInt(p.countNodes());
                    }
                    tmpDeltaE.put(p.getNode(k).getEntryId(), anExpChange);
                }
                tmpAc.setArrayGenes(arrayGenes).setDeltaE(tmpDeltaE);
                if (rule != null) tmpAc.setRule(rule);
                randPIfs[i] = tmpAc.computeAccumulation(p);
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
        double median = new Median().evaluate(randPIfs);
        acc -= median;
        if (acc != 0) {
            for (int i = 0; i < NUMBER_OF_TESTS; i++) {
                if (acc > 0) {
                    pvalue += ((randPIfs[i] - median) >= acc) ? 1 : 0;
                } else if (acc < 0) {
                    pvalue += ((randPIfs[i] - median) <= acc) ? 1 : 0;
                }
            }
            pvalue = 2 * (pvalue / NUMBER_OF_TESTS);
            if (pvalue <= 0) {
                pvalue = 1.0 / (double) NUMBER_OF_TESTS / 100.0;
            } else if (pvalue > 1) {
                pvalue = 1.0;
            }
        } else {
            pvalue = 1.0;
        }
        return new double[]{PValues.normalCombine(pvalue, pi), median};
    }

    public double[][] computePValue(Repository r, double[] impactFactors) {
        double[][] pvalues = new double[impactFactors.length][];
        System.out.println("Running " + NUMBER_OF_TESTS + " tests...");
        for (int i = 0; i < impactFactors.length; i++) {
            pvalues[i] = computePathwayPValue(r.get(i), impactFactors[i]);
            if ((i % 20) == 0) {
                System.out.println(i + " of " + impactFactors.length);
            }
        }
        return pvalues;
    }

    //endregion

    //region Setters

    public AbstractCalculator setArrayGenes(Collection<String> genes) {
        arrayGenes.clear();
        arrayGenes.addAll(genes);
        return this;
    }

    public AbstractCalculator setDeltaE(HashMap<String, Double> deltaE) {
        deGenes.clear();
        deGenes.addAll(deltaE.keySet());
        this.deltaE = deltaE;
        meanDeltaE = 0.0d;
        for (double d : deltaE.values()) {
            meanDeltaE += Math.abs(d);
        }
        meanDeltaE /= deltaE.size();
        hasRule = false;
        return this;
    }

    public AbstractCalculator setRule(String[] rule) {
        this.rule = rule;
        deGenes.retainAll(Arrays.asList(rule));
        meanDeltaE = 0.0d;
        for (String s : deGenes) {
            meanDeltaE += Math.abs(deltaE.get(s));
        }
        meanDeltaE /= deGenes.size();
        hasRule = true;
        return this;
    }

    public AbstractCalculator setPV(int NUMBER_OF_TESTS) {
        AbstractCalculator.NUMBER_OF_TESTS = NUMBER_OF_TESTS;
        return this;
    }

    public boolean hasRule() {
        return hasRule;
    }

    //endregion

    //region Perturbation factor and Pi

    public double computePi(Pathway p) {
        //probability of obtaining a number of DE genes on the given pathway at least as large as the observed one
        int m = intersectGenes(arrayGenes, p).size(),
                k = deGenes.size(),
                x = intersectGenes(deGenes, p).size();
        HypergeometricDistribution hp = new HypergeometricDistribution(arrayGenes.size(), m, k);
        return hp.upperCumulativeProbability(x);
    }

    public double perturbationFactor(Pathway p, Node g) {
        if (!computedPF.containsKey(p)) {
            computedPF.put(p, new HashMap<Node, Double>());
        }
        if (!computedPF.get(p).containsKey(g)) {
            if (deltaE.containsKey(g.getEntryId())) {
                computedPF.get(p).put(g, deltaE.get(g.getEntryId()));
            } else {
                computedPF.get(p).put(g, 0.0d);
            }
        } else {
            return computedPF.get(p).get(g);
        }
        double pf = (deltaE.containsKey(g.getEntryId())) ? deltaE.get(g.getEntryId()) : 0.0;
        for (Node u : p.getParents(g)) {
            double tmp = (perturbationFactor(p, u) / p.outDegree(u));
            if (Double.isNaN(tmp) || Double.isInfinite(tmp)) {
                tmp = 0;
            }
            pf += (double) p.getEdge(u, g).getWeight() * tmp;
        }
        computedPF.get(p).put(g, pf);
        return pf;
    }

    public double computeImpactFactor(Pathway p) {
        double cif = 0.0d;
        int deGenePathway;
        if (rule != null) {
            ArrayList<String> tmp = new ArrayList<>();
            for (String g : rule) {
                if (p.hasNode(g)) {
                    cif += Math.abs(perturbationFactor(p, p.getNode(g)));
                }
                if (deGenes.contains(g)) {
                    tmp.add(g);
                }
            }
            deGenePathway = intersectGenes(tmp, p).size();
        } else {
            for (Node g : p.getNodes()) {
                cif += Math.abs(perturbationFactor(p, g));
            }
            deGenePathway = intersectGenes(deGenes, p).size();
        }
        if (deGenePathway == 0) {
            return 0;
        }
        cif /= (meanDeltaE * (double) deGenePathway);
        cif += Math.log(1 / computePi(p));
        return cif;
    }

    public double computeTotalPerturbation(Pathway p) {
        double cif = 0.0d;
        int deGenePathway;
        if (rule != null) {
            ArrayList<String> tmp = new ArrayList<>();
            for (String g : rule) {
                if (p.hasNode(g)) {
                    cif += Math.abs(perturbationFactor(p, p.getNode(g)));
                }
                if (deGenes.contains(g)) {
                    tmp.add(g);
                }
            }
            deGenePathway = intersectGenes(tmp, p).size();
        } else {
            for (Node g : p.getNodes()) {
                cif += Math.abs(perturbationFactor(p, g));
            }
            deGenePathway = intersectGenes(deGenes, p).size();
        }
        if (deGenePathway == 0) {
            return 0;
        }
        cif /= (meanDeltaE * (double) deGenePathway);
        return cif;
    }

    public double computeAccumulation(Pathway p) {
        double acc = 0.0d;
        if (rule != null) {
            for (String g : rule) {
                if (p.hasNode(g)) {
                    double de = (deltaE.containsKey(g)) ? deltaE.get(g) : 0.0;
                    if (p.getNode(g).getType().equals(Pathway.NodeType.MICRORNA)) {
                        acc -= perturbationFactor(p, p.getNode(g)) - de;
                    } else {
                        acc += perturbationFactor(p, p.getNode(g)) - de;
                    }
                }
            }
        } else {
            for (Node g : p.getNodes()) {
                double de = (deltaE.containsKey(g.getEntryId())) ? deltaE.get(g.getEntryId()) : 0.0;
                if (g.getType().equals(Pathway.NodeType.MICRORNA)) {
                    acc -= perturbationFactor(p, g) - de;
                } else {
                    acc += perturbationFactor(p, g) - de;
                }
            }
        }
        return acc;
    }

    //endregion

    public abstract double[] computeImpactFactor();

    public abstract double[] computeAccumulation();

    public abstract double[] computeTotalPerturbation();

    public abstract double[] computeProbability();

}