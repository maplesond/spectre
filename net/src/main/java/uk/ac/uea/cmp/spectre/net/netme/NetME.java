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

package uk.ac.uea.cmp.spectre.net.netme;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitSystem;
import uk.ac.uea.cmp.spectre.core.io.SpectreReader;
import uk.ac.uea.cmp.spectre.core.io.SpectreReaderFactory;
import uk.ac.uea.cmp.spectre.core.io.nexus.Nexus;
import uk.ac.uea.cmp.spectre.core.io.nexus.NexusReader;
import uk.ac.uea.cmp.spectre.core.ui.gui.RunnableTool;
import uk.ac.uea.cmp.spectre.core.ui.gui.StatusTracker;

import java.io.File;

/**
 * Constructs a minimum evolution tree from the specified network with its
 * implied circular order.
 * <p/>
 * See D Bryant, 1997: <I>Building Trees, Hunting for Trees and
 * Comparing Trees - Theories and Methods in Phylogenetic Analysis</I>
 */
public class NetME extends RunnableTool {

    private final static Logger log = LoggerFactory.getLogger(NetME.class);


    private NetMEOptions options;

    public NetME() {
        this(new NetMEOptions());
    }

    public NetME(NetMEOptions options) {
        this(options, null);
    }

    public NetME(NetMEOptions options, StatusTracker statusTracker) {
        super(statusTracker);
        this.options = options;
    }

    public NetMEOptions getOptions() {
        return options;
    }

    public void setOptions(NetMEOptions options) {
        this.options = options;
    }

    public NetMEResult execute(DistanceMatrix distanceMatrix, IdentifierList circularOrdering) {
        return new MinimumEvolutionCalculator().calcMinEvoTree(distanceMatrix, circularOrdering);
    }

    private void notifyUser(String message) {
        log.info(message);
        this.trackerInitUnknownRuntime(message);
    }

    @Override
    public void run() {
        try {

            // Start timing
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            this.notifyUser("Parsing nexus file: " + this.options.getInputFile().getAbsolutePath());

            // Parse input nexus file
            Nexus nexus = new NexusReader().parse(this.options.getInputFile());

            DistanceMatrix distanceMatrix = nexus.getDistanceMatrix();

            if (distanceMatrix == null) {
                throw new IllegalStateException("Could not load distances block from input nexus file");
            }


            log.info("Distance Matrix Loaded from file: " + this.options.getInputFile().getAbsolutePath());

            // Load circular ordering from the provided nexus file
            SplitSystem splitSystem = nexus.getSplitSystem();

            if (splitSystem == null) {
                throw new IllegalStateException("Could not load splits block from input nexus file");
            }

            IdentifierList circularOrdering = splitSystem.isCircular() ?
                    splitSystem.getOrderedTaxa() :
                    null;

            if (circularOrdering == null) {
                throw new IllegalStateException("The split system loaded from the input nexus file is not circular");
            }

            log.info("Circular Ordering Loaded from file: " + this.options.getInputFile().getAbsolutePath());

            this.notifyUser("Processing data...");

            NetMEResult netMeResult = this.execute(distanceMatrix, circularOrdering);

            this.notifyUser("Saving results to: " + this.options.getOutputDir().getAbsolutePath());

            // Save result to disk
            netMeResult.save(
                    new File(this.options.getOutputDir(), this.options.getPrefix() + ".min-evo.nex"),
                    new File(this.options.getOutputDir(), this.options.getPrefix() + ".original-min-evo.nex"),
                    new File(this.options.getOutputDir(), this.options.getPrefix() + ".treelength"),
                    this.options.isOls()
            );

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
