package uk.ac.uea.cmp.phygen.core.io;

import uk.ac.uea.cmp.phygen.core.ds.Distances;
import uk.ac.uea.cmp.phygen.core.io.nexus.NexusReader;
import uk.ac.uea.cmp.phygen.core.io.phylip.PhylipReader;

import java.io.File;
import java.io.IOException;

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

        @Override
        public String[] getValidExtensions() {
            return new String[]{"nex", "nexus"};
        }
    },
    PHYLIP {
        @Override
        public PhygenReader create() {
            return new PhylipReader();
        }

        @Override
        public String[] getValidExtensions() {
            return new String[]{"phy", "phylip"};
        }
    };

    public abstract PhygenReader create();
    public abstract String[] getValidExtensions();

    public static PhygenReader create(String fileExtension) {

        for(PhygenReaderFactory prf : PhygenReaderFactory.values()) {

            for(String ext : prf.getValidExtensions()) {
                if (ext.equalsIgnoreCase(fileExtension)) {
                    return prf.create();
                }
            }
        }

        return null;
    }

    public String getPrimaryExtension() {
        return this.getValidExtensions()[0];
    }
}
