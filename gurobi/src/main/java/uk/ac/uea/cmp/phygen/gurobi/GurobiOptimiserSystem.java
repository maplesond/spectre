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

import gurobi.GRBEnv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.math.optimise.Objective;
import uk.ac.uea.cmp.phygen.core.math.optimise.OptimiserException;
import uk.ac.uea.cmp.phygen.core.math.optimise.OptimiserSystem;
import uk.ac.uea.cmp.phygen.core.math.optimise.Problem;


public class GurobiOptimiserSystem implements OptimiserSystem {

    private static Logger logger = LoggerFactory.getLogger(GurobiOptimiserSystem.class);

    private enum GurobiObjective {

        LINEAR {

            @Override
            public GurobiOptimiser create() throws OptimiserException {
                return new GurobiOptimiserLinear();
            }
        },
        QUADRATIC {

            @Override
            public GurobiOptimiser create() throws OptimiserException {
                return new GurobiOptimiserQuadratic();
            }
        },
        MINIMA {

            @Override
            public GurobiOptimiser create() throws OptimiserException {
                return new GurobiOptimiserLinear();
            }
        },
        BALANCED {

            @Override
            public GurobiOptimiser create() throws OptimiserException {
                return new GurobiOptimiserLinear();
            }
        };

        public abstract GurobiOptimiser create() throws OptimiserException;

        public static GurobiObjective translate(Objective objective) {
            return GurobiObjective.valueOf(objective.name());
        }
    }

    @Override
    public double[] optimise(Objective objective, Problem problem)
            throws OptimiserException {

        if (!this.isOperational()) {
            throw new UnsupportedOperationException("GUROBI is not operational");
        }
        
        GurobiObjective gurobiObjective = GurobiObjective.translate(objective);

        GurobiOptimiser gurobi = gurobiObjective.create();

        double[] solution = gurobi.optimise(problem);

        return solution;
    }

    @Override
    public boolean isOperational() {
        
        try {
            GRBEnv env = new GRBEnv();
        }
        catch(Throwable t) {
            // Can't find the gurobi native libraries, so it's not operational
            return false;
        }
        
        
        return true;
    }
}
