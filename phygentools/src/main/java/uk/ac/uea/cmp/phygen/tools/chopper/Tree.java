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

import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by IntelliJ IDEA. User: Analysis Date: 2004-jul-11 Time: 20:05:32 To
 * change this template use Options | File Templates.
 */
public class Tree implements Node {

    private static Logger log = LoggerFactory.getLogger(Tree.class);

    private LinkedList<Node> branches;
    private LinkedList<Double> weights;

    /**
     * The source is assumed to be something like
     *
     * (A:0.5, B:0.5):0.1, C:0.2, (D:0.1, E:324)
     *
     * this should be parsed so that three branches and corresponding
     * weights are added
     * @param source
     * @param branchLengths
     * @throws IOException
     */
    public Tree(String source, boolean branchLengths) throws IOException {

        source = source.trim();

        int level = 0;
        int start = 0;
        branches = new LinkedList<>();
        weights = new LinkedList<>();

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

                String subSource = source.substring(start, i).trim();
                start = i + 1;

                if (branchLengths) {

                    int indexColon = subSource.lastIndexOf(':');
                    String wS = subSource.substring(indexColon + 1);

                    if (subSource.lastIndexOf(')') != -1) {

                        subSource = subSource.substring(0, subSource.lastIndexOf(')') + 1).trim();

                    } else {

                        subSource = subSource.substring(0, indexColon).trim();

                    }

                    weights.add(new Double(Double.parseDouble(wS.trim())));

                } else {
                    if (degree2root) {
                        weights.add(new Double(0.5));
                    } else {
                        weights.add(new Double(1.0));
                    }
                }

                if (subSource.startsWith("(") && subSource.endsWith(")")) {

                    branches.add(new Tree(subSource.substring(1, subSource.length() - 1), branchLengths));

                } else if ((!subSource.startsWith("(")) && (!subSource.endsWith(")"))) {

                    branches.add(new Leaf(subSource));

                } else {

                    throw new IOException("QNet.Chopper: Syntax error in Newick string: wrong number of brackets!");
                }

            }

        }

        String subSource = source.substring(start, source.length()).trim();

        if (branchLengths) {

            int indexColon = subSource.lastIndexOf(':');
            String wS = subSource.substring(indexColon + 1);

            if (subSource.lastIndexOf(')') != -1) {

                subSource = subSource.substring(0, subSource.lastIndexOf(')') + 1).trim();

            } else {

                subSource = subSource.substring(0, indexColon).trim();

            }

            weights.add(new Double(Double.parseDouble(wS.trim())));

        } else {
            if (degree2root) {
                weights.add(new Double(0.5));
            } else {
                weights.add(new Double(1.0));
            }
        }

        if (subSource.startsWith("(") && subSource.endsWith(")")) {

            branches.add(new Tree(subSource.substring(1, subSource.length() - 1), branchLengths));

        } else if ((!subSource.startsWith("(")) && (!subSource.endsWith(")"))) {

            branches.add(new Leaf(subSource));

        } else {

            throw new IOException("QNet.Chopper: Syntax error in Newick string: wrong number of brackets!");

        }

    }

    public void index(LinkedList<String> taxonNames) {

        for(Node node : branches) {
            node.index(taxonNames);
        }
    }

    public void harvestNames(LinkedList<String> taxonNames) {

        for(Node node : branches) {
            node.harvestNames(taxonNames);
        }
    }

    public void harvest(LinkedList<Integer> taxa) {

        for(Node node : branches) {
            node.harvest(taxa);
        }
    }

    public void split(QuartetWeights qW, LinkedList<Integer> remainder) {

        // so...
        // we take all splits (those here, and those in the branches)
        // and add their weights to the quartets. Weights are defined regardless, so... = 1 for no weights

        ListIterator lI = branches.listIterator();
        ListIterator wI = weights.listIterator();

        while (lI.hasNext()) {

            LinkedList<Integer> setA = new LinkedList<>();
            LinkedList<Integer> setB = (LinkedList<Integer>) remainder.clone();

            Node branch = (Node) lI.next();
            double w = ((Double) wI.next()).doubleValue();

            branch.harvest(setA);

            ListIterator lJ = branches.listIterator();

            while (lJ.hasNext()) {

                Node otherBranch = (Node) lJ.next();

                if (branch != otherBranch) {
                    otherBranch.harvest(setB);
                }
            }

            if (setA.size() > 1 && setB.size() > 1) {

                // we have a non-trivial split!
                // which we must have, for trivial splits match no quartets...

                // so, for all quartets in here, add the weight to their value

                for (int iA1 = 0; iA1 < setA.size() - 1; iA1++) {

                    for (int iA2 = iA1 + 1; iA2 < setA.size(); iA2++) {

                        int a1 = 1 + setA.get(iA1);
                        int a2 = 1 + setA.get(iA2);

                        for (int iB1 = 0; iB1 < setB.size() - 1; iB1++) {

                            for (int iB2 = iB1 + 1; iB2 < setB.size(); iB2++) {

                                int b1 = 1 + setB.get(iB1);
                                int b2 = 1 + setB.get(iB2);

                                qW.incrementWeight(a1, a2, b1, b2, w);
                            }
                        }
                    }
                }
            }

            /*
             * else if (setA.size () == 1 && setB.size () > 2) {
             *
             * // this is a trivial split
             *
             * for (int iA = 0; iA < setA.size (); iA++) {
             *
             * int a = 1 + ((Integer) setA.get (iA)).intValue ();
             *
             * for (int iB1 = 0; iB1 < setB.size () - 2; iB1++) {
             *
             * for (int iB2 = iB1 + 1; iB2 < setB.size () - 1; iB2++) {
             *
             * for (int iB3 = iB2 + 1; iB3 < setB.size (); iB3++) {
             *
             * int b1 = 1 + ((Integer) setB.get (iB1)).intValue (); int b2 = 1 +
             * ((Integer) setB.get (iB2)).intValue (); int b3 = 1 + ((Integer)
             * setB.get (iB3)).intValue ();
             *
             * qW.setWeight (a, b1, b2, b3, qW.getWeight (a, b1, b2, b3) + w);
             * qW.setWeight (a, b2, b1, b3, qW.getWeight (a, b2, b1, b3) + w);
             * qW.setWeight (a, b3, b1, b2, qW.getWeight (a, b3, b1, b2) + w);
             *
             * }
             *
             * }
             *
             * }
             *
             * }
             *
             * }
             */
            // and recurse if possible

            if (branch.isTree()) {
                ((Tree) branch).split(qW, setB);
            }
        }
    }


    public QuartetWeights quartetize(int N) {

        QuartetWeights qW = new QuartetWeights();

        qW.ensureCapacity(N);

        // this gives values for non-supported quartets in this tree...

        qW.initialize();

        this.split(qW, new LinkedList<Integer>());

        return qW;
    }

    public void rename(LinkedList<String> oldTaxa, LinkedList<String> newTaxa) {

        ListIterator lI = branches.listIterator();

        while (lI.hasNext()) {
            ((Node) lI.next()).rename(oldTaxa, newTaxa);
        }
    }

    public boolean isTree() {

        return true;
    }

}
