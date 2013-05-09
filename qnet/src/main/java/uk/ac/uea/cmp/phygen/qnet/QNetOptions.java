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

package uk.ac.uea.cmp.phygen.qnet;

import org.apache.commons.cli.*;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Sarah_2
 * Date: 24/04/13
 * Time: 14:17
 * To change this template use File | Settings | File Templates.
 */
public class QNetOptions {

    public static final String OPT_INPUT = "input";
    public static final String OPT_LOG = "log";
    public static final String OPT_TOLERANCE = "tolerance";
    public static final String OPT_NNLS = "nnls";
    public static final String OPT_HELP = "help";

    private File input = null;
    private boolean log = false;
    private double tolerance = -1.0;
    private String nnls = "gurobi";
    private boolean help = false;


    /**
     * Process command line args.  Checks for help first, then clean, then args for normal operation.
     *
     * @param cmdLine The command line used to execute the program
     * @throws org.apache.commons.cli.ParseException
     */
    public QNetOptions(CommandLine cmdLine) throws ParseException {

        if (cmdLine.getOptions().length == 0) {
            help = true;
        } else {
            help = cmdLine.hasOption(OPT_HELP);

            if (!help) {

                // Required arguments
                if (cmdLine.hasOption(OPT_INPUT)) {
                    input = new File(cmdLine.getOptionValue(OPT_INPUT));
                } else {
                    throw new ParseException(OPT_INPUT + " argument not specified.");
                }

                // Optional arguments
                log = cmdLine.hasOption(OPT_LOG);
                tolerance = cmdLine.hasOption(OPT_TOLERANCE) ? Double.parseDouble(cmdLine.getOptionValue(OPT_TOLERANCE)) : -1.0;
                nnls = cmdLine.hasOption(OPT_NNLS) ? cmdLine.getOptionValue(OPT_NNLS) : "gurobi";
            }
        }
    }

    public File getInput() {
        return input;
    }

    public boolean isLog() {
        return log;
    }

    public double getTolerance() {
        return tolerance;
    }

    public String getNnls() {
        return nnls;
    }

    public boolean isHelp() {
        return help;
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
        Option optLog = new Option("l", OPT_LOG, false, "Linear if false, Log if true");

        // Options with arguments
        Option optInput = OptionBuilder.withArgName("file").withLongOpt(OPT_INPUT).hasArg()
                .withDescription("The file containing the distance data to input.").create("i");

        Option optTolerance = OptionBuilder.withArgName("double").withLongOpt(OPT_TOLERANCE).hasArg()
                .withDescription("The tolerance").create("t");

        Option optNnls = OptionBuilder.withArgName("string").withLongOpt(OPT_NNLS).hasArg()
                .withDescription("If specified, uses optimisation: [gurobi]").create("n");



        // create Options object
        Options options = new Options();

        // add t option
        options.addOption(optHelp);
        options.addOption(optInput);
        options.addOption(optLog);
        options.addOption(optTolerance);
        options.addOption(optNnls);

        return options;
    }


}
