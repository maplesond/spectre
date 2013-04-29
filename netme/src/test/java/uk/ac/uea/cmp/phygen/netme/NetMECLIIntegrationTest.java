package uk.ac.uea.cmp.phygen.netme;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 28/04/13
 * Time: 19:15
 * To change this template use File | Settings | File Templates.
 */
public class NetMECLIIntegrationTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testTreeBees() throws IOException {

        File outputDir = temporaryFolder.getRoot();

        File distancesFile = FileUtils.toFile(NetMECLIIntegrationTest.class.getResource("/bees.nex"));
        File coFile = FileUtils.toFile(NetMECLIIntegrationTest.class.getResource("/bees-tree.nex"));

        NetMECLI.main(new String[]{
                "--distances_file", distancesFile.getAbsolutePath(),
                "--circular_ordering_file", coFile.getAbsolutePath(),
                "--output", outputDir.getAbsolutePath()
        });

        assertTrue(outputDir.listFiles().length == 2);
    }

    @Test
    public void testColors() throws IOException {

        File outputDir = temporaryFolder.getRoot();

        File distancesFile = FileUtils.toFile(NetMECLIIntegrationTest.class.getResource("/colors.nex"));
        File coFile = FileUtils.toFile(NetMECLIIntegrationTest.class.getResource("/colors-network.nex"));

        NetMECLI.main(new String[]{
                "--distances_file", distancesFile.getAbsolutePath(),
                "--circular_ordering_file", coFile.getAbsolutePath(),
                "--output", outputDir.getAbsolutePath()
        });

        assertTrue(outputDir.listFiles().length == 2);
    }
}
