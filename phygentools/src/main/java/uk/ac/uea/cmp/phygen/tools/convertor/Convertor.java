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

package uk.ac.uea.cmp.phygen.tools.convertor;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FilenameUtils;
import org.kohsuke.MetaInfServices;
import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.phygen.core.io.*;
import uk.ac.uea.cmp.phygen.tools.PhygenTool;

import java.io.File;
import java.io.IOException;

/**
 * Converts phylip to nexus and vice versa
 */
@MetaInfServices
public class Convertor extends PhygenTool {

    private static final String OPT_INPUT = "input";
    private static final String OPT_OUTPUT = "output";
    public static final String OPT_DISTANCES_FILE_TYPE = "distances_file_type";

    @Override
    protected Options createInternalOptions() {

        // Create Options object
        Options options = new Options();

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_INPUT).hasArg().isRequired()
                .withDescription("The file to convert, must be either nexus or phylip format.").create("i"));

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_OUTPUT).hasArg().isRequired()
                .withDescription("The converted file.").create("o"));

        options.addOption(OptionBuilder.withArgName("string").withLongOpt(OPT_DISTANCES_FILE_TYPE).hasArg()
                .withDescription("The file type of the input distance data file: " + PhygenReaderFactory.getInstance().getPhygenReaders(PhygenDataType.DISTANCE_MATRIX) +
                        ".  Use this if your input file has a non-standard extension.").create("t"));


        return options;
    }

    @Override
    protected void execute(CommandLine commandLine) throws IOException {

        File inputFile = new File(commandLine.getOptionValue(OPT_INPUT));
        File outputFile = new File(commandLine.getOptionValue(OPT_OUTPUT));
        String distancesFileType = commandLine.hasOption(OPT_DISTANCES_FILE_TYPE) ? commandLine.getOptionValue(OPT_DISTANCES_FILE_TYPE) : null;

        this.execute(inputFile, outputFile, distancesFileType);
    }

    @Override
    public String getName() {
        return "convertor";
    }

    @Override
    public String getDescription() {
        return "Converts phylip to nexus format and vice versa.";
    }

    public void execute(File inputFile, File outputFile) throws IOException {
        this.execute(inputFile, outputFile, null);
    }

    public void execute(File inputFile, File outputFile, String distancesFileType) throws IOException {

        // Get a handle on the phygen factory
        PhygenReaderFactory factory = PhygenReaderFactory.getInstance();

        // Setup appropriate reader to input file based on file type
        PhygenReader phygenReader = factory.create(distancesFileType != null ?
                distancesFileType :
                FilenameUtils.getExtension(inputFile.getName()));

        // Load file
        DistanceMatrix distanceMatrix = phygenReader.readDistanceMatrix(inputFile);

        // Setup appropriate writer for output file
        PhygenWriter phygenWriter = phygenReader.getClass().getSimpleName().equals("NexusReader") ?
                PhygenWriterFactory.PHYLIP.create() :
                PhygenWriterFactory.NEXUS.create();

        // Write distance matrix out
        phygenWriter.writeDistanceMatrix(outputFile, distanceMatrix);
    }
}
