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

package uk.ac.uea.cmp.phygen.superq.ui;

import gurobi.*;
import uk.ac.uea.cmp.phygen.core.math.matrix.SymmetricMatrix;
import uk.ac.uea.cmp.phygen.superq.optimise.Objective;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * THIS CLASS NEEDS A COMPLETE REWRITE!!!
 */
public class LP {

    static int arr;
    static int rows, cols; //number of colums & rows in topological Matrix
    //static LpSolve solver = null;
    static int[] ia;
    static int[] ja;
    static double[] ar;
    static double[] v;
    static double[][] w; // topological Matrix
    public static double[] sol; //Solution of optimisation problem
    public static double[] coeff; //Coefficients for Objective

    //do we need this?
    public static void set_up_constraints(SymmetricMatrix matrix, double[] y) throws FileNotFoundException {
        int i, j;
        rows = matrix.getSize();
        cols = matrix.getSize();
        setMatrixW();
//        try {
//            solver = LpSolve.makeLp(0, y.length);
//
//            solver.setScaling(LpSolve.SCALE_RANGE + LpSolve.SCALE_DYNUPDATE);
//            solver.setEpslevel(2);
//
//            double[] currow = new double[y.length + 1];

//
//            for (i = 0; i < matrix.getSize(); i++) {
//                for (j = 0; j < matrix.getSize(); j++) {
//                    currow[j + 1] = matrix.getElementAt(i, j);
//                }
//                solver.addConstraint(currow, LpSolve.EQ, 0.0);
//            }
//
//            for (i = 0; i < matrix.getSize(); i++) {
//                double x = y[i];
//                solver.setLowbo(i + 1, -x);
//            }
//        } catch (LpSolveException e) {
//            e.printStackTrace();
//        }
//        System.out.println("Constraints set up.\n");
    }

    public static void find_minima(double y[], String filename) throws FileNotFoundException {

        coeff = new double[rows];
        sol = new double[rows];


//        setMatrixW();

        for (int k = 0; k < coeff.length; k++) {
            coeff[k] = 0.0;
        }
        double[] help = new double[rows];
        for (int k = 0; k < rows; k++) {
            System.out.printf("Split number %d\n", k + 1);
            // double x = y[k];
            if (y[k] > 0.0) {
                try {
                    coeff[k] = 1.0;
                    help = gurobi(y);
                    sol[k] = help[k];
                    coeff[k] = 0.0;
                } catch (Exception e) {
                }
            } else {
                sol[k] = 0;
            }

        }

    }

    public static void find_standard_min(double y[], String filename) throws FileNotFoundException {

        coeff = new double[rows + 1];
        sol = new double[rows];

//        setMatrixW();

        for (int i = 0; i < coeff.length; i++) {
            coeff[i] = 1.0;
        }

        try {
            sol = gurobi(y);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void find_balanced_min(double y[], String filename) throws FileNotFoundException {

        /*int N = QNet.theQNet.getTaxonNames().size();
//        setMatrixW();

        int n = 0;
        int m;
        int a;

        coeff = new double[rows + 1];
        sol = new double[rows];

        for (m = 1; m < N - 1; m++) {

            for (int j = m + 2; j < N + 1; j++) {

                if (m != 1 || j != N) {

                    a = j - m;
                    //      SplitIndex [] splitIndices = new SplitIndex [N * (N - 1) / 2 - N];
                    //      Split index is defined as:   splitIndices [n] = new SplitIndex(m, j);
                    coeff[n] = a * (a - 1) * (N - a) * (N - a - 1);
                    n++;
                }
            }
        }
        sol = gurobi(y); */


    }

    public static void find_quad_balanced_min(double y[], String filename) throws FileNotFoundException {

        /*int N = QNet.theQNet.getTaxonNames().size();
//        setMatrixW();

        int n = 0;
        int m;
        int a;

        coeff = new double[rows + 1];
        sol = new double[rows];

        for (m = 1; m < N - 1; m++) {

            for (int j = m + 2; j < N + 1; j++) {

                if (m != 1 || j != N) {

                    a = j - m;
                    //      SplitIndex [] splitIndices = new SplitIndex [N * (N - 1) / 2 - N];
                    //      Split index is defined as:   splitIndices [n] = new SplitIndex(m, j);
                    coeff[n] = a * (a - 1) * (N - a) * (N - a - 1);
                    n++;
                }
            }
        }
        sol = gurobiQ(y, w); */


    }

    public static void main(Objective obj, double filter, int f, String filename) throws FileNotFoundException, IOException, Exception {

        /*String outfile = "";
        if (obj == Objective.MINIMA) {
            //Pointwise Minimum
            set_up_constraints(WeightsComputeNNLSInformative.getEtE(), WeightsComputeNNLSInformative.getx());
            //System.out.print("pointwise minima\n");
            find_minima(WeightsComputeNNLSInformative.getx(), filename);
            outfile = filename;
            WriteWeightsToNexus.writeWeights(QNet.theQNet, QNet.theQNet.getTheLists(), outfile, sol, null, 0);
            //System.out.print("Network is written to file \n");
        } else if (obj == Objective.LINEAR) {
            set_up_constraints(WeightsComputeNNLSInformative.getEtE(), WeightsComputeNNLSInformative.getx());
            //System.out.println("linear");
            find_standard_min(WeightsComputeNNLSInformative.getx(), filename);
            outfile = filename;
            WriteWeightsToNexus.writeWeights(QNet.theQNet, QNet.theQNet.getTheLists(), outfile, sol, null, 0);
            //System.out.print("Network is written to file \n");
        } else if (obj == Objective.QUADRATIC) {
            //Quadratic target function
            //System.out.println("quadratic");
            set_up_constraints(WeightsComputeNNLSInformative.getEtE(), WeightsComputeNNLSInformative.getx());
            setMatrixW();
            outfile = filename;
            coeff = new double[rows + 1];
            for (int i = 0; i < coeff.length; i++) {
                coeff[i] = 1.0;
            }
            WriteWeightsToNexus.writeWeights(QNet.theQNet, QNet.theQNet.getTheLists(), outfile, gurobiQ(WeightsComputeNNLSInformative.getx(), w), null, 0);
            //System.out.println("Network is written to file");

        } else if (obj == Objective.BALANCED) {
            //Balanced
            set_up_constraints(WeightsComputeNNLSInformative.getEtE(), WeightsComputeNNLSInformative.getx());
            //System.out.println("balanced");
            find_balanced_min(WeightsComputeNNLSInformative.getx(), filename);
            outfile = filename;
            WriteWeightsToNexus.writeWeights(QNet.theQNet, QNet.theQNet.getTheLists(), outfile, sol, null, 0);
            //System.out.print("Network is written to file \n");
        } 
        else {
            throw new Exception("LP ERROR: Unkown objective.");
        }*/

        //if (f == 1) {
        //    String[] args = new String[3];
        //    args[0] = outfile;
        //    args[1] = "filtered_" + outfile;
        //    args[2] = "0.001";
        //    uk.ac.uea.cmp.phygen.qnet.Filterer.main(args);
        //}
    }

    private static void filter(double[] SplitWeight, double filtervalue) {

        for (int i = 0; i < SplitWeight.length; i++) {
            if (SplitWeight[i] <= filtervalue) {
                SplitWeight[i] = 0;
            }

        }
    }

// public static void writeFile(String filename, String fileContent) {
//        File outFile = new File(filename);
//        BufferedWriter fileWriter;
//
//        try {
//            outFile.getAbsoluteFile().getParentFile().mkdirs();
//            fileWriter = new BufferedWriter(new FileWriter(outFile));
//            fileWriter.write(fileContent);
//            fileWriter.close();
//            System.out.println("File writen");
//        } catch (IOException ex) {
//            Logger.getAnonymousLogger().severe("IO Exception while trying "
//                                               + "to write file!");
//        }
//    }

    /*
     * Copyright 2010, Gurobi Optimization, Inc.
     */
    public static double[] gurobi(double b[]) {
        double[] solution = new double[b.length];
        try {
            //GRBEnv env = new GRBEnv("gurobi.log");
            GRBEnv env = new GRBEnv();
            env.set(GRB.IntParam.OutputFlag, 0);
            GRBModel model = new GRBModel(env);
            GRBVar[] variables = new GRBVar[b.length];



            // Create variables

            for (int i = 0; i < b.length; i++) {
                GRBVar x = model.addVar(-b[i], Double.POSITIVE_INFINITY, coeff[i], GRB.CONTINUOUS, "x" + i);
                variables[i] = x;
            }
            // Integrate new variables

            model.update();

            // Add constraints



            for (int i = 0; i < w.length; i++) {
                GRBLinExpr expr = new GRBLinExpr();
                for (int j = 0; j < w.length; j++) {
                    expr.addTerm(w[i][j], variables[j]);
                }
                model.addConstr(expr, GRB.EQUAL, 0, "c0");
            }

            // Optimize model

            model.optimize();

            for (int i = 0; i < w.length; i++) {
//                System.out.println(variables[i].get(GRB.StringAttr.VarName) + " " + variables[i].get(GRB.DoubleAttr.X));
                solution[i] = b[i] + variables[i].get(GRB.DoubleAttr.X);
            }


//            System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));

            model = null;
        } catch (GRBException e) {
            System.out.println("Error code: " + e.getErrorCode() + ". "
                    + e.getMessage());
        }
        System.gc();
        return solution;
    }

    public static double[] gurobiQ(double b[], double M[][]) {
        double[] solution = new double[b.length];
        try {
            //GRBEnv env = new GRBEnv("gurobi.log");
            GRBEnv env = new GRBEnv();
            env.set(GRB.IntParam.OutputFlag, 0);
            GRBModel model = new GRBModel(env);
            GRBVar[] variables = new GRBVar[b.length];



            // Create variables

            for (int i = 0; i < b.length; i++) {
                GRBVar x = model.addVar(0, Double.POSITIVE_INFINITY, coeff[i], GRB.CONTINUOUS, "x" + i);
                variables[i] = x;
            }
            // Integrate new variables

            model.update();


            //Add Objective
            GRBQuadExpr obj = new GRBQuadExpr();
            for (int i = 0; i < b.length; i++) {
                obj.addTerm(1.0, variables[i], variables[i]);
            }
            model.setObjective(obj);

            // Add constraints



            for (int i = 0; i < M.length; i++) {
                GRBLinExpr expr = new GRBLinExpr();
                double sum = 0;
                for (int j = 0; j < M.length; j++) {
                    expr.addTerm(M[i][j], variables[j]);
                    sum += M[i][j] * b[j];
                }
                model.addConstr(expr, GRB.EQUAL, sum, "c0");
            }

            // Optimize model

            model.optimize();

            for (int i = 0; i < M.length; i++) {
//                System.out.println(variables[i].get(GRB.StringAttr.VarName) + " " + variables[i].get(GRB.DoubleAttr.X));
                solution[i] = b[i] + variables[i].get(GRB.DoubleAttr.X);
            }


//            System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));

            model = null;
        } catch (GRBException e) {
            System.out.println("Error code: " + e.getErrorCode() + ". "
                    + e.getMessage());
        }
        System.gc();

        return solution;
    }

    private static void setMatrixW() {
        /*SymmetricMatrix matrix = WeightsComputeNNLSInformative.getEtE();
        w = new double[matrix.getSize()][matrix.getSize()];

        for (int i = 0; i < matrix.getSize(); i++) {
            for (int j = 0; j < matrix.getSize(); j++) {
                w[i][j] = matrix.getElementAt(i, j);

            }
        } */
    }
}
