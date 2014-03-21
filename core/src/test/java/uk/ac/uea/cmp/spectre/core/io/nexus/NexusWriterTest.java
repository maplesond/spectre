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
package uk.ac.uea.cmp.spectre.core.io.nexus;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.uea.cmp.spectre.core.ds.distance.FlexibleDistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.split.SpectreSplitSystem;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitSystem;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: Sarah_2
 * Date: 24/04/13
 * Time: 21:24
 * To change this template use File | Settings | File Templates.
 */
public class NexusWriterTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();


    @Test
    public void testWriteNetwork() throws IOException {

        File outputDir = temp.newFolder("networkTest");
        File outputFile = new File(outputDir, "network.nex");

        final int size = 5;

        SplitSystem ss = new SpectreSplitSystem(new FlexibleDistanceMatrix(size));

        new NexusWriter().writeSplitSystem(outputFile, ss);

        // Check output file was created
        assertTrue(outputFile.exists());

        List<String> lines = FileUtils.readLines(outputFile);

        // Check we have the number of lines we were expecting
        assertTrue(lines.size() == 21);
    }

    @Test
    public void testWriteTree() throws IOException {

        File outputDir = temp.newFolder("treeTest");
        File outputFile = new File(outputDir, "tree.nex");

        final int size = 5;

        SplitSystem ss = new SpectreSplitSystem(new FlexibleDistanceMatrix(size));


        /*new NexusWriter().writeTree(outputFile, ss, ss.calculateTreeWeighting(distanceMatrix));

        // Check output file was created
        assertTrue(outputFile.exists());

        List<String> lines = FileUtils.readLines(outputFile);

        // Check we have the number of lines we were expecting
        assertTrue(lines.size() == 32);  */

    }
}
