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

class CyclicOrderer {

    /**
     *
     * Run method
     *
     */
    public static String order(QNet parent) {

        ArrayList theLists = parent.getTheLists();
        QuartetWeights theQuartetWeights = parent.getWeights();
        ArrayList taxonNames = parent.getTaxonNames();
        boolean useMax = parent.getUseMax();
        int N = parent.getN();

        /**
         *
         * Method for doing the actual analysis. Might return something other
         * than a String, though...
         *
         */
        System.out.println("Beginning analysis...");

        /**
         *
         * The coordinates of the optimal joining
         *
         * A is first list coordinate. P is left-right first list end. B is
         * second list coordinate. Q is left-right second list end.
         *
         */
        int bestA, bestB, bestP, bestQ;

        /**
         *
         * Score of the optimal joining
         *
         */
        double bestScore;

        /**
         *
         * Algorithm detail...
         *
         * Loop from full set of N lists. Each time, a list goes, before this n
         * remains. When only one is left, it is done.
         *
         * Investigate all possible end-joinings of (2 * n) by (2 * (n - 1))
         * taxa...
         *
         * This means... loop from first to next-to-last. For all those, do both
         * ends. Take against all later, both ends...
         *
         *
         */
        for (int n = N; n > 1; n--) {

            System.out.println("Beginning iteration " + (N + 1 - n) + "");

            /**
             *
             * We are about to locate the optimal end-joining. It will be stored
             * as bestA, bestB, bestP, bestQ and have score bestScore. It will
             * be performed.
             *
             * First, though, null these variables.
             *
             */
            bestA = - 1;
            bestB = - 1;
            bestP = - 1;
            bestQ = - 1;

            bestScore = - 1000;

            /**
             *
             * Go through loop...
             *
             */
            for (int a = 0; a < n - 1; a++) {

                for (int p = 0; p < 2; p++) {

                    for (int b = a + 1; b < n; b++) {

                        for (int q = 0; q < 2; q++) {

                            /**
                             *
                             * Here, we see if joining a, b, p, q works any good
                             * or not.
                             *
                             * Basically, we take it�s score...
                             *
                             * ... and if the score is higher than bestScore, we
                             * set the best variables to the current and the
                             * score
                             *
                             */
                            /**
                             *
                             * The issue here is scoring...
                             *
                             * ... for each joining, investigate the various
                             * quartets possible...
                             *
                             * ... which consist for each member of the first
                             * and each member of the second ... of each member
                             * of one and of another, that is beyond that ...
                             * taken among the first and second
                             *
                             * ... and access their scores if they are
                             * realized...
                             *
                             * ... and play with it a little...
                             *
                             * ... according to the desired method...
                             *
                             * ... and so we have it...
                             *
                             */
                            /**
                             *
                             * Scorekeeping object
                             *
                             */
                            ScoreKeeper aS = new MeanKeeper();

                            /**
                             *
                             * Something to remember it by...
                             *
                             */
                            int x, y, u, v;

                            /**
                             *
                             * There are currently n entries in theLists...
                             *
                             * If p = 0 q = 0 joinings are forward| 0 to i are
                             * possible drawrof| j to end are possible
                             *
                             * If p = 0 q = 1 joinings are forward| 0 to i
                             * forward| 0 to j
                             *
                             * If p = 1 q = 0 joinings are drawrof| i to end
                             * drawrof| j to end
                             *
                             * If p = 1 q = 1 joinings are drawrof| i to end
                             * forward| 0 to j
                             *
                             */
                            /**
                             *
                             * Loop 0...i...end, 0...j...end
                             *
                             * That is, the entries in the two suggested paths
                             *
                             */
                            for (int i = 0; i < ((TaxonList) theLists.get(a)).size(); i++) {

                                for (int j = 0; j < ((TaxonList) theLists.get(b)).size(); j++) {

                                    /**
                                     *
                                     * Now then... go through all the rest
                                     *
                                     */
                                    for (int c = 0; c < n - 1; c++) {

                                        for (int d = c + 1; d < n; d++) {

                                            /**
                                             *
                                             * And then the entries in these
                                             * two...
                                             *
                                             * Then, take all of the pairs
                                             * within ... but if c or d equals a
                                             * or b, use the caution
                                             * appropriate...
                                             *
                                             * There will be two layers of loops
                                             * here, possibly 4-6 in total, and
                                             * in all their hearts a realized
                                             * quartet.
                                             *
                                             * Take weights, perform
                                             * manipulation...
                                             *
                                             * ... which is done using an object
                                             * ... accepting the scores and
                                             * computing a final score...
                                             *
                                             */
                                            int kBeg = 0, kEnd = 0, lBeg = 0, lEnd = 0;

                                            if (c != a && c != b) {

                                                kBeg = 0;
                                                kEnd = ((TaxonList) theLists.get(c)).size();

                                            } else if (c == a) {

                                                if (p == 0) {

                                                    kBeg = 0;
                                                    kEnd = i;

                                                } else if (p == 1) {

                                                    kBeg = i + 1;
                                                    kEnd = ((TaxonList) theLists.get(a)).size();

                                                }

                                            } else if (c == b) {

                                                if (q == 0) {

                                                    kBeg = j + 1;
                                                    kEnd = ((TaxonList) theLists.get(b)).size();

                                                } else if (q == 1) {

                                                    kBeg = 0;
                                                    kEnd = j;

                                                }

                                            }

                                            if (d != a && d != b) {

                                                lBeg = 0;
                                                lEnd = ((TaxonList) theLists.get(d)).size();

                                            } else if (d == a) {

                                                if (p == 0) {

                                                    lBeg = 0;
                                                    lEnd = i;

                                                } else if (p == 1) {

                                                    lBeg = i + 1;
                                                    lEnd = ((TaxonList) theLists.get(a)).size();

                                                }

                                            } else if (d == b) {

                                                if (q == 0) {

                                                    lBeg = j + 1;
                                                    lEnd = ((TaxonList) theLists.get(b)).size();

                                                } else if (q == 1) {

                                                    lBeg = 0;
                                                    lEnd = j;

                                                }

                                            }

                                            for (int k = kBeg; k < kEnd; k++) {

                                                for (int l = lBeg; l < lEnd; l++) {

                                                    /**
                                                     *
                                                     * a[i]b[j] | c[k]d[l]
                                                     *
                                                     */
                                                    x = ((Integer) ((ArrayList) theLists.get(a)).get(i)).intValue();
                                                    y = ((Integer) ((ArrayList) theLists.get(b)).get(j)).intValue();
                                                    u = ((Integer) ((ArrayList) theLists.get(c)).get(k)).intValue();
                                                    v = ((Integer) ((ArrayList) theLists.get(d)).get(l)).intValue();

                                                    if (useMax) {

                                                        aS.add(theQuartetWeights.getWeight(x, y, u, v));

                                                    } else {

                                                        aS.add(-theQuartetWeights.getWeight(x, y, u, v));

                                                    }

                                                }

                                            }

                                        }

                                    }

                                }

                            }

                            /**
                             *
                             * Bug fix qlippoth!
                             *
                             * If there is only one long path and a singlet,
                             * there will be no difference in quartets realized
                             * no matter how it�s joined. If this is so, we join
                             * arbitrary (a = 0 p = 0 b = 1 q = 0, say...)
                             *
                             */
                            if (n == 2 && (((ArrayList) theLists.get(0)).size() == 1 || ((ArrayList) theLists.get(1)).size() == 1)) {

                                bestA = 0;
                                bestB = 1;
                                bestP = 0;
                                bestQ = 0;

                            }

                            /**
                             *
                             * A score has been calculated
                             *
                             */
                            if (aS.getScore() > bestScore) {

                                bestA = a;
                                bestB = b;
                                bestP = p;
                                bestQ = q;

                                bestScore = aS.getScore();

                            }

                        }

                    }

                }

            }

            /**
             *
             * Perform the suggested joining, as suggested...
             *
             */
            TaxonList tLA = (TaxonList) theLists.get(bestA);
            TaxonList tLB = (TaxonList) theLists.get(bestB);

            String aJoin = new String();

            TaxonList theJoin = TaxonList.join(tLA, bestP, tLB, bestQ);

            for (int m = 0; m < theJoin.size(); m++) {

                aJoin += (String) taxonNames.get(((Integer) theJoin.get(m)).intValue() - 1) + " ";

            }

            if (useMax) {

                System.out.println("Joining produced list " + aJoin + "at score " + bestScore + ".");

            } else {

                System.out.println("Joining produced list " + aJoin + "at score " + (-bestScore) + ".");

            }

            theLists.add(theJoin);
            theLists.remove(tLA);
            theLists.remove(tLB);

        }

        /**
         *
         * Now, the lists should contain the desired circular ordering
         *
         */
        System.out.println("Done.");

        String result = new String();

        for (int n = 0; n < N; n++) {

            result += (String) taxonNames.get(((Integer) ((TaxonList) theLists.get(0)).get(n)).intValue() - 1) + " ";

        }

        return result;

    }
}