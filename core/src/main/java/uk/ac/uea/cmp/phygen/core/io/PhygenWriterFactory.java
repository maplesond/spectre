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

        @Override
        public String[] getValidExtensions() {
            return new String[]{"nex", "nexus"};
        }
    };

    public abstract PhygenWriter create();
    public abstract String[] getValidExtensions();

    public static PhygenWriter create(String fileExtension) {

        for(PhygenWriterFactory pwf : PhygenWriterFactory.values()) {

            for(String ext : pwf.getValidExtensions()) {
                if (ext.equalsIgnoreCase(fileExtension)) {
                    return pwf.create();
                }
            }
        }

        return null;
    }

    public String getPrimaryExtension() {
        return this.getValidExtensions()[0];
    }
}
