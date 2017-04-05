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

package uk.ac.uea.cmp.spectre.flatnj;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.earlham.metaopt.Objective;
import uk.ac.earlham.metaopt.Optimiser;
import uk.ac.earlham.metaopt.OptimiserFactory;
import uk.ac.earlham.metaopt.external.JOptimizer;
import uk.ac.uea.cmp.spectre.core.ds.Sequences;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.network.FlatNetwork;
import uk.ac.uea.cmp.spectre.core.ds.network.Network;
import uk.ac.uea.cmp.spectre.core.ds.network.Vertex;
import uk.ac.uea.cmp.spectre.core.ds.network.draw.AngleCalculatorMaximalArea;
import uk.ac.uea.cmp.spectre.core.ds.network.draw.CompatibleCorrector;
import uk.ac.uea.cmp.spectre.core.ds.network.draw.PermutationSequenceDraw;
import uk.ac.uea.cmp.spectre.core.ds.quad.quadruple.QuadrupleSystem;
import uk.ac.uea.cmp.spectre.core.ds.split.flat.FlatSplitSystem;
import uk.ac.uea.cmp.spectre.core.ds.split.flat.FlatSplitSystemFinal;
import uk.ac.uea.cmp.spectre.core.ds.split.flat.PermutationSequence;
import uk.ac.uea.cmp.spectre.core.ds.split.flat.PermutationSequenceFactory;
import uk.ac.uea.cmp.spectre.core.io.fasta.FastaReader;
import uk.ac.uea.cmp.spectre.core.ui.cli.CommandLineHelper;
import uk.ac.uea.cmp.spectre.flatnj.tools.*;

import java.io.File;
import java.io.IOException;

/**
 * FlatNJ (FlatNetJoining) is a program for computing split networks that allow
 * for interior vertices to be labeled while being (almost) planar.
 *
 * @author balvociute
 */
public class FlatNJ {

    private static Logger log = LoggerFactory.getLogger(FlatNJ.class);

    private static final String OPT_OUTPUT = "output";
    private static final String OPT_NEXUS_BLOCK = "nexus_block";
    private static final String OPT_THRESHOLD = "threshold";
    private static final String OPT_OPTIMISER = "optimiser";
    private static final String OPT_SAVE_STAGES = "save_stages";
    private static final String OPT_VERBOSE = "verbose";

    private static final double DEFAULT_THRESHOLD = 0.15;
    private static final String DEFAULT_OUTPUT = "flatnj.nex";

    private static final String[] ALLOWED_BLOCKS = new String[]{
            "characters",
            "data",
            "distances",
            "locations",
            "splits",
            "quadruples"
    };

    private File inFile;
    private File outFile;
    private String nexusBlock;
    private double threshold;
    private Optimiser optimiser;
    private boolean saveStages;

    public FlatNJ(File inFile, File outFile, Optimiser optimiser) {
        this.inFile = inFile;
        this.outFile = outFile;
        this.nexusBlock = null;
        this.threshold = DEFAULT_THRESHOLD;
        this.optimiser = optimiser;
        this.saveStages = false;
    }

    public File getInFile() {
        return inFile;
    }

    public void setInFile(File inFile) {
        this.inFile = inFile;
    }

    public File getOutFile() {
        return outFile;
    }

    public void setOutFile(File outFile) {
        this.outFile = outFile;
    }

    public String getNexusBlock() {
        return nexusBlock;
    }

    public void setNexusBlock(String nexusBlock) {
        this.nexusBlock = nexusBlock;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public Optimiser getOptimiser() {
        return optimiser;
    }

    public void setOptimiser(Optimiser optimiser) {
        this.optimiser = optimiser;
    }

    public boolean isSaveStages() {
        return saveStages;
    }

    public void setSaveStages(boolean saveStages) {
        this.saveStages = saveStages;
    }

    public Result execute() throws IOException {
        log.info("Loading input data from: " + inFile);

        // Work out input file type
        String extension = FilenameUtils.getExtension(inFile.getName());

        IdentifierList taxa = null;
        Sequences sequences = null;
        DistanceMatrix distanceMatrix = null;
        Locations locations = null;
        FlatSplitSystem ss = null;
        QuadrupleSystem qs = null;

        if (extension.equalsIgnoreCase("fa") || extension.equalsIgnoreCase("faa") || extension.equalsIgnoreCase("fasta")) {
            sequences = readAlignment(inFile);
            taxa = new IdentifierList(sequences.getTaxaLabels());
        }
        else if (extension.equalsIgnoreCase("nex") || extension.equalsIgnoreCase("nexus") || extension.equalsIgnoreCase("4s")) {

            // Read taxa block regardless
            taxa = readTaxa(inFile.getAbsolutePath());

            if (taxa == null) {
                throw new IOException("No labels for the taxa were indicated.");
            }

            if (nexusBlock == null) {
                log.info("Nexus file provided as input but no nexus block specified by user.  Will use first suitable block found in nexus file.");
                // First check for existing quadruple system
                qs = readQuadruples(inFile.getAbsolutePath());
                if (qs != null) {
                    log.info("Detected and loaded Quadruples Block.");
                }
                else {
                    // Next check for location data
                    locations = readLocations(inFile);
                    if (locations != null) {
                        log.info("Detected and loaded Locations Block.");
                    }
                    else {
                        // Next check for split system
                        ss = readSplitSystem(inFile);
                        if (ss != null) {
                            log.info("Detected and loaded Split System Block.");
                        }
                        else {
                            // Next look for MSA
                            sequences = readNexusAlignment(inFile);
                            if (sequences != null) {
                                log.info("Detected and loaded Sequences Block.");
                            }
                            else {
                                throw new IOException("Couldn't find a valid block in nexus file.");
                            }
                        }
                    }
                }
            }
            else {
                String blockLowerCase = this.nexusBlock.toLowerCase();

                log.info("Searching for " + blockLowerCase + " in nexus file.");
                boolean loaded = false;

                if (blockLowerCase.contentEquals("data") || blockLowerCase.contentEquals("characters")) {
                    sequences = readNexusAlignment(inFile);
                    loaded = true;
                } else if (blockLowerCase.contentEquals("locations")) {
                    locations = readLocations(inFile);
                    loaded = true;
                } else if (blockLowerCase.contentEquals("splits")) {
                    ss = readSplitSystem(inFile);
                    loaded = true;
                } else if (blockLowerCase.contentEquals("quadruples")) {
                    qs = readQuadruples(inFile.getAbsolutePath());
                    loaded = true;
                }

                if (!loaded) {
                    throw new IOException("Couldn't loaded requested block from nexus file.");
                }
            }
        }

        // Compute the Quadruple system from alternate information if we didn't just load it from disk
        if (qs == null) {

            log.info("Computing system of 4-splits (quadruples)");

            QSFactory qsFactory = null;

            if (sequences != null) {
                qsFactory = new QSFactoryAlignment(sequences, distanceMatrix);
            } else if (locations != null) {
                qsFactory = new QSFactoryLocation(locations);
            } else if (ss != null) {
                qsFactory = new QSFactorySplitSystem(ss);
            } else {
                throw new IOException("No suitable data found to create quadruple system");
            }

            if (qsFactory == null) {
                throw new IOException("Error creating quadruple system factory");
            }

            qs = qsFactory.computeQS();

            if (this.saveStages) {

                File quadFile = new File(this.outFile.getParentFile(), this.outFile.getName() + ".quads.nex");
                log.info("Saving quadruples to: " + quadFile.getAbsolutePath());
                Writer writer = new Writer();
                writer.open(quadFile.getAbsolutePath());
                writer.write(taxa);
                writer.write(qs);
                writer.close();
            }
        }

        qs.subtractMin();   //Subtract minimal weights. They will be added back when the network is computed.

        log.info("Computing flat split system");
        PermutationSequence ps = new PermutationSequenceFactory().computePermutationSequence(qs);

        // Updates Permutation Sequence permutationSequence
        log.info("Weighting flat split system");
        new WeightCalculatorImpl(ps, qs).fitWeights(optimiser);

        log.info("Filtering splits below threshold: " + threshold);
        ps.filterSplits(threshold);

        log.debug("Finalising splits system and setting active splits");
        ps.setTaxaNames(taxa.getNames());
        ss = new FlatSplitSystemFinal(ps);
        //ss.setActive(ps.getActive());  // Do we want to reset this from active (extra trivial splits would have been added in the constructor)

        if (this.saveStages) {
            File ssFile = new File(this.outFile.getParentFile(), this.outFile.getName() + ".splits.nex");
            log.info("Saving splits to: " + ssFile.getAbsolutePath());
            Writer writer = new Writer();
            writer.open(ssFile.getAbsolutePath());
            writer.write(taxa);
            writer.write(ss);
            writer.close();
        }

        log.info("Computing network");
        PermutationSequenceDraw psDraw = new PermutationSequenceDraw(ps.getSequence(),
                ps.getSwaps(),
                ps.getWeights(),
                ps.getActive(),
                ps.getTrivial());

        log.debug("Drawing split system");
        Vertex net = psDraw.drawSplitSystem(-1.0);

        Network network = new FlatNetwork(net);
        log.info("Optimising network layout");
        net = net.optimiseLayout(psDraw, network);

        log.debug("Correcting compatible splits");
        CompatibleCorrector compatibleCorrectorPrecise = new CompatibleCorrector(new AngleCalculatorMaximalArea());
        compatibleCorrectorPrecise.addInnerTrivial(net, psDraw, network);

        if (!network.veryLongTrivial()) {
            log.debug("Correcting trivial splits");
            compatibleCorrectorPrecise.moveTrivial(net, 5, network);
        }

        if (this.saveStages) {
            File netFile = new File(this.outFile.getParentFile(), this.outFile.getName() + ".network.nex");
            log.info("Saving network to: " + netFile.getAbsolutePath());
            Writer writer = new Writer();
            writer.open(netFile.getAbsolutePath());
            writer.write(taxa);
            writer.write((FlatNetwork)network, taxa);
            writer.close();
        }

        return new Result(taxa, qs, ps, ss, net);
    }


    protected static Options createOptions() {

        // create Options object
        Options options = new Options();

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_OUTPUT).hasArg()
                .withDescription("Output file - Default value (\"" + DEFAULT_OUTPUT + "\")").create("o"));

        options.addOption(OptionBuilder.withArgName("string").withLongOpt(OPT_NEXUS_BLOCK).hasArg()
                .withDescription("If input file is a nexus file, then the user must specify which block in the file to use as input." +
                        "Allowed blocks:\n" + StringUtils.join(ALLOWED_BLOCKS, ", ") + ". It is strongly recommended" +
                        "that you specify this option if processing a nexus file, otherwise FlatNJ will process the first suitable block.").create("n"));

        options.addOption(OptionBuilder.withArgName("double").withLongOpt(OPT_THRESHOLD).hasArg()
                .withDescription("Filtering threshold, i.e. minimal length ratio allowed for two incompatible splits. Default value (" + DEFAULT_THRESHOLD + ")").create("t"));

        options.addOption(OptionBuilder.withArgName("string").withLongOpt(OPT_OPTIMISER).hasArg()
                .withDescription("The optimiser to use: " + OptimiserFactory.getInstance().listOperationalOptimisers() + " - Default value (JOptimizer)").create("p"));

        options.addOption(OptionBuilder.withLongOpt(OPT_SAVE_STAGES)
                .withDescription("Output nexus files at all stages in the pipeline.  Will use output file name with additional suffix for intermediary stages - Default: false").create("a"));

        options.addOption(OptionBuilder.withLongOpt(OPT_VERBOSE)
                .withDescription("Whether to output detailed logging information").create("v"));

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
        CommandLine commandLine = CommandLineHelper.startApp(createOptions(), "flatnj [options] <input>",
                "Flat NJ computes flat split networks from quadruple data.  Flat NJ also can convert multiple sequence alignment or location data into quadruple data internally.",
                args);

        // If we didn't return a command line object then just return.  Probably the user requested help or
        // input invalid args
        if (commandLine == null) {
            return;
        }

        try {

            BasicConfigurator.configure(new ConsoleAppender(new PatternLayout("%d{HH:mm:ss} %p: %m%n")));
            LogManager.getRootLogger().setLevel(commandLine.hasOption(OPT_VERBOSE) ? Level.DEBUG : Level.INFO);

            // Parsing the command line.
            log.info("Running Flat Net Joining Algorithm");
            log.debug("Parsing command line options");

            if (commandLine.getArgs().length == 0) {
                throw new IOException("No input file specified.");
            }
            else if (commandLine.getArgs().length > 1) {
                throw new IOException("Only expected a single input file.");
            }


            // Required
            File inFile = new File(commandLine.getArgs()[0]);
            Optimiser optimiser = commandLine.hasOption(OPT_OPTIMISER) ?
                    OptimiserFactory.getInstance().createOptimiserInstance(commandLine.getOptionValue(OPT_OPTIMISER), Objective.ObjectiveType.QUADRATIC) :
                    new JOptimizer();

            if (optimiser == null) {
                throw new RuntimeException("Error initialising optimiser");
            }
            else {
                log.info("Initialised " + optimiser.getIdentifier());
            }

            // Optional
            File outFile = commandLine.hasOption(OPT_OUTPUT) ? new File(commandLine.getOptionValue(OPT_OUTPUT)) : new File(DEFAULT_OUTPUT);
            String nexusBlock = commandLine.hasOption(OPT_NEXUS_BLOCK) ? commandLine.getOptionValue(OPT_NEXUS_BLOCK) : null;
            double threshold = commandLine.hasOption(OPT_THRESHOLD) ? Double.parseDouble(commandLine.getOptionValue(OPT_THRESHOLD)) : DEFAULT_THRESHOLD;
            boolean saveStages = commandLine.hasOption(OPT_SAVE_STAGES);

            log.debug("Command line options were interpreted as follows:\n" +
                    "\tInput File: " + inFile.getAbsolutePath() + "\n" +
                    "\tOutput File: " + outFile.getAbsolutePath() + "\n" +
                    "\tNexus block: " + (nexusBlock != null ? nexusBlock : "N/A") + "\n" +
                    "\tThreshold value: " + threshold + "\n" +
                    "\tOptimizer: " + optimiser.getIdentifier() + "\n");

            FlatNJ flatNJ = new FlatNJ(inFile, outFile, optimiser);
            flatNJ.setNexusBlock(nexusBlock);
            flatNJ.setThreshold(threshold);
            flatNJ.setSaveStages(saveStages);

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            Result result = flatNJ.execute();
            stopWatch.stop();
            log.info("FlatNJ completed in: " + result.toString());

            log.info("Saving complete nexus file to: " + outFile.getAbsolutePath());
            result.save(outFile);

            log.info("FlatNJ completed successfully");

        } catch (Exception e) {
            System.err.println("\nException: " + e.toString());
            System.err.println("\nStack trace:");
            System.err.println(StringUtils.join(e.getStackTrace(), "\n"));
            System.exit(1);
        }
    }

    /**
     * Reads TAXA block and prints progress messages
     *
     * @param inFile input file
     */
    protected IdentifierList readTaxa(String inFile) {
        log.debug("Reading taxa labels");
        NexusReader reader = new NexusReaderTaxa();
        return (IdentifierList) reader.readBlock(inFile);
    }

    /**
     * Reads alignment from fasta file and initializes {@linkplain Sequences}
     * and {@linkplain uk.ac.uea.cmp.spectre.core.ds.IdentifierList} objects.
     *
     * @param fastaFile fasta file path.
     */
    protected Sequences readAlignment(File fastaFile) throws IOException {
        log.debug("Reading sequences");
        Sequences a = new FastaReader().readAlignment(fastaFile);
        if (a.getSequences().length == 0) {
            throw new IOException("Could not read sequence alignment from '" + fastaFile + "'");
        }
        return a;
    }

    /**
     * Reads MSAs from a nexus file character block {@linkplain Sequences}
     * and {@linkplain uk.ac.uea.cmp.spectre.core.ds.IdentifierList} objects.
     *
     * @param msaNexusFile nexus file containing character block.
     */
    protected Sequences readNexusAlignment(File msaNexusFile) throws IOException {
        log.debug("Reading sequences");
        Sequences a = new uk.ac.uea.cmp.spectre.core.io.nexus.NexusReader().readAlignment(msaNexusFile);
        if (a.getSequences().length == 0) {
            throw new IOException("Could not read sequence alignments from '" + msaNexusFile.getAbsolutePath() + "'");
        }
        return a;
    }

    /**
     * Reads character distance matrix from DISTANCES block in nexus distance
     * matrix file and initializes {@linkplain uk.ac.uea.cmp.spectre.core.ds.distance.FlexibleDistanceMatrix} object.
     *
     * @param distanceMatrixFile nexus file path.
     */
    protected DistanceMatrix readDistanceMatrix(File distanceMatrixFile) throws IOException {
        log.debug("Reading distance matrix");
        return new uk.ac.uea.cmp.spectre.core.io.nexus.NexusReader().readDistanceMatrix(distanceMatrixFile);
    }

    /**
     * Reads locations from LOCATIONS block in nexus input file and initializes
     * {@linkplain Locations} object.
     *
     * @param inFile nexus file path.
     * @return a {@linkplain String} array containing taxa names.
     */
    protected Locations readLocations(File inFile) {
        log.debug("Reading locations");
        Locations loc = (Locations) new NexusReaderLocations().readBlock(inFile.getAbsolutePath());
        return loc;
    }

    /**
     * Reads QUADRUPLES block and prints progress messages
     *
     * @param inFile input file
     */
    protected QuadrupleSystem readQuadruples(String inFile) throws IOException {
        log.debug("Reading quadruples");
        NexusReader reader = new NexusReaderQuadruples();
        QuadrupleSystem qs = (QuadrupleSystem) reader.readBlock(inFile);
        if (qs == null) {
            throw new IOException("Could not read quadruples from " + inFile);
        }
        return qs;
    }

    /**
     * Reads SPLITS block and prints progress messages
     *
     * @param inFile input file
     */
    protected FlatSplitSystem readSplitSystem(File inFile) {
        log.debug("Reading splits");
        NexusReader reader = new NexusReaderSplits();
        return (FlatSplitSystem) reader.readBlock(inFile.getAbsolutePath());
    }



    public static class Result {
        private IdentifierList taxa;
        private QuadrupleSystem quadrupleSystem;
        private PermutationSequence permutationSequence;
        private FlatSplitSystem splitSystem;
        private Vertex network;

        public Result(IdentifierList taxa, QuadrupleSystem quadrupleSystem, PermutationSequence permutationSequence, FlatSplitSystem splitSystem, Vertex network) {
            this.taxa = taxa;
            this.quadrupleSystem = quadrupleSystem;
            this.permutationSequence = permutationSequence;
            this.splitSystem = splitSystem;
            this.network = network;
        }

        public IdentifierList getTaxa() {
            return taxa;
        }

        public QuadrupleSystem getQuadrupleSystem() {
            return quadrupleSystem;
        }

        public PermutationSequence getPermutationSequence() {
            return permutationSequence;
        }

        public FlatSplitSystem getSplitSystem() {
            return splitSystem;
        }

        public Vertex getNetwork() {
            return network;
        }

        /**
         * Creates output file with network data.
         */
        public void save(File outputFile) throws IOException {

            Writer writer = new Writer();
            writer.open(outputFile.getAbsolutePath());
            writer.write(taxa);
            writer.write(splitSystem);
            writer.write(network, permutationSequence.getnTaxa(), permutationSequence.getCompressed(), taxa);
            writer.close();
        }
    }
}
