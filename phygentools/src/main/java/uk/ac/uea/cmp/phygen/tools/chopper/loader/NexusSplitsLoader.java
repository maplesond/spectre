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
package uk.ac.uea.cmp.phygen.tools.chopper.loader;

import uk.ac.uea.cmp.phygen.core.ds.Taxon;
import uk.ac.uea.cmp.phygen.core.ds.quartet.Quartet;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetWeights;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA. User: Analysis Date: 2004-jul-11 Time: 23:09:07 To
 * change this template use Options | File Templates.
 */
public class NexusSplitsLoader extends AbstractLoader {

    @Override
    public void load(File file, double weight) throws IOException {

        this.weights.add(weight);
        QuartetWeights qW = null;

        /**
         *
         * Code for reading a file and initializing properly
         *
         */
        int N = 0;

        /**
         *
         * File reader
         *
         */
        BufferedReader fileInput = new BufferedReader(new FileReader(file));

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

                        taxonNames.add(new Taxon(""));
                    }

                    qW = new QuartetWeights(Quartet.over4(N));

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

                        taxonNames.set(n, new Taxon(aS));
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

                            LinkedList<Integer> setA = new LinkedList<>();
                            LinkedList<Integer> setB = new LinkedList<>();

                            while (bT.hasMoreTokens()) {

                                setA.add(Integer.parseInt(bT.nextToken()));
                            }

                            for (int n = 0; n < N; n++) {

                                if (!setA.contains(new Integer(n + 1))) {

                                    setB.add(n + 1);
                                }
                            }

                            if (setA.size() > 1 && setB.size() > 1) {

                                // we have a non-trivial split!
                                // which we must have, for trivial splits match no quartets...

                                // so, for all quartets in here, add the length to their value

                                for (int iA1 = 0; iA1 < setA.size() - 1; iA1++) {

                                    for (int iA2 = iA1 + 1; iA2 < setA.size(); iA2++) {

                                        int a1 = setA.get(iA1);
                                        int a2 = setA.get(iA2);

                                        for (int iB1 = 0; iB1 < setB.size() - 1; iB1++) {

                                            for (int iB2 = iB1 + 1; iB2 < setB.size(); iB2++) {

                                                int b1 = setB.get(iB1);
                                                int b2 = setB.get(iB2);

                                                qW.incrementWeight(new Quartet(a1, a2, b1, b2), w);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    readingState = false;
                }
            }
        }

        qWs.add(qW);
    }

    @Override
    public String getName() {
        return "nexus:st_splits";
    }
}
