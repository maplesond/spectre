package uk.ac.uea.cmp.phygen.qnet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetSystem;
import uk.ac.uea.cmp.phygen.core.ds.split.CircularOrdering;

import java.io.File;
import java.io.IOException;

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

    /**
     * Outputs qnet result to disk as a nexus file
     * TODO most of this should be done in NexusWriter
     * @param outputFile
     * @param x
     * @param mode mode tells the method whether we want to write: 0 - standard; 1 - minimum; 2 - maximum
     * @throws IOException
     */
    public void writeWeights(File outputFile, double[] x, int mode) throws IOException {

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
        // print

        String nexusString = new String();

        nexusString += "#NEXUS\nBEGIN taxa;\nDIMENSIONS ntax=" + N + ";\nTAXLABELS\n";

        for (int i = 0; i < N; i++) {

            nexusString += allTaxa.get(i) + "\n";
        }

        nexusString += ";\nEND;\n\nBEGIN st_splits;\nDIMENSIONS ntax=" + N + " nsplits=" + (existingSplits + N) + ";\n";
        nexusString += "FORMAT\nlabels\nweights\n;\nPROPERTIES\nweakly compatible\ncyclic\n;\nCYCLE";

        for (int i = 0; i < N; i++) {

            nexusString += " " + this.circularOrdering.getAt(i);
        }

        nexusString += ";\nMATRIX\n";

        int s = 0;

        int wn = 0;
        double ws = 0.0;

        for (int i = 0; i < N * (N - 1) / 2 - N; i++) {

            if (splitExists[i]) {

                // this split exists

                s++;

                nexusString += "" + (s) + "   " + y[i] + "  ";

                wn++;
                ws += y[i];

                Pair<Integer, Integer> sI = splitIndices[i];

                for (int p = sI.getLeft() + 1; p < sI.getRight() + 1; p++) {

                    nexusString += " " + this.circularOrdering.getAt(p - 1);
                }

                nexusString += ",\n";
            }
        }

        double mw = 1.0;
        if (wn > 0) {
            mw = ws / ((double) wn);
        }

        for (int i = 0; i < N; i++) {

            s++;

            nexusString += "" + (s) + "   " + mw + "  ";

            nexusString += " " + (i + 1);

            nexusString += ",\n";
        }

        s = 0;

        wn = 0;
        ws = 0.0;

        mw = 1.0;
        if (wn > 0) {
            mw = ws / ((double) wn);
        }

        nexusString += ";\nEND;";

        // Write to file
        FileUtils.writeStringToFile(outputFile, nexusString);
    }
}
