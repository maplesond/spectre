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

package uk.ac.uea.cmp.phygen.core.io.phylip;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.phygen.core.ds.distance.RandomDistanceGenerator;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA. User: Dan Date: 14/05/13 Time: 21:10 To change this template use File | Settings | File
 * Templates.
 */
public class PhylipWriterTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void testSaveDistanceMatrix() throws IOException {

        File outputDir = temp.newFolder("dmTest");

        File outputFile = new File(outputDir, "rdm10.phy");

        DistanceMatrix rdm = new RandomDistanceGenerator().generateDistances(10);

        new PhylipWriter().writeDistanceMatrix(outputFile, rdm);

        List<String> lines = FileUtils.readLines(outputFile);

        assertTrue(lines.size() == 11);
        assertTrue(lines.get(1).split(" ").length == 11);
    }
}
