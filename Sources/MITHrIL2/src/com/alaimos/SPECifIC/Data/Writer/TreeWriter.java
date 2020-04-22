package com.alaimos.SPECifIC.Data.Writer;

import com.alaimos.Commons.Writer.AbstractDataWriter;
import com.alaimos.Commons.Writer.DataWriterInterface;
import com.alaimos.SPECifIC.Data.Structures.VisitTree;

import java.io.File;
import java.io.PrintStream;
import java.util.Iterator;

/**
 * Write Driver Nodes to a file
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 08/01/2016
 */
public class TreeWriter extends AbstractDataWriter<VisitTree> {

    private boolean append = false;

    /**
     * Write data
     *
     * @param data the data that will be written into a file
     * @return this object for a fluent interface
     */
    @Override
    public DataWriterInterface<VisitTree> write(VisitTree data) {
        try (PrintStream ps = new PrintStream(getOutputStream(append))) {
            if (!append) {
                ps.println("#Node Id\tParent Id");
            }
            ps.println("#BEGIN TREE");
            for (Iterator<VisitTree> it = data.preorderIterator(); it.hasNext(); ) {
                VisitTree node = it.next();
                VisitTree parent = node.getParent();
                writeDelimited(ps, "\t", node.getObject().getNode().getId(),
                        (parent == null) ? "-" : parent.getObject().getNode().getId(),
                        node.getTreeAccumulator(),
                        node.getTreePValue());
                ps.println();
            }
            ps.println("#END TREE");
        }
        return this;
    }

    /**
     * Set filename and write data into it
     *
     * @param f    the file
     * @param data the data that will be written into a file
     * @return this object for a fluent interface
     */
    public DataWriterInterface<VisitTree> write(File f, VisitTree data) {
        this.append = false;
        return setFile(f).write(data);
    }

    /**
     * Set filename and write data into it
     *
     * @param f      the file
     * @param data   the data that will be written into a file
     * @param append append the data?
     * @return this object for a fluent interface
     */
    public DataWriterInterface<VisitTree> write(File f, VisitTree data, boolean append) {
        this.append = append;
        return setFile(f).write(data);
    }
}
