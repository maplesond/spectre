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

/**
 * Created with IntelliJ IDEA.
 * User: Sarah_2
 * Date: 09/05/13
 * Time: 22:35
 * To change this template use File | Settings | File Templates.
 */
public class GurobiOptimiserQ extends GurobiOptimiser {

    public GurobiOptimiserQ() throws OptimiserException {
        super();
    }

    /**
     * Returns a vector with the scaling factors for the input trees/quartets
     */
    /*public static double[] gurobiQ(double H[][]) {


        double[] w = new double[H.length];

        try {
            //Creates model
            //GRBEnv env = new GRBEnv("gurobi.log");
            GRBEnv env = new GRBEnv();
            env.set(GRB.IntParam.OutputFlag, 0);
            GRBModel model = new GRBModel(env);
            GRBVar[] variables = new GRBVar[H.length];



            // Creates variables

            for (int i = 0; i < H.length; i++) {
                GRBVar x = model.addVar(0, Double.POSITIVE_INFINITY, 1.0, GRB.CONTINUOUS, "x" + i);
                variables[i] = x;
            }
            // Integrates new variables

            model.update();


            //Add Objective
            GRBQuadExpr obj = new GRBQuadExpr();
            for (int i = 0; i < H.length; i++) {
                for (int j = 0; j < H.length; j++) {
                    obj.addTerm(H[i][j], variables[i], variables[j]);
                }
            }
            model.setObjective(obj);

            // Add constraints

            GRBLinExpr expr = new GRBLinExpr();

            for (int i = 0; i < H.length; i++) {

                expr.addTerm(1.0, variables[i]);

            }
            model.addConstr(expr, GRB.EQUAL, 1.0, "c0");
            // Optimize model

            model.optimize();

            //Stores solution in vector w
            for (int i = 0; i < w.length; i++) {
//              System.out.println(variables[i].get(GRB.StringAttr.VarName) + " " + variables[i].get(GRB.DoubleAttr.X));
                w[i] = variables[i].get(GRB.DoubleAttr.X);
            }


//            System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));
            //Reset the model
            model = null;
        } catch (GRBException e) {
            System.out.println("Error code: " + e.getErrorCode() + ". "
                    + e.getMessage());
        }
        //Garbage collection to save space
        System.gc();

        //Returns solution vector
        return w;
    }*/

    @Override
    public void setVariables() throws GRBException {
        for (int i = 0; i < this.getLength(); i++) {
            GRBVar x = this.getModel().addVar(0, Double.POSITIVE_INFINITY, 1.0, GRB.CONTINUOUS, "x" + i);
            this.setVariableAt(i, x);
        }
    }

    @Override
    public void addConstraints() throws GRBException {
        GRBLinExpr expr = new GRBLinExpr();

        for (int i = 0; i < this.getLength(); i++) {
            expr.addTerm(1.0, this.getVariableAt(i));
        }
        this.getModel().addConstr(expr, GRB.EQUAL, 1.0, "c0");
    }

    @Override
    public GRBExpr getObjective() throws GRBException {
        GRBQuadExpr obj = new GRBQuadExpr();
        for (int i = 0; i < this.getLength(); i++) {
            for (int j = 0; j < this.getLength(); j++) {
                obj.addTerm(this.getMatrixAt(i,j), this.getVariableAt(i), this.getVariableAt(j));
            }
        }
        return obj;
    }
}
