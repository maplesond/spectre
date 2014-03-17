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
package uk.ac.uea.cmp.spectre.core.io.phylip;

import org.apache.commons.io.FileUtils;
import org.kohsuke.MetaInfServices;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.distance.FlexibleDistanceMatrix;
import uk.ac.uea.cmp.spectre.core.io.AbstractPhygenReader;
import uk.ac.uea.cmp.spectre.core.io.PhygenDataType;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sarah_2
 * Date: 24/04/13
 * Time: 13:18
 * To change this template use File | Settings | File Templates.
 */
@MetaInfServices(uk.ac.uea.cmp.spectre.core.io.PhygenReader.class)
public class PhylipReader extends AbstractPhygenReader {

    /**
     * Reads the file specified by this reader and converts it the data into a set
     * of taxa and the distances between taxa.
     *
     * @return The distance matrix, with associated taxa set.
     * @throws IOException Thrown if there were any problems accessing the file.
     */
    @Override
    public DistanceMatrix readDistanceMatrix(File file) throws IOException {

        // Validation before reading
        if (file == null) {
            throw new NullPointerException("Must specify a phylip file to read");
        }

        if (!file.exists()) {
            throw new IOException("Phylip file does not exist: " + file.getAbsolutePath());
        }

        if (!file.canRead()) {
            throw new IOException("Phylip file cannot be read: " + file.getAbsolutePath());
        }

        // Load file into line collection
        List<String> inLines = FileUtils.readLines(file);

        // Execute Parseing code.
        return alternativeParser(inLines);
    }

    @Override
    public String[] commonFileExtensions() {
        return new String[]{"phy", "phylip"};
    }

    @Override
    public String getIdentifier() {
        return "PHYLIP";
    }

    @Override
    public boolean acceptsDataType(PhygenDataType phygenDataType) {

        if (phygenDataType == PhygenDataType.DISTANCE_MATRIX)
            return true;

        return false;
    }


    /**
     * Alternative Phylip parser which handles multi-line phylip files
     *
     * @param lines Data from input file split into lines
     * @return DistanceMatrix
     */
    private DistanceMatrix alternativeParser(List<String> lines) {

        if (lines == null || lines.isEmpty()) {
            return null;
        }

        String firstLine = lines.get(0).trim();
        int nbTaxa = Integer.parseInt(firstLine);

        DistanceMatrix distanceMatrix = new FlexibleDistanceMatrix(nbTaxa);

        int dmRow = 1;
        int dmCol = 1;

        // Process the rest of the lines
        for (int i = 1; i < lines.size(); i++) {

            String aLine = lines.get(i);

            // Ignore empty lines
            if (aLine.trim().isEmpty())
                continue;

            String[] parts = aLine.split(" ");

            if (!parts[0].isEmpty()) {
                distanceMatrix.getTaxa().getById(dmRow).setName(parts[0]);
            }

            // Run through the rest of the line
            for (int j = 1; j < parts.length; j++) {

                String trimmedPart = parts[j].trim();

                if (!trimmedPart.isEmpty()) {
                    distanceMatrix.setDistance(dmRow, dmCol++, Double.parseDouble(trimmedPart));
                }
            }

            if (dmCol > nbTaxa) {
                dmRow++;
                dmCol = 1;
            }
        }

        return distanceMatrix;
    }
}
