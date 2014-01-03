package uk.ac.uea.cmp.phygen.superq.qnet;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.tgac.metaopt.OptimiserException;

import java.io.File;

import static junit.framework.TestCase.assertFalse;

/**
 * Created with IntelliJ IDEA.
 * User: maplesod
 * Date: 02/01/14
 * Time: 15:35
 * To change this template use File | Settings | File Templates.
 */
public class QNetITCase {

    @Rule
    public TemporaryFolder folder= new TemporaryFolder();

    private File simpleOutput;

    @Before
    public void setUp() throws Exception {
        simpleOutput = folder.newFolder("simple");
    }

    protected QNetOptions createSimpleOptions() throws OptimiserException {

        QNetOptions options = new QNetOptions(
                FileUtils.toFile(QNetITCase.class.getResource("/simple/in.script")),
                new File(simpleOutput, "simple.out"),
                null,
                false,
                -1.0
        );

        return options;
    }

    @Test
    public void testSimpleScript() throws OptimiserException {

        QNetOptions options = this.createSimpleOptions();

        QNet qnet = new QNet(options, null);

        qnet.run();

        if (qnet.failed()) {
            System.err.println(qnet.getFullErrorMessage());
        }

        assertFalse(qnet.failed());
    }
}
