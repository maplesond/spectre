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
import java.util.Arrays;
import java.util.List;


public enum Objective {



    LINEAR {

        @Override
        public double[] buildCoefficients(final int size) {
            double[] coefficients = new double[size];
            Arrays.fill(coefficients, 1.0);
            return coefficients;
        }

        @Override
        public ObjectiveType getObjectivetype() {
            return ObjectiveType.LINEAR;
        }
    },
    QUADRATIC {

        @Override
        public double[] buildCoefficients(final int size) {
            double[] coefficients = new double[size];
            Arrays.fill(coefficients, 1.0);
            return coefficients;
        }

        @Override
        public ObjectiveType getObjectivetype() {
            return ObjectiveType.QUADRATIC;
        }
    },
    MINIMA {

        @Override
        public double[] buildCoefficients(final int size) {
            double[] coefficients = new double[size];
            Arrays.fill(coefficients, 0.0);
            return coefficients;
        }

        @Override
        public ObjectiveType getObjectivetype() {
            return ObjectiveType.LINEAR;
        }
    },
    BALANCED {

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
        public ObjectiveType getObjectivetype() {
            return ObjectiveType.LINEAR;
        }
    },
    NNLS {

        @Override
        public double[] buildCoefficients(int size) {
            double[] coefficients = new double[size];
            Arrays.fill(coefficients, 1.0);
            return coefficients;
        }

        @Override
        public ObjectiveType getObjectivetype() {
            return ObjectiveType.QUADRATIC;
        }
    },
    SCALING {

        @Override
        public double[] buildCoefficients(int size) {
            double[] coefficients = new double[size];
            Arrays.fill(coefficients, 1.0);
            return coefficients;
        }

        @Override
        public ObjectiveType getObjectivetype() {
            return ObjectiveType.QUADRATIC;
        }
    },
    FLATNJ {

        @Override
        public double[] buildCoefficients(int size) {
            double[] coefficients = new double[size];
            Arrays.fill(coefficients, 1.0);
            return coefficients;
        }

        @Override
        public ObjectiveType getObjectivetype() {
            return ObjectiveType.QUADRATIC;
        }
    },
    NONE {

        @Override
        public double[] buildCoefficients(final int size) {
            return null;
        }

        @Override
        public ObjectiveType getObjectivetype() {
            return null;
        }
    };

    private int nbTaxa;
    private double constant;

    private Objective() {
        this.nbTaxa = 0;
        this.constant = 0;
    }

    public int getNbTaxa() {
        return nbTaxa;
    }

    public void setNbTaxa(int nbTaxa) {
        this.nbTaxa = nbTaxa;
    }

    public double getConstant() {
        return constant;
    }

    public void setConstant(double constant) {
        this.constant = constant;
    }

    public abstract double[] buildCoefficients(final int size);
    public abstract ObjectiveType getObjectivetype();


    public boolean isLinear()       { return this.getObjectivetype() == ObjectiveType.LINEAR; }
    public boolean isQuadratic()    { return this.getObjectivetype() == ObjectiveType.QUADRATIC; }


    public String listObjectivesAsString() {

        List<String> typeStrings = listObjectives();

        return "[" + StringUtils.join(typeStrings, ", ") + "]";
    }

    public List<String> listObjectives() {

        List<String> typeStrings = new ArrayList<String>();

        for(Objective objective : Objective.values()) {
            typeStrings.add(objective.toString());
        }

        return typeStrings;
    }

    public static enum ObjectiveType {
        LINEAR,
        QUADRATIC
    }
}
