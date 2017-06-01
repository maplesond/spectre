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
import uk.ac.uea.cmp.spectre.core.ds.Sequences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 01/12/13
 * Time: 21:33
 * To change this template use File | Settings | File Templates.
 */
public class NexusCharacterBuilder {

    private static Logger log = LoggerFactory.getLogger(NexusCharacterBuilder.class);

    private int expectedNbChars;
    private int expectedNbSeqs;
    private Sequences.Format format;
    private List<String> seqs;

    public NexusCharacterBuilder() {
        this.expectedNbChars = 0;
        this.expectedNbSeqs = 0;
        this.seqs = new ArrayList<String>();
        this.format = new Sequences.Format();
    }


    public Sequences createAlignments() {

        Map<String,String> alns = new TreeMap<>();
        if (!format.labels) {
            for (int i = 0; i < this.seqs.size(); i++) {
                alns.put(Integer.toString(i+1), seqs.get(i));
            }
        }
        else {
            if (this.seqs.size() % 2 != 0) {
                throw new IllegalStateException("Expected that a character matrix with labels would have an even number of entries.  Found " + this.seqs.size() + " entries, including labels.");
            }
            for (int i = 0; i < this.seqs.size(); i+=2) {
                String id = seqs.get(i);
                String seq = seqs.get(i+1);

                if (alns.containsKey(id)) {
                    alns.put(id, alns.get(id) + seq);
                }
                else {
                    alns.put(id, seq);
                }
            }
        }

        Sequences s = new Sequences(alns);
        s.setFormat(this.format);

        // Validation
        if (this.expectedNbSeqs > 0 && this.expectedNbSeqs != s.size()) {
            throw new IllegalStateException("Nexus file contains " + s.size() + " sequences however we expected to find " + this.expectedNbSeqs + " sequences in the Character/Data block.");
        }

        int i = 0;
        for (String seq : s.getSequences()) {
            String label = s.getTaxaLabels()[i];
            if (this.expectedNbChars > 0 && seq.length() != this.expectedNbChars) {
                log.warn("Sequence " + i + "(" + label + ") has an unexpected size.  Sequence contains " + seq.length() + " characters but Nexus Characters block specified " + this.expectedNbChars + " characters per sequence.");
            }

            // Check that sequence is valid according to spec provided
            boolean valid = true;
            for (int j = 0; j < seq.length(); j++) {
                char c = seq.charAt(j);
                if (this.format.symbols.indexOf(c) == -1) {
                    if (c != this.format.missing && c != this.format.gap) {
                        valid = false;
                        break;
                    }
                }
            }
            if (!valid) {
                log.warn("Sequence " + i + "(" + label + ") has unexpected content.  Defined symbols are: " + this.format.symbols +
                        "; Defined missing character is: " + this.format.missing + "; Defined gap character is: " + this.format.gap +
                        "; Sequence is: " + seq);
            }

            i++;
        }

        return s;
    }

    public void addSeq(String seq) {
        this.seqs.add(seq);
    }

    public int getExpectedNbChars() {
        return expectedNbChars;
    }

    public void setExpectedNbChars(int expectedNbChars) {
        this.expectedNbChars = expectedNbChars;
    }

    public int getExpectedNbSeqs() {
        return expectedNbSeqs;
    }

    public void setExpectedNbSeqs(int expectedNbSeqs) {
        this.expectedNbSeqs = expectedNbSeqs;
    }

    public Sequences.Format getFormat() {
        return format;
    }

    public void setFormat(Sequences.Format format) {
        this.format = format;
    }
}
