package com.alaimos.MITHrIL.CommandLine.Observer;

import com.alaimos.Commons.Observer.Interface.EventInterface;
import com.alaimos.Commons.Observer.ObserverImpl.Observer;
import com.alaimos.MITHrIL.Data.Pathway.Interface.PathwayInterface;

import java.util.function.Consumer;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 06/01/2016
 */
public class MITHrILObserver extends Observer {

    protected Consumer<String> print;
    protected Consumer<String> println;

    public MITHrILObserver(Consumer<String> print, Consumer<String> println) {
        this.print = print;
        this.println = println;
        addEventListener("startingMITHrILComputation", this::startingMITHrILComputation);
        addEventListener("computingOn", this::computingOn);
        addEventListener("computingOnVirtual", this::computingOnVirtual);
        addEventListener("sortingNodes", this::sortingNodes);
        addEventListener("computingPerturbations", this::computingPerturbations);
        addEventListener("computingImpactFactor", this::computingImpactFactor);
        addEventListener("computingImpactFactorVirtual", this::computingImpactFactor);
        addEventListener("computingTotalPerturbation", this::computingTotalPerturbation);
        addEventListener("computingAccumulator", this::computingAccumulator);
        addEventListener("computingAccumulatorVirtual", this::computingAccumulator);
        addEventListener("doneComputingOn", this::doneComputingOn);
        addEventListener("doneComputingOnVirtual", this::doneComputingOn);
        addEventListener("computingPValues", this::computingPValues);
        addEventListener("pValueIteration", this::pValueIteration);
        addEventListener("pValueFinalizing", this::pValueFinalizing);
        addEventListener("adjustingPValues", this::adjustingPValues);
        addEventListener("doneMITHrILComputation", this::doneMITHrILComputation);
    }

    private void startingMITHrILComputation(EventInterface e) {
        println.accept("Starting MITHrIL");
    }

    private void computingOn(EventInterface e) {
        print.accept("    Working on pathway " + ((PathwayInterface) e.getData()).getId());
    }

    private void computingOnVirtual(EventInterface e) {
        print.accept("    Working on pathway " + e.getData());
    }

    private void sortingNodes(EventInterface e) {
        print.accept("...Sorting Nodes");
    }

    private void computingPerturbations(EventInterface e) {
        print.accept("...Perturbations");
    }

    private void computingImpactFactor(EventInterface e) {
        print.accept("...Impact Factor");
    }

    private void computingTotalPerturbation(EventInterface e) {
        print.accept("...Total Perturbation");
    }

    private void computingAccumulator(EventInterface e) {
        print.accept("...Accumulator");
    }

    private void doneComputingOn(EventInterface e) {
        println.accept("...OK!");
    }

    private void computingPValues(EventInterface e) {
        print.accept("    Computing p-values");
    }

    private void pValueIteration(EventInterface eventInterface) {
        print.accept(".");
    }

    private void pValueFinalizing(EventInterface eventInterface) {
        print.accept(".");
    }

    private void adjustingPValues(EventInterface e) {
        println.accept("OK!\n    Adjusting p-values");
    }

    private void doneMITHrILComputation(EventInterface e) {
        println.accept("Done!");
    }

}
