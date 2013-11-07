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
package uk.ac.uea.cmp.phygen.core.io.nexus;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 28/04/13
 * Time: 20:40
 * To change this template use File | Settings | File Templates.
 */
public class NexusReaderTest {

    @Test
    public void testNexusReader() throws IOException {

        File testFile = FileUtils.toFile(NexusReaderTest.class.getResource("/bees.nex"));

        DistanceMatrix distanceMatrix = new NexusReader().readDistanceMatrix(testFile);

        assertTrue(distanceMatrix.size() == 6);
    }

    @Test
    public void testTriangularNexusReader() throws IOException {

        File testFile = FileUtils.toFile(NexusReaderTest.class.getResource("/triangular.nex"));

        DistanceMatrix distanceMatrix = new NexusReader().readDistanceMatrix(testFile);

        assertTrue(distanceMatrix.size() == 5);
    }
}
