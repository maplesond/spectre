package uk.ac.uea.cmp.spectre.core.ds.distance;

import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.Sequences;

/**
 * Created by maplesod on 04/05/17.
 */
public abstract class AbstractDistanceMatrixCalculator implements DistanceMatrixCalculator {

    protected abstract double calculateDistance(final String s1, final String s2);

    @Override
    public DistanceMatrix generateDistances(Sequences seqs) {
        DistanceMatrix distanceMatrix = new FlexibleDistanceMatrix(new IdentifierList(seqs.getTaxaLabels()));

        final int n = seqs.size();
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                if (i != j) {
                    // Require a +1 offset on taxa index to get the correct Identifiers
                    distanceMatrix.setDistance(i+1, j+1, this.calculateDistance(seqs.getSeq(i), seqs.getSeq(j)));
                }
            }
        }

        return distanceMatrix;
    }
}
