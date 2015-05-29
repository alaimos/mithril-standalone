package com.alaimos.BioClasses.Cell;

public class MicroRNA extends Gene {

    protected String mirBaseId;

    //region Constructors

    protected MicroRNA() {
        // do nothing
    }

    public MicroRNA(String keggId, String symbols, String name) {
        init(keggId, symbols, name);
    }

    //endregion

    //region Setters and Getters

    @Override
    protected void initOtherIds() {
        if (keggId == null && mirBaseId != null) {
            keggId = GenesRepository.mapMirBaseIdToKeggId(mirBaseId);
        } else if (keggId != null && mirBaseId == null) {
            mirBaseId = GenesRepository.mapKeggIdToMirBaseId(keggId);
        }

        if (keggId == null || mirBaseId == null) {
            throw new RuntimeException("Invalid MicroRNA");
        }

        super.initOtherIds();
    }

    public MicroRNA setKeggId(String id) {
        mirBaseId = null;
        super.setKeggId(id);
        return this;
    }

    public MicroRNA setMainSymbol(String mainSymbol) {
        super.setMainSymbol(mainSymbol);
        return this;
    }

    public MicroRNA setOtherSymbols(String[] otherSymbols) {
        super.setOtherSymbols(otherSymbols);
        return this;
    }

    public String getMirBaseId() {
        return mirBaseId;
    }

    public MicroRNA setMirBaseId(String id) {
        mirBaseId = id;
        keggId = null;
        initOtherIds();
        return this;
    }

    public String getMirBaseLink() {
        return "http://www.mirbase.org/cgi-bin/query.pl?terms=" + mirBaseId;
    }

    //endregion

    //region Comparator Methods

    @Override
    public int compareTo(Gene o) {
        return keggId.compareTo(o.keggId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Gene)) return false;
        Gene g = (Gene) o;
        return keggId.equals(g.keggId);
    }

    @Override
    public int hashCode() {
        return mirBaseId.hashCode();
    }

    //endregion
}
