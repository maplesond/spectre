/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
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

package uk.ac.uea.cmp.phygen.tools.chopper;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.uea.cmp.phygen.core.math.optimise.OptimiserException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 28/10/13
 * Time: 22:48
 * To change this template use File | Settings | File Templates.
 */
public class ChopperTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();


    @Test
    public void sevenTaxa() throws IOException, OptimiserException {

        File treeFile = FileUtils.toFile(ChopperTest.class.getResource("/chopper/7-taxa.tre"));
        File outputDir = temp.newFolder();

        Chopper chopper = new Chopper();
        chopper.execute(treeFile, "newick", null, outputDir, "simpleTest");

        assertTrue(chopper.getQuartetFile().exists());

        List<String> lines = FileUtils.readLines(chopper.getQuartetFile());

        assertTrue(lines.size() == 45);
    }

    @Test
    public void sevenTaxaDeg2() throws IOException, OptimiserException {

        File treeFile = FileUtils.toFile(ChopperTest.class.getResource("/chopper/7-taxa-deg2.tre"));
        File outputDir = temp.newFolder();

        Chopper chopper = new Chopper();
        chopper.execute(treeFile, "newick", null, outputDir, "simpleTest");

        assertTrue(chopper.getQuartetFile().exists());

        List<String> lines = FileUtils.readLines(chopper.getQuartetFile());

        assertTrue(lines.size() == 45);
    }

    @Test
    public void singleTreeScript() throws IOException, OptimiserException {

        File treeFile = FileUtils.toFile(ChopperTest.class.getResource("/chopper/in.script"));
        File outputDir = temp.newFolder();

        Chopper chopper = new Chopper();
        chopper.execute(treeFile, "script", null, outputDir, "singleTreeScript");

        assertTrue(chopper.getQuartetFile().exists());

        List<String> lines = FileUtils.readLines(chopper.getQuartetFile());

        assertTrue(lines.size() == 35995);
    }
}
