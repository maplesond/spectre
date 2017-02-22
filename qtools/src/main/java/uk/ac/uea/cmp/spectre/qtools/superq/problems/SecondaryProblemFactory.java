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

package uk.ac.uea.cmp.spectre.qtools.superq.problems;


import org.apache.commons.lang3.StringUtils;
import uk.ac.tgac.metaopt.Objective;

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

        return listObjectives(null);
    }

    public List<SecondaryProblem> listObjectives(Objective.ObjectiveType objectiveType) {

        List<SecondaryProblem> secondaryProblems = new ArrayList<>();

        for (SecondaryProblem secondaryProblem : loader) {
            if (objectiveType == null || objectiveType == Objective.ObjectiveType.QUADRATIC ||
                    (secondaryProblem.getObjectiveType() == Objective.ObjectiveType.LINEAR &&
                            objectiveType == Objective.ObjectiveType.LINEAR))
                secondaryProblems.add(secondaryProblem);
        }

        return secondaryProblems;
    }

    public List<String> listObjectivesByIdentifier() {

        return listObjectivesByIdentifier(null);
    }

    public List<String> listObjectivesByIdentifier(Objective.ObjectiveType objectiveType) {

        List<String> ids = new ArrayList<>();

        for (SecondaryProblem secondaryProblem : listObjectives(objectiveType)) {
            ids.add(secondaryProblem.getName());
        }

        return ids;
    }
}
