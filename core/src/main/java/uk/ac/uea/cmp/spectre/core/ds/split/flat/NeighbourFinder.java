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

import uk.ac.uea.cmp.spectre.core.ds.quad.quadruple.QuadrupleSystem;

/**
 * {@linkplain Neighbours} selector from {@linkplain QuadrupleSystem}.
 *
 * @author balvociute
 */
public interface NeighbourFinder {
    /**
     * Finds {@linkplain Neighbours} in the current quadruple system.
     *
     * @param qs     current {@linkplain QuadrupleSystem}.
     * @param scores precomputed scores between pairs of taxa.
     * @return
     */
    public Neighbours findNeighbours(QuadrupleSystem qs, double[][][] scores);
}
