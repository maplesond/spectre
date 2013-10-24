

package uk.ac.uea.cmp.phygen.core.math.optimise.test;

import uk.ac.uea.cmp.phygen.core.math.optimise.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: maplesod
 * Date: 24/10/13
 * Time: 10:51
 * To change this template use File | Settings | File Templates.
 */
public class Problems {

    public static Problem empty() {
        return new Problem("empty", new ArrayList<Variable>(), new ArrayList<Constraint>(),
                new Objective("z", Objective.ObjectiveDirection.MINIMISE, new Expression()));
    }


    /**
     * This example formulates the following empty MIP model:
     *  maximize    x +   y + 2 z
     *  subject to  x + 2 y + 3 z <= 4
     *  x + y       >= 1
     *  x, y, z binary
     * @throws OptimiserException
     */
    public static Problem mixedIntegerLinear1() {

        // Create the variables
        Variable x = new Variable("x", new Bounds(0.0, 1.0, Bounds.BoundType.DOUBLE), Variable.VariableType.BINARY);
        Variable y = new Variable("y", new Bounds(0.0, 1.0, Bounds.BoundType.DOUBLE), Variable.VariableType.BINARY);
        Variable z = new Variable("z", new Bounds(0.0, 1.0, Bounds.BoundType.DOUBLE), Variable.VariableType.BINARY);

        List<Variable> variables = new ArrayList<>();
        variables.add(x);
        variables.add(y);
        variables.add(z);

        // Set objective: maximize x + y + 2 z
        Expression objExpr = new Expression().addTerm(1.0, x).addTerm(1.0, y).addTerm(2.0, z);
        Objective objective = new Objective("max_x+y+2z", Objective.ObjectiveDirection.MAXIMISE, objExpr);

        // Setup constraints
        List<Constraint> constraints = new ArrayList<>();

        // Add constraint: x + 2 y + 3 z <= 4
        Expression c0 = new Expression().addTerm(1.0, x).addTerm(2.0, y).addTerm(3.0, z);
        constraints.add(new Constraint("c0", c0, Constraint.Relation.LTE, 4.0));

        // Add constraint: x + y >= 1
        Expression c1 = new Expression().addTerm(1.0, x).addTerm(1.0, y);
        constraints.add(new Constraint("c1", c1, Constraint.Relation.GTE, 1.0));

        // Create and return the problem
        return new Problem("mip1", variables, constraints, objective);
    }

    public static Problem simpleLinear() {

        // Create the variables
        Variable x = new Variable("x", new Bounds(-2.0, 2.0, Bounds.BoundType.FREE), Variable.VariableType.CONTINUOUS);
        Variable y = new Variable("y", new Bounds(-2.0, 2.0, Bounds.BoundType.FREE), Variable.VariableType.CONTINUOUS);

        List<Variable> variables = new ArrayList<>();
        variables.add(x);
        variables.add(y);

        // Set objective: minimize -x - y + 4
        Expression objExpr = new Expression().addTerm(-1.0, x).addTerm(-1.0, y).addConstant(4.0);
        Objective objective = new Objective("min_-x-y+4", Objective.ObjectiveDirection.MINIMISE, objExpr);

        // Setup constraints
        List<Constraint> constraints = new ArrayList<>();

        // Add constraint: 4/3x - y >= -2
        Expression c0 = new Expression().addTerm(4.0/3.0, x).addTerm(-1.0, y);
        constraints.add(new Constraint("c0", c0, Constraint.Relation.LTE, 2.0));

        // Add constraint: -1/2x + y >= -0.5
        Expression c1 = new Expression().addTerm(-0.5, x).addTerm(1.0, y);
        constraints.add(new Constraint("c1", c1, Constraint.Relation.LTE, 0.5));

        // Add constraint: -2x - y >= -2
        Expression c2 = new Expression().addTerm(-2.0, x).addTerm(-1.0, y);
        constraints.add(new Constraint("c2", c2, Constraint.Relation.LTE, 2.0));

        // Add constraint: -2x - y >= -2
        Expression c3 = new Expression().addTerm(1.0/3.0, x).addTerm(1.0, y);
        constraints.add(new Constraint("c3", c3, Constraint.Relation.LTE, 0.5));


        // Create and return the problem
        return new Problem("simple_linear", variables, constraints, objective);
    }

    /**
     * Constructing a Problem:
     * Maximize: 143x+60y
     * Subject to:
     * 120x+210y <= 15000
     * 110x+30y <= 4000
     * x+y <= 75
     *
     * With x,y being integers
     * @return
     */
    public static Problem mixedIntegerLinear2() {


        // Create the variables
        Variable x = new Variable("x", new Bounds(16.0, Bounds.BoundType.UPPER), Variable.VariableType.INTEGER);
        Variable y = new Variable("y", new Bounds(), Variable.VariableType.INTEGER);

        List<Variable> variables = new ArrayList<>();
        variables.add(x);
        variables.add(y);

        // Set objective: maximize 143x + 60y
        Expression objExpr = new Expression().addTerm(143.0, x).addTerm(60.0, y);
        Objective objective = new Objective("max_143x+60y", Objective.ObjectiveDirection.MAXIMISE, objExpr);

        // Setup constraints
        List<Constraint> constraints = new ArrayList<>();

        // Add constraint: 120x+210y <= 15000
        Expression c0 = new Expression().addTerm(120.0, x).addTerm(210.0, y);
        constraints.add(new Constraint("c0", c0, Constraint.Relation.LTE, 15000.0));

        // Add constraint: 110x+30y <= 4000
        Expression c1 = new Expression().addTerm(110.0, x).addTerm(30.0, y);
        constraints.add(new Constraint("c1", c1, Constraint.Relation.LTE, 4000.0));

        // Add constraint: x+y <= 75
        Expression c2 = new Expression().addTerm(1.0, x).addTerm(1.0, y);
        constraints.add(new Constraint("c2", c2, Constraint.Relation.LTE, 75.0));

        Problem problem = new Problem("simple_linear_2", variables, constraints, objective);

        return problem;
    }

    public static Problem simpleQuadratic() {

        // Create the variables
        Variable x = new Variable("x", new Bounds(0.0, Bounds.BoundType.LOWER), Variable.VariableType.CONTINUOUS);
        Variable y = new Variable("y", new Bounds(0.0, Bounds.BoundType.LOWER), Variable.VariableType.CONTINUOUS);

        List<Variable> variables = new ArrayList<>();
        variables.add(x);
        variables.add(y);

        // Set objective: minimize
        Expression objExpr = new Expression().addTerm(1.0, x, x).addTerm(0.4, x, y).addTerm(0.4, y, x).addTerm(1.0, y, y);
        Objective objective = new Objective("min_x^2+0.4xy+0.4yx+y^2", Objective.ObjectiveDirection.MINIMISE, objExpr);

        // Setup constraints
        List<Constraint> constraints = new ArrayList<>();

        constraints.add(new Constraint("eq",
                new Expression().addTerm(1.0, x).addTerm(1.0, y),
                Constraint.Relation.EQUAL,
                1.0));

        Problem problem = new Problem("simple_quadratic", variables, constraints, objective);
        problem.setInitialPoint(new double[]{0.1, 0.9});
        problem.setTolerance(1.E-12);

        return problem;
    }

    public static Problem simpleQuadratic2() {

        // Create the variables
        Variable x = new Variable("x", new Bounds(0.0, 1.0, Bounds.BoundType.DOUBLE), Variable.VariableType.CONTINUOUS);
        Variable y = new Variable("y", new Bounds(0.0, 1.0, Bounds.BoundType.DOUBLE), Variable.VariableType.CONTINUOUS);
        Variable z = new Variable("z", new Bounds(0.0, 1.0, Bounds.BoundType.DOUBLE), Variable.VariableType.CONTINUOUS);

        List<Variable> variables = new ArrayList<>();
        variables.add(x);
        variables.add(y);
        variables.add(z);


        // Create objective

        Expression objExpr = new Expression()
                .addTerm(1.0, x, x)
                .addTerm(1.0, x, y)
                .addTerm(1.0, y, y)
                .addTerm(1.0, y, z)
                .addTerm(1.0, z, z)
                .addTerm(2.0, x);
        Objective objective = new Objective("min_x^2+y^2+z^2+xy+yz+2x", Objective.ObjectiveDirection.MINIMISE, objExpr);


        // Setup constraints
        List<Constraint> constraints = new ArrayList<>();

        // Create constraint: x + 2 y + 3 z >= 4

        Expression c0 = new Expression()
                .addTerm(1.0, x)
                .addTerm(2.0, y)
                .addTerm(3.0, z);

        constraints.add(new Constraint("c0", c0, Constraint.Relation.GREATER_THAN_OR_EQUAL_TO, 4.0));

        // Add constraint: x + y >= 1

        Expression c1 = new Expression()
                .addTerm(1.0, x)
                .addTerm(1.0, y);

        constraints.add(new Constraint("c1", c1, Constraint.Relation.GREATER_THAN_OR_EQUAL_TO, 1.0));

        // Create the problem
        Problem p = new Problem("simple_quadratic_2", variables, constraints, objective);

        return p;
    }
}
