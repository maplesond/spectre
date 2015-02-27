/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2015  UEA School of Computing Sciences
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

package uk.ac.uea.cmp.spectre.flatnj.ds;

import uk.ac.uea.cmp.spectre.flatnj.tools.Utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

/**
 * This class implements methods to handle a quadruple system
 */
public class QuadrupleSystem implements Cloneable {
    //number of taxa
    private int nTaxa = 0;
    private boolean[] taxa;

    //The taxa are 0,1,...,nTaxa-1.

    //number of quadruples
    private int nQuadruples = 0;

    //array containing the quadruples in the system
    //The order of the quadruples is lexicographic
    //on the four taxa inolved.
    private Quadruple[] quadruples = null;

    private static int lastAded;

    private double[] minimalTrivialWeights;

    //constructor of this class from a number of taxa
    public QuadrupleSystem(int inNTaxa) {
        nTaxa = inNTaxa;
        nQuadruples = (nTaxa * (nTaxa - 1) * (nTaxa - 2) * (nTaxa - 3)) / (2 * 3 * 4);
        quadruples = new Quadruple[nQuadruples];
        lastAded = 0;
        taxa = new boolean[nTaxa];
        for (int i = 0; i < nTaxa; i++) {
            taxa[i] = true;
        }
    }

    public void add(Quadruple quadruple) {
        quadruples[lastAded] = quadruple;
        quadruple.setIndex(lastAded);
        lastAded++;
//        if(lastAded == quadruples.length)
//        {
//            subtractMin();
//        }
    }

    //This method computes the total length of the quadruples in the system.
    public double computeTotalWeight() {
        double total = 0.0;

        for (int i = 0; i < nQuadruples; i++) {
            double[] weights = quadruples[i].getWeights();
            for (int j = 0; j < 7; j++) {
                total = total + weights[j];
            }
        }
        return total;
    }

    //This method checks whether or not for given taxa are
    //distinct. The assumption is that a <= b <= c <= d holds.
    public boolean areDistinct(int a, int b, int c, int d) {
        if (((a == b) || (b == c)) || (c == d)) {
            return false;
        } else {
            return true;
        }
    }

    //This method computes the index of the quartet
    //defined by {a,b,c,d} in the quadruples array and
    //returns the corresponding Quartet object.
    //The computation of this index is based on the
    //assumption that the quartet set is 0,1,...,nTaxa-1
    //and quadruples are stored in lexicographic order.
    public Quadruple getQuadruple(int a, int b, int c, int d) {
        int i = 0;
        int nTaxa = taxa.length;
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
            int[] taxa = q.getTaxa();
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

    //This method returns the length array of the quartet formed
    //by the four taxa.
    public double[] getWeightArray(int a, int b, int c, int d) {
        Quadruple q = getQuadrupleUnsorted(a, b, c, d);
        return q.getWeights();
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

    //makes all quadruple split length that are bigger than certain treshol
    //equal to 1 and all that are not bigger -- 0.
    public void unifyAll() {
        double treshold = 0.0;
        for (int i = 0; i < quadruples.length; i++) {
            double[] weights = quadruples[i].getWeights();
            for (int j = 0; j < 7; j++) {
                weights[j] = (weights[j] > treshold) ? 1 : 0;
            }
            quadruples[i].setWeights(weights);
        }
    }

    public boolean[] getTaxa() {
        return taxa;
    }

    public int getnTaxa() {
        return nTaxa;
    }

    public int getnQuadruples() {
        return nQuadruples;
    }

    //returns i-th quadruple if all taxa in it are curently active
    public Quadruple getQuadruple(int i) {
        int[] taxaQ = quadruples[i].getTaxa();
        if (taxa[taxaQ[0]] && taxa[taxaQ[1]] && taxa[taxaQ[2]] && taxa[taxaQ[3]]) {
            return quadruples[i];
        }
        return null;
    }

    //return number of active taxa
    public int[] getTaxaInt() {
        return Utilities.getElements(taxa);
    }

    //inactivates taxa b
    public void setInactive(int b) {
        taxa[b] = false;
        nTaxa--;
        nQuadruples = (nTaxa * (nTaxa - 1) * (nTaxa - 2) * (nTaxa - 3)) / (2 * 3 * 4);
    }

    //activates taxa b
    public void setActive(int b) {
        taxa[b] = true;
        nTaxa++;
        nQuadruples = (nTaxa * (nTaxa - 1) * (nTaxa - 2) * (nTaxa - 3)) / (2 * 3 * 4);
    }

    //prints difference of the quadruple split weights between this qs and qs2
    public void printDifference(QuadrupleSystem qs2) {
        int[] aTaxa = Utilities.getElements(taxa);
        for (int i1 = 0; i1 < aTaxa.length; i1++) {
            for (int i2 = i1 + 1; i2 < aTaxa.length; i2++) {
                for (int i3 = i2 + 1; i3 < aTaxa.length; i3++) {
                    for (int i4 = i3 + 1; i4 < aTaxa.length; i4++) {
                        Quadruple q = getQuadruple(aTaxa[i1], aTaxa[i2], aTaxa[i3], aTaxa[i4]);
                        Quadruple q2 = qs2.getQuadruple(aTaxa[i1], aTaxa[i2], aTaxa[i3], aTaxa[i4]);
                        System.out.print("Quadruple nr. " + q.getIndex() + " : ");
                        System.out.print(aTaxa[i1] + " " + aTaxa[i2] + " " + aTaxa[i3] + " " + aTaxa[i4] + " : ");
                        double[] weights = q.getWeights();
                        double[] weights2 = q2.getWeights();
                        for (int j = 0; j < 7; j++) {
                            System.out.print((weights[j] - weights2[j]) + " ");
                        }
                        System.out.println(" *");
                    }
                }
            }
        }
    }

    //creates identical qs to this one
    @Override
    protected QuadrupleSystem clone() {
        QuadrupleSystem qs = new QuadrupleSystem(nTaxa);
        qs.quadruples = new Quadruple[this.nQuadruples];
        for (int i = 0; i < this.quadruples.length; i++) {
            qs.quadruples[i] = new Quadruple(this.quadruples[i].getTaxa(), this.quadruples[i].getWeights());
            qs.quadruples[i].setIndex(this.quadruples[i].getIndex());
        }
        return qs;
    }

    //Prints quatet information of the current qs to the file. Used to crate 
    //input for qnet.
    public void printQuartets(String filePath, String[] names) throws IOException {
        Writer writer = new FileWriter(new File(filePath));

        writer.write("taxanumber:  " + nTaxa + ";\n");
        writer.write("description: generated by FNet;\n");
        writer.write("sense: max;\n");

        if (names == null) {
            for (int i = 0; i < taxa.length; i++) {
                if (taxa[i]) {
                    String taxNr = getTaxaWith0(i);
                    writer.write("taxon:   " + taxNr + "   name: taxa" + i + ";\n");
                }
            }
        } else {
            for (int i = 0; i < taxa.length; i++) {
                if (taxa[i]) {
                    String taxNr = getTaxaWith0(i);
                    writer.write("taxon:   " + taxNr + "   name: " + names[i] + ";\n");
                }
            }
        }

        for (int i = 0; i < nQuadruples; i++) {
            int[] t = quadruples[i].getTaxa();
            if (taxa[t[0]] && taxa[t[1]] && taxa[t[2]] && taxa[t[3]]) {
                writer.write("quartet: ");
                for (int j = 0; j < t.length; j++) {
                    writer.write(getTaxaWith0(t[j]) + " ");
                }
                writer.write("weights:");
                double[] weights = quadruples[i].getWeights();
                for (int j = 4; j < 7; j++) {
                    writer.write(" " + weights[j]);
                }
                writer.write(";\n");
            }
        }
        writer.close();
    }

    //prints quadruple information
    public void printQuadruples() {
        System.out.println("Number of taxa: " + nTaxa + " *");
        System.out.println("Number of quadruplets: " + nQuadruples + " *");

        int[] aTaxa = Utilities.getElements(taxa);
        for (int i1 = 0; i1 < aTaxa.length; i1++) {
            for (int i2 = i1 + 1; i2 < aTaxa.length; i2++) {
                for (int i3 = i2 + 1; i3 < aTaxa.length; i3++) {
                    for (int i4 = i3 + 1; i4 < aTaxa.length; i4++) {
                        Quadruple q = getQuadruple(aTaxa[i1], aTaxa[i2], aTaxa[i3], aTaxa[i4]);
                        System.out.print("Quadruple nr. " + q.getIndex() + " : ");
                        System.out.print(aTaxa[i1] + " " + aTaxa[i2] + " " + aTaxa[i3] + " " + aTaxa[i4] + " : ");
                        double[] weights = q.getWeights();
                        for (int j = 0; j < 7; j++) {
                            System.out.print(weights[j] + " ");
                        }
                        System.out.println(" *");
                    }
                }
            }
        }
    }

    //forms taxa number with 0s in front ot it. Used for printing quartets.
    private String getTaxaWith0(int i) {
        String taxNr = Integer.toString(i + 1);
        if (i < 9) {
            taxNr = "00" + Integer.toString(i + 1);
        } else if (i < 99) {
            taxNr = "0" + Integer.toString(i + 1);
        }
        return taxNr;
    }

    //prints difference between this qs and qs2. tf is the name of the file
    //to be printed in front of the difference.
    public double compare(QuadrupleSystem qs2, String tf) {
        double diff = 0;
        for (int i = 0; i < quadruples.length; i++) {
            double[] w1 = quadruples[i].getWeights();
            double[] w2 = qs2.getQuadruple(quadruples[i].getTaxa()).getWeights();
            for (int j = 0; j < w1.length; j++) {
                diff += (w1[j] - w2[j]) * (w1[j] - w2[j]);
            }
        }
        diff = Math.sqrt(diff);
        System.err.println(tf + "\t" + diff);
        return diff;
    }


    //subtracts minimal weights for each taxon from all quadruples
    public void subtractMin() {
        minimalTrivialWeights = getMinimalTrivialForAllTaxa();
        int qn = 0;
        int[] i = new int[4];
        for (i[0] = 0; i[0] < nTaxa; i[0]++) {
            for (i[1] = i[0] + 1; i[1] < nTaxa; i[1]++) {
                for (i[2] = i[1] + 1; i[2] < nTaxa; i[2]++) {
                    for (i[3] = i[2] + 1; i[3] < nTaxa; i[3]++) {
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

    //computes minimal weights that should be subtracted from quadruple split
    //weights for each taxa. These weights may be bigger than 0 only for taxa 
    //that are included at least in one quadruple with all quadruple split
    //weights positive (bigger than zero).
    private double[] getMinimalTrivialForAllTaxa() {
        double[] min = new double[nTaxa];
        for (int i = 0; i < min.length; i++) {
            min[i] = -1.0;
        }
        int qn = 0;
        int[] i = new int[4];
        boolean[] taxaToPreprocess = new boolean[nTaxa];
        for (int j = 0; j < taxaToPreprocess.length; j++) {
            taxaToPreprocess[j] = false;
        }

        for (i[0] = 0; i[0] < nTaxa; i[0]++) {
            for (i[1] = i[0] + 1; i[1] < nTaxa; i[1]++) {
                for (i[2] = i[1] + 1; i[2] < nTaxa; i[2]++) {
                    for (i[3] = i[2] + 1; i[3] < nTaxa; i[3]++) {
                        double[] w = quadruples[qn++].getWeights();
                        if (allPositive(w)) {
                            taxaToPreprocess[i[0]] = true;
                            taxaToPreprocess[i[1]] = true;
                            taxaToPreprocess[i[2]] = true;
                            taxaToPreprocess[i[3]] = true;
//                            int minTaxa = i[0];
//                            double minScore = w[0];
//                            for (int j = 1; j < 4; j++)
//                            {
//                                if(minScore > w[j])
//                                {
//                                    minScore = w[j];
//                                    minTaxa = i[j];
//                                }
//                            }
//                            taxaToPreprocess[minTaxa] = true;
                        }
                    }
                }
            }
        }

        //System.err.println("Taxa to preprocess:");
        //System.err.println(Arrays.toString(taxaToPreprocess));

        qn = 0;
        for (i[0] = 0; i[0] < nTaxa; i[0]++) {
            for (i[1] = i[0] + 1; i[1] < nTaxa; i[1]++) {
                for (i[2] = i[1] + 1; i[2] < nTaxa; i[2]++) {
                    for (i[3] = i[2] + 1; i[3] < nTaxa; i[3]++) {
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

    //adds back minimal weights that were subtracted from the qs
    public void addMin() {
        int qn = 0;
        int[] i = new int[4];
        for (i[0] = 0; i[0] < nTaxa; i[0]++) {
            for (i[1] = i[0] + 1; i[1] < nTaxa; i[1]++) {
                for (i[2] = i[1] + 1; i[2] < nTaxa; i[2]++) {
                    for (i[3] = i[2] + 1; i[3] < nTaxa; i[3]++) {
                        double[] w = quadruples[qn].getWeights();
                        for (int j = 0; j < 4; j++) {
                            w[j] += minimalTrivialWeights[i[j]];
                        }
                        quadruples[qn].setWeights(w);
                        qn++;
                    }
                }
            }
        }
        for (int j = 0; j < minimalTrivialWeights.length; j++) {
            minimalTrivialWeights[j] = 0;
        }
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

    public void normalizeWeights() {
        double max = 0.0;
        double avg = 0.0;
        for (int i = 0; i < quadruples.length; i++) {
            Quadruple q = quadruples[i];
            double[] w = q.getWeights();
            double all = 0.0;
            for (int j = 0; j < w.length; j++) {
                max = (max < w[j]) ? w[j] : max;
                all += w[j];
            }
            all /= 7;
            avg += all;
        }
        avg /= quadruples.length;
        for (int i = 0; i < quadruples.length; i++) {
            Quadruple q = quadruples[i];
            double[] w = q.getWeights();
            for (int j = 0; j < w.length; j++) {
                //w[j] /= max;
                w[j] /= avg;
            }
            q.setWeights(w);
        }
    }

    public double quadraticWeight() {
        double qw = 0.0;
        for (int i = 0; i < quadruples.length; i++) {
            Quadruple q = quadruples[i];
            double[] w = q.getWeights();
            for (int j = 0; j < w.length; j++) {
                qw += (w[j] * w[j]);
            }
        }
        return qw;
    }

    public Quadruple[] getQuadruples() {
        return quadruples;
    }


}