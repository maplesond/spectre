package uk.ac.uea.cmp.phygen.core.math.optimise.apache;

import org.junit.Test;
import uk.ac.uea.cmp.phygen.core.math.optimise.Objective;
import uk.ac.uea.cmp.phygen.core.math.optimise.Optimiser;
import uk.ac.uea.cmp.phygen.core.math.optimise.OptimiserException;
import uk.ac.uea.cmp.phygen.core.math.optimise.Problem;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 14/09/13
 * Time: 21:53
 * To change this template use File | Settings | File Templates.
 */
public class ApacheOptimiserTest {


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

        Optimiser apache = new ApacheOptimiser(Objective.LINEAR);

        Problem problem = new Problem();

        double[] solution = apache.optimise(problem);

        assertTrue(true);
    }

    @Test
    public void testSimpleProblem() throws OptimiserException {

        Optimiser apache = new ApacheOptimiser(Objective.LINEAR);

        Problem problem = new Problem();

        double[] solution = apache.optimise(problem);

        assertTrue(true);


    }
}
