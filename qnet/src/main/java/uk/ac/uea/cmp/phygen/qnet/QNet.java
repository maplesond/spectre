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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetNetwork;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetNetworkAgglomerator;
import uk.ac.uea.cmp.phygen.core.ds.quartet.WeightedQuartetMap;
import uk.ac.uea.cmp.phygen.core.ds.quartet.load.QLoader;
import uk.ac.uea.cmp.phygen.core.ds.split.CircularOrdering;
import uk.ac.uea.cmp.phygen.core.math.optimise.Optimiser;
import uk.ac.uea.cmp.phygen.core.math.optimise.OptimiserException;
import uk.ac.uea.cmp.phygen.core.util.SpiFactory;
import uk.ac.uea.cmp.phygen.qnet.holders.*;

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
public class QNet {

    private static Logger log = LoggerFactory.getLogger(QNet.class);




    public ComputedWeights execute(File input, boolean logNormalise, double tolerance, Optimiser optimiser) throws IOException, QNetException, OptimiserException {

        String ext = FilenameUtils.getExtension(input.getName());

        // Load the quartet network
        QuartetNetwork quartetNetwork = new SpiFactory<>(QLoader.class).create(ext).load(input, 1.0).get(0);

        // Normalise the values in the network
        quartetNetwork.normaliseQuartets(logNormalise);

        // Create agglomerator (will only contain a single network!! TODO check this!)
        QuartetNetworkAgglomerator qnetAgglomerator = new QuartetNetworkAgglomerator();
        qnetAgglomerator.agglomerate(quartetNetwork);

        // Order the taxa
        this.computeCircularOrdering(qnetAgglomerator);

        ComputedWeights solution = this.computedWeights(quartetNetwork, tolerance, optimiser);

        return solution;
    }

    public ComputedWeights computedWeights(QuartetNetwork quartetNetworks, double tolerance, Optimiser optimiser)
            throws OptimiserException, QNetException, IOException {

        return WeightsComputeNNLSInformative.computeWeights(quartetNetworks, tolerance, optimiser);
    }

    /**
     * Computes a circular ordering from a quartet network agglomeration
     * @param quartetNetworkAgglomerator
     * @return A circular ordering
     */
    public CircularOrdering computeCircularOrdering(QuartetNetworkAgglomerator quartetNetworkAgglomerator) {

        Taxa allTaxa = quartetNetworkAgglomerator.getTaxa();
        int N = allTaxa.size();
        List<Taxa> taxaSets = quartetNetworkAgglomerator.getOriginalTaxaSets();
        WeightedQuartetMap theQuartetWeights = quartetNetworkAgglomerator.getQuartetWeights();

        double c = 0.5;

        log.debug("QNet: Beginning analysis... ");

        // begin algorithm as described by Stefan

        // initalization step
        int p = N;

        List<Integer> X = new ArrayList<>();

        for (int n = 0; n < N; n++) {

            // The taxa set X (prime)

            X.add((taxaSets.get(n)).get(0).getId());
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

        log.debug("After initiation step:");

        if (X.size() > 2) {

            for (int xI = 0; xI < X.size() - 1; xI++) {

                for (int xJ = xI + 1; xJ < X.size(); xJ++) {

                    int i = X.get(xI);
                    int j = X.get(xJ);

                    if (snH.getS(i, j) != 0.0) {

                        log.debug("n (" + i + ", " + j + "): " + snH.getN(i, j));
                        log.debug("s (" + i + ", " + j + "): " + snH.getS(i, j));
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

                    double q = snH.getS(a, b) / ((double) snH.getN(a, b));

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
                    * (N - zH.getZ(a) - zH.getZ(b)) * u0H.getU(a, b);
            double y2 = (zH.getZ(a) - 1) * tABK
                    + (zH.getZ(b) - 1) * tBKA
                    + (zH.getZ(b) - 1) * (zH.getZ(a) - 1) * c
                    * (N - zH.getZ(a) - zH.getZ(b)) * u1H.getU(a, b);
            double y3 = (zH.getZ(a) - 1) * tAKB
                    + (zH.getZ(b) - 1) * tBAK
                    + (zH.getZ(b) - 1) * (zH.getZ(a) - 1) * c
                    * (N - zH.getZ(a) - zH.getZ(b)) * u1H.getU(a, b);
            double y4 = (zH.getZ(a) - 1) * tAKB
                    + (zH.getZ(b) - 1) * tBKA
                    + (zH.getZ(b) - 1) * (zH.getZ(a) - 1) * c
                    * (N - zH.getZ(a) - zH.getZ(b)) * u0H.getU(a, b);

            log.debug("Deciding on direction to join by:\nsum t (a, b, k): " + tABK + "\nsum t (a, k, b): " + tAKB
                    + "\nsum t (b, a, k):" + tBAK + "\nsum t (b, k, a): " + tBKA + "\nu (a, b, 0): "
                    + u0H.getU(a, b) + "\nu (a, b, 1): " + u1H.getU(a, b) + "\ny1: " + y1 + " y2: " + y2 + " y3: " + y3 + " y4: " + y4);


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

                log.error("QNet: Error: y < 0 while ordering; please report this!");
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

                    double sAK = snH.getS(a, k);
                    double sBK = snH.getS(b, k);

                    double wSum = 0;

                    for (int xL = 0; xL < X.size(); xL++) {

                        int l = X.get(xL);

                        if (l != a && l != b && l != k) {

                            wSum += wH.getW(a, k, b, l) + wH.getW(b, k, a, l);
                        }
                    }

                    snH.setS(a, k, sAK + sBK - wSum);

                    // modify n

                    int nAK = snH.getN(a, k);
                    int nBK = snH.getN(b, k);

                    snH.setN(a, k, nAK + nBK - 2
                            * (N - zH.getZ(a) - zH.getZ(b) - zH.getZ(k))
                            * zH.getZ(a) * zH.getZ(b) * zH.getZ(k));

                    for (int xL = 0; xL < X.size(); xL++) {

                        int l = X.get(xL);

                        // site of troublesome mod

                        if (l != a && l != b && l != k && l < k) {

                            double sKL = snH.getS(k, l);
                            int nKL = snH.getN(k, l);

                            snH.setS(k, l, sKL - wH.getW(a, b, k, l));
                            snH.setN(k, l, nKL - zH.getZ(a) * zH.getZ(b)
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

                                log.error("QNet: No y - weird; please report this!");
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

                        double m1 = u1H.getU(a, k) + u0H.getU(b, k) + tH.getT(k, a, b);
                        double m2 = u0H.getU(a, k) + u1H.getU(b, k) + tH.getT(k, b, a);

                        u0H.setU(a, k, m1);
                        u1H.setU(a, k, m2);
                    }

                    if (y == 2) {

                        double m1 = u1H.getU(a, k) + u1H.getU(b, k) + tH.getT(k, a, b);
                        double m2 = u0H.getU(a, k) + u0H.getU(b, k) + tH.getT(k, b, a);

                        u0H.setU(a, k, m1);
                        u1H.setU(a, k, m2);
                    }

                    if (y == 3) {

                        double m1 = u0H.getU(a, k) + u0H.getU(b, k) + tH.getT(k, a, b);
                        double m2 = u1H.getU(a, k) + u1H.getU(b, k) + tH.getT(k, b, a);

                        u0H.setU(a, k, m1);
                        u1H.setU(a, k, m2);
                    }

                    if (y == 4) {

                        double m1 = u0H.getU(a, k) + u1H.getU(b, k) + tH.getT(k, a, b);
                        double m2 = u1H.getU(a, k) + u0H.getU(b, k) + tH.getT(k, b, a);

                        u0H.setU(a, k, m1);
                        u1H.setU(a, k, m2);
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

                        if (snH.getS(i, j) != 0.0) {

                            log.debug("n (" + i + ", " + j + "): " + snH.getN(i, j));
                            log.debug("s (" + i + ", " + j + "): " + snH.getS(i, j));
                        }

                        if (u0H.getU(i, j) != 0.0) {

                            log.debug("u (" + i + ", " + j + ", 0): " + u0H.getU(i, j));
                        }

                        if (u1H.getU(i, j) != 0.0) {

                            log.debug("u (" + i + ", " + j + ", 1): " + u1H.getU(i, j));
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

        int y = -1;
        double yMax = Double.NEGATIVE_INFINITY;

        double y1 = tH.getT(i, k, j)
                + tH.getT(j, i, k)
                + tH.getT(k, j, i) + c * (u1H.getU(i, j)
                + u1H.getU(i, k)
                + u1H.getU(j, k));

        double y2 = tH.getT(i, k, j)
                + tH.getT(j, i, k)
                + tH.getT(k, i, j) + c * (u1H.getU(i, j)
                + u0H.getU(i, k)
                + u0H.getU(j, k));

        double y3 = tH.getT(i, k, j)
                + tH.getT(j, k, i)
                + tH.getT(k, j, i) + c * (u0H.getU(i, j)
                + u1H.getU(i, k)
                + u0H.getU(j, k));

        double y4 = tH.getT(i, k, j)
                + tH.getT(j, k, i)
                + tH.getT(k, i, j) + c * (u0H.getU(i, j)
                + u0H.getU(i, k)
                + u1H.getU(j, k));

        double y5 = tH.getT(i, j, k)
                + tH.getT(j, i, k)
                + tH.getT(k, j, i) + c * (u0H.getU(i, j)
                + u0H.getU(i, k)
                + u1H.getU(j, k));

        double y6 = tH.getT(i, j, k)
                + tH.getT(j, i, k)
                + tH.getT(k, i, j) + c * (u0H.getU(i, j)
                + u1H.getU(i, k)
                + u0H.getU(j, k));

        double y7 = tH.getT(i, j, k)
                + tH.getT(j, k, i)
                + tH.getT(k, j, i) + c * (u1H.getU(i, j)
                + u0H.getU(i, k)
                + u0H.getU(j, k));

        double y8 = tH.getT(i, j, k)
                + tH.getT(j, k, i)
                + tH.getT(k, i, j) + c * (u1H.getU(i, j)
                + u1H.getU(i, k)
                + u1H.getU(j, k));

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


        // we now know which joining to perform

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

        Taxa rTL = taxaSets.get(0);

        int[] ordering = new int[N];

        for (int n = 0; n < N; n++) {
            ordering[n] = rTL.get(n).getId() - 1;
        }

        return new CircularOrdering(ordering);
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



    //mode tells the method whether we want to write
    //standard ... 0
    //minimum  ... 1
    //maximum  ... 2
    public void writeWeights(File output, double[] y, double[] x, int mode, QuartetNetwork quartetNetwork, CircularOrdering c) throws IOException {

        //Taxa c = null; //quartetNetworkAgglomerator.getTaxa().get(0);
        int N = quartetNetwork.getTaxa().size();
        WeightedQuartetMap theQuartetWeights = quartetNetwork.getQuartets();
        Taxa allTaxa = quartetNetwork.getTaxa();

        Pair<Integer, Integer>[] splitIndices = new ImmutablePair[N * (N - 1) / 2 - N];

        int n = 0;
        int m;

        for (m = 1; m < N - 1; m++) {

            for (int j = m + 2; j < N + 1; j++) {

                if (m != 1 || j != N) {

                    // valid split
                    splitIndices[n] = new ImmutablePair<>(m, j);
                    n++;
                }
            }
        }

        /*final int size = N * (N - 1) * (N - 2) * (N - 3) / 12;
        double[] f = new double[size];
        QuartetIndex[] quartetIndices = new QuartetIndex[size];

        n = 0;

        for (int i = 1; i < N - 2; i++) {

            for (int j = i + 1; j < N - 1; j++) {

                for (int k = j + 1; k < N; k++) {

                    for (int l = k + 1; l < N + 1; l++) {

                        int cI = c.get(i - 1).getId();
                        int cJ = c.get(j - 1).getId();
                        int cK = c.get(k - 1).getId();
                        int cL = c.get(l - 1).getId();

                        quartetIndices[n] = new QuartetIndex(i, j, k, l);
                        f[n] = theQuartetWeights.getWeight(new Quartet(cI, cJ, cK, cL));
                        n++;

                        quartetIndices[n] = new QuartetIndex(i, l, j, k);
                        f[n] = theQuartetWeights.getWeight(new Quartet(cI, cL, cJ, cK));
                        n++;
                    }
                }
            }
        }     */

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

            nexusString += allTaxa.get(i) + "\n";
        }

        nexusString += ";\nEND;\n\nBEGIN st_splits;\nDIMENSIONS ntax=" + N + " nsplits=" + (existingSplits + N) + ";\n";
        nexusString += "FORMAT\nlabels\nweights\n;\nPROPERTIES\nweakly compatible\ncyclic\n;\nCYCLE";

        for (int i = 0; i < N; i++) {

            nexusString += " " + c.getAt(i);
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

                    nexusString += " " + c.getAt(p - 1);
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

        // Write to file
        FileUtils.writeStringToFile(output, nexusString);
    }
}