package com.alaimos.MITHrIL.Data.Records;

import org.jetbrains.annotations.Contract;

import java.util.Objects;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @since 12/12/2015
 * @version 2.0.0.0
 */
public class Species {

    private String  id;
    private String  name;
    private boolean hasMiRNA;
    private boolean hasTF;
    private String  pathwayDatabaseUrl;
    private String  miRNADatabaseUrl;
    private String  TFDatabaseUrl;

    @Contract("null -> fail")
    public static Species fromSplitString(String[] s) {
        if (s == null || s.length < 7 || s[0].isEmpty() || s[1].isEmpty()) {
            throw new RuntimeException("Source string in not correctly formatted");
        }
        return new Species(s[0], s[1], (Integer.parseInt(s[2]) == 1), (Integer.parseInt(s[3]) == 1), s[4], s[5], s[6]);
    }

    public Species(String id, String name, boolean hasMiRNA, boolean hasTF, String pathwayDatabaseUrl,
                   String miRNADatabaseUrl, String TFDatabaseUrl) {
        this.id = id;
        this.name = name;
        this.hasMiRNA = hasMiRNA;
        this.hasTF = hasTF;
        this.pathwayDatabaseUrl = pathwayDatabaseUrl;
        this.miRNADatabaseUrl = miRNADatabaseUrl;
        this.TFDatabaseUrl = TFDatabaseUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean hasMiRNA() {
        return hasMiRNA;
    }

    public void setHasMiRNA(boolean hasMiRNA) {
        this.hasMiRNA = hasMiRNA;
    }

    public String getPathwayDatabaseUrl() {
        return pathwayDatabaseUrl;
    }

    public void setPathwayDatabaseUrl(String pathwayDatabaseUrl) {
        this.pathwayDatabaseUrl = pathwayDatabaseUrl;
    }

    public String getMiRNADatabaseUrl() {
        return miRNADatabaseUrl;
    }

    public void setMiRNADatabaseUrl(String miRNADatabaseUrl) {
        this.miRNADatabaseUrl = miRNADatabaseUrl;
    }

    public boolean hasTF() {
        return hasTF;
    }

    public void setHasTF(boolean hasTF) {
        this.hasTF = hasTF;
    }

    public String getTFDatabaseUrl() {
        return TFDatabaseUrl;
    }

    public void setTFDatabaseUrl(String TFDatabaseUrl) {
        this.TFDatabaseUrl = TFDatabaseUrl;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Species)) return false;
        Species species = (Species) o;
        return Objects.equals(id, species.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
