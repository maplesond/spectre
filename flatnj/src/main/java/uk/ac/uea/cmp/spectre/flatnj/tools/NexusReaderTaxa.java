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

package uk.ac.uea.cmp.spectre.flatnj.tools;

import uk.ac.uea.cmp.spectre.flatnj.ds.Taxa;

import java.util.Arrays;
import java.util.LinkedList;


/**
 * @author balvociute
 */
public class NexusReaderTaxa extends NexusReader {
    LinkedList<String> labels;

    public NexusReaderTaxa() {
        block = "taxa";
        data = "taxlabels";
    }

    @Override
    protected void initializeDataStructures(Dimensions dimensions) {
        labels = new LinkedList<>();
    }

    @Override
    protected void parseLine(Format format) {
        line = line.replace("'", "");
        String[] l = line.split("\\s+");
        labels.addAll(Arrays.asList(l));
    }

    @Override
    protected Taxa createObject(Dimensions dimensions, Cycle cycle, Draw draw) {
        Taxa taxa = null;
        if (labels.size() > 0) {
            String[] labelsAsArray = new String[labels.size()];
            for (int i = 0; i < labelsAsArray.length; i++) {
                labelsAsArray[i] = labels.get(i);
            }
            taxa = new Taxa(labelsAsArray);
        }
        return taxa;
    }

}
