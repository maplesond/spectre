/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
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

package uk.ac.uea.cmp.phygen.core.ds.quartet;

import uk.ac.uea.cmp.phygen.core.ds.Quadruple;
import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.phygen.core.ds.split.Split;

import java.util.HashMap;
import java.util.List;

/**
 * Created by dan on 04/12/13.
 */
public class WeightedQuartetMap extends HashMap<Quartet, QuartetWeights> {

    /**
     * Creates an empty weighted quartet map
     */
    public WeightedQuartetMap() {
        super();
    }

    /**
     * Creates a weighted quartet map from a distance matrix
     * @param distanceMatrix The distance matrix to conver to quartets
     */
    public WeightedQuartetMap(DistanceMatrix distanceMatrix) {

        final int N = distanceMatrix.getNbTaxa();

        double[][] D = distanceMatrix.getMatrix();

        for (int a = 0; a < N - 3; a++) {

            for (int b = a + 1; b < N - 2; b++) {

                for (int c = b + 1; c < N - 1; c++) {

                    for (int d = c + 1; d < N; d++) {

                        double w1, w2, w3;

                        w1 = (D[a][c] + D[b][c] + D[a][d] + D[b][d] + -2 * D[a][b] - 2 * D[c][d]) / 4.0;
                        w2 = (D[a][b] + D[c][b] + D[a][d] + D[c][d] + -2 * D[a][c] - 2 * D[b][d]) / 4.0;
                        w3 = (D[a][c] + D[d][c] + D[a][b] + D[d][b] + -2 * D[a][d] - 2 * D[c][b]) / 4.0;

                        double min = Math.min(w1, Math.min(w2, w3));

                        this.put(new Quartet(a + 1, b + 1, c + 1, d + 1), w1 - min);
                        this.put(new Quartet(a + 1, c + 1, b + 1, d + 1), w2 - min);
                        this.put(new Quartet(a + 1, d + 1, b + 1, c + 1), w3 - min);
                    }
                }
            }
        }
    }

    /**
     * Adds a weighted quartet into the hash map.
     * @param quartet
     * @param weight
     */
    public void put(Quartet quartet, double weight) {

        Quartet sorted = quartet.createSortedQuartet();

        QuartetWeights weights = this.get(sorted);

        if (weights != null) {
            weights.update(sorted, quartet, weight);
        }
        else {
            super.put(sorted, new QuartetWeights(sorted, quartet, weight));
        }
    }


    /**
     * Already given the weights, so just dump these where they should be in the hash
     * @param quartet
     * @param weights
     */
    @Override
    public QuartetWeights put(Quartet quartet, QuartetWeights weights) {

        Quartet sorted = quartet.createSortedQuartet();

        super.put(sorted, weights);

        return weights;
    }


    /**
     * setWeight sets a length
     */
    public void incrementWeight(Quartet q, double increment) {

        Quartet sorted = q.createSortedQuartet();

        if (this.containsKey(sorted)) {
            this.setWeight(sorted, this.get(sorted).selectWeight(sorted, q) + increment);
        }
        else {
            this.put(sorted, increment);
        }
    }

    /**
     * Sets an edge weight for a quartet
     */
    public void setWeight(Quartet q, double weight) {

        Quartet sorted = q.createSortedQuartet();

        if (this.containsKey(sorted)) {
            this.get(sorted).update(sorted, q, weight);
        }
        else {
            this.put(sorted, weight);
        }
    }


    /**
     * Gets the length of a specific quartet.  If the quartet is not in the hash then simply return 0.0.
     * @param q The quartet to find in the hash.
     * @return The weight of the specified quartet if found in the hash, 0.0 otherwise.
     */
    public double getWeight(Quartet q) {

        Quartet sorted = q.createSortedQuartet();

        return this.containsKey(sorted) ? this.get(sorted).selectWeight(sorted, q) : 0.0;
    }



    public void translate(Taxa oldTaxa, Taxa newTaxa) {

        int NOld = oldTaxa.size();

        for (int iA = 0; iA < NOld - 3; iA++) {

            for (int iB = iA + 1; iB < NOld - 2; iB++) {

                for (int iC = iB + 1; iC < NOld - 1; iC++) {

                    for (int iD = iC + 1; iD < NOld; iD++) {

                        int a = iA + 1;
                        int b = iB + 1;
                        int c = iC + 1;
                        int d = iD + 1;

                        int nA = newTaxa.indexOf(oldTaxa.get(iA)) + 1;
                        int nB = newTaxa.indexOf(oldTaxa.get(iB)) + 1;
                        int nC = newTaxa.indexOf(oldTaxa.get(iC)) + 1;
                        int nD = newTaxa.indexOf(oldTaxa.get(iD)) + 1;

                        Quartet q1 = new Quartet(a, b, c, d);
                        Quartet q2 = new Quartet(a, c, b, d);
                        Quartet q3 = new Quartet(a, d, b, c);

                        this.setWeight(new Quartet(nA, nB, nC, nD), this.containsKey(q1) ? this.getWeight(q1) : 0.0);
                        this.setWeight(new Quartet(nA, nC, nB, nD), this.containsKey(q2) ? this.getWeight(q2) : 0.0);
                        this.setWeight(new Quartet(nA, nD, nB, nC), this.containsKey(q3) ? this.getWeight(q3) : 0.0);
                    }
                }
            }
        }
    }

    /**
     * Normalises all the quartet weights in this hash map
     * @param logscale Normalise using natural log scale, or not
     * @param useMax Use max or min
     */
    public void normalize(boolean logscale, boolean useMax) {

        for (QuartetWeights quartetWeights : this.values()) {
            quartetWeights.normalise(logscale, useMax);
        }
    }


    /**
     * note: this now simply computes a weighted sum. This is the part that may be done in any number of ways here,
     * take weighted sum of every quartet where the quartet is nonzero
     */
    public void weighedAdd(WeightedQuartetMap otherMap, double w) {

        for (Quartet q : otherMap.keySet()) {
            this.incrementWeight(q, otherMap.getWeight(q) * w);
        }
    }

    /**
     * Divides quartet weights in this map with those in summer.  Summer must contain the same quartets or be a super set
     * of this hash map otherwise a null pointer exception will be thrown
     * @param summer
     */
    public void divide(WeightedQuartetMap summer) {

        for (Quartet q : this.keySet()) {
            this.get(q).divide(summer.get(q));
        }
    }

    /**
     * Course through all quartets of taxonNames, if taxonNames (quartet entries) are contained in lesserNames add w to
     * summer (quartet)
     * @param taxa A superset of taxon names
     * @param lesserNames A subset of taxon names
     * @param weight The weight to apply
     */
    public void sum(Taxa taxa, Taxa lesserNames, double weight) {

        int N = taxa.size();

        for (int a = 0; a < N - 3; a++) {
            for (int b = a + 1; b < N - 2; b++) {
                for (int c = b + 1; c < N - 1; c++) {
                    for (int d = c + 1; d < N; d++) {
                        Quadruple quad = taxa.getQuadruple(a, b, c, d);

                        if (lesserNames.contains(quad)) {
                            this.scaleWeight(new Quartet(a + 1, b + 1, c + 1, d + 1), weight);
                        }
                    }
                }
            }
        }

    }

    public void scaleWeight(Quartet quartet, double weight) {

        Quartet sorted = quartet.createSortedQuartet();

        if (this.containsKey(sorted)) {
            this.setWeight(sorted, this.getWeight(sorted) * weight);
        }
        else {
            this.setWeight(sorted, weight);
        }
    }

    public MeanSumResult meanSum(List<Integer> aL, List<Integer> bL, List<Integer> cL, List<Integer> dL) {

        int count = 0;
        double score = 0;

        for (int a = 0; a < aL.size(); a++) {
            for (int b = 0; b < bL.size(); b++) {
                for (int c = 0; c < cL.size(); c++) {
                    for (int d = 0; d < dL.size(); d++) {

                        int x = aL.get(a);
                        int y = bL.get(b);
                        int u = cL.get(c);
                        int v = dL.get(d);

                        score += this.getWeight(new Quartet(x, y, u, v));

                        count++;
                    }
                }
            }
        }

        return new MeanSumResult(count, score);
    }


    public static class MeanSumResult {

        private int count;
        private double score;

        public MeanSumResult(final int count, final double score) {
            this.count = count;
            this.score = score;
        }

        public int getCount() {
            return count;
        }

        public double getScore() {
            return score;
        }

        public double getMean() { return score / (double)count; }
    }

    public void addSplit(Split split) {

        // We don't bother with trivial splits as trivial splits match no quartets
        if (!split.onExternalEdge()) {

            // so, for all quartets in here, add the length to their value
            final int aSize = split.getASide().size();
            final int bSize = split.getBSide().size();

            // I think it will work out a little faster doing things this way... if that turns out not to be true consider
            // optimising this.
            int[] setA = split.getASide().toArray();
            int[] setB = split.getBSide().toArray();

            for (int iA1 = 0; iA1 < aSize - 1; iA1++) {

                for (int iA2 = iA1 + 1; iA2 < aSize; iA2++) {

                    int a1 = setA[iA1];
                    int a2 = setA[iA2];

                    for (int iB1 = 0; iB1 < bSize - 1; iB1++) {

                        for (int iB2 = iB1 + 1; iB2 < bSize; iB2++) {

                            int b1 = setB[iB1];
                            int b2 = setB[iB2];

                            this.incrementWeight(new Quartet(a1, a2, b1, b2), split.getWeight());
                        }
                    }
                }
            }
        }
    }
}
