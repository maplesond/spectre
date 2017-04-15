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

package uk.ac.uea.cmp.spectre.viewer;

import uk.ac.uea.cmp.spectre.core.ds.network.Edge;
import uk.ac.uea.cmp.spectre.core.ds.network.Network;
import uk.ac.uea.cmp.spectre.core.ds.network.Vertex;
import uk.ac.uea.cmp.spectre.core.ds.network.draw.Leader;
import uk.ac.uea.cmp.spectre.core.ds.network.draw.ViewerConfig;
import uk.ac.uea.cmp.spectre.core.ui.gui.geom.Leaders;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author balvociute
 */
public class Window extends JPanel implements KeyListener, ComponentListener {
    private ViewerPoint selectedPoint;
    private Set<Cluster> clusters = new HashSet<>();
    double maxLabelH;
    double maxLabelW;
    private double maxOffX = 0.0;
    private double maxOffY = 0.0;

    private int offsetX;
    private int offsetY;

    private java.awt.Point last = null;
    private Point startPoint;

    private double change;
    private final int extraSpace = 20;
    private final int maximalLabelWidth = 40;

    // Maps for keys
    private InputMap inputmap = this.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
    private ActionMap actionmap = this.getActionMap();

    // Popup menu which occurs on right click
    private javax.swing.JPopupMenu popupMenu;

    // Leave public so external frame can modify based on menu item selections
    public ViewerConfig config;


    public Window() {

        // Setup default configuration
        this.config = new ViewerConfig();

        this.offsetX = 0;
        this.offsetY = 0;

        // Create popup menu
        preparePopupMenu();

        // Setup listeners
        setupKeyListeners();
        setupMouseListeners();
    }

    private void preparePopupMenu() {

        popupMenu = new javax.swing.JPopupMenu();

        JMenuItem copySelectedTaxa = new JMenuItem("Copy selected labels");
        copySelectedTaxa.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C,
                java.awt.Event.CTRL_MASK));
        copySelectedTaxa.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                copySelectedTaxa();
            }
        });
        popupMenu.add(copySelectedTaxa);

        JMenuItem selectGroup = new JMenuItem("Select group");
        selectGroup.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G,
                java.awt.Event.CTRL_MASK));
        selectGroup.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selectGroup();
            }
        });
        popupMenu.add(selectGroup);

        JMenuItem group = new JMenuItem("Group selected");
        group.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                makeGroup();
            }
        });
        popupMenu.add(group);

        JMenuItem remove = new JMenuItem("Remove from group");
        remove.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                removeFromGroup();
            }
        });
        popupMenu.add(remove);
    }


    private void setupKeyListeners() {
        this.addKeyListener(this);

        final String rightKeyPressed = "right arrow pressed";
        inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), rightKeyPressed);
        actionmap.put(rightKeyPressed, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                //pan(true);
            }
        });

        final String leftKeyPressed = "left arrow pressed";
        inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), leftKeyPressed);
        actionmap.put(leftKeyPressed, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                //pan(true);
            }
        });

        final String upKeyPressed = "up arrow pressed";
        inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), upKeyPressed);
        actionmap.put(upKeyPressed, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                //pan(true);
            }
        });

        final String downKeyPressed = "down arrow pressed";
        inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), downKeyPressed);
        actionmap.put(downKeyPressed, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                //pan(true);
            }
        });
    }

    private void setupMouseListeners() {

        // **** Mouse Listeners ****

        this.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                zoom(evt.getPreciseWheelRotation() * config.getRatio() / 50.0);
            }
        });

        this.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                java.awt.Point clickedPoint = evt.getPoint();
                if (SwingUtilities.isRightMouseButton(evt)) {
                    popupMenu.show(evt.getComponent(), clickedPoint.x, clickedPoint.y);
                } else {
                    setSelection(clickedPoint, evt.isControlDown() || evt.isShiftDown());
                }
            }

            public void mouseDragged(java.awt.event.MouseEvent evt) {
                if (SwingUtilities.isRightMouseButton(evt)) {
                    rotate(startPoint, evt.getPoint());
                }
                else if (SwingUtilities.isLeftMouseButton(evt)) {
                    if (evt.isShiftDown() || evt.isControlDown()) {
                        setSelection(startPoint,
                                evt.getPoint(),
                                true);
                    }
                    else if (isOnLabel(evt.getPoint())) {
                        moveLabels(evt.getPoint());
                    }
                    else if (isOnPoint()) {
                        moveTheVertex(evt.getPoint());
                    }
                    else {
                        pan(startPoint, evt.getPoint());
                    }
                }
            }

            public void mousePressed(java.awt.event.MouseEvent evt) {

                if (network != null) {
                    startPoint = evt.getPoint();
                    if (SwingUtilities.isRightMouseButton(evt)) {
                        activateRotation(true);
                        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                    } else if (SwingUtilities.isLeftMouseButton(evt)) {
                        if (evt.isShiftDown() || evt.isControlDown()) {
                            selectmode = true;
                        } else if (isOnLabel(evt.getPoint())) {
                            markLabel(startPoint);
                        } else if (isOnPoint()) {
                            markPoint(startPoint);
                        } else {
                            // Do panning
                            panmode = true;
                            setCursor(new Cursor(Cursor.MOVE_CURSOR));
                        }
                    }
                }
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                if (network != null) {
                    activateRotation(false);
                    removeSelectionRectangle();
                    panmode = false;
                    selectmode = false;
                    setCursor(Cursor.getDefaultCursor());
                    startPoint = null;
                }
            }
        });

        this.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                if (network != null) {
                    if (SwingUtilities.isRightMouseButton(evt)) {
                        rotate(startPoint, evt.getPoint());
                    } else if (SwingUtilities.isLeftMouseButton(evt)) {
                        if (evt.isShiftDown() || evt.isControlDown()) {
                            setSelection(startPoint,
                                    evt.getPoint(),
                                    true);
                        } else if (isOnLabel(evt.getPoint())) {
                            moveLabels(evt.getPoint());
                        } else if (isOnPoint()) {
                            moveTheVertex(evt.getPoint());
                        } else {
                            pan(startPoint, evt.getPoint());
                        }
                    }
                }
            }
        });

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                midY = getHeight() / 2;
                midX = getWidth() / 2;

                repaintOnResize();
            }
        });



    }

    private void setupMouseListener() {

    }

    History history = new History();

    NetworkExtents dimensions;
    double rotationAngle = 0.0;
    List<Edge> edges;
    Network network;
    Leaders leaders = new Leaders();
    Map<Integer, ViewerPoint> points = new HashMap<>();
    Map<Integer, Line> lines = new HashMap<>();
    Map<Integer, ViewerLabel> labels = new HashMap<>();
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
    Set<ViewerPoint> pointsToHighlight = new SelectableSet();
    Set<Line> linesToHightlight = new HashSet();
    Color selectionColor = Color.RED;
    ViewerLabel selectedLabel = null;
    boolean rotatemode = false;
    boolean selectmode = false;
    boolean panmode = false;

    java.awt.Point lastPoint = null;
    ClusterFinder cf = new ClusterFinderSplits();
    ClusterPlacementOptimizer cpo = new ClusterPlacementOptimizerBox();

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        setBackground(Color.white);

        Graphics2D g2 = (Graphics2D) g;

        if (lines != null) {
            for (Line l : lines.values()) {
                g2.setStroke(new BasicStroke(l.getWidth()));
                g.setColor(l.fg);
                g2.drawLine(l.p1.getXint(), l.p1.getYint(), l.p2.getXint(), l.p2.getYint());
            }
        }

        if (labels != null && labels.size() > 0 && config.showLabels() && config.leadersVisible()) {
            g2.setStroke(config.getLeaderStroke().getStroke());

            for (ViewerLabel l : labels.values()) {
                if (l.leader) {
                    if (l.p.isSelected()) {
                        g.setColor(Color.red);
                    } else {
                        g.setColor(this.config.getLeaderColor());
                    }
                    if (config.getLeaderType() == Leader.LeaderType.STRAIGHT) {
                        g.drawLine(l.p.getXint(), l.p.getYint(), l.getlX(), l.getlY());
                    } else {
                        ViewerPoint bentPoint = l.computeBendingPoint(midX, midY, config.getLeaderType() == Leader.LeaderType.BENDED);
                        int[] x = new int[]{l.p.getXint(), bentPoint.getXint(), l.getlX()};
                        int[] y = new int[]{l.p.getYint(), bentPoint.getYint(), l.getlY()};
                        g.drawPolyline(x, y, 3);
                    }
                }
            }
        }

        g2.setStroke(new BasicStroke());
        if (points != null && points.size() > 0) {
            for (ViewerPoint p : points.values()) {
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
        if (labels != null && labels.size() > 0 && config.showLabels()) {
            boolean colorLabels = config.colorLabels();
            for (ViewerLabel l : labels.values()) {
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
                    textColor = getTextColor(l.p.v.getBackgroundColor());
                }
                g.setColor(textColor);
                g.drawString(l.name, lx, ly - 1);
            }
        }

        // Draw the range indicator in the top left
        if (config.showRange()) {

            int delta = (int)(config.getRatio());
            double size = 1.0;

            while (delta < 30) {
                delta *= 10;
                size *= 10.0;
            }
            while (delta > 300) {
                delta /= 10;
                size /= 10.0;
            }

            int left = 20;
            int right = 20 + delta;

            g.drawLine(left, 22, left, 18);
            g.drawLine(right, 22, right, 18);
            g.drawLine(left, 20, right, 20);
            g.drawString(Double.toString(size), left + ((right - left) / 2) - 10, 32);
        }

        // Highlight mouse drag window when in selection mode
        if (selectionRectangle != null) {
            g.setColor(Color.red);
            g.drawRect(selectionRectangle[0], selectionRectangle[1], selectionRectangle[2], selectionRectangle[3]);
        }

        // Draw a blue crosshair in centre of screen to indicate we are in rotate mode
        if (rotatemode) {
            g.setColor(Color.BLUE);

            int cSize = 8;
            int lLength = 16;

            g.drawOval(midX - cSize / 2, midY - cSize / 2, cSize, cSize);
            g.drawLine(midX - lLength / 2, midY, midX + lLength / 2, midY);
            g.drawLine(midX, midY - lLength / 2, midX, midY + lLength / 2);
        }


    }

    public void drawNetwork(final ViewerConfig config, Network network) {
        this.network = network;
        this.config = config;
        this.setGraph();
        this.showTrivial(config.showTrivial());
        this.recomputeRatio();
        //this.rescale();
        this.repaintOnResize();
    }

    private static Color getTextColor(Color bg) {
        if (bg.getRed() <= 50 && bg.getGreen() <= 50 && bg.getBlue() <= 50) {
            return Color.white;
        } else {
            return Color.black;
        }
    }

    private void computeIntegerCoordinates() {

        if (vertices != null) {
            this.dimensions = NetworkExtents.determineRange(this.points.values(), this.vertices);
            //scaleCoordinates();
            if (config.showLabels()) {
                sideMargin = sideMarginSaved + maxOffX;
                heightMargin = heightMarginSaved + maxOffY;
            } else {
                sideMargin = extraSpace;
                heightMargin = extraSpace;
            }
            scaleCoordinates();
        }

        resetLabelPositions(false);

        if (lines != null) {
            for (Line line : lines.values()) {
                line.computeAB();
            }
        }

        repaint();
    }


    public void setGraph() {
        clusters.clear();
        points.clear();
        lines.clear();
        labels.clear();
        vertices.clear();

        List<Vertex> verticesList = network.getAllVertices();

        for (Vertex v : verticesList) {
            vertices.put(v.getNxnum(), v);
        }

        if (rotationAngle != 0) {
            rotate(verticesList, rotationAngle);
        }

        heightMargin = 0;
        sideMargin = 0;
        heightMarginSaved = 0;
        sideMarginSaved = 0;

        for (Vertex v : verticesList) {
            int id = v.getNxnum();
            vertices.put(id, v);
            ViewerPoint p = new ViewerPoint(v);
            points.put(id, p);

            if (v.getLabel() != null) {
                ViewerLabel l = new ViewerLabel(v.getLabel(), this);
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

        for (ViewerLabel l : labels.values()) {
            l.checkForLeader();
        }

        maxLabelH = heightMargin;
        maxLabelW = sideMargin;

        heightMargin += extraSpace;
        sideMargin += extraSpace;

        heightMarginSaved = heightMargin;
        sideMarginSaved = sideMargin;

        edges = network.getAllEdges();
        for (Edge e : edges) {
            Line l = new Line(e);
            l.setPoints(points.get(e.getBottom().getNxnum()),
                    points.get(e.getTop().getNxnum()));
            lines.put(e.getNxnum(), l);
        }

        computeIntegerCoordinates();
    }

    private int computeX(double x, int delta) {
        return (int) ((x - this.dimensions.vertices.minX) * config.getRatio()) + delta;
    }

    private int computeY(double y, int delta) {
        return (int) ((y - this.dimensions.vertices.minY) * config.getRatio()) + delta;
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

    public void resetLabelPositions(boolean all) {

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

            for (ViewerLabel l : labels.values()) {
                ViewerPoint p = l.p;
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
            computeIntegerCoordinates();
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
            for (ViewerPoint p : pointsToHighlight) {
                ViewerLabel l = p.l;
                selectedTaxa += l.name + "\n";
            }
        }
        Clipboard system = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection sel = new StringSelection(selectedTaxa);
        system.setContents(sel, sel);
    }

    boolean isOnLabel(Point sp) {
        for (ViewerLabel l : labels.values()) {
            if (l.getX() <= sp.x && l.getX() + l.label.getWidth() >= sp.x
                    && l.getY() >= sp.y && l.getY() - l.label.getHeight() <= sp.y) {
                return true;
            }
        }
        return false;
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
                for (ViewerPoint p : pointsToHighlight) {
                    ViewerLabel l = p.l;
                    if (l.cluster != null && l.cluster.size() > 1) {
                        for (ViewerPoint point : l.cluster.points) {
                            if (!pointsToHighlight.contains(point)) {
                                allMovable = false;
                            }
                        }
                    }
                }

                if (allMovable) {
                    for (ViewerPoint p : pointsToHighlight) {
                        ViewerLabel l = p.l;
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
                    JOptionPane.showMessageDialog(this.getParent(),
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
        for (ViewerLabel l : labels.values()) {
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
            ViewerPoint p = points.get(id);
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
            if (v.getEdgeList().size() == 1) {
                State state = this.history.startState();
                state.vertexPositions.put(v, new double[]{v.getX(), v.getY()});

                Edge e = v.getFirstEdge();
                Vertex c = e.getOther(v);

                ViewerPoint p2 = points.get(c.getNxnum());

                double angle = Vertex.getClockwiseAngle(
                        new Vertex(directionPoint.x, directionPoint.y),
                        new Vertex(p2.getX(), p2.getY()),
                        new Vertex(selectedPoint.getX(), selectedPoint.getY()));

                double xt = v.getX() - c.getX();
                double yt = v.getY() - c.getY();

                v.setCoordinates(xt * Math.cos(angle) - yt * Math.sin(angle) + c.getX(),
                        xt * Math.sin(angle) + yt * Math.cos(angle) + c.getY());

                history.currentState.vertexPositions.put(v, new double[]{v.getX(), v.getY()});
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
            State state = this.history.startState();

            for (ViewerPoint p : pointsToHighlight) {
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
        if (show != config.showTrivial()) {
            config.setShowTrivial(show);
            Iterator<ViewerLabel> labelIt = labels.values().iterator();
            while (labelIt.hasNext()) {
                ViewerLabel l = labelIt.next();
                if (show) {
                    l.p.suppressed = false;
                } else {
                    Vertex v = vertices.get(l.p.id);
                    if (v.getEdgeList().size() == 1) {
                        ViewerPoint p2 = points.get(v.getFirstEdge().getOther(v).getNxnum());
                        l.p.suppress(p2);
                    }
                }
            }
            repaintOnResize();
        }
    }

    void showRange(boolean show) {
        if (show != config.showRange()) {
            config.setShowRange(show);
            repaint();
        }
    }

    void activateRotation(boolean rotate) {
        this.rotatemode = rotate;
        if (!rotate) {
            lastPoint = null;
        }
        repaint();
    }

    void rotate(java.awt.Point startPoint, java.awt.Point endPoint) {
        startPoint = (lastPoint != null) ? lastPoint : startPoint;
        final double angle = Vertex.getClockwiseAngle(
                new Vertex(endPoint.x, endPoint.y),
                new Vertex(midX, midY),
                new Vertex(startPoint.getX(), startPoint.getY()));
        rotate(angle);
        lastPoint = endPoint;
    }


    /**
     * When rotating actually modify the real vertex values
     * @param angle
     */
    void rotate(final double angle) {

        double vX = (midX - deltaX) / config.getRatio() + dimensions.vertices.minX;
        double vY = (midY - deltaY) / config.getRatio() + dimensions.vertices.minY;

        double bX = midX;
        double bY = midY;

        for (Vertex v : vertices.values()) {
            double xt = v.getX() - vX;
            double yt = v.getY() - vY;

            v.setCoordinates(xt * Math.cos(angle) - yt * Math.sin(angle) + vX,
                    xt * Math.sin(angle) + yt * Math.cos(angle) + vY);

            ViewerLabel l = labels.get(v.getNxnum());
            if (l != null && !l.label.movable && l.cluster == null) {
                xt = l.middleX() - bX;
                yt = l.middleY() - bY;

                ViewerPoint p = l.p;
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

            for (ViewerPoint p : c.points) {
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

        repaintOnResize();
    }

    void pan(java.awt.Point startPoint, java.awt.Point endPoint) {

        /*double dX = (endPoint.x - startPoint.x);
        double dY = (endPoint.y - startPoint.y);

        for (Vertex v : vertices.values()) {
            v.setCoordinates(v.getX() + dX, v.getY() + dY);
        }
        for (Cluster c : clusters) {
            for (ViewerPoint p : c.points) {
                p.setX(p.getX() + dX);
                p.setY(p.getY() + dY);
            }
            c.setLabelCoordinates(c.x + dX, c.y + dY);
        }
        */
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
            for (ViewerLabel label : labels.values()) {
                label.computeMiddleDistance();
            }

            repaintOnResize();
        }
    }

    private Cluster getClusterThatAllPointsBelongTo(Set<ViewerPoint> pointsToCheck) {
        Iterator<ViewerPoint> pointIt = pointsToCheck.iterator();
        ViewerPoint p = pointIt.next();
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
        for (ViewerPoint p : pointsToHighlight) {
            ViewerLabel label = p.l;
            if (group == null) {
                group = label.cluster;
            } else {
                if (group != label.cluster) {
                    JOptionPane.showMessageDialog(this.getParent(), "Cannot select group:\n"
                            + "selected labels belong to separate groups.");
                    return;
                }
            }
        }
        if (group != null) {
            pointsToHighlight.clear();
            Iterator<ViewerPoint> pointIt = group.points.iterator();
            while (pointIt.hasNext()) {
                ViewerPoint point = pointIt.next();
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
            ViewerLabel lab = labels.get(id);
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
            int n = JOptionPane.showOptionDialog(this.getParent(),
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
                    for (ViewerPoint p : pointsToHighlight) {
                        ViewerLabel label = p.l;
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


    void removeFromGroup() {
        for (ViewerPoint p : pointsToHighlight) {
            ViewerLabel label = p.l;
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


    boolean isSelected() {
        return !pointsToHighlight.isEmpty();
    }


    void selectAll() {
        pointsToHighlight.clear();
        for (ViewerLabel l : labels.values()) {
            pointsToHighlight.add(l.p);
        }
        repaint();
    }

    public Map<Integer, ViewerLabel> getLabels() {
        return labels;
    }

    private void scaleCoordinates() {

        deltaX = getWidth() / 2 - computeX(dimensions.vertices.width() / 2, offsetX);
        deltaY = getHeight() / 2 - computeY(dimensions.vertices.height() / 2, offsetY);

        for (ViewerPoint p : points.values()) {
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

    private void recomputeRatio() {

        final double lastRatio = config.getRatio();

        double width = (getWidth() / 2.0 - 20.0 > sideMargin) ? (getWidth() - 2.0 * sideMargin) : 40;
        double height = (getHeight() / 2.0 - 20.0 > heightMargin) ? (getHeight() - 2.0 * heightMargin) : 40;

        double newRatio = Math.min(width / Math.abs(dimensions.vertices.width()),
                height / Math.abs(dimensions.vertices.height()));

        change = (lastRatio * newRatio == 0) ? 1 : newRatio / lastRatio;
        config.setRatio(newRatio);
    }

    public void rescale() {

        if (vertices != null) {
            this.dimensions = NetworkExtents.determineRange(points.values(), vertices);
            rescaleCoordinates();
            resetLabelPositions(false);
        }
    }

    private ViewerPoint rescalePoint(ViewerPoint p) {
        final double ratio = config.getRatio();
        p.setX((p.getX() - this.dimensions.vertices.minX) * ratio);
        p.setY((p.getY() - this.dimensions.vertices.minY) * ratio);
        if (p.l != null) {
            p.l.label.setOffsetX((p.l.label.getOffsetX() * ratio));
            p.l.label.setOffsetY((p.l.label.getOffsetY() * ratio));
        }
        return p;
    }

    private void rescaleCoordinates() {

        // Reset ratio to optimal
        config.setRatio(Math.min(getWidth() / this.dimensions.vertices.width(), getHeight() / this.dimensions.vertices.height()));


        for (int id : points.keySet()) {
            rescalePoint(points.get(id));
        }
    }


    void fixLabels(boolean fix) {
        for (ViewerLabel label : labels.values()) {
            if (label.cluster != null) {
                label.cluster.setMovable(fix);
            } else {
                label.label.movable = fix;
            }
        }
    }

    public void zoom(double amount) {

        this.config.setRatio(this.config.getRatio()- amount);
        this.computeIntegerCoordinates();
    }


    // **** Keylistener methods ****

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        this.selectmode = e.isShiftDown();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (this.selectmode) {
            this.selectmode = false;
        }
    }



    // **** Component listener methods ****

    @Override
    public void componentResized(ComponentEvent e) {

    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }

    public void setLeaderType(Leader.LeaderType leaderType) {
        if (leaderType != this.config.getLeaderType()) {
            this.config.setLeaderType(leaderType);
            this.repaint();
        }
    }

    public void setLeaderStroke(Leader.LeaderStroke leaderStroke) {
        if (leaderStroke != this.config.getLeaderStroke()) {
            this.config.setLeaderStroke(leaderStroke);
            this.repaint();
        }
    }

    public void setLeaderColour(Color leaderColour) {
        if (!leaderColour.equals(this.config.getLeaderColor())) {
            this.config.setLeaderColor(leaderColour);
            this.repaint();
        }
    }

    private class History {
        private Stack<State> undo = new Stack();
        private Stack<State> redo = new Stack();
        private State currentState;

        public History() {
            this.undo = new Stack<>();
            this.redo = new Stack<>();
            this.currentState = new State();
        }

        private State startState() {
            State state;
            if (!this.undo.isEmpty()) {
                state = this.currentState;
            } else {
                state = new State();
            }
            this.currentState = new State();
            this.undo.add(state);
            if (!this.redo.isEmpty()) {
                this.redo.clear();
            }
            return state;
        }
    }

    private class State {

        Map<ViewerLabel, int[]> labelPositions;
        Map<Vertex, double[]> vertexPositions;
        Map<ViewerPoint, Color> pointColorsBg;
        Map<ViewerPoint, Color> pointColorsFg;

        String leaderType;

        public State() {
            labelPositions = new HashMap();
            vertexPositions = new HashMap();
            pointColorsBg = new HashMap();
            pointColorsFg = new HashMap();
            leaderType = null;
        }

        void returnTo() {
            for (Map.Entry<ViewerLabel, int[]> entry : labelPositions.entrySet()) {
                int[] xy = entry.getValue();
                entry.getKey().setCoordinates(xy[0], xy[1]);
            }

            for (Map.Entry<Vertex, double[]> entry : vertexPositions.entrySet()) {
                double[] xy = entry.getValue();
                entry.getKey().setCoordinates(xy[0], xy[1]);
            }

            for (Map.Entry<ViewerPoint, Color> entry : pointColorsBg.entrySet()) {
                entry.getKey().setBg(entry.getValue());
            }

            for (Map.Entry<ViewerPoint, Color> entry : pointColorsFg.entrySet()) {
                entry.getKey().setFg(entry.getValue());
            }

            if (vertexPositions.isEmpty()) {
                repaint();
            } else {
                repaintOnResize();
            }
        }
    }

    private static class Range2D {
        public double minX;
        public double maxX;
        public double minY;
        public double maxY;

        public Range2D() {
            this.minX = Double.MAX_VALUE;
            this.maxX = -Double.MAX_VALUE;
            this.minY = Double.MAX_VALUE;
            this.maxY = -Double.MAX_VALUE;
        }

        public double width() {
            return maxX - minX;
        }

        public double height() {
            return maxY - minY;
        }


        public void addPoint(final ViewerPoint p) {

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

            // Add both bottom left and top right of viewpoint to push extents
            addPoint(xMi, yMi);
            addPoint(xMa, yMa);
        }

        public void addPoint(final Vertex p) {
            addPoint(p.getX(), p.getY());
        }

        public void addPoint(final double x, final double y) {
            if (minX > x) {
                minX = x;
            }
            if (maxX < x) {
                maxX = x;
            }
            if (minY > y) {
                minY = y;
            }
            if (maxY < y) {
                maxY = y;
            }
        }

    }

    private static class NetworkExtents {

        public Range2D vertices;
        public Range2D labels;

        /*public ViewerPoint pMinX;
        public ViewerPoint pMaxX;
        public ViewerPoint pMinY;
        public ViewerPoint pMaxY;*/

        public NetworkExtents() {
            this.vertices = new Range2D();
            this.labels = new Range2D();

            /*this.pMinX = null;
            this.pMaxX = null;
            this.pMinY = null;
            this.pMaxY = null;*/
        }


        public static NetworkExtents determineRange(Collection<ViewerPoint> points, Map<Integer, Vertex> vertices) {

            NetworkExtents dims = new NetworkExtents();

            for (ViewerPoint p : points) {

                dims.labels.addPoint(p);

                if (!p.suppressed) {
                    dims.vertices.addPoint(vertices.get(p.id));
                }
            }
            return dims;
        }
    }


}