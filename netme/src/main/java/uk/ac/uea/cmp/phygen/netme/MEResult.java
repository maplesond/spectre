package uk.ac.uea.cmp.phygen.netme;

import uk.ac.uea.cmp.phygen.core.ds.split.CompatibleSplitSystem;
import uk.ac.uea.cmp.phygen.core.ds.split.TreeWeights;
import uk.ac.uea.cmp.phygen.core.ds.split.CircularSplitSystem;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 27/04/13
 * Time: 17:09
 * To change this template use File | Settings | File Templates.
 */
public class MEResult {

    private CompatibleSplitSystem originalMETree;
    private CompatibleSplitSystem meTree;

    public MEResult(CompatibleSplitSystem originalMETree, CompatibleSplitSystem meTree) {
        this.originalMETree = originalMETree;
        this.meTree = meTree;
    }

    public CompatibleSplitSystem getMeTree() {
        return meTree;
    }

    public CompatibleSplitSystem getOriginalMETree() {
        return originalMETree;
    }
}
