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
package uk.ac.uea.cmp.phygen.core.math.tuple;

/**
 * Triplet of values class
 * <p/>
 * Derives from the VisRD Triplet class
 */
public class Triplet<T extends Number> {

    /**
     * Data
     */
    private T a, b, c;


    public Triplet() {
        this.a = null;
        this.b = null;
        this.c = null;
    }

    /**
     * Constructor
     */
    public Triplet(T newA, T newB, T newC) {

        a = newA;
        b = newB;
        c = newC;
    }

    /**
     * a accessor
     */
    public T getA() {
        return a;
    }

    /**
     * b accessor
     */
    public T getB() {
        return b;
    }

    /**
     * c accessor
     */
    public T getC() {
        return c;
    }

    /**
     * a mutator
     */
    public void setA(T newA) {
        a = newA;
    }

    /**
     * b mutator
     */
    public void setB(T newB) {
        b = newB;
    }

    /**
     * c mutator
     */
    public void setC(T newC) {
        c = newC;
    }

    public T get(int position) {

        if (position == 1) {
            return a;
        }

        if (position == 2) {
            return b;
        }

        if (position == 3) {
            return c;
        }

        return null;
    }

    public void set(int position, T newN) {

        if (position == 1) {
            a = newN;
        }

        if (position == 2) {
            b = newN;
        }

        if (position == 3) {
            c = newN;
        }
    }

}