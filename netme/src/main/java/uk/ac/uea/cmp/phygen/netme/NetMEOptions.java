package uk.ac.uea.cmp.phygen.netme;

import org.apache.commons.cli.*;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 27/04/13
 * Time: 15:38
 * To change this template use File | Settings | File Templates.
 */
public class NetMEOptions {

    public static final String OPT_DISTANCES_FILE = "distances_file";
    public static final String OPT_DISTANCES_FILE_TYPE = "distances_file_type";
    public static final String OPT_CIRCULAR_ORDERING_FILE = "circular_ordering_file";
    public static final String OPT_OUTPUT_DIR = "output";
    public static final String OPT_OUTPUT_PREFIX = "prefix";
    public static final String OPT_HELP = "help";

    private File distancesFile = null;
    private String distancesFileType = null;
    private File circularOrderingFile = null;
    private File outputDir = null;
    private String prefix = null;
    private boolean help = false;


    /**
     * Process command line args.  Checks for help first, then clean, then args for normal operation.
     * @param cmdLine The command line used to execute the program
     * @throws org.apache.commons.cli.ParseException
     */
    public NetMEOptions(CommandLine cmdLine) throws ParseException {

        if (cmdLine.getOptions().length == 0) {

            help = true;
        }
        else {

            help = cmdLine.hasOption(OPT_HELP);

            if (!help) {

                // Required arguments

                if (cmdLine.hasOption(OPT_DISTANCES_FILE)) {
                    distancesFile = new File(cmdLine.getOptionValue(OPT_DISTANCES_FILE));
                }
                else {
                    throw new ParseException(OPT_DISTANCES_FILE + " argument not specified.");
                }

                if (cmdLine.hasOption(OPT_CIRCULAR_ORDERING_FILE)) {
                    circularOrderingFile = new File(cmdLine.getOptionValue(OPT_CIRCULAR_ORDERING_FILE));
                }
                else {
                    throw new ParseException(OPT_CIRCULAR_ORDERING_FILE + " argument not specified.");
                }

                // Optional arguments

                distancesFileType = cmdLine.hasOption(OPT_DISTANCES_FILE_TYPE) ? cmdLine.getOptionValue(OPT_DISTANCES_FILE_TYPE) : null;
                outputDir = cmdLine.hasOption(OPT_OUTPUT_DIR) ? new File(cmdLine.getOptionValue(OPT_OUTPUT_DIR)) : new File(".");

                DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
                Date date = new Date();
                String timeStamp = dateFormat.format(date);

                prefix = cmdLine.hasOption(OPT_OUTPUT_PREFIX) ? cmdLine.getOptionValue(OPT_OUTPUT_PREFIX) : "netme-" + timeStamp;
            }
        }
    }

    public File getDistancesFile() {
        return distancesFile;
    }

    public void setDistancesFile(File distancesFile) {
        this.distancesFile = distancesFile;
    }

    public String getDistancesFileType() {
        return distancesFileType;
    }

    public void setDistancesFileType(String distancesFileType) {
        this.distancesFileType = distancesFileType;
    }

    public File getCircularOrderingFile() {
        return circularOrderingFile;
    }

    public void setCircularOrderingFile(File circularOrderingFile) {
        this.circularOrderingFile = circularOrderingFile;
    }

    public File getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean doHelp() {
        return help;
    }

    public void setHelp(boolean help) {
        this.help = help;
    }

    public void printUsage() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(this.getClass().getName(), createOptions());
    }

    @SuppressWarnings("static-access")
    public static Options createOptions() {

        // Boolean options
        Option optHelp = new Option("?", OPT_HELP, false, "Print this message.");

        // Options with arguments
        Option optDistancesFile = OptionBuilder.withArgName("file").withLongOpt(OPT_DISTANCES_FILE).hasArg()
                .withDescription("The file containing the distance data.").create("i");

        Option optDistancesFileType = OptionBuilder.withArgName("string").withLongOpt(OPT_DISTANCES_FILE_TYPE).hasArg()
                .withDescription("The file type of the distance data file: [NEXUS, PHYLIP].").create("t");

        Option optCircularOrderingFile = OptionBuilder.withArgName("file").withLongOpt(OPT_CIRCULAR_ORDERING_FILE).hasArg()
                .withDescription("The nexus file containing the circular ordering.").create("j");

        Option optOutputDir = OptionBuilder.withArgName("file").withLongOpt(OPT_OUTPUT_DIR).hasArg()
                .withDescription("The directory to put output from this job.").create("o");

        Option optOutputPrefix = OptionBuilder.withArgName("string").withLongOpt(OPT_OUTPUT_PREFIX).hasArg()
                .withDescription("The prefix to apply to all files produced by this NetME run.  Default: netme-<timestamp>.").create("p");

        // create Options object
        Options options = new Options();

        // add t option
        options.addOption(optHelp);
        options.addOption(optDistancesFile);
        options.addOption(optDistancesFileType);
        options.addOption(optCircularOrderingFile);
        options.addOption(optOutputDir);
        options.addOption(optOutputPrefix);

        return options;
    }


}
