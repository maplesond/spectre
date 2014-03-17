package uk.ac.uea.cmp.spectre.core.alg;

import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.split.CompatibleSplitSystem;

/**
 * Created by dan on 27/02/14.
 */
public interface NeighborNet {

    /**
     * Neighbornet takes in a distance matrix (which will have a set of taxa embedded within it) and to parameters, the
     * sum of which should equal 1.0.  The result of neighbornet is a compatible split system which should contain a
     * circular ordering
     *
     * @param distanceMatrix The distance matrix to process, which should also contain the set of taxa
     * @param params         Alpha, Beta and Gamma parameters
     * @return The result of neighbornet, a compatible split system
     */
    CompatibleSplitSystem execute(DistanceMatrix distanceMatrix, NeighborNetParams params);
}
