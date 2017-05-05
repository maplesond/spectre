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

package uk.ac.uea.cmp.spectre.qtools.sfilter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import uk.ac.uea.cmp.spectre.core.io.nexus.Nexus;
import uk.ac.uea.cmp.spectre.core.io.nexus.NexusReader;
import uk.ac.uea.cmp.spectre.core.io.nexus.NexusWriter;
import uk.ac.uea.cmp.spectre.tools.SpectreTool;

import java.io.File;
import java.io.IOException;

/**
 * Created by dan on 11/01/14.
 */
public class SFilter extends SpectreTool {

    private static final String DEFAULT_OUTPUT_FILE = "sfilter.nex";
    private static final double DEFAULT_MIN_THRESHOLD = 0.1;

    private static final String OPT_INPUT_FILE = "input";
    private static final String OPT_OUTPUT_FILE = "output";
    private static final String OPT_MIN_THRESHOLD = "min_threshold";


    @Override
    protected Options createInternalOptions() {

        // Create Options object
        Options options = new Options();

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_OUTPUT_FILE).hasArg()
                .withDescription("The output nexus file which will contain the filtered splits.").create("o"));

        options.addOption(OptionBuilder.withArgName("string").withLongOpt(OPT_MIN_THRESHOLD).hasArg()
                .withDescription("The minimum threshold for split weights").create("t"));

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

        File outputFile = commandLine.hasOption(OPT_OUTPUT_FILE) ?
                new File(commandLine.getOptionValue(OPT_OUTPUT_FILE)) :
                new File(DEFAULT_OUTPUT_FILE);

        double minThreshold = commandLine.hasOption(OPT_MIN_THRESHOLD) ?
                Double.parseDouble(commandLine.getOptionValue(OPT_MIN_THRESHOLD)) :
                DEFAULT_MIN_THRESHOLD;

        // Create the quartets from the input and save to file
        this.execute(inputFile, outputFile, minThreshold);
    }

    public void execute(File inputFile, File outputFile) throws IOException {
        this.execute(inputFile, outputFile, DEFAULT_MIN_THRESHOLD);
    }

    public void execute(File inputFile, File outputFile, double minThreshold) throws IOException {

        // Load
        Nexus raw = new NexusReader().readNexusData(inputFile);

        // Filter
        raw.getSplitSystem().filterByWeight(minThreshold);

        // Save
        new NexusWriter().writeNexusData(outputFile, raw);
    }


    @Override
    public String getName() {
        return "sfilter";
    }

    @Override
    public String getPosArgs() {
        return "<nexus_file>";
    }


    @Override
    public String getDescription() {
        return "Filters a nexus file containing splits to remove those splits that have weights below a minimum threshold.";
    }


    /**
     * Main entry point for split filter tool
     *
     * @param args
     */
    public static void main(String[] args) {

        try {
            new SFilter().execute(args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(StringUtils.join(e.getStackTrace(), "\n"));
            System.exit(1);
        }
    }
}
