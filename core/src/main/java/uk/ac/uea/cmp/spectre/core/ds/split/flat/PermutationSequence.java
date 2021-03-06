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

import uk.ac.uea.cmp.spectre.core.ds.quad.quadruple.QuadrupleSystem;
import uk.ac.uea.cmp.spectre.core.ds.split.SpectreSplitSystem;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitSystem;
import uk.ac.uea.cmp.spectre.core.util.CollectionUtils;

import java.util.Arrays;
import java.util.StringJoiner;

/*This class is used to store the permutation sequence representing
  a flat split system*/

public class PermutationSequence {
    //number of taxa
    private int nTaxa = 0;

    //number of swaps
    private int nSwaps = 0;

    //initial permutation
    private int[] initSequ = null;

    //Sequence of swaps. A swap is specified by the index of the element in the
    //array that is swapped with the successor in the array
    private int[] swaps = null;

    //If the permutation sequence is for a set of taxa then we record which
    //swaps correspond to an active split. Used when filtering out splits with
    //small length etc.
    private boolean[] active = null;

    private String[] taxaNames = null;

    private boolean[][][] splits;
    private boolean[][][] splitsInFront;

    private int[][] ab = new int[nSwaps][2];

    private double[] weights;

    private double[] trivial;

    //value of the fit function from gurobi solver
    private double fit;

    //Constructor of this class from a given initial sequence and a sequence of swaps.
    public PermutationSequence(int[] inInitSequ, int[] inSwaps) {
        initSequ = new int[inInitSequ.length];
        swaps = new int[inSwaps.length];
        active = new boolean[inSwaps.length];

        System.arraycopy(inInitSequ, 0, initSequ, 0, initSequ.length);
        System.arraycopy(inSwaps, 0, swaps, 0, swaps.length);

        for (int i = 0; i < swaps.length; i++) {
            active[i] = true;
        }

        nTaxa = initSequ.length;
        nSwaps = swaps.length;

        computeSplits();
    }

    public int getnTaxa() {
        return nTaxa;
    }

    public int[] getSequence() {
        return initSequ;
    }

    public boolean[] getActive() {
        return active;
    }

    public int getnSwaps() {
        return nSwaps;
    }

    public int[] getSwaps() {
        return swaps;
    }

    public double[] getWeights() {
        return weights;
    }

    public int[][] getAb() {
        return ab;
    }

    public int[][] computeBMatrix() {
        int[][] B = new int[nSwaps][nSwaps];

        int[] swapsWithThemseves = new int[nTaxa - 1];
        for (int j = 0; j < nTaxa - 1; j++) {
            int i = j + 1;
            int m = nTaxa - i;

            swapsWithThemseves[j] = i * Utilities.combinations(3, m) + Utilities.combinations(2, i) * Utilities.combinations(2, m) + m * Utilities.combinations(3, i);
        }

        boolean[][][] intersections = new boolean[nTaxa - 1][4][nTaxa];
        int[][] sizes = new int[nTaxa - 1][4];

        int[] currentPermutation = new int[nTaxa];
        System.arraycopy(initSequ, 0, currentPermutation, 0, initSequ.length);

        for (int i1 = 0; i1 < swaps.length; i1++) {
            int sl = swaps[i1];
            int c = currentPermutation[sl];
            currentPermutation[sl] = currentPermutation[sl + 1];
            currentPermutation[sl + 1] = c;

            setInitialIntersections(intersections, sizes, currentPermutation, sl);

            //printIntersectiontable(i1, intersections);


            B[i1][i1] = swapsWithThemseves[swaps[i1]];
            //updateIntersections(intersections, i1);


            for (int i2 = i1 + 1; i2 < swaps.length; i2++) {
                int entry = 0;

                int remove;
                int add;

                //printIntersectiontable(i1, i2, intersections);

                //if(swaps[i1] == swaps[i2]){printIntersectiontable(i1, i2, intersections);}

                moveAandB(intersections, sizes, i2);

                //if(swaps[i1] == swaps[i2]){printIntersectiontable(i1, i2, intersections);}

                entry += getScore(sizes[swaps[i2]][0], sizes[swaps[i2]][1]);
                entry += getScore(sizes[swaps[i2]][2], sizes[swaps[i2]][3]);
                B[i1][i2] = entry;
                B[i2][i1] = entry;
            }
        }

        return B;
    }

    private int getScore(int s1, int s2) {
        int score = 0;

        if (s1 > 0) {
            if (s2 > 0) {
                int min = (s1 < s2) ? s1 : s2;
                int max = (s1 > s2) ? s1 : s2;
                if (min + max > 3) {
                    if (min == 1) {
                        score = Utilities.combinations(3, max);
                    } else if (min == 2 && max == 2) {
                        score = 1;
                    } else if (min == 2) {
                        score = Utilities.combinations(2, max) + 2 * Utilities.combinations(3, max);
                    } else if (min > 2) {
                        score = min * Utilities.combinations(3, max) + Utilities.combinations(2, min) * Utilities.combinations(2, max) + max * Utilities.combinations(3, min);
                    }
                }
            }
        }

        return score;
    }

    public double[] computebVector(QuadrupleSystem qs) {
        double[] vectorb = new double[nSwaps];

        double[][] sums1vs3 = new double[nTaxa][2];
        double[][] sums3vs1 = new double[nTaxa][2];
        double[][] sums2vs2 = new double[nTaxa][2];

        int[] lastScoreIndex = new int[nTaxa - 1];
        boolean[] precomputed = new boolean[nTaxa - 1];

        int[] permutationSeq = new int[nTaxa];
        System.arraycopy(initSequ, 0, permutationSeq, 0, nTaxa);

        double[] initialWeights = computeInitialSums(sums1vs3, sums3vs1, sums2vs2, initSequ, qs);

        Runtime rt = Runtime.getRuntime();

        for (int i = 0; i < nSwaps; i++) {
            if (i > 0 && i % (nSwaps / 2 + 1) == 0) {
                rt.gc();
            }

            int swapLevel = swaps[i];
            int a = permutationSeq[swapLevel];
            int b = permutationSeq[swapLevel + 1];

            boolean[] above = splits[i][1];
            boolean[] below = splits[i][0];

            above[a] = false;
            below[b] = false;

            int c = a;
            permutationSeq[swapLevel] = permutationSeq[swapLevel + 1];
            permutationSeq[swapLevel + 1] = c;

            if (!precomputed[swapLevel]) {
                vectorb[i] = initialWeights[swapLevel];

                lastScoreIndex[swapLevel] = i;
                precomputed[swapLevel] = true;
            }
            vectorb[i] = vectorb[lastScoreIndex[swapLevel]];
            vectorb[i] -= sums1vs3[a][1];
            vectorb[i] -= sums1vs3[b][0];

            vectorb[i] -= sums3vs1[a][1];
            vectorb[i] += compute1vs3fixedAndSetWithOneOfThreeFixed(b, below, a, qs);

            vectorb[i] -= sums3vs1[b][0];
            vectorb[i] += compute1vs3fixedAndSetWithOneOfThreeFixed(a, above, b, qs);

            vectorb[i] -= sums2vs2[a][1];
            vectorb[i] -= sums2vs2[b][0];
            vectorb[i] += compute2vs2bothSetsWithOneOfTwoFixed(a, below, b, above, qs);

            updateSums(sums1vs3, sums3vs1, sums2vs2, a, b, below, above, qs);

            vectorb[i] += sums1vs3[a][0];
            vectorb[i] += sums1vs3[b][1];

            vectorb[i] += sums2vs2[a][0];
            vectorb[i] += sums2vs2[b][1];
            vectorb[i] -= compute2vs2bothSetsWithOneOfTwoFixed(b, below, a, above, qs);

            vectorb[i] += sums3vs1[a][0];
            vectorb[i] -= compute1vs3fixedAndSetWithOneOfThreeFixed(b, above, a, qs);

            vectorb[i] += sums3vs1[b][1];
            vectorb[i] -= compute1vs3fixedAndSetWithOneOfThreeFixed(a, below, b, qs);

            lastScoreIndex[swapLevel] = i;
        }
        return vectorb;
    }

    private void updateSums(double[][] sums1vs3, double[][] sums3vs1, double[][] sums2vs2, int a, int b, boolean[] below, boolean[] above, QuadrupleSystem qs) {
        boolean[] tmp = new boolean[nTaxa];
        tmp[a] = true;

        double avsbBeBe = compute1vs3setAndSetWithOneOfThreeFixed(tmp, below, b, qs);
        double avsbAbAb = compute1vs3setAndSetWithOneOfThreeFixed(tmp, above, b, qs);
        sums1vs3[a][0] += avsbBeBe;
        sums1vs3[a][1] -= avsbAbAb;

        double BevsabAb = compute1vs3setAndSetWithTwoOfThreeFixed(below, above, a, b, qs);
        sums3vs1[b][0] -= avsbAbAb;
        sums3vs1[b][0] += BevsabAb;

        double AbvsabBe = compute1vs3setAndSetWithTwoOfThreeFixed(above, below, a, b, qs);
        sums3vs1[b][1] -= AbvsabBe;
        sums3vs1[b][1] += avsbBeBe;

        tmp[b] = true;
        double sumABBe = compute2vs2bothSets(tmp, below, qs);
        double sumAAbBBe = compute2vs2bothSetsWithOneOfTwoFixed(a, above, b, below, qs);
        sums2vs2[a][0] -= sumABBe;
        sums2vs2[a][0] += sumAAbBBe;

        double sumABeBAb = compute2vs2bothSetsWithOneOfTwoFixed(a, below, b, above, qs);
        double sumABAb = compute2vs2bothSets(tmp, above, qs);
        sums2vs2[a][1] -= sumABeBAb;
        sums2vs2[a][1] += sumABAb;

        sums2vs2[b][0] -= sumABeBAb;
        sums2vs2[b][0] += sumABBe;

        sums2vs2[b][1] -= sumABAb;
        sums2vs2[b][1] += sumAAbBBe;

        tmp[a] = false;

        double bvsaBeBe = compute1vs3setAndSetWithOneOfThreeFixed(tmp, below, a, qs);
        double bvsaAbAb = compute1vs3setAndSetWithOneOfThreeFixed(tmp, above, a, qs);
        sums1vs3[b][0] -= bvsaBeBe;
        sums1vs3[b][1] += bvsaAbAb;

        sums3vs1[a][0] -= BevsabAb;
        sums3vs1[a][0] += bvsaAbAb;

        sums3vs1[a][1] -= bvsaBeBe;
        sums3vs1[a][1] += AbvsabBe;
    }

    private double compute2vs2bothSetsWithOneOfTwoFixed(int a, boolean[] set1, int b, boolean[] set2, QuadrupleSystem qs) {
        double score = 0;
        int[] elements1 = CollectionUtils.getTrueElements(set1);
        int[] elements2 = CollectionUtils.getTrueElements(set2);
        if (elements1.length > 0 && elements2.length > 0) {
            for (int i1 = 0; i1 < elements1.length; i1++) {
                for (int i2 = 0; i2 < elements2.length; i2++) {
                    score += qs.get2Vs2Weight(a, elements1[i1], b, elements2[i2]);
                }
            }
        }
        return score;
    }

    private double compute2vs2bothSets(boolean[] set1, boolean[] set2, QuadrupleSystem qs) {
        double score = 0;
        if (CollectionUtils.nbTrueElements(set1) > 1 && CollectionUtils.nbTrueElements(set2) > 1) {
            int[][] duplets1 = computeDuplets(set1);
            int[][] duplets2 = computeDuplets(set2);
            for (int i1 = 0; i1 < duplets1.length; i1++) {
                for (int i2 = 0; i2 < duplets2.length; i2++) {
                    score += qs.get2Vs2Weight(duplets1[i1][0], duplets1[i1][1], duplets2[i2][0], duplets2[i2][1]);
                }
            }
        }
        return score;
    }

    private double compute2vs2setWithOneOfTwoFixedAndSet(int a, boolean[] set1, boolean[] set2, QuadrupleSystem qs) {
        double score = 0;
        if (CollectionUtils.nbTrueElements(set1) > 0 && CollectionUtils.nbTrueElements(set2) > 1) {
            int[][] duplets = computeDuplets(set2);
            int[] elements1 = CollectionUtils.getTrueElements(set1);
            for (int i1 = 0; i1 < elements1.length; i1++) {
                for (int i = 0; i < duplets.length; i++) {
                    score += qs.get2Vs2Weight(a, elements1[i1], duplets[i][0], duplets[i][1]);
                }
            }
        }
        return score;
    }

    private double compute1vs3setAndSetWithOneOfThreeFixed(boolean[] set1, boolean[] set2, int a, QuadrupleSystem qs) {
        double score = 0;
        if (CollectionUtils.nbTrueElements(set1) > 0 && CollectionUtils.nbTrueElements(set2) > 1) {
            int[][] duplets = computeDuplets(set2);
            int[] elements1 = CollectionUtils.getTrueElements(set1);
            for (int i1 = 0; i1 < elements1.length; i1++) {
                for (int i = 0; i < duplets.length; i++) {
                    score += qs.get1Vs3Weight(elements1[i1], duplets[i][0], duplets[i][1], a);
                }
            }
        }
        return score;
    }

    private double compute1vs3setAndSetWithTwoOfThreeFixed(boolean[] set1, boolean[] set2, int a, int b, QuadrupleSystem qs) {
        double score = 0;
        if (CollectionUtils.nbTrueElements(set1) > 0 && CollectionUtils.nbTrueElements(set2) > 0) {
            int[] elements1 = CollectionUtils.getTrueElements(set1);
            int[] elements2 = CollectionUtils.getTrueElements(set2);
            for (int i1 = 0; i1 < elements1.length; i1++) {
                for (int i2 = 0; i2 < elements2.length; i2++) {
                    score += qs.get1Vs3Weight(elements1[i1], elements2[i2], a, b);
                }
            }
        }
        return score;
    }

    private double compute1vs3fixedAndSetWithOneOfThreeFixed(int a, boolean[] set, int b, QuadrupleSystem qs) {
        double score = 0;
        if (CollectionUtils.nbTrueElements(set) > 1) {
            int[][] duplets = computeDuplets(set);
            for (int i = 0; i < duplets.length; i++) {
                score += qs.get1Vs3Weight(a, duplets[i][0], duplets[i][1], b);
            }
        }
        return score;
    }

    private double[] computeInitialSums(double[][] sums1vs3, double[][] sums3vs1, double[][] sums2vs2, int[] initSequ, QuadrupleSystem qs) {
        boolean[] below = new boolean[nTaxa];
        boolean[] above = new boolean[nTaxa];
        double[] w = new double[nTaxa - 1];

        for (int i = 0; i < initSequ.length; i++) {
            above[initSequ[i]] = true;
        }

        for (int i = 0; i < initSequ.length; i++) {
            above[initSequ[i]] = false;
            boolean[] current = new boolean[nTaxa];
            current[initSequ[i]] = true;
            sums1vs3[initSequ[i]][0] = compute1vs3bothSets(qs, current, below);
            sums1vs3[initSequ[i]][1] = compute1vs3bothSets(qs, current, above);
            sums3vs1[initSequ[i]][0] = compute1vs3setAndSetWithOneOfThreeFixed(below, above, initSequ[i], qs);
            sums3vs1[initSequ[i]][1] = compute1vs3setAndSetWithOneOfThreeFixed(above, below, initSequ[i], qs);
            sums2vs2[initSequ[i]][0] = compute2vs2setWithOneOfTwoFixedAndSet(initSequ[i], above, below, qs);
            sums2vs2[initSequ[i]][1] = compute2vs2setWithOneOfTwoFixedAndSet(initSequ[i], below, above, qs);
            setInitialSwapWeight(w, i, sums1vs3, sums2vs2, sums3vs1);

            below[initSequ[i]] = true;


        }
        return w;
    }

    private void setInitialSwapWeight(double[] w, int i, double[][] sums1vs3, double[][] sums2vs2, double[][] sums3vs1) {
        if (i == 0) {
            w[i] = sums1vs3[initSequ[i]][1];
        } else if (i > 0 && i < nTaxa - 2) {
            w[i] = w[i - 1];
            w[i] -= sums1vs3[initSequ[i]][0];
            w[i] -= sums2vs2[initSequ[i]][0];
            w[i] -= sums3vs1[initSequ[i]][0];

            w[i] += sums1vs3[initSequ[i]][1];
            w[i] += sums2vs2[initSequ[i]][1];
            w[i] += sums3vs1[initSequ[i]][1];
        } else if (i == nTaxa - 1) {
            w[i - 1] = sums1vs3[initSequ[i]][0];
        }
    }

    private double compute1vs3bothSets(QuadrupleSystem qs, boolean[] set1, boolean[] set2) {
        double score = 0;

        if (CollectionUtils.nbTrueElements(set1) > 0 && CollectionUtils.nbTrueElements(set2) > 2) {
            int[][] triplets = computeTriplets(set2);
            int[] elements1 = CollectionUtils.getTrueElements(set1);
            for (int i1 = 0; i1 < elements1.length; i1++) {
                for (int i = 0; i < triplets.length; i++) {
                    score += qs.get1Vs3Weight(elements1[i1], triplets[i][0], triplets[i][1], triplets[i][2]);
                }
            }
        }
        return score;
    }

    private int[][] computeTriplets(boolean[] set) {
        int[] elements = CollectionUtils.getTrueElements(set);
        int[][] triplets = new int[elements.length * (elements.length - 1) * (elements.length - 2) / 6][3];
        int j = 0;
        for (int i1 = 0; i1 < elements.length - 2; i1++) {
            for (int i2 = i1 + 1; i2 < elements.length - 1; i2++) {
                for (int i3 = i2 + 1; i3 < elements.length; i3++) {
                    triplets[j][0] = elements[i1];
                    triplets[j][1] = elements[i2];
                    triplets[j][2] = elements[i3];
                    j++;
                }
            }
        }
        return triplets;
    }

    private int[][] computeDuplets(boolean[] set) {
        int[] elements = CollectionUtils.getTrueElements(set);
        int size = elements.length;

        int[][] duplets = new int[size * (size - 1) / 2][2];
        int j = 0;
        for (int i1 = 0; i1 < size - 1; i1++) {
            for (int i2 = i1 + 1; i2 < size; i2++) {
                duplets[j][0] = elements[i1];
                duplets[j][1] = elements[i2];
                j++;
            }
        }
        return duplets;
    }

    private void computeSplits() {
        int n = 0;
        for (int i = 0; i < initSequ.length; i++) {
            if (initSequ[i] > n) {
                n = initSequ[i];
            }
        }
        n++;

        splits = new boolean[nSwaps][2][n];
        splitsInFront = new boolean[nSwaps][2][n];
        ab = new int[nSwaps][2];

        int[] seq = new int[n];
        System.arraycopy(initSequ, 0, seq, 0, nTaxa);

        int division;
        for (int i = 0; i < nSwaps; i++) {
            division = swaps[i];
            for (int j = 0; j <= division; j++) {
                splitsInFront[i][0][seq[j]] = true;
            }
            for (int j = division + 1; j < nTaxa; j++) {
                splitsInFront[i][1][seq[j]] = true;
            }


            ab[i][0] = seq[swaps[i]];
            ab[i][1] = seq[swaps[i] + 1];

            division = swaps[i];

            int tmp = seq[swaps[i] + 1];
            seq[swaps[i] + 1] = seq[swaps[i]];
            seq[swaps[i]] = tmp;

            for (int j = 0; j <= division; j++) {
                splits[i][0][seq[j]] = true;
            }
            for (int j = division + 1; j < nTaxa; j++) {
                splits[i][1][seq[j]] = true;
            }
        }
    }

    private void setInitialIntersections(boolean[][][] intersections, int[][] sizes, int[] currentPermutation, int i) {
        for (int j = 0; j < intersections.length; j++) {
            if (j == 0) {
                for (int k = 0; k < currentPermutation.length; k++) {
                    int add = 0;
                    if (k <= i && k <= j) {
                        add = 0;
                    } else if (k <= i && k > j) {
                        add = 2;
                    } else if (k > i && k <= j) {
                        add = 3;
                    } else if (k > i && k > j) {
                        add = 1;
                    }
                    for (int l = 0; l < 4; l++) {
                        boolean set = false;
                        if (l == add) {
                            set = true;
                        }
                        intersections[j][l][currentPermutation[k]] = set;
                    }
                }
                sizes[j][0] = CollectionUtils.nbTrueElements(intersections[j][0]);
                sizes[j][1] = CollectionUtils.nbTrueElements(intersections[j][1]);
                sizes[j][2] = CollectionUtils.nbTrueElements(intersections[j][2]);
                sizes[j][3] = CollectionUtils.nbTrueElements(intersections[j][3]);
            } else {
                for (int i1 = 0; i1 < intersections[j].length; i1++) {
                    System.arraycopy(intersections[j - 1][i1], 0, intersections[j][i1], 0, intersections[j][i1].length);
                    sizes[j][i1] = sizes[j - 1][i1];
                }
                if (j <= i) {
                    intersections[j][2][currentPermutation[j]] = false;
                    intersections[j][0][currentPermutation[j]] = true;


                    sizes[j][2]--;
                    sizes[j][0]++;
                } else {
                    intersections[j][1][currentPermutation[j]] = false;
                    intersections[j][3][currentPermutation[j]] = true;


                    sizes[j][1]--;
                    sizes[j][3]++;
                }

            }
        }
    }


    private void moveAandB(boolean[][][] intersections, int[][] sizes, int i2) {
        int s2 = swaps[i2];
        int a = ab[i2][0];
        int b = ab[i2][1];

        int from = 0;
        int to = 0;

        if (intersections[s2][0][a] == true) {
            from = 0;
            to = 2;
        } else if (intersections[s2][3][a] == true) {
            from = 3;
            to = 1;
        }
        intersections[s2][from][a] = false;
        sizes[s2][from]--;
        intersections[s2][to][a] = true;
        sizes[s2][to]++;

        if (intersections[s2][1][b] == true) {
            from = 1;
            to = 3;
        } else if (intersections[s2][2][b] == true) {
            from = 2;
            to = 0;
        }

        intersections[s2][from][b] = false;
        sizes[s2][from]--;
        intersections[s2][to][b] = true;
        sizes[s2][to]++;
    }


    public int[][] computeCrossingsMatrix() {
        int maxTaxa = 0;
        for (int i = 0; i < initSequ.length; i++) {
            maxTaxa = (initSequ[i] > maxTaxa) ? initSequ[i] : maxTaxa;
        }
        maxTaxa++;
        int[][] crossings = new int[maxTaxa][maxTaxa];

        int[] seq = new int[nTaxa];
        System.arraycopy(initSequ, 0, seq, 0, nTaxa);

        for (int i = 0; i < nSwaps; i++) {
            crossings[seq[swaps[i]]][seq[swaps[i] + 1]] = i;
            crossings[seq[swaps[i] + 1]][seq[swaps[i]]] = i;

            int tmp = seq[swaps[i] + 1];
            seq[swaps[i] + 1] = seq[swaps[i]];
            seq[swaps[i]] = tmp;
        }

        return crossings;
    }

    public boolean[][][] getSplits() {
        return splits;
    }

    public PermutationSequence getSplitSystemFor4Taxa(int a, int b, int x1, int x2, int[][] crossings) {
        int[] taxa = new int[4];
        int j = 0;

        int ia = 0;
        int ib = 0;
        int ix1 = 0;
        int ix2 = 0;

        for (int i = 0; i < initSequ.length; i++) {
            if (initSequ[i] == a || initSequ[i] == b || initSequ[i] == x1 || initSequ[i] == x2) {
                taxa[j] = initSequ[i];
            }
            if (initSequ[i] == a) {
                ia = j++;
            } else if (initSequ[i] == b) {
                ib = j++;
            } else if (initSequ[i] == x1) {
                ix1 = j++;
            } else if (initSequ[i] == x2) {
                ix2 = j++;
            }
        }

        int swapAB = crossings[a][b];
        int swapAx1 = crossings[a][x1];
        int swapAx2 = crossings[a][x2];
        int swapBx1 = crossings[b][x1];
        int swapBx2 = crossings[b][x2];
        int swapx1x2 = crossings[x1][x2];

        int[] swapsFromCrossings = new int[6];
        swapsFromCrossings[0] = swapAB;
        swapsFromCrossings[1] = swapAx1;
        swapsFromCrossings[2] = swapAx2;
        swapsFromCrossings[3] = swapBx1;
        swapsFromCrossings[4] = swapBx2;
        swapsFromCrossings[5] = swapx1x2;

        Arrays.sort(swapsFromCrossings);

        int[] swaps = new int[6];
        j = 0;

        for (int i = 0; i < swapsFromCrossings.length; i++) {
            if (swapsFromCrossings[i] == swapAB) {
                swaps[j++] = (ia < ib) ? ia : ib;
                int t = ia;
                ia = ib;
                ib = t;
            } else if (swapsFromCrossings[i] == swapAx1) {
                swaps[j++] = (ia < ix1) ? ia : ix1;
                int t = ia;
                ia = ix1;
                ix1 = t;
            } else if (swapsFromCrossings[i] == swapAx2) {
                swaps[j++] = (ia < ix2) ? ia : ix2;
                int t = ia;
                ia = ix2;
                ix2 = t;
            } else if (swapsFromCrossings[i] == swapBx1) {
                swaps[j++] = (ib < ix1) ? ib : ix1;
                int t = ib;
                ib = ix1;
                ix1 = t;
            } else if (swapsFromCrossings[i] == swapBx2) {
                swaps[j++] = (ib < ix2) ? ib : ix2;
                int t = ib;
                ib = ix2;
                ix2 = t;
            } else if (swapsFromCrossings[i] == swapx1x2) {
                swaps[j++] = (ix1 < ix2) ? ix1 : ix2;
                int t = ix1;
                ix1 = ix2;
                ix2 = t;
            }
        }

        return new PermutationSequence(taxa, swaps);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("Initial sequence: ");
        StringJoiner sj1 = new StringJoiner(" ");
        for (int i = 0; i < nTaxa; i++) {
            sj1.add(Integer.toString(initSequ[i]));
        }
        sb.append(sj1.toString()).append("\n");

        sb.append("Swaps: ");
        StringJoiner sj2 = new StringJoiner(" ");
        for (int i = 0; i < nSwaps; i++) {
            sj2.add(Integer.toString(swaps[i]));
        }
        sb.append(sj2.toString()).append("\n");

        return sb.toString();
    }

    public void setWeights(double[] w, double[] trivial) {
        setWeights(w);
        setTrivial(trivial);
    }

    public void setWeights(double[] w) {
        this.weights = new double[w.length];
        System.arraycopy(w, 0, weights, 0, w.length);
    }

    public void setTrivial(double[] trivial) {
        this.trivial = new double[trivial.length];
        System.arraycopy(trivial, 0, this.trivial, 0, trivial.length);
    }

    public double[] getTrivial() {
        return trivial;
    }

    public String[] getTaxaNames() {
        return taxaNames;
    }

    public void setTaxaNames(String[] taxaNames) {
        this.taxaNames = new String[taxaNames.length];
        System.arraycopy(taxaNames, 0, this.taxaNames, 0, taxaNames.length);
    }

    public void setFit(double fit) {
        this.fit = fit;
    }

    public void filterSplits(double threshold) {
        if (CollectionUtils.nbTrueElements(active) == 0) {
            for (int i = 0; i < active.length; i++) {
                active[i] = true;
            }
        }

        //int[] increasing = Utilities.orderWeights(active, weights);

        SplitSystem ss = new SpectreSplitSystem(this);

        for (int i = weights.length - 1; i >= 0; i--) {
            for (int j = 0; j < weights.length; j++) {
                if (!ss.isCompatible(i, j)) {
                    if (weights[i] < weights[j] * threshold) {
                        active[i] = false;
                    } else if (weights[j] < weights[i] * threshold) {
                        active[j] = false;
                    }
                }
            }
        }
    }

    public double getFit() {
        return fit;
    }

    public void setActive(boolean[] b) {
        this.active = new boolean[b.length];
        System.arraycopy(b, 0, this.active, 0, b.length);
    }

    public void restoreTrivialWeightsForExternalVertices() {
        SplitSystem ss = new SpectreSplitSystem(this);
        for (int i = 0; i < ss.getNbSplits(); i++) {
            if (ss.get(i).isTrivial()) {
                int taxaNr = ss.get(i).getTrivial();
                if (trivial[taxaNr] > 0) {
                    if (!active[i]) {
                        active[i] = true;
                    }
                    weights[i] = trivial[taxaNr];
                    trivial[taxaNr] = 0;
                }
            }
        }
    }

}