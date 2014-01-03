/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
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

package uk.ac.uea.cmp.phygen.superq.qnet;

import uk.ac.uea.cmp.phygen.core.math.matrix.SymmetricMatrix;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 13/11/13
 * Time: 18:34
 * To change this template use File | Settings | File Templates.
 */
public class ComputedWeights {

    private double[] x;
    private SymmetricMatrix EtE;

    public ComputedWeights(double[] x, SymmetricMatrix etE) {
        this.x = x;
        EtE = etE;
    }

    public double[] getX() {
        return x;
    }

    public SymmetricMatrix getEtE() {
        return EtE;
    }
}
