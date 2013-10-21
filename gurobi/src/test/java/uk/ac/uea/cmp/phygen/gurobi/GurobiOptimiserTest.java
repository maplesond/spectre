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

import org.junit.Test;
import uk.ac.uea.cmp.phygen.core.math.optimise.*;
import uk.ac.uea.cmp.phygen.core.math.optimise.apache.ApacheOptimiser;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 21/10/13
 * Time: 14:02
 * To change this template use File | Settings | File Templates.
 */
public class GurobiOptimiserTest {
    @Test
    public void testAcceptsIdentifier() throws OptimiserException {

        /*Optimiser gurobi = new GurobiOptimiser();

        assertTrue(gurobi.acceptsIdentifier("gurobi"));
        assertTrue(gurobi.acceptsIdentifier("Gurobi"));
        assertTrue(gurobi.acceptsIdentifier("GUROBI"));
        assertTrue(gurobi.acceptsIdentifier("uk.ac.uea.cmp.phygen.gurobi.GurobiOptimiser"));  */
    }


    @Test
    public void testSimpleProblem() throws OptimiserException {

        Problem problem = new Problem("simple", new ArrayList<Variable>(), new ArrayList<Constraint>(), null);

        //Solution solution = new GurobiOptimiser().optimise(problem);

        assertTrue(true);
    }


    /**
     * This example formulates and solves the following simple MIP model:
     *  maximize    x +   y + 2 z
     *  subject to  x + 2 y + 3 z <= 4
     *  x + y       >= 1
     *  x, y, z binary
     * @throws OptimiserException
     */
    @Test
    public void testMip1() throws OptimiserException {

        // Create the variables

        Variable x = new Variable("x", 0.0, new Bounds(0.0, 1.0, Bounds.BoundType.DOUBLE), Variable.VariableType.BINARY);
        Variable y = new Variable("y", 0.0, new Bounds(0.0, 1.0, Bounds.BoundType.DOUBLE), Variable.VariableType.BINARY);
        Variable z = new Variable("z", 0.0, new Bounds(0.0, 1.0, Bounds.BoundType.DOUBLE), Variable.VariableType.BINARY);

        List<Variable> variables = new ArrayList<>();
        variables.add(x);
        variables.add(y);
        variables.add(z);

        // Set objective: maximize x + y + 2 z
        Expression objExpr = new Expression().addTerm(1.0, x).addTerm(1.0, y).addTerm(2.0, z);
        Objective objective = new Objective("mip1", Objective.ObjectiveDirection.MAXIMISE, objExpr);

        // Setup constraints
        List<Constraint> constraints = new ArrayList<>();

        // Add constraint: x + 2 y + 3 z <= 4
        Expression c0 = new Expression().addTerm(1.0, x).addTerm(2.0, y).addTerm(3.0, z);
        constraints.add(new Constraint("c0", c0, Constraint.Relation.LTE, 4.0));

        // Add constraint: x + y >= 1
        Expression c1 = new Expression().addTerm(1.0, x).addTerm(1.0, y);
        constraints.add(new Constraint("c1", c1, Constraint.Relation.GTE, 1.0));

        // Create problem
        Problem problem = new Problem("mip1", variables, constraints, objective);

        // Solve
        //Solution solution = new GurobiOptimiser().optimise(problem);

        // Check result
        assertTrue(true);
    }
}
