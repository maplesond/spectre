/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
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
package uk.ac.uea.cmp.phygen.tools.chopper.loader;

import org.apache.commons.io.FileUtils;
import org.kohsuke.MetaInfServices;
import uk.ac.uea.cmp.phygen.core.ds.tree.newick.NewickTree;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Extracts Newick trees from file
 *
 * @author sarah
 */
@MetaInfServices(uk.ac.uea.cmp.phygen.tools.chopper.loader.Source.class)
public class TreeLoader extends AbstractTreeLoader {

    @Override
    public void load(File file, double weight) throws IOException {

        index = 0;

        trees = new LinkedList<>();
        weights = new LinkedList<>();

        List<String> lines = FileUtils.readLines(file);
        for(String line : lines) {

            NewickTree tree = new NewickTree(line);

            weights.add(tree.getScalingFactor() * weight);
            trees.add(tree);
        }
    }

    @Override
    public String getName() {
        return "newick";
    }
}
