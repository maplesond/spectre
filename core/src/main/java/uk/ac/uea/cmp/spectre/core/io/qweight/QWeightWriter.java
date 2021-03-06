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

package uk.ac.uea.cmp.spectre.core.io.qweight;

import uk.ac.uea.cmp.spectre.core.ds.quad.Quad;
import uk.ac.uea.cmp.spectre.core.ds.quad.quartet.GroupedQuartetSystem;
import uk.ac.uea.cmp.spectre.core.ds.quad.quartet.QuartetSystem;
import uk.ac.uea.cmp.spectre.core.io.AbstractSpectreWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;

/**
 * Created by dan on 10/12/13.
 */
public class QWeightWriter extends AbstractSpectreWriter {


    @Override
    public void writeQuartets(File outFile, QuartetSystem quartetSystem) throws IOException {

        this.writeQuartets(outFile, new GroupedQuartetSystem(quartetSystem));
    }


    @Override
    public void writeQuartets(File outFile, GroupedQuartetSystem quartetSystem) throws IOException {


        FileWriter out = new FileWriter(outFile);

        // Output header
        out.write("taxanumber: " + quartetSystem.getTaxa().size() + ";\n");
        out.write("description: supernetwork quartets;\n");
        out.write("sense: " + quartetSystem.getSense().toString() + ";\n");

        NumberFormat nF = NumberFormat.getIntegerInstance();
        nF.setMinimumIntegerDigits(3);
        nF.setMaximumIntegerDigits(3);

        // Output the taxa part
        for (int n = 0; n < quartetSystem.getTaxa().size(); n++) {
            out.write("taxon:   " + nF.format(n + 1) + "   name: " + quartetSystem.getTaxa().get(n).getName() + ";\n");
        }

        // Output the quartets and weights part
        for (Quad quad : quartetSystem.sortedQuartets()) {
            out.write(quad.toString(nF) + " " + quartetSystem.getQuartets().get(quad).toString() + ";\n");
        }

        out.close();
    }
}
