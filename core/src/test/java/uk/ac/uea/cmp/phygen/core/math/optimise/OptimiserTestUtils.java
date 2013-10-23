package uk.ac.uea.cmp.phygen.core.math.optimise;

/**
 * Created with IntelliJ IDEA.
 * User: maplesod
 * Date: 23/10/13
 * Time: 17:03
 * To change this template use File | Settings | File Templates.
 */
public class OptimiserTestUtils {

    public static boolean approxEquals(double actual, double expected) {
        return approxEquals(actual, expected, 0.001);
    }

    public static boolean approxEquals(double actual, double expected, double tolerance) {
        double val = Math.abs(actual - expected);
        return val < tolerance;
    }
}
