package uk.ac.uea.cmp.phygen.superq.qnet;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.Taxon;
import uk.ac.uea.cmp.phygen.core.ds.quartet.CanonicalWeightedQuartetMap;
import uk.ac.uea.cmp.phygen.core.ds.quartet.WeightedQuartetGroupMap;
import uk.ac.uea.cmp.phygen.core.ds.split.CircularOrdering;
import uk.ac.uea.cmp.phygen.core.util.CollectionUtils;
import uk.ac.uea.cmp.phygen.superq.qnet.holders.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dan on 08/01/14.
 */
public class CyclicOrderer {

    private static Logger log = LoggerFactory.getLogger(CyclicOrderer.class);

    private NSHolder nsH;
    private THolder tH;
    private ZHolder zH;
    private U0Holder u0H;
    private U1Holder u1H;
    private WHolder wH;

    private int N;

    private static final double c = 0.5;

    public CyclicOrderer() {
        nsH = null;
        tH = null;
        zH = null;
        u0H = null;
        u1H = null;
        wH = null;

        N = -1;
    }

    /**
     * Computes a circular ordering from a combination of quartet systems
     * @param taxa All of the taxa used in the merged quartet system
     * @param theQuartetWeights The quartet groups mapped to the normalised weights
     * @return A circular ordering
     * @throws QNetException if there were any problems.
     */
    public CircularOrdering computeCircularOrdering(Taxa taxa, WeightedQuartetGroupMap theQuartetWeights)
            throws QNetException {

        // This method is probably going to take a while so start a timer.
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();


        // Number of taxa in combined quartet system
        N = taxa.size();

        // Initalise paths
        List<Taxa> paths = this.initPaths(taxa);

        // Initialise the X', which is a
        List<Integer> X = this.initXPrime(taxa);


        // Convert grouped quartets to canonical quartets
        CanonicalWeightedQuartetMap canonicalWeightedQuartets = new CanonicalWeightedQuartetMap(theQuartetWeights);

        log.info("Initialising QNet Holders");

        // Init all the holders
        Holders holders = new Holders(paths, N, canonicalWeightedQuartets);
        nsH = holders.getNs();
        tH = holders.getT();
        zH = holders.getZ();
        u0H = holders.getU0();
        u1H = holders.getU1();
        wH = holders.getW();

        log.info("QNet Holders initialised");

        // Iterate N-3 times
        int nbIterations = N-3;

        log.info("Processing " + nbIterations + " iterations");
        for (int p = 1; p <= nbIterations; p++) {

            paths = this.iteration(X, paths);

            log.debug("Iteration " + p + " of " + nbIterations + " complete.");
        }

        log.info("Commencing QNet termination step");

        // termination step
        paths = this.terminationJoin(X.get(0), X.get(1), X.get(2), paths);

        // Now, the lists should contain the desired circular ordering, convert those into a circular ordering object
        CircularOrdering circularOrdering = new CircularOrdering(paths.get(0).getIds());

        log.info("QNet circular ordering computed: " + circularOrdering.toString());

        stopWatch.stop();
        log.info("Time taken to compute ordering: " + stopWatch.toString());

        return circularOrdering;
    }

    private List<Taxa> initPaths(Taxa taxa) {

        List<Taxa> paths = new ArrayList<>();

        for(Taxon taxon : taxa) {

            Taxa path = new Taxa();
            path.add(new Taxon(taxon));
            paths.add(path);
        }

        return paths;
    }

    private List<Integer> initXPrime(Taxa taxa) {

        List<Integer> X = new ArrayList<>();

        for (Taxon taxon : taxa) {

            // The taxa set X (prime)
            X.add(taxon.getId());
        }

        return X;
    }


    protected List<Taxa> iteration(List<Integer> X, List<Taxa> taxaSets) throws QNetException {

        // find a, b, a < b, in X so s (a, b) / n (a, b) maximal
        Pair<Integer, Integer> ab = this.findMaxAB(X);
        int a = ab.getLeft();
        int b = ab.getRight();

        int y = this.selectIterationJoin(a, b, c, X);

        if (y < 0) {

            throw new QNetException("Join type unknown!");
        }

        // Make shortcuts to directions
        Taxa.Direction FORWARD = Taxa.Direction.FORWARD;
        Taxa.Direction BACKWARD = Taxa.Direction.BACKWARD;


        // we now know which joining to perform

        switch(y) {
            case 1:
                join2(taxaSets, a, BACKWARD, b, FORWARD);
                break;
            case 2:
                join2(taxaSets, a, BACKWARD, b, BACKWARD);
                break;
            case 3:
                join2(taxaSets, a, FORWARD, b, FORWARD);
                break;
            case 4:
                join2(taxaSets, a, FORWARD, b, BACKWARD);
                break;
        }


        // remove b from X
        X.remove(new Integer(b));

        // loop over all elements k in X that are not a, b

        for (int xK = 0; xK < X.size(); xK++) {

            int k = X.get(xK);

            if (k != a && k != b) {

                // modify s

                double sAK = nsH.getWeight(a, k);
                double sBK = nsH.getWeight(b, k);

                double wSum = 0;

                for (int xL = 0; xL < X.size(); xL++) {

                    int l = X.get(xL);

                    if (l != a && l != b && l != k) {

                        wSum += wH.getW(a, k, b, l) + wH.getW(b, k, a, l);
                    }
                }

                nsH.setWeight(a, k, sAK + sBK - wSum);

                // modify n

                int nAK = nsH.getCount(a, k);
                int nBK = nsH.getCount(b, k);

                nsH.setCount(a, k, nAK + nBK - 2
                        * (N - zH.getZ(a) - zH.getZ(b) - zH.getZ(k))
                        * zH.getZ(a) * zH.getZ(b) * zH.getZ(k));

                for (int xL = 0; xL < X.size(); xL++) {

                    int l = X.get(xL);

                    // site of troublesome mod

                    if (l != a && l != b && l != k && l < k) {

                        double sKL = nsH.getWeight(k, l);
                        int nKL = nsH.getCount(k, l);

                        nsH.setWeight(k, l, sKL - wH.getW(a, b, k, l));
                        nsH.setCount(k, l, nKL - zH.getZ(a) * zH.getZ(b)
                                * zH.getZ(k) * zH.getZ(l));

                        if (y == 1) {

                            double m1 = tH.getWeight(a, l, k) + tH.getWeight(b, k, l) + wH.getW(a, k, b, l);
                            double m2 = tH.getWeight(a, k, l) + tH.getWeight(b, l, k) + wH.getW(a, l, b, k);

                            tH.setWeight(a, l, k, m2);
                            tH.setWeight(a, k, l, m1);

                        } else if (y == 2) {

                            double m1 = tH.getWeight(a, l, k) + tH.getWeight(b, l, k) + wH.getW(a, k, b, l);
                            double m2 = tH.getWeight(a, k, l) + tH.getWeight(b, k, l) + wH.getW(a, l, b, k);

                            tH.setWeight(a, l, k, m2);
                            tH.setWeight(a, k, l, m1);

                        } else if (y == 3) {

                            double m1 = tH.getWeight(a, k, l) + tH.getWeight(b, k, l) + wH.getW(a, k, b, l);
                            double m2 = tH.getWeight(a, l, k) + tH.getWeight(b, l, k) + wH.getW(a, l, b, k);

                            tH.setWeight(a, l, k, m2);
                            tH.setWeight(a, k, l, m1);

                        } else if (y == 4) {

                            double m1 = tH.getWeight(a, k, l) + tH.getWeight(b, l, k) + wH.getW(a, k, b, l);
                            double m2 = tH.getWeight(a, l, k) + tH.getWeight(b, k, l) + wH.getW(a, l, b, k);

                            tH.setWeight(a, l, k, m2);
                            tH.setWeight(a, k, l, m1);

                        } else {

                            throw new QNetException("QNet: No y - weird; please report this!");
                        }

                        double m1 = tH.getWeight(k, a, l) + tH.getWeight(k, b, l);
                        double m2 = tH.getWeight(l, a, k) + tH.getWeight(l, b, k);
                        double m3 = tH.getWeight(k, l, a) + tH.getWeight(k, l, b);
                        double m4 = tH.getWeight(l, k, a) + tH.getWeight(l, k, b);

                        tH.setWeight(k, a, l, m1);
                        tH.setWeight(l, a, k, m2);
                        tH.setWeight(k, l, a, m3);
                        tH.setWeight(l, k, a, m4);

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

                    double m1 = u1H.getWeight(a, k) + u0H.getWeight(b, k) + tH.getWeight(k, a, b);
                    double m2 = u0H.getWeight(a, k) + u1H.getWeight(b, k) + tH.getWeight(k, b, a);

                    u0H.setWeight(a, k, m1);
                    u1H.setWeight(a, k, m2);
                }

                if (y == 2) {

                    double m1 = u1H.getWeight(a, k) + u1H.getWeight(b, k) + tH.getWeight(k, a, b);
                    double m2 = u0H.getWeight(a, k) + u0H.getWeight(b, k) + tH.getWeight(k, b, a);

                    u0H.setWeight(a, k, m1);
                    u1H.setWeight(a, k, m2);
                }

                if (y == 3) {

                    double m1 = u0H.getWeight(a, k) + u0H.getWeight(b, k) + tH.getWeight(k, a, b);
                    double m2 = u1H.getWeight(a, k) + u1H.getWeight(b, k) + tH.getWeight(k, b, a);

                    u0H.setWeight(a, k, m1);
                    u1H.setWeight(a, k, m2);
                }

                if (y == 4) {

                    double m1 = u0H.getWeight(a, k) + u1H.getWeight(b, k) + tH.getWeight(k, a, b);
                    double m2 = u1H.getWeight(a, k) + u0H.getWeight(b, k) + tH.getWeight(k, b, a);

                    u0H.setWeight(a, k, m1);
                    u1H.setWeight(a, k, m2);
                }
            }
        }

        zH.setZ(a, zH.getZ(a) + zH.getZ(b));
        zH.setZ(b, 0);


        return taxaSets;
    }



    protected List<Taxa> terminationJoin(int i, int k, int j, List<Taxa> taxaSets) throws QNetException {

        double[] y8 = new double[8];

        y8[0] = tH.getWeight(i, k, j)
                + tH.getWeight(j, i, k)
                + tH.getWeight(k, j, i) + c * (u1H.getWeight(i, j)
                + u1H.getWeight(i, k)
                + u1H.getWeight(j, k));

        y8[1] = tH.getWeight(i, k, j)
                + tH.getWeight(j, i, k)
                + tH.getWeight(k, i, j) + c * (u1H.getWeight(i, j)
                + u0H.getWeight(i, k)
                + u0H.getWeight(j, k));

        y8[2] = tH.getWeight(i, k, j)
                + tH.getWeight(j, k, i)
                + tH.getWeight(k, j, i) + c * (u0H.getWeight(i, j)
                + u1H.getWeight(i, k)
                + u0H.getWeight(j, k));

        y8[3] = tH.getWeight(i, k, j)
                + tH.getWeight(j, k, i)
                + tH.getWeight(k, i, j) + c * (u0H.getWeight(i, j)
                + u0H.getWeight(i, k)
                + u1H.getWeight(j, k));

        y8[4] = tH.getWeight(i, j, k)
                + tH.getWeight(j, i, k)
                + tH.getWeight(k, j, i) + c * (u0H.getWeight(i, j)
                + u0H.getWeight(i, k)
                + u1H.getWeight(j, k));

        y8[5] = tH.getWeight(i, j, k)
                + tH.getWeight(j, i, k)
                + tH.getWeight(k, i, j) + c * (u0H.getWeight(i, j)
                + u1H.getWeight(i, k)
                + u0H.getWeight(j, k));

        y8[6] = tH.getWeight(i, j, k)
                + tH.getWeight(j, k, i)
                + tH.getWeight(k, j, i) + c * (u1H.getWeight(i, j)
                + u0H.getWeight(i, k)
                + u0H.getWeight(j, k));

        y8[7] = tH.getWeight(i, j, k)
                + tH.getWeight(j, k, i)
                + tH.getWeight(k, i, j) + c * (u1H.getWeight(i, j)
                + u1H.getWeight(i, k)
                + u1H.getWeight(j, k));

        int y = CollectionUtils.findIndexOfMax(y8) + 1;

        // Make shortcuts to directions
        Taxa.Direction FORWARD = Taxa.Direction.FORWARD;
        Taxa.Direction BACKWARD = Taxa.Direction.BACKWARD;


        switch(y) {
            case 1:
                join3(taxaSets, i, FORWARD, j, FORWARD, k, FORWARD);
                break;
            case 2:
                join3(taxaSets, i, FORWARD, j, FORWARD, k, BACKWARD);
                break;
            case 3:
                join3(taxaSets, i, FORWARD, j, BACKWARD, k, FORWARD);
                break;
            case 4:
                join3(taxaSets, i, FORWARD, j, BACKWARD, k, BACKWARD);
                break;
            case 5:
                join3(taxaSets, i, BACKWARD, j, FORWARD, k, FORWARD);
                break;
            case 6:
                join3(taxaSets, i, BACKWARD, j, FORWARD, k, BACKWARD);
                break;
            case 7:
                join3(taxaSets, i, BACKWARD, j, BACKWARD, k, FORWARD);
                break;
            case 8:
                join3(taxaSets, i, BACKWARD, j, BACKWARD, k, BACKWARD);
                break;
        }

        return taxaSets;
    }

    protected List<Taxa> join2(List<Taxa> paths, int taxon1, Taxa.Direction reversed1,
                               int taxon2, Taxa.Direction reversed2) throws QNetException {

        Taxa tL1 = null, tL2 = null;

        for (Taxa path : paths) {

            if (path.containsId(taxon1)) {
                tL1 = path;
            }

            if (path.containsId(taxon2)) {
                tL2 = path;
            }
        }

        if (tL1 == null || tL2 == null) {
            throw new QNetException("Couldn't find specified taxon ids in any path.  Taxon 1: " + taxon1 + "; Taxon 2: " + taxon2);
        }

        paths.remove(tL1);
        paths.remove(tL2);

        Taxa tL12 = Taxa.join(tL1, reversed1, tL2, reversed2);

        paths.add(tL12);

        return paths;

    }

    protected List<Taxa> join3(List<Taxa> paths, int taxon1, Taxa.Direction reversed1,
                               int taxon2, Taxa.Direction reversed2,
                               int taxon3, Taxa.Direction reversed3) throws QNetException {

        Taxa tL1 = null, tL2 = null, tL3 = null;

        for (Taxa tL : paths) {

            if (tL.containsId(taxon1)) {
                tL1 = tL;
            }

            if (tL.containsId(taxon2)) {
                tL2 = tL;
            }

            if (tL.containsId(taxon3)) {
                tL3 = tL;
            }
        }

        if (tL1 == null || tL2 == null || tL3 == null) {
            throw new QNetException("Couldn't find specified taxon ids in any path.  Taxon 1: " + taxon1 + "; Taxon 2: " + taxon2 + "; Taxon 3: " + taxon3);
        }

        paths.remove(tL1);
        paths.remove(tL2);
        paths.remove(tL3);

        Taxa tL123 = Taxa.join(Taxa.join(tL1, reversed1, tL2, reversed2), Taxa.Direction.FORWARD, tL3, reversed3);

        paths.add(tL123);

        return paths;
    }

    /**
     * Find a, b, a < b, in X so s (a, b) / n (a, b) maximal
     * @param X
     * @return
     */
    protected Pair<Integer, Integer> findMaxAB(List<Integer> X) {

        int aMax = -1;
        int bMax = -1;
        double qMax = Double.NEGATIVE_INFINITY;

        for (int xA = 0; xA < X.size() - 1; xA++) {

            for (int xB = xA + 1; xB < X.size(); xB++) {

                int a = X.get(xA);
                int b = X.get(xB);

                double q = nsH.calcWeightedCount(a, b);

                if (q > qMax) {

                    qMax = q;
                    aMax = a;
                    bMax = b;
                }
            }
        }

        return new ImmutablePair<>(aMax, bMax);
    }

    private int selectIterationJoin(int a, int b, double c, List<Integer> X) {

        // we have found which a, b in X' to join
        // this is equivalent to which lists to join

        double tABK = 0.0;
        double tBAK = 0.0;
        double tAKB = 0.0;
        double tBKA = 0.0;

        for (int n = 0; n < X.size(); n++) {

            int k = X.get(n);

            if (k != a && k != b) {

                tABK += tH.getWeight(a, b, k);
                tBAK += tH.getWeight(b, a, k);
                tAKB += tH.getWeight(a, k, b);
                tBKA += tH.getWeight(b, k, a);
            }
        }

        double[] y4 = new double[4];

        y4[0] = (zH.getZ(a) - 1) * tABK
                + (zH.getZ(b) - 1) * tBAK
                + (zH.getZ(b) - 1) * (zH.getZ(a) - 1) * c
                * (N - zH.getZ(a) - zH.getZ(b)) * u0H.getWeight(a, b);
        y4[1] = (zH.getZ(a) - 1) * tABK
                + (zH.getZ(b) - 1) * tBKA
                + (zH.getZ(b) - 1) * (zH.getZ(a) - 1) * c
                * (N - zH.getZ(a) - zH.getZ(b)) * u1H.getWeight(a, b);
        y4[2] = (zH.getZ(a) - 1) * tAKB
                + (zH.getZ(b) - 1) * tBAK
                + (zH.getZ(b) - 1) * (zH.getZ(a) - 1) * c
                * (N - zH.getZ(a) - zH.getZ(b)) * u1H.getWeight(a, b);
        y4[3] = (zH.getZ(a) - 1) * tAKB
                + (zH.getZ(b) - 1) * tBKA
                + (zH.getZ(b) - 1) * (zH.getZ(a) - 1) * c
                * (N - zH.getZ(a) - zH.getZ(b)) * u0H.getWeight(a, b);

        return CollectionUtils.findIndexOfMax(y4) + 1;
    }

}
