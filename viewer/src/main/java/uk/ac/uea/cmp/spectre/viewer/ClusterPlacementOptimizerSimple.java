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

import java.util.*;

/**
 * @author balvociute
 */
class ClusterPlacementOptimizerSimple extends ClusterPlacementOptimizer {

    public ClusterPlacementOptimizerSimple() {
    }

    @Override
    public void placeClusterLabels(Set<Cluster> clusters,
                                   Map<Integer, ViewerLabel> labels,
                                   Window window) {
        this.window = window;

        Iterator<Cluster> clusterIt = clusters.iterator();
        while (clusterIt.hasNext()) {
            Cluster cluster = clusterIt.next();
            boolean leader = false;
            Iterator<ViewerPoint> pIt = cluster.points.iterator();
            while (pIt.hasNext()) {
                if (labels.get(pIt.next().id).sideLeader) {
                    leader = true;
                }
            }
            if (leader) {
                pIt = cluster.points.iterator();
                List<ViewerPoint> pp = new LinkedList();

                Double topY = null;
                Double botY = null;
                int x = 0;
                while (pIt.hasNext()) {
                    ViewerPoint p = pIt.next();
                    double y = p.getY();
                    x += p.getX();
                    if (pp.isEmpty()) {
                        pp.add(p);
                    } else {
                        for (int i = 0; i < pp.size(); i++) {
                            if (y < pp.get(i).getY()) {
                                pp.add(i, p);
                                break;
                            }
                            if (i == pp.size() - 1) {
                                pp.add(p);
                                break;
                            }
                        }
                    }
                    if (topY == null || topY < y) {
                        topY = y;
                    }
                    if (botY == null || botY > y) {
                        botY = y;
                    }
                }
                int y = (int) ((topY + botY - cluster.getHeight()) * 0.5);
                x /= pp.size();

                x = (x < window.midX) ? 0 : window.midX * 2 - cluster.width;

                cluster.setLabelCoordinates(x, y);
            }
        }
    }

}
