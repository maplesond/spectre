package uk.ac.uea.cmp.phygen.core.ds.distance;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 01/05/13
 * Time: 01:19
 * To change this template use File | Settings | File Templates.
 */
public class MetricDistanceGenerator implements DistanceGenerator {

    @Override
    public DistanceMatrix generateDistances(final int n) {

        DistanceMatrix distanceMatrix = new DistanceMatrix(n, 0.5);

        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                if (i == j) {
                    distanceMatrix.setDistance(i, j, 0.0);
                }
                else {
                    double maxVal = 1.;
                    double minVal = 0.00001; // 0.0 only for (i == j)
                    double aDistance;

                    for (int k = 0; k < n; k++) {
                        if (k != i && k != j) {

                            double distIK = distanceMatrix.getDistance(i, k);
                            double distKJ = distanceMatrix.getDistance(k, j);

                            double pathLen = distIK + distKJ;
                            double localMin = Math.min(distIK, distKJ);
                            double localMax = Math.max(distIK, distKJ);

                            minVal = Math.max(minVal, localMax - localMin);
                            maxVal = Math.min(maxVal, pathLen);
                        }
                    }

                    aDistance = Math.round(
                            (Math.random() * (maxVal - minVal)
                                    + minVal) * 1.E5) / 1.E5;

                    distanceMatrix.setDistance(i, j, aDistance);
                    distanceMatrix.setDistance(j, i, aDistance);
                }
            }
        }
        return distanceMatrix;
    }
}
