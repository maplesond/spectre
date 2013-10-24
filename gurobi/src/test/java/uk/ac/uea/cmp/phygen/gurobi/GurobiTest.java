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
import uk.ac.uea.cmp.phygen.core.math.Equality;
import uk.ac.uea.cmp.phygen.core.math.optimise.*;
import uk.ac.uea.cmp.phygen.core.math.optimise.external.JOptimizer;
import uk.ac.uea.cmp.phygen.core.math.optimise.test.Problems;

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
     * @throws OptimiserException
     */
    @Test
    public void testMip1() throws OptimiserException {

        assumeTrue(gurobi != null);

        // Create the MIP1 Problem
        Problem problem = Problems.mixedIntegerLinear1();

        // Solve
        Solution solution = gurobi.optimise(problem);

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

        // Create the Problem
        Problem problem = Problems.simpleLinear();

        // Solve
        Solution solution = gurobi.optimise(problem);

        // Check result
        double[] vals = solution.getVariableValues();

        assertTrue(Equality.approxEquals(vals[0], 1.5));
        assertTrue(Equality.approxEquals(vals[1], 0.0));
        assertTrue(solution.getSolution() == 2.5);
    }

    /**
     * This example formulates and solves a simple linear problem
     *
     * @throws OptimiserException
     */
    @Test
    public void testMixedIntegerLinear2() throws OptimiserException {

        assumeTrue(gurobi != null);

        // Create the simple linear Problem
        Problem problem = Problems.mixedIntegerLinear2();

        // Solve
        Solution solution = gurobi.optimise(problem);

        // Check result
        double[] vals = solution.getVariableValues();
        assertTrue(vals[0] == 16.0);
        assertTrue(vals[1] == 59.0);
        assertTrue(solution.getSolution() == 5828.0);
    }

    /**
     * This example formulates and solves a simple quadratic problem
     *
     * @throws OptimiserException
     */
    @Test
    public void testSimpleQuadratic() throws OptimiserException {

        assumeTrue(gurobi != null);

        // Create the Problem
        Problem problem = Problems.simpleQuadratic();

        // Solve
        Solution solution = gurobi.optimise(problem);

        // Check result
        double[] vals = solution.getVariableValues();
        assertTrue(Equality.approxEquals(vals[0], 0.5));
        assertTrue(Equality.approxEquals(vals[1], 0.5));
        assertTrue(Equality.approxEquals(solution.getSolution(), 0.7));
    }

    /**
     * This example formulates and solves a simple quadratic problem
     *
     * @throws OptimiserException
     */
    @Test
    public void testSimpleQuadratic2() throws OptimiserException {

        assumeTrue(gurobi != null);

        // Create the Problem
        Problem problem = Problems.simpleQuadratic2();

        // Solve
        Solution solution = gurobi.optimise(problem);

        // Check result
        double[] vals = solution.getVariableValues();
        assertTrue(Equality.approxEquals(vals[0], 0.0));
        assertTrue(Equality.approxEquals(vals[1], 1.0));
        assertTrue(Equality.approxEquals(vals[2], 2.0/3.0));
        assertTrue(Equality.approxEquals(solution.getSolution(), 2.1111111));
    }
}
