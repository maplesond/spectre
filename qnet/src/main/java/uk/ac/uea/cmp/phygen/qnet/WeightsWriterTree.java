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
package uk.ac.uea.cmp.phygen.qnet;

import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetWeights;
import uk.ac.uea.cmp.phygen.core.ds.tree.BinaryTree;
import uk.ac.uea.cmp.phygen.core.ds.tree.InnerNode;
import uk.ac.uea.cmp.phygen.core.ds.tree.Leaf;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

class WeightsWriterTree {

    /**
     *
     * Write weights for the presented tree.
     *
     * Calculates split weights and prints them to a nexus file.
     *
     */
    public static void writeWeights(QNet parent, ArrayList cN, String outputName) {

        ArrayList theLists = parent.getTheLists();
        QuartetWeights theQuartetWeights = parent.getWeights();
        ArrayList taxonNames = parent.getTaxonNames();
        boolean useMax = parent.getUseMax();
        int N = parent.getN();

        /**
         *
         * Sooo... for each Leaf add in a trivial split, for each InnerNode add
         * in a split of its fillList against the rest... with the fillList
         * taken against all others for weights...
         *
         */
        ArrayList splits = new ArrayList();
        ArrayList weights = new ArrayList();

        /**
         *
         * Go through all splits surrounding the centre
         *
         */
        BinaryTree t1 = (BinaryTree) cN.get(0);
        BinaryTree t2 = (BinaryTree) cN.get(1);
        BinaryTree t3 = (BinaryTree) cN.get(2);

        ArrayList a1 = new ArrayList();
        ArrayList a2 = new ArrayList();
        ArrayList a3 = new ArrayList();

        t1.fillList(a1);
        t2.fillList(a2);
        t3.fillList(a3);


        recursiveTreeWeights(parent, t1, a2, a3, splits, weights);
        recursiveTreeWeights(parent, t2, a1, a3, splits, weights);
        recursiveTreeWeights(parent, t3, a1, a2, splits, weights);

        /**
         *
         * Knowing the splits, add them
         *
         */
        /**
         *
         * Here, calculate mean weight for the trivial splits, then print it all
         * to file
         *
         */
        int count = 0;
        double score = 0;

        for (int n = 0; n < splits.size(); n++) {

            ArrayList aSplit = (ArrayList) splits.get(n);

            if (aSplit.size() > 1) {

                score += ((Double) weights.get(n)).doubleValue();

                count++;

            }

        }

        for (int n = 0; n < splits.size(); n++) {

            ArrayList aSplit = (ArrayList) splits.get(n);

            if (aSplit.size() == 1) {

                if (count == 0) {

                    weights.set(n, new Double(1.0));

                } else {

                    weights.set(n, new Double(score / ((double) count)));

                }

            }

        }

        /**
         *
         * Then, print.
         *
         */
        String nexusString = new String();

        nexusString += "#NEXUS\nBEGIN taxa;\nDIMENSIONS ntax=" + N + ";\nTAXLABELS\n";

        for (int n = 0; n < N; n++) {

            nexusString += ((String) taxonNames.get(n)) + "\n";

        }

        nexusString += ";\nEND;\n\nBEGIN st_splits;\nDIMENSIONS ntax=" + N + " nsplits=" + splits.size() + ";\n";
        nexusString += "FORMAT\nlabels\nweights\n;\nPROPERTIES\nFIT=100\nweakly compatible\ncyclic\n;";

        nexusString += "\nMATRIX\n";

        for (int n = 0; n < splits.size(); n++) {

            /**
             *
             * Add one for splitstree...
             *
             */
            nexusString += "" + (n + 1) + "   " + ((Double) weights.get(n)).doubleValue() + "  ";

            ArrayList aSplit = (ArrayList) splits.get(n);

            for (int p = 0; p < aSplit.size(); p++) {

                /**
                 *
                 * Add one for splitstree...
                 *
                 */
                nexusString += " " + (((Integer) aSplit.get(p)).intValue());

            }

            nexusString += ",\n";

        }

        nexusString += ";\nEND;";

        try {


            FileWriter fileOutput = new FileWriter(outputName);

            fileOutput.write(nexusString);

            fileOutput.close();

        } catch (IOException e) {
        }

    }

    /**
     *
     * Recursive split calculator
     *
     */
    static void recursiveTreeWeights(QNet parent, BinaryTree aT, ArrayList left, ArrayList right, ArrayList splits, ArrayList weights) {

        ArrayList theLists = parent.getTheLists();
        QuartetWeights theQuartetWeights = parent.getWeights();
        ArrayList taxonNames = parent.getTaxonNames();
        boolean useMax = parent.getUseMax();
        int N = parent.getN();

        /**
         *
         * This function delves deeper and adds splits
         *
         */
        if (aT.hasChildren()) {

            ArrayList previous = QNet.join(left, right);
            ArrayList rightSibling = new ArrayList();
            ArrayList leftSibling = new ArrayList();
            ((InnerNode) aT).getLeft().fillList(leftSibling);
            ((InnerNode) aT).getRight().fillList(rightSibling);

            /**
             *
             * This split
             *
             */
            ArrayList aSplit = new ArrayList();
            aT.fillList(aSplit);

            /**
             *
             * Value of this one should be the value of aSplit
             *
             * This is the value of calcing left, right against the filling of
             * the children
             *
             */
            Summer aSummer = new Summer(theQuartetWeights, left, right, leftSibling, rightSibling, useMax);

            double aWeight = aSummer.getScore() / ((double) aSummer.getCount());

            if (aWeight > 0) {

                weights.add(new Double(aWeight));
                splits.add(aSplit);

            } /**
             *
             * Checking for negative weights where another weight is positive
             *
             */
            else if (aWeight < 0) {

                Summer bSummer = new Summer(theQuartetWeights, left, leftSibling, right, rightSibling, useMax);
                double bWeight = bSummer.getScore() / ((double) bSummer.getCount());

                Summer cSummer = new Summer(theQuartetWeights, left, rightSibling, right, leftSibling, useMax);
                double cWeight = cSummer.getScore() / ((double) cSummer.getCount());

                if (bWeight > 0 || cWeight > 0) {

                    System.out.println();
                    System.out.println("Possible error!");
                    System.out.println();
                    System.out.print("The chosen split is ");

                    for (int n = 0; n < left.size(); n++) {

                        System.out.print("" + ((Integer) left.get(n)).intValue() + " ");

                    }

                    for (int n = 0; n < right.size(); n++) {

                        System.out.print("" + ((Integer) right.get(n)).intValue() + " ");

                    }

                    System.out.println();
                    System.out.println("with weight " + aWeight + ".");
                    System.out.println();
                    System.out.print("against split ");

                    for (int n = 0; n < left.size(); n++) {

                        System.out.print("" + ((Integer) left.get(n)).intValue() + " ");

                    }

                    for (int n = 0; n < leftSibling.size(); n++) {

                        System.out.print("" + ((Integer) leftSibling.get(n)).intValue() + " ");

                    }

                    System.out.println();
                    System.out.println("with weight " + bWeight + ".");
                    System.out.println();
                    System.out.print("and ");

                    for (int n = 0; n < left.size(); n++) {

                        System.out.print("" + ((Integer) left.get(n)).intValue() + " ");

                    }

                    for (int n = 0; n < rightSibling.size(); n++) {

                        System.out.print("" + ((Integer) rightSibling.get(n)).intValue() + " ");

                    }

                    System.out.println();
                    System.out.println("with weight " + cWeight + ".");
                    System.out.println();

                }

            }

            /**
             *
             * The children splits
             *
             */
            recursiveTreeWeights(parent, ((InnerNode) aT).getLeft(), previous, rightSibling, splits, weights);
            recursiveTreeWeights(parent, ((InnerNode) aT).getRight(), previous, leftSibling, splits, weights);

        } else {

            ArrayList aTrivialSplit = new ArrayList();

            aTrivialSplit.add(new Integer(((Leaf) aT).getIdentity()));

            splits.add(aTrivialSplit);

            /**
             *
             * Symbolic weight, alter later
             *
             */
            weights.add(new Double(1.0));

        }

    }
}