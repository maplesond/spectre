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

package uk.ac.uea.cmp.phygen.core.io.nexus;

import uk.ac.uea.cmp.phygen.core.ds.split.SplitBlock;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sarah_2
 * Date: 07/05/13
 * Time: 23:03
 * To change this template use File | Settings | File Templates.
 */
public class NexusData {

    private List<String> taxa;
    private List<SplitBlock> splits;
    private List<Double> weights;
    private List<Integer> cycle;

    public NexusData() {
        this(new LinkedList<String>(), new LinkedList<Integer>(), new LinkedList<SplitBlock>(), new LinkedList<Double>());
    }

    public NexusData(List<String> taxa, List<Integer> cycle, List<SplitBlock> splits, List<Double> weights) {
        this.taxa = taxa;
        this.cycle = cycle;
        this.splits = splits;
        this.weights = weights;
    }

    public List<String> getTaxa() {
        return taxa;
    }

    public List<Integer> getCycle() {
        return cycle;
    }

    public List<SplitBlock> getSplits() {
        return splits;
    }

    public List<Double> getWeights() {
        return weights;
    }

    public int getNbTaxa() {
        return this.taxa.size();
    }

    public String getTaxonAt(final int i) {
        return this.taxa.get(i);
    }

    public int getCycleAt(final int i) {
        return this.cycle.get(i);
    }

    public int getNbSplits() {
        return this.splits.size();
    }

    public SplitBlock getSplitAt(final int i) {
        return this.splits.get(i);
    }

    public int getNbWeights() {
        return this.weights.size();
    }

    public double getWeightAt(final int i) {
        return this.weights.get(i);
    }

    public NexusData filter(double threshold) {

        int N = this.getNbTaxa();

        int noSplits = this.getNbSplits();
        boolean[] splitExists = new boolean[noSplits];

        int existingSplits = 0;

        for (int i = 0; i < noSplits; i++) {

            double maxWeight = 0.0;

            SplitBlock setA = this.getSplitAt(i);

            // trivial splits are always shown, and added later

            if (setA.size() != 1 && setA.size() != N - 1) {

                for (int j = 0; j < noSplits; j++) {

                    if (i != j) {

                        SplitBlock setB = this.getSplitAt(j);
                        SplitBlock tempList = setB.copy();

                        tempList.retainAll(setA);

                        if (tempList.size() != 0 && tempList.size() != Math.min(setA.size(), setB.size())) {

                            // conflicting

                            double w = this.getWeightAt(j);

                            if (w > maxWeight) {
                                maxWeight = w;
                            }
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

        List<String> taxaCopy = new LinkedList<>();
        List<Integer> cycleCopy = new LinkedList<>();
        List<SplitBlock> filteredSplits = new LinkedList<>();
        List<Double> filteredWeights = new LinkedList<>();

        Collections.copy(taxaCopy, this.getTaxa());
        Collections.copy(cycleCopy, this.getCycle());

        for (int i = 0; i < noSplits; i++) {
            if (splitExists[i]) {
                filteredSplits.add(this.getSplitAt(i));
                filteredWeights.add(this.getWeightAt(i));
            }
        }

        int wn = 0;
        double ws = 0.0;

        for (int i = 0; i < filteredWeights.size(); i++) {
            wn++;
            ws += filteredWeights.get(i);
        }

        double mw = ws / ((double) wn);

        for (int i = 0; i < N; i++) {
            filteredWeights.add(mw);
            filteredSplits.add(new SplitBlock(new int[]{i + 1}));
        }

        return new NexusData(taxaCopy, cycleCopy, filteredSplits, filteredWeights);
    }
}
