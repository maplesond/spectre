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
    public void simpleTest() throws IOException {

        File treeFile = FileUtils.toFile(ChopperTest.class.getResource("/chopper/7-taxa.tre"));
        File quartetFile = temp.newFile();

        new Chopper().execute(treeFile, quartetFile, LoaderType.NEWICK);

        assertTrue(quartetFile.exists());

        List<String> lines = FileUtils.readLines(quartetFile);

        assertTrue(lines.size() == 45);
    }
}
