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


import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetIndex;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetWeights;
import uk.ac.uea.cmp.phygen.core.math.matrix.BitMatrix;
import uk.ac.uea.cmp.phygen.core.math.matrix.SymmetricMatrix;
import uk.ac.uea.cmp.phygen.core.math.matrix.UpperTriangularMatrix;

import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

class WeightsWriterNNLS {

    /**
     *
     * Write weights for the presented tree.
     *
     * Calculates split weights and prints them to a nexus file.
     *
     */
    public static void writeWeights(QNet parent, ArrayList cN, String outputName, double tolerance) {

        boolean verbose = false;
        boolean stepMessages = true;
        boolean cycleWarnings = false;
        boolean extract = false;
        boolean startGuess = false;

        ArrayList theLists = parent.getTheLists();
        QuartetWeights theQuartetWeights = parent.getWeights();
        ArrayList taxonNames = parent.getTaxonNames();
        boolean useMax = parent.getUseMax();
        int N = parent.getN();

        // we have N taxa

        // we have theQuartetWeighst weights
        // we have...

        TaxonList c = (TaxonList) theLists.get(0);

        // ... the cyclic ordering

        // we have the taxon names

        // we must now calculate the split weights
        // for such splits as are relevant

        // that is, we first... go through all
        // relevant splits and create a list
        // of split indices

        // there are n choose 2 - n splits
        // i.e. n(n-1)/2 - n

        SplitIndex[] splitIndices = new SplitIndex[N * (N - 1) / 2 - N];

        int n = 0;

        for (int i = 1; i < N - 1; i++) {

            for (int j = i + 2; j < N + 1; j++) {

                if (i != 1 || j != N) {

                    // valid split

                    splitIndices[n] = new SplitIndex(i, j);

                    n++;

                }

            }

        }

        // then we go through relevant quartets and create a list
        // of quartet indices
        // simultaneously we fill a list of quartet weights that correspond to these

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

        // we wish to extract the E matrix separately

        if (extract) {

            // and then, we use our index lists to fill up
            // the E matrix

            BitMatrix E = new BitMatrix(N * (N - 1) * (N - 2) * (N - 3) / 12, N * (N - 1) / 2 - N);

            // simple filler:

            for (int a = 0; a < N * (N - 1) * (N - 2) * (N - 3) / 12; a++) {

                for (int b = 0; b < N * (N - 1) / 2 - N; b++) {

                    // we have

                    int p = splitIndices[b].getN1();
                    int q = splitIndices[b].getN2();
                    int i = quartetIndices[a].getI();
                    int j = quartetIndices[a].getJ();
                    int k = quartetIndices[a].getK();
                    int l = quartetIndices[a].getL();

                    if ((a % 2 == 0) && ((i < j && j <= p && p < k && k < l && l <= q)
                                         || (p < i && i < j && j <= q && q < k && k < l))) {

                        E.setElementAt(a, b, 1);

                    } else if ((a % 2 == 1) && (i <= p && p < k && k < l && l <= q && q < j)) {

                        E.setElementAt(a, b, 1);

                    } else {

                        E.setElementAt(a, b, 0);

                    }

                }

            }

            try {

                FileWriter fileOutput = new FileWriter("E.txt");

                for (int a = 0; a < N * (N - 1) * (N - 2) * (N - 3) / 12; a++) {

                    for (int b = 0; b < N * (N - 1) / 2 - N; b++) {

                        fileOutput.write(E.elementAt(a, b) + " ");

                    }

                    fileOutput.write("\n");

                }

                fileOutput.close();

            } catch (IOException e) {
            }

        }

        if (extract) {

            try {

                FileWriter fileOutput = new FileWriter("f.txt");

                for (int a = 0; a < N * (N - 1) * (N - 2) * (N - 3) / 12; a++) {

                    fileOutput.write(f[a] + " ");

                    fileOutput.write("\n");

                }

                fileOutput.close();

            } catch (IOException e) {
            }

        }

        // HERE: Define matrices EtE and Etf

        SymmetricMatrix EtE = new SymmetricMatrix(N * (N - 1) / 2 - N);
        double[] Etf = new double[N * (N - 1) / 2 - N];

        // HERE: Fill them up...

        // EtE:

        for (int i = 0; i < N * (N - 1) / 2 - N; i++) {

            for (int j = 0; j < i + 1; j++) {

                int p1 = splitIndices[i].getN1();
                int q1 = splitIndices[i].getN2();
                int p2 = splitIndices[j].getN1();
                int q2 = splitIndices[j].getN2();

                int p, pP, q, qP;

                // now, see which pair (pn, qn) is (p, q) and which is (pP, qP)

                if (p1 > p2) {

                    // p1 is pP; q1 is qP

                    p = p2;
                    pP = p1;
                    q = q2;
                    qP = q1;

                } else if (p2 > p1) {

                    // p2 is pP, q2 is qP

                    p = p1;
                    pP = p2;
                    q = q1;
                    qP = q2;

                } else {

                    if (q1 > q2) {

                        // p1 is pP, q1 is qP

                        p = p2;
                        pP = p1;
                        q = q2;
                        qP = q1;

                    } else if (q2 > q1) {

                        // p2 is pP, q2 is qP

                        p = p1;
                        pP = p2;
                        q = q1;
                        qP = q2;

                    } else {

                        // identical - it shouldn't matter then

                        p = p2;
                        pP = p1;
                        q = q2;
                        qP = q1;

                    }

                }

                // calculate

                if (pP >= q) {

                    double sum = (q - p)
                                 * (q - p - 1)
                                 * (qP - pP)
                                 * (qP - pP - 1) / 4;

                    EtE.setElementAt(i, j, sum);

                } else if (qP <= q) {

                    double sum = (Math.min(q, qP) - pP)
                                 * (Math.min(q, qP) - pP - 1)
                                 * (N + p - Math.max(q, qP))
                                 * (N + p - Math.max(q, qP) - 1) / 4;

                    EtE.setElementAt(i, j, sum);

                } else {

                    double sum = (Math.min(q, qP) - pP)
                                 * (Math.min(q, qP) - pP - 1)
                                 * (N + p - Math.max(q, qP))
                                 * (N + p - Math.max(q, qP) - 1) / 4
                                 + (pP - p)
                                   * (pP - p - 1)
                                   * (qP - q)
                                   * (qP - q - 1) / 4;

                    EtE.setElementAt(i, j, sum);

                }

            }

        }

        // Etf:

        double gw[][][][] = new double[N][N][N][N];

        for (int l = 2; l < N - 1; l++) {

            // we loop over lengths
            // for each length, we loop over p

            for (int p = 1; p < N - l + 1; p++) {

                // here, for i < j, (i, j) not in [p + 1 ... p + l]
                // this means... loop, i: i from 1 to p, i from p + l + 1 to N
                // and in that, loop j: j from i + 1 to p, loop j from max (i + 1, p + 1 + l) to N

                for (int i = 1; i < p + 1; i++) {

                    for (int j = i + 1; j < p + 1; j++) {

                        if (l == 2) {

                            int cA = ((Integer) c.get(p)).intValue();
                            int cB = ((Integer) c.get(p + 1)).intValue();
                            int cC = ((Integer) c.get(i - 1)).intValue();
                            int cD = ((Integer) c.get(j - 1)).intValue();

                            double aW = theQuartetWeights.getWeight(cA, cB, cC, cD);

                            gw[p - 1][p + 1][i - 1][j - 1] = aW;

                        } else if (l == 3) {

                            int cA = ((Integer) c.get(p)).intValue();
                            int cB = ((Integer) c.get(p + 2)).intValue();
                            int cC = ((Integer) c.get(i - 1)).intValue();
                            int cD = ((Integer) c.get(j - 1)).intValue();

                            double aW = theQuartetWeights.getWeight(cA, cB, cC, cD);

                            gw[p - 1][p + 2][i - 1][j - 1] = aW
                                                             + gw[p - 1][p + 1][i - 1][j - 1] + gw[p][p + 2][i - 1][j - 1];

                        } else {

                            int cA = ((Integer) c.get(p)).intValue();
                            int cB = ((Integer) c.get(p + l - 1)).intValue();
                            int cC = ((Integer) c.get(i - 1)).intValue();
                            int cD = ((Integer) c.get(j - 1)).intValue();

                            double aW = theQuartetWeights.getWeight(cA, cB, cC, cD);

                            gw[p - 1][p + l - 1][i - 1][j - 1] = aW
                                                                 + gw[p - 1][p + l - 2][i - 1][j - 1] + gw[p][p + l - 1][i - 1][j - 1]
                                                                 - gw[p][p + l - 2][i - 1][j - 1];

                        }

                    }

                    for (int j = p + 1 + l; j < N + 1; j++) {

                        if (l == 2) {

                            int cA = ((Integer) c.get(p)).intValue();
                            int cB = ((Integer) c.get(p + 1)).intValue();
                            int cC = ((Integer) c.get(i - 1)).intValue();
                            int cD = ((Integer) c.get(j - 1)).intValue();

                            double aW = theQuartetWeights.getWeight(cA, cB, cC, cD);

                            gw[p - 1][p + 1][i - 1][j - 1] = aW;

                        } else if (l == 3) {

                            int cA = ((Integer) c.get(p)).intValue();
                            int cB = ((Integer) c.get(p + 2)).intValue();
                            int cC = ((Integer) c.get(i - 1)).intValue();
                            int cD = ((Integer) c.get(j - 1)).intValue();

                            double aW = theQuartetWeights.getWeight(cA, cB, cC, cD);

                            gw[p - 1][p + 2][i - 1][j - 1] = aW
                                                             + gw[p - 1][p + 1][i - 1][j - 1] + gw[p][p + 2][i - 1][j - 1];

                        } else {

                            int cA = ((Integer) c.get(p)).intValue();
                            int cB = ((Integer) c.get(p + l - 1)).intValue();
                            int cC = ((Integer) c.get(i - 1)).intValue();
                            int cD = ((Integer) c.get(j - 1)).intValue();

                            double aW = theQuartetWeights.getWeight(cA, cB, cC, cD);

                            gw[p - 1][p + l - 1][i - 1][j - 1] = aW
                                                                 + gw[p - 1][p + l - 2][i - 1][j - 1] + gw[p][p + l - 1][i - 1][j - 1]
                                                                 - gw[p][p + l - 2][i - 1][j - 1];

                        }

                    }

                }

                for (int i = p + l + 1; i < N + 1; i++) {

                    for (int j = i + 1; j < N + 1; j++) {

                        if (l == 2) {

                            int cA = ((Integer) c.get(p)).intValue();
                            int cB = ((Integer) c.get(p + 1)).intValue();
                            int cC = ((Integer) c.get(i - 1)).intValue();
                            int cD = ((Integer) c.get(j - 1)).intValue();

                            double aW = theQuartetWeights.getWeight(cA, cB, cC, cD);

                            gw[p - 1][p + 1][i - 1][j - 1] = aW;

                        } else if (l == 3) {

                            int cA = ((Integer) c.get(p)).intValue();
                            int cB = ((Integer) c.get(p + 2)).intValue();
                            int cC = ((Integer) c.get(i - 1)).intValue();
                            int cD = ((Integer) c.get(j - 1)).intValue();

                            double aW = theQuartetWeights.getWeight(cA, cB, cC, cD);

                            gw[p - 1][p + 2][i - 1][j - 1] = aW
                                                             + gw[p - 1][p + 1][i - 1][j - 1] + gw[p][p + 2][i - 1][j - 1];

                        } else {

                            int cA = ((Integer) c.get(p)).intValue();
                            int cB = ((Integer) c.get(p + l - 1)).intValue();
                            int cC = ((Integer) c.get(i - 1)).intValue();
                            int cD = ((Integer) c.get(j - 1)).intValue();

                            double aW = theQuartetWeights.getWeight(cA, cB, cC, cD);

                            gw[p - 1][p + l - 1][i - 1][j - 1] = aW
                                                                 + gw[p - 1][p + l - 2][i - 1][j - 1] + gw[p][p + l - 1][i - 1][j - 1]
                                                                 - gw[p][p + l - 2][i - 1][j - 1];

                        }

                    }

                }

            }

        }

        for (int a = 0; a < N * (N - 1) / 2 - N; a++) {

            int p = splitIndices[a].getN1();
            int q = splitIndices[a].getN2();

            double sum = 0.0;

            for (int i = 1; i < p + 1; i++) {

                for (int j = i + 1; j < p + 1; j++) {

                    sum += gw[p - 1][q - 1][i - 1][j - 1];

                }

                for (int j = q + 1; j < N + 1; j++) {

                    sum += gw[p - 1][q - 1][i - 1][j - 1];

                }

            }

            for (int i = q + 1; i < N + 1; i++) {

                for (int j = i + 1; j < N + 1; j++) {

                    sum += gw[p - 1][q - 1][i - 1][j - 1];

                }

            }

            Etf[a] = sum;

        }

        // tolerance level!
        // if set to negative previously, use default

        if (tolerance < -0.0) {

            double epsilon = 2.2204e-016;

            tolerance = 10.0 * Math.pow(((double) N), 8.0) / 48.0 / 64.0 * epsilon;

        }

        int maxIterations = N * N;
        int iterations = 0;

        // once that is done, we wish to solve the NNLS problem Ex -f for x.

        LinkedList P = new LinkedList();
        LinkedList Z = new LinkedList();

        double[] x = new double[N * (N - 1) / 2 - N];
        double[] w = new double[N * (N - 1) / 2 - N];
        double[] z = new double[N * (N - 1) / 2 - N];

        // step 1:

        if (verbose) {

            System.out.println("Step 1");

        }

        // initially, all variables are classed as zero variables

        for (int i = 0; i < N * (N - 1) / 2 - N; i++) {

            Z.add(new Integer(i));

        }

        // initially, all variables are set to zero

        for (int i = 0; i < N * (N - 1) / 2 - N; i++) {

            x[i] = 0.0;
            w[i] = 0.0;
            z[i] = 0.0;

        }

        // finite precision test thing

        boolean calculateW = true;

        // start guess attempt

        if (startGuess) {

            for (int i = 0; i < N * (N - 1) / 2 - N; i++) {

                double jsum = 0;

                for (int j = 0; j < N * (N - 1) / 2 - N; j++) {

                    int p = splitIndices[j].getN1();
                    int q = splitIndices[j].getN2();

                    int a1, a2, b1, b2;

                    if (q == N) {

                        a1 = p + 1;
                        a2 = q;
                        b1 = p;
                        b2 = 1;

                    } else {

                        a1 = p + 1;
                        a2 = q;
                        b1 = p;
                        b2 = q + 1;

                    }

                    x[j] = theQuartetWeights.getWeight(a1, a2, b1, b2);

                    jsum += EtE.getElementAt(i, j) * x[j];

                }

                w[i] = Etf[i] - jsum;

                if (w[i] <= tolerance) {

                    P.add(new Integer(i));
                    Z.remove(new Integer(i));

                } else {

                    x[i] = 0.0;

                }

            }

            calculateW = false;

        }

        // start of outer loop

        // list of choices tested

        LinkedList hypotheses = new LinkedList();

        int it = 0;

        while (true) {

            if (iterations > maxIterations) {

                System.out.println(maxIterations + " iterations have been performed to no avail. Please increase the tolerance.");

                System.exit(0);

            }

            iterations++;

            // step 2:

            if (verbose) {

                System.out.println("Step 2");

            }

            // we are now in loop

            // compute w

            if (calculateW) {

                for (int i = 0; i < N * (N - 1) / 2 - N; i++) {

                    double jsum = 0;

                    for (int j = 0; j < N * (N - 1) / 2 - N; j++) {

                        jsum += EtE.getElementAt(i, j) * x[j];

                    }

                    w[i] = Etf[i] - jsum;

                }

            } else {

                calculateW = true;

            }

            // step 3:

            if (verbose) {

                System.out.println("Step 3");

            }

            // check for stopping conditions
            // if so, break

            if (Z.isEmpty()) {

                break;

            }

            boolean allLequalZero = true;

            ListIterator lI = Z.listIterator();

            while (lI.hasNext()) {

                int i = ((Integer) lI.next()).intValue();

                if (w[i] > tolerance) {

                    allLequalZero = false;
                    break;

                }

            }

            if (allLequalZero) {

                break;

            }

            // step 4, 5:

            if (verbose) {

                System.out.println("Step 4");

            }

            // find index t and move to nonzero set

            int t = - 1;

            while (true) {

                double max = Double.NEGATIVE_INFINITY;
                t = - 1;

                int noPositive = 0;

                lI = Z.listIterator();

                while (lI.hasNext()) {

                    int i = ((Integer) lI.next()).intValue();

                    if (w[i] > max) {

                        t = i;
                        max = w[i];

                    }

                    if (w[i] > 0.0) {

                        noPositive++;

                    }

                }

                if (noPositive == 0) {

                    // if there were no positive w:s this time around
                    // then all must have been set to 0 or been <= 0
                    // already then we can accept even previously tested solutions, for
                    // then we will leave the algorithm anyway as we get out

                    break;

                }

                // we have now found a candidate t
                // before leaving this loop, we must see
                // if it has been used before and if it has, then we zero that w
                // and go back for one more round
                // else, we store our decision

                SolutionHypothesis sH = new SolutionHypothesis(P, t);

                boolean isContained = false;

                ListIterator hI = hypotheses.listIterator();

                while (hI.hasNext()) {

                    SolutionHypothesis oH = (SolutionHypothesis) hI.next();

                    if (oH.equals(sH)) {

                        isContained = true;
                        break;

                    }

                }

                if (isContained) {

                    w[t] = 0.0;

                } else {

                    hypotheses.add(sH);

                    break;

                }

            }

            // stop if there is no good choice

            if (w[t] <= tolerance) {

                // we end anyway

                break;

            } else {

                if (verbose || stepMessages) {

                    SplitIndex sI = splitIndices[t];

                    System.out.print("Adding split " + t + " :");

                    for (int p = sI.getN1() + 1; p < sI.getN2() + 1; p++) {

                        System.out.print(" " + ((Integer) c.get(p - 1)).intValue());

                    }

                    System.out.println();

                }

            }

            if (verbose) {

                System.out.println("Step 5");

            }

            Z.remove(new Integer(t));
            P.add(new Integer(t));

            // start of inner loop

            // see if we enter from 5

            boolean from5 = true;

            while (true) {

                if (verbose) {

                    if (from5) {

                        System.out.print("From outer loop: ");

                    }

                    if (!from5) {

                        System.out.print("From inner loop: ");

                    }

                    System.out.print("P: ");

                    for (int i = 0; i < P.size(); i++) {

                        System.out.print(" x[" + ((Integer) P.get(i)).intValue() + "]: " + x[((Integer) P.get(i)).intValue()]);

                    }

                    System.out.println();

                }

                // step 6:

                if (verbose) {

                    System.out.println("Step 6");

                }

                // LS subproblem!

                // this is the difficult part...

                // generate submatrices, corresponding in size...

                int fullSplits = N * (N - 1) / 2 - N;
                int noSplits = P.size();

                double[][] EtEp = new double[noSplits][noSplits];
                double[] Etfp = new double[noSplits];

                int row = 0;
                int column = 0;

                // fill

                for (int i = 0; i < fullSplits; i++) {

                    if (P.contains(new Integer(i))) {

                        column = 0;

                        for (int j = 0; j < fullSplits; j++) {

                            if (P.contains(new Integer(j))) {

                                EtEp[row][column] = EtE.getElementAt(i, j);

                                column++;

                            }

                        }

                        Etfp[row] = Etf[i];

                        row++;

                    }

                }

                // done
                // now solve their least squares problem EtEp x = Etfp

                if (extract) {

                    try {

                        FileWriter fileOutput = new FileWriter("Ep" + it + ".txt");

                        for (int a = 0; a < noSplits; a++) {

                            for (int b = 0; b < noSplits; b++) {

                                fileOutput.write(EtEp[a][b] + " ");

                            }

                            fileOutput.write("\n");

                        }

                        fileOutput.close();

                    } catch (IOException e) {
                    }

                }

                // should be better initialized

                // BUT! we only have size (P) splits

                int[] aMap = new int[P.size()];

                int mapIndex = 0;

                for (int i = 0; i < fullSplits; i++) {

                    if (P.contains(new Integer(i))) {

                        aMap[mapIndex] = i;

                        mapIndex++;

                    }

                }

                // so aMap is the map reduced z to true z

                double Q[][] = new double[noSplits][noSplits];

                for (int i = 0; i < noSplits; i++) {

                    // for each column

                    // the column is v

                    double[] v = new double[noSplits];

                    // take the list of dot products

                    double[] L = new double[i];

                    for (int j = 0; j < i; j++) {

                        double sum = 0.0;

                        for (int k = 0; k < noSplits; k++) {

                            sum += EtEp[k][i] * Q[k][j];

                        }

                        L[j] = sum;

                    }

                    // next...

                    for (int k = 0; k < noSplits; k++) {

                        double jSum = 0.0;

                        for (int j = 0; j < i; j++) {

                            jSum += L[j] * Q[k][j];

                        }

                        // for all elements in the column

                        v[k] = EtEp[k][i] - jSum;

                    }

                    // then we must normalize v before adding it

                    double length = 0.0;

                    for (int k = 0; k < noSplits; k++) {

                        length += v[k] * v[k];

                    }

                    if (length != 0.0) {

                        for (int k = 0; k < noSplits; k++) {

                            v[k] = v[k] / Math.sqrt(length);

                        }

                    }

                    // then store v

                    for (int k = 0; k < noSplits; k++) {

                        Q[k][i] = v[k];

                    }

                }

                // then calculate R

                UpperTriangularMatrix R = new UpperTriangularMatrix(noSplits);

                for (int i = 0; i < noSplits; i++) {

                    for (int j = 0; j < i + 1; j++) {

                        double sum = 0.0;

                        for (int k = 0; k < noSplits; k++) {

                            sum += EtEp[k][i] * Q[k][j];

                        }

                        R.setElementAt(j, i, sum);

                    }

                }

                // we now have the Q and R matrices

                if (false) {

                    NumberFormat nF = NumberFormat.getInstance();
                    nF.setMaximumFractionDigits(3);
                    nF.setMinimumFractionDigits(3);

                    System.out.println("Q:");

                    for (int i = 0; i < noSplits; i++) {

                        for (int j = 0; j < noSplits; j++) {

                            System.out.print(" " + nF.format(Q[i][j]));

                        }

                        System.out.println();

                    }

                    System.out.println("R:");

                    for (int i = 0; i < noSplits; i++) {

                        for (int j = 0; j < noSplits; j++) {

                            System.out.print(" " + nF.format(R.getElementAt(i, j)));

                        }

                        System.out.println();

                    }

                }

                if (extract) {

                    try {

                        FileWriter fileOutput = new FileWriter("Q" + it + ".txt");

                        for (int i = 0; i < noSplits; i++) {

                            for (int j = 0; j < noSplits; j++) {

                                fileOutput.write(Q[i][j] + " ");

                            }

                            fileOutput.write("\n");

                        }

                        fileOutput.close();

                        fileOutput = new FileWriter("R" + it + ".txt");

                        for (int i = 0; i < noSplits; i++) {

                            for (int j = 0; j < noSplits; j++) {

                                fileOutput.write(R.getElementAt(i, j) + " ");

                            }

                            fileOutput.write("\n");

                        }

                        fileOutput.close();

                    } catch (IOException e) {
                    }

                }

                // least squares solution of z:

                double[] QtEtfp = new double[noSplits];

                for (int i = 0; i < noSplits; i++) {

                    double jSum = 0.0;

                    for (int j = 0; j < noSplits; j++) {

                        jSum += Q[j][i] * Etfp[j];

                    }

                    QtEtfp[i] = jSum;

                }

                // reduced row echelon whatever solver... maybe?

                // first for the non-personas...

                for (int i = 0; i < fullSplits; i++) {

                    z[i] = 0.0;

                }

                // ... second those that exist; wonder if I can do this?

                double[] zRed = new double[noSplits];

                for (int i = 0; i < noSplits; i++) {

                    int d = noSplits - 1;

                    double jSum = 0.0;

                    for (int j = 0; j < i; j++) {

                        jSum += R.getElementAt(d - i, d - j) * z[aMap[d - j]];

                    }

                    z[aMap[d - i]] = (QtEtfp[d - i] - jSum) / R.getElementAt(d - i, d - i);
                    zRed[d - i] = z[aMap[d - i]];

                }

                if (extract) {

                    try {

                        FileWriter fileOutput = new FileWriter("z" + it + ".txt");

                        for (int b = 0; b < noSplits; b++) {

                            fileOutput.write(zRed[b] + "\n");

                        }

                        fileOutput.close();

                    } catch (IOException e) {
                    }

                    it++;

                }

                // finite-precision test

                if (from5) {

                    if (cycleWarnings) {

                        System.out.println("Testing z [" + t + "] = " + z[t] + " for nonnegativity by tolerance " + tolerance);

                    }

                    if (z[t] <= tolerance) {

                        if (verbose || stepMessages) {

                            SplitIndex sI = splitIndices[t];

                            System.out.print("Removing directly split " + t + " :");

                            for (int p = sI.getN1() + 1; p < sI.getN2() + 1; p++) {

                                System.out.print(" " + ((Integer) c.get(p - 1)).intValue());

                            }

                            System.out.println();

                        }

                        w[t] = 0;
                        calculateW = false;
                        P.remove(new Integer(t));
                        Z.add(new Integer(t));
                        break;

                    }

                }

                if (verbose) {

                    System.out.println("Step 7");

                }

                // step 7:

                // check for stopping conditions
                // if so, break

                boolean allAboveZero = true;

                lI = P.listIterator();

                while (lI.hasNext()) {

                    int i = ((Integer) lI.next()).intValue();

                    if (z[i] <= tolerance) {

                        allAboveZero = false;
                        break;

                    }

                }

                if (allAboveZero) {

                    for (int i = 0; i < N * (N - 1) / 2 - N; i++) {

                        x[i] = z[i];

                    }

                    break;

                }

                // step 8:

                if (verbose) {

                    System.out.println("Step 8");

                }

                lI = P.listIterator();

                double min = Double.POSITIVE_INFINITY;
                int q = - 1;

                while (lI.hasNext()) {

                    int i = ((Integer) lI.next()).intValue();

                    if (z[i] <= tolerance) {

                        if (x[i] / (x[i] - z[i]) < min) {

                            q = i;
                            min = x[i] / (x[i] - z[i]);

                        }

                    }

                }

                // step 9:

                if (verbose) {

                    System.out.println("Step 9");

                }

                double alpha = x[q] / (x[q] - z[q]);

                // step 10:

                if (verbose) {

                    System.out.println("Step 10");

                    System.out.println("q " + q + " x " + x[q] + " z " + z[q] + " alpha " + alpha);

                }

                for (int i = 0; i < N * (N - 1) / 2 - N; i++) {

                    x[i] = x[i] + alpha * (z[i] - x[i]);

                }

                // step 11:

                if (verbose) {

                    System.out.println("Step 11");

                }

                lI = P.listIterator();

                while (lI.hasNext()) {

                    int i = ((Integer) lI.next()).intValue();

                    if (x[i] <= tolerance) {

                        if (verbose || stepMessages) {

                            SplitIndex sI = splitIndices[i];

                            System.out.print("Removing split " + i + " :");

                            for (int p = sI.getN1() + 1; p < sI.getN2() + 1; p++) {

                                System.out.print(" " + ((Integer) c.get(p - 1)).intValue());

                            }

                            System.out.println();

                        }

                        lI.remove();
                        Z.add(new Integer(i));

                    }

                }

                // we will now go back to 6 again, and from here

                from5 = false;

                System.gc();

            }

        }

        if (verbose) {

            System.out.println("Step 12");

        }

        // we do, then our split weights are done

        // and we print them to file

        if (extract) {

            try {

                FileWriter fileOutput = new FileWriter("x.txt");

                for (int a = 0; a < N * (N - 1) / 2 - N; a++) {

                    fileOutput.write(x[a] + " ");

                    fileOutput.write("\n");

                }

                fileOutput.close();

            } catch (IOException e) {
            }

        }

        int noSplits = N * (N - 1) / 2 - N;
        boolean[] splitExists = new boolean[noSplits];

        int existingSplits = 0;

        // stuff to print _all_ splits

        for (int i = 0; i < noSplits; i++) {

            if (x[i] > 0.0) {

                splitExists[i] = true;

                existingSplits++;

            } else {

                splitExists[i] = false;

            }

        }

        // print

        StringBuilder nexusString = new StringBuilder();

        nexusString.append("#NEXUS\nBEGIN taxa;\nDIMENSIONS ntax=").append(N).append(";\nTAXLABELS\n");

        for (int i = 0; i < N; i++) {

            nexusString.append((String) taxonNames.get(i)).append("\n");

        }

        nexusString.append(";\nEND;\n\nBEGIN st_splits;\nDIMENSIONS ntax=").append(N).append(" nsplits=").append(existingSplits + N).append(";\n");
        nexusString.append("FORMAT\nlabels\nweights\n;\nPROPERTIES\nweakly compatible\ncyclic\n;\nCYCLE");

        for (int i = 0; i < N; i++) {

            nexusString.append(" ").append(((Integer) (c.get(i))).intValue());

        }

        nexusString.append(";\nMATRIX\n");

        int s = 0;

        int wn = 0;
        double ws = 0.0;

        for (int i = 0; i < N * (N - 1) / 2 - N; i++) {

            if (splitExists[i]) {

                // this split exists

                s++;

                nexusString.append("").append(s).append("   ").append(x[i]).append("  ");

                wn++;
                ws += x[i];

                SplitIndex sI = splitIndices[i];

                for (int p = sI.getN1() + 1; p < sI.getN2() + 1; p++) {

                    nexusString.append(" ").append(((Integer) c.get(p - 1)).intValue());

                }

                nexusString.append(",\n");

            }

        }

        double mw = ws / ((double) wn);

        for (int i = 0; i < N; i++) {

            s++;

            nexusString.append("").append(s).append("   ").append(mw).append("  ");

            nexusString.append(" ").append(i + 1);

            nexusString.append(",\n");

        }

        nexusString.append(";\nEND;");

        try {

            FileWriter fileOutput = new FileWriter(outputName);

            fileOutput.write(nexusString.toString());

            fileOutput.close();

        } catch (IOException e) {
        }

    }
}