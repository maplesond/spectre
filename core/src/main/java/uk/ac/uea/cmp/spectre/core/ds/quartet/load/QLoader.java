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
package uk.ac.uea.cmp.spectre.core.ds.quartet.load;

import uk.ac.uea.cmp.spectre.core.ds.quartet.QuartetSystem;
import uk.ac.uea.cmp.spectre.core.ds.quartet.QuartetSystemList;
import uk.ac.uea.cmp.spectre.core.util.Service;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA. User: Analysis Date: 2004-jul-12 Time: 00:35:38 To
 * change this template use Options | File Templates.
 */
public interface QLoader extends Service {

    /**
     * Loads a quartet network from a file
     *
     * @param file The file to load
     * @return A quartet network
     * @throws IOException Thrown if there was a problem reading the file
     */
    QuartetSystem load(File file) throws IOException;

    /**
     * Loads a list of quartet networks from a file
     *
     * @param file   The file to load
     * @param weight The weight to be applied to this file
     * @return A list of quartet networks
     * @throws IOException Thrown if there was a problem reading the file
     */
    QuartetSystemList load(File file, double weight) throws IOException;


    /**
     * Returns true if this QLoader accepts the file extension, false otherwise.
     *
     * @param ext The file extension to test
     * @return True if accepted, false if not.
     */
    boolean acceptsExtension(String ext);
}
