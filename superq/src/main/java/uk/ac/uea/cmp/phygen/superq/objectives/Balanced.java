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
import java.util.List;


@MetaInfServices(SecondaryProblem.class)
public class Balanced implements SecondaryProblem {

    private double[] buildCoefficients(final int size, final int nbTaxa) {
        double[] coefficients = new double[size];

        int n = 0;

        for (int m = 1; m < nbTaxa - 1; m++) {

            for (int j = m + 2; j < nbTaxa + 1; j++) {

                if (m != 1 || j != nbTaxa) {

                    int a = j - m;
                    //      SplitIndex [] splitIndices = new SplitIndex [N * (N - 1) / 2 - N];
                    //      Split index is defined as:   splitIndices [n] = new SplitIndex(m, j);
                    coefficients[n++] = a * (a - 1) * (nbTaxa - a) * (nbTaxa - a - 1);
                }
            }
        }

        return coefficients;
    }

    @Override
    public Objective.ObjectiveType getObjectiveType() {
        return Objective.ObjectiveType.LINEAR;
    }


    @Override
    public Problem compileProblem(int nbTaxa, double[] X, double[][] EtE) {

        List<Variable> variables = this.createVariables(nbTaxa, X);
        List<Constraint> constraints = this.createConstraints(variables, X, EtE);
        Objective objective = this.createObjective(variables);

        return new Problem(this.getName(), variables, constraints, objective);
    }

    private Objective createObjective(List<Variable> variables) {

        return new Objective(this.getName(), Objective.ObjectiveDirection.MINIMISE, null);
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

    public List<Variable> createVariables(final int nbTaxa, double[] X) {

        double[] coefficients = this.buildCoefficients(nbTaxa, X.length);


        List<Variable> variables = new ArrayList<>();

        for (int i = 0; i < coefficients.length; i++) {
            variables.add(new Variable(
                    "x" + i,                                        // Name
                    coefficients[i],                                // Coefficient
                    new Bounds(-X[i], Bounds.BoundType.LOWER),      // Bounds
                    Variable.VariableType.CONTINUOUS                // Type
            ));
        }

        return variables;
    }
    @Override
    public String getName() {
        return "BALANCED";
    }

    @Override
    public boolean acceptsIdentifier(String id) {
        return id.equalsIgnoreCase(getName()) ||
                id.equalsIgnoreCase(this.getClass().getName());
    }
}
