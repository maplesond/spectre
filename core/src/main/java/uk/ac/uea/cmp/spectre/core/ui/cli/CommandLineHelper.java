/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2015  UEA School of Computing Sciences
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

package uk.ac.uea.cmp.spectre.core.ui.cli;

import org.apache.commons.cli.*;

import java.io.PrintWriter;

/**
 * Created with IntelliJ IDEA. User: Dan Date: 14/05/13 Time: 22:36 To change this template use File | Settings | File
 * Templates.
 */
public class CommandLineHelper {

    public static final int DEFAULT_WIDTH = 100;
    public static final String DEFAULT_FOOTER = "Created in collaboration by the University of East Anglia (UEA), The " +
            "Genome Analysis Centre (TGAC) and the University of Greifswald, as part of the " +
            "Suite of PhylogEnetiCs Tools for Reticulate Evolution (SPECTRE)";

    public static final String OPT_HELP = "help";
    public static final Option HELP_OPTION = new Option("?", OPT_HELP, false, "Print this message.");


    public static void printHelp(Options options, String exeName, String description) {
        new HelpFormatter().printHelp(
                CommandLineHelper.DEFAULT_WIDTH,
                exeName,
                description,
                options,
                CommandLineHelper.DEFAULT_FOOTER,
                true);

    }

    public static void printUsage(Options options, String exeName) {
        new HelpFormatter().printUsage(
                new PrintWriter(System.err),
                HelpFormatter.DEFAULT_WIDTH,
                exeName,
                options);
    }

    public static Options createHelpOptions() {

        Options options = new Options();
        options.addOption(HELP_OPTION);
        return options;
    }

    public static CommandLine startApp(Options options, String exeName, String description, String[] args) {

        try {
            // Test for help first
            CommandLine helpCl = new PosixParser().parse(createHelpOptions(), args, true);

            if (helpCl.hasOption(OPT_HELP) || helpCl.getArgList().isEmpty()) {
                CommandLineHelper.printHelp(options, exeName, description);
                return null;
            }

            // Parse the full options, catch any errors
            return new PosixParser().parse(options, args);
        } catch (ParseException p) {
            System.err.println(p.getMessage());
            CommandLineHelper.printUsage(options, exeName);
            return null;
        }
    }

}
