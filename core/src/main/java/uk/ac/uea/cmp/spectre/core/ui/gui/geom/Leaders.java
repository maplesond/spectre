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

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author balvociute
 */
public class Leaders {
    List<ColoredLine> lines;
    Map<Integer, IndexedPoint> labels;
    Map<Integer, IndexedPoint> points;

    public Leaders() {
        lines = new LinkedList<>();
        labels = new HashMap<>();
    }

    public void add(IndexedPoint a, IndexedPoint b, Color c) {
        lines.add(new ColoredLine(a, b, c));
    }

    public void add(IndexedPoint indexedLabel) {
        labels.put(indexedLabel.getId(), indexedLabel);
    }

    public Map<Integer, IndexedPoint> getLabels() {
        return labels;
    }

    public IndexedPoint getLabel(int nxnum) {
        return labels.get(nxnum);
    }

    public List<ColoredLine> getLines() {
        return lines;
    }

    public void setPoints(Map<Integer, IndexedPoint> pointMap) {
        points = pointMap;
    }

    public Map<Integer, IndexedPoint> getPoints() {
        return points;
    }

    public void setSmallCoordinates(int vId, double smallX, double smallY) {
        if (labels.get(vId) == null) {
            labels.put(vId, new IndexedPoint(vId, smallY, smallY));
        } else {
            labels.get(vId).setLocation(smallX, smallY);
        }
    }

    public void removeLines() {
        lines = null;
        points = null;
    }
}


