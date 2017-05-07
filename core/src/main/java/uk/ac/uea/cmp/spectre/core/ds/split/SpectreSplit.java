/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2017  UEA School of Computing Sciences
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package uk.ac.uea.cmp.spectre.core.ds.split;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.network.draw.SplitSystemDraw;


/**
 * Created with IntelliJ IDEA.
 * User: Sarah_2
 * Date: 24/04/13
 * Time: 16:34
 * To change this template use File | Settings | File Templates.
 */
public class SpectreSplit implements Split {

    private SplitBlock aSide;
    private SplitBlock bSide;
    private double weight;
    private boolean active;

    public SpectreSplit(SplitBlock aSide, int nbTaxa) {
        this(aSide, nbTaxa, 1.0, false);
    }

    public SpectreSplit(SplitBlock aSide, int nbTaxa, boolean zerobased) {
        this(aSide, nbTaxa, 1.0, zerobased);
    }

    public SpectreSplit(SplitBlock aSide, int nbTaxa, double weight) {
        this(aSide, nbTaxa, weight, false);
    }

    public SpectreSplit(SplitBlock aSide, int nbTaxa, double weight, boolean zerobased) {
        this(aSide, aSide.makeComplement(nbTaxa, zerobased), weight);
    }

    public SpectreSplit(SplitBlock aSide, SplitBlock bSide) {
        this(aSide, bSide, 1.0);
    }
    public SpectreSplit(SplitBlock aSide, SplitBlock bSide, double weight) {
        this(aSide, bSide, weight, true);
    }
    public SpectreSplit(SplitBlock aSide, SplitBlock bSide, double weight, boolean active) {
        this.aSide = aSide;
        this.bSide = bSide;
        this.weight = weight;
        this.active = active;
    }


    public SpectreSplit(Split split) {
        this(split.getASide().copy(), split.getBSide().copy(), split.getWeight(), split.isActive());
    }

    /**
     * Creates a new split merged from the two provided splits
     *
     * @param s1 Split 1
     * @param s2 Split 2
     */
    public SpectreSplit(Split s1, Split s2) {
        this(s1.getASide().copy(), s1.getNbTaxa());
        this.mergeASides(s2);
    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder()
                .append(aSide)
                .append(bSide)
                .append(weight)
                .append(active)
                .toHashCode();
    }


    @Override
    public boolean equals(Object o) {

        if (o == null)
            return false;

        if (this == o)
            return true;

        SpectreSplit other = (SpectreSplit) o;

        return new EqualsBuilder()
                .append(aSide, other.aSide)
                .append(bSide, other.bSide)
                .append(weight, other.weight)
                .isEquals();
    }


    public SplitBlock getASide() {
        return aSide;
    }

    public SplitBlock getBSide() {
        return bSide;
    }

    public int getNbTaxa() {
        return this.aSide.size() + this.bSide.size();
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public int getASideFirst() {
        return this.aSide.getFirst();
    }

    @Override
    public int getASideLast() {
        return this.aSide.getLast();
    }

    @Override
    public int getASideSize() {
        return this.aSide.size();
    }

    @Override
    public int getBSideSize() {
        return this.bSide.size();
    }

    @Override
    public int[] getASideAsIntArray() {
        return this.aSide.toIntArray();
    }

    @Override
    public int[] getBSideAsIntArray() {
        return this.bSide.toIntArray();
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void sort() {

        this.aSide.sort();
        this.bSide.sort();
    }

    public int getSplitElement(SplitSide side, int index) {
        return side.getSplitElement(this, index);
    }

    @Override
    public void mergeASides(Split split) {
        this.aSide.merge(split.getASide());
        this.bSide = aSide.makeComplement(this.getNbTaxa());
    }

    @Override
    public Split makeCanonical() {

        SpectreSplit copy = this.makeSortedCopy();
        if (copy.aSide.size() > copy.bSide.size() ||
                (copy.aSide.size() == copy.bSide.size() && copy.aSide.getFirst() > copy.bSide.getFirst())) {
            SplitBlock temp = copy.aSide.copy();
            copy.aSide = copy.bSide;
            copy.bSide = temp;
        }
        return copy;
    }

    @Override
    public Split copy() {
        return new SpectreSplit(this.getASide().copy(), this.getBSide().copy(), this.weight);
    }

    public SpectreSplit makeSortedCopy() {

        SpectreSplit copy = new SpectreSplit(this);
        copy.sort();
        return copy;
    }

    @Override
    public int compareTo(Split o) {

        if (o == null)
            throw new NullPointerException("The split to compare is null");

        if (o == this)
            return 0;

        int difNbTaxa = this.getNbTaxa() - o.getNbTaxa();

        if (difNbTaxa == 0) {

            int difASide = this.aSide.compareTo(o.getASide());

            if (difASide == 0) {
                double diffWeight = this.weight - o.getWeight();

                if (diffWeight == 0.0) {
                    return 0;
                } else {
                    return diffWeight < 0.0 ? -1 : 1;
                }
            } else {
                return difASide;
            }
        } else {
            return difNbTaxa;
        }
    }



    /**
     * Summed up distanceMatrix from all elements on the A side to all elements on the B side.
     *
     * @param distanceMatrix Distance matrix
     * @return P
     */
    public double calculateP(DistanceMatrix distanceMatrix) {

        boolean splited[] = new boolean[this.getNbTaxa()];
        for (int h = 0; h < splited.length; h++) {
            splited[h] = false;
        }

        // Array stores the info which elements are on one side each element of the split
        for (int j = 0; j < this.aSide.size(); j++) {
            for (int k = 0; k < this.getNbTaxa(); k++) {
                if (this.aSide.get(j) == k) {
                    splited[k] = true;
                }
            }
        }

        // Sums up all distanceMatrix from the elements on the one side of the edge to the other side
        double p = 0.0;

        for (int j = 0; j < this.aSide.size(); j++) {
            for (int k = 0; k < this.getNbTaxa(); k++) {
                if (splited[k] == false) {
                    p += distanceMatrix.getDistance(this.aSide.get(j), k);
                }
            }
        }

        return p;
    }

    public boolean isTrivial() {
        return this.aSide.size() == 1 || this.bSide.size() == 1;
    }

    @Override
    public Integer getTrivial() {
        if (this.aSide.size() == 1) {
            return new Integer(this.aSide.getFirst());
        }
        else if (this.bSide.size() == 1) {
            return new Integer(this.bSide.getFirst());
        }
        else {
            return null;
        }
    }

    @Override
    public String toString() {
        return (this.active ? "ON " : "OFF") + " : {" + this.aSide.toString() + " | " + this.bSide.toString() + "} : " + this.weight;
    }

    /**
     * Check to see if this split is compatible with another split.  This returns true (compatible) if one of the four
     * intersections A1 n A2, A1 n B2, A2 n B1 or A2 n B2 is empty.  Otherwise this returns false (incompatible).
     * @param other The other split to test
     * @return True if compatible, false if incompatible
     */
    @Override
    public boolean isCompatible(Split other) {

        if (!(other instanceof SpectreSplit)) {
            throw new IllegalArgumentException("Can't check compatibility of splits of different types.");
        }

        SpectreSplit o = (SpectreSplit)other;

        if (this.getNbTaxa() != o.getNbTaxa()) {
            throw new IllegalArgumentException("Comparing splits that have different numbers of taxa!");
        }

        SplitBlock thisASide = this.getASide();
        SplitBlock thisBSide = this.getBSide();
        SplitBlock otherASide = o.getASide();
        SplitBlock otherBSide = o.getBSide();

        // Check to see that at least one pair of split block doesn't contain any taxa found in the other.  If that's
        // the case then these two splits are compatible
        return  !thisASide.containsAny(otherASide) || !thisASide.containsAny(otherBSide) ||
                !thisBSide.containsAny(otherASide) || !thisBSide.containsAny(otherBSide);
    }

    public Compatible getCompatible(Split other) {

        if (!(other instanceof SpectreSplit)) {
            throw new IllegalArgumentException("Can't check compatibility of splits of different types.");
        }

        SpectreSplit o = (SpectreSplit)other;

        if (this.getNbTaxa() != o.getNbTaxa()) {
            throw new IllegalArgumentException("Comparing splits that have different numbers of taxa!");
        }

        SplitBlock thisASide = this.getASide();
        SplitBlock thisBSide = this.getBSide();
        SplitBlock otherASide = o.getASide();
        SplitBlock otherBSide = o.getBSide();

        if (!thisASide.containsAny(otherASide)) {
            return Compatible.YES_11;
        } else if (!thisASide.containsAny(otherBSide)) {
            return Compatible.YES_10;
        } else if (!thisBSide.containsAny(otherASide)) {
            return Compatible.YES_01;
        } else if (!thisBSide.containsAny(otherBSide)) {
            return Compatible.YES_00;
        } else {
            return Compatible.NO;
        }

    }

    @Override
    public boolean restrictionExists(int a, int b, int c, int d, int nr) {
        boolean aa = this.aSide.contains(a);
        boolean ab = this.aSide.contains(b);
        boolean ac = this.aSide.contains(c);
        boolean ad = this.aSide.contains(d);

        // Just check user provided taxa that exist in this split otherwise error
        if (!aa && !this.bSide.contains(a)) {
            throw new IllegalArgumentException("Index A (" + a + ") does not exist in split.");
        }
        if (!ab && !this.bSide.contains(b)) {
            throw new IllegalArgumentException("Index B (" + b + ") does not exist in split.");
        }
        if (!ac && !this.bSide.contains(c)) {
            throw new IllegalArgumentException("Index C (" + c + ") does not exist in split.");
        }
        if (!ad && !this.bSide.contains(d)) {
            throw new IllegalArgumentException("Index D (" + d + ") does not exist in split.");
        }

        if (nr == 0) {
            if (((ab == ac) && (ac == ad)) && (aa != ab)) {
                return true;
            }
        }
        else if (nr == 1) {
            if (((aa == ac) && (ac == ad)) && (aa != ab)) {
                return true;
            }
        }
        else if (nr == 2) {
            if (((aa == ab) && (ab == ad)) && (aa != ac)) {
                return true;
            }
        }
        else if (nr == 3) {
            if (((aa == ab) && (ab == ac)) && (aa != ad)) {
                return true;
            }
        }
        else if (nr == 4) {
            if (((aa == ab) && (ac == ad)) && (aa != ac)) {
                return true;
            }
        }
        else if (nr == 5) {
            if (((aa == ac) && (ab == ad)) && (aa != ab)) {
                return true;
            }
        }
        else if (nr == 6) {
            if (((aa == ad) && (ab == ac)) && (aa != ab)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void incTaxId() {
        this.aSide.incTaxId();
        this.bSide.incTaxId();
    }


    /**
     * Check to see if this split is consistent with the given ordering, hence is circular.
     *
     * Note, that this only checks if this split is consistent with the given circular ordering.  It is still possible
     * for this method to return false but the split to be part of a circular split system that has a different ordering.
     *
     * @param ordering The ordering of taxa to test this split against
     * @return True, if this split is circular, false if not.
     */
    @Override
    public boolean isCircular(IdentifierList ordering) {

        if (ordering.size() != this.getNbTaxa()) {
            throw new IllegalArgumentException("This split represents a different number of taxa (" + this.getNbTaxa() + ") to the circular ordering provided (" + ordering.size() + ")");
        }

        // Just check the A side.  Id A-side is contiguous then so is B side.
        return this.aSide.isContiguousWithOrdering(ordering);
    }
}
