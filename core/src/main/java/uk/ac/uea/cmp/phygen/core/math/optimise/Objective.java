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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;


public enum Objective {

    
    LINEAR {

        @Override
        public double[] optimise(Problem problem, Optimiser optimiser) throws OptimiserException {
            return optimiser.optimise(this, problem);
        }

        @Override
        public double[] buildCoefficients(final int size) {
            double[] coefficients = new double[size];
            Arrays.fill(coefficients, 1.0);
            return coefficients;
        }
    },
    QUADRATIC {

        @Override
        public double[] optimise(Problem problem, Optimiser optimiser) throws OptimiserException {
            return optimiser.optimise(this, problem);
        }

        @Override
        public double[] buildCoefficients(final int size) {
            double[] coefficients = new double[size];
            Arrays.fill(coefficients, 1.0);
            return coefficients;
        }
    },
    MINIMA {

        @Override
        public double[] optimise(Problem problem, Optimiser optimiser) throws OptimiserException {

            double[] data = problem.getRestriction();
            double[] coefficients = problem.getCoefficients();

            final int rows = problem.getRestriction().length;

            double[] solution = new double[rows];
            for (int k = 0; k < rows; k++) {
                logger.debug("Split number " + k + 1 + "\n");
                // double x = y[k];
                if (data[k] > 0.0) {
                    coefficients[k] = 1.0;
                    double[] help = optimiser.optimise(this, problem);
                    solution[k] = help[k];
                    coefficients[k] = 0.0;
                } else {
                    solution[k] = 0;
                }
            }

            return solution;
        }

        @Override
        public double[] buildCoefficients(final int size) {
            double[] coefficients = new double[size];
            Arrays.fill(coefficients, 0.0);
            return coefficients;
        }
    },
    BALANCED {

        @Override
        protected double[] optimise(Problem problem, Optimiser optimiser) throws OptimiserException {
            return optimiser.optimise(this, problem);
        }

        @Override
        public double[] buildCoefficients(final int size) {
            double[] coefficients = new double[size];
            int nbTaxa = this.getNbTaxa();

            int n = 0, a = 0;

            for (int m = 1; m < nbTaxa - 1; m++) {

                for (int j = m + 2; j < nbTaxa + 1; j++) {

                    if (m != 1 || j != nbTaxa) {

                        a = j - m;
                        //      SplitIndex [] splitIndices = new SplitIndex [N * (N - 1) / 2 - N];
                        //      Split index is defined as:   splitIndices [n] = new SplitIndex(m, j);
                        coefficients[n] = a * (a - 1) * (nbTaxa - a) * (nbTaxa - a - 1);
                        n++;
                    }
                }
            }

            return coefficients;
        }
    },
    NNLS {
        @Override
        protected double[] optimise(Problem problem, Optimiser optimiser) throws OptimiserException {
            throw new UnsupportedOperationException();

        }

        @Override
        public double[] buildCoefficients(int size) {
            throw new UnsupportedOperationException();
        }
    },
    NONE {

        @Override
        public double[] optimise(Problem problem, Optimiser optimiser) throws OptimiserException {
            return null;
        }

        @Override
        public double[] buildCoefficients(final int size) {
            return null;
        }
    };
    private int nbTaxa;
    
    private static Logger logger = LoggerFactory.getLogger(Objective.class);
    

    protected abstract double[] optimise(Problem problem, Optimiser optimiser) throws OptimiserException;

    public abstract double[] buildCoefficients(final int size);

    private Objective() {
        this.nbTaxa = 0;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void setLogger(Logger logger) {
        Objective.logger = logger;
    }

    public int getNbTaxa() {
        return nbTaxa;
    }

    public void setNbTaxa(int nbTaxa) {
        this.nbTaxa = nbTaxa;
    }
  
}
