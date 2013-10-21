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


public class SecondaryProblemFactory {

    private static SecondaryProblemFactory instance;
    private ServiceLoader<SecondaryProblem> loader;

    public SecondaryProblemFactory() {
        this.loader = ServiceLoader.load(SecondaryProblem.class);
    }

    public static synchronized SecondaryProblemFactory getInstance() {
        if (instance == null) {
            instance = new SecondaryProblemFactory();
        }
        return instance;
    }

    public SecondaryProblem createSecondaryObjective(String name) {

        for (SecondaryProblem secondaryProblem : loader) {

            if (secondaryProblem.acceptsIdentifier(name)) {

                return secondaryProblem;
            }
        }

        return null;
    }

    public String listSecondaryObjectivesAsString() {

        List<String> typeStrings = listObjectivesByIdentifier();

        return "[" + StringUtils.join(typeStrings, ", ") + "]";
    }

    public List<SecondaryProblem> listObjectives() {

        List<SecondaryProblem> secondaryProblems = new ArrayList<>();

        for (SecondaryProblem secondaryProblem : loader) {
            secondaryProblems.add(secondaryProblem);
        }

        return secondaryProblems;
    }

    public List<String> listObjectivesByIdentifier() {

        List<SecondaryProblem> secondaryProblems = listObjectives();
        List<String> ids = new ArrayList<>();

        for (SecondaryProblem secondaryProblem : secondaryProblems) {
            ids.add(secondaryProblem.getName());
        }

        return ids;
    }
}
