package uk.ac.uea.cmp.phygen.net.netmake;

import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.phygen.net.netmake.weighting.GreedyMEWeighting;
import uk.ac.uea.cmp.phygen.net.netmake.weighting.Weighting;
import uk.ac.uea.cmp.phygen.net.netmake.weighting.Weightings;

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



    private DistanceMatrix distanceMatrix;
    private Weighting weighting1;
    private Weighting weighting2;

    public NetMakeOptions() {
        this(null, null, null);
    }

    public NetMakeOptions(DistanceMatrix distanceMatrix, Weighting weighting1, Weighting weighting2) {

        this.distanceMatrix = distanceMatrix;
        this.weighting1 = weighting1;
        this.weighting2 = weighting2;
    }


    protected boolean isGreedyMEWeighting(Weighting w) {
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

    public void setDistanceMatrix(DistanceMatrix distanceMatrix) {
        this.distanceMatrix = distanceMatrix;
    }

    public void setWeighting1(Weighting weighting1) {
        this.weighting1 = weighting1;
    }

    public void setWeighting2(Weighting weighting2) {
        this.weighting2 = weighting2;
    }

    public DistanceMatrix getDistanceMatrix() {
        return distanceMatrix;
    }

    public Weighting getWeighting1() {
        return weighting1;
    }

    public Weighting getWeighting2() {
        return weighting2;
    }

    public int getNbTaxa() {
        return this.distanceMatrix != null ? this.distanceMatrix.size() : 0;
    }

    public RunMode getRunMode() {

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
