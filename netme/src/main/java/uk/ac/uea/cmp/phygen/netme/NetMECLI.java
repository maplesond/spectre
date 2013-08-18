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

package uk.ac.uea.cmp.phygen.netme;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.phygen.core.ds.split.CircularOrdering;
import uk.ac.uea.cmp.phygen.core.io.PhygenReader;
import uk.ac.uea.cmp.phygen.core.io.PhygenReaderFactory;
import uk.ac.uea.cmp.phygen.core.io.nexus.NexusReader;
import uk.ac.uea.cmp.phygen.core.ui.cli.CommandLineHelper;
import uk.ac.uea.cmp.phygen.core.util.Time;
import uk.ac.uea.cmp.phygen.netmake.nnet.NeighbourNet;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 27/04/13
 * Time: 15:38
 * To change this template use File | Settings | File Templates.
 */
public class NetMECLI {

    private static Logger log = LoggerFactory.getLogger(NetMECLI.class);

    public static final String OPT_DISTANCES_FILE = "distances_file";
    public static final String OPT_DISTANCES_FILE_TYPE = "distances_file_type";
    public static final String OPT_CIRCULAR_ORDERING_FILE = "circular_ordering_file";
    public static final String OPT_OUTPUT_DIR = "output";
    public static final String OPT_OUTPUT_PREFIX = "prefix";

    public static Options createOptions() {

        // Options with arguments
        Option optDistancesFile = OptionBuilder.withArgName("file").withLongOpt(OPT_DISTANCES_FILE).isRequired().hasArg()
                .withDescription("The file containing the distance data.").create("i");

        Option optDistancesFileType = OptionBuilder.withArgName("string").withLongOpt(OPT_DISTANCES_FILE_TYPE).hasArg()
                .withDescription("The file type of the distance data file: [NEXUS, PHYLIP].").create("t");

        Option optCircularOrderingFile = OptionBuilder.withArgName("file").withLongOpt(OPT_CIRCULAR_ORDERING_FILE).hasArg()
                .withDescription("The nexus file containing the circular ordering.").create("j");

        Option optOutputDir = OptionBuilder.withArgName("file").withLongOpt(OPT_OUTPUT_DIR).hasArg()
                .withDescription("The directory to put output from this job.").create("o");

        Option optOutputPrefix = OptionBuilder.withArgName("string").withLongOpt(OPT_OUTPUT_PREFIX).hasArg()
                .withDescription("The prefix to apply to all files produced by this NetME run.  Default: netme-<timestamp>.").create("p");

        // create Options object
        Options options = new Options();
        options.addOption(optDistancesFile);
        options.addOption(optDistancesFileType);
        options.addOption(optCircularOrderingFile);
        options.addOption(optOutputDir);
        options.addOption(optOutputPrefix);
        options.addOption(CommandLineHelper.HELP_OPTION);

        return options;
    }


    public static void main(String[] args) {

        CommandLine commandLine = CommandLineHelper.startApp(createOptions(), "netme-<version>", "Net-ME", args);

        // If we didn't return a command line object then just return.  Probably the user requested help or
        // input invalid args
        if (commandLine == null) {
            return;
        }

        try {
            // Configure logging
            BasicConfigurator.configure();

            log.info("NetME: Parsing arguments");

            File distancesFile = new File(commandLine.getOptionValue(OPT_DISTANCES_FILE));
            String distancesFileType = commandLine.hasOption(OPT_DISTANCES_FILE_TYPE) ? commandLine.getOptionValue(OPT_DISTANCES_FILE_TYPE) : null;
            File circularOrderingFile = commandLine.hasOption(OPT_CIRCULAR_ORDERING_FILE) ? new File(commandLine.getOptionValue(OPT_CIRCULAR_ORDERING_FILE)) : null;
            File outputDir = commandLine.hasOption(OPT_OUTPUT_DIR) ? new File(commandLine.getOptionValue(OPT_OUTPUT_DIR)) : new File(".");
            String prefix = commandLine.hasOption(OPT_OUTPUT_PREFIX) ? commandLine.getOptionValue(OPT_OUTPUT_PREFIX) : "netme-" + Time.createTimestamp();

            log.info("NetME: Loading distance matrix from: " + distancesFile.getAbsolutePath());

            // Load distanceMatrix from input file based on file type
            PhygenReader phygenReader = distancesFileType != null ?
                    PhygenReaderFactory.valueOf(distancesFileType).create() :
                    PhygenReaderFactory.create(FilenameUtils.getExtension(distancesFile.getName()));

            DistanceMatrix distanceMatrix = phygenReader.read(distancesFile);

            log.info("NetME: Distance Matrix Loaded from file: " + distancesFile.getAbsolutePath());

            // Load circular ordering from nexus file if provided, otherwise run neighbour net on distance matrix
            CircularOrdering circularOrdering = circularOrderingFile != null ?
                    new NexusReader().extractCircOrdering(circularOrderingFile) :
                    NeighbourNet.computeNeighborNetOrdering(distanceMatrix);

            String circularOrderingMessage = circularOrderingFile != null ?
                    "loaded from file " + circularOrderingFile.getAbsolutePath() :
                    "calculated with neighbor net from distance matrix";

            log.info("NetME: Circular ordering " + circularOrderingMessage);

            log.info("NetME: Started");

            NetMEResult netMeResult = new NetME().calcMinEvoTree(distanceMatrix, circularOrdering);

            log.info("NetME: Finished");

            // Save result to disk
            netMeResult.save(
                    new File(outputDir, prefix + ".min-evo.nex"),
                    new File(outputDir, prefix + ".original-min-evo.nex"),
                    new File(outputDir, prefix + ".stats")
            );

            log.info("NetME: Results saved");

        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(StringUtils.join(e.getStackTrace(), "\n"));
            System.exit(1);
        }
    }

}
