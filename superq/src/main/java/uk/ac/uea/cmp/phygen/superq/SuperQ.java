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
package uk.ac.uea.cmp.phygen.superq;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.io.nexus.Nexus;
import uk.ac.uea.cmp.phygen.core.io.nexus.NexusReader;
import uk.ac.uea.cmp.phygen.core.io.nexus.NexusWriter;
import uk.ac.uea.cmp.phygen.core.math.optimise.Optimiser;
import uk.ac.uea.cmp.phygen.core.math.optimise.OptimiserException;
import uk.ac.uea.cmp.phygen.core.math.optimise.Problem;
import uk.ac.uea.cmp.phygen.core.ui.gui.RunnableTool;
import uk.ac.uea.cmp.phygen.core.ui.gui.StatusTracker;
import uk.ac.uea.cmp.phygen.qnet.ComputedWeights;
import uk.ac.uea.cmp.phygen.qnet.QNet;
import uk.ac.uea.cmp.phygen.tools.chopper.Chopper;
import uk.ac.uea.cmp.phygen.tools.scale.Scaling;

import java.io.File;
import java.io.IOException;

public class SuperQ extends RunnableTool {

    private static Logger log = LoggerFactory.getLogger(SuperQ.class);
    private SuperQOptions options;

    public SuperQ(SuperQOptions options) {
        this(options, null);
    }

    public SuperQ(SuperQOptions options, StatusTracker tracker) {

        super(tracker);
        this.options = options;
    }

    protected void validateOptions() throws IOException {
        if (this.options == null) {
            throw new IOException("Must specify a valid set of parameters to control superQ.");
        }

        if (this.options.getInputFile() == null || !this.options.getInputFile().exists() || this.options.getInputFile().isDirectory()) {
            throw new IOException("Must specify a valid input file.");
        }

        if (this.options.getOutputFile() == null || this.options.getOutputFile().isDirectory()) {
            throw new IOException("Must specify a valid path where to create the output file.");
        }

        if (this.options.getInputFileFormat() == null) {
            throw new IOException("Must specify a input file format.");
        }
    }

    @Override
    public void run() {

        try {
            validateOptions();

            // Start timing
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            log.info("SUPERQ - Starting job: " + this.options.getInputFile().getPath());

            Runtime rt = Runtime.getRuntime();

            log.debug("FREE MEM - at start: " + rt.freeMemory());

            String type = this.options.getInputFileFormat().toString().toLowerCase();
            String file = this.options.getInputFile().getPath();
            String path = this.options.getOutputFile().getParent();
            File tmpdir = new File(path, "tempfiles");
            tmpdir.mkdir();
            String tmppath = tmpdir.getPath() + File.separator;

            String primarySolverName = this.options.getPrimarySolver() == null ?
                    "Internal NNLS method" :
                    this.options.getPrimarySolver().getIdentifier();

            //System.out.println("path to output file: " + path);
            //System.out.println("temporary path: " + tmppath);


            this.continueRun();

            if (this.options.isScaleInputTree()) {
                //Optional scaling of input trees
                //can just be applied to newick or script format
                if (!(this.options.getInputFileFormat() == SuperQOptions.InputFormat.NEWICK
                        || this.options.getInputFileFormat() == SuperQOptions.InputFormat.SCRIPT)) {
                    throw new Exception("Scale function can only be applied if the input format is newick or script!");
                } else {
                    notifyUser("SCALING - Scaling input trees - Using: " + primarySolverName);

                    int cutIdx1 = file.lastIndexOf(File.separator);
                    int cutIdx2 = file.lastIndexOf(".");
                    file = tmppath + "scaled" + file.substring(cutIdx1 + 1, cutIdx2) + ".script";


                    //The uk.ac.uea.cmp.phygen.tools.scale method gets the input file and produces a scaled quartet file for each of the input trees
                    //uk.ac.uea.cmp.phygen.tools.scale function gets the format type, input file and a prefix to name the scaled quartet files
                    Scaling.run(
                            this.options.getInputFile(),
                            new File(file),
                            Scaling.Mode.valueOf(this.options.getInputFileFormat().toString().toUpperCase()),
                            this.options.getPrimarySolver());

                    type = "script";


                    rt.gc();
                    log.debug("FREE MEM - after scaling: " + rt.freeMemory());

                    this.continueRun();
                }
            }

            notifyUser("CHOPPER - Breaking input trees into quartets");
            Chopper.run(new File(file), new File(tmppath + "qw"), type.toUpperCase());

            rt.gc();
            log.debug("FREE MEM - after running Chopper: " + rt.freeMemory());

            this.continueRun();

            notifyUser("QNET - Calculating the circular ordering - Using " + primarySolverName);
            QNet qnet = new QNet();
            ComputedWeights computedWeights = qnet.execute(
                    new File(tmppath + "qw"),
                    false,
                    -1.0,
                    this.options.getPrimarySolver()
            );

            rt.gc();
            log.debug("FREE MEM - after running Q-Net: " + rt.freeMemory());

            this.continueRun();

            //System.out.println("output path: " + this.output_file.getPath());
            String filterTempFile = tmppath + "unfiltered-split-system.nex";
            File weightsOutput = this.options.getFilter() != null ? new File(filterTempFile) : this.options.getOutputFile();

            double[] solution = computedWeights.getX();

            if (this.options.getSecondaryProblem() == null || this.options.getSecondarySolver() == null) {
                log.info("SECONDARY OPTIMISATION - Not requested");
            } else {

                Optimiser secondarySolver = this.options.getSecondarySolver();
                notifyUser("SECONDARY OPTIMISATION - Requested " + secondarySolver.toString() + " solver with " + this.options.getSecondaryProblem() + " objective.");

                try {

                    // Create problem from the computer weights
                    Problem problem = this.options.getSecondaryProblem().compileProblem(qnet.getN(), computedWeights.getX(), computedWeights.getEtE().toArray());

                    // Run the secondary optimisation step
                    double[] solution2 = this.options.getSecondaryProblem().getName() == "MINIMA" ?
                            this.minimaOptimise(secondarySolver, problem, computedWeights.getX()) :     // Special handling of MINIMA objective
                            secondarySolver.optimise(problem).getVariableValues();                      // Normally just call child's optimisation method

                    // Sum the solutions
                    for (int i = 0; i < solution.length; i++) {
                        solution[i] = solution[i] + solution2[i];
                    }
                } catch (OptimiserException use) {
                    log.warn("SECONDARY OPTIMISATION - Invalid solution.  Keeping original solution from first optimisation step.");
                }
            }

            notifyUser("SUPERQ - Saving weights to file");
            qnet.writeWeights(weightsOutput, solution, null, 0);


            rt.gc();
            log.debug("FREE MEM - after computing weights: " + rt.freeMemory());

            if (this.options.getFilter() != null) {
                notifyUser("SUPERQ - filtering splits");

                this.filter(new File(filterTempFile), this.options.getOutputFile(), this.options.getFilter());
            }

            notifyUser("CLEANUP - Removing temporary files in " + tmpdir.toString());

            // Clean up temp dir
            FileUtils.deleteDirectory(tmpdir);

            this.trackerFinished(true);

            // Print run time on screen
            stopWatch.stop();
            log.info("SUPERQ - Completed Successfully - Total run time (H:M:S:MS): " + stopWatch.toString());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            this.setErrorMessage(e.getMessage());
            this.trackerFinished(false);
        } finally {
            this.notifyListener();
        }
    }

    /**
     * To be used only in conjunction with the MINIMA objective
     *
     * NOTE: This might be broken... probably need to modify the coefficients of the problem internally for each run
     *
     * @param problem
     * @return
     * @throws OptimiserException
     */
    protected double[] minimaOptimise(Optimiser optimiser, Problem problem, double[] data) throws OptimiserException {

        double[] coefficients = problem.getObjective().getExpression().getLinearCoefficients(problem.getVariables());

        double[] solution = new double[data.length];

        // This is a bit messy, but essentially what is happening is that we run the solver for each coefficient, and if
        // the non-negativity constraint at each variable is > 0, then we run the solver but we only take the result from
        // this variable, otherwise the solution at this position is 0.
        for (int k = 0; k < data.length; k++) {
            if (data[k] > 0.0) {
                coefficients[k] = 1.0;
                double[] help = optimiser.optimise(problem).getVariableValues();
                solution[k] = help[k];
                coefficients[k] = 0.0;
            } else {
                solution[k] = 0;
            }
        }

        return solution;
    }

    protected void filter(File inFile, File outFile, double threshold) throws IOException {

        // Load
        Nexus raw = new NexusReader().readNexusData(inFile);

        // Filter
        Nexus filtered = raw.filter(threshold);

        // Save
        new NexusWriter().writeNexusData(outFile, filtered);
    }

    private void notifyUser(String message) {
        log.info(message);
        this.trackerInitUnknownRuntime(message);
    }
}
