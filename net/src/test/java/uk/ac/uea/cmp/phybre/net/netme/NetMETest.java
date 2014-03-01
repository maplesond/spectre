package uk.ac.uea.cmp.phybre.net.netme;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.junit.Before;
import org.junit.Test;
import uk.ac.uea.cmp.phybre.core.ds.Taxa;
import uk.ac.uea.cmp.phybre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.phybre.core.ds.distance.FlexibleDistanceMatrix;
import uk.ac.uea.cmp.phybre.core.ds.split.CircularOrdering;
import uk.ac.uea.cmp.phybre.core.ds.split.Split;
import uk.ac.uea.cmp.phybre.core.math.Equality;

import static org.junit.Assert.assertTrue;

/**
 * Created by dan on 21/01/14.
 */
public class NetMETest {

    @Before
    public void setup() {

        BasicConfigurator.configure();
        LogManager.getRootLogger().setLevel(Level.WARN);
    }

    @Test
    public void simpleTest() {

        String[] taxa = new String[]{"1", "2", "3", "4", "5", "6", "7"};

        double[][] distances = new double[][]{
                {0, 2, 5, 5, 5, 5, 3},
                {2, 0, 5, 5, 5, 5, 3},
                {5, 5, 0, 2, 4, 4, 4},
                {5, 5, 2, 0, 4, 4, 4},
                {5, 5, 4, 4, 0, 2, 4},
                {5, 5, 4, 4, 2, 0, 4},
                {3, 3, 4, 4, 4, 4, 0}
        };


        DistanceMatrix distanceMatrix = new FlexibleDistanceMatrix(new Taxa(taxa), distances);

        CircularOrdering circularOrdering = new CircularOrdering(new int[] {7,2,1,3,4,5,6});

        NetMEResult result = new NetME().execute(distanceMatrix, circularOrdering);

        assertTrue(result.getMeTree().getNbSplits() == 11);

        for(Split s : result.getMeTree().getSplits()) {
            assertTrue(Equality.approxEquals(s.getWeight(), 1.0, 0.01));
        }

        assertTrue(result.getOriginalMETree().getNbSplits() == 11);

        for(Split s : result.getOriginalMETree().getSplits()) {
            assertTrue(Equality.approxEquals(s.getWeight(), 1.0, 0.000001));
        }

        assertTrue(true);
    }
}
