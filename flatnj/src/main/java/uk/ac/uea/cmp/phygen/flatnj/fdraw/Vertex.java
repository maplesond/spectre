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
import java.util.LinkedList;

public class Vertex {
    //List of the edges that are incident to this vertex.
    //The edges are sorted clockwise around the vertex.

    LinkedList<Edge> elist = null;
    //The list of taxa associated to the vertex.
    LinkedList<Integer> taxa = null;
    //Coordinates of the vertex in the drawing.
    double x = 0.0;
    double y = 0.0;
    //number of vertex in nexus file
    int nxnum = 0;
    //flags used in various methods when traversing the
    //splitsgraph and so on
    boolean visited = false;

    int width = 0;
    int height = 0;

    Color bgColor = Color.BLACK;
    Color fgColor = Color.BLACK;

    String shape = null;

    Label label;

    //Constructor.
    public Vertex(double xcoord, double ycoord) {
        x = xcoord;
        y = ycoord;
        elist = new LinkedList<>();
        taxa = new LinkedList<>();
        visited = false;
    }

    //Methods to add an edge to the list of incident edges.
    void add_edge_before_first(Edge e) {
        elist.addFirst(e);
    }

    void add_edge_after_last(Edge e) {
        elist.addLast(e);
    }

    @Override
    public String toString() {
        return nxnum + " [" + x + ", " + y + "] : " + elist.size();
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
        if (x == v2.x && y == v2.y) {
            return true;
        }
        return false;
    }

    public LinkedList<Edge> getElist() {
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

    public void setLabel(Label label) {
        if (label != null) {
            label.setVertex(this);
        }
        this.label = label;
    }

    public Label getLabel() {
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
}