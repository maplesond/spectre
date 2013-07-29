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

package uk.ac.uea.cmp.phygen.tools.rdg;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.phygen.core.ds.distance.RandomDistanceGenerator;
import uk.ac.uea.cmp.phygen.core.io.PhygenWriter;
import uk.ac.uea.cmp.phygen.core.io.PhygenWriterFactory;
import uk.ac.uea.cmp.phygen.core.ui.cli.PhygenTool;
import uk.ac.uea.cmp.phygen.core.util.Time;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA. User: Dan Date: 14/05/13 Time: 21:27 To change this template use File | Settings | File
 * Templates.
 */
public class RandomDistanceGeneratorTool extends PhygenTool {

    private static final String OPT_PREFIX = "prefix";
    private static final String OPT_OUTPUT_DIR = "output";
    private static final String OPT_OUTPUT_TYPE = "output_type";


    @Override
    protected Options createInternalOptions() {

        // Create Options object
        Options options = new Options();

        options.addOption(OptionBuilder.withArgName("integer").hasArg().isRequired()
                .withDescription("The number of taxa for the new distance matrix").create("n"));

        options.addOption(OptionBuilder.withArgName("integer").hasArg()
                .withDescription("The number of samples to generate").create("s"));

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_OUTPUT_DIR).hasArg()
                .withDescription("The directory for the new distance matricies").create("o"));

        options.addOption(OptionBuilder.withArgName("string").withLongOpt(OPT_PREFIX).hasArg()
                .withDescription("The prefix for the output files").create("p"));

        options.addOption(OptionBuilder.withArgName("string").withLongOpt(OPT_OUTPUT_TYPE).hasArg()
                .withDescription("The output file type: " + PhygenWriterFactory.listWriters()).create("t"));

        return options;
    }

    @Override
    protected void execute(CommandLine commandLine) throws IOException {

        // Get the arguments
        int n = Integer.parseInt(commandLine.getOptionValue("n"));
        int s = commandLine.hasOption("s") ? Integer.parseInt(commandLine.getOptionValue("s")) : 1;
        File outputDir = commandLine.hasOption(OPT_OUTPUT_DIR) ? new File(commandLine.getOptionValue(OPT_OUTPUT_DIR)) : new File(".");
        String prefix = commandLine.hasOption(OPT_PREFIX) ? commandLine.getOptionValue(OPT_PREFIX) : "rdg-" + Time.createTimestamp();
        PhygenWriterFactory phygenWriterFactory = commandLine.hasOption(OPT_OUTPUT_TYPE) ?
                PhygenWriterFactory.valueOf(commandLine.getOptionValue(OPT_OUTPUT_TYPE)) :
                PhygenWriterFactory.PHYLIP;
        PhygenWriter phygenWriter = phygenWriterFactory.create();

        // Create the output directory if required
        outputDir.mkdirs();

        // For each sample
        for(int i = 1; i <= s; i++) {

            // Create the distance matrix
            DistanceMatrix distanceMatrix = new RandomDistanceGenerator().generateDistances(n);

            // Create a filename for this sample
            File outFile = new File(outputDir, prefix + "-" + i + "." + phygenWriterFactory.getPrimaryExtension());

            // Save to disk
            phygenWriter.writeDistanceMatrix(outFile, distanceMatrix);
        }


    }
}
