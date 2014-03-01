/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
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

package uk.ac.uea.cmp.phybre.core.ds.distance;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 16/11/13
 * Time: 00:23
 * To change this template use File | Settings | File Templates.
 */
public class DistanceMatrixBuilder {

    public static enum Labels {
        NONE,
        LEFT,
        RIGHT
    }

    public static enum Triangle {
        BOTH {
            @Override
            public void fillRow(int row, List<Double> elements, DistanceMatrix distanceMatrix) {
                for (int j = 1; j <= elements.size(); j++) {
                    distanceMatrix.setDistance(row, j, elements.get(j-1));
                }
            }

            @Override
            public double[] getRow(int row, DistanceMatrix distanceMatrix) {
                return distanceMatrix.getMatrix()[row-1];
            }
        },
        LOWER {
            @Override
            public void fillRow(int row, List<Double> elements, DistanceMatrix distanceMatrix) {
                for (int j = 1; j <= elements.size(); j++) {
                    distanceMatrix.setDistance(row, j, elements.get(j-1));
                    distanceMatrix.setDistance(j, row, elements.get(j-1));
                }
            }

            @Override
            public double[] getRow(int row, DistanceMatrix distanceMatrix) {
                return ArrayUtils.subarray(distanceMatrix.getMatrix()[row], 0, row + 1);
            }
        },
        UPPER {
            @Override
            public void fillRow(int row, List<Double> elements, DistanceMatrix distanceMatrix) {
                for (int j = 1; j <= elements.size(); j++) {
                    distanceMatrix.setDistance(row, j + row, elements.get(j-1));
                    distanceMatrix.setDistance(j + row, row, elements.get(j-1));
                }
            }

            @Override
            public double[] getRow(int row, DistanceMatrix distanceMatrix) {
                double[] data = distanceMatrix.getMatrix()[row];
                return ArrayUtils.subarray(data, row, data.length);
            }
        };


        public abstract void fillRow(int row, List<Double> elements, DistanceMatrix distanceMatrix);
        public abstract double[] getRow(int row, DistanceMatrix distanceMatrix);
    }

    private int nbTaxa;
    private int nbChars;
    private Triangle triangle;
    private boolean diagonal;
    private boolean interleave;
    private Labels labels;
    private List<List<Double>> rows;

    public DistanceMatrixBuilder() {
        this.nbTaxa = 0;
        this.nbChars = 0;
        this.triangle = null;
        this.diagonal = false;
        this.interleave = false;
        this.labels = Labels.NONE;
        this.rows = new ArrayList<>();
    }

    public DistanceMatrix createDistanceMatrix() {

        this.removeEmptyLines(this.rows);
        DistanceMatrix distanceMatrix = new FlexibleDistanceMatrix(this.rows.size());
        this.fillDistanceMatrix(this.rows, distanceMatrix);
        return distanceMatrix;
    }

    private void removeEmptyLines(List<List<Double>> rows) {
        for (int i = 0; i < rows.size(); i++) {

            if (rows.get(i).isEmpty()) {
                rows.remove(i--);
            }
        }

    }

    private Triangle fillDistanceMatrix(List<List<Double>> lines, DistanceMatrix distanceMatrix) {

        Triangle tf = Triangle.BOTH;

        for (int i = 0; i < lines.size(); i++) {

            List<Double> matrixLine = lines.get(i);

            // We have an lower triangular matrix
            if (i == 0 && matrixLine.size() != distanceMatrix.size()) {
                tf = Triangle.LOWER;
            }
            // Not sure yet, either BOTH or UPPER, assume both for now
            else if (i == 0 && matrixLine.size() == distanceMatrix.size()) {
                tf = Triangle.BOTH;
            }
            // Actually this is an upper triangular matrix
            else if (i == 1 && matrixLine.size() != distanceMatrix.size() && tf == Triangle.BOTH) {
                tf = Triangle.UPPER;
            }

            // We should have covered all the bases here and ensured tf is non-null;
            tf.fillRow(i+1, matrixLine, distanceMatrix);
        }

        // We didn't know it was upper triangular format before the second line so compute the first column now
        if (tf == Triangle.UPPER) {
            for (int i = 1; i < distanceMatrix.size(); i++) {
                distanceMatrix.setDistance(i, 0, distanceMatrix.getDistance(0, i));
            }
        }

        // Return triangle format type just for info
        return tf;
    }

    public int getNbTaxa() {
        return nbTaxa;
    }

    public void setNbTaxa(int nbTaxa) {
        this.nbTaxa = nbTaxa;
    }

    public int getNbChars() {
        return nbChars;
    }

    public void setNbChars(int nbChars) {
        this.nbChars = nbChars;
    }

    public Triangle getTriangle() {
        return triangle;
    }

    public void setTriangle(Triangle triangle) {
        this.triangle = triangle;
    }

    public boolean isDiagonal() {
        return diagonal;
    }

    public void setDiagonal(boolean diagonal) {
        this.diagonal = diagonal;
    }

    public boolean isInterleave() {
        return interleave;
    }

    public void setInterleave(boolean interleave) {
        this.interleave = interleave;
    }

    public Labels getLabels() {
        return labels;
    }

    public void setLabels(Labels labels) {
        this.labels = labels;
    }

    public List<List<Double>> getRows() {
        return rows;
    }

    public void setRows(List<List<Double>> rows) {
        this.rows = rows;
    }
}
