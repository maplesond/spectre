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

package uk.ac.uea.cmp.phygen.net.netmake.edge;

import uk.ac.uea.cmp.phygen.core.ds.Tableau;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: Sarah_2
 * Date: 24/04/13
 * Time: 22:56
 * To change this template use File | Settings | File Templates.
 */
public class TableauSplits {

    private Tableau<Integer> a_side;
    private Tableau<Integer> b_side;
    private int nb_taxa;

    public TableauSplits() {
        this.a_side = new Tableau<Integer>();
        this.b_side = new Tableau<Integer>();
        this.nb_taxa = -1;
    }

    public TableauSplits(Tableau<Integer> a_side, int nb_taxa) {
        this.a_side = a_side;
        this.nb_taxa = nb_taxa;
        this.b_side = makeSplitsComplement(a_side);
    }

    public Tableau<Integer> getASide() {
        return a_side;
    }

    public Tableau<Integer> getBSide() {
        return b_side;
    }

    private Tableau<Integer> makeSplitsComplement(Tableau<Integer> a_side) {
        Tableau<Integer> fullSplits = new Tableau<Integer>();

        for (int j = 0; j < a_side.rows(); j++) {
            ArrayList<Integer> complement = makeComplement(getRow(SplitSide.A_SIDE, j));

            fullSplits.addRow(complement);
        }

        return fullSplits;
    }

    protected ArrayList<Integer> makeComplement(ArrayList<Integer> split) {
        ArrayList<Integer> complement = new ArrayList<Integer>();
        for (int i = 0; i < nb_taxa; i++) {
            if (split.contains(i) == false) {
                complement.add(i);
            }
        }

        return complement;
    }

    public Tableau<Integer> combineSides() {
        Tableau<Integer> combined = new Tableau<Integer>();

        combined.addAllRows(a_side);
        combined.addAllRows(b_side);

        return combined;
    }

    public void sortElementsInAllRows() {
        for (int i = 0; i < a_side.rows(); i++) {
            Collections.sort(a_side.getRow(i));
        }

        for (int i = 0; i < b_side.rows(); i++) {
            Collections.sort(b_side.getRow(i));
        }
    }

    public int rows() throws Exception {
        if (a_side.rows() != b_side.rows()) {
            throw new Exception("a_side and b_side are of different sizes!");
        }

        return a_side.rows();
    }

    public ArrayList<Integer> getRow(SplitSide side, int row) {
        return side.getRow(this, row);
    }

    public enum SplitSide {

        A_SIDE {
            public ArrayList<Integer> getRow(TableauSplits s, int index) {
                return s.a_side.getRow(index);
            }
        },
        B_SIDE {
            public ArrayList<Integer> getRow(TableauSplits s, int index) {
                return s.b_side.getRow(index);
            }
        };

        public abstract ArrayList<Integer> getRow(TableauSplits s, int index);
    }

    public void removeSplit(int i) {
        a_side.removeRow(i);
        b_side.removeRow(i);
    }

    public void addSplit(ArrayList<Integer> Split) {
        a_side.addRow(Split);
        b_side.addRow(makeComplement(Split));
    }
}
