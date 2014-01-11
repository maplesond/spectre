package uk.ac.uea.cmp.phygen.qtools.sfilter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.io.nexus.Nexus;
import uk.ac.uea.cmp.phygen.core.io.nexus.NexusReader;
import uk.ac.uea.cmp.phygen.core.io.nexus.NexusWriter;
import uk.ac.uea.cmp.phygen.tools.PhygenTool;

import java.io.File;
import java.io.IOException;

/**
 * Created by dan on 11/01/14.
 */
public class SFilter extends PhygenTool {

    private static final String DEFAULT_OUTPUT_FILE = "sfilter.nex";
    private static final double DEFAULT_MIN_THRESHOLD = 0.1;

    private static Logger log = LoggerFactory.getLogger(SFilter.class);

    private static final String OPT_INPUT_FILE = "input";
    private static final String OPT_OUTPUT_FILE = "output";
    private static final String OPT_MIN_THRESHOLD = "min_threshold";


    @Override
    protected Options createInternalOptions() {

        // Create Options object
        Options options = new Options();

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_INPUT_FILE).hasArg()
                .withDescription("The input nexus file containing the splits to filter.").create("i"));

        options.addOption(OptionBuilder.withArgName("file").withLongOpt(OPT_OUTPUT_FILE).hasArg()
                .withDescription("The output nexus file which will contain the filtered splits.").create("o"));

        options.addOption(OptionBuilder.withArgName("string").withLongOpt(OPT_MIN_THRESHOLD).hasArg()
                .withDescription("The minimum threshold for split weights: ").create("t"));

        return options;
    }

    @Override
    protected void execute(CommandLine commandLine) throws IOException {

        File inputFile = new File(commandLine.getOptionValue(OPT_INPUT_FILE));

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
    public String getDescription() {
        return "Filters a nexus file containing splits to remove those splits that have weights below a minimum threshold.";
    }



    /**
     * Main entry point for split filter tool
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
