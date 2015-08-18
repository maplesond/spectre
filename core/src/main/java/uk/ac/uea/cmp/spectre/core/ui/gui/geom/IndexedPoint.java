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

package uk.ac.uea.cmp.spectre.core.ui.gui.geom;

import java.awt.geom.Point2D;

/**
 * @author balvociute
 */
public class IndexedPoint extends Point2D.Double {

    private int id;
    private String label;

    public IndexedPoint(int id, double x, double y) {
        this(id, x, y, null);
    }

    public IndexedPoint(int id, double x, double y, String label) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.label = label;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        String loc = x + "\t" + y;
        loc = (label == null) ? loc : label + " " + loc;
        return loc;
    }
}
