package com.alaimos.MITHrIL.Data.Pathway.Impl;

import com.alaimos.MITHrIL.Data.Pathway.Interface.GraphInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.PathwayInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.WeightComputationInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.1.0.0
 * @since 07/12/2015
 */
public class Pathway implements PathwayInterface {

    private static final long serialVersionUID = 2374083955754742844L;
    protected String id = null;
    protected String name = null;
    protected String image = null;
    protected String url = null;
    protected GraphInterface graph = null;
    protected ArrayList<String> categories = new ArrayList<>();
    protected WeightComputationInterface weightComputation = null;
    protected boolean hidden = false;

    public Pathway() {
    }

    public Pathway(String id, String name, GraphInterface graph) {
        this.id = id;
        this.name = name;
        this.setGraph(graph);
    }

    public Pathway(String id, String name, GraphInterface graph, ArrayList<String> categories) {
        this(id, name, graph);
        this.categories.addAll(categories);
    }

    public Pathway(String id, String name, GraphInterface graph, String categories) {
        this(id, name, graph);
        String[] cats = categories.split(";");
        for (String c : cats) {
            if (!c.trim().isEmpty()) this.categories.add(c.trim());
        }
    }

    @Override
    public boolean isEmpty() {
        return (id == null && name == null && image == null && url == null);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public PathwayInterface setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public PathwayInterface setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getImage() {
        return this.image;
    }

    @Override
    public PathwayInterface setImage(String image) {
        this.image = image;
        return this;
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public PathwayInterface setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public boolean hasGraph() {
        return (this.graph != null);
    }

    @Override
    public GraphInterface getGraph() {
        return this.graph;
    }

    @Override
    public PathwayInterface setGraph(GraphInterface graph) {
        this.graph = graph;
        if (graph != null) {
            graph.setOwner(this);
        }
        return this;
    }

    @Override
    public List<String> getCategories() {
        return this.categories;
    }

    @Override
    public PathwayInterface addCategory(String category) {
        if (!category.isEmpty()) {
            this.categories.add(category);
        }
        return this;
    }

    @Override
    public PathwayInterface addCategories(List<String> category) {
        category.stream().filter(s -> s != null && !s.isEmpty()).forEachOrdered(this.categories::add);
        return this;
    }

    @Override
    public PathwayInterface clearCategories() {
        this.categories.clear();
        return this;
    }

    @Override
    public PathwayInterface setCategory(String category) {
        return this.clearCategories().addCategory(category);
    }

    @Override
    public PathwayInterface setCategories(List<String> categories) {
        return this.clearCategories().addCategories(categories);
    }

    @Override
    public boolean hasCategory(String category) {
        return this.categories.contains(category);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pathway)) return false;
        Pathway pathway = (Pathway) o;
        return Objects.equals(id, pathway.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Clone the object
     *
     * @return my clone
     */
    @SuppressWarnings("unchecked")
    public Object clone() {
        Pathway clone;
        try {
            clone = (Pathway) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e.getMessage());
        }
        if (graph != null) {
            clone.setGraph((GraphInterface) graph.clone());
        }
        clone.categories = new ArrayList<>(categories);
        clone.weightComputation = this.weightComputation; //Weight computation in never cloned
        return clone;
    }

    @Override
    public PathwayInterface setDefaultWeightComputation(WeightComputationInterface defaultWeightComputation,
                                                        boolean changeAll) {
        WeightComputationInterface old = this.weightComputation;
        this.weightComputation = defaultWeightComputation;
        if (this.weightComputation != old && this.graph != null && changeAll) {
            this.graph.setDefaultWeightComputation(defaultWeightComputation);
        }
        return this;
    }

    @Override
    public PathwayInterface setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public WeightComputationInterface getDefaultWeightComputation() {
        return weightComputation;
    }
}
