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
package uk.ac.uea.cmp.spectre.core.ds.quartet.load;

import org.kohsuke.MetaInfServices;
import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.quartet.QuartetSystem;
import uk.ac.uea.cmp.spectre.core.ds.quartet.QuartetSystemList;
import uk.ac.uea.cmp.spectre.core.ds.tree.newick.NewickTree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA. User: Analysis Date: 2004-jul-12 Time: 00:03:05 To
 * change this template use Options | File Templates.
 */
@MetaInfServices(QLoader.class)
public class TreeFileLoader extends AbstractQLoader {

    @Override
    public QuartetSystem load(File file) throws IOException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public QuartetSystemList load(File file, double weight) throws IOException {

        QuartetSystemList sourceDataList = new QuartetSystemList();

        IdentifierList taxa = new IdentifierList();

        BufferedReader in = new BufferedReader(new FileReader(file));

        String line = in.readLine();

        // read until translate block

        boolean readingState = true;

        while (readingState) {

            StringTokenizer sT = new StringTokenizer(line);

            while (sT.hasMoreTokens()) {

                String aWord = sT.nextToken().trim();

                if (aWord.equalsIgnoreCase("TRANSLATE")) {

                    readingState = false;
                    break;
                }
            }

            if (readingState) {
                line = in.readLine();
            }
        }

        // we have found translation

        line = in.readLine();

        while (!readingState && line != null) {

            StringTokenizer sT = new StringTokenizer(line);

            String first = sT.nextToken();

            if (first.trim().equals(";")) {

                readingState = true;

            } else {

                // process a translate line

                if (sT.hasMoreTokens()) {

                    String second = sT.nextToken();

                    if (sT.hasMoreTokens()) {

                        second = sT.nextToken();

                    }

                    taxa.add(new Identifier(second.trim().substring(0, second.trim().length() - 1)));
                }
            }

            if (!readingState) {

                line = in.readLine();
            }
        }


        // now, we will read tree lines until we find an end

        line = in.readLine();

        boolean readingTrees = true;

        while (readingTrees && line != null) {

            line = line.trim();

            if (line.toUpperCase().startsWith("END")) {
                readingTrees = false;
                break;
            }

            if (line.indexOf('(') != -1 && line.endsWith(");")) {

                // We have a tree line here, so parse it and create the tree
                NewickTree aTree = new NewickTree(line);

                // Create quartets from the tree and add to the list (taxa should be the same for each tree)
                sourceDataList.add(new QuartetSystem(taxa, weight, aTree.createQuartets()));
            }

            line = in.readLine();
        }

        in.close();

        return sourceDataList;
    }

    @Override
    public boolean acceptsExtension(String ext) {

        //TODO what extension will this file have???
        return false;
    }

    @Override
    public String getName() {
        return "nexus:trees";
    }
}
