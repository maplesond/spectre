/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2014  UEA School of Computing Sciences
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
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

package uk.ac.uea.cmp.spectre.core.alg.nn;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.uea.cmp.spectre.core.ds.distance.FlexibleDistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitSystem;

import static junit.framework.Assert.assertTrue;

public class NeighborNetOldTest {

    private String[] taxa;
    private double[][] distances1;
    private double[][] distances2;

    @Before
    public void setup() {

        this.taxa = new String[]{"A", "B", "C", "D", "E"};

        this.distances1 = new double[][]{
                {0, 3, 6, 6, 6},
                {3, 0, 6, 6, 6},
                {6, 6, 0, 3, 9},
                {6, 6, 3, 0, 9},
                {6, 6, 9, 9, 0}
        };

        this.distances2 = new double[][]{
                {0, 3, 2, 5, 7},
                {3, 0, 6, 6, 6},
                {2, 6, 0, 3, 8},
                {5, 6, 3, 0, 9},
                {7, 6, 8, 9, 0}
        };
    }

    @Test
    public void testExecuteDist1() {

        SplitSystem ss = new NeighborNetOld().execute(new FlexibleDistanceMatrix(distances1), new NeighborNetParams(0.3, 0.3));

        assertTrue(ss.getOrderedTaxa().toString().equalsIgnoreCase("[A,C,D,E,B]"));
        assertTrue(true);
    }

    //@Test
    public void testExecuteDist2() {

        SplitSystem ss = new NeighborNetImpl().execute(new FlexibleDistanceMatrix(distances2), new NeighborNetParams(0.3, 0.3));

        String orderedTaxa = ss.getOrderedTaxa().toString();

        Assert.assertTrue(orderedTaxa.equalsIgnoreCase("[A,C,D,E,B]"));
        Assert.assertTrue(true);
    }


}