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

import java.util.Collection;
import java.util.Iterator;

/**
 * @author balvociute
 */
public class LabelingOptimizer {
    int gridGap = 10;
    GridPoint[][] grid;

    int margin = 30;

    public void optimize(Window window) {
        grid = new GridPoint[(window.getWidth() / gridGap)]
                [(window.getHeight() / gridGap)];

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j] = density(i, j, window);
            }
        }

        Iterator<Label> labelIt = window.labels.values().iterator();
        while (labelIt.hasNext()) {
            Label l = labelIt.next();
            Point p = l.p;

            GridPoint gp = null;

            int xb = (int) (p.getX() - l.label.getWidth() - margin);
            int yb = (int) (p.getY() + l.label.getHeight() - margin);
            int xt = (int) (p.getX() + margin);
            int yt = (int) (p.getY() - l.label.getHeight() + margin);


            for (int i = xb; i <= xt; i++) {
                for (int j = yb; j <= yt; j++) {
                    if (i >= 0 && j >= 0 &&
                            i <= window.getWidth() && j <= window.getHeight() &&
                            i % gridGap == 0 && j % gridGap == 0) {
                        GridPoint currentGP = grid[i / gridGap][j / gridGap];
                        if (gp == null || gp.density > currentGP.density) {
                            gp = currentGP;
                        }
                    }
                }
            }

            if (gp != null) {
                l.setCoordinatesAutomatic(gp.x, gp.y, window.labels.values());
            }
        }
    }

    private GridPoint density(int i, int j, Window window) {
        GridPoint gp = new GridPoint();
        gp.x = i * gridGap;
        gp.y = j * gridGap;

        int deltaY = (int) (window.maxLabelH / 2.0);
        int deltaX = (int) (window.maxLabelW / 2.0);

        gp.density = sumOfIntersectingLines(gp.x - deltaX, gp.y - deltaY,
                gp.x + deltaX, gp.y + deltaY,
                window.lines.values());
        return gp;
    }

    private double sumOfIntersectingLines(int x1, int y1, int x2, int y2,
                                          Collection<Line> lines) {
        double density = 0.0;
        Iterator<Line> lineIt = lines.iterator();
        while (lineIt.hasNext()) {
            Line l = lineIt.next();

            Point p1 = Label.intersectionPoint(l.p1.getX(), l.p1.getY(), l.p2.getX(), l.p2.getY(),
                    x2, y2, x2, y1);
            Point p2 = Label.intersectionPoint(l.p1.getX(), l.p1.getY(), l.p2.getX(), l.p2.getY(),
                    x1, y2, x2, y2);
            Point p3 = Label.intersectionPoint(l.p1.getX(), l.p1.getY(), l.p2.getX(), l.p2.getY(),
                    x1, y1, x1, y2);
            Point p4 = Label.intersectionPoint(l.p1.getX(), l.p1.getY(), l.p2.getX(), l.p2.getY(),
                    x1, y1, x2, y1);

            Point i1;
            Point i2;

            if (p1 != null) {
                i1 = p1;
            } else if (p2 != null) {
                i1 = p2;
            } else if (p3 != null) {
                i1 = p3;
            } else {
                continue;
            }

            if (p2 != null && i1.getX() != p2.getX() && i1.getY() != p2.getY()) {
                i2 = p2;
            } else if (p3 != null && i1.getX() != p3.getX() && i1.getY() != p3.getY()) {
                i2 = p3;
            } else if (p4 != null && i1.getX() != p4.getX() && i1.getY() != p4.getY()) {
                i2 = p4;
            } else {
                continue;
            }

            density += i1.distanceTo(i2);

        }
        return density;
    }

    private static class GridPoint {
        int x;
        int y;
        double density;
    }
}
