package uk.ac.uea.cmp.phygen.core.io.nexus;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import uk.ac.uea.cmp.phygen.core.ds.DistanceMatrix;
import uk.ac.uea.cmp.phygen.core.io.phylip.PhylipReader;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 28/04/13
 * Time: 20:40
 * To change this template use File | Settings | File Templates.
 */
public class NexusReaderTest {

    @Test
    public void testNexusReader() throws IOException {

        File testFile = FileUtils.toFile(NexusReaderTest.class.getResource("/bees.nex"));

        DistanceMatrix distanceMatrix = new NexusReader().read(testFile);

        assertTrue(distanceMatrix.size() == 6);
    }
}
