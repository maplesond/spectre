/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.uea.cmp.phygen.core.io.nexus;


import org.apache.commons.io.FileUtils;
import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.phygen.core.ds.split.Split;
import uk.ac.uea.cmp.phygen.core.ds.split.SplitBlock;
import uk.ac.uea.cmp.phygen.core.ds.split.SplitSystem;
import uk.ac.uea.cmp.phygen.core.io.PhygenWriter;

import java.io.File;
import java.io.IOException;

/**
 * Used to handle streaming data to Nexus format file from SplitSystem objects
 * and other splits and weight data. Can create nexus files designed to
 * represent a network, tree or weighted tree.
 * <p/>
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
     *                             data to the file.
     */
    @Override
    public void writeSplitSystem(File file, SplitSystem ss) throws IOException {

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
        for (String taxa : ss.getTaxa()) {
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

        // Save
        FileUtils.writeStringToFile(file, outputString.toString());
    }

    @Override
    public void writeDistanceMatrix(File file, DistanceMatrix distanceMatrix) throws IOException {

        final int n = distanceMatrix.size();

        StringBuilder fileContent = new StringBuilder(7 * n * n);

        fileContent.append("#nexus\n\nBEGIN Distances;\nDIMENSIONS ntax=");
        fileContent.append(n);
        fileContent.append(";\nFORMAT labels=left diagonal triangle=lower;\n"
                + "MATRIX\n");

        for (int i = 1; i <= n; i++) {
            fileContent.append("[");
            fileContent.append(i);
            fileContent.append("] '");
            fileContent.append(i);
            fileContent.append("'                     ");

            for (int j = 1; j < i; j++) {
                double dist = distanceMatrix.getDistance(i - 1, j - 1);

                fileContent.append(" ");
                fileContent.append(dist);
            }

            fileContent.append(" 0.0");
            fileContent.append("\n");
        }

        fileContent.append(";\nEND; [DISTANCES]\n");

        // Save
        FileUtils.writeStringToFile(file, fileContent.toString());
    }

    public void writeNexusData(File outFile, NexusData nexusData) throws IOException {


        StringBuilder nexusString = new StringBuilder();

        final int N = nexusData.getNbTaxa();

        nexusString.append("#NEXUS\nBEGIN taxa;\nDIMENSIONS ntax=").append(N).append(";\nTAXLABELS\n");

        for (String taxon : nexusData.getTaxa()) {
            nexusString.append(taxon).append("\n");
        }

        nexusString.append(";\nEND;\n\nBEGIN st_splits;\nDIMENSIONS ntax=" + N + " nsplits=").append(nexusData.getNbSplits()).append(";\n");
        nexusString.append("FORMAT\nlabels\nweights\n;\nPROPERTIES\nFIT=100\nweakly compatible\ncyclic\n;\nCYCLE");

        for (int n = 0; n < N; n++) {
            nexusString.append(" ").append(nexusData.getCycleAt(n));
        }

        nexusString.append(";\nMATRIX\n");

        for (int n = 0; n < nexusData.getNbSplits(); n++) {

             // Add one for splitstree...
            nexusString.append(n + 1).append("   ").append(nexusData.getWeightAt(n)).append("  ");

            SplitBlock aSplit = nexusData.getSplitAt(n);

            for (int p = 0; p < aSplit.size(); p++) {

                // Add one for splitstree...
                nexusString.append(" ").append(aSplit.get(p));
            }

            nexusString.append(",\n");
        }

        nexusString.append(";\nEND;");


        // Save
        FileUtils.writeStringToFile(outFile, nexusString.toString());
    }

}