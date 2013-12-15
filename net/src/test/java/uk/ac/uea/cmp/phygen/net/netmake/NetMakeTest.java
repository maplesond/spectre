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

package uk.ac.uea.cmp.phygen.net.netmake;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.junit.Before;
import org.junit.Test;
import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.phygen.net.netmake.weighting.GreedyMEWeighting;
import uk.ac.uea.cmp.phygen.net.netmake.weighting.TSPWeighting;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA. User: Dan Date: 12/05/13 Time: 23:27 To change this template use File | Settings | File
 * Templates.
 */
public class NetMakeTest {

    @Before
    public void setup() {

        BasicConfigurator.configure();
        LogManager.getRootLogger().setLevel(Level.WARN);
    }


    @Test
    public void simpleTest() {

        String[] taxa = new String[]{"1", "2", "3", "4", "5", "6", "7"};

        double[][] distances = new double[][]{
                {0, 2, 5, 5, 5, 5, 3},
                {2, 0, 5, 5, 5, 5, 3},
                {5, 5, 0, 2, 4, 4, 4},
                {5, 5, 2, 0, 4, 4, 4},
                {5, 5, 4, 4, 0, 2, 4},
                {5, 5, 4, 4, 2, 0, 4},
                {3, 3, 4, 4, 4, 4, 0}
        };


        DistanceMatrix distanceMatrix = new DistanceMatrix(taxa, distances);

        NetMake netMake = new NetMake(distanceMatrix, new GreedyMEWeighting(distanceMatrix), new TSPWeighting(distanceMatrix.size()));

        NetMakeResult result = netMake.runNN();

        assertTrue(true);
    }
}
