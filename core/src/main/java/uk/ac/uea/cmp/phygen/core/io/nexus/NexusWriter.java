/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.uea.cmp.phygen.core.io.nexus;


import org.apache.commons.io.FileUtils;
import uk.ac.uea.cmp.phygen.core.ds.Distances;
import uk.ac.uea.cmp.phygen.core.ds.split.CircularSplitSystem;
import uk.ac.uea.cmp.phygen.core.ds.split.Split;
import uk.ac.uea.cmp.phygen.core.ds.split.SplitBlock;
import uk.ac.uea.cmp.phygen.core.ds.split.SplitSystem;
import uk.ac.uea.cmp.phygen.core.ds.TreeWeights;
import uk.ac.uea.cmp.phygen.core.io.PhygenWriter;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Used to handle streaming data to Nexus format file from SplitSystem objects
 * and other splits and weight data. Can create nexus files designed to
 * represent a network, tree or weighted tree.
 *
 * Adapted Version
 *
 * @author Dan
 */
public class NexusWriter implements PhygenWriter {

    /**
     * Creates a network representation of the split system that was provided in
     * the constructor and saves the data to disk.
     *
     * @throws java.io.IOException Thrown if there were any problems writing out the
     * data to the file.
     */
    @Override
    public void writeNetwork(File file, SplitSystem ss, Distances distances) throws IOException {

        if (file == null) {
            throw new NullPointerException("Must specify a nexus file to write.");
        }

        if (file.exists() && !file.canWrite()) {
            throw new IOException("Cannot overwrite existing nexus file: " + file.getPath());
        }

        if (ss == null) {
            throw new NullPointerException("Must specify a split system to write.");
        }

        //For network, we consider a full split system and determine its splits by its weight (non-zero ones)
        //this is not dependent on ss, what ever ss is?
        int n = ss.getNbTaxa();

        int numberOfSplits = ss.getSplits().size();
        int currentSplitIndex = 1;
        StringBuilder outputString = new StringBuilder("");
        /*double[] splitweights = new double[ss.getSplits().size()];

        for (int i = 0; i < ss.getSplits().size(); i++) {

            splitweights[i] = 1.0;

        }*/

        outputString.append("#nexus\n\n\nBEGIN Taxa;\nDIMENSIONS ntax=").append(n).append(";\nTAXLABELS\n");

        int idx = 1;
        for (String taxa : distances.getTaxaSet()) {
            outputString.append("[").append(idx++).append("] '").append(taxa).append("'\n");
        }

        outputString.append(";\nEND; [Taxa]\n\nBEGIN Splits;\nDIMENSIONS ntax=").append(n).append(" nsplits=").append(numberOfSplits).append(";\nFORMAT ").append("labels=no weights=yes confidences=no intervals").append("=no;\nPROPERTIES fit=-1.0 cyclic;\nCYCLE");

        for (int i = 0; i < n; i++) {
            outputString.append(" ").append(ss.getTaxaIndexAt(i));
        }

        outputString.append(";\nMATRIX\n");

        for (int i = 0; i < ss.getSplits().size(); i++) {

            Split s = ss.getSplits().get(i);
            double weighting = s.getWeight();
            SplitBlock sb = s.getASide();

            if (weighting != 0.0) {
                outputString.append("[").append(currentSplitIndex).append(", size=").append(sb.size()).append("]\t").append(weighting).append("\t");
                outputString.append(" ").append(sb.toString());

                currentSplitIndex++;
                outputString.append(",\n");
            }
        }

        outputString.append(";\nEND; [Splits]");
        FileUtils.writeStringToFile(file, outputString.toString());
    }

    /**
     * Writes a tree file in nexus file format, using explictly specified tree weights.
     *
     * @param treeWeights matrix that maps a weighting value to an edge of the tree
     */
    @Override
    public void writeTree(File file, SplitSystem ss, Distances distances, TreeWeights treeWeights) throws IOException {

        List<Split> splits = ss.getSplits();
        int nbTaxa = ss.getNbTaxa();

        int[] permutationInvert = ss.invertOrdering();
        StringBuilder outputString = new StringBuilder("");

         outputString.append("#nexus\n\n\nBEGIN Taxa;\nDIMENSIONS ntax=").append(nbTaxa).append(";\nTAXLABELS\n");

        for (int i = 0; i < nbTaxa; i++) {
            outputString.append("[").append(i + 1).append("] '").append(distances.getTaxa(i)).append("'\n");
        }

        outputString.append(";\nEND; [Taxa]\n\nBEGIN Splits;\nDIMENSIONS ntax=").append(nbTaxa).append(" nsplits=").append(splits.size()).append(";\nFORMAT ").append("labels=no weights=yes confidences=no intervals").append("=no;\nPROPERTIES fit=-1.0 cyclic;\nCYCLE");

        for (int i = 0; i < nbTaxa; i++) {
            outputString.append(" ").append(ss.getTaxaIndexAt(i));
        }

        outputString.append(";\nMATRIX\n");


        for (int i = 0; i < splits.size(); i++) {

            SplitBlock sb = splits.get(i).getASide();
            int k = permutationInvert[sb.get(0)];
            int l = permutationInvert[sb.get(sb.size() - 1)];

            if (k == 0) {
                k = nbTaxa - 1;
                outputString.append("[").append(i + 1).append(", size=").append(k - l).append("]\t").append(treeWeights.getAt(k,l)).append("\t");
            } else {
                k--;
                if ((l < nbTaxa - 1) && (k >= l)) {
                    outputString.append("[").append(i + 1).append(", size=").append(k - l).append("]\t").append(treeWeights.getAt(k,l)).append("\t");
                } else {
                    outputString.append("[").append(i + 1).append(", size=").append(l - k).append("]\t").append(treeWeights.getAt(l,k)).append("\t");
                }
            }

            outputString.append(splits.get(i).getASide().toString());
            outputString.append(",\n");
        }

        outputString.append(";\nEND; [Splits]");

        FileUtils.writeStringToFile(file, outputString.toString());
    }

}