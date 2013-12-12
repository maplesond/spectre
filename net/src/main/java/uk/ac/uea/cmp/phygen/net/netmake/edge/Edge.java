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

package uk.ac.uea.cmp.phygen.net.netmake.edge;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: Sarah_2
 * Date: 24/04/13
 * Time: 22:28
 * To change this template use File | Settings | File Templates.
 */
public class Edge extends ArrayList<Integer> {
    public Edge() {
        super();
    }

    public Edge(Edge copy) {
        this();

        for (Integer i : copy) {
            this.add(new Integer(i.intValue()));
        }
//        this.addAll(copy);
    }

    public Edge(ArrayList<Integer> copy) {
        this();
        this.addAll(copy);
    }

    public void sort() {
        Collections.sort(this);
    }

    public EdgeType getType() {
        return this.size() == 1 ? EdgeType.EXTERNAL : EdgeType.INTERNAL;
    }

    public enum EdgeType {
        EXTERNAL,
        INTERNAL
    }
}

