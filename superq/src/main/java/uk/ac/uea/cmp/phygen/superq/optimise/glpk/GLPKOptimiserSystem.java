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
package uk.ac.uea.cmp.phygen.superq.optimise.glpk;

import org.gnu.glpk.GLPK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.superq.optimise.Objective;
import uk.ac.uea.cmp.phygen.superq.optimise.OptimiserException;
import uk.ac.uea.cmp.phygen.superq.optimise.OptimiserSystem;
import uk.ac.uea.cmp.phygen.superq.optimise.Problem;


public class GLPKOptimiserSystem implements OptimiserSystem {

    private static Logger logger = LoggerFactory.getLogger(GLPKOptimiserSystem.class);

    protected enum GLPKObjective {

        LINEAR {

            @Override
            public GLPKOptimiser create() {
                return new GLPKOptimiser();
            }
        },
        QUADRATIC {

            @Override
            public GLPKOptimiser create() {
                throw new UnsupportedOperationException("Quadratic objective is not supported by GLPK");
            }
        },
        MINIMA {

            @Override
            public GLPKOptimiser create() {
                return new GLPKOptimiser();
            }
        },
        BALANCED {

            @Override
            public GLPKOptimiser create() {
                return new GLPKOptimiser();
            }
        };

        public abstract GLPKOptimiser create();

        public static GLPKObjective translate(Objective objective) {
            return GLPKObjective.valueOf(objective.name());
        }
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
    public double[] optimise(Objective objective, Problem problem) throws OptimiserException {
        
        if (!this.isOperational()) {
            throw new UnsupportedOperationException("GLPK is not operational");
        }
        
        GLPKObjective glpkObjective = GLPKObjective.translate(objective);
        GLPKOptimiser glpk = glpkObjective.create();
        double[] solution = glpk.optimise(problem);
        
        return solution;
    }
}
