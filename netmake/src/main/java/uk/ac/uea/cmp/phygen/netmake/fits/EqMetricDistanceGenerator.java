package uk.ac.uea.cmp.phygen.netmake.fits;

import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceGenerator;
import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.phygen.core.ds.distance.RandomDistanceGenerator;
import uk.ac.uea.cmp.phygen.core.ds.split.CircularSplitSystem;
import uk.ac.uea.cmp.phygen.core.ds.split.CompatibleSplitSystem;
import uk.ac.uea.cmp.phygen.core.ds.split.SplitSystem;
import uk.ac.uea.cmp.phygen.netmake.NetMake;
import uk.ac.uea.cmp.phygen.netmake.weighting.TSPWeighting;
import uk.ac.uea.cmp.phygen.netmake.weighting.Weightings;

import java.io.File;

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
