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
package uk.ac.uea.cmp.phybre.core.ds.split;

import uk.ac.uea.cmp.phybre.core.ds.Identifier;
import uk.ac.uea.cmp.phybre.core.ds.IdentifierList;
import uk.ac.uea.cmp.phybre.core.ds.distance.DistanceMatrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A Compatible Split System can be represented by a tree
 */
public class CompatibleSplitSystem extends CircularSplitSystem {

    public CompatibleSplitSystem(List<Split> splits, DistanceMatrix distanceMatrix, IdentifierList sortedTaxa) {

        super(splits, sortedTaxa);

        if (sortedTaxa.size() != distanceMatrix.size()) {
            throw new IllegalArgumentException("Distance matrix and circular ordering are not the same size");
        }

        //this.setSplitWeights(this.calculateSplitWeighting(distanceMatrix, circularOrdering));

        reweight(this.calculateSplitWeighting(distanceMatrix, sortedTaxa));
    }

    public CompatibleSplitSystem(CircularSplitSystem splitSystem) {

        super(splitSystem.copySplits(), new IdentifierList(splitSystem.getCircularOrdering()));
    }

    public CompatibleSplitSystem(CircularSplitSystem unweightedSplitSystem, TreeSplitWeights treeWeights) {

        this(unweightedSplitSystem); //new Taxa(unweightedSplitSystem.getTaxa()), unweightedSplitSystem.copySplits(), unweightedSplitSystem.getCircularOrdering().copy());

        reweight(treeWeights);
    }

    /**
     * Deletes all splits and adds the splits to the list that have a positive length
     *
     * @param treeWeights
     */
    protected void reweight(SplitWeights treeWeights) {

        int n = this.getNbTaxa();
        this.getSplits().clear();

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (treeWeights.getAt(j, i) != 0.0) {

                    ArrayList<Integer> sb = new ArrayList<>();
                    for (int k = i + 1; k < j + 1; k++) {
                        sb.add(this.getCircularOrdering().get(k).getId());
                    }

                    this.addSplit(new Split(new SplitBlock(sb), n, treeWeights.getAt(j, i)));
                }
            }
        }
    }

    public double calculateTreeLength() {

        double sum = 0.0;
        for (Split s : this.getSplits()) {
            sum += s.getWeight();
        }
        return sum;
    }

    /**
     * Returns weightings for the edges of a specified tree.
     *
     * @return tree edge weightings
     */
    @Override
    public SplitWeights calculateSplitWeighting(DistanceMatrix distanceMatrix, IdentifierList circularOrdering) {

        int n = distanceMatrix.size();
        double[][] permutedDistances = new double[n][n];
        boolean[][] flag = new boolean[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                permutedDistances[i][j] = distanceMatrix.getDistance(circularOrdering.get(i), circularOrdering.get(j));
            }
        }

        Map<Identifier, Integer> translation = circularOrdering.createLookup();

        for (int i = 0; i < this.getSplits().size(); i++) {

            SplitBlock sb = this.getSplitAt(i).getASide();

            int k = translation.get(circularOrdering.getById(sb.getFirst()));
            int l = translation.get(circularOrdering.getById(sb.getLast()));

            if (k == 0) {
                flag[n - 1][l] = true;
            } else {
                if ((l < n - 1) && (k > l)) {
                    flag[k - 1][l] = true;
                } else {
                    flag[l][k - 1] = true;
                }
            }
        }

        return new CircularNNLS().treeInCycleLeastSquares(permutedDistances, flag, n);
    }

    private int checkFlags(boolean[][] flag) {

        int count = 0;
        for (int i = 0; i < flag.length; i++) {
            for (int j = 0; j < flag[i].length; j++) {
                if (flag[i][j] == true) {
                    count++;
                }
            }
        }

        return count;
    }

    private int checkWeights(double[][] weights) {

        int count = 0;
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                if (weights[i][j] != 0.0) {
                    count++;
                }
            }
        }

        return count;
    }

    @Override
    public boolean isCompatible() {
        return true;
    }
}
