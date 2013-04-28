package uk.ac.uea.cmp.phygen.core.io;

import uk.ac.uea.cmp.phygen.core.ds.Distances;
import uk.ac.uea.cmp.phygen.core.ds.split.SplitSystem;
import uk.ac.uea.cmp.phygen.core.ds.TreeWeights;

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

    void writeNetwork(File outFile, SplitSystem splitSystem, Distances distances) throws IOException;
    void writeTree(File outFile, SplitSystem splitSystem, Distances distances, TreeWeights treeWeights) throws IOException;
}
