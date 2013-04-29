package uk.ac.uea.cmp.phygen.core.ds.split;

import uk.ac.uea.cmp.phygen.core.alg.CircularNNLS;
import uk.ac.uea.cmp.phygen.core.ds.DistanceMatrix;

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

    public CircularSplitSystem(DistanceMatrix distanceMatrix, int[] circularOrdering) {

        super(distanceMatrix.getTaxaSet(), new ArrayList<Split>());

        if (circularOrdering.length != distanceMatrix.size()) {
            throw new IllegalArgumentException("Distance matrix and circular ordering are not the same size");
        }

        double[][] splitWeights = this.calculateSplitWeighting(distanceMatrix, circularOrdering);

        int n = circularOrdering.length;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (splitWeights[j][i] != 0.0) {

                    ArrayList<Integer> sb = new ArrayList<>();
                    for(int k = i + 1; k < j + 1; k++) {
                        sb.add(circularOrdering[k]);
                    }

                    this.addSplit(new Split(new SplitBlock(sb), circularOrdering.length, splitWeights[j][i]));
                }
            }
        }

        this.circularOrdering = circularOrdering;
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
    protected double[][] calculateSplitWeighting(DistanceMatrix distanceMatrix, int[] circularOrdering)
    {
        int n = distanceMatrix.size();
        double[][] permutedDistances = new double[n][n];
        double[][] splitWeights = new double[n][n];
        CircularNNLS cnnls = new CircularNNLS();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                permutedDistances[i][j] = distanceMatrix.getDistance(circularOrdering[i], circularOrdering[j]);
            }
        }

        for (int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                splitWeights[i][j] = 0.;
            }
        }

        cnnls.circularLeastSquares(permutedDistances, n, splitWeights);

        return splitWeights;
    }


}
