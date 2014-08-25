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

package uk.ac.uea.cmp.spectre.flatnj.fdraw;


import uk.ac.uea.cmp.spectre.core.ds.network.Edge;
import uk.ac.uea.cmp.spectre.core.ds.network.draw.PermutationSequenceDraw;
import uk.ac.uea.cmp.spectre.core.ds.network.Vertex;

import java.util.*;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */

/**
 * @author balvociute
 */
public class Collector {




    public static void getExternalEdges(Collection<Edge> edges, Edge e1, Vertex a, Edge e2) {
        Edge current = e1;
        //Last vertex that was added to the top vertices list
        Vertex last = null;
        //While current edge is not the last one that we need to visit:
        while (current != e2) {
            //System.out.println(current.nxnum + "\t" + length(current));
            //a->b1 is the starting vetor
            Vertex b1;
            //Vertex a;
            //If this is the first edge, then the vector is going down.
            //Also, if the last vertex that was added is in the bottom of the
            //current vector then the vector is looking down as well.
            if (last == null) {
                b1 = (current.getBot() == a) ? current.getTop() : current.getBot();
            } else if (current.getBot() == last) {
                b1 = current.getBot();
                a = current.getTop();
            }
            //Else, if the last vertex added is in the top of the current
            //vector, then it is looking up.
            else {
                b1 = current.getTop();
                a = current.getBot();
            }

            //Adding the vertex and setting it as the last added.
            if (current != e1) {
                edges.add(current);
            }
            last = a;

            //Getting the list of all edges incident to the "central point" a.
            LinkedList<Edge> incidentEdges = a.getElist();
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
                    Vertex b2 = (e.getTop() == a) ? e.getBot() : e.getTop();
                    double angle = (b1 != b2) ? Vertex.getClockwiseAngle(b1, a, b2) : 2 * Math.PI;
                    if (closest == null || minAngle > angle) {
                        closest = e;
                        minAngle = angle;
                    }
                }
            }
            if (closest != null) {
                current = closest;
            }
        }
    }

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
                a = (current.getBot() == b1) ? current.getTop() : current.getBot();
            } else if (current.getBot() == last) {
                b1 = current.getBot();
                a = current.getTop();
            }
            //Else, if the last vertex added is in the top of the current
            //vector, then it is looking up.
            else {
                b1 = current.getTop();
                a = current.getBot();
            }

            //Adding the vertex and setting it as the last added.
            externalVertices.add(a);
            last = a;

            //Getting the list of all edges incident to the "central point" a.
            LinkedList<Edge> incidentEdges = a.getElist();

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
                    Vertex b2 = (e.getTop() == a) ? e.getBot() : e.getTop();
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
                vertices.add(split.get(i).getBot());
            }
        }
        while (!vertices.isEmpty()) {
            Vertex v = vertices.removeFirst();
            LinkedList<Edge> incident = v.getElist();
            for (int i = 0; i < incident.size(); i++) {
                Edge e = incident.get(i);
                if (!e.isVisited()) {
                    edges.add(e);
                    e.setVisited(true);
                    if (e.getTop() != v) {
                        vertices.add(e.getTop());
                    } else {
                        vertices.add(e.getBot());
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



    public static LinkedList<LinkedList<Edge>> collectBallons(double angleThreshold, Vertex v) {
        LinkedList<LinkedList<Edge>> balloons = new LinkedList<>();

        LinkedList<Edge> external = v.collectAllExternalEdges(false);

        Vertex startVertex = (external.get(0).getBot() == external.get(1).getBot() || external.get(0).getBot() == external.get(1).getTop()) ? external.get(0).getTop() : external.get(0).getBot();

        Vertex startingPoint = null;
        Edge startingEdge = null;
        Edge terminalEdge = null;

        Vertex v1 = (external.getLast().getBot() == startVertex) ? external.getLast().getTop() : external.getLast().getBot();
        Vertex v2 = (external.getFirst().getBot() == startVertex) ? external.getFirst().getTop() : external.getFirst().getBot();
        Vertex a = startVertex;

        if (Vertex.getClockwiseAngle(v1, startVertex, v2) < angleThreshold) {
            startingPoint = startVertex;
            startingEdge = external.getFirst();
            terminalEdge = external.getLast();
        }

        for (int i = 0; i < external.size(); i++) {
            Edge e = external.get(i);
            //e.width = 3;
            if (i > 0 && startingPoint == null) {
                v1 = a;
                a = v2;
                v2 = (e.getTop() == a) ? e.getBot() : e.getTop();
                if (Vertex.getClockwiseAngle(v1, a, v2) < angleThreshold) {
                    startingPoint = a;
                    startingEdge = e;
                    terminalEdge = external.get(i - 1);
                }
            }
        }

        Edge before = null;
        if (startingPoint != null) {
//            startingPoint.setSize(7);
//            startingPoint.bgColor[1] = 204;

            int balloonCount = 0;
            balloons.add(new LinkedList<Edge>());
            balloons.get(balloonCount).add(startingEdge);
            //startingEdge.width = 6;

            int index = external.indexOf(startingEdge) + 1;
            index = (index == external.size()) ? 0 : index;

            a = (startingEdge.getBot() == startingPoint) ? startingEdge.getTop() : startingEdge.getBot();
            v1 = startingPoint;

            Edge current = null;
            while (current != terminalEdge) {
                before = current;
                current = external.get(index);
                v2 = (current.getBot() == a) ? current.getTop() : current.getBot();
                double angle = (v1 == v2) ? 2 * Math.PI : Vertex.getClockwiseAngle(v1, a, v2);
                if (angle < angleThreshold) {
                    balloonCount++;
                    balloons.add(new LinkedList<Edge>());
                }
                balloons.get(balloonCount).add(current);
                v1 = a;
                a = v2;
                index++;
                index = (index == external.size()) ? 0 : index;
            }
        }

        if (balloons.size() == 0) {
            balloons.add(external);
        }

        return balloons;
    }

    public static TreeSet<Edge>[] collectEgdesForTheSplits(PermutationSequenceDraw ps, Vertex v) {
        TreeSet<Edge>[] splitedges = new TreeSet[ps.getNswaps()];

        for (int i = 0; i < ps.getnActive(); i++) {
            LinkedList<Edge> edges = v.collectEdgesForSplit(i);
            splitedges[i] = new TreeSet<>();
            for (int k = 0; k < edges.size(); k++) {
                splitedges[i].add(edges.get(k));
            }
        }
        return splitedges;
    }

    public static int[] collectIndicesOfActiveSplits(PermutationSequenceDraw ps) {
        int[] activeSplits = new int[ps.getnActive()];

        //Index used to fill in array of active splits
        int j = 0;
        //Go through all the splits and select active ones
        for (int i = 0; i < ps.getnActive(); i++) {
            if (ps.getActive()[i]) {
                activeSplits[j++] = i;
            }
        }
        return activeSplits;
    }

    public static Set<Edge> getExternalEdges(Edge e1, Vertex a, Edge e2) {
        Set<Edge> edges = new HashSet<>();
        Edge current = e1;
        //Last vertex that was added to the top vertices list
        Vertex last = null;
        //While current edge is not the last one that we need to visit:
        //System.out.println("------------ " + e2.nxnum + " ---------------");
        while (current != e2) {
            //System.out.println(current.nxnum + "\t" + length(current));
            //a->b1 is the starting vetor
            Vertex b1;
            //Vertex a;
            //If this is the first edge, then the vector is going down.
            //Also, if the last vertex that was added is in the bottom of the
            //current vector then the vector is looking down as well.
            if (last == null) {
                b1 = (current.getBot() == a) ? current.getTop() : current.getBot();
            } else if (current.getBot() == last) {
                b1 = current.getBot();
                a = current.getTop();
            }
            //Else, if the last vertex added is in the top of the current
            //vector, then it is looking up.
            else {
                b1 = current.getTop();
                a = current.getBot();
            }

            //Adding the vertex and setting it as the last added.
            if (current != e1) {
                edges.add(current);
            }
            last = a;

            //Getting the list of all edges incident to the "central point" a.
            LinkedList<Edge> incidentEdges = a.getElist();
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
                    Vertex b2 = (e.getTop() == a) ? e.getBot() : e.getTop();
                    double angle = (b1 != b2) ? Vertex.getClockwiseAngle(b1, a, b2) : 2 * Math.PI;
                    if (closest == null || minAngle > angle) {
                        closest = e;
                        minAngle = angle;
                    }
                }
            }
            if (closest != null) {
                current = closest;
            }
        }
        return edges;
    }

    public static LinkedList<Vertex>[] assignVerticesToBalloons(LinkedList<LinkedList<Edge>> balloons, Vertex v) {
        LinkedList<Vertex>[] verticesInBalloons = new LinkedList[balloons.size()];
        for (int i = 0; i < verticesInBalloons.length; i++) {
            verticesInBalloons[i] = new LinkedList<>();
        }

        LinkedList<Vertex> vertices = v.collectVertices();

        for (int i = 0; i < vertices.size(); i++) {
            Vertex w = vertices.get(i);
            if (w.getTaxa().size() > 0) {
                int closest = 0;
                if (w.getElist().size() == 1) {
                    Vertex another = (w.getElist().getFirst().getTop() == w) ? w.getElist().getFirst().getBot() : w.getElist().getFirst().getTop();
                    for (int j = 0; j < balloons.size(); j++) {
                        LinkedList<Edge> currentBalloon = balloons.get(j);
                        for (int k = 0; k < currentBalloon.size(); k++) {
                            Edge e = currentBalloon.get(k);
                            if (e.getBot() == another || e.getTop() == another) {
                                closest = j;
                            }
                        }
                    }
                } else {
                    Double minDist = null;
                    for (int j = 0; j < balloons.size(); j++) {
                        LinkedList<Edge> currentBalloon = balloons.get(j);
                        for (int k = 0; k < currentBalloon.size(); k++) {
                            Edge e = currentBalloon.get(k);
                            double dist = getDistanceToEgde(w, e);
                            if (minDist == null || minDist > dist) {
                                minDist = dist;
                                closest = j;
                            }
                        }
                    }
                }
                verticesInBalloons[closest].add(w);
            }
        }
        return verticesInBalloons;
    }

    public static double getDistanceEdgeToEgde(Vertex v1, Vertex v2, Edge ee, boolean longEdge) {
        Vertex w1 = ee.getBot();
        Vertex w2 = ee.getTop();

        if (longEdge) {
            double v1x = v1.getX() - (v1.getX() - v2.getX()) * 0.25;
            double v1y = v1.getY() - (v1.getY() - v2.getY()) * 0.25;

            v1 = new Vertex(v1x, v1y);
        }

        Edge e = new Edge(v1, v2, 0, 0);

        double min;

//        if(ee.getTop() != v1 && ee.getBot() != v1)
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
        double b = v.calcDistanceTo(e.getBot());

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


    public static double getDistanceToEnds(Vertex v, Edge e) {

        double a = v.calcDistanceTo(e.getTop());
        double b = v.calcDistanceTo(e.getBot());

        return Math.min(a, b);
    }



    static LinkedList<Edge> collectAllCompatibleNonTrivial(Vertex V) {
        LinkedList<Edge> edges = V.getFirstEdge().collectEdges();
        LinkedList<Edge> compatible = new LinkedList<>();
        for (int i = 0; i < edges.size(); i++) {
            Edge e = edges.get(i);
            if (e.getBot().getElist().size() > 1 && e.getTop().getElist().size() > 1 && V.collectEdgesForSplit(e.getIdxsplit()).size() == 1) {
                compatible.add(e);
            }
        }
        return compatible;
    }

    public static void highlightLargestSplits(Vertex V, PermutationSequenceDraw ps, int n) {
        TreeSet<Edge>[] edges = collectEgdesForTheSplits(ps, V);
        Map<Integer, Double> splits = new HashMap<>();
        for (int i = 0; i < edges.length; i++) {
            if (edges[i].size() > 1) {
                splits.put(i, edges[i].first().length());
            }
        }
        Set<Integer> keySet = splits.keySet();
        List<Integer> sortedKeys = new LinkedList<>();
        Iterator<Integer> keyIt = keySet.iterator();
        while (keyIt.hasNext()) {
            int id = keyIt.next();
            if (sortedKeys.isEmpty()) {
                sortedKeys.add(id);
            } else {
                double l = splits.get(id);
                for (int i = 0; i < sortedKeys.size(); i++) {
                    if (l >= splits.get(sortedKeys.get(i))) {
                        sortedKeys.add(i, id);
                        break;
                    } else if (i == sortedKeys.size() - 1) {
                        sortedKeys.add(id);
                        break;
                    }
                }
            }
        }
        for (int i = 0; i < n; i++) {
            int id = sortedKeys.get(i);
            TreeSet<Edge> bold = edges[id];
            Iterator<Edge> eIt = bold.iterator();
            while (eIt.hasNext()) {
                eIt.next().setWidth(3);
            }
        }
    }


}
