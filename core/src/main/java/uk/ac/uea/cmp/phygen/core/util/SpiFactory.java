/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
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

package uk.ac.uea.cmp.phygen.core.util;

import org.apache.commons.lang3.StringUtils;
import uk.ac.uea.cmp.phygen.core.math.optimise.Objective;
import uk.ac.uea.cmp.phygen.core.math.optimise.Optimiser;
import uk.ac.uea.cmp.phygen.core.math.optimise.OptimiserException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 14/11/13
 * Time: 19:10
 * To change this template use File | Settings | File Templates.
 */
public class SpiFactory<T extends Service> {

    private ServiceLoader<T> loader;
    private Class<T> clazz;

    @SuppressWarnings("unchecked")
    public SpiFactory(Class<T> clazz) {
        this.clazz = clazz;
        this.loader = ServiceLoader.load(clazz);
    }

    /**
     * This default method accepts an identifier if it equals the service's name, or if it matches the class names.
     * @param identifier
     * @param service
     * @return
     */
    protected boolean acceptsIdentifier(String identifier, T service) {
       return   identifier.equalsIgnoreCase(service.getName()) ||
                identifier.equalsIgnoreCase(this.clazz.getCanonicalName()) ||
                identifier.equalsIgnoreCase(this.clazz.getName());
    }

    /**
     * Creates a new instance of the service with the specified name if it can be found, otherwise return null
     * @param name
     * @return
     */
    public T create(String name) {

        // Create the appropriate service, if the name is recognized
        for (T service : loader) {
            if (this.acceptsIdentifier(name, service)) {
                return service;
            }
        }

        return null;
    }

    /**
     * Goes through all requested services found on the classpath and returns a list as a string, surrounded with square
     * brackets
     *
     * @return
     */
    public String listServicesAsString() {

        List<String> typeStrings = listServices();

        return "[" + StringUtils.join(typeStrings, ", ") + "]";
    }


    /**
     * Returns a list of a strings describing all the classes that are available
     * @return
     */
    public List<String> listServices() {

        List<T> services = getServices();

        List<String> typeStrings = new ArrayList<String>();

        for (T service : services) {
            typeStrings.add(service.getName());
        }

        return typeStrings;
    }

    /**
     * Returns a list of the requested services
     * @return
     */
    public List<T> getServices() {

        Iterator<T> it = loader.iterator();

        List<T> serviceList = new ArrayList<>();

        while (it.hasNext()) {
            serviceList.add(it.next());
        }

        return serviceList;
    }

}
