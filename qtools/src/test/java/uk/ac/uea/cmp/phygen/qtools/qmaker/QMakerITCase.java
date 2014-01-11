package uk.ac.uea.cmp.phygen.qtools.qmaker;

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
public class QMakerITCase {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void sevenTaxa() throws IOException, OptimiserException {

        File treeFile = FileUtils.toFile(QMakerTest.class.getResource("/chopper/7-taxa.tre"));
        File outputDir = temp.newFolder();

        QMaker chopper = new QMaker();
        chopper.execute(treeFile, "newick", null, outputDir, "simpleTest");

        assertTrue(chopper.getQuartetFile().exists());

        List<String> lines = FileUtils.readLines(chopper.getQuartetFile());

        assertTrue(lines.size() == 45);
    }

    @Test
    public void sevenTaxaDeg2() throws IOException, OptimiserException {

        File treeFile = FileUtils.toFile(QMakerTest.class.getResource("/chopper/7-taxa-deg2.tre"));
        File outputDir = temp.newFolder();

        QMaker chopper = new QMaker();
        chopper.execute(treeFile, "newick", null, outputDir, "simpleTest");

        assertTrue(chopper.getQuartetFile().exists());

        List<String> lines = FileUtils.readLines(chopper.getQuartetFile());

        assertTrue(lines.size() == 45);
    }

    @Test
    public void singleTreeScript() throws IOException, OptimiserException {

        File treeFile = FileUtils.toFile(QMakerTest.class.getResource("/chopper/in.script"));
        File outputDir = temp.newFolder();

        QMaker chopper = new QMaker();
        chopper.execute(treeFile, "script", null, outputDir, "singleTreeScript");

        assertTrue(chopper.getQuartetFile().exists());

        List<String> lines = FileUtils.readLines(chopper.getQuartetFile());

        assertTrue(lines.size() == 35995);
    }
}
