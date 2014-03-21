/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2014  UEA School of Computing Sciences
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
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
package uk.ac.uea.cmp.spectre.core.ds.split;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Sarah
 */
public class SplitBlock extends ArrayList<Integer> implements Comparable<SplitBlock> {

    public SplitBlock(Collection<Integer> splitBlock) {

        super();

        if (splitBlock == null || splitBlock.size() < 1)
            throw new IllegalArgumentException("SplitBlock must be of at least size 1");

        for (Integer i : splitBlock) {
            this.add(i.intValue());
        }
    }

    public SplitBlock(SplitBlock splitBlock) {

        super();

        if (splitBlock == null || splitBlock.size() < 1)
            throw new IllegalArgumentException("SplitBlock must be of at least size 1");

        for (Integer i : splitBlock) {
            this.add(i.intValue());
        }

    }

    public SplitBlock(int[] splitBlock) {

        super();

        if (splitBlock == null || splitBlock.length < 1)
            throw new IllegalArgumentException("SplitBlock must be of at least size 1");

        for (int i : splitBlock) {
            this.add(i);
        }
    }

    public SplitBlock(String s) {

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
    public String toString() {
        return StringUtils.join(this, " ");
    }

    @Override
    public int compareTo(SplitBlock o) {

        if (this.size() == 0 && o.size() == 0)
            return 0;
        else if (this.size() == 0 && o.size() > 0)
            return -1;
        else if (this.size() > 0 && o.size() == 0)
            return 1;
        else {
            int diff = this.getFirst() - o.getFirst();

            return diff == 0 ? this.size() - o.size() : diff;
        }
    }

    public int[] toIntArray() {

        int[] arr = new int[this.size()];

        for (int i = 0; i < this.size(); i++) {
            arr[i] = this.get(i);
        }

        return arr;
    }


    public SplitBlock makeComplement(int nbTaxa) {
        ArrayList<Integer> complement = new ArrayList<>();
        for (int i = 1; i <= nbTaxa; i++) {
            if (this.contains(i) == false) {
                complement.add(i);
            }
        }

        return new SplitBlock(complement);
    }


    public int getFirst() {
        return this.get(0);
    }

    public int getLast() {
        return this.get(this.size() - 1);
    }

    /**
     * Copies elements from the provided splitblock and adds them to this splitblock
     *
     * @param splitBlock
     */
    public void merge(SplitBlock splitBlock) {

        for (Integer i : splitBlock) {
            this.add(i.intValue());
        }
    }

    public SplitBlock copy() {
        List<Integer> copy = new ArrayList<>();

        for (Integer i : this) {
            copy.add(i.intValue());
        }

        return new SplitBlock(copy);
    }

    public void sort() {
        Collections.sort(this);
    }

    public SplitBlock makeSortedCopy() {

        SplitBlock copy = this.copy();
        copy.sort();
        return copy;
    }

    public void reverse() {
        Collections.reverse(this);
    }

    public SplitBlock makeReversedCopy() {

        SplitBlock copy = this.copy();
        copy.reverse();
        return copy;
    }


    public EdgeType getType() {
        return this.size() == 1 ? EdgeType.EXTERNAL : EdgeType.INTERNAL;
    }

    public enum EdgeType {
        EXTERNAL,
        INTERNAL
    }

    public boolean containsAny(SplitBlock other) {

        for(Integer i : other) {
            if (this.contains(i)) {
                return true;
            }
        }

        return false;
    }

    public boolean isContiguousWithOrdering(IdentifierList ordering) {

        Identifier first = ordering.getById(this.getFirst());

        if (first == null) {
            throw new IllegalStateException("Could not find first index in split block (" + this.getFirst() + "), in the circular ordering: " + ordering.toString());
        }

        int index = ordering.indexOf(first);

        for(int i = index, j = 0; j < this.size(); i++, j++) {

            // Got to the end of the ordered taxa so go back to the start
            if (i >= ordering.size()) {
                i = 0;
            }

            if (this.get(j) != ordering.get(i).getId()) {
                return false;
            }
        }

        return true;
    }
}
