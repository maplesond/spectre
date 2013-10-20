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

import org.kohsuke.MetaInfServices;
import uk.ac.uea.cmp.phygen.core.math.optimise.AbstractObjective;

import java.util.Arrays;


@MetaInfServices(uk.ac.uea.cmp.phygen.superq.objectives.SecondaryObjective.class)
public class QuadraticObjective extends AbstractObjective implements SecondaryObjective {

    @Override
    public double[] buildCoefficients(final int size) {
        double[] coefficients = new double[size];
        Arrays.fill(coefficients, 1.0);
        return coefficients;
    }

    @Override
    public ObjectiveType getType() {
        return ObjectiveType.QUADRATIC;
    }

    @Override
    public ObjectiveDirection getDirection() {
        return ObjectiveDirection.MINIMISE;
    }

    @Override
    public String getIdentifier() {
        return "QUADRATIC";
    }

    @Override
    public boolean acceptsIdentifier(String id) {
        return id.equalsIgnoreCase(getIdentifier()) ||
                id.equalsIgnoreCase(this.getClass().getName());
    }
}
