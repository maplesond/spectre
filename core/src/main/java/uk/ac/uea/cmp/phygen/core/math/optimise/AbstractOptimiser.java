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
        double[] solution = problem.getObjective().runForEachCoefficient() ?
                this.minimaOptimise(problem) :      // Special handling of MINIMA objective
                this.internalOptimise(problem);     // Normally just call child's optimisation method

        // Probably used a lot of memory.  Collect Garbage to save space.
        System.gc();

        return solution;
    }

    /**
     * To be used in conjunction with the MINIMA objective
     *
     * @param problem
     * @return
     * @throws OptimiserException
     */
    protected double[] minimaOptimise(Problem problem) throws OptimiserException {

        double[] data = problem.getNonNegativityConstraint();
        double[] coefficients = problem.getCoefficients();

        double[] solution = new double[data.length];

        // This is a bit messy, but essentially what is happening is that we run the solver for each coefficient, and if
        // the non-negativity constraint at each location is > 0, then we run the solver but we only take the result from
        // this position, otherwise the solution at this position is 0.
        for (int k = 0; k < data.length; k++) {
            if (data[k] > 0.0) {
                coefficients[k] = 1.0;
                double[] help = this.internalOptimise(problem);
                solution[k] = help[k];
                coefficients[k] = 0.0;
            } else {
                solution[k] = 0;
            }
        }

        return solution;
    }

    protected abstract double[] internalOptimise(Problem problem) throws OptimiserException;
}
