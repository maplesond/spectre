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
package uk.ac.uea.cmp.spectre.core.ds.split;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 28/04/13
 * Time: 16:31
 * To change this template use File | Settings | File Templates.
 */
public class SplitWeights {

    private final static Logger log = LoggerFactory.getLogger(SplitWeights.class);

    private double[][] weights;

    public SplitWeights(int nbTaxa) {
        this.weights = new double[nbTaxa][nbTaxa];
    }

    public SplitWeights(double[][] weights) {

        if (weights == null) {
            this.weights = null;
        }

        int n = weights.length;

        if (n != weights[0].length)
            throw new IllegalArgumentException("Tree weights must be a square matrix");

        this.weights = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                this.weights[i][j] = weights[i][j];
            }
        }
    }

    public double getAt(int i, int j) {
        return this.weights[i][j];
    }

    public void setValAt(double val, final int i, final int j) {
        this.weights[i][j] = val;
    }

    public int size() {
        return this.weights.length;
    }

    /**
     * Returns true if the weight is positive and greater than 0.0
     * @param i
     * @param j
     * @return Whether the weight at the specified position is positive
     */
    public boolean hasWeightAt(final int i, final int j) {
        return this.weights[i][j] > 0.0;
    }
}
