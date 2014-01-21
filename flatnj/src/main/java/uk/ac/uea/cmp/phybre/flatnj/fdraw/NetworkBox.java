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

package uk.ac.uea.cmp.phybre.flatnj.fdraw;

//This class is used to store the edges
//of a box in the network

public class NetworkBox {
    Edge e1 = null;
    Edge e2 = null;
    Edge f1 = null;
    Edge f2 = null;

    //constuctor of this class
    public NetworkBox(Edge e1, Edge e2, Edge f1, Edge f2) {
        this.e1 = e1;
        this.e2 = e2;
        this.f1 = f1;
        this.f2 = f2;
    }
}