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

package uk.ac.uea.cmp.spectre.core.co.nm.weighting;

import uk.ac.uea.cmp.spectre.core.ds.Identifier;

/**
 * Resulting circular order is solution for TSPWeighting.
 * <p/>
 * Sarah Bastkowski, 2010: <I>Algorithmen zum Finden von Bäumen in Neighbor Net Netzwerken</I>
 *
 * @author Sarah Bastkowski
 */
public class TSPWeighting extends Weighting {

    /**
     * @param i             index of weighting parameter to be updated
     * @param position      position of i in component
     * @param size size of component
     * @throws ArrayIndexOutOfBoundsException
     */
    @Override
    public void updateWeightingParam(Identifier i, int position, int size) {
        Double weightingparameter = 0.;

        if (size == 1) {
            weightingparameter = 1.;
        }
        if (size > 1
                && (position == 0 || position == (size - 1))) {
            weightingparameter = 0.5;
        } else {
            weightingparameter = 0.;
        }

        setWeightingParam(i, weightingparameter);
    }

    public void process(Identifier i, int position, int customParameter) {
        updateWeightingParam(i, position, customParameter);
    }
}
