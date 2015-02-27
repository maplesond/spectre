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

package uk.ac.uea.cmp.spectre.flatnj.tools;

import uk.ac.uea.cmp.spectre.flatnj.ds.Quadruple;
import uk.ac.uea.cmp.spectre.flatnj.ds.QuadrupleSystem;

/**
 * @author balvociute
 */
public class NexusReaderQuadruples extends NexusReader {
    QuadrupleSystem qs;

    public NexusReaderQuadruples() {
        block = "quadruples";
    }

    @Override
    protected void initializeDataStructures(Dimensions dimensions) {
        qs = new QuadrupleSystem(dimensions.nTax);
    }

    @Override
    protected void parseLine(Format format) {
        String[] tmp = line.split(":");
        if (format.labels && tmp.length < 2) {
            System.err.println("Missing labels in the QUADRUPLES block:\n\t" + line);
            System.exit(1);
        } else {
            String[] taxaPart = tmp[tmp.length - 2].trim().split("\\s+");
            String[] weightsPart = tmp[tmp.length - 1].replace(",", "").trim().split("\\s+");
            if (taxaPart.length != 4) {
                exitError("Wrong number of taxa the quadruple. Must be 4, but found " + taxaPart.length);
            }
            if (weightsPart.length != 7) {
                exitError("Wrong number of quadruple split weights in the quadruple. Must be 7, but found " + weightsPart.length);
            }
            int[] taxa = new int[taxaPart.length];
            for (int i = 0; i < taxa.length; i++) {
                try {
                    taxa[i] = Integer.parseInt(taxaPart[i]);
                } catch (NumberFormatException nfe) {
                    exitError("Taxa indexes in the quadruples must be integers");
                }
            }
            double[] weights = new double[weightsPart.length];
            for (int i = 0; i < weights.length; i++) {
                try {
                    weights[i] = Double.parseDouble(weightsPart[i]);
                } catch (NumberFormatException nfe) {
                    exitError("Quadruple split weights must be real numbers");
                }
            }
            qs.add(new Quadruple(taxa, weights));
        }
    }

    @Override
    protected QuadrupleSystem createObject(Dimensions dimensions, Cycle cycle, Draw draw) {
        return qs;
    }

}
