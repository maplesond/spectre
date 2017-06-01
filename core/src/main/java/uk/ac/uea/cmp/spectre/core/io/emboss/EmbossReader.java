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

package uk.ac.uea.cmp.spectre.core.io.emboss;

import org.apache.commons.io.FileUtils;
import org.kohsuke.MetaInfServices;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.distance.FlexibleDistanceMatrix;
import uk.ac.uea.cmp.spectre.core.io.AbstractSpectreReader;
import uk.ac.uea.cmp.spectre.core.io.SpectreDataType;
import uk.ac.uea.cmp.spectre.core.io.SpectreReader;

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
@MetaInfServices(SpectreReader.class)
public class EmbossReader extends AbstractSpectreReader {

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
            throw new NullPointerException("Must specify an emboss distmat file to read");
        }

        if (!file.exists()) {
            throw new IOException("Emboss distmat file does not exist: " + file.getAbsolutePath());
        }

        if (!file.canRead()) {
            throw new IOException("Emboss distmat file cannot be read: " + file.getAbsolutePath());
        }

        // Load file into line collection
        List<String> inLines = FileUtils.readLines(file, "UTF-8");

        // Execute Parseing code.
        return alternativeParser(inLines);
    }

    @Override
    public String[] commonFileExtensions() {
        return new String[]{"distmat", "emboss"};
    }

    @Override
    public String getIdentifier() {
        return "EMBOSS";
    }

    @Override
    public boolean acceptsDataType(SpectreDataType spectreDataType) {

        if (spectreDataType == SpectreDataType.DISTANCE_MATRIX)
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

        // Skip header
        int start = 0;
        String line = "";
        do {
            line = lines.get(start++).trim();
        } while(!line.startsWith("1"));

        int nbTaxa = line.split("\t").length;

        DistanceMatrix distanceMatrix = new FlexibleDistanceMatrix(nbTaxa);

        // First run through the lines to establish taxa in distance matrix
        for (int i = start; i < lines.size(); i++) {

            String aLine = lines.get(i).trim();

            // Ignore empty lines
            if (aLine.isEmpty())
                continue;

            String[] parts = aLine.split("\t");
            String[] taxa = parts[parts.length - 1].split(" ");

            String name = taxa[0].trim();
            int id = Integer.parseInt(taxa[1].trim());

            distanceMatrix.getTaxa().getById(id).setName(name);
        }

        // Then run through lines again to get the distances
        // We need to do this in two stages in order to get the hashkeys for the taxa correct so they are properly registered
        // in the distance matrix
        for (int i = start; i < lines.size(); i++) {

            String aLine = lines.get(i).trim();

            // Ignore empty lines
            if (aLine.isEmpty())
                continue;

            String[] parts = aLine.split("\t");

            final int id = i - start + 1;

            // Run through the rest of the line
            for (int j = 0; j < parts.length - 1; j++) {

                String trimmedPart = parts[j].trim();

                if (!trimmedPart.isEmpty()) {
                    distanceMatrix.setDistance(id, id+j, Double.parseDouble(trimmedPart));
                }
            }
        }

        return distanceMatrix;
    }
}
