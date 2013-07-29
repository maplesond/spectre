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

package uk.ac.uea.cmp.phygen.tools.scale;

import uk.ac.uea.cmp.phygen.core.math.tuple.Key;

/**
 * this class is used to store the 3-dimensional weight vector that is used to 
 * describe for a 4-set the weights of the quartets
 */
public class WeightVector {
    //weights of the quartets on a 4-set {t1,t2,t3,t4}
    //{t1,t2}|{t3,t4}

    public double w1;
    //{t1,t3}|{t2,t4}
    public double w2;
    //{t1,t4}|{t2,t3}
    public double w3;

    public WeightVector(double w1, double w2, double w3) {
        this.w1 = w1;
        this.w2 = w2;
        this.w3 = w3;
    }

    public double scalarProduct(WeightVector wv) {
        return ((this.w1) * (wv.w1)) + ((this.w2) * (wv.w2)) + ((this.w3) * (wv.w3));
    }

    public double squaredLength() {
        return ((this.w1) * (this.w1)) + ((this.w2) * (this.w2)) + ((this.w3) * (this.w3));
    }

    //needed to map weights 
    public void permute(int t1, int t2, int t3, int t4) {
        //sort taxa increasingly
        int[] a = Key.sortElements(t1, t2, t3, t4);

        //auxiliary variables
        double neww1 = 0.0;
        double neww2 = 0.0;
        double neww3 = 0.0;

        //compute permuted weights
        if ((t1 == a[0]) && (t2 == a[1]) && (t3 == a[2]) && (t4 == a[3])) {
            neww1 = w1;
            neww2 = w2;
            neww3 = w3;
        }
        if ((t1 == a[0]) && (t2 == a[1]) && (t4 == a[2]) && (t3 == a[3])) {
            neww1 = w1;
            neww2 = w3;
            neww3 = w2;
        }
        if ((t1 == a[0]) && (t3 == a[1]) && (t2 == a[2]) && (t4 == a[3])) {
            neww1 = w2;
            neww2 = w1;
            neww3 = w3;
        }
        if ((t1 == a[0]) && (t3 == a[1]) && (t4 == a[2]) && (t2 == a[3])) {
            neww1 = w3;
            neww2 = w1;
            neww3 = w2;
        }
        if ((t1 == a[0]) && (t4 == a[1]) && (t2 == a[2]) && (t3 == a[3])) {
            neww1 = w2;
            neww2 = w3;
            neww3 = w1;
        }
        if ((t1 == a[0]) && (t4 == a[1]) && (t3 == a[2]) && (t2 == a[3])) {
            neww1 = w3;
            neww2 = w2;
            neww3 = w1;
        }
        if ((t2 == a[0]) && (t1 == a[1]) && (t3 == a[2]) && (t4 == a[3])) {
            neww1 = w1;
            neww2 = w3;
            neww3 = w2;
        }
        if ((t2 == a[0]) && (t1 == a[1]) && (t4 == a[2]) && (t3 == a[3])) {
            neww1 = w1;
            neww2 = w2;
            neww3 = w3;
        }
        if ((t2 == a[0]) && (t3 == a[1]) && (t1 == a[2]) && (t4 == a[3])) {
            neww1 = w2;
            neww2 = w3;
            neww3 = w1;
        }
        if ((t2 == a[0]) && (t3 == a[1]) && (t4 == a[2]) && (t1 == a[3])) {
            neww1 = w3;
            neww2 = w2;
            neww3 = w1;
        }
        if ((t2 == a[0]) && (t4 == a[1]) && (t1 == a[2]) && (t3 == a[3])) {
            neww1 = w2;
            neww2 = w1;
            neww3 = w3;
        }
        if ((t2 == a[0]) && (t4 == a[1]) && (t3 == a[2]) && (t1 == a[3])) {
            neww1 = w3;
            neww2 = w1;
            neww3 = w2;
        }
        if ((t3 == a[0]) && (t1 == a[1]) && (t2 == a[2]) && (t4 == a[3])) {
            neww1 = w3;
            neww2 = w1;
            neww3 = w2;
        }
        if ((t3 == a[0]) && (t1 == a[1]) && (t4 == a[2]) && (t2 == a[3])) {
            neww1 = w2;
            neww2 = w1;
            neww3 = w3;
        }
        if ((t3 == a[0]) && (t2 == a[1]) && (t1 == a[2]) && (t4 == a[3])) {
            neww1 = w3;
            neww2 = w2;
            neww3 = w1;
        }
        if ((t3 == a[0]) && (t2 == a[1]) && (t4 == a[2]) && (t1 == a[3])) {
            neww1 = w2;
            neww2 = w3;
            neww3 = w1;
        }
        if ((t3 == a[0]) && (t4 == a[1]) && (t1 == a[2]) && (t2 == a[3])) {
            neww1 = w1;
            neww2 = w2;
            neww3 = w3;
        }
        if ((t3 == a[0]) && (t4 == a[1]) && (t2 == a[2]) && (t1 == a[3])) {
            neww1 = w1;
            neww2 = w3;
            neww3 = w2;
        }
        if ((t4 == a[0]) && (t1 == a[1]) && (t2 == a[2]) && (t3 == a[3])) {
            neww1 = w3;
            neww2 = w2;
            neww3 = w1;
        }
        if ((t4 == a[0]) && (t1 == a[1]) && (t3 == a[2]) && (t2 == a[3])) {
            neww1 = w2;
            neww2 = w3;
            neww3 = w1;
        }
        if ((t4 == a[0]) && (t2 == a[1]) && (t1 == a[2]) && (t3 == a[3])) {
            neww1 = w3;
            neww2 = w1;
            neww3 = w2;
        }
        if ((t4 == a[0]) && (t2 == a[1]) && (t3 == a[2]) && (t1 == a[3])) {
            neww1 = w2;
            neww2 = w1;
            neww3 = w3;
        }
        if ((t4 == a[0]) && (t3 == a[1]) && (t1 == a[2]) && (t2 == a[3])) {
            neww1 = w1;
            neww2 = w3;
            neww3 = w2;
        }
        if ((t4 == a[0]) && (t3 == a[1]) && (t2 == a[2]) && (t1 == a[3])) {
            neww1 = w1;
            neww2 = w2;
            neww3 = w3;
        }

        w1 = neww1;
        w2 = neww2;
        w3 = neww3;
    }
}