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
package uk.ac.uea.cmp.phygen.core.math.optimise;


public class Problem {
    
    private double[] restriction;
    private double[][] matrix;

    public Problem() {
        this(new double[0], new double[0][0]);
    }

    public Problem(double[] restriction, double[][] matrix) {
        this.restriction = restriction;
        this.matrix = matrix;
    }

    public double[] getRestriction() {
        return restriction;
    }

    public void setRestriction(double[] restriction) {
        this.restriction = restriction;
    }

    public double[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(double[][] matrix) {
        this.matrix = matrix;
    }
    
    public int getMatrixRows() {
        return this.matrix.length;
    }
    
    public int getMatrixColumns() {
        return this.matrix.length == 0 ? 0 : this.matrix[0].length;
    }
    
    public double getMatrixElement(int i, int j) {
        return this.matrix[i][j];
    }
}
