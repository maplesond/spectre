/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */
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

        for (PhygenReaderFactory prf : PhygenReaderFactory.values()) {

            for (String ext : prf.getValidExtensions()) {
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
