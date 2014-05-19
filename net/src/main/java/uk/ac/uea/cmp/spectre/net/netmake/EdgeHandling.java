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

package uk.ac.uea.cmp.spectre.net.netmake;

import uk.ac.uea.cmp.spectre.core.ds.split.Split;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitBlock;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitDistanceMap;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes the adjacent elements of an edge.
 *
 * @author Sarah Bastkowski
 */
public class EdgeHandling {

    public static class AdjacentEdges {
        private int leaves_in_adjacents[];
        private SummedDistanceList temp_sdl;
        private int split_point;

        public AdjacentEdges(int[] leaves_in_adjacents, SummedDistanceList temp_sdl, int split_point) {
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
    }


    public AdjacentEdges retrieveAdjacents(Split split, SplitSystem sortedSplits, SplitDistanceMap splitDistanceMap) {

        // Get the edges on either side of the current split
        SplitBlock edgeA = split.getASide().makeSortedCopy();
        SplitBlock edgeB = split.getBSide().makeSortedCopy();

        // Get the A side
        List<SplitBlock> aSide = null;
        boolean internalEdge = edgeA.getType() == SplitBlock.EdgeType.INTERNAL;
        if (internalEdge) {
            aSide = this.findEdgeSubsets(edgeA, sortedSplits);
        }


        // Get the B side

        // First we have to get the elements on the B side A|B all taxa/leafs x \in B these are stores in edge_b
        List<SplitBlock> bSide = this.findEdgeSubsets(edgeB, sortedSplits);

        int offset = (edgeA.getType() == SplitBlock.EdgeType.EXTERNAL) ? 0 : aSide.size();
        int C[] = new int[offset + bSide.size()];
        if (internalEdge) {
            for (int i = 0; i < aSide.size(); i++) {
                C[i] = aSide.get(i).size();
            }
        }


        for (int i = 0; i < bSide.size(); i++) {
            C[i + offset] = bSide.get(i).size();
        }
        SummedDistanceList ptemp = new SummedDistanceList();
        if (internalEdge) {
            for (SplitBlock ss : aSide) {
                ptemp.add(splitDistanceMap.getUsingSplitBlock(ss).doubleValue());
            }
        }
        for (SplitBlock ss : bSide) {
            ptemp.add(splitDistanceMap.getUsingSplitBlock(ss).doubleValue());
        }

        AdjacentEdges aEdgeAdjacents = new AdjacentEdges(C, ptemp, offset);

        return aEdgeAdjacents;
    }

    /**
     * Finds the largest subsets of the all the edges in the split system that together make up the provided edge
     *
     * @param edge        The edge to find subsets from
     * @param splitSystem The split system to search through to find the subsets.
     */
    private List<SplitBlock> findEdgeSubsets(SplitBlock edge, SplitSystem splitSystem) {

        List<SplitBlock> subsets = new ArrayList<>();

        // Make a copy of the edge because we are going to mutilate this as we go
        SplitBlock edgeCopy = edge.copy();

        boolean firstRun = true;

        // Keep shrinking the edge by the largest subset until we have nothing left
        while (edgeCopy.size() > 0) {

            // Get the largest subset for the given edge
            SplitBlock largestSubset = this.getLargestSplitBlock(edgeCopy, firstRun, splitSystem);

            // Sanity check
            if (largestSubset == null)
                throw new IllegalStateException("This shouldn't have happened.  Largest subset couldn't be found in all splits");

            // Add the largest subset to the record
            subsets.add(largestSubset.copy());

            // Remove the largest subset from the current edge
            edgeCopy.removeAll(largestSubset);

            // Not the first run any longer
            firstRun = false;
        }

        return subsets;
    }

    /**
     * Gets the largest edge that's a subset of the given edge
     *
     * @param edge     The edge from which to find the largest subset
     * @param firstRun Whether or not this is the first run
     * @param ss       The split system contains all the splits which we need to check
     * @return The edge that is the largest subset of the given edge
     */
    private SplitBlock getLargestSplitBlock(SplitBlock edge, boolean firstRun, SplitSystem ss) {

        // Obviously can't have a subset of an edge that is 1 element long, so return this.
        if (edge.size() == 1) {
            return edge;
        }

        SplitBlock largest = null;

        for (Split split : ss) {

            if (this.isCandidateLargestValidSubset(edge, split.getASide(), largest, firstRun)) {
                largest = split.getASide();
            }

            if (this.isCandidateLargestValidSubset(edge, split.getBSide(), largest, firstRun)) {
                largest = split.getBSide();
            }
        }

        return largest;
    }

    /**
     * Determines whether or not the candidate edge is the largest valid subset of the edge that we have seen so far
     *
     * @param bigEdge      The big edge from which we want to find subsets.
     * @param candidate    The candidate edge to check
     * @param largestSoFar The largest edge seen so far
     * @param firstRun     Whether or not this is the first run
     * @return True if the candidate is valid and larger that the largest edge seen so far
     */
    private boolean isCandidateLargestValidSubset(SplitBlock bigEdge, SplitBlock candidate, SplitBlock largestSoFar, boolean firstRun) {

        // The big edge must be larger than the candidate to consider the candidate
        if (bigEdge.size() < candidate.size()) {
            return false;
        }

        // This probably means that the big edge is the candidate, so we don't want to consider this candidate either.
        if (bigEdge.size() == candidate.size() && firstRun) {
            return false;
        }

        // The edge must contain all the elements from the candidate edge
        if (bigEdge.containsAll(candidate)) {
            if (largestSoFar == null)
                return true;
            else {
                if (candidate.size() <= largestSoFar.size()) {
                    // Not interested?
                    return false;
                } else {
                    return true;
                }
            }
        }

        // Candidate subset edge could not be found in this edge
        return false;
    }
}

