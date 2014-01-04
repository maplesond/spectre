package uk.ac.uea.cmp.phygen.tools.quart;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.tgac.metaopt.OptimiserException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by dan on 15/12/13.
 */
public class QuartITCase {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void sevenTaxa() throws IOException, OptimiserException {

        File treeFile = FileUtils.toFile(QuartTest.class.getResource("/chopper/7-taxa.tre"));
        File outputDir = temp.newFolder();

        Quart chopper = new Quart();
        chopper.execute(treeFile, "newick", null, outputDir, "simpleTest");

        assertTrue(chopper.getQuartetFile().exists());

        List<String> lines = FileUtils.readLines(chopper.getQuartetFile());

        assertTrue(lines.size() == 45);
    }

    @Test
    public void sevenTaxaDeg2() throws IOException, OptimiserException {

        File treeFile = FileUtils.toFile(QuartTest.class.getResource("/chopper/7-taxa-deg2.tre"));
        File outputDir = temp.newFolder();

        Quart chopper = new Quart();
        chopper.execute(treeFile, "newick", null, outputDir, "simpleTest");

        assertTrue(chopper.getQuartetFile().exists());

        List<String> lines = FileUtils.readLines(chopper.getQuartetFile());

        assertTrue(lines.size() == 45);
    }

    @Test
    public void singleTreeScript() throws IOException, OptimiserException {

        File treeFile = FileUtils.toFile(QuartTest.class.getResource("/chopper/in.script"));
        File outputDir = temp.newFolder();

        Quart chopper = new Quart();
        chopper.execute(treeFile, "script", null, outputDir, "singleTreeScript");

        assertTrue(chopper.getQuartetFile().exists());

        List<String> lines = FileUtils.readLines(chopper.getQuartetFile());

        assertTrue(lines.size() == 35995);
    }
}
