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
package uk.ac.uea.cmp.phygen.core.ds.split;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 01/05/13
 * Time: 21:26
 * To change this template use File | Settings | File Templates.
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

    public CircularOrdering invertOrdering() {

        int n = this.size();
        int[] permutationInvert = new int[n];

        for (int i = 0; i < n; i++) {
            permutationInvert[this.co[i]] = i;
        }

        return new CircularOrdering(permutationInvert);
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

        for (int i = 1; i < size; i++) {
            ordering[i] = i;
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

}
