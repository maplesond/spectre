/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2014  UEA School of Computing Sciences
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
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

package uk.ac.uea.cmp.spectre.core.alg.nn;

import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;

import java.util.LinkedList;

/**
 * Created by dan on 11/03/14.
 */
public class NetworkLayer extends LinkedList<NetworkNode> {

    /**
     * Computes the Rx (a matrix operation)
     *
     * @param z        a node
     * @param Cx       a node
     * @param Cy       a node
     * @param D        the distances
     * @param netNodes the net nodes
     * @return the Rx value
     */
    protected double computeRx(NetworkNode z, NetworkNode Cx, NetworkNode Cy, DistanceMatrix D,
                               NetworkNode netNodes) {
        double Rx = 0.0;

        for (NetworkNode p : this) {

            double dist = D.getDistance(z.id, p.id);

            if (!(p == Cx || p == Cx.adjacent || p == Cy || p == Cy.adjacent || p.adjacent == null)) {
                dist /= 2.0;
            }

            Rx += dist;
        }

        return Rx;
    }
}
