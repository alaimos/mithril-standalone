package com.alaimos.BioClasses.Cell;

import com.alaimos.BioClasses.Common;

public class Gene implements Comparable<Gene>, Cloneable {

    protected String   keggId       = null;
    protected String   entrezId     = null;
    protected String   mainSymbol   = null;
    protected String[] otherSymbols = null;
    protected String   name         = null;

    //region Constructors

    protected Gene() {
        // do nothing
    }

    public Gene(String keggId, String symbols, String name) {
        init(keggId, symbols, name);
    }

    protected void init(String keggId, String symbols, String name) {
        setKeggId(keggId).setName(name);
        String[] symb = symbols.split(",");
        if (symb.length > 0) {
            setMainSymbol(symb[0]);
            if (symb.length > 1) {
                String[] tmp = new String[symb.length - 1];
                System.arraycopy(symb, 1, tmp, 0, tmp.length);
            } else {
                setOtherSymbols(new String[]{});
            }
        } else {
            setMainSymbol(name).setOtherSymbols(new String[]{});
        }
    }

    protected void initOtherIds() {
        if (keggId != null && keggId.toLowerCase().startsWith(Common.GENEPREFIX)) {
            entrezId = keggId.substring(4);
        }
    }

    //endregion

    //region Setters and Getters

    @Override
    public Object clone() {
        Gene cl;
        try {
            cl = (Gene) super.clone();
        } catch (CloneNotSupportedException e) {
            cl = new Gene();
        }
        cl.keggId = keggId;
        cl.name = name;
        cl.mainSymbol = mainSymbol;
        String[] tmp = new String[otherSymbols.length];
        System.arraycopy(otherSymbols, 0, tmp, 0, otherSymbols.length);
        cl.otherSymbols = tmp;
        return cl;
    }

    public String getKeggId() {
        return keggId;
    }

    public Gene setKeggId(String keggId) {
        this.keggId = keggId;
        initOtherIds();
        return this;
    }

    public String getMainSymbol() {
        return mainSymbol;
    }

    public Gene setMainSymbol(String mainSymbol) {
        this.mainSymbol = mainSymbol;
        return this;
    }

    public String[] getOtherSymbols() {
        return otherSymbols;
    }

    public Gene setOtherSymbols(String[] otherSymbols) {
        this.otherSymbols = otherSymbols;
        return this;
    }

    public String getName() {
        return name;
    }

    public Gene setName(String name) {
        this.name = name;
        return this;
    }

    public String getEntrezId() {
        return entrezId;
    }

    public String getKeggLink() {
        return "http://www.kegg.jp/dbget-bin/www_bget?" + keggId;
    }

    public String getNCBILink() {
        return "http://www.ncbi.nlm.nih.gov/gene/?term=" + entrezId;
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
        Gene gene = (Gene) o;
        return keggId.equals(gene.keggId);
    }

    @Override
    public int hashCode() {
        return keggId.hashCode();
    }

    //endregion
}
