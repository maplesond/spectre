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
package uk.ac.uea.cmp.phygen.core.io;

import uk.ac.uea.cmp.phygen.core.ds.Alignment;
import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;
import uk.ac.uea.cmp.phygen.core.ds.split.SplitSystem;
import uk.ac.uea.cmp.phygen.core.ds.tree.newick.NewickTree;

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
public interface PhygenReader {

    /**
     * Loads a distance matrix from a file
     *
     * @param input The file to read
     * @return A distance matrix
     * @throws IOException
     */
    DistanceMatrix readDistanceMatrix(File input) throws IOException;

    List<NewickTree> readTrees(File input) throws IOException;

    List<NewickTree> readTrees(File input, double weight) throws IOException;

    SplitSystem readSplitSystem(File file) throws IOException;

    Alignment readAlignment(File file) throws IOException;

    String[] commonFileExtensions();

    String getIdentifier();

    boolean acceptsIdentifier(String identifier);

    boolean acceptsDataType(PhygenDataType phygenDataType);

    boolean acceptsDataTypes(List<PhygenDataType> phygenDataTypeList);

}
