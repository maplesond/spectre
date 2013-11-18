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
import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetWeights;
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

    private static Logger log = LoggerFactory.getLogger(Chopper.class);

    private static final String OPT_INPUT_FILE = "input";
    private static final String OPT_OUTPUT_FILE = "output";
    private static final String OPT_SOURCE = "source";

    private SpiFactory<Source> sourceFactory;

    public Chopper() {
        this.sourceFactory = new SpiFactory<>(Source.class);
    }



    @Override
    protected Options createInternalOptions() {

        // Create Options object
        Options options = new Options();

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_OUTPUT_FILE).hasArg()
                .withDescription("The output file, which will contain the quartets and their weights").create("o"));

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_INPUT_FILE).hasArg()
                .withDescription("The input file containing the tree(s) to chop").create("i"));

        options.addOption(OptionBuilder.withArgName("string").withLongOpt(OPT_SOURCE).hasArg()
                .withDescription("The type of source that will be input: " + this.sourceFactory.listServicesAsString()).create("s"));

        return options;
    }

    @Override
    protected void execute(CommandLine commandLine) throws IOException {

        File inputFile = new File(commandLine.getOptionValue(OPT_INPUT_FILE));
        File outputFile = new File(commandLine.getOptionValue(OPT_OUTPUT_FILE));

        String source = commandLine.getOptionValue(OPT_SOURCE);

        this.execute(inputFile, outputFile, source);
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
     * Executes Chopper programmatically.
     *
     * @param inputFile
     * @param outputFile
     * @param source
     */
    public void execute(File inputFile, File outputFile, String source) throws IOException {

        ChoppedTree choppedTree = source.equalsIgnoreCase("SCRIPT") ?
                doScript(inputFile) :
                new ChoppedTree().addSource(inputFile, this.sourceFactory.create(source), 1.0);

        choppedTree.divide();
        choppedTree.save(outputFile);
    }


    public static void run(File inputFile, File outputFile, String source) throws IOException {
        new Chopper().execute(inputFile, outputFile, source.trim());
    }


    protected ChoppedTree doScript(File inputFile) throws IOException {

        // Create an empty tree
        ChoppedTree choppedTree = new ChoppedTree();

        // Load the script
        List<String> lines = FileUtils.readLines(inputFile);

        // Execute each line of the script
        for (String line : lines) {

            StringTokenizer sT = new StringTokenizer(line.trim());

            if (sT.hasMoreTokens()) {

                // The first token should specify the source
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

                    // Create a source loader
                    Source source = this.sourceFactory.create(sourceName);

                    // Execute chopper for this file.
                    choppedTree.addSource(sourceFile, source, weight);

                } else {

                    log.warn("Script line specified source (" + sourceName + ") but is lacking file name!  Ignoring line.");
                }
            }

            System.gc();
        }

        return choppedTree;
    }

}
