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

import org.apache.commons.lang3.StringUtils;
import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;

import java.util.HashMap;
import java.util.Map;

/**
 * Fills and updates weighting list
 * <p/>
 * Sarah Bastkowski, 2010: <I>Algorithmen zum Finden von Bäumen in Neighbor Net Netzwerken</I>
 *
 * @author Sarah Bastkowski
 */
public abstract class Weighting {

    private Map<Identifier, Double> weightingParameters;

    /**
     * Does nothing... therefore weighting params array will not be initialised.
     */
    public Weighting() {
        this.weightingParameters = new HashMap<>();
    }

    public void initialiseWeightings(IdentifierList vertices) {
        for(Identifier v : vertices) {
            this.weightingParameters.put(v, 1.0);
        }
    }

    public void updateWeightings(IdentifierList vertices) {

        for (Identifier v : vertices) {
            //if (i == sb1.get(j) - 1) {
            //    position = j;

                if (this instanceof TreeWeighting) {
                    this.updateWeightingParam(v, 1, vertices.size());//position, componentSplitPosition);
                } else {
                    this.updateWeightingParam(v, 1, vertices.size()); //position, vertices.size());
                }
            //}
        }

    }

    /**
     * Updates the weighting parameter at a specified position.
     *
     * @param i               Taxon to update
     * @param position        index of the weighting parameter to be updated
     * @param customParameter parameter depending on implemented algorithm
     */
    public abstract void updateWeightingParam(Identifier i, int position, int customParameter);

    /**
     * Returns the weighting parameter stored at the specified position
     * in the weighting array.
     *
     * @param i index of the weighting parameter to be returned
     * @return the weighting parameter stored at the specified position
     * @throws ArrayIndexOutOfBoundsException if the index is out of range
     */
    public double getWeightingParam(Identifier i) {
        return weightingParameters.get(i);
    }

    /**
     * Replaces the weighting parameter at the specified position
     * in the weighting array with a new value.
     *
     * @param i        index of the value to be replaced
     * @param newValue value that replaces the one at the specified position
     * @throws ArrayIndexOutOfBoundsException if the index is out of range
     */
    public void setWeightingParam(Identifier i, double newValue) {
        weightingParameters.put(i, newValue);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + StringUtils.join(weightingParameters, ", ");
    }
}