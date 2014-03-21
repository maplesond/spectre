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
package uk.ac.uea.cmp.spectre.core.ds.split;

import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;

import java.util.ArrayList;
import java.util.List;

public class SpectreSplitSystem implements SplitSystem {

    private List<Split> splits;
    private IdentifierList orderedTaxa;

    /**
     * Creates split system with the specified number of taxa and the specified list of splits.
     * @param nbTaxa The number of taxa in this split system
     * @param splits The splits making up the split system
     */
    public SpectreSplitSystem(int nbTaxa, List<Split> splits) {
        this(new IdentifierList(nbTaxa), splits);
    }

    /**
     * Creates a split system using the specified ordered taxa, and creates a set of trivial splits to start with.
     * @param taxa The taxa contained in this split system
     */
    public SpectreSplitSystem(IdentifierList taxa) {
        this(taxa.sortById(), SplitUtils.createTrivialSplits(taxa, 1.0));
    }

    /**
     * Creates a split system using the specified taxa and splits.
     * @param taxa The taxa contained in this split system
     * @param splits The splits making up the split system
     */
    public SpectreSplitSystem(IdentifierList taxa, List<Split> splits) {
        this.orderedTaxa = taxa.sortById();
        this.splits = splits;
    }

    /**
     * Creates a SplitSystem from the given distance matrix.  Assumes the taxa ordering should be based on numerically
     * ascending ID value.
     *
     * @param distanceMatrix The distance matrix to base this split system on
     */
    public SpectreSplitSystem(DistanceMatrix distanceMatrix) {
        this(distanceMatrix, distanceMatrix.getTaxa().sortById());
    }


    /**
     * Creates a CircularSplitSystem from the given distance matrix and specified circular ordering
     *
     * @param distanceMatrix   The distance matrix to base this split system on
     * @param circularOrdering The ordering of the taxa in this split system
     */
    public SpectreSplitSystem(DistanceMatrix distanceMatrix, IdentifierList circularOrdering) {

        this(circularOrdering, new ArrayList<Split>());

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
                        sb.add(circularOrdering.get(k).getId());
                    }

                    this.addSplit(new Split(new SplitBlock(sb), n, splitWeights.getAt(j, i)));
                }
            }
        }
    }

    public SpectreSplitSystem(SplitSystem splitSystem) {
        this(new IdentifierList(splitSystem.getOrderedTaxa()), new ArrayList<>(splitSystem.getSplits()));
    }

    @Override
    public List<Split> getSplits() {
        return splits;
    }

    protected void setSplits(List<Split> splits) {
        this.splits = splits;
    }


    @Override
    public int getNbTaxa() {
        return this.orderedTaxa.size();
    }

    public void setCircularOrdering(IdentifierList orderedTaxa) {
        this.orderedTaxa = orderedTaxa;
    }

    @Override
    public IdentifierList getOrderedTaxa() {
        return this.orderedTaxa;
    }

    @Override
    public void addSplit(Split split) {
        this.splits.add(split);
    }

    @Override
    public void removeLastSplit() {
        this.splits.remove(this.splits.size() - 1);
    }

    @Override
    public Split getSplitAt(int index) {
        return this.splits.get(index);
    }

    public int getLastSplitIndex() {
        return this.splits.size() - 1;
    }

    public Split getLastSplit() {
        return this.getSplitAt(this.getLastSplitIndex());
    }

    @Override
    public int getNbSplits() {
        return this.splits.size();
    }

    public void removeSplit(int index) {
        this.splits.remove(index);
    }



    /**
     * Appends a split at a specified position in this split system
     * to another row.
     *
     * @param index1 the index of the split that is to be expanded
     * @param index2 the index of the split that is to be removed
     * @throws IndexOutOfBoundsException if a row is out of range
     */
    @Override
    public Split mergeSplits(int index1, int index2) {
        Split split1 = this.getSplitAt(index1);
        Split split2 = this.getSplitAt(index2);

        split1.merge(split2);

        this.splits.remove(index2);

        return split1;
    }

    public List<Split> copySplits() {

        List<Split> ss = new ArrayList<>();

        for (Split s : this.splits) {
            ss.add(s.copy());
        }

        return ss;
    }

    @Override
    public boolean isWeighted() {
        return true;
    }

    @Override
    public double getWeightAt(int i) {
        return this.getSplitAt(i).getWeight();
    }


    /*public void convertToIndicies() {
        for (int i = 0; i < splits.size(); i++) {
            String split = splits.get(i);

            StringBuilder sb = new StringBuilder();

            String[] taxon = split.split(" ");

            for (String taxa : taxon) {
                int idx = names.indexOf(taxa);

                if (idx == -1) {
                    throw new IllegalStateException("This shouldn't have happened!");
                }

                sb.append(Integer.toString(idx + 1)).append(" ");
            }

            splits.set(i, sb.toString().trim());
        }
    }*/


    @Override
    public SplitSystem filterByWeight(double threshold) {

        int N = this.getNbTaxa();

        int nbSplits = this.getNbSplits();
        boolean[] splitExists = new boolean[nbSplits];

        int existingSplits = 0;

        for (int i = 0; i < nbSplits; i++) {

            double maxWeight = 0.0;

            SplitBlock setA = this.getSplitAt(i).getASide();

            // trivial splits are always shown, and added later

            if (setA.size() != 1 && setA.size() != N - 1) {

                for (int j = 0; j < nbSplits; j++) {

                    if (i != j) {

                        SplitBlock setB = this.getSplitAt(j).getBSide();
                        SplitBlock tempList = setB.copy();

                        tempList.retainAll(setA);

                        if (tempList.size() != 0 && tempList.size() != Math.min(setA.size(), setB.size())) {
                            maxWeight = Math.max(this.getWeightAt(j), maxWeight);
                        }
                    }
                }

                double w = this.getWeightAt(i);

                if (w > maxWeight * threshold) {
                    splitExists[i] = true;
                    existingSplits++;
                } else {
                    splitExists[i] = false;
                }
            } else {
                splitExists[i] = false;
            }
        }

        List<Split> filteredSplits = new ArrayList<>();

        for (int i = 0; i < nbSplits; i++) {
            if (splitExists[i]) {
                filteredSplits.add(this.getSplitAt(i));
            }
        }

        double mw = SplitUtils.meanOfWeights(filteredSplits);

        for (int i = 0; i < N; i++) {
            filteredSplits.add(new Split(new SplitBlock(new int[]{i + 1}), this.getNbTaxa(), mw));
        }

        // Overwrites the current set of splits with the filtered splits
        this.splits = filteredSplits;

        return this;
    }

    @Override
    public boolean isCircular() {
        return false;
    }


    @Override
    public boolean isCompatible() {
        return false;
    }



    /**
     * Calculates the weights of this full circular split system.  The length determines
     * which splits will be returned.  This works by reordering the distance matrix by the circular ordering and then
     * running the circular least squares algorithm on that matrix.
     *
     * @return Split weights matrix
     */
    protected SplitWeights calculateSplitWeighting(DistanceMatrix distanceMatrix, IdentifierList circularOrdering) {
        int n = distanceMatrix.size();

        // Reorder the distance matrix
        double[][] permutedDistances = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                permutedDistances[i][j] = distanceMatrix.getDistance(circularOrdering.get(i), circularOrdering.get(j));
            }
        }

        return new CircularNNLS().circularLeastSquares(permutedDistances, n);
    }

}
