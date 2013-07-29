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
package uk.ac.uea.cmp.phygen.tools.chopper;

import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetWeights;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA. User: Analysis Date: 2004-jul-11 Time: 23:09:07 To
 * change this template use Options | File Templates.
 */
public class NexusSplitsLoader implements Source {

    public void load(String fileName, double weight) {

        index = 0;

        this.weight = weight;

        qW = new QuartetWeights();

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

                    if (tT.toLowerCase().startsWith("ntax=")) {

                        N = Integer.parseInt(tT.substring(5, tT.length() - 1));

                        for (int n = 0; n < N; n++) {

                            taxonNames.add(new String(""));

                        }

                        qW.ensureCapacity(N);
                        qW.setSize(QuartetWeights.over4(N));
                        qW.initialize();

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

                    if (tT.toUpperCase().startsWith("TAXLABELS")) {

                        for (int n = 0; n < N; n++) {

                            StringTokenizer aST = new StringTokenizer(fileInput.readLine().trim());

                            String aS = aST.nextToken();

                            if (aST.hasMoreTokens()) {

                                aS = aST.nextToken();

                            }

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

                    if (tT.toLowerCase().startsWith("st_splits;") || tT.toLowerCase().startsWith("splits;")) {

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

                    if (tT.toUpperCase().startsWith("MATRIX")) {

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

//                                System.out.println("bline: \"" + bLine + "\"");

                                StringTokenizer bT = new StringTokenizer(bLine);
//                                
                                if (bT.nextToken().startsWith("[")) {
                                    bT.nextToken();
                                    bT.nextToken();
                                }
                                double w = Double.parseDouble(bT.nextToken());

                                LinkedList setA = new LinkedList();
                                LinkedList setB = new LinkedList();

                                while (bT.hasMoreTokens()) {

                                    setA.add(new Integer(Integer.parseInt(bT.nextToken())));

                                }

                                for (int n = 0; n < N; n++) {

                                    if (!setA.contains(new Integer(n + 1))) {

                                        setB.add(new Integer(n + 1));

                                    }

                                }

                                if (setA.size() > 1 && setB.size() > 1) {

                                    // we have a non-trivial split!
                                    // which we must have, for trivial splits match no quartets...

                                    // so, for all quartets in here, add the weight to their value

                                    for (int iA1 = 0; iA1 < setA.size() - 1; iA1++) {

                                        for (int iA2 = iA1 + 1; iA2 < setA.size(); iA2++) {

                                            int a1 = ((Integer) setA.get(iA1)).intValue();
                                            int a2 = ((Integer) setA.get(iA2)).intValue();

                                            for (int iB1 = 0; iB1 < setB.size() - 1; iB1++) {

                                                for (int iB2 = iB1 + 1; iB2 < setB.size(); iB2++) {

                                                    int b1 = ((Integer) setB.get(iB1)).intValue();
                                                    int b2 = ((Integer) setB.get(iB2)).intValue();

                                                    qW.setWeight(a1, a2, b1, b2, qW.getWeight(a1, a2, b1, b2) + w);

                                                }

                                            }

                                        }

                                    }

                                }

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

    public void process() {
    }

    public void harvestNames(LinkedList newTaxonNames) {

        ListIterator lI = taxonNames.listIterator();

        while (lI.hasNext()) {

            String taxonName = (String) lI.next();

            if (newTaxonNames.contains(taxonName)) {
            } else {

                newTaxonNames.add(taxonName);

            }

        }

    }

    public void translate(LinkedList newTaxonNames) {

        qW = qW.translate(taxonNames, newTaxonNames);

    }

    public LinkedList getQuartetWeights() {

        LinkedList result = new LinkedList();

        result.add(qW);

        return result;

    }

    public LinkedList getWeights() {

        LinkedList result = new LinkedList();

        result.add(new Double(weight));

        return result;

    }

    public double getWSum() {

        return weight;

    }

    public LinkedList getTaxonNames() {

        LinkedList result = new LinkedList();

        result.add(taxonNames);

        return result;

    }

    public QuartetWeights getNextQuartetWeights() {

        index++;

        return qW;

    }

    public double getNextWeight() {

        return weight;

    }

    public boolean hasMoreSets() {

        if (index < 1) {

            return true;

        } else {

            return false;

        }

    }
    QuartetWeights qW;
    LinkedList taxonNames;
    double weight;
    int index;
}
