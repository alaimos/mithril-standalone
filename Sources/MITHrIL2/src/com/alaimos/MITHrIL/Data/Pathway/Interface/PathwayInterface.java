package com.alaimos.MITHrIL.Data.Pathway.Interface;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 06/12/2015
 */
public interface PathwayInterface extends Cloneable, WeightComputationAwareInterface<PathwayInterface>, Serializable {

    boolean isEmpty();

    String getId();

    PathwayInterface setId(String id);

    String getName();

    PathwayInterface setName(String name);

    String getImage();

    PathwayInterface setImage(String image);

    String getUrl();

    PathwayInterface setUrl(String url);

    boolean hasGraph();

    GraphInterface getGraph();

    PathwayInterface setGraph(GraphInterface graph);

    List<String> getCategories();

    PathwayInterface addCategory(String category);

    PathwayInterface addCategories(List<String> categories);

    PathwayInterface clearCategories();

    PathwayInterface setCategory(String category);

    PathwayInterface setCategories(List<String> categories);

    boolean hasCategory(String category);

    PathwayInterface setHidden(boolean hidden);

    boolean isHidden();

    @Nullable
    default List<NodeInterface> completePathway(Map<String, ?> expressionMap) {
        if (this.hasGraph()) {
            return this.getGraph().completePathway(expressionMap);
        }
        return null;
    }

    Object clone();

}
