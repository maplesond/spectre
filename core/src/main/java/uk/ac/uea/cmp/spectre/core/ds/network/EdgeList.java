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

import java.util.Collection;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.TreeSet;

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
     * Creates a deep copy of this edge list
     * @return A proper copy
     */
    public EdgeList copy() {
        EdgeList copy = new EdgeList();

        for(Edge e : this) {
            copy.add(e.copy());
        }

        return copy;
    }

    /**
     * This method collects the indices of the splits corresponding to the edges in crossboth
     * @return Indicies of splits that cross edges
     */
    public TreeSet<Integer> getCrossIndices() {
        ListIterator<Edge> crossiter = this.listIterator();
        TreeSet<Integer> crossindices = new TreeSet<>();
        while (crossiter.hasNext()) {
            Edge e = crossiter.next();
            crossindices.add(new Integer(e.getIdxsplit()));
        }
        return crossindices;
    }
}
