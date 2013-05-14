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
import org.apache.commons.math3.optimization.linear.UnboundedSolutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.io.nexus.NexusData;
import uk.ac.uea.cmp.phygen.core.io.nexus.NexusReader;
import uk.ac.uea.cmp.phygen.core.io.nexus.NexusWriter;
import uk.ac.uea.cmp.phygen.core.math.optimise.Objective;
import uk.ac.uea.cmp.phygen.core.math.optimise.Solver;
import uk.ac.uea.cmp.phygen.core.ui.gui.RunnableTool;
import uk.ac.uea.cmp.phygen.core.ui.gui.StatusTracker;
import uk.ac.uea.cmp.phygen.qnet.QNet;
import uk.ac.uea.cmp.phygen.qnet.WeightsComputeNNLSInformative;
import uk.ac.uea.cmp.phygen.qnet.WriteWeightsToNexus;
import uk.ac.uea.cmp.phygen.superq.chopper.Chopper;
import uk.ac.uea.cmp.phygen.superq.scale.Scaling;

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

            //System.out.println("path to output file: " + path);
            //System.out.println("temporary path: " + tmppath);


            this.continueRun();

            if (this.options.isScaleInputTree()) {
                //Optional scaling of input trees
                //can just be applied to newick or script format
                if (!Solver.GUROBI.getOptimiserSystem().isOperational()) {
                    log.warn("Can't apply scaling as Gurobi is not available.  Skipping step");
                } else if (!(this.options.getInputFileFormat() == SuperQOptions.InputFormat.NEWICK
                        || this.options.getInputFileFormat() == SuperQOptions.InputFormat.SCRIPT)) {
                    throw new Exception("Scale function can just be applied, if input format is newick or script!");
                } else {
                    notifyUser("SCALING - Scaling input trees - Using GUROBI");
                    //The uk.ac.uea.cmp.phygen.superq.scale method gets the input file and produces a scaled quartet file for each of the input trees
                    //uk.ac.uea.cmp.phygen.superq.scale function gets the format type, input file and a prefix to name the scaled quartet files
                    Scaling.main(new String[]{
                            this.options.getInputFileFormat().toString().toLowerCase(),
                            this.options.getInputFile().getPath(),
                            tmppath
                    });
                    type = "script";

                    int cutIdx1 = file.lastIndexOf(File.separator);
                    int cutIdx2 = file.lastIndexOf(".");
                    file = tmppath + "scaled" + file.substring(cutIdx1 + 1, cutIdx2) + ".script";

                    rt.gc();
                    log.debug("FREE MEM - after scaling: " + rt.freeMemory());

                    this.continueRun();
                }
            }

            notifyUser("CHOPPER - Breaking input trees into quartets");
            Chopper.main(new String[]{
                    type,
                    file,
                    tmppath + "qw"
            });

            rt.gc();
            log.debug("FREE MEM - after running uk.ac.uea.cmp.phygen.superq.chopper: " + rt.freeMemory());

            this.continueRun();

            notifyUser("QNET - Calculating the circular ordering - Using " + this.options.getPrimarySolver().toString());
            QNet qnet = new QNet();
            qnet.execute(
                    new File(tmppath + "qw"),
                    false,
                    -1.0,
                    this.options.getPrimarySolver().toString().toLowerCase()
            );

            rt.gc();
            log.debug("FREE MEM - after running uk.ac.uea.cmp.phygen.superq.qnet: " + rt.freeMemory());

            this.continueRun();

            //System.out.println("output path: " + this.output_file.getPath());
            String filterTempFile = tmppath + "unfiltered-split-system.nex";
            String weightsOutputPath = this.options.getFilter() != null ? filterTempFile : this.options.getOutputFile().getPath();
            double[] solution = WeightsComputeNNLSInformative.getx();

            if (this.options.getBackupObjective() == Objective.NONE || this.options.getBackupSolver() == Solver.NONE) {
                log.info("SECONDARY OPTIMISATION - Not requested");
            } else {
                
                Solver backupSolver = this.options.getBackupSolver();
                if (backupSolver == Solver.BEST_AVAILABLE) {
                    backupSolver = Solver.getBestOperationalSolver();
                }
                notifyUser("SECONDARY OPTIMISATION - Requested " + backupSolver.toString() + " solver with " + this.options.getBackupObjective() + " objective.");

                // Prepare problem matrix
                double[][] matrix = WeightsComputeNNLSInformative.getEtE().toArray();

                try {
                    // Run the secondary optimisation step
                    double[] solution2 = backupSolver.optimise(solution, matrix, this.options.getBackupObjective());

                    // Sum the solutions
                    for (int i = 0; i < solution.length; i++) {
                        solution[i] = solution[i] + solution2[i];
                    }
                } catch (UnboundedSolutionException use) {
                    log.warn("SECONDARY OPTIMISATION - Invalid solution.  Keeping original solution from first optimisation step.");
                }
            }

            notifyUser("SUPERQ - Saving weights to file");
            WriteWeightsToNexus.writeWeights(qnet, weightsOutputPath, solution, null, 0);


            rt.gc();
            log.debug("FREE MEM - after computing weights: " + rt.freeMemory());

            if (this.options.getFilter() != null) {
                notifyUser("FILTER - filtering splits");

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

    protected void filter(File inFile, File outFile, double threshold) throws IOException {

        // Load
        NexusData raw = new NexusReader().readNexusData(inFile);

        // Filter
        NexusData filtered = raw.filter(threshold);

        // Save
        new NexusWriter().writeNexusData(outFile, filtered);
    }

    private void notifyUser(String message) {
        log.info(message);
        this.trackerInitUnknownRuntime(message);
    }
}
