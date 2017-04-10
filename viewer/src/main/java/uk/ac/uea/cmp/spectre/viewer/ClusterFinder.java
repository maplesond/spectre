/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2017  UEA School of Computing Sciences
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

import uk.ac.uea.cmp.spectre.core.ds.network.Network;
import uk.ac.uea.cmp.spectre.core.ds.network.Vertex;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * @author balvociute
 */
class ClusterFinder {

    protected double splitSizeThr;
    protected LinkedList<Vertex> unclusteredVertices;
    protected Set<Cluster> clusters;

    public ClusterFinder() {
        unclusteredVertices = new LinkedList<>();
        clusters = new HashSet<>();
    }


    public Set<Cluster> findClusters(Network network,
                                     Map<Integer, ViewerLabel> labels,
                                     Map<Integer, Vertex> vertices,
                                     boolean changeColors,
                                     boolean trivialVisible) {
        clusters = new HashSet();
        for (ViewerLabel l : labels.values()) {
            Cluster c = new Cluster();
            c.add(l.p, l, false);
            clusters.add(c);
        }
        return clusters;
    }

}
