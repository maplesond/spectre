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
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Sequences.Format format;
    private List<String> seqs;


    public NexusCharacterBuilder() {
        this.expectedNbChars = 0;
        this.seqs = new ArrayList<>();
        this.format = new Sequences.Format();
    }


    public Sequences createAlignments(IdentifierList taxa) {

        final int nbseqs = format.labels ? this.seqs.size() / 2 : this.seqs.size();
        if (taxa.size() != nbseqs) {
            throw new IllegalStateException("Nexus file contains " + taxa.size() + " taxa however we found " + this.seqs.size() + " sequences in the Character block.");
        }

        Map<String,String> alns = new HashMap<>();
        if (!format.labels) {
            for (int i = 0; i < this.seqs.size(); i++) {
                if (seqs.get(i).length() != this.expectedNbChars) {
                    log.warn("Sequence " + i + " has an unexpected size.  Sequence contains " + seqs.get(i).length() + " characters but Nexus Characters block specified " + this.expectedNbChars + " characters per sequence.");
                }
                // Check that sequence is valid according to spec provided
                boolean valid = true;
                for (int j = 0; j < seqs.get(i).length(); j++) {
                    char c = seqs.get(i).charAt(j);
                    if (this.format.symbols.indexOf(c) == -1) {
                        if (c != this.format.missing && c != this.format.gap) {
                            valid = false;
                            break;
                        }
                    }
                }
                if (!valid) {
                    log.warn("Sequence " + i + " has unexpected content.  Defined symbols are: " + this.format.symbols +
                            "; Defined missing character is: " + this.format.missing + "; Defined gap character is: " + this.format.gap +
                            "; Sequence is: " + this.seqs.get(i));
                }
                alns.put(taxa.get(i).getName(), seqs.get(i));
            }
        }
        else {
            for (int i = 0; i < this.seqs.size(); i+=2) {
                if (seqs.get(i+1).length() != this.expectedNbChars) {
                    log.warn("Sequence " + i + " has an unexpected size.  Sequence contains " + seqs.get(i+1).length() + " characters but Nexus Characters block specified " + this.expectedNbChars + " characters per sequence.");
                }
                alns.put(seqs.get(i), seqs.get(i+1));
            }
        }

        Sequences s = new Sequences(alns);
        s.setFormat(this.format);

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

    public Sequences.Format getFormat() {
        return format;
    }

    public void setFormat(Sequences.Format format) {
        this.format = format;
    }
}
