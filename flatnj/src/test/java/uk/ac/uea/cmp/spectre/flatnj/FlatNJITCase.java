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

package uk.ac.uea.cmp.spectre.flatnj;

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
import java.io.IOException;

import static org.junit.Assert.*;

public class FlatNJITCase {

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


    @Test
    public void testFourPoints() throws OptimiserException, IOException {
        
        FlatNJ flatnj = new FlatNJ(
            FileUtils.toFile(FlatNJITCase.class.getResource("/tw_fourpoint.faa")),
            new File(simpleOutput, "test.nex"),
            new JOptimizer());

        flatnj.execute();
        
        
    }

}
