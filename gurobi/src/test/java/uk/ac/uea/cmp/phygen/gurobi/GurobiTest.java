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

package uk.ac.uea.cmp.phygen.gurobi;

import org.junit.Before;
import org.junit.Test;
import uk.ac.uea.cmp.phygen.core.math.optimise.*;
import uk.ac.uea.cmp.phygen.core.math.optimise.external.JOptimizer;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 21/10/13
 * Time: 14:02
 * To change this template use File | Settings | File Templates.
 */
public class GurobiTest {


    private Optimiser gurobi = null;

    @Before
    public void setup() {

        boolean success = true;
        try {
            gurobi = OptimiserFactory.getInstance().createOptimiserInstance("gurobi", Objective.ObjectiveType.QUADRATIC);
        }
        catch (OptimiserException oe) {
            success = false;
        }

        if (!success || gurobi == null) {
            System.err.println("Gurobi not configured for you system... skipping Gurobi tests");
        }

        assumeTrue(success);
    }


    @Test
    public void testAcceptsIdentifier() throws OptimiserException {

        assumeTrue(gurobi != null);

        assertTrue(gurobi.acceptsIdentifier("gurobi"));
        assertTrue(gurobi.acceptsIdentifier("Gurobi"));
        assertTrue(gurobi.acceptsIdentifier("GUROBI"));
        assertTrue(gurobi.acceptsIdentifier("uk.ac.uea.cmp.phygen.gurobi.Gurobi"));
    }


    @Test(expected=OptimiserException.class)
    public void testSimple() throws OptimiserException {

        assumeTrue(gurobi != null);

        Problem problem = Problems.empty();

        Solution solution = gurobi.optimise(problem);

        assertTrue(solution.getSolution() == 0.0);
        assertTrue(solution.getVariableValues().length == 0);
    }

    /**
     * This example formulates and solves the following empty MIP model:
     *  maximize    x +   y + 2 z
     *  subject to  x + 2 y + 3 z <= 4
     *  x + y       >= 1
     *  x, y, z binary
     *
     *  We don't expect this to work because JOptimizer cannot maximise its objectives
     *
     * @throws OptimiserException
     */
    @Test(expected=UnsupportedOperationException.class)
    public void testMip1() throws OptimiserException {

        assumeTrue(gurobi != null);

        // Create the MIP1 Problem
        Problem problem = Problems.mip1();

        // Solve
        Solution solution = new JOptimizer().optimise(problem);

        // Check result
        assertTrue(solution.getSolution() == 3.0);

        double[] vals = solution.getVariableValues();

        assertTrue(vals[0] == 1.0);
        assertTrue(vals[1] == 0.0);
        assertTrue(vals[2] == 1.0);
    }

    /**
     * This example formulates and solves a simple linear problem
     *
     * @throws OptimiserException
     */
    @Test
    public void testSimpleLinear() throws OptimiserException {

        assumeTrue(gurobi != null);

        // Create the MIP1 Problem
        Problem problem = Problems.simpleLinear();

        // Solve
        Solution solution = new JOptimizer().optimise(problem);

        // Check result
        double[] vals = solution.getVariableValues();
        assertTrue(OptimiserTestUtils.approxEquals(vals[0], 1.5));
        assertTrue(OptimiserTestUtils.approxEquals(vals[1], 0.0));
    }

    /**
     * This example formulates and solves a simple quadratic problem
     *
     * @throws OptimiserException
     */
    @Test
    public void testSimpleQuadratic() throws OptimiserException {

        assumeTrue(gurobi != null);

        // Create the MIP1 Problem
        Problem problem = Problems.simpleQuadratic();

        // Solve
        Solution solution = new JOptimizer().optimise(problem);

        // Check result
        double[] vals = solution.getVariableValues();
        assertTrue(OptimiserTestUtils.approxEquals(vals[0], 0.5));
        assertTrue(OptimiserTestUtils.approxEquals(vals[1], 0.5));
    }
}
