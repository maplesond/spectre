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
package uk.ac.uea.cmp.phygen.superq.qnet.holders;

import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.quartet.Quartet;
import uk.ac.uea.cmp.phygen.core.ds.quartet.WeightedQuartetMap;

import java.util.List;

public class THolder {


    private int[][][] counts;
    private double[][][] weights;

    public THolder(List<Taxa> taxaSets, int N, WeightedQuartetMap theQuartetWeights) {

        counts = new int[N][N][N];
        weights = new double[N][N][N];


        for (int i = 0; i < N; i++) {

            for (int j = 0; j < N; j++) {

                // here, we must have that i and j are not on the same path
                // and we want their paths

                int a = -1, b = -1;

                for (int m = 0; m < taxaSets.size(); m++) {

                    Taxa tL = taxaSets.get(m);

                    if (tL.containsId(i)) {

                        a = m;
                        break;
                    }
                }

                for (int m = 0; m < taxaSets.size(); m++) {

                    Taxa tL = taxaSets.get(m);

                    if (tL.containsId(j)) {

                        b = m;
                        break;
                    }
                }

                if (a == -1) {
                    throw new IllegalStateException("Could not find taxaset associated with i: " + i);
                }

                if (b == -1) {
                    throw new IllegalStateException("Could not find taxaset associated with j: " + j);
                }

                if (a == b) {

                    // if on the same path, no quartets meet the conditions
                    continue;
                }

                Taxa A = taxaSets.get(a);
                Taxa B = taxaSets.get(b);

                for (int k = 0; k < N; k++) {

                    int c = -1;

                    // and we must have that k is not on the path of i, j

                    for (int m = 0; m < taxaSets.size(); m++) {

                        Taxa tL = taxaSets.get(m);

                        if (tL.containsId(k)) {

                            c = m;
                            break;
                        }
                    }

                    if (c == -1) {
                        throw new IllegalStateException("Could not find taxaset associated with k: " + k);
                    }

                    if (c != a && c != b) {

                        // yay! uniqueness

                        int count = 0;
                        double weight = 0.0;

                        Taxa C = taxaSets.get(c);

                        for (int xA1 = 0; xA1 < A.size() - 1; xA1++) {

                            for (int xA2 = xA1 + 1; xA2 < A.size(); xA2++) {

                                for (int xB = 0; xB < B.size(); xB++) {

                                    for (int xC = 0; xC < C.size(); xC++) {

                                        // this is a unique, suitable quartet

                                        int yA1 = A.get(xA1).getId();
                                        int yA2 = A.get(xA2).getId();
                                        int yB = B.get(xB).getId();
                                        int yC = C.get(xC).getId();

                                        // DAN:  Add this check, which wasn't here before, because we are now stricter with regards to
                                        // what is and what isn't a quartet
                                        if (Quartet.areDistinct(yA1, yA2, yB, yC)) {
                                            count++;
                                            weight += theQuartetWeights.getWeight(new Quartet(yA1, yB, yA2, yC));
                                        }
                                    }
                                }
                            }
                        }

                        counts[i][j][k] = count;
                        weights[i][j][k] = weight;
                    }
                }
            }
        }
    }

    public int getN(int i, int j, int k) {
        return counts[i][j][k];
    }

    public void setN(int i, int j, int k, int newN) {
        counts[i][j][k] = newN;
    }

    public double getT(int i, int j, int k) {
        return weights[i][j][k];
    }

    public void setT(int i, int j, int k, double newT) {
        weights[i][j][k] = newT;
    }

}
