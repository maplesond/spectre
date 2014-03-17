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

package uk.ac.uea.cmp.spectre.flatnj.netvi;

import uk.ac.uea.cmp.spectre.flatnj.fdraw.Translocator;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author balvociute
 */
public class Cluster {
    Color color;
    Set<Point> points;

    int width = 0;
    int height;

    double x;
    double y;

    double offX;
    double offY;

    boolean leaders;
    private boolean movable = true;

    Window window;

    public Cluster() {
        points = new HashSet();
        Random random = new Random();
        color = new Color(random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256));
    }

    void add(Point p, Label l, boolean changeColors) {
        if (points.isEmpty()) {
            x = l.getX();
            y = l.getY() - l.label.getHeight();
            window = l.mainFrame;
        }
        l.setCluster(this);
        points.add(p);
        if (changeColors) {
            if (p.v.getBackgroundColor().getRGB() == p.v.getLineColor().getRGB()) {
                p.v.setLineColor(color);
            }
            p.v.setBackgroundColor(color);
        }
        width = (width < l.label.getWidth()) ? l.label.getWidth() : width;
        height = computeHeight();
    }

    public Point getAttractionPoint() {
        double cx = 0;
        double cy = 0;
        Iterator<Point> pointIt = points.iterator();
        while (pointIt.hasNext()) {
            Point p = pointIt.next();
            cx += p.getX();
            cy += p.getY();
        }
        return new Point(cx / points.size(), cy / points.size());
    }

    public int computeHeight() {
        Iterator<Point> pointIt = points.iterator();
        int h = 0;
        while (pointIt.hasNext()) {
            Point point = pointIt.next();
            h += point.l.label.getHeight();
        }
        return h;
    }


    public int getHeight() {
        return height;
    }

    void setXY(double x, double y) {
        this.x = x;
        this.y = y;
        Point att = getAttractionPoint();
        offX = att.x - x;
        offY = att.y - y;
    }

    Point closestPoint(Point p) {
        Line l1 = new Line(new Point(x, y), new Point(x + width, y));
        Point p1 = l1.closestPoint(p);

        Line l2 = new Line(new Point(x, y), new Point(x, y - height));
        Point p2 = l2.closestPoint(p);

        Line l3 = new Line(new Point(x, y - height), new Point(x + width, y - height));
        Point p3 = l3.closestPoint(p);

        Line l4 = new Line(new Point(x + width, y), new Point(x + width, y - height));
        Point p4 = l4.closestPoint(p);

        double dist1 = p1.distanceTo(p);
        double dist2 = p2.distanceTo(p);
        double dist3 = p3.distanceTo(p);
        double dist4 = p4.distanceTo(p);

        if (dist1 <= dist2 && dist1 <= dist3 && dist1 <= dist4) {
            return p1;
        } else if (dist2 <= dist3 && dist2 <= dist4) {
            return p2;
        } else if (dist3 <= dist4) {
            return p3;
        } else {
            return p4;
        }
    }

    Point repulsionPoint(Window window, Set<Cluster> clusters) {
        Collection<Point> networkPoints = window.points.values();
        Collection<Line> lines = window.lines.values();
        Point rep = null;
        if (clusters != null && !clusters.isEmpty()) {
            for (int t = 1; t > 0.05; t *= 0.95) {
                Iterator<Cluster> clusterIt = clusters.iterator();
                while (clusterIt.hasNext()) {
                    Cluster clu = clusterIt.next();
                    double min = 0.0;
                    Iterator<Point> pointIt = networkPoints.iterator();
                    while (pointIt.hasNext()) {
                        Point p = pointIt.next();
                        if (!clu.points.contains(p)) {
                            Point cl = clu.closestPoint(p);
                            double dist = cl.distanceTo(p);
                            if (rep == null || min > dist) {
                                rep = p;
                                min = dist;
                            }
                        }
                    }
                    Iterator<Line> lineIt = lines.iterator();
                    while (lineIt.hasNext()) {
                        Line lin = lineIt.next();
                        Point[] closests = clu.closestPoint(lin);
                        double dist = closests[0].distanceTo(closests[1]);
                        if (rep == null || dist < min) {
                            rep = closests[1];
                            min = dist;
                        }
                    }
                    Iterator<Cluster> clusterlIt2 = clusters.iterator();
                    while (clusterlIt2.hasNext()) {
                        Cluster clu2 = clusterlIt2.next();
                        if (clu2 != clu) {
                            Point[] closests = clu.closestPoint(clu2);
                            double dist = closests[0].distanceTo(closests[1]);
                            if (rep == null || dist < min) {
                                rep = closests[1];
                                min = dist;
                            }
                        }
                    }
                }
            }
        }
        return rep;
    }

    public Point[] closestPoint(Cluster lab) {
        Line[] edges = new Line[4];

        double labX = lab.x;
        double labY = lab.y;

        Point[] pl = new Point[4];
        pl[0] = new Point(labX, labY);
        pl[1] = new Point(labX, labY - lab.height);
        pl[2] = new Point(labX + lab.width, labY);
        pl[3] = new Point(labX + lab.width, labY - lab.height);

        edges[0] = new Line(pl[0], pl[1]);
        edges[1] = new Line(pl[0], pl[2]);
        edges[2] = new Line(pl[1], pl[3]);
        edges[3] = new Line(pl[2], pl[3]);

        Point[] result = null;
        double min = 0.0;

        for (Line edge : edges) {
            Point[] candidates = closestPoint(edge);
            double dist = candidates[0].distanceTo(candidates[1]);
            if (result == null || min > dist) {
                result = candidates;
            }
        }

        return result;
    }

    private Point[] closestPoint(Line lin) {
        Point[] pl = new Point[4];

        pl[0] = new Point(x, y);
        pl[1] = new Point(x, y - height);
        pl[2] = new Point(x + width, y);
        pl[3] = new Point(x + width, y - height);

        Point minP = null;
        Point labP = null;
        double minD = 0.0;

        for (Point pp : pl) {
            Point cl = lin.closestPoint(pp);
            double dist = pp.distanceTo(cl);
            if (minP == null || minD > dist) {
                minP = cl;
                minD = dist;
                labP = pp;
            }
        }

        return new Point[]{labP, minP};
    }

    int size() {
        return points.size();
    }

    double getRay() {
        return Math.sqrt(height * height / 4 + width * width / 4);
    }

    public Point furthestPoint(Point att) {
        Point furthest = null;
        Iterator<Point> pointIt = points.iterator();
        while (pointIt.hasNext()) {
            Point point = pointIt.next();
            if (furthest == null
                    ||
                    att.distanceTo(furthest) < att.distanceTo(point)) {
                furthest = point;
            }
        }
        return furthest;
    }

    void remove(Label label) {
        points.remove(label.p);
        height = computeHeight();
        if (label.label.getWidth() == width) {
            width = computeWidth();
        }
        label.cluster = null;
    }

    private int computeWidth() {
        int w = 0;
        Iterator<Point> pointIt = points.iterator();
        while (pointIt.hasNext()) {
            Point point = pointIt.next();
            if (w < point.l.label.getWidth()) {
                w = point.l.label.getWidth();
            }
        }
        return w;
    }

    void setMovable(boolean b) {
        movable = b;
    }

    boolean isMovable() {
        return movable;
    }

    boolean isEmpty() {
        return points.isEmpty();
    }

    public void setLabelCoordinates(double x, double y) {
        setXY(x, y);

        Iterator<Point> pIt = points.iterator();
        List<Point> unconnectedPoints = new LinkedList();

        Double minY = null;
        Double maxY = null;

        Point p = null;

        while (pIt.hasNext()) {
            p = pIt.next();

            if (minY == null || minY > p.getY()) {
                minY = p.getY();
            }
            if (maxY == null || maxY < p.getY()) {
                maxY = p.getY();
            }

            if (unconnectedPoints.isEmpty()) {
                unconnectedPoints.add(p);
            } else {
                for (int i = 0; i < unconnectedPoints.size(); i++) {
                    if (p.getX() > x && p.getX() > unconnectedPoints.get(i).getX()
                            ||
                            p.getX() < x && p.getX() < unconnectedPoints.get(i).getX()) {
                        unconnectedPoints.add(i, p);
                        break;
                    }
                    if (i == unconnectedPoints.size() - 1) {
                        unconnectedPoints.add(p);
                        break;
                    }
                }
            }
        }

        if (p.l != null && p.l.getX() != p.l.getlX()) {
            x += width;
        }

        boolean onTheRight = (x - unconnectedPoints.get(0).getX() > 0);
        int h = unconnectedPoints.get(0).l.label.getHeight();

        int labelsAbove = (int) Math.min(((minY > y) ? (minY - y) : 0) / h,
                unconnectedPoints.size());
        int labelsBelow = (int) Math.min(((maxY < y + unconnectedPoints.size() * h)
                ? y + unconnectedPoints.size() * h - maxY
                : 0) / h,
                unconnectedPoints.size());

        List<Point> backPoints = extractBackPoints(unconnectedPoints,
                labelsAbove + labelsBelow);

        List<Point> above = extractAbovePoints(backPoints, labelsAbove);
        List<Point> below = extraxtBelowPoints(backPoints, above);

        double y2 = y + h * (points.size());
        for (int i = 0; i < below.size(); i++) {
            Point first = below.get(i);
            unconnectedPoints.remove(first);
            Label l = first.l;
            l.forceLeader(x, y2);
            y2 -= l.label.getHeight();
        }

        for (int i = 0; i < above.size(); i++) {
            Point first = above.get(i);
            unconnectedPoints.remove(first);
            Label l = first.l;
            y += l.label.getHeight();
            l.forceLeader(x, y);
        }

        while (!unconnectedPoints.isEmpty()) {
            Point first = closestPointAbove(unconnectedPoints, y, onTheRight);

            unconnectedPoints.remove(first);
            Label l = first.l;
            y += l.label.getHeight();
            l.forceLeader(x, y);
        }

        if (window != null && !window.externalFrame.bendedLeaders()) {
            Iterator<Point> pointIt = points.iterator();
            while (pointIt.hasNext()) {
                Point p1 = pointIt.next();
                Label l1 = p1.l;
                Iterator<Point> pointIt2 = points.iterator();
                while (pointIt2.hasNext()) {
                    Point p2 = pointIt2.next();
                    if (p1 != p2) {
                        Label l2 = p2.l;
                        if (window.externalFrame.straightLeaders()) {
                            if (Translocator.cross(p1.getX(), p1.getY(), l1.getlX(), l1.getlY(),
                                    p2.getX(), p2.getY(), l2.getlX(), l2.getlY())) {
                                l1.changePositionsWith(l2);
                            }
                        } else if (window.externalFrame.slantedLeaders()) {
                            Point b1 = l1.computeBendingPoint(window.midX,
                                    window.midY,
                                    false);
                            Point b2 = l2.computeBendingPoint(window.midX,
                                    window.midY,
                                    false);
                            if (Translocator.cross(p1.getX(), p1.getY(), b1.getX(), b1.getY(),
                                    p2.getX(), p2.getY(), b2.getX(), b2.getY())
                                    ||
                                    Translocator.cross(p1.getX(), p1.getY(), b1.getX(), b1.getY(),
                                            b2.getX(), b2.getY(), l2.getlX(), l2.getlY())
                                    ||
                                    Translocator.cross(b1.getX(), b1.getY(), l1.getlX(), l1.getlY(),
                                            p2.getX(), p2.getY(), b2.getX(), b2.getY())
                                    ||
                                    Translocator.cross(b1.getX(), b1.getY(), l1.getlX(), l1.getlY(),
                                            b2.getX(), b2.getY(), l2.getlX(), l2.getlY())) {
                                l1.changePositionsWith(l2);
                            }
                        }
                    }
                }
            }
        }
    }

    private Point closestPointAbove(List<Point> unconnectedPoints,
                                    double y,
                                    boolean onTheRight) {
        for (int i = unconnectedPoints.size() - 1; i >= 0; i--) {
            Point p = unconnectedPoints.get(i);
            if (p.getY() <= y + p.l.label.getHeight()) {
                return p;
            }
        }
        return unconnectedPoints.get(0);
    }

    private List<Point> extractBackPoints(List<Point> unconnectedPoints,
                                          int n) {
        List<Point> back = new LinkedList();
        {
            for (int i = 0; i < n; i++) {
                back.add(unconnectedPoints.get(i));
            }
        }
        return back;
    }

    private List<Point> extractAbovePoints(List<Point> backPoints,
                                           int n) {
        List<Point> topPoints = new LinkedList();
        for (int i = 0; i < backPoints.size(); i++) {
            topPoints.add(backPoints.get(i));
        }
        while (topPoints.size() > n) {
            Point remove = topPoints.get(0);
            double minY = remove.getY();
            for (int i = 1; i < topPoints.size(); i++) {
                if (minY < topPoints.get(i).getY()) {
                    minY = topPoints.get(i).getY();
                    remove = topPoints.get(i);
                }
            }
            topPoints.remove(remove);
        }
        return topPoints;
    }

    private List<Point> extraxtBelowPoints(List<Point> backPoints,
                                           List<Point> above) {
        List<Point> botPoints = new LinkedList();
        for (int i = 0; i < backPoints.size(); i++) {
            Point p = backPoints.get(i);
            if (!above.contains(p)) {
                botPoints.add(p);
            }
        }
        return botPoints;
    }


}
