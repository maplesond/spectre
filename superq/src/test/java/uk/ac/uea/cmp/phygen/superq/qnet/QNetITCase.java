package uk.ac.uea.cmp.phygen.superq.qnet;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.tgac.metaopt.OptimiserException;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetSystem;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetSystemCombiner;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetSystemList;
import uk.ac.uea.cmp.phygen.core.ds.split.CircularSplitSystem;
import uk.ac.uea.cmp.phygen.core.ds.tree.newick.NewickTree;
import uk.ac.uea.cmp.phygen.tools.quart.Quart;

import java.io.File;
import java.io.IOException;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

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
    public void test5TaxaTree() throws OptimiserException, IOException, QNetException {

        NewickTree tree = new NewickTree("(((A:1,B:1):1,C:1),(D:1,E:1):1);");
        QuartetSystemList qsl = new QuartetSystemList(new QuartetSystem(tree));
        QuartetSystemCombiner qsc = new Quart().execute(qsl);
        QNetResult result = new QNet().execute(qsc, false, -1.0, null);
        CircularSplitSystem ss = result.createSplitSystem(null, QNetResult.SplitLimiter.STANDARD);
        assertTrue(true);
    }

    @Test
    public void test2ConflictingTrees() throws OptimiserException, IOException, QNetException {

        NewickTree tree1 = new NewickTree("(((A:1,B:1):1,C:1),(D:1,E:1):1);");

        NewickTree tree2 = new NewickTree("(((A:1,B:1):1,D:1),(C:1,E:1):1);");

        QuartetSystemList qsl = new QuartetSystemList();
        qsl.add(new QuartetSystem(tree1));
        qsl.add(new QuartetSystem(tree2));

        QuartetSystemCombiner qsc = new Quart().execute(qsl);
        QNetResult result = new QNet().execute(qsc, false, -1.0, null);

        CircularSplitSystem ss = result.createSplitSystem(null, QNetResult.SplitLimiter.STANDARD);


        assertTrue(true);
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
