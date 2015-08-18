/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2015  UEA School of Computing Sciences
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

package uk.ac.uea.cmp.spectre.flatnj;

import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;

/**
 * Estimates split weights from multiple sequence alignment.
 *
 * @author balvociute
 */
public class SplitsEstimator {
    private int size = 75;
    private int min = 48;

    private AlignmentFilter filter;
    private char[][] sequences;

    private DistanceMatrix dm;
    private D[][][][] D;

    /**
     * Initializes splits estimator. Creates matrix for storing weights of
     * possible character distributions.
     */
    public SplitsEstimator() {
        D = new D[size][size][size][size];
    }

    public SplitsEstimator(DistanceMatrix dm) {
        this.dm = dm;
        D = new D[size][size][size][size];
    }

    public double[] estimate(char[][] sequences) {
        this.sequences = sequences;
        /* filter is set to be AlignmentFilterSimple what means that only
         columns containing gaps will be omited. */
        filter = dm != null && dm.getTaxa() != null ?
                new AlignmentFilterDistanceMatrix(dm) :
                new AlignmentFilter();

        return doMatrixBasedEstimation();
    }

    public double[] estimate(char[][] sequences, AlignmentFilter filter) {
        this.sequences = sequences;
        this.filter = filter;
        return doMatrixBasedEstimation();
    }

    private double[] doMatrixBasedEstimation() {
        double[] weights = new double[7];
        int columnsConsidered = 0;
        int columnsAtAll = sequences[0].length;


        for (int i = 0; i < columnsAtAll; i++) {
            char[] s = new char[4];
            boolean hasSpace = false;

            for (int j = 0; j < 4; j++) {
                if (filter.isAllowed(sequences[j][i])) {
                    s[j] = sequences[j][i];
                } else {
                    hasSpace = true;
                    break;
                }
            }

            if (!hasSpace) {
                columnsConsidered++;

                D d = getD(s);

                updateWeights(weights, d);
            }
        }

        for (int i = 0; i < weights.length; i++) {
            weights[i] = weights[i] / columnsConsidered;
        }
        return weights;
    }

    //TODO This needs double checking.  I modified the code for Distance Matrices so that we use Strings rather than chars
    // to get the index of each row and column.   It seems like the original code used the ASCII value of the char to get
    // the indices position.  Need to double check that we are ok with 1-based numbers, possibly this should be changed
    // to 0-based seeing as we are working with array indices.
    private D getD(char[] s) {
        int id1 = this.dm != null ? this.dm.getTaxa().getByName(Character.toString(s[0])).getId() : (int) s[0] - min;
        int id2 = this.dm != null ? this.dm.getTaxa().getByName(Character.toString(s[1])).getId() : (int) s[1] - min;
        int id3 = this.dm != null ? this.dm.getTaxa().getByName(Character.toString(s[2])).getId() : (int) s[2] - min;
        int id4 = this.dm != null ? this.dm.getTaxa().getByName(Character.toString(s[3])).getId() : (int) s[3] - min;

        D d;
        if (D[id1][id2][id3][id4] == null) {
            d = new D(dm, s);
            D[id1][id2][id3][id4] = d;
        } else {
            d = D[id1][id2][id3][id4];
        }
        return d;
    }


    private void updateWeights(double[] weights, D d) {
        for (int j1 = 0; j1 < weights.length; j1++) {
            weights[j1] += d.weights[j1];
        }
    }

    private class D {
        double[][] d;
        double[] sum;
        double max;
        double[] weights;

        public D(DistanceMatrix dm, char[] s) {
            d = new double[4][4];
            sum = new double[3];
            weights = new double[7];

            for (int i1 = 0; i1 < s.length; i1++) {
                if (dm != null) {
                    d[i1][i1] = dm.getDistance(s[i1], s[i1]);
                } else {
                    d[i1][i1] = 0;
                }
                for (int i2 = i1 + 1; i2 < s.length; i2++) {
                    if (dm != null) {
                        d[i1][i2] = dm.getDistance(s[i1], s[i2]);
                        d[i2][i1] = dm.getDistance(s[i2], s[i1]);
                    } else {
                        if (s[i1] != s[i2]) {
                            d[i1][i2] = 1;
                            d[i2][i1] = 1;
                        } else {
                            d[i1][i2] = 0;
                            d[i2][i1] = 0;
                        }
                    }
                    if (i1 == 0) {
                        sum[i2 - 1] = d[i1][i2];
                    } else {
                        sum[5 - (i1 + i2)] += d[i1][i2];
                    }
                }
            }
            max = (sum[0] > sum[1]) ? sum[0] : sum[1];
            max = (max < sum[2]) ? sum[2] : max;

            for (int j1 = 0; j1 < weights.length; j1++) {
                double w = 0;
                if (j1 < 4) {
                    for (int j2 = 0; j2 < 4; j2++) {
                        if (j1 != j2) {
                            w += d[j1][j2];
                        }
                    }
                    w -= max;
                    w /= 2;
                } else {
                    int j2 = j1 - 3;
                    int j3 = (j2 == 1) ? 2 : 1;
                    int j4 = (j2 == 3) ? 2 : 3;
                    w = 0.5 * (max - d[0][j2] - d[j3][j4]);
                }
                weights[j1] = w;
            }
        }
    }
}
