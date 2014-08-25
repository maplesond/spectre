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

/**
 * Arrangement of pseudolines to the NNLS-algorithm in such a way that the matrix-vector multiplications can be carried
 * out efficiently.
 */
public class ArrangementData {
    //For every swap we store the 4 edges incident
    //to the intersection of the corresponding
    //pseudolines.
    //
    //arr[i][0] ... index of swap we reach by left upper edge
    //arr[i][1] ... index of swap we reach by left lower edge
    //arr[i][2] ... index of swap we reach by right lower edge
    //arr[i][2] ... index of swap we reach by right upper edge
    //
    //If an edge is unbounded to the left or right
    //then arr[i][*] equals (-index-1), where plindex
    //is the index of the pseudoline that contains this
    //edge

    public int[][] arr = null;

    //The following two arrays are used to keep track
    //of the structure of the right hand side of the
    //arrangement of pseudolines. The pseudolines are
    //indexed 0,1,...,(ntaxa-1) from top to bottom.
    //
    //lastswap[i] stores the index of the last swap
    //on pseudoline with index i

    public int[] lastswap = null;

    //upperlower[i] can take on the values -1 and +1.
    //
    //-1 indicates that the last edge in the pseudoline
    //with index i is the right lower edge of the last
    //swap on this pseudoline
    //
    //+1 indicated that the last edge in the pseudoline
    //with index i is the right upper edge of the last
    //swap on this pseudoline

    public int[] upperlower = null;

    //The array change is used in the computation of
    //split weights from distances. As an intermediate
    //step we compute the net-change across an edge
    //of the arrangement. To avoid the re-allocation
    //of memory every time we carry out this kind of
    //matrix-vector multiplication, we include this
    //array in this class.
    //
    //double[i][0] ... change across right lower edge
    //                 of swap with index i
    //double[i][1] ... change across right upper edge
    //                 of swap with index i

    public double[][] change = null;
}