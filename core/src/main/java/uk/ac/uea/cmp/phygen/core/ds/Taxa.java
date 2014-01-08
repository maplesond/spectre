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
import java.util.HashMap;
import java.util.Map;

/**
 * Taxa class.  This is an extension of Arraylist, which also acts as a set in that it does not allow the client
 * to add duplicate items into the list.
 */
public class Taxa extends ArrayList<Taxon> {

    private Map<String, Taxon> names;
    private Map<Integer, Taxon> ids;
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
        this.names = new HashMap<>();
        this.ids = new HashMap<>();
    }

    /**
     * Seeded constructor. Add as single element aTaxon.
     */
    public Taxa(Taxon taxon) {

        this(false);
        this.add(taxon);
    }

    public Taxa(final String[] taxa) {

        this.names = new HashMap<>();
        this.ids = new HashMap<>();
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
        this.names = new HashMap<>();
        this.ids = new HashMap<>();
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
     * taxon name or id already exists
     * @param taxon The taxon to add
     * @return True if successfully added taxon to the list
     */
    @Override
    public boolean add(Taxon taxon) {

        if (!duplicatesAllowed) {
            if (this.names.containsKey(taxon.getName()))
                throw new IllegalArgumentException("Taxa list already contains taxon name: " + taxon.getName());

            // If the user didn't both to set the taxa id then we do it ourselves.
            if (taxon.getId() == Taxon.DEFAULT_ID) {
                taxon.setId(this.size()+1);
            }

            if (this.ids.containsKey(taxon.getId()))
                throw new IllegalArgumentException("Taxa list already contains taxon id: " + taxon.getId());

            this.names.put(taxon.getName(), taxon);
            this.ids.put(taxon.getId(), taxon);
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

            // If the user didn't both to set the taxa id then we do it ourselves.
            if (taxon.getId() == Taxon.DEFAULT_ID) {
                taxon.setId(index);
            }

            if (!this.names.containsKey(taxon.getName()) && !this.ids.containsKey(taxon.getId())) {
                this.names.put(taxon.getName(), taxon);
                this.ids.put(taxon.getId(), taxon);
                super.add(index, taxon);
            }
            else {
                throw new IllegalArgumentException("Taxa list already contains taxon with name: " + taxon.getName() + "; or id: " + taxon.getId());
            }
        }
        else {
            super.add(index, taxon);
        }
    }

    /**
     * Adds taxa from another taxa list into this taxa list.  Will ignore taxa that are already present in this list.
     * The return flag indicates if all taxa were merged.
     * @param taxa Taxa to merge into this list.
     * @param retainIds If true, we keep the ids of the original taxa after merging.  If not then we ensure all taxa ids
     *                  in this taxa are unique.
     * @return True if all taxa were merged.  False if some were not
     */
    public boolean addAll(Collection<? extends Taxon> taxa, boolean retainIds) {

        int i = 0;
        for(Taxon t : taxa) {
            if (!duplicatesAllowed) {
                if (!this.names.containsKey(t.getName())) {
                    this.add(retainIds ?
                            t :
                            new Taxon(t.getName(), this.size()+1));
                    i++;
                }
            }
            else {
                this.add(retainIds ?
                        t :
                        new Taxon(t.getName(), this.size()+1));
                i++;
            }
        }
        return i == taxa.size();
    }

    /**
     * Overwrites the taxon at a specific position in the list with the one provided.
     * @param index
     * @param taxon
     * @return The taxon that's been set.
     */
    @Override
    public Taxon set(int index, Taxon taxon) {
        if (!duplicatesAllowed) {
            this.names.remove(this.get(index).getName());
            this.names.put(taxon.getName(), taxon);
            this.ids.remove(this.get(index).getId());
            this.ids.put(taxon.getId(), taxon);
        }
        return super.set(index, taxon);
    }


    public Taxon getByName(String name) {

        return this.names.get(name);
    }

    public Taxon getById(int id) {
        return this.ids.get(id);
    }

    public Quadruple getQuadruple(int a, int b, int c, int d) {

        if (this.size() < 4)
            throw new IllegalStateException("Taxa list must contain at least 4 taxa for this method to work");

        return new Quadruple(this.getById(a), this.getById(b), this.getById(c), this.getById(d));
    }

    public boolean contains(Quadruple quad) {
        return this.contains(quad.getQ1())
            && this.contains(quad.getQ2())
            && this.contains(quad.getQ3())
            && this.contains(quad.getQ4());
    }

    public boolean containsName(String name) {
        return this.names.containsKey(name);
    }

    public boolean containsId(int taxaId) {

        return this.ids.containsKey(taxaId);
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
        int i = 1;
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

    public String[] getNames() {
        String[] nameArray = new String[this.size()];

        for(int i = 0; i < this.size(); i++) {
            nameArray[i] = this.get(i).getName();
        }

        return nameArray;
    }

    public int[] getIds() {
        int[] idArray = new int[this.size()];

        for(int i = 0; i < this.size(); i++) {
            idArray[i] = this.get(i).getId();
        }

        return idArray;
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

        Taxa result = new Taxa();

        if (firstDirection == Direction.FORWARD) {
            result.addAll(firstTaxa, true);
        }
        else {
            result.addAll(firstTaxa.invert(), true);
        }

        if (secondDirection == Direction.FORWARD) {
            result.addAll(secondTaxa, true);
        }
        else if (secondDirection == Direction.BACKWARD) {
            result.addAll(secondTaxa.invert(), true);
        }

        return result;
    }
}