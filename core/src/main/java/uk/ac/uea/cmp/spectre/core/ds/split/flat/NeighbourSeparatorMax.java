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

package uk.ac.uea.cmp.spectre.core.ds.split.flat;

import uk.ac.uea.cmp.spectre.core.ds.quad.quadruple.Quadruple;
import uk.ac.uea.cmp.spectre.core.ds.quad.quadruple.QuadrupleSystem;
import uk.ac.uea.cmp.spectre.core.util.CollectionUtils;

/**
 * Separates neighbors by maximizing length of the {@linkplain PermutationSequence}
 * in each iteration of {@linkplain Neighbours} separation.
 *
 * @author balvociute
 */
public class NeighbourSeparatorMax implements NeighbourSeparator {
    int crossedTaxa;

    @Override
    public PermutationSequence popOutNeighbours(Neighbours[] neighbours,
                                                PermutationSequence ps,
                                                QuadrupleSystem qs,
                                                int j,
                                                int n) {

        Runtime rt = Runtime.getRuntime();
        for (int i = j; i >= 0; i--) {
            if (i > 0 && i % (n / 4) == 0) {
                rt.gc();
            }
            ScoredPermutationSequence[] possiblePermutations =
                    new ScoredPermutationSequence[(ps.getnTaxa() - 1) * 2];

            int a = neighbours[i].getA();
            int b = neighbours[i].getB();

            qs.setActive(b);

            restoreWeights(qs, a, b);

            double maxSoFar = findOptimalPermutationSequence(a, a, b,
                    neighbours[i],
                    ps,
                    -1.0,
                    qs,
                    possiblePermutations,
                    0);
            maxSoFar = findOptimalPermutationSequence(a, b, a,
                    neighbours[i],
                    ps,
                    maxSoFar,
                    qs,
                    possiblePermutations,
                    ps.getnTaxa() - 1);

            PermutationSequence resultPS = null;


            for (int k = 0; k < possiblePermutations.length; k++) {
                if (possiblePermutations[k].score == maxSoFar) {
                    if (possiblePermutations[k].circular == true || resultPS == null) {
                        resultPS = possiblePermutations[k].permutationSequence;
                    }
                }
            }
            ps = resultPS;
        }
        return ps;
    }

    /**
     * Scores candidate permutation sequences.
     *
     * @param taxonRepresentingAgglomeratedPair
     * @param taxonA
     * @param taxonB
     * @param neighbours
     * @param agglomeratedPermutationSequence
     * @param bestSoFar                         best score obtained so far.
     * @param qs                                quadruple system.
     * @param possiblePermutations              array of candidate permutation sequences.
     * @param currentPermutation                index of the current candidate
     *                                          permutation sequence.
     * @return best score found.
     */
    protected double findOptimalPermutationSequence(
            int taxonRepresentingAgglomeratedPair,
            int taxonA,
            int taxonB,
            Neighbours neighbours,
            PermutationSequence agglomeratedPermutationSequence,
            double bestSoFar,
            QuadrupleSystem qs,
            ScoredPermutationSequence[] possiblePermutations,
            int currentPermutation) {
        PermutationSequence tmpPS;
        double score = 0.0;
        int[][] crossings = null;
        for (int i = 1; i < agglomeratedPermutationSequence.getnTaxa(); i++) {
            ScoredPermutationSequence tmpScoredPermutationSequence =
                    new ScoredPermutationSequence();

            tmpPS = generatePermutationSequence(
                    taxonRepresentingAgglomeratedPair,
                    taxonA,
                    taxonB,
                    neighbours,
                    i, agglomeratedPermutationSequence,
                    tmpScoredPermutationSequence);


            PermutationSequence lastPS = (score != 0.0)
                    ? possiblePermutations[currentPermutation - 1].permutationSequence
                    : null;
            int[][] lastCrossings = crossings;
            crossings = tmpPS.computeCrossingsMatrix();

            score = scorePermutationSequence(neighbours,
                    tmpPS,
                    qs,
                    score,
                    lastPS,
                    crossings,
                    lastCrossings);

            tmpScoredPermutationSequence.score = score;

            if (bestSoFar == -1 || score > bestSoFar) {
                bestSoFar = score;
            }
            possiblePermutations[currentPermutation++] = tmpScoredPermutationSequence;
        }
        return bestSoFar;
    }

    /**
     * This method computes from a permutation sequence on n-1 taxa
     * a permutation sequence on n taxa by expanding back an agglomerated
     * pair of elements. These two elements are neighbors in the resulting
     * permutation sequence. The neighbors are swapped immediately after
     * the l-th swap the agglomerated element was involved in.
     *
     * @param taxonRepresentingAgglomeratedPair      the taxon that represents the
     *                                               agglomerated pair.
     * @param a                                      the taxon in the agglomerated pair that comes first in the
     *                                               initial sequence.
     * @param b                                      the taxon in the agglomerated pair that comes second in the
     *                                               initial sequence.
     * @param neighbours                             a pair of neighbors.
     * @param swapAfterWhichNeighboursShouldBeSwaped index of the swap
     * @param agglomeratedPermutationSequence        permutation sequence before
     *                                               neighbor separation.
     * @param tmpScoredPermutationSequence           scored permutation sequence.
     * @return optimal permutation sequence after the separation of the
     * neighbors.
     */
    protected PermutationSequence generatePermutationSequence(
            int taxonRepresentingAgglomeratedPair,
            int a, int b,
            Neighbours neighbours,
            int swapAfterWhichNeighboursShouldBeSwaped,
            PermutationSequence agglomeratedPermutationSequence,
            ScoredPermutationSequence tmpScoredPermutationSequence) {
        PermutationSequence resultPermutationSequence;
        int nTaxaAgg = agglomeratedPermutationSequence.getnTaxa();
        int[] initialPermutationSequence = new int[nTaxaAgg + 1];
        int[] currentPermutationSequence = new int[nTaxaAgg];
        int nSwaps = (initialPermutationSequence.length * (initialPermutationSequence.length - 1)) / 2;
        int[] swaps = new int[nSwaps];


        //Information used to keep track of the position of the agglomerated element.
        int currentPosition = 0;
        boolean involved = false;

        int j = 0;

        //First compute the new initial sequence.
        //Agglomerated taxon is replaced by the taxa that are in it.
        //Also initialize the array that keeps track of the current permutation
        //and also initialize the variable that keeps track of the current position
        //of taxonRepresentingAgglomeratedPair in the current permutation

        int[] sequenceAgg = agglomeratedPermutationSequence.getSequence();

        for (int i = 0; i < nTaxaAgg; i++) {
            currentPermutationSequence[i] = sequenceAgg[i];
            if (sequenceAgg[i] == taxonRepresentingAgglomeratedPair) {
                initialPermutationSequence[j++] = a;
                initialPermutationSequence[j++] = b;
                currentPosition = i;
            } else {
                initialPermutationSequence[j++] = sequenceAgg[i];
            }
        }

        int nSwapsAgg = agglomeratedPermutationSequence.getnSwaps();
        int[] swapsAgg = agglomeratedPermutationSequence.getSwaps();
        int[][] abAgg = agglomeratedPermutationSequence.getAb();

        j = 0; //used to keep track of the index in the new swaps array.
        int jj = 0; //used to keep track of the index in the old swaps array.
        int k = 0; //used to keep track of the number of swaps the agglomerated element is involved.
        for (int i = 0; i < nSwapsAgg; i++) {
            //check whether the agglomerated taxon is involved in the swap and update its current position
            if (currentPermutationSequence[swapsAgg[i]] == taxonRepresentingAgglomeratedPair) {
                involved = true;
                currentPosition++;
                swaps[j++] = swapsAgg[i] + 1;
                swaps[j++] = swapsAgg[i];
                k++;
                if (k == swapAfterWhichNeighboursShouldBeSwaped) {
                    crossedTaxa = (abAgg[jj][0] == a || abAgg[jj][0] == b) ? abAgg[jj][1] : abAgg[jj][0];
                    swaps[j++] = currentPosition;
                    if (currentPosition == currentPermutationSequence.length - 1) {
                        tmpScoredPermutationSequence.circular = true;
                    }
                }

                jj++;
            } else if (currentPermutationSequence[swapsAgg[i] + 1] == taxonRepresentingAgglomeratedPair) {
                involved = true;
                currentPosition--;
                swaps[j++] = swapsAgg[i];
                swaps[j++] = swapsAgg[i] + 1;
                k++;
                if (k == swapAfterWhichNeighboursShouldBeSwaped) {
                    crossedTaxa = (abAgg[jj][0] == a || abAgg[jj][0] == b) ? abAgg[jj][1] : abAgg[jj][0];
                    swaps[j++] = currentPosition;
                    if (currentPosition == 0) {
                        tmpScoredPermutationSequence.circular = true;
                    }
                }

                jj++;
            } else {
                involved = false;
                if (swapsAgg[i] < currentPosition) {
                    swaps[j++] = swapsAgg[i];
                } else {
                    swaps[j++] = swapsAgg[i] + 1;
                }
                jj++;
            }

            //compute the current permutation of the taxa
            int h = currentPermutationSequence[swapsAgg[i]];
            currentPermutationSequence[swapsAgg[i]] = currentPermutationSequence[swapsAgg[i] + 1];
            currentPermutationSequence[swapsAgg[i] + 1] = h;

        }
        resultPermutationSequence = new PermutationSequence(initialPermutationSequence, swaps);
        tmpScoredPermutationSequence.permutationSequence = resultPermutationSequence;
        return resultPermutationSequence;
    }


    /**
     * Scores the permutation sequences in the reconstruction process. Evaluates
     * score only for the splits containing both neighbors
     *
     * @param neighbours    current pair of neighbors.
     * @param ps            candidate permutation sequence.
     * @param qs            current quadruple system.
     * @param score         base score.
     * @param lastPS        last candidate permutation sequence for the current pair of
     *                      neighbors.
     * @param crossings     crossings matrix for candidate permutation sequence.
     * @param lastCrossings crossing matric for last candidate permutation
     *                      sequence.
     * @return score for this candidate permutation sequence.
     */
    protected double scorePermutationSequence(Neighbours neighbours,
                                              PermutationSequence ps,
                                              QuadrupleSystem qs,
                                              double score,
                                              PermutationSequence lastPS,
                                              int[][] crossings,
                                              int[][] lastCrossings) {

        int a = neighbours.getA();
        int b = neighbours.getB();

        int nTaxa = ps.getnTaxa();
        int[] sequence = ps.getSequence();

        if (lastPS == null) {
            for (int i1 = 0; i1 < nTaxa - 1; i1++) {
                for (int i2 = i1 + 1; i2 < nTaxa; i2++) {
                    if (a != sequence[i1] && a != sequence[i2]
                            && b != sequence[i1] && b != sequence[i2]) {
                        FlatSplitSystem ssSmall = new FlatSplitSystem(
                                ps.getSplitSystemFor4Taxa(a, b,
                                        sequence[i1],
                                        sequence[i2],
                                        crossings));
                        score += qs.getFitRestriction(a, b,
                                sequence[i1],
                                sequence[i2],
                                ssSmall);
                    }
                }
            }
        } else {
            for (int i = 0; i < nTaxa; i++) {
                if (a != sequence[i]
                        && b != sequence[i]
                        && crossedTaxa != sequence[i]) {
                    FlatSplitSystem ssSmallOld = new FlatSplitSystem(lastPS.getSplitSystemFor4Taxa(a, b, crossedTaxa, sequence[i], lastCrossings));
                    score -= qs.getFitRestriction(a, b, crossedTaxa, sequence[i], ssSmallOld);

                    FlatSplitSystem ssSmallNew = new FlatSplitSystem(ps.getSplitSystemFor4Taxa(a, b, crossedTaxa, sequence[i], crossings));
                    score += qs.getFitRestriction(a, b, crossedTaxa, sequence[i], ssSmallNew);
                }
            }
        }
        return score;
    }

    /**
     * Restores quadruple system after the separation of neighbors.
     *
     * @param qs quadruple system.
     * @param a  index of the first neighbor.
     * @param b  index of the second neighbor.
     */
    private void restoreWeights(QuadrupleSystem qs, int a, int b) {
        int[] taxa = qs.getTaxaInt();
        for (int i1 = 0; i1 < taxa.length; i1++) {
            if (taxa[i1] != a && taxa[i1] != b) {
                for (int i2 = i1 + 1; i2 < taxa.length; i2++) {
                    if (taxa[i2] != a && taxa[i2] != b) {
                        for (int i3 = i2 + 1; i3 < taxa.length; i3++) {
                            if (taxa[i3] != a && taxa[i3] != b) {
                                Quadruple qA = qs.getQuadrupleUnsorted(a, taxa[i1], taxa[i2], taxa[i3]);
                                Quadruple qB = qs.getQuadrupleUnsorted(b, taxa[i1], taxa[i2], taxa[i3]);
                                reaverageWeights(qs, a, b, qA, qB);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Restores quadruple split weights for two quadruples that were
     * agglomerated when taxa a and b were identified as neighbors.
     *
     * @param qs quadruple system.
     * @param a  index of the first neighbor.
     * @param b  index of the second neighbor.
     * @param qA quadruple containing first neighbor.
     * @param qB quadruple containing second neighbor.
     */
    private void reaverageWeights(QuadrupleSystem qs, int a, int b, Quadruple qA, Quadruple qB) {
        int aIndex = qA.getTaxa(a, 0);

        double[] weights = new double[7];

        int bIndex = qB.getTaxa(b, 0);

        double[] weights1 = new double[7];
        System.arraycopy(qA.getWeights(), 0, weights1, 0, 7);
        double[] weights2 = new double[7];
        System.arraycopy(qB.getWeights(), 0, weights2, 0, 7);

        if (bIndex - aIndex == 1) {
            CollectionUtils.swapTwoInAnArray(weights2, aIndex, bIndex);
            int index2 = (aIndex == 1) ? 4 : 6;
            CollectionUtils.swapTwoInAnArray(weights2, 5, index2);
        }
        if (bIndex - aIndex == 2) {
            CollectionUtils.swapTwoInAnArray(weights2, bIndex, bIndex - 1);
            CollectionUtils.swapTwoInAnArray(weights2, aIndex, bIndex - 1);
            if (aIndex == 0) {
                CollectionUtils.swapTwoInAnArray(weights2, 4, 5);
                CollectionUtils.swapTwoInAnArray(weights2, 5, 6);
            }
            if (aIndex == 1) {
                CollectionUtils.swapTwoInAnArray(weights2, 5, 6);
                CollectionUtils.swapTwoInAnArray(weights2, 4, 5);
            }
        }
        if (bIndex - aIndex == 3) {
            CollectionUtils.swapTwoInAnArray(weights2, 2, 3);
            CollectionUtils.swapTwoInAnArray(weights2, 1, 2);
            CollectionUtils.swapTwoInAnArray(weights2, 0, 1);
            CollectionUtils.swapTwoInAnArray(weights2, 4, 6);
        }

        for (int i = 0; i < weights.length; i++) {
            weights[i] = 2.0 * weights1[i] - weights2[i];
        }
        qA.setWeights(weights);
    }

    /**
     * A class containing candidate permutation sequence with its score.
     */
    protected class ScoredPermutationSequence {
        PermutationSequence permutationSequence;
        double score;
        boolean circular;
        double supplementaryScore;

        protected ScoredPermutationSequence(PermutationSequence permutationSequence, double score, boolean circular) {
            this.permutationSequence = permutationSequence;
            this.score = score;
            this.circular = circular;
        }

        protected ScoredPermutationSequence() {
            this.permutationSequence = null;
            this.score = -0.1;
            this.circular = false;
            this.supplementaryScore = 0.0;
        }
    }

}