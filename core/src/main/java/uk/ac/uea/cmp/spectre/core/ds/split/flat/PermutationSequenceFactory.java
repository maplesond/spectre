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

import uk.ac.uea.cmp.spectre.core.ds.quad.quadruple.Quadruple;
import uk.ac.uea.cmp.spectre.core.ds.quad.quadruple.QuadrupleSystem;
import uk.ac.uea.cmp.spectre.core.util.CollectionUtils;

/**
 * Class for computing {@linkplain PermutationSequence} from {@linkplain QuadrupleSystem}.
 *
 * @author balvociute
 */
public class PermutationSequenceFactory {

    private NeighbourSeparator neighbourSeparator;
    private Scorer scorer;

    /**
     * Constructor that sets default {@linkplain NeighbourSeparator}.
     */
    public PermutationSequenceFactory() {
        this.neighbourSeparator = new NeighbourSeparatorMax();
        this.scorer = new Scorer();
    }

    /**
     * Computes {@linkplain PermutationSequence}.
     *
     * @param qs {@linkplain QuadrupleSystem}
     * @return Computed permutation sequence
     */
    public PermutationSequence computePermutationSequence(QuadrupleSystem qs) {

        Neighbours[] neighbours = new Neighbours[qs.getNbActiveTaxa() - 4];

        int i = agglomerate(neighbours, qs);

        PermutationSequence ps = findBestOnFour(qs);

        ps = neighbourSeparator.popOutNeighbours(neighbours, ps, qs, i, qs.getNbActiveTaxa());

        return ps;
    }


    /**
     * Finds the permutation sequence that best represents remaining quartet.
     */
    private PermutationSequence findBestOnFour(QuadrupleSystem qs) {
        int[] x = CollectionUtils.getTrueElements(qs.getActive());
        int j = 0;
        Quadruple q = qs.getQuadruple(x);
        double[] weights = q.getWeights();

        int[] initSequ = new int[4];
        int[] swaps = new int[6];

        //We choose a permutation sequence/flat split system that maximizes
        //the total length of those splits of the quartet that can be represented.
        //Since every flat split system on four elements can represent 6 out of
        //7 possible splits we first find a split in the quartet of minimal length.
        for (int i = 1; i < 7; i++) {
            if (weights[i] < weights[j]) {
                j = i;
            }
        }

        if (j == 0) {
            //taxon taxa[0] is placed in the interior.
            setInitialSequence(initSequ, 1, 0, 2, 3, x);
        } else if (j == 1) {
            //taxon taxa[1] is placed in the interior.
            setInitialSequence(initSequ, 0, 1, 2, 3, x);
        } else if (j == 2) {
            //taxon taxa[2] is placed in the interior.
            setInitialSequence(initSequ, 0, 2, 1, 3, x);
            //setInitialSequence(initSequ,0,1,2,3,x);
        } else if (j == 3) {
            //taxon taxa[3] is placed in the interior.
            setInitialSequence(initSequ, 0, 3, 1, 2, x);
        } else if (j == 4) {
            //We place taxa in circular ordering taxa[0],taxa[2],taxa[1],taxa[3]
            setInitialSequence(initSequ, 0, 2, 1, 3, x);
        } else if (j == 5) {
            //We place taxa in circular ordering taxa[0],taxa[1],taxa[2],taxa[3]
            setInitialSequence(initSequ, 0, 1, 2, 3, x);
        } else if (j == 6) {
            //We place taxa in circular ordering taxa[0],taxa[1],taxa[3],taxa[2]
            setInitialSequence(initSequ, 0, 1, 3, 2, x);
        }

        setSwaps(swaps, j);
        PermutationSequence pSequ = new PermutationSequence(initSequ, swaps);

        return pSequ;
    }

    /**
     * Sets initial permutation of the initial {@linkplain PermutationSequence}.
     *
     * @param initSequ array to write taxa indexes to.
     * @param x1       index of first taxa.
     * @param x2       index of second taxa.
     * @param x3       index of third taxa.
     * @param x4       index of fourth taxa.
     * @param taxa     array containing taxa indexes.
     */
    private void setInitialSequence(int[] initSequ, int x1, int x2, int x3, int x4, int[] taxa) {
        initSequ[0] = taxa[x1];
        initSequ[1] = taxa[x2];
        initSequ[2] = taxa[x3];
        initSequ[3] = taxa[x4];
    }

    /**
     * Sets swaps in the initial {@linkplain PermutationSequence}.
     *
     * @param swaps array to write swaps to.
     * @param j     index of the quadruple split that will not be represented by
     *              swaps.
     */
    private void setSwaps(int[] swaps, int j) {
        if (j < 4) {
            swaps[0] = 1;
            swaps[1] = 0;
            swaps[2] = 1;
        } else {
            swaps[0] = 0;
            swaps[1] = 1;
            swaps[2] = 0;
        }
        swaps[3] = 2;
        swaps[4] = 1;
        swaps[5] = 0;
    }

    /**
     * Finds {@linkplain Neighbours} and agglomerates {@linkplain QuadrupleSystem} by
     * joining them.
     *
     * @param neighbours array of {@linkplain Neighbours} to save current neighbors
     *                   to.
     * @param qs         {@linkplain QuadrupleSystem}
     * @return number of neighbors.
     */
    protected int agglomerate(Neighbours[] neighbours, QuadrupleSystem qs) {
        int i = -1;
        int n = qs.getNbActiveTaxa();

        double[][][] scores = scorer.initializeScores(qs);

        Runtime rt = Runtime.getRuntime();

        while (qs.getNbActiveTaxa() > 4) {
            i++;
            if (i > 0 && i % (n / 4) == 0) {
                rt.gc();
            }

            neighbours[i] = findNeighbours(qs, scores);

            scorer.updateScoresBeforeWeightChange(scores, neighbours[i], qs);

            quadrupleAgglomerate(qs, neighbours[i]);

            scorer.updateScoresAfterWeightChange(scores, neighbours[i], qs);

        }
        return i;
    }

    protected Neighbours findNeighbours(QuadrupleSystem qs, double[][][] scores) {
        Neighbours neighbours = new Neighbours();
        Double maxSoFar = null;
        Double minSoFar = null;

        int[] taxa = CollectionUtils.getTrueElements(qs.getActive());
        int nTaxa = taxa.length;

        for (int i1 = 0; i1 < nTaxa - 1; i1++) {
            for (int i2 = i1 + 1; i2 < nTaxa; i2++) {
                if (scores[taxa[i1]][taxa[i2]][0] < 0) {
                    throw new IllegalStateException("Score was less than 0.  Taxa Length: " + taxa.length + "; Score: " + scores[taxa[i1]][taxa[i2]][0]);
                }
                minSoFar = (minSoFar == null || minSoFar > scores[taxa[i1]][taxa[i2]][0]) ? scores[taxa[i1]][taxa[i2]][0] : minSoFar;
            }
        }

        double treshold = 0.001 * minSoFar;

        for (int i1 = 0; i1 < nTaxa - 1; i1++) {
            for (int i2 = i1 + 1; i2 < nTaxa; i2++) {
                if (scores[taxa[i1]][taxa[i2]][0] - minSoFar <= treshold) {
                    if (maxSoFar == null || scores[taxa[i1]][taxa[i2]][1] > maxSoFar) {
                        neighbours.setAB(taxa[i1], taxa[i2]);
                        maxSoFar = scores[taxa[i1]][taxa[i2]][1];
                    }
                }
            }
        }
        return neighbours;
    }

    protected void quadrupleAgglomerate(QuadrupleSystem qs, Neighbours neighbours) {
        int[] taxa = CollectionUtils.getTrueElements(qs.getActive());

        int a = neighbours.getA();
        int b = neighbours.getB();

        int[] i = new int[3];

        for (int i1 = 0; i1 < taxa.length; i1++) {
            i[0] = taxa[i1];
            if (i[0] != a && i[0] != b) {
                for (int i2 = i1 + 1; i2 < taxa.length; i2++) {
                    i[1] = taxa[i2];
                    if (i[1] != a && i[1] != b) {
                        for (int i3 = i2 + 1; i3 < taxa.length; i3++) {
                            i[2] = taxa[i3];
                            if (i[2] != a && i[2] != b) {
                                Quadruple qA = qs.getQuadrupleUnsorted(a, i[0], i[1], i[2]);
                                Quadruple qB = qs.getQuadrupleUnsorted(b, i[0], i[1], i[2]);
                                averageWeights(a, b, qA, qB);
                            }
                        }
                    }
                }
            }
        }

        qs.setInactive(b);
    }

    /**
     * Averages weights of quadruple splits given {@linkplain Quadruple}s and sets
     * those weights to quadruple qA.
     *
     * @param a  index of the first taxa from {@linkplain Neighbours}.
     * @param b  index of the second taxa from {@linkplain Neighbours}.
     * @param qA {@linkplain Quadruple} containing taxa a.
     * @param qB {@linkplain Quadruple} containing taxa b.
     */
    protected void averageWeights(int a, int b, Quadruple qA, Quadruple qB) {
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
            weights[i] = (weights1[i] + weights2[i]) / 2.0;
        }
        qA.setWeights(weights);
    }
}
