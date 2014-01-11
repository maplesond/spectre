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
package uk.ac.uea.cmp.phygen.qtools.qnet;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.tgac.metaopt.*;
import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.quartet.GroupedQuartetSystem;
import uk.ac.uea.cmp.phygen.core.ds.quartet.Quartet;
import uk.ac.uea.cmp.phygen.core.ds.quartet.WeightedQuartetGroupMap;
import uk.ac.uea.cmp.phygen.core.ds.split.SplitUtils;
import uk.ac.uea.cmp.phygen.core.math.matrix.SymmetricMatrix;
import uk.ac.uea.cmp.phygen.qtools.qnet.holders.PHolder;

public class WeightsComputer {

    private static Logger log = LoggerFactory.getLogger(WeightsComputer.class);


    /**
     * Calculate the split weights for such splits as are relevant.  That is, we first go through all relevant splits
     * and create a list of split indices.  There are n choose 2 - n splits i.e. n(n-1)/2 - n.
     * @param quartetSystem
     * @param tolerance
     * @param optimiser
     * @return
     * @throws QNetException
     * @throws OptimiserException
     */
    public ComputedWeights computeWeights(GroupedQuartetSystem quartetSystem, double tolerance, Optimiser optimiser)
            throws QNetException, OptimiserException {

        // This method is probably going to take a while so start a timer.
        StopWatch stopWatchAll = new StopWatch();
        stopWatchAll.start();

        // For timing the problem creation part
        StopWatch stopWatchInit = new StopWatch();
        stopWatchInit.start();

        log.debug("Initialising NNLS problem to solve");

        Taxa taxa = quartetSystem.getTaxa();
        final int N = taxa.size();

        Pair<Integer, Integer>[] splitIndices = SplitUtils.createSplitIndices(taxa);

        // Initialise PHolder using the quartet system.
        PHolder pHolder = new PHolder(quartetSystem);

        // Initialise EtE using split indices and the pHolder
        SymmetricMatrix EtE = this.initEtE(N, splitIndices, pHolder);

        // Initialise EtF using splitIndices and the quartets
        double[] Etf = this.initEtf(splitIndices, quartetSystem);

        stopWatchInit.stop();
        log.debug("NNLS Problem initialised in " + stopWatchInit.toString());

        // For timing the problem creation part
        StopWatch stopWatchOptimise = new StopWatch();
        stopWatchOptimise.start();

        double[] solution = null;

        // Call of method to solve NNLS for split weights.  Either use an external solver if specified or use our internal NNLS implementation
        if (optimiser != null) {

            log.info("Using " + optimiser.getIdentifier() + " to solve NNLS problem");
            solution = new ExternalNNLSSolver().optimise(optimiser, Etf, EtE.toArray()).getVariableValues();
        }
        else {

            // Tolerance level!  If set to negative previously, use default
            if (tolerance < -0.0) {
                final double epsilon = 2.2204e-016;
                tolerance = 10.0 * Math.pow(((double) N), 8.0) / 48.0 / 64.0 * epsilon;
            }

            log.info("Using QNet's internal method to solve NNLS problem");
            solution = new InternalNNLSSolver().optimise(N, Etf, EtE, tolerance);
        }

        stopWatchOptimise.stop();
        log.debug("Time taken to solve NNLS problem: " + stopWatchOptimise.toString());

        log.info("Taxa weights: " + ArrayUtils.toString(solution));

        stopWatchAll.stop();
        log.info("Total time taken to compute weights: " + stopWatchAll.toString());

        return new ComputedWeights(solution, EtE);
    }



    private SymmetricMatrix initEtE(final int N, Pair<Integer, Integer>[] splitIndices, PHolder pHolder) {

        final int maxSplits = splitIndices.length;

        SymmetricMatrix EtE = new SymmetricMatrix(maxSplits);

        for (int i = 0; i < maxSplits; i++) {

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
                }
                else if (p2 > p1) {

                    // p2 is pP, q2 is qP

                    p = p1;
                    pP = p2;
                    q = q1;
                    qP = q2;
                }
                else {

                    if (q1 > q2) {

                        // p1 is pP, q1 is qP

                        p = p2;
                        pP = p1;
                        q = q2;
                        qP = q1;
                    }
                    else if (q2 > q1) {

                        // p2 is pP, q2 is qP

                        p = p1;
                        pP = p2;
                        q = q1;
                        qP = q2;
                    }
                    else {

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
                }
                else if (pP < q && q < qP) {

                    value = pHolder.getP(pP, q - 1, qP, N + p - 1) + pHolder.getP(p, pP - 1, q, qP - 1);
                }
                else if (q <= pP) {

                    value = pHolder.getP(p, q - 1, pP, qP - 1);
                }

                EtE.setElementAt(i, j, value);
            }
        }

        return EtE;
    }

    private double[][][][] populateGw(GroupedQuartetSystem quartetSystem) {

        WeightedQuartetGroupMap theQuartetWeights = quartetSystem.getQuartets();
        Taxa c = quartetSystem.getTaxa();
        final int N = c.size();

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

                            int cA = c.get(p).getId();
                            int cB = c.get(p + 1).getId();
                            int cC = c.get(i - 1).getId();
                            int cD = c.get(j - 1).getId();

                            double aW = theQuartetWeights.getWeight(new Quartet(cA, cB, cC, cD));

                            gw[p - 1][p + 1][i - 1][j - 1] = aW;
                        }
                        else if (l == 3) {

                            int cA = c.get(p).getId();
                            int cB = c.get(p + 2).getId();
                            int cC = c.get(i - 1).getId();
                            int cD = c.get(j - 1).getId();

                            double aW = theQuartetWeights.getWeight(new Quartet(cA, cB, cC, cD));

                            gw[p - 1][p + 2][i - 1][j - 1] = aW
                                    + gw[p - 1][p + 1][i - 1][j - 1] + gw[p][p + 2][i - 1][j - 1];
                        }
                        else {

                            int cA = c.get(p).getId();
                            int cB = c.get(p + l - 1).getId();
                            int cC = c.get(i - 1).getId();
                            int cD = c.get(j - 1).getId();

                            double aW = theQuartetWeights.getWeight(new Quartet(cA, cB, cC, cD));

                            gw[p - 1][p + l - 1][i - 1][j - 1] = aW
                                    + gw[p - 1][p + l - 2][i - 1][j - 1] + gw[p][p + l - 1][i - 1][j - 1]
                                    - gw[p][p + l - 2][i - 1][j - 1];
                        }
                    }

                    for (int j = p + 1 + l; j < N + 1; j++) {

                        if (l == 2) {

                            int cA = c.get(p).getId();
                            int cB = c.get(p + 1).getId();
                            int cC = c.get(i - 1).getId();
                            int cD = c.get(j - 1).getId();

                            double aW = theQuartetWeights.getWeight(new Quartet(cA, cB, cC, cD));

                            gw[p - 1][p + 1][i - 1][j - 1] = aW;
                        }
                        else if (l == 3) {

                            int cA = c.get(p).getId();
                            int cB = c.get(p + 2).getId();
                            int cC = c.get(i - 1).getId();
                            int cD = c.get(j - 1).getId();

                            double aW = theQuartetWeights.getWeight(new Quartet(cA, cB, cC, cD));

                            gw[p - 1][p + 2][i - 1][j - 1] = aW
                                    + gw[p - 1][p + 1][i - 1][j - 1] + gw[p][p + 2][i - 1][j - 1];
                        }
                        else {

                            int cA = c.get(p).getId();
                            int cB = c.get(p + l - 1).getId();
                            int cC = c.get(i - 1).getId();
                            int cD = c.get(j - 1).getId();

                            double aW = theQuartetWeights.getWeight(new Quartet(cA, cB, cC, cD));

                            gw[p - 1][p + l - 1][i - 1][j - 1] = aW
                                    + gw[p - 1][p + l - 2][i - 1][j - 1] + gw[p][p + l - 1][i - 1][j - 1]
                                    - gw[p][p + l - 2][i - 1][j - 1];
                        }
                    }
                }

                for (int i = p + l + 1; i < N + 1; i++) {

                    for (int j = i + 1; j < N + 1; j++) {

                        if (l == 2) {

                            int cA = c.get(p).getId();
                            int cB = c.get(p + 1).getId();
                            int cC = c.get(i - 1).getId();
                            int cD = c.get(j - 1).getId();

                            double aW = theQuartetWeights.getWeight(new Quartet(cA, cB, cC, cD));

                            gw[p - 1][p + 1][i - 1][j - 1] = aW;

                        } else if (l == 3) {

                            int cA = c.get(p).getId();
                            int cB = c.get(p + 2).getId();
                            int cC = c.get(i - 1).getId();
                            int cD = c.get(j - 1).getId();

                            double aW = theQuartetWeights.getWeight(new Quartet(cA, cB, cC, cD));

                            gw[p - 1][p + 2][i - 1][j - 1] = aW
                                    + gw[p - 1][p + 1][i - 1][j - 1] + gw[p][p + 2][i - 1][j - 1];

                        } else {

                            int cA = c.get(p).getId();
                            int cB = c.get(p + l - 1).getId();
                            int cC = c.get(i - 1).getId();
                            int cD = c.get(j - 1).getId();

                            double aW = theQuartetWeights.getWeight(new Quartet(cA, cB, cC, cD));

                            gw[p - 1][p + l - 1][i - 1][j - 1] = aW
                                    + gw[p - 1][p + l - 2][i - 1][j - 1] + gw[p][p + l - 1][i - 1][j - 1]
                                    - gw[p][p + l - 2][i - 1][j - 1];

                        }
                    }
                }
            }
        }

        return gw;
    }


    private double[] initEtf(Pair<Integer, Integer>[] splitIndices, GroupedQuartetSystem quartetSystem) {

        final int N = quartetSystem.getTaxa().size();
        final int maxSplits = SplitUtils.calcMaxSplits(N);

        if (splitIndices.length != maxSplits) {
            throw new IllegalArgumentException("Size of split indices and quartet system's taxa are different");
        }

        double[][][][] gw = this.populateGw(quartetSystem);

        double[] Etf = new double[maxSplits];

        for (int a = 0; a < maxSplits; a++) {

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

            Etf[a] = sum;
        }

        return Etf;
    }

}
