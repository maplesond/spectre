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

import org.kohsuke.MetaInfServices;
import uk.ac.uea.cmp.spectre.core.ds.quartet.CanonicalWeightedQuartetMap;
import uk.ac.uea.cmp.spectre.core.ds.quartet.QuartetSystem;
import uk.ac.uea.cmp.spectre.core.ds.split.Split;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitSystem;
import uk.ac.uea.cmp.spectre.core.io.nexus.NexusReader;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA. User: Analysis Date: 2004-jul-11 Time: 23:09:07 To
 * change this template use Options | File Templates.
 */
@MetaInfServices(QLoader.class)
public class NexusSplitsLoader extends AbstractNexusLoader {

    @Override
    public QuartetSystem load(File file) throws IOException {

        // Load the split system from the nexus file
        SplitSystem splitSystem = new NexusReader().readSplitSystem(file);

        // Initialise the quartet weights to the right size, which depends on the number of taxa present in the split system
        CanonicalWeightedQuartetMap qW = new CanonicalWeightedQuartetMap();

        // Add each split to the quartet weights
        for (Split split : splitSystem) {
            qW.addSplit(split);
        }

        return new QuartetSystem(splitSystem.getOrderedTaxa(), 1.0, qW);
    }

    @Override
    public String getName() {
        return "nexus:st_splits";
    }
}
