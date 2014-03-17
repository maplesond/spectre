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
package uk.ac.uea.cmp.spectre.core.math.tuple;

/**
 * Objects of this class are used as keys in the hashmap that associates sorts
 * 4-tuples of taxa with the corresponding weights of the three quartets
 */
public class Key {
    //indices of the four taxa
    //increasingly sorted

    public int t1;
    public int t2;
    public int t3;
    public int t4;

    public Key(int t1, int t2, int t3, int t4) {
        //elements must be sorted increasingly first
        int[] sorted = sortElements(t1, t2, t3, t4);
        this.t1 = sorted[0];
        this.t2 = sorted[1];
        this.t3 = sorted[2];
        this.t4 = sorted[3];
    }

    public static int[] sortElements(int t1, int t2, int t3, int t4) {
        //create array to store the elements
        int[] a = {t1, t2, t3, t4};

        //loop variables
        int i = 0;
        int j = 0;

        //auxiliary variable used to swap
        int tmp = 0;

        //sort array
        for (i = 1; i < a.length; i++) {
            for (j = i - 1; j >= 0; j--) {
                if (a[j] > a[j + 1]) {
                    tmp = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = tmp;
                } else {
                    break;
                }
            }
        }
        return a;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        //know it will be a Key
        Key tmp = (Key) obj;
        if ((tmp.t1 == this.t1) && (tmp.t2 == this.t2) && (tmp.t3 == this.t3) && (tmp.t4 == this.t4)) {
            return true;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return 151 * (151 * ((151 * t1) + t2) + t3) + t4;
    }
}