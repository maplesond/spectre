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

import uk.ac.uea.cmp.spectre.core.ds.Alignment;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by dan on 12/02/14.
 */
public class NexusReaderAlignment extends NexusReader {
    Map<String, String> aln;
    boolean labels = false;

    public NexusReaderAlignment(String block) {
        this.block = block.toLowerCase();
    }

    @Override
    protected void initializeDataStructures(Dimensions dimensions) {
        aln = new LinkedHashMap();
    }

    @Override
    protected void parseLine(Format format) {
        if (format.labels) {
            labels = true;
            matched = scanner.findInLine("(\\S+)\\s+(.+)");
            if (matched == null) {
                System.err.println("Missing taxa labels in the alignment.");
                System.exit(1);
            }
            String taxLabel = scanner.match().group(1);
            String sequence = scanner.match().group(2);
            sequence = sequence.replace("\\s", "");
            if (format.interleaved) {
                sequence = (aln.get(taxLabel) != null) ? aln.get(taxLabel).concat(sequence) : sequence;
            }
            aln.put(taxLabel, sequence);
        } else {
            String sequence = line.replace("\\s", "");
            aln.put(String.valueOf(index), sequence);
            index++;
        }
    }

    @Override
    protected Alignment createObject(Dimensions dimensions, Cycle cycle, Draw draw) {
        Alignment a = new Alignment(aln);
        if (!a.sameLength()) {
            a = null;
            System.err.println("Sequences in the alignment are not the same length.");
            System.exit(1);
        }
        if (!labels) {
            a.setTaxaLabels(null);
        }
        return a;
    }

}
