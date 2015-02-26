package uk.ac.uea.cmp.spectre.core.ds.split;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
        else if (this.size() == 0 && o.size() > 0)
            return -1;
        else if (this.size() > 0 && o.size() == 0)
            return 1;
        else {
            int diff = this.getFirst() - o.getFirst();

            return diff == 0 ? this.size() - o.size() : diff;
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
        ArrayList<Integer> complement = new ArrayList<>();
        for (int i = 1; i <= nbTaxa; i++) {
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
