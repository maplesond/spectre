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

package uk.ac.uea.cmp.spectre.core.ds;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;

/**
 * Class for storing multiple sequence alignment (MSA).
 *
 * @author balvociute
 */
public class Sequences {
    //Taxa labels
    private String[] taxaLabels;
    //Sequences
    private String[] sequences;

    private Format format;

    /**
     * Initializes alignment from the map that maps taxa labels to corresponding
     * sequences
     *
     * @param aln multiple sequence alignment
     */
    public Sequences(Map<String, String> aln) {
        taxaLabels = new String[aln.size()];
        sequences = new String[aln.size()];
        Iterator<String> alnIterator = aln.keySet().iterator();
        int i = 0;
        while (alnIterator.hasNext()) {
            taxaLabels[i] = alnIterator.next();
            sequences[i] = aln.get(taxaLabels[i]);
            i++;
        }
        aln.clear();
    }

    //Initializes alignment from two arrays
    public Sequences(String[] n, String[] seq) {
        taxaLabels = new String[n.length];
        System.arraycopy(n, 0, taxaLabels, 0, n.length);
        sequences = new String[seq.length];
        System.arraycopy(seq, 0, sequences, 0, seq.length);
        if (!sameLength()) {
            taxaLabels = null;
            sequences = null;
        }
    }

    //Prints alignment in the fasta format to the screen
    public void printAlignmentInFasta(PrintStream os) {
        for (int i = 0; i < taxaLabels.length; i++) {
            os.println(">" + taxaLabels[i] + "\n" + sequences[i]);
        }
    }

    //Returns labels of taxa
    public String[] getTaxaLabels() {
        return taxaLabels;
    }

    public void setTaxaLabels(String[] taxaLabels) {
        this.taxaLabels = taxaLabels;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    //Returns sequences
    public String[] getSequences() {
        return sequences;
    }

    //Transforms sequences from strings to the arrays of characters
    public char[][] getSequencesAsCharArray() {
        char[][] s = new char[sequences.length][sequences[0].length()];
        for (int i = 0; i < sequences.length; i++) {
            s[i] = sequences[i].toCharArray();
        }
        return s;
    }

    //Returns number of sequences
    public int size() {
        return sequences.length;
    }

    //Checks if all sequences are the same length
    public boolean sameLength() {
        for (int i = 0; i < sequences.length - 1; i++) {
            if (sequences[i].length() != sequences[i + 1].length()) {
                return false;
            }
        }
        return true;
    }

    public static class Format {

        public boolean labels;
        public boolean interleaved;
        public String triangle;
        public boolean diagonal;
        public boolean activeFlags;
        public boolean weights;
        public boolean confidences;

        public Format() {
            labels = true;
            interleaved = true;
            triangle = "both";
            diagonal = false;
            activeFlags = true;
            weights = true;
            confidences = false;
        }
    }

}
