/**
 * Super Q - Computing super networks from partial trees. Copyright (C) 2012 UEA
 * CMP Phylogenetics Group.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.uea.cmp.phygen.core.ds;

import java.util.ArrayList;

/**
 *
 * TaxonList class
 *
 * Basically an ArrayList which can be joined and inverted
 *
 */
public class TaxonList extends ArrayList {

    /**
     *
     * Constructor
     *
     */
    public TaxonList() {

        super();

    }

    /**
     *
     * Seeded constructor
     *
     */
    public TaxonList(int aTaxon) {

        super();

        /**
         *
         * Add as single element aTaxon
         *
         */
        add(new Integer(aTaxon));

    }

    /**
     *
     * Invert method. Places everything in opposite order...
     *
     */
    public TaxonList invert() {

        /**
         *
         * Returns the inversion...
         *
         */
        TaxonList result = new TaxonList();

        for (int a = 0; a < size(); a++) {

            result.add(get(size() - 1 - a));

        }

        return result;

    }

    /**
     *
     * Join method. Joins in that order the two lists, in the specified
     * orientation
     *
     */
    public static TaxonList join(TaxonList theFirst, int firstDirection,
                                 TaxonList theSecond, int secondDirection) {

        /**
         *
         * Join, invert and the like...
         *
         * return the result...
         *
         */
        TaxonList result = new TaxonList();

        for (int a = 0; a < theFirst.size() + theSecond.size(); a++) {

            result.add(new Integer(0));

        }

        /**
         *
         * Forward direction of first
         *
         */
        if (firstDirection == 0) {

            for (int a = 0; a < theFirst.size(); a++) {

                result.set(a, theFirst.get(a));

            }

        } /**
         *
         * Backward direction of first
         *
         */
        else if (firstDirection == 1) {

            TaxonList inverse = theFirst.invert();

            for (int a = 0; a < theFirst.size(); a++) {

                result.set(a, inverse.get(a));

            }

        }

        int offset = theFirst.size();

        /**
         *
         * Forward direction of second
         *
         */
        if (secondDirection == 0) {

            for (int a = 0; a < theSecond.size(); a++) {

                result.set(offset + a, theSecond.get(a));

            }

        } /**
         *
         * Backward direction of first
         *
         */
        else if (secondDirection == 1) {

            TaxonList inverse = theSecond.invert();

            for (int a = 0; a < theSecond.size(); a++) {

                result.set(offset + a, inverse.get(a));

            }

        }

        return result;

    }

    /**
     *
     * INCLUSIVE sublist
     *
     */
    public TaxonList sublist(int I, int J) {

        TaxonList result = new TaxonList();

        for (int i = I; i <= J; i++) {

            result.add(get(i));

        }

        return result;

    }

    /**
     *
     * EXCLUSIVE sublist-complement, reverse-order (so front-front, back-back)
     *
     */
    public TaxonList complement(int I, int J) {

        TaxonList result = new TaxonList();

        for (int i = I - 1; i >= 0; i--) {

            result.add(get(i));

        }

        for (int i = size() - 1; i > J; i--) {

            result.add(get(i));

        }

        return result;

    }

    /**
     *
     * Assume integer listings, return true if element is contained
     *
     */
    public boolean contains(int I) {

        for (int i = 0; i < size(); i++) {

            if (((Integer) get(i)).intValue() == I) {

                return true;

            }

        }

        return false;

    }

    public int left() {

        return ((Integer) get(0)).intValue();

    }

    public int right() {

        return ((Integer) get(size() - 1)).intValue();

    }
}