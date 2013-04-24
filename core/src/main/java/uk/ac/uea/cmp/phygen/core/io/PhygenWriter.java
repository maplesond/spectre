package uk.ac.uea.cmp.phygen.core.io;

import uk.ac.uea.cmp.phygen.core.ds.SplitSystem;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Sarah_2
 * Date: 24/04/13
 * Time: 17:13
 * To change this template use File | Settings | File Templates.
 */
public interface PhygenWriter {

    String getExtension();
    void writeNetwork(File outFile, SplitSystem splitSystem) throws IOException;
    void writeTree(File outFile, SplitSystem splitSystem, double[][] treeWeights) throws IOException;
}
