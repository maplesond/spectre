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
import uk.ac.uea.cmp.phygen.flatnj.ds.QuadrupleSystem;

/**
 * Finds {@linkplain Neighbours} by combing two criteria. First finds pairs of taxa
 * whose agglomeration would result loosing least length. Then, from these pairs
 * selects one that maximizes the remaining length.
 *
 * @author balvociute
 */
public class NeighbourFinderCombined implements NeighbourFinder {

    @Override
    public Neighbours findNeighbours(QuadrupleSystem qs, double[][][] scores) {
        Neighbours neighbours = new Neighbours();
        Double maxSoFar = null;
        Double minSoFar = null;

        int atAll = 0;

        int[] taxa = CollectionUtils.getElements(qs.getTaxa());
        int nTaxa = taxa.length;

        for (int i1 = 0; i1 < nTaxa - 1; i1++) {
            for (int i2 = i1 + 1; i2 < nTaxa; i2++) {
                if (scores[taxa[i1]][taxa[i2]][0] < 0) {
                    throw new IllegalStateException("Score was less than 0.  Taxa Length: " + taxa.length + "; Score: " + scores[taxa[i1]][taxa[i2]][0]);
                }
                minSoFar = (minSoFar == null || minSoFar > scores[taxa[i1]][taxa[i2]][0]) ? scores[taxa[i1]][taxa[i2]][0] : minSoFar;
                atAll++;
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
}
