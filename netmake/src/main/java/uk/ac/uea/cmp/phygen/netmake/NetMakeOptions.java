package uk.ac.uea.cmp.phygen.netmake;

import org.apache.commons.cli.*;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Sarah_2
 * Date: 24/04/13
 * Time: 14:17
 * To change this template use File | Settings | File Templates.
 */
public class NetMakeOptions {

    public static final String OPT_INPUT = "input";
    public static final String OPT_INPUT_TYPE = "input_file_type";
    public static final String OPT_OUTPUT_DIR = "output";
    public static final String OPT_OUTPUT_PREFIX = "prefix";
    public static final String OPT_TREE_PARAM = "tree_param";
    public static final String OPT_WEIGHTINGS_1 = "weightings_1";
    public static final String OPT_WEIGHTINGS_2 = "weightings_2";
    public static final String OPT_HELP = "help";

    private File input = null;
    private String inputType = null;
    private File outputDir = null;
    private String prefix = null;
    private double treeParam = 0.5;
    private String weightings1 = null;
    private String weightings2 = null;
    private boolean help = false;


    /**
     * Process command line args.  Checks for help first, then clean, then args for normal operation.
     * @param cmdLine The command line used to execute the program
     * @throws ParseException
     */
    public NetMakeOptions(CommandLine cmdLine) throws ParseException {

        if (cmdLine.getOptions().length == 0) {

            help = true;
        }
        else {

            help = cmdLine.hasOption(OPT_HELP);

            if (!help) {

                // Required arguments

                if (cmdLine.hasOption(OPT_INPUT)) {
                    input = new File(cmdLine.getOptionValue(OPT_INPUT));
                }
                else {
                    throw new ParseException(OPT_INPUT + " argument not specified.");
                }

                if (cmdLine.hasOption(OPT_WEIGHTINGS_1)) {
                    weightings1 = cmdLine.getOptionValue(OPT_WEIGHTINGS_1);
                }
                else {
                    throw new ParseException(OPT_WEIGHTINGS_1 + " argument not specified.");
                }

                // Optional arguments

                inputType = cmdLine.hasOption(OPT_INPUT_TYPE) ? cmdLine.getOptionValue(OPT_INPUT_TYPE) : null;
                outputDir = cmdLine.hasOption(OPT_OUTPUT_DIR) ? new File(cmdLine.getOptionValue(OPT_OUTPUT_DIR)) : new File(".");
                treeParam = cmdLine.hasOption(OPT_TREE_PARAM) ? Double.parseDouble(cmdLine.getOptionValue(OPT_TREE_PARAM)) : 0.5;

                DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
                Date date = new Date();
                String timeStamp = dateFormat.format(date);

                prefix = cmdLine.hasOption(OPT_OUTPUT_PREFIX) ? cmdLine.getOptionValue(OPT_OUTPUT_PREFIX) : "netmake-" + timeStamp;

                weightings2 = cmdLine.hasOption(OPT_WEIGHTINGS_2) ? cmdLine.getOptionValue(OPT_WEIGHTINGS_2) : null;
            }
        }
    }

    public File getInput() {
        return input;
    }

    public void setInput(File input) {
        this.input = input;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
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

    public double getTreeParam() {
        return treeParam;
    }

    public void setTreeParam(double treeParam) {
        this.treeParam = treeParam;
    }

    public String getWeightings1() {
        return weightings1;
    }

    public void setWeightings1(String weightings1) {
        this.weightings1 = weightings1;
    }

    public String getWeightings2() {
        return weightings2;
    }

    public void setWeightings2(String weightings2) {
        this.weightings2 = weightings2;
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
        Option optInput = OptionBuilder.withArgName("file").withLongOpt(OPT_INPUT).hasArg()
                .withDescription("The file containing the distance data to input.").create("i");

        Option optInputType = OptionBuilder.withArgName("string").withLongOpt(OPT_INPUT_TYPE).hasArg()
                .withDescription("The file type of the input file: [NEXUS, PHYLIP].").create("t");

        Option optOutputDir = OptionBuilder.withArgName("file").withLongOpt(OPT_OUTPUT_DIR).hasArg()
                .withDescription("The directory to put output from this job.").create("o");

        Option optOutputPrefix = OptionBuilder.withArgName("string").withLongOpt(OPT_OUTPUT_PREFIX).hasArg()
                .withDescription("The prefix to apply to all files produced by this NetMake run.  Default: netmake-<timestamp>.").create("p");

        Option optTreeParam = OptionBuilder.withArgName("double").withLongOpt(OPT_TREE_PARAM).hasArg()
                .withDescription("The weighting parameter passed to the chosen weighting algorithm. " +
                        " Value must be between 0.0 and 1.0.  Default: 0.5.").create("z");

        Option optWeighting1 = OptionBuilder.withArgName("string").withLongOpt(OPT_WEIGHTINGS_1).hasArg()
                .withDescription("Select Weighting type: [TSP, TREE, EQUAL, PARABOLA, GREEDY].  Default: ").create("w");

        Option optWeighting2 = OptionBuilder.withArgName("string").withLongOpt(OPT_WEIGHTINGS_2).hasArg()
                .withDescription("Select 2nd Weighting type: [TSP, TREE, EQUAL, PARABOLA, GREEDY]. Default: ").create("x");


        // create Options object
        Options options = new Options();

        // add t option
        options.addOption(optHelp);
        options.addOption(optInput);
        options.addOption(optInputType);
        options.addOption(optOutputDir);
        options.addOption(optOutputPrefix);
        options.addOption(optTreeParam);
        options.addOption(optWeighting1);
        options.addOption(optWeighting2);

        return options;
    }


}
