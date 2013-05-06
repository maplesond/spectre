/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
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
package uk.ac.uea.cmp.phygen.core.ds.quartet;

import uk.ac.uea.cmp.phygen.core.math.tuple.Key;

import java.util.Comparator;

public class QuartetComparator implements Comparator {

    public int compare(Object obj1, Object obj2)
            throws ClassCastException {
        //we know it can only be keys
        Key k1 = (Key) obj1;
        Key k2 = (Key) obj2;

        //find smallest element where keys disagree
        int x1 = -1;
        int x2 = -1;

        if (k1.t1 != k2.t1) {
            x1 = k1.t1;
            x2 = k2.t1;
        } else {
            if (k1.t2 != k2.t2) {
                x1 = k1.t2;
                x2 = k2.t2;
            } else {
                if (k1.t3 != k2.t3) {
                    x1 = k1.t3;
                    x2 = k2.t3;
                } else {
                    x1 = k1.t4;
                    x2 = k2.t4;
                }
            }
        }

        //compare x1 and x2
        if (x1 < x2) {
            return -1;
        } else if (x1 > x2) {
            return 1;
        } else {
            return 0;
        }
    }
}