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
package uk.ac.uea.cmp.phygen.core.alg;

import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/*
Routines for performing OLS and WLS for circular splits. The splits
are specified by their circular ordering.

For the unconstrained case (and a maximal collection of circular splits), both OLS
and WLS give the same answer - the least squares branch estimates can be computed
directly from the distances as proven by Chepoi et al.

The constrained case (constrained to non-negative values) is handled using an
active set method, with an iterative technique (conjugate gradients) for the
equality constrained optimizations at each step. The use of conjugate gradients
means that we only need O(n^2) memory (n = number of taxa) compared to
O(n^4) memory if we compute A^tWA and apply Cholesky decomposition.

There is still room for improvement - it may be that a more sophisticated
iterative method (e.g. Memoryless Quasi-Newton) converges faster than the conjugate
gradient method. Furthermore, I can imagine that an iterative method combined with
a positivity constraint would speed up the constrained optimization considerably - as yet
I haven't found how this may be done.
*/

/**
 * Given a circular ordering and a distance matrix,
 * computes the unconstrained or constrained least square weighted splits
 */
public class CircularNNLS {
    /* Epsilon constant for the conjugate gradient algorithm */
    static final double logPiPlus2 = Math.log(Math.PI) + 2.0;
    static final double CG_EPSILON = 0.0001;
    private boolean minimizeAIC = false;

    /**
     * Compute the branch lengths for unconstrained least squares using
     * the formula of Chepoi and Fichet (this takes O(N^2) time only!).
     *
     * @param ntax the number of taxa
     * @param d    the distance matrix
     * @param x    the split weights
     */
    static void runUnconstrainedLS(int ntax, double[][] d, double[][] x) {
        for (int j = 0; j < ntax - 1; j++) {
            int i;
            for (i = 0; i < j - 1; i++) {
                x[j][i] = (d[j][i] + d[j + 1][i + 1] - d[j + 1][i] - d[j][i + 1]) / 2.0;
            }
            if (j > 0)
                x[j][j - 1] = (d[j][i] + d[j + 1][i + 1] - d[j + 1][i]) / 2.0;
        }

        x[ntax - 1][0] = (d[ntax - 1][0] + d[1][0] - d[ntax - 1][1]) / 2.0;
        for (int i = 1; i < ntax - 2; i++) {
            x[ntax - 1][i] = (d[ntax - 1][i] + d[i + 1][0] - d[i][0] - d[ntax - 1][i + 1]) / 2.0;
        }
        x[ntax - 1][ntax - 2] = (d[ntax - 1][ntax - 2] + d[ntax - 1][0] - d[ntax - 2][0]) / 2.0;
    }

    /**
     * Fill the weight matrix
     *
     * @param ntax  the number of taxa
     * @param W     the weight matrix
     * @param d     the distance matrix
     * @param power the power
     */
    static void fillW(int ntax, double[][] W, double[][] d, int power) {
        double d_ij, w_ij;

        if (power != 0) {
            for (int j = 0; j < ntax; j++) {
                for (int i = 0; i < j; i++) {
                    d_ij = d[j][i];
                    if (d_ij > 0.0) {
                        w_ij = 1.0 / d_ij;
                        if (power == 2)
                            w_ij *= w_ij;
                    } else {
                        w_ij = 100000000;
                    }
                    W[j][i] = w_ij;
                }
            }
        }
    }

    /**
     * Computes p = A^Td, where A is the topological matrix for the
     * splits with circular ordering 0,1,2,....,ntax-1
     *
     * @param ntax number of taxa
     * @param d    distance matrix
     * @param p    the result
     */
    static void calculateAtx(int ntax, double[][] d, double[][] p) {
        double p_ij = 0;

        for (int i = 0; i <= ntax - 2; i++) {
            p_ij = 0.0;
            for (int k = 0; k <= i; k++)
                p_ij += d[i + 1][k];
            for (int k = i + 2; k <= ntax - 1; k++)
                p_ij += d[k][i + 1];
            p[i + 1][i] = p_ij;
        }
        for (int i = 0; i <= ntax - 3; i++) {
            p[i + 2][i] = p[i + 1][i] + p[i + 2][i + 1] - 2 * d[i + 2][i + 1];
        }
        for (int k = 3; k <= ntax - 1; k++) {
            for (int i = 0; i <= ntax - k - 1; i++) {
                int j = i + k;
                p[j][i] = p[j - 1][i] + p[j][i + 1] - p[j - 1][i + 1] - 2.0 * d[j][i + 1];
            }
        }
    }

    /**
     * Computes d = Ab, where A is the topological matrix for the
     * splits with circular ordering 0,1,2,....,ntax-1
     *
     * @param ntax number of taxa
     * @param b    split weights
     * @param d    pairwise distances from split weights
     */
    static void calculateAb(int ntax, double[][] b, double[][] d) {

        double d_ij;

        for (int i = 0; i <= ntax - 2; i++) {
            d_ij = 0.0;
            for (int k = 0; k <= i - 1; k++)
                d_ij += b[i][k];
            for (int k = i + 1; k <= ntax - 1; k++)
                d_ij += b[k][i];
            d[i + 1][i] = d_ij;
        }
        for (int i = 0; i <= ntax - 3; i++) {
            d[i + 2][i] = d[i + 1][i] + d[i + 2][i + 1] - 2 * b[i + 1][i];
        }
        for (int k = 3; k <= ntax - 1; k++) {
            for (int i = 0; i <= ntax - k - 1; i++) {
                int j = i + k;
                d[j][i] = d[j - 1][i] + d[j][i + 1] - d[j - 1][i + 1] - 2.0 * b[j - 1][i];
            }
        }
    }

    /**
     * Conjugate gradient algorithm solving A^tWA x = b (where b = AtWd)
     * such that all x[i][j] for which active[i][j] = true are set to zero.
     * We assume that x[i][j] is zero for all active i,j, and use the given
     * values for x as our starting vector.
     *
     * @param ntax   the number of taxa
     * @param r      stratch matrix
     * @param w      stratch matrix
     * @param p      stratch matrix
     * @param y      stratch matrix
     * @param W      the W matrix
     * @param b      the b matrix
     * @param active the active constraints
     * @param x      the x matrix
     */
    static void runConjugateGrads(int ntax,
                                  double[][] r, double[][] w, double[][] p, double[][] y,
                                  double[][] W, double[][] b,
                                  boolean[][] active, double[][] x) {
        int kmax = ntax * (ntax - 1) / 2;
        /* Maximum number of iterations of the cg algorithm (probably too many) */
        calculateAb(ntax, x, y);
        //for (int i = 0; i < ntax; i++)
        //    for (int j = 0; j < i; j++)
        //        y[i][j] = W[i][j] * y[i][j];
        calculateAtx(ntax, y, r); /*r = AtWAx */
        for (int i = 0; i < ntax; i++)
            for (int j = 0; j < i; j++)
                if (!active[i][j])
                    r[i][j] = b[i][j] - r[i][j];
                else
                    r[i][j] = 0.0;

        double rho = norm(ntax, r);
        double rho_old = 0;

        double e_0 = CG_EPSILON * Math.sqrt(norm(ntax, b));
        int k = 0;

        while ((rho > e_0 * e_0) && (k < kmax)) {

            k = k + 1;

            if (k == 1) {

                for (int i = 0; i < ntax; i++)
                    for (int j = 0; j < i; j++)
                        p[i][j] = r[i][j];

            } else {
                double beta = rho / rho_old;
                for (int i = 0; i < ntax; i++)
                    for (int j = 0; j < i; j++)
                        p[i][j] = r[i][j] + beta * p[i][j];

            }

            calculateAb(ntax, p, y);
            //for (int i = 0; i < ntax; i++)
            //    for (int j = 0; j < i; j++)
            //        y[i][j] = W[i][j] * y[i][j];
            calculateAtx(ntax, y, w); /*w = AtWAp */
            zeroActive(ntax, w, active);

            double alpha = 0.0;
            for (int i = 0; i < ntax; i++)
                for (int j = 0; j < i; j++)
                    alpha += p[i][j] * w[i][j];
            alpha = rho / alpha;

            /* Update x and the residual, r */
            for (int i = 0; i < ntax; i++) {
                for (int j = 0; j < i; j++) {
                    x[i][j] = x[i][j] + alpha * p[i][j];
                    r[i][j] = r[i][j] - alpha * w[i][j];
                }
            }

            rho_old = rho;
            rho = norm(ntax, r);

        }
    }

    /**
     * Computes sum of squares of the lower triangle of the matrix x
     *
     * @param x the matrix
     * @return sum of squares of the lower triangle
     */
    static double norm(int ntax, double[][] x) {
        double ss = 0.0;

        for (int i = 0; i < ntax; i++) {
            for (int j = 0; j < i; j++) {
                ss += x[i][j] * x[i][j];
            }
        }
        return ss;
    }

    /**
     * Active is a boolean array.
     * This routine sets x[i][j] to zero for all i,j such that active[i][j] != 0
     *
     * @param ntax   the number of taxa
     * @param x      the x matrix
     * @param active the active constraints
     */
    static void zeroActive(int ntax, double[][] x, boolean[][] active) {
        for (int i = 0; i < ntax; i++)
            for (int j = 0; j < i; j++)
                if (active[i][j]) x[i][j] = 0.0;
    }

    public boolean getMinimizeAIC() {
        return minimizeAIC;
    }

    public void setMinimizeAIC(boolean val) {
        minimizeAIC = val;
    }

    /**
     * An implementation of the active set method where the conjugate gradient
     * method is used for optimization.
     *
     * @param d the distance matrix
     * @param W the weight matrix
     * @param x the split weights
     */
    void runActiveConjugate(int ntax, double[][] d, double[][] W, double[][] x) {
        final boolean collapse_many_negs = true;
        int min_i = 0, min_j = 0;
        double xi = 0, min_xi = 0;
        double min_grad = 0, grad_ij = 0;
        SortedSet neg_indices = new TreeSet(new DoubleIntInt());
        boolean all_positive = true, first_pass = true;
        double[][] diagAtWA = null;

        if (getMinimizeAIC()) {
            diagAtWA = new double[ntax][ntax];
            calculateAtx(ntax, W, diagAtWA); /* Use the identity that AtWA_ii = Atw, if w = diag(W) */
        }

        /* First evaluate the unconstrained optima. If this is feasible then we don't have to do anything more! */
        runUnconstrainedLS(ntax, d, x);

        //System.err.println("Run unconstrained");
        for (int i = 0; i < ntax; i++) {
            for (int j = 0; j < i; j++) {
                if (x[i][j] < 0.0) {
                    all_positive = false;
                    break;
                }
            }
            if (!all_positive)
                break;
        }
        if (all_positive) {/* If the unconstrained optimum is feasible then it is also the constrained optimum */
            if (!getMinimizeAIC())
                return;
            /* Check to see if all of the splits are informative. We use an approximate formula... we keep
             * a split when logPiPlus2 < b_i^2 A'WA_ii. The left hand side is how much AIC would go down
             * without the parameter b_i. The RHS is how much the AIC would increase because of the worse likelihood.
             */
            boolean allinformative = true;
            for (int i = 0; i < ntax; i++) {
                for (int j = 0; j < ntax; j++) {
                    double x_ij = x[i][j];
                    if (logPiPlus2 > x_ij * x_ij * diagAtWA[i][j])
                        allinformative = false;
                }
            }
            if (allinformative)
                return;
        }
        /* Allocate memory for the "utility" vectors */

        //System.err.println("Allocating memory");

        //ToDo: Put a memory overflow catch around this.
        double[][] r = new double[ntax][ntax];
        double[][] w = new double[ntax][ntax];
        double[][] p = new double[ntax][ntax];
        double[][] y = new double[ntax][ntax];

        double[][] old_x = new double[ntax][ntax];
        for (int i = 0; i < ntax; i++)
            for (int j = i + 1; j < ntax; j++)
                old_x[j][i] = 1;

        /* Initialise active - originally no variables are active (held to 0.0) */
        boolean[][] active = new boolean[ntax][ntax];
        for (int i = 0; i < ntax; i++) {
            for (int j = i + 1; j < ntax; j++)
                active[j][i] = false;
        }

        /* Allocate and compute AtWd */
        double[][] AtWd = new double[ntax][ntax];

        for (int i = 0; i < ntax; i++)
            for (int j = 0; j < i; j++)
                //y[i][j] = W[i][j] * d[i][j];
                y[i][j] = d[i][j];
        calculateAtx(ntax, y, AtWd);

        while (true) {
            while (true) /* Inner loop: find the next feasible optimum */ {
                if (!first_pass) { /* The first time through we use the unconstrained branch lengths */
                    runConjugateGrads(ntax, r, w, p, y, W, AtWd, active, x);
                }
                //System.err.println("Inner");

                if (collapse_many_negs) { /* Typically, a large number of edges are negative, so on the first
                                                pass of the algorithm we add the worst 60% to the active set */
                    neg_indices.clear();
                    for (int i = 0; i < ntax; i++) {
                        for (int j = 0; j < i; j++) {
                            if (x[i][j] < 0.0) {
                                DoubleIntInt this_index = new DoubleIntInt();
                                this_index.d = x[i][j];
                                this_index.first = i;
                                this_index.second = j;
                                neg_indices.add(this_index);
                            }
                        }
                    }
                    int num_neg_indices = neg_indices.size();
                    //   cout<<"Number negative = "<<num_neg_indices<<endl;
                    if (num_neg_indices > 0) {
                        /* Contract the worse 60% */
                        int top = (6 * num_neg_indices) / 10 + 1;
                        int count = 0;
                        Iterator it = neg_indices.iterator();
                        while (it.hasNext()) {
                            if (count++ == top)
                                break;
                            DoubleIntInt this_index = (DoubleIntInt) (it.next());
                            x[this_index.first][this_index.second] = 0.0;
                            active[this_index.first][this_index.second] = true;
                            //  cout<<"Fixing "<<this_index.first<<","<<this_index.second<<endl;
                        }
                        runConjugateGrads(ntax, r, w, p, y, W, AtWd, active, x); /* Re-uk.ac.uea.cmp.phygen.superq.optimise, so that the current x is always optimal */
                    }
                }

                min_i = -1;
                for (int i = 0; i < ntax; i++) {
                    for (int j = 0; j < i; j++) {
                        if (x[i][j] < 0.0) {
                            xi = (old_x[i][j]) / (old_x[i][j] - x[i][j]);
                            if ((min_i == -1) || (xi < min_xi)) {
                                min_i = i;
                                min_j = j;
                                min_xi = xi;
                            }
                        }
                    }
                }

                first_pass = false;

                if (min_i == -1) {/* This is a feasible solution - go to the next stage to check if its also optimal */
                    /* If we are minimizing AIC then go and check to see
                     * if contracting an edge gives a reduction of AIC -
                     * if it does, then add it to the active set and continue.
                     */

                    break;
                }
                if (min_i != -1) {/* There are still negative edges. We move to the feasible point that is closest to
                                            x on the line from x to old_x */

                    for (int i = 0; i < ntax; i++) /* Move to the last feasible solution on the path from old_x to x */
                        for (int j = 0; j < i; j++)
                            if (!active[i][j]) {

                                old_x[i][j] += min_xi * (x[i][j] - old_x[i][j]);
                            }
                    active[min_i][min_j] = true; /* Add the first constraint met to the active set */
                    x[min_i][min_j] = 0.0; /* This fixes problems with round-off errors */
                } else {
                    if (!getMinimizeAIC())
                        break;
                    /* Check to see if all positive edges are informative. If not, we contract the least informative */
                    double mininfo = 0.0;
                    for (int i = 0; i < ntax; i++) {
                        for (int j = 0; j < ntax; j++) {
                            double x_ij = x[i][j];
                            double info = x_ij * x_ij * diagAtWA[i][j] - logPiPlus2;
                            if (info < mininfo) {
                                min_i = i;
                                min_j = j;
                                mininfo = info;
                            }
                        }
                    }
                    if (mininfo == 0.0)
                        break;
                    else {
                        x[min_i][min_j] = 0.0;
                        old_x[min_i][min_j] = 0.0;
                        active[min_i][min_j] = true;
                    }

                }
            }

            //System.err.println("Outer");

            /* Find i,j that minimizes the gradient over all i,j in the active set. Note that grad = 2(AtWAb-AtWd) */
            min_i = -1;
            calculateAb(ntax, x, y);
            //for (int i = 0; i < ntax; i++)
            //    for (int j = 0; j < i; j++)
            //        y[i][j] *= W[i][j];
            calculateAtx(ntax, y, r); /* r = AtWAx */

            if (!getMinimizeAIC()) {
                /* We check to see that we are at a constrained minimum.... that is that the gradient is positive for
                 * all i,j in the active set.
                 */
                for (int i = 0; i < ntax; i++) {
                    for (int j = 0; j < i; j++) {
                        r[i][j] -= AtWd[i][j];
                        r[i][j] *= 2.0;
                        if (active[i][j]) {
                            grad_ij = r[i][j];
                            if ((min_i == -1) || (grad_ij < min_grad)) {
                                min_i = i;
                                min_j = j;
                                min_grad = grad_ij;
                            }
                        }
                    }
                }
                if ((min_i == -1) || (min_grad > -0.0001))
                    return; /* We have arrived at the constrained optimum */
                else
                    active[min_i][min_j] = false;
            } else {
                /* We check to see whether adding any edges back in gives a redued AIC. We use a heuristic calculation.
                 * If we add b_i back in, and just uk.ac.uea.cmp.phygen.superq.optimise that single variable, then the AIC decreases when
                 * 		A'WA_ii * logPiPlus2 - grad^2 < 0.
                 */
                double mininfo = 0.0;
                for (int i = 0; i < ntax; i++) {
                    for (int j = 0; j < i; j++) {
                        r[i][j] -= AtWd[i][j];
                        r[i][j] *= 2.0;
                        if (active[i][j]) {
                            grad_ij = r[i][j];
                            double info = diagAtWA[i][j] * logPiPlus2 - grad_ij * grad_ij;
                            if (info < mininfo) {
                                min_i = i;
                                min_j = j;
                                mininfo = info;
                            }
                        }
                    }
                }
                if (mininfo == 0.0)
                    return;
                else
                    active[min_i][min_j] = false;
            }
        }
    }

    //This method is a hack used to compute non-negative least
    //squares weights for a tree inside a circular split system.
    //Actually, it can be any subset of the circular split system.
    //The array flag is used to indicate which splits are in the
    //subset.
    void runActiveConjugate(int ntax, double[][] d, double[][] W, boolean[][] flag, double[][] x) {
        final boolean collapse_many_negs = true;
        int min_i = 0, min_j = 0;
        double xi = 0, min_xi = 0;
        double min_grad = 0, grad_ij = 0;
        SortedSet neg_indices = new TreeSet(new DoubleIntInt());
        double[][] diagAtWA = null;

        if (getMinimizeAIC()) {
            diagAtWA = new double[ntax][ntax];
            calculateAtx(ntax, W, diagAtWA); /* Use the identity that AtWA_ii = Atw, if w = diag(W) */
        }

        //set all weights to 0
        for (int i = 0; i < ntax; i++) {
            for (int j = i + 1; j < ntax; j++) {
                x[j][i] = 0.0;
            }
        }

        /* Allocate memory for the "utility" vectors */

        //System.err.println("Allocating memory");

        //ToDo: Put a memory overflow catch around this.
        double[][] r = new double[ntax][ntax];
        double[][] w = new double[ntax][ntax];
        double[][] p = new double[ntax][ntax];
        double[][] y = new double[ntax][ntax];

        double[][] old_x = new double[ntax][ntax];
        for (int i = 0; i < ntax; i++)
            for (int j = i + 1; j < ntax; j++)
                old_x[j][i] = 1;

        /* Initialise active - originally only variables that correspond to
         splits not in the subset are active (held to 0.0) */
        boolean[][] active = new boolean[ntax][ntax];
        for (int i = 0; i < ntax; i++) {
            for (int j = i + 1; j < ntax; j++)
                active[j][i] = (!flag[j][i]);
        }

        /* Allocate and compute AtWd */
        double[][] AtWd = new double[ntax][ntax];

        for (int i = 0; i < ntax; i++)
            for (int j = 0; j < i; j++)
                //y[i][j] = W[i][j] * d[i][j];
                y[i][j] = d[i][j];
        calculateAtx(ntax, y, AtWd);

        while (true) {
            while (true) /* Inner loop: find the next feasible optimum */ {
                runConjugateGrads(ntax, r, w, p, y, W, AtWd, active, x);

                //System.err.println("Inner");

                if (collapse_many_negs) { /* Typically, a large number of edges are negative, so on the first
                								pass of the algorithm we add the worst 60% to the active set */
                    neg_indices.clear();
                    for (int i = 0; i < ntax; i++) {
                        for (int j = 0; j < i; j++) {
                            if (x[i][j] < 0.0) {
                                DoubleIntInt this_index = new DoubleIntInt();
                                this_index.d = x[i][j];
                                this_index.first = i;
                                this_index.second = j;
                                neg_indices.add(this_index);
                            }
                        }
                    }
                    int num_neg_indices = neg_indices.size();
                    //   cout<<"Number negative = "<<num_neg_indices<<endl;
                    if (num_neg_indices > 0) {
                        /* Contract the worse 60% */
                        int top = (6 * num_neg_indices) / 10 + 1;
                        int count = 0;
                        Iterator it = neg_indices.iterator();
                        while (it.hasNext()) {
                            if (count++ == top)
                                break;
                            DoubleIntInt this_index = (DoubleIntInt) (it.next());
                            x[this_index.first][this_index.second] = 0.0;
                            active[this_index.first][this_index.second] = true;
                            //  cout<<"Fixing "<<this_index.first<<","<<this_index.second<<endl;
                        }
                        runConjugateGrads(ntax, r, w, p, y, W, AtWd, active, x); /* Re-uk.ac.uea.cmp.phygen.superq.optimise, so that the current x is always optimal */
                    }
                }

                min_i = -1;
                for (int i = 0; i < ntax; i++) {
                    for (int j = 0; j < i; j++) {
                        if (x[i][j] < 0.0) {
                            xi = (old_x[i][j]) / (old_x[i][j] - x[i][j]);
                            if ((min_i == -1) || (xi < min_xi)) {
                                min_i = i;
                                min_j = j;
                                min_xi = xi;
                            }
                        }
                    }
                }

                //first_pass = false;

                if (min_i == -1) {/* This is a feasible solution - go to the next stage to check if its also optimal */
                    /* If we are minimizing AIC then go and check to see
                     * if contracting an edge gives a reduction of AIC -
                     * if it does, then add it to the active set and continue.
                     */

                    break;
                }
                if (min_i != -1) {/* There are still negative edges. We move to the feasible point that is closest to
                							x on the line from x to old_x */

                    for (int i = 0; i < ntax; i++) /* Move to the last feasible solution on the path from old_x to x */
                        for (int j = 0; j < i; j++)
                            if (!active[i][j]) {

                                old_x[i][j] += min_xi * (x[i][j] - old_x[i][j]);
                            }
                    active[min_i][min_j] = true; /* Add the first constraint met to the active set */
                    x[min_i][min_j] = 0.0; /* This fixes problems with round-off errors */
                } else {
                    if (!getMinimizeAIC())
                        break;
                    /* Check to see if all positive edges are informative. If not, we contract the least informative */
                    double mininfo = 0.0;
                    for (int i = 0; i < ntax; i++) {
                        for (int j = 0; j < ntax; j++) {
                            double x_ij = x[i][j];
                            double info = x_ij * x_ij * diagAtWA[i][j] - logPiPlus2;
                            if (info < mininfo) {
                                min_i = i;
                                min_j = j;
                                mininfo = info;
                            }
                        }
                    }
                    if (mininfo == 0.0)
                        break;
                    else {
                        x[min_i][min_j] = 0.0;
                        old_x[min_i][min_j] = 0.0;
                        active[min_i][min_j] = true;
                    }

                }
            }

            //System.err.println("Outer");

            /* Find i,j that minimizes the gradient over all i,j in the active set. Note that grad = 2(AtWAb-AtWd) */
            min_i = -1;
            calculateAb(ntax, x, y);
            //for (int i = 0; i < ntax; i++)
            //    for (int j = 0; j < i; j++)
            //        y[i][j] *= W[i][j];
            calculateAtx(ntax, y, r); /* r = AtWAx */

            if (!getMinimizeAIC()) {
                /* We check to see that we are at a constrained minimum.... that is that the gradient is positive for
                 * all i,j in the active set.
                 */
                for (int i = 0; i < ntax; i++) {
                    for (int j = 0; j < i; j++) {
                        r[i][j] -= AtWd[i][j];
                        r[i][j] *= 2.0;
                        if (active[i][j] && flag[i][j]) {
                            grad_ij = r[i][j];
                            if ((min_i == -1) || (grad_ij < min_grad)) {
                                min_i = i;
                                min_j = j;
                                min_grad = grad_ij;
                            }
                        }
                    }
                }
                if ((min_i == -1) || (min_grad > -0.0001))
                    return; /* We have arrived at the constrained optimum */
                else
                    active[min_i][min_j] = false;
            } else {
                /* We check to see whether adding any edges back in gives a redued AIC. We use a heuristic calculation.
                 * If we add b_i back in, and just uk.ac.uea.cmp.phygen.superq.optimise that single variable, then the AIC decreases when
                 * 		A'WA_ii * logPiPlus2 - grad^2 < 0.
                 */
                double mininfo = 0.0;
                for (int i = 0; i < ntax; i++) {
                    for (int j = 0; j < i; j++) {
                        r[i][j] -= AtWd[i][j];
                        r[i][j] *= 2.0;
                        if (active[i][j] && flag[i][j]) {
                            grad_ij = r[i][j];
                            double info = diagAtWA[i][j] * logPiPlus2 - grad_ij * grad_ij;
                            if (info < mininfo) {
                                min_i = i;
                                min_j = j;
                                mininfo = info;
                            }
                        }
                    }
                }
                if (mininfo == 0.0)
                    return;
                else
                    active[min_i][min_j] = false;
            }
        }
    }

    //This method computes the least squares weights for the splits.
    //Parameters:
    //dist...distance matrix, the entries are permuted such that
    //       the ordering 0,1,2,...,(ntax-1) is the circular ordering
    //       for the full circular split system we want to compute
    //       weights for.
    //ntax...number of taxa, taxa are numbered 0,1,2,...,(ntax-1)
    //x   ...2-dimensional array that contains the split weights
    //       computed by the NNLS-fitting algorithm.
    public void circularLeastSquares(double[][] dist, int ntax, double[][] x) {
        double[][] W = new double[ntax][ntax];
        fillW(ntax, W, dist, 1);
        runActiveConjugate(ntax, dist, W, x);
    }

    public void treeInCycleLeastSquares(double[][] dist, boolean[][] flag, int ntax, double[][] x) {
        double[][] W = new double[ntax][ntax];
        fillW(ntax, W, dist, 1);
        runActiveConjugate(ntax, dist, W, flag, x);
    }

    class DoubleIntInt implements Comparator {
        double d;
        int first;
        int second;

        public int compare(Object obj1, Object obj2) throws ClassCastException {
            DoubleIntInt dii1 = (DoubleIntInt) obj1;
            DoubleIntInt dii2 = (DoubleIntInt) obj2;
            if (dii1.d < dii2.d)
                return -1;
            else if (dii1.d > dii2.d)
                return 1;
            if (dii1.first < dii2.first)
                return -1;
            else if (dii1.first > dii2.first)
                return 1;
            if (dii1.second < dii2.second)
                return -1;
            else if (dii1.second > dii2.second)
                return 1;
            else
                return 0;
        }

        public boolean equals(Object obj1) {
            return compare(this, obj1) == 0;
        }
    }
}