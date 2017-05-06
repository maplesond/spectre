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

package uk.ac.uea.cmp.spectre.core.ds.split;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;

import java.util.*;

/**
 * Created by maplesod on 14/05/14.
 */
public class SpectreSplitBlock extends ArrayList<Integer> implements SplitBlock {

    public SpectreSplitBlock(Collection<Integer> splitBlock) {

        super();

        if (splitBlock == null || splitBlock.size() < 1)
            throw new IllegalArgumentException("SplitBlock must be of at least size 1");

        for (Integer i : splitBlock) {
            this.add(i.intValue());
        }
    }

    public SpectreSplitBlock(SpectreSplitBlock splitBlock) {

        super();

        if (splitBlock == null || splitBlock.size() < 1)
            throw new IllegalArgumentException("SplitBlock must be of at least size 1");

        for (Integer i : splitBlock) {
            this.add(i.intValue());
        }

    }

    public SpectreSplitBlock(int[] splitBlock) {

        super();

        if (splitBlock == null || splitBlock.length < 1)
            throw new IllegalArgumentException("SplitBlock must be of at least size 1");

        for (int i : splitBlock) {
            this.add(i);
        }
    }

    public SpectreSplitBlock(String s) {

        super();

        String[] parts = s.split(" ");


        int[] raw = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            raw[i] = Integer.parseInt(parts[i]);
        }
        if (parts.length == 1) {
            this.add(raw[0]);
        }

        if (parts.length > 1) {
            int overlap = -1;
            for (int i = 1; i < parts.length; i++) {
                if (raw[i] != raw[i - 1] + 1) {
                    overlap = i;
                    break;
                }
            }

            if (overlap != -1) {
                for (int i = overlap; i < raw.length; i++) {
                    this.add(raw[i]);
                }
                for (int i = 0; i < overlap; i++) {
                    this.add(raw[i]);
                }
            } else {
                for (int i = 0; i < raw.length; i++) {
                    this.add(raw[i]);
                }
            }
        }
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder();

        for (Integer i : this) {
            hcb.append(i.intValue());
        }

        return hcb.toHashCode();
    }

    @Override
    public boolean equals(Object o) {

        if (o == null)
            return false;

        if (this == o)
            return true;

        SplitBlock other = (SplitBlock) o;

        if (this.size() != other.size())
            return false;

        EqualsBuilder eb = new EqualsBuilder();

        for (int i = 0; i < this.size(); i++) {
            eb.append(this.get(i).intValue(), other.get(i).intValue());
        }

        return eb.isEquals();
    }

    @Override
    public int compareTo(SplitBlock o) {

        if (this.size() == 0 && o.size() == 0)
            return 0;
        else if (this.size() < o.size())
            return -1;
        else if (this.size() > o.size())
            return 1;
        else {
            for(int i = 0; i < this.size(); i++) {
                int diff = this.get(i) - o.get(i);
                if (diff != 0) {
                    return diff;
                }
            }
            return 0;
        }

    }

    @Override
    public int[] toIntArray() {

        int[] arr = new int[this.size()];

        for (int i = 0; i < this.size(); i++) {
            arr[i] = this.get(i).intValue();
        }

        return arr;
    }

    @Override
    public SplitBlock makeComplement(int nbTaxa) {
        return this.makeComplement(nbTaxa, false);
    }

    @Override
    public SplitBlock makeComplement(int nbTaxa, boolean zerobased) {
        ArrayList<Integer> complement = new ArrayList<>();
        for (int i = zerobased ? 0 : 1; i < (zerobased ? nbTaxa : nbTaxa + 1); i++) {
            if (this.contains(i) == false) {
                complement.add(i);
            }
        }

        return new SpectreSplitBlock(complement);
    }

    @Override
    public int getFirst() {
        return this.get(0);
    }

    @Override
    public int getLast() {
        return this.get(this.size() - 1);
    }

    /**
     * Copies elements from the provided split block and adds them to this split block
     *
     * @param splitBlock Splitblock to merge with this
     */
    @Override
    public void merge(SplitBlock splitBlock) {

        for (Integer i : splitBlock) {
            this.add(i.intValue());
        }
    }

    @Override
    public SplitBlock copy() {
        List<Integer> copy = new ArrayList<>();

        for (Integer i : this) {
            copy.add(i.intValue());
        }

        return new SpectreSplitBlock(copy);
    }

    @Override
    public void sort() {
        Collections.sort(this);
    }

    @Override
    public SplitBlock makeSortedCopy() {

        SplitBlock copy = this.copy();
        copy.sort();
        return copy;
    }

    @Override
    public void reverse() {
        Collections.reverse(this);
    }

    public SplitBlock makeReversedCopy() {

        SplitBlock copy = this.copy();
        copy.reverse();
        return copy;
    }

    @Override
    public EdgeType getType() {
        return this.size() == 1 ? EdgeType.EXTERNAL : EdgeType.INTERNAL;
    }


    @Override
    public boolean containsAny(SplitBlock other) {

        for(Integer i : other) {
            if (this.contains(i)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isContiguousWithOrdering(IdentifierList ordering) {

        Identifier first = ordering.getById(this.getFirst());

        if (first == null) {
            throw new IllegalStateException("Could not find first index in split block (" + this.getFirst() + "), in the circular ordering: " + ordering.toString());
        }

        // In order to make sure this works all the time we make sure that we can handle the situation where this block
        // is also stored in sorted form (not just in cycle ordering).  So we create a set of this split block, then search
        // either way from the location of that id in the ordering until we find all the ids (in which case we are contiguous),
        // or we don't in which case we are not.
        Set<Integer> ids = new HashSet<>(this);

        int index = ordering.indexOf(first);

        if (this.size() == 1 && index != -1) {
            return true;
        }

        int upidx = index - 1;
        int downidx = index + 1;
        for(int i = 0; i < this.size() - 1; i++) {
            if (upidx < 0) {
                upidx = ordering.size() - 1;
            }
            if (downidx >= ordering.size()) {
                downidx = 0;
            }

            if (ids.contains(ordering.get(upidx).getId())) {
                upidx--;
            }
            else if (ids.contains(ordering.get(downidx).getId())) {
                downidx++;
            }
            else {
                // Couldn't find id in split block
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        boolean first = true;
        for(Integer i : this) {

            if (!first) {
                sb.append(" ");
            }

            sb.append(i.toString());
            first = false;
        }

        return sb.toString();
    }
}
