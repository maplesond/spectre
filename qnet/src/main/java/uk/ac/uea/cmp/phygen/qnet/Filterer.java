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
package uk.ac.uea.cmp.phygen.qnet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA. User: Analysis Date: 2004-jul-19 Time: 19:45:30 To
 * change this template use Options | File Templates.
 */
public class Filterer {

    public static void main(String[] args) {

        Filterer aFilterer = new Filterer();

        if (args.length > 2) {

            //System.out.print ("QNet.Filterer: loading file " + args [0] + "... ");

            aFilterer.load(args[0]);

            //System.out.println ("done.");

            //System.out.print ("QNet.Filterer: saving weights to file " + args [1] + "... ");

            aFilterer.write(args[1], Double.parseDouble(args[2]));

            //System.out.println ("done.");

        } else {

            System.out.println("QNet.Filterer: Please specify input file name, output file name and threshold!");
            System.exit(1);

        }

    }

    public Filterer() {

        taxonNames = new LinkedList();
        weights = new LinkedList();
        splits = new LinkedList();

    }

    public void load(String fileName) {

        taxonNames = new LinkedList();

        /**
         *
         * Code for reading a file and initializing properly
         *
         */
        int N = 0;

        try {

            /**
             *
             * File reader
             *
             */
            BufferedReader fileInput = new BufferedReader(new FileReader(fileName));

            /**
             *
             * Keep on reading and tokenizing until... a token is found
             * beginning with "ntax=" parse its remainder for the number of
             * taxa.
             *
             * Keep on reading until a token is found "TAXLABELS". Then read N
             * lines which will be the taxon names. We assume there are n choose
             * 4 quartets.
             *
             * Keep on reading and tokenizing until "st_splits;" is found. Then
             * proceed to "MATRIX". Then read the quartet lines until a line
             * starts with ";".
             *
             *
             *
             */
            boolean readingState = true;

            while (readingState) {

                String aLine = fileInput.readLine();
                StringTokenizer sT = new StringTokenizer(aLine);

                while (sT.hasMoreTokens()) {

                    String tT = sT.nextToken();

                    if (tT.startsWith("ntax=")) {

                        N = Integer.parseInt(tT.substring(5, tT.length() - 1));

                        for (int n = 0; n < N; n++) {

                            taxonNames.add(new String(""));

                        }

                        readingState = false;

                    }

                }

            }

            readingState = true;

            while (readingState) {

                String aLine = fileInput.readLine();
                StringTokenizer sT = new StringTokenizer(aLine);

                while (sT.hasMoreTokens()) {

                    String tT = sT.nextToken();

                    if (tT.startsWith("TAXLABELS")) {

                        for (int n = 0; n < N; n++) {

                            String aS = fileInput.readLine();

                            taxonNames.set(n, aS);

                        }

                        readingState = false;

                    }

                }


            }

            readingState = true;

            while (readingState) {

                String aLine = fileInput.readLine();
                StringTokenizer sT = new StringTokenizer(aLine);

                while (sT.hasMoreTokens()) {

                    String tT = sT.nextToken();

                    if (tT.startsWith("st_splits;")) {

                        readingState = false;

                    }

                }

            }

            readingState = true;

            while (readingState) {

                String aLine = fileInput.readLine();
                StringTokenizer sT = new StringTokenizer(aLine);

                while (sT.hasMoreTokens()) {

                    String tT = sT.nextToken();

                    if (tT.startsWith("CYCLE")) {

                        cycleString = aLine;
                        readingState = false;

                    }

                }

            }

            readingState = true;

            while (readingState) {

                String aLine = fileInput.readLine();
                StringTokenizer sT = new StringTokenizer(aLine);

                while (sT.hasMoreTokens()) {

                    String tT = sT.nextToken();

                    if (tT.startsWith("MATRIX")) {

                        boolean splitState = true;

                        while (splitState) {

                            String bLine = fileInput.readLine().trim();

                            if (bLine.startsWith(";")) {

                                splitState = false;

                            } else {

                                // BEGIN reading a split line

                                if (bLine.endsWith(",")) {

                                    bLine = bLine.substring(0, bLine.length() - 1);

                                }

                                StringTokenizer bT = new StringTokenizer(bLine);

                                int id = Integer.parseInt(bT.nextToken());

                                double w = Double.parseDouble(bT.nextToken());

                                LinkedList setA = new LinkedList();

                                while (bT.hasMoreTokens()) {

                                    setA.add(new Integer(Integer.parseInt(bT.nextToken())));

                                }

                                splits.add(setA);
                                weights.add(new Double(w));

                                // END reading a split line

                            }

                        }

                        readingState = false;

                    }

                }

            }

        } catch (IOException e) {
        }

    }

    public void write(String fileName, double threshold) {

        int N = taxonNames.size();

        int noSplits = splits.size();
        boolean[] splitExists = new boolean[noSplits];

        int existingSplits = 0;

        for (int i = 0; i < noSplits; i++) {

            double maxWeight = 0.0;

            LinkedList setA = (LinkedList) splits.get(i);

            // trivial splits are always shown, and added later

            if (setA.size() != 1 && setA.size() != N - 1) {

                for (int j = 0; j < noSplits; j++) {

                    if (i != j) {

                        LinkedList setB = (LinkedList) splits.get(j);
                        LinkedList tempList = (LinkedList) setB.clone();

                        tempList.retainAll(setA);

                        if (tempList.size() != 0 && tempList.size() != Math.min(setA.size(), setB.size())) {

                            // conflicting

                            double w = ((Double) weights.get(j)).doubleValue();

                            if (w > maxWeight) {

                                maxWeight = w;

                            }

                        }

                    }

                }

                double w = ((Double) weights.get(i)).doubleValue();

                if (w > maxWeight * threshold) {

                    splitExists[i] = true;
                    existingSplits++;

                } else {

                    splitExists[i] = false;

                }

            } else {

                splitExists[i] = false;

            }

        }

        // print

        String nexusString = new String();

        nexusString += "#NEXUS\nBEGIN taxa;\nDIMENSIONS ntax=" + N + ";\nTAXLABELS\n";

        for (int i = 0; i < N; i++) {

            nexusString += ((String) taxonNames.get(i)) + "\n";

        }

        nexusString += ";\nEND;\n\nBEGIN st_splits;\nDIMENSIONS ntax=" + N + " nsplits=" + (existingSplits + N) + ";\n";
        nexusString += "FORMAT\nlabels\nweights\n;\nPROPERTIES\nweakly compatible\ncyclic\n;\n";

        nexusString += cycleString;

        nexusString += "\nMATRIX\n";

        int s = 0;

        int wn = 0;
        double ws = 0.0;

        for (int i = 0; i < noSplits; i++) {

            if (splitExists[i]) {

                // this split exists

                s++;

                double w = ((Double) weights.get(i)).doubleValue();

                nexusString += "" + (s) + "   " + w + "  ";

                wn++;
                ws += w;

                LinkedList aSplit = (LinkedList) splits.get(i);

                for (int p = 0; p < aSplit.size(); p++) {

                    nexusString += " " + ((Integer) aSplit.get(p)).intValue();

                }

                nexusString += ",\n";

            }

        }

        double mw = ws / ((double) wn);

        for (int i = 0; i < N; i++) {

            s++;

            nexusString += "" + (s) + "   " + mw + "  ";

            nexusString += " " + (i + 1);

            nexusString += ",\n";

        }

        nexusString += ";\nEND;";

        try {

            FileWriter fileOutput = new FileWriter(fileName);

            fileOutput.write(nexusString);

            fileOutput.close();

        } catch (IOException e) {
        }

    }
    LinkedList taxonNames;
    LinkedList splits;
    LinkedList weights;
    String cycleString;
}
