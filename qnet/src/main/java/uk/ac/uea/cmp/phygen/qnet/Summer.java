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
package uk.ac.uea.cmp.phygen.qnet;

import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetWeights;

import java.util.ArrayList;

class Summer {

    Summer(QuartetWeights aQW, ArrayList aL, ArrayList bL, ArrayList cL, ArrayList dL, boolean useMax) {

        count = 0;
        score = 0;

        for (int a = 0; a < aL.size(); a++) {

            for (int b = 0; b < bL.size(); b++) {

                for (int c = 0; c < cL.size(); c++) {

                    for (int d = 0; d < dL.size(); d++) {

                        int x = ((Integer) aL.get(a)).intValue();
                        int y = ((Integer) bL.get(b)).intValue();
                        int u = ((Integer) cL.get(c)).intValue();
                        int v = ((Integer) dL.get(d)).intValue();

                        if (useMax) {

                            score += (aQW.getWeight(x, y, u, v)
                                      - Math.max(aQW.getWeight(x, u, y, v),
                                                 aQW.getWeight(x, v, u, y)));

                            count++;

                        } else {

                            score -= (aQW.getWeight(x, y, u, v)
                                      - Math.max(aQW.getWeight(x, u, y, v),
                                                 aQW.getWeight(x, v, u, y)));

                            count++;

                        }

                    }

                }

            }

        }

    }

    int getCount() {

        return count;

    }

    double getScore() {

        return score;

    }
    int count;
    double score;
}
