package uk.ac.uea.cmp.phygen.netme;

import uk.ac.uea.cmp.phygen.core.ds.split.CompatibleSplitSystem;

/**
 * Created with IntelliJ IDEA.
 * User: Dan
 * Date: 27/04/13
 * Time: 17:09
 * To change this template use File | Settings | File Templates.
 */
public class NetMEResult {

    private CompatibleSplitSystem originalMETree;
    private CompatibleSplitSystem meTree;

    public NetMEResult(CompatibleSplitSystem originalMETree, CompatibleSplitSystem meTree) {
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
