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

package uk.ac.uea.cmp.spectre.flatnj.gen4s;

import uk.ac.uea.cmp.spectre.core.ds.Alignment;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.flatnj.ds.Quadruple;
import uk.ac.uea.cmp.spectre.flatnj.ds.QuadrupleSystem;

/**
 * Quadruple system factory from multiple sequence alignment.
 *
 * @author balvociute
 */
public class QSFactoryAlignment implements QSFactory {
    /**
     * {@linkplain Alignment} to be used for the estimation of
     * {@link QuadrupleSystem}.
     */
    private Alignment a;
    /**
     * character {@link uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix}.
     */
    private DistanceMatrix dm;

    /**
     * Constructs {@linkplain QSFactoryAlignment} object that will use
     * {@linkplain Alignment} and {@linkplain  uk.ac.uea.cmp.spectre.core.ds.distance.FlexibleDistanceMatrix} to compute new
     * {@link QuadrupleSystem}.
     *
     * @param a  an {@linkplain Alignment} to be used for
     *           {@link QuadrupleSystem}
     *           estimation.
     * @param dm a {@linkplain uk.ac.uea.cmp.spectre.core.ds.distance.FlexibleDistanceMatrix} for more precise quadruple split
     *           weight estimation. In case it is <code>null</code>, inverted
     *           identity matrix will be used by default.
     */
    public QSFactoryAlignment(Alignment a, DistanceMatrix dm) {
        this.a = a;
        this.dm = dm;
    }

    @Override
    public QuadrupleSystem computeQS() {
        QuadrupleSystem qs;

        char[][] seq = a.getSequencesAsCharArray();

        SplitsEstimator splitsEstimator = new SplitsEstimator(dm);
        
        /* 
         * variable to store quadruple system for given alignment is
         * initialized
         */
        qs = new QuadrupleSystem(a.size());
        
        /* 
         * inTaxa is an array to store indexes of sequences that ought to be
         * included in the quadruple. inSequences -- stores sequences themselves
         * in the same order as ids are stored in inTaxa.
         */

        int[] inTaxa = new int[4];
        char[][] inSequences = new char[4][seq[0].length];
        for (int i1 = 0; i1 < seq.length; i1++) {
            inTaxa[0] = i1;
            inSequences[0] = seq[i1];
            for (int i2 = i1 + 1; i2 < seq.length; i2++) {
                inTaxa[1] = i2;
                inSequences[1] = seq[i2];
                for (int i3 = i2 + 1; i3 < seq.length; i3++) {
                    inTaxa[2] = i3;
                    inSequences[2] = seq[i3];
                    for (int i4 = i3 + 1; i4 < seq.length; i4++) {
                        inTaxa[3] = i4;
                        inSequences[3] = seq[i4];
                        /* New quadruple is formed */
                        double[] inWeights = splitsEstimator.estimate(inSequences);
                        Quadruple quadruple = new Quadruple(inTaxa, inWeights);
                        /* and added to the current quadruple system */
                        qs.add(quadruple);
                    }
                }
            }
        }
        return qs;
    }
}
