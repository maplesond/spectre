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

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.quartet.Quartet;
import uk.ac.uea.cmp.phygen.core.ds.quartet.WeightedQuartetMap;

import java.util.List;


public class NSHolder extends AbstractBasicHolder {

    public NSHolder(List<Taxa> taxaSets, int N, WeightedQuartetMap theQuartetWeights) {

        super(N, taxaSets, theQuartetWeights);
    }

    @Override
    protected Pair<Integer, Double> calcCountWeight(Taxa A, Taxa B, int a, int b) {

        // now, we store everything properly

        int count = 0;
        double weight = 0.0;

        for (int c = 0; c < taxaSets.size() - 1; c++) {

            for (int d = c + 1; d < taxaSets.size(); d++) {

                if (c != a && c != b && d != a && d != b) {

                    Taxa C = taxaSets.get(c);
                    Taxa D = taxaSets.get(d);

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

                                    // DAN:  Add this check, which wasn't here before, because we are now stricter with regards to
                                    // what is and what isn't a quartet
                                    if (Quartet.areDistinct(yA, yB, yC, yD)) {
                                        count++;
                                        weight += theQuartetWeights.getWeight(new Quartet(yA, yB, yC, yD));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return new ImmutablePair<>(count, weight);
    }
}
