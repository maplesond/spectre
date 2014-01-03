package uk.ac.uea.cmp.phygen.superq.qnet;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetSystem;
import uk.ac.uea.cmp.phygen.core.ds.split.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dan on 15/12/13.
 */
public class QNetResult {

    private CircularOrdering circularOrdering;
    private ComputedWeights computedWeights;
    private QuartetSystem quartetSystem;

    public QNetResult(CircularOrdering circularOrdering, ComputedWeights computedWeights, QuartetSystem quartetSystem) {
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

    public QuartetSystem getQuartetSystem() {
        return quartetSystem;
    }

    public CompatibleSplitSystem createSplitSystem(double[] x, int mode) {

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

        int existingSplits = 0;
        // stuff to print _all_ splits

        for (int i = 0; i < noSplits; i++) {

            if (mode == 0)//standard
            {
                if (y[i] > 0.0) {
                    splitExists[i] = true;
                    existingSplits++;
                } else {
                    splitExists[i] = false;
                }
            } else if (mode == 1)//minimum
            {
                if ((y[i] > 0.0) && (y[i] < x[i])) {
                    splitExists[i] = true;
                    existingSplits++;
                } else {
                    splitExists[i] = false;

                }
            } else if (mode == 2)//maximum
            {
                if (y[i] > x[i]) {
                    splitExists[i] = true;
                    existingSplits++;
                } else {
                    splitExists[i] = false;
                }
            }

        }

        List<Split> splits = new ArrayList<>();

        int s = 0;
        double ws = 0.0;
        int wn = 0;

        for(int i = 0; i < noSplits; i++) {
            if (splitExists[i]) {
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

        CompatibleSplitSystem ss = new CompatibleSplitSystem(new CircularSplitSystem(allTaxa, splits, this.circularOrdering));

        return ss;
    }
}
