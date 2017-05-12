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

import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.network.*;

import java.util.*;

/**
 * @author balvociute
 */
public class CompatibleCorrector {

    private AngleCalculator angleCalculator;

    public CompatibleCorrector(AngleCalculator ac) {
        this.angleCalculator = ac;
    }

    public double correctAnglesForTrivial(EdgeList edges, LinkedList<Vertex> vertices) {

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
                vertices.get(i).setVisited(false);
                LinkedList<Edge> vEd = vertices.get(i).getEdgeList();
                for (int k = 0; k < vEd.size(); k++) {
                    vEd.get(k).setVisited(false);
                }
            }
            return deltaAlpha;
        }
        return 0.0;
    }

    public double correctAngles(EdgeList edges, LinkedList<Vertex> vertices) {
        Edge split = edges.getFirst();

        Set<Edge> topEdges = Collector.getAllEdges(edges, true);
        Set<Edge> bottomEdges = Collector.getAllEdges(edges, false);

        double deltaAlpha = angleCalculator.computeForCompatible(edges);

        if (Translocator.noCollisions(edges, topEdges, bottomEdges, deltaAlpha)) {
            Translocator.changeCoordinates(edges, deltaAlpha);
            for (int i = 0; i < vertices.size(); i++) {
                vertices.get(i).setVisited(false);
                LinkedList<Edge> vEd = vertices.get(i).getEdgeList();
                for (int k = 0; k < vEd.size(); k++) {
                    vEd.get(k).setVisited(false);
                }
            }
            return deltaAlpha;
        }
        return 0.0;
    }

    public void addInnerTrivial(Vertex V, double[] trivial, Network network) {
        //LinkedList<LinkedList<Edge>> balloons = Collector.collectBallons(BALLOON_ANGLE, V);

        //LinkedList<Vertex>[] verticesInBalloons = Collector.assignVerticesToBalloons(balloons, V);

        Vertex c = computeCenterPoint(V);

        int lastSplit = getHighestNexusId(V.getFirstEdge());

        List<Vertex> allV = network.getAllVertices();


//                Vertex some = vertices.getFirst();
//                Edge newE = new Edge(c, some, lastSplit, 0);
//                newE.setBackgroundColor(red);
//                some.elist = new LinkedList();
//                some.elist.add(newE);
//                c.elist.add(newE);
//                vertices.add(c);

        VertexList newVertices = new VertexList();
        VertexList oldVertices = new VertexList();

        for (int j = 0; j < allV.size(); j++) {
            Vertex v = allV.get(j);
            Vertex w;
            Edge e;

            double anglePlus = 0.0; //is used in order to avoid edge overlaping
            if (v.getTaxa().size() > 0) {
                IdentifierList taxa = v.getTaxa();

                for (int t = taxa.size() - 1; t >= 0; t--) {
                    Identifier taxon = taxa.get(t);
                    double length = trivial[taxon.getId()];

                    double x;
                    double y;

                    if (length > 0) {
                        taxa.remove(taxon);

                        w = new Vertex(v.getX(), v.getY());
                        e = new Edge(v, w, ++lastSplit, 0);
                        v.getEdgeList().add(e);
                        w.getEdgeList().add(e);
                        w.setTaxa(new IdentifierList());
                        w.getTaxa().add(taxon);
                        oldVertices.add(v);
                        newVertices.add(w);

                        double sinAlpha = getSinusAlpha(v, c);
                        double alpha = Math.asin(sinAlpha);
                        sinAlpha = Math.sin(alpha + anglePlus);

                        anglePlus += 0.05;

                        if (v.getY() >= c.getY()) {
                            y = v.getY() + length * sinAlpha;
                        } else {
                            y = v.getY() - length * sinAlpha;
                        }

                        double cosAlpha = Math.cos(alpha);

                        if (w.getX() >= c.getX()) {
                            x = v.getX() + length * cosAlpha;
                        } else {
                            x = v.getX() - length * cosAlpha;
                        }

                        w.setX(x);
                        w.setY(y);
                    } else if (length == 0 && v.getEdgeList().size() == 1) {

                        Edge ee = v.getEdgeList().getFirst();

                        length = ee.length();

                        w = v;

                        v = (ee.getBottom() == w) ? ee.getTop() : ee.getBottom();

                        double sinAlpha = getSinusAlpha(v, c);
                        double alpha = Math.asin(sinAlpha);
                        sinAlpha = Math.sin(alpha + anglePlus);

                        anglePlus += 0.05;
                        if (v.getY() >= c.getY()) {
                            y = v.getY() + length * sinAlpha;
                        } else {
                            y = v.getY() - length * sinAlpha;
                        }

                        double cosAlpha = Math.cos(alpha);

                        if (w.getX() >= c.getX()) {
                            x = v.getX() + length * cosAlpha;
                        } else {
                            x = v.getX() - length * cosAlpha;
                        }

                        w.setX(x);
                        w.setY(y);
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
        network.addTrivialEdges(newVertices);
    }

    private int getHighestNexusId(Edge e) {
        LinkedList<Edge> edges = e.collectEdges();
        int max = -1;
        for (int i = 0; i < edges.size(); i++) {
            Edge current = edges.get(i);
            max = (max < current.getSplitIndex()) ? current.getSplitIndex() : max;
        }
        return max;
    }

    private double getSinusAlpha(Vertex v, Vertex c) {
        double sinAlpha = Math.abs(v.getY() - c.getY()) / Math.sqrt((v.getX() - c.getX()) * (v.getX() - c.getX()) + (v.getY() - c.getY()) * (v.getY() - c.getY()));
        return sinAlpha;
    }


    private Vertex computeCenterPoint(Vertex net) {
        double[] corners = net.collectVertices().getCorners();
        double x = 0.5 * (corners[0] + corners[1]);
        double y = 0.5 * (corners[2] + corners[3]);
        return new Vertex(x, y);
    }

    public boolean pointInsideNetwork(Vertex c, List<Edge> external) {
        return castRay(c, external) % 2 != 0;
    }

    private int castRay(Vertex c, List<Edge> edges) {
        Line ray = new Line(c, new Vertex(c.getX() + 1, c.getY() + 1));
        int crossings = 0;
        for(Edge e : edges) {
            if (!e.isCompatible()) {
                Vertex intP = ray.intersection(new Line(e));
                if (Math.signum(intP.getX() - e.getTop().getX()) != Math.signum(intP.getX() - e.getBottom().getX()) &&
                        intP.getX() > c.getX() && intP.getY() > c.getY()) {
                    crossings++;
                }
            }
        }
        return crossings;
    }



    public int moveTrivial(Vertex V, int iterations, Network network) {
        VertexList vertices = new VertexList();

        Vertex centerPoint = computeCenterPoint(V);

        List<Edge> trivial = network.getTrivialEdges();

        for (int i = 0; i < trivial.size(); i++) {
            Edge e = trivial.get(i);
            vertices.add(e.getBottom().getEdgeList().size() == 1 ? e.getBottom() : e.getTop());
        }

        double[] corners = network.getAllVertices().getCorners();
        double X = (corners[0] + corners[1]) * 0.5;
        double Y = (corners[2] + corners[3]) * 0.5;

        Vertex center = new Vertex(X, Y);

        List<Edge> orderedTrivial = sortEdges(trivial, center, V);

        List<Edge> external = network.getExternalEdges();

        //Collect all edges and remove all trivial and external ones so that
        //only inner edges remain.
        List<Edge> inner = network.getInternalEdges();

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
                    Vertex v = (e.getBottom().getEdgeList().size() == 1) ? e.getTop() : e.getBottom();
                    Vertex w = e.getOther(v);

                    List<Edge> edges = new LinkedList(network.getExternalEdges());
                    edges.addAll(network.getTrivialEdges());
                    edges.remove(e);


                    boolean inside = pointNotInTheSet(v, external);

                    double[] environment = angleCalculator.optimizedAngleForCompatible2(v, w, e, edges, this, network, true);

                    if (environment.length == 0 && inside) {
                        environment = angleCalculator.optimizedAngleForCompatible2(v, w, e, network.getInternalEdges(), this, network, false);
                    }

                    edges.add(e);

                    //Score current position
                    //trivial.remove(e);
                    Score currentScore = getScore(w, v.getX(), v.getY(), w.getX(), w.getY(), trivial, e, inner, external, network.getLabeledVertices(), centerPoint);


                    //Score all angles in the environment
                    Score[] scores = scoreAngles(v, w, environment, trivial, e, inner, external, network.getLabeledVertices(), centerPoint);
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

                        w.setX(scores[best].newX);
                        w.setY(scores[best].newY);

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
            Vertex c = (e.getTop().getEdgeList().size() == 1) ? e.getBottom() : e.getTop();
            angles[index++] = Vertex.getClockwiseAngle(w, v, c);
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

    private Score[] scoreAngles(Vertex v, Vertex w, double[] environment, List<Edge> trivial, Edge e, List<Edge> inner, List<Edge> external, List<Vertex> labeled, Vertex centrePoint) {
        Score[] scores = new Score[environment.length];

        double xt = w.getX() - v.getX();
        double yt = w.getY() - v.getY();

        for (int i = 0; i < environment.length; i++) {
            double angle = environment[i];
            double newX = xt * Math.cos(angle) - yt * Math.sin(angle) + v.getX();
            double newY = xt * Math.sin(angle) + yt * Math.cos(angle) + v.getY();

            scores[i] = getScore(w, v.getX(), v.getY(), newX, newY, trivial, e, inner, external, labeled, centrePoint);
            scores[i].newX = newX;
            scores[i].newY = newY;
        }

        return scores;
    }

    private Score getScore(Vertex w, double x, double y, double x1, double y1, List<Edge> trivial, Edge e, List<Edge> internal, List<Edge> external, List<Vertex> labeled, Vertex centrePoint) {
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

        score.dirScore = scoreDirection(e, x, y, x1, y1, centrePoint);

        return score;
    }

    private double getPendantScore(double x, double y, double x1, double y1, Edge ee, List<Edge> external) {
        double score = 0.0;
        for (int i = 0; i < external.size(); i++) {
            Edge e = external.get(i);
            Vertex c = Translocator.getIntersectionPoint(x, y, x1, y1, e.getBottom().getX(), e.getBottom().getY(), e.getTop().getX(), e.getTop().getY());
            if (c != null) {
                score = new Vertex(x1, y1).calcDistanceTo(c);
            }
        }
        return score;
    }

    private double scoreAngles(Edge e, double x, double y, double x1, double y1) {
        Double score = null;
        int atAll = 0;
        double threshold = Math.PI / 2.0;
        Vertex v = (e.getBottom().getX() == x && e.getBottom().getY() == y) ? e.getBottom() : e.getTop();
        Vertex w = new Vertex(x1, y1);
        LinkedList<Edge> incident = v.getEdgeList();
        for (int i = 0; i < incident.size(); i++) {
            Edge ee = incident.get(i);
            if (e != ee) {
                Vertex w2 = (ee.getBottom() == v) ? ee.getTop() : ee.getBottom();
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
                if (Translocator.cross(x, y, x1, y1, ee.getBottom().getX(), ee.getBottom().getY(), ee.getTop().getX(), ee.getTop().getY())) {
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
                if (e.getBottom().getX() == x && e.getBottom().getY() == y) {
                    v1 = e.getBottom();
                } else {
                    v1 = e.getTop();
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
        Double score = 0.0;
        for(Vertex w : vertices) {
            if (w != v) {
                double dist = new Vertex(x, y).calcDistanceTo(w);
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
            if ((e.getBottom().getX() == vertex.getX() && e.getBottom().getY() == vertex.getY()) || (e.getTop().getX() == vertex.getX() && e.getTop().getY() == vertex.getY())) {
                return false;
            }
        }
        return true;
    }

    public double moveCompatible(Vertex V, int iterations, Network network) {
        List<Edge> compatible = new EdgeList(network.getAllEdges()).getCompatible();
        List<Edge> external = network.getExternalEdges();
        external.addAll(network.getTrivialEdges());

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

                Vertex v = e.getBottom();
                Vertex w = e.getTop();

                double bestAngle;

                boolean moved = false;

                List<Edge> trivial = network.getTrivialEdges();
                Iterator<Edge> eIt = trivial.iterator();
                while (eIt.hasNext()) {
                    Edge ed = eIt.next();
                    topEdges.remove(e);
                    bottomEdges.remove(e);
                }


                if (!moved) {
                    bestAngle = angleCalculator.optimizedAngleForCompatible(v, w, e, bottomExternal, topExternal);
                    topEdges.add(e);
                    moved = tryRotating(v, bottomEdges, topEdges, 0 - bestAngle, V.collectVertices());
                    if (moved) {
                        corrected = bestAngle;
                    }
                    topEdges.remove(e);
                    bestAngle = angleCalculator.optimizedAngleForCompatible(w, v, e, topExternal, bottomExternal);
                    bottomEdges.add(e);
                    boolean moved2 = tryRotating(w, topEdges, bottomEdges, 0 - bestAngle, V.collectVertices());
                    if (moved2) {
                        corrected = bestAngle;
                        moved = moved2;
                    }
                    bottomEdges.remove(e);
                }
                if (!moved) {
                    bestAngle = angleCalculator.computeMiddleAngleForTrivial(e, e.getBottom(), e.getTop());
                    moved = tryAngle(v, w, e, tmp, topEdges, bottomEdges, bestAngle, V.collectVertices());
                    if (moved && (corrected == null || corrected < bestAngle)) {
                        corrected = bestAngle;
                    }
                    bestAngle = angleCalculator.computeMiddleAngleForTrivial(e, e.getTop(), e.getBottom());
                    boolean moved2 = tryAngle(w, v, e, tmp, bottomEdges, topEdges, bestAngle, V.collectVertices());
                    if (moved2 && (corrected == null || corrected < bestAngle)) {
                        corrected = bestAngle;
                        moved = moved2;
                    }
                }
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
                vertices.get(i).setVisited(false);
                LinkedList<Edge> vEd = vertices.get(i).getEdgeList();
                for (int k = 0; k < vEd.size(); k++) {
                    vEd.get(k).setVisited(false);
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
                vertices.get(i).setVisited(false);
                LinkedList<Edge> vEd = vertices.get(i).getEdgeList();
                for (int k = 0; k < vEd.size(); k++) {
                    vEd.get(k).setVisited(false);
                }
            }

        }
        return moved;
    }

    private double scoreDirection(Edge e, double x, double y, double x1, double y1, Vertex centrePoint) {
        double length = e.length();
        double sinAlpha = getSinusAlpha(new Vertex(x, y), centrePoint);
        double alpha = Math.asin(sinAlpha);

        double Y;
        double X;

        if (y >= centrePoint.getY()) {
            Y = y + length * sinAlpha;
        } else {
            Y = y - length * sinAlpha;
        }

        double cosAlpha = Math.cos(alpha);

        if (x >= centrePoint.getX()) {
            X = x + length * cosAlpha;
        } else {
            X = x - length * cosAlpha;
        }

        double deviation = angleCalculator.getAngle(new Vertex(X, Y), centrePoint, new Vertex(x1, y1));

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


    private static class Line {

        private double a;
        private double b;

        private Line(Vertex v, Vertex w) {
            setAB(v, w);
        }

        private Line(Edge e) {
            setAB(e.getBottom(), e.getTop());
        }

        private Line(double x1, double y1, double x2, double y2) {
            a = (y1 - y2) / (x1 - x2);
            b = y1 - a * x1;
        }

        private final void setAB(Vertex v, Vertex w) {
            a = (v.getY() - w.getY()) / (v.getX() - w.getX());
            b = v.getY() - a * v.getX();
        }

        @Override
        public boolean equals(Object o) {
            return this.equals((Line)o);
        }

        public boolean equals(Line l2) {
            return this.a == l2.a && this.b == l2.b;
        }

        public Vertex intersection(Line l) {
            double x = (l.b - this.b) / (this.a - l.a);
            double y = this.a * x + this.b;
            return new Vertex(x, y);
        }
    }
}
