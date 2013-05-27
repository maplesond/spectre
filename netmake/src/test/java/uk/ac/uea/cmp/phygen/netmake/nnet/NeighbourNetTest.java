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

package uk.ac.uea.cmp.phygen.netmake.nnet;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.phygen.core.ds.split.CircularOrdering;
import uk.ac.uea.cmp.phygen.core.io.nexus.NexusReader;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA. User: Dan Date: 21/05/13 Time: 20:40 To change this template use File | Settings | File
 * Templates.
 */
public class NeighbourNetTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File testFile = FileUtils.toFile(NeighbourNetTest.class.getResource("/bees.nex"));
    private File test2File = FileUtils.toFile(NeighbourNetTest.class.getResource("/test2.nex"));

    @Test
    public void testNN() throws IOException {

        DistanceMatrix dm = new NexusReader().read(testFile);

        CircularOrdering ordering = NeighbourNet.computeNeighborNetOrdering(dm);

        assertTrue(true);
    }

    @Test
    public void testNN2() throws IOException {

        DistanceMatrix dm = new NexusReader().read(test2File);

        CircularOrdering ordering = NeighbourNet.computeNeighborNetOrdering(dm);

        assertTrue(true);
    }
}
