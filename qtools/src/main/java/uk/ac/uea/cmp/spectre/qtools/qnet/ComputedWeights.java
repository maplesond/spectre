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

package uk.ac.uea.cmp.spectre.qtools.qnet;

import uk.ac.uea.cmp.spectre.core.math.matrix.SymmetricMatrix;

public class ComputedWeights {

    private double[] solution;
    private SymmetricMatrix EtE;

    public ComputedWeights(double[] solution, SymmetricMatrix etE) {
        this.solution = solution;
        EtE = etE;
    }

    public double[] getSolution() {
        return solution;
    }

    public SymmetricMatrix getEtE() {
        return EtE;
    }
}
