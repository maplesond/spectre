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

package uk.ac.uea.cmp.spectre.core.ds.network;

import java.util.*;

/**
 * Created by dan on 20/03/14.
 */
public class EdgeList extends LinkedList<Edge> {

    public EdgeList() {
        super();
    }

    public EdgeList(Collection<Edge> e) {
        super(e);
    }

    public EdgeList getCompatible() {
        EdgeList compatible = new EdgeList();
        for (Edge e : this) {
            if (e.isCompatible()) {
                compatible.add(e);
            }
        }
        return compatible;
    }

    /**
     * This method collects the indices of the splits corresponding to the edges in this list
     * @return A distinct set of split indices in this list
     */
    public Set<Integer> getSplitIndexSet() {
        Set<Integer> crossindices = new HashSet<>();
        for (Edge e : this) {
            crossindices.add(e.getSplitIndex());
        }
        return crossindices;
    }

    /**
     * Gets Edge found after the provided one in this edge list.
     * Automatically wraps to beginning of list if index is pushed off the end.
     * @param e The current edge.  We want to return the Edge following this one.
     * @return The edge following the one provided
     */
    public Edge getNextEdge(Edge e) {
        return this.get((this.indexOf(e) + 1) % this.size());
    }
}
