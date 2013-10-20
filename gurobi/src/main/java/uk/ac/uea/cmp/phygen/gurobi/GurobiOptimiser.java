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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.math.optimise.*;

import java.util.List;


public abstract class GurobiOptimiser extends AbstractOptimiser {

    private static Logger log = LoggerFactory.getLogger(GurobiOptimiser.class);

    // GurobiOptimiser vars
    private GRBEnv env;

    public GurobiOptimiser() throws OptimiserException {
        try {
            //GRBEnv env = new GRBEnv("gurobi.log");
            this.env = new GRBEnv();
            this.env.set(GRB.IntParam.OutputFlag, 0);
        } catch (GRBException ge) {
            // Repackage any GurobiException and rethrow
            throw new OptimiserException(ge, ge.getErrorCode());
        }
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

    public abstract GRBConstr[] addConstraints(Problem problem, GRBModel model, GRBVar[] vars) throws GRBException;

    public abstract GRBExpr addObjective(Problem problem, GRBModel model, GRBVar[] grbVars) throws GRBException;

    @Override
    protected double[] internalOptimise(Problem problem) throws OptimiserException {

        double[] solution = null;

        try {
            // Create a new model
            GRBModel model = new GRBModel(env);

            // Create the decision variables from the problem and add to the model
            GRBVar[] vars = addDecisionVariables(problem, model);

            // Update the model after all decision variables have been added
            model.update();

            // Get the objective if present
            GRBExpr expr = addObjective(problem, model, vars);
            if (expr != null) {
                model.setObjective(expr);
            }

            // Add constraints
            GRBConstr[] constraints = addConstraints(problem, model, vars);

            // Optimise the model
            model.optimize();

            // Create solution array from the optimised model
            solution = this.buildSolution(vars);

            // Logging
            log.debug("Obj: " + model.get(GRB.DoubleAttr.ObjVal));

        } catch (GRBException ge) {
            // Repackage any GurobiException and rethrow
            throw new OptimiserException(ge, ge.getErrorCode());
        }

        return solution;
    }


    protected GRBEnv getEnv() {
        return env;
    }


    protected double[] buildSolution(GRBVar[] vars) throws GRBException {

        double[] solution = new double[vars.length];

        for (int i = 0; i < vars.length; i++) {
            solution[i] = vars[i].get(GRB.DoubleAttr.X);
        }

        return solution;
    }


    @Override
    public boolean acceptsIdentifier(String id) {
        return id.equalsIgnoreCase(this.getIdentifier()) || id.equalsIgnoreCase(GurobiOptimiser.class.getName());
    }


    @Override
    public boolean acceptsObjectiveType(Objective.ObjectiveType objectiveType) {
        return objectiveType.isLinear() || objectiveType.isQuadratic();
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
