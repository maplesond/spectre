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

package uk.ac.uea.cmp.spectre.core.ds.quad.quartet.load;

import org.apache.commons.io.FileUtils;
import org.kohsuke.MetaInfServices;
import uk.ac.uea.cmp.spectre.core.ds.quad.quartet.QuartetSystem;
import uk.ac.uea.cmp.spectre.core.ds.quad.quartet.QuartetSystemList;
import uk.ac.uea.cmp.spectre.core.ds.tree.newick.NewickTree;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Extracts Newick trees from file
 *
 * @author sarah
 */
@MetaInfServices(QLoader.class)
public class TreeLoader implements QLoader {

    @Override
    public QuartetSystem load(File file) throws IOException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public QuartetSystemList load(File file, double weight) throws IOException {

        QuartetSystemList sourceDataList = new QuartetSystemList();

        List<String> lines = FileUtils.readLines(file);
        for (String line : lines) {

            String trimmedLine = line.trim();

            if (!trimmedLine.isEmpty()) {
                NewickTree tree = new NewickTree(trimmedLine);

                sourceDataList.add(new QuartetSystem(tree.getTaxa(), tree.getScalingFactor() * weight, tree.createQuartets()));
            }
        }

        return sourceDataList;
    }

    @Override
    public boolean acceptsExtension(String ext) {
        return ext.equalsIgnoreCase("newick") || ext.equalsIgnoreCase("phylip") || ext.equalsIgnoreCase("tre") || ext.equalsIgnoreCase("tree");
    }

    @Override
    public String getName() {
        return "newick";
    }
}
