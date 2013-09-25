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
package uk.ac.uea.cmp.phygen.tools.chopper;

import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA. User: Analysis Date: 2004-jul-11 Time: 20:05:18 To
 * change this template use Options | File Templates.
 */
public class Leaf implements Node {

    public Leaf(String source) {

        taxonName = source;
    }

    public void index(LinkedList<String> taxonNames) {

        taxonID = taxonNames.indexOf(taxonName);
    }

    public void harvestNames(LinkedList<String> taxonNames) {

        if (!taxonNames.contains(taxonName)) {
            taxonNames.add(taxonName);
        }
    }

    public void harvest(LinkedList<Integer> taxa) {

        taxa.add(new Integer(taxonID));
    }

    public boolean isTree() {

        return false;
    }

    public void rename(LinkedList<String> oldTaxa, LinkedList<String> newTaxa) {

        int i = oldTaxa.indexOf(taxonName);
        taxonName = newTaxa.get(i);
    }

    String taxonName;
    int taxonID;
}
