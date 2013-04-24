package uk.ac.uea.cmp.phygen.core.io;

import uk.ac.uea.cmp.phygen.core.io.nexus.NexusWriter;

/**
 * Created with IntelliJ IDEA.
 * User: Sarah_2
 * Date: 24/04/13
 * Time: 20:12
 * To change this template use File | Settings | File Templates.
 */
public enum PhygenWriterFactory {

    NEXUS {
        @Override
        public PhygenWriter create() {
            return new NexusWriter();
        }
    },
    PHYLIP {
        @Override
        public PhygenWriter create() {
            throw new UnsupportedOperationException("PhylipWriter not implemented yet");
        }
    };

    public abstract PhygenWriter create();
}
