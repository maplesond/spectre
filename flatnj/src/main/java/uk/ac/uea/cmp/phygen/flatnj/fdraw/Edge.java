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

package uk.ac.uea.cmp.phygen.flatnj.fdraw;

import java.awt.*;


public class Edge {
    //endpoints of the edge.

    Vertex top = null;
    Vertex bot = null;
    //index of the split associated to the edge.
    int idxsplit = 0;
    //number of edge in nexus file
    int nxnum = 0;
    //tie stamp, used to keep track of the oder
    //from left to right
    int timestp = 0;
    //Flags used when traversing the splitsgraph
    public boolean visited = false;

    int width = 1;
    Color color = Color.black;
    public boolean compatible = false;

    //Constructor.
    public Edge(Vertex t, Vertex b, int idx, int time) {
        top = t;
        bot = b;
        idxsplit = idx;
        timestp = time;
        visited = false;
    }

    Edge(double x, double y, double x1, double y1) {
        top = new Vertex(x, y);
        bot = new Vertex(x1, y1);
    }

    public void setNxnum(int nxnum) {
        this.nxnum = nxnum;
    }


    public double length() {
        return Math.sqrt((bot.x - top.x) * (bot.x - top.x) + (bot.y - top.y) * (bot.y - top.y));
    }

    @Override
    public String toString() {
        return "Nr. " + nxnum + "\nSplit: " + idxsplit + "\nBot: " + bot.nxnum + " [" + bot.x + ", " + bot.y + "]\nTop: " + top.nxnum + " [" + top.x + ", " + top.y + "]\n";
    }

    public void setColor(Color c) {
        color = c;
    }

    public Vertex getBot() {
        return bot;
    }

    public Color getColor() {
        return color;
    }

    public int getIdxsplit() {
        return idxsplit;
    }

    public int getNxnum() {
        return nxnum;
    }

    public Vertex getTop() {
        return top;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int i) {
        width = i;
    }

    public void setIdxsplit(int idxsplit) {
        this.idxsplit = idxsplit;
    }

    public Vertex getOther(Vertex v) {
        if (bot == v) {
            return top;
        } else if (top == v) {
            return bot;
        } else {
            return null;
        }
    }
}