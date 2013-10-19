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

package uk.ac.uea.cmp.phygen.flatnj;

import uk.ac.uea.cmp.phygen.core.util.CollectionUtils;
import uk.ac.uea.cmp.phygen.flatnj.ds.PermutationSequence;
import uk.ac.uea.cmp.phygen.flatnj.ds.Quadruple;
import uk.ac.uea.cmp.phygen.flatnj.ds.QuadrupleSystem;

/**
 * Class for computing {@linkplain PermutationSequence} from {@linkplain QuadrupleSystem}.
 *
 * @author balvociute
 */
public class PermutationSequenceFactory {
    private NeighbourFinder neighbourFinder = new NeighbourFinderCombined();
    private QuadrupleAgglomerator quadrupleAgglomerator = new QuadrupleAgglomeratorAverage();
    private NeighbourSeparator neighbourSeparator = new NeighbourSeparatorMax();
    private Scorer scorer = new Scorer();

    /**
     * Constructor with specific {@linkplain NeighbourFinder},
     * {@linkplain QuadrupleAgglomerator} and {@linkplain NeighbourSeparator}.
     *
     * @param neighbourFinder       {@linkplain NeighbourFinder}.
     * @param quadrupleAgglomerator {@linkplain QuadrupleAgglomerator}.
     * @param neighbourSeparator    {@linkplain NeighbourSeparator}.
     */
    public PermutationSequenceFactory(NeighbourFinder neighbourFinder, QuadrupleAgglomerator quadrupleAgglomerator, NeighbourSeparator neighbourSeparator) {
        this.neighbourFinder = neighbourFinder;
        this.neighbourSeparator = neighbourSeparator;
        this.quadrupleAgglomerator = quadrupleAgglomerator;
    }

    /**
     * Constructor that sets default {@linkplain NeighbourFinder},
     * {@linkplain QuadrupleAgglomerator} and {@linkplain NeighbourSeparator}.
     */
    public PermutationSequenceFactory() {
        neighbourFinder = new NeighbourFinderCombined();
        quadrupleAgglomerator = new QuadrupleAgglomeratorAverage();
        neighbourSeparator = new NeighbourSeparatorMax();
    }

    /**
     * Computes {@linkplain PermutationSequence}.
     *
     * @param qs {@linkplain QuadrupleSystem}
     * @return
     */
    public PermutationSequence computePermutationSequence(QuadrupleSystem qs) {
        int n = qs.getnTaxa();
        Neighbours[] neighbours = new Neighbours[n - 4];

        int i = agglomerate(neighbours, qs);

        PermutationSequence ps = findBestOnFour(qs);

        ps = neighbourSeparator.popOutNeighbours(neighbours, ps, qs, i, n);

        return ps;
    }


    /**
     * Finds the permutation sequence that best represents remaining quartet.
     */
    private PermutationSequence findBestOnFour(QuadrupleSystem qs) {
        int[] x = CollectionUtils.getElements(qs.getTaxa());
        int j = 0;
        Quadruple q = qs.getQuadruple(x);
        double[] weights = q.getWeights();

        int[] initSequ = new int[4];
        int[] swaps = new int[6];

        //We choose a permutation sequence/flat split system that maximizes
        //the total weight of those splits of the quartet that can be represented.
        //Since every flat split system on four elements can represent 6 out of
        //7 possible splits we first find a split in the quartet of minimal weight.
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
    private int agglomerate(Neighbours[] neighbours, QuadrupleSystem qs) {
        int i = -1;
        int n = qs.getnTaxa();

        double[][][] scores = scorer.initializeScores(qs);

        Runtime rt = Runtime.getRuntime();

        while (qs.getnTaxa() > 4) {
            i++;
            if (i > 0 && i % (n / 4) == 0) {
                rt.gc();
            }

            neighbours[i] = neighbourFinder.findNeighbours(qs, scores);

            scorer.updateScoresBeforeWeightChange(scores, neighbours[i], qs);

            quadrupleAgglomerator.agglomerate(qs, neighbours[i]);

            scorer.updateScoresAfterWeightChange(scores, neighbours[i], qs);

        }
        return i;
    }

    /**
     * Prints min or max scores from scores table.
     *
     * @param scores scores table.
     * @param taxa   taxa indexes.
     * @param i      score index. 0 - min, 1 - max.(probably)
     */
    private void printScores(double[][][] scores, int[] taxa, int i) {
        for (int j1 = 0; j1 < taxa.length; j1++) {
            System.out.print("\t" + taxa[j1]);
        }
        System.out.println();
        for (int j1 = 0; j1 < taxa.length; j1++) {
            System.out.print(taxa[j1]);
            for (int j2 = 0; j2 < taxa.length; j2++) {
                System.out.print("\t" + scores[taxa[j1]][taxa[j2]][i]);
            }
            System.out.println();
        }
        System.out.println();
    }
}
