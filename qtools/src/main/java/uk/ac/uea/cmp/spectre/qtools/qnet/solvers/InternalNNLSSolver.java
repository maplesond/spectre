/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2014  UEA School of Computing Sciences
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

package uk.ac.uea.cmp.spectre.qtools.qnet.solvers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitUtils;
import uk.ac.uea.cmp.spectre.core.math.matrix.SymmetricMatrix;
import uk.ac.uea.cmp.spectre.core.math.matrix.UpperTriangularMatrix;
import uk.ac.uea.cmp.spectre.qtools.qnet.QNetException;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by dan on 08/01/14.
 */
public class InternalNNLSSolver {

    private static Logger log = LoggerFactory.getLogger(InternalNNLSSolver.class);

    /**
     * We wish to solve the NNLS problem Ex -f for x
     *
     * @param N Number of Taxa
     */
    public double[] optimise(final int N, double[] Etf, SymmetricMatrix EtE, final double tolerance) throws QNetException {

        final int MAX_ITERATIONS = N * N;

        int iterations = 0;

        final int maxSplits = SplitUtils.calcMaxSplits(N);


        // step 1: initially, all variables are classed as zero variables
        PList P = new PList(tolerance);
        ZList Z = new ZList(maxSplits); // 0-based list
        double[] x = new double[maxSplits];
        double[] w = new double[maxSplits];


        // finite precision test thing

        boolean calculateW = true;

        // start of outer loop

        // list of choices tested

        List<SolutionHypothesis> hypotheses = new LinkedList<>();

        while (true) {

            if (iterations > MAX_ITERATIONS) {

                throw new QNetException(MAX_ITERATIONS + " iterations have been performed to no avail. Please increase the tolerance or use a different solver.");
            }

            // Start next iteration
            iterations++;

            // step 2: we are now in loop... compute w
            if (calculateW) {

                for (int i = 0; i < maxSplits; i++) {

                    double jsum = 0;

                    for (int j = 0; j < maxSplits; j++) {

                        jsum += EtE.getElementAt(i, j) * x[j];
                    }

                    w[i] = Etf[i] - jsum;
                }

            } else {

                calculateW = true;
            }

            // step 3: check for stopping conditions if so, break
            if (Z.isEmpty() || Z.allLEQTolerance(w, tolerance)) {
                break;
            }


            int t = -1;
            // step 4, 5: find index t and move to nonzero set
            // if there were no positive w:s this time around
            // then all must have been set to 0 or been <= 0
            // already then we can accept even previously tested solutions, for
            // then we will leave the algorithm anyway as we get out
            while (Z.getNbPositive(w) > 0) {

                t = Z.indexOfMax(w);

                // we have now found a candidate t
                // before leaving this loop, we must see
                // if it has been used before and if it has, then we zero that w
                // and go back for one more round
                // else, we store our decision

                SolutionHypothesis sH = new SolutionHypothesis(P, t);

                boolean isContained = false;

                ListIterator<SolutionHypothesis> hI = hypotheses.listIterator();

                while (hI.hasNext()) {

                    SolutionHypothesis oH = hI.next();

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
            }

            Z.remove(new Integer(t));
            P.add(t);

            // start of inner loop

            // see if we enter from 5

            boolean from5 = true;

            while (true) {

                // step 6: LS subproblem! this is the difficult part... generate submatrices, corresponding in size...

                int nbSplits = P.size();

                double[][] EtEp = new double[nbSplits][nbSplits];
                double[] Etfp = new double[nbSplits];

                int row = 0;
                int column = 0;

                // fill

                for (int i = 0; i < maxSplits; i++) {

                    if (P.contains(new Integer(i))) {

                        column = 0;

                        for (int j = 0; j < maxSplits; j++) {

                            if (P.contains(new Integer(j))) {

                                EtEp[row][column] = EtE.getElementAt(i, j);

                                column++;
                            }
                        }

                        Etfp[row++] = Etf[i];
                    }
                }

                // now solve their least squares problem EtEp x = Etfp

                // BUT! we only have size (P) splits

                // so aMap is the map reduced zHolder to true zHolder
                int[] aMap = P.createMap(maxSplits);

                double Q[][] = this.initQ(nbSplits, EtEp);
                UpperTriangularMatrix R = this.initR(nbSplits, EtEp, Q);

                // check consistency!
                if (R.getElementAt(nbSplits - 1, nbSplits - 1) == 0.0) {
                    log.warn("Subproblem is underdetermined, results may not be unique!");
                    //TODO Should I throw an exception here?
                }

                // least squares solution of zHolder:
                double[] QtEtfp = new double[nbSplits];

                for (int i = 0; i < nbSplits; i++) {

                    double jSum = 0.0;

                    for (int j = 0; j < nbSplits; j++) {
                        jSum += Q[j][i] * Etfp[j];
                    }

                    QtEtfp[i] = jSum;
                }

                // reduced row echelon whatever solver... maybe?
                // first for the non-personas...
                double[] z = new double[maxSplits];

                // ... second those that exist; wonder if I can do this?
                double[] zRed = new double[nbSplits];

                for (int i = 0; i < nbSplits; i++) {

                    int d = nbSplits - 1;

                    double jSum = 0.0;

                    for (int j = 0; j < i; j++) {

                        jSum += R.getElementAt(d - i, d - j) * z[aMap[d - j]];
                    }

                    double nom = (QtEtfp[d - i] - jSum);
                    double den = R.getElementAt(d - i, d - i);

                    z[aMap[d - i]] = nom / den;
                    zRed[d - i] = z[aMap[d - i]];
                }


                // finite-precision test
                // only do this first time around this loop
                if (from5) {

                    if (z[t] <= tolerance) {
                        w[t] = 0;
                        calculateW = false;
                        P.remove(new Integer(t));
                        Z.add(new Integer(t));
                        break;
                    }
                }

                // step 7: check for stopping conditions if so, break
                if (P.allGTTolerance(z)) {

                    for (int i = 0; i < maxSplits; i++) {
                        x[i] = z[i];
                    }

                    break;
                }

                // step 8:
                int q = P.indexOfMin(x, z);

                // step 9:
                double alpha = x[q] / (x[q] - z[q]);

                // step 10:
                for (int i = 0; i < maxSplits; i++) {
                    x[i] = x[i] + alpha * (z[i] - x[i]);
                }

                // step 11:
                P.transferZeroWeights(Z, x);

                // we will now go back to 6 again, and from here
                from5 = false;
                System.gc();
            }
        }

        return x;
    }


    protected double[][] initQ(final int nbSplits, double[][] EtEp) {

        double Q[][] = new double[nbSplits][nbSplits];

        for (int i = 0; i < nbSplits; i++) {

            // for each column

            // the column is v

            double[] v = new double[nbSplits];

            // take the list of dot products

            double[] L = new double[i];

            for (int j = 0; j < i; j++) {

                double sum = 0.0;

                for (int k = 0; k < nbSplits; k++) {

                    sum += EtEp[k][i] * Q[k][j];
                }

                L[j] = sum;
            }

            // next...

            for (int k = 0; k < nbSplits; k++) {

                double jSum = 0.0;

                for (int j = 0; j < i; j++) {

                    jSum += L[j] * Q[k][j];
                }

                // for all elements in the column

                v[k] = EtEp[k][i] - jSum;
            }

            // then we must normalize v before adding it

            double length = 0.0;

            for (int k = 0; k < nbSplits; k++) {

                length += v[k] * v[k];
            }

            if (length != 0.0) {

                for (int k = 0; k < nbSplits; k++) {

                    v[k] = v[k] / Math.sqrt(length);
                }
            }

            // then store v

            for (int k = 0; k < nbSplits; k++) {

                Q[k][i] = v[k];
            }
        }

        return Q;
    }

    protected UpperTriangularMatrix initR(final int nbSplits, double[][] EtEp, double[][] Q) {

        UpperTriangularMatrix R = new UpperTriangularMatrix(nbSplits);

        for (int i = 0; i < nbSplits; i++) {

            for (int j = 0; j <= i; j++) {

                double sum = 0.0;

                for (int k = 0; k < nbSplits; k++) {

                    sum += EtEp[k][i] * Q[k][j];
                }

                R.setElementAt(j, i, sum);
            }
        }

        return R;
    }


    protected static class PList extends LinkedList<Integer> {

        private final double tolerance;

        public PList(double tolerance) {
            this.tolerance = tolerance;
        }


        /**
         * Checks if all indexed elements in z are greater than or equal to tolerance
         *
         * @param z The list of weights
         * @return True if all indexed elements in w are less than or equal to the tolerance, otherwise false.
         */
        public boolean allGTTolerance(double[] z) {

            for (Integer i : this) {

                if (z[i] <= tolerance) {

                    return false;
                }
            }

            return true;
        }

        public int indexOfMin(double[] x, double[] z) {

            double min = Double.POSITIVE_INFINITY;
            int q = -1;

            for (Integer i : this) {

                if (z[i] <= tolerance) {

                    if (x[i] / (x[i] - z[i]) < min) {

                        q = i;
                        min = x[i] / (x[i] - z[i]);
                    }
                }
            }

            return q;
        }


        public void transferZeroWeights(ZList Z, double[] x) {

            ListIterator<Integer> lI = this.listIterator();

            while (lI.hasNext()) {
                int i = lI.next();

                if (x[i] <= tolerance) {
                    lI.remove();
                    Z.add(i);
                }
            }
        }

        public int[] createMap(final int maxSplits) {

            int[] aMap = new int[this.size()];

            int mapIndex = 0;

            for (int i = 0; i < maxSplits; i++) {

                if (this.contains(new Integer(i))) {

                    aMap[mapIndex++] = i;
                }
            }

            return aMap;
        }
    }


    protected static class ZList extends LinkedList<Integer> {

        public ZList(final int maxSplits) {
            for (int i = 0; i < maxSplits; i++) {
                this.add(i);
            }
        }

        /**
         * Checks if all indexed elements in w are less than or equal to tolerance
         *
         * @param w         The list of weights
         * @param tolerance The tolerance to allow
         * @return True if all indexed elements in w are less than or equal to the tolerance, otherwise false.
         */
        public boolean allLEQTolerance(double[] w, double tolerance) {

            for (Integer i : this) {

                if (w[i] > tolerance) {

                    return false;
                }
            }

            return true;
        }

        public int getNbPositive(double[] w) {

            int nbPositive = 0;
            for (Integer i : this) {
                if (w[i] > 0.0)
                    nbPositive++;
            }
            return nbPositive;
        }

        public int indexOfMax(double[] w) {

            double max = Double.NEGATIVE_INFINITY;

            int index = -1;
            for (Integer i : this) {

                if (w[i] > max) {

                    index = i;
                    max = w[i];
                }
            }

            return index;
        }

    }

    protected static class SolutionHypothesis {

        private LinkedList<Integer> P;
        private int n;

        public SolutionHypothesis(LinkedList<Integer> p, int n) {
            this.P = p;
            this.n = n;
        }

        public boolean equals(SolutionHypothesis other) {
            return (other.n == this.n && other.P.size() == this.P.size() && other.P.containsAll(this.P));
        }
    }
}
