package uk.ac.uea.cmp.phygen.core.ds.split;

import uk.ac.uea.cmp.phygen.core.alg.CircularNNLS;
import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;

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

    private CircularOrdering circularOrdering;
    private SplitWeights splitWeights;

    public CircularSplitSystem(List<Split> splits, CircularOrdering circularOrdering) {
        super(circularOrdering.size(), splits);
        this.circularOrdering = circularOrdering;
        this.splitWeights = null;
    }

    public CircularSplitSystem(DistanceMatrix distanceMatrix, CircularOrdering circularOrdering) {

        super(distanceMatrix.getTaxaSet(), new ArrayList<Split>());

        int n = circularOrdering.size();

        if (n != distanceMatrix.size()) {
            throw new IllegalArgumentException("Distance matrix and circular ordering are not the same size");
        }

        this.splitWeights = this.calculateSplitWeighting(distanceMatrix, circularOrdering);

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (splitWeights.hasWeightAt(j, i)) {

                    ArrayList<Integer> sb = new ArrayList<>();
                    for(int k = i + 1; k < j + 1; k++) {
                        sb.add(circularOrdering.getAt(k));
                    }

                    this.addSplit(new Split(new SplitBlock(sb), n, splitWeights.getAt(j, i)));
                }
            }
        }

        this.circularOrdering = circularOrdering;
    }

    public void setCircularOrdering(CircularOrdering circularOrdering) {
        this.circularOrdering = circularOrdering;
    }

    public CircularOrdering getCircularOrdering() {
        return circularOrdering;
    }

    protected void setSplitWeights(SplitWeights splitWeights) {
        this.splitWeights = splitWeights;
    }

    public SplitWeights getSplitWeights() {
        return this.splitWeights;
    }

    /**
     * Returns the value of the permutation at the specified position.
     *
     * @param i index of the value to be returned.
     * @return value of the permutation at i
     */
    @Override
    public int getTaxaIndexAt(final int i) {
        return this.circularOrdering.getAt(i) + 1;
    }




    /**
     * Generates a distance matrix based on the weights within this splitsystem.
     * @return
     */
    public DistanceMatrix generateDistanceMatrix() {

        final int t = this.getNbTaxa();

        DistanceMatrix distanceMatrix = new DistanceMatrix(t);

        for (int i = 0; i < t; i++) {
            for (int j = i + 1; j < t; j++) {
                for (int k = j; k < t; k++) {
                    for(int s = i; s < j; s++){
                        distanceMatrix.incrementDistance(i, j, this.splitWeights.getAt(k, s));
                    }
                }
                for (int k = i; k < j; k++){
                    for (int s = 0; s < i; s++) {
                        distanceMatrix.incrementDistance(i, j, this.splitWeights.getAt(k, s));
                    }
                }
            }
        }

        return distanceMatrix;
    }


    /**
     * Calculates the weights of this full circular split system.  The weight determines
     * which splits will be returned.
     * @return Split weights matrix
     */
    protected SplitWeights calculateSplitWeighting(DistanceMatrix distanceMatrix, CircularOrdering circularOrdering)
    {
        int n = distanceMatrix.size();
        double[][] permutedDistances = new double[n][n];
        double[][] splitWeights = new double[n][n];
        CircularNNLS cnnls = new CircularNNLS();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                permutedDistances[i][j] = distanceMatrix.getDistance(circularOrdering.getAt(i), circularOrdering.getAt(j));
            }
        }

        for (int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                splitWeights[i][j] = 0.;
            }
        }

        cnnls.circularLeastSquares(permutedDistances, n, splitWeights);

        return new SplitWeights(splitWeights);
    }


}
