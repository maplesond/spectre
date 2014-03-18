/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2014  UEA School of Computing Sciences
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
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

package uk.ac.uea.cmp.spectre.flatnj.tools;

import uk.ac.uea.cmp.spectre.core.ds.Alignment;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.network.Edge;
import uk.ac.uea.cmp.spectre.core.ds.network.Vertex;
import uk.ac.uea.cmp.spectre.flatnj.ds.*;
import uk.ac.uea.cmp.spectre.flatnj.fdraw.*;
import uk.ac.uea.cmp.spectre.core.ds.network.Label;

import java.awt.*;
import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * @author balvociute
 */
public class Writer {
    protected BufferedWriter bw;
    protected String block;

    public Writer() {
    }

    public Writer(File file) {
        open(file);
    }

    public void rewriteFromInput(String inFile, List<String> exclude) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(inFile));
        } catch (FileNotFoundException fnfe) {
            exitError("Input file not found");
        }
        try {
            String line = br.readLine();
            boolean print = true;
            while (line != null) {
                if (line.toLowerCase().contains("begin")) {
                    print = true;
                    Scanner scannerLC = new Scanner(line.toLowerCase());
                    for (int i = 0; i < exclude.size(); i++) {
                        if (scannerLC.findInLine(exclude.get(i).toLowerCase()) != null) {
                            print = false;
                        }
                    }
                }
                if (print) {
                    bw.write(line + "\n");
                }
                line = br.readLine();
            }
            br.close();
        } catch (IOException ioe) {
            exitError("Error reading input file");
        }
    }

    public void write(Taxa taxa) {
        String[] labels = taxa.getTaxaNames();
        writeLine("#NEXUS");
        writeLine();
        writeLine("BEGIN Taxa;");
        writeLine(" DIMENSIONS ntax=" + labels.length + ";");
        writeLine(" TAXLABELS");
        for (int i = 0; i < labels.length; i++) {
            writeLine("  [" + (i + 1) + "]\t" + labels[i]);
        }
        writeLine(" ;");
        writeLine("END;");
    }

    public void write(Alignment a) {
        String[] id = a.getTaxaLabels();
        String[] sequences = a.getSequences();

        writeLine();
        writeLine("BEGIN Characters;");
        writeLine(" DIMENSIONS ntax=" + a.size() + " nchar=" + sequences[0].length() + ";");
        writeLine(" FORMAT labels=LEFT interleave = NO;");
        writeLine(" MATRIX");
        for (int i = 0; i < id.length; i++) {
            writeLine("  [" + (i + 1) + "]\t" + id[i] + "\t" + sequences[i]);
        }
        writeLine(";");
        writeLine("END;");
    }

    public void write(DistanceMatrix dm) {
        double[][] matrix = dm.getMatrix();

        writeLine();
        writeLine("BEGIN Distances;");
        writeLine(" DIMENSIONS ntax=" + dm.size() + ";");
        writeLine(" FORMAT labels=NO interleave = NO diagonal;");
        writeLine(" MATRIX");
        for (int i = 0; i < matrix.length; i++) {
            String line = "";
            for (int j = 0; j < matrix.length; j++) {
                line = line.concat(" " + matrix[i][j]);
            }
            writeLine(line);
        }
        writeLine(";");
        writeLine("END;");
    }

    public void write(QuadrupleSystem qs) {
        writeLine();
        writeLine("BEGIN Quadruples;");
        writeLine(" DIMENSIONS ntax=" + qs.getnTaxa() + " nquadruples=" + qs.getnQuadruples() + ";");
        writeLine(" FORMAT labels=LEFT;");
        writeLine(" MATRIX");

        Quadruple[] quadruples = qs.getQuadruples();

        for (int i = 0; i < quadruples.length; i++) {
            String line = ("  quadruple" + i + " :");
            int[] taxa = quadruples[i].getTaxa();
            double[] weights = quadruples[i].getWeights();
            for (int j = 0; j < 4; j++) {
                line = line.concat(" " + taxa[j]);
            }
            line = line.concat(" :");
            for (int j = 0; j < 7; j++) {
                line = line.concat(" " + weights[j]);
            }
            line = line.concat(",");
            writeLine(line);
        }

        writeLine(" ;");
        writeLine("END;");
    }

    public void write(PermutationSequence ps) {
        int[] sequence = ps.getSequence();
        int[] swaps = ps.getSwaps();
        int nSwaps = swaps.length;
        int nTaxa = sequence.length;
        int nSplits = swaps.length;
        boolean[] active = ps.getActive();

        double[] weights = ps.getWeights();

        double[] trivial = ps.getTrivial();

        writeLine();
        writeLine("BEGIN Flatsplits;");
        writeLine(" DIMENSIONS ntax=" + nTaxa + " nsplits=" + nSwaps + ";");
        writeLine(" FORMAT weights=yes activeflags=yes;");
        //writeLine(" PROPERTIES fit=" + ps.getFit() + "leastsquares;");

        String cycle = " CYCLE ";
        for (int i = 0; i < sequence.length; i++) {
            cycle = cycle.concat(" " + (sequence[i] + 1));
        }
        writeLine(cycle + ";");

        writeLine(" MATRIX");
        writeLine(" SEQUENCE");

        for (int i = 0; i < swaps.length; i++) {
            int act = (active[i]) ? 1 : 0;
            writeLine("   " + (swaps[i] + 1) + " " + act + " " + weights[i] + ",");
        }

        writeLine("  TRIVIAL");
        for (int i = 0; i < trivial.length; i++) {
            writeLine("   " + (i + 1) + " " + trivial[i] + ",");
        }
        writeLine(";");
        writeLine("END;");
    }

    public void write(SplitSystem ss) {
        boolean[][] splits = ss.getSplits();

        boolean[] active = ss.getActive();

        int[] cycle = ss.getCycle();
        double[] weights = ss.getWeights();

        int nTaxa = ss.getnTaxa();

        int nSplits = Utilities.size(active);

        writeLine();
        writeLine("BEGIN Splits;");
        writeLine("DIMENSIONS ntax=" + nTaxa + " nsplits=" + nSplits + ";");
        writeLine("FORMAT labels=no weights=" + (weights != null ? "yes" : "no") + " confidences=no intervals=no;");
        if (cycle != null) {
            String cyc = "CYCLE";
            for (int i = 0; i < cycle.length; i++) {
                cyc = cyc.concat(" " + (cycle[i] + 1));
            }
            cyc = cyc.concat(";");
            writeLine(cyc);
        }
        writeLine("MATRIX");
        int splitNr = 1;

        for (int i = 0; i < splits.length; i++) {
            if (active[i]) {
                String space = " ";
                for (int j = String.valueOf(nSplits).length(); j > String.valueOf(splitNr).length(); j--) {
                    space = space + " ";
                }
                String splitLine = "[" + (splitNr++) + "]" + space + (weights != null ? weights[i] + "\t" : "");
                for (int j = 0; j < splits[i].length; j++) {
                    if (splits[i][j]) {
                        splitLine = splitLine.concat(" " + (j + 1));
                    }
                }
                splitLine = splitLine.concat(",");
                writeLine(splitLine);
            }
        }
        writeLine(";");
        writeLine("END;");
    }

    public void open(String filePath) {
        try {
            bw = new BufferedWriter(new FileWriter(filePath));
        } catch (IOException ioe) {
            exitError("Unable to open file \"" + filePath + "\"");
        }
    }

    public void close() {
        try {
            bw.close();
        } catch (IOException ioe) {
            exitError("Unable to close output file");
        }
    }

    public void exitError(String message) {
        System.err.println("Error: " + message + ".\n");
        System.exit(1);
    }

    private void writeLine(String text) {
        try {
            bw.write(text + "\n");
        } catch (IOException ioe) {
            exitError("Unable to write to the output file");
        }
    }

    public void write(Vertex net, int nTaxa, int[] compressed, Taxa taxa) {
        Iterator<Vertex> vIter;
        Iterator<Edge> eIter;
        Iterator taxiter;
        Vertex v;
        Edge e;

        List<Vertex> vertices = DrawFlat.collect_vertices(net);
        List<Edge> edges = DrawFlat.collect_edges(net.getFirstEdge());

        writeLine();
        writeLine("BEGIN Network;");
        writeLine("DIMENSIONS ntax=" + nTaxa + " nvertices=" + vertices.size() + " nedges=" + edges.size() + ";");
        writeLine("DRAW to_scale;");
        writeLine("TRANSLATE");
        //write translate section
        vIter = vertices.listIterator();
        while (vIter.hasNext()) {
            v = vIter.next();
            if (v.getTaxa().size() > 0) {
                taxiter = v.getTaxa().listIterator();
                String line = String.valueOf(v.getNxnum());

                while (taxiter.hasNext()) {
                    int index = ((Integer) taxiter.next()).intValue();
                    line = line.concat(" '" + taxa.getTaxaNames()[index] + "'");
                }
                line = line.concat(",");
                writeLine(line);
            }
        }
        writeLine(";");
        //write vertices section
        writeLine("VERTICES");
        vIter = vertices.listIterator();
        while (vIter.hasNext()) {
            v = vIter.next();
            int[] color = Utilities.colorToInt(v.getBackgroundColor());
            String line = v.getNxnum() + " " + v.getX() + " " + v.getY() + " w=" + v.getWidth() + " h=" + v.getHeight() + (v.getShape() != null ? " s=" + v.getShape() : "");
            if (color[0] + color[1] + color[2] > 0) {
                line = line.concat(" fg=" + color[0] + " " + color[1] + " " + color[2] + " bg=" + color[0] + " " + color[1] + " " + color[2]);
            }
            writeLine(line + ",");
        }
        writeLine(";");
        //write vertex labels section
        writeLine("VLABELS");
        vIter = vertices.listIterator();
        while (vIter.hasNext()) {
            v = vIter.next();
            if (v.getTaxa().size() > 0) {
                String label = new String();
                taxiter = v.getTaxa().listIterator();
                while (taxiter.hasNext()) {
                    label = (taxa.getTaxaNames()[((Integer) taxiter.next()).intValue()] + ", ").concat(label);
                    //--------------------- just for testing, so that labels are nor visible --------
                    //label = "";
                }
                label = label.substring(0, label.length() - 2);
                writeLine(v.getNxnum() + " '" + label + "' x=2 y=2 f='Dialog-PLAIN-10',");
            } else if (v.getLabel() != null) {
                Label l = v.getLabel();
                String label = v.getNxnum() + " '" + l.getName() + "' x=" + ((int) l.getOffsetX()) + " y=" + ((int) l.getOffsetY()) + " f='" + l.getFontFamily() + "-" + l.getFontStyle() + "-" + l.getFontSize() + "'";
                if (l.getFontColor() != null) {
                    int[] c = Utilities.colorToInt(l.getFontColor());
                    label = label.concat(" lc=" + c[0] + " " + c[1] + " " + c[2]);
                }
                if (l.getBackgroundColor() != null) {
                    int[] c = Utilities.colorToInt(l.getBackgroundColor());
                    label = label.concat(" lk=" + c[0] + " " + c[1] + " " + c[2]);
                }
                label = label.concat(",");
                writeLine(label);
            }
        }
        writeLine(";");
        //Write the edges.
        writeLine("EDGES");
        eIter = edges.iterator();

        int maxCompressed = 0;
        for (int i = 0; i < compressed.length; i++) {
            if (compressed[i] > maxCompressed) {
                maxCompressed = compressed[i];
            }
        }
        maxCompressed++;

        while (eIter.hasNext()) {
            e = (Edge) eIter.next();
            int[] color = Utilities.colorToInt(e.getColor());

            int comp = e.getIdxsplit() + 1;

//            if(compressed != null && e.getIdxsplit() < compressed.length)
//            {
//                comp = compressed[e.getIdxsplit()] + 1;
//                System.out.println("1: " + comp);
//            }
//            else if(compressed == null)
//            {
//                comp = e.getIdxsplit() + 1;
//                System.out.println("2: " + comp);
//            }
//            else
//            {
//                comp = ++maxCompressed;
//                System.out.println("3: " + comp);
//            }

            writeLine(e.getNxnum() + " " +
                    (e.getTop()).getNxnum() + " " +
                    (e.getBot()).getNxnum() +
                    " s=" + comp +
                    " l=" + e.getWidth() +
                    " fg=" + color[0] + " " + color[1] + " " + color[2] + ",");
        }
        writeLine(";");
        writeLine("END;");
    }

    public void write(Network network, Taxa taxa) {
        Iterator<Vertex> vIter;
        Iterator<Edge> eIter;
        Iterator taxiter;
        Vertex v;
        Edge e;

        int nTaxa = network.getNTaxa();

        List<Vertex> vertices = network.getVertices();
        List<Edge> edges = network.getEdges();

        writeLine();
        writeLine("BEGIN Network;");
        writeLine("DIMENSIONS ntax=" + nTaxa + " nvertices=" + vertices.size() + " nedges=" + edges.size() + ";");
        writeLine("DRAW to_scale;");
        writeLine("TRANSLATE");
        //write translate section
        vIter = vertices.listIterator();
        while (vIter.hasNext()) {
            v = vIter.next();
            if (v.getTaxa().size() > 0) {
                taxiter = v.getTaxa().listIterator();
                String line = String.valueOf((v.getNxnum() + 1));

                while (taxiter.hasNext()) {
                    line = line.concat(" '" + taxa.getTaxaNames()[((Integer) taxiter.next()).intValue()] + "'");
                }
                line = line.concat(",");
                writeLine(line);
            }
        }
        writeLine(";");
        //write vertices section
        writeLine("VERTICES");
        vIter = vertices.listIterator();
        while (vIter.hasNext()) {
            v = vIter.next();

            int[] bg = Utilities.colorToInt(v.getBackgroundColor());
            int[] fg = Utilities.colorToInt(v.getLineColor());
            String shape = (v.getShape() == null) ? "" : " s=" + v.getShape();
            String line = (v.getNxnum() + 1) + " " + v.getX() + " " + v.getY() + " w=" + v.getWidth() + " h=" + v.getHeight() + shape;
            if (fg[0] + fg[1] + fg[2] > 0) {
                line = line.concat(" fg=" + fg[0] + " " + fg[1] + " " + fg[2]);
            }
            if (bg[0] + bg[1] + bg[2] > 0) {
                line = line.concat(" bg=" + bg[0] + " " + bg[1] + " " + bg[2]);
            }
            writeLine(line + ",");
        }
        writeLine(";");
        //write vertex labels section
        writeLine("VLABELS");
        vIter = vertices.listIterator();
        while (vIter.hasNext()) {
            v = vIter.next();
            if (v.getTaxa().size() > 0) {
                String label = new String();
                taxiter = v.getTaxa().listIterator();
                while (taxiter.hasNext()) {
                    label = (taxa.getTaxaNames()[((Integer) taxiter.next()).intValue()] + ", ").concat(label);
                    //--------------------- just for testing, so that labels are nor visible --------
                    //label = "";
                }
                label = label.substring(0, label.length() - 2);
                writeLine((v.getNxnum() + 1) + " '" + label + "' x=2 y=2 f='Dialog-PLAIN-10',");
            } else if (v.getLabel() != null) {
                Label l = v.getLabel();
                String label = (v.getNxnum() + 1) + " '" + l.getName() + "' x=" + ((int) l.getOffsetX()) + " y=" + ((int) l.getOffsetY()) + " f='" + l.getFontFamily() + "-" + l.getFontStyle() + "-" + l.getFontSize() + "'";
                if (l.getFontColor() != null) {
                    int[] c = Utilities.colorToInt(l.getFontColor());
                    label = label.concat(" lc=" + c[0] + " " + c[1] + " " + c[2]);
                }
                if (l.getBackgroundColor() != null) {
                    int[] c = Utilities.colorToInt(l.getBackgroundColor());
                    label = label.concat(" lk=" + c[0] + " " + c[1] + " " + c[2]);
                }
                label = label.concat(",");
                writeLine(label);
            }
        }
        writeLine(";");
        //Write the edges.
        writeLine("EDGES");
        eIter = edges.iterator();
        while (eIter.hasNext()) {
            e = (Edge) eIter.next();
            int[] color = Utilities.colorToInt(e.getColor());
            writeLine((e.getNxnum() + 1) + " " + ((e.getTop()).getNxnum() + 1) + " " + ((e.getBot()).getNxnum() + 1) + " s=" + (e.getIdxsplit() + 1) + " l=" + e.getWidth() + " fg=" + color[0] + " " + color[1] + " " + color[2] + ",");
        }
        writeLine(";");
        writeLine("END;");
    }

    private void writeLine() {
        writeLine("");
    }

    private void open(File file) {
        try {
            bw = new BufferedWriter(new FileWriter(file));
        } catch (IOException ioe) {
            exitError("Unable to open file \"" + file.getAbsolutePath() + "\"");
        }
    }

    public void write(ViewerConfig config, List<Vertex> labeled) {
        Dimension dm = config.getDimensions();
        writeLine();
        writeLine("BEGIN Viewer;");
        writeLine("DIMENSIONS width=" + dm.width + " height=" + dm.height + ";");
        writeLine(" MATRIX");
        writeLine("  ratio=" + config.getRatio());
        writeLine("  showtrivial=" + config.showTrivial());
        writeLine("  showlabels=" + config.showLabels());
        writeLine("  colorlabels=" + config.colorLabels());
        writeLine("  leaders=" + config.getLeaderType());
        writeLine("  leaderstroke=" + config.getLeaderStroke());
        Color leaderColor = config.getLeaderColor();
        writeLine("  leadercolor=" + leaderColor.getRed() + " " +
                leaderColor.getGreen() + " " + leaderColor.getBlue());
        for (Iterator<Vertex> it = labeled.iterator(); it.hasNext(); ) {
            Vertex vertex = it.next();
            Label label = vertex.getLabel();
            if (!label.movable) {
                writeLine("  fix " + (vertex.getNxnum() + 1));
            }
        }
        writeLine(" ;\nEND;");
    }
}
