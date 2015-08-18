/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2015  UEA School of Computing Sciences
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
import uk.ac.uea.cmp.spectre.core.ds.network.EdgeList;
import uk.ac.uea.cmp.spectre.core.ds.network.Network;
import uk.ac.uea.cmp.spectre.core.ds.network.Vertex;

import java.util.*;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */

/**
 * @author balvociute
 */
public class AngleCalculatorSimple implements AngleCalculator {

    private double angleThreshold = 1 * Math.PI;
    private double smallAngle = 0.2;

    @Override
    public double computeOptimalAngle(List<NetworkBox> boxesSorted, EdgeList edges, boolean bottom) {
        if (!boxesSorted.isEmpty()) {
            return computeForIncompatible(boxesSorted, bottom);
        } else if (edges.size() == 1) {
            return computeForCompatible(edges);
        } else {
            return 0.0;
        }
    }

    @Override
    public double computeForCompatible(EdgeList edges) {
        Edge split = edges.getFirst();
        double deltaAlpha = 0.0;

        if (edges.size() == 1 && split.length() != 0) {
            //Collect all the external vertices that are below the current split
            Set<Vertex> bottomVertices = getAllVertices(split, false);

            //Collect all the external vertices that are above
            Set<Vertex> topVertices = getAllVertices(split, true);

            Vertex bottom = split.getBottom();
            Vertex top = split.getTop();

            Vertex bStrikerLeft = findStrikerOnTheLeft(bottom, top, topVertices);
            Vertex bStrikerRight = findStrikerOnTheRight(bottom, top, topVertices);
            Vertex bDefenderLeft = findDefenderOnTheLeft(bottom, top, bottomVertices);
            Vertex bDefenderRight = findDefenderOnTheRight(bottom, top, bottomVertices);

            //Determine all four safe angles
            double bAngleLeft = Vertex.getClockwiseAngle(bDefenderLeft, bottom, bStrikerLeft);
            double bAngleRight = Vertex.getClockwiseAngle(bStrikerRight, bottom, bDefenderRight);

            Vertex tStrikerLeft = findStrikerOnTheLeft(top, bottom, bottomVertices);
            Vertex tStrikerRight = findStrikerOnTheRight(top, bottom, bottomVertices);
            Vertex tDefenderLeft = findDefenderOnTheLeft(top, bottom, topVertices);
            Vertex tDefenderRight = findDefenderOnTheRight(top, bottom, topVertices);
            //Determine all four safe angles
            double tAngleLeft = Vertex.getClockwiseAngle(tDefenderLeft, top, tStrikerLeft);
            double tAngleRight = Vertex.getClockwiseAngle(tStrikerRight, top, tDefenderRight);


            //System.out.println("\n" + bAngleLeft + " " + bAngleRight + " | " + tAngleLeft + " " + tAngleRight );

            if (bAngleLeft != Math.PI && bAngleRight != Math.PI) {
                deltaAlpha = computeOptimalCompatible(bAngleLeft, bAngleRight);
            } else if (tAngleLeft != Math.PI && tAngleRight != Math.PI) {
                deltaAlpha = computeOptimalCompatible(tAngleLeft, tAngleRight);
            }
        }

        return deltaAlpha;
    }

    protected double computeForIncompatible(List<NetworkBox> boxesSorted, boolean bottom) {
        //variables to store limits of the possible angle movements
        //up is the maximum posible change of the angle by which we could
        //increase it,
        //down -- decrease.
        Double alphaUp = null;
        Double alphaDown = null;

        //go through all the boxes
        for (int i = 0; i < boxesSorted.size(); i++) {
            NetworkBox b = boxesSorted.get(i);
            Edge e1 = b.getE1();
            Edge e2 = b.getE2();

            //evaluate angles of current box
            double currUp = getAngle(e1.getBottom(), e2.getBottom(), e2.getTop());
            double currDown = getAngle(e1.getTop(), e1.getBottom(), e2.getBottom());

            //if new angles are smaller, then update limits
            alphaUp = (alphaUp == null || alphaUp > currUp) ? currUp : alphaUp;
            alphaDown = (alphaDown == null || alphaDown > currDown) ? currDown : alphaDown;
        }

        double deltaAlpha = computeOptimal(boxesSorted);

        //System.err.println((0 - alphaDown) + "\t" + deltaAlpha + "\t" + alphaUp);

        if (computeSecondDerivative(boxesSorted, deltaAlpha) > 0) {
            deltaAlpha = 0;
        }

        if (deltaAlpha > alphaUp && deltaAlpha < 0 - alphaDown) {
            deltaAlpha = 0;
        }

        return deltaAlpha;
    }

    protected double computeOptimalCompatible(double bAngleLeft, double bAngleRight) {
        double middle = (bAngleLeft + bAngleRight) / 2;
        double direction = middle - bAngleRight;
        return Math.signum(direction) * smallAngle * -1;
    }

    protected Set<Vertex> getAllVertices(Edge split, boolean top) {
        LinkedList<Vertex> vertices = new LinkedList<>();
        Set<Edge> edges = new HashSet<>();
        Set<Vertex> verticesR = new HashSet<>();

        split.setVisited(true);
        if (top) {
            vertices.add(split.getTop());
            verticesR.add(split.getTop());
        } else {
            vertices.add(split.getBottom());
            verticesR.add(split.getBottom());
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
                        verticesR.add(e.getTop());
                    } else {
                        vertices.add(e.getBottom());
                        verticesR.add(e.getBottom());
                    }
                }
            }
        }

        Iterator<Edge> it = edges.iterator();
        while (it.hasNext()) {
            it.next().setVisited(false);
        }

        split.setVisited(false);

        return verticesR;
    }

    @Override
    public double getAngle(Vertex v1, Vertex a, Vertex v2) {
        double angle;

        angle = Math.min(Vertex.getClockwiseAngle(v1, a, v2), Vertex.getClockwiseAngle(v2, a, v1));

        return angle;
    }

    protected double computeOptimal(List<NetworkBox> boxesSorted) {
        double deltaAlpha = smallAngle * Math.signum(Math.random() - 0.5);
        return deltaAlpha;
    }

    protected double computeSecondDerivative(List<NetworkBox> boxesSorted, double deltaAlpha) {
        double secondDerivative = 0;

        for (int i = 0; i < boxesSorted.size(); i++) {
            NetworkBox b = boxesSorted.get(i);

            // Messy shortcuts
            Edge e1 = b.getE1();
            Edge e2 = b.getE2();

            Vertex e1Bot = e1.getBottom();
            Vertex e1Top = e1.getTop();
            double e1BotX = e1Bot.getX();
            double e1BotY = e1Bot.getY();
            double e1TopX = e1Top.getX();
            double e1TopY = e1Top.getY();

            Vertex e2Bot = e2.getBottom();
            double e2BotX = e2Bot.getX();
            double e2BotY = e2Bot.getY();

            double alphaSi = getAngle(e1Top, e1Bot, e2Bot);
            double a = Math.sqrt((e1TopX - e1BotX) * (e1TopX - e1BotX) + (e1TopY - e1BotY) * (e1TopY - e1BotY));
            double c = Math.sqrt((e1BotX - e2BotX) * (e1BotX - e2BotX) + (e1BotY - e2BotY) * (e1BotY - e2BotY));
            secondDerivative -= a * c * Math.sin(alphaSi + deltaAlpha);

        }
        return secondDerivative;
    }

    public Vertex findDefenderOnTheRight(Vertex bot, Vertex top, Set<Vertex> bottomVertices) {
        Vertex defender = null;
        double minAngle = 0;
        Iterator<Vertex> vertices = bottomVertices.iterator();
        while (vertices.hasNext()) {
            Vertex v = vertices.next();
            double angle = Vertex.getClockwiseAngle(top, bot, v);
            if (angle <= angleThreshold && (defender == null || minAngle > angle)) {
                defender = v;
                minAngle = angle;
            }
        }
        return defender;
    }

    public Vertex findDefenderOnTheLeft(Vertex bot, Vertex top, Set<Vertex> bottomVertices) {
        Vertex defender = null;

        double minAngle = 0;
        Iterator<Vertex> vertices = bottomVertices.iterator();
        while (vertices.hasNext()) {
            Vertex v = vertices.next();
            double angle = Vertex.getClockwiseAngle(v, bot, top);
            if (angle <= angleThreshold && (defender == null || minAngle > angle)) {
                defender = v;
                minAngle = angle;
            }
        }
        return defender;
    }

    public Vertex findDefender(Vertex v, Vertex top, Set<Vertex> bottomVertices) {
        Vertex defender = null;


        double minAngle = 0;
        Iterator<Vertex> vertices = bottomVertices.iterator();
        while (vertices.hasNext()) {
            Vertex w = vertices.next();
            if (w != top) {
                double angle = Vertex.getClockwiseAngle(w, w, top);
                if (angle <= angleThreshold && (defender == null || minAngle > angle)) {
                    defender = w;
                    minAngle = angle;
                }
            }
        }
        return defender;
    }

    public Vertex findStrikerOnTheRight(Vertex bot, Vertex top, Set<Vertex> topVertices) {
        Vertex striker = null;

        double maxAngle = 0;
        Iterator<Vertex> vertices = topVertices.iterator();
        while (vertices.hasNext()) {
            Vertex v = vertices.next();
            double angle = Vertex.getClockwiseAngle(top, bot, v);
            if (angle <= angleThreshold && (striker == null || maxAngle < angle)) {
                striker = v;
                maxAngle = angle;
            }
        }
        return striker;
    }

    public Vertex findStrikerOnTheLeft(Vertex bot, Vertex top, Set<Vertex> topVertices) {
        Vertex striker = null;

        double maxAngle = 0;
        Iterator<Vertex> vertices = topVertices.iterator();
        while (vertices.hasNext()) {
            Vertex v = vertices.next();
            double angle = Vertex.getClockwiseAngle(v, bot, top);
            if (angle <= angleThreshold && (striker == null || maxAngle < angle)) {
                striker = v;
                maxAngle = angle;
            }
        }

        return striker;
    }


    @Override
    public double getSafeAngleBot(double deltaAlpha, Edge leftmost, Edge rightmost, Set<Vertex> bottomVertices, Set<Vertex> topVertices) {
        double safeAngle = 0.0;

        //Determine striker and defender points for the bottom leftmost and rightmost ends of the split
        Vertex bStrikerLeft = findStrikerOnTheLeft(leftmost.getBottom(), leftmost.getTop(), topVertices);
        Vertex bStrikerRight = findStrikerOnTheRight(rightmost.getBottom(), rightmost.getTop(), topVertices);
        Vertex bDefenderLeft = findDefenderOnTheLeft(leftmost.getBottom(), leftmost.getTop(), bottomVertices);
        Vertex bDefenderRight = findDefenderOnTheRight(rightmost.getBottom(), rightmost.getTop(), bottomVertices);

        //Determine all four safe angles
        double bAngleLeft = Vertex.getClockwiseAngle(bDefenderLeft, leftmost.getBottom(), bStrikerLeft);
        double bAngleRight = Vertex.getClockwiseAngle(bStrikerRight, rightmost.getBottom(), bDefenderRight);

        if (bAngleLeft <= Math.PI && bAngleRight <= Math.PI) {
            safeAngle = Math.min(bAngleLeft, bAngleRight);
        }

        return safeAngle;
    }

    @Override
    public double getSafeAngleTop(double deltaAlpha, Edge leftmost, Edge rightmost, Set<Vertex> bottomVertices, Set<Vertex> topVertices) {
        double safeAngle = 0.0;

        //Determining corresponding points for the top
        Vertex tStrikerLeft = findStrikerOnTheRight(leftmost.getTop(), leftmost.getBottom(), bottomVertices);
        Vertex tStrikerRight = findStrikerOnTheLeft(rightmost.getTop(), rightmost.getBottom(), bottomVertices);
        Vertex tDefenderLeft = findDefenderOnTheRight(leftmost.getTop(), leftmost.getBottom(), topVertices);
        Vertex tDefenderRight = findDefenderOnTheLeft(rightmost.getTop(), rightmost.getBottom(), topVertices);

        //Determine all four safe angles
        double tAngleLeft = Vertex.getClockwiseAngle(tStrikerLeft, leftmost.getTop(), tDefenderLeft);
        double tAngleRight = Vertex.getClockwiseAngle(tDefenderRight, rightmost.getTop(), tStrikerRight);

        if (tAngleRight <= Math.PI && tAngleLeft <= Math.PI) {
            safeAngle = Math.min(tAngleRight, tAngleLeft);
        }

        return safeAngle;
    }

    @Override
    public double computeMiddleAngleForTrivial(Edge split, Vertex bot, Vertex top) {
        double[] lrAngles = computeLeftAndRightAngles(split, bot, top);

        double deltaAlpha = (lrAngles[0] + lrAngles[1]) / 2.0 - lrAngles[1];

        return deltaAlpha;
    }

    @Override
    public double[] computeLeftAndRightAngles(Edge split, Vertex v, Vertex w) {
        double[] lr = new double[2];
        LinkedList<Edge> around = new LinkedList<>();
        for (int i = 0; i < v.getEdgeList().size(); i++) {
            if (v.getEdgeList().get(i) != split) {
                around.add(v.getEdgeList().get(i));
            }
        }
        Edge left = null;
        Edge right = null;
        double angleLeft = 0.0;
        double angleRight = 0.0;
        for (int i = 0; i < around.size(); i++) {
            Edge e = around.get(i);
            Vertex ev = (e.getBottom() == v) ? e.getTop() : e.getBottom();
            double edgeSplitAngle = Vertex.getClockwiseAngle(ev, v, w);
            double splitEdgeAngle = Vertex.getClockwiseAngle(w, v, ev);

            if (left == null || angleLeft > edgeSplitAngle) {
                left = e;
                angleLeft = edgeSplitAngle;
            }
            if (right == null || angleRight > splitEdgeAngle) {
                right = e;
                angleRight = splitEdgeAngle;
            }
        }
        lr[0] = angleLeft;
        lr[1] = angleRight;
        return lr;
    }

    @Override
    public double optimizedAngleForCompatible(Vertex v, Vertex w, Edge e, List<Edge> botEdges, List<Edge> topEdges) {
        Set<Vertex> botVertices = new HashSet<>();
        for (int i = 0; i < botEdges.size(); i++) {
            Edge ee = botEdges.get(i);
            botVertices.add(ee.getBottom());
            botVertices.add(ee.getTop());
        }

        Set<Vertex> topVertices = new HashSet<>();
        for (int i = 0; i < topEdges.size(); i++) {
            Edge ee = topEdges.get(i);
            topVertices.add(ee.getBottom());
            topVertices.add(ee.getTop());
        }

        topVertices.add(w);

        Vertex dR = findDefenderOnTheRight(v, w, botVertices);
        Vertex sR = findStrikerOnTheRight(v, w, topVertices);

        Vertex dL = findDefenderOnTheLeft(v, w, botVertices);
        Vertex sL = findStrikerOnTheLeft(v, w, topVertices);


        double aR = Vertex.getClockwiseAngle(sR, v, dR);
        double aL = Vertex.getClockwiseAngle(dL, v, sL);

        double A = (aL - aR) / 2;

        return A;
    }

    @Override
    public double[] optimizedAngleForCompatible2(Vertex v, Vertex w, Edge e,
                                                 List<Edge> edges,
                                                 CompatibleCorrector cc,
                                                 Network network,
                                                 boolean outside) {
        List<Double> angles = new LinkedList<>();

        double r = e.length();

        double vx = v.getX();
        double vy = v.getY();

        Set<double[]> intersections = new HashSet<>();

        Iterator<Edge> edgeIt = edges.iterator();
        while (edgeIt.hasNext()) {
            Edge ee = edgeIt.next();

            if (ee != e)

            //double dist = Collector.getDistanceToEgde(v, ee);
            //if(dist <= r)
            {
                double xb = ee.getBottom().getX();
                double yb = ee.getBottom().getY();
                double xt = ee.getTop().getX();
                double yt = ee.getTop().getY();

                double a = Translocator.a(xb, yb, xt, yt);
                double b = yb - a * xb;

                double A = 1 + a * a;
                double B = -2 * vx + 2 * a * b - 2 * a * vy;
                double C = vx * vx + b * b - 2 * b * vy + vy * vy - r * r;

                double D = B * B - 4 * A * C;

                if (D >= 0) {
                    double x1 = (-B - Math.sqrt(D)) / (2 * A);
                    double y1 = a * x1 + b;
                    intersectionPoint(xb, yb, xt, yt, x1, y1, intersections);

                    double x2 = (-B + Math.sqrt(D)) / (2 * A);
                    double y2 = a * x2 + b;
                    intersectionPoint(xb, yb, xt, yt, x2, y2, intersections);
                }
            }
        }

        List<Vertex> vertices = network.getAllVertices();
        for (int i = 0; i < vertices.size(); i++) {
            Vertex vertex = vertices.get(i);
            double dist = vertex.calcDistanceTo(v);
            if (vertex != v && vertex != w &&
                    ((!cc.pointInsideNetwork(vertex,
                            network.getExternalEdges())
                            && outside
                            && dist <= r && dist >= 0.25 * r) ||
                            (cc.pointInsideNetwork(vertex,
                                    network.getExternalEdges())
                                    && !outside
                                    && dist <= 1.05 * r && dist > 0.95 * r))) {
                double[] p = new double[2];
                p[0] = vertex.getX();
                p[1] = vertex.getY();
                intersections.add(p);
            }
        }

        List<double[]> sortedIntersections = sortPoints(intersections, v, w);


        for (int i = 0; i < sortedIntersections.size(); i++) {
            double[] c1 = sortedIntersections.get(i);
            double x1 = c1[0] - v.getX();
            double y1 = c1[1] - v.getY();
            //window.markPoint(new Vertex(c1[0], c1[1]), 1);
            double[] c2 = (i == sortedIntersections.size() - 1)
                    ? sortedIntersections.get(0)
                    : sortedIntersections.get(i + 1);
            double angle = Vertex.getClockwiseAngle(new Vertex(c2[0], c2[1]),
                    v,
                    new Vertex(c1[0], c1[1]));
            double middle = angle / 2.0;
            if (middle > 0.05) {
                double x = x1 * Math.cos(middle) - y1 * Math.sin(middle) + v.getX();
                double y = x1 * Math.sin(middle) + y1 * Math.cos(middle) + v.getY();

                Vertex c0 = new Vertex(x, y);

                if ((!cc.pointInsideNetwork(c0, network.getExternalEdges()) && outside) ||
                        (cc.pointInsideNetwork(c0, network.getExternalEdges()) && !outside)) {
                    angles.add(Vertex.getClockwiseAngle(c0, v, w));
                    //window.markPoint(c0, 2);
                }
            }
        }

        double[] anglesArray = new double[angles.size()];
        for (int i = 0; i < anglesArray.length; i++) {
            anglesArray[i] = angles.get(i);
        }

        return anglesArray;
    }

    private void intersectionPoint(double xb, double yb, double xt, double yt, double x, double y, Set<double[]> intersections) {
        if (Translocator.pointInTheSection(xb, yb, xt, yt, x, y)) {
            double[] point = new double[2];
            point[0] = x;
            point[1] = y;
            intersections.add(point);
        }
    }

    public List<double[]> sortPoints(Set<double[]> intersections, Vertex v, Vertex w) {
        LinkedList<double[]> sorted = new LinkedList<>();
        LinkedList<Integer> sIndex = new LinkedList<>();

        double[] angles = new double[intersections.size()];
        double[][] points = new double[intersections.size()][2];
        Iterator<double[]> intIt = intersections.iterator();
        int index = 0;
        while (intIt.hasNext()) {
            double[] p = intIt.next();
            points[index] = p;
            angles[index++] = Vertex.getClockwiseAngle(w, v, new Vertex(p[0], p[1]));
        }
        for (int i = 0; i < points.length; i++) {
            if (i == 0) {
                sIndex.add(i);
                sorted.add(points[i]);
            } else {
                for (int j = 0; j < sIndex.size(); j++) {
                    if (angles[i] >= angles[sIndex.get(j)]) {
                        sIndex.add(j, i);
                        sorted.add(j, points[i]);
                        break;
                    } else if (j == sIndex.size() - 1) {
                        sIndex.add(i);
                        sorted.add(points[i]);
                        break;
                    }

                }
            }
        }
        return sorted;
    }


}
