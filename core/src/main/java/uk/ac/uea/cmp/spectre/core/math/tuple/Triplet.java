/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2017  UEA School of Computing Sciences
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package uk.ac.uea.cmp.spectre.core.math.tuple;

/**
 * Triplet of values class
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
     * @param newA New value for A
     * @param newB New value for B
     * @param newC New value for C
     */
    public Triplet(T newA, T newB, T newC) {

        a = newA;
        b = newB;
        c = newC;
    }

    /**
     * a accessor
     * @return Value of A
     */
    public T getA() {
        return a;
    }

    /**
     * b accessor
     * @return Value of B
     */
    public T getB() {
        return b;
    }

    /**
     * c accessor
     * @return Value of C
     */
    public T getC() {
        return c;
    }

    /**
     * a mutator
     * @param newA New value for A
     */
    public void setA(T newA) {
        a = newA;
    }

    /**
     * b mutator
     * @param newB New value for B
     */
    public void setB(T newB) {
        b = newB;
    }

    /**
     * c mutator
     * @param newC New value for C
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