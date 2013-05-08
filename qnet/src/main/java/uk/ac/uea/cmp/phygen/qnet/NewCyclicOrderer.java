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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetWeights;
import uk.ac.uea.cmp.phygen.qnet.holders.*;

import java.util.ArrayList;
import java.util.ListIterator;

class NewCyclicOrderer {

    private static Logger log = LoggerFactory.getLogger(NewCyclicOrderer.class);

    /**
     *
     * Run method
     *
     */
    public static String order(QNet parent) {

        double c = 0.5;

        ArrayList theLists = parent.getTheLists();
        QuartetWeights theQuartetWeights = parent.getWeights();
        ArrayList taxonNames = parent.getTaxonNames();
        boolean useMax = parent.getUseMax();
        int N = parent.getN();

        /**
         *
         * Method for doing the actual analysis. Might return something other
         * than a String, though...
         *
         */
        //System.out.print ("QNet: Beginning analysis... ");
        // begin algorithm as described by Stefan
        // initalization step
        int p = N;

        ArrayList X = new ArrayList();

        for (int n = 0; n < N; n++) {

            // the taxa set X (prime)

            X.add(((TaxonList) theLists.get(n)).get(0));

        }

        // init all the uk.ac.uea.cmp.phygen.qnet.holders

        ZHolder zH = new ZHolder(theLists, N);
        WHolder wH = new WHolder(theLists, N, theQuartetWeights);
        U0Holder u0H = new U0Holder(theLists, N, theQuartetWeights);
        U1Holder u1H = new U1Holder(theLists, N, theQuartetWeights);
        NSHolder snH = new NSHolder(theLists, N, theQuartetWeights);
        THolder tH = new THolder(theLists, N, theQuartetWeights);

        // iteration step

        // DEBUG: output t

        log.debug("After initiation step:");

        if (X.size() > 2) {

            for (int xI = 0; xI < X.size() - 1; xI++) {

                for (int xJ = xI + 1; xJ < X.size(); xJ++) {

                    int i = ((Integer) X.get(xI)).intValue();
                    int j = ((Integer) X.get(xJ)).intValue();

                    if (snH.getS(i, j) != 0.0) {

                        log.debug("n (" + i + ", " + j + "): " + snH.getN(i, j));
                        log.debug("s (" + i + ", " + j + "): " + snH.getS(i, j));
                    }
                }

                for (int xJ = 0; xJ < X.size(); xJ++) {

                    for (int xK = 0; xK < X.size(); xK++) {

                        int i = ((Integer) X.get(xI)).intValue();
                        int j = ((Integer) X.get(xJ)).intValue();
                        int k = ((Integer) X.get(xK)).intValue();

                        if (tH.getT(i, j, k) != 0.0 && tH.getT(i, j, k) != 0.0) {

                            log.debug("t (" + i + ", " + j + ", " + k + "): " + tH.getT(i, j, k));
                        }
                    }
                }
            }
        }

        while (p > 3) {

            // find a, b, a < b, in X so s (a, b) / n (a, b) maximal

            int aMax = - 1;
            int bMax = - 1;
            double qMax = Double.NEGATIVE_INFINITY;

            for (int xA = 0; xA < X.size() - 1; xA++) {

                for (int xB = xA + 1; xB < X.size(); xB++) {

                    int a = ((Integer) X.get(xA)).intValue();
                    int b = ((Integer) X.get(xB)).intValue();

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
            int y = - 1;

            double tABK = 0;
            double tBAK = 0;
            double tAKB = 0;
            double tBKA = 0;

            for (int n = 0; n < X.size(); n++) {

                int k = ((Integer) X.get(n)).intValue();

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

            if (y == 1) {

                join(theLists, a, 1, b, 0, taxonNames);
            }

            if (y == 2) {

                join(theLists, a, 1, b, 1, taxonNames);
            }

            if (y == 3) {

                join(theLists, a, 0, b, 0, taxonNames);
            }

            if (y == 4) {

                join(theLists, a, 0, b, 1, taxonNames);
            }

            // remove b from X

            ListIterator lI = X.listIterator();

            while (lI.hasNext()) {

                int i = ((Integer) lI.next()).intValue();

                if (i == b) {

                    lI.remove();
                }
            }

            // loop over all elements k in X that are not a, b

            for (int xK = 0; xK < X.size(); xK++) {

                int k = ((Integer) X.get(xK)).intValue();

                if (k != a && k != b) {

                    // modify s

                    double sAK = snH.getS(a, k);
                    double sBK = snH.getS(b, k);

                    double wSum = 0;

                    for (int xL = 0; xL < X.size(); xL++) {

                        int l = ((Integer) X.get(xL)).intValue();

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

                        int l = ((Integer) X.get(xL)).intValue();

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

                                int m = ((Integer) X.get(xM)).intValue();

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

                        int i = ((Integer) X.get(xI)).intValue();
                        int j = ((Integer) X.get(xJ)).intValue();

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

                            int i = ((Integer) X.get(xI)).intValue();
                            int j = ((Integer) X.get(xJ)).intValue();
                            int k = ((Integer) X.get(xK)).intValue();

                            if (tH.getT(i, j, k) != 0.0) {

                                log.debug("t (" + i + ", " + j + ", " + k + "): " + tH.getT(i, j, k));
                            }
                        }
                    }
                }
            }
        }

        //System.out.print ("commencing termination step... ");

        // termination step

        int i = ((Integer) X.get(0)).intValue();
        int j = ((Integer) X.get(1)).intValue();
        int k = ((Integer) X.get(2)).intValue();

        int y = - 1;
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

        if (y == 1) {

            join(theLists, i, 0, j, 0, k, 0, taxonNames);

        }

        if (y == 2) {

            join(theLists, i, 0, j, 0, k, 1, taxonNames);

        }

        if (y == 3) {

            join(theLists, i, 0, j, 1, k, 0, taxonNames);

        }

        if (y == 4) {

            join(theLists, i, 0, j, 1, k, 1, taxonNames);

        }

        if (y == 5) {

            join(theLists, i, 1, j, 0, k, 0, taxonNames);

        }

        if (y == 6) {

            join(theLists, i, 1, j, 0, k, 1, taxonNames);

        }

        if (y == 7) {

            join(theLists, i, 1, j, 1, k, 0, taxonNames);

        }

        if (y == 8) {

            join(theLists, i, 1, j, 1, k, 1, taxonNames);

        }

        // end algorithm as described by Stefan

        /**
         *
         * Now, the lists should contain the desired circular ordering
         *
         */
        //System.out.println ("done.");
        String result = new String();

        TaxonList rTL = (TaxonList) theLists.get(0);

        for (int n = 0; n < N; n++) {

            result += (String) taxonNames.get(((Integer) rTL.get(n)).intValue() - 1) + " ";

        }

        return result;

    }

    static ArrayList join(ArrayList theLists, int taxon1, int reversed1,
                          int taxon2, int reversed2, ArrayList taxonNames) {

        TaxonList tL1 = new TaxonList(), tL2 = new TaxonList();

        for (int n = 0; n < theLists.size(); n++) {

            TaxonList tL = (TaxonList) theLists.get(n);

            if (tL.contains(taxon1)) {

                tL1 = tL;

            }

            if (tL.contains(taxon2)) {

                tL2 = tL;

            }

        }

        theLists.remove(tL1);
        theLists.remove(tL2);

        TaxonList tL12 = TaxonList.join(tL1, reversed1, tL2, reversed2);

        theLists.add(tL12);

        return theLists;

    }

    static ArrayList join(ArrayList theLists, int taxon1, int reversed1,
                          int taxon2, int reversed2,
                          int taxon3, int reversed3, ArrayList taxonNames) {

        TaxonList tL1 = new TaxonList(), tL2 = new TaxonList(), tL3 = new TaxonList();

        for (int n = 0; n < theLists.size(); n++) {

            TaxonList tL = (TaxonList) theLists.get(n);

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

        theLists.remove(tL1);
        theLists.remove(tL2);
        theLists.remove(tL3);

        TaxonList tL123 = TaxonList.join(TaxonList.join(tL1, reversed1, tL2, reversed2), 0, tL3, reversed3);

        theLists.add(tL123);

        return theLists;

    }
}