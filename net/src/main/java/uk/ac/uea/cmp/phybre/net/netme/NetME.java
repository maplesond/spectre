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

package uk.ac.uea.cmp.phybre.net.netme;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phybre.core.ds.IdentifierList;
import uk.ac.uea.cmp.phybre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.phybre.core.io.PhygenReader;
import uk.ac.uea.cmp.phybre.core.io.PhygenReaderFactory;
import uk.ac.uea.cmp.phybre.core.io.nexus.NexusReader;
import uk.ac.uea.cmp.phybre.core.ui.gui.RunnableTool;
import uk.ac.uea.cmp.phybre.core.ui.gui.StatusTracker;

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

    public static void configureLogging() {
        // Configure logging
        if (new File("log4j.properties").exists()) {
            PropertyConfigurator.configure("log4j.properties");
        } else {
            BasicConfigurator.configure();
        }
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

            this.notifyUser("Loading distance matrix from: " + this.options.getDistancesFile().getAbsolutePath());

            // Get a handle on the phygen factory
            PhygenReaderFactory factory = PhygenReaderFactory.getInstance();

            // Setup appropriate reader to input file based on file type
            PhygenReader phygenReader = factory.create(FilenameUtils.getExtension(this.options.getDistancesFile().getName()));

            DistanceMatrix distanceMatrix = phygenReader.readDistanceMatrix(this.options.getDistancesFile());

            log.info("Distance Matrix Loaded from file: " + this.options.getDistancesFile().getAbsolutePath());

            // Load circular ordering from the provided nexus file
            IdentifierList circularOrdering = new NexusReader().extractCircOrdering(this.options.getCircularOrderingFile());

            String circularOrderingMessage = "loaded from file " + this.options.getCircularOrderingFile().getAbsolutePath();

            log.info("Circular ordering " + circularOrderingMessage);

            this.notifyUser("Processing data");

            NetMEResult netMeResult = this.execute(distanceMatrix, circularOrdering) ;

            this.notifyUser("Saving results to: " + this.options.getOutputDir().getAbsolutePath());

            // Save result to disk
            netMeResult.save(
                    new File(this.options.getOutputDir(), this.options.getPrefix() + ".min-evo.nex"),
                    new File(this.options.getOutputDir(), this.options.getPrefix() + ".original-min-evo.nex"),
                    new File(this.options.getOutputDir(), this.options.getPrefix() + ".stats")
            );

            this.trackerFinished(true);

            // Print run time on screen
            stopWatch.stop();
            log.info("Completed Successfully - Total run time: " + stopWatch.toString());
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            this.setError(e);
            this.trackerFinished(false);
        }
        finally {
            this.notifyListener();
        }
    }

}
