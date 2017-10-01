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
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author balvociute
 */
public class Window extends JPanel implements KeyListener, ComponentListener {

    private final static int PAN_STEP = 5;

    private ViewerPoint selectedPoint;
    private Set<Cluster> clusters = new HashSet<>();
    double maxLabelH;
    double maxLabelW;
    private double maxOffX = 0.0;
    private double maxOffY = 0.0;

    private Point centrePoint;
    private Point2D offset;

    private java.awt.Point last = null;
    private Point startPoint;

    private double change;
    private final int extraSpace = 20;
    private final int maximalLabelWidth = 40;

    private ViewMode viewMode;

    // Maps for keys
    private InputMap inputmap = this.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
    private ActionMap actionmap = this.getActionMap();

    // Popup menu which occurs on right click
    private javax.swing.JPopupMenu popupMenu;

    // Leave public so external frame can modify based on menu item selections
    public ViewerConfig config;

    // Stores history of changes so that we can implement undo / redo functionality
    History history = new History();

    private JMenuItem copyMnuItem;



    public Window(JMenuItem copyMnuItem) {

        // Easy handle to menu item
        this.copyMnuItem = copyMnuItem;

        // Setup default configuration
        this.config = new ViewerConfig();

        // Setup window markers
        this.centrePoint = new Point(this.getWidth() / 2,this.getHeight() / 2);
        this.offset = new Point2D.Double(0.0, 0.0);

        // Set default view mode to normal
        this.viewMode = ViewMode.NORMAL;

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
        copySelectedTaxa.setEnabled(false);
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
                pan(-PAN_STEP, 0);
            }
        });

        final String leftKeyPressed = "left arrow pressed";
        inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), leftKeyPressed);
        actionmap.put(leftKeyPressed, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                pan(PAN_STEP, 0);
            }
        });

        final String upKeyPressed = "up arrow pressed";
        inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), upKeyPressed);
        actionmap.put(upKeyPressed, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                pan(0, PAN_STEP);
            }
        });

        final String downKeyPressed = "down arrow pressed";
        inputmap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), downKeyPressed);
        actionmap.put(downKeyPressed, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                pan(0, -PAN_STEP);
            }
        });
    }

    private void setupMouseListeners() {

        // **** Mouse Listeners ****

        this.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                zoom(evt.getPreciseWheelRotation() * config.getRatio() / 50.0, evt.getPoint());
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

            /*public void mouseDragged(java.awt.event.MouseEvent evt) {

                // First just to ensure there are no bugs later
                if (startPoint == null) {
                    startPoint = evt.getPoint();
                }

                if (SwingUtilities.isRightMouseButton(evt)) {
                    rotate(startPoint, evt.getPoint());
                }
                else if (SwingUtilities.isLeftMouseButton(evt)) {
                    if (evt.isShiftDown() || evt.isControlDown()) {
                        setSelection(startPoint,
                                evt.getPoint(),
                                true);
                    }
                    else if (viewMode == ViewMode.LABEL) {
                        moveLabels(evt.getPoint());
                    }
                    else if (viewMode == ViewMode.POINT) {
                        moveTheVertex(evt.getPoint());
                    }
                    else {
                        pan(evt.getPoint());
                    }
                }
            }*/

            public void mousePressed(java.awt.event.MouseEvent evt) {

                if (network != null) {
                    startPoint = evt.getPoint();
                    if (SwingUtilities.isRightMouseButton(evt)) {
                        viewMode = ViewMode.ROTATE;
                        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                    } else if (SwingUtilities.isLeftMouseButton(evt)) {
                        if (evt.isShiftDown() || evt.isControlDown()) {
                            viewMode = ViewMode.SELECT;
                        } else if (isOnLabel(evt.getPoint())) {
                            viewMode = ViewMode.LABEL;
                            markLabel(startPoint);
                        } else if (isOnPoint()) {
                            viewMode = ViewMode.POINT;
                            markPoint(startPoint);
                        } else {
                            // Do panning
                            viewMode = ViewMode.PAN;
                            setCursor(new Cursor(Cursor.MOVE_CURSOR));
                        }
                    }
                    repaint();
                }
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                if (network != null) {
                    if (viewMode == ViewMode.ROTATE || viewMode == ViewMode.PAN) {
                        lastPoint = null;
                    }
                    else if (viewMode == ViewMode.SELECT) {
                        removeSelectionRectangle();
                    }
                    else if (viewMode == ViewMode.LABEL) {
                        pointsToHighlight.clear();
                    }
                    viewMode = ViewMode.NORMAL;
                    setCursor(Cursor.getDefaultCursor());
                    startPoint = null;
                    enableCopy(!pointsToHighlight.isEmpty());
                    repaint();
                }
            }
        });

        this.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                if (network != null) {
                    // First just to ensure there are no bugs later
                    if (startPoint == null) {
                        startPoint = evt.getPoint();
                    }

                    if (SwingUtilities.isRightMouseButton(evt)) {
                        rotate(startPoint, evt.getPoint());
                    }
                    else if (SwingUtilities.isLeftMouseButton(evt)) {
                        if (evt.isShiftDown() || evt.isControlDown()) {
                            setSelection(startPoint,
                                    evt.getPoint(),
                                    true);
                        }
                        else if (viewMode == ViewMode.LABEL) {
                            moveLabels(evt.getPoint());
                        }
                        else if (viewMode == ViewMode.POINT) {
                            moveTheVertex(evt.getPoint());
                        }
                        else {
                            pan(evt.getPoint());
                        }
                    }
                }
            }
        });

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                centrePoint = new Point(getWidth() / 2, getHeight() / 2);
                repaintOnResize();
            }
        });
    }



    NetworkExtents dimensions;
    List<Edge> edges;
    Network network;
    Leaders leaders = new Leaders();
    Map<Integer, ViewerPoint> points = new HashMap<>();
    Map<Integer, Line> lines = new HashMap<>();
    Map<Integer, ViewerLabel> labels = new HashMap<>();
    Map<Integer, Vertex> vertices = new HashMap<>();
    int[] selectionRectangle = null;
    boolean resetedLabelPositions = false;
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

    java.awt.Point lastPoint = null;
    ClusterFinder cf = new ClusterFinderSplits();
    ClusterPlacementOptimizer cpo = new ClusterPlacementOptimizerBox();

    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);

        setBackground(Color.white);

        Graphics2D g = (Graphics2D)graphics;

        // Draw lines
        if (lines != null) {
            for (Line l : lines.values()) {
                g.setStroke(new BasicStroke(l.getWidth()));
                g.setColor(l.fg);
                g.drawLine(l.p1.getXint(), l.p1.getYint(), l.p2.getXint(), l.p2.getYint());
            }
        }

        // Draw leaders
        if (labels != null && labels.size() > 0 && config.showLabels() && config.leadersVisible()) {
            g.setStroke(config.getLeaderStroke().getStroke());

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
                        ViewerPoint bentPoint = l.computeBendingPoint(config.getLeaderType() == Leader.LeaderType.BENDED);
                        int[] x = new int[]{l.p.getXint(), bentPoint.getXint(), l.getlX()};
                        int[] y = new int[]{l.p.getYint(), bentPoint.getYint(), l.getlY()};
                        g.drawPolyline(x, y, 3);
                    }
                }
            }
        }

        g.setStroke(new BasicStroke());

        // Draw all viewer points
        if (points != null && points.size() > 0) {
            for (ViewerPoint p : points.values()) {
                p.draw(g, selectionColor);
            }
        }

        // Draw labels
        if (labels != null && labels.size() > 0 && config.showLabels()) {
            for (ViewerLabel l : labels.values()) {
                l.draw(g, selectionColor, config.colorLabels());
            }
        }

        g.setStroke(new BasicStroke());

        // Draw the range indicator in the top left
        if (config.showRange()) {
            g.setColor(Color.BLACK);

            int delta = (int)(config.getRatio());
            if (delta == 0) {
                delta = 1;
            }
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
        if (viewMode == ViewMode.SELECT && selectionRectangle != null) {
            g.setColor(Color.red);
            g.drawRect(selectionRectangle[0], selectionRectangle[1], selectionRectangle[2], selectionRectangle[3]);
        }

        // Draw a blue crosshair in centre of screen to indicate we are in rotate mode
        if (viewMode == ViewMode.ROTATE) {
            g.setColor(Color.BLUE);

            int cSize = 8;
            int lLength = 16;

            int midX = (int)this.centrePoint.getX();
            int midY = (int)this.centrePoint.getY();

            g.drawOval(midX - cSize / 2, midY - cSize / 2, cSize, cSize);
            g.drawLine(midX - lLength / 2, midY, midX + lLength / 2, midY);
            g.drawLine(midX, midY - lLength / 2, midX, midY + lLength / 2);
        }


    }

    public void drawNetwork(final ViewerConfig config, Network network, boolean optimiseLayout) {
        this.network = network;
        this.config = config;
        this.setGraph();
        this.showTrivial(config.showTrivial());
        this.repaintOnResize();
        if (optimiseLayout) {
            this.optimiseScale(true);
        }
        this.repaintOnResize();
    }



    private void computeIntegerCoordinates() {

        if (vertices != null) {
            // Recalculate dimensions of the network
            this.dimensions = NetworkExtents.determineRange(this.points.values(), this.vertices);

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

    private AffineTransform getCurrentTransform() {
        AffineTransform tx = new AffineTransform();
        tx.translate(this.centrePoint.getX(), this.centrePoint.getY());
        tx.scale(this.config.getRatio(), this.config.getRatio());
        tx.rotate(this.config.getAngle());
        tx.translate(this.offset.getX(), this.offset.getY());
        return tx;
    }

    // Convert the panel coordinates into the cooresponding coordinates on the translated image.
    public Point2D getTranslatedPoint(double panelX, double panelY) {

        AffineTransform tx = getCurrentTransform();
        Point2D point2d = new Point2D.Double(panelX, panelY);
        try {
            return tx.inverseTransform(point2d, null);
        } catch (NoninvertibleTransformException e) {
            JOptionPane.showMessageDialog(this.getParent(),
                    "Non invertible transform.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    public void resetLabelPositions(boolean all) {

        LabelPlacementOptimizer lpo = new LabelPlacementOptimizer8Points();
        lpo.placeLabels(labels.values(), lines.values(), this, all);

        if (clusters != null && !clusters.isEmpty()) {
            cpo.placeClusterLabels(clusters, labels, this);
        }
    }

    private void enableCopy(boolean status) {
        this.copyMnuItem.setEnabled(status);
        this.popupMenu.getComponent(0).setEnabled(status);
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

            this.enableCopy(!this.pointsToHighlight.isEmpty());
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
                selectedTaxa += p.getLabelName() + "\n";
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
            this.enableCopy(true);
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
            this.enableCopy(false);
        }
    }

    boolean isOnPoint() {
        return selectedPoint != null;
    }

    void moveTheVertex(java.awt.Point directionPoint) {
        if (selectedPoint != null) {
            Vertex v = vertices.get(selectedPoint.id);
            if (v.getEdgeList().size() == 1) {
                //State state = this.history.startState();
                //state.vertexPositions.put(v, new double[]{v.getX(), v.getY()});

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

                //history.currentState.vertexPositions.put(v, new double[]{v.getX(), v.getY()});
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
            for (ViewerPoint p : pointsToHighlight) {
                if (changeShape) {
                    p.setShape(shape);
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
            for(ViewerLabel l : labels.values()) {
                if (show) {
                    Vertex v = vertices.get(l.p.id);
                    if (v.getEdgeList().size() == 1) {
                        l.p.setSuppressed(false);
                        ViewerPoint p2 = points.get(v.getFirstEdge().getOther(v).getNxnum());
                        l.p.setSize(p2.getSize());
                        l.p.setShape(p2.getShape());
                        l.p.setFg(p2.getFg());
                        l.p.setBg(p2.getBg());
                        p2.setSize(0);

                    }
                    else {

                    }
                } else {
                    Vertex v = vertices.get(l.p.id);
                    int size = l.p.getSize();
                    Color fg = l.p.getFg();
                    Color bg = l.p.getBg();
                    String shape = l.p.getShape();

                    if (v.getEdgeList().size() == 1) {
                        ViewerPoint p2 = points.get(v.getFirstEdge().getOther(v).getNxnum());
                        l.p.suppress(p2);
                        p2.setSize(size);
                        p2.setFg(fg);
                        p2.setBg(bg);
                        p2.setShape(shape);
                    }
                    else {
                        //l.p.setSuppressed(false);
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

    void rotate(double delta) {
        this.config.incAngle(delta);
        repaintOnResize();
    }

    void rotate(java.awt.Point startPoint, java.awt.Point endPoint) {
        startPoint = (lastPoint != null) ? lastPoint : startPoint;
        this.config.incAngle(Vertex.getClockwiseAngle(
                new Vertex(endPoint.getX(), endPoint.getY()),
                new Vertex(centrePoint.getX(), centrePoint.getY()),
                new Vertex(startPoint.getX(), startPoint.getY())));
        lastPoint = endPoint;
        repaintOnResize();
    }


    void pan(int deltaX, int deltaY) {

        Point2D adjNewPoint = getCurrentTransform().transform(this.offset, null);
        adjNewPoint.setLocation(adjNewPoint.getX() + deltaX, adjNewPoint.getY() + deltaY);
        this.offset = getTranslatedPoint(adjNewPoint.getX(), adjNewPoint.getY());
        repaintOnResize();
    }

    void pan(java.awt.Point endPoint) {

        // Determine the old and new mouse coordinates based on the translated coordinate space.
        Point2D adjPreviousPoint = getTranslatedPoint(startPoint.getX(), startPoint.getY());
        Point2D adjNewPoint = getTranslatedPoint(endPoint.getX(), endPoint.getY());

        double newX = adjNewPoint.getX() - adjPreviousPoint.getX();
        double newY = adjNewPoint.getY() - adjPreviousPoint.getY();

        this.startPoint.setLocation((int)endPoint.getX(), (int)endPoint.getY());
        this.offset.setLocation(this.offset.getX() + newX, this.offset.getY() + newY);

        repaintOnResize();
    }

    public void zoom(double amount) {
        this.config.setRatio(this.config.getRatio() - amount);
        repaintOnResize();
    }

    public void zoom(double amount, Point point) {
        /*Point2D translated = this.getTranslatedPoint(point.getX(), point.getY());
        this.config.setRatio(this.config.getRatio() - amount);
        Point2D newoffset = this.getCurrentTransform().transform(translated, null);
        this.offset.x = (int)newoffset.getX();
        this.offset.y = (int)newoffset.getY();*/
        this.config.setRatio(this.config.getRatio() - amount);
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
            this.enableCopy(!this.pointsToHighlight.isEmpty());
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

    int find(String text, boolean regEx, boolean ignoreCase) {
        removeSelection();
        Iterator<Integer> idIt = labels.keySet().iterator();
        while (idIt.hasNext()) {
            Integer id = idIt.next();
            ViewerLabel lab = labels.get(id);
            if (regEx) {
                Pattern pattern = Pattern.compile(ignoreCase ? text.toLowerCase() : text);
                Matcher matcher = pattern.matcher(ignoreCase ? lab.name.toLowerCase() : lab.name);
                if (matcher.find()) {
                    pointsToHighlight.add(lab.p);
                    continue;
                }
            }
            if (ignoreCase) {
                if (lab.name.toLowerCase().contains(text.toLowerCase())) {
                    pointsToHighlight.add(lab.p);
                }
            }
            else {
                if (lab.name.contains(text)) {
                    pointsToHighlight.add(lab.p);
                }
            }
        }
        this.enableCopy(!this.pointsToHighlight.isEmpty());
        repaint();

        return this.pointsToHighlight.size();
    }

    private void removeSelection() {
        pointsToHighlight.clear();
        this.enableCopy(false);
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
        this.enableCopy(!this.pointsToHighlight.isEmpty());

        repaint();
    }

    public Map<Integer, ViewerLabel> getLabels() {
        return labels;
    }

    private void scaleCoordinates() {

        // Iterate through the points recalculating the locations in screen space
        for (ViewerPoint p : points.values()) {
            Point2D src = new Point2D.Double(p.v.getX(), p.v.getY());
            Point2D dst = getCurrentTransform().transform(src, null);
            p.setX(dst.getX());
            p.setY(dst.getY());
            if (change != 0 && p.l != null && !p.isSelected()) {
                //if (change != 1) {
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
                //}
            }
        }
    }

    private void recomputeRatio() {

        final double lastRatio = config.getRatio();

        double width = (getWidth() / 2.0 - 20.0 > sideMargin) ? (getWidth() - 2.0 * sideMargin) : 40;
        double height = (getHeight() / 2.0 - 20.0 > heightMargin) ? (getHeight() - 2.0 * heightMargin) : 40;

        double newRatio = Math.min(width / dimensions.vertices.width(), height / dimensions.vertices.height());

        change = lastRatio == newRatio ? 0.0 : newRatio / lastRatio;
        config.setRatio(newRatio);
    }

    /**
     * Places network optimally within the viewing window
     * Resets offsets to 0, rescales all coordinates to optimal values and then repaints
     * @param resetLabels Whether or not to consider labels when rescaling coordinates of network
     */
    public void optimiseScale(boolean resetLabels) {

        if (vertices != null) {
            this.dimensions = NetworkExtents.determineRange(points.values(), vertices);
            recomputeRatio();
            Point2D centreNet = new Point2D.Double(this.dimensions.vertices.centreX(), this.dimensions.vertices.centreY());
            Point2D screenOffset = this.getCurrentTransform().transform(centreNet, null);
            this.offset.setLocation(-centreNet.getX(), -centreNet.getY());

            scaleCoordinates();
            resetLabelPositions(resetLabels);
        }

        repaintOnResize();
        recomputeRatio();
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




    // **** Keylistener methods ****

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.isShiftDown()) {
            this.viewMode = ViewMode.SELECT;
        };
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (this.viewMode == ViewMode.SELECT) {
            this.viewMode = ViewMode.NORMAL;
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

    public Point getCentrePoint() {
        return centrePoint;
    }
    public Point2D getOffset() {
        return offset;
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

        public double centreX() {
            return maxX - (width() / 2.0);
        }

        public double centreY() {
            return maxY - (height() / 2.0);
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

        public NetworkExtents() {
            this.vertices = new Range2D();
            this.labels = new Range2D();
        }


        public static NetworkExtents determineRange(Collection<ViewerPoint> points, Map<Integer, Vertex> vertices) {

            NetworkExtents dims = new NetworkExtents();

            for (ViewerPoint p : points) {

                dims.labels.addPoint(p);

                if (!p.isSuppressed()) {
                    dims.vertices.addPoint(vertices.get(p.id));
                }
            }
            return dims;
        }
    }

    private enum ViewMode {
        NORMAL,
        ROTATE,
        SELECT,
        LABEL,
        POINT,
        PAN
    }


}