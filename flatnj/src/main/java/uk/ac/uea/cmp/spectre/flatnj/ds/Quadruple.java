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

package uk.ac.uea.cmp.spectre.flatnj.ds;


/*This class implements methods that give
  us access to the split weights for every
  four element subset of the set of taxa.*/

public class Quadruple {
    //array containing the four taxa in increasing order
    private int[] taxa = null;

    //array containing the weights of all splits of the 4 taxa
    //The splits are:
    //0 --> 1|234
    //1 --> 2|134
    //2 --> 3|124
    //3 --> 4|123
    //4 --> 12|34
    //5 --> 13|24
    //6 --> 14|23
    private double[] weights = null;

    //number of this quadrupe in the QuadrupleSystem
    private int index;

    //constructor of this class
    public Quadruple(int[] inTaxa, double[] inWeights) {
        taxa = new int[4];
        weights = new double[7];
        System.arraycopy(inTaxa, 0, taxa, 0, inTaxa.length);
        System.arraycopy(inWeights, 0, weights, 0, inWeights.length);
    }

    public Quadruple(int[] inTaxa) {
        taxa = new int[4];
        System.arraycopy(inTaxa, 0, taxa, 0, inTaxa.length);
        weights = new double[7];
    }

//    public Quadruple(int[] inTaxa, char[][] inSequences, SplitsEstimator splitsEstimator)
//    {
//        taxa = new int[4];
//        System.arraycopy(inTaxa, 0, taxa, 0, inTaxa.length);
//        weights = splitsEstimator.estimate(inSequences);
//    }

    public double getSplitWeightFor2Vs2(int a, int b) {
        if (a > b) {
            int c = a;
            a = b;
            b = c;
        }
        int ia = getTaxa(a, 0);
        int ib = getTaxa(b, ia + 1);


        if (ia == 0) {
            return weights[3 + (ia + ib)];
        } else {
            return weights[9 - (ia + ib)];
        }
    }

    public double getSplitWeightFor1Vs3(int a) {
        int index = getTaxa(a, 0);
        return weights[index];
    }

    @Override
    public String toString() {
        String message = "Qadruple : ";
        for (int i = 0; i < taxa.length; i++) {
            message = message.concat(taxa[i] + " ");
        }
        message = message.concat(":");
        for (int i = 0; i < weights.length; i++) {
            message = message.concat(" " + weights[i]);
        }

        return message;
    }

    public int getTaxa(int x, int iStart) {
        for (int i = iStart; i < 4; i++) {
            if (taxa[i] == x) {
                return i;
            }
        }
        return 0;
    }

    public void setWeights(double[] inWeights) {
        System.arraycopy(inWeights, 0, weights, 0, weights.length);
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public int[] getTaxa() {
        return taxa;
    }

    public double[] getWeights() {
        return weights;
    }

    public void setTaxa(int[] taxa) {
        this.taxa = taxa;
    }


}
