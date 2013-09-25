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

import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetWeights;
import uk.ac.uea.cmp.phygen.tools.chopper.Node;
import uk.ac.uea.cmp.phygen.tools.chopper.Traverser;
import uk.ac.uea.cmp.phygen.tools.chopper.Tree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA. User: Analysis Date: 2004-jul-12 Time: 00:03:05 To
 * change this template use Options | File Templates.
 */
public class TreeFileLoader extends AbstractTreeLoader {

    @Override
    public void load(String fileName, double weight) throws IOException {

        index = 0;

        taxonNames = new LinkedList<>();
        trees = new LinkedList<>();
        weights = new LinkedList<>();

        BufferedReader in = new BufferedReader(new FileReader(fileName));

        String aLine = in.readLine();

        // read until translate block

        boolean readingState = true;

        while (readingState) {

            StringTokenizer sT = new StringTokenizer(aLine);

            while (sT.hasMoreTokens()) {

                String aWord = sT.nextToken().trim();

                if (aWord.equalsIgnoreCase("TRANSLATE")) {

                    readingState = false;
                    break;

                }

            }

            if (readingState) {

                aLine = in.readLine();

            }

        }

        // we have found translation

        aLine = in.readLine();

        while (!readingState && aLine != null) {

            StringTokenizer sT = new StringTokenizer(aLine);

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

                    taxonNames.add(second.trim().substring(0, second.trim().length() - 1));
                }
            }

            if (!readingState) {

                aLine = in.readLine();
            }
        }

        branchLengths = false;
        treeWeights = false;

        // now, we will read tree lines until we find an end

        aLine = in.readLine();

        boolean readingTrees = true;

        while (readingTrees && aLine != null) {

            aLine = aLine.trim();

            if (aLine.toUpperCase().startsWith("END")) {

                readingTrees = false;
                break;

            }

            if (aLine.trim().indexOf('(') != - 1 && aLine.trim().endsWith(");")) {

                // we have a tree line here

                weights.add(weight);

                aLine = aLine.substring(aLine.indexOf('('), aLine.lastIndexOf(')') + 1).trim();

                if (aLine.indexOf(":") != - 1) {

                    branchLengths = true;

                }

                Tree aTree = new Tree(aLine.substring(1, aLine.length() - 1), branchLengths);

                LinkedList<String> numberNames = new LinkedList<>();

                for (int n = 0; n < taxonNames.size(); n++) {

                    numberNames.add(new String((n + 1) + ""));

                }

                aTree.rename(numberNames, taxonNames);

                trees.add(aTree);

            }

            aLine = in.readLine();

        }

        in.close();

    }

}
