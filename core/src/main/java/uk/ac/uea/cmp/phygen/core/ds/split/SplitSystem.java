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

import uk.ac.uea.cmp.phygen.core.ds.SummedDistanceList;
import uk.ac.uea.cmp.phygen.core.ds.distance.DistanceMatrix;

import java.util.ArrayList;
import java.util.List;

public class SplitSystem {

    private List<Split> splits;
    private String[] taxa;

    public SplitSystem(int nbTaxa) {
        this(nbTaxa, new ArrayList<Split>());
    }

    public SplitSystem(int nbTaxa, List<Split> splits) {

        this(createGenericTaxa(nbTaxa), splits);
    }

    public SplitSystem(String[] taxa, List<Split> splits) {

        this.taxa = taxa;
        this.splits = splits;
    }

    protected static String[] createGenericTaxa(int nbTaxa) {

        String[] taxa = new String[nbTaxa];

        // Assumes we don't have more than 26 taxa, otherwise this is going to get weird.
        for (int i = 0; i < nbTaxa; i++) {
            taxa[i] = Character.toString((char) (i + 65));
        }

        return taxa;
    }

    public List<Split> getSplits() {
        return splits;
    }

    protected void setSplits(List<Split> splits) {
        this.splits = splits;
    }

    public String[] getTaxa() {
        return this.taxa;
    }

    protected void setTaxa(String[] taxa) {
        this.taxa = taxa;
    }

    public int getNbTaxa() {
        return this.taxa.length;
    }

    public String getTaxaAt(int i) {
        return this.taxa[i];
    }

    /**
     * This returns the index of the taxa in a 1-based system
     *
     * @param i
     * @return
     */
    public int getTaxaIndexAt(int i) {
        return i + 1;
    }


    public int[] invertOrdering() {

        int nbTaxa = this.getNbTaxa();
        int[] permutationInvert = new int[nbTaxa];

        for (int i = 0; i < nbTaxa; i++) {
            permutationInvert[i] = i;
        }

        return permutationInvert;
    }


    /**
     * Calculate the summed distanceMatrix between elements on the A side and B side for every split
     *
     * @return Array of P
     */
    public SummedDistanceList calculateP(DistanceMatrix distanceMatrix) {

        if (this.getNbTaxa() != distanceMatrix.size())
            throw new IllegalArgumentException("The number of taxa in DistanceMatrix is not that same as in this splis system");

        double P[] = new double[this.splits.size()];

        // For each split, determine how many elements are on each side of the split
        for (int i = 0; i < this.splits.size(); i++) {
            P[i] = this.splits.get(i).calculateP(distanceMatrix);
        }

        return new SummedDistanceList(P);
    }

    public void addSplit(Split split) {
        this.splits.add(split);
    }

    public Split getSplitAt(int index) {
        return this.splits.get(index);
    }

    public int getLastSplitIndex() {
        return this.splits.size() - 1;
    }

    public Split getLastSplit() {
        return this.getSplitAt(this.getLastSplitIndex());
    }

    public int getNbSplits() {
        return this.splits.size();
    }

    public void removeSplit(int index) {
        this.splits.remove(index);
    }

    /**
     * Appends a split at a specified position in this split system
     * to another row.
     *
     * @param index1 the index of the split that is to be expanded
     * @param index2 the index of the split that is to be removed
     * @throws IndexOutOfBoundsException if a row is out of range
     */
    public void mergeSplits(int index1, int index2) {
        Split split1 = this.getSplitAt(index1);
        Split split2 = this.getSplitAt(index2);

        split1.merge(split2);

        this.splits.remove(index2);
    }

    public List<Split> copySplits() {

        List<Split> ss = new ArrayList<>();

        for (Split s : this.splits) {
            ss.add(s.copy());
        }

        return ss;
    }





    /*public void addTrivialSplits() {
        for (String taxa : names) {
            splits.add(taxa);
            weightings.add(1.0);
            StringBuilder complement = new StringBuilder();
            for (String taxa2 : names) {
                if (!taxa2.equals(taxa)) {
                    complement.append(taxa2).append(" ");
                }
            }
            splits.add(complement.toString().trim());
            weightings.add(1.0);
        }
    }

    public void removeTrivialSplits() {
        for (Split split : splits) {
            //?????
        }
    }  */


    /*public void convertToIndicies() {
        for (int i = 0; i < splits.size(); i++) {
            String split = splits.get(i);

            StringBuilder sb = new StringBuilder();

            String[] taxon = split.split(" ");

            for (String taxa : taxon) {
                int idx = names.indexOf(taxa);

                if (idx == -1) {
                    throw new IllegalStateException("This shouldn't have happened!");
                }

                sb.append(Integer.toString(idx + 1)).append(" ");
            }

            splits.set(i, sb.toString().trim());
        }
    }*/


}
