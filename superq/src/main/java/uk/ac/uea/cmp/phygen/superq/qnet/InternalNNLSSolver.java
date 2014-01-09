package uk.ac.uea.cmp.phygen.superq.qnet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.math.matrix.SymmetricMatrix;
import uk.ac.uea.cmp.phygen.core.math.matrix.UpperTriangularMatrix;

import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by dan on 08/01/14.
 */
public class InternalNNLSSolver {

    private static Logger log = LoggerFactory.getLogger(InternalNNLSSolver.class);

    /**
     * @param N Number of Taxa
     */
    public double[] optimise(final int N, double[] Etf, SymmetricMatrix EtE, final double tolerance) throws QNetException {

        int maxIterations = N * N;
        int iterations = 0;

        // once that is done, we wish to solve the NNLS problem Ex -f for x.

        LinkedList<Integer> P = new LinkedList<>();
        LinkedList<Integer> Z = new LinkedList<>();


        int nbSplits = N * (N - 1) / 2 - N;

        //Original implemented method to solve NNLS

        double[] x = new double[nbSplits];
        double[] w = new double[nbSplits];
        double[] z = new double[nbSplits];

        // step 1: initially, all variables are classed as zero variables
        for (int i = 0; i < nbSplits; i++) {
            Z.add(i);
        }


        // finite precision test thing

        boolean calculateW = true;

        // start of outer loop

        // list of choices tested

        List<SolutionHypothesis> hypotheses = new LinkedList<>();

        int it = 0;

        while (true) {

            if (iterations > maxIterations) {

                throw new QNetException(maxIterations + " iterations have been performed to no avail. Please increase the tolerance or use a different solver.");
            }

            iterations++;

            // step 2: we are now in loop... compute w
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

            // step 3: check for stopping conditions if so, break
            if (Z.isEmpty()) {
                break;
            }

            boolean allLequalZero = true;

            ListIterator<Integer> lI = Z.listIterator();

            while (lI.hasNext()) {

                int i = lI.next();

                if (w[i] > tolerance) {

                    allLequalZero = false;
                    break;
                }
            }

            if (allLequalZero) {
                break;
            }

            // step 4, 5: find index t and move to nonzero set
            int t = -1;

            while (true) {

                double max = Double.NEGATIVE_INFINITY;
                t = -1;

                int noPositive = 0;

                lI = Z.listIterator();

                while (lI.hasNext()) {

                    int i = lI.next();

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
                }
                else {

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

                int noSplits = P.size();

                double[][] EtEp = new double[noSplits][noSplits];
                double[] Etfp = new double[noSplits];

                int row = 0;
                int column = 0;

                // fill

                for (int i = 0; i < nbSplits; i++) {

                    if (P.contains(i)) {

                        column = 0;

                        for (int j = 0; j < nbSplits; j++) {

                            if (P.contains(j)) {

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


                // should be better initialized

                // BUT! we only have size (P) splits

                int[] aMap = new int[P.size()];

                int mapIndex = 0;

                for (int i = 0; i < nbSplits; i++) {

                    if (P.contains(i)) {

                        aMap[mapIndex] = i;

                        mapIndex++;
                    }
                }

                // so aMap is the map reduced zHolder to true zHolder

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

                    log.warn("Subproblem is underdetermined, results may not be unique!");
                }

                // done

                // least squares solution of zHolder:

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

                for (int i = 0; i < nbSplits; i++) {

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


                // finite-precision test

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

                boolean allAboveZero = true;

                lI = P.listIterator();

                while (lI.hasNext()) {

                    int i = lI.next();

                    if (z[i] <= tolerance) {

                        allAboveZero = false;
                        break;
                    }
                }

                if (allAboveZero) {

                    for (int i = 0; i < nbSplits; i++) {

                        x[i] = z[i];
                    }

                    break;
                }

                // step 8:
                lI = P.listIterator();

                double min = Double.POSITIVE_INFINITY;
                int q = -1;

                while (lI.hasNext()) {

                    int i = lI.next();

                    if (z[i] <= tolerance) {

                        if (x[i] / (x[i] - z[i]) < min) {

                            q = i;
                            min = x[i] / (x[i] - z[i]);
                        }
                    }
                }

                // step 9:
                double alpha = x[q] / (x[q] - z[q]);

                // step 10:

                for (int i = 0; i < nbSplits; i++) {

                    x[i] = x[i] + alpha * (z[i] - x[i]);
                }

                // step 11:
                lI = P.listIterator();

                while (lI.hasNext()) {
                    int i = lI.next();

                    if (x[i] <= tolerance) {
                        lI.remove();
                        Z.add(i);
                    }
                }

                // we will now go back to 6 again, and from here
                from5 = false;
                System.gc();
            }
        }

        return x;
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
