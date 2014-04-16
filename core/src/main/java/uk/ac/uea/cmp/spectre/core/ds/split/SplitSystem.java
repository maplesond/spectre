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
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 18/11/13
 * Time: 13:11
 * To change this template use File | Settings | File Templates.
 */
public interface SplitSystem {


    // **** Taxa methods ****

    /**
     * Gets the number of taxa associated with this split system
     *
     * @return The number of taxa
     */
    int getNbTaxa();

    /**
     * Gets the actual taxa object stored in this split system
     *
     * @return The taxa
     */
    IdentifierList getOrderedTaxa();


    // **** Standard split methods ****

    /**
     * Gets the number of splits in this split system
     *
     * @return The number of splits
     */
    int getNbSplits();

    /**
     * Gets the splits in this split system
     *
     * @return The splits
     */
    List<Split> getSplits();

    /**
     * Get the split at the specified index
     *
     * @param i The index
     * @return The split at the given index
     */
    Split getSplitAt(final int i);

    /**
     * Adds a split to the end of current list of splits managed by this split system
     *
     * @param split The split to add
     */
    void addSplit(Split split);

    /**
     * Removes the last split in the split system
     */
    void removeLastSplit();

    /**
     * Merges the splits at the given indexes.  After merging Split i will contain the contents of both split i and split
     * j before merging.  This merged split is returned.
     *
     * @param i The split to merge
     * @param j The split which will be added to the split at i, and then deleted.
     * @return The merged split.
     */
    Split mergeSplits(final int i, final int j);

    /**
     * Whether or not this splitsystem contains the specified split.
     * @param s Split to test
     * @return True if this split system contains the split, otherwise false.
     */
    boolean contains(Split s);

    /**
     * Whether or not this splitsystem contains the specified splitblock.
     * @param sb Splitblock to test
     * @return True if this split system contains the splitblock, otherwise false.
     */
    boolean contains(SplitBlock sb);

    // **** Methods related to split weights ****

    /**
     * Returns true if this split system is weighted.
     *
     * @return True if this split system is weighted, false otherwise.
     */
    boolean isWeighted();

    /**
     * Gets the weight of the split at the given index.
     *
     * @param i The index of the split weight
     * @return The weight of the split
     */
    double getWeightAt(final int i);

    /**
     * Filters this split system based on the given threshold.  Splits with weighting less than the threshold will be
     * discarded.
     *
     * @param threshold The threshold to filter by
     * @return The filtered split system
     */
    SplitSystem filterByWeight(double threshold);



    // **** Interrogation methods that try to detect split system properties ****

    /**
     * Whether or not this split system is a circular split system
     *
     * @return True if this is a circular split system, false otherwise.
     */
    boolean isCircular();


    /**
     * Whether or not this split system is a compatible split system
     *
     * @return True if this is a compatible split system, false otherwise
     */
    boolean isCompatible();
}
