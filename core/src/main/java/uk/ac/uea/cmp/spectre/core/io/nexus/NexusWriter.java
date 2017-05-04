/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2017  UEA School of Computing Sciences
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package uk.ac.uea.cmp.spectre.core.io.nexus;

import uk.ac.uea.cmp.spectre.core.ds.Sequences;
import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceList;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrixBuilder;
import uk.ac.uea.cmp.spectre.core.ds.network.Edge;
import uk.ac.uea.cmp.spectre.core.ds.network.Network;
import uk.ac.uea.cmp.spectre.core.ds.network.NetworkLabel;
import uk.ac.uea.cmp.spectre.core.ds.network.Vertex;
import uk.ac.uea.cmp.spectre.core.ds.network.draw.ViewerConfig;
import uk.ac.uea.cmp.spectre.core.ds.split.Split;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitSystem;
import uk.ac.uea.cmp.spectre.core.io.AbstractSpectreWriter;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Used to handle streaming data to Nexus format file from SplitSystem objects and other splits and length data. Can
 * create nexus files designed to represent a network, tree or weighted tree.  The class stores file content in java's
 * StringBuilder and the client can append content into this as they like.  This means that the whole file content is
 * stored in memory before writing to disk.  This could be a problem on low-memory systems, or if processing large files.
 * Consider optimising if we memory related problems are encountered.
 *
 * @author Dan
 */
public class NexusWriter extends AbstractSpectreWriter implements Appendable {

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
                .append(ss.getOrderedTaxa().sortById())
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

        for (Taxon taxon : nexusData.getActive()) {
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

    public NexusWriter append(Sequences a) {
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
        this.appendLine(" FORMAT labels=LEFT diagonal triangle=" + triangle.toString() + ";");
        this.appendLine(" MATRIX");
        for (int i = 0; i < matrix.length; i++) {

            DistanceList dl = dm.getAllDistances().get(i);
            this.append("  [" + dl.getTaxon().getId() + "] '" + dl.getTaxon().getName() + "'\t");

            double[] row = triangle.getRow(i+1, dm);
            this.append(Double.toString(row[0]));
            for(int j = 1; j < row.length; j++) {
                this.append(" " + row[j]);
            }
            this.appendLine();
        }
        this.appendLine(";");
        this.appendLine("END; [Distances]");

        return this;
    }


    public NexusWriter append(SplitSystem ss) {

        this.appendLine("BEGIN Splits;");
        this.appendLine(" DIMENSIONS ntax=" + ss.getNbTaxa() + " nsplits=" + ss.size() + ";");
        this.appendLine(" FORMAT labels=no weights=" + (ss.isWeighted() ? "yes" : "no") + " confidences=no intervals=no;");
        this.appendLine(" PROPERTIES fit=-1.0" + (ss.isCompatible() ? " weakly_compatible" : "") + (ss.isCircular() ? " cyclic" : "") + ";");
        if (ss.isCircular()) {
            this.appendLine(" CYCLE " + ss.getOrderedTaxa().toString(IdentifierList.IdentifierFormat.NEXUS_CIRCULAR_ORDERING) + ";");
        }

        this.appendLine(" MATRIX");
        int currentSplitIndex = 1;

        for (Split s : ss) {
            if (!ss.isWeighted() || s.getWeight() != 0.0) {
                this.appendLine("  [" + currentSplitIndex++ + ", size=" + s.getASideSize() + "]\t" + s.getWeight() + "\t" + s.getASide().toString() + ",");
            }
        }
        this.appendLine(";");
        this.appendLine("END; [Splits]");
        return this;
    }


    public NexusWriter append(Network network) {

        int nTaxa = network.getNbTaxa();

        List<Vertex> vertices = network.getAllVertices();
        List<Edge> edges = network.getAllEdges();

        this.appendLine("BEGIN Network;");
        this.appendLine("DIMENSIONS ntax=" + nTaxa + " nvertices=" + vertices.size() + " nedges=" + edges.size() + ";");
        this.appendLine("DRAW to_scale;");
        this.appendLine("TRANSLATE");

        //write translate section
        if (network.getTranslate() != null && network.getTranslate().size() > 0) {
            SortedSet<Integer> keys = new TreeSet<Integer>(network.getTranslate().keySet());
            for (Integer key : keys) {
                String value = network.getTranslate().get(key);
                String line = key + " '" + value + "',";
                this.appendLine(line);
            }
        }
        else{
            for (Vertex v : vertices) {
                if (v.getTaxa().size() > 0) {
                    String line = String.valueOf(v.getNxnum());
                    for (Identifier i : v.getTaxa()) {
                        line += " '" + i.getName() + "'";
                    }
                    line += ",";
                    this.appendLine(line);
                }
            }
        }
        this.appendLine(";");
        //write vertices section
        this.appendLine("VERTICES");
        for(Vertex v : vertices) {

            Color bg = v.getBackgroundColor();
            Color fg = v.getLineColor();
            String shape = (v.getShape() == null) ? "" : " s=" + v.getShape();
            String line = v.getNxnum() + " " + v.getX() + " " + v.getY() + " w=" + v.getWidth() + " h=" + v.getHeight() + shape;
            if (!fg.equals(Color.BLACK)) {
                line = line.concat(" fg=" + fg.getRed() + " " + fg.getGreen() + " " + fg.getBlue());
            }
            if (!bg.equals(Color.BLACK)) {
                line = line.concat(" bg=" + bg.getRed() + " " + bg.getGreen() + " " + bg.getBlue());
            }
            this.appendLine(line + ",");
        }
        this.appendLine(";");
        //write vertex labels section
        this.appendLine("VLABELS");
        for(Vertex v : vertices) {
            if (v.getTaxa().size() > 0) {
                String label = new String();
                for(Identifier i : v.getTaxa()) {
                    label = (i.getName() + ", ").concat(label);
                }
                label = label.substring(0, label.length() - 2);
                this.appendLine(v.getNxnum() + " '" + label + "' x=2 y=2 f='Dialog-PLAIN-10',");
            } else if (v.getLabel() != null) {
                NetworkLabel l = v.getLabel();
                String label = v.getNxnum() + " '" + l.getName() + "' x=" + ((int) l.getOffsetX()) + " y=" + ((int) l.getOffsetY()) + " f='" + l.getFontFamily() + "-" + l.getFontStyle() + "-" + l.getFontSize() + "'";
                if (l.getFontColor() != null) {
                    Color c = l.getFontColor();
                    label = label.concat(" lc=" + c.getRed() + " " + c.getGreen() + " " + c.getBlue());
                }
                if (l.getBackgroundColor() != null) {
                    Color c = l.getBackgroundColor();
                    label = label.concat(" lk=" + c.getRed() + " " + c.getGreen() + " " + c.getBlue());
                }
                label = label.concat(",");
                this.appendLine(label);
            }
        }
        this.appendLine(";");
        //Write the edges.
        this.appendLine("EDGES");
        for(Edge e : edges) {
            Color c = e.getColor();
            this.appendLine(e.getNxnum() + " " + e.getTop().getNxnum() + " " + e.getBottom().getNxnum() + " s=" + (e.getIdxsplit() + 1) + " l=" + e.getWidth() + " fg=" + c.getRed() + " " + c.getGreen() + " " + c.getBlue() + ",");
        }
        this.appendLine(";");
        this.appendLine("END;");

        return this;
    }

    public NexusWriter append(ViewerConfig config) {
        Dimension dm = config.getDimensions();

        this.appendLine("BEGIN Viewer;");
        this.appendLine("DIMENSIONS width=" + dm.width + " height=" + dm.height + ";");
        this.appendLine(" MATRIX");
        this.appendLine("  ratio=" + config.getRatio());
        this.appendLine("  showtrivial=" + config.showTrivial());
        this.appendLine("  showrange=" + config.showRange());
        this.appendLine("  showlabels=" + config.showLabels());
        this.appendLine("  colorlabels=" + config.colorLabels());
        this.appendLine("  leaders=" + config.getLeaderType());
        this.appendLine("  leaderstroke=" + config.getLeaderStroke());
        Color leaderColor = config.getLeaderColor();
        this.appendLine("  leadercolor=" + leaderColor.getRed() + " " +
                leaderColor.getGreen() + " " + leaderColor.getBlue());
        for (Vertex v : config.getLabeledVertices()) {
            NetworkLabel label = v.getLabel();
            if (!label.movable) {
                this.appendLine("  fix " + (v.getNxnum() + 1));
            }
        }
        this.appendLine(" ;\nEND [Viewer];");

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