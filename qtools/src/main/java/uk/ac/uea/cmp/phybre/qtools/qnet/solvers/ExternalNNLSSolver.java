package uk.ac.uea.cmp.phybre.qtools.qnet.solvers;

import uk.ac.tgac.metaopt.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dan on 09/01/14.
 */
public class ExternalNNLSSolver {


    public Solution optimise(Optimiser optimiser, double[] Etf, double[][] EtE) throws OptimiserException {

        // Create the problem
        List<Variable> variables = createVariables(Etf.length);
        List<Constraint> constraints = createConstraints(variables);
        Objective objective = createObjective(variables, Etf, EtE);
        Problem problem = new Problem("NNLS", variables, constraints, objective);

        // Run the solver on the problem and return the result
        return optimiser.optimise(problem);
    }

    private Objective createObjective(List<Variable> variables, double[] Etf, double[][] EtE) {

        Expression expr = new Expression();

        for (int i = 0; i < variables.size(); i++) {
            for (int j = 0; j < variables.size(); j++) {
                expr.addTerm(EtE[j][i], variables.get(j), variables.get(i));
            }
            expr.addTerm(-2 * Etf[i], variables.get(i));
        }

        return new Objective("NNLS", Objective.ObjectiveDirection.MINIMISE, expr);
    }

    private List<Constraint> createConstraints(List<Variable> variables) {

        List<Constraint> constraints = new ArrayList<>(variables.size());

        for (Variable var : variables) {
            Expression expr = new Expression().addTerm(1.0, var);
            constraints.add(new Constraint("c0", expr, Constraint.Relation.GREATER_THAN_OR_EQUAL_TO, 0.0));
        }

        return constraints;
    }


    private List<Variable> createVariables(int size) {

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
