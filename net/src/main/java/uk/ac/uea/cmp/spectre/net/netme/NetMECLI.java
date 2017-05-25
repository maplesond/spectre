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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.spectre.core.ui.cli.CommandLineHelper;
import uk.ac.uea.cmp.spectre.core.util.LogConfig;
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

    public static final String OPT_OUTPUT_OLS = "ols";
    public static final String OPT_OUTPUT_PREFIX = "prefix";

    public static Options createOptions() {

        Options options = new Options();

        options.addOption(OptionBuilder.withArgName("string").withLongOpt(OPT_OUTPUT_PREFIX).hasArg()
                .withDescription(NetMEOptions.DESC_OUTPUT_PREFIX).create("o"));
        options.addOption(OptionBuilder.withArgName("string").withLongOpt(OPT_OUTPUT_OLS)
                .withDescription(NetMEOptions.DESC_OUTPUT_OLS).create("l"));

        options.addOption(CommandLineHelper.HELP_OPTION);

        return options;
    }


    public static void main(String[] args) {

        CommandLine commandLine = new CommandLineHelper().startApp(createOptions(), "netme [options] <nexus_file>",
                "Finds minimum evolution tree from a distance matrix and circular ordering from a circular split system.\n\n" +
                        "Takes in a nexus file containing a distance matrix and a circular " +
                        "ordering (this file can be obtained by, for example, running Neighbor-Net via netmake).  NetME outputs two files:\n" +
                        " - the weighted split system, in nexus format, corresponding to a restricted minimum evolution tree.\n" +
                        " - a file containing the tree length of tree weighted with OLS.\n", args);

        // If we didn't return a command line object then just return.  Probably the user requested help or
        // input invalid args
        if (commandLine == null) {
            return;
        }

        try {
            // Configure logging
            LogConfig.defaultConfig();

            NetMEOptions options = new NetMEOptions();

            if (commandLine.getArgs().length == 0) {
                throw new IOException("No input files specified.");
            }
            else if (commandLine.getArgs().length > 1) {
                throw new IOException("Expected no more than a single input file as input.");
            }

            // Requires input file containing distance matrices
            options.setInputFile(new File(commandLine.getArgs()[0]));
            options.setOls(commandLine.hasOption(OPT_OUTPUT_OLS));

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
