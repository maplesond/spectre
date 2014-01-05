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
package uk.ac.uea.cmp.phygen.tools.quart;

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
import uk.ac.uea.cmp.phygen.core.ds.quartet.GroupedQuartetSystem;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetSystem;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetSystemCombiner;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetSystemList;
import uk.ac.uea.cmp.phygen.core.ds.quartet.load.QLoader;
import uk.ac.uea.cmp.phygen.core.io.qweight.QWeightWriter;
import uk.ac.uea.cmp.phygen.core.util.SpiFactory;
import uk.ac.uea.cmp.phygen.tools.PhygenTool;

import java.io.File;
import java.io.IOException;

/**
 * This class creates a single combined quartet system from a number of trees.
 */
@MetaInfServices
public class Quart extends PhygenTool {

    private static final String DEFAULT_OUTPUT_PREFIX = "chopper";
    private static final File DEFAULT_OUTPUT_DIR = new File("").getParentFile();


    private static Logger log = LoggerFactory.getLogger(Quart.class);

    private static final String OPT_INPUT_FILE = "input";
    private static final String OPT_OUTPUT_DIR = "output";
    private static final String OPT_OUTPUT_PREFIX = "prefix";
    private static final String OPT_TYPE = "type";
    private static final String OPT_OPTIMISER = "optimiser";

    // These variables get set after execution.  Helps the client get a handle on the output.
    private File quartetFile;
    private File infoFile;



    @Override
    protected Options createInternalOptions() {

        // Create Options object
        Options options = new Options();

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_OUTPUT_DIR).hasArg()
                .withDescription("The directory, which will contain output from quart").create("o"));

        options.addOption(OptionBuilder.withArgName("string").withLongOpt(OPT_OUTPUT_PREFIX).hasArg()
                .withDescription("The prefix for the output files.  Default: " + DEFAULT_OUTPUT_PREFIX).create("p"));

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_INPUT_FILE).hasArg()
                .withDescription("The input file containing the tree(s) to chop").create("i"));

        options.addOption(OptionBuilder.withArgName("string").withLongOpt(OPT_TYPE).hasArg()
                .withDescription("The type of source that will be input: " + new SpiFactory<>(QLoader.class).listServicesAsString()).create("t"));

        options.addOption(OptionBuilder.withArgName("string").withLongOpt(OPT_OPTIMISER).hasArg()
                .withDescription("The optimiser to use: " + OptimiserFactory.getInstance().listOperationalOptimisers()).create("p"));

        return options;
    }

    @Override
    protected void execute(CommandLine commandLine) throws IOException {

        File inputFile = new File(commandLine.getOptionValue(OPT_INPUT_FILE));

        File outputDir = commandLine.hasOption(OPT_OUTPUT_DIR) ?
                new File(commandLine.getOptionValue(OPT_OUTPUT_DIR)) :
                DEFAULT_OUTPUT_DIR;

        String prefix = commandLine.hasOption(OPT_OUTPUT_PREFIX) ?
                commandLine.getOptionValue(OPT_OUTPUT_PREFIX) :
                DEFAULT_OUTPUT_PREFIX;

        String source = commandLine.getOptionValue(OPT_TYPE);

        try {
            Optimiser optimiser = commandLine.hasOption(OPT_OPTIMISER) ?
                    OptimiserFactory.getInstance().createOptimiserInstance(
                            commandLine.getOptionValue(OPT_OPTIMISER), Objective.ObjectiveType.QUADRATIC) :
                    null;

            // Create the quartets from the input and save to file
            this.execute(inputFile, source.trim(), optimiser, outputDir, prefix);
        }
        catch (OptimiserException oe) {
            throw new IOException(oe);
        }
    }

    @Override
    public String getName() {
        return "chopper";
    }


    @Override
    public String getDescription() {
        return "Chopper extracts quartet weights from trees";
    }

    public File getInfoFile() {
        return this.infoFile;
    }

    public File getQuartetFile() {
        return this.quartetFile;
    }

    /**
     * Loads Quartet Networks from file, scales them, then combines them and writes the combined network to file.
     * @param inputFile The file containing the quartet networks .
     * @param source The type of file.
     * @param optimiser The optimiser to use for scaling the quartet networks.
     * @param outputDir The output directory in which the files will be saved.
     * @param outputPrefix The prefix to be applied to all the output files.
     * @return A combination of quartet networks
     * @throws IOException Thrown if there was an issue loading files.
     * @throws OptimiserException Thrown if there was a problem scaling the quartet networks.
     */
    public GroupedQuartetSystem execute(File inputFile, String source, Optimiser optimiser, File outputDir, String outputPrefix) throws IOException, OptimiserException {

        this.infoFile = new File(outputDir, outputPrefix + ".info");
        this.quartetFile = new File(outputDir, outputPrefix + ".qw");

        QuartetSystemCombiner quartetSystemCombiner = this.execute(inputFile, source, optimiser);

        quartetSystemCombiner.saveInformation(this.infoFile);

        GroupedQuartetSystem quartetSystem = quartetSystemCombiner.create();

        // Write to disk
        new QWeightWriter().writeQuartets(this.quartetFile, quartetSystem);

        return quartetSystem;
    }


    /**
     * Loads Quartet Networks from file, then combines them
     * @param inputFile The file containing the quartet networks
     * @param source The type of file
     * @return A combination of quartet networks
     * @throws IOException Thrown if there was an issue loading files.
     */
    public QuartetSystemCombiner execute(File inputFile, String source)
            throws IOException {

        return this.execute(new QuartetSystemList(inputFile, source));
    }

    /**
     * Loads Quartet Networks from file, scales them, then combines them
     * @param inputFile The file containing the quartet networks
     * @param source The type of file
     * @param optimiser The optimiser to use for scaling the quartet networks.
     * @return A combination of quartet networks
     * @throws IOException Thrown if there was an issue loading files.
     * @throws OptimiserException Thrown if there was a problem scaling the quartet networks.
     */
    public QuartetSystemCombiner execute(File inputFile, String source, Optimiser optimiser)
            throws IOException, OptimiserException {

        return this.execute(new QuartetSystemList(inputFile, source), optimiser);
    }


    /**
     * Scales a list of quartet networks and then combines them
     * @param qnets A list of quartet networks
     * @param optimiser The optimiser with which the qnet scaling will be performed.
     * @return A combination of quartet networks
     * @throws OptimiserException Thrown if there was an issue scaling the quartet networks
     */
    public QuartetSystemCombiner execute(QuartetSystemList qnets, Optimiser optimiser)
            throws OptimiserException {

        // Scale the qnets only if an optimiser is provided
        if (optimiser != null) {
            qnets.scaleWeights(optimiser);
        }

        return this.execute(qnets);
    }

    /**
     * Core execution routine for quart.  Takes in a list of quartet networks and combines them.
     * @param qnets A list of quartet networks
     * @return A combination of quartet networks
     */
    public QuartetSystemCombiner execute(QuartetSystemList qnets) {

        // Combines networks
        QuartetSystemCombiner combiner = new QuartetSystemCombiner();
        for(QuartetSystem qnet : qnets) {
            combiner.combine(qnet);
        }

        // Divides the quartet weights in the combined network
        combiner.divide();

        return combiner;
    }


    /**
     * Main entry point for Chopper
     * @param args
     */
    public static void main(String[] args) {

        try {
            new Quart().execute(args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(StringUtils.join(e.getStackTrace(), "\n"));
            System.exit(1);
        }
    }
}
