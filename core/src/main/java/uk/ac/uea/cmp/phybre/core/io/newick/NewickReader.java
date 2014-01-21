/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
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

package uk.ac.uea.cmp.phybre.core.io.newick;

import uk.ac.uea.cmp.phybre.core.ds.tree.newick.NewickTree;
import uk.ac.uea.cmp.phybre.core.io.AbstractPhygenReader;
import uk.ac.uea.cmp.phybre.core.io.PhygenDataType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Can read files that contain newick trees, one per line.
 */
public class NewickReader extends AbstractPhygenReader {


    @Override
    public List<NewickTree> readTrees(File input, double weight) throws IOException {

        List<NewickTree> trees = new ArrayList<>();

        BufferedReader in = new BufferedReader(new FileReader(input));

        String line = in.readLine();


        while ((line = in.readLine()) != null) {

            // Trim the line of whitespace
            line = line.trim();

            // If there is a blank line, go to next line (one tree per line)
            if (!line.isEmpty()) {
                trees.add(new NewickTree(line));
            }
        }

        in.close();

        return trees;
    }


    @Override
    public String[] commonFileExtensions() {
        return new String[]{"newick", "new", "tre"};
    }

    @Override
    public String getIdentifier() {
        return "NEWICK";
    }

    @Override
    public boolean acceptsDataType(PhygenDataType phygenDataType) {

        if (phygenDataType == PhygenDataType.TREE)
            return true;

        return false;
    }
}
