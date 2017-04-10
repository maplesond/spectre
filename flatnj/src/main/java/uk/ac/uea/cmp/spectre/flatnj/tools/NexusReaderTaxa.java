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

import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;

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
    protected IdentifierList createObject(Dimensions dimensions, Cycle cycle, Draw draw) {
        IdentifierList taxa = null;
        if (labels.size() > 0) {
            String[] labelsAsArray = new String[labels.size()];
            for (int i = 0; i < labelsAsArray.length; i++) {
                labelsAsArray[i] = labels.get(i);
            }
            taxa = new IdentifierList(labelsAsArray);
        }
        return taxa;
    }

}
