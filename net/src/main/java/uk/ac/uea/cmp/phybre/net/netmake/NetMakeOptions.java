package uk.ac.uea.cmp.phybre.net.netmake;

import uk.ac.uea.cmp.phybre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.phybre.net.netmake.weighting.GreedyMEWeighting;
import uk.ac.uea.cmp.phybre.net.netmake.weighting.Weighting;
import uk.ac.uea.cmp.phybre.net.netmake.weighting.Weightings;

import java.io.File;

/**
 * Created by dan on 16/01/14.
 */
public class NetMakeOptions {

    // Options descriptions
    public static final String DESC_INPUT = "The file containing the distance matrix to input.";

    public static final String DESC_OUTPUT_DIR = "The directory to put output from this job.";

    public static final String DESC_OUTPUT_PREFIX = "The prefix to apply to all files produced by this NetMake run.  Default: netmake-<timestamp>.";

    public static final String DESC_TREE_PARAM = "The weighting parameter passed to the chosen weighting algorithm. " +
            " Value must be between 0.0 and 1.0.  Default: 0.5.";

    public static final String DESC_WEIGHTINGS_1 = "Select Weighting type: " + Weightings.toListString() + ".  Default: ";

    public static final String DESC_WEIGHTINGS_2 = "Select 2nd Weighting type: " + Weightings.toListString() + ". Default: ";



    private File input;
    private File outputDir;
    private String outputPrefix;
    private String weighting1;
    private String weighting2;
    private double treeParam;

    public NetMakeOptions() {
        this(null, null, "netmake", null, null, 0.5);
    }

    public NetMakeOptions(File input, File outputDir, String outputPrefix, String weighting1, String weighting2, double treeParam) {

        // Validates that we have sensible input for the weightings
        if (weighting1 != null && !weighting1.isEmpty())
            Weightings.valueOf(weighting1);

        if (weighting2 != null && !weighting2.isEmpty())
            Weightings.valueOf(weighting2);

        this.input = input;
        this.outputDir = outputDir;
        this.outputPrefix = outputPrefix;
        this.weighting1 = weighting1;
        this.weighting2 = weighting2;
        this.treeParam = treeParam;
    }


    protected static boolean isGreedyMEWeighting(Weighting w) {
        if (w == null)
            return false;

        return (w instanceof GreedyMEWeighting);
    }

    protected enum RunMode {

        UNKNOWN,
        NORMAL,
        HYBRID,
        HYBRID_GREEDYME;

        public boolean isHybrid() {
            return (this == HYBRID || this == HYBRID_GREEDYME);
        }
    }

    public File getInput() {
        return input;
    }

    public void setInput(File input) {
        this.input = input;
    }

    public File getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir;
    }

    public String getOutputPrefix() {
        return outputPrefix;
    }

    public void setOutputPrefix(String outputPrefix) {
        this.outputPrefix = outputPrefix;
    }

    public String getWeighting1() {
        return weighting1;
    }

    public void setWeighting1(String weighting1) {
        this.weighting1 = weighting1;
    }

    public String getWeighting2() {
        return weighting2;
    }

    public void setWeighting2(String weighting2) {
        this.weighting2 = weighting2;
    }

    public double getTreeParam() {
        return treeParam;
    }

    public void setTreeParam(double treeParam) {
        this.treeParam = treeParam;
    }

    public static RunMode getRunMode(Weighting weighting1, Weighting weighting2) {

        if (weighting1 == null) {
            return RunMode.UNKNOWN;
        } else if (weighting2 == null && !isGreedyMEWeighting(weighting1)) {
            return RunMode.NORMAL;
        } else if (isGreedyMEWeighting(weighting1) && !isGreedyMEWeighting(weighting2)) {
            return RunMode.HYBRID_GREEDYME;
        } else if (!isGreedyMEWeighting(weighting1) && weighting2 != null) {
            return RunMode.HYBRID;
        }

        return RunMode.UNKNOWN;
    }
}
