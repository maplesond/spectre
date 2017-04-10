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
 * A vertex is weighted after its position in the component.
 * The inner vertices are lower weighted then the outer ones.
 * The weighting follows an adjusted parabola function.
 *
 * Sarah Bastkowski, 2010: <I>Algorithmen zum Finden von Bäumen in Neighbor Net Netzwerken</I>
 *
 * @author Sarah Bastkowski
 */
public class ParabolaWeighting extends Weighting {

    /**
     * @param i             index of weighting parameter to be updated
     * @param position      position of i in component
     * @param size size of component
     * @throws ArrayIndexOutOfBoundsException Array index is not valid
     */
    @Override
    public void updateWeightingParam(Identifier i, int position, int size) {

        double alpha = calcAlpha(size);
        double weighting = 1. / alpha * Math.pow(position - 0.5 * (size - 1), 2);

        this.setWeightingParam(i, weighting);
    }

    /**
     * Calculate the normalisation factor alpha
     * @param size Size of component (i.e. number of vertices)
     * @return alpha
     */
    private static double calcAlpha(final int size) {
        double alpha = 0.;

        for (int j = 0; j < size; j++) {
            alpha += (j - 0.5 * (size - 1))
                    * (j - 0.5 * (size - 1));
        }

        return alpha;
    }

    public void process(Identifier i, int position, int customParameter) {
        updateWeightingParam(i, position, customParameter);
    }
}