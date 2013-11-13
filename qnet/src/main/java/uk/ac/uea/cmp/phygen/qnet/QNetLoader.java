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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.Taxon;
import uk.ac.uea.cmp.phygen.core.ds.quartet.Quartet;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetWeighting;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetWeights;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

public class QNetLoader {

    private static Logger log = LoggerFactory.getLogger(QNetLoader.class);

    /**
     * Load method
     */
    public static void load(QNet qnet, String fileName, boolean logNormalize) throws IOException {

        QuartetWeights theQuartetWeights = qnet.getWeights();

        Taxa allTaxa = qnet.getAllTaxa();

        List<Taxa> taxaSets = qnet.getTaxaSets();

        /**
         *
         * Code for reading a file and initializing properly
         *
         */
        //System.out.print ("QNet: Loading data... ");
        boolean useMax = true;

        int N = 0;

        /**
         *
         * Error-handling
         *
         */
        /**
         *
         * Have the number of quartets been specified?
         *
         */
        boolean numberKnown = false;

        /**
         *
         * Have the sense been specified?
         *
         */
        boolean senseKnown = false;

        /**
         *
         * File reader
         *
         */
        BufferedReader fileInput = new BufferedReader(new FileReader(fileName));

        /**
         *
         * Lines are read one at a time, added together, parsed by
         * semicolons, then parsed by space and colon
         *
         */
        /**
         *
         * Input one-liner
         *
         */
        String input = new String("");
        String taxonname = null;

        /**
         *
         * Read while there�s reading to be done
         *
         */
        while ((input = fileInput.readLine()) != null) {

            /**
             *
             * Parse
             *
             * Note now that it requires lower-case
             *
             * Process each command
             *
             */
            String theLine = input;

            /**
             *
             * If this is a description line, we just read, we don�t bother
             * to save the data read
             *
             */
            if (theLine.trim().startsWith("description:")) {

                while (!theLine.endsWith(";") && !theLine.trim().endsWith(";")) {

                    theLine = "description: " + fileInput.readLine();

                }

            } /**
             *
             * Otherwise, it is significant...
             *
             */
            else {

                while (!theLine.endsWith(";") && !theLine.trim().endsWith(";")) {

                    theLine += fileInput.readLine();

                }

            }

            theLine = theLine.trim();

            theLine = theLine.substring(0, theLine.length() - 1);

            /**
             *
             * Tokenize each line by space and colon
             *
             */
            StringTokenizer lineTokenizer = new StringTokenizer(theLine, ": ");

            /**
             *
             * Initial word
             *
             */
            String theFirst = lineTokenizer.nextToken();

            /**
             *
             * The actual switch
             *
             */
            if (theFirst.equalsIgnoreCase("quartet")) {

                /**
                 *
                 * Having read a quartet line, read in the weights
                 *
                 * The coordinates, in the order written
                 *
                 */
                int a = (new Integer(lineTokenizer.nextToken())).intValue();

                int b = (new Integer(lineTokenizer.nextToken())).intValue();

                int c = (new Integer(lineTokenizer.nextToken())).intValue();

                int d = (new Integer(lineTokenizer.nextToken())).intValue();

                /**
                 *
                 * Skip "name" token
                 *
                 */
                lineTokenizer.nextToken();

                /**
                 *
                 * The weights, in the order written
                 *
                 */
                double w1 = (new Double(lineTokenizer.nextToken())).doubleValue();

                double w2 = (new Double(lineTokenizer.nextToken())).doubleValue();

                double w3 = (new Double(lineTokenizer.nextToken())).doubleValue();

                /**
                 *
                 * Set it, just as it is written
                 *
                 */
                theQuartetWeights.setWeight(new Quartet(a, b, c, d), new QuartetWeighting(w1, w2, w3));

            } else if (theFirst.equalsIgnoreCase("taxon")) {

                /**
                 *
                 * Having read a taxon line, add the taxon
                 *
                 */
                int theNumber = (new Integer(lineTokenizer.nextToken())).intValue();

                /**
                 *
                 * Step forward
                 *
                 */
                lineTokenizer.nextToken();

                /**
                 *
                 * Take name
                 *
                 */
                taxonname = "";
                while (lineTokenizer.hasMoreTokens()) {
                    if (taxonname.length() > 0) {
                        taxonname = taxonname + " ";
                    }
                    taxonname = taxonname + lineTokenizer.nextToken();
                }

                allTaxa.set(theNumber - 1, new Taxon(taxonname));

            } else if (theFirst.equalsIgnoreCase("description")) {
                /**
                 *
                 * Having read a comment, do nothing
                 *
                 */
            } else if (theFirst.equalsIgnoreCase("sense")) {

                /**
                 *
                 * Having read a sense line, set the sense accordingly
                 *
                 */
                String theSecond = lineTokenizer.nextToken();

                if (theSecond.equalsIgnoreCase("max")) {

                    useMax = true;

                    senseKnown = true;

                } else if (theSecond.equalsIgnoreCase("min")) {

                    useMax = false;

                    senseKnown = true;

                }

            } else if (theFirst.equalsIgnoreCase("taxanumber")) {

                /**
                 *
                 * Having read the number of taxa, set it accordingly
                 *
                 */
                String theSecond = lineTokenizer.nextToken();

                N = (new Integer(theSecond)).intValue();

                numberKnown = true;

                for (int n = 0; n < N; n++) {

                    Taxon newTaxon = new Taxon("", n+1);
                    allTaxa.add(newTaxon);
                    taxaSets.add(new Taxa(newTaxon));
                }

                theQuartetWeights.ensureCapacity(N);
            }
        }


        if (theQuartetWeights.size() != Quartet.over4(N)) {

            throw new IOException("QNet: Wrong number of quartets in file!");
        }

        qnet.setN(N);

        if (logNormalize) {
            theQuartetWeights.logNormalize(useMax);
        } else {
            theQuartetWeights.normalize(useMax);
        }

        // parameter is now obsolete

        qnet.setUseMax(true);

        //System.out.println ("done.");

    }

    public static void loadNexus(QNet parent, String fileName, boolean logNormalize) throws IOException {

        QuartetWeights theQuartetWeights = parent.getWeights();

        Taxa allTaxa = parent.getAllTaxa();

        List<Taxa> taxaSets = parent.getTaxaSets();

        /**
         *
         * Code for reading a file and initializing properly
         *
         */
        log.info("QNet: Loading data... ");

        boolean useMax = true;

        int N = 0;

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

                    theQuartetWeights = new QuartetWeights(N);
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
            log.debug(aLine);
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

        log.info("done.");

        parent.setN(N);
        parent.setUseMax(useMax);

        if (logNormalize) {
            theQuartetWeights.logNormalize(useMax);
        } else {
            theQuartetWeights.normalize(useMax);
        }

    }
}