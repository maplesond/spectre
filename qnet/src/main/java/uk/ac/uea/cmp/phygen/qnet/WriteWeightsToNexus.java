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

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetIndex;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetWeights;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class WriteWeightsToNexus {

    //mode tells the method whether we want to write
    //standard ... 0
    //minimum  ... 1
    //maximum  ... 2
    public static void writeWeights(QNet parent, ArrayList cN, String outputName, double[] y, double[] x, int mode) {
        ArrayList theLists = parent.getTheLists();
        QuartetWeights theQuartetWeights = parent.getWeights();
        ArrayList taxonNames = parent.getTaxonNames();
        boolean useMax = parent.getUseMax();
        int N = parent.getN();

        TaxonList c = (TaxonList) theLists.get(0);

        Pair<Integer, Integer>[] splitIndices = new Pair[N * (N - 1) / 2 - N];

        int n = 0;
        int m;

        for (m = 1; m < N - 1; m++) {

            for (int j = m + 2; j < N + 1; j++) {

                if (m != 1 || j != N) {

                    // valid split
                    splitIndices[n] = new ImmutablePair<Integer, Integer>(m, j);
                    n++;
                }
            }
        }

        double[] f = new double[N * (N - 1) * (N - 2) * (N - 3) / 12];
        QuartetIndex[] quartetIndices = new QuartetIndex[N * (N - 1) * (N - 2) * (N - 3) / 12];

        n = 0;

        for (int i = 1; i < N - 2; i++) {

            for (int j = i + 1; j < N - 1; j++) {

                for (int k = j + 1; k < N; k++) {

                    for (int l = k + 1; l < N + 1; l++) {

                        int cI = ((Integer) c.get(i - 1)).intValue();
                        int cJ = ((Integer) c.get(j - 1)).intValue();
                        int cK = ((Integer) c.get(k - 1)).intValue();
                        int cL = ((Integer) c.get(l - 1)).intValue();

                        quartetIndices[n] = new QuartetIndex(i, j, k, l);
                        f[n] = theQuartetWeights.getWeight(cI, cJ, cK, cL);
                        n++;

                        quartetIndices[n] = new QuartetIndex(i, l, j, k);
                        f[n] = theQuartetWeights.getWeight(cI, cL, cJ, cK);
                        n++;
                    }
                }
            }
        }

        int noSplits = N * (N - 1) / 2 - N;
        boolean[] splitExists = new boolean[noSplits];

        int existingSplits = 0;
        // stuff to print _all_ splits

        for (int i = 0; i < noSplits; i++) {

            if (mode == 0)//standard
            {
                if (y[i] > 0.0) {
                    splitExists[i] = true;
                    existingSplits++;
                } else {
                    splitExists[i] = false;
                }
            } else if (mode == 1)//minimum
            {
                if ((y[i] > 0.0) && (y[i] < x[i])) {
                    splitExists[i] = true;
                    existingSplits++;
                } else {
                    splitExists[i] = false;

                }
            } else if (mode == 2)//maximum
            {
                if (y[i] > x[i]) {
                    splitExists[i] = true;
                    existingSplits++;
                } else {
                    splitExists[i] = false;
                }
            }

        }
        // print

        String nexusString = new String();

        nexusString += "#NEXUS\nBEGIN taxa;\nDIMENSIONS ntax=" + N + ";\nTAXLABELS\n";

        for (int i = 0; i < N; i++) {

            nexusString += ((String) taxonNames.get(i)) + "\n";
        }

        nexusString += ";\nEND;\n\nBEGIN st_splits;\nDIMENSIONS ntax=" + N + " nsplits=" + (existingSplits + N) + ";\n";
        nexusString += "FORMAT\nlabels\nweights\n;\nPROPERTIES\nweakly compatible\ncyclic\n;\nCYCLE";

        for (int i = 0; i < N; i++) {

            nexusString += " " + ((Integer) (c.get(i))).intValue();
        }

        nexusString += ";\nMATRIX\n";

        int s = 0;

        int wn = 0;
        double ws = 0.0;

        for (int i = 0; i < N * (N - 1) / 2 - N; i++) {

            if (splitExists[i]) {

                // this split exists

                s++;

                nexusString += "" + (s) + "   " + y[i] + "  ";

                wn++;
                ws += y[i];

                Pair<Integer, Integer> sI = splitIndices[i];

                for (int p = sI.getLeft() + 1; p < sI.getRight() + 1; p++) {

                    nexusString += " " + ((Integer) c.get(p - 1)).intValue();

                }

                nexusString += ",\n";

            }

        }

        double mw = 1.0;
        if (wn > 0) {
            mw = ws / ((double) wn);
        }

        for (int i = 0; i < N; i++) {

            s++;

            nexusString += "" + (s) + "   " + mw + "  ";

            nexusString += " " + (i + 1);

            nexusString += ",\n";

        }

        s = 0;

        wn = 0;
        ws = 0.0;

        mw = 1.0;
        if (wn > 0) {
            mw = ws / ((double) wn);
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
