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
import uk.ac.uea.cmp.phygen.core.math.optimise.OptimiserException;
import uk.ac.uea.cmp.phygen.core.math.optimise.Problem;

/**
 *
 */
public class GurobiOptimiserFlatNJ extends GurobiOptimiser {

    private static Logger log = LoggerFactory.getLogger(GurobiOptimiserFlatNJ.class);

    public GurobiOptimiserFlatNJ() throws OptimiserException {
        super();
    }

    @Override
    public GRBVar[] addVariables(Problem problem, GRBModel model) throws GRBException {

        double[] nnc = problem.getNonNegativityConstraint();
        double[] coefficients = problem.getObjective().buildCoefficients(nnc.length);

        GRBVar[] vars = new GRBVar[nnc.length];

        for (int i = 0; i < nnc.length; i++) {

            GRBVar x = model.addVar(
                    0,                         // Lower Bound
                    Double.POSITIVE_INFINITY,  // Upper Bound
                    coefficients[i],           // Objective coefficient
                    GRB.CONTINUOUS,            // Type
                    "x" + i);                  // Name

            vars[i] = x;
        }

        return vars;
    }

    @Override
    public GRBConstr[] addConstraints(Problem problem, GRBModel model, GRBVar[] vars) throws GRBException {

        GRBConstr[] constraints = new GRBConstr[vars.length];

        for (int i = 0; i < vars.length; i++) {

            GRBLinExpr expr = new GRBLinExpr();
            expr.addTerm(1.0, vars[i]);
            constraints[i] = model.addConstr(expr, GRB.GREATER_EQUAL, 0.0, "c0");
        }

        return constraints;
    }

    @Override
    public GRBExpr addObjective(Problem problem, GRBModel model, GRBVar[] vars) throws GRBException {

        double[] nnc = problem.getNonNegativityConstraint();
        double[][] ssc = problem.getSolutionSpaceConstraint();


        GRBQuadExpr obj = new GRBQuadExpr();

        obj.addConstant(problem.getObjective().getConstant());

        for (int i = 0; i < nnc.length; i++)
        {
            obj.addTerm( -2 * nnc[i], vars[i]);
        }

        for (int i = 0; i < nnc.length; i++) {
            for (int j = 0; j < nnc.length; j++) {
                obj.addTerm(ssc[j][i], vars[j], vars[i]);
            }
        }
        model.setObjective(obj);

        return obj;
    }

}
