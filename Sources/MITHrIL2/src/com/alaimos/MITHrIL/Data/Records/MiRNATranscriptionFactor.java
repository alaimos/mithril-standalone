package com.alaimos.MITHrIL.Data.Records;

import com.alaimos.MITHrIL.Data.Records.Type.EvidenceType;
import org.jetbrains.annotations.Contract;

import java.util.Objects;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 16/12/2015
 */
public class MiRNATranscriptionFactor {

    private String       tfId;
    private String       tfType;
    private EvidenceType evidenceType;
    private String       references;

    @Contract("null -> fail")
    public static MiRNATranscriptionFactor fromSplitString(String[] s) {
        if (s == null || s.length < 5 || s[0].isEmpty() || s[1].isEmpty()) {
            throw new RuntimeException("Source string in not correctly formatted");
        }
        return new MiRNATranscriptionFactor(s[0], s[2], s[3], s[4]);
    }

    public MiRNATranscriptionFactor(String tfId, String tfType, String evidenceType, String references) {
        this.tfId = tfId;
        this.tfType = tfType;
        this.evidenceType = EvidenceType.fromString(evidenceType);
        this.references = references;
    }

    public MiRNATranscriptionFactor(String tfId, String tfType, EvidenceType evidenceType, String references) {
        this.tfId = tfId;
        this.tfType = tfType;
        this.evidenceType = evidenceType;
        this.references = references;
    }

    public String getTfId() {
        return tfId;
    }

    public void setTfId(String tfId) {
        this.tfId = tfId;
    }

    public EvidenceType getEvidenceType() {
        return evidenceType;
    }

    public void setEvidenceType(EvidenceType evidenceType) {
        this.evidenceType = evidenceType;
    }

    public String getReferences() {
        return references;
    }

    public void setReferences(String references) {
        this.references = references;
    }

    public String getTfType() {
        return tfType;
    }

    public void setTfType(String tfType) {
        this.tfType = tfType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MiRNATranscriptionFactor)) return false;
        MiRNATranscriptionFactor miRNATranscriptionFactor = (MiRNATranscriptionFactor) o;
        return Objects.equals(tfId, miRNATranscriptionFactor.tfId) &&
                tfType.equalsIgnoreCase(miRNATranscriptionFactor.tfType) &&
                evidenceType == miRNATranscriptionFactor.evidenceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tfId, tfType, evidenceType);
    }
}
