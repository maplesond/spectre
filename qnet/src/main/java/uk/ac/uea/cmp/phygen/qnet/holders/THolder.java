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
package uk.ac.uea.cmp.phygen.qnet.holders;

import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.quartet.Quartet;
import uk.ac.uea.cmp.phygen.core.ds.quartet.WeightedQuartetMap;

import java.util.List;

public class THolder {

    public THolder(List<Taxa> taxaSets, int N, WeightedQuartetMap theQuartetWeights) {

        counts = new Integer[N][N][N];
        weights = new Double[N][N][N];

        for (int n1 = 1; n1 < N + 1; n1++) {

            for (int n2 = 1; n2 < N + 1; n2++) {

                for (int n3 = 1; n3 < N + 1; n3++) {

                    counts[n1 - 1][n2 - 1][n3 - 1] = new Integer(0);
                    weights[n1 - 1][n2 - 1][n3 - 1] = new Double(0.0);
                }
            }
        }

        for (int i = 1; i < N + 1; i++) {

            for (int j = 1; j < N + 1; j++) {

                // here, we must have that i and j are not on the same path
                // and we want their paths

                int a = -1, b = -1;

                for (int m = 0; m < taxaSets.size(); m++) {

                    Taxa tL = taxaSets.get(m);

                    if (tL.contains(i)) {

                        a = m;
                        break;
                    }
                }

                for (int m = 0; m < taxaSets.size(); m++) {

                    Taxa tL = taxaSets.get(m);

                    if (tL.contains(j)) {

                        b = m;
                        break;
                    }
                }

                if (a == b) {

                    // if on the same path, no quartets meet the conditions

                    continue;
                }

                Taxa A = taxaSets.get(a);
                Taxa B = taxaSets.get(b);

                for (int k = 1; k < N + 1; k++) {

                    int c = -1;

                    // and we must have that k is not on the path of i, j

                    for (int m = 0; m < taxaSets.size(); m++) {

                        Taxa tL = taxaSets.get(m);

                        if (tL.contains(k)) {

                            c = m;
                            break;
                        }
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

                                        count++;
                                        weight += theQuartetWeights.getWeight(new Quartet(yA1, yB, yA2, yC));
                                    }
                                }
                            }
                        }

                        counts[i - 1][j - 1][k - 1] = new Integer(count);
                        weights[i - 1][j - 1][k - 1] = new Double(weight);
                    }
                }
            }
        }
    }

    public int getN(int i, int j, int k) {
        return counts[i - 1][j - 1][k - 1].intValue();
    }

    public void setN(int i, int j, int k, int newN) {
        counts[i - 1][j - 1][k - 1] = new Integer(newN);
    }

    public double getT(int i, int j, int k) {
        return weights[i - 1][j - 1][k - 1].doubleValue();
    }

    public void setT(int i, int j, int k, double newT) {
        weights[i - 1][j - 1][k - 1] = new Double(newT);
    }

    Integer[][][] counts;
    Double[][][] weights;
}
