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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class QWeightLoader implements Source {

    private static Logger log = LoggerFactory.getLogger(QWeightLoader.class);

    public void load(String fileName, double weight) throws IOException {

        index = 0;

        this.weight = weight;

        qW = new QuartetWeights();

        taxonNames = new LinkedList();

        /**
         *
         * Code for reading a file and initializing properly
         *
         */
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
            if (theLine.toLowerCase().trim().startsWith("description:")) {

                while (!theLine.toLowerCase().endsWith(";") && !theLine.trim().endsWith(";")) {

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
                qW.setWeight(a, b, c, d, w1, w2, w3);

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
                 * Take name and take care of names that contain blanks
                 *
                 */
                taxonname = "";
                while (lineTokenizer.hasMoreTokens()) {
                    if (taxonname.length() > 0) {
                        taxonname = taxonname + " ";
                    }
                    taxonname = taxonname + lineTokenizer.nextToken();
                }

                taxonNames.set(theNumber - 1, taxonname);

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

                    taxonNames.add(new String(""));

                }

                qW.ensureCapacity(N);
                qW.initialize();

            }

        }


        if (qW.getSize() != qW.over4(N)) {

            throw new IOException("Wrong number of quartets in file!");
        }

        qW.normalize(useMax);

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
