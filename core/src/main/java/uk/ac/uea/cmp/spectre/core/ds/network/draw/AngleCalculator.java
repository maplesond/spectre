/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2015  UEA School of Computing Sciences
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

package uk.ac.uea.cmp.spectre.core.ds.network.draw;

import uk.ac.uea.cmp.spectre.core.ds.network.Edge;
import uk.ac.uea.cmp.spectre.core.ds.network.EdgeList;
import uk.ac.uea.cmp.spectre.core.ds.network.Network;
import uk.ac.uea.cmp.spectre.core.ds.network.Vertex;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author balvociute
 */
public interface AngleCalculator {

    public double computeOptimalAngle(List<NetworkBox> boxesSorted, EdgeList edges, boolean bottom);

    public double computeForCompatible(EdgeList edges);

    /**
     * Calculates the angle at Vertex 'a' from Vertex 'v1' to 'v2'
     *
     * @param v1 Vertex 1
     * @param a Centre point from which to measure the angle
     * @param v2 Vertex 2
     * @return Angle from vertex 1 to vertex 2 at centre point 'a'
     */
    public double getAngle(Vertex v1, Vertex a, Vertex v2);

    public double getSafeAngleBot(double deltaAlpha, Edge leftmost, Edge rightmost, Set<Vertex> bottomVertices, Set<Vertex> topVertices);

    public double getSafeAngleTop(double deltaAlpha, Edge leftmost, Edge rightmost, Set<Vertex> bottomVertices, Set<Vertex> topVertices);

    public double computeMiddleAngleForTrivial(Edge split, Vertex bot, Vertex top);

    public double[] computeLeftAndRightAngles(Edge e, Vertex v, Vertex w);

    public double optimizedAngleForCompatible(Vertex v, Vertex w, Edge e, List<Edge> botEdges, List<Edge> topEdges);

    public double[] optimizedAngleForCompatible2(Vertex v, Vertex w, Edge e, List<Edge> edges, CompatibleCorrector cc, Network network, boolean outside);
}
