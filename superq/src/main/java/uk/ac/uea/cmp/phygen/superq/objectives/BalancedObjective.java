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


@MetaInfServices(uk.ac.uea.cmp.phygen.superq.objectives.SecondaryObjective.class)
public class BalancedObjective extends AbstractObjective implements SecondaryObjective {

    private int nbTaxa;

    public BalancedObjective() {

        super();

        this.nbTaxa = 0;
    }

    public int getNbTaxa() {
        return nbTaxa;
    }

    public void setNbTaxa(int nbTaxa) {
        this.nbTaxa = nbTaxa;
    }

    @Override
    public double[] buildCoefficients(final int size) {
        double[] coefficients = new double[size];
        int nbTaxa = this.getNbTaxa();

        int n = 0;

        for (int m = 1; m < nbTaxa - 1; m++) {

            for (int j = m + 2; j < nbTaxa + 1; j++) {

                if (m != 1 || j != nbTaxa) {

                    int a = j - m;
                    //      SplitIndex [] splitIndices = new SplitIndex [N * (N - 1) / 2 - N];
                    //      Split index is defined as:   splitIndices [n] = new SplitIndex(m, j);
                    coefficients[n++] = a * (a - 1) * (nbTaxa - a) * (nbTaxa - a - 1);
                }
            }
        }

        return coefficients;
    }

    @Override
    public ObjectiveType getType() {
        return ObjectiveType.LINEAR;
    }

    @Override
    public ObjectiveDirection getDirection() {
        return ObjectiveDirection.MINIMISE;
    }

    @Override
    public String getIdentifier() {
        return "BALANCED";
    }

    @Override
    public boolean acceptsIdentifier(String id) {
        return id.equalsIgnoreCase(getIdentifier()) ||
                id.equalsIgnoreCase(this.getClass().getName());
    }
}
