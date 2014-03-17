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
package uk.ac.uea.cmp.spectre.qtools.superq;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.tgac.metaopt.Optimiser;
import uk.ac.tgac.metaopt.OptimiserException;
import uk.ac.tgac.metaopt.Problem;
import uk.ac.uea.cmp.spectre.core.ds.quartet.GroupedQuartetSystem;
import uk.ac.uea.cmp.spectre.core.ds.split.CircularSplitSystem;
import uk.ac.uea.cmp.spectre.core.io.nexus.Nexus;
import uk.ac.uea.cmp.spectre.core.io.nexus.NexusReader;
import uk.ac.uea.cmp.spectre.core.io.nexus.NexusWriter;
import uk.ac.uea.cmp.spectre.core.ui.gui.RunnableTool;
import uk.ac.uea.cmp.spectre.core.ui.gui.StatusTracker;
import uk.ac.uea.cmp.spectre.qtools.qmaker.QMaker;
import uk.ac.uea.cmp.spectre.qtools.qnet.QNet;
import uk.ac.uea.cmp.spectre.qtools.qnet.QNetResult;

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

        if (this.options.getInputFiles() == null || this.options.getInputFiles().length == 0) {
            throw new IOException("Must specify at least one valid input file.");
        }

        if (this.options.getOutputFile() == null || this.options.getOutputFile().isDirectory()) {
            throw new IOException("Must specify a valid path where to create the output file.");
        }

    }

    @Override
    public void run() {

        try {

            // Check we have something sensible to work with
            validateOptions();

            // Get a shortcut to runtime object for checking memory usage
            Runtime rt = Runtime.getRuntime();

            // Start timing
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            log.info("Starting job");
            log.debug("FREE MEM - at start: " + rt.freeMemory());

            this.continueRun();

            notifyUser("Converting input trees into a combined quartet system.  " +
                    (this.options.getScalingSolver() != null ? "(Scaling input (optimiser: " + this.options.getScalingSolver().getIdentifier() + ")" : ""));

            GroupedQuartetSystem combinedQuartetSystem = new QMaker().execute(
                    this.options.getInputFiles(),
                    this.options.getScalingSolver());

            rt.gc();
            log.debug("FREE MEM - after running QMaker: " + rt.freeMemory());

            this.continueRun();

            String primarySolverName = this.options.getPrimarySolver() == null ?
                    "INTERNAL" :
                    this.options.getPrimarySolver().getIdentifier();

            notifyUser("Running QNet (optimiser: " + primarySolverName + ")");
            QNetResult qnetResult = new QNet().execute(combinedQuartetSystem, false, -1.0, this.options.getPrimarySolver());

            rt.gc();
            log.debug("FREE MEM - after running Q-Net: " + rt.freeMemory());

            this.continueRun();

            double[] solution = qnetResult.getComputedWeights().getSolution();

            if (this.options.getSecondaryProblem() == null || this.options.getSecondarySolver() == null) {
                log.info("Secondary optimisation - Not requested");
            } else {

                Optimiser secondarySolver = this.options.getSecondarySolver();
                notifyUser("Secondary optimisation (optimiser: " + secondarySolver.getIdentifier() + "; objective: " +
                        this.options.getSecondaryProblem().getName() + ")");

                try {

                    // Create problem from the computed weights
                    Problem problem = this.options.getSecondaryProblem().compileProblem(qnetResult.getCircularOrdering().size(),
                            solution, qnetResult.getComputedWeights().getEtE().toArray());

                    // Run the secondary optimisation step
                    double[] solution2 = this.options.getSecondaryProblem().getName() == "MINIMA" ?
                            this.minimaOptimise(secondarySolver, problem, solution) :     // Special handling of MINIMA objective
                            secondarySolver.optimise(problem).getVariableValues();        // Normally just call child's optimisation method

                    // Sum the solutions (modifies the qnet result)
                    for (int i = 0; i < solution.length; i++) {
                        solution[i] = solution[i] + solution2[i];
                    }
                } catch (OptimiserException use) {
                    log.warn("Secondary optimisation - Invalid solution.  Keeping original solution from first optimisation step.");
                }
            }

            CircularSplitSystem ss = qnetResult.createSplitSystem(null, QNetResult.SplitLimiter.STANDARD);

            rt.gc();
            log.debug("FREE MEM - after computing weights: " + rt.freeMemory());

            // Filter split system if required
            if (this.options.getFilter() != null) {

                notifyUser("Filtering out bottom " + this.options.getFilter() * 100.0 + " % of splits");
                ss.filterByWeight(this.options.getFilter());
            }


            // Save split system
            File outputFile = this.options.getOutputFile();
            File outputDir = this.options.getOutputFile().getParentFile();

            // Create output dir if required
            if (outputDir != null && !outputDir.exists()) {
                outputDir.mkdirs();
            }

            notifyUser("Saving weights to file: " + outputFile.getAbsolutePath());
            new NexusWriter().writeSplitSystem(outputFile, ss);


            this.trackerFinished(true);

            // Print run time on screen
            stopWatch.stop();
            log.info("Completed Successfully - Total run time: " + stopWatch.toString());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            this.setError(e);
            this.trackerFinished(false);
        } finally {
            this.notifyListener();
        }
    }


    /**
     * To be used only in conjunction with the MINIMA objective
     * <p/>
     * NOTE: This might be broken... probably need to modify the coefficients of the problem internally for each run
     *
     * @param problem
     * @return The solution
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

    protected void filterNexus(File inFile, File outFile, double threshold) throws IOException {

        // Load
        Nexus raw = new NexusReader().readNexusData(inFile);

        // Filter
        raw.getSplitSystem().filterByWeight(threshold);

        // Save
        new NexusWriter().writeNexusData(outFile, raw);
    }

    private void notifyUser(String message) {
        log.info(message);
        this.trackerInitUnknownRuntime(message);
    }

    public static void configureLogging() {
        // Setup logging
        File propsFile = new File("etc/logging.properties");

        if (!propsFile.exists()) {
            BasicConfigurator.configure();
            log.info("No logging configuration found.  Using default logging properties.");
        } else {
            PropertyConfigurator.configure(propsFile.getPath());
            log.info("Found logging configuration: " + propsFile.getAbsoluteFile());
        }
    }
}
