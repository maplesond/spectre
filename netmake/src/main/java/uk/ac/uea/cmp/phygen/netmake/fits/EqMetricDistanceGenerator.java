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

package uk.ac.uea.cmp.phygen.netmake.fits;

import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceGenerator;
import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.phygen.core.ds.distance.RandomDistanceGenerator;
import uk.ac.uea.cmp.phygen.core.ds.split.CompatibleSplitSystem;
import uk.ac.uea.cmp.phygen.netmake.NetMake;
import uk.ac.uea.cmp.phygen.netmake.weighting.TSPWeighting;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 01/05/13
 * Time: 01:19
 * To change this template use File | Settings | File Templates.
 */
public class EqMetricDistanceGenerator implements DistanceGenerator {

    @Override
    public DistanceMatrix generateDistances(final int n) {

        double distances[][] = new double[n][n];

        DistanceMatrix inputData = new RandomDistanceGenerator().generateDistances(n);

        NetMake netMake = new NetMake(inputData, new TSPWeighting(n));
        netMake.process();
        CompatibleSplitSystem splits = netMake.getTree();
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
