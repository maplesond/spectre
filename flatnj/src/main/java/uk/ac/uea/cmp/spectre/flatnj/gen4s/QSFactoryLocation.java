/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2014  UEA School of Computing Sciences
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
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

package uk.ac.uea.cmp.spectre.flatnj.gen4s;

import uk.ac.uea.cmp.spectre.core.ui.gui.geom.IndexedPoint;
import uk.ac.uea.cmp.spectre.flatnj.ds.Locations;
import uk.ac.uea.cmp.spectre.flatnj.ds.Quadruple;
import uk.ac.uea.cmp.spectre.flatnj.ds.QuadrupleSystem;

/**
 * Quadruple system factory from geographic locations.
 *
 * @author balvociute
 */
public class QSFactoryLocation implements QSFactory {
    /**
     * {@linkplain Locations} to be used for the estimation of
     * {@link QuadrupleSystem}.
     */
    private Locations l;

    /**
     * Constructs {@linkplain QSFactoryLocation} object that will use
     * {@linkplain Locations} to compute new {@link QuadrupleSystem}.
     *
     * @param l a {@linkplain Locations} object to be used for the estimation of
     *          {@link QuadrupleSystem}.
     */
    public QSFactoryLocation(Locations l) {
        this.l = l;
    }

    @Override
    public QuadrupleSystem computeQS() {
        IndexedPoint[] locations = l.getLocations();
        QuadrupleSystem qs = new QuadrupleSystem(locations.length);
        double[][] matrix = new double[locations.length][locations.length];
        for (int i = 0; i < locations.length; i++) {
            matrix[i][i] = 0;
            for (int j = i + 1; j < locations.length; j++) {
                double dist = Math.sqrt((locations[i].getX() - locations[j].getX()) * (locations[i].getX() - locations[j].getX()) + (locations[i].getY() - locations[j].getY()) * (locations[i].getY() - locations[j].getY()));
                matrix[i][j] = dist;
                matrix[j][i] = dist;
            }
        }

        int[] inLocations = new int[4];
        double[] weights;
        for (int i1 = 0; i1 < locations.length; i1++) {
            inLocations[0] = i1;
            for (int i2 = i1 + 1; i2 < locations.length; i2++) {
                inLocations[1] = i2;
                for (int i3 = i2 + 1; i3 < locations.length; i3++) {
                    inLocations[2] = i3;
                    for (int i4 = i3 + 1; i4 < locations.length; i4++) {
                        inLocations[3] = i4;
                        double[][] d = makeD(inLocations, matrix);
                        int min = -1;
                        for (int i = 0; i < inLocations.length; i++) {
                            double angle = getAngles(i, d);
                            if (angle >= 2 * Math.PI) {
                                min = i;
                            }
                        }
                        if (min != -1) {
                            weights = computeWeightsFor3trivial(d, min);
                        } else {
                            weights = computeWeightsFor4trivial(d);
                        }
                        Quadruple q = new Quadruple(inLocations, weights);
                        qs.add(q);
                    }
                }
            }
        }
        return qs;
    }

    /**
     * Estimates pairwise distances between subset of {@linkplain Locations}.
     *
     * @param indexes   array of size 4 of integer indexes of the subset of
     *                  locations.
     * @param distances distance matrix over all locations.
     * @return a matrix of {@linkplain double} pairwise distances between set of
     * {@link Locations} listed in indexes.
     */
    private double[][] makeD(int[] indexes, double[][] distances) {
        double[][] d = new double[4][4];

        for (int si1 = 0; si1 < 4; si1++) {
            d[si1][si1] = 0;
            for (int si2 = si1 + 1; si2 < 4; si2++) {
                d[si1][si2] = distances[indexes[si1]][indexes[si2]];
                d[si2][si1] = distances[indexes[si2]][indexes[si1]];
            }
        }

        return d;
    }


    /**
     * Computes sum of angles originating from location i to other
     * locations included in {@linkplain double[][]} distance matrix d.
     *
     * @param i index of location around which angles will be estimated.
     * @param d {@linkplain double} matrix of distances between locations.
     * @return
     */
    private double getAngles(int i, double[][] d) {
        double angles = 0;
        for (int i1 = 0; i1 < d.length; i1++) {
            for (int i2 = i1 + 1; i2 < d.length; i2++) {
                if (i != i1 && i != i2) {
                    angles += Math.acos((d[i][i1] * d[i][i1] + d[i][i2] * d[i][i2] - d[i1][i2] * d[i1][i2]) / (2 * d[i][i1] * d[i][i2]));
                }
            }
        }

        return angles;
    }

    /**
     * Estimates weights for a quadruple with 3 trivial and 3 non trivial
     * quadruple splits.
     *
     * @param d      {@linkplain double} matrix of distances between locations.
     * @param inside index of the location without trivial split.
     * @return an array of {@linkplain double} quadruple split weights.
     */
    private double[] computeWeightsFor3trivial(double[][] d, int inside) {
        double[] weights = new double[7];
        for (int i = 0; i < 4; i++) {
            if (i != inside) {
                int[] x = new int[2];
                int xx = 0;
                for (int j = 0; j < 4; j++) {
                    if (j != i && j != inside) {
                        x[xx++] = j;
                    }
                }
                weights[i] = (d[i][x[0]] + d[i][x[1]] - d[inside][x[0]] - d[inside][x[1]]) / 2;
                weights[i] = (weights[i] > 0) ? weights[i] : 0;
            } else {
                weights[i] = 0;
            }
        }

        for (int i = 4; i < 7; i++) {
            int k = i - 3;
            int[] x = new int[2];
            int xx = 0;
            for (int j = 1; j < d.length; j++) {
                if (j != 0 && j != k) {
                    x[xx++] = j;
                }
            }
            weights[i] = d[0][x[0]] + d[0][x[1]] + d[k][x[0]] + d[k][x[1]] - d[0][k] - d[x[0]][x[1]];
            for (int j = 0; j < 4; j++) {
                weights[i] -= weights[j];
            }
            weights[i] /= 4;
            weights[i] = (weights[i] > 0) ? weights[i] : 0;
        }
        return weights;
    }

    /**
     * Estimates weights for a quadruple with 4 trivial and 2 non trivial
     * quadruple splits.
     *
     * @param d {@linkplain double} matrix of distances between locations.
     * @return an array of {@linkplain double} quadruple split weights.
     */
    private double[] computeWeightsFor4trivial(double[][] d) {
        double[] weights = new double[7];
        double[] sum = new double[3];
        for (int i1 = 0; i1 < d.length; i1++) {
            for (int i2 = i1 + 1; i2 < d.length; i2++) {
                if (i1 == 0) {
                    sum[i2 - 1] = d[i1][i2];
                } else {
                    sum[5 - (i1 + i2)] += d[i1][i2];
                }
            }
        }
        double max = (sum[0] > sum[1]) ? sum[0] : sum[1];
        max = (max < sum[2]) ? sum[2] : max;

        for (int j1 = 0; j1 < weights.length; j1++) {
            double w = 0;
            if (j1 < 4) {
                for (int j2 = 0; j2 < 4; j2++) {
                    if (j1 != j2) {
                        w += d[j1][j2];
                    }
                }
                w -= max;
                w /= 2;
            } else {
                int j2 = j1 - 3;
                int j3 = (j2 == 1) ? 2 : 1;
                int j4 = (j2 == 3) ? 2 : 3;
                w = 0.5 * (max - d[0][j2] - d[j3][j4]);
            }
            weights[j1] = (w > 0) ? w : 0;
        }
        return weights;
    }

}
