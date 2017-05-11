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

import uk.ac.uea.cmp.spectre.core.ds.network.NetworkLabel;

import java.awt.*;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author balvociute
 */
public class ViewerLabel extends Element {
    ViewerPoint p;
    boolean leader;

    int dlX;
    int dlY;

    String name;
    boolean sideLeader = true;

    NetworkLabel label;

    Cluster cluster;

    Window mainFrame;

    Double cosAlpha;

    /**
     * Default constructor.
     */
    public ViewerLabel() {
    }

    /**
     * Constructor copying information from label as read from nexus file.
     *
     * @param label
     * @param drawing
     */
    public ViewerLabel(NetworkLabel label, Window drawing) {
        name = label.getName();
        this.label = label;
        this.mainFrame = drawing;
    }

    /**
     * Sets new coordinates and checks whether leader is needed.
     *
     * @param x      new x position.
     * @param y      new y position.
     * @param labels set of labels.
     */
    public void setCoordinates(double x, double y, Collection<ViewerLabel> labels) {
        setOffX(x - p.getX());
        setOffY(y - p.getY());
        checkForLeader(labels);
    }

    /**
     * Sets new coordinates when they are determined by optimizing labeling and
     * checks whether leader is needed.
     *
     * @param x      new x position.
     * @param y      new y position.
     * @param labels set of labels.
     */
    public void setCoordinatesAutomatic(int x, int y, Collection<ViewerLabel> labels) {
        if (label.movable) {
            setOffX(x - p.getX());
            setOffY(y - p.getY());
            checkForLeader(labels);
        }
        computeMiddleDistance();
    }

    /**
     * Sets new coordinates for the label.
     *
     * @param x new x position.
     * @param y new y position.
     */
    public void setCoordinates(double x, double y) {
        setOffX(x - p.getX());

        setOffY(y - p.getY());
    }

    /**
     * Computes distance between two points.
     *
     * @param x1 x coordinate of the first point.
     * @param y1 y coordinate of the first point.
     * @param x2 x coordinate of the second point.
     * @param y2 y coordinate of the second point.
     * @return distance between points.
     */
    private double distance(double x1, double y1, double x2, double y2) {
        double dX = x1 - x2;
        double dY = y1 - y2;
        return Math.sqrt(dX * dX + dY * dY);
    }

    /**
     * Checks if the leader is necessary.
     *
     * @param labels
     */
    public void checkForLeader(Collection<ViewerLabel> labels) {
        double x2 = getX() + label.getWidth();
        double y2 = getY() - label.getHeight();

        computeMiddleDistance();

        Double minD = distance(p.getX(), p.getY(), getlX(), getlY());

        boolean b1 = minD > 30;
        boolean b2 = closerToSomeOtherPoint(x2, y2, minD, labels);

        if (b1) {
            setLeader(true);
            sideLeader = true;
        } else if (b2) {
            setLeader(true);
        } else {
            setLeader(false);
            sideLeader = false;
        }
    }

    private boolean closerToSomeOtherPoint(double x2, double y2, double minD, Collection<ViewerLabel> labels) {
        double c = 1.3;
        minD *= c;
        Iterator<ViewerLabel> labelIt = labels.iterator();
        while (labelIt.hasNext()) {
            ViewerLabel l = labelIt.next();
            if (l != this) {
                double d1 = distance(l.p.getX(), l.p.getY(), getX(), getY());
                double d2 = distance(l.p.getX(), l.p.getY(), getX(), y2);
                double d3 = distance(l.p.getX(), l.p.getY(), x2, getY());
                double d4 = distance(l.p.getX(), l.p.getY(), x2, y2);
                if (d1 <= minD || d2 <= minD || d3 <= minD || d4 <= minD) {
                    return true;
                }
            }
        }
        return false;
    }

    void setAutomaticMovable(boolean b) {
        label.movable = b;
        if (!b) {
            double cX = mainFrame.getCentrePoint().getX();
            double pX = p.getX();
            double lX = middleX();

            double cY = mainFrame.getCentrePoint().getY();
            double pY = p.getY();
            double lY = middleY();

            cosAlpha = ((pX - cX) * (pX - lX) + (pY - cY) * (pY - lY))
                    / Math.sqrt(
                    (pX * pX - 2 * pX * cX + cX * cX + pY * pY - 2 * pY * cY + cY * cY)
                            *
                            (pX * pX - 2 * pX * lX + lX * lX + pY * pY - 2 * pY * lY + lY * lY));
        }
    }

    public Color getBgColor() {
        return label.getBackgroundColor();
    }


    @Override
    public double getX() {
        return (p.getX() + label.getOffsetX());
    }

    @Override
    public double getY() {
        return (p.getY() + label.getOffsetY());
    }

    @Override
    public int getXint() {
        return (int) getX();
    }

    @Override
    public int getYint() {
        return (int) getY();
    }

    public void distance() {
        double mx = middleX();
        double my = middleY();

        double x1 = getX();
        double y1 = getY();
        double x2 = x1 + label.getWidth();
        double y2 = y1 - label.getHeight();

        ViewerPoint intersection = intersectionPoint(x1, y1, x2, y1, mx, my, p.getX(), p.getY());
        if (intersection == null) {
            intersection = intersectionPoint(x1, y2, x1, y1, mx, my, p.getX(), p.getY());
        }
        if (intersection == null) {
            intersection = intersectionPoint(x1, y2, x2, y2, mx, my, p.getX(), p.getY());
        }
        if (intersection == null) {
            intersection = intersectionPoint(x2, y2, x2, y1, mx, my, p.getX(), p.getY());
        }

        if (intersection != null) {
            setlX((int) intersection.getX());
            setlY((int) intersection.getY());
        } else {
            setlX((int) p.getX());
            setlY((int) p.getY());
        }
    }

    public void computeMiddleDistance() {
        double x1 = getX();
        double y1 = getY();
        double x2 = x1 + label.getWidth();
        double y2 = y1 - label.getHeight();

        if ((p.getX() - x1) * (p.getX() - x2) > 0) {
            sideLeader = true;
            if (Math.abs(p.getX() - x1) <= Math.abs(p.getX() - x2)) {
                setlX((int) x1);
            } else {
                setlX((int) x2);
            }
            setlY((int) ((2 * y1 + y2) / 3));
        } else {
            sideLeader = false;
            if (Math.abs(p.getY() - y1) < Math.abs(p.getY() - y2)) {
                setlY((int) y1);
            } else {
                setlY((int) y2);
            }
            setlX((int) ((x1 + x2) / 2));
        }
    }


    public static ViewerPoint intersectionPoint(double x1, double y1, double x2, double y2,
                                          double X1, double Y1, double X2, double Y2) {
        double ix;
        double iy;

        Double a = null;
        Double A;
        Double b = null;
        Double B;

        if (x1 == x2) {
            ix = x1;
        } else if (X1 == X2) {
            ix = X1;
        } else {
            a = ((double) (y2 - y1)) / ((double) (x2 - x1));
            A = ((double) (Y2 - Y1)) / ((double) (X2 - X1));
            b = y1 - x1 * a;
            B = Y1 - X1 * A;
            ix = (B - b) / (a - A);
        }

        if (y1 == y2) {
            iy = y1;
        } else if (Y1 == Y2) {
            iy = Y1;
        } else {
            if (a == null || b == null) {
                a = ((double) (y2 - y1)) / ((double) (x2 - x1));
                b = y1 - x1 * a;
            }
            iy = a * ix + b;
        }

        if ((ix - X1) * (ix - X2) <= 0 && (iy - Y1) * (iy - Y2) <= 0 &&
                (ix - x1) * (ix - x2) <= 0 && (iy - y1) * (iy - y2) <= 0) {
            return new ViewerPoint(ix, iy);
        }
        return null;
    }

    public double middleX() {
        return getX() + label.getWidth() / 2;
    }

    public double middleY() {
        return getY() - label.getHeight() / 2;
    }

    ViewerPoint computeBendingPoint(boolean bended) {
        int bx;
        int by;

        if (bended) {
            bx = (int) p.getX();
            by = getlY();
        } else {
            if (sideLeader) {
                by = getlY();
                int direction = (getlX() > p.getX()) ? 1 : -1;
                bx = (int) (p.getX() + direction * Math.abs(getlY() - p.getY()));

                if ((bx - getlX()) * (bx - p.getX()) >= 0) {
                    bx = getlX();
                }
            } else {
                bx = getlX();
                int direction = (getlY() > p.getY()) ? 1 : -1;
                by = (int) (p.getY() + direction * Math.abs(getlX() - p.getX()));

                if ((by - getlY()) * (by - p.getY()) >= 0) {
                    by = getlY();
                }

            }
        }
        return new ViewerPoint(bx, by);
    }

    void computeOffsets(double x1, double y1, Collection<ViewerLabel> labels) {
        setOffX(x1 - p.getX());

        setOffY(y1 - p.getY());
        checkForLeader(labels);
    }

    void forceLeader(double x, double y) {
        if (getX() != getlX()) {
            x -= label.getWidth();
        }
        setCoordinates(x, y);
        setLeader(true);
        sideLeader = true;
        computeMiddleDistance();
    }

    void setCluster(Cluster cluster) {
        if (this.cluster != null) {
            this.cluster.remove(this);
        }
        this.cluster = cluster;
    }

    void changePositionsWith(ViewerLabel l2) {
        boolean same1 = (getX() == getlX());
        boolean same2 = (l2.getX() == l2.getlX());

        double x1 = getX();
        double y1 = getY();
        int lx1 = getlX();
        int ly1 = getlY();

        if (!same2) {
            x1 = lx1 - l2.label.getWidth();
        }

        setCoordinates((same1 ? l2.getX() : l2.getlX() - label.getWidth()), l2.getY());
        setlX(l2.getlX());
        setlY(l2.getlY());

        l2.setCoordinates(x1, y1);
        l2.setlX(lx1);
        l2.setlY(ly1);
    }

    public void setOffX(double offX) {
        label.setOffsetX(offX);
    }

    public void setOffY(double offY) {
        label.setOffsetY(offY);
    }

    /**
     * Checks if element stays inside of the window after moving it.
     *
     * @param x1 x coordinate after moving.
     * @param y1 y coordinate after moving.
     * @return if the element stays inside of the window after moving it.
     */
    public boolean staysInside(double x1, double y1) {
        Rectangle bounds = mainFrame.getBounds();
        return (x1 >= bounds.x && x1 + label.getWidth() <= bounds.x + bounds.width &&
                y1 - label.getHeight() >= bounds.y && y1 <= bounds.y + bounds.height)
                ||
                (x >= bounds.x && x + label.getWidth() <= bounds.x + bounds.width &&
                        y - label.getHeight() >= bounds.y && y <= bounds.y + bounds.height);
    }

    /**
     * Checks if the element is inside.
     *
     * @param bounds window bounds.
     * @return if the element is inside of the current window.
     */
    public boolean isInside(Rectangle bounds) {
        return x >= bounds.x && x + label.getWidth() <= bounds.x + bounds.width &&
                y - label.getHeight() >= bounds.y && y <= bounds.y + bounds.height;
    }

    public int getlX() {
        return (int) (getX() + dlX);
    }

    public int getlY() {
        return (int) (getY() + dlY);
    }

    private void setlX(int x) {
        dlX = (int) (x - getX());
    }

    private void setlY(int y) {
        dlY = (int) (y - getY());
    }

    void checkForLeader() {
        Double minD = distance(p.getX(), p.getY(), getlX(), getlY());
        if (minD > 30) {
            setLeader(true);
            sideLeader = true;
        }
    }

    private void setLeader(boolean leader) {
        this.leader = leader;
    }

    private static Color getTextColor(Color bg) {
        if (bg.getRed() <= 50 && bg.getGreen() <= 50 && bg.getBlue() <= 50) {
            return Color.white;
        } else {
            return Color.black;
        }
    }

    public void draw(Graphics g, Color selectionColor, boolean colorLabels) {
        if (label.getFontFamily() == null) {
            label.setFontFamily("Helvetica");
        }
        g.setFont(new Font(label.getFontFamily(), label.getFontStyle(), label.getFontSize()));

        int lx = getXint();
        int ly = getYint();

        if (p.v.getBackgroundColor() != null && colorLabels) {
            g.setColor(p.v.getBackgroundColor());
            g.fillRect(lx - 1, ly - label.getHeight() + 2, label.getWidth() + 3, label.getHeight());
        }
        if (p.isSelected()) {
            g.setColor(selectionColor);
            g.drawRect(lx, ly - label.getHeight() + 1, label.getWidth(), label.getHeight());
        }
        Color textColor = Color.BLACK; //label.getFontColor();
        if (p.v.getBackgroundColor() != null && colorLabels) {
            textColor = getTextColor(p.v.getBackgroundColor());
        }
        g.setColor(textColor);
        g.drawString(name, lx, ly - 1);
    }
}
