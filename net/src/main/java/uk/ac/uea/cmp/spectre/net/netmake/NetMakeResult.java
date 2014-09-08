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

package uk.ac.uea.cmp.spectre.net.netmake;

import uk.ac.uea.cmp.spectre.core.ds.split.SplitSystem;
import uk.ac.uea.cmp.spectre.core.io.PhygenWriter;
import uk.ac.uea.cmp.spectre.core.io.PhygenWriterFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA. User: Dan Date: 02/05/13 Time: 21:51 To change this template use File | Settings | File
 * Templates.
 */
public class NetMakeResult {

    private SplitSystem tree;
    private SplitSystem network;

    public NetMakeResult(SplitSystem tree, SplitSystem network) {
        this.tree = tree;
        this.network = network;
    }

    public SplitSystem getTree() {
        return tree;
    }

    public SplitSystem getNetwork() {
        return network;
    }

    public void save(File outputDir, String prefix) throws IOException {

        PhygenWriter phygenWriter = PhygenWriterFactory.NEXUS.create();
        String extension = PhygenWriterFactory.NEXUS.getPrimaryExtension();

        File networkOutputFile = new File(outputDir, prefix + ".network." + extension);
        File treeOutputFile = new File(outputDir, prefix + ".tree." + extension);

        phygenWriter.writeSplitSystem(networkOutputFile, this.getNetwork());

        if (this.tree != null) {
            phygenWriter.writeSplitSystem(treeOutputFile, this.getTree());
        }
    }
}
