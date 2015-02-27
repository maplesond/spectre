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

package uk.ac.uea.cmp.spectre.net.netmake.fits;

import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrixGenerator;
import uk.ac.uea.cmp.spectre.core.ds.distance.MetricDistanceGenerator;

public class RandomNeighbourNetFitsConfig {

    private static final DistanceMatrixGenerator DEFAULT_DISTANCE_GENERATOR = new MetricDistanceGenerator();
    private static final int DEFAULT_MIN_N = 100;
    private static final int DEFAULT_MAX_N = 100;
    private static final int DEFAULT_STEPPING = 1;
    private static final int DEFAULT_SAMPLES_PER_STEP = 50;

    private DistanceMatrixGenerator distanceGenerator;
    private int minN;
    private int maxN;
    private int stepping;
    private int samplesPerStep;

    public RandomNeighbourNetFitsConfig() {
        this(DEFAULT_DISTANCE_GENERATOR, DEFAULT_MIN_N, DEFAULT_MAX_N, DEFAULT_STEPPING, DEFAULT_SAMPLES_PER_STEP);
    }

    public RandomNeighbourNetFitsConfig(DistanceMatrixGenerator distanceGenerator, int minN, int maxN, int stepping, int samplesPerStep) {
        this.distanceGenerator = distanceGenerator;
        this.minN = minN;
        this.maxN = maxN;
        this.stepping = stepping;
        this.samplesPerStep = samplesPerStep;
    }

    public DistanceMatrixGenerator getDistanceGenerator() {
        return distanceGenerator;
    }

    public int getMinN() {
        return minN;
    }

    public int getMaxN() {
        return maxN;
    }

    public int getStepping() {
        return stepping;
    }

    public int getSamplesPerStep() {
        return samplesPerStep;
    }


    public int calcNumberOfFilesToGenerate() {
        return ((maxN - minN) / stepping + 1) * samplesPerStep;
    }
}
