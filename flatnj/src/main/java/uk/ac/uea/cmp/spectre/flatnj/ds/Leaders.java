/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
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

package uk.ac.uea.cmp.spectre.flatnj.ds;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author balvociute
 */
public class Leaders {
    List<Line> lines;
    Map<Integer, Label> labels;
    Map<Integer, Point> points;

    public Leaders() {
        lines = new LinkedList<>();
        labels = new HashMap<>();
    }

    public void add(Point a, Point b, Color c) {
        lines.add(new Line(a, b, c));
    }

    public void add(Label label) {
        labels.put(label.getVertexId(), label);
    }

    public Map<Integer, Label> getLabels() {
        return labels;
    }

    public Label getLabel(int nxnum) {
        return labels.get(nxnum);
    }

    public List<Line> getLines() {
        return lines;
    }

    public void setPoints(Map<Integer, Point> pointMap) {
        points = pointMap;
    }

    public Map<Integer, Point> getPoints() {
        return points;
    }

    public void setSmallCoordinates(int vId, double smallX, double smallY) {
        if (labels.get(vId) == null) {
            labels.put(vId, new Label(vId, smallY, smallY));
        } else {
            labels.get(vId).setCoordinates(smallX, smallY);
        }
    }

    public void removeLines() {
        lines = null;
        points = null;
    }
}


