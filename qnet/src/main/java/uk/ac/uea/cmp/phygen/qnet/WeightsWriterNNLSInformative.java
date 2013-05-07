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
import uk.ac.uea.cmp.phygen.core.math.matrix.BitMatrix;
import uk.ac.uea.cmp.phygen.core.math.matrix.SymmetricMatrix;
import uk.ac.uea.cmp.phygen.core.math.matrix.UpperTriangularMatrix;
import uk.ac.uea.cmp.phygen.qnet.holders.PHolder;

import java.io.*;
import java.text.NumberFormat;
import java.util.*;

class WeightsWriterNNLSInformative {

    /**
     *
     * Write weights for the presented tree.
     *
     * Calculates split weights and prints them to a nexus file.
     *
     */
    public static void writeWeights(QNet parent, String infoName, ArrayList cN, String outputName, double tolerance) {

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

        Pair<Integer, Integer>[] splitIndices = new ImmutablePair[N * (N - 1) / 2 - N];

        int n = 0;

        for (int i = 1; i < N - 1; i++) {

            for (int j = i + 2; j < N + 1; j++) {

                if (i != 1 || j != N) {

                    // valid split

//                    System.out.println (i + " " + j + " ");

                    splitIndices[n] = new ImmutablePair<Integer, Integer>(i, j);

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

                    int p = splitIndices[b].getLeft();
                    int q = splitIndices[b].getRight();
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

        // so... we need structures, probably quartet structures
        // as we deal with several quantities of data...
        // we do... something, yes. we calculate these for
        // disjoint quartets by the given formula
        // and the loaded existence information quartet structure...
        // basically, we need a data structure with a bool and
        // four ints for each quadruple set... we first load
        // this from the information file, then we fill up
        // the same structure from the provided loops
        // and then access below to get the informative
        // interval combinatorics... it is basically
        // defined like outer and inner interval of two types
        // and existence value for each quadruple...

        PHolder pHolder = new PHolder();
        pHolder.ensureCapacity(N);
        pHolder.initialize();

        // load r values

        load(pHolder, infoName);

        // fill upp pHolder by provided loop

//        System.out.println ("First loop");

        for (int iA = 1; iA < N - 2; iA++) {

            for (int iB = iA + 1; iB < N - 1; iB++) {

                for (int iC = iB + 1; iC < N; iC++) {

                    for (int iD = iC + 1; iD <= N; iD++) {

                        // these are NOT all 1 <= iA < iB < iC < iD <= N

                        if (iB == iA + 1) {

                            if (pHolder.getR(iA + 1, iB + 1, iC + 1, iD + 1, c)) {

                                pHolder.setQ(iA, iB, iC, iD, 1);

                            } else {

                                pHolder.setQ(iA, iB, iC, iD, 0);

                            }

                        }

                        if (iC == iB + 1) {

                            if (pHolder.getR(iA + 1, iB + 1, iC + 1, iD + 1, c)) {

                                pHolder.setQ(iB, iC, iD, iA + N, 1);

                            } else {

                                pHolder.setQ(iB, iC, iD, iA + N, 0);

                            }

                        }

                    }

                }

            }

        }

//        System.out.println ("Second loop");

        for (int iA = 1; iA < N - 1; iA++) {

            for (int iB = iA + 1; iB < N; iB++) {

                for (int iC = iB + 1; iC <= N; iC++) {

                    if (iB > iA + 2) {

                        if (pHolder.getR(iA + 1, iA + 3, iB + 1, iC + 1, c)) {

                            pHolder.setQ(iA, iA + 2, iB, iC, pHolder.getQ(iA + 1, iA + 2, iB, iC)
                                                             + pHolder.getQ(iA, iA + 1, iB, iC) + 1);

                        } else {

                            pHolder.setQ(iA, iA + 2, iB, iC, pHolder.getQ(iA + 1, iA + 2, iB, iC)
                                                             + pHolder.getQ(iA, iA + 1, iB, iC));

                        }

                    }

                    if (iC > iB + 2) {

                        if (pHolder.getR(iA + 1, iB + 1, iB + 3, iC + 1, c)) {

                            pHolder.setQ(iB, iB + 2, iC, iA + N, pHolder.getQ(iB + 1, iB + 2, iC, iA + N)
                                                                 + pHolder.getQ(iB, iB + 1, iC, iA + N) + 1);

                        } else {

                            pHolder.setQ(iB, iB + 2, iC, iA + N, pHolder.getQ(iB + 1, iB + 2, iC, iA + N)
                                                                 + pHolder.getQ(iB, iB + 1, iC, iA + N));

                        }

                    }

                }

            }

        }

        for (int iD = 3; iD < N - 2; iD++) {

            for (int iA = 1; iA < N - 1; iA++) {

                for (int iB = iA + 1; iB < N; iB++) {

                    for (int iC = iB + 1; iC <= N; iC++) {

                        if (iB > iA + iD) {

                            if (pHolder.getR(iA + 1, iA + iD + 1, iB + 1, iC + 1, c)) {

                                pHolder.setQ(iA, iA + iD, iB, iC, pHolder.getQ(iA + 1, iA + iD, iB, iC)
                                                                  + pHolder.getQ(iA, iA + iD - 1, iB, iC)
                                                                  - pHolder.getQ(iA + 1, iA + iD - 1, iB, iC) + 1);

                            } else {

                                pHolder.setQ(iA, iA + iD, iB, iC, pHolder.getQ(iA + 1, iA + iD, iB, iC)
                                                                  + pHolder.getQ(iA, iA + iD - 1, iB, iC)
                                                                  - pHolder.getQ(iA + 1, iA + iD - 1, iB, iC));

                            }

                        }

                        if (iC > iB + iD) {

                            if (pHolder.getR(iA + 1, iB + 1, iB + iD + 1, iC + 1, c)) {

                                pHolder.setQ(iB, iB + iD, iC, iA + N, pHolder.getQ(iB + 1, iB + iD, iC, iA + N)
                                                                      + pHolder.getQ(iB, iB + iD - 1, iC, iA + N)
                                                                      - pHolder.getQ(iB + 1, iB + iD - 1, iC, iA + N) + 1);

                            } else {

                                pHolder.setQ(iB, iB + iD, iC, iA + N, pHolder.getQ(iB + 1, iB + iD, iC, iA + N)
                                                                      + pHolder.getQ(iB, iB + iD - 1, iC, iA + N)
                                                                      - pHolder.getQ(iB + 1, iB + iD - 1, iC, iA + N));

                            }

                        }

                    }

                }

            }

        }

        for (int iB = 2; iB < N; iB++) {

            for (int iC = iB + 1; iC < N; iC++) {

                pHolder.setP(1, iB, iC, iC + 1, pHolder.getQ(1, iB, iC, iC + 1));

            }

        }

        for (int iB = 2; iB < N - 2; iB++) {

            for (int iC = iB + 1; iC < N - 1; iC++) {

                pHolder.setP(1, iB, iC, iC + 2, pHolder.getP(1, iB, iC + 1, iC + 2)
                                                + pHolder.getP(1, iB, iC, iC + 1)
                                                + pHolder.getQ(1, iB, iC, iC + 2));

            }

        }

        for (int iD = 3; iD < N - 2; iD++) {

            for (int iB = 2; iB < N - iD; iB++) {

                for (int iC = iB + 1; iC < N - iD + 1; iC++) {

                    pHolder.setP(1, iB, iC, iC + iD, pHolder.getP(1, iB, iC + 1, iC + iD)
                                                     + pHolder.getP(1, iB, iC, iC + iD - 1)
                                                     - pHolder.getP(1, iB, iC + 1, iC + iD - 1)
                                                     + pHolder.getQ(1, iB, iC, iC + iD));

                }

            }

        }

        for (int iA = 2; iA < N - 1; iA++) {

            for (int iB = iA + 1; iB < N; iB++) {

                for (int iC = iB + 1; iC <= N; iC++) {

                    if (iA + N > iC + 1) {

                        pHolder.setP(iA, iB, iC, iC + 1, pHolder.getQ(iA, iB, iC, iC + 1));

                    }

                }

            }

        }

        for (int iA = 2; iA < N - 1; iA++) {

            for (int iB = iA + 1; iB < N; iB++) {

                for (int iC = iB + 1; iC <= N; iC++) {

                    if (iA + N > iC + 2) {

                        if (iC != N) {

                            pHolder.setP(iA, iB, iC, iC + 2, pHolder.getP(iA, iB, iC + 1, iC + 2)
                                                             + pHolder.getP(iA, iB, iC, iC + 1)
                                                             + pHolder.getQ(iA, iB, iC, iC + 2));

                        } else {

                            pHolder.setP(iA, iB, iC, iC + 2, pHolder.getP(1, 2, iA, iB)
                                                             + pHolder.getP(iA, iB, iC, iC + 1)
                                                             + pHolder.getQ(iA, iB, iC, iC + 2));

                        }

                    }

                }

            }

        }

        for (int iD = 3; iD < N - 2; iD++) {

            for (int iA = 2; iA < N - 1; iA++) {

                for (int iB = iA + 1; iB < N; iB++) {

                    for (int iC = iB + 1; iC <= N; iC++) {

                        if (N + iA > iC + iD) {

                            if (iC != N) {

                                pHolder.setP(iA, iB, iC, iC + iD, pHolder.getP(iA, iB, iC + 1, iC + iD)
                                                                  + pHolder.getP(iA, iB, iC, iC + iD - 1)
                                                                  - pHolder.getP(iA, iB, iC + 1, iC + iD - 1)
                                                                  + pHolder.getQ(iA, iB, iC, iC + iD));

                            } else {

                                pHolder.setP(iA, iB, iC, iC + iD, pHolder.getP(1, iD, iA, iB)
                                                                  + pHolder.getP(iA, iB, iC, iC + iD - 1)
                                                                  - pHolder.getP(1, iD - 1, iA, iB)
                                                                  + pHolder.getQ(iA, iB, iC, iC + iD));

                            }

                        }

                    }

                }

            }

        }

        // old version

        /*
         *
         * for (int iA = 1; iA < N - 1; iA++) {
         *
         * for (int iB = iA + 1; iB < N; iB++) {
         *
         * for (int iC = iB + 1; iC <= N; iC++) {
         *
         * if (iA + N > iC + 1) {
         *
         * pHolder.setP (iA, iB, iC, iC + 1, pHolder.getQ (iA, iB, iC, iC + 1));
         *
         * }
         *
         * }
         *
         * }
         *
         * }
         *
         * for (int iA = 1; iA < N - 1; iA++) {
         *
         * for (int iB = iA + 1; iB < N; iB++) {
         *
         * for (int iC = iB + 1; iC <= N; iC++) {
         *
         * if (iA + N > iC + 2) {
         *
         * if (iC != N) {
         *
         * pHolder.setP (iA, iB, iC, iC + 2, pHolder.getP (iA, iB, iC + 1, iC +
         * 2) + pHolder.getP (iA, iB, iC, iC + 1) + pHolder.getQ (iA, iB, iC, iC
         * + 2));
         *
         * }
         *
         * else {
         *
         * pHolder.setP (iA, iB, iC, iC + 2, pHolder.getP (1, 2, iA, iB) +
         * pHolder.getP (iA, iB, iC, iC + 1) + pHolder.getQ (iA, iB, iC, iC +
         * 2));
         *
         *
         * }
         *
         * }
         *
         * }
         *
         * }
         *
         * }
         *
         * for (int iD = 3; iD < N - 2; iD++) {
         *
         * for (int iA = 1; iA < N - 1; iA++) {
         *
         * for (int iB = iA + 1; iB < N; iB++) {
         *
         * for (int iC = iB + 1; iC <= N; iC++) {
         *
         * if (N + iA > iC + iD) {
         *
         * if (iC != N) {
         *
         * pHolder.setP (iA, iB, iC, iC + iD, pHolder.getP (iA, iB, iC + 1, iC +
         * iD) + pHolder.getP (iA, iB, iC, iC + iD - 1) - pHolder.getP (iA, iB,
         * iC + 1, iC + iD - 1) + pHolder.getQ (iA, iB, iC, iC + iD));
         *
         * }
         *
         * else {
         *
         * pHolder.setP (iA, iB, iC, iC + iD, pHolder.getP (1, iD, iA, iB) +
         * pHolder.getP (iA, iB, iC, iC + iD - 1) - pHolder.getP (1, iD - 1, iA,
         * iB) + pHolder.getQ (iA, iB, iC, iC + iD));
         *
         * }
         *
         * }
         *
         * }
         *
         * }
         *
         * }
         *
         * }
         */
        // done preparing

        // print out p and q
/*
         * for (int i = 1; i < N - 2; i++) {
         *
         * for (int j = i + 1; j < N - 1; j++) {
         *
         * for (int k = j + 1; k < N; k++) {
         *
         * for (int l = k + 1; l < N + 1; l++) {
         *
         * System.out.println ("R (" + i + ", " + j + ", " + k + ", " + l + "):
         * " + pHolder.getR (i, j, k, l) + ", Q (" + i + ", " + j + ", " + k +
         * ", " + l + "): " + pHolder.getQ (i, j, k, l) + ", Q (" + j + ", " + k
         * + ", " + l + ", " + (N + i) + "): " + pHolder.getQ (j, k, l, (N + i))
         * + ", P (" + i + ", " + j + ", " + k + ", " + l + "): " + pHolder.getP
         * (i, j, k, l) + ", P (" + j + ", " + k + ", " + l + ", " + (N + i) +
         * "): " + pHolder.getP (j, k, l, (N + i)));
         *
         * }
         *
         * }
         *
         * }
         *
         * }
         */
        for (int i = 0; i < N * (N - 1) / 2 - N; i++) {

            for (int j = 0; j < i + 1; j++) {

                int p1 = splitIndices[i].getLeft();
                int q1 = splitIndices[i].getRight();
                int p2 = splitIndices[j].getLeft();
                int q2 = splitIndices[j].getRight();

                /*
                 * // what happens here if the end-problematic // cases are
                 * simply translated? // that is... everything beginning with a
                 * 1 // must cease to begin with a 1 // and become its upper
                 * equivalent
                 *
                 * if (p1 == 1) {
                 *
                 * p1 = q1 + 1; q1 = N;
                 *
                 * }
                 *
                 * if (p2 == 1) {
                 *
                 * p2 = q2 + 1; q2 = N;
                 *
                 * }
                 */

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

                int value = 0;

                if (qP <= q) {

                    value = pHolder.getP(pP, qP - 1, q, N + p - 1);

                } else if (pP < q && q < qP) {

                    value = pHolder.getP(pP, q - 1, qP, N + p - 1) + pHolder.getP(p, pP - 1, q, qP - 1);

                } else if (q <= pP) {

                    value = pHolder.getP(p, q - 1, pP, qP - 1);

                }

                //              System.out.println ("EtE: split [" + p + " " + q + ")|rest vs split [" + pP + " " + qP + ")|rest has value " + value);

                EtE.setElementAt(i, j, value);

            }

        }

//**************************************************************************************************************
//                  Begin added stuff
//**************************************************************************************************************


        //extract the Gram matrix of the reduced topological matrix

        try {
            FileWriter fw = new FileWriter("matrix.dat");

            for (int i = 0; i < N * (N - 1) / 2 - N; i++) {
                for (int j = 0; j < N * (N - 1) / 2 - N; j++) {
                    fw.write(EtE.getElementAt(i, j) + " ");
                }
                fw.write("\n");
            }
            fw.close();
        } catch (IOException exception) {
            System.out.println("Error while writing matrix.dat.");
        }

        // writing initial.dat
        try {
            FileWriter out = new FileWriter("initial.dat");
            for (int i = 0; i < N * (N - 1) / 2 - N; i++) {
                out.write("0\n");
            }
            out.close();
        } catch (IOException exception) {
            System.out.println("Error while writing initial.dat.");
        }

        double[] cleaned = new double[N * (N - 1) / 2 - N];
        // end
        try {
            LineNumberReader ln_reader = new LineNumberReader(new FileReader("initial.dat"));

            for (int i = 0; i < N * (N - 1) / 2 - N; i++) {
                cleaned[i] = Integer.parseInt(ln_reader.readLine());
            }
            ln_reader.close();
        } catch (IOException exception) {
            System.out.println("Error while reading in initial.dat");
        }


//*************************************************************************************************************
//                 End added stuff
//*************************************************************************************************************


//        System.out.println ();

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

            int p = splitIndices[a].getLeft();
            int q = splitIndices[a].getRight();

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

//            System.out.println ("Etf: split (" + p + " " + q + "] | rest has value " + sum);

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

                    int p = splitIndices[j].getLeft();
                    int q = splitIndices[j].getRight();

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

                    Pair<Integer, Integer> sI = splitIndices[t];

                    System.out.print("Adding split " + t + " :");

                    for (int p = sI.getLeft() + 1; p < sI.getRight() + 1; p++) {

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

                // check consistency!

                if (R.getElementAt(noSplits - 1, noSplits - 1) == 0) {

                    System.out.println("Warning: Subproblem is underdetermined, results may not be unique!");

                }

                // done

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

                            Pair<Integer, Integer> sI = splitIndices[t];

                            System.out.print("Removing directly split " + t + " :");

                            for (int p = sI.getLeft() + 1; p < sI.getRight() + 1; p++) {

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

                            Pair<Integer, Integer> sI = splitIndices[i];

                            System.out.print("Removing split " + i + " :");

                            for (int p = sI.getLeft() + 1; p < sI.getRight() + 1; p++) {

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


//***********************************************************************************************************
//            Begin added stuff
//***********************************************************************************************************


        try {

            FileWriter fileOutput = new FileWriter("vector.dat");

            for (int a = 0; a < N * (N - 1) / 2 - N; a++) {

                fileOutput.write(x[a] + " ");

                fileOutput.write("\n");

            }

            fileOutput.close();

        } catch (IOException e) {
            System.out.println("Error while writing vector.dat.");
        }




        int noSplits = N * (N - 1) / 2 - N;
        boolean[] splitExists = new boolean[noSplits];

        int existingSplits = 0;
        int existingSplitsnew = 0;

        // stuff to print _all_ splits

        for (int i = 0; i < noSplits; i++) {

            if (x[i] > 0.0) {

                splitExists[i] = true;

                existingSplits++;

            } else {

                splitExists[i] = false;

            }

            if (cleaned[i] > 0.0) {

                existingSplitsnew++;

            }
        }

        // print

        StringBuilder nexusString = new StringBuilder();
        StringBuilder nexusStringnew = new StringBuilder();

        nexusString.append("#NEXUS\nBEGIN taxa;\nDIMENSIONS ntax=").append(N).append(";\nTAXLABELS\n");
        nexusStringnew.append("#NEXUS\nBEGIN taxa;\nDIMENSIONS ntax=").append(N).append(";\nTAXLABELS\n");

        for (int i = 0; i < N; i++) {

            nexusString.append((String) taxonNames.get(i)).append("\n");
            nexusStringnew.append((String) taxonNames.get(i)).append("\n");

        }

        nexusString.append(";\nEND;\n\nBEGIN st_splits;\nDIMENSIONS ntax=").append(N).append(" nsplits=").append(existingSplits + N).append(";\n");
        nexusStringnew.append(";\nEND;\n\nBEGIN st_splits;\nDIMENSIONS ntax=").append(N).append(" nsplits=").append(existingSplitsnew + N).append(";\n");
        nexusString.append("FORMAT\nlabels\nweights\n;\nPROPERTIES\nweakly compatible\ncyclic\n;\nCYCLE");
        nexusStringnew.append("FORMAT\nlabels\nweights\n;\nPROPERTIES\nweakly compatible\ncyclic\n;\nCYCLE");

        for (int i = 0; i < N; i++) {

            nexusString.append(" ").append(((Integer) (c.get(i))).intValue());
            nexusStringnew.append(" ").append(((Integer) (c.get(i))).intValue());
        }

        nexusString.append(";\nMATRIX\n");
        nexusStringnew.append(";\nMATRIX\n");

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

                Pair<Integer, Integer> sI = splitIndices[i];

                for (int p = sI.getLeft() + 1; p < sI.getRight() + 1; p++) {

                    nexusString.append(" ").append(((Integer) c.get(p - 1)).intValue());

                }

                nexusString.append(",\n");

            }

        }

        double mw = 1.0;
        if (wn > 0) {
            mw = ws / ((double) wn);
        }

        for (int i = 0; i < N; i++) {

            s++;

            nexusString.append("").append(s).append("   ").append(mw).append("  ");

            nexusString.append(" ").append(i + 1);

            nexusString.append(",\n");

        }

        s = 0;

        wn = 0;
        ws = 0.0;

        for (int i = 0; i < N * (N - 1) / 2 - N; i++) {

            if (cleaned[i] > 0.0) {

                // this split exists

                s++;

                nexusStringnew.append("").append(s).append("   ").append(cleaned[i]).append("  ");

                wn++;
                ws += cleaned[i];

                Pair<Integer, Integer> sI = splitIndices[i];

                for (int p = sI.getLeft() + 1; p < sI.getRight() + 1; p++) {

                    nexusStringnew.append(" ").append(((Integer) c.get(p - 1)).intValue());

                }

                nexusStringnew.append(",\n");

            }

        }

        mw = 1.0;
        if (wn > 0) {
            mw = ws / ((double) wn);
        }

        for (int i = 0; i < N; i++) {

            s++;

            nexusStringnew.append("").append(s).append("   ").append(mw).append("  ");

            nexusStringnew.append(" ").append(i + 1);

            nexusStringnew.append(",\n");

        }

        nexusString.append(";\nEND;");
        nexusStringnew.append(";\nEND;");

        try {

            FileWriter fileOutput = new FileWriter(outputName);
            FileWriter fileOutputnew = new FileWriter(outputName + "_new");

            fileOutput.write(nexusString.toString());
            fileOutputnew.write(nexusStringnew.toString());

            fileOutput.close();
            fileOutputnew.close();
        } catch (IOException e) {
        }

    }

//***********************************************************************************************************
//           End added stuff
//***********************************************************************************************************
    public static void load(PHolder pHolder, String fileName) {

        int N = 0;

        try {

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
                    int w1 = (new Integer(lineTokenizer.nextToken())).intValue();

                    int w2 = (new Integer(lineTokenizer.nextToken())).intValue();

                    int w3 = (new Integer(lineTokenizer.nextToken())).intValue();

                    /**
                     *
                     * Set it, just as it is written
                     *
                     */
                    if (w1 == 1) {

                        pHolder.setR(a, b, c, d, true);

                    } else {

                        pHolder.setR(a, b, c, d, false);

                    }

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

                        senseKnown = true;

                    } else if (theSecond.equalsIgnoreCase("min")) {

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

                }

            }

        } catch (IOException e) {

            System.out.println("QNet: Cannot read from info file.");

            System.exit(1);

        } catch (NoSuchElementException e) {

            System.out.println("QNet: Error in info file format.");

            System.exit(1);

        }

    }
}
