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
package uk.ac.uea.cmp.phygen.superq.qnet.holders;

import uk.ac.uea.cmp.phygen.core.ds.Taxa;
import uk.ac.uea.cmp.phygen.core.ds.quartet.GroupedQuartetSystem;
import uk.ac.uea.cmp.phygen.core.ds.quartet.Quartet;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetSystem;
import uk.ac.uea.cmp.phygen.core.ds.quartet.QuartetWeights;

import java.util.Map;

/**
 * PHolder class
 */
public class PHolder {

    /**
     * The heart of the data structure
     */
    private PHContent[] data;

    /**
     * this is N
     */
    private int cSize;
    /**
     * Number of actual quartets
     */
    private int theSize;

    /**
     * ArrayList of ArrayLists of etc. of triplets
     * <p/>
     * and a get and set method
     */
    public PHolder() {

        theSize = 0;
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

        data = new PHContent[Quartet.over4(N)];
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

            return data[Quartet.over1(d - cSize - 1) + Quartet.over2(a - 1) + Quartet.over3(b - 1) + Quartet.over4(c - 1)].getOuterP();
        }
        else {

            // we work with in of a, b, c, d

            return data[Quartet.over1(a - 1) + Quartet.over2(b - 1) + Quartet.over3(c - 1) + Quartet.over4(d - 1)].getInnerP();
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

            return data[Quartet.over1(d - cSize - 1) + Quartet.over2(a - 1) + Quartet.over3(b - 1) + Quartet.over4(c - 1)].getOuterQ();

        }
        else {

            // we work with in of a, b, c, d

//            System.out.println ("outer q " + a + " " + b + " " + c + " " + d + " gets " + data[over4 (a - 1) + over3 (b - 1) + over2 (c - 1) + over1 (d - 1)].getOuterQ ());
//            System.out.println ("inner q " + a + " " + b + " " + c + " " + d + " gets " + data[over4 (a - 1) + over3 (b - 1) + over2 (c - 1) + over1 (d - 1)].getInnerQ ());

            return data[Quartet.over1(a - 1) + Quartet.over2(b - 1) + Quartet.over3(c - 1) + Quartet.over4(d - 1)].getInnerQ();
        }
    }

    public boolean getR(int a, int b, int c, int d, Taxa cT) {

        if (d > cT.size()) {

            int oD = d;

            d = c;
            c = b;
            b = a;
            a = oD - cT.size();
        }

        // this is an ugly hack
        // we seek mapped a < b < c < d

        a = cT.get(a - 1).getId();
        b = cT.get(b - 1).getId();
        c = cT.get(c - 1).getId();
        d = cT.get(d - 1).getId();

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

            return data[Quartet.over1(d - cSize - 1) + Quartet.over2(a - 1) + Quartet.over3(b - 1) + Quartet.over4(c - 1)].getR();
        }
        else {

            // we work with in of a, b, c, d

            return data[Quartet.over1(a - 1) + Quartet.over2(b - 1) + Quartet.over3(c - 1) + Quartet.over4(d - 1)].getR();
        }
    }

    public void setP(int a, int b, int c, int d, int newW) {

        // we assume size-order and one-upmanship
        // we check for d > N

        if (d > cSize) {

            // if requesting something after the last (remembering we have one-upmanship

            // we work with out of d - n, a, b, c

            data[Quartet.over1(d - cSize - 1) + Quartet.over2(a - 1) + Quartet.over3(b - 1) + Quartet.over4(c - 1)].setOuterP(newW);
        }
        else {

            // we work with in of a, b, c, d

            data[Quartet.over1(a - 1) + Quartet.over2(b - 1) + Quartet.over3(c - 1) + Quartet.over4(d - 1)].setInnerP(newW);
        }
    }

    public void setQ(int a, int b, int c, int d, int newW) {

        // we assume size-order and one-upmanship
        // we check for d > N

        if (d > cSize) {

            // if requesting something after the last (remembering we have one-upmanship

            // we work with out of d - n, a, b, c

            data[Quartet.over1(d - cSize - 1) + Quartet.over2(a - 1) + Quartet.over3(b - 1) + Quartet.over4(c - 1)].setOuterQ(newW);
        }
        else {

            // we work with in of a, b, c, d

            data[Quartet.over1(a - 1) + Quartet.over2(b - 1) + Quartet.over3(c - 1) + Quartet.over4(d - 1)].setInnerQ(newW);
        }
    }

    public void setR(int a, int b, int c, int d, boolean newW) {

        // we assume size-order and one-upmanship
        // we check for d > N

        if (d > cSize) {

            // if requesting something after the last (remembering we have one-upmanship

            // we work with out of d - n, a, b, c

            data[Quartet.over1(d - cSize - 1) + Quartet.over2(a - 1) + Quartet.over3(b - 1) + Quartet.over4(c - 1)].setR(newW);
        }
        else {

            // we work with in of a, b, c, d

            data[Quartet.over1(a - 1) + Quartet.over2(b - 1) + Quartet.over3(c - 1) + Quartet.over4(d - 1)].setR(newW);
        }
    }


    /**
     * Small helper - the first is p/q (a, b, c, d) - i.e. inner join the second is p/q (b, c, d, n + a) - i.e. outer join
     * it is still defined for a, b, c, d
     */
    static class PHContent {

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


    /**
     * We need structures, probably quartet structures as we deal with several quantities of data.  We calculate
     * these for disjoint quartets by the given formula and the loaded existence information quartet structure.
     * Basically, we need a data structure with a bool and four ints for each quadruple set... we first load this from
     * the information file, then we fill up the same structure from the provided loops and then access below to get the
     * informative interval combinatorics... it is basically defined like outer and inner interval of two types and
     * existence value for each quadruple.
     * @param quartetSystem The quartet system
     */
    public static PHolder create(GroupedQuartetSystem quartetSystem) {

        Taxa c = quartetSystem.getTaxa();
        final int N = c.size();

        PHolder pHolder = new PHolder();
        pHolder.ensureCapacity(N);
        pHolder.initialize();

        // load r values
        for(Map.Entry<Quartet, QuartetWeights> entry : quartetSystem.getQuartets().entrySet()) {

            pHolder.setR(
                    entry.getKey().getA(),
                    entry.getKey().getB(),
                    entry.getKey().getC(),
                    entry.getKey().getD(),
                    entry.getValue().getA() == 1.0);
        }

        // fill upp pHolder by provided loop

        for (int iA = 1; iA < N - 2; iA++) {

            for (int iB = iA + 1; iB < N - 1; iB++) {

                for (int iC = iB + 1; iC < N; iC++) {

                    for (int iD = iC + 1; iD <= N; iD++) {

                        // these are NOT all 1 <= iA < iB < iC < iD <= N

                        if (iB == iA + 1) {

                            if (pHolder.getR(iA + 1, iB + 1, iC + 1, iD + 1, c)) {

                                pHolder.setQ(iA, iB, iC, iD, 1);

                            } else {

                                pHolder.setQ(iA, iB, iC, iD, 0);

                            }

                        }

                        if (iC == iB + 1) {

                            pHolder.setQ(iB, iC, iD, iA + N,
                                    pHolder.getR(iA + 1, iB + 1, iC + 1, iD + 1, c) ? 1 : 0);

                        }
                    }
                }
            }
        }

//        System.out.println ("Second loop");

        for (int iA = 1; iA < N - 1; iA++) {

            for (int iB = iA + 1; iB < N; iB++) {

                for (int iC = iB + 1; iC <= N; iC++) {

                    if (iB > iA + 2) {

                        if (pHolder.getR(iA + 1, iA + 3, iB + 1, iC + 1, c)) {

                            pHolder.setQ(iA, iA + 2, iB, iC, pHolder.getQ(iA + 1, iA + 2, iB, iC)
                                    + pHolder.getQ(iA, iA + 1, iB, iC) + 1);

                        } else {

                            pHolder.setQ(iA, iA + 2, iB, iC, pHolder.getQ(iA + 1, iA + 2, iB, iC)
                                    + pHolder.getQ(iA, iA + 1, iB, iC));

                        }

                    }

                    if (iC > iB + 2) {

                        if (pHolder.getR(iA + 1, iB + 1, iB + 3, iC + 1, c)) {

                            pHolder.setQ(iB, iB + 2, iC, iA + N, pHolder.getQ(iB + 1, iB + 2, iC, iA + N)
                                    + pHolder.getQ(iB, iB + 1, iC, iA + N) + 1);

                        } else {

                            pHolder.setQ(iB, iB + 2, iC, iA + N, pHolder.getQ(iB + 1, iB + 2, iC, iA + N)
                                    + pHolder.getQ(iB, iB + 1, iC, iA + N));

                        }
                    }
                }
            }
        }

        for (int iD = 3; iD < N - 2; iD++) {

            for (int iA = 1; iA < N - 1; iA++) {

                for (int iB = iA + 1; iB < N; iB++) {

                    for (int iC = iB + 1; iC <= N; iC++) {

                        if (iB > iA + iD) {

                            if (pHolder.getR(iA + 1, iA + iD + 1, iB + 1, iC + 1, c)) {

                                pHolder.setQ(iA, iA + iD, iB, iC, pHolder.getQ(iA + 1, iA + iD, iB, iC)
                                        + pHolder.getQ(iA, iA + iD - 1, iB, iC)
                                        - pHolder.getQ(iA + 1, iA + iD - 1, iB, iC) + 1);

                            } else {

                                pHolder.setQ(iA, iA + iD, iB, iC, pHolder.getQ(iA + 1, iA + iD, iB, iC)
                                        + pHolder.getQ(iA, iA + iD - 1, iB, iC)
                                        - pHolder.getQ(iA + 1, iA + iD - 1, iB, iC));

                            }

                        }

                        if (iC > iB + iD) {

                            if (pHolder.getR(iA + 1, iB + 1, iB + iD + 1, iC + 1, c)) {

                                pHolder.setQ(iB, iB + iD, iC, iA + N, pHolder.getQ(iB + 1, iB + iD, iC, iA + N)
                                        + pHolder.getQ(iB, iB + iD - 1, iC, iA + N)
                                        - pHolder.getQ(iB + 1, iB + iD - 1, iC, iA + N) + 1);

                            } else {

                                pHolder.setQ(iB, iB + iD, iC, iA + N, pHolder.getQ(iB + 1, iB + iD, iC, iA + N)
                                        + pHolder.getQ(iB, iB + iD - 1, iC, iA + N)
                                        - pHolder.getQ(iB + 1, iB + iD - 1, iC, iA + N));

                            }
                        }
                    }
                }
            }
        }

        for (int iB = 2; iB < N; iB++) {

            for (int iC = iB + 1; iC < N; iC++) {

                pHolder.setP(1, iB, iC, iC + 1, pHolder.getQ(1, iB, iC, iC + 1));
            }
        }

        for (int iB = 2; iB < N - 2; iB++) {

            for (int iC = iB + 1; iC < N - 1; iC++) {

                pHolder.setP(1, iB, iC, iC + 2, pHolder.getP(1, iB, iC + 1, iC + 2)
                        + pHolder.getP(1, iB, iC, iC + 1)
                        + pHolder.getQ(1, iB, iC, iC + 2));
            }
        }

        for (int iD = 3; iD < N - 2; iD++) {

            for (int iB = 2; iB < N - iD; iB++) {

                for (int iC = iB + 1; iC < N - iD + 1; iC++) {

                    pHolder.setP(1, iB, iC, iC + iD, pHolder.getP(1, iB, iC + 1, iC + iD)
                            + pHolder.getP(1, iB, iC, iC + iD - 1)
                            - pHolder.getP(1, iB, iC + 1, iC + iD - 1)
                            + pHolder.getQ(1, iB, iC, iC + iD));
                }
            }
        }

        for (int iA = 2; iA < N - 1; iA++) {

            for (int iB = iA + 1; iB < N; iB++) {

                for (int iC = iB + 1; iC <= N; iC++) {

                    if (iA + N > iC + 1) {

                        pHolder.setP(iA, iB, iC, iC + 1, pHolder.getQ(iA, iB, iC, iC + 1));
                    }
                }
            }
        }

        for (int iA = 2; iA < N - 1; iA++) {

            for (int iB = iA + 1; iB < N; iB++) {

                for (int iC = iB + 1; iC <= N; iC++) {

                    if (iA + N > iC + 2) {

                        if (iC != N) {

                            pHolder.setP(iA, iB, iC, iC + 2, pHolder.getP(iA, iB, iC + 1, iC + 2)
                                    + pHolder.getP(iA, iB, iC, iC + 1)
                                    + pHolder.getQ(iA, iB, iC, iC + 2));

                        } else {

                            pHolder.setP(iA, iB, iC, iC + 2, pHolder.getP(1, 2, iA, iB)
                                    + pHolder.getP(iA, iB, iC, iC + 1)
                                    + pHolder.getQ(iA, iB, iC, iC + 2));
                        }
                    }
                }
            }
        }

        for (int iD = 3; iD < N - 2; iD++) {

            for (int iA = 2; iA < N - 1; iA++) {

                for (int iB = iA + 1; iB < N; iB++) {

                    for (int iC = iB + 1; iC <= N; iC++) {

                        if (N + iA > iC + iD) {

                            if (iC != N) {

                                pHolder.setP(iA, iB, iC, iC + iD, pHolder.getP(iA, iB, iC + 1, iC + iD)
                                        + pHolder.getP(iA, iB, iC, iC + iD - 1)
                                        - pHolder.getP(iA, iB, iC + 1, iC + iD - 1)
                                        + pHolder.getQ(iA, iB, iC, iC + iD));

                            } else {

                                pHolder.setP(iA, iB, iC, iC + iD, pHolder.getP(1, iD, iA, iB)
                                        + pHolder.getP(iA, iB, iC, iC + iD - 1)
                                        - pHolder.getP(1, iD - 1, iA, iB)
                                        + pHolder.getQ(iA, iB, iC, iC + iD));

                            }
                        }
                    }
                }
            }
        }

        return pHolder;
    }
}

