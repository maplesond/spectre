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
package uk.ac.uea.cmp.phybre.qtools.superq;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.tgac.metaopt.Objective;
import uk.ac.tgac.metaopt.OptimiserException;
import uk.ac.tgac.metaopt.OptimiserFactory;
import uk.ac.uea.cmp.phybre.core.ui.cli.CommandLineHelper;
import uk.ac.uea.cmp.phybre.qtools.superq.problems.SecondaryProblemFactory;

import java.io.File;

public class SuperQCLI {

    private static Logger log = LoggerFactory.getLogger(SuperQCLI.class);

    private static String BIN_NAME = "superq";

    private static String OPT_OUTPUT = "output";
    private static String OPT_SCALING_SOLVER = "scaling_solver";
    private static String OPT_PRIMARY_SOLVER = "primary_solver";
    private static String OPT_SECONDARY_SOLVER = "secondary_solver";
    private static String OPT_SECONDARY_OBJECTIVE = "secondary_objective";
    private static String OPT_FILTER = "filter";
    private static String OPT_HELP = "help";
    private static String OPT_VERBOSE = "verbose";


    public static void main(String args[]) {

        // Parse command line args
        CommandLine commandLine = CommandLineHelper.startApp(createOptions(), BIN_NAME,
                "Creates a Circular Split Network from a set of trees", args);

        // If we didn't return a command line object then just return.  Probably the user requested help or
        // input invalid args
        if (commandLine == null) {
            return;
        }

        try {

            SuperQ.configureLogging();
            SuperQOptions sqOpts = processArgs(commandLine);
            SuperQ superQ = new SuperQ(sqOpts);
            superQ.run();
            if (superQ.failed()) {
                log.error(superQ.getErrorMessage());
            }
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            System.exit(3);
        }

        log.info("Completed successfully");
    }

    private static Options createOptions() {

        Options options = new Options();
        options.addOption(OptionBuilder
                .withDescription(SuperQOptions.DESC_OUTPUT)
                .hasArg().isRequired().withLongOpt(OPT_OUTPUT).create("o"));
        options.addOption("x", OPT_PRIMARY_SOLVER, true, SuperQOptions.DESC_PRIMARY_SOLVER);
        options.addOption("y", OPT_SECONDARY_SOLVER, true, SuperQOptions.DESC_SECONDARY_SOLVER);
        options.addOption("b", OPT_SECONDARY_OBJECTIVE, true, SuperQOptions.DESC_SECONDARY_OBJECTIVE);
        options.addOption("s", OPT_SCALING_SOLVER, true, SuperQOptions.DESC_SCALING_SOLVER);
        options.addOption("f", OPT_FILTER, true, SuperQOptions.DESC_FILTER);
        options.addOption("h", OPT_HELP, false, "Shows this help.");
        options.addOption("v", OPT_VERBOSE, false, "Whether to output extra information");
        return options;
    }

    private static SuperQOptions processArgs(CommandLine commandLine) throws ParseException {

        SuperQOptions sqOpts = null;

        try {
            sqOpts = new SuperQOptions();
        } catch (OptimiserException oe) {
            throw new ParseException("Error occurred configuring optimiser.   Check you have selected an operational " +
                    "optimiser and set an appropriate objective.");
        }

        if (commandLine.hasOption(OPT_OUTPUT)) {
            sqOpts.setOutputFile(new File(commandLine.getOptionValue(OPT_OUTPUT)));
        } else {
            throw new ParseException("You must specify an output file");
        }

        if (commandLine.hasOption(OPT_SECONDARY_OBJECTIVE)) {

            sqOpts.setSecondaryProblem(
                    SecondaryProblemFactory.getInstance().createSecondaryObjective(
                            commandLine.getOptionValue(OPT_SECONDARY_OBJECTIVE).toUpperCase()));
        }

        try {

            if (commandLine.hasOption(OPT_SCALING_SOLVER)) {
                sqOpts.setScalingSolver(
                        OptimiserFactory.getInstance().createOptimiserInstance(
                                commandLine.getOptionValue(OPT_SCALING_SOLVER), Objective.ObjectiveType.QUADRATIC)
                );
            }

            if (commandLine.hasOption(OPT_PRIMARY_SOLVER)) {
                sqOpts.setPrimarySolver(
                        OptimiserFactory.getInstance().createOptimiserInstance(
                                commandLine.getOptionValue(OPT_PRIMARY_SOLVER), Objective.ObjectiveType.QUADRATIC));
            }

            if (commandLine.hasOption(OPT_SECONDARY_SOLVER)) {
                sqOpts.setSecondarySolver(
                        OptimiserFactory.getInstance().createOptimiserInstance(
                                commandLine.getOptionValue(OPT_SECONDARY_SOLVER), sqOpts.getSecondaryProblem().getObjectiveType()));
            }
        } catch (OptimiserException oe) {
            throw new ParseException(oe.getMessage());
        }


        if (commandLine.hasOption(OPT_FILTER)) {
            sqOpts.setFilter(Double.parseDouble(commandLine.getOptionValue(OPT_FILTER)));
        }

        if (commandLine.hasOption(OPT_VERBOSE)) {
            sqOpts.setVerbose(true);
        }

        String[] args = commandLine.getArgs();

        if (args == null || args.length == 0) {
            throw new ParseException("You must specify at least one input file");
        }

        File[] inputFiles = new File[args.length];
        for(int i = 0; i < args.length; i++) {
            inputFiles[i] = new File(args[i].trim());
        }
        sqOpts.setInputFiles(inputFiles);

        return sqOpts;
    }

}
