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

import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.linear.LinearConstraint;
import org.apache.commons.math3.optimization.linear.LinearObjectiveFunction;
import org.apache.commons.math3.optimization.linear.Relationship;
import org.apache.commons.math3.optimization.linear.SimplexSolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.math.optimise.*;

import java.util.ArrayList;
import java.util.Collection;


public class ApacheOptimiser extends AbstractOptimiser {

    public ApacheOptimiser() throws OptimiserException {
        this(Objective.LINEAR);
    }

    public ApacheOptimiser(Objective objective) throws OptimiserException {
        super();
        this.setObjective(objective);
    }

    @Override
    protected double[] internalOptimise(Problem problem, double[] coefficients) {

        LinearObjectiveFunction f = new LinearObjectiveFunction(coefficients, 0.0);
        
        
        Collection<LinearConstraint> constraints = new ArrayList<>();
        for(int i = 0; i < problem.getMatrixRows(); i++) {
            double[] constraint = new double[problem.getMatrixColumns()];
        
            int cols = problem.getMatrixColumns();
            
            for(int j = 0; j < cols; j++) {
                constraint[j] = problem.getMatrixElement(i,j);
            }
            
            constraints.add(new LinearConstraint(constraint, Relationship.EQ, 0.0));
        }
        
        // Add restriction constraint
        for(int i = 0; i < problem.getMatrixColumns(); i++) {
            double[] constraint = new double[coefficients.length];
        
            for(int j = 0; j < problem.getMatrixColumns(); j++) {
                
                constraint[j] = i == j ? 1.0 : 0.0;
            }
            
            constraints.add(new LinearConstraint(constraint, Relationship.GEQ, -problem.getRestriction()[i]));
        }
        
        // create and run the solver
        SimplexSolver solver = new SimplexSolver();
        solver.setMaxIterations(10000);
        PointValuePair pvp = solver.optimize(f, constraints, GoalType.MINIMIZE, false);

        return pvp.getPointRef();
    }


    @Override
    public boolean acceptsIdentifier(String id) {
        return id.equalsIgnoreCase(this.getDescription()) || id.equalsIgnoreCase(ApacheOptimiser.class.getName());
    }

    @Override
    public boolean acceptsObjective(Objective objective) {
        return ApacheObjective.acceptsObjective(objective);
    }

    @Override
    public String getDescription() {
        return "Apache";
    }


    /**
     * Apache should always be operational, as it's an open source java library available via maven
     * @return
     */
    @Override
    public boolean isOperational() {

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

    @Override
    public boolean requiresInitialisation() {
        return false;
    }

    @Override
    public void initialise() {
    }


    /**
     * Apache is currently only setup to support linear and minima objectives
     */
    private enum ApacheObjective {

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
            return ApacheObjective.valueOf(objective.name()).supported();
        }
    }
}
