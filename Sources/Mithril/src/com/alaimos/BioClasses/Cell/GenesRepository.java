package com.alaimos.BioClasses.Cell;


import com.alaimos.BioClasses.Common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class GenesRepository implements List<Gene>, Iterable<Gene> {

    //region MicroRNA Gene Mapper

    protected static HashMap<String, String> keggIdToMirBaseId = new HashMap<String, String>();
    protected static HashMap<String, String> mirBaseIdToKeggId = new HashMap<String, String>();
    protected static boolean mapsLoaded = false;

    static {
        loadMaps();
    }

    protected static void loadMaps() {
        if (mapsLoaded) {
            return;
        }
        //do something else
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            MicroRNA.class.getClass().getResourceAsStream("/com/alaimos/BioResources/mirna.map.txt")));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(";");
                if (data.length == 2 && data[1].startsWith(Common.GENEPREFIX)) {
                    data[0] = data[0].toLowerCase();
                    keggIdToMirBaseId.put(data[1], data[0]);
                    mirBaseIdToKeggId.put(data[0], data[1]);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public static boolean geneIsMirna(String keggId) {
        return keggIdToMirBaseId.containsKey(keggId);
    }

    public static boolean geneIsMirna(Gene g) {
        return keggIdToMirBaseId.containsKey(g.getKeggId());
    }

    public static String mapKeggIdToMirBaseId(String keggId) {
        return (keggIdToMirBaseId.containsKey(keggId)) ? keggIdToMirBaseId.get(keggId) : null;
    }

    public static String mapMirBaseIdToKeggId(String mirbaseId) {
        return (mirBaseIdToKeggId.containsKey(mirbaseId)) ? mirBaseIdToKeggId.get(mirbaseId) : null;
    }

    //endregion

    //region Internal Classes and Fields

    //Singleton Instances
    private final static GenesRepository instance = new GenesRepository();

    //Genes list and index
    private ArrayList<Gene> genes = new ArrayList<Gene>();
    private HashMap<String, Integer> genesIndex = new HashMap<String, Integer>();

    //endregion

    //region Constructors and Singleton Pattern

    public static GenesRepository getInstance() {
        return instance;
    }

    private GenesRepository() {
        readDatabase();
    }

    //endregion

    //region Read external data

    private void readDatabase() {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            getClass().getResourceAsStream("/com/alaimos/BioResources/genes.database.txt")));
            String line;
            Gene g;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(";");
                if (data.length >= 3) {
                    if (geneIsMirna(data[0])) {
                        g = new MicroRNA(data[0], data[1], data[2]);
                    } else {
                        g = new Gene(data[0], data[1], data[2]);
                    }
                    genes.add(g);
                    genesIndex.put(g.getKeggId(), genes.indexOf(g));
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    //endregion

    //region Get Gene

    public boolean hasGene(int id) {
        return id < genes.size();
    }

    public boolean hasGene(String id) {
        return genesIndex.containsKey(id);
    }

    public Gene getGene(int id) {
        return genes.get(id);
    }

    public Gene getGene(String id) {
        return (hasGene(id)) ? genes.get(genesIndex.get(id)) : null;
    }

    //endregion

    //region Overridden methods from List<E>

    @Override
    public Gene get(int index) {
        return genes.get(index);
    }

    @Override
    public int indexOf(Object o) {
        return genes.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return genes.lastIndexOf(o);
    }

    @Override
    public ListIterator<Gene> listIterator() {
        return genes.listIterator();
    }

    @Override
    public ListIterator<Gene> listIterator(int index) {
        return genes.listIterator(index);
    }

    @Override
    public List<Gene> subList(int fromIndex, int toIndex) {
        return genes.subList(fromIndex, toIndex);
    }

    @Override
    public int size() {
        return genes.size();
    }

    @Override
    public boolean isEmpty() {
        return genes.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return genes.contains(o);
    }

    @Override
    public Iterator<Gene> iterator() {
        return genes.iterator();
    }

    @Override
    public Object[] toArray() {
        return genes.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return genes.toArray(a);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return genes.containsAll(c);
    }

    @Override
    public void clear() {
        genes = new ArrayList<Gene>(size());
        readDatabase();
    }

    //endregion

    //region Unsupported Operations from List<E>

    @Override
    public Gene set(int index, Gene element) {
        throw new UnsupportedOperationException("This class is read only.");
    }

    @Override
    public void add(int index, Gene element) {
        throw new UnsupportedOperationException("This class is read only.");
    }

    @Override
    public Gene remove(int index) {
        throw new UnsupportedOperationException("This class is read only.");
    }

    @Override
    public boolean add(Gene gene) {
        throw new UnsupportedOperationException("This class is read only.");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("This class is read only.");
    }

    @Override
    public boolean addAll(Collection<? extends Gene> c) {
        throw new UnsupportedOperationException("This class is read only.");
    }

    @Override
    public boolean addAll(int index, Collection<? extends Gene> c) {
        throw new UnsupportedOperationException("This class is read only.");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("This class is read only.");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("This class is read only.");
    }

    //endregion

}
