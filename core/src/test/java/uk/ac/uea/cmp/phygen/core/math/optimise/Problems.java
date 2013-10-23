package uk.ac.uea.cmp.phygen.core.math.optimise;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: maplesod
 * Date: 22/10/13
 * Time: 11:16
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
    public static Problem mip1() {

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
        Objective objective = new Objective("o", Objective.ObjectiveDirection.MAXIMISE, objExpr);

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
        Variable x = new Variable("x", 0.0, new Bounds(-2.0, 2.0, Bounds.BoundType.FREE), Variable.VariableType.CONTINUOUS);
        Variable y = new Variable("y", 0.0, new Bounds(-2.0, 2.0, Bounds.BoundType.FREE), Variable.VariableType.CONTINUOUS);

        List<Variable> variables = new ArrayList<>();
        variables.add(x);
        variables.add(y);

        // Set objective: minimize -x - y + 4
        Expression objExpr = new Expression().addTerm(-1.0, x).addTerm(-1.0, y).addConstant(4.0);
        Objective objective = new Objective("z", Objective.ObjectiveDirection.MINIMISE, objExpr);

        // Setup constraints
        List<Constraint> constraints = new ArrayList<>();

        // Add constraint: 4/3x - y >= -2
        Expression c0 = new Expression().addTerm(4.0/3.0, x).addTerm(-1.0, y).addConstant(2.0);
        constraints.add(new Constraint("c0", c0, Constraint.Relation.GTE, 0.0));

        // Add constraint: -1/2x + y >= -0.5
        Expression c1 = new Expression().addTerm(-0.5, x).addTerm(1.0, y).addConstant(0.5);
        constraints.add(new Constraint("c1", c1, Constraint.Relation.GTE, 0.0));

        // Add constraint: -2x - y >= -2
        Expression c2 = new Expression().addTerm(-2.0, x).addTerm(-1.0, y).addConstant(2.0);
        constraints.add(new Constraint("c2", c2, Constraint.Relation.GTE, 0.0));

        // Add constraint: -2x - y >= -2
        Expression c3 = new Expression().addTerm(1.0/3.0, x).addTerm(1.0, y).addConstant(0.5);
        constraints.add(new Constraint("c3", c3, Constraint.Relation.GTE, 0.0));


        // Create and return the problem
        return new Problem("simple_linear", variables, constraints, objective);
    }
}
