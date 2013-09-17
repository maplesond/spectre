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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetWeights;
import uk.ac.uea.cmp.phygen.core.ui.cli.PhygenTool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This class chops forests down into little pieces of woods input is a file of newick trees output is a quartet weights
 * file
 */
public class Chopper extends PhygenTool {

    private static Logger log = LoggerFactory.getLogger(Chopper.class);

    private static final String OPT_INPUT_FILE = "input";
    private static final String OPT_OUTPUT_FILE = "output";
    private static final String OPT_TYPE = "type";


    @Override
    protected Options createInternalOptions() {

        // Create Options object
        Options options = new Options();

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_OUTPUT_FILE).hasArg()
                .withDescription("The output file, which will contain the quartets").create("o"));

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_INPUT_FILE).hasArg()
                .withDescription("The input file containing the tree to chop").create("i"));

        options.addOption(OptionBuilder.withArgName("string").withLongOpt(OPT_TYPE).hasArg()
                .withDescription("The output file type: " + Type.listTypes()).create("t"));

        return options;
    }

    @Override
    protected void execute(CommandLine commandLine) throws IOException {

        File inputFile = new File(commandLine.getOptionValue(OPT_INPUT_FILE));
        File outputFile = new File(commandLine.getOptionValue(OPT_OUTPUT_FILE));

        // Ensures that requests to uk.ac.uea.cmp.phygen.superq.chopper are upper case and have colons replaced
        // with underscores to reflect enum Type.
        Type type = Type.valueOf(commandLine.getOptionValue(OPT_TYPE).toUpperCase().replace(':', '_'));

        execute(inputFile, outputFile, type);
    }

    /**
     * Executes Chopper programmatically.
     * @param inputFile
     * @param outputFile
     * @param type
     */
    public void execute(File inputFile, File outputFile, Type type) throws IOException {

        ChoppedTree choppedTree = type == Type.SCRIPT ?
                doScript(inputFile) :
                doOneType(inputFile, type, 1.0);

        choppedTree.divide();
        choppedTree.save(outputFile);
    }


    public static void run(File inputFile, File outputFile, Type type) throws IOException {
        new Chopper().execute(inputFile, outputFile, type);
    }




    protected ChoppedTree doScript(File inputFile) throws IOException {

        ChoppedTree choppedTree = null;

        List<String> lines = FileUtils.readLines(inputFile);

        for(String line : lines) {

            StringTokenizer sT = new StringTokenizer(line);

            if (sT.hasMoreTokens()) {

                Type t = Type.valueOfDescriptiveName(sT.nextToken());

                // line may be:
                // (at each step, load (with dummy weight if need be))

                // newick file, set of trees: if weight is given, multiply with newick weights
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

                    choppedTree = doOneType(sourceFile, t, weight, choppedTree);

                } else {

                    log.warn("QNet.Chopper: Script line lacking file name!");
                }
            }

            System.gc();
        }

        return choppedTree;
    }


    protected ChoppedTree doOneType(File inputFile, Type type, double weight) throws IOException {
       return doOneType(inputFile, type, weight, new ChoppedTree());
    }

    protected ChoppedTree doOneType(File inputFile, Type type, double weight, ChoppedTree choppedTree) throws IOException {

        LinkedList taxonNames = choppedTree.getTaxonNames();
        QuartetWeights qW = choppedTree.getQuartetWeights();
        QuartetWeights summer = choppedTree.getSummer();

        Source loader = type.getLoader();

        // I'm sure it should be possible to tidy this up further.
        loader.load(inputFile.getPath(), weight);
        LinkedList taxonNamesOld = (LinkedList) taxonNames.clone();
        loader.harvestNames(taxonNames);
        loader.translate(taxonNames);
        qW = qW.translate(taxonNamesOld, taxonNames);
        summer = summer.translate(taxonNamesOld, taxonNames);


        // crucial, drop now the list stuff

        while (loader.hasMoreSets()) {


            double aW = loader.getNextWeight();
            QuartetWeights aQW = loader.getNextQuartetWeights();
            Combiner.add(qW, aQW, aW);

            System.gc();
        }

        Combiner.sum(summer, taxonNames, loader);

        return choppedTree;
    }




    public static enum Type {

        SCRIPT {

            public Source getLoader() {
                throw new UnsupportedOperationException("Can't get a loader from SCIPT mode.");
            }

            public String getDescriptiveName() {
                return "script";
            }
        },
        NEWICK {

            public Source getLoader() {
                return new TreeLoader();
            }

            public String getDescriptiveName() {
                return "newick";
            }
        },
        Q_WEIGHTS {

            public Source getLoader() {
                return new QWeightLoader();
            }

            public String getDescriptiveName() {
                return "qweights";
            }
        },
        NEXUS_ST_SPLITS {

            public Source getLoader() {
                return new NexusSplitsLoader();
            }

            public String getDescriptiveName() {
                return "nexus:st_splits";
            }
        },
        NEXUS_ST_QUARTETS {

            public Source getLoader() {
                return new NexusQuartetLoader();
            }

            public String getDescriptiveName() {
                return "nexus:st_quartets";
            }
        },
        NEXUS_TREES {

            public Source getLoader() {
                return new TreeFileLoader();
            }

            public String getDescriptiveName() {
                return "nexus:trees";
            }
        },
        NEXUS_DISTANCES {

            public Source getLoader() {
                return new NexusDistancesLoader();
            }

            public String getDescriptiveName() {
                return "nexus:distances";
            }
        };

        public abstract Source getLoader();

        public abstract String getDescriptiveName();

        public static Type valueOfDescriptiveName(String name) {
            for (Type t : Type.values()) {
                if (t.getDescriptiveName().equalsIgnoreCase(name)) {
                    return t;
                }
            }

            throw new IllegalArgumentException("Unknown type");
        }

        public static String listTypes() {
            List<String> typeStrings = new ArrayList<String>();

            for(Type t : Type.values()) {
                typeStrings.add(t.name());
            }

            return "[" + StringUtils.join(typeStrings, ", ") + "]";
        }
    }
}