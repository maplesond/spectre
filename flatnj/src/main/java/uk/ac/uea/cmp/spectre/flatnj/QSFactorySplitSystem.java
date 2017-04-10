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

package uk.ac.uea.cmp.spectre.flatnj;

import uk.ac.uea.cmp.spectre.core.ds.quad.quadruple.Quadruple;
import uk.ac.uea.cmp.spectre.core.ds.quad.quadruple.QuadrupleSystem;
import uk.ac.uea.cmp.spectre.core.ds.split.flat.FlatSplitSystem;

/**
 * Quadruple system factory from split system.
 *
 * @author balvociute
 */
public class QSFactorySplitSystem implements QSFactory {
    /**
     * {@linkplain uk.ac.uea.cmp.spectre.core.ds.split.flat.FlatSplitSystem} to be used for the estimation of
     * {@link QuadrupleSystem}.
     */
    private FlatSplitSystem ss;

    /**
     * {@linkplain boolean} splits matrix that indicates which taxa are
     * separated by each split.
     */
    private boolean[][] splits;
    /**
     * {@linkplain double} array with weights of all splits in the
     * ss.
     */
    private double[] weights;

    /**
     * Constructs {@linkplain QSFactoryLocation} object that will use
     * {@linkplain  uk.ac.uea.cmp.spectre.core.ds.split.flat.FlatSplitSystem} to compute new {@link QuadrupleSystem}.
     *
     * @param ss a {@linkplain uk.ac.uea.cmp.spectre.core.ds.split.flat.FlatSplitSystem} object to be used for the estimation of
     *           {@link QuadrupleSystem}.
     */
    public QSFactorySplitSystem(FlatSplitSystem ss) {
        this.ss = ss;
    }

    @Override
    public QuadrupleSystem computeQS() {
        int nTaxa = ss.getnTaxa();
        splits = ss.getSplits();
        weights = ss.getWeights();

        QuadrupleSystem qs = new QuadrupleSystem(nTaxa);

        int[] inTaxa = new int[4];
        double[] inWeights = new double[7];

        for (int i1 = 0; i1 < nTaxa - 3; i1++) {
            inTaxa[0] = i1;
            for (int i2 = i1 + 1; i2 < nTaxa - 2; i2++) {
                inTaxa[1] = i2;
                for (int i3 = i2 + 1; i3 < nTaxa - 1; i3++) {
                    inTaxa[2] = i3;
                    for (int i4 = i3 + 1; i4 < nTaxa; i4++) {
                        inTaxa[3] = i4;

                        //We now compute the induced weights for the quartet splits.
                        inWeights[0] = computeWeight(i2, i3, i3, i4, i2, i1);
                        inWeights[1] = computeWeight(i1, i3, i3, i4, i1, i2);
                        inWeights[2] = computeWeight(i1, i2, i2, i4, i1, i3);
                        inWeights[3] = computeWeight(i1, i2, i2, i3, i1, i4);
                        inWeights[4] = computeWeight(i1, i2, i3, i4, i1, i3);
                        inWeights[5] = computeWeight(i1, i3, i2, i4, i1, i2);
                        inWeights[6] = computeWeight(i1, i4, i2, i3, i1, i2);

                        qs.add(new Quadruple(inTaxa, inWeights));
                    }
                }
            }
        }
        return qs;
    }

    /**
     * Computes weight of a quadruple split described by parameters i1..i6. Taxa
     * with index i1 must be in the same partition as taxa with index i2, i3 as
     * i4 and i5 must be in the different partition from i6. For example, to
     * estimate weight of quadruple split a|bcd, i1 = b, i2 = c, i3 = c, i4 = d
     * and i5 = a, i6 = b; For split ab|cd indexes would be: i1 = a, i2 = b,
     * i3 = c, i4 = d and i5 = a, i6 = c.
     *
     * @param i1 index of taxa.
     * @param i2 index of taxa.
     * @param i3 index of taxa.
     * @param i4 index of taxa.
     * @param i5 index of taxa.
     * @param i6 index of taxa.
     * @return {@linkplain double} quadruple split weight.
     */
    private double computeWeight(int i1, int i2, int i3, int i4, int i5, int i6) {
        double w = 0.0;
        for (int m = 0; m < splits.length; m++) {
            if (((splits[m][i1] == splits[m][i2]) && (splits[m][i3] == splits[m][i4])) && (splits[m][i5] != splits[m][i6])) {
                w += weights[m];
            }
        }
        return w;
    }

}
