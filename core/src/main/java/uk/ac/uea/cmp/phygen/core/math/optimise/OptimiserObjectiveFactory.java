package uk.ac.uea.cmp.phygen.core.math.optimise;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 13/09/13
 * Time: 01:29
 * To change this template use File | Settings | File Templates.
 */
public interface OptimiserObjectiveFactory {

    Optimiser create(Objective objective) throws OptimiserException;
}
