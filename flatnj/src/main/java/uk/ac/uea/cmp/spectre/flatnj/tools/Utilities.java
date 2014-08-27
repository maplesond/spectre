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
import uk.ac.uea.cmp.spectre.core.ds.network.NetworkLabel;
import uk.ac.uea.cmp.spectre.core.ds.network.Vertex;
import uk.ac.uea.cmp.spectre.flatnj.ds.PermutationSequence;
import uk.ac.uea.cmp.spectre.flatnj.ds.SplitSystem;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.List;

/**
 * @author balvociutes
 */
public class Utilities {

    public static void printTaxaBlock(Writer outputFile, String[] taxa) throws IOException {
        outputFile.write("#NEXUS\n");
        outputFile.write("BEGIN TAXA;\n");
        outputFile.write("DIMENSIONS NTAX=" + taxa.length + ";\n");
        outputFile.write("TAXLABELS\n");
        for (int i = 0; i < taxa.length; i++) {
            outputFile.write("[" + (i + 1) + "]  '" + taxa[i] + "'\n");
        }
        outputFile.write(";\nEND;\n\n");
    }

    public static void printFictitiousTaxaBlock(Writer outputFile, int nTaxa) throws IOException {
        outputFile.write("#NEXUS\n");
        outputFile.write("BEGIN TAXA;\n");
        outputFile.write("DIMENSIONS NTAX=" + nTaxa + ";\n");
        outputFile.write("TAXLABELS\n");
        for (int i = 0; i < nTaxa; i++) {
            outputFile.write("[" + (i + 1) + "]  'Taxon" + i + "'\n");
        }
        outputFile.write(";\nEND;\n\n");
    }


    public static void printAlignmentToFile(FileWriter outputFile, Alignment a) throws IOException {
        String[] ids = a.getTaxaLabels();
        String[] seq = a.getSequences();
        for (int i = 0; i < ids.length; i++) {
            outputFile.write(">seq_" + ids[i] + "\n" + seq[i] + "\n");
        }
        outputFile.close();
    }

    public static int size(boolean[] array) {
        int size = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == true) {
                size++;
            }
        }
        //size = (size < array.length - size) ? size : array.length - size;

        return size;
    }

    public static int[] getElements(boolean[] set) {
        int size = Utilities.size(set);
        int[] elements = new int[size];
        int j = 0;
        for (int i = 0; i < set.length; i++) {
            if (set[i] == true) {
                elements[j++] = i;
            }
        }
        return elements;
    }

    public static int combination(int i) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static int combinations(int above, int below) {
        int c = 0;
        if (below == above) {
            c = 1;
        } else if (below > above) {
            c = below;
            for (int i = 1; i < above; i++) {
                c *= (below - i);
            }
            for (int i = 2; i <= above; i++) {
                c /= i;
            }
        }
        return c;
    }

    public static void swapTwoInAnArray(double[] array, int i1, int i2) {
        double tmp = array[i1];
        array[i1] = array[i2];
        array[i2] = tmp;
    }

    public static void printMatrix(int[][] m) {
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[i].length; j++) {
                System.out.print(m[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void printMatrix(double[][] m) {
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[i].length; j++) {
                System.out.print(m[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void setActive(int[] active, double fraction) {

        int inVisible = (int) (active.length * fraction);
        Set<Integer> visibleSplits = new HashSet<Integer>();

        for (int i = 0; i < active.length; i++) {
            visibleSplits.add(i);
        }

        while (visibleSplits.size() > inVisible) {
            Object[] remaining = visibleSplits.toArray();
            System.out.println(remaining.length + "\t" + visibleSplits.size() + " " + inVisible);
            int random = Integer.valueOf((remaining[(int) Math.floor(Math.random() * remaining.length)]).toString());
            visibleSplits.remove(random);
        }
        for (int i = 0; i < active.length; i++) {
            if (visibleSplits.contains(i)) {
                active[i] = 1;
            } else {
                active[i] = 0;
            }
        }
    }

    public static void setActive(boolean[] active, double fraction) {
        int inVisible = (int) (active.length * fraction);
        Set<Integer> allSplits = new HashSet<Integer>();
        for (int i = 0; i < active.length; i++) {
            allSplits.add(i);
        }
        Set<Integer> visibleSplits = new HashSet<Integer>();
        while (visibleSplits.size() < inVisible) {
            Object[] remaining = allSplits.toArray();
            int random = Integer.valueOf((remaining[(int) Math.floor(Math.random() * remaining.length)]).toString());
            allSplits.remove(random);
            visibleSplits.add(random);
        }
        for (int i = 0; i < active.length; i++) {
            if (visibleSplits.contains(i)) {
                active[i] = true;
            } else {
                active[i] = false;
            }
        }
    }

    public static void setActive(int[] active, SplitSystem ss, double threshold) {
        boolean current;
        double[] weights = ss.getWeights();
        for (int i = 0; i < active.length; i++) {
            active[i] = 1;
        }
        for (int i = 0; i < ss.getnSplits(); i++) {
            if (active[i] == 1) {
                for (int j = 0; j < ss.getnSplits(); j++) {
                    if (!ss.isCompatible(i, j)) {
                        if (weights[i] < weights[j] * threshold) {
                            active[i] = 0;
                        } else if (weights[j] < weights[i] * threshold) {
                            active[j] = 0;
                        }
                    }
                }
            }
        }
    }

    public static void setActive(boolean[] active, SplitSystem ss, double threshold) {
        boolean current;
        double[] weights = ss.getWeights();
        for (int i = 0; i < active.length; i++) {
            active[i] = true;
        }
        for (int i = 0; i < ss.getnSplits(); i++) {
            if (active[i] == true) {
                for (int j = 0; j < ss.getnSplits(); j++) {
                    if (!ss.isCompatible(i, j)) {
                        if (weights[i] < weights[j] * threshold) {
                            active[i] = false;
                        } else if (weights[j] < weights[i] * threshold) {
                            active[j] = false;
                        }
                    }
                }
            }
        }
    }

    public static void setActive(boolean[] active, double threshold, double[] weights) {
        for (int i = 0; i < weights.length; i++) {
            if (weights[i] >= threshold) {
                active[i] = true;
            }
        }
    }

    public static void printFlatSplitsInNexusToTheScreen(SplitSystem ss, PermutationSequence ps) {
        int nSwaps = ps.getnSwaps();
        int[] sequence = ps.getSequence();
        int[] swaps = ps.getSwaps();

        int nTaxa = ss.getnTaxa();
        double[] weights = ss.getWeights();
        int nSplits = ss.getnSplits();

        double[] trivial = ss.getTrivialWeights();

        System.out.print("#NEXUS\n");
        System.out.print("BEGIN TAXA;\n");
        System.out.print("DIMENSIONS NTAX=" + nTaxa + ";\n");
        System.out.print("TAXLABELS\n");
        for (int i = 0; i < nTaxa; i++) {
            System.out.print("[" + (i + 1) + "]  'Taxon" + i + "'\n");
        }
        System.out.print(";\nEND;\n\n");


        System.out.print("BEGIN FLATSPLITS;\n");
        System.out.print("DIMENSIONS NTAX=" + nTaxa + " NSPLITS=" + nSwaps + ";\n");
        System.out.print("FORMAT WEIGHTS=yes ACTIVEFLAGS=yes;\n");
        System.out.print("CYCLE ");
        for (int i = 0; i < sequence.length; i++) {
            System.out.print(" " + (sequence[i] + 1));
        }
        System.out.print(";\n");

        System.out.print("MATRIX\n");

        int[] active = new int[nSplits];
        setActive(active, 1);

        for (int i = 0; i < swaps.length; i++) {
            System.out.print((swaps[i] + 1) + " " + active[i] + " " + weights[i] + ",\n");
        }

        System.out.print(";\nEND;\n");
    }

    public static void printSplitSystemInNexus(SplitSystem ss, String outputSplitsFile, double threshold) throws IOException {
        FileWriter outputFile = new FileWriter(new File(outputSplitsFile));
        printTaxaBlock(outputFile, ss.getTaxaNames());

        boolean[][] splits = ss.getSplits();

        int[] cycle = ss.getCycle();
        double[] weights = ss.getWeights();
        int[] active = new int[splits.length];

        if (weights != null) {
            setActive(active, ss, threshold);
        } else {
            for (int i = 0; i < active.length; i++) {
                active[i] = 1;
            }
        }


        int nSplits = 0;
        for (int i = 0; i < active.length; i++) {
            if (active[i] == 1) {
                nSplits++;
            }
        }

        outputFile.write("\n");
        outputFile.write("BEGIN Splits;\n");
        outputFile.write("DIMENSIONS ntax=" + ss.getnTaxa() + " nsplits=" + nSplits + ";\n");
        outputFile.write("FORMAT labels=no weights=" + (weights != null ? "yes" : "no") + " confidences=no intervals=no;\n");
        outputFile.write("CYCLE");
        for (int i = 0; i < cycle.length; i++) {
            outputFile.write(" " + (cycle[i] + 1));
        }
        outputFile.write(";\n");
        outputFile.write("MATRIX\n");
        int splitNr = 1;
        for (int i = 0; i < splits.length; i++) {
            if (active[i] == 1) {
                outputFile.write("[" + (splitNr++) + "]\t" + (weights != null ? weights[i] + "\t" : ""));
                for (int j = 0; j < splits[i].length; j++) {
                    if (splits[i][j]) {
                        outputFile.write(" " + (j + 1));
                    }
                }
                outputFile.write(",\n");
            }
        }
        outputFile.write(";\n");
        outputFile.write("END; [Splits]");
        outputFile.close();
    }

    public static int[] orderWeights(boolean[] active, double[] weights) {
        List<Integer> indexes = new LinkedList();
        for (int i = 0; i < active.length; i++) {
            if (active[i]) {
                boolean added = false;
                if (indexes.isEmpty()) {
                    indexes.add(i);
                    added = true;
                } else {
                    for (int j = 0; j < indexes.size(); j++) {
                        double w = weights[indexes.get(j)];
                        if (w > weights[i]) {
                            indexes.add(j, i);
                            added = true;
                            break;
                        }
                    }
                }
                if (!added) {
                    indexes.add(i);
                }
            }
        }

        int[] increasing = new int[indexes.size()];
        for (int i = 0; i < increasing.length; i++) {
            increasing[i] = indexes.get(i);
        }

        return increasing;
    }

    public static Set<Integer> getUnique(SplitSystem ss, SplitSystem ss2) {
        Set<Integer> uniq = new HashSet();
        boolean[][] splits = ss.getSplits();
        boolean[] active = ss.getActive();
        System.out.println(splits.length);
        for (int i = 0; i < splits.length; i++) {
            if (active[i] && !ss2.splitExists(splits[i])) {
                uniq.add(i);
            }
        }
        return uniq;
    }

    public static Set<Integer> getCommon(SplitSystem ss, SplitSystem ss2) {
        Set<Integer> uniq = new HashSet();
        boolean[][] splits = ss.getSplits();
        boolean[] active = ss.getActive();
        System.out.println(splits.length);
        for (int i = 0; i < splits.length; i++) {
            if (active[i] && !ss.isTrivial(splits[i]) && ss2.splitExists(splits[i])) {
                uniq.add(i);
            }
        }
        return uniq;
    }



    public static double getAverage(double[] trivial) {
        double avg = 0.0;
        double summed = 0.0;
        for (int i = 0; i < trivial.length; i++) {
            if (trivial[i] > 0) {
                avg += trivial[i];
                summed += 1.0;
            }
        }
        return avg / summed;
    }

    /**
     * Adds dots to the end of the <code>text</code> until length of the line is
     * equal to <code>len</code>.
     *
     * @param text initial line
     * @param len  required length of the text line
     * @return text line of required length with dots appended to the original
     * line
     */
    public static String addDots(String text, int len) {
        String dots = "";
        for (int i = len; i > text.length(); i--) {
            dots += ".";
        }
        return text.concat(dots);
    }

    public static Color getColor(int[] c) {
        return new Color(c[0], c[1], c[2]);
    }

    static int[] colorToInt(Color c) {
        int[] rgb = new int[3];

        rgb[0] = c.getRed();
        rgb[1] = c.getGreen();
        rgb[2] = c.getBlue();

        return rgb;
    }

    public static boolean differentSide(int x1, int y1, int x2, int y2,
                                        int X1, int Y1, int X2, int Y2) {
        return different(x1, y1, x1, y2, X1, Y1, X2, Y2)
                &&
                different(X1, Y1, X2, Y2, x1, y1, x1, y2);
    }

    private static boolean different(int x1, int y1, int x2, int y2,
                                     int X1, int Y1, int X2, int Y2) {
        return (Math.signum((x1 - X1) * (y2 - Y1) - (y1 - Y1) * (x2 - X1))
                !=
                Math.signum((x1 - X2) * (y2 - Y2) - (y1 - Y2) * (x2 - X2)));
    }
}
