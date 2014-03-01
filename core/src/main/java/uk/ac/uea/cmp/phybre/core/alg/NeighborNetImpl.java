package uk.ac.uea.cmp.phybre.core.alg;

import uk.ac.uea.cmp.phybre.core.ds.Taxa;
import uk.ac.uea.cmp.phybre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.phybre.core.ds.distance.FlexibleDistanceMatrix;
import uk.ac.uea.cmp.phybre.core.ds.split.CompatibleSplitSystem;
import uk.ac.uea.cmp.phybre.core.ds.split.SimpleSplitSystem;
import uk.ac.uea.cmp.phybre.core.ds.split.SplitSystem;
import uk.ac.uea.cmp.phybre.core.ds.split.SplitUtils;
import uk.ac.uea.cmp.phybre.core.math.tuple.Triplet;

import java.util.List;

/**
 * Created by dan on 27/02/14.
 */
public class NeighborNetImpl implements NeighborNet {


    @Override
    public CompatibleSplitSystem execute(DistanceMatrix distanceMatrix, double alpha, double beta) {

        // Validation of the parameters
        if (alpha + beta < 1.0) {
            throw new IllegalArgumentException("alpha and beta to not sum to 1.0.  alpha: " + alpha + "; beta: " + beta);
        }

        // Make a shortcut to the taxa used in the distance matrix
        Taxa taxa = distanceMatrix.getTaxa();

        // Creates a set of trivial splits for ...
        SplitSystem components = new SimpleSplitSystem(taxa, SplitUtils.createTrivialSplits(taxa, 1.0));

        // Creates a simple split system with trivial splits from the given distance matrix
        SplitSystem treeSplits = new SimpleSplitSystem(taxa, SplitUtils.createTrivialSplits(taxa, 1.0));


        // DistanceMatrix between components (make deep copy from initial distance matrix)
        DistanceMatrix c2c = new FlexibleDistanceMatrix(distanceMatrix);

        // DistanceMatrix between components and vertices (make deep copy from initial distance matrix)
        DistanceMatrix c2v = new FlexibleDistanceMatrix(distanceMatrix);

        // DistanceMatrix between verticies and vertices (make deep copy from initial distance matrix)
        DistanceMatrix v2v = new FlexibleDistanceMatrix(distanceMatrix);


        return null;
    }


    /**
     * Reduces the 3 selected verticies down to 2 new verticies.  The output takes the form of an updated distance
     * matrix represented by v2v and an updated split system
     * @param verticies The selected verticies to reduce
     * @param alpha alpha parameter
     * @param beta beta parameter
     * @param v2v the distance matrix to update based on this reduction
     * @param components the split system to update based on this reduction
     */
    protected void reduction(final Triplet<Integer> verticies, final double alpha, final double beta,
                             DistanceMatrix v2v, SplitSystem components) {

        // Get the taxa ids
        List<Integer> activeVerticies = v2v.getTaxa().getIdsAsLinkedList();

        int maxId = v2v.getTaxa().getMaxId();

        int newVertex1 = maxId+1;
        int newVertex2 = maxId+2;
        maxId = maxId+2;

        double[] vertex1Distances = new double[v2v.size() - 2];
        double[] vertex2Distances = new double[v2v.size() - 1];


        // Iterate over all active verticies
        for(int i = 0; i < activeVerticies.size(); i++) {

            int vertex = activeVerticies.get(i);

            // Only process this vertex if it is not in the selected vertex list
            if (vertex != verticies.getA() && vertex != verticies.getB() && vertex != verticies.getC()) {

                //v2v.setDistance(newVertex1, i, (alpha + beta) * v2v.;


            }

        }

    }
}
