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

package uk.ac.uea.cmp.spectre.net.netmake;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.spectre.core.ds.split.circular.ordering.CircularOrderingAlgorithms;
import uk.ac.uea.cmp.spectre.core.ui.cli.CommandLineHelper;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author Sarah Bastkowski
 *         See S. Bastkowski, 2010:
 *         <I>Algorithmen zum Finden von Bäumen in Neighbor Net Netzwerken</I>
 */
public class NetMakeCLI {

    private static Logger log = LoggerFactory.getLogger(NetMakeCLI.class);

    public static final String OPT_INPUT = "input";
    public static final String OPT_INPUT_TYPE = "input_file_type";
    public static final String OPT_OUTPUT_NETWORK = "network_out";
    public static final String OPT_OUTPUT_TREE = "tree_out";
    public static final String OPT_TREE_PARAM = "tree_param";
    public static final String OPT_WEIGHTINGS_1 = "weightings_1";
    public static final String OPT_WEIGHTINGS_2 = "weightings_2";
    public static final String OPT_CO_ALG = "co_alg";
    public static final String OPT_HELP = "help";


    public static Options createOptions() {

        // create Options object
        Options options = new Options();

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_OUTPUT_NETWORK).hasArg()
                .withDescription(NetMakeOptions.DESC_OUTPUT_NETWORK).create("on"));

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_OUTPUT_TREE).hasArg()
                .withDescription(NetMakeOptions.DESC_OUTPUT_TREE).create("ot"));

        options.addOption(OptionBuilder.withArgName("double").withLongOpt(OPT_TREE_PARAM).hasArg()
                .withDescription(NetMakeOptions.DESC_TREE_PARAM).create("z"));

        options.addOption(OptionBuilder.withArgName("weighting").withLongOpt(OPT_WEIGHTINGS_1).hasArg()
                .withDescription(NetMakeOptions.DESC_WEIGHTINGS_1).create("w"));

        options.addOption(OptionBuilder.withArgName("weighting").withLongOpt(OPT_WEIGHTINGS_2).hasArg()
                .withDescription(NetMakeOptions.DESC_WEIGHTINGS_2).create("x"));

        options.addOption(OptionBuilder.withArgName("circular_ordering").withLongOpt(OPT_CO_ALG).hasArg()
                .withDescription(NetMakeOptions.DESC_CO_ALG).create("ordering"));


        options.addOption(CommandLineHelper.HELP_OPTION);

        return options;
    }


    public static void main(String[] args) {

        CommandLine commandLine = CommandLineHelper.startApp(createOptions(), "netmake [options] <distance_matrix_file>",
                "Creates networks from distance matrices.\nSupports nexus format input.", args);

        // If we didn't return a command line object then just return.  Probably the user requested help or
        // input invalid args
        if (commandLine == null) {
            return;
        }

        try {


            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
            Date date = new Date();
            String timeStamp = dateFormat.format(date);

            log.info("NetMake: Parsing arguments");

            if (commandLine.getArgs().length == 0) {
                throw new IOException("No input file specified.");
            }
            else if (commandLine.getArgs().length > 1) {
                throw new IOException("Only expected a single input file.");
            }

            File input = new File(commandLine.getArgs()[0]);
            File outputNetwork = commandLine.hasOption(OPT_OUTPUT_NETWORK) ? new File(commandLine.getOptionValue(OPT_OUTPUT_NETWORK)) : new File("netmake.nex");
            File outputTree = commandLine.hasOption(OPT_OUTPUT_TREE) ? new File(commandLine.getOptionValue(OPT_OUTPUT_TREE)) : null;
            double treeParam = commandLine.hasOption(OPT_TREE_PARAM) ? Double.parseDouble(commandLine.getOptionValue(OPT_TREE_PARAM)) : NetMakeOptions.DEFAULT_TREE_WEIGHT;
            String weightings1 = commandLine.hasOption(OPT_WEIGHTINGS_1) ? commandLine.getOptionValue(OPT_WEIGHTINGS_1) : "TSP";
            String weightings2 = commandLine.hasOption(OPT_WEIGHTINGS_2) ? commandLine.getOptionValue(OPT_WEIGHTINGS_2) : null;
            String coAlg = commandLine.hasOption(OPT_CO_ALG) ? commandLine.getOptionValue(OPT_CO_ALG) : CircularOrderingAlgorithms.NETMAKE.toString();


            // Create the configured NetMake object to process
            NetMakeOptions netMakeOptions = new NetMakeOptions();
            netMakeOptions.setInput(input);
            netMakeOptions.setOutputNetwork(outputNetwork);
            netMakeOptions.setOutputTree(outputTree);
            netMakeOptions.setTreeParam(treeParam);
            netMakeOptions.setWeighting1(weightings1);
            netMakeOptions.setWeighting2(weightings2);
            netMakeOptions.setCoAlg(coAlg);

            // Run NetMake
            new NetMake(netMakeOptions).run();

        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(StringUtils.join(e.getStackTrace(), "\n"));
            System.exit(1);
        }
    }

}