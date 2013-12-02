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

import java.util.ArrayList;
import java.util.List;

/**
 * //TODO might want to consider making this a hash, the key is the quartet indicies and the value are the weights
 */
public class QuartetWeightsList extends ArrayList<WeightedQuartet> {

    /**
     * Creates an empty QuartetWeightsList
     */
    public QuartetWeightsList() {
        this(0);
    }

    /**
     * Creates a list of QuartetWeightsList of specified size
     * @param size
     */
    public QuartetWeightsList(final int size) {
        super(size);

        for(int i = 0; i < size; i++) {
            this.add(new WeightedQuartet());
        }
    }

    /**
     * Creates a list of QuartetWeightings from a distance matrix
     * @param distanceMatrix
     */
    public QuartetWeightsList(DistanceMatrix distanceMatrix) {

        this(Quartet.over4(distanceMatrix.getNbTaxa()));

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

                        this.setWeight(new Quartet(a + 1, b + 1, c + 1, d + 1), w1 - min);
                        this.setWeight(new Quartet(a + 1, c + 1, b + 1, d + 1), w2 - min);
                        this.setWeight(new Quartet(a + 1, d + 1, b + 1, c + 1), w3 - min);
                    }
                }
            }
        }
    }

    /**
     * getWeight gets a length
     */
    public double getWeight(Quartet quartet) {

        Quartet sortedQuartet = quartet.createSortedQuartet();

        QuartetWeights w = this.get(sortedQuartet.getIndex()).getQuartetWeighting();

        return sortedQuartet.selectWeight(quartet, w);
    }

    /**
     * setWeight sets three weights
     */
    public void setWeight(Quartet q, QuartetWeights weights) {

        Quartet sorted = q.createSortedQuartet();

        QuartetWeights w = sorted.selectWeighting(q, weights);

        // TODO Probably shouldn't have an empty quartet here
        this.set(q.getIndex(), new WeightedQuartet(new Quartet(), w));
    }

    /**
     * setWeight sets a length
     */
    public void incrementWeight(Quartet q, double increment) {
        this.setWeight(q, this.getWeight(q) + increment);
    }

    /**
     * setWeight sets a length
     */
    public void setWeight(Quartet q, double newW) {

        Quartet sortedQuartet = q.createSortedQuartet();

        WeightedQuartet w = this.get(sortedQuartet.getIndex());

        sortedQuartet.updateWeighting(q, w.getQuartetWeighting(), newW);

        this.set(sortedQuartet.getIndex(), w);
    }



    public void divide(QuartetWeightsList summer) {

        if (this.size() != summer.size()) {
            throw new IllegalStateException("This quartet system and summer are not the same size");
        }

        for (int n = 0; n < this.size(); n++) {
            this.get(n).getQuartetWeighting().divide(summer.get(n).getQuartetWeighting());
        }
    }

    /**
     * note: this now simply computes a weighted sum. This is the part that may be done in any number of ways here,
     * take weighted sum of every quartet where the quartet is nonzero
     */
    public void add(QuartetWeightsList other, double w) {



        for (int n = 0; n < other.size(); n++) {
            this.get(n).getQuartetWeighting().weightedSum(other.get(n).getQuartetWeighting(), w);
        }
    }


    public void normalize(boolean useMax) {

        for (WeightedQuartet quartet : this) {
            quartet.getQuartetWeighting().normalise(false, useMax);
        }
    }

    public void logNormalize(boolean useMax) {

        for (WeightedQuartet quartet : this) {
            quartet.getQuartetWeighting().normalise(true, useMax);
        }
    }

    public void translate(Taxa oldTaxa, Taxa newTaxa) {

        int NNew = newTaxa.size();
        int NOld = oldTaxa.size();

        int newSize = Quartet.over4(NNew);
        this.ensureCapacity(newSize);

        for(int i = 0; i < newSize; i++) {
            this.add(new WeightedQuartet());
        }

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

                        this.setWeight(new Quartet(nA, nB, nC, nD), this.getWeight(new Quartet(a, b, c, d)));
                        this.setWeight(new Quartet(nA, nC, nB, nD), this.getWeight(new Quartet(a, c, b, d)));
                        this.setWeight(new Quartet(nA, nD, nB, nC), this.getWeight(new Quartet(a, d, b, c)));
                    }
                }
            }
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
    }


    /**
     * So... we go through taxonNames, which is the metalist check every quartet defined for it take the taxonList for
     * the objects in loader, and their corresponding weights if a quartet is defined for that list, add its length to
     * the corresponding summer position summer must have been translated according to the metalist
     * @param taxa
     * @param metaTaxa
     * @param weights
     */
    public void sum(Taxa taxa, List<Taxa> metaTaxa, List<Double> weights) {

        for(int i = 0; i < metaTaxa.size(); i++) {

            double w = weights.get(i);
            Taxa lesserNames = metaTaxa.get(i);

            // course through all quartets of taxonNames
            // if taxonNames (quartet entries) are contained in lesserNames
            // add w to summer (quartet)

            int N = taxa.size();

            for (int a = 0; a < N - 3; a++) {
                for (int b = a + 1; b < N - 2; b++) {
                    for (int c = b + 1; c < N - 1; c++) {
                        for (int d = c + 1; d < N; d++) {
                            Quadruple quad = taxa.getQuadruple(a, b, c, d);

                            if (lesserNames.contains(quad)) {

                                Quartet quartet = new Quartet(a + 1, b + 1, c + 1, d + 1);

                                double oldW = this.getWeight(quartet);

                                this.setWeight(quartet, new QuartetWeights(oldW + w, oldW + w, oldW + w));
                            }
                        }
                    }
                }
            }
        }
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