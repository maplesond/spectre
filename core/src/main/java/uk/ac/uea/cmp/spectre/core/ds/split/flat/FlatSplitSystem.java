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

package uk.ac.uea.cmp.spectre.core.ds.split.flat;

import uk.ac.uea.cmp.spectre.core.util.CollectionUtils;

/**
 * This class implements methods to handle a weighted split system
 */
public class FlatSplitSystem {

    protected int nTaxa = 0;
    protected int[] taxaMap;
    protected String[] taxaNames;
    protected int[] cycle;
    //The taxa are always indexed as 0,1,...,nTaxa-1.
    //This is also the ordering in which they are used
    //in the matrix that stores the split system below.
    //number of splits
    protected int nSplits = 0;
    //2-dimensional 0/1-array encoding the splits.
    //Rows correspond to splits, columns correspond to taxa.
    //The ordering of taxa is 0,1,...,nTaxa-1.
    protected boolean[][] splits = null;
    //weights of the splits
    //The ordering is the same as in the rows in the 0/1 matrix
    //that stores the splits
    protected double[] weights = null;

    //value of the fit function from gurobi solver
    protected double fit;

    protected boolean[] active;

    //This method checks whether the quadruple split number nr 
    //is in the restriction of the split system to {a,b,c,d}.
    //It is assumed that a, b, c and d are pairwise distinct.
    //The possible values for nr are:
    //0 --> a|bcd
    //1 --> b|acd
    //2 --> c|abd
    //3 --> d|abc
    //4 --> ab|cd
    //5 --> ac|bd
    //6 --> ad|bc
    public boolean restrictionExists(int a, int b, int c, int d, int nr) {
        boolean exists = false;
        int i = 0;

        if (nr == 0) {
            for (i = 0; i < nSplits; i++) {
                if (((splits[i][b] == splits[i][c]) && (splits[i][c] == splits[i][d])) && (splits[i][a] != splits[i][b])) {
                    exists = true;
                    break;
                }
            }
        } else if (nr == 1) {
            for (i = 0; i < nSplits; i++) {
                if (((splits[i][a] == splits[i][c]) && (splits[i][c] == splits[i][d])) && (splits[i][a] != splits[i][b])) {
                    exists = true;
                    break;
                }
            }
        } else if (nr == 2) {
            for (i = 0; i < nSplits; i++) {
                if (((splits[i][a] == splits[i][b]) && (splits[i][b] == splits[i][d])) && (splits[i][a] != splits[i][c])) {
                    exists = true;
                    break;
                }
            }
        } else if (nr == 3) {
            for (i = 0; i < nSplits; i++) {
                if (((splits[i][a] == splits[i][b]) && (splits[i][b] == splits[i][c])) && (splits[i][a] != splits[i][d])) {
                    exists = true;
                    break;
                }
            }
        } else if (nr == 4) {
            for (i = 0; i < nSplits; i++) {
                if (((splits[i][a] == splits[i][b]) && (splits[i][c] == splits[i][d])) && (splits[i][a] != splits[i][c])) {
                    exists = true;
                    break;
                }
            }
        } else if (nr == 5) {
            for (i = 0; i < nSplits; i++) {
                if (((splits[i][a] == splits[i][c]) && (splits[i][b] == splits[i][d])) && (splits[i][a] != splits[i][b])) {
                    exists = true;
                    break;
                }
            }
        } else if (nr == 6) {
            for (i = 0; i < nSplits; i++) {
                if (((splits[i][a] == splits[i][d]) && (splits[i][b] == splits[i][c])) && (splits[i][a] != splits[i][b])) {
                    exists = true;
                    break;
                }
            }
        }
        return exists;
    }

    //Constructor of this class from a permutation sequence.
    public FlatSplitSystem(PermutationSequence ps) {
        int h;

        nTaxa = ps.getnTaxa();

        cycle = new int[nTaxa];
        System.arraycopy(ps.getSequence(), 0, cycle, 0, nTaxa);

        active = ps.getActive();

        nSplits = ps.getnSwaps();

        taxaMap = new int[nTaxa];
        System.arraycopy(ps.getSequence(), 0, taxaMap, 0, taxaMap.length);

        int maxN = taxaMap[0];
        for (int i = 1; i < taxaMap.length; i++) {
            maxN = (maxN < taxaMap[i]) ? taxaMap[i] : maxN;
        }
        maxN++;

        splits = new boolean[nSplits][maxN];
        weights = new double[nSplits];
        int[] cur_sequ = new int[nTaxa];

        double[] psWeights = ps.getWeights();

        System.arraycopy(ps.getSequence(), 0, cur_sequ, 0, nTaxa);

        //Write splits into 0/1-array.
        int k = 0;
        int[] swaps = ps.getSwaps();
        for (int i = 0; i < ps.getnSwaps(); i++) {
            //compute current permutation
            h = cur_sequ[swaps[i]];
            cur_sequ[swaps[i]] = cur_sequ[swaps[i] + 1];
            cur_sequ[swaps[i] + 1] = h;
            //turn it into a 0/1 sequence
            if (active[i]) {
                for (int j = 0; j < nTaxa; j++) {
                    if (j <= swaps[i]) {
                        splits[k][cur_sequ[j]] = true;
                    } else {
                        splits[k][cur_sequ[j]] = false;
                    }
                }
            }
            k++;
        }

        if (psWeights != null) {
            setWeights(psWeights, ps.getTrivial());
        } else {
            for (int i = 0; i < nSplits; i++) {
                weights[i] = 1.0;
            }
        }
    }

    public FlatSplitSystem(boolean[][] splits, double[] weights, int[] cycle, boolean[] active) {
        nSplits = splits.length;
        nTaxa = splits[0].length;

        this.active = active;

        this.splits = new boolean[splits.length][splits[0].length];
        for (int i = 0; i < splits.length; i++) {
            System.arraycopy(splits[i], 0, this.splits[i], 0, splits[i].length);
        }
        this.weights = new double[weights.length];
        System.arraycopy(weights, 0, this.weights, 0, weights.length);

        this.cycle = cycle;
    }


    public boolean equals(FlatSplitSystem ss) {
//        if(this.nSplits != ss.nSplits)
//        {
//            return false;
//        }
        if (this.nTaxa != ss.nTaxa) {
            return false;
        }
        for (int i = 0; i < splits.length; i++) {
            boolean found = false;
            for (int j = 0; j < ss.splits.length; j++) {
                int same = 0;
                int opposite = 0;
                for (int i1 = 0; i1 < ss.splits[0].length; i1++) {
                    if (splits[i][i1] == ss.splits[j][i1]) {
                        same++;
                    } else {
                        opposite++;
                    }
                }
                if (same == 0 || opposite == 0) {
                    found = true;
                    if (weights[i] != ss.weights[j]) {
                        return false;
                    }
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }


    public boolean isTrivial(boolean[] b) {
        int s = CollectionUtils.nbTrueElements(b);
        int e = b.length;
        if (s == 1 || e - s == 1) {
            return true;
        }
        return false;
    }

    public double[] getWeights() {
        return weights;
    }

    public int getnTaxa() {
        return nTaxa;
    }

    public int getnSplits() {
        return nSplits;
    }

    public boolean[][] getSplits() {
        return splits;
    }

    public void setWeights(double[] weights, double[] minTrivial) {
        this.weights = new double[weights.length];
        for (int i = 0; i < weights.length; i++) {
            if (isTrivial(splits[i]) && minTrivial != null) {
                this.weights[i] = minTrivial[trivialTaxa(splits[i])];
            }
            this.weights[i] += weights[i];
        }
    }

    public double distance(int x1, int x2) {
        double distance = 0.0;
        for (int i = 0; i < splits.length; i++) {
            if (splits[i][x1] ^ splits[i][x2]) {
                distance += weights[i];
            }
        }
        return distance;
    }

    protected int trivialTaxa(boolean[] b) {
        int len = CollectionUtils.nbTrueElements(b);
        boolean right = true;
        if (len > 1) {
            right = false;
        }
        for (int i = 0; i < b.length; i++) {
            if (b[i] == right) {
                return i;
            }
        }
        return -1;
    }

    public boolean isCompatible(int a, int b) {
        //variables for counting the number of occurences of patterns
        int count11 = 0;
        int count10 = 0;
        int count01 = 0;
        int count00 = 0;

        for (int i = 0; i < nTaxa; i++) {
            if ((splits[a][i] == true) && (splits[b][i] == true)) {
                count11++;
            }
            if ((splits[a][i] == true) && (splits[b][i] == false)) {
                count10++;
            }
            if ((splits[a][i] == false) && (splits[b][i] == true)) {
                count01++;
            }
            if ((splits[a][i] == false) && (splits[b][i] == false)) {
                count00++;
            }
        }

        if (count11 == 0 || count10 == 0 || count01 == 0 || count00 == 0) {
            return true;
        } else {
            return false;
        }
    }

    public int[] getCycle() {
        return cycle;
    }

    public String[] getTaxaNames() {
        return taxaNames;
    }

    public boolean[] getActive() {
        return active;
    }

    public void setActive(boolean[] active) {
        this.active = active;
    }

    public void setFit(double fit) {
        this.fit = fit;
    }

}