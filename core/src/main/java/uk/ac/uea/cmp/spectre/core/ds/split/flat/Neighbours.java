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

package uk.ac.uea.cmp.spectre.core.ds.split.flat;

/**
 * Class for storing pair of taxa ('a' and 'b') that has been chosen as neighbors.
 * 'a' is the smaller taxon.
 */
public class Neighbours {

    private int a;
    private int b;

    /**
     * Constructor without parameters.
     */
    public Neighbours() {
        this(0, 0);
    }

    /**
     * Constructor.
     *
     * @param inA index of the first neighbor.
     * @param inB index of the second neighbor.
     */
    public Neighbours(int inA, int inB) {
        setAB(inA, inB);
    }

    /**
     * @return neighbor with smallest index.
     */
    public int getA() {
        return a;
    }

    /**
     * @return neighbor with biggest index.
     */
    public int getB() {
        return b;
    }

    /**
     * Sets neighbors.
     *
     * @param inA index of the first neighbor.
     * @param inB index of the second neighbor.
     */
    public void setAB(int inA, int inB) {
        a = inA < inB ? inA : inB;
        b = inA < inB ? inB : inA;
    }

    /**
     * Prints indexes of the neighbors to the screen.
     */
    @Override
    public String toString() {
        return "Neighbors are: " + a + " " + b;
    }


}