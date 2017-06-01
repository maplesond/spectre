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

package uk.ac.uea.cmp.spectre.flatnj;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.earlham.metaopt.Objective;
import uk.ac.earlham.metaopt.Optimiser;
import uk.ac.earlham.metaopt.OptimiserException;
import uk.ac.earlham.metaopt.OptimiserFactory;
import uk.ac.earlham.metaopt.external.JOptimizer;
import uk.ac.uea.cmp.spectre.core.ui.cli.CommandLineHelper;
import uk.ac.uea.cmp.spectre.core.util.LogConfig;

import java.io.File;

public class FlatNJCLI {

    private static Logger log = LoggerFactory.getLogger(FlatNJCLI.class);

    private static String BIN_NAME = "flatnj";

    private static final String OPT_OUTPUT = "output";
    private static final String OPT_NEXUS_BLOCK = "nexus_block";
    private static final String OPT_THRESHOLD = "threshold";
    private static final String OPT_OPTIMISER = "optimiser";
    private static final String OPT_SAVE_STAGES = "save_stages";
    private static final String OPT_VERBOSE = "verbose";




    /**
     * Main method
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Setup the command line options
        CommandLine commandLine = new CommandLineHelper().startApp(createOptions(), "flatnj [options] <input>",
                "An implementation of the Flat Net Joining (FlatNJ) algorithm to produce planar networks where labels can appear inside the network.\n" +
                        "Required input is quartet-like (4-split) data, although flatnj can automatically generate this from multiple sequence alignments, location data (X,Y coordinates) or other compatible split networks.\n" +
                        "Input can either be nexus format containing: taxa, character, data, distances, locations, splits or quadruples blocks, or a fasta-style format file containing the multiple sequence alignments.",
                args);

        // If we didn't return a command line object then just return.  Probably the user requested help or
        // input invalid args
        if (commandLine == null) {
            return;
        }

        try {
            // Configure logger
            LogConfig.defaultConfig(commandLine.hasOption(OPT_VERBOSE));

            // Parsing the command line.
            log.info("Running Flat Neighbor Joining (FlatNJ) Algorithm");
            log.debug("Parsing command line options");

            FlatNJOptions opts = processArgs(commandLine);

            FlatNJ flatNJ = new FlatNJ(opts);
            flatNJ.run();

            if (flatNJ.failed()) {
                log.error(flatNJ.getErrorMessage());
            }
            else {
                log.info("Completed successfully");
            }

        } catch (Exception e) {
            System.err.println("\nException: " + e.toString());
            System.err.println("\nStack trace:");
            System.err.println(StringUtils.join(e.getStackTrace(), "\n"));
            System.exit(1);
        }
    }


    protected static Options createOptions() {

        // create Options object
        Options options = new Options();

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_OUTPUT).hasArg()
                .withDescription(FlatNJOptions.DESC_OUTPUT).create("o"));

        options.addOption(OptionBuilder.withArgName("string").withLongOpt(OPT_NEXUS_BLOCK).hasArg()
                .withDescription(FlatNJOptions.DESC_BLOCK).create("n"));

        options.addOption(OptionBuilder.withArgName("double").withLongOpt(OPT_THRESHOLD).hasArg()
                .withDescription(FlatNJOptions.DESC_THRESHOLD).create("t"));

        options.addOption(OptionBuilder.withArgName("string").withLongOpt(OPT_OPTIMISER).hasArg()
                .withDescription(FlatNJOptions.DESC_OPTIMISER).create("p"));

        options.addOption(OptionBuilder.withLongOpt(OPT_SAVE_STAGES)
                .withDescription(FlatNJOptions.DESC_STAGES).create("a"));

        options.addOption(OptionBuilder.withLongOpt(OPT_VERBOSE)
                .withDescription(FlatNJOptions.DESC_VERBOSE).create("v"));

        options.addOption(CommandLineHelper.HELP_OPTION);

        return options;
    }

    private static FlatNJOptions processArgs(CommandLine commandLine) throws ParseException {

        if (commandLine.getArgs().length == 0) {
            throw new ParseException("No input file specified.");
        }
        else if (commandLine.getArgs().length > 1) {
            throw new ParseException("Only expected a single input file.");
        }

        FlatNJOptions opts = new FlatNJOptions();

        // Required
        opts.setInFile(new File(commandLine.getArgs()[0]));

        try {
            String optstring = commandLine.getOptionValue(OPT_OPTIMISER);
            Optimiser optimiser = commandLine.hasOption(OPT_OPTIMISER) ?
                    OptimiserFactory.getInstance().createOptimiserInstance(optstring, Objective.ObjectiveType.QUADRATIC) :
                    new JOptimizer();
            if (optimiser == null) {
                throw new RuntimeException("Failed to initialise optimiser: " + optstring);
            }

            log.debug("Initialised optimiser: " + optstring);
            opts.setOptimiser(optimiser);
        }
        catch (OptimiserException e) {
            throw new RuntimeException("Failed to initialise optimiser", e);
        }

        // Optional
        opts.setOutputFile(commandLine.hasOption(OPT_OUTPUT) ? new File(commandLine.getOptionValue(OPT_OUTPUT)) : new File(FlatNJOptions.DEFAULT_OUTPUT));
        opts.setBlock(commandLine.hasOption(OPT_NEXUS_BLOCK) ? commandLine.getOptionValue(OPT_NEXUS_BLOCK) : null);
        opts.setThreshold(commandLine.hasOption(OPT_THRESHOLD) ? Double.parseDouble(commandLine.getOptionValue(OPT_THRESHOLD)) : FlatNJOptions.DEFAULT_THRESHOLD);
        opts.setSaveStages(commandLine.hasOption(OPT_SAVE_STAGES));

        return opts;
    }


}
