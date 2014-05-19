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

import uk.ac.uea.cmp.spectre.core.ds.Identifier;

/**
 * Represents a NeighborNet node.  Each node is part of a NetworkLayer, which is managed as a doubly linked list.  Each
 * NetworkNode also can point at up to two child nodes.
 * <p/>
 * For simplicity and speed we have not encapsulated the members of this class.
 */
class NetworkNode {

    protected Identifier id;
    protected NetworkNode adjacent;
    protected NetworkNode child1;
    protected NetworkNode child2;
    protected double Rx;
    protected double Sx;

    public NetworkNode(Identifier id) {
        this.id = id;
        this.adjacent = null;
        this.child1 = null;
        this.child2 = null;
        this.Rx = 0;
        this.Sx = 0;
    }


    @Override
    public String toString() {
        String str = "[id=" + id.getId();
        str += " adjacent=" + (adjacent == null ? "null" : ("" + adjacent.id));
        str += " child1=" + (child1 == null ? "null" : ("" + child1.id));
        str += " child2=" + (child2 == null ? "null" : ("" + child2.id));
        str += " Rx=" + Rx;
        str += " Sx=" + Sx;
        str += "]";
        return str;
    }
}
