package uk.ac.uea.cmp.spectre.core.io.emboss;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created by maplesod on 11/04/17.
 */
public class EmbossReaderTest {

    @Test
    public void testEmbossReaderAsh() throws IOException {

        File testFile = FileUtils.toFile(EmbossReaderTest.class.getResource("/ash.distmat"));

        DistanceMatrix distanceMatrix = new EmbossReader().readDistanceMatrix(testFile);

        assertTrue(distanceMatrix.size() == 58);
    }
}