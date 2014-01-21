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

package uk.ac.uea.cmp.phybre.flatnj.fdraw;


import java.util.Comparator;

//This class is used to store the weights
//of those splits that have negative length
public class Split implements Comparator {

    public double weight = 0.0;
    public int ntaxa = 0;
    public int index = 0;
    public int[] s = null;

    public Split(double w, int n, int idx) {
        int i = 0;

        weight = w;
        ntaxa = n;
        index = idx;

        s = new int[ntaxa];
    }

    public Split() {
    }

    @Override
    public int compare(Object obj1, Object obj2) throws ClassCastException {
        Split s1 = (Split) obj1;
        Split s2 = (Split) obj2;

        //use lexicographic order
        for (int i = 0; i < s1.ntaxa; i++) {
            if (s1.s[i] < s2.s[i]) {
                return -1;
            }
            if (s1.s[i] > s2.s[i]) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return compare(this, obj) == 0;
    }

    public void print_split() {
        int i = 0;

        for (i = 0; i < ntaxa; i++) {
            System.out.print(s[i] + " ");
        }
        System.out.println("");
    }
}