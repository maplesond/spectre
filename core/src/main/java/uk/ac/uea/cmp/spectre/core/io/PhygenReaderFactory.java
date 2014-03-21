/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2014  UEA School of Computing Sciences
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
public class PhygenReaderFactory {

    private static PhygenReaderFactory instance;
    private ServiceLoader<PhygenReader> loader;

    public PhygenReaderFactory() {
        this.loader = ServiceLoader.load(PhygenReader.class);
    }

    public static synchronized PhygenReaderFactory getInstance() {
        if (instance == null) {
            instance = new PhygenReaderFactory();
        }
        return instance;
    }

    public PhygenReader create(String name) throws IOException {

        for (PhygenReader phygenReader : loader) {

            if (phygenReader.acceptsIdentifier(name)) {
                return phygenReader;
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

        return this.getPhygenReadersAsString(new ArrayList<PhygenDataType>());
    }

    public String getPhygenReadersAsString(PhygenDataType phygenDataType) {

        List<PhygenDataType> phygenDataTypeList = new ArrayList<>();
        phygenDataTypeList.add(phygenDataType);
        return this.getPhygenReadersAsString(phygenDataTypeList);
    }


    public String getPhygenReadersAsString(List<PhygenDataType> phygenDataTypes) {

        List<String> typeStrings = new ArrayList<String>();

        for (PhygenReader phygenReader : this.getPhygenReaders(phygenDataTypes)) {
            typeStrings.add(phygenReader.getIdentifier());
        }

        return "[" + StringUtils.join(typeStrings, ", ") + "]";
    }


    public List<PhygenReader> getPhygenReaders() {
        return getPhygenReaders(new ArrayList<PhygenDataType>());
    }

    public List<PhygenReader> getPhygenReaders(PhygenDataType phygenDataType) {

        List<PhygenDataType> phygenDataTypes = new ArrayList<>();

        if (phygenDataType != null) {
            phygenDataTypes.add(phygenDataType);
        }

        return this.getPhygenReaders(phygenDataTypes);
    }

    public List<PhygenReader> getPhygenReaders(List<PhygenDataType> phygenDataTypeList) {

        Iterator<PhygenReader> it = loader.iterator();

        List<PhygenReader> phygenReaderList = new ArrayList<>();

        while (it.hasNext()) {
            PhygenReader phygenReader = it.next();
            if (phygenReader.acceptsDataTypes(phygenDataTypeList)) {
                phygenReaderList.add(phygenReader);
            }
        }

        return phygenReaderList;
    }
}
