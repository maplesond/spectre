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

package uk.ac.uea.cmp.spectre.flatnj;

import uk.ac.uea.cmp.spectre.core.util.CollectionUtils;
import uk.ac.uea.cmp.spectre.core.ds.quad.quadruple.Quadruple;
import uk.ac.uea.cmp.spectre.core.ds.quad.quadruple.QuadrupleSystem;

/**
 * Computes and updates scores of pairs of taxa that show their
 * neighborliness.
 *
 * @author balvociute
 */
public class Scorer {
    /**
     * Computes initial scores for full {@linkplain QuadrupleSystem}.
     *
     * @param qs {@linkplain QuadrupleSystem}.
     * @return
     */
    public double[][][] initializeScores(QuadrupleSystem qs) {
        int nTaxa = qs.getnTaxa();
        int[] taxa = qs.getTaxaInt();

        double[][][] scores = new double[nTaxa][nTaxa][3];

        for (int i1 = 0; i1 < taxa.length; i1++) {
            for (int i2 = i1 + 1; i2 < taxa.length; i2++) {
                double score1 = 0.0;
                double score2 = 0.0;
                double distance = 0.0;


                for (int j1 = 0; j1 < taxa.length; j1++) {
                    for (int j2 = j1 + 1; j2 < taxa.length; j2++) {
                        if (i1 != j1 && i1 != j2 && i2 != j1 && i2 != j2) {
                            Quadruple q = qs.getQuadrupleUnsorted(taxa[i1], taxa[i2], taxa[j1], taxa[j2]);

                            score1 += transformWeight(min(i1, i2, j1, j2, q));
                            score2 += transformWeight(q.getSplitWeightFor2Vs2(taxa[i1], taxa[i2]));
                            distance += transformWeight((q.getSplitWeightFor2Vs2(taxa[i1], taxa[j1]) + q.getSplitWeightFor2Vs2(taxa[i1], taxa[j2])));

                        }
                    }
                }
                setValues(scores, taxa[i1], taxa[i2], 0, score1);
                setValues(scores, taxa[i1], taxa[i2], 1, score2);
                setValues(scores, taxa[i1], taxa[i2], 2, distance);
            }
        }
        return scores;
    }

    /**
     * Updates scores before {@linkplain QuadrupleSystem} is agglomerated.
     *
     * @param scores     scores table.
     * @param neighbours current {@linkplain Neighbours}.
     * @param qs         {@linkplain QuadrupleSystem}
     */
    public void updateScoresBeforeWeightChange(double[][][] scores, Neighbours neighbours, QuadrupleSystem qs) {
        int[] taxa = qs.getTaxaInt();
        int a = neighbours.getA();
        int b = neighbours.getB();

        for (int i1 = 0; i1 < taxa.length; i1++) {
            if (taxa[i1] != a && taxa[i1] != b) {
                for (int i2 = i1 + 1; i2 < taxa.length; i2++) {
                    if (taxa[i2] != a && taxa[i2] != b) {
                        double minus1 = scores[taxa[i1]][taxa[i2]][0];
                        double minus2 = scores[taxa[i1]][taxa[i2]][1];
                        double minusDist = scores[taxa[i1]][taxa[i2]][2];
                        for (int i3 = 0; i3 < taxa.length; i3++) {
                            if (i1 != i3 && i2 != i3 & a != taxa[i3] && b != taxa[i3]) {
                                Quadruple qA = qs.getQuadrupleUnsorted(a, taxa[i1], taxa[i2], taxa[i3]);

                                minus1 -= transformWeight(min(taxa[i1], taxa[i2], taxa[i3], a, qA));
                                minus2 -= transformWeight(qA.getSplitWeightFor2Vs2(taxa[i1], taxa[i2]));
                                minusDist -= transformWeight((qA.getSplitWeightFor2Vs2(taxa[i1], taxa[i3]) + qA.getSplitWeightFor2Vs2(taxa[i1], a)));

                                Quadruple qB = qs.getQuadrupleUnsorted(b, taxa[i1], taxa[i2], taxa[i3]);

                                minus1 -= transformWeight(min(taxa[i1], taxa[i2], taxa[i3], b, qB));
                                minus2 -= transformWeight(qB.getSplitWeightFor2Vs2(taxa[i1], taxa[i2]));
                                minusDist -= transformWeight((qB.getSplitWeightFor2Vs2(taxa[i1], taxa[i3]) + qB.getSplitWeightFor2Vs2(taxa[i1], b)));
                            }
                        }
                        Quadruple qAB = qs.getQuadrupleUnsorted(a, b, taxa[i1], taxa[i2]);
                        minus1 -= transformWeight(min(taxa[i1], taxa[i2], a, b, qAB));
                        minus2 -= transformWeight(qAB.getSplitWeightFor2Vs2(taxa[i1], taxa[i2]));
                        minusDist -= transformWeight((qAB.getSplitWeightFor2Vs2(taxa[i1], a) + qAB.getSplitWeightFor2Vs2(taxa[i1], b)));

                        setValues(scores, taxa[i1], taxa[i2], 0, minus1);
                        setValues(scores, taxa[i1], taxa[i2], 1, minus2);
                        setValues(scores, taxa[i1], taxa[i2], 2, minusDist);

                    }
                }
            }
        }
    }

    /**
     * Updates scores after {@linkplain QuadrupleSystem} is agglomerated.
     *
     * @param scores     scores table.
     * @param neighbours current {@linkplain Neighbours}.
     * @param qs         {@linkplain QuadrupleSystem}
     */
    public void updateScoresAfterWeightChange(double[][][] scores, Neighbours neighbours, QuadrupleSystem qs) {
        int[] taxa = CollectionUtils.getElements(qs.getTaxa());
        int a = neighbours.getA();
        int b = neighbours.getB();

        for (int i = 0; i < taxa.length; i++) {
            if (taxa[i] != a) {
                double score2 = 0.0;
                double score = 0.0;
                double distance = 0.0;

                for (int i1 = 0; i1 < taxa.length; i1++) {
                    if (taxa[i1] != a && taxa[i] != taxa[i1]) {
                        for (int i2 = i1 + 1; i2 < taxa.length; i2++) {
                            if (taxa[i2] != a && taxa[i] != taxa[i2]) {
                                Quadruple q = qs.getQuadrupleUnsorted(taxa[i1], taxa[i2], taxa[i], a);

                                score += transformWeight(min(a, taxa[i], taxa[i1], taxa[i2], q));
                                score2 += transformWeight(q.getSplitWeightFor2Vs2(a, taxa[i]));
                                distance += transformWeight((q.getSplitWeightFor2Vs2(a, taxa[i1]) + q.getSplitWeightFor2Vs2(a, taxa[i2])));
                            }
                        }
                    }
                }
                setValues(scores, a, taxa[i], 0, score);
                setValues(scores, a, taxa[i], 1, score2);
                setValues(scores, a, taxa[i], 2, distance);
            }
        }

        for (int i1 = 0; i1 < taxa.length; i1++) {
            if (taxa[i1] != a && taxa[i1] != b) {
                for (int i2 = i1 + 1; i2 < taxa.length; i2++) {
                    if (taxa[i2] != a && taxa[i2] != b) {
                        double plus1 = scores[taxa[i1]][taxa[i2]][0];
                        double plus2 = scores[taxa[i1]][taxa[i2]][1];
                        double plusDist = scores[taxa[i1]][taxa[i2]][2];
                        for (int i3 = 0; i3 < taxa.length; i3++) {
                            if (i1 != i3 && i2 != i3 & a != taxa[i3] && b != taxa[i3]) {
                                Quadruple qA = qs.getQuadrupleUnsorted(taxa[i1], taxa[i2], taxa[i3], a);

                                plus1 += transformWeight(min(taxa[i1], taxa[i2], taxa[i3], a, qA));
                                plus2 += transformWeight(qA.getSplitWeightFor2Vs2(taxa[i1], taxa[i2]));
                                plusDist += transformWeight((qA.getSplitWeightFor2Vs2(taxa[i1], taxa[i3]) + qA.getSplitWeightFor2Vs2(taxa[i1], a)));
                            }
                        }
                        setValues(scores, taxa[i1], taxa[i2], 0, plus1);
                        setValues(scores, taxa[i1], taxa[i2], 1, plus2);
                        setValues(scores, taxa[i1], taxa[i2], 2, plusDist);
                    }
                }
            }
        }

        for (int i = 0; i < scores.length; i++) {
            for (int j = 0; j < scores[i].length; j++) {
                for (int k = 0; k < scores[i][j].length; k++) {
                    if (scores[i][j][k] < 0) {
                        scores[i][j][k] = 0;
                    }
                }
            }
        }
    }

    /**
     * Writes new score to the scores table.
     *
     * @param scores scores table.
     * @param i1     index of first taxa.
     * @param i2     index of second taxa.
     * @param j      score index. 0 - min, 1 - max.
     * @param d      score.
     */
    private void setValues(double[][][] scores, int i1, int i2, int j, double d) {
        scores[i1][i2][j] = d;
        scores[i2][i1][j] = d;
    }

    /**
     * Computes min score for {@linkplain Quadruple}.
     *
     * @param i1 index of the first taxa that is candidate to be a neighbor.
     * @param i2 index of the second taxa that is candidate to be a neighbor.
     * @param j1 index of the first taxa is not evaluated to be a neighbor.
     * @param j2 index of the second taxa is not evaluated to be a neighbor.
     * @param q  {@linkplain Quadruple}
     * @return minimal score.
     */
    private double min(int i1, int i2, int j1, int j2, Quadruple q) {
        double Wi1 = q.getSplitWeightFor1Vs3(i1);
        double Wi2 = q.getSplitWeightFor1Vs3(i2);
        double Wi1j1 = q.getSplitWeightFor2Vs2(i1, j1);
        double Wi1j2 = q.getSplitWeightFor2Vs2(i1, j2);

        double min = Wi1 < Wi2 ? Wi1 : Wi2;
        min = min < Wi1j1 ? min : Wi1j1;
        min = min < Wi1j2 ? min : Wi1j2;

        return min;
    }

    /**
     * Transforms given length. Used in case there will be need to filter or
     * score weights.
     *
     * @param w initial length
     * @return transformed length.
     */
    protected double transformWeight(double w) {
        return w;
    }

}
