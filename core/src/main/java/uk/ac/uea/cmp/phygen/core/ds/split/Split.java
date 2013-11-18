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
package uk.ac.uea.cmp.phygen.core.ds.split;

import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;

/**
 * Created with IntelliJ IDEA.
 * User: Sarah_2
 * Date: 24/04/13
 * Time: 16:34
 * To change this template use File | Settings | File Templates.
 */
public class Split {

    private SplitBlock aSide;
    private SplitBlock bSide;
    private int nbTaxa;
    private double weight;

    public Split(SplitBlock aSide, int nbTaxa) {
        this(aSide, nbTaxa, 1.0);
    }

    public Split(SplitBlock aSide, int nbTaxa, double weight) {
        this.aSide = aSide;
        this.nbTaxa = nbTaxa;
        this.bSide = aSide.makeComplement(nbTaxa);
        this.weight = weight;
    }


    public Split(Split split) {
        this(split.aSide.copy(), split.nbTaxa);
    }

    public SplitBlock getASide() {
        return aSide;
    }

    public SplitBlock getBSide() {
        return bSide;
    }

    public int getNbTaxa() {
        return nbTaxa;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void sort() {

        this.aSide.sort();
        this.bSide.sort();
    }

    public int getSplitElement(SplitSide side, int index) {
        return side.getSplitElement(this, index);
    }

    public void merge(Split split) {
        this.aSide.merge(split.getASide());
        this.bSide = aSide.makeComplement(this.nbTaxa);
    }

    public Split copy() {

        return new Split(this.getASide().copy(), this.nbTaxa);
    }

    public enum SplitSide {

        A_SIDE {
            public int getSplitElement(Split split, int index) {
                return split.aSide.get(index);
            }
        },
        B_SIDE {
            public int getSplitElement(Split split, int index) {
                return split.bSide.get(index);
            }
        };

        public abstract int getSplitElement(Split split, int index);
    }

    /**
     * Summed up distanceMatrix from all elements on the A side to all elements on the B side.
     *
     * @return P
     */
    public double calculateP(DistanceMatrix distanceMatrix) {

        boolean splited[] = new boolean[this.nbTaxa];
        for (int h = 0; h < splited.length; h++) {
            splited[h] = false;
        }

        // Array stores the info which elements are on one side each element of the split
        for (int j = 0; j < this.aSide.size(); j++) {
            for (int k = 0; k < this.nbTaxa; k++) {
                if (this.aSide.get(j) == k) {
                    splited[k] = true;
                }
            }
        }

        // Sums up all distanceMatrix from the elements on the one side of the edge to the other side
        double p = 0.0;

        for (int j = 0; j < this.aSide.size(); j++) {
            for (int k = 0; k < this.nbTaxa; k++) {
                if (splited[k] == false) {
                    p += distanceMatrix.getDistance(this.aSide.get(j), k);
                }
            }
        }

        return p;
    }

    public boolean onExternalEdge() {
        return this.aSide.size() == 1 || this.bSide.size() == 1;
    }

}
