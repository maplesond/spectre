package uk.ac.uea.cmp.phygen.core.math.optimise.external;

import org.junit.Before;
import org.junit.Test;
import uk.ac.uea.cmp.phygen.core.math.Equality;
import uk.ac.uea.cmp.phygen.core.math.optimise.Optimiser;
import uk.ac.uea.cmp.phygen.core.math.optimise.OptimiserException;
import uk.ac.uea.cmp.phygen.core.math.optimise.Problem;
import uk.ac.uea.cmp.phygen.core.math.optimise.Solution;
import uk.ac.uea.cmp.phygen.core.math.optimise.test.Problems;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 14/09/13
 * Time: 21:53
 * To change this template use File | Settings | File Templates.
 */
public class ApacheTest {

    private Optimiser apache = null;

    @Before
    public void setup() {

        boolean success = true;
        try {
            apache = new Apache();
        }
        catch (OptimiserException oe) {
            success = false;
            System.err.println("Apache not configured for you system... skipping Apache tests");
        }

        assumeTrue(success);
    }

    @Test
    public void testAcceptsIdentifier() throws OptimiserException {

        assumeTrue(apache != null);

        assertTrue(apache.acceptsIdentifier("apache"));
        assertTrue(apache.acceptsIdentifier("Apache"));
        assertTrue(apache.acceptsIdentifier("APACHE"));
        assertTrue(apache.acceptsIdentifier("uk.ac.uea.cmp.phygen.core.math.optimise.external.Apache"));
    }


    @Test(expected=OptimiserException.class)
    public void testSimpleProblem() throws OptimiserException {

        assumeTrue(apache != null);

        Problem problem = Problems.empty();

        Solution solution = apache.optimise(problem);

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

        assumeTrue(apache != null);

        // Create the simple linear Problem
        Problem problem = Problems.simpleLinear();

        // Solve
        Solution solution = apache.optimise(problem);

        // Check result
        double[] vals = solution.getVariableValues();
        assertTrue(Equality.approxEquals(vals[0], 1.5));
        assertTrue(Equality.approxEquals(vals[1], 0.0));
        assertTrue(solution.getSolution() == 2.5);
    }
}
