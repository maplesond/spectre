/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2014  UEA School of Computing Sciences
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

package uk.ac.uea.cmp.spectre.qtools.scale;

import uk.ac.tgac.metaopt.*;

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

        for (int i = 0; i < variables.size(); i++) {
            expr.addTerm(1.0, variables.get(i));
        }

        return new Objective("scaling", Objective.ObjectiveDirection.MINIMISE, expr);
    }

    protected List<Constraint> createConstraints(List<Variable> variables, double[][] h) {

        List<Constraint> constraints = new ArrayList<>(variables.size());

        Expression expr = new Expression();
        for (Variable var : variables) {
            expr.addTerm(1.0, var);
        }
        constraints.add(new Constraint("c0", expr, Constraint.Relation.EQUAL, 1.0));

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
