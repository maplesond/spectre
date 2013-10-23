package uk.ac.uea.cmp.phygen.core.math.optimise;

import org.apache.commons.lang3.time.StopWatch;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 13/09/13
 * Time: 00:37
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractOptimiser implements Optimiser {

    @Override
    public Solution optimise(Problem problem) throws OptimiserException {

        // Check the optimiser is operational
        if (!this.isOperational()) {
            throw new UnsupportedOperationException(this.getIdentifier() + " is not operational");
        }

        // Check we have an objective
        if (problem.getObjective() == null || problem.getObjective().getExpression() == null) {
            throw new OptimiserException("An objective must be specified for " + this.getIdentifier());
        }

        // Check we have variables
        if (problem.getVariables() == null || problem.getVariables().isEmpty()) {
            throw new OptimiserException("At least one problem variable must be specified to find a solution for " + this.getIdentifier());
        }

        // Check this solver can handle the objective type
        if (!this.acceptsObjectiveType(problem.getObjectiveType())) {
            throw new UnsupportedOperationException("This optimiser: " + this.getIdentifier() + "; cannot handle objective type: " +
                problem.getObjective().getName() + "; which is: " + problem.getObjectiveType().toString());
        }

        // Check this solver can handle the objective direction
        if (!this.acceptsObjectiveDirection(problem.getObjectiveDirection())) {
            throw new UnsupportedOperationException("This optimiser: " + this.getIdentifier() + "; cannot handle objective direction: " +
                    problem.getObjective().getName() + "; which is: " + problem.getObjectiveDirection().toString());
        }

        // Check this solver can handle the constraints
        if (!this.acceptsConstraintType(problem.getConstraintType())) {
            throw new UnsupportedOperationException("This optimiser: " + this.getIdentifier() + "; cannot handle constraint type: " +
                    problem.getConstraintType().toString());
        }

        // Start the timer
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // Run the solver on the problem to get a (hopefully) optimal solution
        Solution solution = this.internalOptimise(problem);

        // Stop the timer
        stopWatch.stop();
        solution.setTimeTaken(stopWatch.toString());

        // Probably used a lot of memory.  Collect Garbage to save space.
        System.gc();

        return solution;
    }


    protected abstract Solution internalOptimise(Problem problem) throws OptimiserException;
}
