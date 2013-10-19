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

import uk.ac.uea.cmp.phygen.tools.chopper.Tree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA. User: Analysis Date: 2004-jul-12 Time: 00:03:05 To
 * change this template use Options | File Templates.
 */

/**
 * Extracts QNet input from file
 *
 * @author sarah
 */
public class TreeLoader extends AbstractTreeLoader {

    @Override
    public void load(String fileName, double weight) throws IOException {

        index = 0;

        trees = new LinkedList<>();
        weights = new LinkedList<>();

        BufferedReader in = new BufferedReader(new FileReader(fileName));

        String aLine = in.readLine();

        if (aLine != null && aLine.indexOf(':') != -1) {

            branchLengths = true;

        } else {

            branchLengths = false;

        }

        if (aLine != null && !(aLine.trim().endsWith(";"))) {

            treeWeights = true;

        } else {

            treeWeights = false;

        }

        while (aLine != null) {

            //if there is a blank line, go to next line
            if (aLine.length() > 0) {

                aLine = aLine.trim();
                int endColon = aLine.lastIndexOf(";");

                if (treeWeights) {

                    String wS = aLine.substring(endColon + 1).trim();
                    //System.out.println("WS:" + wS);
                    weights.add(new Double(Double.parseDouble(wS) * weight));

                } else {
                    //System.out.println("ELSE");
                    weights.add(new Double(weight));
                }


                aLine = aLine.substring(0, aLine.lastIndexOf(')') + 1).trim();

                //quick hack to work around the problem that the weights
                //used for unweighted trees were not correct
                if (branchLengths) {
                    trees.add(new Tree(aLine.substring(1, aLine.length() - 1), branchLengths));
                } else {
                    trees.add(new Tree((aLine.substring(1, aLine.length() - 1)) + ":", branchLengths));
                }
            }
            aLine = in.readLine();

        }

        in.close();

    }
}
