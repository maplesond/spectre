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

package uk.ac.uea.cmp.phygen.qnet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.math.optimise.Objective;
import uk.ac.uea.cmp.phygen.core.math.optimise.Optimiser;
import uk.ac.uea.cmp.phygen.core.math.optimise.OptimiserFactory;
import uk.ac.uea.cmp.phygen.core.ui.cli.CommandLineHelper;

import java.io.File;


public class QNetCLI {

    private static Logger logger = LoggerFactory.getLogger(QNetCLI.class);

    private static final String OPT_INPUT = "input";
    private static final String OPT_LOG = "log";
    private static final String OPT_TOLERANCE = "tolerance";
    private static final String OPT_OPTIMISER = "optimiser";

    private static Options createOptions() {

        Option optLog = new Option("l", OPT_LOG, false, "Linear if false, Log if true");

        Option optInput = OptionBuilder.withArgName("file").withLongOpt(OPT_INPUT).isRequired().hasArg()
                .withDescription("The file containing the distance data to input.").create("i");

        Option optTolerance = OptionBuilder.withArgName("double").withLongOpt(OPT_TOLERANCE).hasArg()
                .withDescription("The tolerance").create("t");

        Option optOptimiser = OptionBuilder.withArgName("string").withLongOpt(OPT_OPTIMISER).hasArg()
                .withDescription("If specified, uses optimisation: " + OptimiserFactory.getInstance().listOperationalOptimisers()).create("p");

        // create Options object
        Options options = new Options();
        options.addOption(CommandLineHelper.HELP_OPTION);
        options.addOption(optInput);
        options.addOption(optLog);
        options.addOption(optTolerance);
        options.addOption(optOptimiser);

        return options;
    }


    public static void main(String[] args) {

        // Parse command line args
        CommandLine commandLine = CommandLineHelper.startApp(createOptions(), "qnet-<version>", "Q-NET",
                "Stephan's Circular Ordering Generator", args);

        // If we didn't return a command line object then just return.  Probably the user requested help or
        // input invalid args
        if (commandLine == null) {
            return;
        }

        try {
            // Configure logging
            BasicConfigurator.configure();

            // Required arguments
            File input = new File(commandLine.getOptionValue(OPT_INPUT));
            boolean log = commandLine.hasOption(OPT_LOG);
            double tolerance = commandLine.hasOption(OPT_TOLERANCE) ? Double.parseDouble(commandLine.getOptionValue(OPT_TOLERANCE)) : -1.0;
            Optimiser optimiser = commandLine.hasOption(OPT_OPTIMISER) ?
                    OptimiserFactory.getInstance().createOptimiserInstance(commandLine.getOptionValue(OPT_OPTIMISER), Objective.NNLS) :
                    null;

            // Run QNet
            new QNet().execute(input, log, tolerance, optimiser);

            // Now what??? Presumably there is some output created?!

        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            System.exit(1);
        }
    }

}