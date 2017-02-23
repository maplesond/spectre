/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2017  UEA School of Computing Sciences
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

package uk.ac.uea.cmp.spectre.qtools.qmaker;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.earlham.metaopt.OptimiserException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class QMakerITCase {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void sevenTaxa() throws IOException, OptimiserException {

        File treeFile = FileUtils.toFile(QMakerTest.class.getResource("/simple/7-taxa.tre"));
        File outputDir = temp.newFolder();

        QMaker qMaker = new QMaker();
        qMaker.execute(new File[]{treeFile}, null, outputDir, "simpleTest");

        assertTrue(qMaker.getQuartetFile().exists());

        List<String> lines = FileUtils.readLines(qMaker.getQuartetFile());

        assertTrue(lines.size() == 45);
    }

    @Test
    public void sevenTaxaDeg2() throws IOException, OptimiserException {

        File treeFile = FileUtils.toFile(QMakerTest.class.getResource("/simple/7-taxa-deg2.tre"));
        File outputDir = temp.newFolder();

        QMaker chopper = new QMaker();
        chopper.execute(new File[]{treeFile}, null, outputDir, "simpleTest");

        assertTrue(chopper.getQuartetFile().exists());

        List<String> lines = FileUtils.readLines(chopper.getQuartetFile());

        assertTrue(lines.size() == 45);
    }

    @Test
    public void singleTreeScript() throws IOException, OptimiserException {

        File treeFile = FileUtils.toFile(QMakerTest.class.getResource("/simple/in.script"));
        File outputDir = temp.newFolder();

        QMaker chopper = new QMaker();
        chopper.execute(new File[]{treeFile}, null, outputDir, "singleTreeScript");

        assertTrue(chopper.getQuartetFile().exists());

        List<String> lines = FileUtils.readLines(chopper.getQuartetFile());

        assertTrue(lines.size() == 31717);
    }
}
