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

package uk.ac.uea.cmp.spectre.core.ui.cli;

import org.apache.commons.cli.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA. User: Dan Date: 14/05/13 Time: 22:36 To change this template use File | Settings | File
 * Templates.
 */
public class CommandLineHelper {

    public static final int DEFAULT_WIDTH = 100;
    public static final String DEFAULT_FOOTER = "Created in collaboration by the University of East Anglia (UEA) and the " +
            "Earlham Institute (EI), as part of the " +
            "Suite of PhylogEnetiCs Tools for Reticulate Evolution (SPECTRE)";

    public static final String OPT_HELP = "help";
    public static final Option HELP_OPTION = new Option("?", OPT_HELP, false, "Print this message.");

    public static final String OPT_VERSION = "version";
    public static final Option VERSION_OPTION = new Option("V", OPT_VERSION, false, "Print the current version.");


    public static void printHelp(Options options, String cmdLineSyntax, String description) {
        new HelpFormatter().printHelp(
                CommandLineHelper.DEFAULT_WIDTH,
                cmdLineSyntax,
                description + "\nOptions:",
                options,
                CommandLineHelper.DEFAULT_FOOTER,
                false);

    }

    public static void printUsage(Options options, String cmdLineSyntax) {
        new HelpFormatter().printUsage(
                new PrintWriter(System.err),
                HelpFormatter.DEFAULT_WIDTH,
                cmdLineSyntax,
                options);
    }

    public static String getVersion() throws IOException {
        Properties prop = new Properties();
        InputStream in = CommandLineHelper.class.getResourceAsStream("/general.properties");
        prop.load(in);
        in.close();

        return prop.get("project.version").toString();
    }

    public static Options createHelpOptions() {

        Options options = new Options();
        options.addOption(HELP_OPTION);
        options.addOption(VERSION_OPTION);
        return options;
    }

    public CommandLine startApp(Options options, String cmdLineSyntax, String description, String[] args) {
        return startApp(options, cmdLineSyntax, description, args, true);
    }

    public CommandLine startApp(Options options, String cmdLineSyntax, String description, String[] args, boolean helpOnNoArgs) {

        try {
            // Make sure version option is added
            options.addOption(VERSION_OPTION);

            // Test for help first
            CommandLine helpCl = new PosixParser().parse(createHelpOptions(), args, true);

            if (helpCl.hasOption(OPT_VERSION)) {
                System.out.println("spectre " + getVersion());
                return null;
            }
            else if (helpCl.hasOption(OPT_HELP) || (helpOnNoArgs && helpCl.getArgList().isEmpty())) {
                CommandLineHelper.printHelp(options, cmdLineSyntax, description);
                return null;
            }

            // Check if version was requested
            return new PosixParser().parse(options, args);

        } catch (ParseException | IOException p) {
            System.err.println(p.getMessage());
            CommandLineHelper.printUsage(options, cmdLineSyntax);
            return null;
        }
    }

}
