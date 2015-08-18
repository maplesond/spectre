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

package uk.ac.uea.cmp.spectre.tools.convertor;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static junit.framework.TestCase.assertTrue;

public class ConvertorTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void testNexusToPhylip() throws IOException {

        File inputFile = FileUtils.toFile(ConvertorTest.class.getResource("/bees.nex"));
        File outputFile = temp.newFile();

        new DistanceMatrixConvertor().execute(inputFile, outputFile);

        assertTrue(true);
    }

    @Test
    public void testPhylipToNexus() throws IOException {

        File inputFile = FileUtils.toFile(ConvertorTest.class.getResource("/colors.phy"));
        File outputFile = temp.newFile();

        new DistanceMatrixConvertor().execute(inputFile, outputFile);

        assertTrue(true);
    }

}
