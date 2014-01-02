package uk.ac.uea.cmp.phygen.qnet.holders;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.quartet.Quartet;
import uk.ac.uea.cmp.phygen.core.ds.quartet.WeightedQuartetMap;

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
    protected List<Taxa> taxaSets;
    protected WeightedQuartetMap theQuartetWeights;


    protected AbstractBasicHolder(final int N, final List<Taxa> taxaSets, WeightedQuartetMap theQuartetWeights) {

        this.taxaSets = taxaSets;
        this.theQuartetWeights = theQuartetWeights;

        final int size = Quartet.over2(N);

        counts = new int[size];
        weights = new double[size];

        for (int i = 1; i < N; i++) {

            for (int j = i + 1; j < N + 1; j++) {

                int a = indexOfTaxaSet(i - 1);
                int b = indexOfTaxaSet(j - 1);

                if (a == -1) {
                    throw new IllegalStateException("Could not find taxaset associated with i: " + i);
                }

                if (b == -1) {
                    throw new IllegalStateException("Could not find taxaset associated with j: " + j);
                }

                // if on the same path, no quartets meet the conditions
                if (a == b) {

                    counts[Quartet.over2(j - 1) + Quartet.over1(i - 1)] = 0;
                    weights[Quartet.over2(j - 1) + Quartet.over1(i - 1)] = 0.0;

                    continue;
                }

                // otherwise:
                // we now have the list indices

                Taxa A = taxaSets.get(a);
                Taxa B = taxaSets.get(b);

                // Do whatever custom initialisation is required
                Pair<Integer, Double> countWeight = this.calcCountWeight(A, B, a ,b);

                counts[Quartet.over2(j - 1) + Quartet.over1(i - 1)] = countWeight.getLeft();
                weights[Quartet.over2(j - 1) + Quartet.over1(i - 1)] = countWeight.getRight();
            }
        }
    }

    protected int indexOfTaxaSet(int taxaId) {

        for (int m = 0; m < taxaSets.size(); m++) {

            if (taxaSets.get(m).containsId(taxaId)) {

                return m;
            }
        }

        return -1;
    }

    protected abstract Pair<Integer, Double> calcCountWeight(Taxa A, Taxa B, int a, int b);

    public double calcWeightedCount(int i, int j) {
        return this.getWeight(i, j) / ((double) this.getCount(i, j));
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
