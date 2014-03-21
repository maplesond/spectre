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

package uk.ac.uea.cmp.spectre.core.io;

import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.quartet.GroupedQuartetSystem;
import uk.ac.uea.cmp.spectre.core.ds.quartet.QuartetSystem;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitSystem;

import java.io.File;
import java.io.IOException;

/**
 * Created by dan on 10/12/13.
 */
public class AbstractPhygenWriter implements PhygenWriter {

    @Override
    public void writeSplitSystem(File outFile, SplitSystem splitSystem) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getCanonicalName() + " does not support writing of split systems.");
    }

    @Override
    public void writeDistanceMatrix(File outFile, DistanceMatrix distanceMatrix) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getCanonicalName() + " does not support writing of distance matrices.");
    }

    @Override
    public void writeQuartets(File outFile, QuartetSystem quartetSystem) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getCanonicalName() + " does not support writing of quartets.");
    }

    @Override
    public void writeQuartets(File outFile, GroupedQuartetSystem quartetSystem) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getCanonicalName() + " does not support writing of quartets.");
    }
}
