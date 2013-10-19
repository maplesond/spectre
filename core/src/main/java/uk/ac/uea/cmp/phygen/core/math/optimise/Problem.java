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


public class Problem {


    private Objective objective;
    private double[] nonNegativityConstraint;
    private double[][] solutionSpaceConstraint;

    public Problem() {
        this(null, new double[0], new double[0][0]);
    }

    public Problem(Objective objective, double[] nonNegativityConstraint, double[][] solutionSpaceConstraint) {
        this.objective = objective;
        this.nonNegativityConstraint = nonNegativityConstraint;
        this.solutionSpaceConstraint = solutionSpaceConstraint;
    }

    public Objective getObjective() {
        return objective;
    }

    public void setObjective(Objective objective) {
        this.objective = objective;
    }

    public double[] getNonNegativityConstraint() {
        return nonNegativityConstraint;
    }

    public double getNonNegativityConstraintAt(final int i) {
        return nonNegativityConstraint[i];
    }

    public void setNonNegativityConstraint(double[] nonNegativityConstraint) {
        this.nonNegativityConstraint = nonNegativityConstraint;
    }

    public double[][] getSolutionSpaceConstraint() {
        return solutionSpaceConstraint;
    }

    public void setSolutionSpaceConstraint(double[][] solutionSpaceConstraint) {
        this.solutionSpaceConstraint = solutionSpaceConstraint;
    }

    public int getSolutionSpaceConstraintRows() {
        return this.solutionSpaceConstraint.length;
    }

    public int getSolutionSpaceConstraintColumns() {
        return this.solutionSpaceConstraint.length == 0 ? 0 : this.solutionSpaceConstraint[0].length;
    }

    public double getSolutionSpaceConstraintElement(int i, int j) {
        return this.solutionSpaceConstraint[i][j];
    }
}
