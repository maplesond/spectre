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
package uk.ac.uea.cmp.phygen.core.ds.quartet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phygen.core.math.tuple.Triplet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * QuartetWeights class
 */
public class QuartetWeights {

    private static Logger log = LoggerFactory.getLogger(QuartetWeights.class);

    /**
     * The heart of the data structure
     */
    private List<TripletFP> data;
    /**
     * Number of actual quartets
     */
    private int size;

    public QuartetWeights() {

        size = 0;
    }

    /**
     * Local overs
     */
    public static int over4(int n) {

        return n > 4 ?
                n * (n - 1) * (n - 2) * (n - 3) / 24 :
                n == 4 ? 1 : 0;
    }

    public static int over3(int n) {

        return n > 3 ?
                n * (n - 1) * (n - 2) / 6 :
                n == 3 ? 1 : 0;
    }

    public static int over2(int n) {

        return n > 2 ?
                n * (n - 1) / 2 :
                n == 2 ? 1 : 0;
    }

    public static int over1(int n) {

        return n > 0 ? n : 0;
    }



    /**
     * Ensure capacity...
     */
    public void ensureCapacity(int N) {

        data = new ArrayList<>(over4(N));
    }

    public void initialize() {

        int N = data.size();

        for (int n = 0; n < N; n++) {
            data.set(n, new TripletFP(0.0, 0.0, 0.0));
        }

    }


    /**
     * Check if filled...
     */
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    /**
     * getWeight gets a weight
     */
    public double getWeight(int a, int b, int c, int d) {

        if (data.size() == 0)
            return 0.0;

        /**
         *
         * Size-order and decide which topology this is
         *
         * Access the lists, return the value there
         *
         */
        /**
         *
         * Create size-ordered quadruple x, y, u, v from a, b, c, d
         *
         */
        int x = a;
        int y = b;
        int u = c;
        int v = d;
        int m;

        if (y > x) {

            m = x;
            x = y;
            y = m;

        }

        if (u > x) {

            m = x;
            x = u;
            u = m;

        }

        if (v > x) {

            m = x;
            x = v;
            v = m;

        }

        if (u > y) {

            m = y;
            y = u;
            u = m;

        }

        if (v > y) {

            m = y;
            y = v;
            v = m;

        }

        if (v > u) {

            m = u;
            u = v;
            v = m;

        }

        /**
         *
         * Next, use size-ordered numbers to access the triple
         *
         */
        int index = over4(x - 1) + over3(y - 1) + over2(u - 1) + over1(v - 1);
        Triplet w = data.get(index);

        /**
         *
         * Now, determine which quartet to take. Use the unordered numbers, they
         * match one ordering or other
         *
         */
        /**
         *
         * Result variable
         *
         */
        double result = 0.0;

        /**
         *
         * If either first or last pair is first or last pair of the stored one,
         * use first weight
         *
         */
        if (((a == x || a == y) && (b == x || b == y)) || ((c == x || c == y) && (d == x || d == y))) {

            result = w.getA().doubleValue();

        } /**
         *
         * Next possibility
         *
         */
        else if (((a == x || a == u) && (b == x || b == u)) || ((c == x || c == u) && (d == x || d == u))) {

            result = w.getB().doubleValue();

        } /**
         *
         * Third possibility
         *
         */
        else if (((a == x || a == v) && (b == x || b == v)) || ((c == x || c == v) && (d == x || d == v))) {

            result = w.getC().doubleValue();

        }

        return result;

    }

    /**
     * setWeight sets three weights
     */
    public void setWeight(int a, int b, int c, int d, double w1, double w2, double w3) {

        /**
         *
         * Size-order, find which is which
         *
         */
        /**
         *
         * Create size-ordered quadruple x, y, u, v from a, b, c, d
         *
         */
        int x = a;
        int y = b;
        int u = c;
        int v = d;
        int m;

        if (y > x) {

            m = x;
            x = y;
            y = m;

        }

        if (u > x) {

            m = x;
            x = u;
            u = m;

        }

        if (v > x) {

            m = x;
            x = v;
            v = m;

        }

        if (u > y) {

            m = y;
            y = u;
            u = m;

        }

        if (v > y) {

            m = y;
            y = v;
            v = m;

        }

        if (v > u) {

            m = u;
            u = v;
            v = m;

        }

        /**
         *
         * Investigate which topology of a, b, c, d that the topologies of x, y,
         * u, v correspond to, and set weights accordingly
         *
         */
        /**
         *
         * See if xy|uv is ab|cd (w1), ac|bd (w2), or ad|bc (w3)
         *
         */
        /**
         *
         * The first weight of the stored triplet
         *
         */
        double r1 = 0;

        if (((x == a || x == b) && (y == a || y == b)) || ((u == a || u == b) && (v == a || v == b))) {

            r1 = w1;

        } else if (((x == a || x == c) && (y == a || y == c)) || ((u == a || u == c) && (v == a || v == c))) {

            r1 = w2;

        } else if (((x == a || x == d) && (y == a || y == d)) || ((u == a || u == d) && (v == a || v == d))) {

            r1 = w3;

        }

        /**
         *
         * See if xu|yv is ab|cd (w1), ac|bd (w2), or ad|bc (w3)
         *
         */
        /**
         *
         * The second weight of the stored triplet
         *
         */
        double r2 = 0;

        if (((x == a || x == b) && (u == a || u == b)) || ((y == a || y == b) && (v == a || v == b))) {

            r2 = w1;

        } else if (((x == a || x == c) && (u == a || u == c)) || ((y == a || y == c) && (v == a || v == c))) {

            r2 = w2;

        } else if (((x == a || x == d) && (u == a || u == d)) || ((y == a || y == d) && (v == a || v == d))) {

            r2 = w3;

        }

        /**
         *
         * See if xv|uy is ab|cd (w1), ac|bd (w2), or ad|bc (w3)
         *
         */
        /**
         *
         * The third weight of the stored triplet
         *
         */
        double r3 = 0;

        if (((x == a || x == b) && (v == a || v == b)) || ((u == a || u == b) && (y == a || y == b))) {

            r3 = w1;

        } else if (((x == a || x == c) && (v == a || v == c)) || ((u == a || u == c) && (y == a || y == c))) {

            r3 = w2;

        } else if (((x == a || x == d) && (v == a || v == d)) || ((u == a || u == d) && (y == a || y == d))) {

            r3 = w3;

        }

        TripletFP w = new TripletFP(r1, r2, r3);

        /**
         *
         * Set to w
         *
         */
        data.set(over4(x - 1) + over3(y - 1) + over2(u - 1) + over1(v - 1), w);

        size++;

    }

    /**
     * setWeight sets a weight
     */
    public void incrementWeight(int a, int b, int c, int d, double increment) {
        this.setWeight(a, b, c, d, this.getWeight(a, b, c, d) + increment);
    }

    /**
     * setWeight sets a weight
     */
    public void setWeight(int a, int b, int c, int d, double newW) {

        /**
         *
         * Size-order, find which is which
         *
         */
        /**
         *
         * Create size-ordered quadruple x, y, u, v from a, b, c, d
         *
         */
        int x = a;
        int y = b;
        int u = c;
        int v = d;
        int m;

        if (y > x) {

            m = x;
            x = y;
            y = m;

        }

        if (u > x) {

            m = x;
            x = u;
            u = m;

        }

        if (v > x) {

            m = x;
            x = v;
            v = m;

        }

        if (u > y) {

            m = y;
            y = u;
            u = m;

        }

        if (v > y) {

            m = y;
            y = v;
            v = m;

        }

        if (v > u) {

            m = u;
            u = v;
            v = m;

        }
//try{
        TripletFP w = data.get(over4(x - 1) + over3(y - 1) + over2(u - 1) + over1(v - 1));

        /**
         *
         * Investigate which topology of a, b, c, d that the topologies of x, y,
         * u, v correspond to, and set weights accordingly
         *
         */
        /**
         *
         * See if xy|uv is ab|cd (w1), ac|bd (w2), or ad|bc (w3)
         *
         */
        /**
         *
         * The first weight of the stored triplet
         *
         */
        if (((x == a || x == b) && (y == a || y == b)) || ((u == a || u == b) && (v == a || v == b))) {

            w.setA(newW);
        }
        else if (((x == a || x == b) && (u == a || u == b)) || ((y == a || y == b) && (v == a || v == b))) {

            w.setB(newW);
        }
        else if (((x == a || x == b) && (v == a || v == b)) || ((u == a || u == b) && (y == a || y == b))) {

            w.setC(newW);
        }

        data.set(over4(x - 1) + over3(y - 1) + over2(u - 1) + over1(v - 1), w);
    }





    public static class TripletFP extends Triplet<Double> {

        public TripletFP(double a, double b, double c) {
            super(a,b,c);
        }

        public boolean allNonZero() {
            return this.getA() != 0 && this.getB() != 0 && this.getC() != 0;
        }

        private double logNormValue(double value) {
            if (value < 0.0) {
                throw new IllegalStateException("Error: Quartet file contains negative weight!");
            }

            return value == 0.0 ? -15.0 : Math.log(value);
        }


        public void normalise(boolean log, boolean useMax) {

            // lowest weight must be nonnegative
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
            }
            else {
                double max = Math.max(Math.max(newA, newB), newC);

                this.setA(max - newA);
                this.setB(max - newB);
                this.setC(max - newC);
            }
        }

        public void divide(TripletFP other) {

            if (this.allNonZero()) {
                this.setA(this.getA() / other.getA());
                this.setB(this.getB() / other.getB());
                this.setC(this.getC() / other.getC());
            }
        }

        /**
         * just a potentially weighted mean with this, quartets supported by few trees will be weak, although if the
         * trees are strong, they may be too
         * @param other
         * @param weight
         */
        public void weightedSum(TripletFP other, double weight) {

            this.setA(this.getA() + weight * other.getA());
            this.setB(this.getB() + weight * other.getB());
            this.setC(this.getC() + weight * other.getC());
        }
    }

    public void normalize(boolean useMax) {

        for (int n = 0; n < data.size(); n++) {
            data.get(n).normalise(false, useMax);
        }
    }

    public void logNormalize(boolean useMax) {

        for (int n = 0; n < data.size(); n++) {
            data.get(n).normalise(true, useMax);
        }
    }

    public List<TripletFP> getData() {
        return data;
    }

    public void setData(List<TripletFP> newData) {
        data = newData;
    }

    public QuartetWeights translate(LinkedList taxonNamesOld, LinkedList taxonNamesNew) {

        int NNew = taxonNamesNew.size();
        int NOld = taxonNamesOld.size();

        this.ensureCapacity(NNew);
        this.initialize();

        for (int iA = 0; iA < NOld - 3; iA++) {

            for (int iB = iA + 1; iB < NOld - 2; iB++) {

                for (int iC = iB + 1; iC < NOld - 1; iC++) {

                    for (int iD = iC + 1; iD < NOld; iD++) {

                        int a = iA + 1;
                        int b = iB + 1;
                        int c = iC + 1;
                        int d = iD + 1;

                        int nA = taxonNamesNew.indexOf(taxonNamesOld.get(iA)) + 1;
                        int nB = taxonNamesNew.indexOf(taxonNamesOld.get(iB)) + 1;
                        int nC = taxonNamesNew.indexOf(taxonNamesOld.get(iC)) + 1;
                        int nD = taxonNamesNew.indexOf(taxonNamesOld.get(iD)) + 1;

                        this.setWeight(nA, nB, nC, nD, getWeight(a, b, c, d));
                        this.setWeight(nA, nC, nB, nD, getWeight(a, c, b, d));
                        this.setWeight(nA, nD, nB, nC, getWeight(a, d, b, c));

                    }

                }

            }

        }

        return this;
    }


    public MeanSumResult meanSum(List<Integer> aL, List<Integer> bL, List<Integer> cL, List<Integer> dL) {

        int count = 0;
        double score = 0;

        for (int a = 0; a < aL.size(); a++) {
            for (int b = 0; b < bL.size(); b++) {
                for (int c = 0; c < cL.size(); c++) {
                    for (int d = 0; d < dL.size(); d++) {

                        int x = aL.get(a);
                        int y = bL.get(b);
                        int u = cL.get(c);
                        int v = dL.get(d);

                        score += this.getWeight(x, y, u, v);

                        count++;
                    }
                }
            }
        }

        return new MeanSumResult(count, score);
    }


    public static class MeanSumResult {
        private int count;
        private double score;

        public MeanSumResult(final int count, final double score) {
            this.count = count;
            this.score = score;
        }

        public int getCount() {
            return count;
        }

        public double getScore() {
            return score;
        }
    }


    public void divide(QuartetWeights summer) {

        for (int n = 0; n < this.data.size(); n++) {
            this.data.get(n).divide(summer.getData().get(n));
        }
    }

    /**
     * note: this now simply computes a weighted sum. This is the part that may be done in any number of ways here,
     * take weighted sum of every quartet where the quartet is nonzero
     */
    public void add(QuartetWeights aW, double w) {

        for (int n = 0; n < this.data.size(); n++) {
            this.data.get(n).weightedSum(aW.getData().get(n), w);
        }
    }


}