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

package uk.ac.uea.cmp.spectre.core.ds.split.circular.ordering.nm.weighting;

import uk.ac.uea.cmp.spectre.core.ds.Identifier;

/**
 * Every vertex of one component (after merging) gets the same weighting
 * parameter, so they are all equally weighted.
 * Sarah Bastkowski, 2010: <I>Algorithmen zum Finden von Bäumen in Neighbor Net Netzwerken</I>
 *
 * @author Sarah Bastkowski
 */
public class EqualWeighting extends Weighting {

    /**
     * @param i             index of weighting parameter to be updated
     * @param dummy         is not used
     * @param componentSize size of component
     * @throws ArrayIndexOutOfBoundsException Array index is not valid
     */
    @Override
    public void updateWeightingParam(Identifier i, int dummy, int componentSize) {
        Double weightingparameter = 0.;

        weightingparameter = 1. / componentSize;

        setWeightingParam(i, weightingparameter);
    }

    public void process(Identifier i, int position, int customParameter) {
        updateWeightingParam(i, position, customParameter);
    }
}