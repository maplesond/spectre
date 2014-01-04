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
package uk.ac.uea.cmp.phygen.superq.qnet.holders;

import uk.ac.uea.cmp.phygen.core.ds.Taxa;

import java.util.ArrayList;
import java.util.List;

public class ZHolder {

    private int[] sizes;

    public ZHolder(List<Taxa> taxaSets, int N) {

        sizes = new int[N];

        for (int i = 0; i < N; i++) {
            sizes[i] = z(taxaSets, i);
        }
    }

    public int getZ(int i) {

        return sizes[i];
    }

    public void setZ(int i, int newZ) {

        sizes[i] = newZ;
    }



    protected int z(List<Taxa> taxaSets, int i) {

        for (Taxa tL : taxaSets) {

            if (tL.containsId(i)) {

                if (i == tL.first().getId()) {
                    return tL.size();
                }
            }
        }

        return 0;
    }
}
