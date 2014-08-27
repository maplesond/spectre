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

package uk.ac.uea.cmp.spectre.core.ds.network.draw;

import java.util.Comparator;

//This class is used to store the weights
//of those splits that have negative length

public class SplitWeight
        implements Comparator {
    public double weight = 0.0;
    public int index = 0;

    public SplitWeight(double w, int i) {
        weight = w;
        index = i;
    }

    public int compare(Object obj1, Object obj2)
            throws ClassCastException {
        SplitWeight sw1 = (SplitWeight) obj1;
        SplitWeight sw2 = (SplitWeight) obj2;

        //compare weights
        if (sw1.weight < sw2.weight) {
            return -1;
        } else if (sw1.weight > sw2.weight) {
            return 1;
        }
        //weights are the same, compare indices
        if (sw1.index < sw2.index) {
            return -1;
        } else if (sw1.index < sw2.index) {
            return 1;
        } else {
            return 0;
        }
    }

    public boolean equals(Object obj) {
        return compare(this, obj) == 0;
    }
}