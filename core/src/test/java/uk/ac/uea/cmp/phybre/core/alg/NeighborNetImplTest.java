package uk.ac.uea.cmp.phybre.core.alg;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import uk.ac.uea.cmp.phybre.core.ds.Identifier;
import uk.ac.uea.cmp.phybre.core.ds.IdentifierList;
import uk.ac.uea.cmp.phybre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.phybre.core.ds.distance.FlexibleDistanceMatrix;
import uk.ac.uea.cmp.phybre.core.math.tuple.Triplet;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

/**
 * Created by dan on 02/03/14.
 */
public class NeighborNetImplTest {

    private String[] taxa;
    private double[][] distances1;
    private double[][] distances2;

    @Before
    public void setup() {

        this.taxa = new String[]{"A", "B", "C", "D", "E"};

        this.distances1 = new double[][]{
                {0, 3, 6, 6, 6},
                {3, 0, 6, 6, 6},
                {6, 6, 0, 3, 9},
                {6, 6, 3, 0, 9},
                {6, 6, 9, 9, 0}
        };

        this.distances2 = new double[][]{
                {0, 3, 6, 6, 6},
                {3, 0, 6, 6, 6},
                {6, 6, 0, 3, 9},
                {6, 6, 3, 0, 9},
                {6, 6, 9, 9, 0}
        };
    }


    @Test
    public void testReduction() {

        DistanceMatrix v2v = new FlexibleDistanceMatrix(new IdentifierList(taxa), distances1);

        final double aThird = 1.0 / 3.0;

        assertTrue(v2v.size() == 5);

        new NeighborNetImpl().reduction(new Triplet<>(1, 2, 3), new NeighborNetParams(aThird, aThird, aThird), v2v);

        assertTrue(v2v.size() == 4);

        double[][] matrix = v2v.getMatrix();

        assertTrue(matrix.length == 4);
        assertTrue(Arrays.equals(matrix[0], new double[]{0.0, 9.0, 6.0, 4.0}));
        assertTrue(Arrays.equals(matrix[1], new double[]{9.0, 0.0, 6.0, 8.0}));
        assertTrue(Arrays.equals(matrix[2], new double[]{6.0, 6.0, 0.0, 5.0}));
        assertTrue(Arrays.equals(matrix[3], new double[]{4.0, 8.0, 5.0, 0.0}));
    }

    @Test
    public void testSelectionStep1() {

        DistanceMatrix c2c = new FlexibleDistanceMatrix(new IdentifierList(taxa), distances1);

        Pair<Identifier, Identifier> selectedComponents = new NeighborNetImpl().selectionStep1(c2c);

        assertTrue(selectedComponents.getLeft().getName().equals("C"));
        assertTrue(selectedComponents.getRight().getName().equals("D"));
    }

    @Test
    public void testSelectionStep2() {

        String[] taxa = new String[]{"A", "B", "C", "D", "E"};

        double[][] distances = new double[][]{
                {0, 3, 6, 6, 6},
                {3, 0, 6, 6, 6},
                {6, 6, 0, 3, 9},
                {6, 6, 3, 0, 9},
                {6, 6, 9, 9, 0}
        };

        DistanceMatrix c2c = new FlexibleDistanceMatrix(new IdentifierList(taxa), distances1);

        Pair<Identifier, Identifier> selectedComponents = new NeighborNetImpl().selectionStep1(c2c);

        assertTrue(selectedComponents.getLeft().getName().equals("C"));
        assertTrue(selectedComponents.getRight().getName().equals("D"));
    }
}
