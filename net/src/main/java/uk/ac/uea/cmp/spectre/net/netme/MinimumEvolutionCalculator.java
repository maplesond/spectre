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

package uk.ac.uea.cmp.spectre.net.netme;

import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.split.SpectreSplitSystem;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitSystem;
import uk.ac.uea.cmp.spectre.core.ds.split.TreeSplitWeights;
import uk.ac.uea.cmp.spectre.core.math.stats.Statistics;

import java.util.ArrayList;

/**
 * Created by maplesod on 10/02/14.
 */
public class MinimumEvolutionCalculator {

    public NetMEResult calcMinEvoTree(DistanceMatrix distanceMatrix, IdentifierList circularOrdering) {

        if (distanceMatrix == null)
            throw new NullPointerException("Must have a distance matrix to work with.");

        if (circularOrdering == null || circularOrdering.size() == 0)
            throw new NullPointerException("Must have some elements in circular ordering to work with.");

        if (circularOrdering.size() != distanceMatrix.size())
            throw new IllegalArgumentException("CircularOrdering and distanceMatrix have differing sizes");

        TreeAndWeights treeAndWeights = this.calculateME(distanceMatrix, circularOrdering);

        //the ME tree + weights from OLS (\in R^+)
        SplitSystem meTree = treeAndWeights.getTree().convertToSplitSystem(distanceMatrix, circularOrdering);
        //The ME tree + original weights (\in R)
        SplitSystem originalMETree = new SpectreSplitSystem(meTree, treeAndWeights.getTreeWeights());

        // Compile stats
        double treeLength = treeAndWeights.getTreeWeights().calcTreeLength();
        String stats = "Tree length: " + treeLength;

        return new NetMEResult(originalMETree, meTree, stats);
    }


    protected Tableau<Integer> fillInterval(final int nbTaxa) {

        Tableau<Integer> interval = new Tableau<Integer>();

        for (int i = 0; i < nbTaxa - 1; i++) {
            for (int j = 0; j < nbTaxa; j++) {
                int c = 0;
                for (int m = 0; m < i + 1; m++) {
                    int h;
                    if (j + m > nbTaxa - 1) {
                        h = c++;
                    } else {
                        h = j + m;
                    }
                    interval.appendToRow(i * nbTaxa + j, h);
                }
            }
        }

        return interval;
    }

    protected double[][] fillTableP(DistanceMatrix dist, IdentifierList circularOrdering, Tableau<Integer> interval) {

        final int NB_TAXA = dist.size();
        final int K = NB_TAXA * (NB_TAXA - 1);

        double[][] tableP = new double[NB_TAXA - 1][NB_TAXA];

        /* Fill tableP */
        for (int i = 0; i < K; i++) {
            if (interval.rowSize(i) == 1) {
                ArrayList<Double> distances = new ArrayList<Double>();

                for (int j = 0; j < NB_TAXA; j++) {
                    distances.add(dist.getDistance(circularOrdering.get(interval.get(i, 0)), circularOrdering.get(j)));
                }

                tableP[0][interval.get(i, 0)] = Statistics.sumDoubles(distances);
            } else {
                ArrayList<Double> distances = new ArrayList<Double>();

                for (int m = 1; m < interval.rowSize(i); m++) {
                    distances.add(dist.getDistance(circularOrdering.get(interval.get(i, 0)),
                            circularOrdering.get(interval.get(i, m))));
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

        return tableP;
    }

    protected Tableau<Double> fillVal(DistanceMatrix dist, IdentifierList circularOrdering, Tableau<Integer> interval, double[][] tableP) {

        final int NB_TAXA = dist.size();
        final int K = NB_TAXA * (NB_TAXA - 1);

        Tableau<Double> val = new Tableau<Double>();

        for (int i = 0; i < NB_TAXA; i++) {
            val.appendToRow(i, 0.);
        }

        //Fill the 2 element interval values with distances
        for (int i = NB_TAXA; i < 2 * NB_TAXA; i++) {
            val.appendToRow(i, dist.getDistance(circularOrdering.get(interval.get(i, 0)), circularOrdering.get(interval.get(i, 1))));
//            System.out.print("vals for 2-elements done");
        }
        /* Fill val */
        for (int i = 2 * NB_TAXA; i < K; i++) {
            for (int j = 0; j < interval.rowSize(i) - 1; j++) {

//                System.out.println("IntervalSize: "+interval.rowSize(i));
                double psum = Double.POSITIVE_INFINITY;
                double qsum = Double.POSITIVE_INFINITY;

                if (j == 0) {
                    double nj = interval.rowSize(i) - 1;
                    double nk = NB_TAXA - interval.rowSize(i);

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
                        double nl = NB_TAXA - interval.rowSize(i);
                        double branchlength = ((NB_TAXA / nl + NB_TAXA / nk + NB_TAXA / nj + NB_TAXA / ni - 4.)
                                * tableP[j][interval.get(i, 0)] + ((ni + nj) / (ni * nj)) * ((2. * nj - NB_TAXA)
                                * tableP[m][interval.get(i, 0)] + (2. * ni - NB_TAXA)
                                * tableP[j - m - 1][interval.get(i, m + 1)]) + ((nk + nl) / (nk * nl)) * ((2. * nl - NB_TAXA)
                                * tableP[interval.rowSize(i) - (j + 1) - 1][interval.get(i, j + 1)] + (2. * nk - NB_TAXA)
                                * tableP[interval.rowSize(i) - 1][interval.get(i, 0)])) * (1. / (4. * (ni + nj) * (nk + nl)));

                        double phelp = val.get((j * NB_TAXA + interval.get(i, 0)), m)
                                + branchlength;
                        if (phelp < psum) {
                            psum = phelp;
                        }
                    }
                }

                if (j == interval.rowSize(i) - 2) {
                    double nj = interval.rowSize(i) - 1;
                    double nk = NB_TAXA - interval.rowSize(i);

                    qsum = (1. / (4. * nj * nk)) * ((1. + nj + nk) * tableP[0][interval.get(i, j + 1)] - (1. + nj - nk)
                            * tableP[interval.rowSize(i) - 2][interval.get(i, 0)] - (1. + nk - nj) * tableP[interval.rowSize(i) - 1][interval.get(i, 0)]);
                } else {
                    for (int m = j + 1; m < interval.rowSize(i) - 1; m++) {
                        double ni = m - j;
                        double nj = interval.rowSize(i) - (m + 1);
                        double nk = j + 1;
                        double nl = NB_TAXA - interval.rowSize(i);
                        double branchlength = ((NB_TAXA / nl + NB_TAXA / nk + NB_TAXA / nj + NB_TAXA / ni - 4.)
                                * tableP[interval.rowSize(i) - (j + 1) - 1][interval.get(i, j + 1)] + ((ni + nj) / (ni * nj)) * ((2. * nj - NB_TAXA)
                                * tableP[m - j - 1][interval.get(i, j + 1)] + (2. * ni - NB_TAXA)
                                * tableP[interval.rowSize(i) - (m + 1) - 1][interval.get(i, m + 1)]) + ((nk + nl) / (nk * nl)) * ((2. * nl - NB_TAXA)
                                * tableP[j][interval.get(i, 0)] + (2. * nk - NB_TAXA)
                                * tableP[interval.rowSize(i) - 1][interval.get(i, 0)])) * (1. / (4. * (ni + nj) * (nk + nl)));

                        double qhelp = val.get((interval.rowSize(i) - (j + 1) - 1) * NB_TAXA + interval.get(i, j + 1), m - j - 1) + branchlength;
                        if (qhelp < qsum) {
                            qsum = qhelp;
                        }
                    }
                }
                val.appendToRow(i, psum + qsum);

            }
        }

        return val;
    }


    private class TreeAndWeights {

        private Tableau<Integer> tree;
        private TreeSplitWeights treeWeights;

        public TreeAndWeights(Tableau<Integer> tree, TreeSplitWeights treeWeights) {
            this.tree = tree;
            this.treeWeights = treeWeights;
        }

        public Tableau<Integer> getTree() {
            return tree;
        }

        public TreeSplitWeights getTreeWeights() {
            return treeWeights;
        }
    }

    protected TreeAndWeights createTree(final int nbTaxa, IdentifierList circularOrdering, Tableau<Integer> interval, double[][] tableP, Tableau<Double> val) {

        double usedBranchlength = 0.;
        int split = -1;
        int usedInterval = -1;

        /* last element */
        double result = Double.POSITIVE_INFINITY;
        for (int j = 0; j < nbTaxa; j++) {
            for (int m = 0; m < nbTaxa - 2; m++) {
                double nj = m + 1;
                double nk = (nbTaxa - 1) - (m + 1);
                double branchlength = (1. / (4. * (nj * nk))) * ((1. + nj + nk)
                        * tableP[nbTaxa - 2][interval.get(nbTaxa * (nbTaxa - 2) + j, 0)] - (1. + nj - nk)
                        * tableP[m][interval.get(nbTaxa * (nbTaxa - 2) + j, 0)] - (1. + nk - nj)
                        * tableP[nbTaxa - m - 3][interval.get(nbTaxa * (nbTaxa - 2) + j, m + 1)]);

                double help = val.get((nbTaxa - 2) * nbTaxa + j, m) + branchlength;

                if (help < result) {
                    result = help;
                    split = m;
                    usedInterval = (nbTaxa - 2) * nbTaxa + j;
                    usedBranchlength = branchlength;
                }
            }
        }

        Tableau<Integer> tree = new Tableau<Integer>();
        TreeSplitWeights treeWeights = new TreeSplitWeights(nbTaxa);

        tree.addRow(interval.getRow(usedInterval));
        int m = interval.get(usedInterval, 0);
        int f = interval.get(usedInterval, interval.rowSize(usedInterval) - 1);

        treeWeights.setBranchLengthAt(usedBranchlength, m, f);
        treeExtration(nbTaxa, usedInterval, split, interval, tableP, val, tree, treeWeights);

        for (int i = 0; i < tree.rows(); i++) {
            for (int j = 0; j < tree.rowSize(i); j++) {
                int currentVal = tree.get(i, j);
                tree.set(i, j, circularOrdering.get(currentVal).getId());
            }
        }

        return new TreeAndWeights(tree, treeWeights);
    }

    /**
     * Calculates the optimal values for treelengths under the
     * minimum evolution criterion and extracts a minimum evolution tree.
     * <p/>
     * See D Bryant, 1997: <I>Building Trees, Hunting for Trees and
     * Comparing Trees - Theories and Methods in Phylogenetic Analysis</I>
     */
    protected TreeAndWeights calculateME(DistanceMatrix dist, IdentifierList circularOrdering) {

        final int NB_TAXA = dist.size();

        Tableau<Integer> interval = fillInterval(NB_TAXA);
        double[][] tableP = fillTableP(dist, circularOrdering, interval);
        Tableau<Double> val = fillVal(dist, circularOrdering, interval, tableP);
        TreeAndWeights treeAndWeights = createTree(NB_TAXA, circularOrdering, interval, tableP, val);

        return treeAndWeights;
    }


    /**
     * Finds backtrace through the tableau val and saves the associated splits.
     *
     * @param usedInterval
     * @param split
     * @return A tableau
     */
    private Tableau<Integer> treeExtration(final int nbTaxa, int usedInterval, int split, Tableau<Integer> interval, double[][] tableP,
                                           Tableau<Double> val, Tableau<Integer> tree, TreeSplitWeights treeWeights) {

        int length1 = split + 1;
        int firstVal = interval.get(usedInterval, 0);
        int length2 = interval.rowSize(usedInterval) - (split + 1);
        int secondVal = interval.get(usedInterval, split + 1);
        double usedBranchlength = 0;


        //Attention intervals are just indices for circ.Ordering, has to be translated!
        tree.addRow(interval.getRow(nbTaxa * (length1 - 1) + firstVal));
        tree.addRow(interval.getRow(nbTaxa * (length2 - 1) + secondVal));

        if (length1 > 1) {
            double result = Double.POSITIVE_INFINITY;
            int newSplit = -1;
            int newUsedInterval = -1;

            for (int j = 0; j < length1 - 1; j++) {
                double ni = j + 1;
                double nj = length1 - (j + 1);
                double nk = length2;
                double nl = nbTaxa - interval.rowSize(usedInterval);
                double pn = tableP[length1 - 1][firstVal];
                double pi = tableP[j][interval.get(usedInterval, 0)];
                double pj = tableP[length1 - (j + 1) - 1]
                        [interval.get(usedInterval, j + 1)];
                double pk = tableP[length2 - 1][secondVal];
                double pl = tableP[interval.rowSize(usedInterval) - 1]
                        [firstVal];

                double branchlength = ((nbTaxa / nl + nbTaxa / nk + nbTaxa / nj + nbTaxa / ni - 4.)
                        * pn + ((ni + nj) / (ni * nj)) * ((2. * nj - nbTaxa)
                        * pi + (2. * ni - nbTaxa) * pj) + ((nk + nl) / (nk * nl))
                        * ((2. * nl - nbTaxa) * pk + (2. * nk - nbTaxa) * pl))
                        * (1. / (4. * (ni + nj) * (nk + nl)));
                double phelp = branchlength + val.get((length1 - 1) * nbTaxa
                        + interval.get(usedInterval, 0), j);

                if (phelp < result) {
                    result = phelp;
                    newSplit = j;
                    newUsedInterval = (length1 - 1) * nbTaxa
                            + interval.get(usedInterval, 0);
                    usedBranchlength = branchlength;
                }
            }

            int k = interval.get(usedInterval, 0);
            int l = interval.get(usedInterval, length1 - 1);

            treeWeights.setBranchLengthAt(usedBranchlength, k, l);
            treeExtration(nbTaxa, newUsedInterval, newSplit, interval, tableP, val, tree, treeWeights);
        } else {
            double nj = interval.rowSize(usedInterval) - 1;
            double nk = nbTaxa - interval.rowSize(usedInterval);
            double pi = tableP[0][interval.get(usedInterval, 0)];
            double pj = tableP[interval.rowSize(usedInterval) - 2]
                    [interval.get(usedInterval, length1)];
            double pk = tableP[interval.rowSize(usedInterval) - 1]
                    [interval.get(usedInterval, 0)];

            usedBranchlength = 1. / (4. * nj * nk) * ((1. + nj + nk) * pi
                    - (1. + nj - nk) * pj - (1. + nk - nj) * pk);

            int k = interval.get(usedInterval, 0);
            int l = interval.get(usedInterval, length1 - 1);

            treeWeights.setBranchLengthAt(usedBranchlength, k, l);
        }

        if (length2 > 1) {
            double result = Double.POSITIVE_INFINITY;
            int newSplit = -1;
            int newUsedInterval = -1;

            for (int j = length1; j < interval.rowSize(usedInterval) - 1; j++) {
                double ni = (j + 1) - length1;
                double nj = interval.rowSize(usedInterval) - (j + 1);
                double nk = length1;
                double nl = nbTaxa - interval.rowSize(usedInterval);
                double pm = tableP[length2 - 1][secondVal];
                double pi = tableP[j - length1][secondVal];
                double pj = tableP[interval.rowSize(usedInterval) - (j + 1) - 1][interval.get(usedInterval, j + 1)];
                double pk = tableP[length1 - 1][interval.get(usedInterval, 0)];
                double pl = tableP[interval.rowSize(usedInterval) - 1][firstVal];
                double branchlength = ((nbTaxa / nl + nbTaxa / nk + nbTaxa / nj + nbTaxa / ni - 4.) *
                        pm + ((ni + nj) / (ni * nj)) * ((2. * nj - nbTaxa) *
                        pi + (2. * ni - nbTaxa) *
                        pj) + (nk + nl) / (nk * nl) * ((2. * nl - nbTaxa) *
                        pk + (2. * nk - nbTaxa) *
                        pl)) * (1. / (4. * (ni + nj) * (nk + nl)));
                double qhelp = val.get((length2 - 1) * nbTaxa + secondVal, j - length1) + branchlength;

                if (qhelp < result) {
                    result = qhelp;
                    newSplit = j - length1;
                    newUsedInterval = (length2 - 1) * nbTaxa + secondVal;
                    usedBranchlength = branchlength;
                }
            }
            int k = interval.get(usedInterval, length1);
            int l = interval.get(usedInterval, interval.rowSize(usedInterval) - 1);

            treeWeights.setBranchLengthAt(usedBranchlength, k, l);

            treeExtration(nbTaxa, newUsedInterval, newSplit, interval, tableP, val, tree, treeWeights);
        } else {
            double nj = interval.rowSize(usedInterval) - 1;
            double nk = nbTaxa - interval.rowSize(usedInterval);
            double pi = tableP[0][interval.get(usedInterval, length1)];
            double pj = tableP[interval.rowSize(usedInterval) - 2]
                    [interval.get(usedInterval, 0)];
            double pk = tableP[interval.rowSize(usedInterval) - 1]
                    [interval.get(usedInterval, 0)];

            usedBranchlength = 1. / (4. * nj * nk)
                    * ((1. + nj + nk) * pi - (1. + nj - nk) * pj
                    - (1. + nk - nj) * pk);

            int k = interval.get(usedInterval, interval.rowSize(usedInterval) - 1);
            int l = interval.get(usedInterval, length1);

            treeWeights.setBranchLengthAt(usedBranchlength, k, l);
        }

        return tree;
    }
}
