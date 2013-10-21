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
package uk.ac.uea.cmp.phygen.core.math.optimise.glpk;

import de.xypron.linopt.*;
import org.apache.commons.math3.util.Pair;
import org.gnu.glpk.*;
import org.kohsuke.MetaInfServices;
import uk.ac.uea.cmp.phygen.core.math.optimise.*;
import uk.ac.uea.cmp.phygen.core.math.optimise.Problem;

import java.util.ArrayList;
import java.util.List;

@MetaInfServices(uk.ac.uea.cmp.phygen.core.math.optimise.Optimiser.class)
public class GLPKOptimiser extends AbstractOptimiser {

    public GLPKOptimiser() throws OptimiserException {
        super();
    }


    protected de.xypron.linopt.Problem.ColumnType convertVariableType(Variable.VariableType variableType) {

        if (variableType == Variable.VariableType.CONTINUOUS) {
            return de.xypron.linopt.Problem.ColumnType.FLOAT;
        } else if (variableType == Variable.VariableType.INTEGER) {
            return de.xypron.linopt.Problem.ColumnType.INTEGER;
        } else if (variableType == Variable.VariableType.BINARY) {
            return de.xypron.linopt.Problem.ColumnType.BINARY;
        }

        throw new IllegalArgumentException("Unknown variable type encountered: " + variableType.toString());
    }

    protected de.xypron.linopt.Problem.Direction convertObjectiveDirection(Objective.ObjectiveDirection objectiveDirection) {
        if (objectiveDirection == Objective.ObjectiveDirection.MAXIMISE) {
            return de.xypron.linopt.Problem.Direction.MAXIMIZE;
        } else if (objectiveDirection == Objective.ObjectiveDirection.MINIMISE) {
            return de.xypron.linopt.Problem.Direction.MINIMIZE;
        }

        throw new IllegalArgumentException("Unknown objective direction encountered: " + objectiveDirection.toString());
    }

    protected void addDecisionVariables(de.xypron.linopt.Problem glpkProblem, List<Variable> variables) {

        for (int i = 0; i < variables.size(); i++) {

            Variable var = variables.get(i);

            glpkProblem.column(var.getName(), i)
                    .type(convertVariableType(var.getType()))
                    .bounds(
                            var.getBounds().getLower() == Double.NEGATIVE_INFINITY ? null : var.getBounds().getLower(),
                            var.getBounds().getUpper() == Double.POSITIVE_INFINITY ? null : var.getBounds().getUpper());
        }
    }

    private void addConstraints(de.xypron.linopt.Problem glpkProblem, List<Constraint> constraints) {

        for(Constraint constraint : constraints) {
            String name = constraint.getName();

            for(LinearTerm term : constraint.getExpression().getLinearTerms()) {

                de.xypron.linopt.Problem.Column col = null;

                for(de.xypron.linopt.Problem.Column column : glpkProblem.getColumns()) {
                    if (column.getKey().equals(term.getVariable().getName())) {
                        col = column;
                        break;
                    }
                }

                if (col == null)
                    throw new IllegalArgumentException("GLPK columns and phygen variables are out of sync");

                glpkProblem.row(name, col.getColumnNumber()).add(term.getCoefficient(), col);

                if (constraint.getRelation() == Constraint.Relation.EQUAL) {
                    glpkProblem.row(name, col.getColumnNumber()).bounds(constraint.getValue(), constraint.getValue());
                }
                else if (constraint.getRelation() == Constraint.Relation.GREATER_THAN_OR_EQUAL_TO) {
                    glpkProblem.row(name, col.getColumnNumber()).bounds(0.0, null);
                }
                else if (constraint.getRelation() == Constraint.Relation.LESS_THAN_OR_EQUAL_TO) {
                    glpkProblem.row(name, col.getColumnNumber()).bounds(null, 0.0);
                }
            }
        }

    }

    protected void addObjective(de.xypron.linopt.Problem glpkProblem, Objective objective) {

        glpkProblem.objective(objective.getName(), convertObjectiveDirection(objective.getDirection()));

        for(LinearTerm term : objective.getExpression().getLinearTerms()) {

            de.xypron.linopt.Problem.Column col = null;

            for(de.xypron.linopt.Problem.Column column : glpkProblem.getColumns()) {
                if (column.getKey().equals(term.getVariable().getName())) {
                    col = column;
                    break;
                }
            }

            if (col == null)
                throw new IllegalArgumentException("GLPK columns and phygen variables are out of sync");

            glpkProblem.objective().add(term.getCoefficient(), col);
        }

        // What about the constant???
    }

    protected Solution buildSolution(de.xypron.linopt.Problem glpkProblem) {

        List<Pair<String,Double>> variableValues = new ArrayList<>(glpkProblem.getColumns().size());

        for (de.xypron.linopt.Problem.Column column : glpkProblem.getColumns()) {
            variableValues.add(new Pair<>(column.getKey(), column.getValue()));
        }

        return new Solution(variableValues, glpkProblem.objective().getValue());
    }

    @Override
    protected Solution internalOptimise(Problem problem) {

        SWIGTYPE_p_int ia;
        SWIGTYPE_p_double ar;

        // Initialize Problem
        de.xypron.linopt.Problem glpkProblem = new de.xypron.linopt.Problem().setName(problem.getName());

        // Add Variables to problem (columns)
        this.addDecisionVariables(glpkProblem, problem.getVariables());

        // Add objectives
        this.addObjective(glpkProblem, problem.getObjective());

        // Add constraints
        this.addConstraints(glpkProblem, problem.getConstraints());

        // Solve the problem using simplex
        if (!new SolverGlpk().solve(glpkProblem)) {
            return this.buildSolution(glpkProblem);
        } else {
            return null;
        }
    }


    @Override
    public boolean acceptsIdentifier(String id) {
        return id.equalsIgnoreCase(this.getIdentifier()) || id.equalsIgnoreCase(GLPKOptimiser.class.getName());
    }

    @Override
    public boolean acceptsObjectiveType(Objective.ObjectiveType objectiveType) {
        return objectiveType.isLinear();
    }

    @Override
    public String getIdentifier() {
        return "GLPK";
    }


    @Override
    public boolean isOperational() {

        try {
            GLPK.glp_version();
        } catch (Throwable err) {
            // This means that GLPK isn't on the PATH env var.
            return false;
        }

        return true;
    }

}
