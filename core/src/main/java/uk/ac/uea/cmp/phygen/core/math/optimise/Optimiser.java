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


public interface Optimiser {

    /**
     * Given a problem, finds the solution
     *
     * @param problem
     * @return
     * @throws OptimiserException
     */
    double[] optimise(Problem problem) throws OptimiserException;

    /**
     * The identifier which which the user can locate this optimsier
     *
     * @return
     */
    String getIdentifier();

    /**
     * Whether or not this optimiser recognises the given id.
     *
     * @param id
     * @return
     */
    boolean acceptsIdentifier(String id);

    /**
     * Whether or not this optimiser can process a certain kind of objective.  i.e. linear or quadratic objectives
     *
     * @param objective
     * @return
     */
    boolean acceptsObjectiveType(Objective.ObjectiveType objective);


    /**
     * Whether or not this optimiser is currently operational.  This is useful for optimisers that are external to phygen,
     * in order to ensure the necessary links are in place so that phygen can access the functions of the optimiser.
     *
     * @return
     */
    boolean isOperational();

    /**
     * Any additional setup to the optimiser that is required to take place after construction can be done here.
     *
     * @throws OptimiserException
     */
    void initialise() throws OptimiserException;
}
