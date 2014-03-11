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

import uk.ac.uea.cmp.phybre.flatnj.tools.Utilities;

import java.util.*;

/**
 *
 * @author balvociute
 */
public class LabelPlacementOptimizer4Points implements LabelPlacementOptimizer
{
    protected int defaultOffsetX = 2;
    protected int defaultOffsetY = 2;
    
    protected int pn = 4;
    
    protected int[][] preferedQuarters;

    public LabelPlacementOptimizer4Points()
    {
        preferedQuarters = new int[4][];
        preferedQuarters[0] = new int[] {0, 3, 1, 2};
        preferedQuarters[1] = new int[] {1, 2, 0, 3};
        preferedQuarters[2] = new int[] {2, 1, 3, 0};
        preferedQuarters[3] = new int[] {3, 0, 2, 1};
    }
    
    @Override
    public void placeLabels(Collection<Label> labelSet,
                            Collection<Line> lineSet,
                            Window drawing,
                            boolean all)
    {
        int midX = drawing.midX;
        int midY = drawing.midY;
        
        int n = labelSet.size();
                
        estimateNeighborhoods(labelSet);
        
        Label[] labels = new Label[n];
        Box[] candidatePositions = new Box[pn*n];        
        
        computeInitialPositions(n, labelSet, labels, candidatePositions);
        
        setQuarters(labelSet, midX, midY);
        
        scorePositions(candidatePositions, labels, lineSet);
        
        boolean[][] intersectionGraph = makeIntersectionGraph(n,
                                                            candidatePositions);
        
        Map<Integer, Box> fixedPositions = findTrivialPositions(n,
                                                            labels,
                                                            intersectionGraph,
                                                            preferedQuarters,
                                                            candidatePositions);
        
        resolveRemainingPositions(n,
                                  fixedPositions,
                                  candidatePositions,
                                  intersectionGraph,
                                  labels);
        
        fixPostions(fixedPositions, labels, all);
        LeaderMaker lm = new LeaderMakerSimple();
        lm.placeLeaders(fixedPositions, labels, drawing);
        
    }

    private boolean hasPointInside(Box box, Label[] labels)
    {
        for (Label label : labels) 
        {
            Point p = label.p;
            if(((p.getX() - p.width/2 - box.x1) * (p.getX() - p.width/2 - box.x2) <= 0 ||
                    (p.getX() + p.width/2 - box.x1) * (p.getX() + p.width/2 - box.x2) <= 0)
                    &&
                    ((p.getY() - p.height/2 - box.y1) * (p.getY() - p.height/2 - box.y2) <= 0 ||
                    (p.getY() + p.height/2 - box.y1) * (p.getY() + p.height/2 - box.y2) <= 0))
            {
                return true;
            }
        }
        return false;
    }

    private int intersectsWithLine(Box box, Collection<Line> lineSet)
    {
        double nLines = 0;
        Iterator<Line> lineIt = lineSet.iterator();
        while(lineIt.hasNext())
        {
            Line l = lineIt.next();
                        
            Point p1 = Label.intersectionPoint(l.p1.getX(), l.p1.getY(), l.p2.getX(), l.p2.getY(),
                                       box.x2, box.y2, box.x2, box.y1);
            Point p2 = Label.intersectionPoint(l.p1.getX(), l.p1.getY(), l.p2.getX(), l.p2.getY(),
                                       box.x1, box.y2, box.x2, box.y2);
            Point p3 = Label.intersectionPoint(l.p1.getX(), l.p1.getY(), l.p2.getX(), l.p2.getY(),
                                       box.x1, box.y1, box.x1, box.y2);
            Point p4 = Label.intersectionPoint(l.p1.getX(), l.p1.getY(), l.p2.getX(), l.p2.getY(),
                                       box.x1, box.y1, box.x2, box.y1);
             
            Point i1;
            Point i2;
            
            if(p1 != null)
            {
                i1 = p1;
            }
            else if(p2 != null)
            {
                i1 = p2;
            }
            else if(p3 != null)
            {
                i1 = p3;
            }
            else if(p4 != null)
            {
                i1 = p4;
            }
            else
            {
                continue;
            }
            
            if(p2 != null 
               && (i1.getX() != p2.getX()
                   || i1.getY() != p2.getY()))
            {
                i2 = p2;
            }
            else if(p3 != null 
                    && (i1.getX() != p3.getX()
                        || i1.getY() != p3.getY()))
            {
                i2 = p3;
            }
            else if(p4 != null 
                    && (i1.getX() != p4.getX() 
                        || i1.getY() != p4.getY()))
            {
                i2 = p4;
            }
            else
            {
                if(box.pointInside(l.p1))
                {
                    i2 = l.p1;
                }
                else if(box.pointInside(l.p2))
                {
                    i2 = l.p2;
                }
                else
                {
                    continue;
                }
            }
            nLines += i1.distanceTo(i2)/((double)(box.y1 - box.y2));
        }
        return (int)(nLines + 0.95);
    }

    public static int computeOverlap(Box b1, Box b2)
    {
        double x1 = (b1.x1 > b2.x1) ? b1.x1 : b2.x1;
        double x2 = (b1.x2 < b2.x2) ? b1.x2 : b2.x2;
        double y1 = (b1.y1 < b2.y1) ? b1.y1 : b2.y1;
        double y2 = (b1.y2 > b2.y2) ? b1.y2 : b2.y2;
        
        if(x2 < x1 || y2 > y1)
        {
            return 0;
        }
        else
        {
            double overlap = (x2 - x1) * (y1 - y2);
            double area1 = (b1.x2 - b1.x1) * (b1.y1 - b1.y2);
            double area2 = (b2.x2 - b2.x1) * (b2.y1 - b2.y2);

            return (int) (100*((double)overlap/(double)Math.min(area1,area2)));
        }
    }

    private void computeInitialPositions(int n, Collection<Label> labelSet, Label[] labels, Box[] candidatePositions)
    {
        Iterator<Label> labelIt = labelSet.iterator();
        for(int i = 0; i < n; i ++)
        {
            Label l = labelIt.next();
            
            labels[i] = l;
            
            fillInCandidatePositions(l, i, candidatePositions);
        }
    }

    protected void setQuarters(Collection<Label> labels, int midX, int midY)
    {
        Iterator<Label> labelIt = labels.iterator();
        while(labelIt.hasNext())
        {
            Label l = labelIt.next();
            Point p = l.p;
            p.quarterNo = 0;
            if(p.getX() <= midX && p.getY() <= midY){p.quarterNo = 1;}
            else if(p.getX() <= midX && p.getY() >= midY){p.quarterNo = 2;}
            else if(p.getX() >= midX && p.getY() >= midY){p.quarterNo = 3;}
        }
    }
    
    private void scorePositions(Box[] candidatePositions,
                                Label[] labels,
                                Collection<Line> lineSet)
    {
        for(int i = 0; i < labels.length; i ++)
        {
            Point p = labels[i].p;
            for(int k = 0; k < preferedQuarters[p.quarterNo].length; k ++)
            {
                Box position = candidatePositions[i*pn + preferedQuarters[p.quarterNo][k]];
                position.positionScore = k;
                if(hasPointInside(position, labels))
                {
                    position.pointScore += 100;
                }
                int intersectingLines = intersectsWithLine(position, lineSet);
                position.intersectionScore += 10 * intersectingLines;
            }
            scoreDistancesToOtherPoints(candidatePositions, i, labels);
        }
    }
    
    private boolean[][] makeIntersectionGraph(int n, Box[] candidatePositions)
    {
        boolean[][] intersectionGraph = new boolean[pn*n][pn*n];
        for(int i = 0; i < n; i ++)
        {
            for(int k = 0; k < pn; k++)
            {
                Box posI = candidatePositions[pn*i + k];
                for(int j = i+1; j < n; j ++)
                {
                    for(int l = 0; l < pn; l ++)
                    {
                        Box posJ = candidatePositions[pn*j + l];
                        if(((posI.x1 - posJ.x1)*(posI.x1 - posJ.x2) < 0 ||
                           (posI.x2 - posJ.x1)*(posI.x2 - posJ.x2) < 0)
                                &&
                           ((posI.y1 - posJ.y1)*(posI.y1 - posJ.y2) < 0 ||
                           (posI.y2 - posJ.y1)*(posI.y2 - posJ.y2) < 0))
                        {
                            intersectionGraph[pn*i + k][pn*j + l] = true;
                            intersectionGraph[pn*j + l][pn*i + k] = true;
                        }
                    }
                }
            }
        }
        return intersectionGraph;
    }

    private Map<Integer, Box> findTrivialPositions(int n,
                                                   Label[] labels,
                                                   boolean[][] intersectionGraph,
                                                   int[][] preferedQuarters,
                                                   Box[] candidatePositions)
    {
        Map<Integer,Box> fixedPositions = new HashMap();
        for(int i = 0; i < n; i ++)
        {
            Label l = labels[i];
            Point p = l.p;
            Double minScore = null;
            Integer bestPosition = null;
            for(int j = 0; j < pn; j ++)
            {
                if(Utilities.size(intersectionGraph[i * pn + preferedQuarters[p.quarterNo][j]]) == 0)
                {
                    int index = i*pn + preferedQuarters[p.quarterNo][j];
                    double score = candidatePositions[index].getScore();
                    if(minScore == null || minScore > score)
                    {
                        minScore = score;
                        bestPosition = index;
                    }
                }
            }
            if(bestPosition != null)
            {
                fixedPositions.put(i, candidatePositions[bestPosition]);
                for(int k = 0; k < intersectionGraph.length; k ++)
                {
                    for(int m = 0; m < pn; m ++)
                    {
                        intersectionGraph[pn*i + m][k] = false;
                        intersectionGraph[k][pn*i + m] = false;
                    }
                }
            }
                    
        }
        return fixedPositions;
    }

    private void resolveRemainingPositions(int n, Map<Integer, Box> fixedPositions, Box[] candidatePositions, boolean[][] intersectionGraph, Label[] labels)
    {
        for(int i = 0; i < n; i ++)
        {
            if(!fixedPositions.containsKey(i))
            {
                for(int j = 0; j < pn; j ++)
                {
                    Box posI = candidatePositions[i*pn + j];
                    for(int k = 0; k < n; k ++)
                    {
                        if(k != i)
                        {
                            for(int l = 0; l < pn; l ++)
                            {
                                Box posJ = candidatePositions[k*pn + l];
                                if(intersectionGraph[i*pn + j][k*pn + l] == true)
                                {
                                    int overlap = computeOverlap(posI, posJ);
                                    posI.overlapScore += overlap;
                                }
                            }
                        }
                    }
                }
                
                
                Integer min = null;
                for(int j = 0; j < pn; j ++)
                {
                    if(min == null || min > candidatePositions[i*pn + j].getScore())
                    {
                        min = candidatePositions[i*pn + j].getScore();
                    }
                }
                for(int j = 0; j < pn; j ++)
                {
                    if(candidatePositions[i*pn + j].getScore() == min)
                    {
                        fixedPositions.put(i, candidatePositions[i*pn + j]);
                        for(int k = 0; k < pn; k ++)
                        {
                            if(k != j)
                            {
                                for(int l = 0; l < intersectionGraph.length; l ++)
                                {
                                    if(l != i*pn + j)
                                    {
                                        intersectionGraph[i*pn + k][l] = false;
                                        intersectionGraph[l][i*pn + k] = false;
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
        
        int[] ids = new int[fixedPositions.size()];
        int q = 0;
        Iterator<Integer> idIt = fixedPositions.keySet().iterator();
        while(idIt.hasNext())
        {
            ids[q++] = idIt.next();
        }
        
        for(int optimize = 0; optimize < 1; optimize ++)
        {
            for(int j = 0; j < ids.length; j ++)
            {
                int i = ids[j];
                Box b = fixedPositions.get(i);
                b.scoreDistances(fixedPositions.values());
                if(b.getScoreWithDistance() > 20)
                {
                    for(int k = 0; k < pn; k ++)
                    {
                        Box candidateB = candidatePositions[i*pn + k];
                        //if(candidateB.getScore() - b.getScore() < 10)
                        {
                            double currentScore = scoreSystem(fixedPositions, labels,
                                                              labels[i].p.neighborhood);


                            fixedPositions.remove(i);
                            fixedPositions.put(i, candidateB);

                            double newScore = scoreSystem(fixedPositions, labels,
                                                          labels[i].p.neighborhood);

                            if(currentScore < newScore)
                            {
                                fixedPositions.remove(i);
                                fixedPositions.put(i, b);
                            }
                        }
                    }
                }
            }
        }
        
    }

    protected void fillInCandidatePositions(Label l, int i, Box[] candidatePositions)
    {
        Point p = l.p;
        
        int j = 0;
        double xPlus = p.getX() + p.width/2 + defaultOffsetX;
        double xMinus = p.getX() - p.width/2 - defaultOffsetX - l.label.getWidth();
        double yPlus = p.getY() - p.height/2 - defaultOffsetY;
        double yMinus = p.getY() + p.height/2 + defaultOffsetY + l.label.getHeight();

        candidatePositions[i*pn + j++] = new Box(xPlus, yPlus, l.label.getWidth(), l.label.getHeight(), l);
        candidatePositions[i*pn + j++] = new Box(xMinus, yPlus, l.label.getWidth(), l.label.getHeight(), l);
        candidatePositions[i*pn + j++] = new Box(xMinus, yMinus, l.label.getWidth(), l.label.getHeight(), l);
        candidatePositions[i*pn + j] = new Box(xPlus, yMinus, l.label.getWidth(), l.label.getHeight(), l);
    }

    private void fixPostions(Map<Integer, Box> fixedPositions, Label[] labels, boolean all)
    {
        for(int i = 0; i < labels.length; i ++)
        {
            Label l = labels[i];
            if(l.label.movable || all)
            {
                Box b = fixedPositions.get(i);
                l.computeOffsets(b.x1, b.y1, Arrays.asList(labels));
                l.setAutomaticMovable(true);
            }
        }
    }
    
    private double scoreSystem(Map<Integer, Box> fixedPositions,
                               Label[] labels,
                               Set<Label> neighborhood)
    {
        double currentSystemScore = 0;
        for(int i = 0; i < labels.length; i ++)
        {
            if(neighborhood.contains(labels[i]))
            {
                Box b2 = fixedPositions.get(i);
                b2.recomputeScore(fixedPositions.values());
                currentSystemScore += b2.getScoreWithDistance();
            }
        }
        return currentSystemScore;
    }

    private void estimateNeighborhoods(Collection<Label> labelSet)
    {
        Iterator<Label> labelIt = labelSet.iterator();
        List<Label> labelsToCheck = new LinkedList();
        while(labelIt.hasNext())
        {
            Label l = labelIt.next();
            Point p = l.p;
            p.neighborhood = new HashSet();
            for(int i = 0; i < labelsToCheck.size(); i ++)
            {
                Label l2 = labelsToCheck.get(i);
                Point p2 = l2.p;
                int x = p.width/2 + p2.width/2 + 2*defaultOffsetX + l.label.getWidth() + l2.label.getWidth();
                double distanceThreshold = Math.sqrt(x*x + x*x);
                if(p.distanceTo(p2) <= distanceThreshold)
                {
                    p.neighborhood.add(p2.l);
                    p2.neighborhood.add(p.l);
                }
            }
            labelsToCheck.add(l);
        }
    }

    private void scoreDistancesToOtherPoints(Box[] candidatePositions,
                                             int labelId,
                                             Label[] labels)
    {
        if(labels.length > 1)
        {
            double[] distances = new double[pn];
            List<Integer> sorted = new LinkedList();
            for(int i = 0; i < pn; i ++)
            {
                Double min = null;
                for(int j = 0; j < labels.length; j ++)
                {
                    if(j != labelId)
                    {
                        double dist = labels[j].p.distanceTo(candidatePositions[i].middlePoint());
                        if(min == null || min > dist)
                        {
                            min = dist;
                        }
                    }
                }
                distances[i] = min;
                boolean added = false;
                for(int j = 0; j < sorted.size(); j ++)
                {
                    if(min > distances[sorted.get(j)])
                    {
                        sorted.add(j, i);
                        added = true;
                        break;
                    }
                }
                if(!added)
                {
                    sorted.add(i);
                }
            }
            for(int i = 0; i < sorted.size(); i ++)
            {
                candidatePositions[labelId*pn + sorted.get(i)].positionScore += i;
            }
        }
    }
    
    protected static class Box
    {
        double x1;
        double y1;
        double x2;
        double y2;
        
        Label l;
        
        int intersectionScore;
        int pointScore;
        int overlapScore;
        int positionScore;
        double distanceScore;

        public Box(double x1, double y1, int width, int height, Label l)
        {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x1 + width;
            this.y2 = y1 - height;
            
            this.l = l;
        }

        public int getScore()
        {
            return intersectionScore + pointScore + overlapScore + positionScore;
        }
        
        public double getScoreWithDistance()
        {
            return getScore() + distanceScore;
        }

        private void scoreDistances(Collection<Box> fixedPositions)
        {
            distanceScore = 0;
            Iterator<Box> boxIt = fixedPositions.iterator();
            while(boxIt.hasNext())
            {                
                Box b = boxIt.next();
                if(b != this)
                {
                    distanceScore += (y1-y2) / distanceTo(b);
                }
            }
        }

        private void scoreOverlaps(Collection<Box> fixedPositions)
        {
            overlapScore = 0;
            Iterator<Box> boxIt = fixedPositions.iterator();
            while(boxIt.hasNext())
            {                
                Box b = boxIt.next();
                if(b != this && l.p.neighborhood.contains(b.l))
                {
                    overlapScore += computeOverlap(this, b);
                }
            }
        }
        
        private double distanceTo(Box b)
        {
            double midX = (x2 + x1)/2;
            double midXB = (b.x2 + b.x1)/2;
            double midY = (y1 + y2)/2;
            double midYB = (b.y1 + b.y2)/2;
            return Math.sqrt((midX - midXB)*(midX - midXB) + (midY - midYB)*(midY - midYB));
        }

        private void recomputeScore(Collection<Box> values)
        {
            scoreDistances(values);
            scoreOverlaps(values);
        }

        private Point middlePoint()
        {
            return new Point((int)(0.5*(x1 + x2)), (int)(0.5*(y1 + y2)));
        }

        private boolean pointInside(Point p1)
        {
            return (p1.getX() >= x1 && p1.getX() <= x2
                    && p1.getY() >= y2 && p1.getY() <= y1);
        }
    }
    
}
