package com.alaimos.BioClasses.Pathways;

import com.alaimos.BioClasses.Common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class Repository implements List<Pathway>, Iterable<Pathway> {

    //region Internal Classes and Fields

    private class MirnaData {

        public final HashSet<String>               mirna           = new HashSet<String>();
        public final HashMap<String, String[]>     interactionData = new HashMap<String, String[]>();
        public final HashMap<String, List<String>> mirnaToGene     = new HashMap<String, List<String>>();
        public final HashMap<String, List<String>> geneToMirna     = new HashMap<String, List<String>>();

    }

    //Categories list
    public final static  HashMap<String, List<String>> categories       = new HashMap<String, List<String>>();
    //Singleton Instances
    protected final static Repository                    instance         = new Repository();
    protected static       Repository                    enrichedInstance = null;
    //Pathways list and index
    protected              ArrayList<Pathway>            pathways         = new ArrayList<Pathway>();
    protected              HashMap<String, Integer>      pathwaysIndex    = new HashMap<String, Integer>();
    //Enrichment data
    protected              MirnaData                     tfData           = new MirnaData();
    protected              MirnaData                     miRegData        = new MirnaData();

    //endregion

    //region Constructors and Singleton Pattern

    public static Repository getInstance() {
        return instance;
    }

    public static Repository getEnrichedInstance() {
        if (enrichedInstance == null) {
            instance.enrichRepo();
        }
        return enrichedInstance;
    }

    protected Repository() {
        readCategories();
        readPathways();
        readEnrichmentData();
    }

    protected Repository(boolean do_nothing) {
        //do nothing
    }

    public void filter(String[] filter) {
        ArrayList<String> fpathways = new ArrayList<String>();
        if (filter.length > 0) {
            for (String c : filter) {
                if (categories.containsKey(c)) {
                    fpathways.addAll(categories.get(c));
                }
            }
        }
        if (fpathways.size() > 0) {
            ArrayList<Pathway> tmpPathways = new ArrayList<Pathway>();
            HashMap<String, Integer> tmpIndex = new HashMap<String, Integer>();
            for (String name : fpathways) {
                if (pathwaysIndex.containsKey(name) && !tmpIndex.containsKey(name)) {
                    Pathway p = pathways.get(pathwaysIndex.get(name));
                    tmpPathways.add(p);
                    tmpIndex.put(name, tmpPathways.lastIndexOf(p));
                }
            }
            pathways = tmpPathways;
            pathwaysIndex = tmpIndex;
            System.gc();
        }
    }

    protected void addDecoy(Pathway p, HashMap<String, Integer> genesToId, HashMap<Integer, String> idToGenes) {
        Pathway pn = new Pathway(p.getId() + "d", p.getName() + " - Decoy");
        pn.setOrganism(p.getOrganism());
        Random r = new Random();
        HashMap<Integer, Integer> oldToNew = new HashMap<Integer, Integer>();
        HashSet<Integer> used = new HashSet<Integer>();
        for (Node n : p.getNodes()) {
            int newId = -1, tmp, oldId = genesToId.get(n.getEntryId());
            while (newId < 0) {
                tmp = r.nextInt(genesToId.size());
                if (tmp != oldId && used.add(tmp)) {
                    newId = tmp;
                }
            }
            oldToNew.put(oldId, newId);
            pn.addNode(idToGenes.get(newId), idToGenes.get(newId), n.getType());
        }
        for (Edge e : p.getEdges()) {
            int start = oldToNew.get(genesToId.get(e.getStart().getEntryId())),
                    end = oldToNew.get(genesToId.get(e.getEnd().getEntryId()));
            pn.addEdge(pn.getNode(idToGenes.get(start)), pn.getNode(idToGenes.get(end)), e.getType(), e.getSubType());
        }
        pathways.add(pn);
        pathwaysIndex.put(pn.getId(), pathways.lastIndexOf(pn));
    }

    public void addDecoys() {

        HashMap<String, Integer> genesToId = new HashMap<String, Integer>();
        HashMap<Integer, String> idToGenes = new HashMap<Integer, String>();
        int lastId = 0;
        for (Pathway p : this) {
            for (Node n : p.getNodes()) {
                if (!genesToId.containsKey(n.getEntryId())) {
                    genesToId.put(n.getEntryId(), lastId);
                    idToGenes.put(lastId++, n.getEntryId());
                }
            }
        }
        int ps = this.pathways.size();
        for (int i = 0; i < ps; i++) {
            addDecoy(pathways.get(i), genesToId, idToGenes);
        }
    }

    //endregion

    //region Read external data

    protected void readCategories() {
        try {
            categories.clear();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            getClass().getResourceAsStream("/com/alaimos/BioResources/categories.txt")));
            String line;
            String currCategory = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {
                    currCategory = line.replace("#", "").toLowerCase();
                    if (!categories.containsKey(currCategory)) {
                        categories.put(currCategory, new ArrayList<String>());
                    }
                } else if (currCategory != null) {
                    categories.get(currCategory).add(line.toLowerCase());
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            getClass().getResourceAsStream("/com/alaimos/BioResources/mirna.txt")));
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] dt = line.split(";");
                if (dt.length >= 4) {
                    dt[2] = Common.GENEPREFIX + dt[2];
                    String idx = dt[0] + "-" + dt[2];
                    if (miRegData.mirna.add(dt[0])) {
                        miRegData.mirnaToGene.put(dt[0], new ArrayList<String>());
                    }
                    if (!miRegData.geneToMirna.containsKey(dt[2])) {
                        miRegData.geneToMirna.put(dt[2], new ArrayList<String>());
                    }
                    if (!miRegData.interactionData.containsKey(idx)) {
                        miRegData.interactionData.put(idx, new String[]{dt[1], dt[3]});
                        miRegData.mirnaToGene.get(dt[0]).add(dt[2]);
                        miRegData.geneToMirna.get(dt[2]).add(dt[0]);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    protected void readPathways() {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            getClass().getResourceAsStream("/com/alaimos/BioResources/pathways.txt")));
            boolean readingPathway = false,
                    readingNodes = false,
                    readingEdges = false;
            int pos = 0;
            Pathway currPathway = null;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equalsIgnoreCase("#-p")) {
                    readingPathway = true;
                    readingNodes = readingEdges = false;
                    if (currPathway != null && currPathway.countEdges() > 1) {
                        pathways.add(currPathway);
                        pathwaysIndex.put(currPathway.getId(), pathways.lastIndexOf(currPathway));
                    }
                    currPathway = new Pathway();
                    pos = 0;
                    continue;
                } else if (line.equalsIgnoreCase("#-n")) {
                    readingNodes = true;
                    readingPathway = readingEdges = false;
                    pos = 0;
                    continue;
                } else if (line.equalsIgnoreCase("#-e")) {
                    readingEdges = true;
                    readingPathway = readingNodes = false;
                    pos = 0;
                    continue;
                }
                if (readingPathway) {
                    switch (pos) {
                        case 0:
                            currPathway.setId(line);
                            break;
                        case 1:
                            currPathway.setName(line);
                            break;
                        case 2:
                            currPathway.setOrganism(line);
                            break;
                        case 3:
                            currPathway.setImage(line);
                            break;
                        case 4:
                            currPathway.setUrl(line);
                            break;
                    }
                } else if (readingNodes) {
                    String[] dt = line.split(";");
                    if (dt.length >= 3) {
                        currPathway.addNode(dt[0], dt[1], dt[2]);
                    }
                } else if (readingEdges) {
                    String[] dt = line.split(";");
                    if (dt.length >= 4) {
                        if (currPathway.hasNode(dt[0]) && currPathway.hasNode(dt[1])) {
                            currPathway.addEdge(currPathway.getNode(dt[0]), currPathway.getNode(dt[1]), dt[2], dt[3]);
                        }
                    }
                }
                pos++;
            }
            if (currPathway != null && !currPathway.isEmpty() && currPathway.countEdges() > 1) {
                pathways.add(currPathway);
                pathwaysIndex.put(currPathway.getId(), pathways.lastIndexOf(currPathway));
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    protected void readEnrichmentData() {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            getClass().getResourceAsStream("/com/alaimos/BioResources/tf.txt")));
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] dt = line.split(";");
                if (dt.length >= 5) {
                    dt[1] = Common.GENEPREFIX + dt[1];
                    String idx = dt[1] + "-" + dt[2];
                    if (tfData.mirna.add(dt[2])) {
                        tfData.mirnaToGene.put(dt[2], new ArrayList<String>());
                    }
                    if (!tfData.geneToMirna.containsKey(dt[1])) {
                        tfData.geneToMirna.put(dt[1], new ArrayList<String>());
                    }
                    if (!tfData.interactionData.containsKey(idx)) {
                        tfData.interactionData.put(idx, new String[]{dt[0], dt[3], dt[4]});
                        tfData.mirnaToGene.get(dt[2]).add(dt[1]);
                        tfData.geneToMirna.get(dt[1]).add(dt[2]);
                    } else {
                        int i1 = Integer.parseInt(dt[3]),
                                i2 = Integer.parseInt(tfData.interactionData.get(idx)[1]);
                        if (i1 != i2) {
                            tfData.interactionData.put(idx, new String[]{dt[0], Integer.toString(i1 + i2), dt[4]});
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            getClass().getResourceAsStream("/com/alaimos/BioResources/mirna.txt")));
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] dt = line.split(";");
                if (dt.length >= 4) {
                    dt[2] = Common.GENEPREFIX + dt[2];
                    String idx = dt[0] + "-" + dt[2];
                    if (miRegData.mirna.add(dt[0])) {
                        miRegData.mirnaToGene.put(dt[0], new ArrayList<String>());
                    }
                    if (!miRegData.geneToMirna.containsKey(dt[2])) {
                        miRegData.geneToMirna.put(dt[2], new ArrayList<String>());
                    }
                    if (!miRegData.interactionData.containsKey(idx)) {
                        miRegData.interactionData.put(idx, new String[]{dt[1], dt[3]});
                        miRegData.mirnaToGene.get(dt[0]).add(dt[2]);
                        miRegData.geneToMirna.get(dt[2]).add(dt[0]);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    //endregion

    //region Get Pathway

    public boolean hasPathway(int id) {
        return id < pathways.size();
    }

    public boolean hasPathway(String id) {
        return pathwaysIndex.get(id) != null;
    }

    public Pathway getPathway(int id) {
        return pathways.get(id);
    }

    public Pathway getPathway(String id) {
        return pathways.get(pathwaysIndex.get(id));
    }

    //endregion

    //region Overridden methods from List<E>

    @Override
    public Pathway get(int index) {
        return pathways.get(index);
    }

    @Override
    public int indexOf(Object o) {
        return pathways.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return pathways.lastIndexOf(o);
    }

    @Override
    public ListIterator<Pathway> listIterator() {
        return pathways.listIterator();
    }

    @Override
    public ListIterator<Pathway> listIterator(int index) {
        return pathways.listIterator(index);
    }

    @Override
    public List<Pathway> subList(int fromIndex, int toIndex) {
        return pathways.subList(fromIndex, toIndex);
    }

    @Override
    public int size() {
        return pathways.size();
    }

    @Override
    public boolean isEmpty() {
        return pathways.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return pathways.contains(o);
    }

    @Override
    public Iterator<Pathway> iterator() {
        return pathways.iterator();
    }

    @Override
    public Object[] toArray() {
        return pathways.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return pathways.toArray(a);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return pathways.containsAll(c);
    }

    @Override
    public void clear() {
        pathways = new ArrayList<Pathway>(size());
        readPathways();
    }
    //endregion

    //region Unsupported Operations from List<E>

    @Override
    public Pathway set(int index, Pathway element) {
        throw new UnsupportedOperationException("This class is read only.");
    }

    @Override
    public void add(int index, Pathway element) {
        throw new UnsupportedOperationException("This class is read only.");
    }

    @Override
    public Pathway remove(int index) {
        throw new UnsupportedOperationException("This class is read only.");
    }

    @Override
    public boolean add(Pathway pathway) {
        throw new UnsupportedOperationException("This class is read only.");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("This class is read only.");
    }

    @Override
    public boolean addAll(Collection<? extends Pathway> c) {
        throw new UnsupportedOperationException("This class is read only.");
    }

    @Override
    public boolean addAll(int index, Collection<? extends Pathway> c) {
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

    //region Pathway Enrichment

    @SuppressWarnings("unchecked")
    private void enrichRepo() {
        enrichedInstance = new Repository(true);
        //clone pathways and index
        enrichedInstance.pathways = new ArrayList<Pathway>(pathways.size());
        enrichedInstance.pathwaysIndex = (HashMap<String, Integer>) pathwaysIndex.clone();
        //and put a reference to original tfData and miRegData
        enrichedInstance.tfData = tfData;
        enrichedInstance.miRegData = miRegData;
        for (Pathway p : pathways) {
            Pathway pc = (Pathway) p.clone();
            enrichedInstance.enrichPathway(pc);
            enrichedInstance.pathways.add(pc);
        }
    }

    @SuppressWarnings("unchecked")
    private void enrichPathway(Pathway p) {
        p.setName(p.getName() + " - Enriched with miRNAs");
        if (p.countEdges() <= 1) {
            return;
        }
        int n1 = p.countNodes(), n2 = p.countEdges();
        //Add MicroRNA --| gene relationships
        HashSet<String> tmp = new HashSet<String>();
        for (String g : miRegData.geneToMirna.keySet()) {
            if (p.hasNode(g)) {
                Node n = p.getNode(g);
                for (String m : miRegData.geneToMirna.get(g)) {
                    Node nm;
                    if (tmp.add(m)) {
                        nm = p.getNode(p.addNode(m, m, Pathway.NodeType.MICRORNA));
                    } else {
                        nm = p.getNode(m);
                    }
                    p.addEdge(nm, n, Pathway.EdgeType.MGRel, Pathway.EdgeSubType.MIRNA_REPRESSION);
                }
            }
        }
        n1 = p.countNodes();
        n2 = p.countEdges();
        //Add gene --o MicroRNA relationships
        for (String g : tfData.geneToMirna.keySet()) {
            if (p.hasNode(g)) {
                Node n = p.getNode(g);
                for (String m : tfData.geneToMirna.get(g)) {
                    Node nm;
                    if (tmp.add(m)) {
                        nm = p.getNode(p.addNode(m, m, Pathway.NodeType.MICRORNA));
                    } else {
                        nm = p.getNode(m);
                    }
                    int type = Integer.parseInt(tfData.interactionData.get(g + "-" + m)[1]);
                    p.addEdge(n, nm, Pathway.EdgeType.GMRel,
                            ((type > 0) ? Pathway.EdgeSubType.TFMIRNA_EXPRESSION :
                             ((type < 0) ? Pathway.EdgeSubType.TFMIRNA_REPRESSION : Pathway.EdgeSubType.MIXED)));
                }
            }
        }
    }

    //endregion

}
