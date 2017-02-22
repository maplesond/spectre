/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2015  UEA School of Computing Sciences
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

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Created with IntelliJ IDEA.
 * User: Sarah_2
 * Date: 24/04/13
 * Time: 17:21
 * To change this template use File | Settings | File Templates.
 */
public class SpectreReaderFactory {

    private static SpectreReaderFactory instance;
    private ServiceLoader<SpectreReader> loader;

    public SpectreReaderFactory() {
        this.loader = ServiceLoader.load(SpectreReader.class);
    }

    public static synchronized SpectreReaderFactory getInstance() {
        if (instance == null) {
            instance = new SpectreReaderFactory();
        }
        return instance;
    }

    public SpectreReader create(String name) throws IOException {

        for (SpectreReader spectreReader : loader) {

            if (spectreReader.acceptsIdentifier(name)) {
                return spectreReader;
            }
        }

        return null;
    }


    /**
     * Goes through all phygen readers found on the classpath and joins all their identifiers into a string
     *
     * @return The phygen readers as a string.
     */
    public String getPhygenReadersAsString() {

        return this.getPhygenReadersAsString(new ArrayList<SpectreDataType>());
    }

    public String getPhygenReadersAsString(SpectreDataType spectreDataType) {

        List<SpectreDataType> spectreDataTypeList = new ArrayList<>();
        spectreDataTypeList.add(spectreDataType);
        return this.getPhygenReadersAsString(spectreDataTypeList);
    }


    public String getPhygenReadersAsString(List<SpectreDataType> spectreDataTypes) {

        List<String> typeStrings = new ArrayList<String>();

        for (SpectreReader spectreReader : this.getPhygenReaders(spectreDataTypes)) {
            typeStrings.add(spectreReader.getIdentifier());
        }

        return "[" + StringUtils.join(typeStrings, ", ") + "]";
    }


    public List<SpectreReader> getPhygenReaders() {
        return getPhygenReaders(new ArrayList<SpectreDataType>());
    }

    public List<SpectreReader> getPhygenReaders(SpectreDataType spectreDataType) {

        List<SpectreDataType> spectreDataTypes = new ArrayList<>();

        if (spectreDataType != null) {
            spectreDataTypes.add(spectreDataType);
        }

        return this.getPhygenReaders(spectreDataTypes);
    }

    public List<SpectreReader> getPhygenReaders(List<SpectreDataType> spectreDataTypeList) {

        Iterator<SpectreReader> it = loader.iterator();

        List<SpectreReader> spectreReaderList = new ArrayList<>();

        while (it.hasNext()) {
            SpectreReader spectreReader = it.next();
            if (spectreReader.acceptsDataTypes(spectreDataTypeList)) {
                spectreReaderList.add(spectreReader);
            }
        }

        return spectreReaderList;
    }
}
