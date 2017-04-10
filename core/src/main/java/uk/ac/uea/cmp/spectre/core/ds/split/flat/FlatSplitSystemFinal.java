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

package uk.ac.uea.cmp.spectre.core.ds.split.flat;

/**
 * @author Mo
 */
public class FlatSplitSystemFinal extends FlatSplitSystem {
    double[] trivialTmp;

    public FlatSplitSystemFinal(PermutationSequence ps) {
        super(ps);
        int extra = 0;
        for (int i = 0; i < trivialTmp.length; i++) {
            if (trivialTmp[i] > 0.0) {
                extra++;
            }
        }
        boolean[][] splits2 = new boolean[nSplits + extra][nTaxa];
        double[] weights2 = new double[nSplits + extra];
        boolean[] active2 = new boolean[nSplits + extra];
        for (int i = 0; i < nSplits; i++) {
            System.arraycopy(splits[i], 0, splits2[i], 0, nTaxa);
            weights2[i] = weights[i];
            active2[i] = active[i];
        }
        int j = nSplits;
        for (int i = 0; i < trivialTmp.length; i++) {
            if (trivialTmp[i] > 0.0) {
                splits2[j][i] = true;
                weights2[j] = trivialTmp[i];
                active2[j] = true;
                j++;
            }
        }
        nSplits += extra;
        splits = splits2;
        weights = weights2;
        active = active2;
    }

    @Override
    public void setWeights(double[] weights, double[] minTrivial) {
        trivialTmp = new double[nTaxa];
        System.arraycopy(minTrivial, 0, trivialTmp, 0, nTaxa);

        this.weights = new double[weights.length];
        trivialWeights = new double[nTaxa];
        for (int i = 0; i < weights.length; i++) {
            if (isTrivial(splits[i]) && minTrivial != null) {
                this.weights[i] = minTrivial[trivialTaxa(splits[i])];
                trivialTmp[trivialTaxa(splits[i])] = 0.0;
            }
            this.weights[i] += weights[i];
        }
    }

}
