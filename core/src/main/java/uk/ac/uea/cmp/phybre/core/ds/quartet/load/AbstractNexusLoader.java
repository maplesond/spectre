package uk.ac.uea.cmp.phybre.core.ds.quartet.load;

import uk.ac.uea.cmp.phybre.core.io.nexus.NexusFileFilter;

/**
 * Created by dan on 12/01/14.
 */
public abstract class AbstractNexusLoader extends AbstractQLoader {

    @Override
    public boolean acceptsExtension(String ext) {
        for(String fe : new NexusFileFilter().commonFileExtensions()) {
            if (fe.equalsIgnoreCase(ext)) {
                return true;
            }
        }

        return false;
    }
}
