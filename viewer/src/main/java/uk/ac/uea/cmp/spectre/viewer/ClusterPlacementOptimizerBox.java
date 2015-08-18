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

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author balvociute
 */
public class ClusterPlacementOptimizerBox extends ClusterPlacementOptimizer {
    int bufferX = 20;
    List<Rectangle> takenZones = new LinkedList();

    @Override
    public void placeClusterLabels(Set<Cluster> clusters,
                                   Map<Integer, ViewerLabel> labels,
                                   Window window) {
        this.window = window;

        Iterator<Cluster> clusterIt = clusters.iterator();
        while (clusterIt.hasNext()) {
            Cluster cluster = clusterIt.next();
            if (cluster.isMovable() && cluster.size() > 0) {
                boolean leader = false;
                Iterator<ViewerPoint> pIt = cluster.points.iterator();
                while (pIt.hasNext()) {
                    if (labels.get(pIt.next().id).sideLeader) {
                        leader = true;
                    }
                }
                if (leader) {
                    cluster.leaders = true;
                    pIt = cluster.points.iterator();

                    Double topY = null;
                    Double botY = null;
                    double x = 0;
                    while (pIt.hasNext()) {
                        ViewerPoint p = pIt.next();
                        double y = p.getY();
                        x += p.getX();
                        if (topY == null || topY < y) {
                            topY = y;
                        }
                        if (botY == null || botY > y) {
                            botY = y;
                        }
                    }
                    double y = ((topY + botY - cluster.getHeight()) * 0.5);
                    x /= cluster.points.size();

                    boolean ok = false;
                    takenZones = new LinkedList();

                    while (!ok) {
                        x = findX(window, y, cluster, x);
                        if (takenZones.isEmpty()) {
                            ok = true;
                            takenZones.add(new Rectangle((int) x, (int) y, cluster.width, cluster.height));
                        }
                        for (int i = 0; i < takenZones.size(); i++) {
                            Rectangle zone = takenZones.get(i);
                            if ((zone.y - y)
                                    * (zone.y - (y + cluster.getHeight())) < 0
                                    ||
                                    ((zone.y + zone.height) - y)
                                            * ((zone.y + zone.height) - (y + cluster.getHeight())) < 0
                                    ) {
                                y = (Math.abs(zone.y - y)
                                        < Math.abs(zone.y + zone.height - y - cluster.getHeight()))
                                        ? zone.y - cluster.getHeight()
                                        : zone.y + zone.height;
                            } else {
                                ok = true;
                            }
                        }
                    }

                    for (ViewerLabel label : window.labels.values()) {
                        if (!cluster.points.contains(label.p)) {
                            if ((label.getX() >= x && label.getX() <= x + cluster.width
                                    ||
                                    label.getX() + label.label.getWidth() >= x && label.getX() + label.label.getWidth() <= x + cluster.width
                            )
                                    &&
                                    (label.getY() >= y && label.getY() <= y + cluster.height
                                            ||
                                            label.getY() - label.label.getHeight() >= y && label.getY() - label.label.getHeight() <= y + cluster.height)
                                    ) {
                                if (x < window.getWidth() / 2) {
                                    x = label.getX() - cluster.width - 5;
                                } else {
                                    x = label.getX() + label.label.getWidth() + 5;
                                }
                                if (label.cluster != null && label.cluster.size() > 1) {
                                    if (y < window.getHeight() / 2) {
                                        y = label.getY()
                                                - label.label.getHeight()
                                                - 5
                                                - cluster.height;
                                    } else {
                                        y = label.getY() + 5;
                                    }
                                }
                            }

                        }
                    }

                    cluster.setLabelCoordinates(x, y);
                    cluster.setLabelCoordinates(x, y);
                } else {
                    cluster.leaders = false;
                }
            } else {
                ViewerPoint attPoint = cluster.getAttractionPoint();
                cluster.setLabelCoordinates(attPoint.x - cluster.offX,
                        attPoint.y - cluster.offY);
                cluster.setLabelCoordinates(attPoint.x - cluster.offX,
                        attPoint.y - cluster.offY);
            }
        }
    }

    private double findX(Window window, double y, Cluster cluster, double x) {
        Iterator<ViewerPoint> pointIt = window.points.values().iterator();
        Double x1 = null;
        Double x2 = null;
        while (pointIt.hasNext()) {
            ViewerPoint p = pointIt.next();
            double pY = p.getY();
            double pX = p.getX();
            if (pY >= y && pY <= y + cluster.getHeight()) {
                if (x1 == null || x1 > pX) {
                    x1 = pX;
                }
                if (x2 == null || x2 < pX) {
                    x2 = pX;
                }
            }
        }
        Iterator<ViewerLabel> labelIt = window.labels.values().iterator();
        while (labelIt.hasNext()) {
            ViewerLabel l = labelIt.next();
            if (!l.sideLeader) {
                double y1 = l.getY();
                double y2 = l.getY() - l.label.getHeight();
                if ((y1 >= y && y1 <= y + cluster.getHeight()) ||
                        (y2 >= y && y2 <= y + cluster.getHeight())) {
                    if (x1 == null || x1 > l.getX()) {
                        x1 = l.getX();
                    }
                    if (x2 == null || x2 < l.getX() + l.label.getWidth()) {
                        x2 = l.getX() + l.label.getWidth();
                    }
                }
            }
        }

        return (x < window.midX) ? x1 - cluster.width - bufferX : x2 + bufferX;
    }

}
