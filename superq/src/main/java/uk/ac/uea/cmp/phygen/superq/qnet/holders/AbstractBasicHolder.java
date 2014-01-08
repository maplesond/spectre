package uk.ac.uea.cmp.phygen.superq.qnet.holders;

import org.apache.commons.lang3.tuple.Pair;
import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.quartet.CanonicalWeightedQuartetMap;
import uk.ac.uea.cmp.phygen.core.ds.quartet.Quartet;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: maplesod
 * Date: 02/01/14
 * Time: 14:37
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractBasicHolder {

    protected int[] counts;
    protected double[] weights;
    protected List<Taxa> paths;
    protected CanonicalWeightedQuartetMap theQuartetWeights;


    protected AbstractBasicHolder(final int N, final List<Taxa> paths, CanonicalWeightedQuartetMap theQuartetWeights) {

        this.paths = paths;
        this.theQuartetWeights = theQuartetWeights;

        final int size = Quartet.over2(N);

        counts = new int[size];
        weights = new double[size];

        for (int i = 1; i <= N; i++) {

            for (int j = i + 1; j <= N; j++) {

                Taxa A = Holders.findFirstPathContainingId(paths, i);
                Taxa B = Holders.findFirstPathContainingId(paths, j);

                if (A == null) {
                    throw new IllegalStateException("Could not find taxaset associated with i: " + i);
                }

                if (B == null) {
                    throw new IllegalStateException("Could not find taxaset associated with j: " + j);
                }

                // if on the same path, no quartets meet the conditions
                if (A == B) {

                    counts[Quartet.over2(j - 1) + Quartet.over1(i - 1)] = 0;
                    weights[Quartet.over2(j - 1) + Quartet.over1(i - 1)] = 0.0;

                    continue;
                }

                // Do whatever custom initialisation is required
                Pair<Integer, Double> countWeight = this.calcCountWeight(A, B);

                int index = Quartet.over2(j - 1) + Quartet.over1(i - 1);
                counts[index] = countWeight.getLeft();
                weights[index] = countWeight.getRight();
            }
        }
    }

    protected abstract Pair<Integer, Double> calcCountWeight(Taxa A, Taxa B);

    public double calcWeightedCount(int i, int j) {

        int count = this.getCount(i, j);

        return count == 0 ? 0.0 : this.getWeight(i, j) / ((double)count);
    }

    public int getCount(int i, int j) {

        int x = Math.max(i, j);
        int y = Math.min(i, j);

        return counts[Quartet.over2(x - 1) + Quartet.over1(y - 1)];
    }

    public void setCount(int i, int j, int count) {

        int x = Math.max(i, j);
        int y = Math.min(i, j);

        counts[Quartet.over2(x - 1) + Quartet.over1(y - 1)] = count;
    }

    public double getWeight(int i, int j) {

        int x = Math.max(i, j);
        int y = Math.min(i, j);

        return weights[Quartet.over2(x - 1) + Quartet.over1(y - 1)];
    }

    public void setWeight(int i, int j, double weight) {

        int x = Math.max(i, j);
        int y = Math.min(i, j);

        weights[Quartet.over2(x - 1) + Quartet.over1(y - 1)] = weight;
    }
}
