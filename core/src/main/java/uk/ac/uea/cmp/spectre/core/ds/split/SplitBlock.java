/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2014  UEA School of Computing Sciences
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
package uk.ac.uea.cmp.spectre.core.ds.split;

import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;

import java.util.List;

/**
 * @author Dan
 */
public interface SplitBlock extends List<Integer>, Comparable<SplitBlock> {

    public enum EdgeType {
        EXTERNAL,
        INTERNAL
    }

    /**
     * Whether or not this split block might represent an external of internal edge
     * @return EdgeType.EXTERNAL, or EdgeType.INTERNAL.
     */
    EdgeType getType();

    /**
     * Create a new (deep) copy of this split block.
     * @return A new copy of this split block.
     */
    SplitBlock copy();

    /**
     * Creates a new (deep) copy of this split block that has the taxa indicies sorted ascending numerically
     * @return A sorted copy of this split block.
     */
    SplitBlock makeSortedCopy();

    /**
     * Sorts the taxa indices in this split block into ascending numerical order
     */
    void sort();

    /**
     * Reverses the order of the taxa indices in this split block.
     */
    void reverse();

    /**
     * Merges the contents of the given split block with this split block.
     * @param splitBlock The split block to merge
     */
    void merge(SplitBlock splitBlock);

    /**
     * Get the first element in this split block
     * @return
     */
    int getFirst();

    /**
     * Get the last element in this split block
     * @return
     */
    int getLast();

    /**
     * Checks if this split block contains any elements from the given split block.
     * @param other The split block with elements to check
     * @return True if this split block contains any elements from the given split block, false otherwise.
     */
    boolean containsAny(SplitBlock other);

    /**
     * Checks to see if this split block is ordered in a way that is contiguous with the specified circular ordering.
     * @param ordering The circular ordering
     * @return True if this split block is contiguous with the given circular ordering, false otherwise.
     */
    boolean isContiguousWithOrdering(IdentifierList ordering);

    /**
     * Create a representation of this split block as an int array.
     * @return This split block as an int[]
     */
    int[] toIntArray();

    /**
     * Generates the complement of this split block assuming the taxa set has the given number of taxa
     * @param nbTaxa The number of taxa in the complete taxa set
     * @return The complement of this split block
     */
    SplitBlock makeComplement(int nbTaxa);

    /**
     * A string representation of this split block
     * @return A string representation of this split block
     */
    String toString();
}
