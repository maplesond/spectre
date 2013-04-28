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


    private final File testFile = FileUtils.toFile(NetMakeCLIIntegrationTest.class.getResource("/test.nex"));

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();



    @Test
    public void test1() throws IOException {

        File outputDir = temporaryFolder.getRoot();

        NetMakeCLI.main(new String[]{
                "--input", testFile.getAbsolutePath(),
                "--output", outputDir.getAbsolutePath(),
                "--weightings_1", "TREE"
        });

        assertTrue(true);
    }


}
