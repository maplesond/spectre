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

import java.util.LinkedList;
import java.util.List;

/**
 * Created by dan on 20/03/14.
 */
public class FlatNetwork implements Network {


    private List<Vertex> vertices;
    private List<Label> vertexLabels;
    private List<Edge> edges;

    public FlatNetwork() {
        this.vertices = new LinkedList<>();
        this.vertexLabels = new LinkedList<>();
        this.edges = new LinkedList<>();
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

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    @Override
    public List<Edge> getEdges() {
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


}
