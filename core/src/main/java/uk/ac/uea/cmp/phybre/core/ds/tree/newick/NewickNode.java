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

package uk.ac.uea.cmp.phybre.core.ds.tree.newick;

import uk.ac.uea.cmp.phybre.core.ds.Taxa;
import uk.ac.uea.cmp.phybre.core.ds.Taxon;
import uk.ac.uea.cmp.phybre.core.ds.quartet.CanonicalWeightedQuartetMap;
import uk.ac.uea.cmp.phybre.core.ds.quartet.Quartet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 08/11/13
 * Time: 00:08
 * To change this template use File | Settings | File Templates.
 */
public abstract class NewickNode {

    protected Taxon taxon;
    protected NewickNode parent;
    protected List<NewickNode> branches;
    protected double length;

    protected NewickNode() {
        this.taxon = null;
        this.parent = null;
        this.branches = new ArrayList<>();
        this.length = 0.0;
    }

    public NewickNode getFirstBranch() {
        if (branches.size() >= 1)
            return branches.get(0);

        return null;
    }

    public NewickNode getSecondBranch() {
        if (branches.size() >= 2)
            return branches.get(1);

        return null;
    }

    public boolean hasChildren() {
        return this.branches.size() > 0;
    }

    public boolean isLeaf() {
        return this.branches.isEmpty();
    }

    /**
     * Walks the tree and returns the taxa list.  May throw an IllegalArgumentException if there are duplicate taxa
     * @return The taxa found from this node.
     */
    public Taxa findAllTaxa() {

        Taxa taxa = new Taxa();

        if (this.taxon != null && !this.taxon.isEmpty()) {
            taxa.add(this.taxon);
        }

        for(NewickNode node : this.branches) {
            node.getTaxa(taxa);
        }

        return taxa;
    }

    public int getNbTaxa() {
        return this.findAllTaxa().size();
    }

    protected void getTaxa(Taxa taxa) {
        if (this.taxon != null && !this.taxon.isEmpty()) {
            taxa.add(this.taxon);
        }

        for(NewickNode node : this.branches) {
            node.getTaxa(taxa);
        }
    }

    public boolean isBinary() {

        if (this.isLeaf())
            return true;

        if (branches.size() != 2)
            return false;

        return this.getFirstBranch().isBinary() && this.getSecondBranch().isBinary();
    }

    public boolean hasNonLeafNames() {

        if (!this.isLeaf() && (this.taxon != null && !this.taxon.isEmpty()))
            return true;

        for(NewickNode node : this.branches) {
            if (node.hasNonLeafNames())
                return true;
        }

        return false;
    }

    public boolean allHaveLengths() {

        for(NewickNode node : this.branches) {
            if (!node.allHaveLengths(true))
                return false;
        }

        return true;
    }

    protected boolean allHaveLengths(boolean atRoot) {

        if (this.length == 0.0)
            return false;

        for(NewickNode node : this.branches) {
            if (!node.allHaveLengths(false)) {
                return false;
            }
        }

        return true;
    }

    public void setIndiciesToExternalTaxaList(Taxa externalTaxaList) {
        if (this.taxon != null && !this.taxon.getName().isEmpty()) {
            this.taxon.setId(externalTaxaList.indexOf(this.taxon));
        }

        for(NewickNode node : this.branches) {
            node.setIndiciesToExternalTaxaList(externalTaxaList);
        }
    }

    /*public void rename(TaxaSet oldTaxa, TaxaSet newTaxa) {

        if (this.taxon != null && oldTaxa.contains(this.taxon)) {
            this.name = newTaxa.get(oldTaxa.indexOf(this.name));
        }

        for(NewickNode node : branches) {
            node.rename(oldTaxa, newTaxa);
        }
    }*/

    public List<NewickNode> getBranches() {
        return this.branches;
    }

    public Taxon getTaxon() {
        return this.taxon;
    }

    public double getLength() {
        return this.length;
    }

    public void setTaxon(Taxon taxon) {
        this.taxon = taxon;
    }

    public NewickNode getParent() {
        return parent;
    }

    public void setParent(NewickNode parent) {
        this.parent = parent;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public void addBranch(NewickNode branch) {
        branch.setParent(this);
        this.branches.add(branch);
    }

    /**
     * We take all splits (those here, and those in the branches) and add their weights to the quartets. Weights are
     * defined regardless, so... = 1 for no weights
     * @param qW The quartet weights to update
     * @param remainder The taxa that are not found in this node
     * @return Calculated quartet weights for this node
     */
    protected CanonicalWeightedQuartetMap split(CanonicalWeightedQuartetMap qW, Taxa remainder) {

        for(int i = 0; i < this.branches.size(); i++) {

            NewickNode branch = this.branches.get(i);
            double w = branch.getLength();

            Taxa setA = branch.findAllTaxa();
            Taxa setB = new Taxa(remainder);

            for(NewickNode otherBranch : this.branches) {

                if (branch != otherBranch) {
                    setB.addAll(otherBranch.findAllTaxa(), true);
                }
            }

            if (setA.size() > 1 && setB.size() > 1) {

                // we have a non-trivial split!
                // which we must have, for trivial splits match no quartets...

                // so, for all quartets in here, add the length to their value

                for (int iA1 = 0; iA1 < setA.size() - 1; iA1++) {

                    for (int iA2 = iA1 + 1; iA2 < setA.size(); iA2++) {

                        int a1 = setA.get(iA1).getId();
                        int a2 = setA.get(iA2).getId();

                        for (int iB1 = 0; iB1 < setB.size() - 1; iB1++) {

                            for (int iB2 = iB1 + 1; iB2 < setB.size(); iB2++) {

                                int b1 = setB.get(iB1).getId();
                                int b2 = setB.get(iB2).getId();

                                qW.incrementWeight(new Quartet(a1, a2, b1, b2), w);
                            }
                        }
                    }
                }
            }

            // and recurse if possible
            if (branch.hasChildren()) {
                branch.split(qW, setB);
            }
        }

        return qW;
    }
}
