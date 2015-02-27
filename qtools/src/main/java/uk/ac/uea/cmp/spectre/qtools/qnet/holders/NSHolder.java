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
package uk.ac.uea.cmp.spectre.qtools.qnet.holders;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.quartet.CanonicalWeightedQuartetMap;
import uk.ac.uea.cmp.spectre.core.ds.quartet.Quartet;
import uk.ac.uea.cmp.spectre.core.ds.quartet.QuartetUtils;
import uk.ac.uea.cmp.spectre.core.ds.quartet.SpectreQuartet;
import uk.ac.uea.cmp.spectre.qtools.qnet.QNetException;

import java.util.List;


public class NSHolder extends AbstractBasicHolder {

    public NSHolder(List<IdentifierList> paths, int N, CanonicalWeightedQuartetMap theQuartetWeights) throws QNetException {

        super(N, paths, theQuartetWeights);
    }

    @Override
    protected Pair<Integer, Double> calcCountWeight(IdentifierList A, IdentifierList B) throws QNetException {

        // now, we store everything properly

        int count = 0;
        double weight = 0.0;

        for (int c = 0; c < paths.size() - 1; c++) {

            for (int d = c + 1; d < paths.size(); d++) {

                IdentifierList C = paths.get(c);
                IdentifierList D = paths.get(d);

                if (C != A && C != B && D != A && D != B) {

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
                                    if (QuartetUtils.areDistinct(yA, yB, yC, yD)) {

                                        Quartet q = new SpectreQuartet(yA, yB, yC, yD);

                                        count++;
                                        weight += theQuartetWeights.containsKey(q) ?
                                                theQuartetWeights.get(q) :
                                                0.0;
                                    } else {
                                        throw new QNetException("Not sure if we are supposed to be here! :s");
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
