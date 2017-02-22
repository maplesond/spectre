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

package uk.ac.uea.cmp.spectre.core.io;

import org.apache.commons.lang3.StringUtils;
import uk.ac.uea.cmp.spectre.core.io.nexus.NexusWriter;
import uk.ac.uea.cmp.spectre.core.io.phylip.PhylipWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sarah_2
 * Date: 24/04/13
 * Time: 20:12
 * To change this template use File | Settings | File Templates.
 */
public enum SpectreWriterFactory {

    NEXUS {
        @Override
        public SpectreWriter create() {
            return new NexusWriter();
        }

        @Override
        public String[] getValidExtensions() {
            return new String[]{"nex", "nxs", "nexus"};
        }
    },
    PHYLIP {
        @Override
        public SpectreWriter create() {
            return new PhylipWriter();
        }

        @Override
        public String[] getValidExtensions() {
            return new String[]{"phy", "phylip"};
        }
    };

    public abstract SpectreWriter create();

    public abstract String[] getValidExtensions();

    public static SpectreWriter create(String fileExtension) {

        for (SpectreWriterFactory pwf : SpectreWriterFactory.values()) {

            for (String ext : pwf.getValidExtensions()) {
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

    public static String listWriters() {

        List<String> writerStrings = new ArrayList<String>();

        for (SpectreWriterFactory mode : SpectreWriterFactory.values()) {
            writerStrings.add(mode.name());
        }

        return "[" + StringUtils.join(writerStrings, ", ") + "]";
    }
}
