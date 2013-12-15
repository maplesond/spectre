package uk.ac.uea.cmp.phygen.core.ds.quartet.scale;

import uk.ac.uea.cmp.phygen.core.math.optimise.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dan on 14/12/13.
 */
public class ScalingOptimiser {

    private Optimiser optimiser;

    public ScalingOptimiser(Optimiser optimiser) {
        this.optimiser = optimiser;
    }

    public Solution optimise(double[][] h) throws OptimiserException {

        List<Variable> variables = this.createVariables(h.length);
        List<Constraint> constraints = this.createConstraints(variables, h);
        Objective objective = this.createObjective(variables, h);

        Problem problem = new Problem("scaling", variables, constraints, objective);

        // Run the solver on the problem and return the result
        return this.optimiser.optimise(problem);
    }

    protected Objective createObjective(List<Variable> variables, double[][] h) {

        Expression expr = new Expression();

        for (int i = 0; i < variables.size(); i++) {
            for (int j = 0; j < variables.size(); j++) {
                expr.addTerm(h[j][i], variables.get(j), variables.get(i));
            }
        }

        for(int i = 0; i < variables.size(); i++) {
            expr.addTerm(1.0, variables.get(i));
        }

        return new Objective("scaling", Objective.ObjectiveDirection.MINIMISE, expr);
    }

    protected List<Constraint> createConstraints(List<Variable> variables, double[][] h) {

        List<Constraint> constraints = new ArrayList<>(variables.size());

        for (Variable var : variables) {
            Expression expr = new Expression().addTerm(1.0, var);
            constraints.add(new Constraint("c0", expr, Constraint.Relation.EQUAL, 0.0));
        }

        return constraints;
    }

    protected List<Variable> createVariables(final int size) {

        List<Variable> variables = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            variables.add(new Variable(
                    "x" + i,                                    // Name
                    new Bounds(0.0, Bounds.BoundType.LOWER),    // Bounds
                    Variable.VariableType.CONTINUOUS            // Type
            ));
        }

        return variables;
    }
}
