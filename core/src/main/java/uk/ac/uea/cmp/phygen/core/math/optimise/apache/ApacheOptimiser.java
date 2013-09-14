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
import org.gnu.glpk.GLPK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.math.optimise.*;

import java.util.ArrayList;
import java.util.Collection;


public class ApacheOptimiser extends AbstractOptimiser {

    private static Logger logger = LoggerFactory.getLogger(ApacheOptimiser.class);

    @Override
    protected double[] optimise2(Objective objective, Problem problem) {

        LinearObjectiveFunction f = new LinearObjectiveFunction(problem.getCoefficients(), 0.0);
        
        
        Collection constraints = new ArrayList();
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
            double[] constraint = new double[problem.getCoefficients().length];
        
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
        if (objective == Objective.LINEAR) {
            return true;
        }
        else if (objective == Objective.QUADRATIC) {
            return false;
        }
        else if (objective == Objective.MINIMA) {
            return true;
        }
        else if (objective == Objective.NNLS) {
            return false;
        }
        else {
            return false;
        }
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
}
