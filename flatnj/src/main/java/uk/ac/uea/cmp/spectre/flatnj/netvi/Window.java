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

import uk.ac.uea.cmp.spectre.flatnj.ds.Leaders;
import uk.ac.uea.cmp.spectre.flatnj.ds.Network;
import uk.ac.uea.cmp.spectre.flatnj.fdraw.AngleCalculatorSimple;
import uk.ac.uea.cmp.spectre.flatnj.fdraw.Edge;
import uk.ac.uea.cmp.spectre.flatnj.fdraw.Vertex;
import uk.ac.uea.cmp.spectre.flatnj.tools.Utilities;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author balvociute
 */
public class Window extends JPanel {
    private Point selectedPoint;
    private Set<Cluster> clusters = new HashSet<>();
    double maxLabelH;
    double maxLabelW;
    private java.awt.Point last = null;
    private final Stroke dashedStroke = new BasicStroke(
            1.0f,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,
            10.0f,
            new float[]{5.0f},
            0.0f);
    private final Stroke dottedStroke = new BasicStroke(
            1.0f,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,
            10.0f,
            new float[]{1.0f},
            0.0f);
    private double maxOffX;
    private double maxOffY;
    private double change;
    private final int extraSpace = 20;
    private final int maximalLabelWidth = 40;

    public Window() {
    }

    public Window(NetView frame) {
        externalFrame = frame;
    }

    Stack<State> undo = new Stack();
    Stack<State> redo = new Stack();
    State currentState;
    NetView externalFrame;
    double ratio;
    Double minX = null;
    Double maxX = null;
    Double minY = null;
    Double maxY = null;
    Point pMinX;
    Point pMaxX;
    Point pMinY;
    Point pMaxY;
    double rotationAngle = 0.0;
    List<Edge> edges;
    Network network;
    Leaders leaders = new Leaders();
    Map<Integer, Point> points = new HashMap<>();
    Map<Integer, Line> lines = new HashMap<>();
    Map<Integer, Label> labels = new HashMap<>();
    Map<Integer, Vertex> vertices = new HashMap<>();
    int[] selectionRectangle = null;
    boolean resetedLabelPositions = false;
    public int midX = 0;
    public int midY = 0;
    double heightMargin = 0;
    double sideMargin = 0;
    double heightMarginSaved = 0;
    double sideMarginSaved = 0;
    int deltaX;
    int deltaY;
    Set<Point> pointsToHighlight = new SelectableSet();
    Set<Line> linesToHightlight = new HashSet();
    Color selectionColor = Color.RED;
    Color leaderColor = Color.BLUE;
    Label selectedLabel = null;
    boolean rotate = false;
    java.awt.Point lastPoint = null;
    ClusterFinder cf = new ClusterFinderSplits();
    ClusterPlacementOptimizer cpo = new ClusterPlacementOptimizerBox();

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        setBackground(Color.white);

        Graphics2D g2 = (Graphics2D) g;

        if (lines != null && lines.size() > 0) {
            for (Line l : lines.values()) {
                g2.setStroke(new BasicStroke(l.getWidth()));
                g.setColor(l.fg);
                g2.drawLine(l.p1.getXint(), l.p1.getYint(), l.p2.getXint(), l.p2.getYint());
            }
        }
        g2.setStroke(new BasicStroke(1));

        if (labels != null && labels.size() > 0 && externalFrame.showLabels() && externalFrame.leadersVisible()) {
            if (externalFrame.dashedLeaders()) {
                g2.setStroke(dashedStroke);
            } else if (externalFrame.dottedLeaders()) {
                g2.setStroke(dottedStroke);
            }

            boolean straight = externalFrame.straightLeaders();

            for (Label l : labels.values()) {
                if (l.leader) {
                    if (l.p.isSelected()) {
                        g.setColor(Color.red);
                    } else {
                        g.setColor(leaderColor);
                    }
                    if (straight) {
                        g.drawLine(l.p.getXint(), l.p.getYint(), l.getlX(), l.getlY());
                    } else {
                        Point bentPoint = l.computeBendingPoint(midX, midY, externalFrame.bendedLeaders());
                        int[] x = new int[]{l.p.getXint(), bentPoint.getXint(), l.getlX()};
                        int[] y = new int[]{l.p.getYint(), bentPoint.getYint(), l.getlY()};
                        g.drawPolyline(x, y, 3);
                    }
                }
            }
        }

        g2.setStroke(new BasicStroke());
        if (points != null && points.size() > 0) {
            for (Point p : points.values()) {
                boolean selected = p.isSelected();

                g.setColor(vertices.get(p.id).getBackgroundColor());

                int x = p.getXint() - (int) (((double) p.width) / 2.0);
                int y = p.getYint() - (int) (((double) p.height) / 2.0);

                if (p.round) {
                    g.fillOval(x, y, p.width, p.height);
                } else {
                    g.fillRect(x, y, p.width, p.height);
                }

                g.setColor(vertices.get(p.id).getLineColor());
                if (p.round && p.width > 0 && p.height > 0) {
                    g.drawOval(x, y, p.width, p.height);
                    if (selected) {
                        g.setColor(selectionColor);
                        g.drawOval(x - 1, y - 1, p.width + 2, p.height + 2);
                    }
                } else {
                    g.drawRect(x, y, p.width, p.height);
                    if (selected) {
                        g.setColor(selectionColor);
                        g.drawRect(x - 1, y - 1, p.width + 2, p.height + 2);
                    }
                }
            }
        }
        if (labels != null && labels.size() > 0 && externalFrame.showLabels()) {
            boolean colorLabels = externalFrame.colorLabels();
            for (Label l : labels.values()) {
                boolean selected = l.p.isSelected();

                if (l.label.getFontFamily() == null) {
                    l.label.setFontFamily("Helvetica");
                }
                g.setFont(new Font(l.label.getFontFamily(), l.label.getFontStyle(), l.label.getFontSize()));

                int lx = l.getXint();
                int ly = l.getYint();

                if (l.p.v.getBackgroundColor() != null && colorLabels) {
                    g.setColor(l.p.v.getBackgroundColor());
                    g2.fillRect(lx - 1, ly - l.label.getHeight() + 2, l.label.getWidth() + 3, l.label.getHeight());
                }
                if (selected) {
                    g.setColor(selectionColor);
                    g2.drawRect(lx, ly - l.label.getHeight() + 1, l.label.getWidth(), l.label.getHeight());
                }
                Color textColor = l.label.getFontColor();
                if (l.p.v.getBackgroundColor() != null && colorLabels) {
                    textColor = Utilities.getTextColor(l.p.v.getBackgroundColor());
                }
                g.setColor(textColor);
                g.drawString(l.name, lx, ly - 1);
            }
        }

        if (selectionRectangle != null) {
            g.setColor(Color.red);
            g.drawRect(selectionRectangle[0], selectionRectangle[1], selectionRectangle[2], selectionRectangle[3]);
        }

        if (rotate) {
            g.setColor(Color.BLUE);

            int cSize = 8;
            int lLength = 16;

            g.drawOval(midX - cSize / 2, midY - cSize / 2, cSize, cSize);
            g.drawLine(midX - lLength / 2, midY, midX + lLength / 2, midY);
            g.drawLine(midX, midY - lLength / 2, midX, midY + lLength / 2);
        }
    }

    private void computeIntegerCoordinates(Double ratio) {
        if (vertices != null) {
            findCornerPoints();
            scaleCoordinates(ratio);
            if (externalFrame.showLabels()) {
                findMaxOffsets();
                sideMargin = sideMarginSaved + maxOffX;
                heightMargin = heightMarginSaved + maxOffY;
            } else {
                sideMargin = extraSpace;
                heightMargin = extraSpace;
            }
            scaleCoordinates(ratio);
        }

        resetLabelPositions(false);

        if (lines != null) {
            for (Line line : lines.values()) {
                line.computeAB();
            }
        }

        repaint();
    }

    public Map<Integer, Vertex> getVertices() {
        return vertices;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public List<Vertex> getLabeled() {
        return network.getLabeled();
    }

    public void setGraph(Network network, int w, int h, boolean changeColors, Double ratio) {
        clusters.clear();
        points.clear();
        lines.clear();
        labels.clear();
        vertices.clear();

        this.network = network;

        List<Vertex> verticesList = network.getVertices();

        for (Vertex v : verticesList) {
            vertices.put(v.getNxnum(), v);
        }

        if (network.getRotationAngle() != 0) {
            rotate(verticesList, network.getRotationAngle());
        }

        heightMargin = 0;
        sideMargin = 0;
        heightMarginSaved = 0;
        sideMarginSaved = 0;

        for (Vertex v : verticesList) {
            int id = v.getNxnum();
            vertices.put(id, v);
            Point p = new Point(v);
            points.put(id, p);

            if (v.getLabel() != null) {
                Label l = new Label(v.getLabel(), this);
                p.setL(l);
                l.computeMiddleDistance();
                labels.put(id, l);

                int labelWidth = l.label.getWidth();
                if (labelWidth > maximalLabelWidth) {
                    labelWidth = maximalLabelWidth;
                }

                heightMargin = (heightMargin < l.label.getHeight()) ? l.label.getHeight() : heightMargin;
                sideMargin = (sideMargin < labelWidth) ? labelWidth : sideMargin;
            }
        }

        for (Label l : labels.values()) {
            l.checkForLeader();
        }

        maxLabelH = heightMargin;
        maxLabelW = sideMargin;

        heightMargin += extraSpace;
        sideMargin += extraSpace;

        heightMarginSaved = heightMargin;
        sideMarginSaved = sideMargin;

        edges = network.getEdges();
        for (Edge e : edges) {
            Line l = new Line(e);
            l.setPoints(points.get(e.getBot().getNxnum()),
                    points.get(e.getTop().getNxnum()));
            lines.put(e.getNxnum(), l);
        }

        rotationAngle = network.getRotationAngle();
        computeIntegerCoordinates(ratio);
    }

    private int computeX(double x, int delta) {
        return (int) ((x - minX) * ratio) + delta;
    }

    private int computeY(double y, int delta) {
        return (int) ((y - minY) * ratio) + delta;
    }

    private void rotate(List<Vertex> vertices, double rotationAngle) {
        Iterator<Vertex> vertexIt = vertices.iterator();
        while (vertexIt.hasNext()) {
            Vertex v = vertexIt.next();
            double x = v.getX();
            double y = v.getY();

            double newX = x * Math.cos(rotationAngle) - y * Math.sin(rotationAngle);
            double newY = x * Math.sin(rotationAngle) + y * Math.cos(rotationAngle);

            v.setCoordinates(newX, newY);
        }
    }

    void resetLabelPositions(boolean all) {
        midY = getHeight() / 2;
        midX = getWidth() / 2;

        LabelPlacementOptimizer lpo = new LabelPlacementOptimizer8Points();
        lpo.placeLabels(labels.values(), lines.values(), this, all);

        if (clusters != null && !clusters.isEmpty()) {
            cpo.placeClusterLabels(clusters, labels, this);
        }
    }

    public void highlightSelectedObjects(boolean append) {
        if (labels != null && !labels.isEmpty()) {
            int x1 = selectionRectangle[0];
            int y1 = selectionRectangle[1];
            int x2 = x1 + selectionRectangle[2];
            int y2 = y1 + selectionRectangle[3];

            if (!append) {
                pointsToHighlight.clear();
            }

            for (Label l : labels.values()) {
                Point p = l.p;
                double x = p.getX();
                double y = p.getY();
                if (x1 != x2 || y1 != y2) {
                    if (l.getX() >= x1 && l.getX() + l.label.getWidth() <= x2
                            && l.getY() - l.label.getHeight() >= y1 && l.getY() <= y2
                            || x - p.width / 2 >= x1 && x + p.width / 2 <= x2
                            && y - p.height / 2 >= y1 && y + p.height / 2 <= y2) {
                        pointsToHighlight.add(p);
                    }
                } else {
                    if ((l.getX() <= x1 && l.getX() + l.label.getWidth() >= x2
                            && l.getY() >= y1 && l.getY() - l.label.getHeight() <= y2)
                            || (x - p.width / 2 <= x1 && x + p.width / 2 >= x2
                            && y - p.height / 2 <= y1 && y + p.height / 2 >= y2)) {
                        pointsToHighlight.add(p);
                    }
                }

            }
            repaint();
        }
    }

    void setSelection(java.awt.Point startPoint,
                      java.awt.Point endPoint,
                      boolean append) {
        int x = Math.min(startPoint.x, endPoint.x);
        int y = Math.min(startPoint.y, endPoint.y);
        int width = Math.abs(startPoint.x - endPoint.x);
        int height = Math.abs(startPoint.y - endPoint.y);
        selectionRectangle = new int[]
                {
                        x, y, width, height
                };
        highlightSelectedObjects(append);
    }

    void setSelection(java.awt.Point clickedPoint, boolean append) {
        int x = clickedPoint.x;
        int y = clickedPoint.y;
        selectionRectangle = new int[]
                {
                        x, y, 0, 0
                };
        highlightSelectedObjects(append);
    }

    void repaintOnResize() {
        if (network != null) {
            computeIntegerCoordinates(null);
        }
        repaint();
    }

    void removeSelectionRectangle() {

        selectionRectangle = null;
        repaint();
    }

    void copySelectedTaxa() {
        String selectedTaxa = "";
        if (pointsToHighlight != null) {
            for (Point p : pointsToHighlight) {
                Label l = p.l;
                selectedTaxa += l.name + "\n";
            }
        }
        Clipboard system = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection sel = new StringSelection(selectedTaxa);
        system.setContents(sel, sel);
    }

    boolean isOnLabel() {
        return selectedLabel != null;
    }

    boolean moveInAction = false;

    void moveLabels(java.awt.Point endPoint) {
        if (!pointsToHighlight.isEmpty()) {
            if (!moveInAction) {
                moveInAction = true;
            }

            int dX = endPoint.x - last.x;
            int dY = endPoint.y - last.y;
            Cluster cluster = getClusterThatAllPointsBelongTo(pointsToHighlight);
            if (cluster != null && cluster.size() > 1) {
                cluster.setLabelCoordinates(cluster.x + dX, cluster.y + dY);
                cluster.setMovable(false);
            } else {
                boolean allMovable = true;
                for (Point p : pointsToHighlight) {
                    Label l = p.l;
                    if (l.cluster != null && l.cluster.size() > 1) {
                        for (Point point : l.cluster.points) {
                            if (!pointsToHighlight.contains(point)) {
                                allMovable = false;
                            }
                        }
                    }
                }

                if (allMovable) {
                    for (Point p : pointsToHighlight) {
                        Label l = p.l;
                        if (l.cluster != null) {
                            l.cluster.setLabelCoordinates(l.cluster.x + dX, l.cluster.y + dY);
                            l.cluster.setMovable(false);
                        } else {
                            double newX = l.getX() + dX;
                            double newY = l.getY() + dY;
                            if (l.staysInside(newX, newY)
                                    || !l.staysInside(l.getX(), l.getY())) {
                                l.setCoordinates(newX, newY, labels.values());
                                l.setAutomaticMovable(false);
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(externalFrame,
                            "Some of the highlighted labels belong to groups. Ungroup them before moving separately.",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
            last = endPoint;

            repaintOnResize();

        }
    }

    public boolean markLabel(java.awt.Point sp) {
        last = sp;
        boolean selectedNew = false;
        selectedLabel = null;
        for (Label l : labels.values()) {
            if (l.getX() <= sp.x && l.getX() + l.label.getWidth() >= sp.x
                    && l.getY() >= sp.y && l.getY() - l.label.getHeight() <= sp.y) {
                if (selectedLabel == null) {
                    selectedLabel = l;
                }
            }
        }
        if (selectedLabel != null && !selectedLabel.p.isSelected()) {
            pointsToHighlight.clear();
            pointsToHighlight.add(selectedLabel.p);
            selectedNew = true;
        }
        return selectedNew;
    }

    void markPoint(java.awt.Point sp) {
        selectedPoint = null;
        int thr = 1;
        Iterator<Integer> labelIdIt = labels.keySet().iterator();
        while (labelIdIt.hasNext()) {
            int id = labelIdIt.next();
            Point p = points.get(id);
            if (p.getX() - p.width / 2 - thr <= sp.x
                    && p.getX() + p.width / 2 + thr >= sp.x
                    && p.getY() + p.height / 2 + thr >= sp.y
                    && p.getY() - p.height - thr <= sp.y) {
                if (selectedPoint == null) {
                    selectedPoint = p;
                }
            }
        }
        if (selectedPoint != null) {
            pointsToHighlight.clear();
            pointsToHighlight.add(selectedPoint);
        }
    }

    boolean isOnPoint() {
        return selectedPoint != null;
    }

    void moveTheVertex(java.awt.Point directionPoint) {
        if (selectedPoint != null) {
            Vertex v = vertices.get(selectedPoint.id);
            if (v.getElist().size() == 1) {
                State state = startState();
                state.vertexPositions.put(v, new double[]{v.getX(), v.getY()});

                Edge e = v.getFirstEdge();
                Vertex c = e.getOther(v);

                Point p2 = points.get(c.getNxnum());

                double angle = AngleCalculatorSimple.getClockwiseAngle(
                        new Vertex(directionPoint.x, directionPoint.y),
                        new Vertex(p2.getX(), p2.getY()),
                        new Vertex(selectedPoint.getX(), selectedPoint.getY()));

                double xt = v.getX() - c.getX();
                double yt = v.getY() - c.getY();

                v.setCoordinates(xt * Math.cos(angle) - yt * Math.sin(angle) + c.getX(),
                        xt * Math.sin(angle) + yt * Math.cos(angle) + c.getY());

                currentState.vertexPositions.put(v, new double[]{v.getX(), v.getY()});
                repaintOnResize();
            }
        }
    }

    void formatSelectedNodes(Color fg, boolean changeFg,
                             Color bg, boolean changeBg,
                             String shape, boolean changeShape,
                             int size, boolean changeSize,
                             int fSize, boolean changeFSize,
                             boolean bold, boolean changeBold,
                             boolean italic, boolean changeItalic) {
        if (!pointsToHighlight.isEmpty()) {
            State state = startState();

            for (Point p : pointsToHighlight) {
                if (changeShape) {
                    switch (shape) {
                        case "square":
                            p.round = false;
                            p.v.setShape("r");
                            break;
                        case "circle":
                            p.round = true;
                            p.v.setShape(null);
                            break;
                    }
                }
                if (changeSize) {
                    p.setSize(size);
                }
                if (changeBg) {
                    p.setBg(bg);
                }
                if (changeFg) {
                    p.setFg(fg);
                }
                if (changeFSize) {
                    p.l.label.setFontSize(fSize);
                }
                if (changeBold) {
                    p.l.label.makeBold(bold);
                }
                if (changeItalic) {
                    p.l.label.makeItalic(italic);
                }
            }
            repaint();
        }
    }

    void showTrivial(boolean show) {
        if (show != network.trivialVisible()) {
            network.showTrivial(show);
            Iterator<Label> labelIt = labels.values().iterator();
            while (labelIt.hasNext()) {
                Label l = labelIt.next();
                if (show) {
                    l.p.suppressed = false;
                } else {
                    Vertex v = vertices.get(l.p.id);
                    if (v.getElist().size() == 1) {
                        Point p2 = points.get(v.getFirstEdge().getOther(v).getNxnum());
                        l.p.suppress(p2);
                    }
                }
            }
            repaintOnResize();
        }
    }

    private void findCornerPoints() {
        Iterator<Point> pIt = points.values().iterator();
        minX = null;
        minY = null;
        maxX = null;
        maxY = null;
        while (pIt.hasNext()) {
            Point p = pIt.next();
            if (!p.suppressed) {
                Vertex v = vertices.get(p.id);
                if (minX == null || minX > v.getX()) {
                    minX = v.getX();
                    pMinX = p;
                }
                if (maxX == null || maxX < v.getX()) {
                    maxX = v.getX();
                    pMaxX = p;
                }
                if (minY == null || minY > v.getY()) {
                    minY = v.getY();
                    pMinY = p;
                }
                if (maxY == null || maxY < v.getY()) {
                    maxY = v.getY();
                    pMaxY = p;
                }
            }
        }
    }

    void compareTo(Network network2) {
        Map<Integer, boolean[]> splits1 = network.getSplits();
        Map<Integer, boolean[]> splits2 = network2.getSplits();
        List<Integer> splitsToBold = new LinkedList();

        Iterator<Integer> it2 = splits2.keySet().iterator();
        while (it2.hasNext()) {
            int i = it2.next();
            boolean[] split2 = splits2.get(i);

            Iterator<Integer> it1 = splits1.keySet().iterator();
            while (it1.hasNext()) {
                int j = it1.next();
                if (!splitsToBold.contains(j)) {
                    boolean[] split1 = splits1.get(j);
                    int same = 0;
                    for (int k = 0; k < split1.length; k++) {
                        if (split1[k] == split2[k]) {
                            same++;
                        }
                    }
                    if ((split1.length - same) <= 0) {
                        splitsToBold.add(j);
                        //break;
                    }
                }
            }
        }

        for (int i = 0; i < edges.size(); i++) {
            Edge e = edges.get(i);
            if (splitsToBold.contains(e.getIdxsplit())) {
                lines.get(e.getNxnum()).width += 1;
            }
        }
        repaint();

        System.out.println("Same splits: " + splitsToBold.size());
    }

    void activateRotation(boolean rotate) {
        this.rotate = rotate;
        if (!rotate) {
            lastPoint = null;
        }
        repaint();
    }

    void rotate(java.awt.Point startPoint, java.awt.Point endPoint) {
        startPoint = (lastPoint != null) ? lastPoint : startPoint;
        double angle = AngleCalculatorSimple.getClockwiseAngle(
                new Vertex(endPoint.x, endPoint.y),
                new Vertex(midX, midY),
                new Vertex(startPoint.getX(), startPoint.getY()));

        double vX = (midX - deltaX) / ratio + minX;
        double vY = (midY - deltaY) / ratio + minY;

        double bX = midX;
        double bY = midY;

        for (Vertex v : vertices.values()) {
            double xt = v.getX() - vX;
            double yt = v.getY() - vY;

            v.setCoordinates(xt * Math.cos(angle) - yt * Math.sin(angle) + vX,
                    xt * Math.sin(angle) + yt * Math.cos(angle) + vY);

            Label l = labels.get(v.getNxnum());
            if (l != null && !l.label.movable && l.cluster == null) {
                xt = l.middleX() - bX;
                yt = l.middleY() - bY;

                Point p = l.p;
                double pX = p.getX() - bX;
                double pY = p.getY() - bY;

                double npX = pX * Math.cos(angle) - pY * Math.sin(angle) + bX;
                double npY = pX * Math.sin(angle) + pY * Math.cos(angle) + bY;

                p.setX(npX);
                p.setY(npY);

                double nX = xt * Math.cos(angle) - yt * Math.sin(angle) + bX;
                double nY = xt * Math.sin(angle) + yt * Math.cos(angle) + bY;

                l.setRotated(nX, nY);
            }
        }
        for (Cluster c : clusters) {
            double xt = c.x + c.width / 2 - bX;
            double yt = c.y + c.height / 2 - bY;

            for (Point p : c.points) {
                double pX = p.getX() - bX;
                double pY = p.getY() - bY;

                double npX = pX * Math.cos(angle) - pY * Math.sin(angle) + bX;
                double npY = pX * Math.sin(angle) + pY * Math.cos(angle) + bY;

                p.setX(npX);
                p.setY(npY);
            }

            c.setLabelCoordinates(xt * Math.cos(angle) - yt * Math.sin(angle) - c.width / 2 + bX,
                    xt * Math.sin(angle) + yt * Math.cos(angle) - c.height / 2 + bY);
        }

        lastPoint = endPoint;

        //repaint();
        repaintOnResize();
    }

    void flipNetwork(boolean horizontal) {
        if (network != null) {
            for (Vertex v : vertices.values()) {
                if (horizontal) {
                    v.setX(0 - v.getX());
                    if (v.getLabel() != null) {
                        int width = v.getLabel().getWidth();
                        v.getLabel().setOffsetX(0 - v.getLabel().getOffsetX() - width);
                    }
                } else {
                    v.setY(0 - v.getY());
                    if (v.getLabel() != null) {
                        int height = v.getLabel().getHeight();
                        v.getLabel().setOffsetY(0 - v.getLabel().getOffsetY() + height);
                    }
                }
            }
            for (Label label : labels.values()) {
                label.computeMiddleDistance();
            }
            repaintOnResize();
        }
    }

    private Cluster getClusterThatAllPointsBelongTo(Set<Point> pointsToCheck) {
        Iterator<Point> pointIt = pointsToCheck.iterator();
        Point p = pointIt.next();
        if (clusters != null) {
            Iterator<Cluster> clusterIt = clusters.iterator();
            while (clusterIt.hasNext()) {
                Cluster cluster = clusterIt.next();
                if (cluster.points.contains(p)) {
                    int i = 1;
                    while (pointIt.hasNext()) {
                        p = pointIt.next();
                        if (cluster.points.contains(p)) {
                            i++;
                        } else {
                            return null;
                        }
                    }
                    if (i == pointsToCheck.size() && i == cluster.points.size()) {
                        return cluster;
                    }
                }
            }
        }
        return null;
    }

    void selectGroup() {
        Cluster group = null;
        for (Point p : pointsToHighlight) {
            Label label = p.l;
            if (group == null) {
                group = label.cluster;
            } else {
                if (group != label.cluster) {
                    JOptionPane.showMessageDialog(this, "Cannot select group:\n"
                            + "selected labels belong to separate groups.");
                    return;
                }
            }
        }
        if (group != null) {
            pointsToHighlight.clear();
            Iterator<Point> pointIt = group.points.iterator();
            while (pointIt.hasNext()) {
                Point point = pointIt.next();
                pointsToHighlight.add(point);
            }
            repaint();
        }
    }


    private void repositionLabelsInClusters() {
        if (clusters != null) {
            for (Cluster cluster : clusters) {
                if (cluster.leaders) {
                    cluster.setLabelCoordinates(cluster.x, cluster.y);
                }
            }
        }
    }

    void find(String text, boolean regEx) {
        removeSelection();
        Iterator<Integer> idIt = labels.keySet().iterator();
        while (idIt.hasNext()) {
            Integer id = idIt.next();
            Label lab = labels.get(id);
            if (regEx) {
                Pattern pattern = Pattern.compile(text);
                Matcher matcher = pattern.matcher(lab.name);
                if (matcher.find()) {
                    pointsToHighlight.add(lab.p);
                }
            }
            if (lab.name.contains(text)) {
                pointsToHighlight.add(lab.p);
            }
        }
        repaint();
    }

    private void removeSelection() {
        pointsToHighlight.clear();
    }

    void makeGroup() {
        if (!pointsToHighlight.isEmpty()) {
            Object[] options = {"Keep current",
                    "Assign random",
                    "Select new color"};
            int n = JOptionPane.showOptionDialog(externalFrame,
                    "Change coloring?",
                    "",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[2]);
            boolean changeColor = (n != JOptionPane.YES_OPTION);
            if (n >= 0) {
                Color color = null;
                if (n == JOptionPane.CANCEL_OPTION) {
                    color = JColorChooser.showDialog(this,
                            "Cluster color",
                            null);
                }
                if (n != JOptionPane.CANCEL_OPTION || color != null) {
                    Cluster newCluster = new Cluster();
                    if (color != null) {
                        newCluster.color = color;
                    }

                    Set<Cluster> affectedClusters = new HashSet();
                    affectedClusters.add(newCluster);
                    clusters.add(newCluster);
                    for (Point p : pointsToHighlight) {
                        Label label = p.l;
                        if (label.cluster != null) {
                            affectedClusters.add(label.cluster);
                        }
                        newCluster.add(label.p, label, changeColor);
                    }

                    for (Cluster cluster : affectedClusters) {
                        if (cluster.points.isEmpty()) {
                            clusters.remove(cluster);
                        }
                    }
                }
                resetLabelPositions(false);
                repaint();
            }
            repaint();
        }
    }

    void undo() {
        if (!undo.isEmpty()) {
            redo.add(currentState);
            currentState = undo.pop();
            currentState.returnTo();
        }
    }

    void redo() {
        if (!redo.isEmpty()) {
            undo.add(currentState);
            currentState = redo.pop();
            currentState.returnTo();
        }
    }

    private State startState() {
        State state;
        if (!undo.isEmpty()) {
            state = currentState;
        } else {
            state = new State();
        }
        currentState = new State();
        undo.add(state);
        if (!redo.isEmpty()) {
            redo.clear();
        }
        return state;
    }


    boolean isSelected() {
        return !pointsToHighlight.isEmpty();
    }

    private void findMaxOffsets() {
        maxOffX = 0;
        maxOffY = 0;

        if (pMaxX.getX() != 0 && pMaxY.getY() != 0) {
            for (Label l : labels.values()) {
                if (!l.label.movable || l.cluster != null) {
                    double offXMi = l.getX() - pMinX.getX() + maxLabelW + extraSpace;
                    double offXMa = pMaxX.getX() - (l.getX() + l.label.getWidth()) + maxLabelW + extraSpace;
                    double offX = (offXMi < offXMa) ? offXMi : offXMa;
                    if (offX < 0) {
                        offX = 0 - offX;
                        if (maxOffX < offX) {
                            maxOffX = offX;
                        }
                    }

                    double offYMi = l.getY() - (pMinY.getY() + l.label.getHeight()) + maxLabelH + extraSpace;
                    double offYMa = pMaxY.getY() - l.getY() + maxLabelH + extraSpace;
                    double offY = (offYMi < offYMa) ? offYMi : offYMa;
                    if (offY < 0) {
                        offY = 0 - offY;
                        if (maxOffY < offY) {
                            maxOffY = offY;
                        }
                    }
                }
            }
        }
    }

    void selectAll() {
        pointsToHighlight.clear();
        for (Label l : labels.values()) {
            pointsToHighlight.add(l.p);
        }
        repaint();
    }

    public Map<Integer, Label> getLabels() {
        return labels;
    }

    private void scaleCoordinates(Double savedRatio) {
        if (savedRatio != null) {
            this.ratio = savedRatio;
        } else {
            computeRatio();
        }

        network.setRatio(ratio);
        deltaX = getWidth() / 2 - computeX((maxX + minX) / 2, 0);
        deltaY = getHeight() / 2 - computeY((maxY + minY) / 2, 0);

        for (Point p : points.values()) {
            p.setX(computeX(p.v.getX(), deltaX));
            p.setY(computeY(p.v.getY(), deltaY));
            if (change != 0 && p.l != null && !p.selected) {
                if (change != 1) {
                    double offX = p.l.label.getOffsetX() * change;
                    if (offX == 0) {
                        offX = p.l.label.getOffsetX();
                    }

                    double offY = p.l.label.getOffsetY() * change;
                    if (offY == 0) {
                        offY = p.l.label.getOffsetY();
                    }

                    p.l.label.setOffsetX(offX);
                    p.l.label.setOffsetY(offY);
                }
            }
        }
    }

    private void computeRatio() {
        double lastRatio = ratio;

        double width = (getWidth() / 2.0 - 20.0 > sideMargin) ? (getWidth() - 2.0 * sideMargin) : 40;
        double height = (getHeight() / 2.0 - 20.0 > heightMargin) ? (getHeight() - 2.0 * heightMargin) : 40;

        ratio = Math.min(width / Math.abs(maxX - minX),
                height / Math.abs(maxY - minY));

        if (ratio <= 0) {
            ratio = 0;
        }

        externalFrame.setRatio(ratio);
        change = (lastRatio * ratio == 0) ? 1 : ratio / lastRatio;
    }

    private int checkOffset(int l, int pMi, int pMa, int maxOff) {
        int diffMin = l - pMi;
        int diffMax = pMa - l;
        int minDiff = (diffMin < diffMax) ? diffMin : diffMax;
        if (minDiff < 0) {
            int off = Math.abs(minDiff);
            if (maxOff < off) {
                maxOff = off;
            }
        }

        return maxOff;
    }

    private void rescale() {
        if (vertices != null) {
            if (externalFrame.showLabels()) {
                findCornerPointsLabelsIncluded();
            } else {
                findCornerPoints();
            }
            rescaleCoordinates();
            resetLabelPositions(false);
        }
    }

    private void findCornerPointsLabelsIncluded() {
        if (points != null) {
            minX = null;
            maxX = null;
            minY = null;
            maxY = null;
            for (Point p : points.values()) {
                double xMi = p.getX() - p.getSize() / 2;
                double xMa = p.getX() + p.getSize() / 2;
                double yMi = p.getY() - p.getSize() / 2;
                double yMa = p.getY() + p.getSize() / 2;

                if (p.l != null) {
                    if (p.l.label.getOffsetX() < 0) {
                        xMi = p.l.getX();
                    }
                    double mxl = p.l.getX() + p.l.label.getWidth();
                    if (mxl > xMa) {
                        xMa = mxl;
                    }

                    if (p.l.label.getOffsetY() > 0) {
                        yMa = p.l.getY();
                    }
                    double myl = p.l.getY() - p.l.label.getHeight();
                    if (myl < yMi) {
                        yMi = myl;
                    }
                }

                if (minX == null || minX > xMi) {
                    minX = xMi;
                }
                if (maxX == null || maxX < xMa) {
                    maxX = xMa;
                }
                if (minY == null || minY > yMi) {
                    minY = yMi;
                }
                if (maxY == null || maxY < yMa) {
                    maxY = yMa;
                }
            }
        }
    }

    private void rescaleCoordinates() {
        ratio = Math.min(getWidth() / (maxX - minX), getHeight() / (maxY - minY));
        network.setRatio(ratio);

        Iterator<Integer> idIt = points.keySet().iterator();
        while (idIt.hasNext()) {
            int id = idIt.next();
            Point p = points.get(id);
            double nX = (p.getX() - minX) * ratio;
            double nY = (p.getY() - minY) * ratio;
            p.setX(nX);
            p.setY(nY);
            if (p.l != null)// && !labelsToHighlight.contains(p.l))
            {
                p.l.label.setOffsetX((p.l.label.getOffsetX() * ratio));
                p.l.label.setOffsetY((p.l.label.getOffsetY() * ratio));
            }
        }
    }

    void removeFromGroup() {
        for (Point p : pointsToHighlight) {
            Label label = p.l;
            if (label.cluster != null) {
                label.cluster.remove(label);
            }
        }
        for (Cluster cluster : clusters) {
            if (cluster.isEmpty()) {
                clusters.remove(cluster);
            }
        }
    }

    void fixLabels(boolean fix) {
        for (Label label : labels.values()) {
            if (label.cluster != null) {
                label.cluster.setMovable(fix);
            } else {
                label.label.movable = fix;
            }
        }
    }

    private class State {

        Map<Label, int[]> labelPositions;
        Map<Vertex, double[]> vertexPositions;
        Map<Point, Color> pointColorsBg;
        Map<Point, Color> pointColorsFg;

        String leaderType;

        public State() {
            labelPositions = new HashMap();
            vertexPositions = new HashMap();
            pointColorsBg = new HashMap();
            pointColorsFg = new HashMap();
            leaderType = null;
        }

        void returnTo() {
            for (Map.Entry<Label, int[]> entry : labelPositions.entrySet()) {
                int[] xy = entry.getValue();
                entry.getKey().setCoordinates(xy[0], xy[1]);
            }

            for (Map.Entry<Vertex, double[]> entry : vertexPositions.entrySet()) {
                double[] xy = entry.getValue();
                entry.getKey().setCoordinates(xy[0], xy[1]);
            }

            for (Map.Entry<Point, Color> entry : pointColorsBg.entrySet()) {
                entry.getKey().setBg(entry.getValue());
            }

            for (Map.Entry<Point, Color> entry : pointColorsFg.entrySet()) {
                entry.getKey().setFg(entry.getValue());
            }

            if (vertexPositions.isEmpty()) {
                repaint();
            } else {
                repaintOnResize();
            }
        }
    }
}