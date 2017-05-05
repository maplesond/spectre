/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2017  UEA School of Computing Sciences
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

package uk.ac.uea.cmp.spectre.core.io.nexus.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.spectre.core.ds.Locations;
import uk.ac.uea.cmp.spectre.core.ui.gui.geom.IndexedPoint;

/**
 * Created by dan on 18/03/14.
 */
public class NexusLocationBuilder {

    private static Logger log = LoggerFactory.getLogger(NexusLocationBuilder.class);

    private int nbExpectedTaxa;

    private Locations locations;

    public NexusLocationBuilder() {

        this.nbExpectedTaxa = 0;
        this.locations = new Locations();
    }

    public int getNbExpectedTaxa() {
        return nbExpectedTaxa;
    }

    public void setNbExpectedTaxa(int nbExpectedTaxa) {
        this.nbExpectedTaxa = nbExpectedTaxa;
    }

    public Locations getLocations() {
        return locations;
    }

    public void setLocations(Locations locations) {
        this.locations = locations;
    }

    public Locations createLocations() {

        if (this.locations.size() != this.nbExpectedTaxa) {
            throw new IllegalStateException("Number of detected taxa (" + this.locations.size() + ") is not the number we expected (" + this.nbExpectedTaxa + ")");
        }

        return this.locations;
    }

    public void addLocation(IndexedPoint ip) {
        this.locations.add(ip);
    }
}
