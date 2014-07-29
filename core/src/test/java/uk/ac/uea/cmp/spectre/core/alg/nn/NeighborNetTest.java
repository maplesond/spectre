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

import org.junit.Before;
import org.junit.Test;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.distance.FlexibleDistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitSystem;

import static org.junit.Assert.assertTrue;

public class NeighborNetTest {

    private DistanceMatrix dist1;
    private DistanceMatrix dist2;
    private DistanceMatrix dist3;

    private NeighborNet oldNN;
    private NeighborNet newNN;

    private NeighborNetParams params;

    private static final String orderDist1 = "[A,C,D,E,B]";
    private static final String orderDist2 = "[A,B,C,D,E]";
    private static final String orderDist3 = "[A,B,C,D,E]";

    @Before
    public void setup() {

        this.oldNN = new NeighborNetOld();
        this.newNN = new NeighborNetImpl();

        this.params = new NeighborNetParams(0.3, 0.3);

        this.dist1 = new FlexibleDistanceMatrix(new double[][]{
                {0, 3, 2, 5, 7},
                {3, 0, 6, 6, 6},
                {2, 6, 0, 3, 8},
                {5, 6, 3, 0, 9},
                {7, 6, 8, 9, 0}
        });

        this.dist2 = new FlexibleDistanceMatrix(new double[][]{
                {0, 2, 4, 5, 4},
                {2, 0, 5, 5, 4},
                {4, 5, 0, 3, 4},
                {5, 5, 3, 0, 3},
                {4, 4, 4, 3, 0}
        });

        this.dist3 = new FlexibleDistanceMatrix(new double[][]{
                {0, 4, 7, 7, 8},
                {4, 0, 7, 7, 8},
                {7, 7, 0, 6, 5},
                {7, 7, 6, 0, 5},
                {8, 8, 5, 5, 0}
        });

    }

    @Test
    public void testOldDist1() {

        SplitSystem ss = this.oldNN.execute(this.dist1, this.params);

        String orderedTaxa = ss.getOrderedTaxa().toString();

        assertTrue(orderedTaxa.equalsIgnoreCase(orderDist1));
        assertTrue(true);
    }

    //@Test
    public void testNewDist1() {

        SplitSystem ss = this.newNN.execute(this.dist1, this.params);

        String orderedTaxa = ss.getOrderedTaxa().toString();

        assertTrue(orderedTaxa.equalsIgnoreCase(orderDist1));
        assertTrue(true);
    }

    //@Test
    public void testOldDist2() {

        SplitSystem ss = this.oldNN.execute(this.dist2, this.params);

        String orderedTaxa = ss.getOrderedTaxa().toString();

        assertTrue(orderedTaxa.equalsIgnoreCase(orderDist2));
        assertTrue(true);
    }

    //@Test
    public void testNewDist2() {

        SplitSystem ss = this.newNN.execute(this.dist2, this.params);

        String orderedTaxa = ss.getOrderedTaxa().toString();

        assertTrue(orderedTaxa.equalsIgnoreCase(orderDist2));
        assertTrue(true);
    }

    //@Test
    public void testDist3() {

        SplitSystem ssO = this.oldNN.execute(this.dist2, this.params);
        SplitSystem ssN = this.newNN.execute(this.dist3, this.params);

        String orderedTaxaOld = ssO.getOrderedTaxa().toString();
        String orderedTaxaNew = ssN.getOrderedTaxa().toString();

        assertTrue(orderedTaxaOld.equalsIgnoreCase(orderDist3));
        assertTrue(orderedTaxaNew.equalsIgnoreCase(orderDist3));
        assertTrue(true);
    }

}