package uk.ac.uea.cmp.phygen.core.ds.split;

import uk.ac.uea.cmp.phygen.core.alg.CircularNNLS;
import uk.ac.uea.cmp.phygen.core.ds.Distances;
import uk.ac.uea.cmp.phygen.core.ds.TreeWeights;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 27/04/13
 * Time: 19:08
 * To change this template use File | Settings | File Templates.
 */
public class CircularSplitSystem extends SplitSystem {

    private int[] circularOrdering;

    public CircularSplitSystem(List<Split> splits, int[] circularOrdering) {
        super(circularOrdering.length, splits);
        this.circularOrdering = circularOrdering;
    }

    public CircularSplitSystem(int[] circularOrdering) {

        super(circularOrdering.length);

        this.circularOrdering = circularOrdering;

        for(int i = 0; i < circularOrdering.length - 1; i++) {
            for(int j = i; j < circularOrdering.length - 1; j++) {

                ArrayList<Integer> s = new ArrayList<>();
                for(int k = i; k <= j; k++) {
                    s.add(circularOrdering[k]);
                }

                this.addSplit(new Split(new SplitBlock(s), circularOrdering.length));
            }
        }
    }

    public void setCircularOrdering(int[] circularOrdering) {
        this.circularOrdering = circularOrdering;
    }

    public int[] getCircularOrdering() {
        return circularOrdering;
    }

    /**
     * Returns the value of the permutation at the specified position.
     *
     * @param i index of the value to be returned.
     * @return value of the permutation at i
     */
    @Override
    public int getTaxaIndexAt(final int i) {
        return this.circularOrdering[i] + 1;
    }

    @Override
    public int[] invertOrdering() {
        int[] permutationInvert = new int[this.getNbTaxa()];

        for (int i = 0; i < this.getNbTaxa(); i++) {
            permutationInvert[this.circularOrdering[i]] = i;
        }

        return permutationInvert;
    }

    /**
     * Calculates the weights of this full circular split system.  The weight determines
     * which splits will be returned.
     * @return Split weights matrix
     */
    public double[][] calculateSplitWeighting(Distances distances)
    {
        int n = distances.size();
        double[][] permutedDistances = new double[n][n];
        double[][] splitWeights = new double[n][n];
        CircularNNLS cnnls = new CircularNNLS();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                permutedDistances[i][j] = distances.getDistance(this.circularOrdering[i], this.circularOrdering[j]);
            }
        }

        for (int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                splitWeights[i][j] = 0.;
            }
        }

        cnnls.circular_least_squares(permutedDistances, n, splitWeights);

        return splitWeights;
    }

    /**
     * Returns weightings for the edges of a specified tree.
     *
     * @return tree edge weightings
     */
    public TreeWeights calculateTreeWeighting(Distances distances) {

        int n = distances.size();
        double[][] treeWeights = new double[n][n];
        double[][] permutedDistances = new double[n][n];
        boolean[][] flag = new boolean[n][n];
        int[] permutationInvert = new int[n];

        for(int i = 0; i < n; i++) {
            permutationInvert[circularOrdering[i]] = i;
        }

        for (int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                flag[i][j] = false;
            }
        }

        for (int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                treeWeights[i][j] = 0.;
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                permutedDistances[i][j] = distances.getDistance(circularOrdering[i], circularOrdering[j]);
            }
        }

        for (int i = 0; i < this.getSplits().size(); i++) {

            SplitBlock sb = this.getSplitAt(i).getASide();

            int k = permutationInvert[sb.get(0)];
            int l = permutationInvert[sb.get(sb.size() - 1)];

            if (k == 0) {
                flag[n - 1][l] = true;
            } else {
                if ((l < n - 1) && (k > l)) {
                    flag[k - 1][l] = true;
                } else {
                    flag[l][k - 1] = true;
                }
            }
        }

        new CircularNNLS().tree_in_cycle_least_squares(permutedDistances, flag,
                n, treeWeights);

        return new TreeWeights(treeWeights);
    }

}
