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

package uk.ac.uea.cmp.phygen.netmake.edge;

import uk.ac.uea.cmp.phygen.core.ds.SummedDistanceList;
import uk.ac.uea.cmp.phygen.core.ds.Tableau;

/**
 * Describes the adjacent elements of an edge.
 *
 * @author Sarah Bastkowski
 */
public class EdgeAdjacents {

    private int leaves_in_adjacents[];
    private SummedDistanceList temp_sdl;
    private int split_point;

    public EdgeAdjacents(int[] leaves_in_adjacents, SummedDistanceList temp_sdl, int split_point) {
        this.leaves_in_adjacents = leaves_in_adjacents;
        this.temp_sdl = temp_sdl;
        this.split_point = split_point;
    }

    public int[] getNumberOfLeavesInAdjacents() {
        return leaves_in_adjacents;
    }

    public SummedDistanceList getTempSDL() {
        return temp_sdl;
    }

    public void setSplitPoint(int split_point) {
        this.split_point = split_point;
    }

    public void setNumberOfLeavesInAdjacents(int[] leaves_in_adjacents) {
        this.leaves_in_adjacents = leaves_in_adjacents;
    }

    public void setTempSDL(SummedDistanceList temp_sdl) {
        this.temp_sdl = temp_sdl;
    }

    public int getSplitPoint() {
        return split_point;
    }

    /**
     * Creates an object EdgeAdjacents, so C, pTemp and C_alpha will be determined
     *
     * @param splitsASide The split list
     * @param k          The index of particular split we are dealing with
     * @param P
     * @return A new EdgeAdjacents object containing C, pTemp and C_alpha values.
     */
    public static EdgeAdjacents retrieveAdjacents(Tableau<Integer> splitsASide, int k, SummedDistanceList sdlOriginal, int nbTaxa) {

        // These two lines are killing performance... we are eating up far too much memory and cpu time duplicating
        // this summed distance list... are we sure this is what we want to do???
        SummedDistanceList sdl = new SummedDistanceList(sdlOriginal);
        sdl.addAll(sdlOriginal);

        TableauSplits splits = new TableauSplits(splitsASide, nbTaxa);

        // Get the edges on either side of the current split and sort them
        Edge edgeA = new Edge(splits.getASide().getRow(k));
        edgeA.sort();

        Edge edgeB = new Edge(splits.getBSide().getRow(k));
        edgeB.sort();


        Tableau<Integer> combinedSplitList = splits.combineSides();
        // Also we want to sort the elements in all rows of both a_side and b_side 
        // of the split object.
        splits.sortElementsInAllRows();

        EdgeSubsetFinder.SubsetList a_side = null;
        boolean internalEdge = edgeA.getType() == Edge.EdgeType.INTERNAL;
        if (internalEdge) {
            Edge edge_a = new Edge(edgeA);

            EdgeSubsetFinder esf = new EdgeSubsetFinder(combinedSplitList, sdl, edge_a);
            a_side = esf.process();
        }


        //Getting the B_side

        //First we have to get the elements on the B side A|B all  taxa/leafs x \in B
        //these are stores in edge_b


        EdgeSubsetFinder esf_b = new EdgeSubsetFinder(combinedSplitList, sdl, edgeB);
        EdgeSubsetFinder.SubsetList b_side = esf_b.process();

        int offset = (edgeA.getType() == Edge.EdgeType.EXTERNAL) ? 0 : a_side.size();
        int C[] = new int[offset + b_side.size()];
        if (internalEdge) {
            for (int i = 0; i < a_side.size(); i++) {
                C[i] = a_side.get(i).size();
            }
        }


        for (int i = 0; i < b_side.size(); i++) {
            C[i + offset] = b_side.get(i).size();
        }
        SummedDistanceList ptemp = new SummedDistanceList();
        if (internalEdge) {
            for (EdgeSubsetFinder.Subset ss : a_side) {

                ptemp.add(ss.getSummedDistance());

            }
        }
        for (EdgeSubsetFinder.Subset ss : b_side) {

            ptemp.add(ss.getSummedDistance());

        }

        EdgeAdjacents aEdgeAdjacents = new EdgeAdjacents(C, ptemp, offset);

        return aEdgeAdjacents;

    }

}

