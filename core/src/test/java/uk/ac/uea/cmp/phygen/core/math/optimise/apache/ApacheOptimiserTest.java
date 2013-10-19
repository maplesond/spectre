package uk.ac.uea.cmp.phygen.core.math.optimise.apache;

import org.junit.Test;
import uk.ac.uea.cmp.phygen.core.math.optimise.*;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 14/09/13
 * Time: 21:53
 * To change this template use File | Settings | File Templates.
 */
public class ApacheOptimiserTest {

    private static class SimpleObjective extends AbstractObjective {

        @Override
        public double[] buildCoefficients(final int size) {
            double[] coefficients = new double[size];
            Arrays.fill(coefficients, 1.0);
            return coefficients;
        }

        @Override
        public ObjectiveType getObjectiveType() {
            return ObjectiveType.LINEAR;
        }
    }


    @Test
    public void testAcceptsIdentifier() throws OptimiserException {

        Optimiser apache = new ApacheOptimiser();

        assertTrue(apache.acceptsIdentifier("apache"));
        assertTrue(apache.acceptsIdentifier("Apache"));
        assertTrue(apache.acceptsIdentifier("APACHE"));
        assertTrue(apache.acceptsIdentifier("uk.ac.uea.cmp.phygen.core.math.optimise.apache.ApacheOptimiser"));
    }


    @Test
    public void testNullOptimise() throws OptimiserException {

        double[] nonNegativityConstraint = new double[0];
        double[][] solutionSpaceConstraint = new double[0][0];

        Problem problem = new Problem(new SimpleObjective(), nonNegativityConstraint, solutionSpaceConstraint);

        double[] solution = new ApacheOptimiser().optimise(problem);

        assertTrue(true);
    }

    @Test
    public void testSimpleProblem() throws OptimiserException {

        double[] nonNegativityConstraint = new double[0];
        double[][] solutionSpaceConstraint = new double[0][0];

        Problem problem = new Problem(new SimpleObjective(), nonNegativityConstraint, solutionSpaceConstraint);

        double[] solution = new ApacheOptimiser().optimise(problem);

        assertTrue(true);
    }
}
