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
public class GurobiOptimiserQuadratic extends GurobiOptimiser {

    public GurobiOptimiserQuadratic() throws OptimiserException {
        super();
    }

    @Override
    public GRBConstr[] addConstraints(Problem problem, GRBModel model, GRBVar[] vars) throws GRBException {

        double[] nnc = problem.getNonNegativityConstraint();
        double[][] ssc = problem.getSolutionSpaceConstraint();

        GRBConstr[] constraints = new GRBConstr[vars.length];

        for (int i = 0; i < ssc.length; i++) {
            GRBLinExpr expr = new GRBLinExpr();
            double sum = 0;
            for (int j = 0; j < ssc.length; j++) {
                expr.addTerm(ssc[i][j], vars[j]);
                sum += ssc[i][j] * nnc[j];
            }
            constraints[i] = model.addConstr(expr, GRB.EQUAL, sum, "c0");
        }

        return constraints;
    }

    @Override
    public GRBExpr addObjective(Problem problem, GRBModel model, GRBVar[] vars) throws GRBException {

        GRBQuadExpr obj = new GRBQuadExpr();
        for (int i = 0; i < vars.length; i++) {
            obj.addTerm(1.0, vars[i], vars[i]);
        }

        model.setObjective(obj);

        return obj;
    }

}