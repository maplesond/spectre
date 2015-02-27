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

import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.quartet.CanonicalWeightedQuartetMap;
import uk.ac.uea.cmp.spectre.core.ds.quartet.Quartet;
import uk.ac.uea.cmp.spectre.core.ds.quartet.QuartetUtils;
import uk.ac.uea.cmp.spectre.core.ds.quartet.SpectreQuartet;
import uk.ac.uea.cmp.spectre.qtools.qnet.QNetException;

import java.util.List;

public class THolder {


    private int[][][] counts;
    private double[][][] weights;

    public THolder(List<IdentifierList> taxaSets, int N, CanonicalWeightedQuartetMap theQuartetWeights) throws QNetException {

        counts = new int[N][N][N];
        weights = new double[N][N][N];


        for (int i = 1; i <= N; i++) {

            for (int j = 1; j <= N; j++) {

                // here, we must have that i and j are not on the same path
                // and we want their paths

                IdentifierList A = Holders.findFirstPathContainingId(taxaSets, i);
                IdentifierList B = Holders.findFirstPathContainingId(taxaSets, j);

                if (A == null) {
                    throw new QNetException("Could not find path associated with i: " + i);
                }

                if (B == null) {
                    throw new QNetException("Could not find path associated with j: " + j);
                }

                if (A == B) {

                    // if on the same path, no quartets meet the conditions
                    continue;
                }


                for (int k = 1; k <= N; k++) {

                    // and we must have that k is not on the path of i, j

                    IdentifierList C = Holders.findFirstPathContainingId(taxaSets, k);

                    if (C == null) {
                        throw new IllegalStateException("Could not find path associated with k: " + k);
                    }

                    if (C != A && C != B) {

                        // yay! uniqueness

                        int count = 0;
                        double weight = 0.0;

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
                                        if (QuartetUtils.areDistinct(yA1, yA2, yB, yC)) {

                                            Quartet q = new SpectreQuartet(yA1, yB, yA2, yC);

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

                        counts[i - 1][j - 1][k - 1] = count;
                        weights[i - 1][j - 1][k - 1] = weight;
                    }
                }
            }
        }
    }

    public double getWeight(int i, int j, int k) {
        return weights[i - 1][j - 1][k - 1];
    }

    public void setWeight(int i, int j, int k, double newT) {
        weights[i - 1][j - 1][k - 1] = newT;
    }

}
