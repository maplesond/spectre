package uk.ac.uea.cmp.phygen.qtools.qnet;

import org.apache.commons.lang3.tuple.Pair;
import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.quartet.GroupedQuartetSystem;
import uk.ac.uea.cmp.phygen.core.ds.split.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dan on 15/12/13.
 */
public class QNetResult {

    private CircularOrdering circularOrdering;
    private ComputedWeights computedWeights;
    private GroupedQuartetSystem quartetSystem;

    public QNetResult(CircularOrdering circularOrdering, ComputedWeights computedWeights, GroupedQuartetSystem quartetSystem) {
        this.circularOrdering = circularOrdering;
        this.computedWeights = computedWeights;
        this.quartetSystem = quartetSystem;
    }

    public CircularOrdering getCircularOrdering() {
        return circularOrdering;
    }

    public ComputedWeights getComputedWeights() {
        return computedWeights;
    }

    public GroupedQuartetSystem getQuartetSystem() {
        return quartetSystem;
    }

    public CircularSplitSystem createSplitSystem(double[] limit, SplitLimiter mode) {

        // Setup shortcuts
        Taxa taxa = this.quartetSystem.getTaxa();
        final int N = taxa.size();
        final int maxSplits = SplitUtils.calcMaxSplits(N);
        double[] solution = this.computedWeights.getSolution();

        // Create the basic split indices
        Pair<Integer, Integer>[] splitIndices = SplitUtils.createSplitIndices(taxa);

        List<Split> splits = new ArrayList<>();
        double totalWeight = 0.0;
        int nbValidSplits = 0;
        for (int i = 0; i < maxSplits; i++) {

            if (mode.validSplit(solution[i], limit != null ? limit[i] : 0.0)) {

                Pair<Integer, Integer> sI = splitIndices[i];

                List<Integer> list = new ArrayList<>();

                for (int p = sI.getLeft(); p < sI.getRight(); p++) {

                    list.add(this.circularOrdering.getAt(p));
                }

                nbValidSplits++;
                totalWeight += solution[i];

                splits.add(new Split(new SplitBlock(list), N, solution[i]));
            }
        }


        double meanWeight = 1.0;
        if (nbValidSplits > 0) {
            meanWeight = totalWeight / ((double) nbValidSplits);
        }

        // Now add all the trivial splits
        splits.addAll(SplitUtils.createTrivialSplits(taxa, meanWeight));

        return new CircularSplitSystem(taxa, splits, this.circularOrdering);
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
