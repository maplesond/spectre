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

package uk.ac.uea.cmp.spectre.qtools.qnet;

import uk.ac.earlham.metaopt.Objective;
import uk.ac.earlham.metaopt.Optimiser;
import uk.ac.earlham.metaopt.OptimiserFactory;

import java.io.File;

public class QNetOptions {

    public static final String DESC_INPUT = "The q weight file containing the taxa and quartets to process.";

    public static final String DESC_OUTPUT = "REQUIRED: The nexus file that will contain output.";

    public static final String DESC_LOG = "If false, normalises quartets linearly, if true normalises quartets using natural log";

    public static final String DESC_TOLERANCE = "The tolerance to use when computing edge weights using internal method.  Not used if an external optimiser is selected.";

    public static final String DESC_OPTIMISER = "If specified, uses an external quadratic optimiser to compute weights.  If not, " +
            "then an internal method is used instead.  Available external optimisers: " + OptimiserFactory.getInstance().listOperationalOptimisers(Objective.ObjectiveType.QUADRATIC);


    private File input;
    private File output;
    private Optimiser optimiser;
    private boolean logNormalise;
    private double tolerance;

    public QNetOptions() {
        this(null, null, null, false, -1.0);
    }

    public QNetOptions(File input, File output, Optimiser optimiser, boolean logNormalise, double tolerance) {
        this.input = input;
        this.output = output;
        this.optimiser = optimiser;
        this.logNormalise = logNormalise;
        this.tolerance = tolerance;
    }

    public File getInput() {
        return input;
    }

    public void setInput(File input) {
        this.input = input;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public Optimiser getOptimiser() {
        return optimiser;
    }

    public void setOptimiser(Optimiser optimiser) {
        this.optimiser = optimiser;
    }

    public boolean isLogNormalise() {
        return logNormalise;
    }

    public void setLogNormalise(boolean logNormalise) {
        this.logNormalise = logNormalise;
    }

    public double getTolerance() {
        return tolerance;
    }

    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }
}
