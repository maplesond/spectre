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

        List<Variable> variables = this.createVariables(X);
        List<Constraint> constraints = this.createConstraints(variables, X, EtE);
        Objective objective = this.createObjective(nbTaxa, variables);

        return new Problem(this.getName(), variables, constraints, objective);
    }

    private Objective createObjective(final int nbTaxa, List<Variable> variables) {

        double[] coefficients = this.buildCoefficients(variables.size(), nbTaxa);

        Expression expr = new Expression();
        for (int i = 0; i < variables.size(); i++) {
            expr.addTerm(coefficients[i], variables.get(i));
        }

        return new Objective(this.getName(), Objective.ObjectiveDirection.MINIMISE, expr);
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

    public List<Variable> createVariables(final double[] X) {

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
        return "BALANCED";
    }

    @Override
    public boolean acceptsIdentifier(String id) {
        return id.equalsIgnoreCase(getName()) ||
                id.equalsIgnoreCase(this.getClass().getName());
    }
}
