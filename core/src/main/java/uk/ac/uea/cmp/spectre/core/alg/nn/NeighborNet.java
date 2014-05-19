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

package uk.ac.uea.cmp.spectre.core.alg.nn;

import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitSystem;

/**
 * Created by dan on 27/02/14.
 */
public interface NeighborNet {

    /**
     * Neighbornet takes in a distance matrix (which will have a set of taxa embedded within it) and to parameters, the
     * sum of which should equal 1.0.  The result of neighbornet is a compatible split system which should contain a
     * circular ordering
     *
     * @param distanceMatrix The distance matrix to process, which should also contain the set of taxa
     * @param params         Alpha, Beta and Gamma parameters
     * @return The result of neighbornet, a compatible split system
     */
    SplitSystem execute(DistanceMatrix distanceMatrix, NeighborNetParams params);
}
