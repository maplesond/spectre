package uk.ac.uea.cmp.phygen.core.math.optimise.external;

import org.junit.Before;
import org.junit.Test;
import uk.ac.uea.cmp.phygen.core.math.optimise.*;
import uk.ac.uea.cmp.phygen.core.math.optimise.OptimiserTestUtils;
import uk.ac.uea.cmp.phygen.core.math.optimise.Problems;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;
import static org.junit.Assume.assumeTrue;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 22/09/13
 * Time: 21:31
 * To change this template use File | Settings | File Templates.
 */
public class GLPKTest {

    private Optimiser glpk = null;

    @Before
    public void setup() {

        boolean success = true;
        try {
            glpk = OptimiserFactory.getInstance().createOptimiserInstance("glpk", Objective.ObjectiveType.LINEAR);
        }
        catch (OptimiserException oe) {
            success = false;
            System.err.println("GLPK not configured for you system... skipping GLPK tests");
        }

        assumeTrue(success);
    }

    @Test
    public void testAcceptsIdentifier() throws OptimiserException {

        assumeTrue(glpk != null);

        assertTrue(glpk.acceptsIdentifier("glpk"));
        assertTrue(glpk.acceptsIdentifier("Glpk"));
        assertTrue(glpk.acceptsIdentifier("GLPK"));
        assertTrue(glpk.acceptsIdentifier("uk.ac.uea.cmp.phygen.core.math.optimise.external.GLPK"));
    }



    @Test(expected=OptimiserException.class)
    public void testSimple() throws OptimiserException {

        assumeTrue(glpk != null);

        Problem problem = Problems.empty();

        Solution solution = glpk.optimise(problem);

        assertTrue(solution.getSolution() == 0.0);
        assertTrue(solution.getVariableValues().length == 0);
    }

    /**
     * This example formulates and solves the following empty MIP model:
     *  maximize    x +   y + 2 z
     *  subject to  x + 2 y + 3 z <= 4
     *  x + y       >= 1
     *  x, y, z binary
     * @throws OptimiserException
     */
    @Test
    public void testMip1() throws OptimiserException {

        assumeTrue(glpk != null);

        // Create the MIP1 Problem
        Problem problem = Problems.mip1();

        // Solve
        Solution solution = glpk.optimise(problem);

        // Check result
        //assertTrue(solution.getSolution() == 3.0);

        double[] vals = solution.getVariableValues();

        //assertTrue(vals[0] == 1.0);
        //assertTrue(vals[1] == 0.0);
        //assertTrue(vals[2] == 1.0);
    }

    /**
     * This example formulates and solves a simple linear problem
     *
     * @throws OptimiserException
     */
    @Test
    public void testSimpleLinear() throws OptimiserException {

        assumeTrue(glpk != null);

        // Create the simple linear Problem
        Problem problem = Problems.simpleLinear();

        // Solve
        Solution solution = glpk.optimise(problem);

        // Check result
        double[] vals = solution.getVariableValues();
        //assertTrue(OptimiserTestUtils.approxEquals(vals[0], 1.5));
        //assertTrue(OptimiserTestUtils.approxEquals(vals[1], 0.0));
    }
}
