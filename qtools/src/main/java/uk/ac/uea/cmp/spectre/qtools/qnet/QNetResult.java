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

package uk.ac.uea.cmp.spectre.qtools.qnet;

import org.apache.commons.lang3.tuple.Pair;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.quad.quartet.GroupedQuartetSystem;
import uk.ac.uea.cmp.spectre.core.ds.split.*;

import java.util.ArrayList;
import java.util.List;

public class QNetResult {

    private IdentifierList circularOrdering;
    private ComputedWeights computedWeights;
    private GroupedQuartetSystem quartetSystem;

    public QNetResult(IdentifierList circularOrdering, ComputedWeights computedWeights, GroupedQuartetSystem quartetSystem) {
        this.circularOrdering = circularOrdering;
        this.computedWeights = computedWeights;
        this.quartetSystem = quartetSystem;
    }

    public IdentifierList getCircularOrdering() {
        return circularOrdering;
    }

    public ComputedWeights getComputedWeights() {
        return computedWeights;
    }

    public GroupedQuartetSystem getQuartetSystem() {
        return quartetSystem;
    }

    public SplitSystem createSplitSystem(double[] limit, SplitLimiter mode) {

        // Setup shortcuts
        IdentifierList taxa = this.quartetSystem.getTaxa();
        final int N = taxa.size();
        final int maxSplits = SplitUtils.calcMaxSplits(N);
        double[] solution = this.computedWeights.getSolution();

        // Create the basic split indices
        List<Pair<Integer, Integer>> splitIndices = SplitUtils.createSplitIndices(N);

        List<Split> splits = new ArrayList<>();
        double totalWeight = 0.0;
        int nbValidSplits = 0;
        for (int i = 0; i < maxSplits; i++) {

            if (mode.validSplit(solution[i], limit != null ? limit[i] : 0.0)) {

                Pair<Integer, Integer> sI = splitIndices.get(i);

                List<Integer> list = new ArrayList<>();

                for (int p = sI.getLeft(); p < sI.getRight(); p++) {

                    list.add(this.circularOrdering.get(p).getId());
                }

                nbValidSplits++;
                totalWeight += solution[i];

                splits.add(new SpectreSplit(new SpectreSplitBlock(list), N, solution[i]));
            }
        }


        double meanWeight = 1.0;
        if (nbValidSplits > 0) {
            meanWeight = totalWeight / ((double) nbValidSplits);
        }

        // Now add all the trivial splits
        splits.addAll(SplitUtils.createTrivialSplits(taxa, meanWeight));

        SplitSystem circularSplitSystem = new SpectreSplitSystem(this.circularOrdering, splits);

        // Verify this is a circular split system
        if (!circularSplitSystem.isCircular()) {
            throw new IllegalStateException("Created split system is not circular!");
        }

        return circularSplitSystem;
    }

    public static enum SplitLimiter {

        STANDARD {
            @Override
            public boolean validSplit(double weight, double limit) {
                return weight > 0.0;
            }
        },
        MIN {
            @Override
            public boolean validSplit(double weight, double limit) {
                return (weight > 0.0) && (weight < limit);
            }
        },
        MAX {
            @Override
            public boolean validSplit(double weight, double limit) {
                return weight > limit;
            }
        };

        public abstract boolean validSplit(double weight, double limit);
    }
}
