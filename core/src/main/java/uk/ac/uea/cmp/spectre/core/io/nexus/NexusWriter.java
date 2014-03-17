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
package uk.ac.uea.cmp.spectre.core.io.nexus;


import org.apache.commons.lang3.StringUtils;
import uk.ac.uea.cmp.spectre.core.ds.Alignment;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrixBuilder;
import uk.ac.uea.cmp.spectre.core.ds.split.Split;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitBlock;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitSystem;
import uk.ac.uea.cmp.spectre.core.io.AbstractPhygenWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Used to handle streaming data to Nexus format file from SplitSystem objects and other splits and length data. Can
 * create nexus files designed to represent a network, tree or weighted tree.  The class stores file content in java's
 * StringBuilder and the client can append content into this as they like.  This means that the whole file content is
 * stored in memory before writing to disk.  This could be a problem on low-memory systems, or if processing large files.
 * Consider optimising if we memory related problems are encountered.
 *
 * @author Dan
 */
public class NexusWriter extends AbstractPhygenWriter implements Appendable {

    /**
     * Stores the file contents before writing to disk.
     */
    private StringBuilder fileContent;

    /**
     * Creates a new NexusWriter and ensures we have a reasonable amount of memory to work with
     */
    public NexusWriter() {
        this.fileContent = new StringBuilder(2048);
    }


    /**
     * Creates a network representation of the split system that was provided in
     * the constructor and saves the data to disk.
     *
     * @throws java.io.IOException Thrown if there were any problems writing out the
     *                             data to the file.
     */
    @Override
    public void writeSplitSystem(File file, SplitSystem ss) throws IOException {

        // Clear out whatever was in the file content buffer before
        this.fileContent = new StringBuilder();

        // Construct file content
        this.appendHeader()
                .appendLine()
                .append(ss.getTaxa())
                .appendLine()
                .append(ss);

        // Save to disk
        this.write(file);
    }

    @Override
    public void writeDistanceMatrix(File file, DistanceMatrix distanceMatrix) throws IOException {

        // Clear out whatever was in the file content buffer before
        this.fileContent = new StringBuilder();

        // Construct file content
        this.appendHeader()
                .appendLine()
                .append(distanceMatrix.getTaxa())
                .appendLine()
                .append(distanceMatrix);

        // Save to disk
        this.write(file);
    }

    public void writeNexusData(File file, Nexus nexusData) throws IOException {


        // Clear out whatever was in the file content buffer before
        this.fileContent = new StringBuilder();

        // Construct file content
        this.appendHeader();

        if (nexusData.getTaxa() != null) {
            this.appendLine()
                    .append(nexusData.getTaxa());
        }

        if (nexusData.getDistanceMatrix() != null) {
            this.appendLine()
                    .append(nexusData.getDistanceMatrix());
        }

        /*if (nexusData.getCycle() != null) {

        }*/

        if (nexusData.getSplitSystem() != null) {
            this.appendLine();
            this.append(nexusData.getSplitSystem());
        }

        // Save to disk
        this.write(file);

        /*
        SplitSystem ss = nexusData.getSplitSystem();


        StringBuilder nexusString = new StringBuilder();

        final int N = nexusData.getNbTaxa();

        nexusString.append("#NEXUS\nBEGIN taxa;\nDIMENSIONS ntax=").append(N).append(";\nTAXLABELS\n");

        for (Taxon taxon : nexusData.getTaxa()) {
            nexusString.append(taxon.getName()).append("\n");
        }

        nexusString.append(";\nEND;\n\nBEGIN st_splits;\nDIMENSIONS ntax=" + N + " nsplits=").append(ss.getNbSplits()).append(";\n");
        nexusString.append("FORMAT\nlabels\nweights\n;\nPROPERTIES\nFIT=100\nweakly compatible\ncyclic\n;\nCYCLE");

        for (int n = 0; n < N; n++) {
            nexusString.append(" ").append(nexusData.getCycleAt(n));
        }

        nexusString.append(";\nMATRIX\n");


        for (int n = 0; n < ss.getNbSplits(); n++) {

            // Add one for splitstree...
            nexusString.append(n + 1).append("   ").append(ss.getWeightAt(n)).append("  ");

            SplitBlock aSplit = ss.getSplitAt(n).getASide();

            for (int p = 0; p < aSplit.size(); p++) {

                // Add one for splitstree...
                nexusString.append(" ").append(aSplit.get(p));
            }

            nexusString.append(",\n");
        }

        nexusString.append(";\nEND;");


        // Save
        FileUtils.writeStringToFile(outFile, nexusString.toString());*/
    }


    /**
     * Uses a buffer to stream file content into the specified file.
     *
     * @param file
     * @throws IOException
     */
    public void write(File file) throws IOException {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsoluteFile()))) {
            writer.write(this.fileContent.toString());
        }
    }


    // ****** Append methods specific to known nexus blocks ******

    public NexusWriter appendHeader() {
        this.appendLine("#NEXUS");
        return this;
    }

    public NexusWriter append(IdentifierList taxa) {
        this.appendLine("BEGIN Taxa;");
        this.appendLine(" DIMENSIONS ntax=" + taxa.size() + ";");
        this.appendLine(" TAXLABELS");

        for (int i = 0; i < taxa.size(); i++) {
            this.appendLine("  [" + (i + 1) + "]\t" + taxa.get(i).getName());
        }

        this.appendLine(" ;");
        this.appendLine("END; [Taxa]");

        return this;
    }

    public NexusWriter append(Alignment a) {
        String[] id = a.getTaxaLabels();
        String[] sequences = a.getSequences();

        this.appendLine("BEGIN Characters;");
        this.appendLine(" DIMENSIONS ntax=" + a.size() + " nchar=" + sequences[0].length() + ";");
        this.appendLine(" FORMAT labels=LEFT interleave=NO;");
        this.appendLine(" MATRIX");
        for (int i = 0; i < id.length; i++) {
            this.appendLine("  [" + (i + 1) + "]\t" + id[i] + "\t" + sequences[i]);
        }
        this.appendLine(";");
        this.appendLine("END;  [Characters]");

        return this;
    }

    public NexusWriter append(DistanceMatrix dm) {
        return this.append(dm, DistanceMatrixBuilder.Triangle.BOTH);
    }

    public NexusWriter append(DistanceMatrix dm, DistanceMatrixBuilder.Triangle triangle) {
        double[][] matrix = dm.getMatrix();

        this.appendLine("BEGIN Distances;");
        this.appendLine(" DIMENSIONS ntax=" + dm.size() + ";");
        this.appendLine(" FORMAT labels=LEFT interleave=NO diagonal triangle=" + triangle.toString() + ";");
        this.appendLine(" MATRIX");
        for (int i = 1; i <= matrix.length; i++) {
            this.appendLine("  [" + i + "]\t" + StringUtils.join(triangle.getRow(i, dm), " "));
        }
        this.appendLine(";");
        this.appendLine("END; [Distances]");

        return this;
    }


    public NexusWriter append(SplitSystem ss) {

        this.appendLine("BEGIN Splits;");
        this.appendLine(" DIMENSIONS ntax=" + ss.getNbTaxa() + " nsplits=" + ss.getNbSplits() + ";");
        this.appendLine(" FORMAT labels=no weights=" + (ss.isWeighted() ? "yes" : "no") + " confidences=no intervals=no;");
        this.appendLine(" PROPERTIES fit=-1.0" + (ss.isCompatible() ? " weakly_compatible" : "") + (ss.isCircular() ? " cyclic" : "") + ";");
        if (ss.isCircular()) {
            this.appendLine(" CYCLE " + ss.getCircularOrdering().toString(IdentifierList.IdentifierFormat.NEXUS_CIRCULAR_ORDERING) + ";");
        }

        this.appendLine(" MATRIX");
        int currentSplitIndex = 1;

        for (int i = 0; i < ss.getNbSplits(); i++) {
            Split s = ss.getSplits().get(i);
            SplitBlock sb = s.getASide();

            if (!ss.isWeighted() || s.getWeight() != 0.0) {
                this.appendLine("  [" + currentSplitIndex++ + ", size=" + sb.size() + "]\t" + s.getWeight() + "\t" + sb.toString() + ",");
            }
        }
        this.appendLine(";");
        this.appendLine("END; [Splits]");
        return this;
    }


    // ****** These extra append methods allows the client to add their own custom content to the file *******

    @Override
    public NexusWriter append(CharSequence csq) throws IOException {
        this.fileContent.append(csq);
        return this;
    }

    @Override
    public NexusWriter append(CharSequence csq, int start, int end) throws IOException {
        this.fileContent.append(csq, start, end);
        return this;
    }

    @Override
    public NexusWriter append(char c) throws IOException {
        this.fileContent.append(c);
        return this;
    }

    public NexusWriter append(String s) {
        this.fileContent.append(s);
        return this;
    }

    public NexusWriter appendLine(String s) {
        this.fileContent.append(s).append("\n");
        return this;
    }

    public NexusWriter appendLine() {
        this.fileContent.append("\n");
        return this;
    }
}