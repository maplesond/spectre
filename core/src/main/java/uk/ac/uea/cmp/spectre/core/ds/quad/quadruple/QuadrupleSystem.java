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

package uk.ac.uea.cmp.spectre.core.ds.quad.quadruple;

import org.apache.commons.lang3.ArrayUtils;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.split.SplitSystem;
import uk.ac.uea.cmp.spectre.core.ds.split.flat.FlatSplitSystem;
import uk.ac.uea.cmp.spectre.core.util.CollectionUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

/**
 * This class implements methods to handle a quadruple system
 */
public class QuadrupleSystem implements Cloneable {

    // Complete list of named taxa
    private IdentifierList taxa;

    // The taxa that are active
    private boolean[] active;

    // Number of active taxa
    private int nbActiveTaxa;

    // Number of active quadruples
    private int nQuadruples = 0;

    //array containing the quadruples in the system
    //The order of the quadruples is lexicographic
    //on the four taxa inolved.
    private Quadruple[] quadruples = null;

    private static int lastAdded;

    private double[] minimalTrivialWeights;

    public QuadrupleSystem(final int nbTaxa) {
        this(new IdentifierList(nbTaxa));
    }

    public QuadrupleSystem(IdentifierList originalTaxa) {
        this(originalTaxa, originalTaxa.size(), new boolean[originalTaxa.size()]);
    }

    public QuadrupleSystem(IdentifierList originalTaxa, final int nbActiveTaxa, final boolean[] active) {
        this.nbActiveTaxa = nbActiveTaxa;
        this.taxa = originalTaxa;
        nQuadruples = calculateNbQuadruples(nbActiveTaxa);
        quadruples = new Quadruple[nQuadruples];
        lastAdded = 0;
        this.active = ArrayUtils.clone(active);

        // Assume we want all taxa active to start with
        if (nbActiveTaxa == originalTaxa.size()) {
            for (int i = 0; i < nbActiveTaxa; i++) {
                this.active[i] = true;
            }
        }
    }

    public IdentifierList getTaxa() {
        return taxa;
    }

    public static int calculateNbQuadruples(int nbActiveTaxa) {
        return (nbActiveTaxa * (nbActiveTaxa - 1) * (nbActiveTaxa - 2) * (nbActiveTaxa - 3)) / (2 * 3 * 4);
    }

    public void add(Quadruple quadruple) {
        quadruples[lastAdded] = quadruple;
        quadruple.setIndex(lastAdded);
        lastAdded++;
    }

    /**
     * This method checks whether or not for given active are distinct. The assumption is that a <= b <= c <= d holds.
     * @param a taxon id a
     * @param b taxon id b
     * @param c taxon id c
     * @param d taxon id d
     * @return True if all ids are distinct, otherwise false
     */
    public boolean areDistinct(int a, int b, int c, int d) {
        return !(((a == b) || (b == c)) || (c == d));
    }

    /**
     * This method computes the index of the quartet defined by {a,b,c,d} in the quadruples array and returns the
     * corresponding Quartet object. The computation of this index is based on the assumption that the quartet set is
     * 0,1,...,nTaxa-1 and quadruples are stored in lexicographic order.
     * @param a taxon id a
     * @param b taxon id b
     * @param c taxon id c
     * @param d taxon id d
     * @return Quadruple defined by taxa ids
     */
    public Quadruple getQuadruple(int a, int b, int c, int d) {
        int i = 0;
        int nTaxa = active.length;
        i = i + ((nTaxa * (nTaxa - 1) * (nTaxa - 2) * (nTaxa - 3)) / (2 * 3 * 4));
        i = i - (((nTaxa - a) * (nTaxa - a - 1) * (nTaxa - a - 2) * (nTaxa - a - 3)) / (2 * 3 * 4));
        i = i + (((nTaxa - a - 1) * (nTaxa - a - 2) * (nTaxa - a - 3)) / (2 * 3));
        i = i - (((nTaxa - b) * (nTaxa - b - 1) * (nTaxa - b - 2)) / (2 * 3));
        i = i + (((nTaxa - b - 1) * (nTaxa - b - 2)) / 2);
        i = i - (((nTaxa - c) * (nTaxa - c - 1)) / 2);
        i = i + d - c - 1;

        if (i < 0) {
            return null;
        }
        return quadruples[i];
    }

    //This method computes the total length of those quartet splits
    //that are also in the restriction of the given split system.
    public double getFitRestriction(int a, int b, int c, int d, SplitSystem ss) {
        double fit = 0.0;

        Quadruple q = getQuadrupleUnsorted(a, b, c, d);

        if (q != null) {
            int[] taxa = q.getTaxa().toIntArray();
            double[] weights = q.getWeights();
            for (int i = 0; i < 7; i++) {
                if (ss.restrictionExists(taxa[0], taxa[1], taxa[2], taxa[3], i)) {
                    fit += weights[i];
                }
            }
        }

        return fit;
    }

    //Returns length for the quadruple split a|bcd. a does not have to be
    //smaller than b/c/d
    public double get1Vs3Weight(int a, int b, int c, int d) {
        Quadruple q = getQuadrupleUnsorted(a, b, c, d);

        if (q != null) {
            return q.getSplitWeightFor1Vs3(a);
        }

        return 0;
    }

    //This method returns the length of the quadruple split ab|cd
    //for the specified quadruple. We know a < b.
    public double get2Vs2Weight(int a, int b, int c, int d) {
        Quadruple q = getQuadrupleUnsorted(a, b, c, d);

        if (q != null) {
            return q.getSplitWeightFor2Vs2(a, b);
        }

        return 0;
    }

    public Quadruple getQuadruple(int[] array) {
        return getQuadruple(array[0], array[1], array[2], array[3]);
    }

    public Quadruple getQuadrupleUnsorted(int a, int b, int c, int d) {
        int[] sortedTaxa = new int[4];

        sortedTaxa[0] = a;
        sortedTaxa[1] = b;
        sortedTaxa[2] = c;
        sortedTaxa[3] = d;

        Arrays.sort(sortedTaxa);

        Quadruple q = null;
        if (areDistinct(sortedTaxa[0], sortedTaxa[1], sortedTaxa[2], sortedTaxa[3])) {
            q = getQuadruple(sortedTaxa[0], sortedTaxa[1], sortedTaxa[2], sortedTaxa[3]);
        }

        return q;
    }

    public boolean[] getActive() {
        return active;
    }

    public int getNbActiveTaxa() {
        return this.nbActiveTaxa;
    }

    public int getnQuadruples() {
        return nQuadruples;
    }

    //return number of active active
    public int[] getTaxaInt() {
        return CollectionUtils.getTrueElements(active);
    }

    //inactivates active b
    public void setInactive(int b) {
        active[b] = false;
        nbActiveTaxa--;
        nQuadruples = (nbActiveTaxa * (nbActiveTaxa - 1) * (nbActiveTaxa - 2) * (nbActiveTaxa - 3)) / (2 * 3 * 4);
    }

    //activates active b
    public void setActive(int b) {
        active[b] = true;
        nbActiveTaxa++;
        nQuadruples = (nbActiveTaxa * (nbActiveTaxa - 1) * (nbActiveTaxa - 2) * (nbActiveTaxa - 3)) / (2 * 3 * 4);
    }

    //creates identical qs to this one (except we lose track of which taxa are inactive)
    @Override
    protected QuadrupleSystem clone() {
        QuadrupleSystem qs = new QuadrupleSystem(this.taxa, this.nbActiveTaxa, this.active);
        qs.quadruples = new Quadruple[this.nQuadruples];
        for (int i = 0; i < this.quadruples.length; i++) {
            qs.quadruples[i] = new Quadruple(this.quadruples[i].getTaxa(), this.quadruples[i].getWeights());
            qs.quadruples[i].setIndex(this.quadruples[i].getIndex());
        }
        return qs;
    }

    //subtracts minimal weights for each taxon from all quadruples
    public void subtractMin() {
        minimalTrivialWeights = getMinimalTrivialForAllTaxa();
        int qn = 0;
        int[] i = new int[4];
        for (i[0] = 0; i[0] < nbActiveTaxa; i[0]++) {
            for (i[1] = i[0] + 1; i[1] < nbActiveTaxa; i[1]++) {
                for (i[2] = i[1] + 1; i[2] < nbActiveTaxa; i[2]++) {
                    for (i[3] = i[2] + 1; i[3] < nbActiveTaxa; i[3]++) {
                        double[] w = quadruples[qn].getWeights();
                        for (int j = 0; j < 4; j++) {
                            w[j] -= minimalTrivialWeights[i[j]];
                        }
                        quadruples[qn].setWeights(w);
                        qn++;
                    }
                }
            }
        }
    }

    /**
     * Computes minimal weights that should be subtracted from quadruple split
     * weights for each active. These weights may be bigger than 0 only for active
     * that are included at least in one quadruple with all quadruple split
     * weights positive (bigger than zero).
     * @return
     */
    private double[] getMinimalTrivialForAllTaxa() {
        double[] min = new double[nbActiveTaxa];
        for (int i = 0; i < min.length; i++) {
            min[i] = -1.0;
        }
        int qn = 0;
        int[] i = new int[4];
        boolean[] taxaToPreprocess = new boolean[nbActiveTaxa];
        for (int j = 0; j < taxaToPreprocess.length; j++) {
            taxaToPreprocess[j] = false;
        }

        for (i[0] = 0; i[0] < nbActiveTaxa; i[0]++) {
            for (i[1] = i[0] + 1; i[1] < nbActiveTaxa; i[1]++) {
                for (i[2] = i[1] + 1; i[2] < nbActiveTaxa; i[2]++) {
                    for (i[3] = i[2] + 1; i[3] < nbActiveTaxa; i[3]++) {
                        double[] w = quadruples[qn++].getWeights();
                        if (allPositive(w)) {
                            taxaToPreprocess[i[0]] = true;
                            taxaToPreprocess[i[1]] = true;
                            taxaToPreprocess[i[2]] = true;
                            taxaToPreprocess[i[3]] = true;
                        }
                    }
                }
            }
        }

        qn = 0;
        for (i[0] = 0; i[0] < nbActiveTaxa; i[0]++) {
            for (i[1] = i[0] + 1; i[1] < nbActiveTaxa; i[1]++) {
                for (i[2] = i[1] + 1; i[2] < nbActiveTaxa; i[2]++) {
                    for (i[3] = i[2] + 1; i[3] < nbActiveTaxa; i[3]++) {
                        double[] w = quadruples[qn++].getWeights();

                        for (int j = 0; j < 4; j++) {
                            if (taxaToPreprocess[i[j]] && (min[i[j]] == -1 || min[i[j]] > w[j])) {
                                min[i[j]] = w[j];
                            }
                        }
                    }
                }
            }
        }

        for (int j = 0; j < taxaToPreprocess.length; j++) {
            if (!taxaToPreprocess[j]) {
                min[j] = 0;
            }
        }
        return min;
    }

    public double[] getTrivial() {
        return minimalTrivialWeights;
    }

    //checks if all numbers in the array have positive weights
    private boolean allPositive(double[] w) {
        for (int i = 0; i < w.length; i++) {
            if (w[i] <= 0) {
                return false;
            }
        }
        return true;
    }

    //computes quadruple split length matrix times its transpose
    public double computeWxWT() {
        double wwT = 0;
        for (int i = 0; i < quadruples.length; i++) {
            double[] w = quadruples[i].getWeights();
            for (int j = 0; j < w.length; j++) {
                wwT += w[j] * w[j];
            }
        }
        return wwT;
    }

    public Quadruple[] getQuadruples() {
        return quadruples;
    }

}