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
import uk.ac.uea.cmp.phygen.core.ds.quartet.CanonicalWeightedQuartetMap;
import uk.ac.uea.cmp.phygen.core.ds.quartet.Quartet;

import java.util.List;

public class U0Holder extends AbstractBasicHolder {

    public U0Holder(List<Taxa> taxaSets, int N, CanonicalWeightedQuartetMap theQuartetWeights) {

        super(N, taxaSets, theQuartetWeights);
    }

    @Override
    protected Pair<Integer, Double> calcCountWeight(Taxa A, Taxa B) {

        int count = 0;
        double weight = 0.0;

        // we now have two non-same lists

        for (int xA1 = 0; xA1 < A.size() - 1; xA1++) {

            for (int xA2 = xA1 + 1; xA2 < A.size(); xA2++) {

                for (int xB1 = 0; xB1 < B.size() - 1; xB1++) {

                    for (int xB2 = xB1 + 1; xB2 < B.size(); xB2++) {

                        // this is a unique, suitable quartet

                        int yA1 = A.get(xA1).getId();
                        int yA2 = A.get(xA2).getId();
                        int yB1 = B.get(xB1).getId();
                        int yB2 = B.get(xB2).getId();

                        // DAN:  Add this check, which wasn't here before, because we are now stricter with regards to
                        // what is and what isn't a quartet
                        if (Quartet.areDistinct(yA1, yA2, yB1, yB2)) {

                            Quartet q1 = new Quartet(yA1, yB1, yA2, yB2);
                            Quartet q2 = new Quartet(yA2, yB2, yA1, yB1);

                            count++;
                            weight += theQuartetWeights.containsKey(q1) ?
                                    theQuartetWeights.get(q1) :
                                    0.0;

                            count++;
                            weight += theQuartetWeights.containsKey(q2) ?
                                    theQuartetWeights.get(q2) :
                                    0.0;
                        }
                    }
                }
            }
        }

        return new ImmutablePair<>(count, weight);
    }


}
