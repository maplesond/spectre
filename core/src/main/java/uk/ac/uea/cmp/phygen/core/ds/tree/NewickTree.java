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

package uk.ac.uea.cmp.phygen.core.ds.tree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 06/11/13
 * Time: 22:30
 * To change this template use File | Settings | File Templates.
 */
public class NewickTree {

    private List<Node> branches= new ArrayList<>();
    private List<Double> weights = new ArrayList<>();

    public NewickTree(String source) throws IOException {
        this.parseNewick(source, 1.0);
    }


    public void parseNewick(String source, double weight) throws IOException {

        if (source == null)
            return;

        String trimmedSource = source.trim();

        // Should be a semi colon in the source
        int endColon = trimmedSource.lastIndexOf(";");

        if (endColon == -1)
            throw new IOException("A newick tree must contain a semi-colon.  Typically this should be the last character in the " +
                    "tree, unless this is a weighting will come after the semi-colon.");

        // If the semi-colon isn't at the end of the string then this is probably a weighted newick tree
        boolean weightedNewickTree = !trimmedSource.endsWith(";");

        // Try to work out the weighting factor, multiply it with a user specified factor if present, otherwise just use
        // the user weighting factor
        double weightingFactor = weightedNewickTree ?
                Double.parseDouble(trimmedSource.substring(endColon + 1).trim()) * weight :
                weight;


        // This represents either a Subtree or a Branch
        String subSource = trimmedSource.substring(0, trimmedSource.lastIndexOf(')') + 1).trim();

        /*

        boolean branchLengths = trimmedSource != null && trimmedSource.indexOf(':') != -1;




        line = trimmedSource.substring(0, trimmedSource.lastIndexOf(')') + 1).trim();

        //quick hack to work around the problem that the weights
        //used for unweighted trees were not correct
        NewickTree tree = branchLengths ?
                new NewickTree(line.substring(1, line.length() - 1), newWeight) :
                new NewickTree((line.substring(1, line.length() - 1) + ":", newWeight);


        boolean branchLengths = trimmedSource.indexOf(':') != -1;

        this.parseNewick(trimmedSource, branchLengths);   */
    }

    private void parseNewick(String source, boolean branchLengths) throws IOException {


        int level = 0;
        int start = 0;

        //flag used to indicate whether the root has degree 2
        //used to get the weights in unweighted trees right
        boolean degree2root = false;
        int rootflagposition = source.lastIndexOf(':');
        if ((!branchLengths) && (rootflagposition != -1)) {
            degree2root = true;
            source = source.substring(0, rootflagposition);
        }

        for (int i = 0; i < source.length(); i++) {

            char c = source.charAt(i);

            if (c == '(') {
                level++;
            } else if (c == ')') {
                level--;
            } else if (c == ',' && level == 0) {
                start = i + 1;
                this.parseChunk(source.substring(start, i).trim(), branchLengths, degree2root);
            }
        }

        this.parseChunk(source.substring(start, source.length()).trim(), branchLengths, degree2root);
    }


    private void parseChunk(String source, boolean branchLengths, boolean degree2root) throws IOException {

        /*String subSource = source.trim();

        if (branchLengths) {

            int indexColon = subSource.lastIndexOf(':');
            String wS = subSource.substring(indexColon + 1);

            subSource = subSource.lastIndexOf(')') != -1 ?
                    subSource.substring(0, subSource.lastIndexOf(')') + 1).trim() :
                    subSource.substring(0, indexColon).trim();

            weights.add(new Double(Double.parseDouble(wS.trim())));
        }
        else {
            weights.add(degree2root ? 0.5 : 1.0);
        }

        if (subSource.startsWith("(") && subSource.endsWith(")")) {

            branches.add(new NewickTree(subSource.substring(1, subSource.length() - 1), branchLengths, degree2root));

        } else if ((!subSource.startsWith("(")) && (!subSource.endsWith(")"))) {

            branches.add(new Leaf(subSource));

        } else {

            throw new IOException("Syntax error in Newick string: wrong number of brackets!");
        }             */
    }



    protected static class SubTree {

    }


}
