/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2015  UEA School of Computing Sciences
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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by dan on 21/01/14.
 */
public class SplitDistanceMap extends LinkedHashMap<Split, Double> {

    public SplitDistanceMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public SplitDistanceMap(int initialCapacity) {
        super(initialCapacity);
    }

    public SplitDistanceMap() {
        super();
    }

    public SplitDistanceMap(Map<? extends Split, ? extends Double> m) {
        super(m);
    }

    /**
     * Get the weight of the split that has the associated split block.
     * Note: This will run in linear time.
     *
     * @param splitBlock The split block to find amongst the splits in this map
     * @return The weight of the split associated with the split block, or null if the split was not found.
     */
    public Double getUsingSplitBlock(SplitBlock splitBlock) {

        for (Split split : this.keySet()) {
            if (split.getASide().equals(splitBlock) || split.getBSide().equals(splitBlock)) {
                return this.get(split);
            }
        }

        return null;
    }
}
