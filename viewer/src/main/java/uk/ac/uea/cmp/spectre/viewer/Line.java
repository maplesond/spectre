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

package uk.ac.uea.cmp.spectre.viewer;

import uk.ac.uea.cmp.spectre.core.ds.network.Edge;
import uk.ac.uea.cmp.spectre.core.ds.network.Translocator;

import java.awt.*;

/**
 * @author balvociute
 */
public class Line {
    ViewerPoint p1;
    ViewerPoint p2;
    int width;
    Color fg;

    Edge e;

    int split;

    double a;
    double b;

    Line(Edge e) {
        width = e.getWidth();
        fg = e.getColor();
        split = e.getIdxsplit();
        this.e = e;
    }

    public Line(ViewerPoint p1, ViewerPoint p2) {
        setPoints(p1, p2);
    }

    public ViewerPoint getOther(ViewerPoint p) {
        if (p1 == p) {
            return p2;
        } else if (p2 == p) {
            return p1;
        } else {
            return null;
        }
    }

    float getWidth() {
        return e.getWidth();
    }

    ViewerPoint closestPoint(ViewerPoint p) {
        double x;
        double y;

//        double b = p1.distanceTo(p2);
//        double s1 = p.distanceTo(p1);
//        double s2 = p.distanceTo(p2);
//        double s = 0.5*(b + s1 + s2);
//        double S = Math.sqrt(s*(s-b)*(s-s1)*(s-s2));

        if (a == 0) {
            if (p1.getX() == p2.getX()) {
                x = p1.getX();
            } else {
                x = p.getX();
                if (x < p1.getX()) {
                    x = p1.getX();
                }
                if (x > p2.getX()) {
                    x = p2.getX();
                }
            }

            if (p1.getY() == p2.getY()) {
                y = p1.getY();
            } else {
                y = p.getY();
                if (y < p1.getY()) {
                    y = p1.getY();
                }
                if (y > p2.getY()) {
                    y = p2.getY();
                }
            }
        } else {
            double a2 = -1.0 / a;
            double b2 = p.getY() - a2 * p.getX();
            x = (int) ((b2 - b) / (a - a2));
            y = (int) (a * x + b);

            if ((x - p1.getX()) * (x - p2.getX()) > 0 || (y - p1.getY()) * (y - p2.getY()) > 0) {
                double d1 = p.distanceTo(new ViewerPoint(p1.getX(), p1.getY()));
                double d2 = p.distanceTo(new ViewerPoint(p2.getX(), p2.getY()));
                if (d1 < d2) {
                    x = p1.getX();
                    y = p1.getY();
                } else {
                    x = p2.getX();
                    y = p2.getY();
                }
            }
        }
        return new ViewerPoint(x, y);
    }

    void setPoints(ViewerPoint p1, ViewerPoint p2) {
        this.p1 = p1;
        this.p2 = p2;
        computeAB();
    }

    public void computeAB() {
        a = Translocator.a(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        b = p1.getY() - p1.getX() * a;
    }
}
