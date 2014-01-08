package uk.ac.uea.cmp.phygen.superq.qnet.holders;

import org.apache.commons.lang3.tuple.Pair;
import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.quartet.CanonicalWeightedQuartetMap;
import uk.ac.uea.cmp.phygen.core.ds.quartet.Quartet;
import uk.ac.uea.cmp.phygen.superq.qnet.QNetException;

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


    protected AbstractBasicHolder(final int N, final List<Taxa> paths, CanonicalWeightedQuartetMap theQuartetWeights)
            throws QNetException {

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

                // 0-base the indicies and use Quartet overs to calculate the index to use
                int index = Quartet.over2(j - 1) + Quartet.over1(i - 1);

                // if on the same path, no quartets meet the conditions
                if (A == B) {

                    counts[index] = 0;
                    weights[index] = 0.0;
                }
                else {

                    // Do whatever custom initialisation is required
                    Pair<Integer, Double> countWeight = this.calcCountWeight(A, B);

                    counts[index] = countWeight.getLeft();
                    weights[index] = countWeight.getRight();
                }
            }
        }
    }

    /**
     * Calculates the counts and weights using 2 paths.
     * @param A The first path
     * @param B The second path
     * @return The count and weight for the 2 paths.
     */
    protected abstract Pair<Integer, Double> calcCountWeight(Taxa A, Taxa B) throws QNetException;

    /**
     * Returns the weighted count at i, j.
     * @param i First 1-based index
     * @param j Second 1-based index
     * @return The weighted count at i, j
     */
    public double calcWeightedCount(int i, int j) {

        int count = this.getCount(i, j);

        return count == 0 ? 0.0 : this.getWeight(i, j) / ((double)count);
    }

    /**
     * Returns the count at i, j.  Indicies are 1-based.  Calculates the index by using quartet over2 on the largest
     * index and quartet over1 on the smallest index.
     * First 1-based index
     * @param j Second 1-based index
     * @return The count at i, j
     */
    public int getCount(int i, int j) {

        int x = Math.max(i, j) - 1;
        int y = Math.min(i, j) - 1;

        return counts[Quartet.over2(x) + Quartet.over1(y)];
    }


    public void setCount(int i, int j, int count) {

        int x = Math.max(i, j) - 1;
        int y = Math.min(i, j) - 1;

        counts[Quartet.over2(x) + Quartet.over1(y)] = count;
    }

    /**
     * Returns the weights at i, j.  Indicies are 1-based.  Calculates the index by using quartet over2 on the largest
     * index and quartet over1 on the smallest index.
     * @param i First 1-based index
     * @param j Second 1-based index
     * @return The weight at i, j
     */
    public double getWeight(int i, int j) {

        int x = Math.max(i, j) - 1;
        int y = Math.min(i, j) - 1;

        return weights[Quartet.over2(x) + Quartet.over1(y)];
    }

    public void setWeight(int i, int j, double weight) {

        int x = Math.max(i, j) - 1;
        int y = Math.min(i, j) - 1;

        weights[Quartet.over2(x) + Quartet.over1(y)] = weight;
    }
}
