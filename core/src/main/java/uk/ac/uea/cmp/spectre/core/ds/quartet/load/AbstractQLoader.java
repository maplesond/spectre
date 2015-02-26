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

package uk.ac.uea.cmp.spectre.core.ds.quartet.load;

import uk.ac.uea.cmp.spectre.core.ds.quartet.QuartetSystemList;

import java.io.File;
import java.io.IOException;

/**
 * Created by dan on 14/12/13.
 */
public abstract class AbstractQLoader implements QLoader {

    /**
     * By default we assume that there is only one quartet network loaded by this QLoader.  That being the case for
     * this method, we just add that network into a new network list and set the weight for the network.
     *
     * @param file   The file to load
     * @param weight The weight to be applied to this file
     * @return A quartet network with a single weighted element
     * @throws IOException Thrown if there were any issues loading from file
     */
    @Override
    public QuartetSystemList load(File file, double weight) throws IOException {

        // Loads the file and adds to list
        QuartetSystemList qnets = new QuartetSystemList(this.load(file));

        // Sets the weight
        qnets.get(0).setWeight(weight);

        // Create a single quartet network based on these quartet weight and add to the list
        return qnets;
    }

}
