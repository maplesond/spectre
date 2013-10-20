package uk.ac.uea.cmp.phygen.core.math.optimise;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 13/09/13
 * Time: 00:37
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractOptimiser implements Optimiser {

    @Override
    public void initialise() {
    }


    @Override
    public double[] optimise(Problem problem) throws OptimiserException {

        // Check the optimiser is operational
        if (!this.isOperational()) {
            throw new UnsupportedOperationException(this.getIdentifier() + " is not operational");
        }

        // Check we have an objective
        if (problem.getObjective() == null) {
            throw new OptimiserException("An objective must be specified for " + this.getIdentifier());
        }

        // Run the solver on the problem to get a (hopefully) optimal solution
        double[] solution = this.internalOptimise(problem);

        // Probably used a lot of memory.  Collect Garbage to save space.
        System.gc();

        return solution;
    }


    protected abstract double[] internalOptimise(Problem problem) throws OptimiserException;
}
