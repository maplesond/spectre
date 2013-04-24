package uk.ac.uea.cmp.phygen.core.io;

import uk.ac.uea.cmp.phygen.core.io.nexus.NexusReader;
import uk.ac.uea.cmp.phygen.core.io.phylip.PhylipReader;

/**
 * Created with IntelliJ IDEA.
 * User: Sarah_2
 * Date: 24/04/13
 * Time: 17:21
 * To change this template use File | Settings | File Templates.
 */
public enum PhygenReaderFactory {

    NEXUS {
        @Override
        public PhygenReader create() {
            return new NexusReader();
        }
    },
    PHYLIP {
        @Override
        public PhygenReader create() {
            return new PhylipReader();
        }
    };

    public abstract PhygenReader create();
}
