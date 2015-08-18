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
package uk.ac.uea.cmp.spectre.core.io.phylip;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 28/04/13
 * Time: 20:37
 * To change this template use File | Settings | File Templates.
 */
public class PhylipReaderTest {

    @Test
    public void testPhylipReader() throws IOException {

        File testFile = FileUtils.toFile(PhylipReaderTest.class.getResource("/colors.phy"));

        DistanceMatrix distanceMatrix = new PhylipReader().readDistanceMatrix(testFile);

        assertTrue(distanceMatrix.size() == 10);
    }

    @Test
    public void testMultiLine() throws IOException {

        File testFile = FileUtils.toFile(PhylipReaderTest.class.getResource("/multi-line.phy"));

        DistanceMatrix distanceMatrix = new PhylipReader().readDistanceMatrix(testFile);

        assertTrue(distanceMatrix.size() == 25);
    }
}
