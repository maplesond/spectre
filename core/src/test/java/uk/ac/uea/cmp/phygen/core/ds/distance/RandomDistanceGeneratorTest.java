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

package uk.ac.uea.cmp.phygen.core.ds.distance;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA. User: Dan Date: 14/05/13 Time: 20:00 To change this template use File | Settings | File
 * Templates.
 */
public class RandomDistanceGeneratorTest {

    @Test
    public void create3Taxa() {
        testNTaxa(3);
    }

    /*@Test
    public void create500Taxa() {
        testNTaxa(500);
    }*/

    protected void testNTaxa(final int n) {

        DistanceMatrixGenerator distanceGenerator = new RandomDistanceGenerator();

        DistanceMatrix distanceMatrix = distanceGenerator.generateDistances(n);

        assertTrue(distanceMatrix != null);
        assertTrue(distanceMatrix.size() == n);
        assertTrue(distanceMatrix.getRow(0).length == n);
    }
}
