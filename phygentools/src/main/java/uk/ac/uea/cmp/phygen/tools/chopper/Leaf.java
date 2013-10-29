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
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: Analysis Date: 2004-jul-11 Time: 20:05:18 To
 * change this template use Options | File Templates.
 */
public class Leaf implements Node {

    private String taxonName;
    private int taxonID;

    public Leaf(String source) {
        taxonName = source;
    }

    @Override
    public void index(List<String> taxonNames) {
        taxonID = taxonNames.indexOf(taxonName);          }

    @Override
    public void harvestNames(List<String> taxonNames) {
        if (!taxonNames.contains(taxonName)) {
            taxonNames.add(taxonName);
        }
    }

    @Override
    public void harvest(List<Integer> taxa) {
        taxa.add(new Integer(taxonID));
    }

    @Override
    public boolean isTree() {
        return false;
    }

    @Override
    public void rename(List<String> oldTaxa, List<String> newTaxa) {
        taxonName = newTaxa.get(oldTaxa.indexOf(taxonName));
    }


}
