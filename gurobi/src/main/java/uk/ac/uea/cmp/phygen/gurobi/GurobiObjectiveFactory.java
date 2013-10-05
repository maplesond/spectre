package uk.ac.uea.cmp.phygen.gurobi;

import uk.ac.uea.cmp.phygen.core.math.optimise.*;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 13/09/13
 * Time: 02:00
 * To change this template use File | Settings | File Templates.
 */
public class GurobiObjectiveFactory implements OptimiserObjectiveFactory {


    @Override
    public Optimiser create(Objective objective) throws OptimiserException {

        if (objective == Objective.LINEAR ||
                objective == Objective.MINIMA ||
                objective == Objective.BALANCED) {
            return new GurobiOptimiserLinear();
        }
        else if (objective == Objective.QUADRATIC) {
            return new GurobiOptimiserQuadratic();
        }
        else if (objective == Objective.NNLS) {
            return new GurobiOptimiserNNLS();
        }
        else if (objective == Objective.SCALING) {
            return new GurobiOptimiserScaler();
        }

        return null;
    }
}
