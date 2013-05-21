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

package uk.ac.uea.cmp.phygen.netmake.nnet;

/**
 * Created with IntelliJ IDEA. User: Dan Date: 20/05/13 Time: 00:32 To change this template use File | Settings | File
 * Templates.
 */
public class NetNode {

    protected int id = 0;
    protected NetNode nbr = null; // adjacent node
    protected NetNode ch1 = null; // first child
    protected NetNode ch2 = null; // second child
    protected NetNode next = null; // next in list of active nodes
    protected NetNode prev = null; // prev in list of active nodes
    protected double Rx = 0;
    protected double Sx = 0;

    @Override
    public String toString() {
        String str = "[id=" + id;
        str += " nbr=" + (nbr == null ? "null" : ("" + nbr.id));
        str += " ch1=" + (ch1 == null ? "null" : ("" + ch1.id));
        str += " ch2=" + (ch2 == null ? "null" : ("" + ch2.id));
        str += " prev=" + (prev == null ? "null" : ("" + prev.id));
        str += " next=" + (next == null ? "null" : ("" + next.id));
        str += " Rx=" + Rx;
        str += " Sx=" + Sx;
        str += "]";
        return str;
    }

}
