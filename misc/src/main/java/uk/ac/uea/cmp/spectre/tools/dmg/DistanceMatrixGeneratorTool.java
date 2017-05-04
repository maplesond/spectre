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
        boolean msamode = true;
        File msafile = null;
        int n = 0;
        try {
            n = Integer.parseInt(commandLine.getArgs()[0]);
            msamode = false;
        }
        catch (NumberFormatException e) {
            msafile = new File(commandLine.getArgs()[0]);
        }

        int s = commandLine.hasOption(OPT_SAMPLES) ?
                Integer.parseInt(commandLine.getOptionValue(OPT_SAMPLES)) :
                1;

        DistanceCalculatorFactory dcf = commandLine.hasOption(OPT_DIST_CALC) ?
                DistanceCalculatorFactory.valueOf(commandLine.getOptionValue(OPT_DIST_CALC).toUpperCase().trim()) :
                DistanceCalculatorFactory.JUKES_CANTOR;

        SpectreWriterFactory spectreWriterFactory = commandLine.hasOption(OPT_OUTPUT_TYPE) ?
                SpectreWriterFactory.valueOf(commandLine.getOptionValue(OPT_OUTPUT_TYPE).toUpperCase().trim()) :
                SpectreWriterFactory.PHYLIP;

        SpectreWriter spectreWriter = spectreWriterFactory.create();

        // Create the output directory if required
        outputDir.mkdirs();


        if (msamode) {

            // Load sequences
            SpectreReader sr = SpectreReaderFactory.getInstance().create(FilenameUtils.getExtension(msafile.getName()));
            Sequences seqs = sr.readAlignment(msafile);

            // Create the distance matrix
            DistanceMatrix distanceMatrix = dcf.createDistanceMatrix(seqs);

            // Create a filename for this sample
            File outFile = new File(outputDir, prefix + "." + spectreWriterFactory.getPrimaryExtension());

            // Save to disk
            spectreWriter.writeDistanceMatrix(outFile, distanceMatrix);

        }
        else {
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

    }

    @Override
    public String getName() {
        return "distmatgen";
    }

    @Override
    public String getPosArgs() {
        return "(<nb_taxa>|<msa_file>)";
    }


    @Override
    public String getDescription() {
        return "Generates a distance matrix that takes a single argument as input.  If argument is an integer then this " +
                "tool a random distance matrix given that number of taxa.  If argument is a file containing a MSA, then we " +
                "generate the distance matrix using the specified distance calculator (JUKES_CANTOR by default).";
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
