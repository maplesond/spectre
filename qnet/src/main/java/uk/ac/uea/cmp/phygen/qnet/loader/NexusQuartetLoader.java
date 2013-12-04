/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
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

package uk.ac.uea.cmp.phygen.qnet.loader;

import org.kohsuke.MetaInfServices;
import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.Taxon;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetNetworkAgglomerator;
import uk.ac.uea.cmp.phygen.core.ds.quartet.Quartet;
import uk.ac.uea.cmp.phygen.core.ds.quartet.WeightedQuartetMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 20/11/13
 * Time: 00:19
 * To change this template use File | Settings | File Templates.
 */
@MetaInfServices
public class NexusQuartetLoader implements Source {

    @Override
    public QuartetNetworkAgglomerator load(File file, boolean logNormalize) throws IOException {

        WeightedQuartetMap theQuartetWeights = new WeightedQuartetMap();
        Taxa allTaxa = new Taxa();
        List<Taxa> taxaSets = new ArrayList<>();

        /**
         *
         * Code for reading a file and initializing properly
         *
         */
        boolean useMax = true;

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
         * Keep on reading and tokenizing until "st_quartets;" is found.
         * Then proceed to "MATRIX". Then read the quartet lines until a
         * line starts with ";".
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
                        Taxon newTaxon = new Taxon("", n+1);
                        allTaxa.add(newTaxon);
                        taxaSets.add(new Taxa(newTaxon));
                    }

                    theQuartetWeights = new WeightedQuartetMap();
                    useMax = true;

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

                        StringTokenizer aT = new StringTokenizer(fileInput.readLine());

                        String aS = aT.nextToken();

                        while (aT.hasMoreTokens()) {

                            aS = aT.nextToken();
                        }

                        allTaxa.set(n, new Taxon(aS));
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

                if (tT.toLowerCase().startsWith("st_quartets;")) {

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

                    boolean quartetState = true;

                    while (quartetState) {

                        String bLine = fileInput.readLine();

                        if (bLine.startsWith(";")) {

                            quartetState = false;

                        } else {

                            StringTokenizer bT = new StringTokenizer(bLine);

                            String label = bT.nextToken();
                            double weight = Double.parseDouble(bT.nextToken());
                            int x = Integer.parseInt(bT.nextToken());
                            int y = Integer.parseInt(bT.nextToken());
                            String sC = bT.nextToken();
                            int u = Integer.parseInt(bT.nextToken());
                            String cS = bT.nextToken();
                            int v = Integer.parseInt(cS.substring(0, cS.length() - 1));

                            if (x != y && x != u && x != v && y != u && y != v && u != v) {

                                theQuartetWeights.setWeight(new Quartet(x, y, u, v), weight);

                            }
                        }
                    }

                    readingState = false;
                }
            }
        }

        if (logNormalize) {
            theQuartetWeights.logNormalize(useMax);
        } else {
            theQuartetWeights.normalize(useMax);
        }

        return null; //new QuartetNetworkList(new QuartetNetwork(allTaxa, 1.0, theQuartetWeights));
    }

    @Override
    public String getName() {
        return "nexus";
    }
}
