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

package uk.ac.uea.cmp.spectre.core.co.nn;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Before;
import org.junit.Test;
import uk.ac.uea.cmp.spectre.core.co.CircularOrderingCreator;
import uk.ac.uea.cmp.spectre.core.ds.CircularOrdering;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.distance.FlexibleDistanceMatrix;
import uk.ac.uea.cmp.spectre.core.io.nexus.NexusWriter;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Please consult TestMaticies_NNet.pdf in the resources folder for details and diagrams of the distance mx and
 * associated circular orderings.
 *
 * Note that it is not possible at the moment to automatically test all circular orderings due to unpredictable differences in
 * the output.
 */
public class NeighborNetTest {

    private DistanceMatrix dist1;
    private DistanceMatrix dist2;
    private DistanceMatrix dist3;

    //private CircularOrderingCreator oldNN;
    private CircularOrderingCreator newNN;

    private static final CircularOrdering orderDist1 = new CircularOrdering(new String[]{"A","C","D","E","B"});
    private static final CircularOrdering orderDist2a = new CircularOrdering(new String[]{"C","E","D","B","A"});
    private static final CircularOrdering orderDist2b = new CircularOrdering(new String[]{"A","B","C","E","D"});
    private static final CircularOrdering orderDist3a = new CircularOrdering(new String[]{"A","C","D","E","F","B"});
    private static final CircularOrdering orderDist3b = new CircularOrdering(new String[]{"D","C","B","A","E","F"});


    @Before
    public void setup() {

        //this.oldNN = new NeighborNetOld();
        this.newNN = new NeighborNetImpl();

        this.dist1 = new FlexibleDistanceMatrix(new double[][]{
                {0, 2, 3, 4, 4},
                {2, 0, 3, 4, 4},
                {3, 3, 0, 3, 3},
                {4, 4, 3, 0, 2},
                {4, 4, 3, 2, 0}
        });


        this.dist2 = new FlexibleDistanceMatrix(new double[][]{
                {0, 4, 7, 7, 8},
                {4, 0, 7, 7, 8},
                {7, 7, 0, 6, 5},
                {7, 7, 6, 0, 5},
                {8, 8, 5, 5, 0}
        });


        this.dist3 = new FlexibleDistanceMatrix(new double[][]{
                {0, 2, 7, 7, 6, 6},
                {2, 0, 7, 7, 6, 6},
                {7, 7, 0, 2, 7, 7},
                {7, 7, 2, 0, 7, 7},
                {6, 6, 7, 7, 0, 2},
                {6, 6, 7, 7, 2, 0}
        });

    }

    private void test(DistanceMatrix dm, CircularOrdering correctResult) {
        this.test(dm, correctResult, null);
    }

    private void test(DistanceMatrix dm, CircularOrdering correctResult, CircularOrdering alternateResult) {

        //IdentifierList ssO = this.oldNN.createCircularOrdering(dm);
        IdentifierList ssN = this.newNN.createCircularOrdering(dm);

        //CircularOrdering orderedTaxaOld = new CircularOrdering(ssO);
        CircularOrdering orderedTaxaNew = new CircularOrdering(ssN);

        if (correctResult != null) {

            if (alternateResult != null) {
                //assertTrue(orderedTaxaOld.equals(correctResult) || orderedTaxaOld.equals(alternateResult));
                assertTrue(orderedTaxaNew.equals(correctResult) || orderedTaxaNew.equals(alternateResult));
            }
            else {
                //assertTrue(orderedTaxaOld.equals(correctResult));
                assertTrue(orderedTaxaNew.equals(correctResult));
            }
        }
        assertTrue(true);
    }

    @Test
    public void makeDist() throws IOException {

        new NexusWriter()
                .appendHeader()
                .appendLine()
                .append(new IdentifierList(5))
                .appendLine()
                .append(dist1)
                .write(new File("dist1.nex"));
    }

    @Test
    public void testDist1() {
        this.test(this.dist1, this.orderDist1);
    }


    @Test
    public void testDist2() {
        this.test(this.dist2, this.orderDist2a, this.orderDist2b);
    }


    @Test
    public void testDist3() {
        this.test(this.dist3, this.orderDist3a, this.orderDist3b);
    }

    /*@Test
    public void testRuntime() {

        DistanceMatrix dm = this.dist3;

        StopWatch sw1 = new StopWatch();
        long memStart1 = getMemoryUse();
        sw1.start();
        IdentifierList ssO = this.oldNN.createCircularOrdering(dm);
        sw1.stop();
        long memEnd1 = getMemoryUse();
        long mem1 = memEnd1 - memStart1;
        System.out.println("Old implementation: Runtime (s):" + sw1.toString() + "; Mem usage (bytes): " + mem1);

        StopWatch sw2 = new StopWatch();
        long memStart2 = getMemoryUse();
        sw2.start();
        IdentifierList ssN = this.newNN.createCircularOrdering(dm);
        sw2.stop();
        long memEnd2 = getMemoryUse();
        long mem2 = memEnd2 - memStart2;
        System.out.println("New implementation: Runtime (s):" + sw2.toString() + "; Mem usage (bytes): " + mem2);

    } */

    private long getMemoryUse(){
        putOutTheGarbage();
        long totalMemory = Runtime.getRuntime().totalMemory();
        putOutTheGarbage();
        long freeMemory = Runtime.getRuntime().freeMemory();
        return (totalMemory - freeMemory);
    }

    private void putOutTheGarbage() {
        collectGarbage();
        collectGarbage();
    }

    private static long fSLEEP_INTERVAL = 100;

    private void collectGarbage() {
        try {
            System.gc();
            Thread.currentThread().sleep(fSLEEP_INTERVAL);
            System.runFinalization();
            Thread.currentThread().sleep(fSLEEP_INTERVAL);
        }
        catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

}