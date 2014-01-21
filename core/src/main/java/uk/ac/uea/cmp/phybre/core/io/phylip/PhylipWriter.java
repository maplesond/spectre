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

package uk.ac.uea.cmp.phybre.core.io.phylip;

import org.apache.commons.io.FileUtils;
import uk.ac.uea.cmp.phybre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.phybre.core.io.AbstractPhygenWriter;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA. User: Dan Date: 14/05/13 Time: 20:24 To change this template use File | Settings | File
 * Templates.
 */
public class PhylipWriter extends AbstractPhygenWriter {

    @Override
    public void writeDistanceMatrix(File outFile, DistanceMatrix distanceMatrix) throws IOException {

        final int n = distanceMatrix.size();

        StringBuilder fileContent = new StringBuilder(10 * n * n);

        fileContent.append(n);
        fileContent.append("\n");

        for (int i = 0; i < n; i++) {

            fileContent.append(distanceMatrix.getTaxonName(i));
            fileContent.append(" ");
            fileContent.append(distanceMatrix.getRowAsString(i));
            fileContent.append("\n");
        }

        // Save
        FileUtils.writeStringToFile(outFile, fileContent.toString());
    }
}
