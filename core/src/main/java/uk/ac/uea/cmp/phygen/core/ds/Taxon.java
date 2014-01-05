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

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 12/11/13
 * Time: 19:26
 * To change this template use File | Settings | File Templates.
 */
public class Taxon {

    public static final int DEFAULT_ID = 0;

    private String name;
    private int id;

    public Taxon(String name) {
        this(name, DEFAULT_ID);
    }

    public Taxon(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public boolean isEmpty() {
        return this.name != null && this.name.isEmpty() && this.id != DEFAULT_ID;
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
    public boolean equals(Object o) {
        Taxon taxon = (Taxon)o;
        return this.getName().equals(taxon.getName());
    }
}
