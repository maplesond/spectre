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

package uk.ac.uea.cmp.spectre.core.ds.distance;

import uk.ac.uea.cmp.spectre.core.ds.Identifier;

import java.util.List;

/**
 * Created by dan on 18/07/14.
 */
public abstract class AbstractDistanceMatrix implements DistanceMatrix {

    @Override
    public double incrementDistance(final Identifier taxon1, final Identifier taxon2, final double increment) {

        double newValue = this.getDistance(taxon1, taxon2) + increment;

        this.setDistance(taxon1, taxon2, newValue);

        return newValue;
    }

    @Override
    public DistanceList getDistances(Identifier taxon) {
        return this.getDistances(taxon, null);
    }

    @Override
    public DistanceList getDistances(int taxonId) {
        return this.getDistances(taxonId, null);
    }

    @Override
    public DistanceList getDistances(String taxonName) {
        return this.getDistances(taxonName, null);
    }

    @Override
    public List<DistanceList> getAllDistances() {
        return this.getAllDistances(null);
    }

    @Override
    public double[][] getMatrix() {
        return this.getMatrix(null);
    }


}
