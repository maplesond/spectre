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

package uk.ac.uea.cmp.spectre.core.ds.quad;

import org.apache.commons.lang3.tuple.Pair;

import java.text.NumberFormat;

/**
 * Created by maplesod on 15/05/14.
 */
public interface Quad extends Comparable<Quad> {

    /**
     * Creates a new copy of this quartet that has already been sorted
     * @return A sorted copy of this quartet
     */
    Quad createSortedCopy();

    int getA();

    int getB();

    int getC();

    int getD();

    Pair<Quad, Integer> getGroupKeys();

    /**
     * This method checks whether or not for given taxa indices are distinct.
     * @return True if this quartet contains all distinct elements, false otherwise.
     */
    boolean areDistinct();

    /**
     * Returns the taxa indices represented by this quartet as an int array.
     * @return An int array representing this quartet
     */
    int[] toIntArray();

    /**
     * Returns a string representation of this quartet using the specified number format.
     * @param quartetFormat The NumberFormat to represent the taxa indicies represented by this quartet
     * @return a string representation of this quartet
     */
    String toString(NumberFormat quartetFormat);
}
