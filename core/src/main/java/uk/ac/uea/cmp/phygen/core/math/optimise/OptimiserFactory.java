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
package uk.ac.uea.cmp.phygen.core.math.optimise;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 13/09/13
 * Time: 00:00
 * To change this template use File | Settings | File Templates.
 */
public class OptimiserFactory {

    private static OptimiserFactory instance;
    private ServiceLoader<Optimiser> loader;

    public OptimiserFactory() {
        this.loader = ServiceLoader.load(Optimiser.class);
    }

    public static synchronized OptimiserFactory getInstance() {
        if (instance == null) {
            instance = new OptimiserFactory();
        }
        return instance;
    }

    public Optimiser createOptimiserInstance(String name, Objective.ObjectiveType objectiveType) throws OptimiserException {

        for (Optimiser optimiser : loader) {

            if (optimiser.acceptsIdentifier(name)) {

                // Check if the requested objective is supported
                if (!optimiser.acceptsObjectiveType(objectiveType))
                    throw new UnsupportedOperationException("Objective Type: " + objectiveType.toString() +
                            "; not accepted by " + optimiser.getIdentifier());

                // Check the optimiser is operational
                if (!optimiser.isOperational()) {
                    throw new UnsupportedOperationException(optimiser.getIdentifier() + " is not operational");
                }

                // Create the appropriate optimiser, based on the objective if required
                return optimiser;
            }
        }

        return null;
    }

    /**
     * Goes through all optimisers found on the classpath and checks to see if they are operational.  Returns a list as
     * a string of all operational optimisers
     *
     * @return
     */
    public String listOperationalOptimisersAsString() {

        List<String> typeStrings = listOperationalOptimisers();

        return "[" + StringUtils.join(typeStrings, ", ") + "]";
    }

    public List<String> listOperationalOptimisers() {

        List<Optimiser> operationalOptimisers = getOperationalOptimisers();

        List<String> typeStrings = new ArrayList<String>();

        for (Optimiser optimiser : operationalOptimisers) {
            typeStrings.add(optimiser.getIdentifier());
        }

        return typeStrings;
    }


    public List<Optimiser> getOperationalOptimisers() {
        return getOperationalOptimisers(null);
    }

    public List<Optimiser> getOperationalOptimisers(Objective.ObjectiveType objectiveType) {

        Iterator<Optimiser> it = loader.iterator();

        List<Optimiser> optimiserList = new ArrayList<Optimiser>();

        while (it.hasNext()) {
            Optimiser optimiser = it.next();
            if (optimiser.isOperational() &&
                    (objectiveType == null || (objectiveType != null && optimiser.acceptsObjectiveType(objectiveType)))
                    ) {
                optimiserList.add(optimiser);
            }
        }

        return optimiserList;
    }

}
