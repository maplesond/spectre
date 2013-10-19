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
import org.kohsuke.MetaInfServices;
import uk.ac.uea.cmp.phygen.core.math.optimise.OptimiserException;
import uk.ac.uea.cmp.phygen.core.math.optimise.Problem;

@MetaInfServices(uk.ac.uea.cmp.phygen.core.math.optimise.Optimiser.class)
public class GurobiOptimiserLinear extends GurobiOptimiser {

    public GurobiOptimiserLinear() throws OptimiserException {
        super();
    }

    @Override
    public GRBVar[] addVariables(Problem problem, GRBModel model) throws GRBException {

        double[] nnc = problem.getNonNegativityConstraint();
        double[] coefficients = problem.getObjective().buildCoefficients(nnc.length);

        GRBVar[] vars = new GRBVar[nnc.length];

        for (int i = 0; i < nnc.length; i++) {

            // Adds variable to model and retains a handle to it
            vars[i] = model.addVar(
                    -nnc[i],                   // Lower bound
                    Double.POSITIVE_INFINITY,   // Upper bound
                    coefficients[i],            // Objective
                    GRB.CONTINUOUS,             // Type
                    "x" + i);                   // Name
        }

        return vars;
    }

    @Override
    public GRBConstr[] addConstraints(Problem problem, GRBModel model, GRBVar[] vars) throws GRBException {

        double[][] ssc = problem.getSolutionSpaceConstraint();

        GRBConstr[] constraints = new GRBConstr[vars.length];

        for (int i = 0; i < vars.length; i++) {

            GRBLinExpr expr = new GRBLinExpr();
            for (int j = 0; j < vars.length; j++) {
                expr.addTerm(ssc[i][j], vars[j]);
            }

            constraints[i] = model.addConstr(expr, GRB.EQUAL, 0, "c0");
        }

        return constraints;
    }

    @Override
    public GRBExpr addObjective(Problem problem, GRBModel model, GRBVar[] vars) throws GRBException {
        return null;
    }

}
