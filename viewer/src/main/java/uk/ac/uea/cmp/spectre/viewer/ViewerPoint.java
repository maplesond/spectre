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

import uk.ac.uea.cmp.spectre.core.ds.network.Vertex;

import java.awt.*;
import java.util.Set;

/**
 * @author balvociute
 */
public class ViewerPoint extends Element implements Selectable {

    int id;
    ViewerLabel l;
    boolean round;
    private boolean suppressed;

    private ViewerPoint supp;

    int quarterNo;

    Set<ViewerLabel> neighborhood;

    Vertex v;

    int width;
    int height;

    private boolean selected;

    public ViewerPoint(Vertex v) {
        this.v = v;
        this.height = v.getHeight();
        this.width = v.getWidth();
        this.id = v.getNxnum();
        this.selected = false;
        if (v.getShape() == null) {
            this.round = true;
        } else if (v.getShape().equals("r")) {
            this.round = false;
        } else if (v.getShape().contentEquals("n")) {
            this.round = true;
            this.height = 0;
            this.width = 0;
        }
    }

    public ViewerPoint(double ix, double iy) {
        super(ix,iy);
        this.height = 0;
        this.width = 0;
        this.selected = false;
        this.round = false;
        this.selected = false;
        this.v = null;
    }

    @Override
    public double getX() {
        return !suppressed ? super.getX() : supp.getX();
    }

    @Override
    public double getY() {
        return !suppressed ? super.getY() : supp.getY();
    }

    @Override
    public int getXint() {
        return (int) getX();
    }

    @Override
    public int getYint() {
        return (int) getY();
    }

    public int getCentreX() {
        return this.getXint() - (int) (((double) this.width) / 2.0);
    }

    public int getCentreY() {
        return this.getYint() - (int) (((double) this.height) / 2.0);
    }

    public void suppress(ViewerPoint supp) {
        suppressed = true;
        this.supp = supp;
    }

    double distanceTo(ViewerPoint p2) {
        return Math.sqrt((getX() - p2.getX()) * (getX() - p2.getX()) +
                (getY() - p2.getY()) * (getY() - p2.getY()));
    }

    void setSize(int i) {
        width = i;
        height = i;
        v.setSize(i);
    }

    public int getSize() {
        return v.getHeight();
    }

    double distanceToBoundary(Rectangle bounds) {
        double dist1 = getX();
        double dist2 = getY();
        double dist3 = bounds.getWidth() - getX();
        double dist4 = bounds.getHeight() - getY();

        if (dist1 < dist2 && dist1 < dist3 && dist1 < dist4) {
            return dist1;
        } else if (dist2 < dist3 && dist2 < dist4) {
            return dist2;
        } else if (dist3 < dist4) {
            return dist3;
        } else {
            return dist4;
        }
    }

    void setBg(Color bg) {
        v.setBackgroundColor(bg);
    }

    void setFg(Color fg) {
        v.setLineColor(fg);
    }

    public Color getBg() {
        return v.getBackgroundColor();
    }

    public Color getFg() {
        return v.getLineColor();
    }

    boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    void setL(ViewerLabel l) {
        this.l = l;
        l.p = this;
    }

    public String getLabelName() {
        return this.l.name;
    }

    public boolean isSuppressed() {
        return suppressed;
    }

    public void setSuppressed(boolean suppressed) {
        this.suppressed = suppressed;
    }

    public void draw(Graphics g, Color selectionColor) {

        g.setColor(v.getBackgroundColor());

        int x = this.getCentreX();
        int y = this.getCentreY();

        if (round) {
            g.fillOval(x, y, this.width, this.height);
        } else {
            g.fillRect(x, y, this.width, this.height);
        }

        g.setColor(v.getLineColor());
        if (round && width > 0 && height > 0) {
            g.drawOval(x, y, width, height);
            if (selected) {
                g.setColor(selectionColor);
                g.drawOval(x - 1, y - 1, width + 2, height + 2);
            }
        } else {
            g.drawRect(x, y, width, height);
            if (selected) {
                g.setColor(selectionColor);
                g.drawRect(x - 1, y - 1, width + 2, height + 2);
            }
        }
    }

    public void setShape(String shape) {
        switch (shape) {
            case "square":
                round = false;
                v.setShape("r");
                break;
            case "circle":
                round = true;
                v.setShape(null);
                break;
        }
    }

    public void setQuarters(int midX, int midY) {
        quarterNo = 0;
        if (getX() <= midX && getY() <= midY) {
            quarterNo = 1;
        } else if (getX() <= midX && getY() >= midY) {
            quarterNo = 2;
        } else if (getX() >= midX && getY() >= midY) {
            quarterNo = 3;
        }
    }
}
