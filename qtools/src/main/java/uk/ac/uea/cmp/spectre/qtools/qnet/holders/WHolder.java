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

package uk.ac.uea.cmp.spectre.qtools.qnet.holders;

import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.quad.SpectreQuad;
import uk.ac.uea.cmp.spectre.core.ds.quad.quartet.CanonicalWeightedQuartetMap;
import uk.ac.uea.cmp.spectre.core.ds.quad.quartet.QuartetUtils;
import uk.ac.uea.cmp.spectre.core.math.tuple.Triplet;
import uk.ac.uea.cmp.spectre.qtools.qnet.QNetException;

import java.util.List;

public class WHolder {

    private Triplet<Integer>[] counts;
    private Triplet<Double>[] weights;

    public WHolder(List<IdentifierList> paths, int N, CanonicalWeightedQuartetMap quartetMap) throws QNetException {

        this.counts = new Triplet[QuartetUtils.over4(N)];
        this.weights = new Triplet[QuartetUtils.over4(N)];

        for (int n = 0; n < QuartetUtils.over4(N); n++) {

            this.counts[n] = new Triplet<>(0, 0, 0);
            this.weights[n] = new Triplet<>(0.0, 0.0, 0.0);
        }

        // now, we store everything properly

        for (int i = 1; i < N - 2; i++) {

            for (int j = i + 1; j < N - 1; j++) {

                for (int k = j + 1; k < N; k++) {

                    for (int l = k + 1; l < N + 1; l++) {

                        // for every pair i, j

                        // find which list they are in
                        // for those two indices
                        // take all combinations of path entries
                        // against all combinations of other paths by all other path entries

                        IdentifierList A = Holders.findFirstPathContainingId(paths, i);
                        IdentifierList B = Holders.findFirstPathContainingId(paths, j);
                        IdentifierList C = Holders.findFirstPathContainingId(paths, k);
                        IdentifierList D = Holders.findFirstPathContainingId(paths, l);

                        if (A == null || B == null || C == null || D == null) {
                            throw new QNetException("Could not find paths for all indicies: " + i + ", " + j + ", " + k + ", " + l);
                        }

                        if (A == B || A == C || A == D || B == C || B == D || C == D) {

                            // if on the same path, no quartets meet the conditions

                            int index = QuartetUtils.sumOvers(i - 1, j - 1, k - 1, l - 1);
                            counts[index] = new Triplet<>(0, 0, 0);
                            weights[index] = new Triplet<>(0.0, 0.0, 0.0);
                        } else {

                            int count1 = 0;
                            double weight1 = 0.0;
                            int count2 = 0;
                            double weight2 = 0.0;
                            int count3 = 0;
                            double weight3 = 0.0;

                            // we now have four non-same lists
                            for (int xA = 0; xA < A.size(); xA++) {

                                for (int xB = 0; xB < B.size(); xB++) {

                                    for (int xC = 0; xC < C.size(); xC++) {

                                        for (int xD = 0; xD < D.size(); xD++) {

                                            // this is a unique, suitable quartet

                                            int yA = A.get(xA).getId();
                                            int yB = B.get(xB).getId();
                                            int yC = C.get(xC).getId();
                                            int yD = D.get(xD).getId();

                                            SpectreQuad q1 = new SpectreQuad(yA, yB, yC, yD);
                                            SpectreQuad q2 = new SpectreQuad(yA, yC, yB, yD);
                                            SpectreQuad q3 = new SpectreQuad(yA, yD, yB, yC);


                                            count1++;
                                            weight1 += quartetMap.containsKey(q1) ?
                                                    quartetMap.get(q1) :
                                                    0.0;

                                            count2++;
                                            weight2 += quartetMap.containsKey(q2) ?
                                                    quartetMap.get(q2) :
                                                    0.0;

                                            count3++;
                                            weight3 += quartetMap.containsKey(q3) ?
                                                    quartetMap.get(q3) :
                                                    0.0;

                                        }
                                    }
                                }
                            }

                            int index = QuartetUtils.sumOvers(i - 1, j - 1, k - 1, l - 1);

                            counts[index] = new Triplet<>(count1, count2, count3);
                            weights[index] = new Triplet<>(weight1, weight2, weight3);
                        }
                    }
                }
            }
        }
    }


    public double getW(int i, int j, int k, int l) {

        /**
         *
         * Create size-ordered quadruple x, y, u, v from i, j, k, l
         *
         */
        int position = 0;

        if ((i > k && j > k && i > l && j > l)
                || (i < k && j < k && i < l && j < l)) {

            // both largest to the left or to the right

            position = 1;
        } else if (((i > k && i > l && ((j > l && j < k) || (j < l && j > k)))
                || (j > k && j > l && ((i > l && i < k) || (i < l && i > k))))
                || (l > j && l > i && ((k > i && k < j) || (k < i && k > j)))
                || (k > j && k > i && ((l > i && l < j) || (l < i && l > j)))) {

            // largest and third to the left or to the right

            position = 2;
        } else if ((i > k && i > l && (j < l && j < k))
                || (j > k && j > l && (i < l && i < k))
                || (l > j && l > i && (k < i && k < j))
                || (k > j && k > i && (l < i && l < j))) {

            // largest and smallest to the left or to the right

            position = 3;
        }

        int x = i;
        int y = j;
        int u = k;
        int v = l;
        int m;

        if (y > x) {

            m = x;
            x = y;
            y = m;

        }

        if (u > x) {

            m = x;
            x = u;
            u = m;

        }

        if (v > x) {

            m = x;
            x = v;
            v = m;

        }

        if (u > y) {

            m = y;
            y = u;
            u = m;

        }

        if (v > y) {

            m = y;
            y = v;
            v = m;

        }

        if (v > u) {

            m = u;
            u = v;
            v = m;
        }

        return weights[QuartetUtils.sumOvers(v - 1, u - 1, y - 1, x - 1)].get(position);
    }

    public void setW(int i, int j, int k, int l, double newW) {

        /**
         *
         * Create size-ordered quadruple x, y, u, v from i, j, k, l
         *
         */
        int position = 0;

        if ((i > k && j > k && i > l && j > l)
                || (i < k && j < k && i < l && j < l)) {

            // both largest to the left or to the right

            position = 1;

        } else if (((i > k && i > l && ((j > l && j < k) || (j < l && j > k)))
                || (j > k && j > l && ((i > l && i < k) || (i < l && i > k))))
                || (l > j && l > i && ((k > i && k < j) || (k < i && k > j)))
                || (k > j && k > i && ((l > i && l < j) || (l < i && l > j)))) {

            // largest and third to the left or to the right

            position = 2;

        } else if ((i > k && i > l && (j < l && j < k))
                || (j > k && j > l && (i < l && i < k))
                || (l > j && l > i && (k < i && k < j))
                || (k > j && k > i && (l < i && l < j))) {

            // largest and smallest to the left or to the right

            position = 3;

        }

        int x = i;
        int y = j;
        int u = k;
        int v = l;
        int m;

        if (y > x) {

            m = x;
            x = y;
            y = m;
        }

        if (u > x) {

            m = x;
            x = u;
            u = m;
        }

        if (v > x) {

            m = x;
            x = v;
            v = m;
        }

        if (u > y) {

            m = y;
            y = u;
            u = m;
        }

        if (v > y) {

            m = y;
            y = v;
            v = m;
        }

        if (v > u) {

            m = u;
            u = v;
            v = m;
        }

        Triplet t = weights[QuartetUtils.sumOvers(v - 1, u - 1, y - 1, x - 1)];

        t.set(position, newW);

        weights[QuartetUtils.sumOvers(v - 1, u - 1, y - 1, x - 1)] = t;

    }

}
