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
package uk.ac.uea.cmp.phygen.qnet;

import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA. User: Analysis Date: 2004-jul-01 Time: 14:49:07 To
 * change this template use Options | File Templates.
 */
public class SolutionHypothesis {

    public SolutionHypothesis(LinkedList p, int n) {

        P = p;
        this.n = n;

    }

    public boolean equals(SolutionHypothesis other) {

        if (other.n == this.n && other.P.size() == this.P.size() && other.P.containsAll(this.P)) {

            return true;

        } else {

            return false;

        }

    }
    LinkedList P;
    int n;
}
