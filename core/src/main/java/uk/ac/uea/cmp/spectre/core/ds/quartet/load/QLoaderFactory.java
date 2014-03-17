package uk.ac.uea.cmp.spectre.core.ds.quartet.load;

import uk.ac.uea.cmp.spectre.core.util.SpiFactory;

/**
 * Created by dan on 12/01/14.
 */
public class QLoaderFactory extends SpiFactory<QLoader> {

    public QLoaderFactory() {
        super(QLoader.class);
    }

    /**
     * This default method accepts an identifier if it equals the service's name, or if it matches the class names.
     * @param identifier
     * @param service
     * @return Whether the service accepts the given identifier
     */
    public boolean acceptsIdentifier(String identifier, QLoader service) {
        return  service.acceptsExtension(identifier) ||
                identifier.equalsIgnoreCase(service.getName()) ||
                identifier.equalsIgnoreCase(service.getClass().getCanonicalName()) ||
                identifier.equalsIgnoreCase(service.getClass().getName());
    }
}
