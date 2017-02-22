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

import java.awt.*;
import java.util.Iterator;


public class Edge implements Comparable<Edge> {

    // Endpoints of the edge.
    private Vertex top;
    private Vertex bottom;

    // Index of the split associated to the edge.
    private int idxsplit;

    // Index of edge
    private int nxnum;

    // Time stamp, used to keep track of the order from left to right
    private int timestp;

    //Flags used when traversing the splitsgraph
    private boolean visited;

    private int width;
    private Color color;
    private boolean compatible;

    public Edge() {
        this(null, null, 0, 0);
    }

    public Edge(Vertex t, Vertex b, int idx, int time) {
        top = t;
        bottom = b;
        idxsplit = idx;
        timestp = time;
        visited = false;
        width = 1;
        color = Color.black;
        compatible = false;
    }

    public void setNxnum(int nxnum) {
        this.nxnum = nxnum;
    }


    public double length() {
        return Math.sqrt((bottom.getX() - top.getX()) * (bottom.getX() - top.getX()) + (bottom.getY() - top.getY()) * (bottom.getY() - top.getY()));
    }

    @Override
    public String toString() {
        return "Nr. " + nxnum + "\nSplit: " + idxsplit + "\nBot: " + bottom.toSimpleString() + "\nTop: " + top.toSimpleString() + "\n";
    }


    public void setTop(Vertex top) {
        this.top = top;
    }

    public void setBottom(Vertex bottom) {
        this.bottom = bottom;
    }

    public void setColor(Color c) {
        color = c;
    }

    public Vertex getBottom() {
        return bottom;
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
        if (bottom == v) {
            return top;
        } else if (top == v) {
            return bottom;
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

    private void collectEdges(EdgeList elist) {
        EdgeList toBeExplored = new EdgeList();
        toBeExplored.addLast(this);
        this.setVisited(true);

        Edge g, h;

        while (toBeExplored.size() > 0) {
            g = toBeExplored.removeFirst();
            elist.addLast(g);
            Iterator iter = ((g.getTop()).getEdgeList()).iterator();
            while (iter.hasNext()) {
                h = (Edge) iter.next();
                if (h.isVisited() == false) {
                    toBeExplored.addLast(h);
                    h.setVisited(true);
                }
            }
            iter = ((g.getBottom()).getEdgeList()).iterator();
            while (iter.hasNext()) {
                h = (Edge) iter.next();
                if (h.isVisited() == false) {
                    toBeExplored.addLast(h);
                    h.setVisited(true);
                }
            }
        }
    }

    /**
     * This method computes a list of the edges in the split network.
     * @return The computed list of edges in the split network
     */
    public EdgeList collectEdges() {

        EdgeList elist = new EdgeList();
        this.collectEdges(elist);

        int i = 1;
        for(Edge e : elist) {
            e.setVisited(false);
            e.setNxnum(i++);
        }

        return elist;
    }

    public boolean bottomEquals(Edge e) {
        return this.getBottom().equals(e.getBottom());
    }
}