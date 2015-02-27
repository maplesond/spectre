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

package uk.ac.uea.cmp.spectre.net.netme;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.junit.Before;
import org.junit.Test;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.distance.FlexibleDistanceMatrix;

public class NetMETest {

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


        DistanceMatrix distanceMatrix = new FlexibleDistanceMatrix(new IdentifierList(taxa), distances);

        IdentifierList circularOrdering = new IdentifierList(new int[]{7, 2, 1, 3, 4, 5, 6});

        NetMEResult result = new NetME().execute(distanceMatrix, circularOrdering);

        /*assertTrue(result.getMeTree().getNbSplits() == 11);

        for(Split s : result.getMeTree().getSplits()) {
            assertTrue(Equality.approxEquals(s.getWeight(), 1.0, 0.01));
        }

        assertTrue(result.getOriginalMETree().getNbSplits() == 11);

        for(Split s : result.getOriginalMETree().getSplits()) {
            assertTrue(Equality.approxEquals(s.getWeight(), 1.0, 0.000001));
        }

        assertTrue(true); */
    }
}
