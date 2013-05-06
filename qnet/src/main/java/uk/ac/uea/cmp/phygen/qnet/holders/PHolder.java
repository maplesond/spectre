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
package uk.ac.uea.cmp.phygen.qnet.holders;

import uk.ac.uea.cmp.phygen.qnet.TaxonList;

/**
 * PHolder class
 */
public class PHolder {

    /**
     * ArrayList of ArrayLists of etc. of triplets
     * <p/>
     * and a get and set method
     */
    public PHolder() {

        theSize = 0;

    }

    /**
     * Local overs
     */
    public static int over4(int n) {

        if (n > 4) {

            return n * (n - 1) * (n - 2) * (n - 3) / 24;

        } else if (n == 4) {

            return 1;

        } else {

            return 0;

        }

    }

    public static int over3(int n) {

        if (n > 3) {

            return n * (n - 1) * (n - 2) / 6;

        } else if (n == 3) {

            return 1;

        } else {

            return 0;

        }

    }

    public static int over2(int n) {

        if (n > 2) {

            return n * (n - 1) / 2;

        } else if (n == 2) {

            return 1;

        } else {

            return 0;

        }

    }

    public static int over1(int n) {

        if (n > 1) {

            return n;

        } else if (n == 1) {

            return 1;

        } else {

            return 0;

        }

    }

    /**
     * Check if filled...
     */
    public int getSize() {

        return theSize;

    }

    /**
     * Ensure capacity...
     */
    public void ensureCapacity(int N) {

        data = new PHContent[over4(N)];
        cSize = N;

    }

    public void initialize() {

        int N = data.length;

        for (int n = 0; n < N; n++) {

            PHContent d = new PHContent(0, 0);
            data[n] = d;

        }

    }

    public void setSize(int theNewSize) {

        theSize = theNewSize;

    }

    public int getP(int a, int b, int c, int d) {

        // we assume size-order and one-upmanship
        // we check for d > N

        if (!(a < b && b < c && c < d)) {

            return 0;

        }

        if (d > cSize) {

            // if requesting something after the last (remembering we have one-upmanship

            // we work with out of d - n, a, b, c

            return data[over1(d - cSize - 1) + over2(a - 1) + over3(b - 1) + over4(c - 1)].getOuterP();

        } else {

            // we work with in of a, b, c, d

            return data[over1(a - 1) + over2(b - 1) + over3(c - 1) + over4(d - 1)].getInnerP();

        }

    }

    public int getQ(int a, int b, int c, int d) {

        // we assume size-order and one-upmanship
        // we check for d > N

        if (d > cSize) {

            // if requesting something after the last (remembering we have one-upmanship

            // we work with out of d - n, a, b, c

//            System.out.println ("outer q " + a + " " + b + " " + c + " " + d + " gets " + data[over4 (d - cSize - 1) + over3 (a - 1) + over2 (b - 1) + over1 (c - 1)].getOuterQ ());
//            System.out.println ("inner q " + a + " " + b + " " + c + " " + d + " gets " + data[over4 (d - cSize - 1) + over3 (a - 1) + over2 (b - 1) + over1 (c - 1)].getInnerQ ());

            return data[over1(d - cSize - 1) + over2(a - 1) + over3(b - 1) + over4(c - 1)].getOuterQ();

        } else {

            // we work with in of a, b, c, d

//            System.out.println ("outer q " + a + " " + b + " " + c + " " + d + " gets " + data[over4 (a - 1) + over3 (b - 1) + over2 (c - 1) + over1 (d - 1)].getOuterQ ());
//            System.out.println ("inner q " + a + " " + b + " " + c + " " + d + " gets " + data[over4 (a - 1) + over3 (b - 1) + over2 (c - 1) + over1 (d - 1)].getInnerQ ());

            return data[over1(a - 1) + over2(b - 1) + over3(c - 1) + over4(d - 1)].getInnerQ();

        }

    }

    public boolean getR(int a, int b, int c, int d, TaxonList cT) {

        if (d > cT.size()) {

            int oD = d;

            d = c;
            c = b;
            b = a;
            a = oD - cT.size();

        }

        // this is an ugly hack
        // we seek mapped a < b < c < d

        a = ((Integer) cT.get(a - 1)).intValue();
        b = ((Integer) cT.get(b - 1)).intValue();
        c = ((Integer) cT.get(c - 1)).intValue();
        d = ((Integer) cT.get(d - 1)).intValue();

        int m;

        if (c > d) {

            m = d;
            d = c;
            c = m;

        }

        if (b > d) {

            m = d;
            d = b;
            b = m;

        }

        if (a > d) {

            m = d;
            d = a;
            a = m;

        }

        if (b > c) {

            m = c;
            c = b;
            b = m;

        }

        if (a > c) {

            m = c;
            c = a;
            a = m;

        }

        if (a > b) {

            m = b;
            b = a;
            a = m;

        }

        // now we have size-order and proper mapping

        // we assume size-order and one-upmanship
        // we check for d > N

        if (d > cSize) {

            // if requesting something after the last (remembering we have one-upmanship

            // we work with out of d - n, a, b, c

            return data[over1(d - cSize - 1) + over2(a - 1) + over3(b - 1) + over4(c - 1)].getR();

        } else {

            // we work with in of a, b, c, d

            return data[over1(a - 1) + over2(b - 1) + over3(c - 1) + over4(d - 1)].getR();

        }

    }

    public void setP(int a, int b, int c, int d, int newW) {

        // we assume size-order and one-upmanship
        // we check for d > N

        if (d > cSize) {

            // if requesting something after the last (remembering we have one-upmanship

            // we work with out of d - n, a, b, c

            data[over1(d - cSize - 1) + over2(a - 1) + over3(b - 1) + over4(c - 1)].setOuterP(newW);

        } else {

            // we work with in of a, b, c, d

            data[over1(a - 1) + over2(b - 1) + over3(c - 1) + over4(d - 1)].setInnerP(newW);

        }

    }

    public void setQ(int a, int b, int c, int d, int newW) {

        // we assume size-order and one-upmanship
        // we check for d > N

        if (d > cSize) {

            // if requesting something after the last (remembering we have one-upmanship

            // we work with out of d - n, a, b, c

            data[over1(d - cSize - 1) + over2(a - 1) + over3(b - 1) + over4(c - 1)].setOuterQ(newW);

        } else {

            // we work with in of a, b, c, d

            data[over1(a - 1) + over2(b - 1) + over3(c - 1) + over4(d - 1)].setInnerQ(newW);

        }

    }

    public void setR(int a, int b, int c, int d, boolean newW) {

        // we assume size-order and one-upmanship
        // we check for d > N

        if (d > cSize) {

            // if requesting something after the last (remembering we have one-upmanship

            // we work with out of d - n, a, b, c

            data[over1(d - cSize - 1) + over2(a - 1) + over3(b - 1) + over4(c - 1)].setR(newW);

        } else {

            // we work with in of a, b, c, d

            data[over1(a - 1) + over2(b - 1) + over3(c - 1) + over4(d - 1)].setR(newW);

        }

    }

    public PHContent[] getData() {

        return data;

    }

    public void setData(PHContent[] newData) {

        data = newData;

    }

    /**
     * The heart of the data structure
     */
    PHContent[] data;
    // this is N
    int cSize;
    /**
     * Number of actual quartets
     */
    int theSize;
}

/**
 * Small helper - the first is p/q (a, b, c, d) - i.e. inner join the second is p/q (b, c, d, n + a) - i.e. outer join
 * it is still defined for a, b, c, d
 */
class PHContent {

    int outerP, innerP;
    int outerQ, innerQ;
    boolean R;

    public PHContent(int out, int in) {

        this.outerP = out;
        this.innerP = in;

        innerQ = 0;
        outerQ = 0;
        R = false;

    }

    public boolean getR() {

        return R;

    }

    public void setR(boolean r) {

        this.R = r;

    }

    public int getOuterQ() {

        return outerQ;

    }

    public void setOuterQ(int outerQ) {

        this.outerQ = outerQ;

    }

    public int getInnerQ() {

        return innerQ;

    }

    public void setInnerQ(int innerQ) {

        this.innerQ = innerQ;

    }

    public int getOuterP() {

        return outerP;

    }

    public void setOuterP(int outerP) {

        this.outerP = outerP;

    }

    public int getInnerP() {

        return innerP;

    }

    public void setInnerP(int innerP) {

        this.innerP = innerP;

    }
}