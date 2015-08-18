/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2015  UEA School of Computing Sciences
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

package uk.ac.uea.cmp.spectre.core.ds.network.draw;

import uk.ac.uea.cmp.spectre.core.ds.network.Edge;
import uk.ac.uea.cmp.spectre.core.ds.network.EdgeList;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * This class is used to store the edges of a box in the network
 */
public class NetworkBox {

    private Edge e1;
    private Edge e2;
    private Edge f1;
    private Edge f2;

    public NetworkBox(Edge e1, Edge e2, Edge f1, Edge f2) {
        this.e1 = e1;
        this.e2 = e2;
        this.f1 = f1;
        this.f2 = f2;
    }

    public Edge getE1() {
        return e1;
    }

    public Edge getE2() {
        return e2;
    }

    public Edge getF1() {
        return f1;
    }

    public Edge getF2() {
        return f2;
    }

    /**
     * This static constructor method checks if two splits form a box in the network
     * @param a Split A
     * @param b Split B
     * @param splitedges Set of splits
     * @return A network box formed from the splits if possible, null otherwise
     */
    public static NetworkBox formBox(int a, int b, TreeSet<Edge>[] splitedges) {
        NetworkBox netbox = null;

        Iterator<Edge> iter = splitedges[a].iterator();
        Edge h = splitedges[a].last();

        while (iter.hasNext()) {
            Edge e1 = iter.next();
            if (e1 != h) {

                EdgeList e1TopEdges = e1.getTop().getEdgeList();
                EdgeList e1BottomEdges = e1.getBottom().getEdgeList();

                Edge f1 = e1TopEdges.get((e1TopEdges.indexOf(e1) + e1TopEdges.size() - 1) % e1TopEdges.size());
                Edge f2 = e1BottomEdges.get((e1BottomEdges.indexOf(e1) + 1) % e1BottomEdges.size());

                if (f1.getIdxsplit() == b) {
                    netbox = new NetworkBox(e1, iter.next(), f1, f2);
                    break;
                }
            }
        }
        return netbox;
    }
}