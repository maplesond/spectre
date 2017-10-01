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

import org.apache.commons.lang3.StringUtils;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;

import java.util.*;

/**
 * Created by dan on 20/03/14.
 */
public class SpectreNetwork implements Network {


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

    private Vertex v;


    public SpectreNetwork() {
        this(new VertexList(), new EdgeList());
    }

    /**
     * Used when loading a network from file
     * @param vertices Vertices in network
     * @param edges Edges in network
     */
    public SpectreNetwork(VertexList vertices, EdgeList edges) {

        this.v = vertices.getLeftmostVertex();

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

    /**
     * Used when creating network from split system draw
     * @param v Initial vertex
     */
    public SpectreNetwork(Vertex v) {
        this(v.collectVertices(), v.getFirstEdge().collectEdges());
        this.setupLabels();     // TODO this should probably be done when creating the vertex
        this.v = v;
    }

    @Override
    public void setTaxa(IdentifierList taxa) {
        this.taxa = taxa;
    }

    @Override
    public Vertex getPrimaryVertex() {
        return this.v;
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
            EdgeList split = collectEdgesForSplit(e.getSplitIndex(), this.edges);
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
        EdgeList elist = new EdgeList();
        for (Edge e : elistall) {
            if (e != null) {
                if (e.getSplitIndex() == s) {
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

        Vertex w = null;

        if (v.getEdgeList().size() == 1) {
            first = v.getFirstEdge();
            // The vertex only contains a single edge, so make w the vertex which isn't v.
            w = first.getBottom() == v ? first.getTop() : first.getBottom();

            Vertex t = w;
            w = v;
            v = t;
        } else {
            for(Edge e : v.getEdgeList()) {
                Vertex ww = null;
                Vertex w0 = e.getBottom() == v ? e.getTop() : e.getBottom();
                double minAngle = 0;
                for (Edge f : v.getEdgeList()) {
                    if (e != f) {
                        Vertex w1 = f.getBottom() == v ? f.getTop() : f.getBottom();
                        double currentAngle = Vertex.getClockwiseAngle(w0, v, w1);
                        if (ww == null || currentAngle < minAngle) {
                            ww = w0;
                            minAngle = currentAngle;
                            first = e;
                        }
                    }
                }
                if (minAngle > Math.PI) {
                    w = ww;
                    break;
                }
            }
        }

        if (w == null) {
            return new EdgeList();
        }

        Edge current = first;
        EdgeList external = new EdgeList();
        boolean roundMade = false;

        while (current != first || !roundMade) {
            roundMade = true;
            double minAngle = 2 * Math.PI;
            Edge nextE = null;
            Vertex W2 = null;
            for (Edge e : v.getEdgeList()) {
                Vertex w2 = (e.getBottom() == v) ? e.getTop() : e.getBottom();
                double angle = (current == e) ? 2 * Math.PI : Vertex.getClockwiseAngle(w, v, w2);
                if (nextE == null || minAngle > angle) {
                    nextE = e;
                    minAngle = angle;
                    W2 = w2;
                }
            }
            external.add(nextE);
            current = nextE;
            w = v;
            v = W2;
        }

        return external;
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
        // Add labels to all required vertices
        for (Vertex v : this.getAllVertices()) {
            if (v.getTaxa().size() > 0) {
                String label = StringUtils.join(v.getTaxa().getNames(), ',');
                v.setLabel(new NetworkLabel(label));
                v.setSize(5);
                v.setShape(null);
            }
        }
    }

}
