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

package uk.ac.uea.cmp.spectre.core.ds.split;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.util.CombinatoricsUtils;
import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.network.draw.PermutationSequenceDraw;
import uk.ac.uea.cmp.spectre.core.ds.split.circular.ordering.CircularNNLS;
import uk.ac.uea.cmp.spectre.core.ds.split.flat.PermutationSequence;

import java.util.*;

public class SpectreSplitSystem extends ArrayList<Split> implements SplitSystem {

    private IdentifierList orderedTaxa;

    private HashMap<Split, Integer> mapOfSplits;

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
     * @param orderedTaxa The ordered taxa contained in this split system
     */
    public SpectreSplitSystem(IdentifierList orderedTaxa) {
        this(orderedTaxa, SplitUtils.createTrivialSplits(orderedTaxa, 1.0));
    }

    /**
     * Creates a split system using the specified ordered taxa and pre-made splits.
     * @param orderedTaxa The ordered taxa contained in this split system
     * @param splits The pre-made splits making up the split system
     */
    public SpectreSplitSystem(IdentifierList orderedTaxa, List<Split> splits) {
        this.orderedTaxa = orderedTaxa;
        this.mapOfSplits = new HashMap<>();

        for(Split s : splits) {
            this.add(s);
        }
    }

    /**
     * Creates a SplitSystem from the given distance matrix.  Assumes the taxa ordering should be based on numerically
     * ascending ID value.  Uses circular least squares for calculating the split weights
     *
     * @param distanceMatrix The distance matrix to base this split system on
     */
    public SpectreSplitSystem(DistanceMatrix distanceMatrix) {
        this(distanceMatrix, distanceMatrix.getTaxa().sortById());
    }

    /**
     * Creates a SplitSystem from the given distance matrix using the specified taxa ordering.  Uses circular least
     * squares for calculating the split weights
     *
     * @param distanceMatrix    The distance matrix to base this split system on
     * @param orderedTaxa       The ordering of the taxa in this split system
     */
    public SpectreSplitSystem(DistanceMatrix distanceMatrix, IdentifierList orderedTaxa) {
        this(distanceMatrix, orderedTaxa, LeastSquaresCalculator.CIRCULAR);
    }

    /**
     * Creates a SplitSystem from the given distance matrix using the specified split weights calculator.
     * Assumes the taxa ordering should be based on numerically ascending ID value.
     *
     * @param distanceMatrix The distance matrix to base this split system on
     * @param calculator The calculator to use for calculating the split weights
     */
    public SpectreSplitSystem(DistanceMatrix distanceMatrix, LeastSquaresCalculator calculator) {
        this(distanceMatrix, distanceMatrix.getTaxa().sortById(), calculator);
    }


    /**
     * Creates a CircularSplitSystem from the given distance matrix and specified circular ordering
     *
     * @param distanceMatrix    The distance matrix to base this split system on
     * @param circularOrdering  The ordering of the taxa in this split system
     * @param calculator        The calculator to use for calculating the split weights
     */
    public SpectreSplitSystem(DistanceMatrix distanceMatrix, IdentifierList circularOrdering, LeastSquaresCalculator calculator) {

        this(distanceMatrix, circularOrdering, calculator, new ArrayList<Split>());
    }

    /**
     *
     * @param distanceMatrix    The distance matrix to base this split system on
     * @param circularOrdering  The ordering of the taxa in this split system
     * @param calculator        The calculator to use for calculating the split weights
     * @param splits            The pre-made splits making up the split system
     */
    public SpectreSplitSystem(DistanceMatrix distanceMatrix, IdentifierList circularOrdering, LeastSquaresCalculator calculator, List<Split> splits) {

        this(circularOrdering, splits);

        if (circularOrdering.size() != distanceMatrix.size()) {
            throw new IllegalArgumentException("Distance matrix and circular ordering are not the same size");
        }

        this.reweight(calculator.calculate(distanceMatrix, circularOrdering, splits));
    }

    /**
     * Copy constructor
     * @param splitSystem Split system to copy
     */
    public SpectreSplitSystem(SplitSystem splitSystem) {
        this(new IdentifierList(splitSystem.getOrderedTaxa()), new ArrayList<>(splitSystem));
    }


    public SpectreSplitSystem(SplitSystem unweightedSplitSystem, TreeSplitWeights treeWeights) {

        // Create a copy of the split system
        this(unweightedSplitSystem);

        // Reweight the split system using the provided tree weights
        this.reweight(treeWeights);
    }

    public SpectreSplitSystem(PermutationSequence p_sequ) {
        int ntaxa = p_sequ.getnTaxa();
        int nsplits = p_sequ.getnSwaps();
        int[] cur_sequ = ArrayUtils.clone(p_sequ.getSequence());
        String[] tax = p_sequ.getTaxaNames();

        this.mapOfSplits = new HashMap<>();
        this.orderedTaxa = new IdentifierList();
        for(int i = 0; i < ntaxa; i++) {
            this.orderedTaxa.add(new Identifier(tax == null ? Integer.toString(cur_sequ[i]) : tax[cur_sequ[i]], cur_sequ[i]));
        }

        //Write splits into 0/1-array.
        int[] swaps = p_sequ.getSwaps();
        for (int i = 0; i < nsplits; i++) {
            //compute current permutation
            int h = cur_sequ[swaps[i]];
            cur_sequ[swaps[i]] = cur_sequ[swaps[i] + 1];
            cur_sequ[swaps[i] + 1] = h;
            //turn it into a 0/1 sequence
            List<Integer> aside = new ArrayList<>();
            List<Integer> bside = new ArrayList<>();
            for (int j = 0; j < ntaxa; j++) {
                if (j <= swaps[i]) {
                    aside.add(cur_sequ[j]);
                }
                else {
                    bside.add(cur_sequ[j]);
                }
            }
            Split s = new SpectreSplit(new SpectreSplitBlock(aside), new SpectreSplitBlock(bside), p_sequ.getWeights() != null ? p_sequ.getWeights()[i] : 1.0);
            if (p_sequ.getActive() != null) {
                s.setActive(p_sequ.getActive()[i]);
            }
            this.add(s);
        }
    }

    public SpectreSplitSystem(PermutationSequenceDraw p_sequ) {
        int ntaxa = p_sequ.getNbTaxa();
        int nsplits = p_sequ.getNswaps();
        int[] cur_sequ = ArrayUtils.clone(p_sequ.getInitSequ());
        String[] tax = p_sequ.getTaxaname();

        this.mapOfSplits = new HashMap<>();
        this.orderedTaxa = new IdentifierList();
        for(int i = 0; i < ntaxa; i++) {
            this.orderedTaxa.add(new Identifier(tax == null ? Integer.toString(cur_sequ[i]) : tax[cur_sequ[i]], cur_sequ[i]));
        }

        //Write splits into 0/1-array.
        int[] swaps = p_sequ.getSwaps();
        for (int i = 0; i < nsplits; i++) {
            //compute current permutation
            int h = cur_sequ[swaps[i]];
            cur_sequ[swaps[i]] = cur_sequ[swaps[i] + 1];
            cur_sequ[swaps[i] + 1] = h;
            //turn it into a 0/1 sequence
            List<Integer> aside = new ArrayList<>();
            for (int j = 0; j < ntaxa; j++) {
                if (j <= swaps[i]) {
                    aside.add(cur_sequ[j]);
                }
            }
            this.add(new SpectreSplit(new SpectreSplitBlock(aside), ntaxa, true));
        }
    }

    public Split get(Split s) {
        return this.get(this.mapOfSplits.get(s.makeCanonical()));
    }

    public Split set(int index, Split element) {
        Split s = this.get(index);
        this.mapOfSplits.remove(s.makeCanonical());
        this.mapOfSplits.put(element.makeCanonical(), index);
        return super.set(index, element);
    }

    @Override
    public boolean add(Split s) {
        boolean res = super.add(s);
        this.mapOfSplits.put(s.makeCanonical(), this.size() - 1);
        return res;
    }

    @Override
    public void add(int index, Split s) {
        super.add(index, s);
        this.mapOfSplits.put(s.makeCanonical(), index);
    }

    @Override
    public Split remove(int index) {
        Split s = this.get(index);
        super.remove(index);
        this.mapOfSplits.remove(s.makeCanonical());
        return s;
    }

    @Override
    public boolean remove(Object o) {
        this.mapOfSplits.remove(((Split)o).makeCanonical());
        return super.remove(o);
    }


    @Override
    public boolean equals(Object other) {
        if (!(other instanceof SplitSystem)) {
            return false;
        }

        SplitSystem that = (SplitSystem) other;

        if (this == that) {
            return true;
        }

        if (this.getNbSplits() != that.getNbSplits() || this.getNbTaxa() != that.getNbTaxa()) {
            return false;
        }

        for (Split s : this) {
            if (!that.contains(s)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int getNbTaxa() {
        return this.orderedTaxa.size();
    }

    @Override
    public int getNbSplits() {
        return this.size();
    }

    @Override
    public int getNbTrivialSplits() {
        int count = 0;
        for(Split s : this) {
            if (s.isTrivial()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int getNbNonTrivialSplits() {
        return this.getNbSplits() - this.getNbTrivialSplits();
    }

    @Override
    public int getNbActiveWeightedSplits() {
        int count = 0;
        for(Split s : this) {
            if (s.isActive() && (!this.isWeighted() || s.getWeight() > 0.0)) {
                count++;
            }
        }
        return count;
    }

    public void setCircularOrdering(IdentifierList orderedTaxa) {
        this.orderedTaxa = orderedTaxa;
    }

    @Override
    public IdentifierList getOrderedTaxa() {
        return this.orderedTaxa;
    }

    public int getLastSplitIndex() {
        return this.size() - 1;
    }

    @Override
    public Split removeLastSplit() {
        return this.remove(this.getLastSplitIndex());
    }

    public Split getLastSplit() {
        return this.get(this.getLastSplitIndex());
    }


    @Override
    public boolean contains(Split split) {
        return this.mapOfSplits.get(split) != null;
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
        Split split1 = this.get(index1);
        Split split2 = this.get(index2);

        split1.mergeASides(split2);

        this.remove(index2);

        return split1;
    }

    @Override
    public boolean isWeighted() {

        for (Split s : this) {
            if (s.getWeight() <= 0.0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public double getWeightAt(int i) {
        return this.get(i).getWeight();
    }


    @Override
    public SplitSystem filterByRelativeWeight(double percentThreshold) {

        int N = this.getNbTaxa();

        int nbSplits = this.size();
        boolean[] splitExists = new boolean[nbSplits];

        int existingSplits = 0;

        for (int i = 0; i < nbSplits; i++) {

            double maxWeight = 0.0;

            SplitBlock setA = this.get(i).getASide();

            // trivial splits are always shown, and added later

            if (setA.size() != 1 && setA.size() != N - 1) {

                for (int j = 0; j < nbSplits; j++) {

                    if (i != j) {

                        SplitBlock setB = this.get(j).getBSide();
                        SplitBlock tempList = setB.copy();

                        tempList.retainAll(setA);

                        if (tempList.size() != 0 && tempList.size() != Math.min(setA.size(), setB.size())) {
                            maxWeight = Math.max(this.getWeightAt(j), maxWeight);
                        }
                    }
                }

                double w = this.getWeightAt(i);

                if (w > maxWeight * percentThreshold) {
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
                filteredSplits.add(this.get(i));
            }
        }

        double mw = SplitUtils.meanOfWeights(filteredSplits);

        for (int i = 0; i < N; i++) {
            filteredSplits.add(new SpectreSplit(new SpectreSplitBlock(new int[]{i + 1}), this.getNbTaxa(), mw, false));
        }

        // Overwrites the current set of splits with the filtered splits
        this.clear();
        for(Split s : filteredSplits) {
            this.add(s);
        }

        return this;
    }

    @Override
    public SplitSystem filterByAbsoluteWeight(double minWeight) {

        List<Split> filtered = new ArrayList<>();

        for(Split s : this) {
            if (s.getWeight() >= minWeight) {
                filtered.add(new SpectreSplit(s));
            }
        }

        return new SpectreSplitSystem(new IdentifierList(this.orderedTaxa), filtered);
    }

    @Override
    public void activateByWeight(double threshold) {
        for (Split s : this) {
            s.setActive(s.getWeight() < threshold);
        }
    }

    @Override
    public SplitSystem makeCanonical() {
        List<Split> splits = new ArrayList<>();

        for(Split s : this) {
            splits.add(s.makeCanonical());
        }

        Collections.sort(splits);

        return new SpectreSplitSystem(this.orderedTaxa, splits);
    }

    @Override
    public SplitSystem makeInducedOrdering() {
        List<Split> splits = new ArrayList<>();

        IdentifierList currentPerm = new IdentifierList(this.orderedTaxa);

        int found = 0;  // For sanity checking.

        for(int i = 0; i < this.getNbTaxa(); i++){
            for(int j = 0; j < this.getNbTaxa() - i - 1; j++){
                int[] sideA = new int[j+1];
                for(int a = 0; a <= j; a++) { sideA[a] = currentPerm.get(a).getId(); }
                SplitBlock newSideA = new SpectreSplitBlock(sideA);
                newSideA.sort();
                int[] sideB = new int[this.getNbTaxa() - j - 1];
                for(int b = 0; b < this.getNbTaxa() - j - 1; b++) { sideB[b] = currentPerm.get(b + j + 1).getId(); }
                SplitBlock newSideB = new SpectreSplitBlock(sideB);
                newSideB.sort();
                Split newSplit = new SpectreSplit(newSideA, newSideB);

                //next check whether the input split system contains a split with one SplitBlock equal to the one we just created
                if (this.contains(newSplit)) {
                    Split s = this.get(newSplit);
                    newSplit.setWeight(s.getWeight());
                    newSplit.setActive(s.isActive());
                    found++;
                    splits.add(newSplit);
                }

                // Flip position j and j+1 in current permutation
                Identifier temp = currentPerm.get(j);
                currentPerm.set(j, currentPerm.get(j+1));
                currentPerm.set(j+1, temp);
            }
        }

        if (found != this.getNbSplits()) {
            throw new IllegalStateException("Could not create induced ordering.  This could be because you have duplicate splits in the system, or because the split system is either not flat and/or circular.");
        }

        return new SpectreSplitSystem(new IdentifierList(this.orderedTaxa), splits);
    }

    @Override
    public boolean isCircular() {

        for(Split s : this) {
            if (!s.isCircular(this.orderedTaxa)) {
                return false;
            }
        }

        return true;
    }


    /**
     * Checks to see that all pairs of splits in this split system are compatible.
     *
     * If this is true, then this split system can be represented as a tree.
     *
     * @return True if all pairs of splits are compatible, false otherwise.
     */
    @Override
    public boolean isCompatible() {

        for(Split a : this) {
            for(Split b : this) {
                if (a != b && !a.isCompatible(b)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Whether or not the two specified splits in the system are compatible     *
     * @return True if these are compatible splits, false otherwise
     */
    @Override
    public boolean isCompatible(final int i, final int j) {
        return this.get(i).isCompatible(this.get(j));
    }

    @Override
    public Split.Compatible getCompatible(int i, int j) {
        return this.get(i).getCompatible(this.get(j));
    }

    @Override
    public boolean isFull() {

        final long maxSplits = CombinatoricsUtils.binomialCoefficient(this.getNbTaxa(), 2);

        if (this.getNbSplits() > maxSplits) {
            throw new IllegalStateException("This split system contains " + this.getNbTaxa() + " and therefore should not contain more than " + maxSplits + ".  This split system contains " + this.getNbSplits() + " splits.");
        }

        return this.getNbSplits() == maxSplits;
    }

    @Override
    public boolean isTaxonOnSameSide(int i, int j, int taxon) {
        Split.SplitSide iSide = this.get(i).getSide(taxon);
        Split.SplitSide jSide = this.get(j).getSide(taxon);
        return iSide == jSide;
    }

    @Override
    public boolean restrictionExists(int a, int b, int c, int d, int nr) {

        for(Split s : this) {
            if (s.restrictionExists(a, b, c, d, nr)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean[][] getAs2DBooleanArray() {
        boolean[][] splits = new boolean[this.size()][this.getNbTaxa()];
        for (int i = 0; i < this.size(); i++) {
            for (int j = 0; j < this.getNbTaxa(); j++) {
                int id = this.orderedTaxa.getById(j).getId();
                splits[i][j] = this.get(i).getASide().contains(id);
            }
        }
        return splits;
    }

    @Override
    public double[] getWeightsAsArray() {

        double[] weights = new double[this.size()];
        for(int i = 0; i < this.size(); i++) {
            weights[i] = this.get(i).getWeight();
        }
        return weights;
    }

    @Override
    public void incTaxId() {
        IdentifierList newTaxa = new IdentifierList();
        for(Identifier i : this.orderedTaxa) {
            newTaxa.add(new Identifier(Integer.toString(i.getId() + 1), i.getId() + 1));
        }
        this.orderedTaxa = newTaxa;

        for(Split s : this) {
            s.incTaxId();
        }
    }


    /**
     * Deletes all splits and recalculates them.  All splits must have a positive length
     *
     * @param treeWeights Tree weights to use to reweight this split system
     */
    protected void reweight(SplitWeights treeWeights) {

        int n = this.getNbTaxa();

        // Clear out any splits
        this.clear();

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (treeWeights.getAt(j, i) > 0.0) {

                    ArrayList<Integer> sb = new ArrayList<>();
                    for (int k = i + 1; k < j + 1; k++) {
                        sb.add(this.getOrderedTaxa().get(k).getId());
                    }

                    this.add(new SpectreSplit(new SpectreSplitBlock(sb), n, treeWeights.getAt(j, i), false));
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Taxa: " + this.orderedTaxa.toString() + " : " + super.toString();
    }

    public enum LeastSquaresCalculator {

        CIRCULAR {
            @Override
            public SplitWeights calculate(DistanceMatrix distanceMatrix, IdentifierList circularOrdering, List<Split> splits) {
                return new CircularNNLS().circularLeastSquares(distanceMatrix, circularOrdering);
            }
        },
        TREE_IN_CYCLE {
            @Override
            public SplitWeights calculate(DistanceMatrix distanceMatrix, IdentifierList circularOrdering, List<Split> splits) {
                return new CircularNNLS().treeInCycleLeastSquares(distanceMatrix, circularOrdering, splits);
            }
        };

        public abstract SplitWeights calculate(DistanceMatrix distanceMatrix, IdentifierList circularOrdering, List<Split> splits);
    }
}
