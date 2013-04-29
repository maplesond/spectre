package uk.ac.uea.cmp.phygen.core.io.nexus;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.uea.cmp.phygen.core.ds.DistanceMatrix;
import uk.ac.uea.cmp.phygen.core.ds.split.CircularSplitSystem;
import uk.ac.uea.cmp.phygen.core.ds.split.SplitSystem;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: Sarah_2
 * Date: 24/04/13
 * Time: 21:24
 * To change this template use File | Settings | File Templates.
 */
public class NexusWriterTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();


    @Test
    public void testWriteNetwork() throws IOException {

        File outputDir = temp.newFolder("networkTest");

        File outputFile = new File(outputDir, "network.nex");

        DistanceMatrix distanceMatrix = new DistanceMatrix(5);

        SplitSystem ss = new CircularSplitSystem(distanceMatrix, new int[]{0,1,2,3,4});

        new NexusWriter().writeSplitSystem(outputFile, ss);

        // Check output file was created
        assertTrue(outputFile.exists());

        List<String> lines = FileUtils.readLines(outputFile);

        // Check we have the number of lines we were expecting
        assertTrue(lines.size() == 22);
    }

    @Test
    public void testWriteTree() throws IOException {

        File outputDir = temp.newFolder("treeTest");

        File outputFile = new File(outputDir, "tree.nex");

        DistanceMatrix distanceMatrix = new DistanceMatrix(5);

        SplitSystem ss = new CircularSplitSystem(distanceMatrix, new int[]{0,1,2,3,4});


        /*new NexusWriter().writeTree(outputFile, ss, ss.calculateTreeWeighting(distanceMatrix));

        // Check output file was created
        assertTrue(outputFile.exists());

        List<String> lines = FileUtils.readLines(outputFile);

        // Check we have the number of lines we were expecting
        assertTrue(lines.size() == 32);  */

    }
}
