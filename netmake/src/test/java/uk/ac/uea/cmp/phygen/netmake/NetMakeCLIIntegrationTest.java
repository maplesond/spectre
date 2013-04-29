package uk.ac.uea.cmp.phygen.netmake;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.uea.cmp.phygen.netmake.weighting.Weightings;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 27/04/13
 * Time: 17:37
 * To change this template use File | Settings | File Templates.
 */
public class NetMakeCLIIntegrationTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testTree1() throws IOException {

        File outputDir = temporaryFolder.getRoot();

        File testFile1 = FileUtils.toFile(NetMakeCLIIntegrationTest.class.getResource("/test.nex"));

        NetMakeCLI.main(new String[]{
                "--input", testFile1.getAbsolutePath(),
                "--output", outputDir.getAbsolutePath(),
                "--weightings_1", "TREE"
        });

        assertTrue(outputDir.listFiles().length == 2);
    }

    @Test
    public void testTreeBees() throws IOException {

        File outputDir = temporaryFolder.getRoot();

        File testFile2 = FileUtils.toFile(NetMakeCLIIntegrationTest.class.getResource("/bees.nex"));

        NetMakeCLI.main(new String[]{
                "--input", testFile2.getAbsolutePath(),
                "--output", outputDir.getAbsolutePath(),
                "--weightings_1", "TREE"
        });

        assertTrue(outputDir.listFiles().length == 2);
    }
}
