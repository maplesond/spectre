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
import java.util.LinkedList;
import java.util.ListIterator;

public class Vertex {

    //List of the edges that are incident to this vertex.
    //The edges are sorted clockwise around the vertex.
    private EdgeList elist;

    //The list of taxa associated to the vertex.
    private LinkedList<Integer> taxa;

    //Coordinates of the vertex in the drawing.
    private double x;
    private double y;

    //number of vertex in nexus file
    private int nxnum = 0;

    //flags used in various methods when traversing the
    //splitsgraph and so on
    private boolean visited;

    private int width = 0;
    private int height = 0;

    private Color bgColor = Color.BLACK;
    private Color fgColor = Color.BLACK;

    private String shape = null;

    private NetworkLabel label;

    private EdgeList externalEdges = null;


    public Vertex() {
        this (0.0, 0.0);
    }

    public Vertex(double xcoord, double ycoord) {
        x = xcoord;
        y = ycoord;
        elist = new EdgeList();
        taxa = new LinkedList<>();
        visited = false;
    }

    //Methods to add an edge to the list of incident edges.
    public void add_edge_before_first(Edge e) {
        elist.addFirst(e);
    }

    public void add_edge_after_last(Edge e) {
        elist.addLast(e);
    }

    @Override
    public String toString() {
        return nxnum + " [" + x + ", " + y + "] : " + elist.size();
    }

    public String toSimpleString() {
        return nxnum + " [" + x + ", " + y + "]";
    }


    public void setBackgroundColor(Color color) {
        bgColor = color;
        //System.arraycopy(c, 0, bgColor, 0, bgColor.length);
    }

    public void setSize(int i) {
        width = i;
        height = i;
    }

    public Edge getFirstEdge() {
        return elist.getFirst();
    }

    public void setTaxa(LinkedList<Integer> taxa) {
        this.taxa = taxa;
    }

    public LinkedList<Integer> getTaxa() {
        return taxa;
    }

    public int getNxnum() {
        return nxnum;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Color getBackgroundColor() {
        return bgColor;
    }

    public void setShape(String s) {
        shape = s;
    }

    public String getShape() {
        return shape;
    }

    public boolean equals(Vertex v2) {
        return x == v2.x && y == v2.y;
    }

    public EdgeList getElist() {
        return elist;
    }

    public void setWidth(int w) {
        width = w;
    }

    public void setHeight(int h) {
        height = h;
    }

    public void setLineColor(Color c) {
        fgColor = c;
    }

    public Color getLineColor() {
        return fgColor;
    }

    public void setLabel(NetworkLabel label) {
        if (label != null) {
            label.setVertex(this);
        }
        this.label = label;
    }

    public NetworkLabel getLabel() {
        return label;
    }

    public void setNxnum(int nxnum) {
        this.nxnum = nxnum;
    }

    public void setCoordinates(double newX, double newY) {
        x = newX;
        y = newY;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public double calcDistanceTo(Vertex v) {
        return Math.sqrt((this.x - v.x) * (this.x - v.x) + (this.y - v.y) * (this.y - v.y));
    }

    /**
     * Returns all the vertices attached to this vertex as a LinkedList
     * @return A linked list of all attached vertices
     */
    public VertexList collectVertices() {

        VertexList vList = new VertexList();
        collectVertices(vList);

        int i = 1;
        for(Vertex v : vList) {
            v.setVisited(false);
            v.setNxnum(i++);
        }

        return vList;
    }

    /**
     * Auxilary method used to collect the attached vertices.  Populates the provided linked list.
     * @param vlist List of vertices to populate
     */
    private void collectVertices(VertexList vlist) {

        VertexList toBeExplored = new VertexList();
        toBeExplored.addLast(this);
        this.setVisited(true);

        Vertex u;
        Edge e;

        while (toBeExplored.size() > 0) {
            u = toBeExplored.removeFirst();
            vlist.addLast(u);
            ListIterator<Edge> iter = u.getElist().listIterator();
            while (iter.hasNext()) {
                e = iter.next();
                if (u == e.getTop()) {
                    if (!e.getBot().isVisited()) {
                        toBeExplored.addLast(e.getBot());
                        (e.getBot()).setVisited(true);
                    }
                } else {
                    if (!e.getTop().isVisited()) {
                        toBeExplored.addLast(e.getTop());
                        (e.getTop()).setVisited(true);
                    }
                }
            }
        }
    }


    /**
     * This method collects the edges that represent a given split in the network.
     * @param splitIndex The split index to find
     * @return A list of edges attached to this vertex that have the associated split index
     */
    public EdgeList collectEdgesForSplit(int splitIndex) {

        EdgeList elist = new EdgeList();

        for (Edge e : this.elist.getFirst().collectEdges()) {
            if (e.getIdxsplit() == splitIndex) {
                elist.add(e);
            }
        }

        return elist;
    }

    public EdgeList collectAllTrivialEdges() {

        EdgeList trivial = new EdgeList();
        for (Vertex w : this.collectVertices()) {
            if (w.getElist().size() == 1) {
                trivial.add(w.getElist().getFirst());
            }
        }
        return trivial;
    }


    public EdgeList collectAllExternalEdges(boolean withTrivial) {
        EdgeList trivialEdges = this.collectAllTrivialEdges();

        EdgeList external = this.collectExternalEdges();

        if (!withTrivial) {
            for (int i = 0; i < trivialEdges.size(); i++) {
                //while is used to assure that all copies of each trivial split are
                //deleted from chain of external edges.
                while (external.remove(trivialEdges.get(i))) ;
            }
        }

        return external;
    }

    public EdgeList collectExternalEdges() {

        // Just use the cache if available
        if (externalEdges != null) {
            return new EdgeList(externalEdges);
        }

        // Get all the associated vertices
        VertexList vertices = this.collectVertices();

        // Make sure we have something to work with otherwise return null
        if (vertices == null || vertices.isEmpty())
            return null;

        // Gets the vertex with the highest X value
        Vertex v = vertices.getLeftmostVertex();

        Edge first = null;

        EdgeList ext = new EdgeList();
        Vertex w = null;

        if (v.getElist().size() == 1) {
            w = (v.getElist().getFirst().getBot() == v) ?
                    v.getElist().getFirst().getTop() :
                    v.getElist().getFirst().getBot();

            first = v.getElist().getFirst();

            Vertex t = w;
            w = v;
            v = t;
        } else {
            EdgeList elist = v.getElist();

            for (int i = 0; i < elist.size(); i++) {
                Vertex ww = null;
                Vertex w0 = (elist.get(i).getBot() == v) ?
                        elist.get(i).getTop() :
                        elist.get(i).getBot();

                double angle = 0;
                for (int j = 0; j < elist.size(); j++) {
                    if (i != j) {
                        Vertex w1 = (elist.get(j).getBot() == v) ?
                                elist.get(j).getTop() :
                                elist.get(j).getBot();

                        double currentAngle = Vertex.getClockwiseAngle(w0, v, w1);
                        if (ww == null || currentAngle < angle) {
                            ww = w0;
                            angle = currentAngle;
                            first = elist.get(i);
                        }
                    }
                }
                if (angle > Math.PI) {
                    w = ww;
                    break;
                }
            }
        }

        Edge currentE = first;

        boolean roundMade = false;

        while (currentE != first || !roundMade) {
            roundMade = true;
            double minAngle = 2 * Math.PI;
            Edge nextE = null;
            Vertex W2 = null;
            for (Edge e : v.getElist()) {
                Vertex w2 = (e.getBot() == v) ? e.getTop() : e.getBot();
                double angle = (currentE == e) ? 2 * Math.PI : Vertex.getClockwiseAngle(w, v, w2);
                if (nextE == null || minAngle > angle) {
                    nextE = e;
                    minAngle = angle;
                    W2 = w2;
                }
            }
            ext.add(nextE);
            currentE = nextE;
            w = v;
            v = W2;
        }

        externalEdges = new EdgeList(ext);


        return ext;
    }

    public void resetExternalEdges() {
        this.externalEdges = null;
    }

    /**
     * Calculates the clockwise angle between 3 vertices
     * @param v1
     * @param a
     * @param v2
     * @return The angle between the vertices
     */
    public static double getClockwiseAngle(Vertex v1, Vertex a, Vertex v2) {

        double v1X = v1.getX();
        double v1Y = v1.getY();
        double v2X = v2.getX();
        double v2Y = v2.getY();
        double aX = a.getX();
        double aY = a.getY();

        double angle;
        if (v1X == v2X && v1Y == v2Y) {
            angle = 0;
        } else if ((v1X == aX && v1Y == aY) || (v2X == aX && v2Y == aY)) {
            angle = Math.PI;
        } else {
            if (v1.calcDistanceTo(a) == 0 || v2.calcDistanceTo(a) == 0) {
                angle = 0;
            } else {
                angle = Math.atan2((v1Y - aY), (v1X - aX)) - Math.atan2((v2Y - aY), (v2X - aX));
                angle = (angle + 2 * Math.PI) % (2 * Math.PI);
            }
        }
        return angle;
    }
}