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
package uk.ac.uea.cmp.phygen.tools.chopper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetWeights;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by IntelliJ IDEA. User: Analysis Date: 2004-jul-12 Time: 00:03:05 To
 * change this template use Options | File Templates.
 */
/**
 * Extracts QNet input from file
 *
 * @author sarah
 */
public class TreeLoader implements Source {

    private static Logger log = LoggerFactory.getLogger(TreeLoader.class);

    int index;
    LinkedList qWs;
    boolean branchLengths, treeWeights;
    LinkedList trees, weights, taxonNames;

    public TreeLoader() {
    }

    public void load(String fileName, double weight) throws IOException {

        index = 0;

        trees = new LinkedList();
        weights = new LinkedList();

        BufferedReader in = new BufferedReader(new FileReader(fileName));

        String aLine = in.readLine();

        if (aLine != null && aLine.indexOf(':') != - 1) {

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

    public void harvestNames(LinkedList newTaxonNames) {

        ListIterator lI = trees.listIterator();

        while (lI.hasNext()) {

            ((Node) lI.next()).harvestNames(newTaxonNames);

        }

    }

    public LinkedList getTaxonNames() {

        LinkedList result = new LinkedList();

        ListIterator lI = trees.listIterator();

        while (lI.hasNext()) {

            LinkedList treeNames = new LinkedList();

            ((Node) lI.next()).harvestNames(treeNames);

            result.add(treeNames);

        }

        return result;

    }

    public void translate(LinkedList newTaxonNames) {

        taxonNames = newTaxonNames;

        ListIterator lI = trees.listIterator();

        while (lI.hasNext()) {

            ((Node) lI.next()).index(taxonNames);

        }

    }

    public void process() {

        qWs = Traverser.traverse(trees, taxonNames.size());

    }

    public LinkedList getQuartetWeights() {

        return qWs;

    }

    public LinkedList getWeights() {

        return weights;

    }

    public double getWSum() {

        double sum = 0.0;

        for (int n = 0; n < weights.size(); n++) {

            sum += ((Double) weights.get(n)).doubleValue();

        }

        return sum;

    }

    public QuartetWeights getNextQuartetWeights() {

        Tree aTree = (Tree) trees.get(index);

        index++;

        return Traverser.quartetize(aTree, taxonNames.size());

    }

    public double getNextWeight() {

        return ((Double) weights.get(index)).doubleValue();

    }

    public boolean hasMoreSets() {

        if (index < trees.size()) {

            return true;

        } else {

            return false;

        }

    }
}
