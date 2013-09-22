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
package uk.ac.uea.cmp.phygen.core.math.optimise.glpk;

import org.gnu.glpk.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.math.optimise.*;

public class GLPKOptimiser extends AbstractOptimiser {

    private static Logger logger = LoggerFactory.getLogger(GLPKOptimiser.class);

    @Override
    protected double[] internalOptimise(Problem problem) {

        double[] sol = new double[problem.getCoefficients().length];

        double[] restriction = problem.getRestriction();

        double[] coeff = problem.getCoefficients();

        double[][] matrix = problem.getMatrix();

        SWIGTYPE_p_int ia;
        SWIGTYPE_p_double ar;

        int ret;
//        int[] ia = new int[matrix[1].length * matrix.length];
//        
//       int[] ja = new int[matrix[1].length * matrix.length];
//       double[] ar =new double[matrix[1].length * matrix.length];
//        
//     
//    
//        
        //Initialize Problem

        glp_prob lp = GLPK.glp_create_prob();
        glp_smcp parm;
//        GlpkSolver lp = new GlpkSolver();
        GLPK.glp_set_prob_name(lp, "LP");
        logger.info("Problem created");

        //set optimisation direction

        GLPK.glp_set_obj_dir(lp, GLPK.GLP_MIN);

        //Create Variables (columns)
//        
        GLPK.glp_add_cols(lp, problem.getCoefficients().length);
        for (int i = 1; i < problem.getCoefficients().length + 1; i++) {
            String s = "x" + i;
            GLPK.glp_set_col_name(lp, i, s);
            GLPK.glp_set_col_bnds(lp, i, GLPKConstants.GLP_LO, -restriction[i - 1], 0.0);
//            GLPK.glp_set_obj_coef(lp, i, coeff[i-1]);

        }
        GLPK.glp_add_rows(lp, problem.getMatrix().length);
        for (int i = 1; i < problem.getMatrix().length + 1; i++) {
            String s = "y" + i;
            GLPK.glp_set_row_name(lp, i, s);
            GLPK.glp_set_row_bnds(lp, i, GLPKConstants.GLP_FX, 0.0, 0);
        }
//        
//        //Set up constrains
        ia = GLPK.new_intArray(matrix.length * coeff.length);
        ar = GLPK.new_doubleArray(matrix.length * coeff.length);

        for (int i = 1; i < matrix.length + 1; i++) {
            int k = 1;
            for (int j = 1; j < coeff.length + 1; j++) {
//          GLPK.intArray_setitem(ia, k, i);
                GLPK.intArray_setitem(ia, k, j);
                GLPK.doubleArray_setitem(ar, k, matrix[i - 1][j - 1]);

//                System.out.println("k: "+k);
//                System.out.println("ia: "+GLPK.intArray_getitem(ia, k));
//                System.out.println("ar: "+GLPK.doubleArray_getitem(ar, k));
                
//            ia[k] = i;
//            ja[k] = j;
//            ar[k] = matrix[i-1][j-1];
//          
                k++;
            }
            GLPK.glp_set_mat_row(lp, i, coeff.length, ia, ar);
//            System.out.println("row"+i+": "+GLPK.glp_get_mat_row(lp, i, ia, ar));
        }
//        
        GLPK.glp_print_mip(lp, "lp");
        // Set Objective

        GLPK.glp_set_obj_name(lp, "z");
        GLPK.glp_set_obj_dir(lp, GLPKConstants.GLP_MIN);

        for (int i = 0; i < coeff.length; i++) {

            GLPK.glp_set_obj_coef(lp, i, coeff[i]);

        }

// Solve model
        parm = new glp_smcp();
        GLPK.glp_init_smcp(parm);
        ret = GLPK.glp_simplex(lp, parm);
// Retrieve solution
        if (ret == 0) {
            int i;
            int n;
            String name;

            name = GLPK.glp_get_obj_name(lp);

            logger.debug(name + " = " + GLPK.glp_get_obj_val(lp));
            n = GLPK.glp_get_num_cols(lp);
            for (i = 1; i <= n; i++) {
                name = GLPK.glp_get_col_name(lp, i);
                sol[i - 1] = GLPK.glp_get_col_prim(lp, i);
                logger.debug(name + " = " + sol[i - 1]);
            }
        } else {
            logger.info("The problem could not be solved");
        }
//        GLPK.glp_load_matrix(lp,matrix.length*matrix[1].length,ia,ja,ar);
//
//
//        for (int i = 0; i < problem.getCoefficients().length; i++) {
//            sol[i] = GLPK.glp_get_col_prim(lp, i);
//        }
//
//   


//   
        return sol;




    }


    @Override
    public boolean acceptsIdentifier(String id) {
        return id.equalsIgnoreCase(this.getDescription()) || id.equalsIgnoreCase(GLPKOptimiser.class.getName());
    }

    @Override
    public boolean acceptsObjective(Objective objective) {
        return GLPKObjective.acceptsObjective(objective);
    }

    @Override
    public String getDescription() {
        return "GLPK";
    }


    @Override
    public boolean isOperational() {

        try {
            GLPK.glp_version();
        } catch (Throwable err) {
            // This means that GLPK isn't on the PATH env var.
            return false;
        }

        return true;
    }

    @Override
    public boolean hasObjectiveFactory() {
        return false;
    }

    @Override
    public OptimiserObjectiveFactory getObjectiveFactory() {
        return null;
    }


    /**
     * GLPK is currently only setup to support linear and minima objectives
     */
    private enum GLPKObjective {

        LINEAR {
            @Override
            public boolean supported() {
                return true;
            }
        },
        QUADRATIC {
            @Override
            public boolean supported() {
                return false;
            }
        },
        BALANCED {
            @Override
            public boolean supported() {
                return false;
            }
        },
        MINIMA {
            @Override
            public boolean supported() {
                return true;
            }
        },
        NNLS {
            @Override
            public boolean supported() {
                return false;
            }
        };

        public abstract boolean supported();

        public static boolean acceptsObjective(Objective objective) {
            return GLPKObjective.valueOf(objective.name()).supported();
        }
    }
}
