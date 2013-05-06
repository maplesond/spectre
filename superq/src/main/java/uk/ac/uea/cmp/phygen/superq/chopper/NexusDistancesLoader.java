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
package uk.ac.uea.cmp.phygen.superq.chopper;

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
public class NexusDistancesLoader implements Source {

    public void load(String fileName, double weight) {

        boolean upper = false;

        index = 0;

        D = null;

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

                        D = new double[N][N];

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

                    if (tT.toLowerCase().startsWith("distances;")) {

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

                                // BEGIN reading a distance line

                                StringTokenizer bT = new StringTokenizer(bLine);

                                String taxa = bT.nextToken().trim();
                                String blub = "";

                                if (taxa.startsWith("[") && taxa.endsWith("]")) {
                                    taxa = bT.nextToken().trim();
                                    if (taxa.startsWith("'") && taxa.endsWith("'")) {
                                        bT.nextToken();
                                        taxa = bT.nextToken().trim();
                                    }


                                }

                                int pos = taxonNames.indexOf(taxa);

                                int rowCount = 0;

                                while (bT.hasMoreTokens()) {

                                    D[pos][rowCount] = Double.parseDouble(bT.nextToken());

                                    rowCount++;

                                }

                                if (pos == 0 && rowCount > 1) {

                                    upper = true;

                                }

                                // END reading a distance line

                            }

                        }

                        readingState = false;

                    }

                }

            }

        } catch (IOException e) {
        }

        if (upper) {

            for (int i = 0; i < N; i++) {

                for (int j = i; j < N; j++) {

                    D[j][i] = D[i][j];

                }

            }

        } else {

            for (int i = 0; i < N; i++) {

                for (int j = i; j < N; j++) {

                    D[i][j] = D[j][i];

                }

            }

        }

        for (int a = 0; a < N - 3; a++) {

            for (int b = a + 1; b < N - 2; b++) {

                for (int c = b + 1; c < N - 1; c++) {

                    for (int d = c + 1; d < N; d++) {

                        double w1, w2, w3;

                        w1 = (D[a][c] + D[b][c] + D[a][d] + D[b][d] + - 2 * D[a][b] - 2 * D[c][d]) / 4.0;
                        w2 = (D[a][b] + D[c][b] + D[a][d] + D[c][d] + - 2 * D[a][c] - 2 * D[b][d]) / 4.0;
                        w3 = (D[a][c] + D[d][c] + D[a][b] + D[d][b] + - 2 * D[a][d] - 2 * D[c][b]) / 4.0;

                        double min = Math.min(w1, Math.min(w2, w3));

                        qW.setWeight(a + 1, b + 1, c + 1, d + 1, w1 - min);
                        qW.setWeight(a + 1, c + 1, b + 1, d + 1, w2 - min);
                        qW.setWeight(a + 1, d + 1, b + 1, c + 1, w3 - min);

                    }

                }

            }

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
    double[][] D;
    QuartetWeights qW;
    LinkedList taxonNames;
    double weight;
    int index;
}
