package com.alaimos.BioClasses.Pathways;

import com.alaimos.Utils.Action;
import com.alaimos.other.Pair;

import java.util.*;

public class Pathway implements Cloneable {

    //region Custom Types and Map Initialization
    public static HashMap<EdgeSubType, Integer> interactionWeights     = new HashMap<EdgeSubType, Integer>();
    public static HashMap<String, EdgeType>     stringToEdgeTypeMap    = new HashMap<String, EdgeType>();
    public static HashMap<String, EdgeSubType>  stringToEdgeSubTypeMap = new HashMap<String, EdgeSubType>();

    static {
        stringToEdgeSubTypeMap.put("activation", EdgeSubType.ACTIVATION);
        stringToEdgeSubTypeMap.put("compound", EdgeSubType.COMPOUND);
        stringToEdgeSubTypeMap.put("binding/association", EdgeSubType.BINDING);
        stringToEdgeSubTypeMap.put("expression", EdgeSubType.EXPRESSION);
        stringToEdgeSubTypeMap.put("inhibition", EdgeSubType.INHIBITION);
        stringToEdgeSubTypeMap.put("activation_phosphorylation", EdgeSubType.ACTIVATION);
        stringToEdgeSubTypeMap.put("phosphorylation", EdgeSubType.PHOSPHORYLATION);
        stringToEdgeSubTypeMap.put("inhibition_phosphorylation", EdgeSubType.INHIBITION);
        stringToEdgeSubTypeMap.put("inhibition_dephosphorylation", EdgeSubType.INHIBITION);
        stringToEdgeSubTypeMap.put("dissociation", EdgeSubType.DISSOCIATION);
        stringToEdgeSubTypeMap.put("dephosphorylation", EdgeSubType.DEPHOSPHORYLATION);
        stringToEdgeSubTypeMap.put("activation_dephosphorylation", EdgeSubType.ACTIVATION);
        stringToEdgeSubTypeMap.put("state change", EdgeSubType.STATE_CHANGE);
        stringToEdgeSubTypeMap.put("activation_indirect effect", EdgeSubType.ACTIVATION);
        stringToEdgeSubTypeMap.put("inhibition_ubiquination", EdgeSubType.INHIBITION);
        stringToEdgeSubTypeMap.put("ubiquination", EdgeSubType.UBIQUITINATION);
        stringToEdgeSubTypeMap.put("expression_indirect effect", EdgeSubType.EXPRESSION);
        stringToEdgeSubTypeMap.put("inhibition_indirect effect", EdgeSubType.INHIBITION);
        stringToEdgeSubTypeMap.put("repression", EdgeSubType.REPRESSION);
        stringToEdgeSubTypeMap.put("dissociation_phosphorylation", EdgeSubType.DISSOCIATION);
        stringToEdgeSubTypeMap.put("indirect effect_phosphorylation", EdgeSubType.PHOSPHORYLATION);
        stringToEdgeSubTypeMap.put("activation_binding/association", EdgeSubType.ACTIVATION);
        stringToEdgeSubTypeMap.put("indirect effect", EdgeSubType.INDIRECT_EFFECT);
        stringToEdgeSubTypeMap.put("activation_compound", EdgeSubType.ACTIVATION);
        stringToEdgeSubTypeMap.put("activation_ubiquination", EdgeSubType.ACTIVATION);
        stringToEdgeSubTypeMap.put("methylation", EdgeSubType.METHYLATION);
        stringToEdgeSubTypeMap.put("ubiquitination", EdgeSubType.UBIQUITINATION);
        stringToEdgeSubTypeMap.put("unknown", EdgeSubType.UNKNOWN);
        stringToEdgeTypeMap.put("ecrel", EdgeType.ECRel);
        stringToEdgeTypeMap.put("pprel", EdgeType.PPRel);
        stringToEdgeTypeMap.put("gerel", EdgeType.GERel);
        stringToEdgeTypeMap.put("pcrel", EdgeType.PCRel);
        interactionWeights.put(EdgeSubType.ACTIVATION, 1);
        interactionWeights.put(EdgeSubType.COMPOUND, 0);
        interactionWeights.put(EdgeSubType.BINDING, 0);
        interactionWeights.put(EdgeSubType.EXPRESSION, 1);
        interactionWeights.put(EdgeSubType.INHIBITION, -1);
        interactionWeights.put(EdgeSubType.PHOSPHORYLATION, 0);
        interactionWeights.put(EdgeSubType.DISSOCIATION, 0);
        interactionWeights.put(EdgeSubType.DEPHOSPHORYLATION, 0);
        interactionWeights.put(EdgeSubType.STATE_CHANGE, 0);
        interactionWeights.put(EdgeSubType.UBIQUITINATION, 0);
        interactionWeights.put(EdgeSubType.REPRESSION, -1);
        interactionWeights.put(EdgeSubType.INDIRECT_EFFECT, 0);
        interactionWeights.put(EdgeSubType.GLYCOSYLATION, 0);
        interactionWeights.put(EdgeSubType.METHYLATION, 0);
        interactionWeights.put(EdgeSubType.MISSING, 0);
        interactionWeights.put(EdgeSubType.HIDDEN_COMPOUND, 0);
        interactionWeights.put(EdgeSubType.UNKNOWN, 0);
        interactionWeights.put(EdgeSubType.TFMIRNA_EXPRESSION, 1);
        interactionWeights.put(EdgeSubType.TFMIRNA_REPRESSION, -1);
        interactionWeights.put(EdgeSubType.MIRNA_REPRESSION, -1);
        interactionWeights.put(EdgeSubType.MIXED, 0);
    }

    public enum NodeType {
        GENE,
        MICRORNA,
        OTHER
    }

    public enum EdgeType {
        ECRel,  //enzyme-enzyme relation, indicating two enzymes catalyzing successive reaction steps
        PPRel,  //protein-protein interaction, such as binding and modification
        GERel,  //gene expression interaction, indicating relation of transcription factor and target gene product
        PCRel,  //protein-compound interaction
        MGRel,  //MicroRNA-gene regulation
        GMRel   //TranscriptionFactor-gene regulation
    }

    public enum EdgeSubType {
        COMPOUND,           //
        HIDDEN_COMPOUND,    //
        ACTIVATION,         //-->
        INHIBITION,         //--|
        EXPRESSION,         //-->
        REPRESSION,         //--|
        INDIRECT_EFFECT,    //..>
        STATE_CHANGE,       //...
        BINDING,            //---
        DISSOCIATION,       //-+-
        MISSING,            //-/-
        PHOSPHORYLATION,    //+p
        DEPHOSPHORYLATION,  //-p
        GLYCOSYLATION,      //+g
        UBIQUITINATION,     //+u
        METHYLATION,        //+m
        UNKNOWN,            //unknown
        TFMIRNA_EXPRESSION, //TF->MIRNA interaction
        TFMIRNA_REPRESSION, //TF-|MIRNA interaction
        MIRNA_REPRESSION,   //MIRNA-|GENE interaction
        MIXED               //a mixed interaction in TF -o MicroRNA regulation
    }
    //endregion

    //region Pathway description fields
    private String id       = null;
    private String name     = null;
    private String organism = null;
    private String image    = null;
    private String url      = null;
    //endregion

    //region Pathway Graph Fields
    private ArrayList<Node>                             nodes        = new ArrayList<Node>();
    private ArrayList<Edge>                             edges        = new ArrayList<Edge>();
    public  HashMap<String, Integer>                    idNodesIndex = new HashMap<String, Integer>();
    private HashMap<Node, Integer>                      nodesIndex   = new HashMap<Node, Integer>();
    private HashMap<Integer, HashMap<Integer, Integer>> outList      =
            new HashMap<Integer, HashMap<Integer, Integer>>(); //list of outgoing edges from a node
    private HashMap<Integer, HashMap<Integer, Integer>> inList       =
            new HashMap<Integer, HashMap<Integer, Integer>>(); //list of ingoing edged to a node
    //endregion

    //region Internal Classes

    class NodeImpl implements Node, Comparable<Node> {

        private String   entryId;
        private String   name;
        private NodeType type;

        public NodeImpl() {
            //do nothing
        }

        public NodeImpl(String entryId, String name, NodeType type) {
            this.entryId = entryId;
            this.name = name;
            this.type = type;
        }

        public String getEntryId() {
            return entryId;
        }

        public Node setEntryId(String entryId) {
            this.entryId = entryId;
            return this;
        }

        public String getName() {
            return name;
        }

        public Node setName(String name) {
            this.name = name;
            return this;
        }

        public NodeType getType() {
            return type;
        }

        public Node setType(NodeType type) {
            this.type = type;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof NodeImpl)) return false;
            NodeImpl node = (NodeImpl) o;
            return entryId.equals(node.entryId);
        }

        @Override
        public int hashCode() {
            return entryId.hashCode();
        }

        @Override
        public int compareTo(Node o) {
            return entryId.compareTo(o.getEntryId());
        }

        @Override
        public String toString() {
            return "Node{" +
                    "entryId='" + entryId + '\'' +
                    ", name='" + name + '\'' +
                    ", type=" + type +
                    '}';
        }

        public int getIndex() {
            return getNodeIndex(this);
        }

        public List<Node> parents() {
            return getParents(this);
        }

        public List<Node> children() {
            return getChildren(this);
        }

        public List<Node> upstream() {
            return upstreamNodes(this);
        }

        public List<Node> downstream() {
            return downStreamNodes(this);
        }

        @Override
        @SuppressWarnings("unchecked")
        public Object clone() {
            NodeImpl nn = new NodeImpl();
            nn.entryId = new String(entryId);
            nn.name = new String(name);
            nn.type = type;
            return nn;
        }

    }

    class EdgeImpl implements Edge {

        private Node        start;
        private Node        end;
        private EdgeType    type;
        private EdgeSubType subType;
        private int         weight;

        public EdgeImpl() {
            //do nothing
        }

        public EdgeImpl(Node start, Node end, EdgeType type, EdgeSubType subType) {
            this.start = start;
            this.end = end;
            this.type = type;
            this.subType = subType;
            this.weight = interactionWeights.get(subType);
        }

        public Node getStart() {
            return start;
        }

        public Edge setStart(Node start) {
            this.start = start;
            return this;
        }

        public Node getEnd() {
            return end;
        }

        public Edge setEnd(Node end) {
            this.end = end;
            return this;
        }

        public EdgeType getType() {
            return type;
        }

        public Edge setType(EdgeType type) {
            this.type = type;
            return this;
        }

        public EdgeSubType getSubType() {
            return subType;
        }

        public Edge setSubType(EdgeSubType subType) {
            this.subType = subType;
            this.weight = interactionWeights.get(this.subType);
            return this;
        }

        public Edge addSubType(EdgeSubType subType) {
            //There is another edge with another type so we modify only the total weight
            this.weight += interactionWeights.get(subType);
            return this;
        }

        public int getWeight() {
            return this.weight;
        }

        public boolean partialEquals(Edge e) {
            if (e == null) return false;
            return (this == e || (start.equals(e.getStart()) && end.equals(e.getEnd())));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof EdgeImpl)) return false;
            EdgeImpl edge = (EdgeImpl) o;
            if (!end.equals(edge.end)) return false;
            if (!start.equals(edge.start)) return false;
            if (subType != edge.subType) return false;
            if (type != edge.type) return false;
            return true;
        }

        @Override
        public int hashCode() {
            int result = start.hashCode();
            result = 31 * result + end.hashCode();
            result = 31 * result + type.hashCode();
            result = 31 * result + subType.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "Edge{" +
                    "from=" + start +
                    ", to=" + end +
                    ", type=" + type +
                    ", subType=" + subType +
                    ", weight=" + weight +
                    '}';
        }

        @Override
        @SuppressWarnings("unchecked")
        public Object clone() {
            EdgeImpl ne = new EdgeImpl();
            ne.start = (Node) start.clone();
            ne.end = (Node) end.clone();
            ne.type = type;
            ne.subType = subType;
            ne.weight = weight;
            return ne;
        }
    }

    //endregion

    //region Utility Methods
    public static void setInteractionWeights(HashMap<EdgeSubType, Integer> newWeights) {
        for (Map.Entry<EdgeSubType, Integer> l : newWeights.entrySet()) {
            if (interactionWeights.containsKey(l.getKey())) {
                interactionWeights.remove(l.getKey());
            }
            interactionWeights.put(l.getKey(), l.getValue());
        }
    }

    public Node newNode() {
        return new NodeImpl();
    }

    public Edge newEdge() {
        return new EdgeImpl();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object clone() {
        Pathway np = new Pathway();
        np.id = new String(id);
        np.name = new String(name);
        np.organism = new String(organism);
        np.image = new String(image);
        np.url = new String(url);
        np.nodes = (ArrayList<Node>) nodes.clone();
        np.edges = (ArrayList<Edge>) edges.clone();
        np.idNodesIndex = (HashMap<String, Integer>) idNodesIndex.clone();
        np.nodesIndex = (HashMap<Node, Integer>) nodesIndex.clone();
        np.outList = (HashMap<Integer, HashMap<Integer, Integer>>) outList.clone();
        np.inList = (HashMap<Integer, HashMap<Integer, Integer>>) inList.clone();
        return np;
    }

    //endregion

    //region Constructors

    public Pathway() {
        //do nothing
    }

    public Pathway(String id, String name) {
        String[] d = id.split(":");
        this.id = id;
        this.name = name;
        this.organism = d[0];
    }
    //endregion

    //region Pathway description Setters and Getters
    public boolean isEmpty() {
        return (id == null && name == null && organism == null && image == null && url == null);
    }

    public String getId() {
        return id;
    }

    public Pathway setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Pathway setName(String name) {
        this.name = name;
        return this;
    }

    public String getOrganism() {
        return organism;
    }

    public Pathway setOrganism(String organism) {
        this.organism = organism;
        return this;
    }

    public String getImage() {
        return image;
    }

    public Pathway setImage(String image) {
        this.image = image;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Pathway setUrl(String url) {
        this.url = url;
        return this;
    }
    //endregion

    //region Indexing helpers
    private int indexNode(Node n, boolean indexed) {
        if (indexed) {
            return nodesIndex.get(n);
        } else {
            int idx = this.nodes.indexOf(n);
            idNodesIndex.put(n.getEntryId(), idx);
            nodesIndex.put(n, idx);
            inList.put(idx, new HashMap<Integer, Integer>());
            outList.put(idx, new HashMap<Integer, Integer>());
            return idx;
        }
    }

    private int indexEdge(Edge e, boolean indexed) {
        int i1 = indexNode(e.getStart(), true),
                i2 = indexNode(e.getEnd(), true);
        if (indexed) {
            return outList.get(i1).get(i2);
        } else {
            int idx = this.edges.indexOf(e);
            outList.get(i1).put(i2, idx);
            inList.get(i2).put(i1, idx);
            return idx;
        }
    }
    //endregion

    //region Everything about nodes
    public int addNode(String entryId, String name, String type) {
        if (type.equalsIgnoreCase("gene")) {
            return addNode(new NodeImpl(entryId, name, NodeType.GENE));
        } else if (type.equalsIgnoreCase("microrna")) {
            return addNode(new NodeImpl(entryId, name, NodeType.MICRORNA));
        } else {
            return addNode(new NodeImpl(entryId, name, NodeType.OTHER));
        }
    }

    public int addNode(String entryId, String name, NodeType type) {
        return addNode(new NodeImpl(entryId, name, type));
    }

    public int addNode(Node n) {
        boolean indexed = true;
        if (!this.nodes.contains(n)) {
            this.nodes.add(n);
            indexed = false;
        }
        return indexNode(n, indexed);
    }

    public Node getNode(int n) {
        return this.nodes.get(n);
    }

    public Node getNode(String id) {
        return getNode(idNodesIndex.get(id));
    }

    public int getNodeIndex(Node n) {
        return indexNode(n, true);
    }

    public int getNodeIndex(String n) {
        return idNodesIndex.get(n);
    }

    public boolean hasNode(int n) {
        return nodes.size() > n;
    }

    public boolean hasNode(Node n) {
        return nodesIndex.containsKey(n) && hasNode(nodesIndex.get(n));
    }

    public boolean hasNode(String n) {
        return idNodesIndex.containsKey(n) && hasNode(idNodesIndex.get(n));
    }

    public int inDegree(Node n) {
        return inDegree(nodesIndex.get(n));
    }

    public int inDegree(int n) {
        return inList.get(n).size();
    }

    public int outDegree(Node n) {
        return outDegree(nodesIndex.get(n));
    }

    public int outDegree(int n) {
        return outList.get(n).size();
    }

    public List<Node> getParents(Node n) {
        return getParents(getNodeIndex(n));
    }

    public List<Node> getParents(int n) {
        ArrayList<Node> results = new ArrayList<Node>(inList.get(n).size());
        for (int ni : inList.get(n).keySet()) {

            results.add(nodes.get(ni));
        }
        return results;
    }

    public List<Node> getChildren(Node n) {
        return getChildren(getNodeIndex(n));
    }

    public List<Node> getChildren(int n) {
        ArrayList<Node> results = new ArrayList<Node>(outList.get(n).size());
        for (int ni : outList.get(n).keySet()) {
            results.add(nodes.get(ni));
        }
        return results;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    //endregion

    //region Everything about edges
    public int addEdge(int start, int end, String type, String subType) {
        return addEdge(getNode(start), getNode(end), type, subType);
    }

    public int addEdge(int start, int end, EdgeType type, EdgeSubType subType) {
        return addEdge(getNode(start), getNode(end), type, subType);
    }

    public int addEdge(Node start, Node end, String type, String subType) {
        if (subType.equalsIgnoreCase("missing interaction")) {
            return -1;
        }
        return addEdge(new EdgeImpl(start, end,
                stringToEdgeTypeMap.get(type.toLowerCase()), stringToEdgeSubTypeMap.get(subType.toLowerCase())));
    }

    public int addEdge(Node start, Node end, EdgeType type, EdgeSubType subType) {
        return addEdge(new EdgeImpl(start, end, type, subType));
    }

    public int addEdge(Edge e) {
        int i1 = indexNode(e.getStart(), true),
                i2 = indexNode(e.getEnd(), true);
        boolean indexed = true;
        if (outList.get(i1).containsKey(i2)) {
            Edge e1 = edges.get(outList.get(i1).get(i2));
            if (!e.equals(e1) && e.partialEquals(e1)) {
                e1.addSubType(e.getSubType());
            }
        } else {
            edges.add(e);
            indexed = false;
        }
        return indexEdge(e, indexed);
    }

    public Edge getEdge(int e) {
        return edges.get(e);
    }

    public Edge getEdge(int start, int end) {
        return edges.get(getEdgeIndex(start, end));
    }

    public Edge getEdge(Node start, Node end) {
        return edges.get(getEdgeIndex(start, end));
    }

    public int getEdgeIndex(Edge e) {
        return indexEdge(e, true);
    }

    public int getEdgeIndex(int start, int end) {
        return outList.get(start).get(end);
    }

    public int getEdgeIndex(Node start, Node end) {
        return outList.get(getNodeIndex(start)).get(getNodeIndex(end));
    }

    public boolean hasEdge(Edge e) {
        return hasNode(e.getStart()) && hasNode(e.getEnd()) &&
                hasEdge(getNodeIndex(e.getStart()), getNodeIndex(e.getEnd()));
    }

    public boolean hasEdge(int e) {
        return edges.size() > e;
    }

    public boolean hasEdge(int start, int end) {
        return outList.containsKey(start) && outList.get(start).containsKey(end)
                && hasEdge(outList.get(start).get(end));
    }

    public boolean hasEdge(Node start, Node end) {
        return hasNode(start) && hasNode(end) && hasEdge(getNodeIndex(start), getNodeIndex(end));
    }

    public List<Edge> getEdges() {
        return edges;
    }
    //endregion

    //region Upstream Nodes Retrieval
    public List<Node> upstreamNodes(Node n) {
        return upstreamNodes(getNodeIndex(n));
    }

    public List<Node> upstreamNodes(int n) {
        HashSet<Node> result = new HashSet<Node>();
        traverseUpstream(result, n);
        return new ArrayList<Node>(result);
    }

    //endregion

    //region Downstream Nodes Retrieval
    public List<Node> downStreamNodes(Node n) {
        return downStreamNodes(getNodeIndex(n));
    }

    public List<Node> downStreamNodes(int n) {
        HashSet<Node> result = new HashSet<Node>();
        traverseDownstream(result, n);
        return new ArrayList<Node>(result);
    }
    //endregion

    //region Counting
    public int countNodes() {
        return nodes.size();
    }

    public int countEdges() {
        return edges.size();
    }
    //endregion

    //region Traversal Upstream

    public void runUpstream(Node currentNode, Action<Node> action) {
        runUpstream(getNodeIndex(currentNode), action);
    }

    public void runUpstream(int currentNode, Action<Node> action) {
        Stack<Integer> traversalGuide = new Stack<Integer>();
        traversalGuide.push(currentNode);
        while (!traversalGuide.isEmpty()) {
            int ni = traversalGuide.pop();
            Node n = nodes.get(ni);
            Action.ActionResult r = action.run(n);
            if (r == Action.ActionResult.STOP) {
                break;
            } else if (r == Action.ActionResult.CONTINUE) {
                if (inDegree(ni) > 0) {
                    for (Map.Entry<Integer, Integer> un : inList.get(ni).entrySet()) {
                        traversalGuide.push(un.getKey());
                    }
                }
            }
        }
    }

    public void traverseUpstream(final Collection<Node> results, final Node currentNode) {
        traverseUpstream(results, currentNode, true);
    }

    public void traverseUpstream(final Collection<Node> results, final int currentNode) {
        traverseUpstream(results, currentNode, true);
    }

    public void traverseUpstream(final Collection<Node> results, final Node currentNode, final boolean markTraversal) {
        traverseUpstream(results, getNodeIndex(currentNode), markTraversal);
    }

    public void traverseUpstream(final Collection<Node> results, final int currentNode, final boolean markTraversal) {
        final Node start = getNode(currentNode);
        final HashSet<Integer> marked = new HashSet<Integer>();
        runUpstream(currentNode, new Action<Node>() {
            @Override
            public ActionResult run(Node o) {
                if (o.equals(start)) {
                    if (markTraversal) {
                        if (!marked.add(currentNode)) {
                            return ActionResult.PRUNE;
                        }
                    }
                    return ActionResult.CONTINUE;
                }
                if (!markTraversal) {
                    if (results.add(o)) {
                        return ActionResult.CONTINUE;
                    } else {
                        return ActionResult.PRUNE;
                    }
                } else {
                    if (marked.add(getNodeIndex(o))) {
                        results.add(o);
                        return ActionResult.CONTINUE;
                    } else {
                        return ActionResult.PRUNE;
                    }
                }
            }
        });
    }

    //endregion

    //region Traversal Downstream

    public void runDownstream(Node currentNode, Action<Node> action) {
        runDownstream(getNodeIndex(currentNode), action);
    }

    public void runDownstream(int currentNode, Action<Node> action) {
        Stack<Integer> traversalGuide = new Stack<Integer>();
        traversalGuide.push(currentNode);
        while (!traversalGuide.isEmpty()) {
            int ni = traversalGuide.pop();
            Node n = nodes.get(ni);
            Action.ActionResult r = action.run(n);
            if (r == Action.ActionResult.STOP) {
                break;
            } else if (r == Action.ActionResult.CONTINUE) {
                if (outDegree(ni) > 0) {
                    for (Map.Entry<Integer, Integer> un : outList.get(ni).entrySet()) {
                        traversalGuide.push(un.getKey());
                    }
                }
            }
        }
    }

    public void traverseDownstream(final Collection<Node> results, final Node currentNode) {
        traverseDownstream(results, currentNode, true);
    }

    public void traverseDownstream(final Collection<Node> results, final int currentNode) {
        traverseDownstream(results, currentNode, true);
    }

    public void traverseDownstream(final Collection<Node> results, final Node currentNode,
                                   final boolean markTraversal) {
        traverseDownstream(results, getNodeIndex(currentNode), markTraversal);
    }

    public void traverseDownstream(final Collection<Node> results, final int currentNode, final boolean markTraversal) {
        final Node start = getNode(currentNode);
        final HashSet<Integer> marked = new HashSet<Integer>();
        runUpstream(currentNode, new Action<Node>() {
            @Override
            public ActionResult run(Node o) {
                if (o.equals(start)) {
                    if (markTraversal) {
                        if (!marked.add(currentNode)) {
                            return ActionResult.PRUNE;
                        }
                    }
                    return ActionResult.CONTINUE;
                }
                if (!markTraversal) {
                    if (results.add(o)) {
                        return ActionResult.CONTINUE;
                    } else {
                        return ActionResult.PRUNE;
                    }
                } else {
                    if (marked.add(getNodeIndex(o))) {
                        results.add(o);
                        return ActionResult.CONTINUE;
                    } else {
                        return ActionResult.PRUNE;
                    }
                }
            }
        });
    }

    @Override
    public String toString() {
        return "Pathway{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", organism='" + organism + '\'' +
                '}';
    }

    //endregion

    private boolean recursiveCycleFinder(int v, boolean[] visited, boolean[] recStack,
                                         HashSet<Pair<Integer, Integer>> edges) {
        if (!visited[v]) {
            visited[v] = true;
            recStack[v] = true;
            int i;
            for (Node n : getChildren(v)) {
                i = getNodeIndex(n);
                if (!visited[i] && recursiveCycleFinder(i, visited, recStack, edges)) {
                    edges.add(new Pair<>(v, i));
                    return true;
                } else if (recStack[i]) {
                    edges.add(new Pair<>(v, i));
                    return true;
                }
            }
        }
        recStack[v] = false;
        return false;
    }

    public HashSet<Pair<Integer, Integer>> findCycles() {
        boolean[] visited = new boolean[countNodes()];
        boolean[] recStack = new boolean[countNodes()];
        Arrays.fill(visited, false);
        Arrays.fill(recStack, false);
        HashSet<Pair<Integer, Integer>> cycles = new HashSet<>();
        for (int i = 0; i < visited.length; i++) {
            recursiveCycleFinder(i, visited, recStack, cycles);
        }
        return cycles;
    }

    private boolean recursiveHasCycle(int v, boolean[] visited, boolean[] recStack) {
        if (!visited[v]) {
            visited[v] = true;
            recStack[v] = true;
            int i;
            for (Node n : getChildren(v)) {
                i = nodesIndex.get(n);
                if (!visited[i] && recursiveHasCycle(i, visited, recStack)) {
                    return true;
                } else if (recStack[i]) {
                    return true;
                }
            }
        }
        recStack[v] = false;
        return false;
    }

    public boolean isCyclic() {
        boolean[] visited = new boolean[countNodes()];
        boolean[] recStack = new boolean[countNodes()];
        Arrays.fill(visited, false);
        Arrays.fill(recStack, false);
        for (int i = 0; i < visited.length; i++) {
            if (recursiveHasCycle(i, visited, recStack)) {
                return true;
            }
        }
        return false;
    }

}
