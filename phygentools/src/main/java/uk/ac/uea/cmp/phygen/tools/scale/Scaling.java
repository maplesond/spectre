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
package uk.ac.uea.cmp.phygen.tools.scale;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.tgac.metaopt.Objective;
import uk.ac.tgac.metaopt.Optimiser;
import uk.ac.tgac.metaopt.OptimiserException;
import uk.ac.tgac.metaopt.OptimiserFactory;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetSystemList;
import uk.ac.uea.cmp.phygen.core.ds.quartet.load.QLoader;
import uk.ac.uea.cmp.phygen.core.util.SpiFactory;
import uk.ac.uea.cmp.phygen.tools.PhygenTool;

import java.io.File;
import java.io.IOException;

@MetaInfServices
public class Scaling extends PhygenTool {

    private static Logger log = LoggerFactory.getLogger(Scaling.class);

    private static final String OPT_INPUT_FILE = "input";
    private static final String OPT_OUTPUT_PREFIX = "output";
    private static final String OPT_TYPE = "type";
    private static final String OPT_OPTIMISER = "optimiser";

    @Override
    public String getName() {
        return "scaler";
    }

    @Override
    public String getDescription() {
        return "Scales input trees to a given range based on their quartet weights (uses \"chopper\" to get the quartet weights from the trees)";
    }

    @Override
    protected Options createInternalOptions() {

        // Create Options object
        Options options = new Options();

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_OUTPUT_PREFIX).hasArg()
                .withDescription("The output prefix path, which will be used for all output files").create("o"));

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_INPUT_FILE).hasArg()
                .withDescription("The input file containing the tree to be scaled").create("i"));

        options.addOption(OptionBuilder.withArgName("string").withLongOpt(OPT_TYPE).hasArg()
                .withDescription("The output file type: " + new SpiFactory<>(QLoader.class).listServicesAsString()).create("t"));

        options.addOption(OptionBuilder.withArgName("string").withLongOpt(OPT_OPTIMISER).hasArg()
                .withDescription("The optimiser to use: " + OptimiserFactory.getInstance().listOperationalOptimisers()).create("p"));

        return options;
    }


    // **** Execution methods ****

    /**
     * Executes scaler using command line input
     * @param commandLine
     * @throws IOException
     */
    @Override
    protected void execute(CommandLine commandLine) throws IOException {

        // All options are required
        File inputFile = new File(commandLine.getOptionValue(OPT_INPUT_FILE));
        File outputPrefix = new File(commandLine.getOptionValue(OPT_OUTPUT_PREFIX));
        String type = commandLine.getOptionValue(OPT_TYPE);

        // Create the optimiser to use and execute scaler, wrap any optimiser exceptions as io exceptions.
        Optimiser optimiser;
        try {
            optimiser = commandLine.hasOption(OPT_OPTIMISER) ?
                    OptimiserFactory.getInstance().createOptimiserInstance(
                            commandLine.getOptionValue(OPT_OPTIMISER), Objective.ObjectiveType.QUADRATIC) :
                    null;

            this.execute(inputFile, outputPrefix, type, optimiser);

        } catch (OptimiserException oe) {
            throw new IOException(oe);
        }
    }

    /**
     * Scales a list of quartet networks loaded from a file and then writes scaled networks to file.
     * @param inputFile The file to load
     * @param type The type of file to load
     * @param optimiser The optimiser to use for scaling
     * @param outputPrefix The location for output files to be created
     * @return A scaled list of quartet networks.
     * @throws OptimiserException
     * @throws IOException
     */
    public void execute(File inputFile, File outputPrefix, String type, Optimiser optimiser) throws OptimiserException, IOException {

        this.execute(inputFile, type, optimiser).saveNetworks(outputPrefix);
    }


    /**
     * Scales a list of quartet networks loaded from a file
     * @param inputFile The file to load
     * @param type The type of file to load
     * @param optimiser The optimiser to use for scaling
     * @return A scaled list of quartet networks.
     * @throws OptimiserException
     * @throws IOException
     */
    public QuartetSystemList execute(File inputFile, String type, Optimiser optimiser) throws OptimiserException, IOException {

        return new QuartetSystemList(inputFile, type).scaleWeights(optimiser);
    }


    /**
     * Main entry point for the scaling program
     * @param args
     */
    public static void main(String[] args) {

        try {
            new Scaling().execute(args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(StringUtils.join(e.getStackTrace(), "\n"));
            System.exit(1);
        }
    }
}
