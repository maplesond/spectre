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

import com.joptimizer.functions.ConvexMultivariateRealFunction;
import com.joptimizer.functions.LinearMultivariateRealFunction;
import com.joptimizer.functions.PDQuadraticMultivariateRealFunction;
import com.joptimizer.optimizers.OptimizationRequest;
import org.apache.commons.math3.optimization.linear.LinearConstraint;
import org.apache.commons.math3.optimization.linear.Relationship;
import org.apache.commons.math3.util.Pair;
import org.kohsuke.MetaInfServices;
import uk.ac.uea.cmp.phygen.core.math.optimise.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@MetaInfServices(Optimiser.class)
public class JOptimizer extends AbstractOptimiser {

    public JOptimizer() throws OptimiserException {
        super();
    }

    protected void addDecisionVariables(List<ConvexMultivariateRealFunction> constraints, Problem problem) throws OptimiserException {

        List<Variable> variables = problem.getVariables();

        // Add restriction constraint
        for (int i = 0; i < variables.size(); i++) {

            Variable var = variables.get(i);

            double[] lowerCoefficients = new double[variables.size()];

            for (int j = 0; j < variables.size(); j++) {

                lowerCoefficients[j] = i == j ? -1.0 : 0.0;
            }

            double[] upperCoefficients = new double[variables.size()];

            for (int j = 0; j < variables.size(); j++) {

                upperCoefficients[j] = i == j ? -1.0 : 0.0;
            }

            Bounds.BoundType boundType = var.getBounds().getBoundType();

            if (boundType == Bounds.BoundType.FREE) {
                // Do nothing in this case
            }
            else if (boundType == Bounds.BoundType.LOWER) {
                constraints.add(new LinearMultivariateRealFunction(lowerCoefficients, var.getBounds().getLower()));
            }
            else if (boundType == Bounds.BoundType.UPPER) {
                constraints.add(new LinearMultivariateRealFunction(upperCoefficients, var.getBounds().getUpper()));
            }
            else if (boundType == Bounds.BoundType.DOUBLE) {
                constraints.add(new LinearMultivariateRealFunction(lowerCoefficients, var.getBounds().getLower()));
                constraints.add(new LinearMultivariateRealFunction(upperCoefficients, var.getBounds().getUpper()));
            }
            else if (boundType == Bounds.BoundType.FIXED) {
                constraints.add(new LinearMultivariateRealFunction(lowerCoefficients, var.getBounds().getLower()));
                constraints.add(new LinearMultivariateRealFunction(upperCoefficients, var.getBounds().getLower()));
            }
            else {
                throw new IllegalArgumentException("Unknown bound type encountered: " + boundType.toString());
            }
        }
    }

    protected void addInequalityConstraints(List<ConvexMultivariateRealFunction> constraints, Problem problem) {
        for(Constraint constraint: problem.getConstraints()) {

            if (constraint.getRelation() != Constraint.Relation.EQUAL) {
                constraints.add(new LinearMultivariateRealFunction(
                        constraint.getExpression().getLinearCoefficients(problem.getVariables()),
                        constraint.getValue() - constraint.getExpression().getConstant())
                );
            }
        }
    }

    protected EqualityConstraints getEqualityConstraints(Problem problem) {

        int nbEqualityConstraints = problem.getNbEqualityConstraints();

        if (nbEqualityConstraints == 0)
            return null;

        double[][] coefficients = new double[nbEqualityConstraints][problem.getNbVariables()];
        double[] constants = new double[nbEqualityConstraints];

        double constant = 0.0;
        boolean found = false;

        int eqConstIdx = 0;
        for(Constraint constraint: problem.getConstraints()) {
            if (constraint.getRelation() == Constraint.Relation.EQUAL) {
                double[] phygenCoefficients = constraint.getExpression().getLinearCoefficients(problem.getVariables());

                for(int i = 0; i < problem.getNbVariables(); i++) {
                    coefficients[eqConstIdx][i] = phygenCoefficients[i];
                }

                constants[eqConstIdx] = constraint.getExpression().getConstant() - constraint.getValue();

                eqConstIdx++;
            }
        }

        return new EqualityConstraints(coefficients, constants);
    }

    protected ConvexMultivariateRealFunction convertObjective(Objective phygenObjective, List<Variable> variables) {

        if (phygenObjective.getType().isLinear()) {
            return new LinearMultivariateRealFunction(
                    phygenObjective.getExpression().getLinearCoefficients(variables),
                    phygenObjective.getExpression().getConstant()
            );
        }
        else if (phygenObjective.getType().isQuadratic()) {

            return new PDQuadraticMultivariateRealFunction(
                    phygenObjective.getExpression().getQuadraticCoefficients(variables),
                    phygenObjective.getExpression().getLinearCoefficients(variables),
                    phygenObjective.getExpression().getConstant()
            );
        }
        else {
            throw new IllegalArgumentException("JOptimizer is not sure how to translate objective.");
        }
    }

    protected Solution createSolution(double[] solution, List<Variable> variables) {

        List<Pair<String,Double>> variableValues = new ArrayList<>();

        for(int i = 0; i < variables.size(); i++) {
            variableValues.add(new Pair<>(variables.get(i).getName(), solution[i]));
        }

        return new Solution(variableValues, 0.0);
    }

    private class EqualityConstraints {
        private double[][] A;
        private double[] B;

        private EqualityConstraints(double[][] a, double[] b) {
            A = a;
            B = b;
        }

        private double[][] getA() {
            return A;
        }

        private double[] getB() {
            return B;
        }
    }


    @Override
    protected Solution internalOptimise(Problem problem) throws OptimiserException {

        // Create the objective function
        ConvexMultivariateRealFunction objective = this.convertObjective(problem.getObjective(), problem.getVariables());

        // Setup collection for constraints
        List<ConvexMultivariateRealFunction> constraints = new ArrayList<>();

        // Add the decision variable constraints (i.e. upper and lower bounds)
        this.addDecisionVariables(constraints, problem);

        // Add the inequality constraints
        this.addInequalityConstraints(constraints, problem);

        // Add quality constraints
        EqualityConstraints equalityConstraint = this.getEqualityConstraints(problem);

        // Create the request
        OptimizationRequest or = new OptimizationRequest();
        or.setF0(objective);
        or.setFi(constraints.toArray(new ConvexMultivariateRealFunction[constraints.size()]));

        if (equalityConstraint != null) {
            or.setA(equalityConstraint.getA());
            or.setB(equalityConstraint.getB());
        }

        if (problem.isInitialPointSet()) {
            or.setInitialPoint(problem.getInitialPointCoefficients());
        }

        if (problem.getMaxIterations() != 0) {
            or.setMaxIteration(problem.getMaxIterations());
        }

        if (problem.getTolerance() != 0.0) {
            or.setTolerance(problem.getTolerance());
            or.setToleranceFeas(problem.getTolerance());
            or.setToleranceInnerStep(problem.getTolerance());
        }

        // Run the solver and return the results or throw the exception
        com.joptimizer.optimizers.JOptimizer optimizer = new com.joptimizer.optimizers.JOptimizer();
        optimizer.setOptimizationRequest(or);

        try {
            int returnCode = optimizer.optimize();

            if (returnCode == 0) {
                return this.createSolution(optimizer.getOptimizationResponse().getSolution(), problem.getVariables());
            }
            else {
                throw new OptimiserException("JOptimize could not solve problem.  Return code: " + returnCode);
            }
        }
        catch (Exception e) {
            throw new OptimiserException(e, 1);
        }

    }


    @Override
    public boolean acceptsIdentifier(String id) {
        return id.equalsIgnoreCase(this.getIdentifier()) || id.equalsIgnoreCase(JOptimizer.class.getName());
    }

    @Override
    public boolean acceptsObjectiveType(Objective.ObjectiveType objectiveType) {
        return true;
    }

    @Override
    public boolean acceptsObjectiveDirection(Objective.ObjectiveDirection objectiveDirection) {
        return objectiveDirection == Objective.ObjectiveDirection.MINIMISE;
    }

    @Override
    public boolean acceptsConstraintType(Constraint.ConstraintType constraintType) {
        if (constraintType.isQuadratic())
            throw new UnsupportedOperationException("JOptimizer can handle quadratic constraints but the translation code has " +
                    "not been implemented yet");

        return true;
    }

    @Override
    public String getIdentifier() {
        return "JOptimizer";
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
