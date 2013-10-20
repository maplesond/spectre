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
import org.kohsuke.MetaInfServices;
import uk.ac.uea.cmp.phygen.core.math.optimise.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@MetaInfServices(uk.ac.uea.cmp.phygen.core.math.optimise.Optimiser.class)
public class ApacheOptimiser extends AbstractOptimiser {

    public ApacheOptimiser() throws OptimiserException {
        super();
    }


    @Override
    protected double[] internalOptimise(Problem problem) {

        double[][] ssc = problem.getSolutionSpaceConstraint();
        double[] coefficients = problem.getCoefficients();

        LinearObjectiveFunction f = new LinearObjectiveFunction(coefficients, 0.0);

        int columns = ssc.length > 0 ? ssc[0].length : 0;

        Collection<LinearConstraint> constraints = new ArrayList<>();
        for (int i = 0; i < ssc.length; i++) {

            double[] constraint = new double[columns];

            for (int j = 0; j < columns; j++) {
                constraint[j] = ssc[i][j];
            }

            constraints.add(new LinearConstraint(constraint, Relationship.EQ, 0.0));
        }

        // Add restriction constraint
        for (int i = 0; i < columns; i++) {
            double[] constraint = new double[coefficients.length];

            for (int j = 0; j < columns; j++) {

                constraint[j] = i == j ? 1.0 : 0.0;
            }

            constraints.add(new LinearConstraint(constraint, Relationship.GEQ, -problem.getNonNegativityConstraint()[i]));
        }

        // create and run the solver
        SimplexSolver solver = new SimplexSolver();
        solver.setMaxIterations(10000);
        PointValuePair pvp = solver.optimize(f, constraints, GoalType.MINIMIZE, false);

        return pvp.getPointRef();
    }


    @Override
    public boolean acceptsIdentifier(String id) {
        return id.equalsIgnoreCase(this.getIdentifier()) || id.equalsIgnoreCase(ApacheOptimiser.class.getName());
    }

    @Override
    public boolean acceptsObjectiveType(Objective.ObjectiveType objectiveType) {
        return objectiveType.isLinear();
    }

    @Override
    public String getIdentifier() {
        return "Apache";
    }


    /**
     * Apache should always be operational, as it's an open source java library available via maven
     *
     * @return
     */
    @Override
    public boolean isOperational() {
        return true;
    }


}
