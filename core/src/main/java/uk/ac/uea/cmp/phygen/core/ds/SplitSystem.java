/**
 * Super Q - Computing super networks from partial trees. Copyright (C) 2012 UEA
 * CMP Phylogenetics Group.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.uea.cmp.phygen.core.ds;

import uk.ac.uea.cmp.phygen.core.alg.CircularNNLS;

import java.util.ArrayList;
import java.util.List;

public class SplitSystem {

    private List<String> names;
    private List<Split> splits;
    private List<Double> weightings;
    private int[] circularOrdering;

    public SplitSystem(int[] circularOrdering) {
        
        this.circularOrdering = circularOrdering;
        this.splits = new ArrayList<>();
        this.names = new ArrayList<>();
        this.weightings = new ArrayList<>();
        
        for(int i = 0; i < circularOrdering.length - 1; i++) {
            for(int j = i; j < circularOrdering.length - 1; j++) {

                ArrayList<Integer> s = new ArrayList<>();
                for(int k = i; k <= j; k++) {
                    s.add(circularOrdering[k]);
                }

                this.splits.add(new Split(new SplitBlock(s), circularOrdering.length));
                this.weightings.add(new Double(1.0));
            }
        }
        
        for(int name : circularOrdering) {
            this.names.add(Integer.toString(name));
        }
    }

    public SplitSystem(List<Split> splits) {
        this(splits, null, null);
    }
    
    public SplitSystem(List<Split> splits, List<String> names, List<Double> weightings) {

        if (splits == null) {
            throw new NullPointerException("SplitBlock system must be created with non-null data.");
        }

        /*if (splits.size() != weightings.size()) {
            throw new IllegalArgumentException("Weightings must be same size as splits");
        } */

        this.names = names;
        this.splits = splits;
        this.weightings = weightings;
        this.circularOrdering = null;
        
        // Create circular ordering here
    }

    public List<Split> getSplits() {
        return splits;
    }

    public List<String> getNames() {
        return names;
    }

    public List<Double> getWeightings() {
        return weightings;
    }

    public void setCircularOrdering(int[] circularOrdering) {
        this.circularOrdering = circularOrdering;
    }

    public int[] getCircularOrdering() {
        return circularOrdering;
    }

    /**
     * Returns the value of the permutation at the specified position.
     *
     * @param i index of the value to be returned.
     * @return value of the permutation at i
     */
    public int getCircularOrderingAt(final int i) {
        return this.circularOrdering[i];
    }

    public int nbTaxa() {
        return this.names.size();
    }

    /**
     * Calculate the summed distances between elements on the A side and B side for every split
     * @return Array of P
     */
    public SummedDistanceList calculateP(Distances distances) {

        if (this.nbTaxa() != distances.size())
            throw new IllegalArgumentException("The number of taxa in Distances is not that same as in this splis system");

        double P[] = new double[this.splits.size()];

        // For each split, determine how many elements are on each side of the split
        for (int i = 0; i < this.splits.size(); i++) {
            P[i] = this.splits.get(i).calculateP(distances);
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

    /**
     * We store two entries for each split so the actual split count is the
     * collection size divided by 2.
     *
     * @return
     */
    /*public int nbSplits() {
        return this.splits.size() / 2;
    }*/

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
    }*/

    public void removeTrivialSplits() {
        for (Split split : splits) {
            //?????
        }
    }


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

    /**
     * Returns weightings for the edges of a specified tree.
     *
     * @return tree edge weightings
     */
    public double[][] calculateTreeWeighting(Distances distances) {

        int n = distances.size();
        double[][] treeWeights = new double[n][n];
        double[][] permutedDistances = new double[n][n];
        boolean[][] flag = new boolean[n][n];
        int[] permutationInvert = new int[n];

        for(int i = 0; i < n; i++) {
            permutationInvert[circularOrdering[i]] = i;
        }

        for (int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                flag[i][j] = false;
            }
        }

        for (int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                treeWeights[i][j] = 0.;
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                permutedDistances[i][j] = distances.getDistance(circularOrdering[i], circularOrdering[j]);
            }
        }

        for (int i = 0; i < this.getSplits().size(); i++) {

            SplitBlock sb = splits.get(i).getASide();

            int k = permutationInvert[sb.get(0)];
            int l = permutationInvert[sb.get(sb.size() - 1)];

            if (k == 0) {
                flag[n - 1][l] = true;
            } else {
                if ((l < n - 1) && (k > l)) {
                    flag[k - 1][l] = true;
                } else {
                    flag[l][k - 1] = true;
                }
            }
        }

        new CircularNNLS().tree_in_cycle_least_squares(permutedDistances, flag,
                n, treeWeights);

        return treeWeights;
    }
}
