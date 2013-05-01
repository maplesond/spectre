package uk.ac.uea.cmp.phygen.core.ds.distance;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 01/05/13
 * Time: 01:18
 * To change this template use File | Settings | File Templates.
 */
public class RandomDistanceGenerator implements DistanceGenerator {

    @Override
    public DistanceMatrix generateDistances(final int n) {

        DistanceMatrix distanceMatrix = new DistanceMatrix(n);

        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                if (i == j) {
                    distanceMatrix.setDistance(i, j, 0.0);
                } else {
                    double aDistance = Math.round(Math.random() * 1.E5) / 1.E5;
                    distanceMatrix.setDistance(i, j, aDistance);
                    distanceMatrix.setDistance(j, i, aDistance);
                }
            }
        }

        return distanceMatrix;
    }
}
