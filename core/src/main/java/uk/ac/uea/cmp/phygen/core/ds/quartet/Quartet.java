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

import java.text.NumberFormat;
import java.util.Arrays;

/**
 * Objects of this class are used as keys in the hashmap that associates sorts 4-tuples of taxa with the corresponding
 * weights of the three quartets.  Assumes the quartets are sorted when created.  If not, the sort method is available
 * to sort the indices.
 */
public class Quartet implements Comparable {

    protected int a;
    protected int b;
    protected int c;
    protected int d;


    public Quartet(int a, int b, int c, int d) {

        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;

        if (!areDistinct()) {
            throw new IllegalArgumentException("Each taxa id must be distinct");
        }
    }

    public Quartet(int[] elements) {

        if (elements.length != 4)
            throw new IllegalArgumentException("Array must be of length 4");

        this.a = elements[0];
        this.b = elements[1];
        this.c = elements[2];
        this.d = elements[3];

        if (!areDistinct()) {
            throw new IllegalArgumentException("Each taxa id must be distinct");
        }
    }

    /**
     * Copy constructor.
     *
     * Note: We do not reuse the other constructor here as we already know that the other quartet must be distinct.  Therefore
     * we can save some time here by just directly copying the variables.
     * @param q
     */
    public Quartet(Quartet q) {

        this.a = q.a;
        this.b = q.b;
        this.c = q.c;
        this.d = q.d;
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    public int getC() {
        return c;
    }

    public int getD() {
        return d;
    }


    /**
     * This method checks whether or not for given taxa are distinct. The assumption is that a <= b <= c <= d holds.
     * @param a
     * @param b
     * @param c
     * @param d
     * @return True, if all taxa ids are distinct, false otherwise.
     */
    public static boolean areDistinct(int a, int b, int c, int d) {
        return !(a == b || b == c || c == d || a == c || b == d || a == d);
    }

    public boolean areDistinct() {

        return areDistinct(a, b, c, d);
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

        return ((other.a == this.a) && (other.b == this.b) && (other.c == this.c) && (other.d == this.d));
    }

    @Override
    public int hashCode() {
        return 151 * (151 * ((151 * a) + b) + c) + d;
    }

    @Override
    public int compareTo(Object o) {

        Quartet k2 = (Quartet) o;

        //find smallest element where keys disagree
        int x1 = -1;
        int x2 = -1;

        if (this.a != k2.a) {
            x1 = this.a;
            x2 = k2.a;
        } else {
            if (this.b != k2.b) {
                x1 = this.b;
                x2 = k2.b;
            } else {
                if (this.c != k2.c) {
                    x1 = this.c;
                    x2 = k2.c;
                } else {
                    x1 = this.d;
                    x2 = k2.d;
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
     * @return The index to use for this quartet.
     */
    public int getIndex() {
        return over4(a - 1) + over3(b - 1) + over2(c - 1) + over1(d - 1);
    }


    /**
     * Local overs
     */
    public static int over4(int n) {

        return n > 4 ?
                n * (n - 1) * (n - 2) * (n - 3) / 24 :
                n == 4 ? 1 : 0;
    }

    public static int over3(int n) {

        return n > 3 ?
                n * (n - 1) * (n - 2) / 6 :
                n == 3 ? 1 : 0;
    }

    public static int over2(int n) {

        return n > 2 ?
                n * (n - 1) / 2 :
                n == 2 ? 1 : 0;
    }

    public static int over1(int n) {

        return n > 0 ? n : 0;
    }


    @Override
    public String toString() {
        return "quartet: " + a + " " + b + " " + c + " " + d;
    }

    public String toString(NumberFormat quartetFormat) {
        return "quartet: " + quartetFormat.format(a) + " " + quartetFormat.format(b) + " " + quartetFormat.format(c) + " "
                + quartetFormat.format(d);
    }

    public Quartet createSortedQuartet() {

        Quartet newQuartet = new Quartet(this);

        newQuartet.sort();

        return newQuartet;
    }
}