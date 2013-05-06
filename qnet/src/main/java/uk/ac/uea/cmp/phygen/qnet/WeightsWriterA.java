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

import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetWeights;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

class WeightsWriterA {

    /**
     *
     * Write weights method.
     *
     * Calculates split weights and prints them to a nexus file.
     *
     */
    public static void writeWeights(QNet parent, String outputName) {

        ArrayList theLists = parent.getTheLists();
        QuartetWeights theQuartetWeights = parent.getWeights();
        ArrayList taxonNames = parent.getTaxonNames();
        boolean useMax = parent.getUseMax();
        int N = parent.getN();

        /**
         *
         * But first, time for splits.
         *
         */
        /**
         *
         * Splits and weights
         *
         */
        ArrayList splits = new ArrayList();
        ArrayList weights = new ArrayList();

        for (int i = 1; i < N - 1; i++) {

            for (int j = i + 1; j <= Math.min(N - 1, N + i - 3); j++) {

                /**
                 *
                 * Note - 1 below for indexing
                 *
                 */
                /**
                 *
                 * The two edges... their weights...
                 *
                 * Helper variables
                 *
                 */
                double wLeft = 0;
                double wRight = 0;
                double w;
                int x, y, u, v;

                /**
                 *
                 * Get list from i to and including j
                 *
                 */
                TaxonList inside = ((TaxonList) theLists.get(0)).sublist(i - 1, j - 1);


                /**
                 *
                 * Get reverse-order complement
                 *
                 */
                TaxonList outside = ((TaxonList) theLists.get(0)).complement(i - 1, j - 1);

                /**
                 *
                 * Now, all quartet pairs fulfilling the conditions...
                 *
                 */
                for (int m = 1; m < inside.size(); m++) {

                    for (int n = 1; n < outside.size(); n++) {

                        x = ((Integer) inside.get(0)).intValue();
                        y = ((Integer) inside.get(m)).intValue();
                        u = ((Integer) outside.get(0)).intValue();
                        v = ((Integer) outside.get(n)).intValue();

                        if (useMax) {

                            wLeft += (theQuartetWeights.getWeight(x, y, u, v)
                                      - (theQuartetWeights.getWeight(x, u, y, v) + theQuartetWeights.getWeight(x, v, u, y)) * 0.5);

                        } else {

                            wLeft -= (theQuartetWeights.getWeight(x, y, u, v)
                                      - (theQuartetWeights.getWeight(x, u, y, v) + theQuartetWeights.getWeight(x, v, u, y)) * 0.5);

                        }

                    }

                }

                wLeft = wLeft / ((inside.size() - 1) * (outside.size() - 1));

                for (int m = 0; m < inside.size() - 1; m++) {

                    for (int n = 0; n < outside.size() - 1; n++) {

                        x = ((Integer) inside.get(inside.size() - 1)).intValue();
                        y = ((Integer) inside.get(m)).intValue();
                        u = ((Integer) outside.get(outside.size() - 1)).intValue();
                        v = ((Integer) outside.get(n)).intValue();

                        if (useMax) {

                            wRight += (theQuartetWeights.getWeight(x, y, u, v)
                                       - (theQuartetWeights.getWeight(x, u, y, v) + theQuartetWeights.getWeight(x, v, u, y)) * 0.5);

                        } else {

                            wRight -= (theQuartetWeights.getWeight(x, y, u, v)
                                       - (theQuartetWeights.getWeight(x, u, y, v) + theQuartetWeights.getWeight(x, v, u, y)) * 0.5);

                        }

                    }

                }

                wRight = wRight / ((inside.size() - 1) * (outside.size() - 1));

                w = Math.min(wLeft, wRight);

                /**
                 *
                 * Add to list...
                 *
                 */
                if (w > 0) {

                    splits.add(inside);

                    weights.add(new Double(w));

                }

            }

        }

        /**
         *
         * Then, add all trivial splits with an average weight
         *
         */
        double averageWeight = 0;

        for (int p = 0; p < weights.size(); p++) {

            averageWeight += ((Double) weights.get(p)).doubleValue();

        }

        if (weights.size() > 0) {

            averageWeight = averageWeight / weights.size();

        }

        for (int n = 0; n < N; n++) {

            splits.add(new TaxonList(n + 1));
            weights.add(new Double(averageWeight));

        }

        /**
         *
         * Then, print.
         *
         */
        String nexusString = new String();

        nexusString += "#NEXUS\nBEGIN taxa;\nDIMENSIONS ntax=" + N + ";\nTAXLABELS\n";

        for (int n = 0; n < N; n++) {

            nexusString += ((String) taxonNames.get(n)) + "\n";

        }

        nexusString += ";\nEND;\n\nBEGIN st_splits;\nDIMENSIONS ntax=" + N + " nsplits=" + splits.size() + ";\n";
        nexusString += "FORMAT\nlabels\nweights\n;\nPROPERTIES\nFIT=100\nweakly compatible\ncyclic\n;\nCYCLE";

        for (int n = 0; n < N; n++) {

            nexusString += " " + ((Integer) ((TaxonList) theLists.get(0)).get(n)).intValue();

        }

        nexusString += ";\nMATRIX\n";

        for (int n = 0; n < splits.size(); n++) {

            /**
             *
             * Add one for splitstree...
             *
             */
            nexusString += "" + (n + 1) + "   " + ((Double) weights.get(n)).doubleValue() + "  ";

            TaxonList aSplit = (TaxonList) splits.get(n);

            for (int p = 0; p < aSplit.size(); p++) {

                /**
                 *
                 * Add one for splitstree...
                 *
                 */
                nexusString += " " + ((Integer) aSplit.get(p)).intValue();

            }

            nexusString += ",\n";

        }

        nexusString += ";\nEND;";

        try {


            FileWriter fileOutput = new FileWriter(outputName);

            fileOutput.write(nexusString);

            fileOutput.close();

        } catch (IOException e) {
        }

    }
}