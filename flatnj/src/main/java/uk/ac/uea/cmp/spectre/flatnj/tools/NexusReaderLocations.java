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

package uk.ac.uea.cmp.spectre.flatnj.tools;

import uk.ac.uea.cmp.spectre.core.ui.gui.geom.IndexedPoint;
import uk.ac.uea.cmp.spectre.flatnj.Locations;

import java.util.LinkedList;

/**
 * Created by dan on 12/02/14.
 */
public class NexusReaderLocations extends NexusReader {
    LinkedList<IndexedPoint> llist;

    public NexusReaderLocations() {
        block = "locations";
    }


    @Override
    protected void initializeDataStructures(Dimensions dimensions) {
        llist = new LinkedList();
    }

    @Override
    protected void parseLine(Format format) {
        String taxLabel;
        double x;
        double y;
        if (format.labels) {
            matched = scanner.findInLine("(\\S+)\\s+(\\S+)\\s+(\\S+)");
            if (matched == null) {
                System.err.println("Missing taxa label in the locations block:\n\t" + line);
                System.exit(1);
            }
            taxLabel = scanner.match().group(1);
            try {
                x = Double.parseDouble(scanner.match().group(2));
                y = Double.parseDouble(scanner.match().group(3));
                llist.add(new IndexedPoint(-1, x, y, taxLabel));
            } catch (NumberFormatException nfe) {
                System.err.println("Error while parsing coordinates in the LOCATIONS block. Coordinates must be real numbers:\n\t" + line);
                System.exit(1);
            }
        } else {
            matched = scanner.findInLine("(\\S+)\\s+(\\S+)");
            if (matched == null) {
                System.err.println("Wrong entry in a matrix of the LOCATIONS block: \n\t" + line);
                System.exit(1);
            }
            try {
                x = Double.parseDouble(scanner.match().group(1));
                y = Double.parseDouble(scanner.match().group(2));
                llist.add(new IndexedPoint(-1, x, y));
            } catch (NumberFormatException nfe) {
                System.err.println("Error while parsing coordinates in the LOCATIONS block. Coordinates must be real numbers:\n\t" + line);
                System.exit(1);
            }
        }
    }

    @Override
    protected Locations createObject(Dimensions dimensions, Cycle cycle, Draw draw) {
        Locations locations = new Locations(llist);
        return locations;
    }
}
