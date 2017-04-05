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

package uk.ac.uea.cmp.spectre.net.netme;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.spectre.core.ui.cli.CommandLineHelper;
import uk.ac.uea.cmp.spectre.core.util.Time;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 27/04/13
 * Time: 15:38
 * To change this template use File | Settings | File Templates.
 */
public class NetMECLI {

    private static Logger log = LoggerFactory.getLogger(NetMECLI.class);

    public static final String OPT_CIRCULAR_ORDERING_FILE = "circular_ordering";
    public static final String OPT_OUTPUT_PREFIX = "prefix";

    public static Options createOptions() {

        Options options = new Options();

        options.addOption(OptionBuilder.withArgName("string").withLongOpt(OPT_OUTPUT_PREFIX).hasArg()
                .withDescription(NetMEOptions.DESC_OUTPUT_PREFIX).create("o"));

        options.addOption(CommandLineHelper.HELP_OPTION);

        return options;
    }


    public static void main(String[] args) {

        CommandLine commandLine = CommandLineHelper.startApp(createOptions(), "netme [options] <distance_matrix_file> <circular_ordering_file>",
                "Finds minimum evolution tree within a circular split system.\n\n" +
                        "Takes in a nexus or phylip file containing a distance matrix and a nexus file containing a circular " +
                        "ordering (this file can be obtained by, for example, running NeighborNet in SplitsTree4 and saving the " +
                        "split system in nexus format to disk).  NetME outputs three files:\n" +
                        " - the weighted split system, in nexus format, corresponding to a restricted minimum evolution tree, where the weights " +
                        "are derived from the Ordinary Least Squares (OLS) method used for constructing the tree.\n" +
                        " - the weighted split system, in nexus format, corresponding to a restricted minimum evolution tree, where the weights " +
                        "are recalculated by using a Non-Negative Least Squares (NNLS) method.\n" +
                        " - a file containing the tree length of tree weighted with OLS.\n\n" +
                        "The resulting split systems in nexus format can be visualised in SplitsTree4.", args);

        // If we didn't return a command line object then just return.  Probably the user requested help or
        // input invalid args
        if (commandLine == null) {
            return;
        }

        try {
            // Configure logging
            BasicConfigurator.configure(new ConsoleAppender(new PatternLayout("%d{HH:mm:ss} %p: %m%n")));

            NetMEOptions options = new NetMEOptions();

            if (commandLine.getArgs().length == 0) {
                throw new IOException("No input files specified.");
            }
            else if (commandLine.getArgs().length == 1) {
                throw new IOException("Expected two input files.  The first should be the distance matrix file, the second should be a circular ordering file.");
            }
            else if (commandLine.getArgs().length > 2) {
                throw new IOException("Expected no more than two input files, the first should be the distance matrix file, the second should be a circular ordering file.");
            }

            // Requires input file containing distance matrices
            options.setDistancesFile(new File(commandLine.getArgs()[0]));
            options.setCircularOrderingFile(new File(commandLine.getArgs()[1]));

            if (commandLine.hasOption(OPT_OUTPUT_PREFIX)) {
                File op = new File(commandLine.getOptionValue(OPT_OUTPUT_PREFIX));
                if (op.getParentFile() != null) {
                    options.setOutputDir(op.getParentFile());
                }
                else {
                    options.setOutputDir(new File("."));
                }
                options.setPrefix(commandLine.getOptionValue(OPT_OUTPUT_PREFIX));
            }
            else {
                options.setOutputDir(new File("."));
                options.setPrefix("netme-" + Time.createTimestamp());
            }

            new NetME(options).run();

        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(StringUtils.join(e.getStackTrace(), "\n"));
            System.exit(1);
        }
    }

}
