package uk.ac.uea.cmp.phygen.qnet;

import org.apache.commons.cli.OptionBuilder;
import uk.ac.tgac.metaopt.Objective;
import uk.ac.tgac.metaopt.Optimiser;
import uk.ac.tgac.metaopt.OptimiserFactory;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: maplesod
 * Date: 29/12/13
 * Time: 13:06
 * To change this template use File | Settings | File Templates.
 */
public class QNetOptions {

    public static final String DESC_INPUT = "The file containing the taxa to input.";

    public static final String DESC_OUTPUT = "The nexus file that will contain output.";

    public static final String DESC_LOG = "If false, normalises quartets linearly, if true normalises quartets using natural log";

    public static final String DESC_TOLERANCE = "The tolerance to use when computing edge weights using internal method.  Not used if an external optimiser is selected.";

    public static final String DESC_OPTIMISER = "If specified, uses an external quadratic optimiser to compute weights.  If not, " +
            "then an internal method is used instead.  Available external optimisers: " + OptimiserFactory.getInstance().listOperationalOptimisers(Objective.ObjectiveType.QUADRATIC);


    private File input;
    private File output;
    private Optimiser optimiser;
    private boolean logNormalise;
    private double tolerance;

    public QNetOptions() {
        this(null, null, null, false, -1.0);
    }

    public QNetOptions(File input, File output, Optimiser optimiser, boolean logNormalise, double tolerance) {
        this.input = input;
        this.output = output;
        this.optimiser = optimiser;
        this.logNormalise = logNormalise;
        this.tolerance = tolerance;
    }

    public File getInput() {
        return input;
    }

    public void setInput(File input) {
        this.input = input;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public Optimiser getOptimiser() {
        return optimiser;
    }

    public void setOptimiser(Optimiser optimiser) {
        this.optimiser = optimiser;
    }

    public boolean isLogNormalise() {
        return logNormalise;
    }

    public void setLogNormalise(boolean logNormalise) {
        this.logNormalise = logNormalise;
    }

    public double getTolerance() {
        return tolerance;
    }

    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }
}
