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
package uk.ac.uea.cmp.phygen.core.math.optimise;

import uk.ac.uea.cmp.phygen.core.math.optimise.apache.ApacheOptimiserSystem;
import uk.ac.uea.cmp.phygen.core.math.optimise.glpk.GLPKOptimiserSystem;
import uk.ac.uea.cmp.phygen.core.math.optimise.gurobi.GurobiOptimiserSystem;
import uk.ac.uea.cmp.phygen.core.math.optimise.phygen.PhygenOptimiserSystem;


public enum Solver {

    BEST_AVAILABLE {
        @Override
        public OptimiserSystem getOptimiserSystem() {
            throw new UnsupportedOperationException("This entry cannot itself generate an optimiser system");
        }
    },
    GUROBI {
        @Override
        public OptimiserSystem getOptimiserSystem() {
            return new GurobiOptimiserSystem();
        }
    },
    GLPK {
        @Override
        public OptimiserSystem getOptimiserSystem() {
            return new GLPKOptimiserSystem();
        }
    },
    APACHE {
        @Override
        public OptimiserSystem getOptimiserSystem() {
            return new ApacheOptimiserSystem();
        }
    },
    NNLS {
        @Override
        public OptimiserSystem getOptimiserSystem() {
            return new PhygenOptimiserSystem();
        }
    },
    NONE {
        @Override
        public OptimiserSystem getOptimiserSystem() {
            throw new UnsupportedOperationException();
        }
    };
    
    public abstract OptimiserSystem getOptimiserSystem();
    
    
    public static Solver getBestOperationalSolver() {
        for(Solver os : Solver.values()) {
            if (os != Solver.BEST_AVAILABLE && os != Solver.NONE && os != Solver.APACHE && os != Solver.NNLS && os.getOptimiserSystem().isOperational())
                return os;
        }
        
        throw new UnsupportedOperationException("No Optimiser Systems are operational");
    }

    public double[] optimise(double[] restrictions, double[][] matrix, Objective objective) throws OptimiserException {

        double[] coefficients = objective.buildCoefficients(restrictions.length);
        Problem problem = new Problem(restrictions, matrix, coefficients);
        
        Solver s = this;
        if (this == Solver.BEST_AVAILABLE) {
            s = Solver.getBestOperationalSolver();
        }
        
        if (!s.getOptimiserSystem().isOperational()) {
            throw new UnsupportedOperationException(this.toString() + " is not operational");
        }
        
        double[] solution = objective.optimise(problem, s.getOptimiserSystem());

        // Probably used a lot of memory.  Collect Garbage to save space.
        System.gc();
        
        return solution;
    }

}
