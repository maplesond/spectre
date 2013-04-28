package uk.ac.uea.cmp.phygen.core.io.nexus;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.uea.cmp.phygen.core.ds.Distances;
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

        SplitSystem ss = new CircularSplitSystem(new int[]{1,2,3,4,5});

        Distances distances = new Distances(5);

        new NexusWriter().writeNetwork(outputFile, ss, distances);

        // Check output file was created
        assertTrue(outputFile.exists());

        List<String> lines = FileUtils.readLines(outputFile);

        // Check we have the number of lines we were expecting
        assertTrue(lines.size() == 32);
    }

    @Test
    public void testWriteTree() throws IOException {

        File outputDir = temp.newFolder("treeTest");

        File outputFile = new File(outputDir, "tree.nex");

        SplitSystem ss = new CircularSplitSystem(new int[]{1,2,3,4,5});

        Distances distances = new Distances(5);

        /*new NexusWriter().writeTree(outputFile, ss, ss.calculateTreeWeighting(distances));

        // Check output file was created
        assertTrue(outputFile.exists());

        List<String> lines = FileUtils.readLines(outputFile);

        // Check we have the number of lines we were expecting
        assertTrue(lines.size() == 32);  */

    }
}
