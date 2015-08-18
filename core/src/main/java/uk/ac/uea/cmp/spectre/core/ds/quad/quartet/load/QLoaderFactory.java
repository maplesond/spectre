/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2015  UEA School of Computing Sciences
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package uk.ac.uea.cmp.spectre.core.ds.quad.quartet.load;

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
     *
     * @param identifier Identifier to check
     * @param service QLoader service
     * @return Whether the service accepts the given identifier
     */
    public boolean acceptsIdentifier(String identifier, QLoader service) {
        return service.acceptsExtension(identifier) ||
                identifier.equalsIgnoreCase(service.getName()) ||
                identifier.equalsIgnoreCase(service.getClass().getCanonicalName()) ||
                identifier.equalsIgnoreCase(service.getClass().getName());
    }
}
