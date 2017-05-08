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

package uk.ac.uea.cmp.spectre.core.io.fasta;

import org.kohsuke.MetaInfServices;
import uk.ac.uea.cmp.spectre.core.ds.Sequences;
import uk.ac.uea.cmp.spectre.core.io.AbstractSpectreReader;
import uk.ac.uea.cmp.spectre.core.io.SpectreDataType;
import uk.ac.uea.cmp.spectre.core.io.SpectreReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author balvociute + maplesond
 */
@MetaInfServices(SpectreReader.class)
public class FastaReader extends AbstractSpectreReader {

    private Map<String, String> aln;
    private BufferedReader bufferedReader;

    public Sequences readAlignment(File file) throws IOException {

        this.aln = new LinkedHashMap<>();

        //Open alignment file
        FileReader fileReader = new FileReader(file);
        bufferedReader = new BufferedReader(fileReader);

        /* file reading is done in method readAlignmentFile which is
         * different depending on a particular AlignmentReader that is used.
         */
        readAlignmentFromFile();
        return new Sequences(aln);
    }

    protected void readAlignmentFromFile() throws IOException {
        //id - last read sequence identifier
        String id = null;

        while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            line = line.trim();
            line = line.replaceAll("\\s", "");
            if (line.startsWith(">")) {
                id = line.substring(1);
            } else if (id != null) {
                String sequence = (aln.get(id) != null ? aln.get(id) : "").concat(line);
                aln.put(id, sequence);
            }
        }

    }


    @Override
    public String[] commonFileExtensions() {
        return new String[]{"fa", "fasta", "faa", "fna", "fas"};
    }

    @Override
    public String getIdentifier() {
        return "FASTA";
    }

    @Override
    public boolean acceptsDataType(SpectreDataType spectreDataType) {
        if (spectreDataType == SpectreDataType.ALIGNMENT)
            return true;

        return false;
    }
}
