/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2017  UEA School of Computing Sciences
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
 * This class implements methods to handle a weighted split system.
 * The taxa are always indexed as 0,1,...,ntaxa-1.  This is also the ordering in which they are used in the matrix
 * that stores the split system.
 */
public class SplitSystemDraw {

    /**
     * Number of taxa
     */
    public int ntaxa = 0;

    /**
     * Number of splits
     */
    public int nsplits = 0;

    /**
     * 2-dimensional 0/1-array encoding the splits.  Rows correspond to splits, columns correspond to taxa.  The ordering
     * of taxa is 0,1,...,ntaxa-1.
     */
    public int[][] splits = null;

    /**
     * This method presents information about the split system on the screen in string form
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("Number of taxa: " + ntaxa + " *\n");
        sb.append("Number of splits: " + nsplits + " *\n");

        sb.append("List of splits:\n");

        for (int i = 0; i < nsplits; i++) {
            for (int j = 0; j < ntaxa; j++) {
                sb.append(splits[i][j]);
            }
            sb.append(" *\n");
        }

        return sb.toString();
    }

    /**
     * Constructor of this class from a permutation sequence. The coding by 0/1 is such that a taxon is on the 1-side of
     * the split if it lies below the face represening the split. The pseudoline arrangement is given as a permutation
     * sequence.
     * @param p_sequ permutation sequence representing a flat split system
     */
    public SplitSystemDraw(PermutationSequenceDraw p_sequ) {

        int h = 0;

        ntaxa = p_sequ.ntaxa;
        nsplits = p_sequ.nswaps;
        splits = new int[nsplits][ntaxa];
        int[] cur_sequ = new int[ntaxa];

        //Initialize current sequence with initial permutation
        for (int i = 0; i < ntaxa; i++) {
            cur_sequ[i] = p_sequ.initSequ[i];
        }

        //Write splits into 0/1-array.
        for (int i = 0; i < nsplits; i++) {
            //compute current permutation
            h = cur_sequ[p_sequ.swaps[i]];
            cur_sequ[p_sequ.swaps[i]] = cur_sequ[p_sequ.swaps[i] + 1];
            cur_sequ[p_sequ.swaps[i] + 1] = h;
            //turn it into a 0/1 sequence
            for (int j = 0; j < ntaxa; j++) {
                if (j <= p_sequ.swaps[i]) {
                    splits[i][cur_sequ[j]] = 1;
                } else {
                    splits[i][cur_sequ[j]] = 0;
                }
            }
        }
    }

    public enum Compatible {
        NO {
            @Override
            public boolean isCompatible() {
                return false;
            }
        },
        YES_11 {
            @Override
            public boolean isCompatible() {
                return true;
            }
        },
        YES_10 {
            @Override
            public boolean isCompatible() {
                return true;
            }
        },
        YES_01 {
            @Override
            public boolean isCompatible() {
                return true;
            }
        },
        YES_00 {
            @Override
            public boolean isCompatible() {
                return true;
            }
        };

        public abstract boolean isCompatible();
    }

    /**
     * This method checks whether the splits with indices a and b are compatible.
     * @param a Index A
     * @param b Index B
     * @return Compatible enum describing if the splits are compatible, and if so, which pattern they show.
     */
    public Compatible isCompatible(int a, int b) {

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

        if (count11 == 0) {
            return Compatible.YES_11;
        } else if (count10 == 0) {
            return Compatible.YES_10;
        } else if (count01 == 0) {
            return Compatible.YES_01;
        } else if (count00 == 0) {
            return Compatible.YES_00;
        } else {
            return Compatible.NO;
        }
    }
}
