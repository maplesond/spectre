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
package uk.ac.uea.cmp.phygen.superq.qnet;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.tgac.metaopt.Optimiser;
import uk.ac.tgac.metaopt.OptimiserException;
import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetSystem;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetSystemCombiner;
import uk.ac.uea.cmp.phygen.core.ds.quartet.WeightedQuartetMap;
import uk.ac.uea.cmp.phygen.core.ds.quartet.load.QLoader;
import uk.ac.uea.cmp.phygen.core.ds.split.CircularOrdering;
import uk.ac.uea.cmp.phygen.core.ds.split.CompatibleSplitSystem;
import uk.ac.uea.cmp.phygen.core.io.nexus.NexusWriter;
import uk.ac.uea.cmp.phygen.core.ui.gui.RunnableTool;
import uk.ac.uea.cmp.phygen.core.ui.gui.StatusTracker;
import uk.ac.uea.cmp.phygen.core.util.SpiFactory;
import uk.ac.uea.cmp.phygen.superq.qnet.holders.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * The QNet main class
 * <p/>
 * Presently runnable holder for Stefan Gr�newalds circular ordering-generating
 * algorithm
 */
public class QNet extends RunnableTool {

    private static Logger log = LoggerFactory.getLogger(QNet.class);

    private QNetOptions options;

    /**
     * Default constructor for CLI use
     */
    public QNet() {
    }

    /**
     * Constructor for use from the GUI
     * @param options
     * @param statusTracker
     */
    public QNet(QNetOptions options, StatusTracker statusTracker) {
        super(statusTracker);
        this.options = options;
    }

    /**
     * Runs QNet from input file, which might contain trees, distance matrices or quartet systems, converts the input
     * into a quartet network, normalises the quartet weights, then calculates the circular ordering and computes
     * edge weights.
     * @param input The file containing trees, distance matrices or quartet systems, will parse as appropriate based on
     *              the file extension.
     * @param logNormalise Whether to normalise the quartet weights by natural log, or not.
     * @param tolerance The tolerance to apply when computing weights
     * @param optimiser The optimiser to use when computing weights
     * @return The QNet results, which contains a quartet system, a set of computed weights and the quartet system
     * that was used to calculate these things.
     * @throws IOException Thrown if there was an issue loading the input file.
     * @throws QNetException Thrown if there were any unexpected issues with the QNET algorithm implementation
     * @throws OptimiserException Thrown if there was an issue running the optimiser when computing weights
     */
    public QNetResult execute(File input, boolean logNormalise, double tolerance, Optimiser optimiser)
            throws IOException, QNetException, OptimiserException {

        String ext = FilenameUtils.getExtension(input.getName());

        notifyUser("Loading quartet system from: " + input.getName());

        // Load the quartet network
        QuartetSystem quartetNetwork = new SpiFactory<>(QLoader.class).create(ext).load(input, 1.0).get(0);

        notifyUser("Normalising quartets " + (logNormalise ? " (using log)" : "") + ".");

        // Normalise the values in the network
        quartetNetwork.normaliseQuartets(logNormalise);

        // Create combiner (will only contain a single network!! TODO check this!)
        QuartetSystemCombiner combinedQuartetSystem = new QuartetSystemCombiner().combine(quartetNetwork);

        return this.execute(combinedQuartetSystem, tolerance, optimiser);
    }

    /**
     * Runs QNet from a combined quartet system.  Calculates the circular ordering and compute edge weights.
     * @param combinedQuartetSystem The combined split system to process
     * @param tolerance The tolerance to apply when computing weights
     * @param optimiser The optimiser to use when computing weights
     * @return The QNet results, which contains a quartet system derived from the combined quartet system that was input,
     * a set of computed weights and the quartet system that was used to calculate these things.
     * @throws QNetException Thrown if there were any unexpected issues with the QNET algorithm implementation
     * @throws OptimiserException Thrown if there was an issue running the optimiser when computing weights
     */
    public QNetResult execute(QuartetSystemCombiner combinedQuartetSystem, double tolerance, Optimiser optimiser)
            throws OptimiserException, QNetException {

        notifyUser("Computing circular ordering.");

        // Order the taxa
        CircularOrdering circularOrdering = this.computeCircularOrdering(combinedQuartetSystem);

        notifyUser("Computing weights");

        // Get the actual combined quartet system
        QuartetSystem quartetSystem = combinedQuartetSystem.create();

        // Compute the weights
        ComputedWeights solution = this.computedWeights(quartetSystem, tolerance, optimiser);

        return new QNetResult(circularOrdering, solution, quartetSystem);
    }


    /**
     * Computers edge weights from a quartet system
     * @param quartetSystem The quartet system
     * @param tolerance The tolerance to apply when computing weights
     * @param optimiser The optimiser to use when computing weights
     * @return A set of computer edge weights
     * @throws QNetException Thrown if there were any unexpected issues with the QNET algorithm implementation
     * @throws OptimiserException Thrown if there was an issue running the optimiser when computing weights
     */
    public ComputedWeights computedWeights(QuartetSystem quartetSystem, double tolerance, Optimiser optimiser)
            throws OptimiserException, QNetException {

        return WeightsComputeNNLSInformative.computeWeights(quartetSystem, tolerance, optimiser);
    }

    /**
     * Computes a circular ordering from a combination of quartet systems
     * @param combinedQuartetSystem The combined split system to process
     * @return A circular ordering
     */
    public CircularOrdering computeCircularOrdering(QuartetSystemCombiner combinedQuartetSystem) throws QNetException {

        Taxa allTaxa = combinedQuartetSystem.getTaxa();
        int N = allTaxa.size();
        List<Taxa> taxaSets = combinedQuartetSystem.getTranslatedTaxaSets();
        WeightedQuartetMap theQuartetWeights = combinedQuartetSystem.getQuartetWeights();

        double c = 0.5;

        log.debug("QNet: Beginning analysis... ");

        // begin algorithm as described by Stefan

        // initalization step
        int p = N;

        List<Integer> X = new ArrayList<>();

        for (int n = 0; n < N; n++) {

            // The taxa set X (prime)

            //X.add((taxaSets.get(n)).get(0).getId());

            X.add(allTaxa.get(n).getId());
        }

        // init all the holders

        ZHolder zH = new ZHolder(taxaSets, N);
        WHolder wH = new WHolder(taxaSets, N, theQuartetWeights);
        U0Holder u0H = new U0Holder(taxaSets, N, theQuartetWeights);
        U1Holder u1H = new U1Holder(taxaSets, N, theQuartetWeights);
        NSHolder snH = new NSHolder(taxaSets, N, theQuartetWeights);
        THolder tH = new THolder(taxaSets, N, theQuartetWeights);


        // Make shortcuts to directions
        Taxa.Direction FORWARD = Taxa.Direction.FORWARD;
        Taxa.Direction BACKWARD = Taxa.Direction.BACKWARD;


        // iteration step

        // DEBUG: output t

        log.debug("After initialisation step:");

        if (X.size() > 2) {

            for (int xI = 0; xI < X.size() - 1; xI++) {

                for (int xJ = xI + 1; xJ < X.size(); xJ++) {

                    int i = X.get(xI);
                    int j = X.get(xJ);

                    if (snH.getWeight(i, j) != 0.0) {

                        log.debug("n (" + i + ", " + j + "): " + snH.getCount(i, j));
                        log.debug("s (" + i + ", " + j + "): " + snH.getWeight(i, j));
                    }
                }

                for (int xJ = 0; xJ < X.size(); xJ++) {

                    for (int xK = 0; xK < X.size(); xK++) {

                        int i = X.get(xI);
                        int j = X.get(xJ);
                        int k = X.get(xK);

                        if (tH.getT(i, j, k) != 0.0 && tH.getT(i, j, k) != 0.0) {

                            log.debug("t (" + i + ", " + j + ", " + k + "): " + tH.getT(i, j, k));
                        }
                    }
                }
            }
        }

        while (p > 3) {

            // find a, b, a < b, in X so s (a, b) / n (a, b) maximal

            int aMax = -1;
            int bMax = -1;
            double qMax = Double.NEGATIVE_INFINITY;

            for (int xA = 0; xA < X.size() - 1; xA++) {

                for (int xB = xA + 1; xB < X.size(); xB++) {

                    int a = X.get(xA);
                    int b = X.get(xB);

                    double q = snH.calcWeightedCount(a, b);

                    if (q > qMax) {

                        qMax = q;
                        aMax = a;
                        bMax = b;
                    }
                }
            }

            int a = aMax;
            int b = bMax;

            // we have found which a, b in X' to join
            // this is equivalent to which lists to join

            // the four terms

            double yMax = Double.NEGATIVE_INFINITY;
            int y = -1;

            double tABK = 0;
            double tBAK = 0;
            double tAKB = 0;
            double tBKA = 0;

            for (int n = 0; n < X.size(); n++) {

                int k = X.get(n);

                if (k != a && k != b) {

                    tABK += tH.getT(a, b, k);
                    tBAK += tH.getT(b, a, k);
                    tAKB += tH.getT(a, k, b);
                    tBKA += tH.getT(b, k, a);
                }
            }

            double y1 = (zH.getZ(a) - 1) * tABK
                    + (zH.getZ(b) - 1) * tBAK
                    + (zH.getZ(b) - 1) * (zH.getZ(a) - 1) * c
                    * (N - zH.getZ(a) - zH.getZ(b)) * u0H.getWeight(a, b);
            double y2 = (zH.getZ(a) - 1) * tABK
                    + (zH.getZ(b) - 1) * tBKA
                    + (zH.getZ(b) - 1) * (zH.getZ(a) - 1) * c
                    * (N - zH.getZ(a) - zH.getZ(b)) * u1H.getWeight(a, b);
            double y3 = (zH.getZ(a) - 1) * tAKB
                    + (zH.getZ(b) - 1) * tBAK
                    + (zH.getZ(b) - 1) * (zH.getZ(a) - 1) * c
                    * (N - zH.getZ(a) - zH.getZ(b)) * u1H.getWeight(a, b);
            double y4 = (zH.getZ(a) - 1) * tAKB
                    + (zH.getZ(b) - 1) * tBKA
                    + (zH.getZ(b) - 1) * (zH.getZ(a) - 1) * c
                    * (N - zH.getZ(a) - zH.getZ(b)) * u0H.getWeight(a, b);

            log.debug("Deciding on direction to join by:\nsum t (a, b, k): " + tABK + "\nsum t (a, k, b): " + tAKB
                    + "\nsum t (b, a, k):" + tBAK + "\nsum t (b, k, a): " + tBKA + "\nu (a, b, 0): "
                    + u0H.getWeight(a, b) + "\nu (a, b, 1): " + u1H.getWeight(a, b) + "\ny1: " + y1 + " y2: " + y2 + " y3: " + y3 + " y4: " + y4);


            if (y1 > yMax) {

                y = 1;
                yMax = y1;
            }

            if (y2 > yMax) {

                y = 2;
                yMax = y2;
            }

            if (y3 > yMax) {

                y = 3;
                yMax = y3;
            }

            if (y4 > yMax) {

                y = 4;
                yMax = y4;
            }

            if (y < 0) {

                throw new QNetException("QNet: Error: y < 0 while ordering; please report this!");
            }



            // we now know which joining to perform

            switch(y) {
                case 1:
                    join(taxaSets, a, BACKWARD, b, FORWARD);
                    break;
                case 2:
                    join(taxaSets, a, BACKWARD, b, BACKWARD);
                    break;
                case 3:
                    join(taxaSets, a, FORWARD, b, FORWARD);
                    break;
                case 4:
                    join(taxaSets, a, FORWARD, b, BACKWARD);
                    break;
            }


            // remove b from X

            ListIterator<Integer> lI = X.listIterator();

            while (lI.hasNext()) {

                int i = lI.next();

                if (i == b) {

                    lI.remove();
                }
            }

            // loop over all elements k in X that are not a, b

            for (int xK = 0; xK < X.size(); xK++) {

                int k = X.get(xK);

                if (k != a && k != b) {

                    // modify s

                    double sAK = snH.getWeight(a, k);
                    double sBK = snH.getWeight(b, k);

                    double wSum = 0;

                    for (int xL = 0; xL < X.size(); xL++) {

                        int l = X.get(xL);

                        if (l != a && l != b && l != k) {

                            wSum += wH.getW(a, k, b, l) + wH.getW(b, k, a, l);
                        }
                    }

                    snH.setWeight(a, k, sAK + sBK - wSum);

                    // modify n

                    int nAK = snH.getCount(a, k);
                    int nBK = snH.getCount(b, k);

                    snH.setCount(a, k, nAK + nBK - 2
                            * (N - zH.getZ(a) - zH.getZ(b) - zH.getZ(k))
                            * zH.getZ(a) * zH.getZ(b) * zH.getZ(k));

                    for (int xL = 0; xL < X.size(); xL++) {

                        int l = X.get(xL);

                        // site of troublesome mod

                        if (l != a && l != b && l != k && l < k) {

                            double sKL = snH.getWeight(k, l);
                            int nKL = snH.getCount(k, l);

                            snH.setWeight(k, l, sKL - wH.getW(a, b, k, l));
                            snH.setCount(k, l, nKL - zH.getZ(a) * zH.getZ(b)
                                    * zH.getZ(k) * zH.getZ(l));

                            if (y == 1) {

                                double m1 = tH.getT(a, l, k) + tH.getT(b, k, l) + wH.getW(a, k, b, l);
                                double m2 = tH.getT(a, k, l) + tH.getT(b, l, k) + wH.getW(a, l, b, k);

                                tH.setT(a, l, k, m2);
                                tH.setT(a, k, l, m1);

                            } else if (y == 2) {

                                double m1 = tH.getT(a, l, k) + tH.getT(b, l, k) + wH.getW(a, k, b, l);
                                double m2 = tH.getT(a, k, l) + tH.getT(b, k, l) + wH.getW(a, l, b, k);

                                tH.setT(a, l, k, m2);
                                tH.setT(a, k, l, m1);

                            } else if (y == 3) {

                                double m1 = tH.getT(a, k, l) + tH.getT(b, k, l) + wH.getW(a, k, b, l);
                                double m2 = tH.getT(a, l, k) + tH.getT(b, l, k) + wH.getW(a, l, b, k);

                                tH.setT(a, l, k, m2);
                                tH.setT(a, k, l, m1);

                            } else if (y == 4) {

                                double m1 = tH.getT(a, k, l) + tH.getT(b, l, k) + wH.getW(a, k, b, l);
                                double m2 = tH.getT(a, l, k) + tH.getT(b, k, l) + wH.getW(a, l, b, k);

                                tH.setT(a, l, k, m2);
                                tH.setT(a, k, l, m1);

                            } else {

                                throw new QNetException("QNet: No y - weird; please report this!");
                            }

                            double m1 = tH.getT(k, a, l) + tH.getT(k, b, l);
                            double m2 = tH.getT(l, a, k) + tH.getT(l, b, k);
                            double m3 = tH.getT(k, l, a) + tH.getT(k, l, b);
                            double m4 = tH.getT(l, k, a) + tH.getT(l, k, b);

                            tH.setT(k, a, l, m1);
                            tH.setT(l, a, k, m2);
                            tH.setT(k, l, a, m3);
                            tH.setT(l, k, a, m4);

                            for (int xM = 0; xM < X.size(); xM++) {

                                int m = X.get(xM);

                                if (m != a && m != b && m != k && m != l && m < l) {

                                    m1 = wH.getW(a, k, l, m) + wH.getW(b, k, l, m);
                                    m2 = wH.getW(a, l, k, m) + wH.getW(b, l, k, m);
                                    m3 = wH.getW(a, m, l, k) + wH.getW(b, m, l, k);

                                    wH.setW(a, k, l, m, m1);
                                    wH.setW(a, l, k, m, m2);
                                    wH.setW(a, m, l, k, m3);

                                }
                            }
                        }
                    }

                    if (y == 1) {

                        double m1 = u1H.getWeight(a, k) + u0H.getWeight(b, k) + tH.getT(k, a, b);
                        double m2 = u0H.getWeight(a, k) + u1H.getWeight(b, k) + tH.getT(k, b, a);

                        u0H.setWeight(a, k, m1);
                        u1H.setWeight(a, k, m2);
                    }

                    if (y == 2) {

                        double m1 = u1H.getWeight(a, k) + u1H.getWeight(b, k) + tH.getT(k, a, b);
                        double m2 = u0H.getWeight(a, k) + u0H.getWeight(b, k) + tH.getT(k, b, a);

                        u0H.setWeight(a, k, m1);
                        u1H.setWeight(a, k, m2);
                    }

                    if (y == 3) {

                        double m1 = u0H.getWeight(a, k) + u0H.getWeight(b, k) + tH.getT(k, a, b);
                        double m2 = u1H.getWeight(a, k) + u1H.getWeight(b, k) + tH.getT(k, b, a);

                        u0H.setWeight(a, k, m1);
                        u1H.setWeight(a, k, m2);
                    }

                    if (y == 4) {

                        double m1 = u0H.getWeight(a, k) + u1H.getWeight(b, k) + tH.getT(k, a, b);
                        double m2 = u1H.getWeight(a, k) + u0H.getWeight(b, k) + tH.getT(k, b, a);

                        u0H.setWeight(a, k, m1);
                        u1H.setWeight(a, k, m2);
                    }
                }
            }

            zH.setZ(a, zH.getZ(a) + zH.getZ(b));
            zH.setZ(b, 0);

            p--;

            // end of iteration step

//          DEBUG: output t

            if (X.size() > 2) {

                log.debug("An iteration just took place. " + X.size() + " paths remaining. Non-zero parameters:");

                for (int xI = 0; xI < X.size() - 1; xI++) {

                    for (int xJ = xI + 1; xJ < X.size(); xJ++) {

                        int i = X.get(xI);
                        int j = X.get(xJ);

                        if (snH.getWeight(i, j) != 0.0) {

                            log.debug("n (" + i + ", " + j + "): " + snH.getCount(i, j));
                            log.debug("s (" + i + ", " + j + "): " + snH.getWeight(i, j));
                        }

                        if (u0H.getWeight(i, j) != 0.0) {

                            log.debug("u (" + i + ", " + j + ", 0): " + u0H.getWeight(i, j));
                        }

                        if (u1H.getWeight(i, j) != 0.0) {

                            log.debug("u (" + i + ", " + j + ", 1): " + u1H.getWeight(i, j));
                        }

                    }

                    for (int xJ = 0; xJ < X.size(); xJ++) {

                        for (int xK = 0; xK < X.size(); xK++) {

                            int i = X.get(xI);
                            int j = X.get(xJ);
                            int k = X.get(xK);

                            if (tH.getT(i, j, k) != 0.0) {

                                log.debug("t (" + i + ", " + j + ", " + k + "): " + tH.getT(i, j, k));
                            }
                        }
                    }
                }
            }
        }

        log.debug("commencing termination step... ");

        // termination step

        int i = X.get(0);
        int j = X.get(1);
        int k = X.get(2);

        // Determine which join to perform
        int y = this.selectTerminationJoin(i, j, k, c, tH, u0H, u1H);


        switch(y) {
            case 1:
                join(taxaSets, i, FORWARD, j, FORWARD, k, FORWARD);
                break;
            case 2:
                join(taxaSets, i, FORWARD, j, FORWARD, k, BACKWARD);
                break;
            case 3:
                join(taxaSets, i, FORWARD, j, BACKWARD, k, FORWARD);
                break;
            case 4:
                join(taxaSets, i, FORWARD, j, BACKWARD, k, BACKWARD);
                break;
            case 5:
                join(taxaSets, i, BACKWARD, j, FORWARD, k, FORWARD);
                break;
            case 6:
                join(taxaSets, i, BACKWARD, j, FORWARD, k, BACKWARD);
                break;
            case 7:
                join(taxaSets, i, BACKWARD, j, BACKWARD, k, FORWARD);
                break;
            case 8:
                join(taxaSets, i, BACKWARD, j, BACKWARD, k, BACKWARD);
                break;
        }
        // end algorithm as described by Stefan

        /**
         *
         * Now, the lists should contain the desired circular ordering
         *
         */
        log.debug("QNet done.");

        int[] ordering = new int[N];

        for (int n = 0; n < N; n++) {
            ordering[n] = allTaxa.get(n).getId() + 1;
        }

        return new CircularOrdering(ordering);
    }

    protected int selectTerminationJoin(int i, int k, int j, double c, THolder tH, U0Holder u0H, U1Holder u1H) {
        int y = -1;
        double yMax = Double.NEGATIVE_INFINITY;

        double y1 = tH.getT(i, k, j)
                + tH.getT(j, i, k)
                + tH.getT(k, j, i) + c * (u1H.getWeight(i, j)
                + u1H.getWeight(i, k)
                + u1H.getWeight(j, k));

        double y2 = tH.getT(i, k, j)
                + tH.getT(j, i, k)
                + tH.getT(k, i, j) + c * (u1H.getWeight(i, j)
                + u0H.getWeight(i, k)
                + u0H.getWeight(j, k));

        double y3 = tH.getT(i, k, j)
                + tH.getT(j, k, i)
                + tH.getT(k, j, i) + c * (u0H.getWeight(i, j)
                + u1H.getWeight(i, k)
                + u0H.getWeight(j, k));

        double y4 = tH.getT(i, k, j)
                + tH.getT(j, k, i)
                + tH.getT(k, i, j) + c * (u0H.getWeight(i, j)
                + u0H.getWeight(i, k)
                + u1H.getWeight(j, k));

        double y5 = tH.getT(i, j, k)
                + tH.getT(j, i, k)
                + tH.getT(k, j, i) + c * (u0H.getWeight(i, j)
                + u0H.getWeight(i, k)
                + u1H.getWeight(j, k));

        double y6 = tH.getT(i, j, k)
                + tH.getT(j, i, k)
                + tH.getT(k, i, j) + c * (u0H.getWeight(i, j)
                + u1H.getWeight(i, k)
                + u0H.getWeight(j, k));

        double y7 = tH.getT(i, j, k)
                + tH.getT(j, k, i)
                + tH.getT(k, j, i) + c * (u1H.getWeight(i, j)
                + u0H.getWeight(i, k)
                + u0H.getWeight(j, k));

        double y8 = tH.getT(i, j, k)
                + tH.getT(j, k, i)
                + tH.getT(k, i, j) + c * (u1H.getWeight(i, j)
                + u1H.getWeight(i, k)
                + u1H.getWeight(j, k));

        if (y1 > yMax) {

            y = 1;
            yMax = y1;
        }

        if (y2 > yMax) {

            y = 2;
            yMax = y2;
        }

        if (y3 > yMax) {

            y = 3;
            yMax = y3;
        }

        if (y4 > yMax) {

            y = 4;
            yMax = y4;
        }

        if (y5 > yMax) {

            y = 5;
            yMax = y5;
        }

        if (y6 > yMax) {

            y = 6;
            yMax = y6;
        }

        if (y7 > yMax) {

            y = 7;
            yMax = y7;
        }

        if (y8 > yMax) {

            y = 8;
            yMax = y8;
        }

        return y;
    }

    protected List<Taxa> join(List<Taxa> taxaSets, int taxon1, Taxa.Direction reversed1,
                           int taxon2, Taxa.Direction reversed2) {

        Taxa tL1 = new Taxa(), tL2 = new Taxa();

        for (int n = 0; n < taxaSets.size(); n++) {

            Taxa tL = taxaSets.get(n);

            if (tL.contains(taxon1)) {
                tL1 = tL;
            }

            if (tL.contains(taxon2)) {
                tL2 = tL;
            }
        }

        taxaSets.remove(tL1);
        taxaSets.remove(tL2);

        Taxa tL12 = Taxa.join(tL1, reversed1, tL2, reversed2);

        taxaSets.add(tL12);

        return taxaSets;

    }

    protected List<Taxa> join(List<Taxa> taxaSets, int taxon1, Taxa.Direction reversed1,
                           int taxon2, Taxa.Direction reversed2,
                           int taxon3, Taxa.Direction reversed3) {

        Taxa tL1 = new Taxa(), tL2 = new Taxa(), tL3 = new Taxa();

        for (Taxa tL : taxaSets) {

            if (tL.contains(taxon1)) {
                tL1 = tL;
            }

            if (tL.contains(taxon2)) {
                tL2 = tL;
            }

            if (tL.contains(taxon3)) {
                tL3 = tL;
            }
        }

        taxaSets.remove(tL1);
        taxaSets.remove(tL2);
        taxaSets.remove(tL3);

        Taxa tL123 = Taxa.join(Taxa.join(tL1, reversed1, tL2, reversed2), Taxa.Direction.FORWARD, tL3, reversed3);

        taxaSets.add(tL123);

        return taxaSets;
    }


    private void notifyUser(String message) {
        log.info(message);
        this.trackerInitUnknownRuntime(message);
    }

    public static void configureLogging() {
        // Setup logging
        File propsFile = new File("logging.properties");

        if (!propsFile.exists()) {
            BasicConfigurator.configure();
            log.info("No logging configuration found.  Using default logging properties.");
        } else {
            PropertyConfigurator.configure(propsFile.getPath());
            log.info("Found logging configuration: " + propsFile.getAbsoluteFile());
        }
    }


    protected void validateOptions() throws IOException {
        if (this.options == null) {
            throw new IOException("Must specify a valid set of parameters to control superQ.");
        }

        if (this.options.getInput() == null || !this.options.getInput().exists() || this.options.getInput().isDirectory()) {
            throw new IOException("Must specify a valid input file.");
        }

        if (this.options.getOutput() == null || this.options.getOutput().isDirectory()) {
            throw new IOException("Must specify a valid path where to create the output file.");
        }
    }

    @Override
    public void run() {

        try{

            // Check we have something sensible to work with
            validateOptions();

            // Get a shortcut to runtime object for checking memory usage
            Runtime rt = Runtime.getRuntime();

            // Start timing
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            log.info("Starting job: " + this.options.getInput().getPath());

            // Execute QNet
            QNetResult result = this.execute(this.options.getInput(), this.options.isLogNormalise(), this.options.getTolerance(), this.options.getOptimiser());

            notifyUser("QNet algorithm completed.  Saving results...");

            // Output results in nexus file in standard mode
            CompatibleSplitSystem ss = result.createSplitSystem(result.getComputedWeights().getX(), 0);

            new NexusWriter().writeSplitSystem(this.options.getOutput(), ss);



            this.trackerFinished(true);

            // Print run time on screen
            stopWatch.stop();
            log.info("Completed Successfully - Total run time: " + stopWatch.toString());


        } catch (Exception e) {
            log.error(e.getMessage(), e);
            this.setError(e);
            this.trackerFinished(false);
        } finally {
            this.notifyListener();
        }
    }
}