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

package uk.ac.uea.cmp.phybre.flatnj.fdraw;

import uk.ac.uea.cmp.phybre.flatnj.ds.Network;
import uk.ac.uea.cmp.phybre.flatnj.tools.Utilities;

import java.util.*;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */

/**
 * @author balvociute
 */
public class CompatibleCorrector {

    AngleCalculator angleCalculator;
    private double balloonAngle = Math.PI * 0.99;
    LinkedList<Vertex> vertices;

    Vertex C;

    public CompatibleCorrector(AngleCalculator ac) {
        this.angleCalculator = ac;
    }

    public double correctAnglesForTrivial(LinkedList<Edge> edges, LinkedList<Vertex> vertices) {
        Edge split = edges.getFirst();

        Set<Edge> topEdges = Collector.getAllEdges(edges, true);
        Set<Edge> bottomEdges = Collector.getAllEdges(edges, false);

        double deltaAlpha;

        deltaAlpha = angleCalculator.computeForCompatible(edges);
        if (!Translocator.noCollisions(edges, topEdges, bottomEdges, deltaAlpha)) {
            //deltaAlpha = angleCalculator.computeMiddleAngleForTrivial(split);
        }

        if (Translocator.noCollisions(edges, topEdges, bottomEdges, deltaAlpha)) {
            Translocator.changeCoordinates(edges, deltaAlpha);
            for (int i = 0; i < vertices.size(); i++) {
                vertices.get(i).visited = false;
                LinkedList<Edge> vEd = vertices.get(i).elist;
                for (int k = 0; k < vEd.size(); k++) {
                    vEd.get(k).visited = false;
                }
            }
            return deltaAlpha;
        }
        return 0.0;
    }

    public double correctAngles(LinkedList<Edge> edges, LinkedList<Vertex> vertices) {
        Edge split = edges.getFirst();

        Vertex bottom = split.bot;
        Vertex top = split.top;

        Vertex v = bottom;
        Vertex w = top;


        Set<Edge> topEdges = Collector.getAllEdges(edges, true);
        Set<Edge> bottomEdges = Collector.getAllEdges(edges, false);

        double deltaAlpha = angleCalculator.computeForCompatible(edges);

        if (Translocator.noCollisions(edges, topEdges, bottomEdges, deltaAlpha)) {
            Translocator.changeCoordinates(edges, deltaAlpha);
            for (int i = 0; i < vertices.size(); i++) {
                vertices.get(i).visited = false;
                LinkedList<Edge> vEd = vertices.get(i).elist;
                for (int k = 0; k < vEd.size(); k++) {
                    vEd.get(k).visited = false;
                }
            }
            return deltaAlpha;
        }
        return 0.0;
    }

    public Vertex addEdgesforExternalTrivialSplits(Vertex v, PermutationSequenceDraw pseq) {
        Double minW = null;

        LinkedList<Edge> allEdges = DrawFlat.collect_edges(v.elist.getFirst());
        for (int i = 0; i < allEdges.size(); i++) {
            minW = ((minW == null || minW > allEdges.get(i).length()) ? allEdges.get(i).length() : minW);
        }
        minW /= 10;

        Set<Edge> externalEdges = new HashSet<>();
        Collector.getExternalEdges(externalEdges, v.elist.getFirst(), v, v.elist.getFirst());

        double[] trivial = pseq.trivial;
        if (trivial != null) {
            Set<Vertex> externalVertices = Collector.getExternalVertices(v.elist.getFirst(), v.elist.getFirst().top, v.elist.getFirst());
            externalVertices.addAll(Collector.getExternalVertices(v.elist.getFirst(), v.elist.getFirst().bot, v.elist.getFirst()));
            Iterator<Vertex> it = externalVertices.iterator();
            while (it.hasNext()) {
                Vertex vertex = it.next();
                int partitions = 0;
                for (int i = 0; i < vertex.taxa.size(); i++) {
                    int taxaNr = vertex.taxa.get(i);
                    double w = trivial[taxaNr];
                    if (w > 0) {
                        partitions++;
                    }
                }
                if (partitions > 0) {
                    Edge left;
                    Edge right;

                }
            }
        }
        return v;
    }

    public void addInnerTrivial(Vertex V, PermutationSequenceDraw ps, Network network) {
        //LinkedList<LinkedList<Edge>> balloons = Collector.collectBallons(balloonAngle, V);

        //LinkedList<Vertex>[] verticesInBalloons = Collector.assignVerticesToBalloons(balloons, V);

        Vertex c = computeCenterPoint(V);

        int lastSplit = getHighestNexusId(V.getFirstEdge());

        List<Vertex> allV = network.getVertices();


//                Vertex some = vertices.getFirst();
//                Edge newE = new Edge(c, some, lastSplit, 0);
//                newE.setBackgroundColor(red);
//                some.elist = new LinkedList();
//                some.elist.add(newE);
//                c.elist.add(newE);
//                vertices.add(c);

        LinkedList<Vertex> newVertices = new LinkedList<>();
        LinkedList<Vertex> oldVertices = new LinkedList<>();

        for (int j = 0; j < allV.size(); j++) {
            Vertex v = allV.get(j);
            Vertex w;
            Edge e;

            double anglePlus = 0.0; //is used in order to avoid edge overlaping
            if (v.taxa.size() > 0) {
                LinkedList<Integer> taxa = v.taxa;

                for (int t = taxa.size() - 1; t >= 0; t--) {
                    Integer taxon = taxa.get(t);
                    double length = ps.trivial[taxon];

                    double x;
                    double y;

                    if (length > 0) {
                        taxa.remove(taxon);

                        w = new Vertex(v.x, v.y);
                        e = new Edge(v, w, ++lastSplit, 0);
                        v.elist.add(e);
                        w.elist.add(e);
                        w.taxa = new LinkedList<>();
                        w.taxa.add(taxon);
                        oldVertices.add(v);
                        newVertices.add(w);

                        double sinAlpha = getSinusAlpha(v, c);
                        double alpha = Math.asin(sinAlpha);
                        sinAlpha = Math.sin(alpha + anglePlus);

                        anglePlus += 0.05;

                        if (v.y >= c.y) {
                            y = v.y + length * sinAlpha;
                        } else {
                            y = v.y - length * sinAlpha;
                        }

                        double cosAlpha = Math.cos(alpha);

                        if (w.x >= c.x) {
                            x = v.x + length * cosAlpha;
                        } else {
                            x = v.x - length * cosAlpha;
                        }

                        w.x = x;
                        w.y = y;
                    } else if (length == 0 && v.elist.size() == 1) {

                        Edge ee = v.elist.getFirst();

                        length = ee.length();

                        w = v;

                        v = (ee.bot == w) ? ee.top : ee.bot;

                        double sinAlpha = getSinusAlpha(v, c);
                        double alpha = Math.asin(sinAlpha);
                        sinAlpha = Math.sin(alpha + anglePlus);

                        anglePlus += 0.05;
                        if (v.y >= c.y) {
                            y = v.y + length * sinAlpha;
                        } else {
                            y = v.y - length * sinAlpha;
                        }

                        double cosAlpha = Math.cos(alpha);

                        if (w.x >= c.x) {
                            x = v.x + length * cosAlpha;
                        } else {
                            x = v.x - length * cosAlpha;
                        }

                        w.x = x;
                        w.y = y;
                    }
                }
            }

        }
        for (int j = 0; j < oldVertices.size(); j++) {
            allV.remove(oldVertices.get(j));
        }
        for (int q = 0; q < newVertices.size(); q++) {
            allV.add(newVertices.get(q));
        }

        network.removeVertices(oldVertices);
        network.addTrivial(newVertices);
        Iterator<Vertex> labeledVertexIterator = network.getLabeled().iterator();
        while (labeledVertexIterator.hasNext()) {
            Vertex v = labeledVertexIterator.next();
            v.setSize(3);
            v.setShape(null);
        }
    }

    private int getHighestNexusId(Edge e) {
        LinkedList<Edge> edges = DrawFlat.collect_edges(e);
        int max = -1;
        for (int i = 0; i < edges.size(); i++) {
            Edge current = edges.get(i);
            max = (max < current.idxsplit) ? current.idxsplit : max;
        }
        return max;
    }

    public void correctCompatible(LinkedList<Edge> allEdges, Vertex V) {

        LinkedList<LinkedList<Edge>> balloons = Collector.collectBallons(balloonAngle, V);
        LinkedList<Vertex>[] verticesInBalloons = Collector.assignVerticesToBalloons(balloons, V);

        for (int i = 0; i < balloons.size(); i++) {
            LinkedList<Edge> edges = balloons.get(i);
            //Vertex c = computeCenterPoint(edges);

            LinkedList<Edge> edgesBefore = (i == 0) ? balloons.getLast() : balloons.get(i - 1);
            LinkedList<Edge> edgesAfter = (i == balloons.size() - 1) ? balloons.getFirst() : balloons.get(i + 1);
            Vertex c = computeCenterPoint(edgesBefore, edges, edgesAfter);

            LinkedList<Vertex> vertices = verticesInBalloons[i];

            for (int j = 0; j < vertices.size(); j++) {
                Vertex w = vertices.get(j);
                if (w.elist.size() == 1) {
                    Edge e = w.elist.getFirst();

                    double x;
                    double y;
                    Vertex v = (e.top == w) ? e.bot : e.top;
                    double sinAlpha = getSinusAlpha(v, c);


                    double length = e.length();
                    if (v.y >= c.y) {
                        y = v.y + length * sinAlpha;
                    } else {
                        y = v.y - length * sinAlpha;
                    }

                    double cosAlpha = getCosinusAlpha(v, c);

                    if (v.x >= c.x) {
                        x = v.x + length * cosAlpha;
                    } else {
                        x = v.x - length * cosAlpha;
                    }

                    if (!Translocator.twoLinesCrosses(v.x, v.y, x, y, new HashSet<>(allEdges))
                            && !Translocator.twoLinesCrosses(w.x, w.y, x, y, new HashSet<>(allEdges))) {
                        w.x = x;
                        w.y = y;
                    }
                }
            }
        }
    }

    private double getSinusAlpha(Vertex v, Vertex c) {
        double sinAlpha = Math.abs(v.y - c.y) / Math.sqrt((v.x - c.x) * (v.x - c.x) + (v.y - c.y) * (v.y - c.y));
        return sinAlpha;
    }

    private double getCosinusAlpha(Vertex v, Vertex c) {
        double sinAlpha = Math.abs(v.x - c.x) / Math.sqrt((v.x - c.x) * (v.x - c.x) + (v.y - c.y) * (v.y - c.y));
        return sinAlpha;
    }

    private Vertex computeCenterPoint(Vertex net) {
        double[] corners = Utilities.getCorners(DrawFlat.collect_vertices(net));
        double x = 0.5 * (corners[0] + corners[1]);
        double y = 0.5 * (corners[2] + corners[3]);
        return new Vertex(x, y);
    }

    private Vertex computeCenterPoint(LinkedList<Edge> edgesBefore, LinkedList<Edge> edges, LinkedList<Edge> edgesAfter) {
        Line first = getMiddleLine(edgesBefore, edges);
        Line last = getMiddleLine(edges, edgesAfter);
        double x = (first.b - last.b) / (last.a - first.a);
        double y = first.a * x + first.b;
        return new Vertex(x, y);
    }

    private Line getMiddleLine(LinkedList<Edge> edges1, LinkedList<Edge> edges2) {
        Edge beforeLast = edges1.getLast();
        Edge fist = edges2.getFirst();

        Set<Vertex> vertices1 = getVerticesFromEdges(edges1);
        Set<Vertex> vertices2 = getVerticesFromEdges(edges2);

        Vertex v = (beforeLast.bot == fist.bot || beforeLast.bot == fist.top) ? beforeLast.bot : beforeLast.top;
        Vertex w1 = (beforeLast.bot == v) ? beforeLast.top : beforeLast.bot;
        Vertex w2 = (fist.bot == v) ? fist.top : fist.bot;

        vertices1.remove(v);
        vertices2.remove(v);

        AngleCalculatorSimple angleCalculatorSimple = new AngleCalculatorSimple();
        Vertex striker = angleCalculatorSimple.findStrikerOnTheRight(v, w1, vertices1);
        Vertex defender = angleCalculatorSimple.findDefenderOnTheRight(v, w2, vertices2);

        double alpha = AngleCalculatorSimple.getClockwiseAngle(striker, v, defender) * 0.5;
        double xt = w2.x - v.x;
        double yt = w2.y - v.y;

        double x = xt * Math.cos(alpha) - yt * Math.sin(alpha) + v.x;
        double y = xt * Math.sin(alpha) + yt * Math.cos(alpha) + v.y;

        return new Line(v, new Vertex(x, y));

    }

    private Set<Vertex> getVerticesFromEdges(LinkedList<Edge> edges) {
        Set<Vertex> vertices = new HashSet<>();
        for (int i = 0; i < edges.size(); i++) {
            vertices.add(edges.get(i).bot);
            vertices.add(edges.get(i).top);
        }
        return vertices;
    }

    public void correctAllCompatible(TreeSet[] splitedges, LinkedList<Vertex> vertices) {
        for (int i = 0; i < splitedges.length; i++) {
            if (splitedges[i].size() == 1) {
                Edge e = (Edge) splitedges[i].first();
                LinkedList<Edge> edges = new LinkedList<>();
                edges.add(e);
                double moved = 0.0;
                if (e.top.elist.size() == 1 || e.bot.elist.size() == 1) {
                    moved = correctAnglesForTrivial(edges, vertices);
                }
                if (moved == 0) {
                    correctAngles(edges, vertices);
                }
            }
        }
    }

    public boolean pointInsideNetwork(Vertex c, List<Edge> external) {
        int crossingsWithBorders = castRay(c, external);
        if (crossingsWithBorders % 2 == 0) {
            return false;
        }
        return true;
    }

    private int castRay(Vertex c, List<Edge> edges) {
        Line ray = new Line(c, new Vertex(c.x + 1, c.y + 1));
        int crossings = 0;
        Iterator<Edge> edgeIterator = edges.iterator();
        while (edgeIterator.hasNext()) {
            Edge e = edgeIterator.next();
            if (!e.compatible) {
                Line eLine = new Line(e);
                Vertex intP = intersection(ray, eLine);
                if (Math.signum(intP.x - e.top.x) != Math.signum(intP.x - e.bot.x) && intP.x > c.x && intP.y > c.y) {
                    crossings++;
                }
            }
        }
        return crossings;
    }

    private Vertex intersection(Line l1, Line l2) {
        double x = (l2.b - l1.b) / (l1.a - l2.a);
        double y = l1.a * x + l1.b;
        return new Vertex(x, y);
    }

    public int moveTrivial(Vertex V, int iterations, Window window, Network network) {
        vertices = new LinkedList<>();

        C = computeCenterPoint(V);

        Set<double[]> po = new HashSet<>();
        Set<double[]> li = new HashSet<>();

        List<Edge> trivial = network.getTrivial();

        for (int i = 0; i < trivial.size(); i++) {
            Edge e = trivial.get(i);
            vertices.add(e.bot.elist.size() == 1 ? e.bot : e.top);
        }

        double[] corners = Utilities.getCorners(network.getVertices());
        double X = (corners[0] + corners[1]) * 0.5;
        double Y = (corners[2] + corners[3]) * 0.5;

        Vertex center = new Vertex(X, Y);

        List<Edge> orderedTrivial = sortEdges(trivial, center, V);

        List<Edge> external = network.getExternal();

        //Collect all edges and remove all trivial and external ones so that
        //only inner edges remain.
        List<Edge> inner = network.getInternal();

        //Variable to track if any changes were made;
        int corrected = 0;

        boolean[] correctedEdges;

        //Repeat
        for (int i = 0; i < iterations; i++) {
            corrected = 0;

            correctedEdges = new boolean[trivial.size()];

            //Go through all edges
            for (int j = 0; j < orderedTrivial.size(); j++) {
                char sign = '.';
                Edge e = orderedTrivial.get(j);

                if (e.length() > 0) {
                    Vertex v = (e.bot.elist.size() == 1) ? e.top : e.bot;
                    Vertex w = e.getOther(v);

                    List<Edge> edges = new LinkedList(network.getExternal());
                    edges.addAll(network.getTrivial());
                    edges.remove(e);


                    boolean inside = pointNotInTheSet(v, external);

                    double[] environment = angleCalculator.optimizedAngleForCompatible2(v, w, e, edges, this, network, window, true);

                    if (environment.length == 0 && inside) {
                        environment = angleCalculator.optimizedAngleForCompatible2(v, w, e, network.getInternal(), this, network, window, false);
                    }

                    edges.add(e);

                    //Score current position
                    //trivial.remove(e);
                    Score currentScore = getScore(w, v.x, v.y, w.x, w.y, trivial, e, inner, external, network.getLabeled());


                    //Score all angles in the environment
                    Score[] scores = scoreAngles(v, w, environment, trivial, e, inner, external, network.getLabeled());
                    //trivial.add(e);

                    //Find the best angle:
                    int best = -1;
                    Score bestSoFar = currentScore;

                    for (int k = 0; k < scores.length; k++) {
                        if (inside) {
                            if (scores[k].betterInternal(bestSoFar)) {
                                best = k;
                                bestSoFar = scores[k];
                            }
                        } else {
                            if (scores[k].betterExternal(bestSoFar)) {
                                best = k;
                                bestSoFar = scores[k];
                            }
                        }
                    }

                    if (best != -1) {
                        if (window != null) {
                            double[] point = new double[3];
                            point[0] = w.x;
                            point[1] = w.y;
                            point[2] = w.height;

                            double[] line = new double[4];
                            line[0] = w.x;
                            line[1] = w.y;
                            line[2] = v.x;
                            line[3] = v.y;

                            po.add(point);
                            li.add(line);
                        }

                        w.x = scores[best].newX;
                        w.y = scores[best].newY;

                        corrected++;
                        sign = '*';
                        correctedEdges[j] = true;
                    }
                    //System.err.print(sign);
                }

            }

            //System.err.println();
            if (corrected == 0) {
                break;
            }
        }
        if (window != null) {
            window.setLast(li, po);
        }

        return corrected;

    }

    public List<Edge> sortEdges(List<Edge> edges, Vertex v, Vertex w) {
        LinkedList<Edge> sorted = new LinkedList<>();
        LinkedList<Integer> sIndex = new LinkedList<>();

        double[] angles = new double[edges.size()];
        Iterator<Edge> eIt = edges.iterator();
        int index = 0;
        while (eIt.hasNext()) {
            Edge e = eIt.next();
            Vertex c = (e.top.elist.size() == 1) ? e.bot : e.top;
            angles[index++] = AngleCalculatorSimple.getClockwiseAngle(w, v, c);
        }
        for (int i = 0; i < angles.length; i++) {
            if (i == 0) {
                sIndex.add(i);
                sorted.add(edges.get(i));
            } else {
                for (int j = 0; j < sIndex.size(); j++) {
                    if (angles[i] >= angles[sIndex.get(j)]) {
                        sIndex.add(j, i);
                        sorted.add(j, edges.get(i));
                        break;
                    } else if (j == sIndex.size() - 1) {
                        sIndex.add(i);
                        sorted.add(edges.get(i));
                        break;
                    }

                }
            }
        }
        return sorted;
    }

    private Score[] scoreAngles(Vertex v, Vertex w, double[] environment, List<Edge> trivial, Edge e, List<Edge> inner, List<Edge> external, List<Vertex> labeled) {
        Score[] scores = new Score[environment.length];

        double xt = w.x - v.x;
        double yt = w.y - v.y;

        for (int i = 0; i < environment.length; i++) {
            double angle = environment[i];
            double newX = xt * Math.cos(angle) - yt * Math.sin(angle) + v.x;
            double newY = xt * Math.sin(angle) + yt * Math.cos(angle) + v.y;

            scores[i] = getScore(w, v.x, v.y, newX, newY, trivial, e, inner, external, labeled);
            scores[i].newX = newX;
            scores[i].newY = newY;
        }

        return scores;
    }

    private Score getScore(Vertex w, double x, double y, double x1, double y1, List<Edge> trivial, Edge e, List<Edge> internal, List<Edge> external, List<Vertex> labeled) {
        Score score = new Score();

        double punishment = 1;
        LinkedList<Edge> eee = new LinkedList(external);
        eee.addAll(trivial);

        boolean outter = !pointNotInTheSet(w, external);

        //Determine if the endpoint is inside of the network
        if (pointInsideNetwork(new Vertex(x1, y1), external)) {
            score.insideScore = punishment;
            eee.addAll(internal);
        }
        //If the point is outside, then pendant length is evaluated
        else if (!outter) {
            score.pendantScore = getPendantScore(x, y, x1, y1, e, external) / e.length();
        }


        //Evaluate how many edges this one would cross
        //System.out.println(trivial.remove(e));
        score.intTrScore = sumUpIntersections(x, y, x1, y1, punishment, trivial);
        //trivial.add(e);

        score.intInScore = sumUpIntersections(x, y, x1, y1, punishment, internal);
        score.intOutScore = sumUpIntersections(x, y, x1, y1, punishment, external);


        //Get distance to the nearest edge
        score.distScore = sumUpDistancestoEdges(e, x, y, x1, y1, eee);

        eee.addAll(internal);
        score.distScore2 = sumUpDistancesForVertex(w, x1, y1, labeled) / e.length();
        if (score.distScore2 > 1) {
            score.distScore2 = 1;
        }

        //Get smallest angle with incident edges
        score.anglScore = scoreAngles(e, x, y, x1, y1);

        score.dirScore = scoreDirection(e, x, y, x1, y1);

        return score;
    }

    private double getPendantScore(double x, double y, double x1, double y1, Edge ee, List<Edge> external) {
        double score = 0.0;
        for (int i = 0; i < external.size(); i++) {
            Edge e = external.get(i);
            Vertex c = Translocator.getIntersectionPoint(x, y, x1, y1, e.bot.x, e.bot.y, e.top.x, e.top.y);
            if (c != null) {
                score = AngleCalculatorSimple.distance(new Vertex(x1, y1), c);
            }
        }
        return score;
    }

    private double scoreAngles(Edge e, double x, double y, double x1, double y1) {
        Double score = null;
        int atAll = 0;
        double threshold = Math.PI / 2.0;
        Vertex v = (e.bot.x == x && e.bot.y == y) ? e.bot : e.top;
        Vertex w = new Vertex(x1, y1);
        LinkedList<Edge> incident = v.elist;
        for (int i = 0; i < incident.size(); i++) {
            Edge ee = incident.get(i);
            if (e != ee) {
                Vertex w2 = (ee.bot == v) ? ee.top : ee.bot;
                double angle = angleCalculator.getAngle(w, v, w2);

                score = (score == null || score > angle) ? angle : score;
            }
        }

        return score;
    }


    private double sumUpIntersections(double x, double y, double x1, double y1, double punishment, List<Edge> edges) {
        double score = 0.0;
        for (int i = 0; i < edges.size(); i++) {
            Edge ee = edges.get(i);
            if (ee.length() > 0) {
                if (Translocator.cross(x, y, x1, y1, ee.bot.x, ee.bot.y, ee.top.x, ee.top.y)) {
                    score += punishment;
                }
            }
        }
        return score;
    }

    private double sumUpDistancestoEdges(Edge e, double x, double y, double x1, double y1, List<Edge> edges) {
        double score = e.length();
        //double avgLength = computeAverageLength(e);
        boolean longEdge = true;//(e.length() > avgLength * 2.0) ? true : false;
        Iterator<Edge> iterator = (new HashSet(edges)).iterator();
        while (iterator.hasNext()) {
            Edge ee = iterator.next();
            if (ee != e) {
                Vertex v1;
                if (e.bot.x == x && e.bot.y == y) {
                    v1 = e.bot;
                } else {
                    v1 = e.top;
                }
                Vertex v2 = new Vertex(x1, y1);

                double distance = Collector.getDistanceEdgeToEgde(v1, v2, ee, longEdge);

                score = (score > distance) ? distance : score;
            }
        }
        score = score / e.length();
        score = (score > 1) ? 1 : score;
        return score;
    }

    private double sumUpDistancesForVertex(Vertex v, double x, double y, List<Vertex> vertices) {
        Iterator<Vertex> iterator = vertices.iterator();
        Double score = null;
        while (iterator.hasNext()) {
            Vertex w = iterator.next();
            if (w != v) {
                double dist = AngleCalculatorSimple.distance(new Vertex(x, y), w);
                if (score == null || score > dist) {
                    score = dist;
                }
            }
        }
        return score;
    }

    public boolean pointNotInTheSet(Vertex vertex, List<Edge> edges) {
        for (int i = 0; i < edges.size(); i++) {
            Edge e = edges.get(i);
            if ((e.bot.x == vertex.x && e.bot.y == vertex.y) || (e.top.x == vertex.x && e.top.y == vertex.y)) {
                return false;
            }
        }
        return true;
    }

    public double moveCompatible(Vertex V, int iterations, Window window, Network network) {
        List<Edge> compatible = network.getCompatible();
        List<Edge> external = network.getExternal();
        external.addAll(network.getTrivial());

        Double corrected = 0.0;

        for (int j = 0; j < iterations; j++) {
            for (int k = 0; k < compatible.size(); k++) {
                Edge e = compatible.get(k);

                LinkedList<Edge> tmp = new LinkedList<>();
                tmp.add(e);
                Set<Edge> topEdges = Collector.getAllEdges(tmp, true);
                Set<Edge> bottomEdges = Collector.getAllEdges(tmp, false);

                List<Edge> topExternal = new LinkedList(topEdges);
                for (int i = topExternal.size() - 1; i >= 0; i--) {
                    if (!external.contains(topExternal.get(i))) {
                        topExternal.remove(i);
                    }
                }
                List<Edge> bottomExternal = new LinkedList(bottomEdges);
                for (int i = bottomExternal.size() - 1; i >= 0; i--) {
                    if (!external.contains(bottomExternal.get(i))) {
                        bottomExternal.remove(i);
                    }
                }

                Vertex v = e.bot;
                Vertex w = e.top;

                double bestAngle;

                boolean moved = false;

                List<Edge> trivial = network.getTrivial();
                Iterator<Edge> eIt = trivial.iterator();
                while (eIt.hasNext()) {
                    Edge ed = eIt.next();
                    topEdges.remove(e);
                    bottomEdges.remove(e);
                }


                if (!moved) {
                    bestAngle = angleCalculator.optimizedAngleForCompatible(v, w, e, bottomExternal, topExternal);
                    topEdges.add(e);
                    moved = tryRotating(v, bottomEdges, topEdges, 0 - bestAngle, DrawFlat.collect_vertices(V));
                    if (moved) {
                        corrected = bestAngle;
                    }
                    topEdges.remove(e);
                    bestAngle = angleCalculator.optimizedAngleForCompatible(w, v, e, topExternal, bottomExternal);
                    bottomEdges.add(e);
                    boolean moved2 = tryRotating(w, topEdges, bottomEdges, 0 - bestAngle, DrawFlat.collect_vertices(V));
                    if (moved2) {
                        corrected = bestAngle;
                        moved = moved2;
                    }
                    bottomEdges.remove(e);
                }
                if (!moved) {
                    bestAngle = angleCalculator.computeMiddleAngleForTrivial(e, e.bot, e.top);
                    moved = tryAngle(v, w, e, tmp, topEdges, bottomEdges, bestAngle, DrawFlat.collect_vertices(V));
                    if (moved && (corrected == null || corrected < bestAngle)) {
                        corrected = bestAngle;
                    }
                    bestAngle = angleCalculator.computeMiddleAngleForTrivial(e, e.top, e.bot);
                    boolean moved2 = tryAngle(w, v, e, tmp, bottomEdges, topEdges, bestAngle, DrawFlat.collect_vertices(V));
                    if (moved2 && (corrected == null || corrected < bestAngle)) {
                        corrected = bestAngle;
                        moved = moved2;
                    }
                }
//                if(!moved)
//                {
//                    bestAngle = 0.2;
//                    moved = tryAngle(v, w, e, tmp, topEdges, bottomEdges, bestAngle, DrawFlat.collect_vertices(V));
//                    if (moved && (corrected == null || corrected < bestAngle))
//                    {
//                        corrected = bestAngle;
//                    }
//                    bestAngle = Math.PI * 2 - 0.2;
//                    boolean moved2 = tryAngle(w, v, e, tmp, bottomEdges, topEdges, bestAngle, DrawFlat.collect_vertices(V));
//                    if (moved2 && (corrected == null || corrected < bestAngle))
//                    {
//                        corrected = bestAngle;
//                        moved = moved2;
//                    }
//                }

            }

        }
        return corrected;
    }

    private boolean tryAngle(Vertex bot, Vertex top, Edge e, LinkedList<Edge> edges, Set<Edge> topEdges, Set<Edge> bottomEdges, double deltaAlpha, List<Vertex> vertices) {
        boolean moved = false;
        double gap = Math.PI / 36;
        double angleWithGap = (edges.size() == 1) ? deltaAlpha : Math.signum(deltaAlpha) * (Math.abs(deltaAlpha) + gap);
        if (Translocator.noCollisions(bot, top, e, edges, topEdges, bottomEdges, angleWithGap) && Translocator.noCollisions(bot, top, e, edges, topEdges, bottomEdges, deltaAlpha)) {
            Translocator.changeCoordinates(bot, top, edges, deltaAlpha);
            moved = true;
            for (int i = 0; i < vertices.size(); i++) {
                vertices.get(i).visited = false;
                LinkedList<Edge> vEd = vertices.get(i).elist;
                for (int k = 0; k < vEd.size(); k++) {
                    vEd.get(k).visited = false;
                }
            }

        }
        return moved;
    }

    private boolean tryRotating(Vertex bot, Set<Edge> bottomEdges, Set<Edge> topEdges, double deltaAlpha, List<Vertex> vertices) {
        boolean moved = false;
        double gap = Math.PI / 36;
        double angleWithGap = Math.signum(deltaAlpha) * (Math.abs(deltaAlpha) + gap);
        if (Translocator.noCollisionsForRotation(bot, bottomEdges, topEdges, angleWithGap) && Translocator.noCollisionsForRotation(bot, bottomEdges, topEdges, deltaAlpha)) {
            Translocator.rotateAll(bot, bottomEdges, deltaAlpha);
            moved = true;
            for (int i = 0; i < vertices.size(); i++) {
                vertices.get(i).visited = false;
                LinkedList<Edge> vEd = vertices.get(i).elist;
                for (int k = 0; k < vEd.size(); k++) {
                    vEd.get(k).visited = false;
                }
            }

        }
        return moved;
    }

    private double scoreDirection(Edge e, double x, double y, double x1, double y1) {
        double length = e.length();
        double sinAlpha = getSinusAlpha(new Vertex(x, y), C);
        double alpha = Math.asin(sinAlpha);

        double Y;
        double X;

        if (y >= C.y) {
            Y = y + length * sinAlpha;
        } else {
            Y = y - length * sinAlpha;
        }

        double cosAlpha = Math.cos(alpha);

        if (x >= C.x) {
            X = x + length * cosAlpha;
        } else {
            X = x - length * cosAlpha;
        }

        double deviation = angleCalculator.getAngle(new Vertex(X, Y), C, new Vertex(x1, y1));

        return deviation;
    }

    private class Score {

        double distScore;
        double distScore2;
        double anglScore;
        double insideScore;
        double pendantScore;
        double dirScore;
        private double intTrScore;
        private double intInScore;
        double intOutScore;
        private double newX;
        private double newY;

        private boolean betterExternal(Score s) {
            int trBoolean = (intTrScore > 0) ? 1 : 0;
            int trBoolean2 = (s.intTrScore > 0) ? 1 : 0;

            double c1 = 0.75;
            double c2 = 1;
            double c3 = 0.5;
            double c4 = 0.25;

            double score = c1 * anglScore / Math.PI + c2 * (Math.PI - dirScore) / Math.PI + c3 * distScore + c4 * distScore2;
            double score2 = c1 * s.anglScore / Math.PI + c2 * (Math.PI - s.dirScore) / Math.PI + c3 * s.distScore + c4 * s.distScore2;

            if (insideScore < s.insideScore) {
                return true;
            } else if (insideScore == s.insideScore) {
                if (trBoolean + intOutScore < trBoolean2 + s.intOutScore) {
                    return true;
                } else if (trBoolean + intOutScore == trBoolean2 + s.intOutScore) {
                    if (score > score2) {
                        return true;
                    }

                }
            }
            return false;
        }

        private boolean betterInternal(Score s) {
            int trBoolean = (intTrScore > 0) ? 1 : 0;
            int trBoolean2 = (s.intTrScore > 0) ? 1 : 0;

            int intInBoolean = (intInScore == 0) ? 1 : 0;
            int intInBoolean2 = (s.intInScore == 0) ? 1 : 0;

            //Algae
            double c1 = 0.5;    //0.5
            double c2 = 0.5;    //0.5
            double c3 = 1;      //1
            double c4 = 1;      //1
            double c5 = 0.5;      //1
            double c6 = 0.25;

            double score = c1 * anglScore / Math.PI + c5 * (Math.PI - dirScore) / Math.PI + c2 * distScore + c3 * pendantScore + c4 * distScore2 + c6 * intInBoolean;
            double score2 = c1 * s.anglScore / Math.PI + c5 * (Math.PI - s.dirScore) / Math.PI + c2 * s.distScore + c3 * pendantScore + c4 * s.distScore2 + c6 * intInBoolean2;

            if (insideScore < s.insideScore) {
                return true;
            } else if (insideScore == s.insideScore) {
                if (trBoolean + intOutScore < trBoolean2 + s.intOutScore) {
                    return true;
                } else if (trBoolean + intOutScore == trBoolean2 + s.intOutScore) {
                    if (score > score2) {
                        return true;
                    }

                }
            }
            return false;
        }

        boolean betterCompatible(Score s) {
            if (intInScore < s.intInScore) {
                return true;
            } else if (intInScore == s.intInScore) {
                if (distScore > s.distScore) {
                    return true;
                } else if (distScore == s.distScore) {
                    if (anglScore > s.anglScore) {
                        return true;
                    }
                }
            }
            return false;
        }
    }


    private class Line {

        double a;
        double b;

        private Line(Vertex v, Vertex w) {
            setAB(v, w);
        }

        private Line(Edge e) {
            setAB(e.bot, e.top);
        }

        private Line(double x1, double y1, double x2, double y2) {
            a = (y1 - y2) / (x1 - x2);
            b = y1 - a * x1;
        }

        private void setAB(Vertex v, Vertex w) {
            a = (v.y - w.y) / (v.x - w.x);
            b = v.y - a * v.x;
        }

        public boolean equals(Line l2) {
            if (this.a == l2.a && this.b == l2.b) {
                return true;
            }
            return false;
        }
    }
}
