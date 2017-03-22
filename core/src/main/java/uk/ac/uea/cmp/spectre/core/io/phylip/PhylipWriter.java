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

package uk.ac.uea.cmp.spectre.core.io.phylip;

import org.apache.commons.io.FileUtils;
import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.io.AbstractSpectreWriter;
import uk.ac.uea.cmp.spectre.core.util.CollectionUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA. User: Dan Date: 14/05/13 Time: 20:24 To change this template use File | Settings | File
 * Templates.
 */
public class PhylipWriter extends AbstractSpectreWriter {

    @Override
    public void writeDistanceMatrix(File outFile, DistanceMatrix distanceMatrix) throws IOException {

        final int n = distanceMatrix.size();

        StringBuilder fileContent = new StringBuilder(10 * n * n);

        fileContent.append(n);
        fileContent.append("\n");

        IdentifierList nameSortedTaxa = distanceMatrix.getTaxa(new Identifier.NameComparator());
        double[][] matrix = distanceMatrix.getMatrix(new Identifier.NameComparator());

        for (int i = 0; i < n; i++) {

            fileContent.append(nameSortedTaxa.get(i).getName());
            fileContent.append(" ");
            fileContent.append(CollectionUtils.doubleArrayToString(matrix[i], " "));
            fileContent.append("\n");
        }

        // Save
        FileUtils.writeStringToFile(outFile, fileContent.toString(), "UTF-8");
    }
}
