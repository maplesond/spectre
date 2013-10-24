package uk.ac.uea.cmp.phygen.core.math.optimise.external;

import org.junit.Before;
import org.junit.Test;
import uk.ac.uea.cmp.phygen.core.math.Equality;
import uk.ac.uea.cmp.phygen.core.math.optimise.*;
import uk.ac.uea.cmp.phygen.core.math.optimise.test.Problems;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

/**
 * Created with IntelliJ IDEA.
 * User: maplesod
 * Date: 23/10/13
 * Time: 16:31
 * To change this template use File | Settings | File Templates.
 */
public class JOptimizerTest {

    private Optimiser jOptimizer = null;

    @Before
    public void setup() {

        boolean success = true;
        try {
            jOptimizer = new JOptimizer();
        }
        catch (OptimiserException oe) {
            success = false;
            System.err.println("JOptimizer not configured for you system... skipping JOptimizer tests");
        }

        assumeTrue(success);
    }

    @Test
    public void testAcceptsIdentifier() throws OptimiserException {

        assumeTrue(jOptimizer != null);

        assertTrue(jOptimizer.acceptsIdentifier("joptimizer"));
        assertTrue(jOptimizer.acceptsIdentifier("Joptimizer"));
        assertTrue(jOptimizer.acceptsIdentifier("JOPTIMIZER"));
        assertTrue(jOptimizer.acceptsIdentifier("uk.ac.uea.cmp.phygen.core.math.optimise.external.JOptimizer"));
    }


    @Test(expected=OptimiserException.class)
    public void testEmptyProblem() throws OptimiserException {

        assumeTrue(jOptimizer != null);

        Problem problem = Problems.empty();

        Solution solution = new JOptimizer().optimise(problem);

        assertTrue(solution.getSolution() == 0.0);
        assertTrue(solution.getVariableValues().length == 0);
    }


    /**
     * This example formulates and solves a simple linear problem
     *
     * @throws OptimiserException
     */
    @Test
    public void testSimpleLinear() throws OptimiserException {

        assumeTrue(jOptimizer != null);

        // Create the Problem
        Problem problem = Problems.simpleLinear();

        // Solve
        Solution solution = jOptimizer.optimise(problem);

        // Check result
        double[] vals = solution.getVariableValues();
        assertTrue(Equality.approxEquals(vals[0], 1.5));
        assertTrue(Equality.approxEquals(vals[1], 0.0));
    }


    /**
     * This example formulates and solves a simple quadratic problem
     *
     * @throws OptimiserException
     */
    @Test
    public void testSimpleQuadratic() throws OptimiserException {

        assumeTrue(jOptimizer != null);

        // Create the Problem
        Problem problem = Problems.simpleQuadratic();

        // Solve
        Solution solution = new JOptimizer().optimise(problem);

        // Check result
        double[] vals = solution.getVariableValues();
        assertTrue(Equality.approxEquals(vals[0], 0.5));
        assertTrue(Equality.approxEquals(vals[1], 0.5));
    }


    /**
     * This example formulates and solves a simple quadratic problem
     *
     * @throws OptimiserException
     */
    @Test
    public void testSimpleQuadratic2() throws OptimiserException {

        assumeTrue(jOptimizer != null);

        // Create the Problem
        Problem problem = Problems.simpleQuadratic2();

        // Solve
        Solution solution = new JOptimizer().optimise(problem);

        // Check result
        double[] vals = solution.getVariableValues();
        assertTrue(Equality.approxEquals(vals[0], 0.0));
        assertTrue(Equality.approxEquals(vals[1], 1.0));
        assertTrue(Equality.approxEquals(vals[2], 2.0/3.0));
    }
}
