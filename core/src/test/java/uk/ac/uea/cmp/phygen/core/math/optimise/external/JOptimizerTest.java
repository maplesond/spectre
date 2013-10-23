package uk.ac.uea.cmp.phygen.core.math.optimise.external;

import org.junit.Test;
import uk.ac.uea.cmp.phygen.core.math.optimise.*;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: maplesod
 * Date: 23/10/13
 * Time: 16:31
 * To change this template use File | Settings | File Templates.
 */
public class JOptimizerTest {

    @Test
    public void testAcceptsIdentifier() throws OptimiserException {

        Optimiser apache = new JOptimizer();

        assertTrue(apache.acceptsIdentifier("joptimizer"));
        assertTrue(apache.acceptsIdentifier("Joptimizer"));
        assertTrue(apache.acceptsIdentifier("JOPTIMIZER"));
        assertTrue(apache.acceptsIdentifier("uk.ac.uea.cmp.phygen.core.math.optimise.external.JOptimizer"));
    }


    @Test(expected=OptimiserException.class)
    public void testEmptyProblem() throws OptimiserException {

        Problem problem = Problems.empty();

        Solution solution = new JOptimizer().optimise(problem);

        assertTrue(solution.getSolution() == 0.0);
        assertTrue(solution.getVariableValues().length == 0);
    }


    /**
     * This example formulates and solves the following empty MIP model:
     *  maximize    x +   y + 2 z
     *  subject to  x + 2 y + 3 z <= 4
     *  x + y       >= 1
     *  x, y, z binary
     *
     *  We don't expect this to work because JOptimizer cannot maximise its objectives
     *
     * @throws OptimiserException
     */
    @Test(expected=UnsupportedOperationException.class)
    public void testMip1() throws OptimiserException {

        // Create the MIP1 Problem
        Problem problem = Problems.mip1();

        // Solve
        Solution solution = new JOptimizer().optimise(problem);

        // Check result
        assertTrue(solution.getSolution() == 3.0);

        double[] vals = solution.getVariableValues();

        assertTrue(vals[0] == 1.0);
        assertTrue(vals[1] == 0.0);
        assertTrue(vals[2] == 1.0);
    }

    /**
     * This example formulates and solves a simple linear problem
     *
     * @throws OptimiserException
     */
    @Test
    public void testSimpleLinear() throws OptimiserException {

        // Create the MIP1 Problem
        Problem problem = Problems.simpleLinear();

        // Solve
        Solution solution = new JOptimizer().optimise(problem);

        // Check result
        double[] vals = solution.getVariableValues();
        assertTrue(OptimiserTestUtils.approxEquals(vals[0], 1.5));
        assertTrue(OptimiserTestUtils.approxEquals(vals[1], 0.0));
    }
}
