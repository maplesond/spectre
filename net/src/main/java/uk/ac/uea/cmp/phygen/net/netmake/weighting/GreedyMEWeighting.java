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

package uk.ac.uea.cmp.phygen.net.netmake.weighting;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.ds.SummedDistanceList;
import uk.ac.uea.cmp.phygen.core.ds.Tableau;
import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.phygen.core.math.Statistics;
import uk.ac.uea.cmp.phygen.net.netmake.edge.EdgeAdjacents;

import java.util.ArrayList;
import java.util.List;

/**
 * GreedyMEWeighting extends weighting, although it doesn't actually update a Weighting param.
 * Explanation....
 *
 * @author Sarah Bastkowski
 */
public class GreedyMEWeighting extends Weighting {

    private final static Logger log = LoggerFactory.getLogger(GreedyMEWeighting.class);

    private DistanceMatrix distanceMatrix;


    /**
     * Initialises a GreedyMEWeighting with a SplitSystem
     *
     * @param distanceMatrix
     */
    public GreedyMEWeighting(DistanceMatrix distanceMatrix) {

        super();

        this.distanceMatrix = distanceMatrix;
    }


    public Pair<Integer, Integer> makeMECherry(Tableau<Integer> splits, final Tableau<Integer> splitsCreation) {

        double oldTreeLength = Double.POSITIVE_INFINITY;

        Pair<Integer, Integer> bestSplits = null;

        log.debug(splitsCreation.rows() + " cherries to produce");

        for (int i = 0; i < splitsCreation.rows() - 1; i++) {

            log.debug("Making cherry " + i + "...");
            for (int j = i + 1; j < splitsCreation.rows(); j++) {

                splits.addRow(splitsCreation.copyRow(i));
                splits.addRow(splitsCreation.copyRow(j));
                splits.mergeRows(splits.rows() - 2, splits.rows() - 1);

                // If we've created a split which contains the entire taxa set then remove it
                /*if (splits.getRow(splits.rows() -1).size() == this.distanceMatrix.size()) {
                    splits.removeRow(splits.rows() - 1);
                } */

                log.debug("  Number of splits: " + splits.rows());

                double treeLength = this.calculateTreeLength(splits);

                log.debug("  Tree length is " + treeLength + " for " + i + "," + j);

                if (treeLength < oldTreeLength) {
                    oldTreeLength = treeLength;
                    bestSplits = new ImmutablePair<Integer, Integer>(i, j);
                }

                splits.removeRow(splits.rows() - 1);
            }

            log.debug("Made cherry " + i);
        }

        return bestSplits;
    }

    private double calculateTreeLength(Tableau<Integer> splits) {

        return Statistics.sumDoubles(this.getEdgeWeights(splits));
    }

    /*
     * method to calculate a split length
     * input is an arrayList of splits representing the tree topology,
     * the relevant distanceMatrix d and the number of the split, the length is calculated for
     */
    private double calculateEdges(double P_0, EdgeAdjacents aEdgeAdjacents, boolean external) {

        int nbTaxa = this.distanceMatrix.size();

        int C[] = aEdgeAdjacents.getNumberOfLeavesInAdjacents();

        double d[] = calcD(C, nbTaxa);

        InitialVars initialVars = external ?
                InitialVars.createExternalVars(C.length, nbTaxa) :
                InitialVars.createInternalVars(C, aEdgeAdjacents.getSplitPoint());


        //Matrix inversion for X^-1 (see D.Bryant thesis page 153)
        InversionResult inversionResult = matrixInversion(C, nbTaxa);

        double gamma = calcGamma(initialVars, inversionResult.getS());

        // There is no zero in the matrix "D" (notation refers to D.Bryant thesis algorithm 15)
        double[] w = calcW(inversionResult.getZero(), nbTaxa, inversionResult.calcK(), C, gamma, d, initialVars.getV());

        Sums sums = Sums.createSums(initialVars, w, C, aEdgeAdjacents.getTempSDL());

        //edge length calculation
        double edgeWeight = calcEdgeWeight(sums, initialVars.getnAlpha(), initialVars.getnBeta(), P_0);

        // Check for dodgy results... sometimes can happen due to div by 0
        if (Double.isInfinite(edgeWeight)) {
            throw new IllegalArgumentException("Edge Weight was infinite!");
        }
        if (Double.isNaN(edgeWeight)) {
            throw new IllegalArgumentException("Edge Weight was not a number");
        }

        return edgeWeight;
    }

    private double calcEdgeWeight(Sums sums, int n_alpha, int n_beta, double P_0) {
        //edge length calculation
        double edgeWeight = P_0 - sums.getSum1();

        if (edgeWeight != 0.0) {
            edgeWeight = edgeWeight / (n_alpha * n_beta - sums.getSum2() - sums.getSum3());
        }

        return edgeWeight;
    }


    private static class InitialVars {

        private int v[];
        private int nAlpha;
        private int nBeta;
        private int cAlpha;

        public InitialVars(int[] v, int nAlpha, int nBeta, int cAlpha) {
            this.v = v;
            this.nAlpha = nAlpha;
            this.nBeta = nBeta;
            this.cAlpha = cAlpha;
        }

        public int[] getV() {
            return v;
        }

        public int getnAlpha() {
            return nAlpha;
        }

        public int getnBeta() {
            return nBeta;
        }

        public int getcAlpha() {
            return cAlpha;
        }

        public static InitialVars createExternalVars(int cLen, int nbTaxa) {

            int[] v = new int[cLen];
            for (int i = 0; i < cLen; i++) {
                v[i] = 1;
            }
            int nAlpha = (nbTaxa - 1);
            int nBeta = 1;
            int cAlpha = cLen;

            return new InitialVars(v, nAlpha, nBeta, cAlpha);
        }

        public static InitialVars createInternalVars(int[] C, int cAlpha) {

            int nAlpha = 0;
            int nBeta = 0;
            int v[] = new int[C.length];

            //Check if C_alpha is right index
            for (int i = 0; i < cAlpha; i++) {
                nAlpha += C[i];
            }

            for (int i = cAlpha; i < C.length; i++) {
                nBeta += C[i];
            }

            for (int i = 0; i < cAlpha; i++) {
                v[i] = nBeta;
            }

            for (int i = cAlpha; i < C.length; i++) {
                v[i] = nAlpha;
            }

            return new InitialVars(v, nAlpha, nBeta, cAlpha);
        }
    }


    private double[] calcD(int[] C, int nbTaxa) {
        double[] d = new double[C.length];

        for (int i = 0; i < C.length; i++) {
            d[i] = (double) nbTaxa / (double) C[i] - 2.0;
        }

        return d;
    }

    private static class Sums {
        private double sum1;
        private double sum2;
        private double sum3;

        private Sums(double sum1, double sum2, double sum3) {
            this.sum1 = sum1;
            this.sum2 = sum2;
            this.sum3 = sum3;
        }

        public double getSum1() {
            return sum1;
        }

        public double getSum2() {
            return sum2;
        }

        public double getSum3() {
            return sum3;
        }

        public static Sums createSums(InitialVars initialVars, double[] w, int[] C, SummedDistanceList P) {
            double sum1 = 0.0;
            double sum2 = 0.0;
            double sum3 = 0.0;

            for (int i = 0; i < C.length; i++) {
                sum1 += ((w[i] * P.get(i)) / (double) C[i]);
            }

            for (int i = 0; i < initialVars.getcAlpha(); i++) {
                sum2 += (initialVars.getnBeta() * w[i]);
            }

            for (int i = initialVars.getcAlpha(); i < C.length; i++) {
                sum3 += (initialVars.getnAlpha() * w[i]);
            }

            return new Sums(sum1, sum2, sum3);
        }
    }


    private double calcGamma(InitialVars initialVars, final double[] s) {

        double gamma = 0.0;

        for (int i = 0; i < initialVars.getcAlpha(); i++) {
            gamma += initialVars.getnBeta() * s[i];
        }
        for (int i = initialVars.getcAlpha(); i < initialVars.getV().length; i++) {
            gamma += initialVars.getnAlpha() * s[i];
        }

        return gamma;
    }


    private class InversionResult {
        private double[] s;
        private int zero;

        private InversionResult(double[] s, int zero) {
            this.s = s;
            this.zero = zero;
        }

        public double[] getS() {
            return s;
        }

        public int getZero() {
            return zero;
        }

        public double calcK() {
            return Statistics.sumDoubles(s) + 1.0;
        }
    }

    /**
     * Matrix inversion for X^-1 (see D.Bryant thesis page 153)
     *
     * @param C
     * @param nbTaxa
     * @return Matrix Inversion Result
     */
    private InversionResult matrixInversion(final int[] C, final int nbTaxa) {

        double[] s = new double[C.length];
        int zero = -1;
        for (int i = 0; i < C.length; i++) {
            if (C[i] == (double) nbTaxa / 2.0) {
                s[i] = 0;
                zero = i;
            } else {
                s[i] = (double) C[i] / ((double) nbTaxa - 2.0 * (double) C[i]);
            }
        }

        return new InversionResult(s, zero);
    }

    /**
     * There is no zero in the matrix "D" (notation refers to D.Bryant thesis algorithm 15)
     *
     * @param zero
     * @param nbTaxa
     * @param k
     * @param C
     * @param gamma
     * @param d
     * @param v
     * @return W
     */
    private double[] calcW(final int zero, final int nbTaxa, final double k, final int[] C, final double gamma, final double[] d, final int[] v) {

        double[] w = new double[C.length];

        if (zero == -1) {
            for (int i = 0; i < C.length; i++) {
                w[i] = (-1 / ((double) nbTaxa / (double) C[i] - 2.0)) * (gamma / k - (double) v[i]);
            }
            //there is a zero at position zero
        } else {
            for (int i = 0; i < zero; i++) {
                w[i] = (1.0 / d[i]) * (v[i] - v[zero]);
            }
            w[zero] = (-gamma) + k * v[zero];
            for (int i = zero + 1; i < C.length; i++) {
                w[i] = (1.0 / d[i]) * (v[i] - v[zero]);
            }

        }

        return w;
    }


    public List<Double> getEdgeWeights(Tableau<Integer> tableau) {

        log.debug("  Calculating " + tableau.rows() + " Edge Weights...");

        ArrayList<Double> edgeWeights = new ArrayList<Double>();

        SummedDistanceList P = new SummedDistanceList(this.calculateP(tableau, this.distanceMatrix));

        for (int i = 0; i < tableau.rows(); i++) {

            Tableau<Integer> splitsCopy = new Tableau<Integer>(tableau);

            boolean external = tableau.rowSize(i) == 1 || tableau.rowSize(i) == this.distanceMatrix.size() - 1;

            EdgeAdjacents aEdgeAdjacents = EdgeAdjacents.retrieveAdjacents(splitsCopy, i, P, this.distanceMatrix.size());

            log.debug("    Retrieved edge adjacents for " + i + ". " + aEdgeAdjacents.getNumberOfLeavesInAdjacents().length + " leaves in adjacents");

            edgeWeights.add(calculateEdges(P.get(i), aEdgeAdjacents, external));

            log.debug("    Calculated edge length for " + i + ": " + edgeWeights.get(i));
        }

        log.debug("  Calculated Edge Weights");

        return edgeWeights;
    }


    private double[] calculateP(Tableau<Integer> splits, DistanceMatrix distanceMatrix) {

        int nb_taxa = this.distanceMatrix.size();

        double P[] = new double[splits.rows()];

        //for each split, determine how many elements are on each side of the split
        for (int i = 0; i < splits.rows(); i++) {

            boolean splited[] = new boolean[nb_taxa];
            for (int h = 0; h < splited.length; h++) {
                splited[h] = false;
            }

            //Array stores the info which elements are on one side
            //each element of the split
            for (int j = 0; j < splits.rowSize(i); j++) {
                for (int k = 0; k < nb_taxa; k++) {
                    if (splits.get(i, j) == k) {
                        splited[k] = true;
                    }
                }
            }

            //sums up all distances from the elements on the one side of the edge
            //to the other side
            for (int j = 0; j < splits.rowSize(i); j++) {
                for (int k = 0; k < nb_taxa; k++) {
                    if (splited[k] == false) {
                        P[i] += this.distanceMatrix.getDistance(splits.get(i, j), k);
                    }
                }
            }
        }
        return P;
    }

    /**
     * Will always throw an UnsupportedOperationException because this GreedyMEWeighting class
     * is not technically a Weighting but is sometimes used in a Weighting object's
     * place in NeighborNet
     *
     * @param i
     * @param position
     * @param customParameter
     */
    @Override
    public void updateWeightingParam(int i, int position, int customParameter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

