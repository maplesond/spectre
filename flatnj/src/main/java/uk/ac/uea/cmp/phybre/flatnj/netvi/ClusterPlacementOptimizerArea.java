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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author balvociute
 */
class ClusterPlacementOptimizerArea extends ClusterPlacementOptimizer
{

    public ClusterPlacementOptimizerArea()
    {
    }

    @Override
    public void placeClusterLabels(Set<Cluster> clusters,
                                   Map<Integer, Label> labels,
                                   Window window)
    {
        this.window = window;
        int step = 10;
        int exclusion = 70;
        double[][] rays = computeGrid(window, step);
        
        Iterator<Cluster> clusterIt = clusters.iterator();
        while(clusterIt.hasNext())        
        {
            Cluster cluster = clusterIt.next();
            if(cluster.isMovable() && cluster.size() == 1)
            {
                boolean leader = false;
                Iterator<Point> pIt = cluster.points.iterator();
                while(pIt.hasNext())            
                {
                    if(labels.get(pIt.next().id).sideLeader)
                    {
                        leader = true;
                    }
                }
                if(leader)
                {
                    findPosition(cluster, rays, step);
                }
            }
        }
    }

    private void checkRays(double[][] rays, int step, Cluster cluster)
    {
        
        for(int i = 0; i < rays.length; i ++)
        {
            for(int j = 0; j < rays[i].length; j ++)
            {
                Point p = new Point(i*step, j*step);
                if(p.getX() >= cluster.x 
                   && p.getX() <= cluster.x + cluster.width
                   && p.getY() >= cluster.y
                   && p.getY() <= cluster.y + cluster.height)
                {
                    rays[i][j] = 0;
                }
                else
                {
                    Point closest = cluster.closestPoint(p);
                    double dist = closest.distanceTo(p);
                    if(rays[i][j] > dist)
                    {
                        rays[i][j] = dist;
                    }
                }
            }
        }
    }

    private void findPosition(Cluster cluster, double[][] rays, int step)
    {
        int exclusion;
        double clusterRay = cluster.getRay();
        Point att = cluster.getAttractionPoint();
        Point min = new Point(0, 0);
        Point furthest = cluster.furthestPoint(att);
        double dist = furthest.distanceTo(att);
        exclusion = (int) (cluster.height * 0.75);
        int minX = (int) (att.getX() - dist - exclusion);
        int maxX = (int) (att.getX() + dist + exclusion);
        for(int i = 0; i < rays.length; i ++)
        {
            if(step*i < minX || step*i > maxX)
            {
                for(int j = 0; j < rays[i].length; j ++)
                {
                    if(rays[i][j] > clusterRay + 1)
                    {
                        Point pp = new Point(i*step, j*step);
                        if(att.distanceTo(pp) < att.distanceTo(min))
                        {
                            min = pp;
                        }
                    }
                }
            }
        }
        cluster.setXY(min.getX() - cluster.width/2,
                      min.getY() - cluster.height/2);
        checkRays(rays, step, cluster);
        cluster.setLabelCoordinates(min.getX() - cluster.width/2,
                                    min.getY() - cluster.height/2);
        cluster.setLabelCoordinates(min.getX() - cluster.width/2,
                                    min.getY() - cluster.height/2);
    }

    private double[][] computeGrid(Window window, int step)
    {
        double[][] rays = new double[window.getWidth()/step]
                                    [window.getHeight()/step];
        for(int i = 0; i < rays.length; i ++)
        {
            for(int j = 0; j < rays[i].length; j ++)
            {
                Point p = new Point(i*step, j*step);
                rays[i][j] = p.distanceToBoundary(window.getBounds());
                Iterator<Line> lineIt = window.lines.values().iterator();
                while(lineIt.hasNext())                
                {
                    Line line = lineIt.next();
                    Point closest = line.closestPoint(p);
                    double dist = p.distanceTo(closest);
                    if(dist < rays[i][j])
                    {
                        rays[i][j] = dist;
                    }
                }
            }
        }
        return rays;
    }
    
}
