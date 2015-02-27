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

package uk.ac.uea.cmp.spectre.core.co.nm.weighting;

import uk.ac.uea.cmp.spectre.core.ds.Identifier;

/**
 * Weighting of vertices in merged component depends on number of vertices in
 * original component before mergin
 * (equal to number of times the components has been choosen in selection step)
 * <p/>
 * Sarah Bastkowski, 2010: <I>Algorithmen zum Finden von Bäumen in Neighbor Net Netzwerken</I>
 *
 * @author Sarah Bastkowski
 */
public class TreeWeighting extends Weighting {
    private double alpha;

    /**
     * Creates a TreeWeighting object with a weighting list of a specified size
     * and a specified constant alpha.
     *
     * @param alpha 0.5 for balanced tree weighting leads to NJ tree
     */
    public TreeWeighting(double alpha) {

        this.alpha = alpha;
    }

    /**
     * @param i                      index of weighting parameter to be updated
     * @param position               position of i in component
     * @param componentSplitposition position where second component begins after merging step
     * @throws ArrayIndexOutOfBoundsException Array index is not valid
     */
    @Override
    public void updateWeightingParam(Identifier i, int position,
                                     int componentSplitposition) {

        double weightingparameter = (position < componentSplitposition ? alpha : 1.0 - alpha) * getWeightingParam(i);

        setWeightingParam(i, weightingparameter);
    }

    public void process(Identifier i, int position, int customParameter) {
        updateWeightingParam(i, position, customParameter);
    }
}