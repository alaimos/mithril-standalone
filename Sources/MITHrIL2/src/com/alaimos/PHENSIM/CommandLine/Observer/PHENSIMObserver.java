package com.alaimos.PHENSIM.CommandLine.Observer;

import com.alaimos.Commons.Observer.Interface.EventInterface;
import com.alaimos.Commons.Observer.ObserverImpl.Observer;
import com.alaimos.MITHrIL.Data.Pathway.Interface.Enrichment.EdgeEnrichmentInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.PathwayInterface;
import me.tongfei.progressbar.ProgressBar;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * This class is an observer for PHENSIM Algorithm used to display the state of its computation
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.0
 * @since 19/08/2020
 */
public class PHENSIMObserver extends Observer {

    protected Consumer<String> print;
    protected Consumer<String> println;
    protected boolean printProgress;
    protected ProgressBar pb;

    public PHENSIMObserver(Consumer<String> print, Consumer<String> println, boolean printProgress) {
        this.print = print;
        this.println = println;
        this.printProgress = printProgress;
        addEventListener("phensimInitializing", this::phensimInitializing);
        addEventListener("phensimInitialized", this::phensimInitialized);
        addEventListener("phensimStarting", this::phensimStarting);
        addEventListener("phensimSimulationStepIterationDone", this::phensimSimulationStepIterationDone);
        addEventListener("phensimError", this::phensimError);
        addEventListener("phensimDone", this::phensimDone);
        addEventListener("enrichmentStart", this::enrichmentStart);
        addEventListener("startEnrichingWithEdge", this::startEnrichingWithEdge);
        addEventListener("doneEnrichingWithEdge", this::doneEnrichingWithEdge);
        addEventListener("startEnrichingPathway", this::startEnrichingPathway);
        addEventListener("enrichmentDone", this::enrichmentDone);
    }

    private void phensimInitializing(EventInterface e) {
        print.accept("Initializing PHENSIM...");
    }

    private void phensimInitialized(EventInterface e) {
        println.accept("OK!");
    }

    private void phensimStarting(EventInterface e) {
        println.accept("Starting PHENSIM Simulation");
        if (printProgress) {
            pb = new ProgressBar("Simulating", (int) e.getData());
        }
    }

    private void phensimSimulationStepIterationDone(EventInterface e) {
        if (printProgress) {
            pb.step();
        }
    }

    private void phensimDone(EventInterface e) {
        if (printProgress) {
            pb.close();
        }
        println.accept("OK!");
    }

    private void phensimError(EventInterface e) {
        if (printProgress) {
            pb.setExtraMessage("Stopped!");
            pb.close();
        }
        println.accept("Error!!\n");
        if (e.getData() != null) {
            var ex = (Exception) e.getData();
            println.accept(ex.toString());
        }
    }

    private void enrichmentStart(EventInterface e) {
        println.accept("====================================================================================");
        println.accept("Enriching");
    }

    private void startEnrichingWithEdge(@NotNull EventInterface e) {
        EdgeEnrichmentInterface ee = (EdgeEnrichmentInterface) e.getData();
        print.accept("Edge " + ee.getStart().getId() + "->" + ee.getEnd().getId() + "...");
    }

    private void doneEnrichingWithEdge(EventInterface e) {
        println.accept("OK!");
    }

    private void startEnrichingPathway(EventInterface e) {
        PathwayInterface p = (PathwayInterface) e.getData();
        print.accept(p.getId() + "...");
    }

    private void enrichmentDone(EventInterface e) {
        println.accept("Done Enriching!!");
        println.accept("====================================================================================");
    }

}
