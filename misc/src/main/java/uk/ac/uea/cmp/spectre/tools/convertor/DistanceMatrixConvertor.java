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

package uk.ac.uea.cmp.spectre.tools.convertor;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.MetaInfServices;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.io.*;
import uk.ac.uea.cmp.spectre.tools.SpectreTool;

import java.io.File;
import java.io.IOException;

/**
 * Converts phylip to nexus and vice versa
 */
@MetaInfServices
public class DistanceMatrixConvertor extends SpectreTool {

    private static final String OPT_INPUT = "input";
    private static final String OPT_OUTPUT = "output";
    public static final String OPT_DISTANCES_FILE_TYPE = "distances_file_type";

    @Override
    protected Options createInternalOptions() {

        // Create Options object
        Options options = new Options();

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_OUTPUT).hasArg().isRequired()
                .withDescription("The converted file.").create("o"));

        options.addOption(OptionBuilder.withArgName("string").withLongOpt(OPT_DISTANCES_FILE_TYPE).hasArg()
                .withDescription("The file type of the input distance data file: " +
                        SpectreReaderFactory.getInstance().getSpectreReadersAsString(SpectreDataType.DISTANCE_MATRIX) +"." +
                        "Use this if your input file has a non-standard extension.").create("t"));

        return options;
    }

    @Override
    protected void execute(CommandLine commandLine) throws IOException {

        if (commandLine.getArgs().length == 0) {
            throw new IOException("No input file specified.");
        }
        else if (commandLine.getArgs().length > 1) {
            throw new IOException("Only expected a single input file.");
        }

        File inputFile = new File(commandLine.getArgs()[0]);
        File outputFile = new File(commandLine.getOptionValue(OPT_OUTPUT));
        String distancesFileType = commandLine.hasOption(OPT_DISTANCES_FILE_TYPE) ? commandLine.getOptionValue(OPT_DISTANCES_FILE_TYPE) : null;

        this.execute(inputFile, outputFile, distancesFileType);
    }

    @Override
    public String getName() {
        return "distmxconv";
    }

    @Override
    public String getPosArgs() {
        return "<phylip/nexus_file>";
    }

    @Override
    public String getDescription() {
        return "Converts a distance matrix from phylip to nexus format and vice versa.";
    }

    public void execute(File inputFile, File outputFile) throws IOException {
        this.execute(inputFile, outputFile, null);
    }

    public void execute(File inputFile, File outputFile, String distancesFileType) throws IOException {

        // Get a handle on the phygen factory
        SpectreReaderFactory factory = SpectreReaderFactory.getInstance();

        // Setup appropriate reader to input file based on file type
        SpectreReader spectreReader = factory.create(distancesFileType != null ?
                distancesFileType :
                FilenameUtils.getExtension(inputFile.getName()));

        // Load file
        DistanceMatrix distanceMatrix = spectreReader.readDistanceMatrix(inputFile);

        // Setup appropriate writer for output file
        SpectreWriter spectreWriter = spectreReader.getClass().getSimpleName().equals("NexusReader") ?
                SpectreWriterFactory.PHYLIP.create() :
                SpectreWriterFactory.NEXUS.create();

        // Write distance matrix out
        spectreWriter.writeDistanceMatrix(outputFile, distanceMatrix);
    }

    public static void main(String[] args) {

        try {
            new DistanceMatrixConvertor().execute(args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(StringUtils.join(e.getStackTrace(), "\n"));
            System.exit(1);
        }
    }
}
