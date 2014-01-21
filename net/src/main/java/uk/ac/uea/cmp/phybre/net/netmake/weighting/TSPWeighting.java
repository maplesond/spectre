/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
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

package uk.ac.uea.cmp.phybre.net.netmake.weighting;

/**
 * Resulting circular order is solution for TSPWeighting.
 *
 * Sarah Bastkowski, 2010: <I>Algorithmen zum Finden von Bäumen in Neighbor Net Netzwerken</I>
 *
 * @author Sarah Bastkowski
 */
public class TSPWeighting extends Weighting {

    /**
     * Creates an TSPWeighting object with a weighting list of the given size.
     *
     * @param size size of the weighting list
     */
    public TSPWeighting(int size) {
        super(size);
    }

    /**
     * @param i             index of weighting parameter to be updated
     * @param position      position of i in component
     * @param componentSize size of component
     * @throws ArrayIndexOutOfBoundsException
     */
    @Override
    public void updateWeightingParam(int i, int position, int componentSize) {
        Double weightingparameter = 0.;

        if (componentSize == 1) {
            weightingparameter = 1.;
        }
        if (componentSize > 1
                && (position == 0 || position == (componentSize - 1))) {
            weightingparameter = 0.5;
        } else {
            weightingparameter = 0.;
        }

        setWeightingParam(i, weightingparameter);
    }

    public void process(int i, int position, int customParameter) {
        updateWeightingParam(i, position, customParameter);
    }
}
