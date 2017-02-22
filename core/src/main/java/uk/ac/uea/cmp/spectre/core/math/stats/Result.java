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

package uk.ac.uea.cmp.spectre.core.math.stats;

/**
 * Created by dan on 18/04/14.
 */
public class Result {

    private int truePositives;
    private int falsePositives;
    private int falseNegatives;

    public Result(int truePositives, int falsePositives, int falseNegatives, int nbInputEntries, int nbOutputEntries) {
        this.truePositives = truePositives;
        this.falsePositives = falsePositives;
        this.falseNegatives = falseNegatives;
    }

    public int getTruePositives() {
        return truePositives;
    }

    public int getFalsePositives() {
        return falsePositives;
    }

    public int getFalseNegatives() {
        return falseNegatives;
    }

    public double getRecall() {
        return 100.0 * (double)truePositives / (double)(truePositives + falseNegatives);
    }

    public double getPrecision() {
        return 100.0 * (double)truePositives / (double)(truePositives + falsePositives);
    }

    public double getF1Score() {
        return 100.0 * (double)(2 * truePositives) / (double)(2 * truePositives + falseNegatives + falsePositives);
    }

}
