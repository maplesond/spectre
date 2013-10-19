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
public class Translocator {

    public static boolean noCollisions(LinkedList<Edge> split, Set<Edge> topEdges, Set<Edge> bottomEdges, double deltaAlpha) {
        double[] xyDelta = getDifference(split.getFirst(), deltaAlpha);

        double xDelta = xyDelta[0];
        double yDelta = xyDelta[1];

        for (int i = 0; i < split.size(); i++) {
            Edge e = split.get(i);
            if (twoLinesCrosses(e.bot.x, e.bot.y, e.top.x - xDelta, e.top.y - yDelta, bottomEdges)) {
                return false;
            }
            if (twoLinesCrosses(e.top.x, e.top.y, e.top.x - xDelta, e.top.y - yDelta, bottomEdges)) {
                return false;
            }
        }

        for (int i = 0; i < split.size(); i++) {
            Edge e = split.get(i);
            if (twoLinesCrosses(e.bot.x, e.bot.y, e.top.x - xDelta, e.top.y - yDelta, topEdges, xDelta, yDelta)) {
                return false;
            }
        }

        Iterator<Edge> topEdgeIterator = topEdges.iterator();
        while (topEdgeIterator.hasNext()) {
            Edge e = topEdgeIterator.next();
            if (twoLinesCrosses(e.bot.x - xDelta, e.bot.y - yDelta, e.top.x - xDelta, e.top.y - yDelta, bottomEdges)) {
                return false;
            }
            if (twoLinesCrosses(e.bot.x, e.bot.y, e.bot.x - xDelta, e.bot.y - yDelta, bottomEdges)) {
                return false;
            }
            if (twoLinesCrosses(e.top.x, e.top.y, e.top.x - xDelta, e.top.y - yDelta, bottomEdges)) {
                return false;
            }
        }
        return true;
    }

    private static double[] getDifference(Edge e, double alpha) {
        double[] xyDelta = new double[2];

        Vertex top = e.top;
        Vertex bot = e.bot;

        double xt = top.x - bot.x;
        double yt = top.y - bot.y;

        double x = xt * Math.cos(alpha) - yt * Math.sin(alpha) + bot.x;
        double y = xt * Math.sin(alpha) + yt * Math.cos(alpha) + bot.y;

        xyDelta[0] = top.x - x;
        xyDelta[1] = top.y - y;

        return xyDelta;
    }

    public static boolean twoLinesCrosses(double xb, double yb, double xt, double yt, Set<Edge> edges) {
        Iterator<Edge> iterator = edges.iterator();
        while (iterator.hasNext()) {
            Edge e = iterator.next();
            double XB = e.bot.x;
            double YB = e.bot.y;
            double XT = e.top.x;
            double YT = e.top.y;

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
            double XB = e.bot.x - xDelta;
            double YB = e.bot.y - yDelta;
            double XT = e.top.x - xDelta;
            double YT = e.top.y - yDelta;

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

        double deltaX = edges.getFirst().top.x;
        double deltaY = edges.getFirst().top.y;

        rotate(edges.getFirst(), alpha);

        deltaX -= edges.getFirst().top.x;
        deltaY -= edges.getFirst().top.y;

        LinkedList<Vertex> vertices = new LinkedList<>();
        for (int i = 0; i < edges.size(); i++) {
            vertices.add(edges.get(i).top);
            edges.get(i).visited = true;
        }

        translocate(vertices, deltaX, deltaY);
    }

    private static void rotate(Edge e, double alpha) {
        Vertex top = e.top;
        Vertex bot = e.bot;

        double xt = top.x - bot.x;
        double yt = top.y - bot.y;

        double x = xt * Math.cos(alpha) - yt * Math.sin(alpha) + bot.x;
        double y = xt * Math.sin(alpha) + yt * Math.cos(alpha) + bot.y;

        top.x = x;
        top.y = y;
        e.visited = true;
        top.visited = true;
    }

    private static void translocate(LinkedList<Vertex> vertices, double deltaX, double deltaY) {
        while (!vertices.isEmpty()) {
            Vertex v = vertices.removeFirst();
            if (!v.visited) {
                //System.out.print(v.nxnum + " ");
                move(v, deltaX, deltaY);
            }

            LinkedList<Edge> edges = v.elist;
            for (int i = 0; i < edges.size(); i++) {
                Edge e = edges.get(i);
                if (!e.visited) {
                    //System.out.print(e.nxnum + " ");
                    e.visited = true;
                    if (!e.top.visited) {
                        vertices.add(e.top);
                    }
                    if (!e.bot.visited) {
                        vertices.add(e.bot);
                    }
                }
            }
        }
        //System.out.println();
    }

    private static void move(Vertex v, double deltaX, double deltaY) {
        v.x = v.x - deltaX;
        v.y = v.y - deltaY;
        v.visited = true;
    }

    public static boolean isBottomPartFreeFromTopVertices(Edge leftmost, Edge rightmost, Set<Edge> edges) {
        double lxb = leftmost.bot.x;
        double lyb = leftmost.bot.y;
        double lxt = leftmost.top.x;
        double lyt = leftmost.top.y;
        double la = (lyt - lyb) / (lxt - lxb);

        double rxb = rightmost.bot.x;
        double ryb = rightmost.bot.y;
        double rxt = rightmost.top.x;
        double ryt = rightmost.top.y;
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
        double lxb = leftmost.bot.x;
        double lyb = leftmost.bot.y;
        double lxt = leftmost.top.x;
        double lyt = leftmost.top.y;
        double la = (lyt - lyb) / (lxt - lxb);

        double rxb = rightmost.bot.x;
        double ryb = rightmost.bot.y;
        double rxt = rightmost.top.x;
        double ryt = rightmost.top.y;
        double ra = (ryt - ryb) / (rxt - rxb);

        Iterator<Edge> edgeIterator = edges.iterator();
        while (edgeIterator.hasNext()) {
            Edge e = edgeIterator.next();
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
        double xb = e.bot.x;
        double yb = e.bot.y;
        double xt = e.top.x;
        double yt = e.top.y;
        double a = a(xb, yb, xt, yt);

        double x = (yb2 - xb2 * a2 - yb + xb * a) / (a - a2);
        double y = yb + (x - xb) * a;

        if (x > xb && x < xt && y > yb && y < yt && y > yb2) {
            return true;
        }
        return false;
    }

    private static boolean crosses2(double xb2, double yb2, double xt2, double yt2, double a2, Edge e) {
        double xb = e.bot.x;
        double yb = e.bot.y;
        double xt = e.top.x;
        double yt = e.top.y;
        double a = a(xb, yb, xt, yt);

        double x = (yb2 - xb2 * a2 - yb + xb * a) / (a - a2);
        double y = yb + (x - xb) * a;

        if (x > xb && x < xt && y > yb && y < yt && y < yt2) {
            return true;
        }
        return false;
    }

    public static boolean collides(Edge e, boolean top, boolean bottom, double deltaX, double deltaY, Collection<Edge> edges, boolean top2, boolean bottom2) {
        double xb = (bottom) ? e.bot.x - deltaX : e.bot.x;
        double yb = (bottom) ? e.bot.y - deltaY : e.top.y;
        double xt = (top) ? e.top.x - deltaX : e.top.x;
        double yt = (top) ? e.top.y - deltaY : e.top.y;
        double a = a(xb, yb, xt, yt);

        Iterator<Edge> edgeIterator = edges.iterator();

        while (edgeIterator.hasNext()) {
            Edge current = edgeIterator.next();
            double XB = (bottom2) ? current.bot.x - deltaX : current.bot.x;
            double YB = (bottom2) ? current.bot.y - deltaY : current.bot.y;
            double XT = (top2) ? current.top.x - deltaX : current.top.x;
            double YT = (top2) ? current.top.y - deltaY : current.top.y;

            if (cross(xb, yb, xt, yt, XB, YB, XT, YT)) {
                return true;
            }
            if (top && cross(xt, yt, e.top.x, e.top.y, XB, YB, XT, YT) && !top2) {
                return true;
            }
            if (bottom && cross(xb, yb, e.bot.x, e.bot.y, XB, YB, XT, YT) && !top2) {
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

        double xt = w.x - v.x;
        double yt = w.y - v.y;

        double x = xt * Math.cos(alpha) - yt * Math.sin(alpha) + v.x;
        double y = xt * Math.sin(alpha) + yt * Math.cos(alpha) + v.y;

        xyDelta[0] = w.x - x;
        xyDelta[1] = w.y - y;

        return xyDelta;
    }

    static boolean noCollisions(Vertex v, Vertex w, Edge e, LinkedList<Edge> split, Set<Edge> topEdges, Set<Edge> bottomEdges, double angle) {
        double[] xyDelta = getDifference(angle, v, w);

        double xDelta = xyDelta[0];
        double yDelta = xyDelta[1];

        boolean bottom = (e.bot == v) ? true : false;

        for (int i = 0; i < split.size(); i++) {
            Edge ee = split.get(i);

            double x1;
            double x2;
            double y1;
            double y2;

            if (bottom) {
                x1 = ee.bot.x;
                y1 = ee.bot.y;
                x2 = ee.top.x;
                y2 = ee.top.y;
            } else {
                x1 = ee.top.x;
                y1 = ee.top.y;
                x2 = ee.bot.x;
                y2 = ee.bot.y;
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
            if (twoLinesCrosses(ee.bot.x - xDelta, ee.bot.y - yDelta, ee.top.x - xDelta, ee.top.y - yDelta, bottomEdges)) {
                return false;
            }
            if (twoLinesCrosses(ee.bot.x, ee.bot.y, ee.bot.x - xDelta, ee.bot.y - yDelta, bottomEdges)) {
                return false;
            }
            if (twoLinesCrosses(ee.top.x, ee.top.y, ee.top.x - xDelta, ee.top.y - yDelta, bottomEdges)) {
                return false;
            }
        }
        return true;
    }

    static void changeCoordinates(Vertex v, Vertex w, LinkedList<Edge> edges, double alpha) {
        Edge e = edges.getFirst();

        boolean top = (e.bot == v) ? true : false;

        double deltaX = w.x;
        double deltaY = w.y;

        rotate(v, w, e, alpha);

        deltaX -= w.x;
        deltaY -= w.y;

        LinkedList<Vertex> vertices = new LinkedList<>();
        for (int i = 0; i < edges.size(); i++) {
            if (top) {
                vertices.add(edges.get(i).top);
            } else {
                vertices.add(edges.get(i).bot);
            }
            edges.get(i).visited = true;
        }

        translocate(vertices, deltaX, deltaY);
    }

    private static void rotate(Vertex v, Vertex w, Edge e, double alpha) {
        double xt = w.x - v.x;
        double yt = w.y - v.y;

        double x = xt * Math.cos(alpha) - yt * Math.sin(alpha) + v.x;
        double y = xt * Math.sin(alpha) + yt * Math.cos(alpha) + v.y;

        w.x = x;
        w.y = y;
        e.visited = true;
        w.visited = true;
    }

    static boolean noCollisionsForRotation(Vertex v, Set<Edge> bottom, Set<Edge> top, double alpha) {
        Iterator<Edge> edgeIt = bottom.iterator();
        while (edgeIt.hasNext()) {
            Edge ee = edgeIt.next();

            Vertex eb = ee.bot;
            double xtb = eb.x - v.x;
            double ytb = eb.y - v.y;

            double xb = xtb * Math.cos(alpha) - ytb * Math.sin(alpha) + v.x;
            double yb = xtb * Math.sin(alpha) + ytb * Math.cos(alpha) + v.y;

            Vertex et = ee.top;
            double xtt = et.x - v.x;
            double ytt = et.y - v.y;

            double xt = xtt * Math.cos(alpha) - ytt * Math.sin(alpha) + v.x;
            double yt = xtt * Math.sin(alpha) + ytt * Math.cos(alpha) + v.y;

            if (twoLinesCrosses(xb, yb, xt, yt, top)) {
                return false;
            }
        }

        return true;
    }

    static void rotateAll(Vertex v, Set<Edge> edges, double alpha) {
        Set<Vertex> verticesToRotate = new HashSet<>();
        Iterator<Edge> edgeIt = edges.iterator();
        while (edgeIt.hasNext()) {
            Edge e = edgeIt.next();
            verticesToRotate.add(e.bot);
            verticesToRotate.add(e.top);
        }

        verticesToRotate.remove(v);

        Iterator<Vertex> vertexIt = verticesToRotate.iterator();
        while (vertexIt.hasNext()) {
            Vertex w = vertexIt.next();
            double xt = w.x - v.x;
            double yt = w.y - v.y;

            double x = xt * Math.cos(alpha) - yt * Math.sin(alpha) + v.x;
            double y = xt * Math.sin(alpha) + yt * Math.cos(alpha) + v.y;

            w.x = x;
            w.y = y;
            w.visited = true;

        }
    }
}
