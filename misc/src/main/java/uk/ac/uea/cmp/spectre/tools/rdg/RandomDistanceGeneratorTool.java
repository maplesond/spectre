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

package uk.ac.uea.cmp.spectre.tools.rdg;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.MetaInfServices;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.distance.RandomDistanceGenerator;
import uk.ac.uea.cmp.spectre.core.io.SpectreWriter;
import uk.ac.uea.cmp.spectre.core.io.SpectreWriterFactory;
import uk.ac.uea.cmp.spectre.core.util.Time;
import uk.ac.uea.cmp.spectre.tools.SpectreTool;

import java.io.File;
import java.io.IOException;

@MetaInfServices
public class RandomDistanceGeneratorTool extends SpectreTool {

    private static final String OPT_PREFIX = "output_prefix";
    private static final String OPT_OUTPUT_TYPE = "output_type";
    private static final String OPT_SAMPLES = "samples";


    @Override
    protected Options createInternalOptions() {

        // Create Options object
        Options options = new Options();

        options.addOption(OptionBuilder.withArgName("integer").withLongOpt(OPT_SAMPLES).hasArg()
                .withDescription("The number of samples to generate").create("s"));

        options.addOption(OptionBuilder.withArgName("string").withLongOpt(OPT_PREFIX).hasArg()
                .withDescription("The prefix for the output files").create("o"));

        options.addOption(OptionBuilder.withArgName("string").withLongOpt(OPT_OUTPUT_TYPE).hasArg()
                .withDescription("The output file type: " + SpectreWriterFactory.listWriters()).create("t"));

        return options;
    }

    @Override
    protected void execute(CommandLine commandLine) throws IOException {

        File outputDir = new File("");
        String prefix = "rdg-" + Time.createTimestamp();

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
        int n = Integer.parseInt(commandLine.getArgs()[0]);
        int s = commandLine.hasOption("s") ? Integer.parseInt(commandLine.getOptionValue("s")) : 1;
        SpectreWriterFactory spectreWriterFactory = commandLine.hasOption(OPT_OUTPUT_TYPE) ?
                SpectreWriterFactory.valueOf(commandLine.getOptionValue(OPT_OUTPUT_TYPE)) :
                SpectreWriterFactory.PHYLIP;
        SpectreWriter spectreWriter = spectreWriterFactory.create();

        // Create the output directory if required
        outputDir.mkdirs();

        // For each sample
        for (int i = 1; i <= s; i++) {

            // Create the distance matrix
            DistanceMatrix distanceMatrix = new RandomDistanceGenerator().generateDistances(n);

            // Create a filename for this sample
            File outFile = new File(outputDir, prefix + "-" + i + "." + spectreWriterFactory.getPrimaryExtension());

            // Save to disk
            spectreWriter.writeDistanceMatrix(outFile, distanceMatrix);
        }


    }

    @Override
    public String getName() {
        return "rdg";
    }

    @Override
    public String getPosArgs() {
        return "<nb_taxa>";
    }


    @Override
    public String getDescription() {
        return "Creates a random distance matrix of a given number of taxa.";
    }

    public static void main(String[] args) {

        try {
            new RandomDistanceGeneratorTool().execute(args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(StringUtils.join(e.getStackTrace(), "\n"));
            System.exit(1);
        }
    }
}
