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
    public double[] optimise(Objective objective, Problem problem) throws OptimiserException {

        if (!this.acceptsObjective(objective)) {
            throw new UnsupportedOperationException("Objective: " + objective.toString() + " not accepted by " +
                    this.getClass().getCanonicalName());
        }

        if (!this.isOperational()) {
            throw new UnsupportedOperationException(this.getDescription() + " is not operational");
        }

        double[] coefficients = objective.buildCoefficients(problem.getRestriction().length);
        problem.setCoefficients(coefficients);

        double[] solution = this.internalOptimise(problem);

        // Probably used a lot of memory.  Collect Garbage to save space.
        System.gc();

        return solution;
    }

    @Override
    public double[] multiOptimise(Objective objective, Problem problem) throws OptimiserException {

        if (!this.acceptsObjective(objective)) {
            throw new UnsupportedOperationException("Objective: " + objective.toString() + " not accepted by " +
                    this.getClass().getCanonicalName());
        }

        if (!this.isOperational()) {
            throw new UnsupportedOperationException(this.getDescription() + " is not operational");
        }

        problem.setCoefficients(objective.buildCoefficients(problem.getRestriction().length));

        double[] data = problem.getRestriction();

        final int rows = problem.getRestriction().length;
        double[] coefficients = problem.getCoefficients();

        double[] solution = new double[rows];
        for (int k = 0; k < rows; k++) {
            if (data[k] > 0.0) {
                coefficients[k] = 1.0;
                double[] help = this.internalOptimise(problem);
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

    protected abstract double[] internalOptimise(Problem problem) throws OptimiserException;
}
