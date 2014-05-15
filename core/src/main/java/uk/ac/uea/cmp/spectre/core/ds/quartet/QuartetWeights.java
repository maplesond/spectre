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

package uk.ac.uea.cmp.spectre.core.ds.quartet;

import uk.ac.uea.cmp.spectre.core.math.tuple.Triplet;

/**
 * Created with IntelliJ IDEA.
 * User: maplesod
 * Date: 29/10/13
 * Time: 17:47
 * To change this template use File | Settings | File Templates.
 */
public class QuartetWeights extends Triplet<Double> {

    public QuartetWeights() {
        super(0.0, 0.0, 0.0);
    }

    public QuartetWeights(double a, double b, double c) {
        super(a, b, c);
    }

    public QuartetWeights(Quartet q1, Quartet q2, double weight) {
        this(0.0, 0.0, 0.0);
        this.update(q1, q2, weight);
    }

    public QuartetWeights(QuartetWeights qW, double weight) {
        this(qW.getA(), qW.getB(), qW.getC());

        this.multiply(weight);
    }

    @Override
    public boolean equals(Object o) {

        return this.equals((QuartetWeights) o);
    }

    public boolean equals(QuartetWeights quartetWeights) {

        if (this == quartetWeights)
            return true;

        if (quartetWeights == null)
            return false;

        return (this.getA().doubleValue() == quartetWeights.getA().doubleValue() &&
                this.getB().doubleValue() == quartetWeights.getB().doubleValue() &&
                this.getC().doubleValue() == quartetWeights.getC().doubleValue());
    }


    public boolean allNonZero() {
        return this.getA() != 0 && this.getB() != 0 && this.getC() != 0;
    }

    private double logNormValue(double value) {
        if (value < 0.0) {
            throw new IllegalStateException("Error: Quartet file contains negative length!");
        }

        return value == 0.0 ? -15.0 : Math.log(value);
    }


    public void set(int index, double weight) {

        if (index == 0) {
            this.setA(weight);
        } else if (index == 1) {
            this.setB(weight);
        } else if (index == 2) {
            this.setC(weight);
        } else {
            throw new IllegalArgumentException("Invalid weight index: " + index);
        }
    }

    /**
     * Investigate which topology of a, b, c, d that the topologies of x, y, u, v correspond to, and set weights accordingly
     * See if xy|uv is ab|cd (w1), ac|bd (w2), or ad|bc (w3)
     * The first length of the stored triplet
     */
    public void update(Quartet q1, Quartet q2, double newW) {

        int x = q1.getA(), y = q1.getB(), u = q1.getC(), v = q1.getD();
        int a = q2.getA(), b = q2.getB(), c = q2.getC(), d = q2.getD();

        if (((x == a || x == b) && (y == a || y == b)) || ((u == a || u == b) && (v == a || v == b))) {
            this.setA(newW);
        } else if (((x == a || x == b) && (u == a || u == b)) || ((y == a || y == b) && (v == a || v == b))) {
            this.setB(newW);
        } else if (((x == a || x == b) && (v == a || v == b)) || ((u == a || u == b) && (y == a || y == b))) {
            this.setC(newW);
        }
    }

    /**
     * Determine which quartet to take. Use the unordered numbers, they
     * match one ordering or other
     *
     * @param sorted
     * @param unsorted
     * @return The weight determined by considering q1 and q2
     */
    public double selectWeight(Quartet sorted, Quartet unsorted) {

        if (((unsorted.getA() == sorted.getA() || unsorted.getA() == sorted.getB()) && (unsorted.getB() == sorted.getA() || unsorted.getB() == sorted.getB())) ||
                ((unsorted.getC() == sorted.getA() || unsorted.getC() == sorted.getB()) && (unsorted.getD() == sorted.getA() || unsorted.getD() == sorted.getB()))) {

            return this.getA();
        } else if (((unsorted.getA() == sorted.getA() || unsorted.getA() == sorted.getC()) && (unsorted.getB() == sorted.getA() || unsorted.getB() == sorted.getC())) ||
                ((unsorted.getC() == sorted.getA() || unsorted.getC() == sorted.getC()) && (unsorted.getD() == sorted.getA() || unsorted.getD() == sorted.getC()))) {

            return this.getB();
        } else if (((unsorted.getA() == sorted.getA() || unsorted.getA() == sorted.getD()) && (unsorted.getB() == sorted.getA() || unsorted.getB() == sorted.getD())) ||
                ((unsorted.getC() == sorted.getA() || unsorted.getC() == sorted.getD()) && (unsorted.getD() == sorted.getA() || unsorted.getD() == sorted.getD()))) {

            return this.getC();
        }

        return 0.0;
    }


    public void normalise(boolean log, boolean useMax) {

        // lowest length must be nonnegative
        double newA = this.getA();
        double newB = this.getB();
        double newC = this.getC();

        if (log) {
            newA = logNormValue(this.getA());
            newB = logNormValue(this.getB());
            newC = logNormValue(this.getC());
        }

        if (useMax) {
            double min = Math.min(Math.min(newA, newB), newC);

            this.setA(newA - min);
            this.setB(newB - min);
            this.setC(newC - min);
        } else {
            double max = Math.max(Math.max(newA, newB), newC);

            this.setA(max - newA);
            this.setB(max - newB);
            this.setC(max - newC);
        }
    }


    public double scalarProduct(QuartetWeights other) {
        return (this.getA() * other.getA()) + (this.getB() * other.getB()) + (this.getC() * other.getC());
    }

    public double squaredLength() {
        return Math.pow(this.getA(), 2.0) + Math.pow(this.getB(), 2.0) + Math.pow(this.getC(), 2.0);
    }

    public QuartetWeights permute(int t1, int t2, int t3, int t4) {

        // Sort taxa ascending
        int[] ta = new SpectreQuartet(t1, t2, t3, t4).createSortedCopy().toIntArray();

        // Shortcuts to variables
        double a = this.getA();
        double b = this.getB();
        double c = this.getC();

        // Auxiliary variables
        double newA = 0.0;
        double newB = 0.0;
        double newC = 0.0;

        // Compute permuted weights
        if ((t1 == ta[0]) && (t2 == ta[1]) && (t3 == ta[2]) && (t4 == ta[3])) {
            newA = a;
            newB = b;
            newC = c;
        }
        if ((t1 == ta[0]) && (t2 == ta[1]) && (t4 == ta[2]) && (t3 == ta[3])) {
            newA = a;
            newB = c;
            newC = b;
        }
        if ((t1 == ta[0]) && (t3 == ta[1]) && (t2 == ta[2]) && (t4 == ta[3])) {
            newA = b;
            newB = a;
            newC = c;
        }
        if ((t1 == ta[0]) && (t3 == ta[1]) && (t4 == ta[2]) && (t2 == ta[3])) {
            newA = c;
            newB = a;
            newC = b;
        }
        if ((t1 == ta[0]) && (t4 == ta[1]) && (t2 == ta[2]) && (t3 == ta[3])) {
            newA = b;
            newB = c;
            newC = a;
        }
        if ((t1 == ta[0]) && (t4 == ta[1]) && (t3 == ta[2]) && (t2 == ta[3])) {
            newA = c;
            newB = b;
            newC = a;
        }
        if ((t2 == ta[0]) && (t1 == ta[1]) && (t3 == ta[2]) && (t4 == ta[3])) {
            newA = a;
            newB = c;
            newC = b;
        }
        if ((t2 == ta[0]) && (t1 == ta[1]) && (t4 == ta[2]) && (t3 == ta[3])) {
            newA = a;
            newB = b;
            newC = c;
        }
        if ((t2 == ta[0]) && (t3 == ta[1]) && (t1 == ta[2]) && (t4 == ta[3])) {
            newA = b;
            newB = c;
            newC = a;
        }
        if ((t2 == ta[0]) && (t3 == ta[1]) && (t4 == ta[2]) && (t1 == ta[3])) {
            newA = c;
            newB = b;
            newC = a;
        }
        if ((t2 == ta[0]) && (t4 == ta[1]) && (t1 == ta[2]) && (t3 == ta[3])) {
            newA = b;
            newB = a;
            newC = c;
        }
        if ((t2 == ta[0]) && (t4 == ta[1]) && (t3 == ta[2]) && (t1 == ta[3])) {
            newA = c;
            newB = a;
            newC = b;
        }
        if ((t3 == ta[0]) && (t1 == ta[1]) && (t2 == ta[2]) && (t4 == ta[3])) {
            newA = c;
            newB = a;
            newC = b;
        }
        if ((t3 == ta[0]) && (t1 == ta[1]) && (t4 == ta[2]) && (t2 == ta[3])) {
            newA = b;
            newB = a;
            newC = c;
        }
        if ((t3 == ta[0]) && (t2 == ta[1]) && (t1 == ta[2]) && (t4 == ta[3])) {
            newA = c;
            newB = b;
            newC = a;
        }
        if ((t3 == ta[0]) && (t2 == ta[1]) && (t4 == ta[2]) && (t1 == ta[3])) {
            newA = b;
            newB = c;
            newC = a;
        }
        if ((t3 == ta[0]) && (t4 == ta[1]) && (t1 == ta[2]) && (t2 == ta[3])) {
            newA = a;
            newB = b;
            newC = c;
        }
        if ((t3 == ta[0]) && (t4 == ta[1]) && (t2 == ta[2]) && (t1 == ta[3])) {
            newA = a;
            newB = c;
            newC = b;
        }
        if ((t4 == ta[0]) && (t1 == ta[1]) && (t2 == ta[2]) && (t3 == ta[3])) {
            newA = c;
            newB = b;
            newC = a;
        }
        if ((t4 == ta[0]) && (t1 == ta[1]) && (t3 == ta[2]) && (t2 == ta[3])) {
            newA = b;
            newB = c;
            newC = a;
        }
        if ((t4 == ta[0]) && (t2 == ta[1]) && (t1 == ta[2]) && (t3 == ta[3])) {
            newA = c;
            newB = a;
            newC = b;
        }
        if ((t4 == ta[0]) && (t2 == ta[1]) && (t3 == ta[2]) && (t1 == ta[3])) {
            newA = b;
            newB = a;
            newC = c;
        }
        if ((t4 == ta[0]) && (t3 == ta[1]) && (t1 == ta[2]) && (t2 == ta[3])) {
            newA = a;
            newB = c;
            newC = b;
        }
        if ((t4 == ta[0]) && (t3 == ta[1]) && (t2 == ta[2]) && (t1 == ta[3])) {
            newA = a;
            newB = b;
            newC = c;
        }

        // Reset the state
        return new QuartetWeights(newA, newB, newC);
    }


    /**
     * just a potentially weighted mean with this, quartets supported by few trees will be weak, although if the
     * trees are strong, they may be too
     *
     * @param other
     * @param weight
     */
    public QuartetWeights weightedIncrement(QuartetWeights other, double weight) {

        this.setA(this.getA() + weight * other.getA());
        this.setB(this.getB() + weight * other.getB());
        this.setC(this.getC() + weight * other.getC());

        return this;
    }


    @Override
    public String toString() {
        return "weights: " + this.getA() + " " + this.getB() + " " + this.getC();
    }

    public void multiply(double weight) {
        this.setA(this.getA() * weight);
        this.setB(this.getB() * weight);
        this.setC(this.getC() * weight);
    }
}
