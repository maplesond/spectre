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

package uk.ac.uea.cmp.spectre.qtools.superq;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.earlham.metaopt.Optimiser;
import uk.ac.earlham.metaopt.OptimiserException;
import uk.ac.earlham.metaopt.external.JOptimizer;

import java.io.File;

import static org.junit.Assert.assertFalse;

public class SuperQITCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File simpleOutput;

    @Before
    public void setUp() throws Exception {
        simpleOutput = folder.newFolder("simple");

        // Uncomment this line if you want to see output.
        BasicConfigurator.configure();
        LogManager.getRootLogger().setLevel(Level.INFO);
    }


    protected SuperQOptions createSimpleOptions(String inputResource, File output) throws OptimiserException {

        SuperQOptions options = new SuperQOptions(
                new File[]{FileUtils.toFile(SuperQITCase.class.getResource(inputResource))},
                output,
                null,
                null,
                null,
                null,
                null,
                false
        );

        return options;
    }

    @Test
    public void test7TaxaTree() throws OptimiserException {
        SuperQOptions options = this.createSimpleOptions(
                "/simple/7-taxa-deg2.tre",
                new File(simpleOutput, "7taxa-deg2.out"));

        SuperQ superQ = new SuperQ(options);

        superQ.run();

        if (superQ.failed()) {
            System.err.println(superQ.getFullErrorMessage());
        }

        assertFalse(superQ.failed());
    }

    @Test
    public void testSimpleTree() throws OptimiserException {
        SuperQOptions options = this.createSimpleOptions(
                "/simple/single-tree-1.tre",
                new File(simpleOutput, "newick-1.out"));

        SuperQ superQ = new SuperQ(options);

        superQ.run();

        if (superQ.failed()) {
            System.err.println(superQ.getFullErrorMessage());
        }

        assertFalse(superQ.failed());
    }

    @Test
    public void testTreeCollectionNewickInOneFile() throws OptimiserException {
        SuperQOptions options = this.createSimpleOptions(
                "/simple/2-trees-4-taxa-01.tre",
                new File(simpleOutput, "2-trees-4-taxa-01.out"));

        SuperQ superQ = new SuperQ(options);

        superQ.run();

        if (superQ.failed()) {
            System.err.println(superQ.getFullErrorMessage());
        }

        assertFalse(superQ.failed());
    }

    @Test
    public void testTreeCollectionNewickScript() throws OptimiserException {
        SuperQOptions options = this.createSimpleOptions(
                "/simple/in_02.script",
                new File(simpleOutput, "in_02.script.out"));
        SuperQ superQ = new SuperQ(options);

        superQ.run();

        if (superQ.failed()) {
            System.err.println(superQ.getFullErrorMessage());
        }

        assertFalse(superQ.failed());
    }

    //@Test
    public void testSimpleScript() throws OptimiserException {

        SuperQOptions options = this.createSimpleOptions(
                "/simple/in.script",
                new File(simpleOutput, "simple.out"));

        SuperQ superQ = new SuperQ(options);

        superQ.run();

        if (superQ.failed()) {
            System.err.println(superQ.getFullErrorMessage());
        }

        assertFalse(superQ.failed());
    }

    //@Test
    public void testArabidopsisScript() throws OptimiserException {

        SuperQOptions options = this.createSimpleOptions(
                "/arabidopsis/flowers.script",
                new File(simpleOutput, "arabidopsis.out"));

        SuperQ superQ = new SuperQ(options);

        superQ.run();

        if (superQ.failed()) {
            System.err.println(superQ.getFullErrorMessage());
        }

        assertFalse(superQ.failed());
    }

    //@Test
    public void testSPR32() throws OptimiserException {

        Optimiser opt = new JOptimizer();

        SuperQOptions options = new SuperQOptions();
        options.setInputFiles(new File[]{FileUtils.toFile(SuperQITCase.class.getResource("/simple/SPR_32.tre"))});
        options.setOutputFile(new File(simpleOutput, "SPR_32.out.nex"));
        options.setPrimarySolver(opt);
        options.setFilter(0.001);

        SuperQ superQ = new SuperQ(options);

        superQ.run();

        if (superQ.failed()) {
            System.err.println(superQ.getFullErrorMessage());
        }

        assertFalse(superQ.failed());
    }
}
