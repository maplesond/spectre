package uk.ac.uea.cmp.phygen.core.ds.distance;

import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 01/05/13
 * Time: 01:17
 * To change this template use File | Settings | File Templates.
 */
public interface DistanceGenerator {

    DistanceMatrix generateDistances(final int n);
}
