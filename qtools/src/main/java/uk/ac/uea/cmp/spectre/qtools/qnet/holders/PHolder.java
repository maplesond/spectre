/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2017  UEA School of Computing Sciences
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package uk.ac.uea.cmp.spectre.qtools.qnet.holders;

import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.quad.Quad;
import uk.ac.uea.cmp.spectre.core.ds.quad.quartet.GroupedQuartetSystem;
import uk.ac.uea.cmp.spectre.core.ds.quad.quartet.QuartetUtils;
import uk.ac.uea.cmp.spectre.core.ds.quad.quartet.QuartetWeights;

import java.util.Arrays;
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
    private int nbTaxa;


    /**
     * We need structures, probably quartet structures as we deal with several quantities of data.  We calculate
     * these for disjoint quartets by the given formula and the loaded existence information quartet structure.
     * Basically, we need a data structure with a bool and four ints for each quadruple set... we first load this from
     * the information file, then we fill up the same structure from the provided loops and then access below to get the
     * informative interval combinatorics... it is basically defined like outer and inner interval of two types and
     * existence value for each quadruple.
     *
     * @param quartetSystem The quartet system
     * @param c             The circular ordering of taxa ids
     */
    public PHolder(GroupedQuartetSystem quartetSystem, IdentifierList c) {

        final int N = c.size();

        this.ensureCapacity(N);
        this.initialize();


        /*for(Quartet q : quartetSystem.sortedQuartets()) {
            this.setR(
                    q.getA(),
                    q.getB(),
                    q.getC(),
                    q.getD(),
                    quartetSystem..getQuartets().get(q).getA().intValue() == 1);
        }*/

        // load r values
        for (Map.Entry<Quad, QuartetWeights> entry : quartetSystem.getQuartets().entrySet()) {

            this.setR(
                    entry.getKey().getA(),
                    entry.getKey().getB(),
                    entry.getKey().getC(),
                    entry.getKey().getD(),
                    //entry.getValue().getA().intValue() == 1);
                    true);
        }

        // fill upp pHolder by provided loop

        for (int iA = 1; iA < N - 2; iA++) {

            for (int iB = iA + 1; iB < N - 1; iB++) {

                for (int iC = iB + 1; iC < N; iC++) {

                    for (int iD = iC + 1; iD <= N; iD++) {

                        // these are NOT all 1 <= iA < iB < iC < iD <= N

                        if (iB == iA + 1) {

                            if (this.getR(iA + 1, iB + 1, iC + 1, iD + 1, c)) {

                                this.setQ(iA, iB, iC, iD, 1);
                            } else {

                                this.setQ(iA, iB, iC, iD, 0);
                            }
                        }

                        if (iC == iB + 1) {

                            this.setQ(iB, iC, iD, iA + N,
                                    this.getR(iA + 1, iB + 1, iC + 1, iD + 1, c) ? 1 : 0);
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

                        if (this.getR(iA + 1, iA + 3, iB + 1, iC + 1, c)) {

                            this.setQ(iA, iA + 2, iB, iC, this.getQ(iA + 1, iA + 2, iB, iC)
                                    + this.getQ(iA, iA + 1, iB, iC) + 1);

                        } else {

                            this.setQ(iA, iA + 2, iB, iC, this.getQ(iA + 1, iA + 2, iB, iC)
                                    + this.getQ(iA, iA + 1, iB, iC));
                        }
                    }

                    if (iC > iB + 2) {

                        if (this.getR(iA + 1, iB + 1, iB + 3, iC + 1, c)) {

                            this.setQ(iB, iB + 2, iC, iA + N, this.getQ(iB + 1, iB + 2, iC, iA + N)
                                    + this.getQ(iB, iB + 1, iC, iA + N) + 1);
                        } else {

                            this.setQ(iB, iB + 2, iC, iA + N, this.getQ(iB + 1, iB + 2, iC, iA + N)
                                    + this.getQ(iB, iB + 1, iC, iA + N));
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

                            if (this.getR(iA + 1, iA + iD + 1, iB + 1, iC + 1, c)) {

                                this.setQ(iA, iA + iD, iB, iC, this.getQ(iA + 1, iA + iD, iB, iC)
                                        + this.getQ(iA, iA + iD - 1, iB, iC)
                                        - this.getQ(iA + 1, iA + iD - 1, iB, iC) + 1);
                            } else {

                                this.setQ(iA, iA + iD, iB, iC, this.getQ(iA + 1, iA + iD, iB, iC)
                                        + this.getQ(iA, iA + iD - 1, iB, iC)
                                        - this.getQ(iA + 1, iA + iD - 1, iB, iC));
                            }

                        }

                        if (iC > iB + iD) {

                            if (this.getR(iA + 1, iB + 1, iB + iD + 1, iC + 1, c)) {

                                this.setQ(iB, iB + iD, iC, iA + N, this.getQ(iB + 1, iB + iD, iC, iA + N)
                                        + this.getQ(iB, iB + iD - 1, iC, iA + N)
                                        - this.getQ(iB + 1, iB + iD - 1, iC, iA + N) + 1);
                            } else {

                                this.setQ(iB, iB + iD, iC, iA + N, this.getQ(iB + 1, iB + iD, iC, iA + N)
                                        + this.getQ(iB, iB + iD - 1, iC, iA + N)
                                        - this.getQ(iB + 1, iB + iD - 1, iC, iA + N));
                            }
                        }
                    }
                }
            }
        }

        for (int iB = 2; iB < N; iB++) {

            for (int iC = iB + 1; iC < N; iC++) {

                this.setP(1, iB, iC, iC + 1, this.getQ(1, iB, iC, iC + 1));
            }
        }

        for (int iB = 2; iB < N - 2; iB++) {

            for (int iC = iB + 1; iC < N - 1; iC++) {

                this.setP(1, iB, iC, iC + 2, this.getP(1, iB, iC + 1, iC + 2)
                        + this.getP(1, iB, iC, iC + 1)
                        + this.getQ(1, iB, iC, iC + 2));
            }
        }

        for (int iD = 3; iD < N - 2; iD++) {

            for (int iB = 2; iB < N - iD; iB++) {

                for (int iC = iB + 1; iC < N - iD + 1; iC++) {

                    this.setP(1, iB, iC, iC + iD, this.getP(1, iB, iC + 1, iC + iD)
                            + this.getP(1, iB, iC, iC + iD - 1)
                            - this.getP(1, iB, iC + 1, iC + iD - 1)
                            + this.getQ(1, iB, iC, iC + iD));
                }
            }
        }

        for (int iA = 2; iA < N - 1; iA++) {

            for (int iB = iA + 1; iB < N; iB++) {

                for (int iC = iB + 1; iC <= N; iC++) {

                    if (iA + N > iC + 1) {

                        this.setP(iA, iB, iC, iC + 1, this.getQ(iA, iB, iC, iC + 1));
                    }
                }
            }
        }

        for (int iA = 2; iA < N - 1; iA++) {

            for (int iB = iA + 1; iB < N; iB++) {

                for (int iC = iB + 1; iC <= N; iC++) {

                    if (iA + N > iC + 2) {

                        if (iC != N) {

                            this.setP(iA, iB, iC, iC + 2, this.getP(iA, iB, iC + 1, iC + 2)
                                    + this.getP(iA, iB, iC, iC + 1)
                                    + this.getQ(iA, iB, iC, iC + 2));

                        } else {

                            this.setP(iA, iB, iC, iC + 2, this.getP(1, 2, iA, iB)
                                    + this.getP(iA, iB, iC, iC + 1)
                                    + this.getQ(iA, iB, iC, iC + 2));
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

                                this.setP(iA, iB, iC, iC + iD, this.getP(iA, iB, iC + 1, iC + iD)
                                        + this.getP(iA, iB, iC, iC + iD - 1)
                                        - this.getP(iA, iB, iC + 1, iC + iD - 1)
                                        + this.getQ(iA, iB, iC, iC + iD));

                            } else {

                                this.setP(iA, iB, iC, iC + iD, this.getP(1, iD, iA, iB)
                                        + this.getP(iA, iB, iC, iC + iD - 1)
                                        - this.getP(1, iD - 1, iA, iB)
                                        + this.getQ(iA, iB, iC, iC + iD));
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * Ensure capacity.
     * @param N The number of taxa
     */
    public void ensureCapacity(int N) {

        data = new PHContent[QuartetUtils.over4(N)];
        nbTaxa = N;
    }

    public void initialize() {

        int N = data.length;

        for (int n = 0; n < N; n++) {

            PHContent d = new PHContent(0, 0);
            data[n] = d;
        }
    }

    public int getP(int a, int b, int c, int d) {

        // we assume size-order and one-upmanship
        // we check for d > N
        if (!(a < b && b < c && c < d)) {

            return 0;
        }

        if (d > nbTaxa) {

            // if requesting something after the last (remembering we have one-upmanship

            // we work with out of d - n, a, b, c

            return data[QuartetUtils.sumOvers(d - nbTaxa - 1, a - 1, b - 1, c - 1)].getOuterP();
        } else {

            // we work with in of a, b, c, d

            return data[QuartetUtils.sumOvers(a - 1, b - 1, c - 1, d - 1)].getInnerP();
        }
    }

    public int getQ(int a, int b, int c, int d) {

        // we assume size-order and one-upmanship
        // we check for d > N
        return d > nbTaxa ?
                data[QuartetUtils.sumOvers(d - nbTaxa - 1, a - 1, b - 1, c - 1)].getOuterQ() :
                data[QuartetUtils.sumOvers(a - 1, b - 1, c - 1, d - 1)].getInnerQ();
    }

    public boolean getR(int a, int b, int c, int d, IdentifierList cT) {

        if (d > cT.size()) {

            int oD = d;

            d = c;
            c = b;
            b = a;
            a = oD - cT.size();
        }

        // this is an ugly hack
        // we seek mapped a < b < c < d

        int[] qidx = new int[]{
                cT.get(a - 1).getId(),
                cT.get(b - 1).getId(),
                cT.get(c - 1).getId(),
                cT.get(d - 1).getId()
        };

        Arrays.sort(qidx);

        a = qidx[0];
        b = qidx[1];
        c = qidx[2];
        d = qidx[3];

        // now we have size-order and proper mapping

        // we assume size-order and one-upmanship
        // we check for d > N

        return d > nbTaxa ?
                data[QuartetUtils.sumOvers(d - nbTaxa - 1, a - 1, b - 1, c - 1)].getR() :
                data[QuartetUtils.sumOvers(a - 1, b - 1, c - 1, d - 1)].getR();
    }


    public void setP(int a, int b, int c, int d, int newW) {

        // we assume size-order and one-upmanship
        // we check for d > N
        if (d > nbTaxa) {

            // if requesting something after the last (remembering we have one-upmanship
            // we work with out of d - n, a, b, c
            data[QuartetUtils.sumOvers(d - nbTaxa - 1, a - 1, b - 1, c - 1)].setOuterP(newW);
        } else {
            // we work with in of a, b, c, d
            data[QuartetUtils.sumOvers(a - 1, b - 1, c - 1, d - 1)].setInnerP(newW);
        }
    }

    public void setQ(int a, int b, int c, int d, int newW) {

        // we assume size-order and one-upmanship
        // we check for d > N

        if (d > nbTaxa) {

            // if requesting something after the last (remembering we have one-upmanship
            // we work with out of d - n, a, b, c
            data[QuartetUtils.sumOvers(d - nbTaxa - 1, a - 1, b - 1, c - 1)].setOuterQ(newW);
        } else {

            // we work with in of a, b, c, d
            data[QuartetUtils.sumOvers(a - 1, b - 1, c - 1, d - 1)].setInnerQ(newW);
        }
    }

    public void setR(int a, int b, int c, int d, boolean newW) {

        // we assume size-order and one-upmanship
        // we check for d > N

        if (d > nbTaxa) {

            // if requesting something after the last (remembering we have one-upmanship
            // we work with out of d - n, a, b, c
            data[QuartetUtils.sumOvers(d - nbTaxa - 1, a - 1, b - 1, c - 1)].setR(newW);
        } else {

            // we work with in of a, b, c, d
            data[QuartetUtils.sumOvers(a - 1, b - 1, c - 1, d - 1)].setR(newW);
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


}

