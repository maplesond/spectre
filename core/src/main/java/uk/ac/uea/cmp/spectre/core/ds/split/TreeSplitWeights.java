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
 * Date: 27/04/13
 * Time: 16:59
 * To change this template use File | Settings | File Templates.
 */
public class TreeSplitWeights extends SplitWeights {

    private final static Logger log = LoggerFactory.getLogger(TreeSplitWeights.class);


    public TreeSplitWeights(int nbTaxa) {
        super(nbTaxa);
    }

    public TreeSplitWeights(double[][] weights) {
        super(weights);
    }


    /**
     * Returns the length of the minimum evolution tree.
     *
     * @return length of the minimum evolution tree
     */
    public final double calcTreeLength() {
        double sum = 0;

        for (int i = 0; i < this.size(); i++) {
            for (int j = 0; j < this.size(); j++) {
                if (this.getAt(j, i) == Double.POSITIVE_INFINITY) {
                    log.warn("TreeWeight [" + j + "," + i + "] is infinity");
                }
                sum += this.getAt(j, i);
            }
        }

        return sum;
    }

    /**
     * Updates the tree weights matrix.
     *
     * @param branchLength length of a vertex
     * @param i            specifies the split
     * @param j            specifies the split
     */
    public void setBranchLengthAt(double branchLength, int i, int j) {

        final int n = this.size();
        if (i == 0) {
            this.setValAt(branchLength, n - 1, j);
        } else {
            if ((j < n - 1) && (i - 1 > j)) {
                this.setValAt(branchLength, i - 1, j);
            } else {
                this.setValAt(branchLength, j, i - 1);
            }
        }
    }
}
