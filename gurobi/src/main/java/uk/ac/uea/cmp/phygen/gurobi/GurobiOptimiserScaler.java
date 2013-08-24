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
import uk.ac.uea.cmp.phygen.core.math.optimise.OptimiserException;


public class GurobiOptimiserScaler extends GurobiOptimiser {

    public GurobiOptimiserScaler() throws OptimiserException {
        super();
    }

    @Override
    public void setVariables() throws GRBException {
        for (int i = 0; i < this.getMatrixRows(); i++) {
            GRBVar x = this.getModel().addVar(0, Double.POSITIVE_INFINITY, 1.0, GRB.CONTINUOUS, "x" + i);
            this.setVariableAt(i, x);
        }
    }

    @Override
    public void addConstraints() throws GRBException {
        GRBLinExpr expr = new GRBLinExpr();

        for (int i = 0; i < this.getMatrixRows(); i++) {
            expr.addTerm(1.0, this.getVariableAt(i));
        }
        this.getModel().addConstr(expr, GRB.EQUAL, 1.0, "c0");
    }

    @Override
    public GRBExpr getObjective() throws GRBException {
        GRBQuadExpr obj = new GRBQuadExpr();
        for (int i = 0; i < this.getMatrixRows(); i++) {
            for (int j = 0; j < this.getMatrixColumns(); j++) {
                obj.addTerm(this.getMatrixAt(i,j), this.getVariableAt(i), this.getVariableAt(j));
            }
        }

        return obj;
    }

}
