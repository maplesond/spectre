package uk.ac.uea.cmp.phygen.qtools.qnet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.BasicConfigurator;
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
import uk.ac.uea.cmp.phygen.qtools.qmaker.QMaker;

import java.io.File;
import java.io.IOException;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

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
        BasicConfigurator.configure();
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

    protected QuartetSystemCombiner create2ConflictingTreesWithSameFiveTaxa() throws IOException {

        NewickTree tree1 = new NewickTree("(((A:1,B:1):1,C:1),(D:1,E:1):1);");

        NewickTree tree2 = new NewickTree("(((A:1,B:1):1,D:1),(C:1,E:1):1);");

        QuartetSystemList qsl = new QuartetSystemList();
        qsl.add(new QuartetSystem(tree1));
        qsl.add(new QuartetSystem(tree2));

        return new QMaker().execute(qsl);
    }

    @Test
    public void test5TaxaTree() throws OptimiserException, IOException, QNetException {

        NewickTree tree = new NewickTree("(((A:1,B:1):1,C:1),(D:1,E:1):1);");
        QuartetSystemList qsl = new QuartetSystemList(new QuartetSystem(tree));
        QuartetSystemCombiner qsc = new QMaker().execute(qsl);
        QNetResult result = new QNet().execute(qsc, false, -1.0, null);

        // Check circular ordering
        assertTrue(ArrayUtils.isEquals(result.getCircularOrdering().toArray(), new int[]{2,1,3,4,5}));

        CircularSplitSystem ss = result.createSplitSystem(null, QNetResult.SplitLimiter.STANDARD);
        assertTrue(true);
    }

    @Test
    public void test2ConflictingTreesInternal() throws OptimiserException, IOException, QNetException {

        QuartetSystemCombiner qsc = create2ConflictingTreesWithSameFiveTaxa();
        QNetResult result = new QNet().execute(qsc, false, -1.0, null);

        // Check circular ordering
        assertTrue(ArrayUtils.isEquals(result.getCircularOrdering().toArray(),
                new int[]{3,1,2,4,5}));

        CircularSplitSystem ss = result.createSplitSystem(null, QNetResult.SplitLimiter.STANDARD);


        assertTrue(true);
    }

    /*@Test
    public void test2ConflictingTreesJOptimiser() throws OptimiserException, IOException, QNetException {

        QuartetSystemCombiner qsc = create2ConflictingTreesWithSameFiveTaxa();
        QNetResult result = new QNet().execute(qsc, false, -1.0, new JOptimizer());

        // Check circular ordering
        assertTrue(ArrayUtils.isEquals(result.getCircularOrdering().toArray(),
                new int[]{3,1,2,4,5}));

        CircularSplitSystem ss = result.createSplitSystem(null, QNetResult.SplitLimiter.STANDARD);


        assertTrue(true);
    } */

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
