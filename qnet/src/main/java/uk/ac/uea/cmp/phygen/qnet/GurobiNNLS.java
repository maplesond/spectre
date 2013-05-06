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
package uk.ac.uea.cmp.phygen.qnet;

import gurobi.*;
import uk.ac.uea.cmp.phygen.core.math.matrix.SymmetricMatrix;

/**
 *
 */
public class GurobiNNLS {

    public static void solveNNLS(double[] Etf, SymmetricMatrix EtE, double[] x) {
        try {
            //GRBEnv env = new GRBEnv("gurobi.log");
            GRBEnv env = new GRBEnv();
            env.set(GRB.IntParam.OutputFlag, 0);
            GRBModel model = new GRBModel(env);

            // Create variables

            GRBVar[] variables = new GRBVar[x.length];





            // Create variables

            for (int i = 0; i < x.length; i++) {
                GRBVar y = model.addVar(0, Double.POSITIVE_INFINITY, 1.0, GRB.CONTINUOUS, "x" + i);
                variables[i] = y;
            }
            model.update();

            //System.out.println("EtE: "+EtE.getSize()+ " Etf: " +Etf.length+ " X: "+ x.length);
            // Objective

            GRBQuadExpr obj = new GRBQuadExpr();
            for (int i = 0; i < x.length; i++) {
                for (int j = 0; j < x.length; j++) {
                    obj.addTerm(EtE.getElementAt(j, i), variables[j], variables[i]);
                }
                obj.addTerm(-2 * Etf[i], variables[i]);

            }
            model.setObjective(obj);

            for (int i = 0; i < x.length; i++) {
                GRBLinExpr expr = new GRBLinExpr();

                expr.addTerm(1.0, variables[i]);
                model.addConstr(expr, GRB.GREATER_EQUAL, 0.0, "c0");
            }


            model.optimize();

            for (int i = 0; i < x.length; i++) {
                //System.out.println(variables[i].get(GRB.StringAttr.VarName)+ " " +variables[i].get(GRB.DoubleAttr.X));
                x[i] = variables[i].get(GRB.DoubleAttr.X);
            }


            //System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));


        } catch (GRBException e) {
            System.out.println("Error code: " + e.getErrorCode() + ". "
                               + e.getMessage());
        }
    }
}
