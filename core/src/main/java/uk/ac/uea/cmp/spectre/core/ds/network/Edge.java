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

package uk.ac.uea.cmp.spectre.core.ds.network;

import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;


public class Edge implements Comparable<Edge> {

    // Endpoints of the edge.
    private Vertex top = null;
    private Vertex bot = null;

    // Index of the split associated to the edge.
    private int idxsplit = 0;

    // Number of edges in nexus file
    // TODO: Should this really be in here???
    private int nxnum = 0;

    // Time stamp, used to keep track of the order from left to right
    private int timestp = 0;

    //Flags used when traversing the splitsgraph
    private boolean visited = false;

    private int width = 1;
    private Color color = Color.black;
    private boolean compatible = false;

    //Constructor.
    public Edge(Vertex t, Vertex b, int idx, int time) {
        top = t;
        bot = b;
        idxsplit = idx;
        timestp = time;
        visited = false;
    }


    public void setNxnum(int nxnum) {
        this.nxnum = nxnum;
    }


    public double length() {
        return Math.sqrt((bot.getX() - top.getX()) * (bot.getX() - top.getX()) + (bot.getY() - top.getY()) * (bot.getY() - top.getY()));
    }

    @Override
    public String toString() {
        return "Nr. " + nxnum + "\nSplit: " + idxsplit + "\nBot: " + bot.toSimpleString() + "\nTop: " + top.toSimpleString() + "\n";
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

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public int getTimestp() {
        return timestp;
    }

    public void setTimestp(int timestp) {
        this.timestp = timestp;
    }

    public boolean isCompatible() {
        return compatible;
    }

    public void setCompatible(boolean compatible) {
        this.compatible = compatible;
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

    /**
     * Natural ordering of edges is based on the timestamp alone
     * @param o The other edge to look at
     * @return Whether this edge is less than, equal to or greater than the other edge
     */
    @Override
    public int compareTo(Edge o) {

        if (this.timestp < o.timestp) {
            return -1;
        } else if (this.timestp > o.timestp) {
            return 1;
        } else {
            return 0;
        }
    }

    private void collectEdges(LinkedList<Edge> elist) {
        LinkedList<Edge> tobeexplored = new LinkedList<>();
        tobeexplored.addLast(this);
        this.setVisited(true);

        Edge g, h;

        while (tobeexplored.size() > 0) {
            g = tobeexplored.removeFirst();
            elist.addLast(g);
            Iterator iter = ((g.getTop()).getElist()).iterator();
            while (iter.hasNext()) {
                h = (Edge) iter.next();
                if (h.isVisited() == false) {
                    tobeexplored.addLast(h);
                    h.setVisited(true);
                }
            }
            iter = ((g.getBot()).getElist()).iterator();
            while (iter.hasNext()) {
                h = (Edge) iter.next();
                if (h.isVisited() == false) {
                    tobeexplored.addLast(h);
                    h.setVisited(true);
                }
            }
        }
    }

    /**
     * This method computes a list of the edges in the split network.
     * @return
     */
    public LinkedList<Edge> collectEdges() {
        LinkedList<Edge> elist = new LinkedList<>();
        this.collectEdges(elist);
        ListIterator iter = elist.listIterator(0);
        int i = 1;
        Edge h;
        while (iter.hasNext()) {
            h = (Edge) iter.next();
            h.setVisited(false);
            h.setNxnum(i);
            i++;
        }
        return elist;
    }

}