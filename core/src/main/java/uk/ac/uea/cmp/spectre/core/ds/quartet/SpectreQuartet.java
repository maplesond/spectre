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
package uk.ac.uea.cmp.spectre.core.ds.quartet;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Objects of this class are used as keys in the hashmap that associates sorts 4-tuples of taxa with the corresponding
 * weights of the three quartets.  Assumes the quartets are sorted when created.  If not, the sort method is available
 * to sort the indices.
 * <p/>
 * A quartet is always stored in its canonical form: the left pair must contain the smallest taxa id of the quadruple
 */
public class SpectreQuartet implements Quartet {

    protected int a;
    protected int b;
    protected int c;
    protected int d;


    public SpectreQuartet(int a, int b, int c, int d) {

        this.setCanonically(a, b, c, d);
    }

    public SpectreQuartet(int[] elements) {

        if (elements.length != 4)
            throw new IllegalArgumentException("Array must be of length 4");

        this.setCanonically(elements[0], elements[1], elements[2], elements[3]);
    }

    /**
     * Copy constructor.
     * <p/>
     * Note: We do not reuse the other constructor here as we already know that the other quartet must be distinct and
     * in canonical form.  Therefore we can save some time here by just directly copying the variables.
     *
     * @param q
     */
    public SpectreQuartet(Quartet q) {

        this.a = q.getA();
        this.b = q.getB();
        this.c = q.getC();
        this.d = q.getD();
    }

    /**
     * This method is critical for setting up quartets.  It converts the taxa ids into their quartet canonical form.  This
     * means that the first taxa id should be the minimum for the quadruple, whilst maintaining the input pairings.
     *
     * @param a
     * @param b
     * @param c
     * @param d
     */
    protected final void setCanonically(int a, int b, int c, int d) {

        if (!QuartetUtils.areDistinct(a, b, c, d)) {
            throw new IllegalArgumentException("Each taxa id must be distinct");
        }

        // Sort each pair
        int u = a < b ? a : b;
        int v = a < b ? b : a;
        int x = c < d ? c : d;
        int y = c < d ? d : c;

        // Sort pairs
        this.a = u < x ? u : x;
        this.b = u < x ? v : y;
        this.c = u < x ? x : u;
        this.d = u < x ? y : v;
    }

    @Override
    public int getA() {
        return a;
    }

    @Override
    public int getB() {
        return b;
    }

    @Override
    public int getC() {
        return c;
    }

    @Override
    public int getD() {
        return d;
    }


    public boolean areDistinct() {
        return QuartetUtils.areDistinct(a, b, c, d);
    }

    public void sort() {

        int[] array = new int[]{a, b, c, d};

        Arrays.sort(array);

        this.a = array[0];
        this.b = array[1];
        this.c = array[2];
        this.d = array[3];
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }

        Quartet tmp = (Quartet) obj;
        return this.equals(tmp);
    }

    public boolean equals(Quartet other) {

        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }

        return ((other.getA() == this.a) && (other.getB() == this.b) && (other.getC() == this.c) && (other.getD() == this.d));
    }

    @Override
    public int hashCode() {
        return 151 * (151 * ((151 * a) + b) + c) + d;
    }

    @Override
    public int compareTo(Quartet q) {

        //find smallest element where keys disagree
        int x1 = -1;
        int x2 = -1;

        if (this.a != q.getA()) {
            x1 = this.a;
            x2 = q.getA();
        } else {
            if (this.b != q.getB()) {
                x1 = this.b;
                x2 = q.getB();
            } else {
                if (this.c != q.getC()) {
                    x1 = this.c;
                    x2 = q.getC();
                } else {
                    x1 = this.d;
                    x2 = q.getD();
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


    /**
     * Using the indices in the quartet it is possible to work out where in a list of quartets in a quartet system that
     * this particular quartet should be placed
     *
     * @return The index to use for this quartet.
     */
    public int getIndex() {
        return QuartetUtils.getIndex(a, b, c, d);
    }



    @Override
    public String toString() {
        return "quartet: " + a + " " + b + " " + c + " " + d;
    }

    @Override
    public String toString(NumberFormat quartetFormat) {
        return "quartet: " + quartetFormat.format(a) + " " + quartetFormat.format(b) + " " + quartetFormat.format(c) + " "
                + quartetFormat.format(d);
    }

    @Override
    public Quartet createSortedCopy() {

        SpectreQuartet newQuartet = new SpectreQuartet(this);

        newQuartet.sort();

        return newQuartet;
    }

    public int getGroupIndex() {

        Quartet sorted = this.createSortedCopy();

        if (this.equals(sorted)) {
            return 0;
        }

        int[] array = new int[]{a, b, c, d};

        List<Integer> list = Arrays.asList(ArrayUtils.toObject(array));

        return b == Collections.max(list) ? 2 : 1;
    }

    public Pair<Quartet, Integer> getGroupKeys() {

        Quartet sorted = this.createSortedCopy();

        int index = 0;

        if (this.equals(sorted)) {
            index = 0;
        } else {

            int[] array = new int[]{a, b, c, d};

            List<Integer> list = Arrays.asList(ArrayUtils.toObject(array));

            index = b == Collections.max(list) ? 2 : 1;
        }

        return new ImmutablePair<>(sorted, index);
    }

    @Override
    public int[] toIntArray() {
        return new int[]{a, b, c, d};
    }
}