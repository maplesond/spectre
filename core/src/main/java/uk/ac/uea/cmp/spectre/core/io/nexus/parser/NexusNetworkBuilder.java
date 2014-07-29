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

package uk.ac.uea.cmp.spectre.core.io.nexus.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.spectre.core.ds.network.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dan on 18/03/14.
 */
public class NexusNetworkBuilder {

    private static Logger log = LoggerFactory.getLogger(NexusNetworkBuilder.class);

    private int nbExpectedTaxa;
    private int nbExpectedVertices;
    private int nbExpectedEdges;

    private boolean drawToScale;
    private double rotateAbout;

    private Map<Integer, Vertex> vertices;
    private Vertex currentVertex;

    private Map<Integer, NetworkLabel> labels;
    private NetworkLabel currentLabel;

    private Map<Integer, Edge> edges;
    private Edge currentEdge;

    public NexusNetworkBuilder() {

        this.nbExpectedEdges = 0;
        this.nbExpectedTaxa = 0;
        this.nbExpectedVertices = 0;

        this.drawToScale = false;
        this.rotateAbout = 0.0;

        this.vertices = new HashMap<>();
        this.currentVertex = null;

        this.labels = new HashMap<>();
        this.currentLabel = null;

        this.edges = new HashMap<>();
        this.currentEdge = null;
    }

    public void setExpectedDimensions(int nbExpectedTaxa, int nbExpectedVertices, int nbExpectedEdges) {
        this.nbExpectedTaxa = nbExpectedTaxa;
        this.nbExpectedVertices = nbExpectedVertices;
        this.nbExpectedEdges = nbExpectedEdges;
    }

    public int getNbExpectedTaxa() {
        return nbExpectedTaxa;
    }

    public void setNbExpectedTaxa(int nbExpectedTaxa) {
        this.nbExpectedTaxa = nbExpectedTaxa;
    }

    public int getNbExpectedVertices() {
        return nbExpectedVertices;
    }

    public void setNbExpectedVertices(int nbExpectedVertices) {
        this.nbExpectedVertices = nbExpectedVertices;
    }

    public int getNbExpectedEdges() {
        return nbExpectedEdges;
    }

    public void setNbExpectedEdges(int nbExpectedEdges) {
        this.nbExpectedEdges = nbExpectedEdges;
    }

    public boolean isDrawToScale() {
        return drawToScale;
    }

    public void setDrawToScale(boolean drawToScale) {
        this.drawToScale = drawToScale;
    }

    public double getRotateAbout() {
        return rotateAbout;
    }

    public void setRotateAbout(double rotateAbout) {
        this.rotateAbout = rotateAbout;
    }

    public Map<Integer, Vertex> getVertices() {
        return vertices;
    }

    public Vertex getCurrentVertex() {
        return currentVertex;
    }

    public void setCurrentVertex(Vertex currentVertex) {
        this.currentVertex = currentVertex;
    }

    public Map<Integer, NetworkLabel> getLabels() {
        return labels;
    }

    public void setLabels(Map<Integer, NetworkLabel> labels) {
        this.labels = labels;
    }

    public NetworkLabel getCurrentLabel() {
        return currentLabel;
    }

    public void setCurrentLabel(NetworkLabel currentLabel) {
        this.currentLabel = currentLabel;
    }

    public void setVertices(Map<Integer, Vertex> vertices) {
        this.vertices = vertices;
    }


    public Map<Integer, Edge> getEdges() {
        return edges;
    }

    public void setEdges(Map<Integer, Edge> edges) {
        this.edges = edges;
    }

    public Edge getCurrentEdge() {
        return currentEdge;
    }

    public void setCurrentEdge(Edge currentEdge) {
        this.currentEdge = currentEdge;
    }

    public Network createNetwork() {

        if (this.vertices.size() != this.nbExpectedVertices) {
            log.warn("Number of detected vertices (" + this.vertices.size() + ") is not the same as the " +
                    "number of vertices we expected to see (" + this.nbExpectedVertices + ")");
        }

        if (this.labels.size() != this.nbExpectedTaxa) {
            log.warn("Number of detected vertex labels (" + this.labels.size() + ") is not the same as the " +
                    "number of labels we expected to see (" + this.nbExpectedTaxa + ")");
        }

        if (this.edges.size() != this.nbExpectedEdges) {
            log.warn("Number of detected edges (" + this.edges.size() + ") is not the same as the " +
                    "number of edges we expected to see (" + this.nbExpectedEdges + ")");
        }

        FlatNetwork network = new FlatNetwork();

        network.setVertices(new VertexList(this.vertices.values()));
        //network.setVertexLabels(new LinkedList<>(this.labels.values()));
        network.setEdges(new EdgeList(this.edges.values()));

        return network;
    }

}
