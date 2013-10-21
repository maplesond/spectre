/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
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
package uk.ac.uea.cmp.phygen.superq.objectives;

import org.kohsuke.MetaInfServices;
import uk.ac.uea.cmp.phygen.core.math.optimise.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@MetaInfServices(SecondaryProblem.class)
public class Quadratic implements SecondaryProblem {

    @Override
    public Problem compileProblem(int nbTaxa, double[] X, double[][] EtE) {

        List<Variable> variables = this.createVariables(X);
        List<Constraint> constraints = this.createConstraints(variables, X, EtE);
        Objective objective = this.createObjective(variables);

        return new Problem(this.getName(), variables, constraints, objective);
    }

    @Override
    public Objective.ObjectiveType getObjectiveType() {
        return Objective.ObjectiveType.QUADRATIC;
    }

    private Objective createObjective(List<Variable> variables) {
        Expression expression = new Expression();
        for (int i = 0; i < variables.size(); i++) {
            expression.addTerm(1.0, variables.get(i), variables.get(i));
        }

        return new Objective(this.getName(), Objective.ObjectiveDirection.MINIMISE, expression);
    }

    private List<Constraint> createConstraints(List<Variable> variables, double[] X, double[][] EtE) {

        List<Constraint> constraints = new ArrayList<>();

        for (int i = 0; i < EtE.length; i++) {
            Expression expr = new Expression();
            double sum = 0.0;
            for (int j = 0; j < EtE.length; j++) {
                expr.addTerm(EtE[i][j], variables.get(j));
                sum += EtE[i][j] * X[j];
            }
            constraints.add(new Constraint("c0", expr, Constraint.Relation.EQUAL, sum));
        }

        return constraints;
    }

    public List<Variable> createVariables(double[] X) {

        double[] coefficients = new double[X.length];
        Arrays.fill(coefficients, 1.0);

        List<Variable> variables = new ArrayList<>();

        for (int i = 0; i < coefficients.length; i++) {
            variables.add(new Variable(
                    "x" + i,                                        // Name
                    coefficients[i],                                // Coefficient
                    new Bounds(0.0, Bounds.BoundType.LOWER),        // Bounds
                    Variable.VariableType.CONTINUOUS                // Type
            ));
        }

        return variables;
    }


    @Override
    public String getName() {
        return "QUADRATIC";
    }

    @Override
    public boolean acceptsIdentifier(String id) {
        return id.equalsIgnoreCase(getName()) ||
                id.equalsIgnoreCase(this.getClass().getName());
    }
}
