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

import uk.ac.uea.cmp.phygen.core.ds.quartet.Quartet;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetWeights;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 06/11/13
 * Time: 22:58
 * To change this template use File | Settings | File Templates.
 */
public class SubTree implements Node {



    @Override
    public void index(List<String> taxonNames) {

        /*for(Node node : branches) {
            node.index(taxonNames);
        }  */
    }

    @Override
    public void harvestNames(List<String> taxonNames) {

        /*for(Node node : branches) {
            node.harvestNames(taxonNames);
        }  */
    }

    @Override
    public void harvest(List<Integer> taxa) {

       /* for(Node node : branches) {
            node.harvest(taxa);
        } */
    }

    /**
     * We take all splits (those here, and those in the branches) and add their weights to the quartets. Weights are
     * defined regardless, so... = 1 for no weights
     * @param qW
     * @param remainder
     * @return
     */
    public QuartetWeights split(QuartetWeights qW, List<Integer> remainder) {

        /*for(int i = 0; i < this.branches.size(); i++) {

            List<Integer> setA = new ArrayList<>();
            List<Integer> setB = new ArrayList<>(remainder);

            Node branch = branches.get(i);
            double w = this.weights.get(i);

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

                // so, for all quartets in here, add the length to their value

                for (int iA1 = 0; iA1 < setA.size() - 1; iA1++) {

                    for (int iA2 = iA1 + 1; iA2 < setA.size(); iA2++) {

                        int a1 = 1 + setA.get(iA1);
                        int a2 = 1 + setA.get(iA2);

                        for (int iB1 = 0; iB1 < setB.size() - 1; iB1++) {

                            for (int iB2 = iB1 + 1; iB2 < setB.size(); iB2++) {

                                int b1 = 1 + setB.get(iB1);
                                int b2 = 1 + setB.get(iB2);

                                qW.incrementWeight(new Quartet(a1, a2, b1, b2), w);
                            }
                        }
                    }
                }
            }

            // and recurse if possible
            if (branch.internalNode()) {
                ((NewickTree) branch).split(qW, setB);
            }
        }

        return qW; */

        return null;
    }

    public QuartetWeights quartetize(int N) {

        QuartetWeights qW = new QuartetWeights(Quartet.over4(N));

        this.split(qW, new LinkedList<Integer>());

        return qW;
    }

    @Override
    public void rename(List<String> oldTaxa, List<String> newTaxa) {

       /* for(Node node : this.branches) {
            node.rename(oldTaxa, newTaxa);
        }  */
    }

    @Override
    public boolean internalNode() {
        return true;
    }
}
