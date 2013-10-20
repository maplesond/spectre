/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
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

package uk.ac.uea.cmp.phygen.core.math.optimise;

public class Bounds {

    public static enum BoundType {

        FREE,
        LOWER,
        UPPER,
        DOUBLE,
        FIXED
    }

    private double lower;
    private double upper;
    private BoundType boundType;

    public Bounds() {
        this(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, BoundType.FREE);
    }

    public Bounds(double bound, BoundType boundType) {
        if (boundType == BoundType.DOUBLE) {
            throw new IllegalArgumentException("BoundType.DOUBLE (a double bounded variable), requires both a lower and an upper bound.  Please use the alternate constructor for this BoundType");
        } else if (boundType == BoundType.FREE) {
            throw new IllegalArgumentException("BoundType.FREE (a non-bounded variable), does not require a bound.  Please use the default constructor for this BoundType.");
        } else if (boundType == BoundType.LOWER) {
            this.lower = bound;
            this.upper = Double.POSITIVE_INFINITY;
            this.boundType = boundType;
        } else if (boundType == BoundType.UPPER) {
            this.lower = Double.NEGATIVE_INFINITY;
            this.upper = bound;
            this.boundType = boundType;
        } else if (boundType == BoundType.FIXED) {
            this.lower = bound;
            this.upper = bound;
            this.boundType = boundType;
        } else {
            throw new IllegalArgumentException("Unrecognized BoundType: " + boundType.toString());
        }
    }

    public Bounds(double lower, double upper, BoundType boundType) {
        this.lower = lower;
        this.upper = upper;
        this.boundType = boundType;
    }

    public double getLower() {
        return lower;
    }

    public double getUpper() {
        return upper;
    }

    public BoundType getBoundType() {
        return boundType;
    }
}