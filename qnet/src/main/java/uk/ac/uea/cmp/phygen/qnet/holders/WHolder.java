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

import uk.ac.uea.cmp.phygen.core.ds.TaxonList;
import uk.ac.uea.cmp.phygen.core.ds.quartet.Quartet;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetWeights;
import uk.ac.uea.cmp.phygen.core.math.tuple.Triplet;

import java.util.List;

public class WHolder {

    public WHolder(List<TaxonList> theLists, int N, QuartetWeights theQuartetWeights) {

        counts = new Triplet[Quartet.over4(N)];
        weights = new Triplet[Quartet.over4(N)];

        for (int n = 0; n < Quartet.over4(N); n++) {

            counts[n] = new Triplet<Integer>(0, 0, 0);
            weights[n] = new Triplet<Double>(0.0, 0.0, 0.0);
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

                        int a = -1, b = -1, c = -1, d = -1;

                        for (int m = 0; m < theLists.size(); m++) {

                            TaxonList tL = theLists.get(m);

                            if (tL.contains(i)) {

                                a = m;
                                break;

                            }

                        }

                        for (int m = 0; m < theLists.size(); m++) {

                            TaxonList tL = theLists.get(m);

                            if (tL.contains(j)) {

                                b = m;
                                break;

                            }

                        }

                        for (int m = 0; m < theLists.size(); m++) {

                            TaxonList tL = theLists.get(m);

                            if (tL.contains(k)) {

                                c = m;
                                break;

                            }

                        }

                        for (int m = 0; m < theLists.size(); m++) {

                            TaxonList tL = theLists.get(m);

                            if (tL.contains(l)) {

                                d = m;
                                break;

                            }

                        }

                        if (a == b || a == c || a == d || b == c || b == d || c == d) {

                            // if on the same path, no quartets meet the conditions

                            counts[Quartet.over4(l - 1) + Quartet.over3(k - 1)
                                    + Quartet.over2(j - 1) + Quartet.over1(i - 1)] = new Triplet<Integer>(0, 0, 0);
                            weights[Quartet.over4(l - 1) + Quartet.over3(k - 1)
                                    + Quartet.over2(j - 1) + Quartet.over1(i - 1)] = new Triplet<Double>(0.0, 0.0, 0.0);

                            continue;

                        }

                        // otherwise:
                        // we now have the list indices

                        int count1 = 0;
                        double weight1 = 0.0;
                        int count2 = 0;
                        double weight2 = 0.0;
                        int count3 = 0;
                        double weight3 = 0.0;

                        TaxonList A = theLists.get(a);
                        TaxonList B = theLists.get(b);
                        TaxonList C = theLists.get(c);
                        TaxonList D = theLists.get(d);

                        // we now have four non-same lists

                        for (int xA = 0; xA < A.size(); xA++) {

                            for (int xB = 0; xB < B.size(); xB++) {

                                for (int xC = 0; xC < C.size(); xC++) {

                                    for (int xD = 0; xD < D.size(); xD++) {

                                        // this is a unique, suitable quartet

                                        int yA = A.get(xA);
                                        int yB = B.get(xB);
                                        int yC = C.get(xC);
                                        int yD = D.get(xD);

                                        count1++;
                                        weight1 += theQuartetWeights.getWeight(new Quartet(yA, yB, yC, yD));
                                        count2++;
                                        weight2 += theQuartetWeights.getWeight(new Quartet(yA, yC, yB, yD));
                                        count3++;
                                        weight3 += theQuartetWeights.getWeight(new Quartet(yA, yD, yB, yC));

                                    }

                                }

                            }

                        }

                        counts[Quartet.over4(l - 1) + Quartet.over3(k - 1)
                                + Quartet.over2(j - 1) + Quartet.over1(i - 1)] = new Triplet<>(count1, count2, count3);
                        weights[Quartet.over4(l - 1) + Quartet.over3(k - 1)
                                + Quartet.over2(j - 1) + Quartet.over1(i - 1)] = new Triplet<>(weight1, weight2, weight3);

                    }

                }

            }

        }

    }

    public int getN(int i, int j, int k, int l) {

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

        return counts[Quartet.over4(x - 1) + Quartet.over3(y - 1)
                + Quartet.over2(u - 1) + Quartet.over1(v - 1)].get(position);

    }

    public void setN(int i, int j, int k, int l, int newN) {

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

        Triplet<Integer> iT = counts[Quartet.over4(x - 1) + Quartet.over3(y - 1)
                + Quartet.over2(u - 1) + Quartet.over1(v - 1)];

        iT.set(position, newN);

        counts[Quartet.over4(x - 1) + Quartet.over3(y - 1)
                + Quartet.over2(u - 1) + Quartet.over1(v - 1)] = iT;

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

        return weights[Quartet.over4(x - 1) + Quartet.over3(y - 1)
                + Quartet.over2(u - 1) + Quartet.over1(v - 1)].get(position);

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

        Triplet t = weights[Quartet.over4(x - 1) + Quartet.over3(y - 1)
                + Quartet.over2(u - 1) + Quartet.over1(v - 1)];

        t.set(position, newW);

        weights[Quartet.over4(x - 1) + Quartet.over3(y - 1)
                + Quartet.over2(u - 1) + Quartet.over1(v - 1)] = t;

    }

    Triplet<Integer>[] counts;
    Triplet<Double>[] weights;
}
