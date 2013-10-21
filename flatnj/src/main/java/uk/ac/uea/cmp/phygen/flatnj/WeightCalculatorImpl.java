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

package uk.ac.uea.cmp.phygen.flatnj;

import uk.ac.uea.cmp.phygen.core.math.optimise.*;
import uk.ac.uea.cmp.phygen.flatnj.ds.PermutationSequence;
import uk.ac.uea.cmp.phygen.flatnj.ds.QuadrupleSystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Computes split weights in the resulting {@linkplain  PermutationSequence}
 * using <a href="http://www.gurobi.com/">Gurobi solver</a>.
 *
 * @author balvociute
 */

public class WeightCalculatorImpl implements WeightCalculator {
    PermutationSequence ps;
    QuadrupleSystem qs;

    /**
     * Constructor used to initiate {@linkplain  WeightCalculator} and set
     * {@linkplain  PermutationSequence} and {@linkplain  QuadrupleSystem}.
     *
     * @param ps {@link PermutationSequence}.
     * @param qs {@link QuadrupleSystem}.
     */
    public WeightCalculatorImpl(PermutationSequence ps, QuadrupleSystem qs) {
        this.ps = ps;
        this.qs = qs;
    }


    protected List<Variable> createVariables(final int size) {

        double[] coefficients = new double[size];
        Arrays.fill(coefficients, 1.0);

        List<Variable> variables = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            variables.add(new Variable(
                    "x" + i,                                 // Name
                    coefficients[i],                         // Coefficient
                    new Bounds(0.0, Bounds.BoundType.LOWER), // Bounds
                    Variable.VariableType.CONTINUOUS         // Type
            ));
        }

        return variables;
    }

    /**
     * Ensures all variables are non-negative (are these actually required?  Might want to try removing these and see
     * if we get the same result, as we have already requested that the variables are non-negative.  Probably these
     * constraints will just slow the solver down.)
     * @param variables
     * @return
     */
    protected List<Constraint> createConstraints(List<Variable> variables) {

        List<Constraint> constraints = new ArrayList<>(variables.size());

        for (Variable var : variables) {

            LinearExpression expr = new LinearExpression().addTerm(1.0, var);

            constraints.add(new Constraint("c0", expr, Constraint.Relation.GREATER_THAN_OR_EQUAL_TO, 0.0));
        }

        return constraints;
    }

    protected QuadraticObjective createObjective(List<Variable> variables) {

        // Extract linear and quadratic terms from ps and qs

        double[] linearCoefficients = ps.computebVector(qs);

        int[][] B = ps.computeBMatrix();

        double[][] quadraticCoefficients = new double[B.length][B[0].length];

        for (int i = 0; i < B.length; i++) {
            for (int j = 0; j < B[i].length; j++) {
                quadraticCoefficients[i][j] = B[i][j];
            }
        }

        double constant = qs.computeWxWT();

        // Create the phygen quadratic objective
        QuadraticExpression quadExpr = new QuadraticExpression();

        quadExpr.addConstant(constant);

        // Add linear terms
        for (int i = 0; i < variables.size(); i++) {
            quadExpr.addTerm(-2 * linearCoefficients[i], variables.get(i));
        }

        // Add quadratic terms
        for (int i = 0; i < variables.size(); i++) {
            for (int j = 0; j < variables.size(); j++) {
                quadExpr.addTerm(quadraticCoefficients[j][i], variables.get(j), variables.get(i));
            }
        }

        return new QuadraticObjective(Objective.ObjectiveDirection.MINIMISE, quadExpr);
    }

    @Override
    public void fitWeights(Optimiser optimiser) {
        //qs.normalizeWeights();

        List<Variable> variables = this.createVariables(ps.getnSwaps());
        List<Constraint> constraints = this.createConstraints(variables);
        Objective objective = this.createObjective(variables);

        try {
            double[] weights = optimiser.optimise(new Problem(variables, constraints, objective));

            // ps.setFit();???
            ps.setWeights(weights);
            ps.setTrivial(qs.getTrivial());
        } catch (OptimiserException oe) {
            System.err.println(oe.getMessage());
        }



        /*
        try
        {
            GRBEnv env = new GRBEnv();
            GRBModel model = new GRBModel(env);
            GRBVar[] variables = new GRBVar[b.length];

            // Create variables

            for (int i = 0; i < b.length; i++)
            {
                GRBVar x = model.addVar(0, Double.POSITIVE_INFINITY, 1.0, GRB.CONTINUOUS, "x" + i);
                variables[i] = x;
            }
            model.update();

            GRBQuadExpr obj = new GRBQuadExpr();

            obj.addConstant(wwT);        

            for (int i = 0; i < b.length; i++)
            {
                obj.addTerm( -2*b[i], variables[i]);
            }

            for (int i = 0; i < b.length; i++)
            {
                for (int j = 0; j < b.length; j++)
                {
                    obj.addTerm(B[j][i],variables[j],variables[i]);
                }
            }

            model.setObjective(obj);

            for (int i = 0; i < b.length; i++) 
            {
                GRBLinExpr expr = new GRBLinExpr();

                expr.addTerm(1.0, variables[i]);
                model.addConstr(expr, GRB.GREATER_EQUAL, 0.0, "c0");
            }
            model.getEnv().set(GRB.IntParam.OutputFlag, 0);

            model.optimize();

            for (int i = 0; i < b.length; i++)
            {
                weights[i] = variables[i].get(GRB.DoubleAttr.X);
            }
            
            ps.setFit(model.get(GRB.DoubleAttr.ObjVal));
        }
        catch(GRBException grbe)
        {
            System.err.println(grbe.getMessage());
        }  */

    }

}
