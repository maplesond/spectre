/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2014  UEA School of Computing Sciences
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
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

package uk.ac.uea.cmp.spectre.flatnj;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import uk.ac.tgac.metaopt.Objective;
import uk.ac.tgac.metaopt.Optimiser;
import uk.ac.tgac.metaopt.OptimiserFactory;
import uk.ac.uea.cmp.spectre.core.ds.network.FlatNetwork;
import uk.ac.uea.cmp.spectre.core.ds.network.Network;
import uk.ac.uea.cmp.spectre.core.ds.network.Vertex;
import uk.ac.uea.cmp.spectre.core.ui.cli.CommandLineHelper;
import uk.ac.uea.cmp.spectre.flatnj.ds.*;
import uk.ac.uea.cmp.spectre.flatnj.fdraw.*;
import uk.ac.uea.cmp.spectre.flatnj.tools.*;

import java.io.File;

/**
 * FlatNJ (FlatNetJoining) is a program for computing split networks that allow
 * for interior vertices to be labeled while being (almost) planar.
 *
 * @author balvociute
 */
public class FlatNJ {

    private static final String OPT_THRESHOLD = "threshold";
    private static final String OPT_FILTER = "filter";
    private static final String OPT_INPUT = "in";
    private static final String OPT_OUTPUT = "out";
    private static final String OPT_OPTIMISER = "optimiser";

    private static final double DEFAULT_THRESHOLD = 0.15;
    private static final String DEFAULT_OUTPUT = "flatnj.out";

    /**
     * Nexus file reader
     */
    private static NexusReader reader;

    /**
     * Split length estimator
     */
    private static WeightCalculator wCalculator;

    /**
     * Nexus file writer
     */
    private static Writer writer;

    /**
     * Quadruple system from input file
     */
    private static QuadrupleSystem qs = null;

    /**
     * Flat split system coded as allowable sequence
     */
    private static PermutationSequence ps = null;

    /**
     * Flat split system in general split system format
     */
    private static SplitSystem ss = null;

    /**
     * Starting vertex of split network
     */
    private static Vertex net = null;

    /**
     * List of the taxa
     */
    private static Taxa taxa = null;

    /**
     * Length of the information line
     */
    private static int nDots = 40;

    /**
     * Flat split system as allowable sequence for the drawing module
     */
    private static PermutationSequenceDraw psDraw = null;

    /**
     * Split network computed from flat split system
     */
    private static Network network = null;

    /**
     * Iteration counter used by interactive user interface
     */
    static int iter = 0;

    protected static Options createOptions() {

        // create Options object
        Options options = new Options();

        // Options with arguments
        options.addOption(OptionBuilder.withArgName("double").withLongOpt(OPT_THRESHOLD).hasArg()
                .withDescription("Filtering threshold, i.e. minimal length ratio allowed for two incompatible splits. Default value (" + DEFAULT_THRESHOLD + ")").create("t"));

        options.addOption(OptionBuilder.withArgName("boolean").withLongOpt(OPT_FILTER)
                .withDescription("Filter the split system").create("f"));

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_INPUT).isRequired().hasArg()
                .withDescription("Input file - Quadruple data in nexus format.").create("i"));

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_OUTPUT).hasArg()
                .withDescription("Output file - Default value (\"" + DEFAULT_OUTPUT + "\")").create("o"));

        options.addOption(OptionBuilder.withArgName("string").withLongOpt(OPT_OPTIMISER).hasArg()
                .withDescription("The optimiser to use: " + OptimiserFactory.getInstance().listOperationalOptimisers()).create("p"));

        options.addOption(CommandLineHelper.HELP_OPTION);

        return options;
    }

    /**
     * Main method
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Setup the command line options
        CommandLine commandLine = CommandLineHelper.startApp(createOptions(), "flatnj",
                "Flat NJ computes flat split networks from quadruple data. To generate quadruples please use GenQS.", args);

        // If we didn't return a command line object then just return.  Probably the user requested help or
        // input invalid args
        if (commandLine == null) {
            return;
        }

        try {
            // Parsing the command line.

            // Required
            File inFile = new File(commandLine.getOptionValue(OPT_INPUT));

            // Optional
            File outFile = commandLine.hasOption(OPT_OUTPUT) ? new File(commandLine.getOptionValue(OPT_OUTPUT)) : new File(DEFAULT_OUTPUT);
            double threshold = commandLine.hasOption(OPT_THRESHOLD) ? Double.parseDouble(commandLine.getOptionValue(OPT_THRESHOLD)) : DEFAULT_THRESHOLD;
            boolean filterSplits = commandLine.hasOption(OPT_FILTER);
            Optimiser optimiser = commandLine.hasOption(OPT_OPTIMISER) ?
                    OptimiserFactory.getInstance().createOptimiserInstance(commandLine.getOptionValue(OPT_OPTIMISER), Objective.ObjectiveType.QUADRATIC) :
                    null;

            readTaxa(inFile.getAbsolutePath());

            if (filterSplits) {
                readSplitsystem(inFile.getAbsolutePath());
                ss.filterSplits(threshold);
                saveSplitSystem(outFile);
            } else {
                readQuadruples(inFile.getAbsolutePath());
                qs.subtractMin();   //Subtract minimal weights. Tey will be
                //added back when the network is computed.
                computeSplitSystem(threshold, optimiser);
                computeNetwork(threshold, optimiser);
                saveNetwork(outFile);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(StringUtils.join(e.getStackTrace(), "\n"));
            System.exit(1);
        }
    }


    /**
     * Reads TAXA block and prints progress messages
     *
     * @param inFile input file
     */
    private static void readTaxa(String inFile) {
        System.err.print(Utilities.addDots("Reading taxa labels ", nDots));
        reader = new NexusReaderTaxa();
        taxa = (Taxa) reader.readBlock(inFile);
        System.err.println(" done.");
    }

    /**
     * Reads QUADRUPLES block and prints progress messages
     *
     * @param inFile input file
     */
    private static void readQuadruples(String inFile) {
        System.err.print(Utilities.addDots("Reading quadruples ", nDots));
        reader = new NexusReaderQuadruples();
        qs = (QuadrupleSystem) reader.readBlock(inFile);
        if (qs == null) {
            System.err.println();
            System.err.println("Error: could not read quadruples from '" +
                    inFile + "'");
            System.exit(0);
        } else {
            System.err.println(" done.");
        }
    }

    /**
     * Reads SPLITS block and prints progress messages
     *
     * @param inFile input file
     */
    private static void readSplitsystem(String inFile) {
        System.err.print(Utilities.addDots("Reading splits ", nDots));
        reader = new NexusReaderSplits();
        ss = (SplitSystem) reader.readBlock(inFile);
        System.err.println(" done.");
    }

    /**
     * Computes flat split system from input quadruple system
     */
    private static void computeSplitSystem(double threshold, Optimiser optimiser) {
        System.err.print(Utilities.addDots("Computing flat split system ",
                nDots));
        PermutationSequenceFactory psf = new PermutationSequenceFactory();
        ps = psf.computePermutationSequence(qs);
        System.err.println(" done.");

        System.err.print(Utilities.addDots("Weighting flat split system ",
                nDots));

        wCalculator = new WeightCalculatorImpl(ps, qs);
        wCalculator.fitWeights(optimiser);

        ps.filterSplits(threshold);

        ps.setTaxaNames(taxa.getTaxaNames());
        ss = new SplitSystemFinal(ps);
        System.err.println(" done.");

    }

    /**
     * Computes planar network for previously computed flat split system
     */
    private static void computeNetwork(double threshold, Optimiser optimiser) {
        if (ps == null) {
            computeSplitSystem(threshold, optimiser);
        }

        System.err.print(Utilities.addDots("Computing network ", nDots));
        System.out.println();
        ps.filterSplits(threshold);

        if (ss == null) {
            ss = new SplitSystemFinal(ps);
            ss.setActive(ps.getActive());
        }

        psDraw = new PermutationSequenceDraw(ps.getSequence(),
                ps.getSwaps(),
                ps.getWeights(),
                ps.getActive(),
                ps.getTrivial());

        net = DrawFlat.drawsplitsystem(psDraw, -1, taxa);

        network = new FlatNetwork(net);


        LayoutOptimizer layoutOptimizer = new LayoutOptimizer();

        net = layoutOptimizer.optimize(net, psDraw, network);
        CompatibleCorrector compatibleCorrectorPrecise =
                new CompatibleCorrector(new AngleCalculatorMaximalArea());
        compatibleCorrectorPrecise.addInnerTrivial(net, psDraw, network);
        if (!network.veryLongTrivial()) {
            Long time1 = System.currentTimeMillis();
            compatibleCorrectorPrecise.moveTrivial(net, 5, null, network);
            System.out.println("\nTime: " + (System.currentTimeMillis() - time1));
        }

        System.out.print(Utilities.addDots("", nDots));
        System.err.println(" done.");
    }

    /**
     * Creates output file with network data.
     */
    private static void saveNetwork(File outputFile) {
        writer = new Writer();
        writer.open(outputFile.getAbsolutePath());
        writeTaxa();
        writeNetwork();
        writer.close();
    }

    /**
     * Creates output file with splits.
     */
    private static void saveSplitSystem(File outputFile) {
        writer = new Writer();
        writer.open(outputFile.getAbsolutePath());
        writeTaxa();
        writeSplits();
        writer.close();
    }

    /**
     * Writes TAXA block to the output file.
     */
    private static void writeTaxa() {
        System.err.print(Utilities.addDots("Writing TAXA block ", nDots));
        writer.write(taxa);
        System.err.println(" done.");
    }

    /**
     * Writes NETWORK block to the output file.
     */
    private static void writeNetwork() {
        System.err.print(Utilities.addDots("Writing NETWORK block ", nDots));
        writer.write(net, ps.getnTaxa(), ps.getCompressed(), taxa);
        System.err.println(" done.");
    }

    /**
     * Writes SPLITS block to the output file.
     */
    private static void writeSplits() {
        System.err.print(Utilities.addDots("Writing SPLITS block ", nDots));
        writer.write(ss);
        System.err.println(" done.");
    }
}
