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

package uk.ac.uea.cmp.phybre.flatnj.ds;

import uk.ac.uea.cmp.phybre.flatnj.fdraw.Collector;
import uk.ac.uea.cmp.phybre.flatnj.fdraw.DrawFlat;
import uk.ac.uea.cmp.phybre.flatnj.fdraw.Edge;
import uk.ac.uea.cmp.phybre.flatnj.fdraw.Vertex;

import java.util.*;

/**
 * @author balvociute
 */
public class Network {
    private List<Vertex> allVertices;
    private List<Vertex> labeledVertices;

    private List<Edge> allEdges;
    private List<Edge> externalEdges;
    private List<Edge> internalEdges;
    private List<Edge> trivialEdges;
    private List<Edge> compatible;

    private Leaders leaders;

    private int nTaxa;
    private double rotationAngle;
    private double ratio;

    boolean trivialVisible = true;

    public Network(Vertex v) {
        allVertices = DrawFlat.collect_vertices(v);
        labeledVertices = new LinkedList<>();
        for (int i = 0; i < allVertices.size(); i++) {
            Vertex w = allVertices.get(i);
            if (w.getTaxa().size() > 0 || w.getLabel() != null) {
                labeledVertices.add(w);
            }
        }

        allEdges = DrawFlat.collect_edges(v.getFirstEdge());
        externalEdges = Collector.externalEdges(v);
        trivialEdges = new LinkedList<>();
        internalEdges = new LinkedList<>();
        for (int i = 0; i < allEdges.size(); i++) {
            Edge e = allEdges.get(i);
            if (e.getBot().getElist().size() == 1 || e.getTop().getElist().size() == 1) {
                trivialEdges.add(e);
                while (externalEdges.remove(e)) ;
            } else {
                if (!externalEdges.contains(e)) {
                    internalEdges.add(e);
                }
            }
        }
        for (int i = 0; i < externalEdges.size(); i++) {
            Edge e = externalEdges.get(i);
            List<Edge> split = DrawFlat.collect_edges_for_split(e.getIdxsplit(), v);
            if (split.size() == 1 && e.getBot().getElist().size() > 1 && e.getTop().getElist().size() > 1) {
                e.compatible = true;
            }
        }
    }

    public Network(Vertex[] vertices, Edge[] edges) {
        allVertices = new LinkedList<>();
        allVertices.addAll(Arrays.asList(vertices));

        labeledVertices = new LinkedList<>();
        for (int i = 0; i < allVertices.size(); i++) {
            Vertex w = allVertices.get(i);
            if (w.getTaxa().size() > 0 || w.getLabel() != null) {
                labeledVertices.add(w);
            }
        }

        allEdges = new LinkedList<>();
        allEdges.addAll(Arrays.asList(edges));
        externalEdges = Collector.externalEdges(allVertices);
        trivialEdges = new LinkedList<>();
        internalEdges = new LinkedList<>();
        for (Iterator<Edge> it = allEdges.iterator(); it.hasNext(); ) {
            Edge e = it.next();
            if (e != null) {
                if (e.getBot().getElist().size() == 1 || e.getTop().getElist().size() == 1) {
                    trivialEdges.add(e);
                    while (externalEdges.remove(e)) ;
                } else {
                    if (!externalEdges.contains(e)) {
                        internalEdges.add(e);
                    }
                }
            }
        }
        for (int i = 0; i < externalEdges.size(); i++) {
            Edge e = externalEdges.get(i);
            List<Edge> split = DrawFlat.collect_edges_for_split(e.getIdxsplit(), allEdges);
            if (split.size() == 1 && e.getBot().getElist().size() > 1 && e.getTop().getElist().size() > 1) {
                e.compatible = true;
            }
        }
    }

    public List<Edge> getEdges() {
        return new LinkedList<>(allEdges);
    }

    public List<Vertex> getVertices() {
        return new LinkedList<>(allVertices);
    }

    public List<Edge> getExternal() {
        return new LinkedList<>(externalEdges);
    }

    public List<Edge> getInternal() {
        return new LinkedList<>(internalEdges);
    }

    public List<Vertex> getLabeled() {
        return new LinkedList<>(labeledVertices);
    }

    public List<Edge> getTrivial() {
        return new LinkedList<>(trivialEdges);
    }

    public void removeVertices(LinkedList<Vertex> vertices) {
        Iterator<Vertex> vertexIt = vertices.iterator();
        while (vertexIt.hasNext()) {
            labeledVertices.remove(vertexIt.next());
        }
    }

    public void addTrivial(LinkedList<Vertex> vertices) {
        Iterator<Vertex> vertexIt = vertices.iterator();
        while (vertexIt.hasNext()) {
            Vertex v = vertexIt.next();
            allVertices.add(v);
            labeledVertices.add(v);
            trivialEdges.add(v.getFirstEdge());
            allEdges.add(v.getFirstEdge());
        }
    }

    public Set<Edge> getExternalEdges(Edge e1, Vertex a, Edge e2) {
        Set<Edge> subset = new HashSet<>();
        subset.add(e1);
        Edge current = e1;
        int index = externalEdges.indexOf(e1);

        int indexBefore = (index > 0) ? index - 1 : externalEdges.size() - 1;
        //Last vertex that was added to the top vertices list
        Vertex last = null;
        //While current edge is not the last one that we need to visit:

        boolean forward = (externalEdges.get(indexBefore).getBot() == a || externalEdges.get(indexBefore).getTop() == a) ? true : false;

        while (current != e2) {
            if (forward) {
                index++;
                if (index == externalEdges.size()) {
                    index = 0;
                }
            } else {
                index--;
                if (index == -1) {
                    index = externalEdges.size() - 1;
                }
            }
            current = externalEdges.get(index);
            subset.add(current);
        }

        return subset;
    }

    public void setNTaxa(Integer nTax) {
        nTaxa = nTax;
    }

    public int getNTaxa() {
        return nTaxa;
    }

    public void setRotationAngle(double rotationAngle) {
        this.rotationAngle = rotationAngle;
    }

    public double getRotationAngle() {
        return rotationAngle;
    }

    public boolean veryLongTrivial() {
        double avgLength = 0;
        for (int i = 0; i < internalEdges.size(); i++) {
            avgLength += internalEdges.get(i).length();
        }
        for (int i = 0; i < externalEdges.size(); i++) {
            avgLength += externalEdges.get(i).length();
        }
        avgLength /= (internalEdges.size() + externalEdges.size());

        double trLength = 0;
        for (int i = 0; i < trivialEdges.size(); i++) {
            trLength += trivialEdges.get(i).length();
        }
        trLength /= trivialEdges.size();
        if (trivialEdges.size() > nTaxa * 0.8 && trLength / avgLength > 10) {
            return true;
        } else {
            return false;
        }
    }

    public void setLeaders(Leaders leaders) {
        this.leaders = leaders;
    }

    public Leaders getLeaders() {
        return leaders;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public double getRatio() {
        return ratio;
    }

    public Map<Integer, boolean[]> getSplits() {
        List<String> allLabels = new LinkedList<>();
        for (int i = 0; i < labeledVertices.size(); i++) {
            uk.ac.uea.cmp.phybre.flatnj.fdraw.Label l = labeledVertices.get(i).getLabel();
            if (l != null) {
                String[] labels = l.getName().split("\\s*,\\s*");
                for (int j = 0; j < labels.length; j++) {
                    allLabels.add(labels[j]);
                }
            }
        }
        String[] sortedLabels = new String[allLabels.size()];
        allLabels.toArray(sortedLabels);
        Arrays.sort(sortedLabels);
        Map<String, Integer> indexes = new HashMap<>();
        for (int i = 0; i < sortedLabels.length; i++) {
            indexes.put(sortedLabels[i], i);
        }

        Map<Integer, boolean[]> intSplits = new HashMap<>();

        Map<Integer, List<Edge>> splits = new HashMap<>();
        for (int i = 0; i < allEdges.size(); i++) {
            int id = allEdges.get(i).getIdxsplit();
            if (!splits.containsKey(id)) {
                splits.put(id, new LinkedList<Edge>());
            }
            splits.get(id).add(allEdges.get(i));
        }


        Iterator<Integer> sIt = splits.keySet().iterator();
        while (sIt.hasNext()) {
            boolean[] intSplit = new boolean[nTaxa];
            int sId = sIt.next();
            List<Edge> eIt = splits.get(sId);
            for (int i = 0; i < eIt.size(); i++) {
                eIt.get(i).visited = true;
            }
            List<Vertex> vv = new LinkedList<>();
            vv.add(eIt.get(0).getBot());
            Set<String> taxa = new HashSet<>();
            while (!vv.isEmpty()) {
                Vertex v = vv.remove(0);

                uk.ac.uea.cmp.phybre.flatnj.fdraw.Label l = v.getLabel();
                if (l != null) {
                    String[] labels = l.getName().split("\\s*,\\s*");
                    for (int i = 0; i < labels.length; i++) {
                        intSplit[indexes.get(labels[i])] = true;
                    }
                }

                List<Edge> edges = v.getElist();
                for (int i = 0; i < edges.size(); i++) {
                    Edge e = edges.get(i);
                    if (!e.visited) {
                        vv.add(e.getOther(v));
                        e.visited = true;
                    }
                }
            }

            for (int i = 0; i < allEdges.size(); i++) {
                allEdges.get(i).visited = false;
            }

            if (!intSplit[0]) {
                for (int i = 0; i < intSplit.length; i++) {
                    intSplit[i] = (intSplit[i]) ? false : true;
                }
            }

            System.out.println(sId + "\t" + Arrays.toString(intSplit));

            intSplits.put(sId, intSplit);

        }
        return intSplits;
    }

    public void showTrivial(boolean b) {
        trivialVisible = b;
    }

    public boolean trivialVisible() {
        return trivialVisible;
    }

    public List<Edge> getCompatible() {
        List<Edge> compatible = new LinkedList<>();
        for (int i = 0; i < allEdges.size(); i++) {
            Edge e = allEdges.get(i);
            if (e.compatible) {
                compatible.add(e);
            }
        }
        return compatible;
    }

    public void setLabeled(List<Vertex> labeled) {
        this.labeledVertices = labeled;
    }
}
