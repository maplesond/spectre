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

package uk.ac.uea.cmp.spectre.tools.dmg;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.MetaInfServices;
import uk.ac.uea.cmp.spectre.core.ds.Sequences;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceCalculatorFactory;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.distance.RandomDistanceGenerator;
import uk.ac.uea.cmp.spectre.core.io.SpectreReader;
import uk.ac.uea.cmp.spectre.core.io.SpectreReaderFactory;
import uk.ac.uea.cmp.spectre.core.io.SpectreWriter;
import uk.ac.uea.cmp.spectre.core.io.SpectreWriterFactory;
import uk.ac.uea.cmp.spectre.core.io.emboss.EmbossReader;
import uk.ac.uea.cmp.spectre.core.io.fasta.FastaReader;
import uk.ac.uea.cmp.spectre.core.io.nexus.Nexus;
import uk.ac.uea.cmp.spectre.core.io.nexus.NexusReader;
import uk.ac.uea.cmp.spectre.core.io.phylip.PhylipReader;
import uk.ac.uea.cmp.spectre.core.util.Time;
import uk.ac.uea.cmp.spectre.tools.SpectreTool;

import java.io.File;
import java.io.IOException;

@MetaInfServices
public class DistanceMatrixGeneratorTool extends SpectreTool {

    private static final String OPT_PREFIX = "output_prefix";
    private static final String OPT_OUTPUT_TYPE = "output_type";
    private static final String OPT_SAMPLES = "samples";
    private static final String OPT_DIST_CALC = "dist_calc";

    private enum Mode {
        RANDOM,
        MSA,
        CONVERT
    }

    @Override
    protected Options createInternalOptions() {

        // Create Options object
        Options options = new Options();

        options.addOption(OptionBuilder.withArgName("integer").withLongOpt(OPT_SAMPLES).hasArg()
                .withDescription("The number of samples to generate (only used if generating random distance matrices)").create("s"));

        options.addOption(OptionBuilder.withArgName("string").withLongOpt(OPT_PREFIX).hasArg()
                .withDescription("The prefix for the output files").create("o"));

        options.addOption(OptionBuilder.withArgName("string").withLongOpt(OPT_OUTPUT_TYPE).hasArg()
                .withDescription("The output file type: " + SpectreWriterFactory.listWriters() + ". Default: PHYLIP").create("t"));

        options.addOption(OptionBuilder.withArgName("dist_calc").withLongOpt(OPT_DIST_CALC).hasArg()
                .withDescription("The distance calculator to use (only used if generating a distance matrix from MSA file).  Options: " +
                        DistanceCalculatorFactory.toListString() + ". Default: " + DistanceCalculatorFactory.JUKES_CANTOR.name()).create("dc"));

        return options;
    }

    @Override
    protected void execute(CommandLine commandLine) throws IOException {

        File outputDir = new File(".");
        String prefix = this.getName() + "-" + Time.createTimestamp();

        if (commandLine.hasOption(OPT_PREFIX)) {
            File op = new File(commandLine.getOptionValue(OPT_PREFIX));
            if (op.getParentFile() != null) {
                outputDir = op.getParentFile();
            }
            prefix = op.getName();
        }

        if (commandLine.getArgs().length == 0) {
            throw new IOException("Did not specify number of taxa.");
        }
        else if (commandLine.getArgs().length > 1) {
            throw new IOException("Only expected a single positive integer to represent number of taxa.");
        }

        // Get the arguments
        Mode mode = Mode.RANDOM;
        File inputfile = null;
        int n = 0;
        try {
            n = Integer.parseInt(commandLine.getArgs()[0]);
        }
        catch (NumberFormatException e) {
            inputfile = new File(commandLine.getArgs()[0]);
            if (!inputfile.exists()) {
                throw new IOException("Input file does not exist: " + inputfile.getAbsolutePath());
            }
            SpectreReader sr = SpectreReaderFactory.getInstance().create(FilenameUtils.getExtension(inputfile.getName()));
            if (sr instanceof FastaReader) {
                mode = Mode.MSA;
            }
            else if (sr instanceof EmbossReader || sr instanceof PhylipReader) {
                mode = Mode.CONVERT;
            }
            else if (sr instanceof NexusReader) {
                Nexus nexus = ((NexusReader) sr).parse(inputfile);
                if (nexus.getAlignments() != null) {
                    mode = Mode.MSA;
                }
                else if (nexus.getDistanceMatrix() != null) {
                    mode = Mode.CONVERT;
                }
                else {
                    throw new IOException("Input nexus file does not contain either an MSA or a distance matrix.");
                }
            }

        }

        int s = commandLine.hasOption(OPT_SAMPLES) ?
                Integer.parseInt(commandLine.getOptionValue(OPT_SAMPLES)) :
                1;

        DistanceCalculatorFactory dcf = commandLine.hasOption(OPT_DIST_CALC) ?
                DistanceCalculatorFactory.valueOf(commandLine.getOptionValue(OPT_DIST_CALC).toUpperCase().trim()) :
                DistanceCalculatorFactory.JUKES_CANTOR;

        SpectreWriterFactory spectreWriterFactory = commandLine.hasOption(OPT_OUTPUT_TYPE) ?
                SpectreWriterFactory.valueOf(commandLine.getOptionValue(OPT_OUTPUT_TYPE).toUpperCase().trim()) :
                SpectreWriterFactory.NEXUS;

        SpectreWriter spectreWriter = spectreWriterFactory.create();

        // Create the output directory if required
        outputDir.mkdirs();


        if (mode == Mode.MSA) {

            // Load sequences
            SpectreReader sr = SpectreReaderFactory.getInstance().create(FilenameUtils.getExtension(inputfile.getName()));
            Sequences seqs = sr.readAlignment(inputfile);

            // Create the distance matrix
            DistanceMatrix distanceMatrix = dcf.createDistanceMatrix(seqs);

            // Create a filename for this sample
            File outFile = new File(outputDir, prefix + "." + spectreWriterFactory.getPrimaryExtension());

            // Save to disk
            spectreWriter.writeDistanceMatrix(outFile, distanceMatrix);

        }
        else if (mode == Mode.RANDOM) {
            // For each sample
            for (int i = 1; i <= s; i++) {

                // Create the distance matrix
                DistanceMatrix distanceMatrix = new RandomDistanceGenerator().generateDistances(n);

                // Create a filename for this sample
                String fname = s > 1 ? prefix + "-" + i + "." + spectreWriterFactory.getPrimaryExtension() : prefix + "." + spectreWriterFactory.getPrimaryExtension();
                File outFile = new File(outputDir, fname);

                // Save to disk
                spectreWriter.writeDistanceMatrix(outFile, distanceMatrix);
            }
        }
        else if (mode == Mode.CONVERT) {
            // Load dist mat
            SpectreReader sr = SpectreReaderFactory.getInstance().create(FilenameUtils.getExtension(inputfile.getName()));
            DistanceMatrix distanceMatrix = sr.readDistanceMatrix(inputfile);

            // Create a filename for this sample
            File outFile = new File(outputDir, prefix + "." + spectreWriterFactory.getPrimaryExtension());
            spectreWriter.writeDistanceMatrix(outFile, distanceMatrix);
        }
        else {
            throw new IllegalStateException("Unknown mode");
        }

    }

    @Override
    public String getName() {
        return "distmatgen";
    }

    @Override
    public String getPosArgs() {
        return "(<nb_taxa>|<msa_file>|<distmat_file>)";
    }


    @Override
    public String getDescription() {
        return "Generates a distance matrix that takes a single argument as input.  If argument is an integer then this " +
                "tool a random distance matrix given that number of taxa.  If argument is a file containing a MSA, then we " +
                "generate the distance matrix using the specified distance calculator (JUKES_CANTOR by default).  If the " +
                "argument is a file and contains an existing distance matrix then we convert to the specified format.\n " +
                "Note: If input file is a nexus file containing both an MSA and distance matrix then we assume we want to generate " +
                "a distance matrix from the MSA.\n" +
                "Supported input file formats are: emboss (distmat), phylip, nexus, fasta.";
    }

    public static void main(String[] args) {

        try {
            new DistanceMatrixGeneratorTool().execute(args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(StringUtils.join(e.getStackTrace(), "\n"));
            System.exit(1);
        }
    }
}
