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

package uk.ac.uea.cmp.phybre.flatnj.tools;

import uk.ac.uea.cmp.phybre.flatnj.ds.SplitSystem;

/**
 * @author balvociute
 */
public class NexusReaderSplits extends NexusReader {
    boolean[][] splits;
    boolean[] active;
    double[] weights;
    int currentSplit;

    public NexusReaderSplits() {
        block = "splits";
    }

    @Override
    protected void initializeDataStructures(Dimensions dimensions) {
        splits = new boolean[dimensions.nSplits][dimensions.nTax];
        active = new boolean[dimensions.nSplits];
        weights = new double[dimensions.nSplits];
        currentSplit = 0;
    }

    @Override
    protected void parseLine(Format format) {
        double wgh = 1.0;

        String toMatch = "";

        String sWgh = null;
        String sSpl = null;
        if (format.weights) {
            if (format.labels && format.confidences) {
                toMatch = "\\S+\\s+(\\S+)\\s+\\S+\\s+(.+)$";
            } else if (format.labels && !format.confidences) {
                toMatch = "\\S+\\s+(\\S+)\\s+(.+)$";
            } else if (!format.labels && format.confidences) {
                toMatch = "(\\S+)\\s+\\S+\\s+(.+)$";
            } else if (!format.labels && !format.confidences) {
                toMatch = "(\\S+)\\s+(.+)$";
            }
            matched = scanner.findInLine(toMatch);
            if (matched == null) {
                exitError("Wrong line in the SPLITS block");
            } else {
                sWgh = scanner.match().group(1);
                sSpl = scanner.match().group(2);
            }
        } else {
            if (format.labels && format.confidences) {
                toMatch = "\\S+\\s+\\S+\\s+(.+)\\s*,";
            } else if (format.labels && !format.confidences) {
                toMatch = "\\S+\\s+(.+)\\s*,";
            } else if (!format.labels && format.confidences) {
                toMatch = "\\S+\\s+(.+)\\s*,";
            } else if (!format.labels && !format.confidences) {
                toMatch = "(.+)\\s*,";
            }
            matched = scanner.findInLine(toMatch);
            if (matched == null) {
                exitError("Wrong line in the SPLITS block");
            } else {
                sSpl = scanner.match().group(1);
            }
        }
        if (sWgh != null) {
            try {
                wgh = Double.parseDouble(sWgh);
            } catch (NumberFormatException nfe) {
                exitError("Weight in the flatsplits block is not a real number");
            }
        }
        weights[currentSplit] = wgh;
        active[currentSplit] = true;

        String[] tmp = sSpl.split("\\s+");
        for (int i = 0; i < tmp.length; i++) {
            try {
                index = Integer.parseInt(tmp[i]) - 1;
            } catch (NumberFormatException nfe) {
                exitError("Taxa id is not an integer");
            }

            if (index < 0 || index > splits[0].length) {
                exitError("Wrong taxa id. Ssmaller than 1 or bigger than the number of taxa");
            }

            try {
                splits[currentSplit][index] = true;
            } catch (IndexOutOfBoundsException ioobe) {
                exitError("Wrong number of splits is bigger than indicated by nsplits");
            }
        }
        currentSplit++;
    }

    @Override
    protected SplitSystem createObject(Dimensions dimensions, Cycle cycle, Draw draw) {
        return new SplitSystem(splits, weights, cycle.permutation, active);
    }

}
