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

    /**
     * This method computes information about the graph that corresponds to the arrangement of pseudolines that is used
     * to carry out matrix-vector multiplications efficiently in the class FitWeight.
     *
     * @return Arrangement of pseudolines
     */
    public static ArrangementData computeArrangement(int[] swaps, int[] taxa) {

        final int nbSwaps = swaps.length;
        final int nbTaxa = taxa.length;

        //Create the object that stores the information about the arrangement
        ArrangementData arrdata = new ArrangementData();

        //Allocate memory for the arrays in this object.
        arrdata.arr = new int[nbSwaps][4];
        arrdata.lastswap = new int[nbTaxa];
        arrdata.upperlower = new int[nbTaxa];
        arrdata.change = new double[nbSwaps][2];

        int[] cursequ = new int[nbTaxa];

        int i = 0;
        int h = 0;

        for (i = 0; i < nbTaxa; i++) {
            cursequ[i] = taxa[i];
            arrdata.lastswap[i] = -i - 1;
        }

        for (i = 0; i < nbSwaps; i++) {
            //store the two edges that lead to the left
            arrdata.arr[i][0] = arrdata.lastswap[cursequ[swaps[i] + 1]];
            arrdata.arr[i][1] = arrdata.lastswap[cursequ[swaps[i]]];
            //store two dummy edges that lead to the right
            arrdata.arr[i][2] = -cursequ[swaps[i] + 1] - 1;
            arrdata.arr[i][3] = -cursequ[swaps[i]] - 1;
            //store edges that go from last swap to the right
            //if they exist to replace the dummy edges
            if (arrdata.lastswap[cursequ[swaps[i] + 1]] >= 0) {
                if (arrdata.upperlower[cursequ[swaps[i] + 1]] == -1) {
                    arrdata.arr[arrdata.lastswap[cursequ[swaps[i] + 1]]][2] = i;
                } else {
                    arrdata.arr[arrdata.lastswap[cursequ[swaps[i] + 1]]][3] = i;
                }
            }
            if (arrdata.lastswap[cursequ[swaps[i]]] >= 0) {
                if (arrdata.upperlower[cursequ[swaps[i]]] == -1) {
                    arrdata.arr[arrdata.lastswap[cursequ[swaps[i]]]][2] = i;
                } else {
                    arrdata.arr[arrdata.lastswap[cursequ[swaps[i]]]][3] = i;
                }
            }
            //update arrays
            arrdata.upperlower[cursequ[swaps[i] + 1]] = -1;
            arrdata.upperlower[cursequ[swaps[i]]] = 1;
            arrdata.lastswap[cursequ[swaps[i] + 1]] = i;
            arrdata.lastswap[cursequ[swaps[i]]] = i;
            h = cursequ[swaps[i] + 1];
            cursequ[swaps[i] + 1] = cursequ[swaps[i]];
            cursequ[swaps[i]] = h;
        }
        return arrdata;
    }
}