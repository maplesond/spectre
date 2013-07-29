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
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA. User: Analysis Date: 2004-jul-12 Time: 00:03:05 To
 * change this template use Options | File Templates.
 */
public class TreeFileLoader implements Source {

    private static Logger log = LoggerFactory.getLogger(TreeFileLoader.class);

    public void load(String fileName, double weight) throws IOException {

        index = 0;

        taxonNames = new LinkedList();
        trees = new LinkedList();
        weights = new LinkedList();

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

                weights.add(new Double(weight));

                aLine = aLine.substring(aLine.indexOf('('), aLine.lastIndexOf(')') + 1).trim();

                if (aLine.indexOf(":") != - 1) {

                    branchLengths = true;

                }

                Tree aTree = new Tree(aLine.substring(1, aLine.length() - 1), branchLengths);

                LinkedList numberNames = new LinkedList();

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
    LinkedList qWs;
    boolean branchLengths, treeWeights;
    LinkedList trees, weights, taxonNames;
    int index;
}
