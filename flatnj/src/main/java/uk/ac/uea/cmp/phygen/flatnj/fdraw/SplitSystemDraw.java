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

package uk.ac.uea.cmp.phygen.flatnj.fdraw;

/*This class implements methods to handle a weighted split system*/

public class SplitSystemDraw {
    //number of taxa

    public int ntaxa = 0;
    //The taxa are always indexed as 0,1,...,ntaxa-1.
    //This is also the ordering in which they are used
    //in the matrix that stores the split system below.
    //number of splits
    public int nsplits = 0;
    //2-dimensional 0/1-array encoding the splits.
    //Rows correspond to splits, columns correspond to taxa.
    //The ordering of taxa is 0,1,...,ntaxa-1.
    public int[][] splits = null;

    //This method prints information about the split system on the screen
    public void print_splits() {
        int i = 0;
        int j = 0;

        System.out.println("Number of taxa: " + ntaxa + " *");
        System.out.println("Number of splits: " + nsplits + " *");

        System.out.println("List of splits:");

        for (i = 0; i < nsplits; i++) {
            for (j = 0; j < ntaxa; j++) {
                System.out.print(splits[i][j]);
            }
            System.out.println(" *");
        }
    }

    //Constructor of this class from a permutation sequence.
    //The coding by 0/1 is such that a taxon is on the
    //1-side of the split if it lies below the face
    //represening the split. The pseudoline arrangement
    //is given as a permutation sequence.
    public SplitSystemDraw(PermutationSequenceDraw p_sequ) {
        int i = 0;
        int j = 0;
        int h = 0;

        ntaxa = p_sequ.ntaxa;
        nsplits = p_sequ.nswaps;
        splits = new int[nsplits][ntaxa];
        int[] cur_sequ = new int[ntaxa];

        //Initialize current sequence with initial permutation
        for (i = 0; i < ntaxa; i++) {
            cur_sequ[i] = p_sequ.initSequ[i];
        }

        //Write splits into 0/1-array.
        for (i = 0; i < nsplits; i++) {
            //compute current permutation
            h = cur_sequ[p_sequ.swaps[i]];
            cur_sequ[p_sequ.swaps[i]] = cur_sequ[p_sequ.swaps[i] + 1];
            cur_sequ[p_sequ.swaps[i] + 1] = h;
            //turn it into a 0/1 sequence
            for (j = 0; j < ntaxa; j++) {
                if (j <= p_sequ.swaps[i]) {
                    splits[i][cur_sequ[j]] = 1;
                } else {
                    splits[i][cur_sequ[j]] = 0;
                }
            }
        }
    }

    //This method checks whether the splits with
    //indices a and b are compatible. It returns:
    //-1: splits incompatible
    // 1: splits compatible, 11 pattern
    // 2: splits compatible, 10 pattern 
    // 3: splits compatible, 01 pattern
    // 4: splits compatible, 00 pattern
    public int is_compatible(int a, int b) {
        //loop variable
        int i = 0;

        //variables for counting the number of occurences of patterns
        int count11 = 0;
        int count10 = 0;
        int count01 = 0;
        int count00 = 0;

        for (i = 0; i < ntaxa; i++) {
            if ((splits[a][i] == 1) && (splits[b][i] == 1)) {
                count11++;
            }
            if ((splits[a][i] == 1) && (splits[b][i] == 0)) {
                count10++;
            }
            if ((splits[a][i] == 0) && (splits[b][i] == 1)) {
                count01++;
            }
            if ((splits[a][i] == 0) && (splits[b][i] == 0)) {
                count00++;
            }
        }

        if (count11 == 0) {
            return 1;
        } else if (count10 == 0) {
            return 2;
        } else if (count01 == 0) {
            return 3;
        } else if (count00 == 0) {
            return 4;
        } else {
            return -1;
        }
    }

    public boolean isCompatible(int a, int b) {
        //variables for counting the number of occurences of patterns
        int count11 = 0;
        int count10 = 0;
        int count01 = 0;
        int count00 = 0;

        for (int i = 0; i < ntaxa; i++) {
            if ((splits[a][i] == 1) && (splits[b][i] == 1)) {
                count11++;
            }
            if ((splits[a][i] == 1) && (splits[b][i] == 0)) {
                count10++;
            }
            if ((splits[a][i] == 0) && (splits[b][i] == 1)) {
                count01++;
            }
            if ((splits[a][i] == 0) && (splits[b][i] == 0)) {
                count00++;
            }
        }

        if (count11 == 0 || count10 == 0 || count01 == 0 || count00 == 0) {
            return true;
        } else {
            return false;
        }
    }
}
