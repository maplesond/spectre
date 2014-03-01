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
package uk.ac.uea.cmp.phybre.core.ds.distance;

import uk.ac.uea.cmp.phybre.core.ds.Taxa;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 01/05/13
 * Time: 01:18
 * To change this template use File | Settings | File Templates.
 */
public class RandomDistanceGenerator implements DistanceMatrixGenerator {

    @Override
    public DistanceMatrix generateDistances(final int n) {

        DistanceMatrix distanceMatrix = new FlexibleDistanceMatrix();
        Taxa taxa = new Taxa(n);

        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                if (i != j) {
                    double aDistance = Math.round(Math.random() * 1.E5) / 1.E5;
                    distanceMatrix.setDistance(taxa.get(i), taxa.get(j), aDistance);
                    distanceMatrix.setDistance(taxa.get(j), taxa.get(i), aDistance);
                }
            }
        }

        return distanceMatrix;
    }
}
