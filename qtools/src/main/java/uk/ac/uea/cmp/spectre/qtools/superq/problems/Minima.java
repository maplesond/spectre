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

package uk.ac.uea.cmp.spectre.qtools.superq.problems;

import org.kohsuke.MetaInfServices;
import uk.ac.tgac.metaopt.*;

import java.util.ArrayList;
import java.util.List;


@MetaInfServices(SecondaryProblem.class)
public class Minima implements SecondaryProblem {

    @Override
    public Problem compileProblem(int nbTaxa, double[] X, double[][] EtE) {

        List<Variable> variables = this.createVariables(X);
        List<Constraint> constraints = this.createConstraints(variables, X, EtE);
        Objective objective = this.createObjective(variables);

        return new Problem(this.getName(), variables, constraints, objective);
    }

    @Override
    public Objective.ObjectiveType getObjectiveType() {
        return Objective.ObjectiveType.LINEAR;
    }

    private Objective createObjective(List<Variable> variables) {

        Expression expression = new Expression();
        for (int i = 0; i < variables.size(); i++) {
            expression.addTerm(0.0, variables.get(i));
        }

        return new Objective(this.getName(), Objective.ObjectiveDirection.MINIMISE, expression);
    }

    private List<Constraint> createConstraints(List<Variable> variables, double[] X, double[][] EtE) {

        List<Constraint> constraints = new ArrayList<>();

        for (int i = 0; i < EtE.length; i++) {
            Expression expr = new Expression();
            for (int j = 0; j < EtE.length; j++) {
                expr.addTerm(EtE[i][j], variables.get(j));
            }
            constraints.add(new Constraint("c0", expr, Constraint.Relation.EQUAL, 0.0));
        }

        return constraints;
    }

    public List<Variable> createVariables(double[] X) {

        List<Variable> variables = new ArrayList<>();

        for (int i = 0; i < X.length; i++) {
            variables.add(new Variable(
                    "x" + i,                                        // Name
                    new Bounds(-X[i], Bounds.BoundType.LOWER),      // Bounds
                    Variable.VariableType.CONTINUOUS                // Type
            ));
        }

        return variables;
    }


    @Override
    public String getName() {
        return "MINIMA";
    }

    @Override
    public boolean acceptsIdentifier(String id) {
        return id.equalsIgnoreCase(getName()) ||
                id.equalsIgnoreCase(this.getClass().getName());
    }
}
