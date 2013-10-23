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
package uk.ac.uea.cmp.phygen.gurobi;

import gurobi.*;
import org.apache.commons.math3.util.Pair;
import org.kohsuke.MetaInfServices;
import uk.ac.uea.cmp.phygen.core.math.optimise.*;

import java.util.ArrayList;
import java.util.List;

@MetaInfServices(uk.ac.uea.cmp.phygen.core.math.optimise.Optimiser.class)
public class Gurobi extends AbstractOptimiser {

    // Gurobi vars
    private GRBEnv env;

    public Gurobi() throws OptimiserException {
        try {
            //GRBEnv env = new GRBEnv("gurobi.log");
            this.env = new GRBEnv();
            this.env.set(GRB.IntParam.OutputFlag, 0);
        } catch (GRBException ge) {
            // Repackage any GurobiException and rethrow
            throw new OptimiserException(ge, ge.getErrorCode());
        }
    }

    protected char convertRelationship(Constraint.Relation relation) {

        if (relation == Constraint.Relation.EQUAL) {
            return GRB.EQUAL;
        }
        else if (relation == Constraint.Relation.GREATER_THAN_OR_EQUAL_TO) {
            return GRB.GREATER_EQUAL;
        }
        else if (relation == Constraint.Relation.LESS_THAN_OR_EQUAL_TO) {
            return GRB.LESS_EQUAL;
        }

        throw new UnsupportedOperationException("Gurobi cannot handle this Relation: " + relation.toString());
    }

    protected char convertVariableType(Variable.VariableType variableType) {

        if (variableType == Variable.VariableType.CONTINUOUS) {
            return GRB.CONTINUOUS;
        } else if (variableType == Variable.VariableType.INTEGER) {
            return GRB.INTEGER;
        } else if (variableType == Variable.VariableType.BINARY) {
            return GRB.BINARY;
        }

        throw new IllegalArgumentException("Unknown Variable Type");
    }

    protected int convertObjectiveDirection(Objective.ObjectiveDirection objectiveDirection) {
        if (objectiveDirection == Objective.ObjectiveDirection.MAXIMISE) {
            return GRB.MAXIMIZE;
        } else if (objectiveDirection == Objective.ObjectiveDirection.MINIMISE) {
            return GRB.MINIMIZE;
        }

        throw new IllegalArgumentException("Unknown objective direction encountered: " + objectiveDirection.toString());
    }


    protected GRBVar[] addDecisionVariables(Problem problem, GRBModel model) throws GRBException {

        List<Variable> variables = problem.getVariables();

        GRBVar[] grbVars = new GRBVar[variables.size()];

        for (int i = 0; i < variables.size(); i++) {

            Variable var = variables.get(i);
            grbVars[i] = model.addVar(
                    var.getBounds().getLower(),
                    var.getBounds().getUpper(),
                    var.getCoefficient(),
                    convertVariableType(var.getType()),
                    var.getName()
            );
        }

        return grbVars;
    }

    public GRBConstr[] addLinearConstraints(List<Constraint> constraints, GRBModel model, GRBVar[] vars) throws GRBException {

        GRBConstr[] grbConstraints = new GRBConstr[constraints.size()];

        for(int i = 0; i < constraints.size(); i++) {

            Constraint phygenConstraint = constraints.get(i);

            GRBLinExpr grbLinExpr = new GRBLinExpr();

            for(LinearTerm term : phygenConstraint.getExpression().getLinearTerms()) {

                GRBVar var = findGurobiVar(vars, term.getVariable().getName());
                grbLinExpr.addTerm(term.getCoefficient(), var);
            }

            grbLinExpr.addConstant(phygenConstraint.getExpression().getConstant());

            grbConstraints[i] = model.addConstr(
                    grbLinExpr,
                    this.convertRelationship(phygenConstraint.getRelation()),
                    phygenConstraint.getValue(),
                    phygenConstraint.getName());
        }

        return grbConstraints;
    }

    public GRBExpr addObjective(Objective objective, GRBModel model, GRBVar[] grbVars) throws GRBException {

        GRBExpr expr = null;
        if (objective.getType() == Objective.ObjectiveType.QUADRATIC) {
            GRBQuadExpr quadExpr = new GRBQuadExpr();

            quadExpr.addConstant(objective.getExpression().getConstant());

            for (LinearTerm linTerm : objective.getExpression().getLinearTerms()) {
                GRBVar var = findGurobiVar(grbVars, linTerm.getVariable().getName());
                quadExpr.addTerm(linTerm.getCoefficient(), var);
            }

            for(QuadraticTerm quadraticTerm : objective.getExpression().getQuadraticTerms()) {
                GRBVar var1 = findGurobiVar(grbVars, quadraticTerm.getVariable1().getName());
                GRBVar var2 = findGurobiVar(grbVars, quadraticTerm.getVariable2().getName());

                quadExpr.addTerm(quadraticTerm.getCoefficient(), var1, var2);
            }

            expr = quadExpr;
        }
        else {
            GRBLinExpr linExpr = new GRBLinExpr();

            linExpr.addConstant(objective.getExpression().getConstant());

            for (LinearTerm linTerm : objective.getExpression().getLinearTerms()) {
                GRBVar var = findGurobiVar(grbVars, linTerm.getVariable().getName());
                linExpr.addTerm(linTerm.getCoefficient(), var);
            }

            expr = linExpr;
        }

        model.setObjective(expr, convertObjectiveDirection(objective.getDirection()));

        return expr;
    }

    protected GRBVar findGurobiVar(GRBVar[] vars, String name) throws GRBException {

        for(int j = 0; j < vars.length; j++) {
            if (name.equals(vars[j].get(GRB.StringAttr.VarName))) {
                return vars[j];
            }
        }

        throw new IllegalArgumentException("Gurobi variable: " + name + "; could not be found.");
    }


    protected Solution buildSolution(GRBVar[] vars, double solution) throws GRBException {

        List<Pair<String,Double>> variableValues = new ArrayList<>(vars.length);

        for (int i = 0; i < vars.length; i++) {
            variableValues.add(new Pair<>(vars[i].get(GRB.StringAttr.VarName), vars[i].get(GRB.DoubleAttr.X)));
        }

        return new Solution(variableValues, solution);
    }

    @Override
    protected Solution internalOptimise(Problem problem) throws OptimiserException {

        Solution solution = null;

        try {
            // Create a new model
            GRBModel model = new GRBModel(env);

            // Create the decision variables from the problem and add to the model
            GRBVar[] vars = addDecisionVariables(problem, model);

            // Update the model after all decision variables have been added
            model.update();

            // Get the objective if present
            GRBExpr expr = addObjective(problem.getObjective(), model, vars);
            if (expr != null) {
                model.setObjective(expr, convertObjectiveDirection(problem.getObjective().getDirection()));
            }

            // Add constraints
            GRBConstr[] constraints = addLinearConstraints(problem.getConstraints(), model, vars);

            // Optimise the model
            model.optimize();

            // Create solution array from the optimised model
            solution = this.buildSolution(vars, model.get(GRB.DoubleAttr.ObjVal));

            // Delete the model now that we have the solution
            model.dispose();

        } catch (GRBException ge) {
            // Repackage any GurobiException and rethrow
            throw new OptimiserException(ge, ge.getErrorCode());
        }

        return solution;
    }


    protected GRBEnv getEnv() {
        return env;
    }


    @Override
    public boolean acceptsIdentifier(String id) {
        return id.equalsIgnoreCase(this.getIdentifier()) || id.equalsIgnoreCase(Gurobi.class.getName());
    }


    @Override
    public boolean acceptsObjectiveType(Objective.ObjectiveType objectiveType) {
        return objectiveType.isLinear() || objectiveType.isQuadratic();
    }

    @Override
    public boolean acceptsObjectiveDirection(Objective.ObjectiveDirection objectiveDirection) {
        return true;
    }

    @Override
    public boolean acceptsConstraintType(Constraint.ConstraintType constraintType) {

        if (constraintType.isQuadratic())
            throw new UnsupportedOperationException("Gurobi can handle quadratic constraints but the translation code has " +
                    "not been implemented yet");

        return true;
    }

    @Override
    public String getIdentifier() {
        return "Gurobi";
    }

    @Override
    public boolean isOperational() {

        try {
            GRBEnv env = new GRBEnv();
        } catch (Throwable t) {
            // Can't find the gurobi native libraries, so it's not operational
            return false;
        }

        return true;
    }
}
