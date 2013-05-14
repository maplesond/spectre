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

package uk.ac.uea.cmp.phygen.tools;

import org.apache.commons.cli.*;

/**
 * Created with IntelliJ IDEA. User: Dan Date: 12/05/13 Time: 19:11 To change this template use File | Settings | File
 * Templates.
 */
public class ToolsOptions {

    public static final String OPT_MODE = "mode";
    public static final String OPT_HELP = "help";

    private ToolsMode mode;
    private boolean help = false;


    /**
     * Process command line args.  Checks for help first, then clean, then args for normal operation.
     *
     * @param cmdLine The command line used to execute the program
     * @throws org.apache.commons.cli.ParseException
     *
     */
    public ToolsOptions(CommandLine cmdLine) throws ParseException {

        help = cmdLine.hasOption(OPT_HELP) || cmdLine.getArgList().size() == 0 && cmdLine.getOptions().length == 0;

        if (!help) {

            // Required arguments
            mode = ToolsMode.parseName(cmdLine.getOptionValue(OPT_MODE));

            if (mode == null)
                throw new ParseException("Requested mode not found: " + cmdLine.getOptionValue(OPT_MODE));
        }
    }

    public ToolsMode getMode() {
        return mode;
    }

    public boolean doHelp() {
        return help;
    }

    public void printUsage() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(this.getClass().getName(), createOptions());
    }

    @SuppressWarnings("static-access")
    public static Options createOptions() {

        // Boolean options
        Option optHelp = new Option("?", OPT_HELP, false, "Print this message.");

        // Options with arguments
        Option optMode = OptionBuilder.withArgName("string").withLongOpt(OPT_MODE).hasArg()
                .withDescription("The mode to run: " + ToolsMode.listTools()).create("m");

        // create Options object
        Options options = new Options();

        // add t option
        options.addOption(optHelp);
        options.addOption(optMode);

        return options;
    }

}
