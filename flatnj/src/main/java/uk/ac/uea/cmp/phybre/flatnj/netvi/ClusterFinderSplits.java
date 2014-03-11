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

package uk.ac.uea.cmp.phybre.flatnj.netvi;


import uk.ac.uea.cmp.phybre.flatnj.ds.Network;
import uk.ac.uea.cmp.phybre.flatnj.fdraw.Edge;
import uk.ac.uea.cmp.phybre.flatnj.fdraw.Vertex;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 *
 * @author balvociute
 */
public class ClusterFinderSplits extends ClusterFinder
{

    @Override
    public Set<Cluster> findClusters(Network network,
                                     Map<Integer, Label> labels,
                                     Map<Integer, Vertex> vertices,
                                     boolean changeColors)
    {
        initializeClusterization(network, labels, vertices, changeColors);
        
        while(!unclusteredVertices.isEmpty())
        {
            iterateClusterization(network, labels, changeColors);
        }
        return clusters;
    }
    
    void initializeClusterization(Network network,
                                     Map<Integer, Label> labels,
                                     Map<Integer, Vertex> vertices,
                                     boolean changeColors)
    {
        unclusteredVertices = new LinkedList();
        clusters = new HashSet();
        
        Iterator<Integer> idIt = labels.keySet().iterator();
        while(idIt.hasNext())
        {
            int id = idIt.next();
            Vertex v = vertices.get(id);
            if(changeColors)
            {
                v.setSize(7);
                v.setBackgroundColor(Color.BLACK);
                v.setLineColor(Color.BLACK);
            }
            unclusteredVertices.add(v);
        }
        
        splitSizeThr = 0.0;
        int nSplits = 0;
        
//        Set<Integer> usedSplits = new HashSet();
//        
//        List<Edge> trivial = network.getTrivial();
//        Iterator<Edge> edgeIt = network.getEdges().iterator();
//        while(edgeIt.hasNext())        
//        {
//            Edge e = edgeIt.next();
//            if(!usedSplits.contains(e.getIdxsplit()) && !trivial.contains(e))
//            {
//                splitSizeThr += e.length();
//                nSplits++;
//                usedSplits.add(e.getIdxsplit());
//            }
//        }
//        splitSizeThr /= nSplits;
    }

    void iterateClusterization(Network network,
                               Map<Integer, Label> labels,
                               boolean changeColors)
    {        
        Vertex vertex = unclusteredVertices.getFirst();
        List<Edge> trivial = network.getTrivial();
        List<GroupingNode> candidates = new LinkedList();
        GroupingNode firstGn = new GroupingNode(vertex, null, 0.0);
        candidates.add(firstGn);
        Cluster cluster = new Cluster();
        Set<Vertex> taken = new HashSet();
        while(!candidates.isEmpty())
        {
            List<GroupingNode> gnList = new LinkedList();
            GroupingNode candidate = candidates.remove(0);
            candidate.distanceFromStart = 0.0;
            gnList.add(candidate);
            Set<Edge> visited = new HashSet();
            while(!gnList.isEmpty())
            {
                GroupingNode gn = gnList.remove(0);
                Vertex v = gn.v;
                taken.add(v);
                if(v.getLabel() != null)
                {
                    int id = v.getNxnum();
                    Label l = labels.get(id);
                    Point p = l.p;
                    cluster.add(p, l, changeColors);
                    unclusteredVertices.remove(v);
                }
                List<Edge> elist = v.getElist();
                for(int i = 0; i < elist.size(); i ++)
                {
                    Edge e = elist.get(i);
                    if(!e.visited)
                    {
                        Vertex w = e.getOther(v);
                        
                        double length = (trivial.contains(e) 
                                         && network.trivialVisible()
                                             || !trivial.contains(e))
                                            ? e.length()
                                            : 0.0;
                        GroupingNode gn2 = new GroupingNode(w, gn, length);
                        if(gn2.distanceFromStart <= splitSizeThr)
                        {
                            e.visited = true;
                            visited.add(e);
                            if(w.getLabel() != null && !taken.contains(w))
                            {
                                candidates.add(gn2);
                                taken.add(w);
                            }
                            gnList.add(gn2);
                        }
                    }
                }
            }
            Iterator<Edge> visitedIt = visited.iterator();
            while(visitedIt.hasNext())            
            {
                Edge edge = visitedIt.next();
                edge.visited = false;
            }
        }
        clusters.add(cluster);
    }
    
}
