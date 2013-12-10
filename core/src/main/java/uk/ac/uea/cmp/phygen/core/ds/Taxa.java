/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
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
package uk.ac.uea.cmp.phygen.core.ds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Taxa class.  This is an extension of Arraylist, which also acts as a set in that it does not allow the client
 * to add duplicate items into the list.
 */
public class Taxa extends ArrayList<Taxon> {

    private Set<String> names;
    private boolean duplicatesAllowed;

    /**
     * Constructor
     */
    public Taxa() {
        this(false);
    }

    public Taxa(boolean duplicatesAllowed) {
        super();
        this.duplicatesAllowed = duplicatesAllowed;
        this.names = new HashSet<>();
    }

    /**
     * Seeded constructor. Add as single element aTaxon.
     */
    public Taxa(Taxon taxon) {

        this(false);
        this.add(taxon);
    }

    public Taxa(final String[] taxa) {

        this.names = new HashSet<>();
        this.duplicatesAllowed = false;

        for(String taxon : taxa) {
            this.add(new Taxon(taxon));
        }
    }

    /**
     * Copy constructor
     * @param taxa
     */
    public Taxa(Taxa taxa) {
        super(taxa);
        this.duplicatesAllowed = taxa.isDuplicatesAllowed();
        this.names = new HashSet<>();
    }


    @Override
    public boolean equals(Object o) {

        Taxa other = (Taxa)o;

        if (other.size() != this.size())
            return false;

        for(int i = 0; i < this.size(); i++) {

            if (!this.get(i).equals(other.get(i)))
                return false;
        }

        return true;
    }


    /**
     * Will try to add a new taxon to the end of the list.  Throws an IllegalArgumentException if this
     * taxon name already exists
     * @param taxon The taxon to add
     * @return True if successfully added taxon to the list
     */
    @Override
    public boolean add(Taxon taxon) {

        if (!duplicatesAllowed) {
            if (this.names.contains(taxon.getName()))
                throw new IllegalArgumentException("Taxa list already contains taxon: " + taxon.getName());

            this.names.add(taxon.getName());
        }

        return super.add(taxon);
    }

    /**
     * Will try to insert a new taxon into the list at a specific location.  Throws an IllegalArgumentException if this
     * taxon name already exists
     * @param index Location to insert new taxon
     * @param taxon The taxon to insert
     */
    @Override
    public void add(int index, Taxon taxon) {
        if (!duplicatesAllowed) {
            if (!this.names.contains(taxon.getName())) {
                this.names.add(taxon.getName());
                super.add(index, taxon);
            }
            else {
                throw new IllegalArgumentException("Taxa list already contains taxon: " + taxon.getName());
            }
        }
        else {
            super.add(index, taxon);
        }
    }

    /**
     * Adds taxon from another taxa list into this taxa list.  Will ignore taxa that are already present in this list.
     * The return flag indicates if all taxa were merged.
     * @param taxa Taxa to merge into this list.
     * @return True if all taxa were merged.  False if some were not
     */
    @Override
    public boolean addAll(Collection<? extends Taxon> taxa) {

        int i = 0;
        for(Taxon t : taxa) {
            if (!duplicatesAllowed) {
                if (!this.names.contains(t.getName())) {
                    this.add(t);
                    i++;
                }
            }
            else {
                this.add(t);
                i++;
            }
        }
        return i == taxa.size();
    }

    /**
     * Overwrites the taxon at a specific position in the list with the one provided.
     * @param index
     * @param taxon
     * @return
     */
    @Override
    public Taxon set(int index, Taxon taxon) {
        if (!duplicatesAllowed) {
            this.names.remove(this.get(index).getName());
            this.names.add(taxon.getName());
        }
        return super.set(index, taxon);
    }

    public Quadruple getQuadruple(int a, int b, int c, int d) {

        if (this.size() < 4)
            throw new IllegalStateException("Taxa list must contain at least 4 taxa for this method to work");

        return new Quadruple(this.get(a), this.get(b), this.get(c), this.get(d));
    }

    public boolean contains(Quadruple quad) {
        return this.contains(quad.getQ1())
            && this.contains(quad.getQ2())
            && this.contains(quad.getQ3())
            && this.contains(quad.getQ4());
    }

    /**
     * Invert method. Places everything in opposite order... Returns the inversion...
     */
    public Taxa invert() {

        Taxa result = new Taxa();

        for (int a = 0; a < size(); a++) {
            result.add(this.get(this.size() - 1 - a));
        }

        return result;
    }

    public boolean isDuplicatesAllowed() {
        return duplicatesAllowed;
    }

    public static Taxa createTrivialTaxaSet(int expectedNbTaxa) {

        Taxa t = new Taxa();

        for(int i = 0; i < expectedNbTaxa; i++) {
            t.add(new Taxon(new String(Character.toString((char)(i + 65)))));
        }

        return t;
    }

    public void setDefaultIndicies() {
        int i = 0;
        for(Taxon taxon : this) {
            taxon.setId(i++);
        }

    }

    public static enum Direction {
        FORWARD,
        BACKWARD
    }

    /**
     * EXCLUSIVE sublist-complement, reverse-order (so front-front, back-back)
     */
    public Taxa complement(int I, int J) {

        Taxa result = new Taxa();

        for (int i = I - 1; i >= 0; i--) {
            result.add(get(i));
        }

        for (int i = size() - 1; i > J; i--) {
            result.add(get(i));
        }

        return result;
    }



    public Taxon first() {
        return this.size() > 0 ? this.get(0) : null;
    }

    public Taxon last() {
        return this.size() > 0 ? this.get(size() - 1) : null;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("[");
        int i = 0;
        for(Taxon t : this) {

            if (i != 0) {
                sb.append(",");
            }
            sb.append(t.getName());

            i++;
        }
        sb.append("]");

        return sb.toString();
    }

    /**
     * Join method. Joins in that order the two lists, in the specified
     * orientation
     */
    public static Taxa join(Taxa firstTaxa, Direction firstDirection,
                            Taxa secondTaxa, Direction secondDirection) {

        Taxa result = new Taxa(true);

        if (firstDirection == Direction.FORWARD) {
            result.addAll(firstTaxa);
        }
        else {
            result.addAll(firstTaxa.invert());
        }

        if (secondDirection == Direction.FORWARD) {
            result.addAll(secondTaxa);
        }
        else if (secondDirection == Direction.BACKWARD) {
            result.addAll(secondTaxa.invert());
        }

        return result;
    }
}