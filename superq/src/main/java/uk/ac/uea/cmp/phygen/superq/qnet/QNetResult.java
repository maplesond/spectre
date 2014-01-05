package uk.ac.uea.cmp.phygen.superq.qnet;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.quartet.GroupedQuartetSystem;
import uk.ac.uea.cmp.phygen.core.ds.split.CircularOrdering;
import uk.ac.uea.cmp.phygen.core.ds.split.CircularSplitSystem;
import uk.ac.uea.cmp.phygen.core.ds.split.Split;
import uk.ac.uea.cmp.phygen.core.ds.split.SplitBlock;

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
        Taxa allTaxa = this.quartetSystem.getTaxa();
        int N = allTaxa.size();
        double[] y = this.computedWeights.getX();

        Pair<Integer, Integer>[] splitIndices = new ImmutablePair[N * (N - 1) / 2 - N];

        int n = 0;
        int m;

        for (m = 1; m < N - 1; m++) {

            for (int j = m + 2; j < N + 1; j++) {

                if (m != 1 || j != N) {

                    // valid split
                    splitIndices[n] = new ImmutablePair<>(m, j);
                    n++;
                }
            }
        }

        /*final int size = N * (N - 1) * (N - 2) * (N - 3) / 12;
        double[] f = new double[size];
        QuartetIndex[] quartetIndices = new QuartetIndex[size];

        n = 0;

        for (int i = 1; i < N - 2; i++) {

            for (int j = i + 1; j < N - 1; j++) {

                for (int k = j + 1; k < N; k++) {

                    for (int l = k + 1; l < N + 1; l++) {

                        int cI = c.get(i - 1).getId();
                        int cJ = c.get(j - 1).getId();
                        int cK = c.get(k - 1).getId();
                        int cL = c.get(l - 1).getId();

                        quartetIndices[n] = new QuartetIndex(i, j, k, l);
                        f[n] = theQuartetWeights.getWeight(new Quartet(cI, cJ, cK, cL));
                        n++;

                        quartetIndices[n] = new QuartetIndex(i, l, j, k);
                        f[n] = theQuartetWeights.getWeight(new Quartet(cI, cL, cJ, cK));
                        n++;
                    }
                }
            }
        }     */

        int noSplits = N * (N - 1) / 2 - N;
        boolean[] splitExists = new boolean[noSplits];
        List<Split> splits = new ArrayList<>();

        int s = 0;
        double ws = 0.0;
        int wn = 0;
        int existingSplits = 0;
        for (int i = 0; i < noSplits; i++) {

            if (mode.validSplit(y[i], limit != null ? limit[i] : 0.0)) {
                s++;

                Pair<Integer, Integer> sI = splitIndices[i];

                List<Integer> list = new ArrayList<>();

                for (int p = sI.getLeft(); p < sI.getRight(); p++) {

                    list.add(this.circularOrdering.getAt(p));
                }

                wn++;
                ws += y[i];

                splits.add(new Split(new SplitBlock(list), N, y[i]));
            }
        }


        double mw = 1.0;
        if (wn > 0) {
            mw = ws / ((double) wn);
        }

        for (int i = 0; i < N; i++) {

            s++;

            splits.add(new Split(new SplitBlock(new int[]{i+1}), N, mw));
        }

        return new CircularSplitSystem(allTaxa, splits, this.circularOrdering);
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
