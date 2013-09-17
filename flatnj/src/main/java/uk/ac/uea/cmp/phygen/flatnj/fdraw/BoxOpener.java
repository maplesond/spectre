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

package uk.ac.uea.cmp.phygen.flatnj.fdraw;

import uk.ac.uea.cmp.phygen.flatnj.ds.Network;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author balvociute
 */
public class BoxOpener
{

    AngleCalculator angleCalculator;
    private int nr = 0;

    public BoxOpener(AngleCalculator ac)
    {
        nr = 0;
        this.angleCalculator = ac;
    }

    public double openIncompatible(int[] activeSplits,
                                   Vertex v,
                                   SplitSystemDraw ss,
                                   LinkedList<Vertex> vertices,
                                   TreeSet[] splitedges,
                                   Network network)
    {
        Double maxAngle = null;
        for (int i = 0; i < activeSplits.length; i++)
        {
            int S = activeSplits[i];
            LinkedList<Edge> edges = DrawFlat.collect_edges_for_split(S, v);
            if (edges.size() > 1)
            {
                double angle = open(activeSplits, S, ss, edges, splitedges, v, vertices, network);
                if(maxAngle == null || maxAngle < angle)
                {
                    maxAngle = angle;
                }
            }
        }
        return maxAngle;
    }
   
        public void openOneIncompatible(int[] activeSplits, Vertex v, SplitSystemDraw ss, LinkedList<Vertex> vertices, TreeSet[] splitedges, Network network)
    {
        boolean foundSplit = false;
        while(!foundSplit)
        {
            int S = activeSplits[nr++];
            LinkedList<Edge> edges = DrawFlat.collect_edges_for_split(S, v);
            if (edges.size() > 1)
            {
                foundSplit = true;
                open(activeSplits, S, ss, edges, splitedges, v, vertices, network);
            }
            if(nr == activeSplits.length)
            {
                nr = 0;
            }
        }
    }
    

    public double open(int[] activeSplits,
                       int S,
                       SplitSystemDraw ss,
                       LinkedList<Edge> edges,
                       TreeSet[] splitedges,
                       Vertex v,
                       LinkedList<Vertex> vertices,
                       Network network)
    {
        double actualAngle = 0.0;
        
        //Angle that will be added to the current one
        double deltaAlpha = 0;
        
        boolean moved = false;

        if (edges.size() > 1)
        {

            //Collect all the boxes induced by this split
            LinkedList<NetworkBox> boxesSorted = collectAndSortBoxesForTheSplit(activeSplits, ss, S, splitedges);

            //Just print the number of splits that the current one is incompatible
            //with
            //System.out.println("Split no. " + S + " is incompatible with " + boxesSorted.size() + " splits.");

            
            deltaAlpha = angleCalculator.computeOptimalAngle(boxesSorted, edges, true);

            if (deltaAlpha != 0)
            {

                Set<Edge> topEdges = Collector.getAllEdges(edges, true);
                Set<Edge> bottomEdges = Collector.getAllEdges(edges, false);

                
                moved = tryAngle(edges.getFirst().bot, edges.getFirst().top, edges.getFirst(), edges, topEdges, bottomEdges, deltaAlpha, vertices);
                if(!moved)
                {
                    moved = tryAngle(edges.getFirst().top, edges.getFirst().bot, edges.getFirst(), edges, bottomEdges, topEdges, deltaAlpha, vertices);
                }

                if(!moved)
                {
                    
                    //Define leftmost and rightmost egdes of the current split
                    Edge leftmost = boxesSorted.getFirst().e1;
                    Edge rightmost = boxesSorted.getLast().e2;
                    //Collect all external egdes that are below current split
                    Set<Edge> bottomExternalEdges = network.getExternalEdges(rightmost, rightmost.top, leftmost);
                    //Collect all the external edges that are above 
                    Set<Edge> topExternalEdges = network.getExternalEdges(leftmost, leftmost.bot, rightmost);


                    //Collect all the external vertices that are below the current split
                    Set<Vertex> bottomVertices = Collector.getExternalVertices(rightmost, rightmost.top, leftmost);
                    //Collect all the external vertices that are above
                    Set<Vertex> topVertices = Collector.getExternalVertices(leftmost, leftmost.bot, rightmost);



                    //Check if any of the 'bottom' vertices are above the split and 
                    //if any of the 'top' ones are below
                    if (Translocator.isUpperPartFreeFromBottomVertices(leftmost, rightmost, bottomExternalEdges) && Translocator.isBottomPartFreeFromTopVertices(leftmost, rightmost, topExternalEdges))
                    {
                        double safeAngleBot = angleCalculator.getSafeAngleBot(deltaAlpha, leftmost, rightmost, bottomVertices, topVertices);
                        double safeAngleTop = angleCalculator.getSafeAngleTop(deltaAlpha, leftmost, rightmost, bottomVertices, topVertices);
                        
                        if (safeAngleBot != 0)
                        {
                            moved = tryAngle(edges.getFirst().bot, edges.getFirst().top, edges.getFirst(), edges, topEdges, bottomEdges, safeAngleBot, vertices);
                            if(moved)
                            {
                                deltaAlpha = safeAngleBot;
                            }
                        }
                        if(!moved && safeAngleTop != 0)
                        {
                            moved = tryAngle(edges.getFirst().top, edges.getFirst().bot, edges.getFirst(), edges, bottomEdges, topEdges, safeAngleTop, vertices);
                            if(moved)
                            {
                                deltaAlpha = safeAngleTop;
                            }
                        }
                        
                    }
                }
            }

        }
        if(moved)
        {
            actualAngle = deltaAlpha;
        }
        return actualAngle;
    }

    private boolean tryAngle(Vertex bot, Vertex top, Edge e, LinkedList<Edge> edges, Set<Edge> topEdges, Set<Edge> bottomEdges, double deltaAlpha, List<Vertex> vertices)
    {
        boolean moved = false;
        double gap = Math.PI / 20;
        double angleWithGap = (edges.size() == 1) ? deltaAlpha : Math.signum(deltaAlpha) * (Math.abs(deltaAlpha) + gap);
        if(Translocator.noCollisions(bot, top, e, edges, topEdges, bottomEdges, angleWithGap) && Translocator.noCollisions(bot, top, e, edges, topEdges, bottomEdges, deltaAlpha))
        {
            Translocator.changeCoordinates(bot, top, edges, deltaAlpha);
            moved = true;
            for (int i = 0; i < vertices.size(); i++)
            {
                vertices.get(i).visited = false;
                LinkedList<Edge> vEd = vertices.get(i).elist;
                for (int k = 0; k < vEd.size(); k++)
                {
                    vEd.get(k).visited = false;
                }
            }
            
        }
        return moved;
    }


    public double move(int[] activeSplits, int S, SplitSystemDraw ss, LinkedList<Edge> edges, TreeSet[] splitedges, Vertex v, LinkedList<Vertex> vertices)
    {
        Edge e = edges.getFirst();

        //Gap that protects vertices and edges from touching
        double gap = Math.PI / 20;


        //Angle that will be added to the current one
        double deltaAlpha = angleCalculator.computeMiddleAngleForTrivial(e, e.bot, e.top);
        
        
        if (deltaAlpha != 0)
        {

            Set<Edge> topEdges = Collector.getAllEdges(edges, true);
            Set<Edge> bottomEdges = Collector.getAllEdges(edges, false);

            double angleWithGap = (edges.size() == 1) ? deltaAlpha : Math.signum(deltaAlpha) * (Math.abs(deltaAlpha) + gap);

            if (Translocator.noCollisions(edges, topEdges, bottomEdges, angleWithGap))
            {
                Translocator.changeCoordinates(edges, deltaAlpha);
                for (int i = 0; i < vertices.size(); i++)
                {
                    vertices.get(i).visited = false;
                    LinkedList<Edge> vEd = vertices.get(i).elist;
                    for (int k = 0; k < vEd.size(); k++)
                    {
                        vEd.get(k).visited = false;
                    }
                }
            }
        }
        return deltaAlpha;
    }

    private LinkedList<NetworkBox> collectAndSortBoxesForTheSplit(int[] activeSplits, SplitSystemDraw ss, int S, TreeSet[] splitedges)
    {
        LinkedList<NetworkBox> boxes = new LinkedList();
        for (int i2 = 0; i2 < activeSplits.length; i2++)
        {
            int Si = activeSplits[i2];

            if (ss.is_compatible(S, Si) == -1)
            {
                NetworkBox bi = DrawFlat.form_box(S, Si, splitedges);
                if (bi != null)
                {
                    boxes.add(bi);
                }
            }
        }
        LinkedList<NetworkBox> boxesSorted = new LinkedList();

        while (!boxes.isEmpty())
        {
            if (boxesSorted.isEmpty())
            {
                boxesSorted.add(boxes.removeFirst());
            }
            else
            {
                for (int i = 0; i < boxes.size(); i++)
                {
                    NetworkBox currentNotInserted = boxes.get(i);
                    for (int k = 0; k < boxesSorted.size(); k++)
                    {
                        NetworkBox currentInserted = boxesSorted.get(k);
                        if (currentNotInserted.e1.bot.equals(currentInserted.e2.bot))
                        {
                            boxesSorted.add(k + 1, currentNotInserted);
                            boxes.remove(currentNotInserted);
                            break;
                        }
                        else if (currentNotInserted.e2.bot.equals(currentInserted.e1.bot))
                        {
                            boxesSorted.add(k, currentNotInserted);
                            boxes.remove(currentNotInserted);
                            break;
                        }
                    }
                }
            }
        }

        return boxesSorted;
    }
}
