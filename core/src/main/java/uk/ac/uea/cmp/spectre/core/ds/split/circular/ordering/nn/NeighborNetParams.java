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

package uk.ac.uea.cmp.spectre.core.ds.split.circular.ordering.nn;

/**
 * Created by dan on 09/03/14.
 */
public class NeighborNetParams {

    private static final double ATHIRD = 1.0 / 3.0;

    private double alpha;
    private double beta;
    private double gamma;

    public NeighborNetParams() {
        this(ATHIRD, ATHIRD);
    }

    public NeighborNetParams(final double alpha, final double beta) {

        this(alpha, beta, 1.0 - alpha - beta);
    }

    public NeighborNetParams(final double alpha, final double beta, final double gamma) {

        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;

        if (!valid()) {
            throw new IllegalArgumentException("Parameters do not all sum to 1.0: " + this.toString());
        }
    }

    boolean valid() {
        return alpha + beta + gamma == 1.0;
    }

    @Override
    public String toString() {
        return "alpha: " + alpha + "; beta: " + beta + "; gamma: " + gamma;
    }

    public double getAlpha() {
        return this.alpha;
    }

    public double getBeta() {
        return this.beta;
    }

    public double getGamma() {
        return this.gamma;
    }

}
