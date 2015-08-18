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
import uk.ac.uea.cmp.spectre.core.ds.network.Vertex;

import java.util.*;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */

/**
 * @author balvociute
 */
public class Translocator {

    public static boolean noCollisions(LinkedList<Edge> split, Set<Edge> topEdges, Set<Edge> bottomEdges, double deltaAlpha) {
        double[] xyDelta = getDifference(split.getFirst(), deltaAlpha);

        double xDelta = xyDelta[0];
        double yDelta = xyDelta[1];

        for (int i = 0; i < split.size(); i++) {
            Edge e = split.get(i);
            if (twoLinesCrosses(e.getBottom().getX(), e.getBottom().getY(), e.getTop().getX() - xDelta, e.getTop().getY() - yDelta, bottomEdges)) {
                return false;
            }
            if (twoLinesCrosses(e.getTop().getX(), e.getTop().getY(), e.getTop().getX() - xDelta, e.getTop().getY() - yDelta, bottomEdges)) {
                return false;
            }
        }

        for (int i = 0; i < split.size(); i++) {
            Edge e = split.get(i);
            if (twoLinesCrosses(e.getBottom().getX(), e.getBottom().getY(), e.getTop().getX() - xDelta, e.getTop().getY() - yDelta, topEdges, xDelta, yDelta)) {
                return false;
            }
        }

        Iterator<Edge> topEdgeIterator = topEdges.iterator();
        while (topEdgeIterator.hasNext()) {
            Edge e = topEdgeIterator.next();
            if (twoLinesCrosses(e.getBottom().getX() - xDelta, e.getBottom().getY() - yDelta, e.getTop().getX() - xDelta, e.getTop().getY() - yDelta, bottomEdges)) {
                return false;
            }
            if (twoLinesCrosses(e.getBottom().getX(), e.getBottom().getY(), e.getBottom().getX() - xDelta, e.getBottom().getY() - yDelta, bottomEdges)) {
                return false;
            }
            if (twoLinesCrosses(e.getTop().getX(), e.getTop().getY(), e.getTop().getX() - xDelta, e.getTop().getY() - yDelta, bottomEdges)) {
                return false;
            }
        }
        return true;
    }

    public static boolean noCollisions(Vertex v, Vertex w, Edge e, LinkedList<Edge> split, Set<Edge> topEdges, Set<Edge> bottomEdges, double angle) {
        double[] xyDelta = getDifference(angle, v, w);

        double xDelta = xyDelta[0];
        double yDelta = xyDelta[1];

        boolean bottom = (e.getBottom() == v) ? true : false;

        for (int i = 0; i < split.size(); i++) {
            Edge ee = split.get(i);

            double x1;
            double x2;
            double y1;
            double y2;

            if (bottom) {
                x1 = ee.getBottom().getX();
                y1 = ee.getBottom().getY();
                x2 = ee.getTop().getX();
                y2 = ee.getTop().getY();
            } else {
                x1 = ee.getTop().getX();
                y1 = ee.getTop().getY();
                x2 = ee.getBottom().getX();
                y2 = ee.getBottom().getY();
            }

            if (twoLinesCrosses(x1, y1, x2 - xDelta, y2 - yDelta, bottomEdges)) {
                return false;
            }
            if (twoLinesCrosses(x1, y1, x2 - xDelta, y2 - yDelta, bottomEdges)) {
                return false;
            }
            if (twoLinesCrosses(x1, y1, x2 - xDelta, y2 - yDelta, topEdges, xDelta, yDelta)) {
                return false;
            }
        }

        Iterator<Edge> topEdgeIterator = topEdges.iterator();
        while (topEdgeIterator.hasNext()) {
            Edge ee = topEdgeIterator.next();
            if (twoLinesCrosses(ee.getBottom().getX() - xDelta, ee.getBottom().getY() - yDelta, ee.getTop().getX() - xDelta, ee.getTop().getY() - yDelta, bottomEdges)) {
                return false;
            }
            if (twoLinesCrosses(ee.getBottom().getX(), ee.getBottom().getY(), ee.getBottom().getX() - xDelta, ee.getBottom().getY() - yDelta, bottomEdges)) {
                return false;
            }
            if (twoLinesCrosses(ee.getTop().getX(), ee.getTop().getY(), ee.getTop().getX() - xDelta, ee.getTop().getY() - yDelta, bottomEdges)) {
                return false;
            }
        }
        return true;
    }

    private static double[] getDifference(Edge e, double alpha) {
        double[] xyDelta = new double[2];

        Vertex top = e.getTop();
        Vertex bot = e.getBottom();

        double xt = top.getX() - bot.getX();
        double yt = top.getY() - bot.getY();

        double x = xt * Math.cos(alpha) - yt * Math.sin(alpha) + bot.getX();
        double y = xt * Math.sin(alpha) + yt * Math.cos(alpha) + bot.getY();

        xyDelta[0] = top.getX() - x;
        xyDelta[1] = top.getY() - y;

        return xyDelta;
    }

    public static boolean twoLinesCrosses(double xb, double yb, double xt, double yt, Set<Edge> edges) {
        Iterator<Edge> iterator = edges.iterator();
        while (iterator.hasNext()) {
            Edge e = iterator.next();
            double XB = e.getBottom().getX();
            double YB = e.getBottom().getY();
            double XT = e.getTop().getX();
            double YT = e.getTop().getY();

            if (cross(xb, yb, xt, yt, XB, YB, XT, YT)) {
                return true;
            }
        }
        return false;
    }

    public static boolean twoLinesCrosses(double xb, double yb, double xt, double yt, Set<Edge> edges, double xDelta, double yDelta) {
        Iterator<Edge> iterator = edges.iterator();
        while (iterator.hasNext()) {
            Edge e = iterator.next();
            double XB = e.getBottom().getX() - xDelta;
            double YB = e.getBottom().getY() - yDelta;
            double XT = e.getTop().getX() - xDelta;
            double YT = e.getTop().getY() - yDelta;

            if (cross(xb, yb, xt, yt, XB, YB, XT, YT)) {
                return true;
            }
        }
        return false;
    }

    public static boolean cross(double xb, double yb, double xt, double yt, double XB, double YB, double XT, double YT) {
        if (xb == XB && yb == YB || xb == XT && yb == YT || xt == XB && yt == YB || xt == YT && yt == YT) {
            return false;
        }

        double a = a(xb, yb, xt, yt);
        double b = yb - xb * a;

        double A = a(XB, YB, XT, YT);
        double B = YB - XB * A;

        double x = (B - b) / (a - A);
        double y = a * x + b;

        if (xb == xt && yb == yt) {
            x = xt;
            y = yt;
        } else if (XB == XT && YB == YT) {
            x = XB;
            y = YB;
        }


        if (pointInTheSection(xb, yb, xt, yt, x, y) && pointInTheSection(XB, YB, XT, YT, x, y)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean pointInTheSection(double xb, double yb, double xt, double yt, double x, double y) {
        if (((x - xb) * (x - xt) < 0 || xb == xt) && (((y - yb) * (y - yt) < 0) || (yb == yt))) {
            return true;
        }
        return false;
    }

    public static void changeCoordinates(LinkedList<Edge> edges, double alpha) {
        alpha = (alpha < 0) ? 2 * Math.PI + alpha : alpha;

        double deltaX = edges.getFirst().getTop().getX();
        double deltaY = edges.getFirst().getTop().getY();

        rotate(edges.getFirst(), alpha);

        deltaX -= edges.getFirst().getTop().getX();
        deltaY -= edges.getFirst().getTop().getY();

        LinkedList<Vertex> vertices = new LinkedList<>();
        for (int i = 0; i < edges.size(); i++) {
            vertices.add(edges.get(i).getTop());
            edges.get(i).setVisited(true);
        }

        translocate(vertices, deltaX, deltaY);
    }

    private static void rotate(Edge e, double alpha) {
        Vertex top = e.getTop();
        Vertex bot = e.getBottom();

        double xt = top.getX() - bot.getX();
        double yt = top.getY() - bot.getY();

        double x = xt * Math.cos(alpha) - yt * Math.sin(alpha) + bot.getX();
        double y = xt * Math.sin(alpha) + yt * Math.cos(alpha) + bot.getY();

        top.setX(x);
        top.setY(y);
        e.setVisited(true);
        top.setVisited(true);
    }

    private static void translocate(LinkedList<Vertex> vertices, double deltaX, double deltaY) {
        while (!vertices.isEmpty()) {
            Vertex v = vertices.removeFirst();
            if (!v.isVisited()) {
                //System.out.print(v.nxnum + " ");
                move(v, deltaX, deltaY);
            }

            LinkedList<Edge> edges = v.getEdgeList();
            for (int i = 0; i < edges.size(); i++) {
                Edge e = edges.get(i);
                if (!e.isVisited()) {
                    //System.out.print(e.nxnum + " ");
                    e.setVisited(true);
                    if (!e.getTop().isVisited()) {
                        vertices.add(e.getTop());
                    }
                    if (!e.getBottom().isVisited()) {
                        vertices.add(e.getBottom());
                    }
                }
            }
        }
        //System.out.println();
    }

    private static void move(Vertex v, double deltaX, double deltaY) {
        v.setX(v.getX() - deltaX);
        v.setY(v.getY() - deltaY);
        v.setVisited(true);
    }

    public static boolean isBottomPartFreeFromTopVertices(Edge leftmost, Edge rightmost, Set<Edge> edges) {
        double lxb = leftmost.getBottom().getX();
        double lyb = leftmost.getBottom().getY();
        double lxt = leftmost.getTop().getX();
        double lyt = leftmost.getTop().getY();
        double la = (lyt - lyb) / (lxt - lxb);

        double rxb = rightmost.getBottom().getX();
        double ryb = rightmost.getBottom().getY();
        double rxt = rightmost.getTop().getX();
        double ryt = rightmost.getTop().getY();
        double ra = (ryt - ryb) / (rxt - rxb);

        Iterator<Edge> edgeIterator = edges.iterator();
        while (edgeIterator.hasNext()) {
            Edge e = edgeIterator.next();
            if (crosses2(lxb, lyb, lxt, lyt, la, e)) {
                return false;
            }
            if (crosses2(rxb, ryb, rxt, ryt, ra, e)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isUpperPartFreeFromBottomVertices(Edge leftmost, Edge rightmost, Set<Edge> edges) {
        double lxb = leftmost.getBottom().getX();
        double lyb = leftmost.getBottom().getY();
        double lxt = leftmost.getTop().getX();
        double lyt = leftmost.getTop().getY();
        double la = (lyt - lyb) / (lxt - lxb);

        double rxb = rightmost.getBottom().getX();
        double ryb = rightmost.getBottom().getY();
        double rxt = rightmost.getTop().getX();
        double ryt = rightmost.getTop().getY();
        double ra = (ryt - ryb) / (rxt - rxb);

        for (Edge e : edges) {
            if (crosses(lxb, lyb, lxt, lyt, la, e)) {
                return false;
            }
            if (crosses(rxb, ryb, rxt, ryt, ra, e)) {
                return false;
            }
        }
        return true;
    }

    private static boolean crosses(double xb2, double yb2, double xt2, double yt2, double a2, Edge e) {
        double xb = e.getBottom().getX();
        double yb = e.getBottom().getY();
        double xt = e.getTop().getX();
        double yt = e.getTop().getY();
        double a = a(xb, yb, xt, yt);

        double x = (yb2 - xb2 * a2 - yb + xb * a) / (a - a2);
        double y = yb + (x - xb) * a;

        if (x > xb && x < xt && y > yb && y < yt && y > yb2) {
            return true;
        }
        return false;
    }

    private static boolean crosses2(double xb2, double yb2, double xt2, double yt2, double a2, Edge e) {
        double xb = e.getBottom().getX();
        double yb = e.getBottom().getY();
        double xt = e.getTop().getX();
        double yt = e.getTop().getY();
        double a = a(xb, yb, xt, yt);

        double x = (yb2 - xb2 * a2 - yb + xb * a) / (a - a2);
        double y = yb + (x - xb) * a;

        if (x > xb && x < xt && y > yb && y < yt && y < yt2) {
            return true;
        }
        return false;
    }

    public static boolean collides(Edge e, boolean top, boolean bottom, double deltaX, double deltaY, Collection<Edge> edges, boolean top2, boolean bottom2) {
        double xb = (bottom) ? e.getBottom().getX() - deltaX : e.getBottom().getX();
        double yb = (bottom) ? e.getBottom().getY() - deltaY : e.getTop().getY();
        double xt = (top) ? e.getTop().getX() - deltaX : e.getTop().getX();
        double yt = (top) ? e.getTop().getY() - deltaY : e.getTop().getY();
        double a = a(xb, yb, xt, yt);

        Iterator<Edge> edgeIterator = edges.iterator();

        while (edgeIterator.hasNext()) {
            Edge current = edgeIterator.next();
            double XB = (bottom2) ? current.getBottom().getX() - deltaX : current.getBottom().getX();
            double YB = (bottom2) ? current.getBottom().getY() - deltaY : current.getBottom().getY();
            double XT = (top2) ? current.getTop().getX() - deltaX : current.getTop().getX();
            double YT = (top2) ? current.getTop().getY() - deltaY : current.getTop().getY();

            if (cross(xb, yb, xt, yt, XB, YB, XT, YT)) {
                return true;
            }
            if (top && cross(xt, yt, e.getTop().getX(), e.getTop().getY(), XB, YB, XT, YT) && !top2) {
                return true;
            }
            if (bottom && cross(xb, yb, e.getBottom().getX(), e.getBottom().getY(), XB, YB, XT, YT) && !top2) {
                return true;
            }
        }
        return false;
    }

    public static boolean collides(Collection<Edge> edgesToMove, boolean top, boolean bottom, double deltaX, double deltaY, Collection<Edge> edges, boolean top2, boolean bottom2) {
        Iterator<Edge> edgeIterator = edgesToMove.iterator();
        while (edgeIterator.hasNext()) {
            if (collides(edgeIterator.next(), top, bottom, deltaX, deltaY, edges, top2, bottom2)) {
                return true;
            }
        }
        return false;
    }

    public static double a(double x1, double y1, double x2, double y2) {
        if (x1 != x2) {
            return (y2 - y1) / (x2 - x1);
        } else {
            return 0.0;
        }
    }

    public static Vertex getIntersectionPoint(double xb, double yb, double xt, double yt, double XB, double YB, double XT, double YT) {
        double a = a(xb, yb, xt, yt);
        double b = yb - xb * a;

        double A = a(XB, YB, XT, YT);
        double B = YB - XB * A;

        double x = (B - b) / (a - A);
        double y = a * x + b;

        if ((x - xb) * (x - xt) < 0 && (y - yb) * (y - yt) < 0 && (x - XB) * (x - XT) < 0 && (y - YB) * (y - YT) < 0) {
            return new Vertex(x, y);
        } else {
            return null;
        }
    }

    static double[] getDifference(double alpha, Vertex v, Vertex w) {
        double[] xyDelta = new double[2];

        double xt = w.getX() - v.getX();
        double yt = w.getY() - v.getY();

        double x = xt * Math.cos(alpha) - yt * Math.sin(alpha) + v.getX();
        double y = xt * Math.sin(alpha) + yt * Math.cos(alpha) + v.getY();

        xyDelta[0] = w.getX() - x;
        xyDelta[1] = w.getY() - y;

        return xyDelta;
    }



    public static void changeCoordinates(Vertex v, Vertex w, LinkedList<Edge> edges, double alpha) {
        Edge e = edges.getFirst();

        boolean top = (e.getBottom() == v) ? true : false;

        double deltaX = w.getX();
        double deltaY = w.getY();

        rotate(v, w, e, alpha);

        deltaX -= w.getX();
        deltaY -= w.getY();

        LinkedList<Vertex> vertices = new LinkedList<>();
        for (int i = 0; i < edges.size(); i++) {
            if (top) {
                vertices.add(edges.get(i).getTop());
            } else {
                vertices.add(edges.get(i).getBottom());
            }
            edges.get(i).setVisited(true);
        }

        translocate(vertices, deltaX, deltaY);
    }

    private static void rotate(Vertex v, Vertex w, Edge e, double alpha) {
        double xt = w.getX() - v.getX();
        double yt = w.getY() - v.getY();

        double x = xt * Math.cos(alpha) - yt * Math.sin(alpha) + v.getX();
        double y = xt * Math.sin(alpha) + yt * Math.cos(alpha) + v.getY();

        w.setX(x);
        w.setY(y);
        e.setVisited(true);
        w.setVisited(true);
    }

    public static boolean noCollisionsForRotation(Vertex v, Set<Edge> bottom, Set<Edge> top, double alpha) {
        Iterator<Edge> edgeIt = bottom.iterator();
        while (edgeIt.hasNext()) {
            Edge ee = edgeIt.next();

            Vertex eb = ee.getBottom();
            double xtb = eb.getX() - v.getX();
            double ytb = eb.getY() - v.getY();

            double xb = xtb * Math.cos(alpha) - ytb * Math.sin(alpha) + v.getX();
            double yb = xtb * Math.sin(alpha) + ytb * Math.cos(alpha) + v.getY();

            Vertex et = ee.getTop();
            double xtt = et.getX() - v.getX();
            double ytt = et.getY() - v.getY();

            double xt = xtt * Math.cos(alpha) - ytt * Math.sin(alpha) + v.getX();
            double yt = xtt * Math.sin(alpha) + ytt * Math.cos(alpha) + v.getY();

            if (twoLinesCrosses(xb, yb, xt, yt, top)) {
                return false;
            }
        }

        return true;
    }

    public static void rotateAll(Vertex v, Set<Edge> edges, double alpha) {
        Set<Vertex> verticesToRotate = new HashSet<>();
        Iterator<Edge> edgeIt = edges.iterator();
        while (edgeIt.hasNext()) {
            Edge e = edgeIt.next();
            verticesToRotate.add(e.getBottom());
            verticesToRotate.add(e.getTop());
        }

        verticesToRotate.remove(v);

        Iterator<Vertex> vertexIt = verticesToRotate.iterator();
        while (vertexIt.hasNext()) {
            Vertex w = vertexIt.next();
            double xt = w.getX() - v.getX();
            double yt = w.getY() - v.getY();

            double x = xt * Math.cos(alpha) - yt * Math.sin(alpha) + v.getX();
            double y = xt * Math.sin(alpha) + yt * Math.cos(alpha) + v.getY();

            w.setX(x);
            w.setY(y);
            w.setVisited(true);
        }
    }
}
