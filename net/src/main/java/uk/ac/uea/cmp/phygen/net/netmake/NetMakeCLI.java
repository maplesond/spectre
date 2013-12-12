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

package uk.ac.uea.cmp.phygen.net.netmake;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.phygen.core.io.PhygenDataType;
import uk.ac.uea.cmp.phygen.core.io.PhygenReader;
import uk.ac.uea.cmp.phygen.core.io.PhygenReaderFactory;
import uk.ac.uea.cmp.phygen.core.ui.cli.CommandLineHelper;
import uk.ac.uea.cmp.phygen.net.netmake.weighting.Weighting;
import uk.ac.uea.cmp.phygen.net.netmake.weighting.Weightings;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author Sarah Bastkowski
 *         See S. Bastkowski, 2010:
 *         <I>Algorithmen zum Finden von Bäumen in Neighbor Net Netzwerken</I>
 */
public class NetMakeCLI {

    private static Logger log = LoggerFactory.getLogger(NetMakeCLI.class);

    public static final String OPT_INPUT = "input";
    public static final String OPT_INPUT_TYPE = "input_file_type";
    public static final String OPT_OUTPUT_DIR = "output";
    public static final String OPT_OUTPUT_PREFIX = "prefix";
    public static final String OPT_TREE_PARAM = "tree_param";
    public static final String OPT_WEIGHTINGS_1 = "weightings_1";
    public static final String OPT_WEIGHTINGS_2 = "weightings_2";
    public static final String OPT_HELP = "help";


    public static Options createOptions() {

        // Options with arguments
        Option optInput = OptionBuilder.withArgName("file").withLongOpt(OPT_INPUT).isRequired().hasArg()
                .withDescription("The file containing the distance matrix to input.").create("i");

        Option optInputType = OptionBuilder.withArgName("string").withLongOpt(OPT_INPUT_TYPE).hasArg()
                .withDescription("The file type of the input file: " +
                        PhygenReaderFactory.getInstance().getPhygenReadersAsString(PhygenDataType.DISTANCE_MATRIX) + ".")
                .create("t");

        Option optOutputDir = OptionBuilder.withArgName("file").withLongOpt(OPT_OUTPUT_DIR).hasArg()
                .withDescription("The directory to put output from this job.").create("o");

        Option optOutputPrefix = OptionBuilder.withArgName("string").withLongOpt(OPT_OUTPUT_PREFIX).hasArg()
                .withDescription("The prefix to apply to all files produced by this NetMake run.  Default: netmake-<timestamp>.").create("p");

        Option optTreeParam = OptionBuilder.withArgName("double").withLongOpt(OPT_TREE_PARAM).hasArg()
                .withDescription("The weighting parameter passed to the chosen weighting algorithm. " +
                        " Value must be between 0.0 and 1.0.  Default: 0.5.").create("z");

        Option optWeighting1 = OptionBuilder.withArgName("string").withLongOpt(OPT_WEIGHTINGS_1).isRequired().hasArg()
                .withDescription("Select Weighting type: " + Weightings.toListString() + ".  Default: ").create("w");

        Option optWeighting2 = OptionBuilder.withArgName("string").withLongOpt(OPT_WEIGHTINGS_2).hasArg()
                .withDescription("Select 2nd Weighting type: " + Weightings.toListString() + ". Default: ").create("x");


        // create Options object
        Options options = new Options();
        options.addOption(optInput);
        options.addOption(optInputType);
        options.addOption(optOutputDir);
        options.addOption(optOutputPrefix);
        options.addOption(optTreeParam);
        options.addOption(optWeighting1);
        options.addOption(optWeighting2);
        options.addOption(CommandLineHelper.HELP_OPTION);

        return options;
    }


    public static void main(String[] args) {

        CommandLine commandLine = CommandLineHelper.startApp(createOptions(), "netmake-<version>", "Network Maker",
                "Creates networks from distance matricies", args);

        // If we didn't return a command line object then just return.  Probably the user requested help or
        // input invalid args
        if (commandLine == null) {
            return;
        }

        try {

            // Configure logging
            if (new File("log4j.properties").exists()) {
                PropertyConfigurator.configure("log4j.properties");
            } else {
                BasicConfigurator.configure();
            }

            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
            Date date = new Date();
            String timeStamp = dateFormat.format(date);

            log.info("NetMake: Parsing arguments");

            File input = new File(commandLine.getOptionValue(OPT_INPUT));
            String inputType = commandLine.hasOption(OPT_INPUT_TYPE) ? commandLine.getOptionValue(OPT_INPUT_TYPE) : null;
            File outputDir = commandLine.hasOption(OPT_OUTPUT_DIR) ? new File(commandLine.getOptionValue(OPT_OUTPUT_DIR)) : new File(".");
            String prefix = commandLine.hasOption(OPT_OUTPUT_PREFIX) ? commandLine.getOptionValue(OPT_OUTPUT_PREFIX) : "netmake-" + timeStamp;
            double treeParam = commandLine.hasOption(OPT_TREE_PARAM) ? Double.parseDouble(commandLine.getOptionValue(OPT_TREE_PARAM)) : 0.5;
            String weightings1 = commandLine.getOptionValue(OPT_WEIGHTINGS_1);
            String weightings2 = commandLine.hasOption(OPT_WEIGHTINGS_2) ? commandLine.getOptionValue(OPT_WEIGHTINGS_2) : null;


            log.info("NetMake: Loading distance matrix from: " + input.getAbsolutePath());

            // Load distanceMatrix from input file based on file type
            // Get a handle on the phygen factory
            PhygenReaderFactory factory = PhygenReaderFactory.getInstance();

            // Setup appropriate reader to input file based on file type
            PhygenReader phygenReader = factory.create(inputType != null ?
                    inputType :
                    FilenameUtils.getExtension(input.getName()));

            DistanceMatrix distanceMatrix = phygenReader.readDistanceMatrix(input);

            log.info("NetMake: Loaded distance matrix.  Found " + distanceMatrix.size() + " taxa.");

            // Create weighting objects
            Weighting weighting1 = Weightings.createWeighting(weightings1, distanceMatrix, treeParam, true);
            Weighting weighting2 = weightings2 != null ?
                    Weightings.createWeighting(weightings2, distanceMatrix, treeParam, false) :
                    null;

            log.info("NetMake: Weightings configured.");
            log.info("          - Weighting 1: " + weighting1.toString());
            log.info("          - Weighting 2: " + (weighting2 == null ? "null" : weighting2.toString()));

            // Create the configured NetMake object to process
            NetMake netMake = new NetMake(
                    distanceMatrix,
                    weighting1,
                    weighting2);

            log.info("NetMake: System configured.  Netmake runmode: " + netMake.getRunMode());

            log.info("NetMake: Processing Started");

            // Run NetMake
            NetMakeResult netMakeResult = netMake.process();

            log.info("NetMake: Processing Finished");

            // Save results.
            netMakeResult.save(outputDir, prefix);

            log.info("NetMake: Saved results to disk");

        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(StringUtils.join(e.getStackTrace(), "\n"));
            System.exit(1);
        }
    }

}