/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.uea.cmp.phybre.core.ds.distance;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 01/05/13
 * Time: 01:19
 * To change this template use File | Settings | File Templates.
 */
public class MetricDistanceGenerator implements DistanceMatrixGenerator {

    @Override
    public DistanceMatrix generateDistances(final int n) {

        DistanceMatrix distanceMatrix = new FlexibleDistanceMatrix();

        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                if (i != j) {

                    double maxVal = 1.;
                    double minVal = 0.00001; // 0.0 only for (i == j)
                    double aDistance;

                    for (int k = 0; k < n; k++) {
                        if (k != i && k != j) {

                            double distIK = distanceMatrix.getDistance(i, k);
                            double distKJ = distanceMatrix.getDistance(k, j);

                            distIK = distIK == 0.0 ? 0.5 : distIK;
                            distKJ = distKJ == 0.0 ? 0.5 : distKJ;

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
                }
            }
        }
        return distanceMatrix;
    }
}
