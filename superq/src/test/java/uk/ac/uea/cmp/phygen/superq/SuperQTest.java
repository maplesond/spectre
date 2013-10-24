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

package uk.ac.uea.cmp.phygen.superq;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.uea.cmp.phygen.core.math.optimise.Objective;
import uk.ac.uea.cmp.phygen.core.math.optimise.OptimiserException;
import uk.ac.uea.cmp.phygen.core.math.optimise.OptimiserFactory;
import uk.ac.uea.cmp.phygen.core.math.optimise.external.JOptimizer;

import java.io.File;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 24/10/13
 * Time: 23:54
 * To change this template use File | Settings | File Templates.
 */
public class SuperQTest {

    @Rule
    public TemporaryFolder folder= new TemporaryFolder();

    private File simpleOutput;

    @Before
    public void setUp() throws Exception {
        simpleOutput = folder.newFolder("simple");
    }

    protected SuperQOptions createSimpleOptions() throws OptimiserException {

        SuperQOptions options = new SuperQOptions(
                FileUtils.toFile(SuperQTest.class.getResource("/simple/in.script")),
                SuperQOptions.InputFormat.SCRIPT,
                new File(simpleOutput, "simple.out"),
                null,
                null,
                null,
                false,
                0.0,
                false
        );

        return options;
    }

    @Test
    public void testSimpleScript() throws OptimiserException {

        SuperQOptions options = this.createSimpleOptions();

        SuperQ superQ = new SuperQ(options);

        superQ.run();

        assertFalse(superQ.failed());
    }
}
