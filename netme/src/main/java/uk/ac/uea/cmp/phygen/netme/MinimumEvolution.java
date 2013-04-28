package uk.ac.uea.cmp.phygen.netme;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.ds.Distances;
import uk.ac.uea.cmp.phygen.core.ds.split.CircularSplitSystem;
import uk.ac.uea.cmp.phygen.core.ds.split.SplitSystem;
import uk.ac.uea.cmp.phygen.core.ds.Tableau;
import uk.ac.uea.cmp.phygen.core.ds.TreeWeights;
import uk.ac.uea.cmp.phygen.core.math.Statistics;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 27/04/13
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
public class MinimumEvolution {

    private final static Logger log = LoggerFactory.getLogger(MinimumEvolution.class);

    // Input Data
    private final int NB_TAXA;
    private final int[] circularOrdering;
    private final Distances dist;

    private double[][] tableP = null;
    private TreeWeights treeWeights = null;
    private Tableau<Integer> interval = null;
    private Tableau<Integer> tree = new Tableau<Integer>(); // me tree
    private Tableau<Double> val = null;

    /**
     * Constructs a minimum evolution tree from the specified network with its
     * implied circular order.
     *
     * See D Bryant, 1997: <I>Building Trees, Hunting for Trees and
     * Comparing Trees - Theories and Methods in Phylogenetic Analysis</I>
     */
    public MinimumEvolution(final Distances distances, final int[] circularOrdering) {

        if (distances == null)
            throw new NullPointerException("Must have a distance matrix to work with.");

        if (circularOrdering == null || circularOrdering.length == 0)
            throw new NullPointerException("Must have some elements in circular ordering to work with.");

        if (circularOrdering.length != distances.size())
            throw new IllegalArgumentException("CircularOrdering and distances have differing sizes");

        this.NB_TAXA = distances.size();
        this.circularOrdering = circularOrdering;
        this.dist = distances;
    }

    public double calculateTreeLength() {
        this.calculateME();
        return this.treeWeights.calcTreeLength();
    }

    /**
     * Returns the minimum evolution tree.
     *
     * @return minimum evolution tree
     */
    public CircularSplitSystem getMETree() {
        return this.tree.convertToSplitSystem(this.dist.getTaxaSet(), this.circularOrdering);
    }

    /**
     * Calculates the optimal values for treelengths under the
     * minimum evolution criterion and extracts a minimum evolution tree.
     *
     * See D Bryant, 1997: <I>Building Trees, Hunting for Trees and
     * Comparing Trees - Theories and Methods in Phylogenetic Analysis</I>
     */
    private void calculateME() {
        final int n = this.NB_TAXA;
        int k = n * (n - 1);
        interval = new Tableau<Integer>();
        tableP = new double[n - 1][n];
        val = new Tableau<Double>();
        treeWeights = new TreeWeights(n);
        double usedBranchlength = 0.;

        /* Fill interval */
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n; j++) {
                int c = 0;
                for (int m = 0; m < i + 1; m++) {
                    int h;
                    if (j + m > n - 1) {
                        h = c++;
                    } else {
                        h = j + m;
                    }
                    interval.appendToRow(i * n + j, h);
                }
            }
        }

        /* Fill tableP */
        for (int i = 0; i < k; i++) {
            if (interval.rowSize(i) == 1) {
                ArrayList<Double> distances = new ArrayList<Double>();

                for (int j = 0; j < n; j++) {
                    distances.add(dist.getDistance(circularOrdering[interval.get(i, 0)], circularOrdering[j]));
                }

                tableP[0][interval.get(i, 0)] = Statistics.sumDoubles(distances);
            } else {
                ArrayList<Double> distances = new ArrayList<Double>();

                for (int m = 1; m < interval.rowSize(i); m++) {
                    distances.add(dist.getDistance(circularOrdering[interval.get(i, 0)],
                            circularOrdering[interval.get(i, m)]));
                }
                tableP[interval.rowSize(i) - 1][interval.get(i, 0)] = Statistics.sumDoubles(distances);

                double help = tableP[0][interval.get(i, 0)]
                        + tableP[interval.rowSize(i) - 2]
                        [interval.get(i, 1)];

                tableP[interval.rowSize(i) - 1][interval.get(i, 0)]
                        = help - 2 * tableP[interval.rowSize(i) - 1]
                        [interval.get(i, 0)];
            }
        }



        for (int i = 0; i < n; i++) {
            val.appendToRow(i, 0.);
        }

        //Fill the 2 element interval values with distances
        for (int i = n; i < 2*n; i++) {
            val.appendToRow(i, dist.getDistance(circularOrdering[interval.get(i,0)],circularOrdering[interval.get(i, 1)]));
//            System.out.print("vals for 2-elements done");
        }
        /* Fill val */
        for (int i = 2*n; i < k; i++) {
            for (int j = 0; j < interval.rowSize(i) - 1; j++) {

//                System.out.println("IntervalSize: "+interval.rowSize(i));
                double psum = Double.POSITIVE_INFINITY;
                double qsum = Double.POSITIVE_INFINITY;

                if (j == 0) {
                    double nj = interval.rowSize(i) - 1;
                    double nk = n - interval.rowSize(i);

                    psum = (1. / (4. * nj * nk)) * ((1. + nj + nk)
                            * tableP[0][interval.get(i, j)] - (1. + nj - nk)
                            * tableP[interval.rowSize(i) - 2]
                            [interval.get(i, j + 1)]
                            - (1. + nk - nj)
                            * tableP[interval.rowSize(i) - 1][interval.get(i, 0)]);
                } else {
                    for (int m = 0; m < j; m++) {
                        double ni = m + 1;
                        double nj = j - m;
                        double nk = interval.rowSize(i) - (j + 1);
                        double nl = n - interval.rowSize(i);
                        double branchlength = ((n / nl + n / nk + n / nj + n / ni - 4.)
                                * tableP[j][interval.get(i, 0)] + ((ni + nj) / (ni * nj)) * ((2. * nj - n)
                                * tableP[m][interval.get(i, 0)] + (2. * ni - n)
                                * tableP[j - m - 1][interval.get(i, m + 1)]) + ((nk + nl) / (nk * nl)) * ((2. * nl - n)
                                * tableP[interval.rowSize(i) - (j + 1) - 1][interval.get(i, j + 1)] + (2. * nk - n)
                                * tableP[interval.rowSize(i) - 1][interval.get(i, 0)])) * (1. / (4. * (ni + nj) * (nk + nl)));

                        double phelp = val.get((j * n + interval.get(i, 0)), m)
                                + branchlength;
                        if (phelp < psum) {
                            psum = phelp;
                        }
                    }
                }

                if (j == interval.rowSize(i) - 2) {
                    double nj = interval.rowSize(i) - 1;
                    double nk = n - interval.rowSize(i);

                    qsum = (1. / (4. * nj * nk)) * ((1. + nj + nk) * tableP[0][interval.get(i, j + 1)] - (1. + nj - nk)
                            * tableP[interval.rowSize(i) - 2][interval.get(i, 0)] - (1. + nk - nj) * tableP[interval.rowSize(i) - 1][interval.get(i, 0)]);
                } else {
                    for (int m = j + 1; m < interval.rowSize(i) - 1; m++) {
                        double ni = m - j;
                        double nj = interval.rowSize(i) - (m + 1);
                        double nk = j + 1;
                        double nl = n - interval.rowSize(i);
                        double branchlength = ((n / nl + n / nk + n / nj + n / ni - 4.)
                                * tableP[interval.rowSize(i) - (j + 1) - 1][interval.get(i, j + 1)] + ((ni + nj) / (ni * nj)) * ((2. * nj - n)
                                * tableP[m - j - 1][interval.get(i, j + 1)] + (2. * ni - n)
                                * tableP[interval.rowSize(i) - (m + 1) - 1][interval.get(i, m + 1)]) + ((nk + nl) / (nk * nl)) * ((2. * nl - n)
                                * tableP[j][interval.get(i, 0)] + (2. * nk - n)
                                * tableP[interval.rowSize(i) - 1][interval.get(i, 0)])) * (1. / (4. * (ni + nj) * (nk + nl)));

                        double qhelp = val.get((interval.rowSize(i) - (j + 1) - 1) * n + interval.get(i, j + 1), m - j - 1) + branchlength;
                        if (qhelp < qsum) {
                            qsum = qhelp;
                        }
                    }
                }
                val.appendToRow(i, psum + qsum);

            }
        }

        int split = -1;
        int usedInterval = -1;

        /* last element */
        double result = Double.POSITIVE_INFINITY;
        for (int j = 0; j < n; j++) {
            for (int m = 0; m < n - 2; m++) {
                double nj = m + 1;
                double nk = (n - 1) - (m + 1);
                double branchlength = (1. / (4. * (nj * nk))) * ((1. + nj + nk)
                        * tableP[n - 2][interval.get(n * (n - 2) + j, 0)] - (1. + nj - nk)
                        * tableP[m][interval.get(n * (n - 2) + j, 0)] - (1. + nk - nj)
                        * tableP[n - m - 3][interval.get(n * (n - 2) + j, m + 1)]);

                double help = val.get((n - 2) * n + j, m) + branchlength;

                if (help < result) {
                    result = help;
                    split = m;
                    usedInterval = (n - 2) * n + j;
                    usedBranchlength = branchlength;
                }
            }
        }



        tree.addRow(interval.getRow(usedInterval));
        int m = interval.get(usedInterval, 0);
        int f = interval.get(usedInterval, interval.rowSize(usedInterval)-1);

        this.treeWeights.setWeightAt(usedBranchlength, m, f);
        treeExtration(usedInterval, split);

        for (int i = 0; i < tree.rows(); i++) {
            for (int j = 0; j < tree.rowSize(i); j++) {
                int currentVal = tree.get(i, j);
                tree.set(i, j, this.circularOrdering[currentVal]);
            }
        }



//Print table with treelenghts
//        for (int j = 0; j < val.rows(); j++) {
//            for (int i = 0; i < val.rowSize(j); i++) {
//                 System.out.print(val.get(j, i)+" ");
//            }
//            System.out.println();
//        }
//        System.out.println(interval.getRow(usedInterval));
//        System.out.println(result);
        showContribution(val);


        clean(); // allow garbage collection on no longer needed variables
    }


    /**
     * Returns the Minimum Evolution split weights.
     *
     * @return Minimum Evolution split weights
     */
    public TreeWeights getMESplitWeights() {
        return treeWeights;
    }

    /**
     * Finds backtrace through the tableau val and saves the associated splits.
     *
     * @param usedInterval
     * @param split
     * @return
     */
    private Tableau<Integer> treeExtration(int usedInterval, int split) {
        final int n = this.NB_TAXA;
        int length1 = split + 1;
        int firstVal = interval.get(usedInterval, 0);
        int length2 = interval.rowSize(usedInterval) - (split + 1);
        int secondVal = interval.get(usedInterval, split + 1);
        double usedBranchlength = 0;



        //Attention intervals are just indices for circ.Ordering, has to be translated!
        tree.addRow(interval.getRow(n * (length1 - 1) + firstVal));
        tree.addRow(interval.getRow(n * (length2 - 1) + secondVal));

        if (length1 > 1) {
            double result = Double.POSITIVE_INFINITY;
            int newSplit = -1;
            int newUsedInterval = -1;

            for (int j = 0; j < length1 - 1; j++) {
                double ni = j + 1;
                double nj = length1 - (j + 1);
                double nk = length2;
                double nl = n - interval.rowSize(usedInterval);
                double pn = tableP[length1 - 1][firstVal];
                double pi = tableP[j][interval.get(usedInterval, 0)];
                double pj = tableP[length1 - (j + 1) - 1]
                        [interval.get(usedInterval, j + 1)];
                double pk = tableP[length2 - 1][secondVal];
                double pl = tableP[interval.rowSize(usedInterval) - 1]
                        [firstVal];

                double branchlength = ((n / nl + n / nk + n / nj + n / ni - 4.)
                        * pn + ((ni + nj) / (ni * nj)) * ((2. * nj - n)
                        * pi + (2. * ni - n) * pj) + ((nk + nl) / (nk * nl))
                        * ((2. * nl - n) * pk + (2. * nk - n) * pl))
                        * (1. / (4. * (ni + nj) * (nk + nl)));
                double phelp = branchlength + val.get((length1 - 1) * n
                        + interval.get(usedInterval, 0), j);

                if (phelp < result) {
                    result = phelp;
                    newSplit = j;
                    newUsedInterval = (length1 - 1) * n
                            + interval.get(usedInterval, 0);
                    usedBranchlength = branchlength;
                }
            }

            int k = interval.get(usedInterval, 0);
            int l = interval.get(usedInterval, length1 - 1);

            this.treeWeights.setWeightAt(usedBranchlength, k, l);
            treeExtration(newUsedInterval, newSplit);
        } else {
            double nj = interval.rowSize(usedInterval) - 1;
            double nk = n - interval.rowSize(usedInterval);
            double pi = tableP[0][interval.get(usedInterval, 0)];
            double pj = tableP[interval.rowSize(usedInterval) - 2]
                    [interval.get(usedInterval, length1)];
            double pk = tableP[interval.rowSize(usedInterval) - 1]
                    [interval.get(usedInterval, 0)];

            usedBranchlength = 1. / (4. * nj * nk) * ((1. + nj + nk) * pi
                    - (1. + nj - nk) * pj - (1. + nk - nj) * pk);

            int k = interval.get(usedInterval, 0);
            int l = interval.get(usedInterval, length1 - 1);

            this.treeWeights.setWeightAt(usedBranchlength, k, l);
        }

        if (length2 > 1) {
            double result = Double.POSITIVE_INFINITY;
            int newSplit = -1;
            int newUsedInterval = -1;

            for (int j = length1; j < interval.rowSize(usedInterval) - 1; j++) {
                double ni = (j + 1) - length1  ;
                double nj = interval.rowSize(usedInterval) - (j + 1);
                double nk = length1;
                double nl = n - interval.rowSize(usedInterval);
                double pm = tableP[length2 - 1][secondVal];
                double pi = tableP[j - length1][secondVal];
                double pj = tableP[interval.rowSize(usedInterval) - (j + 1) - 1][interval.get(usedInterval, j + 1)];
                double pk = tableP[length1 - 1][interval.get(usedInterval, 0)];
                double pl = tableP[interval.rowSize(usedInterval) - 1][firstVal];
                double branchlength = ((n / nl + n / nk + n / nj + n / ni - 4.) *
                        pm + ((ni + nj) / (ni * nj)) * ((2. * nj - n)*
                        pi + (2. * ni - n) *
                        pj) + (nk + nl) / (nk * nl) * ((2. * nl - n) *
                        pk + (2. * nk - n)*
                        pl)) * (1. / ( 4. * (ni + nj) * (nk + nl)));
                double qhelp = val.get((length2 - 1) * n + secondVal, j - length1) + branchlength;

                if (qhelp < result) {
                    result = qhelp;
                    newSplit = j - length1;
                    newUsedInterval = (length2 - 1) * n + secondVal;
                    usedBranchlength = branchlength;
                }
            }
            int k = interval.get(usedInterval, length1);
            int l = interval.get(usedInterval, interval.rowSize(usedInterval)-1);

            this.treeWeights.setWeightAt(usedBranchlength, k, l);

            treeExtration(newUsedInterval, newSplit);
        } else {
            double nj = interval.rowSize(usedInterval) - 1;
            double nk = n - interval.rowSize(usedInterval);
            double pi = tableP[0][interval.get(usedInterval, length1)];
            double pj= tableP[interval.rowSize(usedInterval) - 2]
                    [interval.get(usedInterval, 0)];
            double pk = tableP[interval.rowSize(usedInterval) - 1]
                    [interval.get(usedInterval, 0)];

            usedBranchlength = 1. / (4. * nj * nk)
                    * ((1. + nj + nk) * pi - (1. + nj - nk) * pj
                    - (1. + nk - nj) * pk);

            int k = interval.get(usedInterval, interval.rowSize(usedInterval)-1);
            int l = interval.get(usedInterval, length1);

            this.treeWeights.setWeightAt(usedBranchlength, k, l);

        }

        return tree;
    }

    /**
     * Allows garbage collection for certain instance variables.
     */
    private void clean() {
        interval = null;
        tableP = null;
        val = null;
    }


    void showContribution(Tableau<Double> val) {

        int k = 0;
        for (int intervalLength = 1; intervalLength < this.NB_TAXA - 2; intervalLength++) {

            double[] avarageVals = new double[val.rowSize(k)];
            while (val.rowSize(k) == intervalLength) {
                for (int j = 0; j < val.rowSize(k); j++) {
                    avarageVals[j] = val.get(k, j);
                }
                k++;

            }
//            System.out.println("number of intervals: "+k);


            for (int j = 0; j < avarageVals.length; j++) {
//        System.out.print(avarageVals[j]/k+" ");
            }
//            System.out.println();
        }
    }
}
