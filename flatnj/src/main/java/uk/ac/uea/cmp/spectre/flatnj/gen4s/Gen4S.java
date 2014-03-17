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

package uk.ac.uea.cmp.spectre.flatnj.gen4s;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import uk.ac.uea.cmp.spectre.core.ds.Alignment;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.io.fasta.FastaReader;
import uk.ac.uea.cmp.spectre.core.io.nexus.NexusReader;
import uk.ac.uea.cmp.spectre.core.ui.cli.CommandLineHelper;
import uk.ac.uea.cmp.spectre.flatnj.ds.Locations;
import uk.ac.uea.cmp.spectre.flatnj.ds.QuadrupleSystem;
import uk.ac.uea.cmp.spectre.flatnj.ds.SplitSystem;
import uk.ac.uea.cmp.spectre.flatnj.ds.Taxa;
import uk.ac.uea.cmp.spectre.flatnj.tools.*;

import java.io.File;
import java.io.IOException;

/**
 * Main class.
 *
 * @author balvociute
 */
public class Gen4S {

    private static final String OPT_FASTA = "in_fasta";
    private static final String OPT_DIST_MTX = "distance_matrix";
    private static final String OPT_IN_BLOCK = "in_block";
    private static final String OPT_INPUT = "in";
    private static final String OPT_OUTPUT = "out";

    private static final String[] ALLOWED_BLOCKS = new String[]{
            "characters",
            "data",
            "locations",
            "splits"
    };

    private static final String DEFAULT_OUTPUT = "gen4s.out";


    private static Writer writer = new Writer();
    private static QSFactory qsFactory;
    private static Alignment a;
    private static DistanceMatrix dm;
    private static Locations loc;
    private static SplitSystem ss;
    private static Taxa taxa = null;
    private static QuadrupleSystem qs;
    private static int nDots = 40;


    protected static Options createOptions() {

        // create Options object
        Options options = new Options();

        // Options with arguments
        // Options with arguments
        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_INPUT).hasArg()
                .withDescription("Input nexus file.").create("i"));

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_FASTA).hasArg()
                .withDescription("File containing multiple sequence alignment in fasta format.").create("fa"));

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_DIST_MTX).hasArg()
                .withDescription("Character distance matrix for more accurate estimation. Default values for distance matrix " +
                        "are 0 - for identical characters and 1 - for different.").create("dm"));

        options.addOption(OptionBuilder.withArgName("block").withLongOpt(OPT_IN_BLOCK).hasArg()
                .withDescription("Nexus block containing input data. Must be specified when \"-i\" options is used specifying a file in nexus format.  " +
                        "May be one of the following:\n" + StringUtils.join(ALLOWED_BLOCKS, ", ") + ".").create("b"));

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_OUTPUT).hasArg()
                .withDescription("Output file - Default value (\"" + DEFAULT_OUTPUT + "\")").create("o"));

        options.addOption(CommandLineHelper.HELP_OPTION);

        return options;
    }


    /**
     * Main method that reads in input data, computes
     * {@link QuadrupleSystem} and save in the nexus output file.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Setup the command line options
        CommandLine commandLine = CommandLineHelper.startApp(createOptions(), "gen4s",
                "Gen4S computes a system of 4-splits from input data (multiple sequence alignment,\n" +
                        "geographic coordinates or split system) and writes it to the QUADRUPLES block\n" +
                        "in output file.", args);

        // If we didn't return a command line object then just return.  Probably the user requested help or
        // input invalid args
        if (commandLine == null) {
            return;
        }

        try {
            // Parsing the command line.

            // Required
            File outputFile = new File(commandLine.getOptionValue(OPT_OUTPUT));

            // Optional
            File fastaFile = commandLine.hasOption(OPT_FASTA) ? new File(commandLine.getOptionValue(OPT_FASTA)) : null;
            File distanceMatrixFile = commandLine.hasOption(OPT_DIST_MTX) ? new File(commandLine.getOptionValue(OPT_DIST_MTX)) : null;
            File inFile = commandLine.hasOption(OPT_INPUT) ? new File(commandLine.getOptionValue(OPT_INPUT)) : null;
            String inBlock = commandLine.hasOption(OPT_IN_BLOCK) ? commandLine.getOptionValue(OPT_IN_BLOCK) : null;


            if (fastaFile != null) {
                readAlignment(fastaFile);
            }
            if (distanceMatrixFile != null) {
                readDistanceMatrix(distanceMatrixFile);
            }
            if (inFile != null) {

                if (inBlock == null) {
                    throw new IllegalArgumentException("You specified that a nexus file should be input, so you must also " +
                            "specify the specific nexus block to use in that file.");
                }

                String[] taxaLabels = null;
                String blockLowerCase = inBlock.toLowerCase();
                if (blockLowerCase.contentEquals("data") || blockLowerCase.contentEquals("characters")) {
                    taxaLabels = readAlignment(inBlock, inFile);
                } else if (blockLowerCase.contentEquals("locations")) {
                    taxaLabels = readLocations(inFile);
                } else if (blockLowerCase.contentEquals("splits")) {
                    readSplits(inFile);
                }

                if (taxaLabels != null) {
                    taxa = new Taxa(taxaLabels);
                } else {
                    readTaxa(inFile);
                }
            }
            if (taxa == null) {
                exitError("Error: No labels for the taxa were indicated.");
            }

            computeQuadruples();

            writer.open(outputFile.getAbsolutePath());
            writeTaxa();
            writeQuadruples();
            writer.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(StringUtils.join(e.getStackTrace(), "\n"));
            System.exit(1);
        }
    }

    /**
     * Reads alignment from fasta file and initializes {@linkplain Alignment}
     * and {@linkplain Taxa} objects.
     *
     * @param fastaFile fasta file path.
     */
    private static void readAlignment(File fastaFile) throws IOException {
        System.err.print(Utilities.addDots("Reading sequences ", nDots));
        a = new FastaReader().readAlignment(fastaFile);
        if (a.getSequences().length == 0) {
            System.err.println();
            exitError("Error: could not read sequence alignment from '" + fastaFile + "'");
        }
        taxa = new Taxa(a.getTaxaLabels());
        System.err.println(" done.");
    }

    /**
     * Reads alignment from a block in a nexus file and initializes
     * {@linkplain Alignment} object.
     *
     * @param inBlock name of the block that contains alignment. May be either
     *                DATA or CHARACTERS.
     * @param inFile  nexus file path.
     * @return a {@linkplain String} array containing taxa names.
     */
    private static String[] readAlignment(String inBlock, File inFile) {
        String[] taxaLabels;
        System.err.print(Utilities.addDots("Reading sequences ", nDots));
        a = (Alignment) new NexusReaderAlignment(inBlock).readBlock(inFile.getAbsolutePath());
        if (a == null) {
            System.err.println();
            exitError("Error: could not read sequence alignment from '" + inFile + "'");
        }
        taxaLabels = a.getTaxaLabels();
        System.err.println(" done.");
        return taxaLabels;
    }

    /**
     * Reads character distance matrix from DISTANCES block in nexus distance
     * matrix file and initializes {@linkplain uk.ac.uea.cmp.spectre.core.ds.distance.FlexibleDistanceMatrix} object.
     *
     * @param distanceMatrixFile nexus file path.
     */
    private static void readDistanceMatrix(File distanceMatrixFile) throws IOException {
        System.err.print(Utilities.addDots("Reading distance matrix ", nDots));
        dm = new NexusReader().readDistanceMatrix(distanceMatrixFile);
        System.out.println(dm.toString());
        System.err.println(" done.");
    }

    /**
     * Reads locations from LOCATIONS block in nexus input file and initializes
     * {@linkplain Locations} object.
     *
     * @param inFile nexus file path.
     * @return a {@linkplain String} array containing taxa names.
     */
    private static String[] readLocations(File inFile) {
        String[] taxaLabels;
        System.err.print(Utilities.addDots("Reading locations ", nDots));
        loc = (Locations) new NexusReaderLocations().readBlock(inFile.getAbsolutePath());
        taxaLabels = loc.getTaxa();
        System.err.println(" done.");
        return taxaLabels;
    }

    /**
     * Reads splits from SPLITS block in nexus input file and initializes
     * {@linkplain SplitSystem} object.
     *
     * @param inFile nexus file path.
     */
    private static void readSplits(File inFile) throws IOException {
        System.err.print(Utilities.addDots("Reading splits ", nDots));
        ss = (SplitSystem) new NexusReaderSplits().readBlock(inFile.getAbsolutePath());
        System.err.println(" done.");
    }

    /**
     * Reads taxa names from TAXA block in nexus input file and initializes
     * {@linkplain Taxa} object.
     *
     * @param inFile
     */
    private static void readTaxa(File inFile) {
        System.err.print(Utilities.addDots("Reading taxa labels ", nDots));
        taxa = (Taxa) new NexusReaderTaxa().readBlock(inFile.getAbsolutePath());
        System.err.println(" done.");
    }

    /**
     * Computes quadruple system from input data.
     */
    private static void computeQuadruples() {
        System.err.print(Utilities.addDots("Computing system of 4-splits", nDots));
        if (a != null) {
            qsFactory = new QSFactoryAlignment(a, dm);
        } else if (loc != null) {
            qsFactory = new QSFactoryLocation(loc);
        } else if (ss != null) {
            qsFactory = new QSFactorySplitSystem(ss);
        }
        qs = qsFactory.computeQS();
        System.err.println(" done.");
    }

    /**
     * Writes TAXA block to the nexus output file.
     */
    private static void writeTaxa() {
        System.err.print(Utilities.addDots("Writing TAXA block ", nDots));
        writer.write(taxa);
        System.err.println(" done.");
    }

    /**
     * Writes QUADRUPLES block to the nexus output file.
     */
    private static void writeQuadruples() {
        System.err.print(Utilities.addDots("Writing QUADRUPLES block ", nDots));
        writer.write(qs);
        System.err.println(" done.");
    }

    /**
     * Halts the program.
     *
     * @param msg cause for exiting.
     */
    private static void exitError(String msg) {
        System.err.println(msg);
        System.exit(1);
    }
}
