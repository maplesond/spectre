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

package uk.ac.uea.cmp.spectre.net.netme;

import org.apache.commons.io.FileUtils;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitSystem;
import uk.ac.uea.cmp.spectre.core.io.nexus.NexusWriter;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 27/04/13
 * Time: 17:09
 * To change this template use File | Settings | File Templates.
 */
public class NetMEResult {

    private SplitSystem originalMETree;
    private SplitSystem meTree;
    private String stats;

    public NetMEResult(SplitSystem originalMETree, SplitSystem meTree, String stats) {
        this.originalMETree = originalMETree;
        this.meTree = meTree;
        this.stats = stats;
    }

    public SplitSystem getMeTree() {
        return meTree;
    }

    public SplitSystem getOriginalMETree() {
        return originalMETree;
    }

    public void save(File minEvoFile, File origMinEvoFile, File statFile, boolean saveOLS) throws IOException {

        NexusWriter nexusWriter = new NexusWriter();

        nexusWriter.writeSplitSystem(minEvoFile, this.getMeTree());
        if (saveOLS) {
            nexusWriter.writeSplitSystem(origMinEvoFile, this.getOriginalMETree());
        }

        FileUtils.writeStringToFile(statFile, stats, "UTF-8");
    }
}
