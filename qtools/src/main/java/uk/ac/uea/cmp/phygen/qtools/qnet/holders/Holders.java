package uk.ac.uea.cmp.phygen.qtools.qnet.holders;

import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.quartet.CanonicalWeightedQuartetMap;
import uk.ac.uea.cmp.phygen.core.util.CollectionUtils;
import uk.ac.uea.cmp.phygen.qtools.qnet.QNetException;

import java.util.List;

/**
 * Created by dan on 05/01/14.
 */
public class Holders {

    protected ZHolder zHolder;
    protected WHolder wHolder;
    protected U0Holder u0Holder;
    protected U1Holder u1Holder;
    protected NSHolder nsHolder;
    protected THolder tHolder;

    private int N;

    /**
     * Initialises all QNet Holders, using the supplied paths and quartets
     * @param initalPaths The initial paths
     * @param N The number of quartets
     * @param canonicalWeightedQuartets the canonical weighted quartets
     * @throws QNetException Thrown if there are any inconsistencies in the holder initialisation
     */
    public Holders(List<Taxa> initalPaths, int N, CanonicalWeightedQuartetMap canonicalWeightedQuartets) throws QNetException {
        zHolder = new ZHolder(initalPaths, N);
        wHolder = new WHolder(initalPaths, N, canonicalWeightedQuartets);
        u0Holder = new U0Holder(initalPaths, N, canonicalWeightedQuartets);
        u1Holder = new U1Holder(initalPaths, N, canonicalWeightedQuartets);
        nsHolder = new NSHolder(initalPaths, N, canonicalWeightedQuartets);
        tHolder = new THolder(initalPaths, N, canonicalWeightedQuartets);

        this.N = N;
    }

    public ZHolder getzHolder() {
        return zHolder;
    }

    public WHolder getwHolder() {
        return wHolder;
    }

    public U0Holder getU0Holder() {
        return u0Holder;
    }

    public U1Holder getU1Holder() {
        return u1Holder;
    }

    public NSHolder getNsHolder() {
        return nsHolder;
    }

    public THolder gettHolder() {
        return tHolder;
    }


    public int selectJoin2(int a, int b, double c, List<Integer> X) {

        // we have found which a, b in X' to join
        // this is equivalent to which lists to join

        double tABK = 0.0;
        double tBAK = 0.0;
        double tAKB = 0.0;
        double tBKA = 0.0;

        for (int n = 0; n < X.size(); n++) {

            int k = X.get(n);

            if (k != a && k != b) {

                tABK += tHolder.getWeight(a, b, k);
                tBAK += tHolder.getWeight(b, a, k);
                tAKB += tHolder.getWeight(a, k, b);
                tBKA += tHolder.getWeight(b, k, a);
            }
        }

        double[] y4 = new double[4];

        final int aZ = zHolder.getZ(a) - 1;
        final int bZ = zHolder.getZ(b) - 1;
        final double scale = bZ * aZ * c * (N - zHolder.getZ(a) - zHolder.getZ(b));

        y4[0] = aZ * tABK + bZ * tBAK + scale * u0Holder.getWeight(a, b);
        y4[1] = aZ * tABK + bZ * tBKA + scale * u1Holder.getWeight(a, b);
        y4[2] = aZ * tAKB + bZ * tBAK + scale * u1Holder.getWeight(a, b);
        y4[3] = aZ * tAKB + bZ * tBKA + scale * u0Holder.getWeight(a, b);

        return CollectionUtils.findIndexOfMax(y4) + 1;
    }



    public int selectJoin3(int i, int j, int k, double c) {

        double[] y8 = new double[8];

        y8[0] = tHolder.getWeight(i, k, j)
                + tHolder.getWeight(j, i, k)
                + tHolder.getWeight(k, j, i) + c * (u1Holder.getWeight(i, j)
                + u1Holder.getWeight(i, k)
                + u1Holder.getWeight(j, k));

        y8[1] = tHolder.getWeight(i, k, j)
                + tHolder.getWeight(j, i, k)
                + tHolder.getWeight(k, i, j) + c * (u1Holder.getWeight(i, j)
                + u0Holder.getWeight(i, k)
                + u0Holder.getWeight(j, k));

        y8[2] = tHolder.getWeight(i, k, j)
                + tHolder.getWeight(j, k, i)
                + tHolder.getWeight(k, j, i) + c * (u0Holder.getWeight(i, j)
                + u1Holder.getWeight(i, k)
                + u0Holder.getWeight(j, k));

        y8[3] = tHolder.getWeight(i, k, j)
                + tHolder.getWeight(j, k, i)
                + tHolder.getWeight(k, i, j) + c * (u0Holder.getWeight(i, j)
                + u0Holder.getWeight(i, k)
                + u1Holder.getWeight(j, k));

        y8[4] = tHolder.getWeight(i, j, k)
                + tHolder.getWeight(j, i, k)
                + tHolder.getWeight(k, j, i) + c * (u0Holder.getWeight(i, j)
                + u0Holder.getWeight(i, k)
                + u1Holder.getWeight(j, k));

        y8[5] = tHolder.getWeight(i, j, k)
                + tHolder.getWeight(j, i, k)
                + tHolder.getWeight(k, i, j) + c * (u0Holder.getWeight(i, j)
                + u1Holder.getWeight(i, k)
                + u0Holder.getWeight(j, k));

        y8[6] = tHolder.getWeight(i, j, k)
                + tHolder.getWeight(j, k, i)
                + tHolder.getWeight(k, j, i) + c * (u1Holder.getWeight(i, j)
                + u0Holder.getWeight(i, k)
                + u0Holder.getWeight(j, k));

        y8[7] = tHolder.getWeight(i, j, k)
                + tHolder.getWeight(j, k, i)
                + tHolder.getWeight(k, i, j) + c * (u1Holder.getWeight(i, j)
                + u1Holder.getWeight(i, k)
                + u1Holder.getWeight(j, k));

        return CollectionUtils.findIndexOfMax(y8) + 1;
    }
    

    public void update(int a, int b, int y, List<Integer> X) throws QNetException {

        for (int xK = 0; xK < X.size(); xK++) {

            int k = X.get(xK);

            if (k != a && k != b) {

                // modify s

                double sAK = nsHolder.getWeight(a, k);
                double sBK = nsHolder.getWeight(b, k);

                double wSum = 0;

                for (int xL = 0; xL < X.size(); xL++) {

                    int l = X.get(xL);

                    if (l != a && l != b && l != k) {

                        wSum += wHolder.getW(a, k, b, l) + wHolder.getW(b, k, a, l);
                    }
                }

                nsHolder.setWeight(a, k, sAK + sBK - wSum);

                // modify n

                int nAK = nsHolder.getCount(a, k);
                int nBK = nsHolder.getCount(b, k);

                nsHolder.setCount(a, k, nAK + nBK - 2
                        * (N - zHolder.getZ(a) - zHolder.getZ(b) - zHolder.getZ(k))
                        * zHolder.getZ(a) * zHolder.getZ(b) * zHolder.getZ(k));

                for (int xL = 0; xL < X.size(); xL++) {

                    int l = X.get(xL);

                    // site of troublesome mod

                    if (l != a && l != b && l != k && l < k) {

                        double sKL = nsHolder.getWeight(k, l);
                        int nKL = nsHolder.getCount(k, l);

                        nsHolder.setWeight(k, l, sKL - wHolder.getW(a, b, k, l));
                        nsHolder.setCount(k, l, nKL - zHolder.getZ(a) * zHolder.getZ(b)
                                * zHolder.getZ(k) * zHolder.getZ(l));

                        if (y == 1) {

                            double m1 = tHolder.getWeight(a, l, k) + tHolder.getWeight(b, k, l) + wHolder.getW(a, k, b, l);
                            double m2 = tHolder.getWeight(a, k, l) + tHolder.getWeight(b, l, k) + wHolder.getW(a, l, b, k);

                            tHolder.setWeight(a, l, k, m2);
                            tHolder.setWeight(a, k, l, m1);

                        } else if (y == 2) {

                            double m1 = tHolder.getWeight(a, l, k) + tHolder.getWeight(b, l, k) + wHolder.getW(a, k, b, l);
                            double m2 = tHolder.getWeight(a, k, l) + tHolder.getWeight(b, k, l) + wHolder.getW(a, l, b, k);

                            tHolder.setWeight(a, l, k, m2);
                            tHolder.setWeight(a, k, l, m1);

                        } else if (y == 3) {

                            double m1 = tHolder.getWeight(a, k, l) + tHolder.getWeight(b, k, l) + wHolder.getW(a, k, b, l);
                            double m2 = tHolder.getWeight(a, l, k) + tHolder.getWeight(b, l, k) + wHolder.getW(a, l, b, k);

                            tHolder.setWeight(a, l, k, m2);
                            tHolder.setWeight(a, k, l, m1);

                        } else if (y == 4) {

                            double m1 = tHolder.getWeight(a, k, l) + tHolder.getWeight(b, l, k) + wHolder.getW(a, k, b, l);
                            double m2 = tHolder.getWeight(a, l, k) + tHolder.getWeight(b, k, l) + wHolder.getW(a, l, b, k);

                            tHolder.setWeight(a, l, k, m2);
                            tHolder.setWeight(a, k, l, m1);

                        } else {

                            throw new QNetException("QNet: No y - weird; please report this!");
                        }

                        double m1 = tHolder.getWeight(k, a, l) + tHolder.getWeight(k, b, l);
                        double m2 = tHolder.getWeight(l, a, k) + tHolder.getWeight(l, b, k);
                        double m3 = tHolder.getWeight(k, l, a) + tHolder.getWeight(k, l, b);
                        double m4 = tHolder.getWeight(l, k, a) + tHolder.getWeight(l, k, b);

                        tHolder.setWeight(k, a, l, m1);
                        tHolder.setWeight(l, a, k, m2);
                        tHolder.setWeight(k, l, a, m3);
                        tHolder.setWeight(l, k, a, m4);

                        for (int xM = 0; xM < X.size(); xM++) {

                            int m = X.get(xM);

                            if (m != a && m != b && m != k && m != l && m < l) {

                                m1 = wHolder.getW(a, k, l, m) + wHolder.getW(b, k, l, m);
                                m2 = wHolder.getW(a, l, k, m) + wHolder.getW(b, l, k, m);
                                m3 = wHolder.getW(a, m, l, k) + wHolder.getW(b, m, l, k);

                                wHolder.setW(a, k, l, m, m1);
                                wHolder.setW(a, l, k, m, m2);
                                wHolder.setW(a, m, l, k, m3);

                            }
                        }
                    }
                }

                if (y == 1) {

                    double m1 = u1Holder.getWeight(a, k) + u0Holder.getWeight(b, k) + tHolder.getWeight(k, a, b);
                    double m2 = u0Holder.getWeight(a, k) + u1Holder.getWeight(b, k) + tHolder.getWeight(k, b, a);

                    u0Holder.setWeight(a, k, m1);
                    u1Holder.setWeight(a, k, m2);
                }

                if (y == 2) {

                    double m1 = u1Holder.getWeight(a, k) + u1Holder.getWeight(b, k) + tHolder.getWeight(k, a, b);
                    double m2 = u0Holder.getWeight(a, k) + u0Holder.getWeight(b, k) + tHolder.getWeight(k, b, a);

                    u0Holder.setWeight(a, k, m1);
                    u1Holder.setWeight(a, k, m2);
                }

                if (y == 3) {

                    double m1 = u0Holder.getWeight(a, k) + u0Holder.getWeight(b, k) + tHolder.getWeight(k, a, b);
                    double m2 = u1Holder.getWeight(a, k) + u1Holder.getWeight(b, k) + tHolder.getWeight(k, b, a);

                    u0Holder.setWeight(a, k, m1);
                    u1Holder.setWeight(a, k, m2);
                }

                if (y == 4) {

                    double m1 = u0Holder.getWeight(a, k) + u1Holder.getWeight(b, k) + tHolder.getWeight(k, a, b);
                    double m2 = u1Holder.getWeight(a, k) + u0Holder.getWeight(b, k) + tHolder.getWeight(k, b, a);

                    u0Holder.setWeight(a, k, m1);
                    u1Holder.setWeight(a, k, m2);
                }
            }
        }

        zHolder.setZ(a, zHolder.getZ(a) + zHolder.getZ(b));
        zHolder.setZ(b, 0);
    }

    /**
     * Finds the first path that contains the specified ID
     * @param paths The list of paths to search
     * @param id The id of the taxa to find
     * @return The first path found containing the specified taxa ID, or null.
     */
    public static Taxa findFirstPathContainingId(List<Taxa> paths, int id) {

        for (Taxa path : paths) {

            if (path.containsId(id)) {

                return path;
            }
        }

        return null;
    }
}
