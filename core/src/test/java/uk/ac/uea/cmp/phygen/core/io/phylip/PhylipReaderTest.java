package uk.ac.uea.cmp.phygen.core.io.phylip;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 28/04/13
 * Time: 20:37
 * To change this template use File | Settings | File Templates.
 */
public class PhylipReaderTest {

    @Test
    public void testPhylipReader() throws IOException {

        File testFile = FileUtils.toFile(PhylipReaderTest.class.getResource("/colors.phy"));

        DistanceMatrix distanceMatrix = new PhylipReader().read(testFile);

        assertTrue(distanceMatrix.size() == 10);
    }
}
