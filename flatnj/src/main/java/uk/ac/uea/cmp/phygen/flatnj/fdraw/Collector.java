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


import java.util.*;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */

/**
 * @author balvociute
 */
public class Collector {

    private static LinkedList<Edge> externalEdges = null;

    public static LinkedList<Edge> collectAllTrivial(Vertex v) {
        LinkedList<Edge> edges = DrawFlat.collect_edges(v.elist.getFirst());
        LinkedList<Vertex> vertices = DrawFlat.collect_vertices(v);
        LinkedList<Edge> trivial = new LinkedList<>();
        for (int i = 0; i < vertices.size(); i++) {
            Vertex w = vertices.get(i);
            if (w.elist.size() == 1) {
                trivial.add(w.elist.getFirst());
            }
        }
        return trivial;
    }

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
                b1 = (current.bot == a) ? current.top : current.bot;
            } else if (current.bot == last) {
                b1 = current.bot;
                a = current.top;
            }
            //Else, if the last vertex added is in the top of the current
            //vector, then it is looking up.
            else {
                b1 = current.top;
                a = current.bot;
            }

            //Adding the vertex and setting it as the last added.
            if (current != e1) {
                edges.add(current);
            }
            last = a;

            //Getting the list of all edges incident to the "central point" a.
            LinkedList<Edge> incidentEdges = a.elist;
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
                    Vertex b2 = (e.top == a) ? e.bot : e.top;
                    double angle = (b1 != b2) ? AngleCalculatorSimple.getClockwiseAngle(b1, a, b2) : 2 * Math.PI;
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
                a = (current.bot == b1) ? current.top : current.bot;
            } else if (current.bot == last) {
                b1 = current.bot;
                a = current.top;
            }
            //Else, if the last vertex added is in the top of the current
            //vector, then it is looking up.
            else {
                b1 = current.top;
                a = current.bot;
            }

            //Adding the vertex and setting it as the last added.
            externalVertices.add(a);
            last = a;

            //Getting the list of all edges incident to the "central point" a.
            LinkedList<Edge> incidentEdges = a.elist;

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
                    Vertex b2 = (e.top == a) ? e.bot : e.top;
                    double angle = (b1 != b2) ? AngleCalculatorSimple.getClockwiseAngle(b1, a, b2) : 2 * Math.PI;
                    if (closest == null || minAngle > angle) {
                        closest = e;
                        minAngle = angle;
                    }
                }
            }
            if (closest != null) {
                //externalVertices.add(closest.top);
                current = closest;
            }
        }


        return externalVertices;
    }

    public static Set<Edge> getAllEdges(LinkedList<Edge> split, boolean top) {
        LinkedList<Vertex> vertices = new LinkedList<>();
        Set<Edge> edges = new HashSet<>();

        for (int i = 0; i < split.size(); i++) {
            split.get(i).visited = true;
            if (top) {
                vertices.add(split.get(i).top);
            } else {
                vertices.add(split.get(i).bot);
            }
        }
        while (!vertices.isEmpty()) {
            Vertex v = vertices.removeFirst();
            LinkedList<Edge> incident = v.elist;
            for (int i = 0; i < incident.size(); i++) {
                Edge e = incident.get(i);
                if (!e.visited) {
                    edges.add(e);
                    e.visited = true;
                    if (e.top != v) {
                        vertices.add(e.top);
                    } else {
                        vertices.add(e.bot);
                    }
                }
            }
        }

        Iterator<Edge> it = edges.iterator();
        while (it.hasNext()) {
            it.next().visited = false;
        }
        it = split.iterator();
        while (it.hasNext()) {
            it.next().visited = false;
        }

        return edges;
    }

    public static LinkedList<Edge> collectAllExternalEdges(Vertex v, boolean withTrivial) {
        LinkedList<Edge> trivialEdges = collectAllTrivial(v);

        LinkedList<Edge> external = externalEdges(v);

        if (!withTrivial) {
            for (int i = 0; i < trivialEdges.size(); i++) {
                //while is used to assure that all copies of each trivial split are
                //deleted from chain of external edges.
                while (external.remove(trivialEdges.get(i))) ;
            }
        }

        return external;
    }

    public static LinkedList<LinkedList<Edge>> collectBallons(double angleThreshold, Vertex v) {
        LinkedList<LinkedList<Edge>> balloons = new LinkedList<>();

        LinkedList<Edge> external = Collector.collectAllExternalEdges(v, false);

        Vertex startVertex = (external.get(0).bot == external.get(1).bot || external.get(0).bot == external.get(1).top) ? external.get(0).top : external.get(0).bot;

        Vertex startingPoint = null;
        Edge startingEdge = null;
        Edge terminalEdge = null;

        Vertex v1 = (external.getLast().bot == startVertex) ? external.getLast().top : external.getLast().bot;
        Vertex v2 = (external.getFirst().bot == startVertex) ? external.getFirst().top : external.getFirst().bot;
        Vertex a = startVertex;

        if (AngleCalculatorSimple.getClockwiseAngle(v1, startVertex, v2) < angleThreshold) {
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
                v2 = (e.top == a) ? e.bot : e.top;
                if (AngleCalculatorSimple.getClockwiseAngle(v1, a, v2) < angleThreshold) {
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

            a = (startingEdge.bot == startingPoint) ? startingEdge.top : startingEdge.bot;
            v1 = startingPoint;

            Edge current = null;
            while (current != terminalEdge) {
                before = current;
                current = external.get(index);
                v2 = (current.bot == a) ? current.top : current.bot;
                double angle = (v1 == v2) ? 2 * Math.PI : AngleCalculatorSimple.getClockwiseAngle(v1, a, v2);
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

    public static TreeSet[] collectEgdesForTheSplits(PermutationSequenceDraw ps, Vertex v) {
        TreeSet[] splitedges = new TreeSet[ps.nswaps];

        for (int i = 0; i < ps.active.length; i++) {
            LinkedList<Edge> edges = DrawFlat.collect_edges_for_split(i, v);
            splitedges[i] = new TreeSet(new EdgeComparator());
            for (int k = 0; k < edges.size(); k++) {
                splitedges[i].add(edges.get(k));
            }
        }
        return splitedges;
    }

    public static int[] collectIndicesOfActiveSplits(PermutationSequenceDraw ps) {
        int[] activeSplits = new int[ps.nActive];

        //Index used to fill in array of active splits
        int j = 0;
        //Go through all the splits and select active ones
        for (int i = 0; i < ps.active.length; i++) {
            if (ps.active[i]) {
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
                b1 = (current.bot == a) ? current.top : current.bot;
            } else if (current.bot == last) {
                b1 = current.bot;
                a = current.top;
            }
            //Else, if the last vertex added is in the top of the current
            //vector, then it is looking up.
            else {
                b1 = current.top;
                a = current.bot;
            }

            //Adding the vertex and setting it as the last added.
            if (current != e1) {
                edges.add(current);
            }
            last = a;

            //Getting the list of all edges incident to the "central point" a.
            LinkedList<Edge> incidentEdges = a.elist;
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
                    Vertex b2 = (e.top == a) ? e.bot : e.top;
                    double angle = (b1 != b2) ? AngleCalculatorSimple.getClockwiseAngle(b1, a, b2) : 2 * Math.PI;
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

        LinkedList<Vertex> vertices = DrawFlat.collect_vertices(v);

        for (int i = 0; i < vertices.size(); i++) {
            Vertex w = vertices.get(i);
            if (w.taxa.size() > 0) {
                int closest = 0;
                if (w.elist.size() == 1) {
                    Vertex another = (w.elist.getFirst().top == w) ? w.elist.getFirst().bot : w.elist.getFirst().top;
                    for (int j = 0; j < balloons.size(); j++) {
                        LinkedList<Edge> currentBalloon = balloons.get(j);
                        for (int k = 0; k < currentBalloon.size(); k++) {
                            Edge e = currentBalloon.get(k);
                            if (e.bot == another || e.top == another) {
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
        Vertex w1 = ee.bot;
        Vertex w2 = ee.top;

        if (longEdge) {
            double v1x = v1.x - (v1.x - v2.x) * 0.25;
            double v1y = v1.y - (v1.y - v2.y) * 0.25;

            v1 = new Vertex(v1x, v1y);
        }

        Edge e = new Edge(v1, v2, 0, 0);

        double min;

//        if(ee.top != v1 && ee.bot != v1)
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

        double a = AngleCalculatorSimple.distance(v, e.top);
        double b = AngleCalculatorSimple.distance(v, e.bot);

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
        double x = e.length();

        double a = AngleCalculatorSimple.distance(v, e.top);
        double b = AngleCalculatorSimple.distance(v, e.bot);

        return Math.min(a, b);
    }

    public static LinkedList<Edge> externalEdges(Vertex V) {
        if (externalEdges != null) {
            return new LinkedList(externalEdges);
        }
        List<Vertex> vertices = DrawFlat.collect_vertices(V);

        Vertex v = V;

        Iterator<Vertex> vertexIt = vertices.iterator();
        while (vertexIt.hasNext()) {
            Vertex vertex = vertexIt.next();
            if (v.x > vertex.x) {
                v = vertex;
            }
        }

        Edge first = null;

        LinkedList<Edge> ext = new LinkedList<>();
        Vertex w = null;

        if (v.elist.size() == 1) {
            w = (v.elist.getFirst().bot == v) ? v.elist.getFirst().top : v.elist.getFirst().bot;
            first = v.elist.getFirst();

            Vertex t = w;
            w = v;
            v = t;
        } else {
            List<Edge> elist = v.elist;

            for (int i = 0; i < elist.size(); i++) {
                Vertex ww = null;
                Vertex w0 = (elist.get(i).bot == v) ? elist.get(i).top : elist.get(i).bot;
                double angle = 0;
                for (int j = 0; j < elist.size(); j++) {
                    if (i != j) {
                        Vertex w1 = (elist.get(j).bot == v) ? elist.get(j).top : elist.get(j).bot;
                        double currentAngle = AngleCalculatorSimple.getClockwiseAngle(w0, v, w1);
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
            LinkedList<Edge> vIn = v.elist;
            double minAngle = 2 * Math.PI;
            Edge nextE = null;
            Vertex W2 = null;
            for (int i = 0; i < vIn.size(); i++) {
                Edge e = vIn.get(i);
                Vertex w2 = (e.bot == v) ? e.top : e.bot;
                double angle = (currentE == e) ? 2 * Math.PI : AngleCalculatorSimple.getClockwiseAngle(w, v, w2);
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

        externalEdges = new LinkedList(ext);


        return ext;
    }

    static LinkedList<Edge> collectAllCompatibleNonTrivial(Vertex V) {
        LinkedList<Edge> edges = DrawFlat.collect_edges(V.getFirstEdge());
        LinkedList<Edge> compatible = new LinkedList<>();
        for (int i = 0; i < edges.size(); i++) {
            Edge e = edges.get(i);
            if (e.bot.elist.size() > 1 && e.top.elist.size() > 1 && DrawFlat.collect_edges_for_split(e.idxsplit, V).size() == 1) {
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

    public static List<Edge> externalEdges(List<Vertex> vertices) {
        if (externalEdges != null) {
            return new LinkedList(externalEdges);
        }

        Vertex v = vertices.get(0);

        Iterator<Vertex> vertexIt = vertices.iterator();
        while (vertexIt.hasNext()) {
            Vertex vertex = vertexIt.next();
            if (v.x > vertex.x) {
                v = vertex;
            }
        }

        Edge first = null;

        LinkedList<Edge> ext = new LinkedList<>();
        Vertex w = null;

        if (v.elist.size() == 1) {
            w = (v.elist.getFirst().bot == v) ? v.elist.getFirst().top : v.elist.getFirst().bot;
            first = v.elist.getFirst();

            Vertex t = w;
            w = v;
            v = t;
        } else {
            List<Edge> elist = v.elist;

            for (int i = 0; i < elist.size(); i++) {
                Vertex ww = null;
                Vertex w0 = (elist.get(i).bot == v) ? elist.get(i).top : elist.get(i).bot;
                double angle = 0;
                for (int j = 0; j < elist.size(); j++) {
                    if (i != j) {
                        Vertex w1 = (elist.get(j).bot == v) ? elist.get(j).top : elist.get(j).bot;
                        double currentAngle = AngleCalculatorSimple.getClockwiseAngle(w0, v, w1);
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
            LinkedList<Edge> vIn = v.elist;
            double minAngle = 2 * Math.PI;
            Edge nextE = null;
            Vertex W2 = null;
            for (int i = 0; i < vIn.size(); i++) {
                Edge e = vIn.get(i);
                Vertex w2 = (e.bot == v) ? e.top : e.bot;
                double angle = (currentE == e) ? 2 * Math.PI : AngleCalculatorSimple.getClockwiseAngle(w, v, w2);
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

        externalEdges = new LinkedList(ext);


        return ext;
    }

    public static void reset() {
        externalEdges = null;
    }
}
