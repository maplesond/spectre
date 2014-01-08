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
package uk.ac.uea.cmp.phygen.superq.qnet;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.tgac.metaopt.Optimiser;
import uk.ac.tgac.metaopt.OptimiserException;
import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.Taxon;
import uk.ac.uea.cmp.phygen.core.ds.quartet.CanonicalWeightedQuartetMap;
import uk.ac.uea.cmp.phygen.core.ds.quartet.GroupedQuartetSystem;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetSystemCombiner;
import uk.ac.uea.cmp.phygen.core.ds.quartet.WeightedQuartetGroupMap;
import uk.ac.uea.cmp.phygen.core.ds.split.CircularOrdering;
import uk.ac.uea.cmp.phygen.core.ds.split.CircularSplitSystem;
import uk.ac.uea.cmp.phygen.core.io.nexus.NexusWriter;
import uk.ac.uea.cmp.phygen.core.ui.gui.RunnableTool;
import uk.ac.uea.cmp.phygen.core.ui.gui.StatusTracker;
import uk.ac.uea.cmp.phygen.core.util.CollectionUtils;
import uk.ac.uea.cmp.phygen.superq.qnet.holders.*;
import uk.ac.uea.cmp.phygen.tools.quart.Quart;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The QNet main class
 * <p/>
 * Presently runnable holder for Stefan Gr�newalds circular ordering-generating
 * algorithm
 */
public class QNet extends RunnableTool {

    private static Logger log = LoggerFactory.getLogger(QNet.class);

    private QNetOptions options;

    /**
     * Default constructor for CLI use
     */
    public QNet() {
    }

    /**
     * Constructor for use from the GUI
     * @param options
     * @param statusTracker
     */
    public QNet(QNetOptions options, StatusTracker statusTracker) {
        super(statusTracker);
        this.options = options;
    }

    /**
     * Runs QNet from input file, which might contain trees, distance matrices or quartet systems, converts the input
     * into a quartet network, normalises the quartet weights, then calculates the circular ordering and computes
     * edge weights.
     * @param input The file containing trees, distance matrices or quartet systems, will parse as appropriate based on
     *              the file extension.
     * @param logNormalise Whether to normalise the quartet weights by natural log, or not.
     * @param tolerance The tolerance to apply when computing weights
     * @param optimiser The optimiser to use when computing weights
     * @return The QNet results, which contains a quartet system, a set of computed weights and the quartet system
     * that was used to calculate these things.
     * @throws IOException Thrown if there was an issue loading the input file.
     * @throws QNetException Thrown if there were any unexpected issues with the QNET algorithm implementation
     * @throws OptimiserException Thrown if there was an issue running the optimiser when computing weights
     */
    public QNetResult execute(File input, boolean logNormalise, double tolerance, Optimiser optimiser)
            throws IOException, QNetException, OptimiserException {

        String ext = FilenameUtils.getExtension(input.getName());

        notifyUser("Loading and combining quartet systems from: " + input.getName());

        QuartetSystemCombiner combinedQuartetSystem =  new Quart().execute(input, ext.toUpperCase());

        return this.execute(combinedQuartetSystem, logNormalise, tolerance, optimiser);
    }

    /**
     * Runs QNet from a combined quartet system.  Calculates the circular ordering and compute edge weights.
     * @param combinedQuartetSystem The combined split system to process
     * @param logNormalise Whether to normalise the quartet weights by natural log, or not.
     * @param tolerance The tolerance to apply when computing weights
     * @param optimiser The optimiser to use when computing weights
     * @return The QNet results, which contains a quartet system derived from the combined quartet system that was input,
     * a set of computed weights and the quartet system that was used to calculate these things.
     * @throws QNetException Thrown if there were any unexpected issues with the QNET algorithm implementation
     * @throws OptimiserException Thrown if there was an issue running the optimiser when computing weights
     */
    public QNetResult execute(QuartetSystemCombiner combinedQuartetSystem, boolean logNormalise, double tolerance, Optimiser optimiser)
            throws OptimiserException, QNetException {

        notifyUser("Creating a grouped quartet system");

        // Get the actual combined quartet system
        GroupedQuartetSystem groupedQuartetSystem = combinedQuartetSystem.create();

        notifyUser("Normalising quartets " + (logNormalise ? " (using log)" : "") + ".");

        // Normalise the values in the network
        groupedQuartetSystem.normaliseQuartets(logNormalise);


        notifyUser("Computing circular ordering.");

        // Order the taxa
        CircularOrdering circularOrdering = new CyclicOrderer().computeCircularOrdering(
                combinedQuartetSystem.getTaxa(),
                groupedQuartetSystem.getQuartets());

        notifyUser("Computing weights");

        // Get the actual combined quartet system
        GroupedQuartetSystem quartetSystem = combinedQuartetSystem.create();

        // Compute the weights
        ComputedWeights solution = this.computeWeights(quartetSystem, tolerance, optimiser);

        return new QNetResult(circularOrdering, solution, quartetSystem);
    }


    /**
     * Computers edge weights from a quartet system
     * @param quartetSystem The quartet system
     * @param tolerance The tolerance to apply when computing weights
     * @param optimiser The optimiser to use when computing weights
     * @return A set of computer edge weights
     * @throws QNetException Thrown if there were any unexpected issues with the QNET algorithm implementation
     * @throws OptimiserException Thrown if there was an issue running the optimiser when computing weights
     */
    public ComputedWeights computeWeights(GroupedQuartetSystem quartetSystem, double tolerance, Optimiser optimiser)
            throws OptimiserException, QNetException {

        return WeightsComputeNNLSInformative.computeWeights(quartetSystem, tolerance, optimiser);
    }



    private void notifyUser(String message) {
        log.info(message);
        this.trackerInitUnknownRuntime(message);
    }

    public static void configureLogging() {
        // Setup logging
        File propsFile = new File("logging.properties");

        if (!propsFile.exists()) {
            BasicConfigurator.configure();
            log.info("No logging configuration found.  Using default logging properties.");
        } else {
            PropertyConfigurator.configure(propsFile.getPath());
            log.info("Found logging configuration: " + propsFile.getAbsoluteFile());
        }
    }


    protected void validateOptions() throws IOException {
        if (this.options == null) {
            throw new IOException("Must specify a valid set of parameters to control superQ.");
        }

        if (this.options.getInput() == null || !this.options.getInput().exists() || this.options.getInput().isDirectory()) {
            throw new IOException("Must specify a valid input file.");
        }

        if (this.options.getOutput() == null || this.options.getOutput().isDirectory()) {
            throw new IOException("Must specify a valid path where to create the output file.");
        }
    }

    @Override
    public void run() {

        try{

            // Check we have something sensible to work with
            validateOptions();

            // Get a shortcut to runtime object for checking memory usage
            Runtime rt = Runtime.getRuntime();

            // Start timing
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            log.info("Starting job: " + this.options.getInput().getPath());

            // Execute QNet
            QNetResult result = this.execute(this.options.getInput(), this.options.isLogNormalise(), this.options.getTolerance(), this.options.getOptimiser());

            notifyUser("QNet algorithm completed.  Saving results...");

            // Output results in nexus file in standard mode
            CircularSplitSystem ss = result.createSplitSystem(null, QNetResult.SplitLimiter.STANDARD);

            new NexusWriter().writeSplitSystem(this.options.getOutput(), ss);



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
}