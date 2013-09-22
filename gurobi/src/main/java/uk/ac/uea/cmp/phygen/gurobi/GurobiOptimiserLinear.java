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
import uk.ac.uea.cmp.phygen.core.math.optimise.Objective;
import uk.ac.uea.cmp.phygen.core.math.optimise.OptimiserException;


public class GurobiOptimiserLinear extends GurobiOptimiser {

    public GurobiOptimiserLinear(Objective objective) throws OptimiserException {
        super();
        this.setObjective(objective);
    }
    
    @Override
    public void setVariables() throws GRBException {
        
        for (int i = 0; i < this.getLength(); i++) {
            GRBVar x = this.getModel().addVar(-this.getRestrictionAt(i), Double.POSITIVE_INFINITY, this.getCoefficientAt(i), GRB.CONTINUOUS, "x" + i);
            this.setVariableAt(i, x);
        }
    }

    @Override
    public void addConstraints() throws GRBException {
        
        for (int i = 0; i < this.getLength(); i++) {
            GRBLinExpr expr = new GRBLinExpr();
            for (int j = 0; j < this.getLength(); j++) {
                expr.addTerm(this.getMatrixAt(i,j), this.getVariableAt(j));
            }
            this.getModel().addConstr(expr, GRB.EQUAL, 0, "c0");
        }
    }

    @Override
    public GRBExpr getObjective() throws GRBException {
        return null;
    }

}
