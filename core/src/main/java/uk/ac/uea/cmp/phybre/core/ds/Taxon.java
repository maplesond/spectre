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

package uk.ac.uea.cmp.phybre.core.ds;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 12/11/13
 * Time: 19:26
 * To change this template use File | Settings | File Templates.
 */
public class Taxon implements Comparable<Taxon> {

    public static final int DEFAULT_ID = 0;

    private String name;
    private int id;

    public Taxon(String name) {
        this(name, DEFAULT_ID);
    }

    public Taxon(int id) {
        this(Integer.toString(id), id);
    }

    public Taxon(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public Taxon(Taxon taxon) {
        this(taxon.getName(), taxon.getId());
    }

    public boolean isEmpty() {
        return this.name != null && this.name.isEmpty() && this.id != DEFAULT_ID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder()
                .append(name)
                .append(id)
                .toHashCode();
    }

    @Override
    public boolean equals(Object o) {

        if (o == null)
            return false;
        if (o == this)
            return true;
        if (!(o instanceof Taxon))
            return false;

        Taxon other = (Taxon)o;
        return new EqualsBuilder()
                .append(this.name, other.name)
                .append(this.id, other.id)
                .isEquals();
    }

    /**
     * Natural ordering is first by id, then name
     * @param o
     * @return
     */
    @Override
    public int compareTo(Taxon o) {

        int idDiff = this.id - o.id;

        if (idDiff == 0) {
            return this.name.compareTo(o.name);
        }
        else {
            return idDiff;
        }
    }

    public static class NameComparator implements Comparator<Taxon> {

        @Override
        public int compare(Taxon o1, Taxon o2) {
            return o1.name.compareTo(o2.name);
        }
    }

    public static class IdComparator implements Comparator<Taxon> {

        @Override
        public int compare(Taxon o1, Taxon o2) {
            return o1.id - o2.id;
        }
    }
}
