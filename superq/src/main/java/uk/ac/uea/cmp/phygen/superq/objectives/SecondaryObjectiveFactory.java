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
package uk.ac.uea.cmp.phygen.superq.objectives;


import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;


public class SecondaryObjectiveFactory {

    private static SecondaryObjectiveFactory instance;
    private ServiceLoader<SecondaryObjective> loader;

    public SecondaryObjectiveFactory() {
        this.loader = ServiceLoader.load(SecondaryObjective.class);
    }

    public static synchronized SecondaryObjectiveFactory getInstance() {
        if (instance == null) {
            instance = new SecondaryObjectiveFactory();
        }
        return instance;
    }

    public SecondaryObjective createSecondaryObjective(String name) {

        for (SecondaryObjective secondaryObjective : loader) {

            if (secondaryObjective.acceptsIdentifier(name)) {

                return secondaryObjective;
            }
        }

        return null;
    }

    public String listSecondaryObjectivesAsString() {

        List<String> typeStrings = listObjectivesByIdentifier();

        return "[" + StringUtils.join(typeStrings, ", ") + "]";
    }

    public List<SecondaryObjective> listObjectives() {

        List<SecondaryObjective> secondaryObjectives = new ArrayList<>();

        for (SecondaryObjective secondaryObjective : loader) {
            secondaryObjectives.add(secondaryObjective);
        }

        return secondaryObjectives;
    }

    public List<String> listObjectivesByIdentifier() {

        List<SecondaryObjective> secondaryObjectives = listObjectives();
        List<String> ids = new ArrayList<>();

        for (SecondaryObjective secondaryObjective : secondaryObjectives) {
            ids.add(secondaryObjective.getName());
        }

        return ids;
    }
}
