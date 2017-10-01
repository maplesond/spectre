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

package uk.ac.uea.cmp.spectre.core.io;

import uk.ac.uea.cmp.spectre.core.ds.Locations;
import uk.ac.uea.cmp.spectre.core.ds.Sequences;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.spectre.core.ds.quad.quartet.QuartetSystem;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitSystem;
import uk.ac.uea.cmp.spectre.core.ds.tree.newick.NewickTree;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sarah_2
 * Date: 24/04/13
 * Time: 17:12
 * To change this template use File | Settings | File Templates.
 */
public interface SpectreReader {

    /**
     * Loads a distance matrix from a file
     *
     * @param input The file to read
     * @return A distance matrix
     * @throws IOException Thrown if any disk related issues occur (e.g. file does not exist or inaccessible)
     */
    DistanceMatrix readDistanceMatrix(File input) throws IOException;

    /**
     * Loads a list of Newick Trees from a file
     *
     * @param file The file to read
     * @return A list of NewickTrees
     * @throws IOException Thrown if any disk related issues occur (e.g. file does not exist or inaccessible)
     */
    List<NewickTree> readTrees(File file) throws IOException;

    List<NewickTree> readTrees(File file, double weight) throws IOException;

    /**
     * Loads a split system from a file
     *
     * @param file The file to read
     * @return A split system
     * @throws IOException Thrown if any disk related issues occur (e.g. file does not exist or inaccessible)
     */
    SplitSystem readSplitSystem(File file) throws IOException;

    /**
     * Loads a quartet system from a file
     *
     * @param file The file to read
     * @return A quartet system
     * @throws IOException Thrown if any disk related issues occur (e.g. file does not exist or inaccessible)
     */
    QuartetSystem readQuartets(File file) throws IOException;


    Sequences readAlignment(File file) throws IOException;

    Locations readLocations(File file) throws IOException;

    /**
     * Commonly used file extensions for this type of reader.
     *
     * @return An array of strings representing commonly used file extensions for this reader.
     */
    String[] commonFileExtensions();

    /**
     * An identifier with which the client.
     *
     * @return An identifier for this SpectreReader.
     */
    String getIdentifier();

    /**
     * Whether or not this phygen reader accepts the given identifier.
     *
     * @param identifier The phygen reader identifier.
     * @return True if this SpectreReader recognises the identifier, false otherwise.
     */
    boolean acceptsIdentifier(String identifier);

    /**
     * Whether or not this SpectreReader can handle the specified data type.
     *
     * @param spectreDataType The data type.
     * @return True, if this SpectreReader can load the specified data type, false otherwise.
     */
    boolean acceptsDataType(SpectreDataType spectreDataType);

    /**
     * Whether or not this SpectreReader can handle the list of specified data types.
     *
     * @param spectreDataTypeList A list of data types.
     * @return True, if this SpectreReader can load ALL the specified data types, false otherwise.
     */
    boolean acceptsDataTypes(List<SpectreDataType> spectreDataTypeList);

}
