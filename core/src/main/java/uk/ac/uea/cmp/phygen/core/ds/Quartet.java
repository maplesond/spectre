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
package uk.ac.uea.cmp.phygen.core.ds;

import java.util.Arrays;

/**
 * Objects of this class are used as keys in the hashmap that associates sorts
 * 4-tuples of taxa with the corresponding weights of the three quartets
 */
public class Quartet implements Comparable {
    //indices of the four taxa
    //increasingly sorted

    private int t1;
    private int t2;
    private int t3;
    private int t4;

    public Quartet(int t1, int t2, int t3, int t4) {
        //elements must be sorted increasingly first
        int[] sorted = sortElements(t1, t2, t3, t4);
        this.t1 = sorted[0];
        this.t2 = sorted[1];
        this.t3 = sorted[2];
        this.t4 = sorted[3];
    }

    public int getT1() {
        return t1;
    }

    public int getT2() {
        return t2;
    }

    public int getT3() {
        return t3;
    }

    public int getT4() {
        return t4;
    }

    public static int[] sortElements(int t1, int t2, int t3, int t4) {
        //create array to store the elements
        int[] a = {t1, t2, t3, t4};

        Arrays.sort(a);

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
        Quartet tmp = (Quartet) obj;
        if ((tmp.t1 == this.t1) && (tmp.t2 == this.t2) && (tmp.t3 == this.t3) && (tmp.t4 == this.t4)) {
            return true;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return 151 * (151 * ((151 * t1) + t2) + t3) + t4;
    }

    @Override
    public int compareTo(Object o) {

        //we know it can only be keys
        Quartet k1 = (Quartet) this;
        Quartet k2 = (Quartet) o;

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