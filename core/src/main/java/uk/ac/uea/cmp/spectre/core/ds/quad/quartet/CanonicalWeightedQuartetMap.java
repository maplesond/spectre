/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2017  UEA School of Computing Sciences
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package uk.ac.uea.cmp.spectre.core.ds.quad.quartet;

import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.quad.Quad;
import uk.ac.uea.cmp.spectre.core.ds.quad.SpectreQuad;
import uk.ac.uea.cmp.spectre.core.ds.split.Split;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dan on 04/01/14.
 */
public class CanonicalWeightedQuartetMap extends HashMap<Quad, Double> {

    public CanonicalWeightedQuartetMap() {
        super();
    }

    /**
     * Creates a weighted quartet map from a distance matrix
     *
     * @param distanceMatrix The distance matrix to conver to quartets
     */
    public CanonicalWeightedQuartetMap(DistanceMatrix distanceMatrix) {

        final int N = distanceMatrix.size();

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

                        this.put(new SpectreQuad(a + 1, b + 1, c + 1, d + 1), w1 - min);
                        this.put(new SpectreQuad(a + 1, c + 1, b + 1, d + 1), w2 - min);
                        this.put(new SpectreQuad(a + 1, d + 1, b + 1, c + 1), w3 - min);
                    }
                }
            }
        }
    }

    public CanonicalWeightedQuartetMap(WeightedQuartetGroupMap groupMap) {
        super();

        for (Map.Entry<Quad, QuartetWeights> entry : groupMap.entrySet()) {

            Quad sorted = entry.getKey();
            QuartetWeights weights = entry.getValue();

            this.put(new SpectreQuad(sorted), weights.getA());
            this.put(new SpectreQuad(sorted.getA(), sorted.getC(), sorted.getB(), sorted.getD()), weights.getB());
            this.put(new SpectreQuad(sorted.getA(), sorted.getD(), sorted.getB(), sorted.getC()), weights.getC());
        }
    }


    public void incrementWeight(Quad quad, double weightIncrement) {

        this.put(quad, this.containsKey(quad) ? this.get(quad) + weightIncrement : weightIncrement);
    }

    /**
     * Scales the current quartet weight if quartet is already present.  If quartet is not present then we just set the weight.
     *
     * @param quad The quartet to scale
     * @param weight  The weight to apply
     */
    public void scaleWeight(Quad quad, double weight) {

        this.put(quad, this.containsKey(quad) ? this.get(quad) * weight : weight);
    }

    /**
     * Divides quartet weights in this map with those in summer.  Summer must contain the same quartets or be a super set
     * of this hash map otherwise a null pointer exception will be thrown
     *
     * @param other Other quartets to divide from those in this
     */
    public void divide(CanonicalWeightedQuartetMap other) {

        for (Quad q : this.keySet()) {

            if (other.containsKey(q)) {
                this.put(q, this.get(q) / other.get(q));
            }
        }
    }

    public void addSplit(Split split) {

        // We don't bother with trivial splits as trivial splits match no quartets
        if (!split.onExternalEdge()) {

            // so, for all quartets in here, add the length to their value
            final int aSize = split.getASideSize();
            final int bSize = split.getBSideSize();

            // I think it will work out a little faster doing things this way... if that turns out not to be true consider
            // optimising this.
            int[] setA = split.getASideAsIntArray();
            int[] setB = split.getBSideAsIntArray();

            for (int iA1 = 0; iA1 < aSize - 1; iA1++) {

                for (int iA2 = iA1 + 1; iA2 < aSize; iA2++) {

                    int a1 = setA[iA1];
                    int a2 = setA[iA2];

                    for (int iB1 = 0; iB1 < bSize - 1; iB1++) {

                        for (int iB2 = iB1 + 1; iB2 < bSize; iB2++) {

                            int b1 = setB[iB1];
                            int b2 = setB[iB2];

                            this.incrementWeight(new SpectreQuad(a1, a2, b1, b2), split.getWeight());
                        }
                    }
                }
            }
        }
    }


}
