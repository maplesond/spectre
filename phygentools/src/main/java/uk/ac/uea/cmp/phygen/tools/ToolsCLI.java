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
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import uk.ac.uea.cmp.phygen.core.ui.cli.CommandLineHelper;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 27/04/13
 * Time: 15:38
 * To change this template use File | Settings | File Templates.
 */
public class ToolsCLI {

    public static final String OPT_HELP = "help";

    @SuppressWarnings("static-access")
    private static Options createOptions() {

        // add t option
        return new Options().addOption(new Option("?", OPT_HELP, false, "Print this message."));
    }

    private static void printHelp() {
        CommandLineHelper.printHelp(
                createOptions(),
                "phygentools.jar",
                "Miscellaneous Phylogenetic Tools",
                "A collection of miscellaneous tools: " + ToolsFactory.getInstance().listToolsDescriptions());
    }


    public static void main(String[] args) {

        try {
            // Process the command line
            CommandLine cmdLine = new PosixParser().parse(createOptions(), args, true);

            if (cmdLine.getArgList().isEmpty() || cmdLine.getArgs()[0].equalsIgnoreCase(OPT_HELP)) {
                printHelp();
            }
            else {
                // Required arguments
                PhygenTool phygenTool = ToolsFactory.getInstance().createOptimiserInstance(cmdLine.getArgs()[0]);

                if (phygenTool == null) {
                    throw new ParseException("Requested tool not found: " + cmdLine.getOptionValue(cmdLine.getArgs()[0]));
                }
                else {
                    phygenTool.execute(ArrayUtils.subarray(cmdLine.getArgs(), 1, cmdLine.getArgs().length));
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(StringUtils.join(e.getStackTrace(), "\n"));
            System.exit(6);
        }
    }

}
