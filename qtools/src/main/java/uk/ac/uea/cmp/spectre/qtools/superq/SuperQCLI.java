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

package uk.ac.uea.cmp.spectre.qtools.superq;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.earlham.metaopt.Objective;
import uk.ac.earlham.metaopt.OptimiserException;
import uk.ac.earlham.metaopt.OptimiserFactory;
import uk.ac.uea.cmp.spectre.core.ui.cli.CommandLineHelper;
import uk.ac.uea.cmp.spectre.core.util.LogConfig;
import uk.ac.uea.cmp.spectre.qtools.superq.problems.SecondaryProblemFactory;

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
        CommandLine commandLine = new CommandLineHelper().startApp(createOptions(), BIN_NAME + " [options] (<input>)+",
                "Generates a circular split network from a set of trees.\n" +
                        "Input can take the form of one or more, nexus, phylip and newick tree files.\n" +
                        "Also superQ can automatically generate trees from distance matrices.", args);

        // If we didn't return a command line object then just return.  Probably the user requested help or
        // input invalid args
        if (commandLine == null) {
            return;
        }

        try {

            SuperQOptions sqOpts = processArgs(commandLine);
            LogConfig.defaultConfig(sqOpts.isVerbose());
            SuperQ superQ = new SuperQ(sqOpts);
            superQ.run();
            if (superQ.failed()) {
                log.error(superQ.getErrorMessage());
            }
            else {
                log.info("Completed successfully");
            }
        } catch (Exception e) {
            System.err.println("\nException: " + e.toString());
            System.err.println("\nStack trace:");
            System.err.println(StringUtils.join(e.getStackTrace(), "\n"));
            System.exit(3);
        }
    }

    private static Options createOptions() {

        Options options = new Options();
        options.addOption(CommandLineHelper.HELP_OPTION);
        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_OUTPUT).isRequired(true).hasArg(true)
                .withDescription(SuperQOptions.DESC_OUTPUT).create("o"));
        options.addOption(OptionBuilder.withArgName("solver").withLongOpt(OPT_PRIMARY_SOLVER).isRequired(false).hasArg(true)
                .withDescription(SuperQOptions.DESC_PRIMARY_SOLVER).create("x"));
        options.addOption(OptionBuilder.withArgName("solver").withLongOpt(OPT_SECONDARY_SOLVER).isRequired(false).hasArg(true)
                .withDescription(SuperQOptions.DESC_SECONDARY_SOLVER).create("y"));
        options.addOption(OptionBuilder.withArgName("objective").withLongOpt(OPT_SECONDARY_OBJECTIVE).isRequired(false).hasArg(true)
                .withDescription(SuperQOptions.DESC_SECONDARY_OBJECTIVE).create("b"));
        options.addOption(OptionBuilder.withArgName("solver").withLongOpt(OPT_SCALING_SOLVER).isRequired(false).hasArg(true)
                .withDescription(SuperQOptions.DESC_SCALING_SOLVER).create("s"));
        options.addOption(OptionBuilder.withArgName("double").withLongOpt(OPT_FILTER).isRequired(false).hasArg(true)
                .withDescription(SuperQOptions.DESC_FILTER).create("f"));
        options.addOption(OptionBuilder.withLongOpt(OPT_VERBOSE).isRequired(false).hasArg(false)
                .withDescription("Whether to output extra information").create("v"));
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
            throw new ParseException("You must specify an output file.");
        }

        if (commandLine.getArgs().length >= 1) {
            File[] inputFiles = new File[commandLine.getArgs().length];
            for (int i = 0; i < commandLine.getArgs().length; i++) {
                inputFiles[i] = new File(commandLine.getArgs()[i]);
            }
            sqOpts.setInputFiles(inputFiles);
        } else {
            throw new ParseException("You must specify at least one file.");
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

            if (commandLine.hasOption(OPT_SECONDARY_SOLVER) && !commandLine.hasOption(OPT_SECONDARY_OBJECTIVE)) {
                throw new ParseException("If you request a secondary solver, then you must also specify a secondary objective");
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

        return sqOpts;
    }

}
