/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2017  UEA School of Computing Sciences
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package uk.ac.uea.cmp.spectre.qtools.superq.problems;

import uk.ac.earlham.metaopt.Objective;
import uk.ac.earlham.metaopt.Problem;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 19/10/13
 * Time: 11:56
 * To change this template use File | Settings | File Templates.
 */
public interface SecondaryProblem {

    /**
     * Creates the problem to solve from SuperQ input for this objective
     *
     * @param nbTaxa
     * @param X
     * @param EtE
     * @return The problem to solve
     */
    Problem compileProblem(int nbTaxa, double[] X, double[][] EtE);

    /**
     * Whether this problem requires a quadratic or linear objective to find a solution
     *
     * @return The objective type
     */
    Objective.ObjectiveType getObjectiveType();

    /**
     * The identifier for this objective, which allows the user to easily identify and request this problem.
     *
     * @return The identifier
     */
    String getName();

    /**
     * Whether or not this objective recognises the input identifier
     *
     * @param id
     * @return True if accept identifier, false otherwise.
     */
    boolean acceptsIdentifier(String id);
}
