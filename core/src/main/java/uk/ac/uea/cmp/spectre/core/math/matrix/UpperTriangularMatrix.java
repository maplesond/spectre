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
package uk.ac.uea.cmp.spectre.core.math.matrix;

import uk.ac.uea.cmp.spectre.core.ds.quartet.Quartet;

/**
 * Created by IntelliJ IDEA. User: Analysis Date: 2004-jul-11 Time: 19:08:50 To
 * change this template use Options | File Templates.
 */
public class UpperTriangularMatrix {

    private int size;
    private double[] diagonal;
    private double[] triangle;

    public UpperTriangularMatrix(int size) {

        this.size = size;
        final int triangleSize = Quartet.over2(size) + Quartet.over1(size);

        this.diagonal = new double[size];
        this.triangle = new double[triangleSize];

        for (int n = 0; n < size; n++) {
            diagonal[n] = 0.0;
        }

        for (int n = 0; n < triangleSize; n++) {
            triangle[n] = 0.0;
        }
    }

    public void setElementAt(int i, int j, double newW) {

        if (i > j) {
            // Do nothing
        } else if (j > i) {
            triangle[Quartet.over2(j) + Quartet.over1(i)] = newW;
        } else {
            diagonal[i] = newW;
        }
    }

    public double getElementAt(int i, int j) {

        return i > j ?
                0.0 :
                j > i ?
                        triangle[Quartet.over2(j) + Quartet.over1(i)] :
                        diagonal[i];
    }

}
