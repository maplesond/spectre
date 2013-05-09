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
package uk.ac.uea.cmp.phygen.qnet.holders;

import uk.ac.uea.cmp.phygen.core.ds.TaxonList;

import java.util.ArrayList;
import java.util.List;

public class ZHolder {

    public ZHolder(List<TaxonList> theLists, int N) {

        sizes = new ArrayList();

        for (int i = 1; i < N + 1; i++) {

            sizes.add(new Integer(z(theLists, i)));

        }


    }

    public int getZ(int i) {

        return ((Integer) sizes.get(i - 1)).intValue();

    }

    public void setZ(int i, int newZ) {

        sizes.set(i - 1, new Integer(newZ));

    }

    ArrayList sizes;

    int z(List<TaxonList> theLists, int i) {

        for (int n = 0; n < theLists.size(); n++) {

            TaxonList tL = theLists.get(n);

            if (tL.contains(i)) {

                if (i == tL.left()) {

                    return tL.size();

                }

            }

        }

        return 0;

    }
}
