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
package uk.ac.uea.cmp.phygen.superq.chopper;

import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetWeights;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by IntelliJ IDEA. User: Analysis Date: 2004-jul-11 Time: 20:05:32 To
 * change this template use Options | File Templates.
 */
public class Tree implements Node {

    public Tree(String source, boolean branchLengths) {

        /*
         *
         * the source is assumed to be something like
         *
         * (A:0.5, B:0.5):0.1, C:0.2, (D:0.1, E:324)
         *
         * this should be parsed so that three branches and corresponding
         * weights are added
         *
         */

        source = source.trim();

        int level = 0;
        int start = 0;
        branches = new LinkedList();
        weights = new LinkedList();

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

                    if (subSource.lastIndexOf(')') != - 1) {

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

                    //System.out.println("Found taxon: " + subSource);
                    branches.add(new Leaf(subSource));

                } else {

                    System.out.println("QNet.Chopper: Syntax error in Newick string: wrong number of brackets!");
                    System.exit(1);

                }

            }

        }

        String subSource = source.substring(start, source.length()).trim();

        if (branchLengths) {

            int indexColon = subSource.lastIndexOf(':');
            String wS = subSource.substring(indexColon + 1);

            if (subSource.lastIndexOf(')') != - 1) {

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

            System.out.println("QNet.Chopper: Syntax error in Newick string: wrong number of brackets!");
            System.exit(1);

        }

    }

    public void index(LinkedList taxonNames) {

        ListIterator lI = branches.listIterator();

        while (lI.hasNext()) {

            ((Node) lI.next()).index(taxonNames);

        }

    }

    public void harvestNames(LinkedList taxonNames) {

        ListIterator lI = branches.listIterator();

        while (lI.hasNext()) {

            ((Node) lI.next()).harvestNames(taxonNames);

        }

    }

    public void harvest(LinkedList taxa) {

        ListIterator lI = branches.listIterator();

        while (lI.hasNext()) {

            ((Node) lI.next()).harvest(taxa);

        }

    }

    public void split(QuartetWeights qW, LinkedList remainder) {

        // so...
        // we take all splits (those here, and those in the branches)
        // and add their weights to the quartets. Weights are defined regardless, so... = 1 for no weights

        ListIterator lI = branches.listIterator();
        ListIterator wI = weights.listIterator();

        while (lI.hasNext()) {

            LinkedList setA = new LinkedList();
            LinkedList setB = (LinkedList) remainder.clone();

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

                        int a1 = 1 + ((Integer) setA.get(iA1)).intValue();
                        int a2 = 1 + ((Integer) setA.get(iA2)).intValue();

                        for (int iB1 = 0; iB1 < setB.size() - 1; iB1++) {

                            for (int iB2 = iB1 + 1; iB2 < setB.size(); iB2++) {

                                int b1 = 1 + ((Integer) setB.get(iB1)).intValue();
                                int b2 = 1 + ((Integer) setB.get(iB2)).intValue();

                                qW.setWeight(a1, a2, b1, b2, qW.getWeight(a1, a2, b1, b2) + w);

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

    public void rename(LinkedList oldTaxa, LinkedList newTaxa) {

        ListIterator lI = branches.listIterator();

        while (lI.hasNext()) {

            ((Node) lI.next()).rename(oldTaxa, newTaxa);

        }

    }

    public boolean isTree() {

        return true;

    }
    LinkedList branches;
    LinkedList weights;
}
