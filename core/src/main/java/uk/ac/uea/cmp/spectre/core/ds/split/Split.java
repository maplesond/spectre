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

import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;

/**
 * Created by maplesod on 14/05/14.
 */
public interface Split extends Comparable<Split> {

    /**
     * A helpful enum for getting elements from a given side of the split
     */
    enum SplitSide {

        A_SIDE {
            public int getSplitElement(Split split, int index) {
                return split.getASide().get(index);
            }
        },
        B_SIDE {
            public int getSplitElement(Split split, int index) {
                return split.getBSide().get(index);
            }
        };

        public abstract int getSplitElement(Split split, int index);
    }

    /**
     * Create a new (deep) copy of this split.
     * @return A new copy of this split.
     */
    Split copy();


    /**
     * Get the weight given by this split
     * @return The split weight
     */
    double getWeight();

    /**
     * Sets the weight of this split.
     * @param weight Weight
     */
    void setWeight(double weight);

    /**
     * Returns True if this split is active, False otherwise.
     * @return True if this split is active, False otherwise.
     */
    boolean isActive();

    /**
     * Used to toggle whether or not this split is active
     * @param active
     */
    void setActive(boolean active);


    /**
     * Get the number of taxa in this split.  This should be the sum of the number of taxa on both the A side and B side
     * of the split.
     * @return The number of taxa represented by this split.
     */
    int getNbTaxa();

    /**
     * Retrieves the first taxon id stored on the A side
     * @return The first taxon id stored on the A side
     */
    int getASideFirst();

    /**
     * Retrieves the last taxon id stored on the A side
     * @return The last taxon id stored on the A side
     */
    int getASideLast();

    /**
     * Get the side of the A side of this split
     * @return A side size
     */
    int getASideSize();

    /**
     * Get the side of the B side of this split
     * @return B side size
     */
    int getBSideSize();

    /**
     * Retreives the A side as a SplitBlock
     * @return The A side of the split
     */
    SplitBlock getASide();

    /**
     * Retreives the B side as a SplitBlock.
     * @return The B side of the split
     */
    SplitBlock getBSide();

    SplitSide getSide(int taxonId);

    /**
     * Gets an array of integers representing the taxa indicies on the split's A side.
     * @return A Side as int[].
     */
    int[] getASideAsIntArray();

    /**
     * Gets an array of integers representing the taxa indicies on the split's A side.
     * @return B Side as int[].
     */
    int[] getBSideAsIntArray();


    /**
     * Combines the taxa on the a side of this and the given split.  Then should rebuild the b side according to the remaining
     * taxa.
     * @param split The split to merge with this split
     */
    void mergeASides(Split split);

    /**
     * Returns a canonical version of this split.  This means that the a-side is always smaller than or equal to the size
     * of the b-side, and taxa are sorted in both sides.
     * @return
     */
    Split makeCanonical();

    /**
     * Returns true if one side of this split contains only a single taxon.
     * @return True if one side of this split contains only a single taxon, otherwise false.
     */
    boolean isTrivial();

    /**
     * Returns the index of the trivial split if this is a trivial split, otherwise returns null.
     * @return Null if this is not a trivial split, otherwise the index of the taxon that's on the side with 1 element.
     */
    Integer getTrivial();

    /**
     * Check to see if this split is consistent with the given ordering, hence is circular.
     *
     * Note, that this only checks if this split is consistent with the given circular ordering.  It is still possible
     * for this method to return false but the split to be part of a circular split system that has a different ordering.
     *
     * @param ordering The ordering of taxa to test this split against
     * @return True, if this split is circular, false if not.
     */
    boolean isCircular(IdentifierList ordering);

    /**
     * Check to see if this split is compatible with another split.  This returns true (compatible) if one of the four
     * intersections A1 n A2, A1 n B2, A2 n B1 or A2 n B2 is empty.  Otherwise this returns false (incompatible).
     * @param other The other split to test
     * @return True if compatible, false if incompatible
     */
    boolean isCompatible(Split other);

    Compatible getCompatible(Split other);

    /**
     * Possible patterns of compatibility between splits
     */
    enum Compatible {
        NO {
            @Override
            public boolean isCompatible() {
                return false;
            }
        },
        YES_11 {
            @Override
            public boolean isCompatible() {
                return true;
            }
        },
        YES_10 {
            @Override
            public boolean isCompatible() {
                return true;
            }
        },
        YES_01 {
            @Override
            public boolean isCompatible() {
                return true;
            }
        },
        YES_00 {
            @Override
            public boolean isCompatible() {
                return true;
            }
        };

        public abstract boolean isCompatible();
    }

    /**
     * This method checks whether the quadruple split number nr is in the restriction of this split to {a,b,c,d}.
     * It is assumed that a, b, c and d are pairwise distinct. The possible values for nr are:
     * 0 --> a|bcd
     * 1 --> b|acd
     * 2 --> c|abd
     * 3 --> d|abc
     * 4 --> ab|cd
     * 5 --> ac|bd
     * 6 --> ad|bc
     * @param a Taxon a
     * @param b Taxon b
     * @param c Taxon c
     * @param d Taxon d
     * @param nr
     * @return True if restriction exists, false otherwise
     */
    boolean restrictionExists(int a, int b, int c, int d, int nr);

    void incTaxId();
}
