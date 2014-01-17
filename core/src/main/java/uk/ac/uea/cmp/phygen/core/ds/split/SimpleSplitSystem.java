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

import uk.ac.uea.cmp.phygen.core.ds.SummedDistanceList;
import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.Taxon;
import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;

import java.util.ArrayList;
import java.util.List;

public class SimpleSplitSystem implements SplitSystem {

    private List<Split> splits;
    private Taxa taxa;

    public SimpleSplitSystem(int nbTaxa) {
        this(nbTaxa, new ArrayList<Split>());
    }

    public SimpleSplitSystem(int nbTaxa, List<Split> splits) {

        this(createGenericTaxa(nbTaxa), splits);
    }

    public SimpleSplitSystem(Taxa taxa, List<Split> splits) {

        this.taxa = taxa;
        this.splits = splits;
    }

    protected static Taxa createGenericTaxa(int nbTaxa) {

        Taxa taxa = new Taxa();

        // Assumes we don't have more than 26 taxa, otherwise this is going to get weird.
        for (int i = 0; i < nbTaxa; i++) {
            taxa.add(new Taxon(Character.toString((char) (i + 65))));
        }

        return taxa;
    }

    @Override
    public List<Split> getSplits() {
        return splits;
    }

    protected void setSplits(List<Split> splits) {
        this.splits = splits;
    }

    @Override
    public Taxa getTaxa() {
        return this.taxa;
    }

    protected void setTaxa(Taxa taxa) {
        this.taxa = taxa;
    }

    @Override
    public int getNbTaxa() {
        return this.taxa.size();
    }

    public String getTaxaAt(int i) {
        return this.taxa.get(i).getName();
    }


    /**
     * Calculate the summed distanceMatrix between elements on the A side and B side for every split
     *
     * @return Array of P
     */
    public SummedDistanceList calculateP(DistanceMatrix distanceMatrix) {

        if (this.getNbTaxa() != distanceMatrix.size())
            throw new IllegalArgumentException("The number of taxa in DistanceMatrix is not that same as in this splis system");

        double P[] = new double[this.splits.size()];

        // For each split, determine how many elements are on each side of the split
        for (int i = 0; i < this.splits.size(); i++) {
            P[i] = this.splits.get(i).calculateP(distanceMatrix);
        }

        return new SummedDistanceList(P);
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
    public CircularOrdering getCircularOrdering() {
        throw new UnsupportedOperationException("A simple split system is not necessarily circular.  Can't retrieve circular ordering.");
    }

    @Override
    public boolean isCompatible() {
        return false;
    }

}
