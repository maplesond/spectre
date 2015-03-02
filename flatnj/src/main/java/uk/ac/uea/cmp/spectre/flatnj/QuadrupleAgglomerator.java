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

import uk.ac.uea.cmp.spectre.core.ds.quad.quadruple.QuadrupleSystem;

/**
 * {@linkplain QuadrupleSystem} agglomerator.
 *
 * @author balvociute
 */
public interface QuadrupleAgglomerator {
    /**
     * Agglomerates Quadruples in the given {@linkplain QuadrupleSystem} by joining
     * given {@linkplain Neighbours}.
     *
     * @param qs         {@linkplain QuadrupleSystem}.
     * @param neighbours {@linkplain Neighbours}.
     */
    public void agglomerate(QuadrupleSystem qs, Neighbours neighbours);
}
