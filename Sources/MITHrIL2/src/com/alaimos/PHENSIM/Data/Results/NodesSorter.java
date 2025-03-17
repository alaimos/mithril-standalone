package com.alaimos.PHENSIM.Data.Results;

import com.alaimos.MITHrIL.Data.Pathway.Interface.PathwayInterface;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface NodesSorter {

    /**
     * Given a pathway sorts the list of its nodes and return the list.
     * After the first call of this method no changes will happen to the sorting.
     *
     * @param p A pathway interface
     * @return A list of sorted nodes
     */
    String[] getNodes(@NotNull PathwayInterface p);

}
