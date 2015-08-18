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

package uk.ac.uea.cmp.spectre.core.ds.split.circular.ordering;

import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitSystem;

/**
 * Created by dan on 07/09/14.
 */
public interface CircularOrderingCreator {

    /**
     * Creates a circular ordering of taxa from a distance matrix
     * @param distanceMatrix Distances between each taxa
     * @return A list of taxa matching taxa found in the distance matrix except that it has been sorted according to the
     * underlying algorithm
     */
    IdentifierList createCircularOrdering(final DistanceMatrix distanceMatrix);

    boolean createsTreeSplits();

    SplitSystem getTreeSplits();
}
