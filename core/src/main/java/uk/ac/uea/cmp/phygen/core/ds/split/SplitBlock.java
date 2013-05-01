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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Sarah
 */
public class SplitBlock implements Comparable<SplitBlock> {

    private ArrayList<Integer> elements;

    public SplitBlock(Collection<Integer> splitBlock) {

        if (splitBlock == null || splitBlock.size() < 1)
            throw new IllegalArgumentException("SplitBlock must be of at least size 1");

        this.elements = new ArrayList<>(splitBlock);
    }

    public SplitBlock(int[] splitBlock) {

        if (splitBlock == null || splitBlock.length < 1)
            throw new IllegalArgumentException("SplitBlock must be of at least size 1");

        this.elements = new ArrayList<>();

        for (int i : splitBlock) {
            this.elements.add(i);
        }
    }

    public SplitBlock(String s) {

        this.elements = new ArrayList<>();

        String[] parts = s.split(" ");


        int[] raw = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            raw[i] = Integer.parseInt(parts[i]);
        }
        if (parts.length == 1) {
            this.elements.add(raw[0]);
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
                    this.elements.add(new Integer(raw[i]));
                }
                for (int i = 0; i < overlap; i++) {
                    this.elements.add(new Integer(raw[i]));
                }
            } else {
                for (int i = 0; i < raw.length; i++) {
                    this.elements.add(new Integer(raw[i]));
                }
            }
        }
    }

    public SplitBlock makeComplement(int nbTaxa) {
        ArrayList<Integer> complement = new ArrayList<Integer>();
        for (int i = 0; i < nbTaxa; i++) {
            if (this.elements.contains(i) == false) {
                complement.add(i);
            }
        }

        return new SplitBlock(complement);
    }

    public void sort() {
        Collections.sort(this.elements);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Integer i : this.elements) {
            sb.append(i + 1);
            sb.append(" ");
        }

        return sb.toString().trim();
    }

    public int getFirst() {
        return this.elements.get(0);
    }

    public int getLast() {
        return this.elements.get(this.elements.size() - 1);
    }

    public int get(int index) {
        return this.elements.get(index);
    }

    public int size() {
        return this.elements.size();
    }

    public void merge(SplitBlock splitBlock) {
        this.elements.addAll(splitBlock.elements);
    }

    public SplitBlock copy() {
        List<Integer> copy = new ArrayList<>();

        for (Integer i : this.elements) {
            copy.add(i);
        }

        return new SplitBlock(copy);
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
}
