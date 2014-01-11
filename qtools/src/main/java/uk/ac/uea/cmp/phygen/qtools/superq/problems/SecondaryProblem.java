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
package uk.ac.uea.cmp.phygen.qtools.superq.problems;

import uk.ac.tgac.metaopt.Objective;
import uk.ac.tgac.metaopt.Problem;

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
     * @param nbTaxa
     * @param X
     * @param EtE
     * @return
     */
    Problem compileProblem(int nbTaxa, double[] X, double[][] EtE);

    /**
     * Whether this problem requires a quadratic or linear objective to find a solution
     * @return
     */
    Objective.ObjectiveType getObjectiveType();

    /**
     * The identifier for this objective, which allows the user to easily identify and request this problem.
     * @return
     */
    String getName();

    /**
     * Whether or not this objective recognises the input identifier
     * @param id
     * @return
     */
    boolean acceptsIdentifier(String id);
}
