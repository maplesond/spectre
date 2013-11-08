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

import java.util.Arrays;

/**
 * Objects of this class are used as keys in the hashmap that associates sorts 4-tuples of taxa with the corresponding
 * weights of the three quartets
 */
public class Quartet implements Comparable {

    //indices of the four taxa
    //increasingly sorted
    private int t1;
    private int t2;
    private int t3;
    private int t4;

    /**
     *
     * @param t1
     * @param t2
     * @param t3
     * @param t4
     */
    public Quartet(int t1, int t2, int t3, int t4) {

        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
    }

    public Quartet(int[] elements) {
        if (elements.length != 4)
            throw new IllegalArgumentException("Array must be of length 4");

        this.t1 = elements[0];
        this.t2 = elements[1];
        this.t3 = elements[2];
        this.t4 = elements[3];
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



    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }

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

        Quartet k2 = (Quartet) o;

        //find smallest element where keys disagree
        int x1 = -1;
        int x2 = -1;

        if (this.t1 != k2.t1) {
            x1 = this.t1;
            x2 = k2.t1;
        } else {
            if (this.t2 != k2.t2) {
                x1 = this.t2;
                x2 = k2.t2;
            } else {
                if (this.t3 != k2.t3) {
                    x1 = this.t3;
                    x2 = k2.t3;
                } else {
                    x1 = this.t4;
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

    public Quartet createSortedQuartet() {
        //create array to store the elements
        int[] a = {t1, t2, t3, t4};

        Arrays.sort(a);

        return new Quartet(a[0], a[1], a[2], a[3]);
    }

    /**
     * Using the indicies in the quartet it is possible to work out where in a list of quartets in a quartet system that
     * this particular quartet should be placed
     * @return
     */
    public int getIndex() {
        return over4(t1 - 1) + over3(t2 - 1) + over2(t3 - 1) + over1(t4 - 1);
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

    /**
     * Determine which quartet to take. Use the unordered numbers, they
     * match one ordering or other
     * @param other
     * @return
     */
    public double selectWeight(Quartet other, QuartetWeighting w) {
        
        double result = 0.0;

        if (((other.t1 == t1 || other.t1 == t2) && (other.t2 == t1 || other.t2 == t2)) ||
                ((other.t3 == t1 || other.t3 == t2) && (other.t4 == t1 || other.t4 == t2))) {

            result = w.getA();
        }
        else if (((other.t1 == t1 || other.t1 ==  t3) && (other.t2 == t1 || other.t2 ==  t3)) ||
                ((other.t3 == t1 || other.t3 ==  t3) && (other.t4 == t1 || other.t4 ==  t3))) {

            result = w.getB();
        }
        else if (((other.t1 == t1 || other.t1 ==  t4) && (other.t2 == t1 || other.t2 ==  t4)) ||
                ((other.t3 == t1 || other.t3 ==  t4) && (other.t4 == t1 || other.t4 ==  t4))) {

            result = w.getC();
        }
        
        return result;
    }

    public QuartetWeighting selectWeighting(Quartet other, QuartetWeighting w) {

        int x = t1, y = t2, u = t3, v = t4;
        int a = other.t1, b = other.t2, c = other.t3, d = other.t4;

        /**
         *
         * Investigate which topology of a, b, c, d that the topologies of x, y,
         * u, v correspond to, and set weights accordingly
         *
         */
        /**
         *
         * See if xy|uv is ab|cd (w1), ac|bd (w2), or ad|bc (w3)
         *
         */
        /**
         *
         * The first length of the stored triplet
         *
         */
        double r1 = 0;

        if (((x == a || x == b) && (y == a || y == b)) || ((u == a || u == b) && (v == a || v == b))) {

            r1 = w.getA();
        }
        else if (((x == a || x == c) && (y == a || y == c)) || ((u == a || u == c) && (v == a || v == c))) {

            r1 = w.getB();
        }
        else if (((x == a || x == d) && (y == a || y == d)) || ((u == a || u == d) && (v == a || v == d))) {

            r1 = w.getC();
        }

        /**
         *
         * See if xu|yv is ab|cd (w1), ac|bd (w2), or ad|bc (w3)
         *
         */
        /**
         *
         * The second length of the stored triplet
         *
         */
        double r2 = 0;

        if (((x == a || x == b) && (u == a || u == b)) || ((y == a || y == b) && (v == a || v == b))) {

            r2 = w.getA();
        }
        else if (((x == a || x == c) && (u == a || u == c)) || ((y == a || y == c) && (v == a || v == c))) {

            r2 = w.getB();
        }
        else if (((x == a || x == d) && (u == a || u == d)) || ((y == a || y == d) && (v == a || v == d))) {

            r2 = w.getC();
        }

        /**
         *
         * See if xv|uy is ab|cd (w1), ac|bd (w2), or ad|bc (w3)
         *
         */
        /**
         *
         * The third length of the stored triplet
         *
         */
        double r3 = 0;

        if (((x == a || x == b) && (v == a || v == b)) || ((u == a || u == b) && (y == a || y == b))) {

            r3 = w.getA();
        }
        else if (((x == a || x == c) && (v == a || v == c)) || ((u == a || u == c) && (y == a || y == c))) {

            r3 = w.getB();
        }
        else if (((x == a || x == d) && (v == a || v == d)) || ((u == a || u == d) && (y == a || y == d))) {

            r3 = w.getC();
        }

        return new QuartetWeighting(r1, r2, r3);
    }

    public void updateWeighting(Quartet other, QuartetWeighting w, double newW) {

        int x = t1, y = t2, u = t3, v = t4;
        int a = other.t1, b = other.t2, c = other.t3, d = other.t4;

        /**
         *
         * Investigate which topology of a, b, c, d that the topologies of x, y,
         * u, v correspond to, and set weights accordingly
         *
         */
        /**
         *
         * See if xy|uv is ab|cd (w1), ac|bd (w2), or ad|bc (w3)
         *
         */
        /**
         *
         * The first length of the stored triplet
         *
         */
        if (((x == a || x == b) && (y == a || y == b)) || ((u == a || u == b) && (v == a || v == b))) {

            w.setA(newW);
        }
        else if (((x == a || x == b) && (u == a || u == b)) || ((y == a || y == b) && (v == a || v == b))) {

            w.setB(newW);
        }
        else if (((x == a || x == b) && (v == a || v == b)) || ((u == a || u == b) && (y == a || y == b))) {

            w.setC(newW);
        }
    }

}