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

import uk.ac.uea.cmp.spectre.flatnj.ds.Network;

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
    public double computeOptimalAngle(LinkedList<NetworkBox> boxesSorted, LinkedList<Edge> edges, boolean bottom) {
        if (!boxesSorted.isEmpty()) {
            return computeForIncompatible(boxesSorted, bottom);
        } else if (edges.size() == 1) {
            return computeForCompatible(edges);
        } else {
            return 0.0;
        }
    }

    @Override
    public double computeForCompatible(LinkedList<Edge> edges) {
        Edge split = edges.getFirst();
        double deltaAlpha = 0.0;

        if (edges.size() == 1 && split.length() != 0) {
            //Collect all the external vertices that are below the current split
            Set<Vertex> bottomVertices = getAllVertices(split, false);

            //Collect all the external vertices that are above
            Set<Vertex> topVertices = getAllVertices(split, true);

            Vertex bStrikerLeft = findStrikerOnTheLeft(split.bot, split.top, topVertices);
            Vertex bStrikerRight = findStrikerOnTheRight(split.bot, split.top, topVertices);
            Vertex bDefenderLeft = findDefenderOnTheLeft(split.bot, split.top, bottomVertices);
            Vertex bDefenderRight = findDefenderOnTheRight(split.bot, split.top, bottomVertices);

            //Determine all four safe angles
            double bAngleLeft = getClockwiseAngle(bDefenderLeft, split.bot, bStrikerLeft);
            double bAngleRight = getClockwiseAngle(bStrikerRight, split.bot, bDefenderRight);

            Vertex tStrikerLeft = findStrikerOnTheLeft(split.top, split.bot, bottomVertices);
            Vertex tStrikerRight = findStrikerOnTheRight(split.top, split.bot, bottomVertices);
            Vertex tDefenderLeft = findDefenderOnTheLeft(split.top, split.bot, topVertices);
            Vertex tDefenderRight = findDefenderOnTheRight(split.top, split.bot, topVertices);
            //Determine all four safe angles
            double tAngleLeft = getClockwiseAngle(tDefenderLeft, split.top, tStrikerLeft);
            double tAngleRight = getClockwiseAngle(tStrikerRight, split.top, tDefenderRight);


            //System.out.println("\n" + bAngleLeft + " " + bAngleRight + " | " + tAngleLeft + " " + tAngleRight );

            if (bAngleLeft != Math.PI && bAngleRight != Math.PI) {
                deltaAlpha = computeOptimalCompatible(bAngleLeft, bAngleRight);
            } else if (tAngleLeft != Math.PI && tAngleRight != Math.PI) {
                deltaAlpha = computeOptimalCompatible(tAngleLeft, tAngleRight);
            }
        }

        return deltaAlpha;
    }

    protected double computeForIncompatible(LinkedList<NetworkBox> boxesSorted, boolean bottom) {
        //variables to store limits of the possible angle movements
        //up is the maximum posible change of the angle by which we could
        //increase it,
        //down -- decrease.
        Double alphaUp = null;
        Double alphaDown = null;

        //go through all the boxes
        for (int i = 0; i < boxesSorted.size(); i++) {
            NetworkBox b = boxesSorted.get(i);
            Edge e1 = b.e1;
            Edge e2 = b.e2;

            //evaluate angles of current box
            double currUp = getAngle(e1.bot, e2.bot, e2.top);
            double currDown = getAngle(e1.top, e1.bot, e2.bot);

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

        split.visited = true;
        if (top) {
            vertices.add(split.top);
            verticesR.add(split.top);
        } else {
            vertices.add(split.bot);
            verticesR.add(split.bot);
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
                        verticesR.add(e.top);
                    } else {
                        vertices.add(e.bot);
                        verticesR.add(e.bot);
                    }
                }
            }
        }

        Iterator<Edge> it = edges.iterator();
        while (it.hasNext()) {
            it.next().visited = false;
        }

        split.visited = false;

        return verticesR;
    }

    @Override
    public double getAngle(Vertex v1, Vertex a, Vertex v2) {
        double angle;

        angle = Math.min(getClockwiseAngle(v1, a, v2), getClockwiseAngle(v2, a, v1));

        return angle;
    }

    protected double computeOptimal(LinkedList<NetworkBox> boxesSorted) {
        double deltaAlpha = smallAngle * Math.signum(Math.random() - 0.5);
        return deltaAlpha;
    }

    protected double computeSecondDerivative(LinkedList<NetworkBox> boxesSorted, double deltaAlpha) {
        double secondDerivative = 0;

        for (int i = 0; i < boxesSorted.size(); i++) {
            NetworkBox b = boxesSorted.get(i);
            Edge e1 = b.e1;
            Edge e2 = b.e2;

            double alphaSi = getAngle(e1.top, e1.bot, e2.bot);
            double a = Math.sqrt((e1.top.x - e1.bot.x) * (e1.top.x - e1.bot.x) + (e1.top.y - e1.bot.y) * (e1.top.y - e1.bot.y));
            double c = Math.sqrt((e1.bot.x - e2.bot.x) * (e1.bot.x - e2.bot.x) + (e1.bot.y - e2.bot.y) * (e1.bot.y - e2.bot.y));
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
            double angle = getClockwiseAngle(top, bot, v);
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
            double angle = getClockwiseAngle(v, bot, top);
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
                double angle = getClockwiseAngle(w, w, top);
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
            double angle = getClockwiseAngle(top, bot, v);
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
            double angle = getClockwiseAngle(v, bot, top);
            if (angle <= angleThreshold && (striker == null || maxAngle < angle)) {
                striker = v;
                maxAngle = angle;
            }
        }

        return striker;
    }

    public static double getClockwiseAngle(Vertex v1, Vertex a, Vertex v2) {
        double angle;
        if (v1.x == v2.x && v1.y == v2.y) {
            angle = 0;
        } else if ((v1.x == a.x && v1.y == a.y) || (v2.x == a.x && v2.y == a.y)) {
            angle = Math.PI;
        } else {
            if (distance(v1, a) == 0 || distance(v2, a) == 0) {
                angle = 0;
            } else {
                angle = Math.atan2((v1.y - a.y), (v1.x - a.x)) - Math.atan2((v2.y - a.y), (v2.x - a.x));
                angle = (angle + 2 * Math.PI) % (2 * Math.PI);
            }
        }
        return angle;
    }

    public static double distance(Vertex v1, Vertex v2) {
        return Math.sqrt((v1.x - v2.x) * (v1.x - v2.x) + (v1.y - v2.y) * (v1.y - v2.y));
    }

    @Override
    public double getSafeAngleBot(double deltaAlpha, Edge leftmost, Edge rightmost, Set<Vertex> bottomVertices, Set<Vertex> topVertices) {
        double safeAngle = 0.0;

        //Determine striker and defender points for the bottom leftmost and rightmost ends of the split
        Vertex bStrikerLeft = findStrikerOnTheLeft(leftmost.bot, leftmost.top, topVertices);
        Vertex bStrikerRight = findStrikerOnTheRight(rightmost.bot, rightmost.top, topVertices);
        Vertex bDefenderLeft = findDefenderOnTheLeft(leftmost.bot, leftmost.top, bottomVertices);
        Vertex bDefenderRight = findDefenderOnTheRight(rightmost.bot, rightmost.top, bottomVertices);

        //Determine all four safe angles
        double bAngleLeft = getClockwiseAngle(bDefenderLeft, leftmost.bot, bStrikerLeft);
        double bAngleRight = getClockwiseAngle(bStrikerRight, rightmost.bot, bDefenderRight);

        if (bAngleLeft <= Math.PI && bAngleRight <= Math.PI) {
            safeAngle = Math.min(bAngleLeft, bAngleRight);
        }

        return safeAngle;
    }

    @Override
    public double getSafeAngleTop(double deltaAlpha, Edge leftmost, Edge rightmost, Set<Vertex> bottomVertices, Set<Vertex> topVertices) {
        double safeAngle = 0.0;

        //Determining corresponding points for the top
        Vertex tStrikerLeft = findStrikerOnTheRight(leftmost.top, leftmost.bot, bottomVertices);
        Vertex tStrikerRight = findStrikerOnTheLeft(rightmost.top, rightmost.bot, bottomVertices);
        Vertex tDefenderLeft = findDefenderOnTheRight(leftmost.top, leftmost.bot, topVertices);
        Vertex tDefenderRight = findDefenderOnTheLeft(rightmost.top, rightmost.bot, topVertices);

        //Determine all four safe angles
        double tAngleLeft = getClockwiseAngle(tStrikerLeft, leftmost.top, tDefenderLeft);
        double tAngleRight = getClockwiseAngle(tDefenderRight, rightmost.top, tStrikerRight);

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
        for (int i = 0; i < v.elist.size(); i++) {
            if (v.elist.get(i) != split) {
                around.add(v.elist.get(i));
            }
        }
        Edge left = null;
        Edge right = null;
        double angleLeft = 0.0;
        double angleRight = 0.0;
        for (int i = 0; i < around.size(); i++) {
            Edge e = around.get(i);
            Vertex ev = (e.bot == v) ? e.top : e.bot;
            double edgeSplitAngle = AngleCalculatorSimple.getClockwiseAngle(ev, v, w);
            double splitEdgeAngle = AngleCalculatorSimple.getClockwiseAngle(w, v, ev);

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

    /**
     * @param v
     * @param w
     * @param e
     * @return
     */
    @Override
    public double optimizedAngleForCompatible(Vertex v, Vertex w, Edge e, List<Edge> botEdges, List<Edge> topEdges) {
        Set<Vertex> botVertices = new HashSet<>();
        for (int i = 0; i < botEdges.size(); i++) {
            Edge ee = botEdges.get(i);
            botVertices.add(ee.bot);
            botVertices.add(ee.top);
        }

        Set<Vertex> topVertices = new HashSet<>();
        for (int i = 0; i < topEdges.size(); i++) {
            Edge ee = topEdges.get(i);
            topVertices.add(ee.bot);
            topVertices.add(ee.top);
        }

        topVertices.add(w);

        Vertex dR = findDefenderOnTheRight(v, w, botVertices);
        Vertex sR = findStrikerOnTheRight(v, w, topVertices);

        Vertex dL = findDefenderOnTheLeft(v, w, botVertices);
        Vertex sL = findStrikerOnTheLeft(v, w, topVertices);


        double aR = getClockwiseAngle(sR, v, dR);
        double aL = getClockwiseAngle(dL, v, sL);

        double A = (aL - aR) / 2;

        return A;
    }

    @Override
    public double[] optimizedAngleForCompatible2(Vertex v, Vertex w, Edge e,
                                                 List<Edge> edges,
                                                 CompatibleCorrector cc,
                                                 Network network,
                                                 Window window,
                                                 boolean outside) {
        List<Double> angles = new LinkedList<>();

        double r = e.length();

        double vx = v.x;
        double vy = v.y;

        Set<double[]> intersections = new HashSet<>();

        Iterator<Edge> edgeIt = edges.iterator();
        while (edgeIt.hasNext()) {
            Edge ee = edgeIt.next();

            if (ee != e)

            //double dist = Collector.getDistanceToEgde(v, ee);
            //if(dist <= r)
            {
                double xb = ee.bot.x;
                double yb = ee.bot.y;
                double xt = ee.top.x;
                double yt = ee.top.y;

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

        List<Vertex> vertices = network.getVertices();
        for (int i = 0; i < vertices.size(); i++) {
            Vertex vertex = vertices.get(i);
            if (vertex != v && vertex != w &&
                    ((!cc.pointInsideNetwork(vertex,
                            network.getExternal())
                            && outside
                            && distance(vertex, v) <= r && distance(vertex, v) >= 0.25 * r) ||
                            (cc.pointInsideNetwork(vertex,
                                    network.getExternal())
                                    && !outside
                                    && distance(vertex, v) <= 1.05 * r && distance(vertex, v) > 0.95 * r))) {
                double[] p = new double[2];
                p[0] = vertex.x;
                p[1] = vertex.y;
                intersections.add(p);
            }
        }

        List<double[]> sortedIntersections = sortPoints(intersections, v, w);


        for (int i = 0; i < sortedIntersections.size(); i++) {
            double[] c1 = sortedIntersections.get(i);
            double x1 = c1[0] - v.x;
            double y1 = c1[1] - v.y;
            //window.markPoint(new Vertex(c1[0], c1[1]), 1);
            double[] c2 = (i == sortedIntersections.size() - 1)
                    ? sortedIntersections.get(0)
                    : sortedIntersections.get(i + 1);
            double angle = getClockwiseAngle(new Vertex(c2[0], c2[1]),
                    v,
                    new Vertex(c1[0], c1[1]));
            double middle = angle / 2.0;
            if (middle > 0.05) {
                double x = x1 * Math.cos(middle) - y1 * Math.sin(middle) + v.x;
                double y = x1 * Math.sin(middle) + y1 * Math.cos(middle) + v.y;

                Vertex c0 = new Vertex(x, y);

                if ((!cc.pointInsideNetwork(c0, network.getExternal()) && outside) ||
                        (cc.pointInsideNetwork(c0, network.getExternal()) && !outside)) {
                    angles.add(getClockwiseAngle(c0, v, w));
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
            angles[index++] = getClockwiseAngle(w, v, new Vertex(p[0], p[1]));
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
