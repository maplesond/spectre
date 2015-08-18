/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2015  UEA School of Computing Sciences
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package uk.ac.uea.cmp.spectre.net.netmake.fits;

import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrixGenerator;
import uk.ac.uea.cmp.spectre.core.ds.distance.RandomDistanceGenerator;

public class EqMetricDistanceGenerator implements DistanceMatrixGenerator {

    @Override
    public DistanceMatrix generateDistances(final int n) {

        double distances[][] = new double[n][n];

        DistanceMatrix inputData = new RandomDistanceGenerator().generateDistances(n);

        /*NetMake nm = new NetMake();
        nm.getOptions().setWeighting1(new TSPWeighting(n));


        NetMakeResult netMakeResult = , null).execute(inputData);
        SplitSystem splits = netMakeResult.getTree();           */
        /*double[][] treeWeights = splits.calculateSplitWeighting();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (treeWeights[i][j] != 0.) {
                    treeWeights[i][j] = Math.round(Math.random() * 1.E5) / 1.E5;
                }
            }
        }

        distances = splitToDistance(treeWeights);

        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                distances[j][i] = distances[i][j];
            }
        }*/

        return null;
    }

}
