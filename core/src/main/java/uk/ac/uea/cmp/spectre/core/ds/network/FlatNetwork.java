/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2014  UEA School of Computing Sciences
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
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

package uk.ac.uea.cmp.spectre.core.ds.network;

import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dan on 20/03/14.
 */
public class FlatNetwork implements Network {


    private List<Vertex> vertices;
    private List<Label> vertexLabels;
    private EdgeList edges;

    /**
     * The external edges may take some time to calculate.  This variable is essentially a cache of the results of that
     * computation, if it is requested.
     */
    private LinkedList<Edge> externalEdges = null;

    public FlatNetwork() {
        this.vertices = new LinkedList<>();
        this.vertexLabels = new LinkedList<>();
        this.edges = new EdgeList();
    }

    @Override
    public IdentifierList getTaxa() {
        return null;
    }

    public void setVertices(List<Vertex> vertices) {
        this.vertices = vertices;
    }

    @Override
    public List<Vertex> getVertices() {
        return vertices;
    }

    public void setEdges(EdgeList edges) {
        this.edges = edges;
    }

    @Override
    public EdgeList getEdges() {
        return this.edges;
    }

    public List<Label> getVertexLabels() {
        return vertexLabels;
    }

    public void setVertexLabels(List<Label> vertexLabels) {
        this.vertexLabels = vertexLabels;
    }

    @Override
    public List<Label> getLabels() {
        return this.vertexLabels;
    }

    public List<Edge> externalEdges(List<Vertex> vertices) {
        if (externalEdges != null) {
            return new LinkedList<>(externalEdges);
        }

        Vertex v = vertices.get(0);

        Iterator<Vertex> vertexIt = vertices.iterator();
        while (vertexIt.hasNext()) {
            Vertex vertex = vertexIt.next();
            if (v.getX() > vertex.getX()) {
                v = vertex;
            }
        }

        Edge first = null;

        LinkedList<Edge> ext = new LinkedList<>();
        Vertex w = null;

        if (v.getElist().size() == 1) {
            w = (v.getElist().getFirst().getBot() == v) ? v.getElist().getFirst().getTop() : v.getElist().getFirst().getBot();
            first = v.getElist().getFirst();

            Vertex t = w;
            w = v;
            v = t;
        } else {
            java.util.List<Edge> elist = v.getElist();

            for (int i = 0; i < elist.size(); i++) {
                Vertex ww = null;
                Vertex w0 = (elist.get(i).getBot() == v) ? elist.get(i).getTop() : elist.get(i).getBot();
                double angle = 0;
                for (int j = 0; j < elist.size(); j++) {
                    if (i != j) {
                        Vertex w1 = (elist.get(j).getBot() == v) ? elist.get(j).getTop() : elist.get(j).getBot();
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
            LinkedList<Edge> vIn = v.getElist();
            double minAngle = 2 * Math.PI;
            Edge nextE = null;
            Vertex W2 = null;
            for (int i = 0; i < vIn.size(); i++) {
                Edge e = vIn.get(i);
                Vertex w2 = (e.getBot() == v) ? e.getTop() : e.getBot();
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

        this.externalEdges = new LinkedList<>(ext);


        return ext;
    }

    public LinkedList<Edge> collectExternalEdges(Vertex v1) {
        if (externalEdges != null) {
            return new LinkedList<>(externalEdges);
        }
        List<Vertex> vertices = v1.collectVertices();

        Vertex v = v1;

        Iterator<Vertex> vertexIt = vertices.iterator();
        while (vertexIt.hasNext()) {
            Vertex vertex = vertexIt.next();
            if (v.getX() > vertex.getX()) {
                v = vertex;
            }
        }

        Edge first = null;

        LinkedList<Edge> ext = new LinkedList<>();
        Vertex w = null;

        if (v.getElist().size() == 1) {
            w = (v.getElist().getFirst().getBot() == v) ? v.getElist().getFirst().getTop() : v.getElist().getFirst().getBot();
            first = v.getElist().getFirst();

            Vertex t = w;
            w = v;
            v = t;
        } else {
            List<Edge> elist = v.getElist();

            for (int i = 0; i < elist.size(); i++) {
                Vertex ww = null;
                Vertex w0 = (elist.get(i).getBot() == v) ? elist.get(i).getTop() : elist.get(i).getBot();
                double angle = 0;
                for (int j = 0; j < elist.size(); j++) {
                    if (i != j) {
                        Vertex w1 = (elist.get(j).getBot() == v) ? elist.get(j).getTop() : elist.get(j).getBot();
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
            LinkedList<Edge> vIn = v.getElist();
            double minAngle = 2 * Math.PI;
            Edge nextE = null;
            Vertex W2 = null;
            for (int i = 0; i < vIn.size(); i++) {
                Edge e = vIn.get(i);
                Vertex w2 = (e.getBot() == v) ? e.getTop() : e.getBot();
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

        externalEdges = new LinkedList<>(ext);


        return ext;
    }



}
