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
package uk.ac.uea.cmp.phygen.core.math.optimise.apache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.math.optimise.Objective;
import uk.ac.uea.cmp.phygen.core.math.optimise.OptimiserException;
import uk.ac.uea.cmp.phygen.core.math.optimise.OptimiserSystem;
import uk.ac.uea.cmp.phygen.core.math.optimise.Problem;


public class ApacheOptimiserSystem implements OptimiserSystem {

    private static Logger logger = LoggerFactory.getLogger(ApacheOptimiserSystem.class);

    protected enum ApacheObjective {

        LINEAR {

            @Override
            public ApacheOptimiser create() {
                return new ApacheOptimiser();
            }
        },
        QUADRATIC {

            @Override
            public ApacheOptimiser create() {
                throw new UnsupportedOperationException("Quadratic objective is not supported by APACHE");
            }
        },
        MINIMA {

            @Override
            public ApacheOptimiser create() {
                return new ApacheOptimiser();
            }
        },
        BALANCED {

            @Override
            public ApacheOptimiser create() {
                return new ApacheOptimiser();
            }
        };

        public abstract ApacheOptimiser create();

        public static ApacheObjective translate(Objective objective) {
            return ApacheObjective.valueOf(objective.name());
        }
    }

    @Override
    public boolean isOperational() {
        return true;
    }

    @Override
    public double[] optimise(Objective objective, Problem problem) throws OptimiserException {
        
        if (!this.isOperational()) {
            throw new UnsupportedOperationException("APACHE is not operational");
        }
        
        ApacheObjective apacheObjective = ApacheObjective.translate(objective);
        ApacheOptimiser apache = apacheObjective.create();
        double[] solution = apache.optimise(problem);
        
        return solution;
    }
}
