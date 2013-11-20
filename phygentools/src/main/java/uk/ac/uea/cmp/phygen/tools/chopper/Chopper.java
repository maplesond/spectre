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
package uk.ac.uea.cmp.phygen.tools.chopper;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.ds.network.QuartetNetwork;
import uk.ac.uea.cmp.phygen.core.ds.network.QuartetNetworkAgglomerator;
import uk.ac.uea.cmp.phygen.core.util.SpiFactory;
import uk.ac.uea.cmp.phygen.tools.PhygenTool;
import uk.ac.uea.cmp.phygen.tools.chopper.loader.Source;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This class chops forests down into little pieces of woods input is a file of newick trees output is a quartet weights
 * file
 */
@MetaInfServices
public class Chopper extends PhygenTool {

    private static final String DEFAULT_OUTPUT_PREFIX = "chopper";
    private static final File DEFAULT_OUTPUT_DIR = new File("").getParentFile();


    private static Logger log = LoggerFactory.getLogger(Chopper.class);

    private static final String OPT_INPUT_FILE = "input";
    private static final String OPT_OUTPUT_DIR = "output";
    private static final String OPT_OUTPUT_PREFIX = "prefix";
    private static final String OPT_SOURCE = "source";

    private SpiFactory<Source> sourceFactory;

    public Chopper() {
        this.sourceFactory = new SpiFactory<>(Source.class);
    }



    @Override
    protected Options createInternalOptions() {

        // Create Options object
        Options options = new Options();

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_OUTPUT_DIR).hasArg()
                .withDescription("The directory, which will contain output from chopper").create("o"));

        options.addOption(OptionBuilder.withArgName("string").withLongOpt(OPT_OUTPUT_PREFIX).hasArg()
                .withDescription("The prefix for the output files.  Default: " + DEFAULT_OUTPUT_PREFIX).create("p"));

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_INPUT_FILE).hasArg()
                .withDescription("The input file containing the tree(s) to chop").create("i"));

        options.addOption(OptionBuilder.withArgName("string").withLongOpt(OPT_SOURCE).hasArg()
                .withDescription("The type of source that will be input: " + this.sourceFactory.listServicesAsString()).create("s"));

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

        String source = commandLine.getOptionValue(OPT_SOURCE);

        // Create the quartets from the input and save to file
        this.execute(inputFile, source.trim(), outputDir, prefix);
    }

    @Override
    public String getName() {
        return "chopper";
    }


    @Override
    public String getDescription() {
        return "Chopper extracts quartet weights from trees";
    }

    /**
     * Executes Chopper programmatically.  Loads input from file and saves output to file.
     *
     * @param inputFile
     * @param source
     */
    public QuartetNetwork execute(File inputFile, String source, File outputDir, String outputPrefix) throws IOException {

        QuartetNetworkAgglomerator quartetNetworkAgglomerator = this.execute(inputFile, source);

        quartetNetworkAgglomerator.saveInformation(new File(outputDir, outputPrefix + ".info"));

        QuartetNetwork quartetNetwork = quartetNetworkAgglomerator.create();

        quartetNetwork.saveQuartets(new File(outputDir, outputPrefix + ".qw"));

        return quartetNetwork;
    }

    public QuartetNetworkAgglomerator execute(File inputFile, String source) throws IOException {

        QuartetNetworkAgglomerator quartetNetworkAgglomerator = source.equalsIgnoreCase("SCRIPT") ?
                doScript(inputFile) :
                new QuartetNetworkAgglomerator().addSource(this.sourceFactory.create(source).load(inputFile, 1.0));

        quartetNetworkAgglomerator.divide();

        return quartetNetworkAgglomerator;
    }


    public static void run(File inputFile, String source, File outputDir, String outputPrefix) throws IOException {

        new Chopper().execute(inputFile, source, outputDir, outputPrefix);
    }


    protected QuartetNetworkAgglomerator doScript(File inputFile) throws IOException {

        // Create an empty tree
        QuartetNetworkAgglomerator choppedTree = new QuartetNetworkAgglomerator();

        // Load the script
        List<String> lines = FileUtils.readLines(inputFile);

        // Execute each line of the script
        for (String line : lines) {

            StringTokenizer sT = new StringTokenizer(line.trim());

            if (sT.hasMoreTokens()) {

                // The first token should specify the loader
                String sourceName = sT.nextToken();

                // The second token should be the file to load
                if (sT.hasMoreTokens()) {

                    String sourceFileName = sT.nextToken();
                    File sourceFile = new File(sourceFileName);

                    // If relative path was given, assume we are using the script's relative path rather than relative to
                    // the current working directory
                    if (!sourceFile.isAbsolute() && inputFile.getParent() != null) {
                        sourceFile = new File(inputFile.getParent(), sourceFileName);
                    }

                    // The third token is optional, but if present we multiply all weights in the tree of this file by
                    // the given amount
                    double weight = sT.hasMoreTokens() ?
                            Double.parseDouble(sT.nextToken()) :
                            1.0;

                    // Create a loader loader
                    Source source = this.sourceFactory.create(sourceName);

                    // Execute chopper for this file.
                    choppedTree.addSource(source.load(sourceFile, weight));

                } else {

                    log.warn("Script line specified source (" + sourceName + ") but is lacking file name!  Ignoring line.");
                }
            }

            System.gc();
        }

        return choppedTree;
    }

}
