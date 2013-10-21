package uk.ac.uea.cmp.phygen.core.math.optimise.apache;

import org.junit.Test;
import uk.ac.uea.cmp.phygen.core.math.optimise.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 14/09/13
 * Time: 21:53
 * To change this template use File | Settings | File Templates.
 */
public class ApacheOptimiserTest {

    @Test
    public void testAcceptsIdentifier() throws OptimiserException {

        Optimiser apache = new ApacheOptimiser();

        assertTrue(apache.acceptsIdentifier("apache"));
        assertTrue(apache.acceptsIdentifier("Apache"));
        assertTrue(apache.acceptsIdentifier("APACHE"));
        assertTrue(apache.acceptsIdentifier("uk.ac.uea.cmp.phygen.core.math.optimise.apache.ApacheOptimiser"));
    }


    @Test
    public void testSimpleProblem() throws OptimiserException {

        double[] nonNegativityConstraint = new double[0];
        double[][] solutionSpaceConstraint = new double[0][0];

        Problem problem = new Problem(new ArrayList<Variable>(), new ArrayList<Constraint>(), null, nonNegativityConstraint, solutionSpaceConstraint);

        double[] solution = new ApacheOptimiser().optimise(problem);

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
        LinearExpression objExpr = new LinearExpression().addTerm(1.0, x).addTerm(1.0, y).addTerm(2.0, z);
        Objective objective = new LinearObjective(Objective.ObjectiveDirection.MAXIMISE, objExpr);

        // Setup constraints
        List<Constraint> constraints = new ArrayList<>();

        // Add constraint: x + 2 y + 3 z <= 4
        LinearExpression c0 = new LinearExpression().addTerm(1.0, x).addTerm(2.0, y).addTerm(3.0, z);
        constraints.add(new Constraint("c0", c0, Constraint.Relation.LTE, 4.0));

        // Add constraint: x + y >= 1
        LinearExpression c1 = new LinearExpression().addTerm(1.0, x).addTerm(1.0, y);
        constraints.add(new Constraint("c1", c1, Constraint.Relation.GTE, 1.0));

        // Create problem
        Problem problem = new Problem(variables, constraints, objective);

        // Solve
        double[] solution = new ApacheOptimiser().optimise(problem);

        // Check result
        assertTrue(true);
    }
}
