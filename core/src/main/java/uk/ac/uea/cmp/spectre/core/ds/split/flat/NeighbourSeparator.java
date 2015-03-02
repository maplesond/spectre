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

package uk.ac.uea.cmp.spectre.core.ds.split.flat;

import uk.ac.uea.cmp.spectre.core.ds.quad.quadruple.QuadrupleSystem;

/**
 * Computes {@linkplain PermutationSequence} by separating {@linkplain Neighbours} in the
 * reverse order.
 *
 * @author balvociute
 */
public interface NeighbourSeparator {
    /**
     * Separates all pairs of taxa that were identified as neighbors in the
     * agglomeration phase.
     *
     * @param neighbours array of pairs of neighbors
     * @param ps         permutation sequence on 4 taxa
     * @param qs         quadruple system
     * @param j          number of neighbors
     * @param n          number of taxa
     * @return full flat split system as a permutation sequence
     */
    public PermutationSequence popOutNeighbours(Neighbours[] neighbours,
                                                PermutationSequence ps,
                                                QuadrupleSystem qs,
                                                int j,
                                                int n);
}
