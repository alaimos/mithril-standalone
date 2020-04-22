package com.alaimos.MITHrIL.Data.Records;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @since 12/12/2015
 * @version 2.0.0.0
 */
public class MiRNA {

    private String                              miRNAId;
    private String                              miRNASpecies;
    private ArrayList<MiRNATarget>              targets;
    private ArrayList<MiRNATranscriptionFactor> transcriptionFactors;

    @Contract("null -> fail")
    public static MiRNA fromSplitString(String[] s) {
        if (s == null || s.length < 8 || s[0].isEmpty() || s[2].isEmpty()) {
            throw new RuntimeException("Source string in not correctly formatted");
        }
        return (new MiRNA(s[0], s[2])).addTarget(s);
    }

    public MiRNA(String miRNAId, String miRNASpecies) {
        this.miRNAId = miRNAId;
        this.miRNASpecies = miRNASpecies;
        this.targets = new ArrayList<>();
        this.transcriptionFactors = new ArrayList<>();
    }

    public String getMiRNAId() {
        return miRNAId;
    }

    public MiRNA setMiRNAId(String miRNAId) {
        this.miRNAId = miRNAId;
        return this;
    }

    public String getMiRNASpecies() {
        return miRNASpecies;
    }

    public MiRNA setMiRNASpecies(String miRNASpecies) {
        this.miRNASpecies = miRNASpecies;
        return this;
    }

    public ArrayList<MiRNATarget> getTargets() {
        return targets;
    }

    public boolean containsTarget(String target) {
        for (MiRNATarget t : targets) {
            if (t.getTargetId().equals(target)) return true;
        }
        return false;
    }

    public boolean containsTarget(MiRNATarget target) {
        return this.targets.contains(target);
    }

    public MiRNA addTarget(String[] target) {
        MiRNATarget t = MiRNATarget.fromSplitString(target);
        return addTarget(t);
    }

    public MiRNA addTarget(MiRNATarget target) {
        if (!this.containsTarget(target)) {
            targets.add(target);
        }
        return this;
    }

    public MiRNA clearTargets() {
        this.targets.clear();
        return this;
    }

    public MiRNA setTargets(ArrayList<MiRNATarget> targets) {
        this.targets = targets;
        return this;
    }

    public ArrayList<MiRNATranscriptionFactor> getTranscriptionFactors() {
        return transcriptionFactors;
    }

    public boolean containsTranscriptionFactor(String transcriptionFactor) {
        for (MiRNATranscriptionFactor t : transcriptionFactors) {
            if (t.getTfId().equals(transcriptionFactor)) return true;
        }
        return false;
    }

    public boolean containsTranscriptionFactor(MiRNATranscriptionFactor transcriptionFactor) {
        return this.transcriptionFactors.contains(transcriptionFactor);
    }

    public MiRNA addTranscriptionFactor(String[] transcriptionFactor) {
        MiRNATranscriptionFactor t = MiRNATranscriptionFactor.fromSplitString(transcriptionFactor);
        return addTranscriptionFactor(t);
    }

    public MiRNA addTranscriptionFactor(MiRNATranscriptionFactor transcriptionFactor) {
        if (!this.containsTranscriptionFactor(transcriptionFactor)) {
            transcriptionFactors.add(transcriptionFactor);
        }
        return this;
    }

    public MiRNA clearTranscriptionFactors() {
        this.transcriptionFactors.clear();
        return this;
    }

    public MiRNA setTranscriptionFactors(ArrayList<MiRNATranscriptionFactor> transcriptionFactors) {
        this.transcriptionFactors = transcriptionFactors;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MiRNA)) return false;
        MiRNA miRNA = (MiRNA) o;
        return Objects.equals(miRNAId, miRNA.miRNAId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(miRNAId);
    }
}
