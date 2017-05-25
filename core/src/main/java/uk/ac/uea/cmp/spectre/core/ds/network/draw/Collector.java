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

package uk.ac.uea.cmp.spectre.core.ds.network.draw;


import uk.ac.uea.cmp.spectre.core.ds.network.Edge;
import uk.ac.uea.cmp.spectre.core.ds.network.Vertex;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;


/**
 * @author balvociute
 */
public class Collector {

    //Collects all the vertices that are on thepart of the convex hull of the
    //network that is above edges e1 and e2 includind top points of these two
    //edges as well.
    public static Set<Vertex> getExternalVertices(Edge e1, Vertex b1, Edge e2) {
        //Set for storing all vertices in the top convex hull
        Set<Vertex> externalVertices = new HashSet<>();
        //current edge of the hull, for which we will be looking next one
        Edge current = e1;

        //Last vertex that was added to the top vertices list
        Vertex last = null;
        //While current edge is not the last one that we need to visit:
        //System.out.println("------------ " + e2.nxnum + " ---------------");
        while (current != e2 || externalVertices.isEmpty()) {
            //b1->a is the starting vetor
            //Vertex b1;
            Vertex a;
            //If this is the first edge, then the vector is going down.
            //Also, if the last vertex that was added is in the bottom of the
            //current vector then the vector is looking down as well.
            if (last == null) {
                a = (current.getBottom() == b1) ? current.getTop() : current.getBottom();
            } else if (current.getBottom() == last) {
                b1 = current.getBottom();
                a = current.getTop();
            }
            //Else, if the last vertex added is in the top of the current
            //vector, then it is looking up.
            else {
                b1 = current.getTop();
                a = current.getBottom();
            }

            //Adding the vertex and setting it as the last added.
            externalVertices.add(a);
            last = a;

            //Getting the list of all edges incident to the "central point" a.
            LinkedList<Edge> incidentEdges = a.getEdgeList();

            //Removing current edge, because we need to find the one that is
            //clockwise closest to it.
            //incidentEdges.remove(current);
            //Variable to store the last closest edge found.
            Edge closest = null;
            //Variable to store the smallest angle found.
            double minAngle = 0.0;

            for (int i = 0; i < incidentEdges.size(); i++) {
                Edge e = incidentEdges.get(i);
                if (e.length() > 0) {
                    Vertex b2 = (e.getTop() == a) ? e.getBottom() : e.getTop();
                    double angle = (b1 != b2) ? Vertex.getClockwiseAngle(b1, a, b2) : 2 * Math.PI;
                    if (closest == null || minAngle > angle) {
                        closest = e;
                        minAngle = angle;
                    }
                }
            }
            if (closest != null) {
                //externalVertices.add(closest.getTop());
                current = closest;
            }
        }


        return externalVertices;
    }

    public static Set<Edge> getAllEdges(LinkedList<Edge> split, boolean top) {
        LinkedList<Vertex> vertices = new LinkedList<>();
        Set<Edge> edges = new HashSet<>();

        for (int i = 0; i < split.size(); i++) {
            split.get(i).setVisited(true);
            if (top) {
                vertices.add(split.get(i).getTop());
            } else {
                vertices.add(split.get(i).getBottom());
            }
        }
        while (!vertices.isEmpty()) {
            Vertex v = vertices.removeFirst();
            LinkedList<Edge> incident = v.getEdgeList();
            for (int i = 0; i < incident.size(); i++) {
                Edge e = incident.get(i);
                if (!e.isVisited()) {
                    edges.add(e);
                    e.setVisited(true);
                    if (e.getTop() != v) {
                        vertices.add(e.getTop());
                    } else {
                        vertices.add(e.getBottom());
                    }
                }
            }
        }

        Iterator<Edge> it = edges.iterator();
        while (it.hasNext()) {
            it.next().setVisited(false);
        }
        it = split.iterator();
        while (it.hasNext()) {
            it.next().setVisited(false);
        }

        return edges;
    }


    public static double getDistanceEdgeToEgde(Vertex v1, Vertex v2, Edge ee, boolean longEdge) {
        Vertex w1 = ee.getBottom();
        Vertex w2 = ee.getTop();

        if (longEdge) {
            double v1x = v1.getX() - (v1.getX() - v2.getX()) * 0.25;
            double v1y = v1.getY() - (v1.getY() - v2.getY()) * 0.25;

            v1 = new Vertex(v1x, v1y);
        }

        Edge e = new Edge(v1, v2, 0, 0);

        double min;

//        if(ee.getTop() != v1 && ee.getBottom() != v1)
//        {
//            min = (Translocator.cross(v1.x, v1.y, v2.x, v2.y, w1.x, w1.y, w2.x, w2.y)) ? 0.0 : getDistanceToEgde(v2, ee);
//        }
//        else
        {
            min = getDistanceToEgde(v2, ee);
        }


        //double dist1 = getDistanceToEgde(v2, ee);
        if (!w1.equals(v1)) {
            double dist2 = getDistanceToEgde(w1, e);
            min = (dist2 < min) ? dist2 : min;
        }
        if (!w2.equals(v1)) {
            double dist3 = getDistanceToEgde(w2, e);
            min = (dist3 < min) ? dist3 : min;
        }


        return min;
    }

    public static double getDistanceToEgde(Vertex v, Edge e) {
        double x = e.length();

        double a = v.calcDistanceTo(e.getTop());
        double b = v.calcDistanceTo(e.getBottom());

        double p = (a + b + x) * 0.5;

        double S = Math.sqrt(p * (p - a) * (p - b) * (p - x));
        double h = 2.0 * S / x;

        double cosAlpha = (x * x + b * b - a * a) / (2.0 * x * b);
        double cosBeta = (x * x + a * a - b * b) / (2.0 * x * a);

        if (cosAlpha < 0 || cosBeta < 0) {
            h = Math.min(a, b);
        }

        return h;
    }

}
