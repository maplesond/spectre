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
import org.apache.commons.io.FilenameUtils;
import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.phygen.core.ds.distance.RandomDistanceGenerator;
import uk.ac.uea.cmp.phygen.core.io.PhygenWriter;
import uk.ac.uea.cmp.phygen.core.io.PhygenWriterFactory;
import uk.ac.uea.cmp.phygen.core.io.phylip.PhylipWriter;
import uk.ac.uea.cmp.phygen.tools.PhygenTool;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA. User: Dan Date: 14/05/13 Time: 21:27 To change this template use File | Settings | File
 * Templates.
 */
public class RandomDistanceGeneratorTool extends PhygenTool {

    public static final String OPT_OUTPUT = "output";
    public static final String OPT_OUTPUT_TYPE = "output_type";


    @Override
    protected Options createInternalOptions() {

        // Create Options object
        Options options = new Options();

        options.addOption(OptionBuilder.withArgName("integer").hasArg().isRequired()
                .withDescription("The number of taxa for the new distance matrix").create("n"));

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_OUTPUT).hasArg().isRequired()
                .withDescription("The file name for the new distance matrix").create("o"));

        options.addOption(OptionBuilder.withArgName("string").withLongOpt(OPT_OUTPUT_TYPE).hasArg()
                .withDescription("The output file type: " + PhygenWriterFactory.listWriters()).create("t"));

        return options;
    }

    @Override
    protected void execute(CommandLine commandLine) throws IOException {

        // Get the arguments
        int n = Integer.parseInt(commandLine.getOptionValue("n"));
        File outFile = new File(commandLine.getOptionValue(OPT_OUTPUT));
        PhygenWriter phygenWriter = determinePhygenWriter(commandLine, outFile);

        // Create the distance matrix
        DistanceMatrix distanceMatrix = new RandomDistanceGenerator().generateDistances(n);

        // Save to disk
        phygenWriter.writeDistanceMatrix(outFile, distanceMatrix);
    }

    protected PhygenWriter determinePhygenWriter(CommandLine commandLine, File outputFile) {

        // First set if specified at the command line
        PhygenWriter phygenWriter = commandLine.hasOption(OPT_OUTPUT_TYPE) ?
                PhygenWriterFactory.create(commandLine.getOptionValue(OPT_OUTPUT_TYPE)) :
                null;

        // If not on the command line guess from the output file
        if (phygenWriter == null) {
            phygenWriter = PhygenWriterFactory.create(FilenameUtils.getExtension(outputFile.getName()));
        }

        // If still don't know what the extension is then assume we want Phylip
        if (phygenWriter == null) {
            phygenWriter = new PhylipWriter();
        }

        return phygenWriter;
    }
}
