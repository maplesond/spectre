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
package uk.ac.uea.cmp.spectre.core.ds;

import java.util.*;

/**
 * IdentifierList class.  This is an extension of Arraylist, which also acts as a set in that it does not allow the client
 * to add duplicate items into the list.
 */
public class IdentifierList extends ArrayList<Identifier> {

    private Map<String, Identifier> names;
    private Map<Integer, Identifier> numbers;
    private boolean duplicatesAllowed;

    private int maxId;

    /**
     * Constructor
     */
    public IdentifierList() {
        this(false);
    }

    public IdentifierList(final int size) {
        this(false);

        for (int i = 1; i <= size; i++) {
            this.add(new Identifier(i));
        }
    }

    public IdentifierList(boolean duplicatesAllowed) {
        super();
        this.duplicatesAllowed = duplicatesAllowed;
        this.names = new HashMap<>();
        this.numbers = new HashMap<>();
        this.maxId = 0;
    }

    /**
     * Seeded constructor. Add as single element aTaxon.
     */
    public IdentifierList(Identifier identifier) {

        this(false);
        this.add(identifier);
    }

    public IdentifierList(final String[] names) {

        this(false);
        int i = 1;
        for (String idName : names) {
            this.add(new Identifier(idName, i++));
        }
    }

    public IdentifierList(int[] idList) {
        this(false);
        for (Integer i : idList) {
            this.add(new Identifier(i));
        }
    }

    public IdentifierList(List<Integer> idList) {
        this(false);
        for (Integer i : idList) {
            this.add(new Identifier(i));
        }
    }

    /**
     * Copy constructor
     *
     * @param identifierList
     */
    public IdentifierList(IdentifierList identifierList) {
        this();
        this.duplicatesAllowed = identifierList.isDuplicatesAllowed();
        this.names = new HashMap<>();
        this.numbers = new HashMap<>();

        for (Identifier t : identifierList) {
            this.add(new Identifier(t));
        }
    }


    @Override
    public boolean equals(Object o) {

        IdentifierList other = (IdentifierList) o;

        if (other.size() != this.size())
            return false;

        for (int i = 0; i < this.size(); i++) {

            if (!this.get(i).equals(other.get(i)))
                return false;
        }

        return true;
    }


    /**
     * Will try to add a new identifier to the end of the list.  Throws an IllegalArgumentException if this
     * taxon name or id already exists
     *
     * @param identifier The identifier to add
     * @return True if successfully added identifier to the list
     */
    @Override
    public boolean add(Identifier identifier) {

        if (!duplicatesAllowed) {
            if (this.names.containsKey(identifier.getName()))
                throw new IllegalArgumentException("Identifier list already contains an identifier with the same name: " + identifier.getName());

            // If the user didn't both to set the taxa id then we do it ourselves.
            if (identifier.getId() == Identifier.DEFAULT_ID) {
                this.maxId = this.getNextId();
                identifier.setId(this.maxId);
            } else {
                this.maxId = this.maxId > identifier.getId() ? this.maxId : identifier.getId();
            }

            if (this.numbers.containsKey(identifier.getId()))
                throw new IllegalArgumentException("Taxa list already contains taxon id: " + identifier.getId());

            this.names.put(identifier.getName(), identifier);
            this.numbers.put(identifier.getId(), identifier);
        }

        return super.add(identifier);
    }

    /**
     * Will try to insert a new identifier into the list at a specific location.  Throws an IllegalArgumentException if this
     * taxon name already exists
     *
     * @param index      Location to insert new taxon
     * @param identifier The taxon to insert
     */
    @Override
    public void add(int index, Identifier identifier) {
        if (!duplicatesAllowed) {

            // If the user didn't both to set the identifier number then we do it ourselves.
            if (identifier.getId() == Identifier.DEFAULT_ID) {
                this.maxId = this.getNextId();
                identifier.setId(this.maxId);
            } else {
                this.maxId = this.maxId > identifier.getId() ? this.maxId : identifier.getId();
            }


            if (!this.names.containsKey(identifier.getName()) && !this.numbers.containsKey(identifier.getId())) {
                this.names.put(identifier.getName(), identifier);
                this.numbers.put(identifier.getId(), identifier);
                super.add(index, identifier);
            } else {
                throw new IllegalArgumentException("Identifier list already contains an identifier with the name: " + identifier.getName() + "; or number: " + identifier.getId());
            }
        } else {
            super.add(index, identifier);
        }
    }

    /**
     * Adds identifiers from another IdentifierList into this IdentifierList.  Will ignore identifiers that are already present in this list.
     * The return flag indicates if all identifers were merged.
     *
     * @param identifiers IdentifierList to merge into this list.
     * @param retainIds   If true, we keep the numbers of the original identifiers after merging.  If not then we ensure all identfier numbers
     *                    in this list are unique.
     * @return True if all identifiers were merged.  False if some were not
     */
    public boolean addAll(Collection<? extends Identifier> identifiers, boolean retainIds) {

        int i = 0;
        for (Identifier t : identifiers) {
            if (!duplicatesAllowed) {
                if (!this.names.containsKey(t.getName())) {
                    this.add(retainIds ?
                            t :
                            new Identifier(t.getName(), this.size() + 1));
                    i++;
                }
            } else {
                this.add(retainIds ?
                        t :
                        new Identifier(t.getName(), this.size() + 1));
                i++;
            }
        }
        return i == identifiers.size();
    }

    /**
     * Overwrites the identifier at a specific position in the list with the one provided.
     *
     * @param index
     * @param identifier
     * @return The identifier that's been set.
     */
    @Override
    public Identifier set(int index, Identifier identifier) {
        if (!duplicatesAllowed) {
            this.names.remove(this.get(index).getName());
            this.names.put(identifier.getName(), identifier);
            this.numbers.remove(this.get(index).getId());
            this.numbers.put(identifier.getId(), identifier);
        }
        return super.set(index, identifier);
    }


    public Identifier getByName(String name) {

        return this.names.get(name);
    }

    public Identifier getById(int id) {
        return this.numbers.get(id);
    }

    public Quadruple getQuadruple(int a, int b, int c, int d) {

        if (this.size() < 4)
            throw new IllegalStateException("Identifier list must contain at least 4 taxa for this method to work");

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

    public boolean containsId(int idNumber) {

        return this.numbers.containsKey(idNumber);
    }

    /**
     * Reverses the ordering.  i.e. places identifier in the opposite order..
     */
    public IdentifierList reverseOrdering() {

        IdentifierList result = new IdentifierList();

        for (int a = 0; a < size(); a++) {
            result.add(this.get(this.size() - 1 - a));
        }

        return result;
    }


    public boolean isDuplicatesAllowed() {
        return duplicatesAllowed;
    }

    public static IdentifierList createSimpleIdentifiers(int expectedNbIdentifiers) {

        IdentifierList t = new IdentifierList();

        for (int i = 0; i < expectedNbIdentifiers; i++) {
            t.add(new Identifier(new String(Character.toString((char) (i + 65)))));
        }

        return t;
    }

    public void setDefaultIndicies() {
        int i = 1;
        for (Identifier id : this) {
            id.setId(i++);
        }

        this.maxId = this.size();
    }

    public static enum Direction {
        FORWARD,
        BACKWARD
    }

    /**
     * EXCLUSIVE sublist-complement, reverse-order (so front-front, back-back)
     */
    public IdentifierList complement(int I, int J) {

        IdentifierList result = new IdentifierList();

        for (int i = I - 1; i >= 0; i--) {
            result.add(get(i));
        }

        for (int i = size() - 1; i > J; i--) {
            result.add(get(i));
        }

        return result;
    }


    public Identifier first() {
        return this.size() > 0 ? this.get(0) : null;
    }

    public Identifier last() {
        return this.size() > 0 ? this.get(size() - 1) : null;
    }

    public String[] getNames() {
        String[] nameArray = new String[this.size()];

        for (int i = 0; i < this.size(); i++) {
            nameArray[i] = this.get(i).getName();
        }

        return nameArray;
    }

    public Set<String> getNameSet() {
        Set<String> nameSet = new LinkedHashSet<>(this.size());

        for (int i = 0; i < this.size(); i++) {
            nameSet.add(this.get(i).getName());
        }

        return nameSet;
    }

    public int[] getNumbers() {
        int[] idArray = new int[this.size()];

        for (int i = 0; i < this.size(); i++) {
            idArray[i] = this.get(i).getId();
        }

        return idArray;
    }


    public List<Integer> getIdsAsLinkedList() {

        List<Integer> list = new LinkedList<>();

        for (int i = 0; i < this.size(); i++) {
            list.add(this.get(i).getId());
        }

        return list;
    }

    /**
     * Finds the maximum id already in the list and returns the next number
     *
     * @return The next available id, which might be used to add another unique taxon into this list
     */
    public int getNextId() {
        return this.maxId + 1;
    }

    /**
     * Finds the maximum id already in the list
     *
     * @return The largest taxon id stored in this list
     */
    public int getMaxId() {
        return this.maxId;
    }

    @Override
    public String toString() {

        return this.toString(IdentifierFormat.BY_NAME);
    }

    public String toString(IdentifierFormat format) {
        return format.toString(this);
    }

    public static enum IdentifierFormat {

        BY_NAME {
            @Override
            public String toString(IdentifierList identifierList) {
                StringBuilder sb = new StringBuilder();

                sb.append("[");
                int i = 0;
                for (Identifier t : identifierList) {

                    if (i != 0) {
                        sb.append(",");
                    }
                    sb.append(t.getName());

                    i++;
                }
                sb.append("]");

                return sb.toString();
            }
        },
        BY_ID {
            @Override
            public String toString(IdentifierList identifierList) {
                StringBuilder sb = new StringBuilder();

                sb.append("[");
                int i = 0;
                for (Identifier t : identifierList) {

                    if (i != 0) {
                        sb.append(",");
                    }
                    sb.append(t.getId());

                    i++;
                }
                sb.append("]");

                return sb.toString();
            }
        },
        BOTH {
            @Override
            public String toString(IdentifierList identifierList) {
                StringBuilder sb = new StringBuilder();

                sb.append("[");
                int i = 0;
                for (Identifier t : identifierList) {

                    if (i != 0) {
                        sb.append(",");
                    }
                    sb.append(t.toString());

                    i++;
                }
                sb.append("]");

                return sb.toString();
            }
        },
        NEXUS_CIRCULAR_ORDERING {
            @Override
            public String toString(IdentifierList identifierList) {
                if (identifierList.isEmpty()) {
                    return "";
                } else {
                    StringBuilder sb = new StringBuilder();

                    sb.append(identifierList.get(0).getId());

                    for (int i = 1; i < identifierList.size(); i++) {
                        sb.append(" ").append(identifierList.get(i).getId());
                    }

                    return sb.toString();
                }
            }
        };

        public abstract String toString(IdentifierList identifierList);
    }

    /**
     * Join method. Joins in that order the two lists, in the specified
     * orientation
     */
    public static IdentifierList join(IdentifierList firstList, Direction firstDirection,
                                      IdentifierList secondList, Direction secondDirection) {

        IdentifierList result = new IdentifierList();

        if (firstDirection == Direction.FORWARD) {
            result.addAll(firstList, true);
        } else {
            result.addAll(firstList.reverseOrdering(), true);
        }

        if (secondDirection == Direction.FORWARD) {
            result.addAll(secondList, true);
        } else if (secondDirection == Direction.BACKWARD) {
            result.addAll(secondList.reverseOrdering(), true);
        }

        return result;
    }


    public IdentifierList sort(Comparator<Identifier> comparator) {

        IdentifierList identifiers = new IdentifierList(this);

        Collections.sort(identifiers, comparator);

        return identifiers;
    }


    public IdentifierList sortById() {

        return this.sort(new Identifier.NumberComparator());
    }

    public IdentifierList sortByName() {

        return this.sort(new Identifier.NameComparator());
    }

    public void shuffle() {
        Collections.shuffle(this);
    }

    public Map<Identifier, Integer> createLookup() {

        Map<Identifier, Integer> lut = new HashMap<>();

        int i = 0;
        for (Identifier id : this) {
            lut.put(id, i++);
        }

        return lut;
    }
}