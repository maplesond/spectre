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

package uk.ac.uea.cmp.spectre.core.ds.network;

import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;

import java.util.*;

/**
 * Created by dan on 20/03/14.
 */
public class FlatNetwork implements Network {


    private VertexList vertices;
    private EdgeList edges;

    /**
     * This variable is essentially a cache of the results of that
     * computation, if it is requested.
     */
    private EdgeList internalEdges;
    private EdgeList externalEdges;
    private EdgeList trivialEdges;

    private IdentifierList taxa;

    private VertexList labeledVertices;

    private Map<Integer, String> translate;
    private List<NetworkLabel> vLabels;


    public FlatNetwork() {
        this(new VertexList(), new EdgeList());
    }

    public FlatNetwork(VertexList vertices, EdgeList edges) {

        this.vertices = vertices;
        this.edges = edges;

        this.internalEdges = new EdgeList();
        this.externalEdges = new EdgeList();
        this.trivialEdges = new EdgeList();

        this.taxa = null;

        this.labeledVertices = new VertexList();
        this.vLabels = new ArrayList<>();

        this.translate = new HashMap<>();

        this.classifyVertices();

        // May take some time
        this.classifyEdges();
    }

    public FlatNetwork(Vertex v) {
        this(v.collectVertices(), v.getFirstEdge().collectEdges());
        this.setupLabels();
    }

    @Override
    public void setTaxa(IdentifierList taxa) {
        this.taxa = taxa;
    }

    @Override
    public IdentifierList getTaxa() {
        return this.taxa;
    }

    public void setVertices(VertexList vertices) {
        this.vertices = vertices;
    }

    @Override
    public VertexList getAllVertices() {
        return vertices;
    }

    @Override
    public VertexList getLabeledVertices() {
        return this.labeledVertices;
    }

    public void setEdges(EdgeList edges) {
        this.edges = edges;
    }

    @Override
    public EdgeList getAllEdges() {
        return this.edges;
    }

    @Override
    public EdgeList getTrivialEdges() {
        return this.trivialEdges;
    }

    @Override
    public EdgeList getInternalEdges() {
        return this.internalEdges;
    }

    @Override
    public EdgeList getExternalEdges() {
        return this.externalEdges;
    }

    public Map<Integer, String> getTranslate() {
        return translate;
    }

    public void setTranslate(Map<Integer, String> translate) {
        this.translate = translate;
    }

    public List<NetworkLabel> getVertexLabels() {
        return vLabels;
    }

    public void setVertexLabels(List<NetworkLabel> vLabels) {
        this.vLabels = vLabels;
    }

    @Override
    public Set<Edge> getExternalEdges(Edge e1, Vertex a, Edge e2) {
        Set<Edge> subset = new HashSet<>();
        subset.add(e1);
        Edge current = e1;
        int index = externalEdges.indexOf(e1);

        int indexBefore = (index > 0) ? index - 1 : externalEdges.size() - 1;
        //Last vertex that was added to the top vertices list
        Vertex last = null;
        //While current edge is not the last one that we need to visit:

        boolean forward = (externalEdges.get(indexBefore).getBottom() == a || externalEdges.get(indexBefore).getTop() == a) ? true : false;

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


    @Override
    public List<NetworkLabel> getLabels() {

        List<NetworkLabel> labels = new ArrayList<>();

        for(Vertex v : vertices) {
            labels.add(v.getLabel());
        }

        return labels;
    }

    @Override
    public int getNbTaxa() {
        return this.taxa == null || this.taxa.isEmpty() ? this.labeledVertices.size() : this.taxa.size();
    }

    public void classifyVertices() {
        this.labeledVertices = new VertexList();
        for (Vertex v : vertices) {
            if (v.getTaxa().size() > 0 || v.getLabel() != null) {
                labeledVertices.add(v);
            }
        }
    }

    public void classifyEdges() {

        this.externalEdges = this.classifyExternalEdges(this.vertices);

        // Move some external edges to the trivial edge list
        // Classifies all other edges (i.e. non external or trivial edges) as internal
        for (Edge e : this.edges) {
            if (e.getBottom().getEdgeList().size() == 1 || e.getTop().getEdgeList().size() == 1) {
                this.trivialEdges.add(e);
                while (this.externalEdges.remove(e)) ;
            } else {
                if (!this.externalEdges.contains(e)) {
                    this.internalEdges.add(e);
                }
            }
        }

        // Set compatible property on those edges that are compatible
        for (int i = 0; i < externalEdges.size(); i++) {
            Edge e = externalEdges.get(i);
            EdgeList split = collectEdgesForSplit(e.getIdxsplit(), this.edges);
            if (split.size() == 1 && e.getBottom().getEdgeList().size() > 1 && e.getTop().getEdgeList().size() > 1) {
                e.setCompatible(true);
            }
        }
    }

    /**
     * This method collects the edges that represent a given split in the network.
     * @param s Index of split
     * @param elistall Complete list of edges in the network
     * @return Edges that represent the specified split in the network
     */
    protected static EdgeList collectEdgesForSplit(int s, EdgeList elistall) {
        ListIterator iter = elistall.listIterator();
        EdgeList elist = new EdgeList();
        Edge e = null;
        while (iter.hasNext()) {
            e = (Edge) iter.next();
            if (e != null) {
                if (e.getIdxsplit() == s) {
                    elist.add(e);
                }
            }
        }
        return elist;
    }

    protected EdgeList classifyExternalEdges(VertexList vertices) {

        if (vertices == null || vertices.isEmpty()) {
            return new EdgeList();
        }

        Vertex v = vertices.getLeftmostVertex();

        Edge first = null;

        EdgeList ext = new EdgeList();
        Vertex w = null;

        if (v.getEdgeList().size() == 1) {
            w = (v.getEdgeList().getFirst().getBottom() == v) ? v.getEdgeList().getFirst().getTop() : v.getEdgeList().getFirst().getBottom();
            first = v.getEdgeList().getFirst();

            Vertex t = w;
            w = v;
            v = t;
        } else {
            EdgeList elist = v.getEdgeList();

            for (int i = 0; i < elist.size(); i++) {
                Vertex ww = null;
                Vertex w0 = (elist.get(i).getBottom() == v) ? elist.get(i).getTop() : elist.get(i).getBottom();
                double angle = 0;
                for (int j = 0; j < elist.size(); j++) {
                    if (i != j) {
                        Vertex w1 = (elist.get(j).getBottom() == v) ? elist.get(j).getTop() : elist.get(j).getBottom();
                        double currentAngle = Vertex.getClockwiseAngle(w0, v, w1);
                        if (ww == null || currentAngle < angle) {
                            ww = w0;
                            angle = currentAngle;
                            first = elist.get(i);
                        }
                    }
                }
                if (angle > Math.PI) {
                    w = ww;
                    break;
                }
            }
        }

        Edge currentE = first;

        boolean roundMade = false;

        while (currentE != first || !roundMade) {
            roundMade = true;
            LinkedList<Edge> vIn = v.getEdgeList();
            double minAngle = 2 * Math.PI;
            Edge nextE = null;
            Vertex W2 = null;
            for (int i = 0; i < vIn.size(); i++) {
                Edge e = vIn.get(i);
                Vertex w2 = (e.getBottom() == v) ? e.getTop() : e.getBottom();
                double angle = (currentE == e) ? 2 * Math.PI : Vertex.getClockwiseAngle(w, v, w2);
                if (nextE == null || minAngle > angle) {
                    nextE = e;
                    minAngle = angle;
                    W2 = w2;
                }
            }
            ext.add(nextE);
            currentE = nextE;
            w = v;
            v = W2;
        }

        return ext;
    }

    @Override
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
        if (trivialEdges.size() > this.getNbTaxa() * 0.8 && trLength / avgLength > 10) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void removeVertices(VertexList vertices) {
        labeledVertices.removeAll(vertices);
    }

    @Override
    public void addTrivialEdges(VertexList vertices) {
        for(Vertex v : vertices) {
            this.vertices.add(v);
            this.labeledVertices.add(v);
            this.trivialEdges.add(v.getFirstEdge());
            this.edges.add(v.getFirstEdge());
        }
    }

    public void setupLabels() {
        for (Vertex v : this.getAllVertices()) {
            if (v.getTaxa().size() > 0) {
                String label = new String();
                for (Identifier i : v.getTaxa()) {
                    label = (i.getName() + ", ").concat(label);
                }
                label = label.substring(0, label.length() - 2);
                v.setLabel(new NetworkLabel(label));
            }
        }
    }
}
