package uk.ac.uea.cmp.phygen.netmake.fits;

import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceGenerator;
import uk.ac.uea.cmp.phygen.core.ds.distance.MetricDistanceGenerator;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 01/05/13
 * Time: 02:01
 * To change this template use File | Settings | File Templates.
 */
public class RandomNeighbourNetFitsConfig {

    private static final DistanceGenerator DEFAULT_DISTANCE_GENERATOR = new MetricDistanceGenerator();
    private static final int DEFAULT_MIN_N = 100;
    private static final int DEFAULT_MAX_N = 100;
    private static final int DEFAULT_STEPPING = 1;
    private static final int DEFAULT_SAMPLES_PER_STEP = 50;

    private DistanceGenerator distanceGenerator;
    private int minN;
    private int maxN;
    private int stepping;
    private int samplesPerStep;

    public RandomNeighbourNetFitsConfig() {
        this(DEFAULT_DISTANCE_GENERATOR, DEFAULT_MIN_N, DEFAULT_MAX_N, DEFAULT_STEPPING, DEFAULT_SAMPLES_PER_STEP);
    }

    public RandomNeighbourNetFitsConfig(DistanceGenerator distanceGenerator, int minN, int maxN, int stepping, int samplesPerStep) {
        this.distanceGenerator = distanceGenerator;
        this.minN = minN;
        this.maxN = maxN;
        this.stepping = stepping;
        this.samplesPerStep = samplesPerStep;
    }

    public DistanceGenerator getDistanceGenerator() {
        return distanceGenerator;
    }

    public int getMinN() {
        return minN;
    }

    public int getMaxN() {
        return maxN;
    }

    public int getStepping() {
        return stepping;
    }

    public int getSamplesPerStep() {
        return samplesPerStep;
    }


    public int calcNumberOfFilesToGenerate() {
        return ((maxN - minN) / stepping + 1) * samplesPerStep;
    }
}
