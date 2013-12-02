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

import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 27/04/13
 * Time: 19:08
 * To change this template use File | Settings | File Templates.
 */
public class CircularSplitSystem extends SimpleSplitSystem {

    private CircularOrdering circularOrdering;

    public CircularSplitSystem(Taxa taxa, List<Split> splits, CircularOrdering circularOrdering) {
        super(taxa, splits);
        this.circularOrdering = circularOrdering;
    }

    public CircularSplitSystem(DistanceMatrix distanceMatrix, CircularOrdering circularOrdering) {

        super(distanceMatrix.getTaxaSet(), new ArrayList<Split>());

        int n = circularOrdering.size();

        if (n != distanceMatrix.size()) {
            throw new IllegalArgumentException("Distance matrix and circular ordering are not the same size");
        }

        SplitWeights splitWeights = this.calculateSplitWeighting(distanceMatrix, circularOrdering);

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (splitWeights.hasWeightAt(j, i)) {

                    ArrayList<Integer> sb = new ArrayList<>();
                    for (int k = i + 1; k < j + 1; k++) {
                        sb.add(circularOrdering.getIndexAt(k));
                    }

                    this.addSplit(new Split(new SplitBlock(sb), n, splitWeights.getAt(j, i)));
                }
            }
        }

        this.circularOrdering = circularOrdering;
    }


    @Override
    public boolean isCircular() {
        return true;
    }

    public void setCircularOrdering(CircularOrdering circularOrdering) {
        this.circularOrdering = circularOrdering;
    }

    @Override
    public CircularOrdering getCircularOrdering() {
        return circularOrdering;
    }

    /**
     * Returns the value of the permutation at the specified position.
     *
     * @param i index of the value to be returned.
     * @return value of the permutation at i
     */
    @Override
    public int getTaxaIndexAt(final int i) {
        return this.circularOrdering.getAt(i);
    }


    /**
     * Generates a distance matrix based on the weights within this splitsystem.
     *
     * @return
     */
    /*public DistanceMatrix generateDistanceMatrix() {

        final int t = this.getNbTaxa();

        DistanceMatrix distanceMatrix = new DistanceMatrix(t);

        for (int i = 0; i < t; i++) {
            for (int j = i + 1; j < t; j++) {
                for (int k = j; k < t; k++) {
                    for (int s = i; s < j; s++) {
                        distanceMatrix.incrementDistance(i, j, this.splitWeights.getAt(k, s));
                    }
                }
                for (int k = i; k < j; k++) {
                    for (int s = 0; s < i; s++) {
                        distanceMatrix.incrementDistance(i, j, this.splitWeights.getAt(k, s));
                    }
                }
            }
        }

        return distanceMatrix;
    }*/


    /**
     * Calculates the weights of this full circular split system.  The length determines
     * which splits will be returned.
     *
     * @return Split weights matrix
     */
    protected SplitWeights calculateSplitWeighting(DistanceMatrix distanceMatrix, CircularOrdering circularOrdering) {
        int n = distanceMatrix.size();
        double[][] permutedDistances = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                permutedDistances[i][j] = distanceMatrix.getDistance(circularOrdering.getIndexAt(i), circularOrdering.getIndexAt(j));
            }
        }

        return new CircularNNLS().circularLeastSquares(permutedDistances, n);
    }


}
