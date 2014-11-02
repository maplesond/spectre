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

package uk.ac.uea.cmp.spectre.core.ds.network.draw;

import uk.ac.uea.cmp.spectre.core.ds.network.Edge;
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
    /**
     * @param boxesSorted
     * @param edges
     * @param bottom
     * @return
     */
    public double computeOptimalAngle(LinkedList<NetworkBox> boxesSorted, LinkedList<Edge> edges, boolean bottom);

    public double computeForCompatible(LinkedList<Edge> edges);

    public double getAngle(Vertex v1, Vertex a, Vertex v2);

    public double getSafeAngleBot(double deltaAlpha, Edge leftmost, Edge rightmost, Set<Vertex> bottomVertices, Set<Vertex> topVertices);

    /**
     * @param deltaAlpha
     * @param leftmost
     * @param rightmost
     * @param bottomVertices
     * @param topVertices
     * @return
     */
    public double getSafeAngleTop(double deltaAlpha, Edge leftmost, Edge rightmost, Set<Vertex> bottomVertices, Set<Vertex> topVertices);

    public double computeMiddleAngleForTrivial(Edge split, Vertex bot, Vertex top);

    public double[] computeLeftAndRightAngles(Edge e, Vertex v, Vertex w);

    public double optimizedAngleForCompatible(Vertex v, Vertex w, Edge e, List<Edge> botEdges, List<Edge> topEdges);

    public double[] optimizedAngleForCompatible2(Vertex v, Vertex w, Edge e, List<Edge> edges, CompatibleCorrector cc, Network network, Window window, boolean outside);
}