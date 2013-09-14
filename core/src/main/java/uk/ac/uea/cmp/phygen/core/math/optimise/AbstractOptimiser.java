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

        double[] solution = objective.optimise(problem, this);

        // Probably used a lot of memory.  Collect Garbage to save space.
        System.gc();

        return solution;
    }

    protected abstract double[] optimise2(Objective objective, Problem problem) throws OptimiserException;
}
