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
package uk.ac.uea.cmp.phygen.core.math.optimise.external;

import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.linear.LinearConstraint;
import org.apache.commons.math3.optimization.linear.LinearObjectiveFunction;
import org.apache.commons.math3.optimization.linear.Relationship;
import org.apache.commons.math3.optimization.linear.SimplexSolver;
import org.apache.commons.math3.util.Pair;
import org.kohsuke.MetaInfServices;
import uk.ac.uea.cmp.phygen.core.math.optimise.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//@MetaInfServices(uk.ac.uea.cmp.phygen.core.math.optimise.Optimiser.class)
public class Apache extends AbstractOptimiser {

    public Apache() throws OptimiserException {
        super();
    }

    protected Relationship convertRelationship(Constraint.Relation relation) {

        if (relation == Constraint.Relation.EQUAL) {
            return Relationship.EQ;
        }
        else if (relation == Constraint.Relation.GREATER_THAN_OR_EQUAL_TO) {
            return Relationship.GEQ;
        }
        else if (relation == Constraint.Relation.LESS_THAN_OR_EQUAL_TO) {
            return Relationship.LEQ;
        }

        throw new UnsupportedOperationException("Apache cannot handle this Relation: " + relation.toString());
    }

    protected GoalType convertObjectiveDirection(Objective.ObjectiveDirection objectiveDirection) {
        if (objectiveDirection == Objective.ObjectiveDirection.MAXIMISE) {
            return GoalType.MAXIMIZE;
        } else if (objectiveDirection == Objective.ObjectiveDirection.MINIMISE) {
            return GoalType.MINIMIZE;
        }

        throw new IllegalArgumentException("Unknown objective direction encountered: " + objectiveDirection.toString());
    }

    protected void addDecisionVariables(Collection<LinearConstraint> constraints, Problem problem) {

        List<Variable> variables = problem.getVariables();

        // Add restriction constraint
        for (int i = 0; i < variables.size(); i++) {

            Variable var = variables.get(i);

            double[] coefficients = new double[variables.size()];

            for (int j = 0; j < variables.size(); j++) {

                coefficients[j] = i == j ? 1.0 : 0.0;
            }

            Bounds.BoundType boundType = var.getBounds().getBoundType();

            if (boundType == Bounds.BoundType.FREE) {
                // Do nothing in this case
            }
            else if (boundType == Bounds.BoundType.LOWER) {
                constraints.add(new LinearConstraint(coefficients, Relationship.GEQ, var.getBounds().getLower()));
            }
            else if (boundType == Bounds.BoundType.UPPER) {
                constraints.add(new LinearConstraint(coefficients, Relationship.LEQ, var.getBounds().getUpper()));
            }
            else if (boundType == Bounds.BoundType.DOUBLE) {
                constraints.add(new LinearConstraint(coefficients, Relationship.GEQ, var.getBounds().getLower()));
                constraints.add(new LinearConstraint(coefficients, Relationship.LEQ, var.getBounds().getUpper()));
            }
            else if (boundType == Bounds.BoundType.FIXED) {
                constraints.add(new LinearConstraint(coefficients, Relationship.EQ, var.getBounds().getLower()));
            }
            else {
                throw new IllegalArgumentException("Unknown bound type encountered: " + boundType.toString());
            }
        }
    }

    protected void addRegularConstraints(Collection<LinearConstraint> apacheConstraints, Problem problem) {

        for(Constraint constraint: problem.getConstraints()) {

            double[] coefficients = new double[problem.getNbVariables()];

            List<LinearTerm> terms = constraint.getExpression().getLinearTerms();

            for(LinearTerm term : terms) {
                for (int j = 0; j < problem.getNbVariables(); j++) {

                    if (problem.getVariables().get(j).getName().equals(term.getVariable().getName())) {
                        coefficients[j] = term.getCoefficient();
                    }
                }
            }

            Relationship relationship = this.convertRelationship(constraint.getRelation());

            apacheConstraints.add(new LinearConstraint(coefficients, relationship, constraint.getValue() - constraint.getExpression().getConstant()));
        }
    }

    protected LinearObjectiveFunction convertObjective(Objective phygenObjective, List<Variable> variables) {

        return new LinearObjectiveFunction(
                phygenObjective.getExpression().getLinearCoefficients(variables),
                phygenObjective.getExpression().getConstant());
    }

    protected Solution createSolution(PointValuePair pvp, List<Variable> variables) {

        List<Pair<String,Double>> variableValues = new ArrayList<>();

        for(int i = 0; i < variables.size(); i++) {
            variableValues.add(new Pair<>(variables.get(i).getName(), pvp.getPoint()[i]));
        }

        return new Solution(variableValues, pvp.getValue());
    }


    @Override
    protected Solution internalOptimise(Problem problem) {

        // Create the objective function
        LinearObjectiveFunction f = this.convertObjective(problem.getObjective(), problem.getVariables());

        // Setup collection for constraints
        Collection<LinearConstraint> constraints = new ArrayList<>();

        // Add the decision variable constraints (i.e. upper and lower bounds)
        this.addDecisionVariables(constraints, problem);

        // Add the regular constraints
        this.addRegularConstraints(constraints, problem);

        // Create the solver
        SimplexSolver solver = new SimplexSolver();

        // Set max iterations if set by the user
        if (problem.getMaxIterations() != 0) {
            solver.setMaxIterations(problem.getMaxIterations());
        }

        // Run the solver
        PointValuePair pvp = solver.optimize(f, constraints,
                convertObjectiveDirection(problem.getObjective().getDirection()),
                false);

        // Return results
        return this.createSolution(pvp, problem.getVariables());
    }


    @Override
    public boolean acceptsIdentifier(String id) {
        return id.equalsIgnoreCase(this.getIdentifier()) || id.equalsIgnoreCase(Apache.class.getName());
    }

    @Override
    public boolean acceptsObjectiveType(Objective.ObjectiveType objectiveType) {
        return objectiveType.isLinear();
    }

    @Override
    public boolean acceptsConstraintType(Constraint.ConstraintType constraintType) {
        return constraintType.isLinear();
    }

    @Override
    public boolean acceptsVariableType(Variable.VariableType variableType) {
        return variableType == Variable.VariableType.CONTINUOUS;
    }

    @Override
    public String getIdentifier() {
        return "Apache";
    }


    /**
     * Apache should always be operational, as it's an open source java library available via maven
     *
     * @return
     */
    @Override
    public boolean isOperational() {
        return true;
    }


}
