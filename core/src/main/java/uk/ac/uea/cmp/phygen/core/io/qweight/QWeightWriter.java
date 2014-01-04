/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
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

package uk.ac.uea.cmp.phygen.core.io.qweight;

import uk.ac.uea.cmp.phygen.core.ds.quartet.Quartet;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetSystem;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetWeights;
import uk.ac.uea.cmp.phygen.core.io.AbstractPhygenWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by dan on 10/12/13.
 */
public class QWeightWriter extends AbstractPhygenWriter {

    @Override
    public void writeQuartets(File outFile, QuartetSystem quartetNetwork) throws IOException {


        FileWriter out = new FileWriter(outFile);

        // Output header
        out.write("taxanumber: " + quartetNetwork.getTaxa().size() + ";\n");
        out.write("description: supernetwork quartets;\n");
        out.write("sense: " + quartetNetwork.getSense().toString() + ";\n");

        NumberFormat nF = NumberFormat.getIntegerInstance();
        nF.setMinimumIntegerDigits(3);
        nF.setMaximumIntegerDigits(3);

        // Output the taxa part
        for (int n = 0; n < quartetNetwork.getTaxa().size(); n++) {
            out.write("taxon:   " + nF.format(n + 1) + "   name: " + quartetNetwork.getTaxa().get(n).getName() + ";\n");
        }

        // Sort the quartets
        List<Quartet> keys = new LinkedList<>(quartetNetwork.getQuartets().keySet());
        Collections.sort(keys);

        // Output the quartets and weights part
        for(Quartet quartet : keys) {
            out.write(quartet.toString(nF) + " " + quartetNetwork.getQuartets().get(quartet).toString() + ";\n");
        }

        out.close();
    }
}
