package uk.ac.uea.cmp.phygen.core.math.optimise;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 13/09/13
 * Time: 00:37
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractOptimiser implements Optimiser {

    private Objective objective;

    @Override
    public void setObjective(Objective objective) {

        // Check if the requested objective is supported before setting it
        if (!this.acceptsObjective(objective))
            throw new UnsupportedOperationException("Objective: " + objective.toString() + " not accepted by " +
                    this.getDescription());

        this.objective = objective;
    }

    @Override
    public double[] optimise(Problem problem) throws OptimiserException {

        // Check the optimiser is operational
        if (!this.isOperational()) {
            throw new UnsupportedOperationException(this.getDescription() + " is not operational");
        }

        // Check we have an objective
        if (this.objective == null) {
            throw new OptimiserException("An objective must be specified for " + this.getDescription());
        }

        // Build coeffiecients using objective
        double[] coefficients = objective.buildCoefficients(problem.getRestriction().length);

        // Call child class' optimise method
        double[] solution = this.internalOptimise(problem, coefficients);

        // Probably used a lot of memory.  Collect Garbage to save space.
        System.gc();

        return solution;
    }

    @Override
    public double[] multiOptimise(Problem problem) throws OptimiserException {

        // Check the optimiser is operational
        if (!this.isOperational()) {
            throw new UnsupportedOperationException(this.getDescription() + " is not operational");
        }

        // Check we have an objective
        if (this.objective == null) {
            throw new OptimiserException("An objective must be specified for " + this.getDescription());
        }

        double[] coefficients = objective.buildCoefficients(problem.getRestriction().length);

        double[] data = problem.getRestriction();

        final int rows = problem.getRestriction().length;

        double[] solution = new double[rows];
        for (int k = 0; k < rows; k++) {
            if (data[k] > 0.0) {
                coefficients[k] = 1.0;
                double[] help = this.internalOptimise(problem, coefficients);
                solution[k] = help[k];
                coefficients[k] = 0.0;
            } else {
                solution[k] = 0;
            }
        }

        // Probably used a lot of memory.  Collect Garbage to save space.
        System.gc();

        return solution;
    }

    protected abstract double[] internalOptimise(Problem problem, double[] coefficients) throws OptimiserException;
}
