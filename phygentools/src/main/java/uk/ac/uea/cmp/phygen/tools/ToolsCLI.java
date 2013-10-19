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
import org.apache.commons.lang3.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 27/04/13
 * Time: 15:38
 * To change this template use File | Settings | File Templates.
 */
public class ToolsCLI {

    private static CommandLine buildCmdLine(String[] args) throws ParseException {

        // Create the available options
        Options options = ToolsOptions.createOptions();

        // Parse the actual arguments
        CommandLineParser parser = new PosixParser();

        // parse the command line arguments
        CommandLine cmdLine = parser.parse(options, args, true);

        return cmdLine;
    }


    public static void main(String[] args) {

        try {
            // Process the command line
            CommandLine cmdLine = buildCmdLine(args);

            ToolsOptions toolsOptions = new ToolsOptions(cmdLine);

            // If help was requested output that and finish before starting Spring
            if (toolsOptions.doHelp()) {
                toolsOptions.printUsage();
            }
            // Otherwise run NetMake proper
            else {
                // Execute the requested simulation
                toolsOptions.getMode().execute(cmdLine.getArgs());
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(StringUtils.join(e.getStackTrace(), "\n"));
            System.exit(6);
        }
    }

}
