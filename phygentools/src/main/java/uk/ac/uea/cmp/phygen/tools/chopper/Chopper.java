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
                doOneType(inputFile, source, 1.0);

        choppedTree.divide();
        choppedTree.save(outputFile);
    }


    public static void run(File inputFile, File outputFile, String source) throws IOException {
        new Chopper().execute(inputFile, outputFile, source.trim());
    }


    protected ChoppedTree doScript(File inputFile) throws IOException {

        ChoppedTree choppedTree = null;

        List<String> lines = FileUtils.readLines(inputFile);

        for (String line : lines) {

            StringTokenizer sT = new StringTokenizer(line);

            if (sT.hasMoreTokens()) {

                // Create an instance of the specified source
                String source = sT.nextToken();

                // line may be:
                // (at each step, load (with dummy length if need be))

                // newick file, set of trees: if length is given, multiply with newick weights
                if (sT.hasMoreTokens()) {

                    String sourceFileName = sT.nextToken();
                    File sourceFile = new File(sourceFileName);

                    if (!sourceFile.isAbsolute()) {

                        // if no path, give it the path of the script file
                        if (inputFile.getParent() != null) {

                            // a path was given to the script file
                            sourceFile = new File(inputFile.getParent(), sourceFileName);
                        }
                    }

                    double weight = sT.hasMoreTokens() ?
                            Double.parseDouble(sT.nextToken()) :
                            1.0;

                    choppedTree = doOneType(sourceFile, source, weight, choppedTree);

                } else {

                    log.warn("Chopper: Script line lacking file name!");
                }
            }

            System.gc();
        }

        return choppedTree;
    }


    protected ChoppedTree doOneType(File inputFile, String sourceName, double weight) throws IOException {
        return doOneType(inputFile, sourceName, weight, new ChoppedTree());
    }

    protected ChoppedTree doOneType(File inputFile, String sourceName, double weight, ChoppedTree choppedTree) throws IOException {

        Taxa taxonNames = choppedTree.getTaxa();
        QuartetWeights qW = choppedTree.getQuartetWeights();
        QuartetWeights summer = choppedTree.getSummer();

        // Create a new instance of the source
        Source source = this.sourceFactory.create(sourceName);

        // I'm sure it should be possible to tidy this up further.
        source.load(inputFile, weight);
        Taxa oldTaxa = new Taxa(taxonNames);
        source.addTaxa(taxonNames);
        source.translate(taxonNames);


        qW = qW.translate(oldTaxa, taxonNames);
        summer = summer.translate(oldTaxa, taxonNames);


        // crucial, drop now the list stuff
        while (source.hasMoreSets()) {

            double aW = source.getNextWeight();
            QuartetWeights aQW = source.getNextQuartetWeights();
            qW.add(aQW, aW);

            System.gc();
        }

        summer.sum(taxonNames, source.findTaxaSets(), source.getWeights());

        return choppedTree;
    }


}
