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
package uk.ac.uea.cmp.phybre.core.ds.split;

import uk.ac.uea.cmp.phybre.core.ds.Taxa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages a simple int array to represent a 1-based circular ordering of taxa in a split system.  Can use getIndexAt to
 * return a 0-based taxa index at a particular location in the array.
 * @author Dan
 */
public class CircularOrdering {

    private int[] co;

    public CircularOrdering(int[] circularOrdering) {
        this.co = circularOrdering;
    }

    public int size() {
        return this.co.length;
    }

    public int getAt(final int i) {
        return this.co[i];
    }

    public int getIndexAt(final int i) {
        return this.co[i] - 1;
    }

    public CircularOrdering invertOrdering() {

        int n = this.size();
        int[] permutationInvert = new int[n];

        for (int i = 0; i < n; i++) {
            permutationInvert[this.co[i] - 1] = i + 1;
        }

        return new CircularOrdering(permutationInvert);
    }

    @Override
    public boolean equals(Object o) {

        if (o == null)
            return false;

        CircularOrdering other = (CircularOrdering)o;

        if (this.size() != other.size())
            return false;

        if (this.size() == 0)
            return true;

        int idx = other.indexOf(this.co[0]);

        if (idx == -1)
            return false;

        for(int i = 0; i < this.size(); i++) {
            if (this.co[i] != other.getAt(idx++))
                return false;
        }

        return true;
    }


    public int indexOf(int val) {

        for(int i = 0; i < this.size(); i++) {
            if (co[i] == val)
                return i;
        }

        return -1;
    }

    public CircularOrdering copy() {
        int[] copy = this.co.clone();
        return new CircularOrdering(copy);
    }

    /**
     * Creates a trivial circular ordering object.. e.g. 1, 2, 3, 4, ..., [size]
     *
     * @param size The size of the circular order object to create.
     * @return The trivial circular ordering object
     */
    public static CircularOrdering createTrivialOrdering(final int size) {

        int[] ordering = new int[size];

        for (int i = 0; i < size; i++) {
            ordering[i] = i + 1;
        }

        return new CircularOrdering(ordering);
    }

    /**
     * Creates a random circular ordering object of specified size
     *
     * @param size The size of the circular order object to create
     * @return The randomised circular ordering object
     */
    public static CircularOrdering createRandomCircularOrdering(final int size) {

        List<Integer> random = new ArrayList<Integer>();

        for (int i = 0; i < size; i++) {
            random.add(i);
        }

        Collections.shuffle(random);
        int[] circOrdering = new int[size];
        int k = 0;
        for (Integer n : random) {
            circOrdering[k] = n.intValue();
            k++;
        }

        return new CircularOrdering(circOrdering);
    }

    public static CircularOrdering createFromList(List<Integer> circularOrdering) {
        int[] co = new int[circularOrdering.size()];
        int i = 0;
        for(Integer coi : circularOrdering) {
            co[i++] = coi;
        }

        return new CircularOrdering(co);
    }

    @Override
    public String toString() {

        if (this.co == null || this.co.length == 0) {
            return "[]";
        }
        else if (this.co.length == 1) {
            return "[ " + Integer.toString(this.co[0]) + " ]";
        }
        else {

            StringBuilder sb = new StringBuilder();

            sb.append("[ ").append(this.co[0]);

            for(int i = 1; i < this.co.length; i++) {
               sb.append(", ").append(this.co[i]);
            }

            sb.append(" ]");

            return sb.toString();
        }
    }

    public String toString(Taxa taxa) {

        if (this.co == null || this.co.length == 0) {
            return "[]";
        }
        else if (this.co.length == 1) {
            return "[ " + taxa.getById(this.co[0]).getName() + " ]";
        }
        else {
            StringBuilder sb = new StringBuilder();

            sb.append("[ ").append(taxa.getById(this.co[0]).getName());

            for(int i = 1; i < this.co.length; i++) {
                sb.append(", ").append(taxa.getById(this.co[i]).getName());
            }

            sb.append(" ]");

            return sb.toString();
        }
    }

    public int[] toArray() {
        return this.co;
    }
}
