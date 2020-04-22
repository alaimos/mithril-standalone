package com.alaimos.MITHrIL.Data.Records;

import com.alaimos.MITHrIL.Data.Records.Type.EvidenceType;
import org.jetbrains.annotations.Contract;

import java.util.Objects;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @since 16/12/2015
 * @version 2.0.0.0
 */
public class MiRNATarget {

    private String       targetId;
    private String       targetName;
    private String       targetSpecies;
    private EvidenceType evidenceType;
    private String       experiments;
    private String       references;

    @Contract("null -> fail")
    public static MiRNATarget fromSplitString(String[] s) {
        if (s == null || s.length < 8 || s[1].isEmpty() || s[4].isEmpty()) {
            throw new RuntimeException("Source string in not correctly formatted");
        }
        return new MiRNATarget(s[1], s[5], s[3], s[4], s[6], s[7]);
    }

    public MiRNATarget(String targetId, String targetName, String targetSpecies, String evidenceType,
                       String experiments, String references) {
        this(targetId, targetName, targetSpecies, EvidenceType.fromString(evidenceType), experiments, references);
    }

    public MiRNATarget(String targetId, String targetName, String targetSpecies, EvidenceType evidenceType,
                       String experiments, String references) {
        this.targetId = targetId;
        this.targetName = targetName;
        this.targetSpecies = targetSpecies;
        this.evidenceType = evidenceType;
        this.experiments = experiments;
        this.references = references;
    }

    public String getTargetId() {
        return targetId;
    }

    public MiRNATarget setTargetId(String targetId) {
        this.targetId = targetId;
        return this;
    }

    public String getTargetName() {
        return targetName;
    }

    public MiRNATarget setTargetName(String targetName) {
        this.targetName = targetName;
        return this;
    }

    public String getTargetSpecies() {
        return targetSpecies;
    }

    public MiRNATarget setTargetSpecies(String targetSpecies) {
        this.targetSpecies = targetSpecies;
        return this;
    }

    public EvidenceType getEvidenceType() {
        return evidenceType;
    }

    public MiRNATarget setEvidenceType(EvidenceType evidenceType) {
        this.evidenceType = evidenceType;
        return this;
    }

    public String getExperiments() {
        return experiments;
    }

    public MiRNATarget setExperiments(String experiments) {
        this.experiments = experiments;
        return this;
    }

    public String getReferences() {
        return references;
    }

    public MiRNATarget setReferences(String references) {
        this.references = references;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MiRNATarget)) return false;
        MiRNATarget that = (MiRNATarget) o;
        return Objects.equals(targetId, that.targetId) &&
                evidenceType == that.evidenceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetId, evidenceType);
    }
}
